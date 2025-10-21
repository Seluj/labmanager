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

package fr.utbm.ciad.labmanager.views.appviews.journals;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.utils.io.scimago.ScimagoPlatform;
import fr.utbm.ciad.labmanager.utils.io.wos.WebOfSciencePlatform;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Data in the wizard for updating the journal ranking
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class JournalRankingUpdate extends AbstractContextData {

    private static final long serialVersionUID = -7689528428844273761L;
    private final Map<Long, JournalRankingInformation> scimago = new TreeMap<>();
    private final Map<Long, JournalRankingInformation> wos = new TreeMap<>();
    private final Map<Long, JournalImpactFactor> impactFactors = new TreeMap<>();
    private int year = LocalDate.now().getYear() - 1;
    private boolean wosEnable = true;
    private boolean scimagoEnable = true;
    private boolean impactFactorsEnable = true;
    private List<Journal> journals = new ArrayList<>();

    /**
     * Constructor.
     */
    public JournalRankingUpdate() {
        //
    }

    private static QuartileRanking extractNewQuartile(String category, JournalRankingInformation information) {
        final var oldQuartile = QuartileRanking.normalize(information.knownQuartile());
        QuartileRanking currentQuartile = null;
        if (!Strings.isNullOrEmpty(category)) {
            currentQuartile = information.rankings().get(category);
        }
        currentQuartile = QuartileRanking.normalize(currentQuartile);
        if (oldQuartile != currentQuartile) {
            return currentQuartile;
        }
        return null;
    }

    private static Float extractNewImpactFactor(JournalImpactFactor information) {
        final var oldFactor = information.oldImpactFactor();
        final var newFactor = information.currentImpactFactor();
        final var oldString = oldFactor > 0f ? String.format("%1.3f", Float.valueOf(oldFactor)) : ""; //$NON-NLS-1$ //$NON-NLS-2$
        final var newString = newFactor > 0f ? String.format("%1.3f", Float.valueOf(newFactor)) : ""; //$NON-NLS-1$ //$NON-NLS-2$
        if (!oldString.equals(newString)) {
            return Float.valueOf(newFactor);
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
     * Replies the list of journals.
     *
     * @return the journals.
     */
    public synchronized List<Journal> getJournals() {
        return this.journals;
    }

    /**
     * Change the list of journals.
     *
     * @param journals the journals.
     */
    public synchronized void setJournals(List<Journal> journals) {
        assert journals != null;
        this.journals = journals;
    }

    /**
     * Replies if WoS update is enabled.
     *
     * @return {@code true} if WoS is enabled.
     */
    public synchronized boolean getWosEnable() {
        return this.wosEnable;
    }

    /**
     * Change the flag that indicates if WoS update is enabled.
     *
     * @param enable {@code true} if WoS is enabled.
     */
    public synchronized void setWosEnable(boolean enable) {
        this.wosEnable = enable;
    }

    /**
     * Replies if Scimago update is enabled.
     *
     * @return {@code true} if Scimago is enabled.
     */
    public synchronized boolean getScimagoEnable() {
        return this.scimagoEnable;
    }

    /**
     * Change the flag that indicates if Scimago update is enabled.
     *
     * @param enable {@code true} if Scimago is enabled.
     */
    public synchronized void setScimagoEnable(boolean enable) {
        this.scimagoEnable = enable;
    }

    /**
     * Replies if impact factor update is enabled.
     *
     * @return {@code true} if impact factor update is enabled.
     */
    public synchronized boolean getImpactFactorsEnable() {
        return this.impactFactorsEnable;
    }

    /**
     * Change the flag that indicates if impact factor update is enabled.
     *
     * @param enable {@code true} if impact factor update is enabled.
     */
    public synchronized void setImpactFactorsEnable(boolean enable) {
        this.impactFactorsEnable = enable;
    }

    /**
     * Add Scimago ranking for the journal with the given identifier.
     *
     * @param journalId     the identifier of the journal.
     * @param kwownQuartile the quartile that is already known for the journal
     * @param choices       the rankings per scientific field.
     */
    public synchronized void addScimagoRanking(long journalId, QuartileRanking kwownQuartile, Map<String, QuartileRanking> choices) {
        this.scimago.put(Long.valueOf(journalId), new JournalRankingInformation(kwownQuartile, choices));
    }

    /**
     * Remove all the references to the Scimago rankings.
     */
    public synchronized void clearScimagoRankings() {
        this.scimago.clear();
    }

    /**
     * Add WoS ranking for the journal with the given identifier.
     *
     * @param journalId     the identifier of the journal.
     * @param kwownQuartile the quartile that is already known for the journal
     * @param choices       the rankings per scientific field.
     */
    public synchronized void addWosRanking(long journalId, QuartileRanking kwownQuartile, Map<String, QuartileRanking> choices) {
        this.wos.put(Long.valueOf(journalId), new JournalRankingInformation(kwownQuartile, choices));
    }

    /**
     * Remove all the references to the WoS rankings.
     */
    public synchronized void clearWosRankings() {
        this.wos.clear();
    }

    /**
     * Add impact factor for the journal with the given identifier.
     *
     * @param journalId           the identifier of the journal.
     * @param oldImpactFactor     the previously known impact factor.
     * @param currentImpactFactor the current impact factor.
     */
    public synchronized void addImpactFactor(long journalId, float oldImpactFactor, float currentImpactFactor) {
        this.impactFactors.put(Long.valueOf(journalId), new JournalImpactFactor(oldImpactFactor, currentImpactFactor));
    }

    /**
     * Remove all the references to the impact factors.
     */
    public synchronized void clearImpactFactors() {
        this.impactFactors.clear();
    }

    /**
     * Replies all the update information about the journals.
     *
     * @return the stream of information, one entry per journal.
     */
    public Stream<JournalNewInformation> getJournalUpdates() {
        final var defaultRanking = new JournalRankingInformation(QuartileRanking.NR, Collections.emptyMap());
        final var defaultImpacts = new JournalImpactFactor(0f, 0f);
        return getJournals().stream().map(journal -> {
            final var journalId = Long.valueOf(journal.getId());

            final var scimago = this.scimago.getOrDefault(journalId, defaultRanking);
            final var scimagoQ = extractNewQuartile(ScimagoPlatform.formatCategory(journal.getScimagoCategory()), scimago);

            final var wos = this.wos.getOrDefault(journalId, defaultRanking);
            final var wosQ = extractNewQuartile(WebOfSciencePlatform.formatCategory(journal.getWosCategory()), wos);

            final var impactFactors = this.impactFactors.getOrDefault(journalId, defaultImpacts);
            final var currentIF = extractNewImpactFactor(impactFactors);

            if (scimagoQ != null || wosQ != null || currentIF != null) {
                return new JournalNewInformation(journal,
                        (scimagoQ != null ? scimago.knownQuartile() : null), scimagoQ,
                        (wosQ != null ? wos.knownQuartile : null), wosQ,
                        (impactFactors != null ? Float.valueOf(impactFactors.oldImpactFactor) : null), currentIF);
            }
            return null;
        }).filter(it -> it != null);
    }

    /**
     * Description of the information for a journal.
     *
     * @param journal         the journal.
     * @param oldScimago      the old Scimago indicator, or {@code null}.
     * @param newScimago      the new Scimago indicator, or {@code null}.
     * @param oldWos          the old WOS indicator, or {@code null}.
     * @param newWos          the new WOS indicator, or {@code null}.
     * @param oldImpactFactor the old impact factor, or {@code null}.
     * @param newImpactFactor the new impact factor, or {@code null}.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record JournalNewInformation(Journal journal, QuartileRanking oldScimago, QuartileRanking newScimago,
                                        QuartileRanking oldWos, QuartileRanking newWos, Float oldImpactFactor,
                                        Float newImpactFactor) {
        //
    }

    /**
     * Description of the quartile information for a journal.
     *
     * @param knownQuartile the previously known quartile value for the journal.
     * @param rankings      the current quartiles per scientific category.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record JournalRankingInformation(QuartileRanking knownQuartile, Map<String, QuartileRanking> rankings) {
        //
    }

    /**
     * Description of the impact factor information for a journal.
     *
     * @param oldImpactFactor     the previously known impact factor.
     * @param currentImpactFactor the current impact factor.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public record JournalImpactFactor(float oldImpactFactor, float currentImpactFactor) {
        //
    }

}
