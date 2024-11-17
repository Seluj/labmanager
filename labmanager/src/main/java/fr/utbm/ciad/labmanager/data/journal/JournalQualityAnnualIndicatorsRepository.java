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

package fr.utbm.ciad.labmanager.data.journal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/** JPA repository for journal quality indicators.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JournalQualityAnnualIndicatorsRepository extends JpaRepository<JournalQualityAnnualIndicators, Long>, JpaSpecificationExecutor<JournalQualityAnnualIndicators> {

    /**
     * Replies the quality indicators of the journal with the given identifier.
     *
     * @param journalId the identifier of the journal.
     * @return the quality indicators.
     */
    @Query("SELECT qi FROM Journal j JOIN j.qualityIndicators qi WHERE j.id = :journalId")
    List<JournalQualityAnnualIndicators> findByJournalId(@Param("journalId") Long journalId);

}
