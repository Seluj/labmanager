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

package fr.utbm.ciad.labmanager.data.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA Repository for the organization addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see OrganizationAddress
 */
public interface OrganizationAddressRepository extends JpaRepository<OrganizationAddress, Long>, JpaSpecificationExecutor<OrganizationAddress> {

    /**
     * Find an address from its symbolic name.
     *
     * @param name the symbolic name to search for.
     * @return the result of the search.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    Optional<OrganizationAddress> findDistinctByName(String name);


    /**
     * Replies the organization addresses with the given identifiers.
     *
     * @param addresses the identifiers to match.
     * @return the list of addresses with the given identifiers.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    Set<OrganizationAddress> findAllByIdIn(List<Long> addresses);

}
