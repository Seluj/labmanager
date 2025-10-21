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

import com.vaadin.flow.server.StreamResource;

import java.io.InputStream;

/**
 * Memory buffer for upload of files.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface UploadBuffer {

    /**
     * Get the file name for this buffer.
     *
     * @return file name or empty if no file
     */
    String getFileName();

    /**
     * Get the input stream for file with filename.
     *
     * @return input stream for file or empty stream if file not found
     */
    InputStream getInputStream();

    /**
     * Replies if the buffer has file data.
     *
     * @return {@code true} if data is available.
     */
    boolean hasFileData();


    /**
     * Create a stream resource for the upload image.
     * This function should be inoked when the file is uploaded.
     *
     * @return the stream resource for the uploaded file.
     */
    StreamResource createStreamResource();

}
