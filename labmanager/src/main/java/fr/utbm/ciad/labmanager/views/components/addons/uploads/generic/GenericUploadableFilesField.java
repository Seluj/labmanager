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

import java.util.List;

/**
 * A field that enables to upload files (whatever the content), and provides the file bytes.
 * This field does not assumes that the data is linked to a backend JPA.
 *
 * <p>CAUTION: Data is in memory only until the subclasses are using this stream.
 *
 * <p>To specify the type of uploadable file, it is recommended to provide file extensions
 * to the constructor or to invoke {@link #setAcceptedFileTypes(String...)}.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class GenericUploadableFilesField extends AbstractUploadableFilesField<UploadBuffer> {

    private static final long serialVersionUID = -7315122911322363884L;

    /**
     * Constructor.
     *
     * @param loggerSupplier the dynamic supplier of the loggers.
     * @param fileExtensions the file extensions that are accepted by the field. Providing
     *                       this argument is equivalent to a call to {@link #setAcceptedFileTypes(String...)}.
     */
    public GenericUploadableFilesField(SerializableSupplier<Logger> loggerSupplier, String... fileExtensions) {
        super(loggerSupplier);
        if (fileExtensions != null && fileExtensions.length > 0) {
            setAcceptedFileTypes(fileExtensions);
        }
    }

    @Override
    protected void uploadSucceeded(String filename) {
        updateValue();
    }

    @Override
    protected List<UploadBuffer> generateModelValue() {
        return getUploadBuffers();
    }

    @Override
    protected void setPresentationValue(List<UploadBuffer> newPresentationValue) {
        setUploadBuffers(newPresentationValue);
    }

}