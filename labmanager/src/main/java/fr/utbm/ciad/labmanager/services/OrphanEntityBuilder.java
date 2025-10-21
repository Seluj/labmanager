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

package fr.utbm.ciad.labmanager.services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.arakhne.afc.progress.Progression;

import java.util.Locale;

/**
 * Interface that represents a tool for computing the orphan entities.
 *
 * @param <T> the type of the entities.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface OrphanEntityBuilder<T> {

    /**
     * Fill up the given JSON array with the list of the orphan entities.
     * The content of the JSON nodes for each orphan entity depends on the implementation
     * class of this interface.
     *
     * @param receiver the JSON node that receives the orphan entities.
     * @param locale   the locale to use.
     * @param progress a progress indicator.
     */
    void computeOrphans(ArrayNode receiver, Locale locale, Progression progress);

    /**
     * Replies the reason why the given entity may be considered as orphan.
     * An orphan entity is an entity that has no links to the existing
     * other entities and that could be safely deleted from the system.
     *
     * @param entity the entity to check.
     * @param locale the locale to use.
     * @return the reason to be orphan; or {@code null} if the entity is not orphan.
     */
    String getOrphanCriteria(T entity, Locale locale);

    /**
     * Replies the name of the given entity.
     *
     * @param entity the entity to check.
     * @param locale the locale to use.
     * @return the name of the orphan entity.
     */
    String getOrphanEntityLabel(T entity, Locale locale);

    /**
     * Replies the name of the type of the orphan entities that is supported by this builder.
     *
     * @param locale the locale to use.
     * @return the name of type of the orphan entities.
     */
    String getOrphanTypeLabel(Locale locale);

}
