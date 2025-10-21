/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
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

package fr.utbm.ciad.labmanager.views.components.addons.uploads.generic;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;

import java.io.*;

/**
 * A field that enables to upload a binary file to the server and shows its graphical representation
 * named the thumbnail..
 * This field does not assume that the field's data is of a specific type.
 * Subclasses must implement function to handle the upload file data.
 *
 * <p>CAUTION: Data is in memory only until the function {@link #saveUploadedFileOnServer(File)} is invoked.
 *
 * @param <T> the type of the values managed by this field.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractServerSideUploadableFileThumbnailField<T> extends AbstractServerSideUploadableFileImageViewerField<T> {

    private static final long serialVersionUID = -3720879038691372485L;

    private final DownloadableFileManager fileManager;

    private final SerializableSupplier<File> filenameSupplier;

    private byte[] thumbnail = null;

    /**
     * Default constructor.
     *
     * @param fileManager      the manager of the server-side files.
     * @param filenameSupplier provides the client-side name that should be considered as
     *                         the field's value for the uploaded file.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public AbstractServerSideUploadableFileThumbnailField(DownloadableFileManager fileManager,
                                                          SerializableSupplier<File> filenameSupplier, SerializableSupplier<Logger> loggerSupplier) {
        super(loggerSupplier);
        this.fileManager = fileManager;
        this.filenameSupplier = filenameSupplier;
    }

    /**
     * Constructor.
     *
     * @param fileManager      the manager of the server-side files.
     * @param filenameSupplier provides the client-side name that should be considered as
     *                         the field's value for the uploaded file.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public AbstractServerSideUploadableFileThumbnailField(DownloadableFileManager fileManager,
                                                          SerializableFunction<String, File> filenameSupplier, SerializableSupplier<Logger> loggerSupplier) {
        super(loggerSupplier);
        this.fileManager = fileManager;
        this.filenameSupplier = () -> {
            final var file = getClientSideFilename();
            final String ext;
            if (file != null) {
                ext = FileSystem.extension(file);
            } else {
                ext = FileManager.PDF_FILE_EXTENSION;
            }
            return filenameSupplier.apply(ext);
        };
    }

    /**
     * Replies the file manager for the server-side files.
     *
     * @return the file manager.
     */
    protected DownloadableFileManager getFileManager() {
        return this.fileManager;
    }

    /**
     * Replies the supplier of filename for the uploaded data.
     *
     * @return the filename supplier.
     */
    protected SerializableSupplier<File> getFilenameSupplier() {
        return this.filenameSupplier;
    }

    @Override
    protected void updateImage(Image viewer, Button clearButton) {
        final var fm = getFileManager();
        final var fns = getFilenameSupplier();
        final var mm = getMemoryReceiver();
        var defaultImage = true;
        StreamResource source = null;
        if (fm != null && mm != null && fns != null) {
            final var filename = fns.get();
            final var thumbnailName = toClientSideThumbnailFile(filename);
            try (final var thumbnailStream = new ByteArrayOutputStream()) {
                try (final var fileStream = mm.getInputStream()) {
                    fm.generateThumbnail(filename.getName(), fileStream, thumbnailStream, getLogger());
                }
                //
                thumbnailStream.flush();
                this.thumbnail = thumbnailStream.toByteArray();
                //
                final InputStreamFactory factory = () -> {
                    return new ByteArrayInputStream(this.thumbnail);
                };
                source = new StreamResource(thumbnailName.getName(), factory);
                clearButton.setEnabled(true);
                defaultImage = false;
            } catch (IOException ex) {
                //
            }
        }
        if (defaultImage) {
            source = ComponentFactory.newEmptyBackgroundStreamImage();
            clearButton.setEnabled(false);
        }
        viewer.setSrc(source);
    }

    @Override
    protected void resetProperties() {
        super.resetProperties();
        this.thumbnail = null;
    }

    /**
     * Replies the filename of the thumbnail that corresponds to the given filename.
     *
     * @param clientPath the input filename, using client-side notation.
     * @return the thumbnail filename, with client-side notation.
     */
    protected File toClientSideThumbnailFile(File clientPath) {
        final var fm = getFileManager();
        if (fm != null) {
            final var thumbnail = fm.toThumbnailFilename(clientPath);
            return thumbnail;
        }
        return null;
    }

    /**
     * Replies the filename of the thumbnail that corresponds to the given filename.
     *
     * @param clientPath the input filename, using client-side notation.
     * @return the thumbnail filename, with server-side notation.
     */
    protected File toServerSideThumbnailFile(File clientPath) {
        final var fm = getFileManager();
        if (fm != null) {
            final var thumbnail = fm.toThumbnailFilename(clientPath);
            final var targetFile = fm.normalizeForServerSide(thumbnail);
            return targetFile;
        }
        return null;
    }

    @Override
    public void saveUploadedFileOnServer(File output) throws IOException {
        super.saveUploadedFileOnServer(output);
        // Generate the thumbnail for the uploaded file
        if (this.thumbnail != null) {
            final var thumbnailFile = toServerSideThumbnailFile(output);
            try (final var outputStream = new FileOutputStream(thumbnailFile)) {
                try (final var inputStream = new ByteArrayInputStream(this.thumbnail)) {
                    inputStream.transferTo(outputStream);
                }
            }
        } else {
            final var fm = getFileManager();
            if (fm != null) {
                fm.regenerateThumbnail(output, getLogger());
            }
        }
    }

    @Override
    public void saveUploadedFileOnServer() throws IOException {
        final var filename = getFileManager().normalizeForServerSide(getFilenameSupplier().get());
        saveUploadedFileOnServer(filename);
    }

}