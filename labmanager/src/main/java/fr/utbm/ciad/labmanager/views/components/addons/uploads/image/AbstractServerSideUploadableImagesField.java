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
import fr.utbm.ciad.labmanager.views.components.addons.uploads.generic.AbstractUploadableFilesViewerField;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A field that enables to upload an image and show the image representations of the uploaded files.
 * This field does not assume that the field's data is of a specific type.
 * Subclasses must implement function to handle the upload file data.
 *
 * @param <T> the type of the values managed by this field.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractServerSideUploadableImagesField<T> extends AbstractUploadableFilesViewerField<T>
        implements ServerSideUploadableImageConstants {

    private static final long serialVersionUID = 7227372185378315410L;

    /**
     * Constructor with an image renderer. The rendering of the image is based on
     * the function {@link #renderImage(Object)}.
     *
     * @param filenameSupplier the supplier of the filenames. First argument is the index of the image.
     *                         Second argument is the filename extension. It returns the filename.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public AbstractServerSideUploadableImagesField(SerializableBiFunction<Integer, String, File> filenameSupplier,
                                                   SerializableSupplier<Logger> loggerSupplier) {
        super(filenameSupplier, loggerSupplier);
        setAcceptedFileTypes(DEFAULT_ACCEPTED_MIME_TYPES);
    }

    /**
     * Save the uploaded data on the server file system.
     *
     * @param outputFile output filename for the uploaded data.
     * @param buffer     the buffer that contains the uploaded data to be saved on the server.
     * @throws IOException if the data cannot be saved.
     */
    @SuppressWarnings("static-method")
    protected void saveUploadedFileOnServer(File outputFile, ResetableMemoryBuffer buffer) throws IOException {
        assert buffer != null;
        if (buffer.hasFileData()) {
            outputFile.getParentFile().mkdirs();
            try (final var outputStream = new FileOutputStream(outputFile)) {
                try (final var inputStream = buffer.getInputStream()) {
                    inputStream.transferTo(outputStream);
                }
            }
        }
    }

}