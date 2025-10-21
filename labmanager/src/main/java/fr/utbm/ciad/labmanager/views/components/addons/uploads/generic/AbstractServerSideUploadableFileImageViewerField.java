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

import com.vaadin.flow.function.SerializableSupplier;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A field that enables to upload a file and show an image representation, and to write the uploaded file in a
 * folder of the server.
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
public abstract class AbstractServerSideUploadableFileImageViewerField<T> extends AbstractUploadableFileImageViewerField<T> {

    private static final long serialVersionUID = 598244456800081674L;

    /**
     * Default constructor.
     *
     * @param loggerSupplier the dynamic supplier of the loggers.
     */
    public AbstractServerSideUploadableFileImageViewerField(SerializableSupplier<Logger> loggerSupplier) {
        super(loggerSupplier);
    }

    /**
     * Change the image that is displayed in the viewer.
     *
     * @param path the path on the server.
     */
    protected void setImageSource(File serverPath) {
        if (serverPath != null) {
            setImageSource(ComponentFactory.newStreamImage(serverPath));
        }
    }

    /**
     * Save the uploaded data on the server file system.
     *
     * @throws IOException if the data cannot be saved.
     */
    public abstract void saveUploadedFileOnServer() throws IOException;

    /**
     * Save the uploaded data on the server file system.
     *
     * @param outputFile output filename for the uploaded data.
     * @throws IOException if the data cannot be saved.
     */
    public void saveUploadedFileOnServer(File outputFile) throws IOException {
        if (hasUploadedData()) {
            final var buffer = getMemoryReceiver();
            if (buffer == null) {
                throw new IOException("No memory buffer"); //$NON-NLS-1$
            }
            outputFile.getParentFile().mkdirs();
            try (final var outputStream = new FileOutputStream(outputFile)) {
                try (final var inputStream = buffer.getInputStream()) {
                    inputStream.transferTo(outputStream);
                }
            }
        }
    }

}