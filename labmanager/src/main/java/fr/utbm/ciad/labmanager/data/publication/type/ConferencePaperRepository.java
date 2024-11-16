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

package fr.utbm.ciad.labmanager.data.publication.type;

import java.util.Set;

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.publication.AbstractConferenceBasedPublication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** JPA Repository for conference and workshop papers.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ConferencePaperRepository extends JpaRepository<ConferencePaper, Long>, JpaSpecificationExecutor<ConferencePaper> {

	/** Replies the list of conference papers for the persons with the given identifiers.
	 *
	 * @param personIds the list of identifiers of the authors.
	 * @return the list of journal papers.
	 */
	Set<ConferencePaper> findAllByAuthorshipsPersonIdIn(Set<Long> personIds);

	/** Replies the list of conference papers for the conference.
	 *
	 * @param conference the conference.
	 * @return the list of conference papers.
	 */
	Set<ConferencePaper> findAllByConference(Conference conference);



}

