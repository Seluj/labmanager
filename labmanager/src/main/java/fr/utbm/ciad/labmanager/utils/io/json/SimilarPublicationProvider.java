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

package fr.utbm.ciad.labmanager.utils.io.json;

import fr.utbm.ciad.labmanager.data.publication.Publication;

import java.util.List;

/**
 * Provider of publication that could be used for completing a given publication.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface SimilarPublicationProvider {

    /**
     * Replies the publications that corresponds to the given source. The replied publication is
     * assumed to be different than the source. The similarity between the source publication and
     * the replied publication depends on the implementation of the {@code SimilarPublicationProvider}.
     *
     * @param source the publication source.
     * @return the similar publications.
     */
    List<Publication> get(Publication source);

}
