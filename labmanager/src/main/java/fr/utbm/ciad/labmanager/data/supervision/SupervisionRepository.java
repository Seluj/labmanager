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

package fr.utbm.ciad.labmanager.data.supervision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/** JPA repository for surpervision declaration.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
public interface SupervisionRepository extends JpaRepository<Supervision, Long>, JpaSpecificationExecutor<Supervision> {

	/** Replies all the supervisions associated to the person with the given identifier, when he/she is the supervised person.
	 *
	 * @param supervisedPersonId the identifier of the supervised person.
	 * @return the list of the supervisions for the supervised person.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	List<Supervision> findAllBySupervisedPersonPersonId(Long supervisedPersonId);

	/** Replies all the supervisions associated to the membership with the given identifier.
	 *
	 * @param membershipId the identifier of the membership.
	 * @return the list of the supervisions for the membership.
	 * @since 3.6
	 */
	List<Supervision> findAllBySupervisedPersonId(Long membershipId);

	/** Replies all the supervisions associated to the person with the given identifier, when he/she is one of the supervisors.
	 *
	 * @param supervisorId the identifier of the supervisor.
	 * @return the list of the supervisions for the supervisor.
	 */
	List<Supervision> findAllDisctinctBySupervisorsSupervisorId(Long supervisorId);

	/** Count the number of supervisions for the supervisor with the given id.
	 *
	 * @param id the identifier of the supervisor.
	 * @return the count of supervisions.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	int countDistinctBySupervisedPersonPersonId(long id);

}
