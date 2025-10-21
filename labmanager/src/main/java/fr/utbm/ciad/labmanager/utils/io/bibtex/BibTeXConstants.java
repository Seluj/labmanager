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

package fr.utbm.ciad.labmanager.utils.io.bibtex;

import org.springframework.http.MediaType;

/**
 * Constants for BibTeX.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface BibTeXConstants {

    /**
     * Mime type for BibTeX.
     */
    String MIME_TYPE_VALUE = "application/x-bibtex"; //$NON-NLS-1$

    /**
     * Mime type and charset (UTF-8) for BibTeX.
     */
    String MIME_TYPE_UTF8_VALUE = MIME_TYPE_VALUE + ";charset=utf-8"; //$NON-NLS-1$
    /**
     * Mime type and charset (UTF-8) for BibTeX.
     */
    MediaType MIME_TYPE_UTF8 = MediaType.parseMediaType(MIME_TYPE_UTF8_VALUE);
    /**
     * Mime type for BibTeX.
     */
    MediaType MIME_TYPE = MediaType.parseMediaType(MIME_TYPE_VALUE);
    /**
     * Standard filename extension for BibTeX files.
     *
     * @since 4.0
     */
    String FILENAME_EXTENSION = ".bib"; //$NON-NLS-1$

}
