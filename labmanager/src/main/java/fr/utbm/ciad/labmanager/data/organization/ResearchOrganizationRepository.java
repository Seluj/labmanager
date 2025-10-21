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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * JPA Repository for the research organizations.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see ResearchOrganization
 */
public interface ResearchOrganizationRepository extends JpaRepository<ResearchOrganization, Long>, JpaSpecificationExecutor<ResearchOrganization> {

    /**
     * Find a research organization with the given acronym or name. This function is case sensitive.
     *
     * @param acronym the acronym to search for.
     * @param name    the name to search for.
     * @return the research organization.
     */
    Optional<ResearchOrganization> findDistinctByAcronymOrName(String acronym, String name);

    /**
     * Find a research organization with the given acronym. This function is case sensitive.
     *
     * @param acronym the acronym to search for.
     * @return the research organization.
     */
    Optional<ResearchOrganization> findDistinctByAcronym(String acronym);

    /**
     * Find a research organization with the given name. This function is case sensitive.
     *
     * @param name the name to search for.
     * @return the research organization.
     * @deprecated no replacement
     */
    @Deprecated(since = "4.0", forRemoval = true)
    Optional<ResearchOrganization> findDistinctByName(String name);

    /**
     * Replies the super organizations of the given organization.
     *
     * @param organization the sub organization.
     * @param pageable     the definition of the page.
     * @param filter       the filtering criteria.
     * @return the page.
     */
    @Query("SELECT DISTINCT o FROM ResearchOrganization o WHERE ?1 MEMBER OF o.subOrganizations ")
    Page<ResearchOrganization> findSuperOrganizations(ResearchOrganization organization, Pageable pageable, Specification<ResearchOrganization> filter);

}
