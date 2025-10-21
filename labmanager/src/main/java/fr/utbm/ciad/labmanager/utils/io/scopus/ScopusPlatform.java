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

package fr.utbm.ciad.labmanager.utils.io.scopus;

import org.arakhne.afc.progress.Progression;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * Accessor to the online Elsevier Scopus platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://www.scopus.com/"
 * @since 3.3
 */
public interface ScopusPlatform {

    /**
     * Replies the ranking descriptions for the person with the given URL.
     *
     * @param scProfile the URL to the profile of the person on Scopus.
     * @param progress  progress monitor.
     * @return the ranking descriptions for the person, never {@code null}.
     * @throws Exception if rankings cannot be read.
     */
    ScopusPerson getPersonRanking(URL scProfile, Progression progress) throws Exception;

    /**
     * Replies the ranking descriptions for the person with the given identifier.
     *
     * @param personId the identifier of the person on Scopus.
     * @param progress progress monitor.
     * @return the ranking descriptions for the person, never {@code null}.
     * @throws Exception if rankings cannot be read.
     */
    default ScopusPerson getPersonRanking(String personId, Progression progress) throws Exception {
        final var apiUrl = new URI("https://www.scopus.com/authid/detail.uri?authorId=" + personId).toURL(); //$NON-NLS-1$
        return getPersonRanking(apiUrl, progress);
    }

    /**
     * Accessor to the online Scopus platform.
     *
     * @param hindex    The h-index of the person.
     * @param citations The number of citations for the person.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://www.scopus.com/"
     * @since 3.3
     */
        record ScopusPerson(int hindex, int citations) implements Serializable {

            private static final long serialVersionUID = 8357860848328947999L;

        /**
         * Constructor.
         *
         * @param hindex    the H-index of the person
         * @param citations the number of citations for the person.
         */
        public ScopusPerson {
        }

        }

}
