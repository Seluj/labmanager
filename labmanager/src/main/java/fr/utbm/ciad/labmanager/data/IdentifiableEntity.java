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

package fr.utbm.ciad.labmanager.data;

/**
 * Interface that represents an object that has an database identifier.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
public interface IdentifiableEntity {

    /**
     * Replies the database identifier for this entity.
     *
     * @return the identifier. It is equals to {@code 0} when the entity is not linked to a JPA entity.
     */
    long getId();

    /**
     * Replies if the given entity is a fake entity or not.
     * A fake entity is created for being provided by the JPA to the front-end.
     * It is not supposed to be saved into the JPA database.
     *
     * @return {@code true} if the entity is a fake entity.
     * @since 2.4
     */
    default boolean isFakeEntity() {
        return false;
    }

}
