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

package fr.utbm.ciad.labmanager.data.assostructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA repository for the declarations of the holders of associated structures.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public interface AssociatedStructureHolderRepository extends JpaRepository<AssociatedStructureHolder, Long> {

    /**
     * Replies the list of holders that corresponds to the persons with the give identifier.
     *
     * @param id the identifier of the person.
     * @return the list of associate structures' holders.
     */
    List<AssociatedStructureHolder> findDistinctByPersonId(long id);

}
