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
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;

import java.io.File;
import java.io.OutputStream;

/**
 * Abstract implementation of a field that enables to upload a file.
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
public abstract class AbstractUploadableFileField<T> extends AbstractBaseUploadableFilesField<T> {

    private static final long serialVersionUID = 3961496150989387624L;
    private final ResetableMemoryBuffer receiver = new ResetableMemoryBuffer();

    /**
     * Default constructor.
     *
     * @param loggerSupplier the dynamic supplier of the loggers.
     */
    public AbstractUploadableFileField(SerializableSupplier<Logger> loggerSupplier) {
        super(1, loggerSupplier);
    }

    /**
     * Replies the name of the uploaded file on the client computer.
     *
     * @return the name on the client computer.
     */
    public File getClientSideFilename() {
        return FileSystem.convertStringToFile(getMemoryReceiver().getFileName());
    }

    @Override
    public boolean hasUploadedData() {
        return getMemoryReceiver().hasFileData();
    }

    @SuppressWarnings("resource")
    @Override
    protected OutputStream receiveUpload(String filename, String mime) {
        return uploadStreamOpen(getMemoryReceiver().receiveUpload(filename, mime), filename, mime);
    }

    /**
     * Replies the receiver of the uploaded file data.
     *
     * @return the receiver.
     */
    protected ResetableMemoryBuffer getMemoryReceiver() {
        return this.receiver;
    }

    @Override
    protected void resetUploader() {
        super.resetUploader();
        this.receiver.reset();
    }

}