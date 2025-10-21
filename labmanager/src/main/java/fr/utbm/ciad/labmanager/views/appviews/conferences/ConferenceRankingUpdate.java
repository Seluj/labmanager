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

package fr.utbm.ciad.labmanager.views.appviews.conferences;

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Data in the wizard for updating the conference ranking.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ConferenceRankingUpdate extends AbstractContextData {

    private static final long serialVersionUID = 1506566568548752541L;
    private final Map<Long, ConferenceRankingInformation> core = new TreeMap<>();
    private int year = LocalDate.now().getYear() - 1;
    private List<Conference> conferences = new ArrayList<>();

    /**
     * Constructor.
     */
    public ConferenceRankingUpdate() {
        //
    }

    private static CoreRanking extractKnownValue(CoreRanking oldValue, CoreRanking newValue) {
        if (newValue != null) {
            return CoreRanking.normalize(oldValue);
        }
        return null;
    }

    private static CoreRanking extractValue(CoreRanking oldValue, CoreRanking newValue) {
        final var ov = CoreRanking.normalize(oldValue);
        final var nv = CoreRanking.normalize(newValue);
        if (ov != nv) {
            return newValue;
        }
        return null;
    }

    /**
     * Replies the year for the ranking updates.
     *
     * @return the year.
     */
    public synchronized int getYear() {
        return this.year;
    }

    /**
     * Set the year for the ranking updates.
     *
     * @param year the year.
     */
    public synchronized void setYear(int year) {
        this.year = year;
    }

    /**
     * Replies the list of conferences.
     *
     * @return the conferences.
     */
    public synchronized List<Conference> getConferences() {
        return this.conferences;
    }

    /**
     * Change the list of conferences.
     *
     * @param conferences the conferences.
     */
    public synchronized void setConferences(List<Conference> conferences) {
        assert conferences != null;
        this.conferences = conferences;
    }

    /**
     * Remove all the references to the Worankings.
     */
    public synchronized void clearRankings() {
        this.core.clear();
    }

    /**
     * Add ranking for the conference with the given identifier.
     *
     * @param conferenceId the identifier of the conference.
     * @param knownRanking the CORE ranking of the conference that is already known.
     * @param newRanking   the new CORE ranking for the conference.
     */
    public synchronized void addRanking(long conferenceId, CoreRanking knownRanking, CoreRanking newRanking) {
        this.core.put(Long.valueOf(conferenceId), new ConferenceRankingInformation(knownRanking, newRanking));
    }

    /**
     * Replies all the update information about the conferences.
     *
     * @return the stream of information, one entry per conference.
     */
    public Stream<ConferenceNewInformation> getConferenceUpdates() {
        final var defaultRanking = new ConferenceRankingInformation(CoreRanking.NR, CoreRanking.NR);
        return getConferences().stream().map(conference -> {
            final var conferenceId = Long.valueOf(conference.getId());

            final var core = this.core.getOrDefault(conferenceId, defaultRanking);
            final var coreRanking = extractValue(core.knownRanking(), core.newRanking());

            if (coreRanking != null) {
                return new ConferenceNewInformation(conference,
                        extractKnownValue(core.knownRanking(), coreRanking), coreRanking);
            }
            return null;
        }).filter(it -> it != null);
    }

    /**
     * Description of the ranking information for a conference.
     *
     * @param knownRanking the ranking of the conference that is already known.
     * @param newRanking   the new ranking for the conference.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record ConferenceRankingInformation(CoreRanking knownRanking, CoreRanking newRanking) {
        //
    }

    /**
     * Description of the information for a conference.
     *
     * @param conference the conference.
     * @param oldRanking the old CORE ranking, or {@code null}.
     * @param newRanking the ew CORE ranking, or {@code null}.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record ConferenceNewInformation(Conference conference,
                                           CoreRanking oldRanking, CoreRanking newRanking) {
        //
    }

}
