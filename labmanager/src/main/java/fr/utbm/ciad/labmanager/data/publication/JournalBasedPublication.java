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

package fr.utbm.ciad.labmanager.data.publication;

import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.utils.ranking.JournalRankingSystem;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;

/**
 * A publication that is associated to a journal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface JournalBasedPublication extends Production {

    /**
     * Replies the journal in which the publication was published.
     *
     * @return the journal.
     */
    Journal getJournal();

    /**
     * Change the journal in which the publication was published.
     *
     * @param journal the journal.
     */
    void setJournal(Journal journal);

    /**
     * Replies the Scimago Q-index.
     *
     * @return the Scimago ranking, never {@code null}.
     */
    QuartileRanking getScimagoQIndex();

    /**
     * Replies the category that is used for obtaining the Scimago Q-index.
     *
     * @return the Scimago category or {@code null}.
     * @since 3.8
     */
    String getScimagoCategory();

    /**
     * Replies the JCR/Web-of-Science Q-index.
     *
     * @return the JCR/WOS ranking, never {@code null}.
     */
    QuartileRanking getWosQIndex();

    /**
     * Replies the category that is used for obtaining the JCR/Web-of-Science Q-index.
     *
     * @return the JCR/Web-of-Science category or {@code null}.
     * @since 3.8
     */
    String getWosCategory();

    /**
     * Replies the journal impact factor.
     *
     * @return the IF or zero.
     */
    float getImpactFactor();

    /**
     * Replies if the publication is ranked in at least one of the ranking systems for the year of publication.
     *
     * <p>Any ranking that is known for a year after the publication's year is ignored.
     *
     * @return {@code true} if a ranking system is ranking this publication.
     * @since 3.6
     */
    boolean isRanked();

    /**
     * Replies if the publication is published into a ranked support (journal or conference).
     *
     * @param randkingSystem the ranking system to be used.
     * @return {@code true} if the publication is in a ranked support.
     * @since 3.6
     */
    boolean isRanked(JournalRankingSystem randkingSystem);

    /**
     * Replies the category of publication.
     *
     * @param rankingsystem indicated the type of ranking system that should be used for determining the category.
     *                      If it is {@code null}, the {@link JournalRankingSystem#getDefault() default ranking system} is used.
     * @return the category, never {@code null}.
     * @since 3.6
     */
    PublicationCategory getCategory(JournalRankingSystem rankingsystem);

}
