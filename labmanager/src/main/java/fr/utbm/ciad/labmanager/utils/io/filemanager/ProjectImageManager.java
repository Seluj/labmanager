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

import fr.utbm.ciad.labmanager.data.project.Project;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;

/**
 * Utilities for managing the project images.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface ProjectImageManager extends FileManager {

    /**
     * Replies the path to the thumbnail image for the given project.
     * Depending on the implementation, the thumbnail image may be generated on the fly.
     *
     * @param project the project for which the thumbnail image must be generated.
     * @param logger  the logger to be used for output the log messages.
     * @return the path to the thumbnail image or {@code null} if there is no available thumbnail image.
     */
    String getThumbnailPath(Project project, @NotNull Logger logger);

}
