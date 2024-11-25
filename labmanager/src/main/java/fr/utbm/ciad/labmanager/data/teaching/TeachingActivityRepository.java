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

package fr.utbm.ciad.labmanager.data.teaching;

import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Period;
import java.util.Collection;
import java.util.List;

/** JPA repository for teaching activity declaration.
 * 
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
public interface TeachingActivityRepository extends JpaRepository<TeachingActivity, Long>, JpaSpecificationExecutor<TeachingActivity> {

	/** Replies the list of activities that associated to the person.
	 *
	 * @param person the identifier.
	 * @return the list of activities.
	 */
	List<TeachingActivity> findDistinctByPerson(Person person);

	/** Replies the list of activities that associated to the organization with the given id.
	 *
	 * @param organization the identifier.
	 * @return the list of activities.
	 */
	List<TeachingActivity> findDistinctByUniversity(ResearchOrganization organization);

	/** Replies the list of activities that associated to the person with the given id.
	 *
	 * @param aLong the identifier.
	 * @return the list of activities.
	 */
	List<TeachingActivity> findDistinctByPersonId(Long aLong);
}
