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

package fr.utbm.ciad.labmanager.utils.io.scimago;

import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.Progression;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * Accessor to the online Scimago platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.scimagojr.com"
 * @since 2.5
 */
public interface ScimagoPlatform {

    /**
     * Name that could be used for retrieving the best quartile for a journal.
     *
     * @see #getJournalRanking(int, String)
     */
    String BEST = "~BEST"; //$NON-NLS-1$

    /**
     * Format the given scimago category in order to by found in the data given by {@link #getJournalRanking(int, URL, Progression)}.
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
     * Replies the URL of the quartile picture for a journal on Scimago.
     *
     * @param journalId the identifier of the journal.
     * @return the URL for the journal picture.
     */
    URL getJournalPictureUrl(String journalId);

    /**
     * Replies the URL of a journal on Scimago.
     *
     * @param journalId the identifier of the journal.
     * @return the URL for the journal.
     */
    URL getJournalUrl(String journalId);

    /**
     * Replies the URL for obtaining the CSV data for the journals.
     *
     * @param year the year for which the journal data must be retrieved from the Scimago platform.
     * @return the URL to access to the journal CSV.
     */
    URL getJournalCsvUrl(int year);

    /**
     * Replies the ranking descriptions for all the journals and for the given year.
     * The ranking descriptions maps journal identifier to a single ranking description.
     * Each ranking description provides the quartiles per scientific topics. The key
     * {@link #BEST} represents the best quartile from Scimago database.
     *
     * @param year     the reference year.
     * @param csvUrl   the URL of the CSV file.
     * @param progress progress monitor.
     * @return the ranking descriptions for all the journals.
     * @throws Exception if rankings cannot be read.
     */
    Map<String, Map<String, QuartileRanking>> getJournalRanking(int year, URL csvUrl, Progression progress) throws Exception;

    /**
     * Replies the ranking descriptions for all the journals and for the given year.
     * The ranking descriptions maps journal identifier to a single ranking description.
     * Each ranking description provides the quartiles per scientific topics. The key
     * {@link #BEST} represents the best quartile from Scimago database.
     *
     * @param year     the reference year.
     * @param progress progress monitor.
     * @return the ranking descriptions for all the journals.
     * @throws Exception if rankings cannot be read.
     */
    default Map<String, Map<String, QuartileRanking>> getJournalRanking(int year, Progression progress) throws Exception {
        return getJournalRanking(year, getJournalCsvUrl(year), progress);
    }

    /**
     * Replies the ranking description for the journal with the given identifier and for the given year.
     * The ranking description provides the quartiles per scientific topics. The key
     * {@link #BEST} represents the best quartile from Scimago database.
     *
     * @param year      the reference year.
     * @param csvUrl    the URL of the CSV file.
     * @param journalId the identifier of the journal on Scimago.
     * @param progress  progress monitor.
     * @return the ranking description for the journal.
     * @throws Exception if rankings cannot be read.
     */
    default Map<String, QuartileRanking> getJournalRanking(int year, URL csvUrl, String journalId, Progression progress) throws Exception {
        final var rankings0 = getJournalRanking(year, csvUrl, progress);
        final var rankings1 = rankings0.get(journalId);
        if (rankings1 == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(rankings1);
    }

    /**
     * Replies the ranking description for the journal with the given identifier and for the given year.
     * The ranking description provides the quartiles per scientific topics. The key
     * {@link #BEST} represents the best quartile from Scimago database.
     *
     * @param year      the reference year.
     * @param journalId the identifier of the journal on Scimago.
     * @param progress  progress monitor.
     * @return the ranking description for the journal.
     * @throws Exception if rankings cannot be read.
     */
    default Map<String, QuartileRanking> getJournalRanking(int year, String journalId, Progression progress) throws Exception {
        return getJournalRanking(year, getJournalCsvUrl(year), journalId, progress);
    }

}
