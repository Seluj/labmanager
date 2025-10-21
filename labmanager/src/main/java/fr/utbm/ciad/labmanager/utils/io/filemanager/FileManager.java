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

package fr.utbm.ciad.labmanager.utils.io.filemanager;

import java.io.File;
import java.io.Serializable;

/**
 * Utilities for managing the files.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface FileManager extends Serializable {

    /**
     * JPEG filename extension.
     */
    String JPEG_FILE_EXTENSION = ".jpg"; //$NON-NLS-1$

    /**
     * PDF filename extension.
     */
    String PDF_FILE_EXTENSION = ".pdf"; //$NON-NLS-1$

    /**
     * Normalize a relative filename to be absolute for the server.
     *
     * @param file the relative filename.
     * @return the absolute filename.
     */
    File normalizeForServerSide(File file);

}
