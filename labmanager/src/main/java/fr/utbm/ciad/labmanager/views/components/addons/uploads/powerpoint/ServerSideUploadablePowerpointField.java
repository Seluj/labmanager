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

package fr.utbm.ciad.labmanager.views.components.addons.uploads.powerpoint;

import com.google.common.base.Strings;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.arakhne.afc.vmutil.FileSystem;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * A field that enables to upload and show a Powerpoint file, and to write the file in a
 * folder of the server.
 * This field assumes that the data linked to the backend JPA is the relative server-side filename.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ServerSideUploadablePowerpointField extends AbstractServerSideUploadablePowerpointField<String> implements HasAsynchronousUploadService {

    private static final long serialVersionUID = 6353874816079573524L;

    private String lastJpaData = ""; //$NON-NLS-1$

    /**
     * Constructor.
     *
     * @param fileManager      the manager of the server-side files.
     * @param filenameSupplier provides the client-side name that should be considered as
     *                         the field's value for the uploaded file.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public ServerSideUploadablePowerpointField(DownloadableFileManager fileManager, SerializableSupplier<File> filenameSupplier,
                                               SerializableSupplier<Logger> loggerSupplier) {
        super(fileManager, filenameSupplier, loggerSupplier);
    }

    /**
     * Constructor.
     *
     * @param fileManager      the manager of the server-side files.
     * @param filenameSupplier provides the client-side name that should be considered as
     *                         the field's value for the uploaded file.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public ServerSideUploadablePowerpointField(DownloadableFileManager fileManager, SerializableFunction<String, File> filenameSupplier,
                                               SerializableSupplier<Logger> loggerSupplier) {
        super(fileManager, filenameSupplier, loggerSupplier);
    }

    @Override
    protected void resetProperties() {
        super.resetProperties();
        this.lastJpaData = ""; //$NON-NLS-1$
    }

    @Override
    protected void uploadSucceeded(String filename) {
        super.uploadSucceeded(filename);
        updateValue();
    }

    @Override
    protected void imageCleared() {
        super.imageCleared();
        updateValue();
    }

    @Override
    public void updateValue() {
        // Overridden for increasing the visibility of this function
        super.updateValue();
    }

    @Override
    protected String generateModelValue() {
        if (hasUploadedData()) {
            this.lastJpaData = getFilenameSupplier().get().toString();
        }
        return this.lastJpaData;
    }

    @Override
    protected void setPresentationValue(String newPresentationValue) {
        resetProperties();
        resetUi();
        this.lastJpaData = Strings.nullToEmpty(newPresentationValue);
        if (!Strings.isNullOrEmpty(newPresentationValue)) {
            final var file = FileSystem.convertStringToFile(newPresentationValue);
            final var thumbnailFile = toServerSideThumbnailFile(file);
            if (thumbnailFile != null) {
                setImageSource(thumbnailFile);
            }
        }
    }

    @Override
    public void saveUploadedFileOnServer() throws IOException {
        final var filename = getFileManager().normalizeForServerSide(getFilenameSupplier().get());
        saveUploadedFileOnServer(filename);
    }

}