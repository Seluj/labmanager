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

package fr.utbm.ciad.labmanager.views.components.addons.progress;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableConsumer;
import org.arakhne.afc.progress.ProgressionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A model dialog box for waiting for the completion of a task.
 *
 * @param <T> the type of data that is the result of the long parallel task.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ProgressDialog<T> extends Dialog {

    private static final long serialVersionUID = 2194442609831149646L;

    private final Span text;

    private final ProgressBar progressBar;

    private final SerializableBiFunction<UI, ProgressionListener, CompletableFuture<T>> taskProvider;

    private final List<SerializableConsumer<T>> successListeners = new ArrayList<>();

    private final List<SerializableConsumer<Throwable>> failureListeners = new ArrayList<>();

    private final List<SerializableConsumer<Throwable>> cancellationListeners = new ArrayList<>();
    private final AtomicBoolean hasFailure = new AtomicBoolean();
    private CompletableFuture<T> task;

    /**
     * Constructor.
     *
     * @param icon         the icon to be shown in the dialog both, or {@code null} to have none.
     * @param title        the title of the task, or {@code null} to have none.
     * @param cancelation  indicates the type of cancel action that is enabled. If it is {@code null}, the first element in the enumeration {@link DialogCancelation} is assumed.
     * @param taskProvider the provider of the parallel task.
     */
    public ProgressDialog(Component icon, String title, DialogCancelation cancelation, SerializableBiFunction<UI, ProgressionListener, CompletableFuture<T>> taskProvider) {
        this.taskProvider = taskProvider;

        final var cancel = cancelation == null ? DialogCancelation.values()[0] : cancelation;
        final var cancelable = cancel != DialogCancelation.NO_CANCELLATION;

        setModal(true);
        setCloseOnEsc(cancelable);
        setCloseOnOutsideClick(cancelable);
        setDraggable(true);
        setResizable(false);

        final var content = new HorizontalLayout();
        content.setAlignItems(Alignment.CENTER);
        add(content);

        if (icon != null) {
            content.add(icon);
        }

        final var textContent = new VerticalLayout();
        textContent.setAlignItems(Alignment.START);
        content.add(textContent);

        final var hasTitle = !Strings.isNullOrEmpty(title);
        if (hasTitle) {
            final var titleSpan = new Span(title);
            textContent.add(titleSpan);
        }

        this.text = new Span();
        if (hasTitle) {
            final var style = this.text.getStyle();
            style.setFontSize("--vaadin-input-field-helper-font-size"); //$NON-NLS-1$
            style.setFontWeight("--vaadin-input-field-helper-font-weight"); //$NON-NLS-1$
            style.setColor("--vaadin-input-field-helper-color"); //$NON-NLS-1$
        }
        textContent.add(this.text);

        this.progressBar = new ProgressBar();
        this.progressBar.setIndeterminate(true);
        textContent.add(this.progressBar);

        if (cancel == DialogCancelation.CLICK_ESC_BUTTON) {
            final var cancelButton = new Button(getTranslation("views.cancel"), e -> cancel()); //$NON-NLS-1$
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            getFooter().add(cancelButton);
        }

        addDialogCloseActionListener(it -> cancel());
    }

    /**
     * Replies the text component.
     *
     * @return the text component.
     */
    public Span getTextComponent() {
        return this.text;
    }

    /**
     * Open the dialog and run the asynchronous task.
     */
    public void openAndRun() {
        if (startTask()) {
            open();
        }
    }

    /**
     * Start the execution of the parallel task.
     *
     * @return {@code true} if the asynchronous task was created; or {@code false} if the asynchronous task cannot be created.
     */
    protected boolean startTask() {
        final var progressWrapper = new ProgressAdapter(this.progressBar, this.text);
        try {
            this.task = this.taskProvider.apply(this.progressBar.getUI().orElse(null), progressWrapper);
            if (this.task != null) {
                this.task
                        .exceptionally(error -> onTaskErrorAndClose(error))
                        .thenAccept(data -> onTaskSuccessAndClose(data));
                return true;
            }
            onTaskErrorAndClose(new IllegalStateException());
        } catch (Throwable ex) {
            onTaskErrorAndClose(ex);
        }
        return false;
    }

    /**
     * Cancel the task.
     */
    public void cancel() {
        final var tsk = this.task;
        this.task = null;
        if (tsk != null) {
            tsk.cancel(true);
        }
        closeSafe();
    }

    private void closeSafe() {
        final var ui = getUI().orElse(null);
        if (ui != null) {
            ui.access(() -> {
                try {
                    close();
                } catch (Throwable ex) {
                    //
                }
            });
        }
    }

    /**
     * Invoked when the task is terminated on a success.
     *
     * @param result the result of the task running.
     */
    private void onTaskSuccessAndClose(T result) {
        closeSafe();
        if (!this.hasFailure.get()) {
            fireTaskSuccess(result);
        }
    }

    /**
     * Invoked when the task is terminated on a success.
     *
     * @param result the result of the task running.
     */
    protected void fireTaskSuccess(T result) {
        for (final var listener : this.successListeners) {
            listener.accept(result);
        }
    }

    /**
     * Add listener on success.
     *
     * @param listener the listener on success.
     */
    public final void addSuccessListener(SerializableConsumer<T> listener) {
        this.successListeners.add(listener);
    }

    /**
     * Invoked when the task is terminated on an error.
     *
     * @param error the error.
     */
    private T onTaskErrorAndClose(Throwable error) {
        this.hasFailure.set(true);
        closeSafe();
        fireTaskError(error);
        return null;
    }

    /**
     * Invoked when the task is terminated on an error.
     *
     * @param error the error.
     */
    protected void fireTaskError(Throwable error) {
        final List<SerializableConsumer<Throwable>> list;
        if (error instanceof CancellationException) {
            list = this.cancellationListeners;
        } else {
            list = this.failureListeners;
        }
        for (final var listener : list) {
            listener.accept(error);
        }
    }

    /**
     * Add listener on failure.
     *
     * @param listener the listener on failure.
     */
    public final void addFailureListener(SerializableConsumer<Throwable> listener) {
        this.failureListeners.add(listener);
    }

    /**
     * Add listener on cancellation.
     *
     * @param listener the listener on cancellation.
     */
    public final void addCancellationListener(SerializableConsumer<Throwable> listener) {
        this.cancellationListeners.add(listener);
    }

    /**
     * Type of cancellation that is supported by a {@link ProgressDialog}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public enum DialogCancelation {

        /**
         * Cancellation is possible with cancel button, click outside the dialog and Esc key.
         */
        CLICK_ESC_BUTTON,

        /**
         * Cancellation is possible with click outside the dialog and Esc key.
         */
        CLICK_ESC,

        /**
         * No cancellation is possible.
         */
        NO_CANCELLATION

    }

}
