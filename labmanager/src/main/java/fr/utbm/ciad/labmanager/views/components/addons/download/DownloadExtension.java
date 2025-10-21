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

package fr.utbm.ciad.labmanager.views.components.addons.download;

import com.google.common.base.Strings;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.StreamResource;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionFunction;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressExtension;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.FileSystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.function.Supplier;

/**
 * Extension of a component that is clickable in order to start asynchronous a task and download the result from the client browser.
 * In order to enable the downloading, it is important that the component is visible during the download process. Otherwise there is a risk to
 * have the error "data nor available on server".
 *
 * @param <C> the type of the extended component that must be a valid Vaadin component and a received of click event.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DownloadExtension<C extends Component & ClickNotifier<C>> implements Serializable {

    private static final long serialVersionUID = 755789822679733519L;

    private static final int CHILD_PROGRESS_SIZE = 200;

    private static final int EXTRA_PROGRESS_SIZE = 10;

    private static final String DEFAULT_FILE_NAME = "download.bin"; //$NON-NLS-1$

    private static final String DEFAULT_FILE_TYPE = "application/octet-stream"; //$NON-NLS-1$

    private static final SerializableSupplier<String> DEFAULT_FILE_NAME_SUPPLIER = () -> DEFAULT_FILE_NAME;

    private static final SerializableSupplier<String> DEFAULT_FILE_TYPE_SUPPLIER = () -> DEFAULT_FILE_TYPE;

    private final ProgressExtension<String, C> progress;

    private SerializableExceptionFunction<Progression, InputStream> inputStreamFactory;

    private SerializableExceptionFunction<Progression, DownloadableFileDescription> downloadableFileDescription;

    private SerializableExceptionFunction<Progression, StreamResource> streamResourceSupplier;

    private SerializableSupplier<String> fileNameSupplier = DEFAULT_FILE_NAME_SUPPLIER;

    private SerializableSupplier<String> fileTypeSupplier = DEFAULT_FILE_TYPE_SUPPLIER;

    /**
     * Constructor.
     *
     * @param component the component to extend.
     */
    protected DownloadExtension(C component) {
        assert component != null;
        this.progress = ProgressExtension.<String, C>extend(component)
                .withAsyncTask((ui, progress) -> {
                    final var progression = new DefaultProgression(0, CHILD_PROGRESS_SIZE + EXTRA_PROGRESS_SIZE);
                    if (progress != null) {
                        progression.addProgressionListener(progress);
                    }

                    String name;

                    try {
                        // Create href object for representing the downloadable file
                        final var subTask = progression.subTask(CHILD_PROGRESS_SIZE);
                        final var href = buildStreamResource(subTask);
                        href.setCacheTime(0);
                        subTask.end();

                        final Anchor anchor = new Anchor();
                        final var anchorElement = anchor.getElement();
                        anchorElement.setAttribute("download", true); //$NON-NLS-1$
                        anchorElement.getStyle().set("display", "none"); //$NON-NLS-1$ //$NON-NLS-2$

                        // Run the UI-dependent code
                        final var ui0 = ui == null ? component.getUI().orElse(null) : ui;
                        if (ui0 != null) {
                            ui0.access(() -> {
                                getAnchorReceiver(component).appendChild(anchorElement);
                                anchor.setHref(href);
                                anchorElement.callJsFunction("click"); //$NON-NLS-1$
                            });
                        }
                        name = href.getName();
                    } catch (RuntimeException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                    progression.end();

                    return name;
                });
    }

    /**
     * Extends the given component to enable asynchronous download of data to a client-side file.
     *
     * @param <C>       the type of the component that must be a valid Vaadin component and a received of click event.
     * @param component the component to extend.
     * @return the extension.
     */
    public static <C extends Component & ClickNotifier<C>> DownloadExtension<C> extend(C component) {
        return new DownloadExtension<>(component);
    }

    private StreamResource buildStreamResource(Progression progression) throws Exception {
        if (this.streamResourceSupplier != null) {
            final var href = this.streamResourceSupplier.apply(progression);
            if (href == null) {
                // Do nothing because the stream factory does not create a stream.
                throw new CancellationException();
            }
            if (this.fileTypeSupplier != null) {
                final var mime = this.fileTypeSupplier.get();
                if (!Strings.isNullOrEmpty(mime)) {
                    href.setContentType(mime);
                }
            }
            return href;
        }
        if (this.downloadableFileDescription != null) {
            final var description = this.downloadableFileDescription.apply(progression);
            if (description == null) {
                // Do nothing because the stream factory does not create a description.
                throw new CancellationException();
            }
            String filename = this.fileNameSupplier.get();
            final var extension = description.filenameExtension();
            if (!Strings.isNullOrEmpty(extension)) {
                var url = FileSystem.convertStringToURL(filename, false);
                url = FileSystem.replaceExtension(url, extension);
                final var file = FileSystem.convertURLToFile(url);
                filename = file.getName();
            }
            final var href = new StreamResource(filename, () -> new ByteArrayInputStream(description.content() == null ? new byte[0] : description.content()));
            String mime = null;
            if (description.mime() != null) {
                mime = description.mime().toString();
            } else if (this.fileTypeSupplier != null) {
                mime = this.fileTypeSupplier.get();
            }
            if (!Strings.isNullOrEmpty(mime)) {
                href.setContentType(mime);
            }
            return href;
        }
        if (this.inputStreamFactory != null) {
            final var inputStream = this.inputStreamFactory.apply(progression);
            if (inputStream == null) {
                // Do nothing because the stream factory does not create a stream.
                throw new CancellationException();
            }
            final var href = new StreamResource(this.fileNameSupplier.get(), () -> inputStream);
            if (this.fileTypeSupplier != null) {
                final var mime = this.fileTypeSupplier.get();
                if (!Strings.isNullOrEmpty(mime)) {
                    href.setContentType(mime);
                }
            }
            return href;
        }
        throw new IllegalStateException();
    }

    /**
     * Replies the component that is receiving the anchor. This component must be not clikable to avoid infinite loop.
     *
     * @param reference the linked component.
     * @return the anchor receiver, never {@code null}.
     */
    @SuppressWarnings("static-method")
    protected Element getAnchorReceiver(Component reference) {
        if (reference instanceof MenuItem item) {
            // In the case of a menu item, the anchor receiver is the parent of the menu bar
            Optional<Component> parent = item.getParent();
            while (parent.isPresent() && !(parent.get() instanceof MenuBar)) {
                parent = parent.get().getParent();
            }
            return parent.orElseThrow().getParent().orElseThrow().getElement();
        }
        return reference.getParent().orElseThrow().getElement();
    }

    /**
     * Change the supplier of stream resource. A call to this function will cancel any previous call to {@link #withFilename(Supplier)} and
     * {@link #withMimeType(SerializableSupplier)}.
     *
     * @param supplier the supplier. A lambda that replies the stream resource of the content of the downloadable file.
     * @return {@code this}
     */
    public DownloadExtension<C> withStreamResource(SerializableExceptionFunction<Progression, StreamResource> supplier) {
        this.streamResourceSupplier = supplier;
        return this;
    }

    /**
     * Change the supplier of name for the downloadable file.
     *
     * @param supplier the supplier. A lambda that replies the filename. If this argument is {@code null}, the default supplier is used.
     * @return {@code this}
     */
    public DownloadExtension<C> withFilename(SerializableSupplier<String> supplier) {
        this.fileNameSupplier = supplier == null ? DEFAULT_FILE_NAME_SUPPLIER : supplier;
        return this;
    }

    /**
     * Change the supplier of MIME type for the downloadable file.
     *
     * @param supplier the supplier. A lambda that replies the MIME type. If this argument is {@code null}, the default supplier is used.
     * @return {@code this}
     */
    public DownloadExtension<C> withMimeType(SerializableSupplier<String> supplier) {
        this.fileTypeSupplier = supplier == null ? DEFAULT_FILE_TYPE_SUPPLIER : supplier;
        return this;
    }

    /**
     * Change the factory for the input stream of the content of the downloadable file.
     *
     * @param factory the object that is able to create the input stream to the client.
     * @return {@code this}
     */
    public DownloadExtension<C> withInputStreamFactory(SerializableExceptionFunction<Progression, InputStream> factory) {
        this.inputStreamFactory = factory;
        return this;
    }

    /**
     * Change the factory for the input stream of the content of the downloadable file. This function reads the input stream
     * and set the {@link #withMimeType(SerializableSupplier) MIME type} and add extension to the {@link #withFilename(SerializableSupplier) filename}.
     *
     * @param factory the object that is able to create the input stream to the client.
     * @return {@code this}
     */
    public DownloadExtension<C> withFileDescription(SerializableExceptionFunction<Progression, DownloadableFileDescription> factory) {
        this.downloadableFileDescription = factory;
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon the icon component.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> DownloadExtension<C> withProgressIcon(CC icon) {
        this.progress.withProgressIcon(icon);
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @param unit     the unit of the given {@code minWidth}.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> DownloadExtension<C> withProgressIcon(CC icon, float minWidth, Unit unit) {
        this.progress.withProgressIcon(icon, minWidth, unit);
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @return {@code this}.
     */
    public <CC extends Component & HasSize> DownloadExtension<C> withProgressIcon(CC icon, String minWidth) {
        this.progress.withProgressIcon(icon, minWidth);
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon the icon component.
     * @return {@code this}.
     */
    public DownloadExtension<C> withProgressIcon(AbstractIcon<?> icon) {
        this.progress.withProgressIcon(icon);
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @param unit     the unit of the given {@code minWidth}.
     * @return {@code this}.
     */
    public DownloadExtension<C> withProgressIcon(AbstractIcon<?> icon, float minWidth, Unit unit) {
        this.progress.withProgressIcon(icon, minWidth, unit);
        return this;
    }

    /**
     * Change the icon of the progress window.
     *
     * @param icon     the icon component.
     * @param minWidth the minimal width of the icon.
     * @return {@code this}.
     */
    public DownloadExtension<C> withProgressIcon(AbstractIcon<?> icon, String minWidth) {
        this.progress.withProgressIcon(icon, minWidth);
        return this;
    }

    /**
     * Change the title of the progress window.
     *
     * @param title the title.
     * @return {@code this}.
     */
    public DownloadExtension<C> withProgressTitle(String title) {
        this.progress.withProgressTitle(title);
        return this;
    }

    /**
     * Add a listener on the error completion of the download task.
     *
     * @param listener the lambda function that must be invoked when the task has failed.
     * @return {@code this}.
     */
    public DownloadExtension<C> withFailureListener(SerializableConsumer<Throwable> listener) {
        this.progress.withFailureListener(listener);
        return this;
    }

    /**
     * Add a listener on the success completion of the download.
     *
     * @param listener the lambda function that must be invoked when the task is successfully terminated. The argument of the lambda is the base of the downloaded file.
     * @return {@code this}.
     */
    public DownloadExtension<C> withSuccessListener(SerializableConsumer<String> listener) {
        this.progress.withSuccessListener(listener);
        return this;
    }

    /**
     * Add a listener on the cancellation of the download.
     *
     * @param listener the lambda function that must be invoked when the task has been cancelled.
     * @return {@code this}.
     */
    public DownloadExtension<C> withCancellationListener(SerializableConsumer<Throwable> listener) {
        this.progress.withCancellationListener(listener);
        return this;
    }

}
