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

package fr.utbm.ciad.labmanager.utils.io.wos;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.Progression;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * Accessor to the online Web-of-Science platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.webofscience.com"
 * @since 2.5
 */
public interface WebOfSciencePlatform {

    /**
     * Format the given web-of-science category in order to by found in the data given by {@link #getJournalRanking(int, InputStream, Progression)}.
     *
     * @param inputCategory the input scientific category.
     * @return the formatted category.
     * @since 4.0
     */
    static String formatCategory(String inputCategory) {
        if (inputCategory == null) {
            return null;
        }
        return inputCategory.trim().toLowerCase();
    }

    /**
     * Replies the URL of a journal on Web-of-Science.
     *
     * @param journalId the identifier of the journal.
     * @return the URL for the journal.
     * @since 4.0
     */
    URL getJournalUrl(String journalId);

    /**
     * Replies the ranking description for the journal with the given identifier.
     * The ranking description provides the quartiles per scientific topics and the impact factor.
     * The source of information does not permit to extract information for a specific year.
     *
     * @param journalId the identifier of the journal on WoS.
     * @param progress  progress monitor.
     * @return the ranking description for the journal.
     * @throws Exception if rankings cannot be read.
     */
    WebOfScienceJournal getJournalRanking(String journalId, Progression progress) throws Exception;

    /**
     * Replies the ranking descriptions for all the journals and for the given year.
     * The ranking descriptions maps journal identifier to a single ranking description.
     * Each ranking description provides the quartiles per scientific topics.
     *
     * @param year     the reference year.
     * @param csvUrl   the URL of the CSV file.
     * @param progress progress monitor.
     * @return the ranking descriptions for all the journals.
     * @throws Exception if rankings cannot be read.
     */
    default Map<String, WebOfScienceJournal> getJournalRanking(int year, URL csvUrl, Progression progress) throws Exception {
        try (final var is = csvUrl.openStream()) {
            return getJournalRanking(year, is, progress);
        }
    }

    /**
     * Replies the ranking description for the journal with the given identifier and for the given year.
     * The ranking description provides the quartiles per scientific topics.
     *
     * @param year      the reference year.
     * @param csvUrl    the URL of the CSV file.
     * @param journalId the identifier of the journal on WoS.
     * @param progress  progress monitor.
     * @return the ranking description for the journal.
     * @throws Exception if rankings cannot be read.
     */
    default WebOfScienceJournal getJournalRanking(int year, URL csvUrl, String journalId, Progression progress) throws Exception {
        try (final var is = csvUrl.openStream()) {
            return getJournalRanking(year, is, journalId, progress);
        }
    }

    /**
     * Replies the ranking descriptions for all the journals and for the given year.
     * The ranking descriptions maps journal identifier to a single ranking description.
     * Each ranking description provides the quartiles per scientific topics.
     *
     * @param year     the reference year.
     * @param csv      the stream of the CSV file.
     * @param progress progress monitor.
     * @return the ranking descriptions for all the journals.
     * @throws Exception if rankings cannot be read.
     */
    Map<String, WebOfScienceJournal> getJournalRanking(int year, InputStream csv, Progression progress) throws Exception;

    /**
     * Replies the ranking description for the journal with the given identifier and for the given year.
     * The ranking description provides the quartiles per scientific topics.
     *
     * @param year      the reference year.
     * @param csv       the stream of the CSV file.
     * @param journalId the identifier of the journal on WoS.
     * @param progress  progress monitor.
     * @return the ranking description for the journal.
     * @throws Exception if rankings cannot be read.
     */
    default WebOfScienceJournal getJournalRanking(int year, InputStream csv, String journalId, Progression progress) throws Exception {
        final var rankings0 = getJournalRanking(year, csv, progress);
        final var normalizedId = normalizeIssn(journalId);
        final var rankings1 = rankings0.get(normalizedId);
        return rankings1;
    }

    /**
     * Normalize the ISSN number from WoS in order to be used as an journal identifier in {@link #getJournalRanking(int, InputStream, Progression)}.
     *
     * @param wosIssn the ISSN number to normalize.
     * @return the normalized identifier.
     */
    default String normalizeIssn(String wosIssn) {
        if (Strings.isNullOrEmpty(wosIssn)) {
            return null;
        }
        return wosIssn.replaceAll("[^0-9a-zA-Z]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Replies the ranking descriptions for the person with the given WoS identifier.
     *
     * @param wosProfile the URL to the profile of the person on WoS.
     * @param progress   progress monitor.
     * @return the ranking descriptions for the person.
     * @throws Exception if rankings cannot be read.
     * @since 3.3
     */
    WebOfSciencePerson getPersonRanking(URL wosProfile, Progression progress) throws Exception;

    /**
     * Accessor to the online Web-of-Science platform.
     *
     * @param quartiles    The quartiles of the journal per category.
     * @param impactFactor The impact factor of the journal.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://www.webofscience.com"
     * @since 2.5
     */
        record WebOfScienceJournal(Map<String, QuartileRanking> quartiles, float impactFactor) implements Serializable {

            private static final long serialVersionUID = -5636236034630289851L;

            /**
             * Constructor.
             *
             * @param quartiles    the rankings per category.
             * @param impactFactor the impact factor.
             */
            public WebOfScienceJournal(Map<String, QuartileRanking> quartiles, float impactFactor) {
                this.quartiles = Collections.unmodifiableMap(quartiles);
                this.impactFactor = impactFactor;
            }

        }

    /**
     * Accessor to the online Web-of-Science platform.
     *
     * @param hindex    The h-index of the person.
     * @param citations The number of citations for the person.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://www.webofscience.com"
     * @since 3.3
     */
        record WebOfSciencePerson(int hindex, int citations) implements Serializable {

            private static final long serialVersionUID = 4017710928346959632L;

        /**
         * Constructor.
         *
         * @param hindex    the H-index of the person
         * @param citations the number of citations for the person.
         */
        public WebOfSciencePerson {
        }

        }

}
