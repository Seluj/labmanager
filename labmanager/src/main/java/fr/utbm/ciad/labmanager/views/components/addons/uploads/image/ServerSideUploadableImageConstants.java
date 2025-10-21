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

package fr.utbm.ciad.labmanager.views.components.addons.uploads.image;

/**
 * Constants for the components to upload images.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface ServerSideUploadableImageConstants {

    /**
     * Default accepted MIME types.
     */
    String[] DEFAULT_ACCEPTED_MIME_TYPES = new String[]{
            ".png", //$NON-NLS-1$
            "image/png", //$NON-NLS-1$
            ".jpg", //$NON-NLS-1$
            ".jpeg", //$NON-NLS-1$
            "image/jpeg", //$NON-NLS-1$
            ".svg", //$NON-NLS-1$
            "image/svg+xml" //$NON-NLS-1$
    };

}