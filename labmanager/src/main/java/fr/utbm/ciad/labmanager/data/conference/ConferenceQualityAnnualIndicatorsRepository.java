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
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

/** JPA repository for conference quality indicators.
 * 
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface ConferenceQualityAnnualIndicatorsRepository extends JpaRepository<ConferenceQualityAnnualIndicators, Long>, JpaSpecificationExecutor<ConferenceQualityAnnualIndicators> {

    /**
     * Replies the quality indicators of the conference with the given identifier.
     *
     * @param conferenceId the identifier of the conference.
     * @return the quality indicators.
     */
    @Query("SELECT ci FROM Conference c JOIN c.qualityIndicators ci WHERE c.id = :conferenceId")
    List<ConferenceQualityAnnualIndicators> findByConferenceId(@Param("conferenceId")Long conferenceId);

}
