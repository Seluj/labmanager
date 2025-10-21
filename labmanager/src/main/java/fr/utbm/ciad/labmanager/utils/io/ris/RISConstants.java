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

package fr.utbm.ciad.labmanager.utils.io.ris;

import org.springframework.http.MediaType;

/**
 * Constants for RIS.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.8
 */
public interface RISConstants {

    /**
     * Mime type for RIS.
     */
    String MIME_TYPE_VALUE = "text/x-research-info-systems"; //$NON-NLS-1$

    /**
     * Mime type and charset (UTF-8) for RIS.
     */
    String MIME_TYPE_UTF8_VALUE = MIME_TYPE_VALUE + ";charset=utf-8"; //$NON-NLS-1$
    /**
     * Mime type and charset (UTF-8) for RIS.
     */
    MediaType MIME_TYPE_UTF8 = MediaType.parseMediaType(MIME_TYPE_UTF8_VALUE);
    /**
     * Mime type for RIS.
     */
    MediaType MIME_TYPE = MediaType.parseMediaType(MIME_TYPE_VALUE);
    /**
     * Standard filename extension for RIS files.
     *
     * @since 4.0
     */
    String FILENAME_EXTENSION = ".ris"; //$NON-NLS-1$

}
