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

package fr.utbm.ciad.labmanager.utils.io.coreportal;

import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import org.arakhne.afc.progress.Progression;

import java.io.Serializable;
import java.net.URL;

/**
 * Accessor to the online CORE Portal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://portal.core.edu.au/"
 * @since 3.6
 */
public interface CorePortal {

    /**
     * Replies the URL of a conference on CORE.
     *
     * @param conferenceId the identifier of the conference.
     * @return the URL for the conference.
     */
    URL getConferenceUrl(String conferenceId);

    /**
     * Replies the ranking descriptions for the conference with the given identifier.
     *
     * @param year       the year for which the ranking should be retrieved.
     * @param identifier the identifier of the conference on CORE Portal.
     * @param progress   progress monitor.
     * @return the ranking descriptions for the conference, never {@code null}.
     * @throws Exception if rankings cannot be read.
     */
    CorePortalConference getConferenceRanking(int year, String identifier, Progression progress) throws Exception;

    /**
     * Accessor to the online Core Portal.
     *
     * @param ranking The CORE index of the conference.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "http://portal.core.edu.au/"
     * @since 3.6
     */
        record CorePortalConference(CoreRanking ranking) implements Serializable {

            private static final long serialVersionUID = -6517131062597187166L;

        /**
         * Constructor.
         *
         * @param ranking the CORE index of the conference.
         */
        public CorePortalConference {
        }

        }

}
