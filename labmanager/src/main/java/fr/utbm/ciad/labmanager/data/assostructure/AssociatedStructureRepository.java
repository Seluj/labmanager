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
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JPA repository for associated structure declaration.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public interface AssociatedStructureRepository extends JpaRepository<AssociatedStructure, Long>, JpaSpecificationExecutor<AssociatedStructure> {

    /**
     * Replies all the associated structures that match the organization identifier, and the confidential
     * flag.
     *
     * @param id           the identifier for the organization.
     * @param confidential indicates the expected confidentiality flag for the associated structures.
     * @return the list of associated structures.
     */
    @Query("SELECT DISTINCT s FROM AssociatedStructure s, AssociatedStructureHolder h WHERE s.fundingOrganization.id = :id OR ((h.organization.id = :id OR h.superOrganization.id = :id) AND h MEMBER OF s.holders) AND s.confidential = :confidential")
    List<AssociatedStructure> findDistinctOrganizationAssociatedStructures(
            @Param("confidential") Boolean confidential, @Param("id") Long id);

    /**
     * Replies all the associated structures that match the person identifier, and the confidential
     * flag.
     *
     * @param confidential indicates the expected confidentiality flag for the associated structures.
     * @param id           the identifier for the person.
     * @return the list of associated structures.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    @Query("SELECT DISTINCT s FROM AssociatedStructure s, AssociatedStructureHolder h WHERE h.person.id = :id AND h MEMBER OF s.holders AND s.confidential = :confidential")
    List<AssociatedStructure> findDistinctPersonAssociatedStructures(
            @Param("confidential") Boolean confidential, @Param("id") Long id);

}
