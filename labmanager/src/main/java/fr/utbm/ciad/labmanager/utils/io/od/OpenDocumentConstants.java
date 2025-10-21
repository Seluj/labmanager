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

package fr.utbm.ciad.labmanager.utils.io.od;

import org.odftoolkit.odfdom.type.Color;
import org.springframework.http.MediaType;

/**
 * Constants for Open Document standard.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface OpenDocumentConstants {

    /**
     * Mime type for ODT.
     */
    String ODT_MIME_TYPE_VALUE = "application/vnd.oasis.opendocument.text"; //$NON-NLS-1$

    /**
     * Mime type for ODT.
     */
    MediaType ODT_MIME_TYPE = MediaType.parseMediaType(ODT_MIME_TYPE_VALUE);

    /**
     * Green color for CIAD lab.
     */
    Color CIAD_GREEN = Color.valueOf("#95bc0f"); //$NON-NLS-1$

    /**
     * Dark green color for CIAD lab.
     */
    Color CIAD_DARK_GREEN = Color.valueOf("#4b5e08"); //$NON-NLS-1$

    /**
     * Relative path to the ODT document that is a template for the RIPEC-C3 activity report.
     *
     * @since 3.7
     */
    String ODT_REPEC_C3_TEMPLATE_PATH = "odt/ripec_c3_template.odt"; //$NON-NLS-1$

}
