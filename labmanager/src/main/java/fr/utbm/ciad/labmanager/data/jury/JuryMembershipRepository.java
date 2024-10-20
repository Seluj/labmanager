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

package fr.utbm.ciad.labmanager.data.jury;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/** JPA repository for jury memberships.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface JuryMembershipRepository extends JpaRepository<JuryMembership, Long>, JpaSpecificationExecutor<JuryMembership> {

	/** Replies the list of memberships that are associated to the person with the given identifier.
	 *
	 * @param personId the identifier of the person.
	 * @return the list of memberships.
	 * @deprecated no replacement
	 */
	List<JuryMembership> findAllByPersonId(long personId);

	/** Replies the list of memberships that are associated to the candidate with the given identifier.
	 *
	 * @param candidateId the identifier of the candidate.
	 * @return the list of memberships.
	 * @deprecated no replacement
	 */
	List<JuryMembership> findAllByCandidateId(long candidateId);

	/** Replies the list of memberships that are associated to the promoter with the given identifier.
	 *
	 * @param promoterId the identifier of the promoter.
	 * @return the list of memberships.
	 * @deprecated no replacement
	 */
	List<JuryMembership> findAllByPromotersId(long promoterId);

	/** Replies a membership that corresponds to the person, candidate and type.
	 *
	 * @param personId the identifier of the person.
	 * @param candidateId the identifier of the candidate.
	 * @param type the type of membership.
	 * @return the membership from the database.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	Optional<JuryMembership> findByPersonIdAndCandidateIdAndType(long personId, long candidateId, JuryMembershipType type);

	/** Count the number of jury memberships for the participant with the given id.
	 *
	 * @param id the identifier of the participant.
	 * @return the count of supervisions.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	int countDistinctByPersonId(long id);

}
