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

package fr.utbm.ciad.labmanager.views.components.addons.uploads.image;

import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableSupplier;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A field that enables to upload and show images, and to write the images in a
 * folder of the server.
 * This field assumes that the data linked to the backend JPA is the relative server-side filenames.
 *
 * <p>CAUTION: Data is in memory only until the function {@link #saveUploadedFileOnServer(File)} is invoked.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ServerSideUploadableImagesField extends AbstractServerSideUploadableImagesField<String> implements HasAsynchronousUploadService {

    private static final long serialVersionUID = -3231549542016073846L;

    private final FileManager fileManager;

    /**
     * Constructor.
     *
     * @param fileManager      the manager of the server-side files.
     * @param filenameSupplier provides the client-side name that should be considered as
     *                         the field's value for the uploaded file. The argument of the lambda is the filename extension.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public ServerSideUploadableImagesField(FileManager fileManager, SerializableBiFunction<Integer, String, File> filenameSupplier,
                                           SerializableSupplier<Logger> loggerSupplier) {
        super((index, file) -> {
            final String ext;
            if (file != null) {
                ext = FileSystem.extension(file);
            } else {
                ext = FileManager.JPEG_FILE_EXTENSION;
            }
            return filenameSupplier.apply(index, ext);
        }, loggerSupplier);
        this.fileManager = fileManager;
    }

    @Override
    protected void uploadSucceeded(String filename) {
        super.uploadSucceeded(filename);
        updateValue();
    }

    @Override
    protected void thumbnailRemoved() {
        super.thumbnailRemoved();
        updateValue();
    }

    @Override
    public void updateValue() {
        // Overridden for increasing the visibility of this function
        super.updateValue();
    }

    @Override
    public void saveUploadedFileOnServer() throws IOException {
        for (final var thumbnail : getThumbnails().toList()) {
            final var buffer = thumbnail.getMemoryBuffer();
            if (buffer != null) {
                final var publicFilename = new File(thumbnail.getName());
                final var serverFilename = this.fileManager.normalizeForServerSide(publicFilename);
                saveUploadedFileOnServer(serverFilename, buffer);
                thumbnail.setImageFromServerFilename(serverFilename);
            }
        }
    }

    @Override
    protected List<String> generateModelValue() {
        final var list = getThumbnails().map(it -> it.getName()).toList();
        return list;
    }

    private Thumbnail createThumbnail(String filename) {
        final var file = FileSystem.convertStringToFile(filename);
        final var targetFile = this.fileManager.normalizeForServerSide(file);
        if (targetFile != null) {
            return new Thumbnail(file, targetFile);
        }
        return null;
    }

    @Override
    protected void setPresentationValue(List<String> newPresentationValue) {
        removeAllThumbnails();
        if (!newPresentationValue.isEmpty()) {
            for (final var thumbnail : newPresentationValue.stream().map(this::createThumbnail).filter(it -> it != null).toList()) {
                addThumbnail(thumbnail);
            }
        }
    }

}