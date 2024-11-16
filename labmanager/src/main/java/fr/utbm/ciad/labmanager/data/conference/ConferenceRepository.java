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

package fr.utbm.ciad.labmanager.data.conference;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

/** JPA repository for conferences.
 * 
 * @author $Author: sgalland$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface ConferenceRepository extends JpaRepository<Conference, Long>, JpaSpecificationExecutor<Conference> {

	/** Find the conference with the given acronym or name.
	 *
	 * @param conference the name or acronym.
	 * @return the conference, or nothing.
	 * @deprecated no replacement
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Query("SELECT DISTINCT c FROM Conference c WHERE c.acronym = :name OR c.name = :name")
	Optional<Conference> findByAcronymOrName(@Param("name") String conference);

	/** Find the conferences by their name.
	 *
	 * @param name the name of the conference to search for.
	 * @return the conferences with the given name.
	 */
	@Query("SELECT DISTINCT c FROM Conference c WHERE c.acronym = :name OR c.name = :name")
	Set<Conference> findDistinctByAcronymOrName(String name);

	Set<Conference> findByEnclosingConference(Conference conference);


}
