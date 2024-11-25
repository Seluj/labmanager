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

package fr.utbm.ciad.labmanager.data.member;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/** JPA repository for the membership relations.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface MembershipRepository extends JpaRepository<Membership, Long>, JpaSpecificationExecutor<Membership> {

	/** Find the membership by their organization and member identifiers.
	 *
	 * @param researchoOrganizationId the identifier of the organization.
	 * @param personId the identifier of the member.
	 * @return the membership.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	Optional<Membership> findDistinctByResearchOrganizationIdAndPersonId(long researchoOrganizationId, long personId);

	/** Find the memberships by their organization and member identifiers.
	 *
	 * @param researchoOrganizationId the identifier of the organization.
	 * @param personId the identifier of the member.
	 * @return the memberships.
	 */
	Set<Membership> findByResearchOrganizationIdAndPersonId(long researchoOrganizationId, long personId);

	/** Replies the list of memberships for the given member.
	 *
	 * @param memberId the identifier of the member.
	 * @return the list of memberships.
	 */
	List<Membership> findAllByPersonId(long memberId);

	/** Count the number of memberships for the person with the given id.
	 *
	 * @param id the identifier of the person.
	 * @return the count of memberships.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	int countDistinctByPersonId(long id);

	/** Replies the list of memberships for the given research organization.
	 *
	 * @param organizationId the identifier of the organization.
	 * @return the list of memberships.
	 * @since 3.2
	 */
	List<Membership> findDistinctByResearchOrganization(ResearchOrganization organization);

	/** Replies the list of memberships for the given super research organization.
	 *
	 * @param organizationId the identifier of the organization.
	 * @return the list of memberships.
	 * @since 3.2
	 */
	List<Membership> findDistinctBySuperResearchOrganization(ResearchOrganization organizationId);

	/** Replies the persons that have memberships fitting the given filter.
	 *
	 * @param pageable the tool for building query pages.
	 * @param filter the query filter.
	 * @return the result of the query by page.
	 * @since 4.0
	 */
	@Query("SELECT DISTINCT p FROM Membership m, Person p WHERE m.person.id = p.id")
	Page<Person> findDistinctPerson(Pageable pageable, Specification<Membership> filter);

}
