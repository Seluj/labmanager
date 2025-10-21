/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.addons.uploads;

import com.google.common.base.Strings;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionBiFunction;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressDialog;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressDialog.DialogCancelation;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Extension of a component that is clickable in order to start a task for uploading a file and use its content.
 *
 * @param <T> the type of the result computed by the asynchronous task.
 * @param <C> the type of the extended component that must be a valid Vaadin component and a received of click event.
 * @author $Author: pschneiderlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class UploadExtension<T, C extends Component & ClickNotifier<C>> implements Serializable {

    private static final long serialVersionUID = -9208005362814721948L;

    private final C component;

    private final List<SerializableConsumer<List<T>>> successListeners = new ArrayList<>();

    private final List<SerializableConsumer<Throwable>> failureListeners = new ArrayList<>();

    private final List<SerializableRunnable> cancellationListeners = new ArrayList<>();

    private Component uploadIcon;

    private String title;

    private SerializableExceptionBiFunction<Reader, Progression, List<T>> extractor;

    private String[] fileExtensions;

    private Dialog dialog;

    private Upload upload;

    private Button importButton;

    private ProgressDialog<List<T>> importDialog;

    private MultiFileMemoryBuffer inputBuffer;

    /**
     * Constructor.
     *
     * @param component the component to extend.
     */
    protected UploadExtension(C component) {
        assert component != null;
        this.component = component;
        registerClickListener(false);
    }

    /**
     * Extends the given component to enable upload of data from a client-side file.
     *
     * @param <T>       the type of the result computed by the asynchronous task.
     * @param <C>       the type of the component that must be a valid Vaadin component and a received of click event.
     * @param component the component to extend.
     * @return the extension.
     */
    public static <T, C extends Component & ClickNotifier<C>> UploadExtension<T, C> extend(C component) {
        return new UploadExtension<>(component);
    }

    /**
     * Extends the given component to enable upload of data from a client-side file.
     *
     * @param <T>       the type of the result computed by the asynchronous task.
     * @param <C>       the type of the component that must be a valid Vaadin component and a received of click event.
     * @param component the component to extend.
     * @param type      the type of the result computed by the asynchronous task.
     * @return the extension.
     */
    public static <T, C extends Component & ClickNotifier<C>> UploadExtension<T, C> extend(C component, Class<T> type) {
        return new UploadExtension<>(component);
    }

    private static String toSizeWidth(float size, Unit unit) {
        return size + unit.getSymbol();
    }

    private static String ensureWidth(String width) {
        if (Strings.isNullOrEmpty(width)) {
            return ViewConstants.DEFAULT_DIALOG_ICON_WIDTH;
        }
        return width;
    }

    /**
     * Close the given dialog safely.
     *
     * @param dialog the dialog to close.
     */
    protected static void closeSafe(Dialog dialog) {
        if (dialog != null) {
            final var ui = dialog.getUI().orElse(null);
            if (ui != null) {
                ui.access(() -> {
                    try {
                        dialog.close();
                    } catch (Throwable ex) {
                        //
                    }
                });
            }
        }
    }

    /**
     * Register the click listener on the initial comopnent.
     *
     * @param inUi indicates if the listener registration must be done on the UI thread or not.
     */
    private void registerClickListener(boolean inUi) {
        if (inUi) {
            final var ui = this.component.getUI().orElseThrow();
            ui.access(() -> this.component.addClickListener(it -> startTask()));
        } else {
            this.component.addClickListener(it -> startTask());
        }
    }

    /**
     * Change the data extraction function.
     *
     * @param extractor the function for extracting data from the input stream.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withDataExtractor(SerializableExceptionBiFunction<Reader, Progression, List<T>> extractor) {
        this.extractor = extractor;
        return this;
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon the icon component.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> UploadExtension<T, C> withUploadIcon(CC icon) {
        return withUploadIcon(icon, null);
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @param unit     the unit of the given {@code minWidth}.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> UploadExtension<T, C> withUploadIcon(CC icon, float minWidth, Unit unit) {
        return withUploadIcon(icon, toSizeWidth(minWidth, unit));
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> UploadExtension<T, C> withUploadIcon(CC icon, String minWidth) {
        this.uploadIcon = icon;
        if (icon != null) {
            final var width = ensureWidth(minWidth);
            icon.setMinWidth(width);
            //icon.setMaxWidth(width);
        }
        return this;
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon the icon component.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withUploadIcon(AbstractIcon<?> icon) {
        return withUploadIcon(icon, null);
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @param unit     the unit of the given {@code minWidth}.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withUploadIcon(AbstractIcon<?> icon, float minWidth, Unit unit) {
        return withUploadIcon(icon, toSizeWidth(minWidth, unit));
    }

    /**
     * Change the icon of the upload window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withUploadIcon(AbstractIcon<?> icon, String minWidth) {
        this.uploadIcon = icon;
        if (icon != null) {
            icon.setSize(ensureWidth(minWidth));
        }
        return this;
    }

    /**
     * Change the title of the upload window.
     *
     * @param title the title of the dialog.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Replies the title of the upload window.
     *
     * @return the title.
     */
    protected String getTitle() {
        return this.title;
    }

    /**
     * Change the filename extensions that must be used in the upload dialog.
     *
     * @param extensions the filename extensions.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withFilenameExtensions(String... extensions) {
        this.fileExtensions = extensions;
        return this;
    }

    /**
     * Add a listener on the error completion of the task.
     *
     * @param listener the lambda function that must be invoked when the task has failed.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withFailureListener(SerializableConsumer<Throwable> listener) {
        this.failureListeners.add(listener);
        return this;
    }

    /**
     * Add a listener on the success completion of the upload.
     *
     * @param listener the lambda function that must be invoked when the task is successfully terminated. The argument of the lambda is the base of the uploaded files.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withSuccessListener(SerializableConsumer<List<T>> listener) {
        this.successListeners.add(listener);
        return this;
    }

    /**
     * Add a listener on the cancellation of the upload.
     *
     * @param listener the lambda function that must be invoked when the task has been cancelled.
     * @return {@code this}.
     */
    public UploadExtension<T, C> withCancellationListener(SerializableRunnable listener) {
        this.cancellationListeners.add(listener);
        return this;
    }

    /**
     * Start the task.
     */
    protected synchronized void startTask() {
        if (this.dialog == null) {
            this.dialog = new Dialog();
            ComponentFactory.configureModalDialog(this.dialog, getTitle(), false);

            final var content = new HorizontalLayout();
            content.setAlignItems(Alignment.CENTER);
            this.dialog.add(content);

            if (this.uploadIcon != null) {
                content.add(this.uploadIcon);
            }

            this.inputBuffer = new MultiFileMemoryBuffer();

            this.upload = new Upload(this.inputBuffer);
            if (this.fileExtensions != null && this.fileExtensions.length > 0) {
                this.upload.setAcceptedFileTypes(this.fileExtensions);
            }
            this.upload.setDropAllowed(true);
            this.upload.setAutoUpload(true);
            this.upload.addAllFinishedListener(event -> {
                if (this.importButton != null) {
                    this.importButton.setEnabled(true);
                }
            });
            content.add(this.upload);

            final var cancelButton = new Button(ComponentFactory.getTranslation("views.cancel"), e -> cancel()); //$NON-NLS-1$

            this.importButton = new Button(ComponentFactory.getTranslation("views.import"), e -> startImports()); //$NON-NLS-1$
            this.importButton.setEnabled(false);

            this.dialog.getFooter().add(cancelButton, this.importButton);
            this.dialog.open();
        }
    }

    /**
     * Start the imports of date from the uploaded files.
     * This function is supposed to invoked {@link #onDataUploaded(List)} when data is extracted from uploaded files.
     *
     * @param input the uploaded files.
     */
    protected void startImports() {
        closeSafe(this.dialog);
        processFiles(this.inputBuffer, this.extractor);
    }

    /**
     * Run the tasks to be done when closing the upload tasks on success, error or cancellation. This function must be overridden for providing specific tasks.
     */
    protected void stopUploadTasks() {
        if (this.upload != null) {
            this.upload.interruptUpload();
            this.upload.clearFileList();
            this.upload = null;
        }
        if (this.dialog != null) {
            closeSafe(this.dialog);
            this.dialog = null;
        }
        if (this.importDialog != null) {
            this.importDialog.cancel();
            closeSafe(this.importDialog);
            this.importDialog = null;
        }
        this.importButton = null;
    }

    /**
     * Cancel of the uploads.
     */
    public final void cancel() {
        stopUploadTasks();
        for (final var listener : this.cancellationListeners) {
            listener.run();
        }
    }

    /**
     * Stop the uploads on errors.
     *
     * @param error the error.
     */
    public final void error(Throwable error) {
        stopUploadTasks();
        fireError(error);
    }

    /**
     * Stop the uploads on success.
     *
     * @param data the uploaded data..
     */
    public final void success(List<T> data) {
        stopUploadTasks();
        for (final var listener : this.successListeners) {
            listener.accept(data);
        }
    }

    /**
     * Notifies listeners on error.
     *
     * @param error the error.
     */
    protected void fireError(Throwable error) {
        for (final var listener : this.failureListeners) {
            listener.accept(error);
        }
    }

    /**
     * Invoked when the data is uploaded.
     * This function is defined for enabling the overriding of the component behavior when data is successfully uploaded.
     *
     * @param data data read from the uploaded files.
     */
    protected void onDataUploaded(List<T> data) {
        success(data);
    }

    /**
     * Read all the data elements contained in the given files.
     *
     * @param buffer         the buffer that contains the files.
     * @param importFunction the function that is used to import the publications.
     */
    protected void processFiles(MultiFileMemoryBuffer buffer, SerializableExceptionBiFunction<Reader, Progression, List<T>> importFunction) {
        this.importDialog = new ProgressDialog<>(this.uploadIcon, null, DialogCancelation.CLICK_ESC_BUTTON,
                (ui, progress) -> CompletableFuture.supplyAsync(() -> readData(ui, buffer, importFunction, progress)));
        ComponentFactory.configureModalDialog(this.importDialog, getTitle(), false);
        this.importDialog.addSuccessListener(data -> onDataUploaded(data));
        this.importDialog.addFailureListener(this::error);
        this.importDialog.addCancellationListener(error -> cancel());
        this.importDialog.openAndRun();
    }

    private List<T> readData(UI ui, MultiFileMemoryBuffer buffer, SerializableExceptionBiFunction<Reader, Progression, List<T>> importFunction, ProgressionListener progressListener) {
        final var progression = new DefaultProgression();
        if (progressListener != null) {
            progression.addProgressionListener(progressListener);
        }
        progression.setProperties(0, 0, buffer.getFiles().size(), false);

        final var data = new ArrayList<T>();

        if (importFunction != null) {
            for (final var filename : buffer.getFiles()) {
                final var subprogression = progression.subTask(1);
                try (final var reader = new BufferedReader(new InputStreamReader(buffer.getInputStream(filename)))) {
                    final var fileData = importFunction.apply(reader, subprogression);
                    if (fileData != null) {
                        data.addAll(fileData);
                    }
                } catch (Throwable ex) {
                    fireError(ex);
                } finally {
                    subprogression.end();
                }
            }
        }

        progression.end();

        return data;
    }

}
