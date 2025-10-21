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

package fr.utbm.ciad.labmanager.utils;

import java.io.IOException;
import java.io.Serializable;

/**
 * Interface that provides a function for updating a value in the implementation object
 * and saving any uploaded data in memory.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface HasAsynchronousUploadService extends Serializable {

    /**
     * Update the value stored in the memory of the object.
     */
    void updateValue();

    /**
     * Save any uploaded data in memory to a file.
     *
     * <p>The target filename is decied by the implementation object.
     *
     * @throws IOException if the files cannot be saved on the server.
     */
    void saveUploadedFileOnServer() throws IOException;

}
