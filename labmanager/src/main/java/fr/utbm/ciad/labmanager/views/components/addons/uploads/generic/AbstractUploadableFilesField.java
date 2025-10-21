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
import org.slf4j.Logger;

import java.io.OutputStream;
import java.util.*;

/**
 * Abstract implementation of a field that enables to upload files.
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
public abstract class AbstractUploadableFilesField<T> extends AbstractBaseUploadableFilesField<List<T>> {

    /**
     * Maximum number of files to be uploaded.
     */
    public static final int MAX_NUMBER_OF_FILES = 20;
    private static final long serialVersionUID = -8898340570308261699L;
    private final Map<String, ResetableMemoryBuffer> uploadReceivers = new TreeMap<>();

    /**
     * Default constructor.
     *
     * @param loggerSupplier the dynamic supplier of the loggers.
     */
    public AbstractUploadableFilesField(SerializableSupplier<Logger> loggerSupplier) {
        super(MAX_NUMBER_OF_FILES, loggerSupplier);
    }

    @Override
    public boolean hasUploadedData() {
        synchronized (this.uploadReceivers) {
            return this.uploadReceivers.values().stream().anyMatch(it -> it.hasFileData());
        }
    }

    /**
     * Replies the filenames of all the uploaded files.
     *
     * @return the filenames.
     */
    protected Collection<String> getUploadedFilenames() {
        synchronized (this.uploadReceivers) {
            return Collections.unmodifiableCollection(this.uploadReceivers.keySet());
        }
    }

    /**
     * Replies the buffers of all the uploaded files.
     *
     * @return the buffers.
     */
    protected List<UploadBuffer> getUploadBuffers() {
        synchronized (this.uploadReceivers) {
            final var list = new ArrayList<UploadBuffer>();
            list.addAll(this.uploadReceivers.values());
            return list;
        }
    }

    /**
     * Change the buffers of all the uploaded files. This function considers only the instances of {@link ResetableMemoryBuffer}.
     *
     * @param buffers the buffers.
     */
    protected void setUploadBuffers(List<? extends UploadBuffer> buffers) {
        synchronized (this.uploadReceivers) {
            this.uploadReceivers.clear();
            if (buffers != null && !buffers.isEmpty()) {
                buffers.forEach(it -> {
                    if (it instanceof ResetableMemoryBuffer buffer) {
                        this.uploadReceivers.put(buffer.getFileName(), buffer);
                    }
                });
            }
        }
    }

    /**
     * Replies the buffer that is used for the uploaded.
     *
     * @param filename the filename of the buffer.
     * @return the buffer.
     */
    protected ResetableMemoryBuffer getUploadMemoryBufferFor(String filename) {
        synchronized (this.uploadReceivers) {
            return this.uploadReceivers.get(filename);
        }
    }

    /**
     * Reset the buffer that is used for the uploaded.
     *
     * @param filename            the filename for the buffer.
     * @param resetInternalBuffer indicates if data in the buffer is also cleared ({@code true}).
     */
    protected void resetUploadMemoryBuffer(String filename, boolean resetInternalBuffer) {
        synchronized (this.uploadReceivers) {
            final var buffer = this.uploadReceivers.remove(filename);
            if (buffer != null && resetInternalBuffer) {
                buffer.reset();
            }
        }
    }

    /**
     * Reset all the buffers that is used for the uploaded.
     */
    protected void resetAllUploadMemoryBuffers() {
        synchronized (this.uploadReceivers) {
            for (final var buffer : this.uploadReceivers.values()) {
                buffer.reset();
            }
            this.uploadReceivers.clear();
        }
    }

    @Override
    protected synchronized void uploadFailed(String filename, Throwable error) {
        resetUploadMemoryBuffer(filename, true);
        super.uploadFailed(filename, error);
    }

    @SuppressWarnings("resource")
    @Override
    protected OutputStream receiveUpload(String filename, String mime) {
        final var uploadReceiver = new ResetableMemoryBuffer();
        synchronized (this.uploadReceivers) {
            this.uploadReceivers.put(filename, uploadReceiver);
        }
        return uploadStreamOpen(uploadReceiver.receiveUpload(filename, mime), filename, mime);
    }

    @Override
    protected void resetUploader() {
        super.resetUploader();
        resetAllUploadMemoryBuffers();
    }

}