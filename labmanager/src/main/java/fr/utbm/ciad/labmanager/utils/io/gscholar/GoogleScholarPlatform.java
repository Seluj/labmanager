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

package fr.utbm.ciad.labmanager.utils.io.gscholar;

import org.arakhne.afc.progress.Progression;

import java.io.Serializable;
import java.net.URL;

/**
 * Accessor to the online Google Scholar platform.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://scholar.google.com/"
 * @since 3.3
 */
public interface GoogleScholarPlatform {

    /**
     * Replies the ranking descriptions for the person with the given URL.
     *
     * @param gsProfile the URL to the profile of the person on GS.
     * @param progress  progress monitor.
     * @return the ranking descriptions for the person.
     * @throws Exception if rankings cannot be read.
     * @since 3.3
     */
    GoogleScholarPerson getPersonRanking(URL gsProfile, Progression progress) throws Exception;

    /**
     * Accessor to the online Google Scholar platform.
     *
     * @param hindex    The h-index of the person.
     * @param citations The number of citations for the person.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see "https://scholar.google.com/"
     * @since 3.3
     */
        record GoogleScholarPerson(int hindex, int citations) implements Serializable {

            private static final long serialVersionUID = 5779940249749841338L;

        /**
         * Constructor.
         *
         * @param hindex    the H-index of the person
         * @param citations the number of citations for the person.
         */
        public GoogleScholarPerson {
        }

        }

}
