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

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;

/**
 * A publication that is associated to a conference.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface ConferenceBasedPublication extends Production {

    /**
     * Replies the decorator for a number depending on the given language.
     *
     * @param number   the number to decorate.
     * @param language the language.
     * @return the decorator.
     */
    static String getNumberDecorator(int number, PublicationLanguage language) {
        switch (language) {
            case FRENCH:
                switch (number) {
                    case 1:
                        return "er"; //$NON-NLS-1$
                    default:
                        return "ème"; //$NON-NLS-1$
                }
            case GERMAN:
            case ITALIAN:
            case OTHER:
            case ENGLISH:
            default:
                switch (number) {
                    case 1:
                        return "st"; //$NON-NLS-1$
                    case 2:
                        return "nd"; //$NON-NLS-1$
                    case 3:
                        return "rd"; //$NON-NLS-1$
                    default:
                        return "th"; //$NON-NLS-1$
                }
        }
    }

    /**
     * Replies the conference in which the publication was published.
     *
     * <p>In the example of the "14th International Conference on Systems", the conference is related to "International
     * Conference on Systems" and the "14" is not considered by this function.
     *
     * @return the conference.
     * @see #getConferenceOccurrenceNumber()
     */
    Conference getConference();

    /**
     * Change the conference in which the publication was published.
     *
     * <p>In the example of the "14th International Conference on Systems", the conference is related to "International
     * Conference on Systems" and the "14" is not considered by this function.
     *
     * @param conference the conference.
     * @see #setConferenceOccurrenceNumber(int)
     */
    void setConference(Conference conference);

    /**
     * Replies the number of the occurrence of the conference in which the publication was published.
     * <p>
     * In the example of the "14th International Conference on Systems", the occurrence number is "14".
     *
     * @return the conference occurrence number.
     * @see #getConference()
     */
    int getConferenceOccurrenceNumber();

    /**
     * Change the number of the occurrence of the conference in which the publication was published.
     * <p>
     * In the example of the "14th International Conference on Systems", the occurrence number is "14".
     *
     * @param number the conference occurrence number.
     * @see #setConference(Conference)
     */
    void setConferenceOccurrenceNumber(int number);

    /**
     * Replies the CORE index of the conference.
     *
     * @return the CORE ranking, never {@code null}.
     */
    CoreRanking getCoreRanking();

    /**
     * Deprecated.
     *
     * @param name deprecated.
     * @deprecated Don't use.
     */
    @Deprecated(forRemoval = true)
    default void setScientificEventName(String name) {
        //
    }

    /**
     * Deprecated.
     *
     * @param name deprecated.
     * @deprecated Don't use.
     */
    @Deprecated(forRemoval = true)
    default void setPublisher(String name) {
        //
    }

}
