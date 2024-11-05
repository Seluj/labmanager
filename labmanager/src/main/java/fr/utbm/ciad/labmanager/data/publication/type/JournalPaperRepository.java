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

import java.util.List;
import java.util.Set;

import fr.utbm.ciad.labmanager.data.publication.AbstractJournalBasedPublication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** JPA Repository for journal papers.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JournalPaperRepository extends JpaRepository<JournalPaper, Long>, JpaSpecificationExecutor<JournalPaper> {

	/** Replies the journal papers that are associated to the journal with the given identifier.
	 *
	 * @param journalId the identifier of the journal.
	 * @return the journal papers.
	 */
	List<JournalPaper> findAllByJournalId(long journalId);

	/** Replies the list of journal papers for the persons with the given identifiers.
	 *
	 * @param personIds the list of identifiers of the authors.
	 * @return the list of publications.
	 */
	Set<JournalPaper> findAllByAuthorshipsPersonIdIn(Set<Long> personIds);

	/**
	 * Find all papers associated with a specific journal.
	 *
	 * @param journalId the ID of the journal to retrieve papers for.
	 * @return a set of publications associated with the given journal.
	 */
    Set<AbstractJournalBasedPublication> findByJournalId(Long journalId);
}

