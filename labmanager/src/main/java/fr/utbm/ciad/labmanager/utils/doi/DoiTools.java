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

package fr.utbm.ciad.labmanager.utils.doi;

import java.net.URL;

/**
 * Utilities for DOI number.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface DoiTools {

    /**
     * Replies the DOI number that is specified inside the given DOI URL.
     * A DOI URL is based on the standard {@link "https://doi.org"} to which
     * the DOI number is added.
     *
     * @param url the DOI URL.
     * @return the DOI number, never {@code null}.
     */
    String getDOINumberFromDOIUrl(URL url);

    /**
     * Replies the DOI number that is specified inside the given DOI URL.
     * A DOI URL is based on the standard {@link "https://doi.org"} to which
     * the DOI number is added.
     *
     * @param url the DOI URL.
     * @return the DOI number, or {@code null} if the given URL does not corresponds to a DOI url.
     * @since 3.6
     */
    default String getDOINumberFromDOIUrlOrNull(URL url) {
        try {
            return getDOINumberFromDOIUrl(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the DOI number that is specified inside the given DOI URL.
     * A DOI URL is based on the standard {@link "https://doi.org"} to which
     * the DOI number is added.
     * If the input string is not a valid URL, the string is parsed a a DOI number.
     *
     * @param url the DOI URL.
     * @return the DOI number, never {@code null}.
     * @throws IllegalArgumentException if the given URL cannot be parsed.
     */
    String getDOINumberFromDOIUrl(String url);

    /**
     * Replies the DOI number that is specified inside the given DOI URL.
     * A DOI URL is based on the standard {@link "https://doi.org"} to which
     * the DOI number is added.
     * If the input string is not a valid URL, the string is parsed as a DOI number.
     *
     * @param url the DOI URL.
     * @return the DOI number, or {@code null} if the given URL does not corresponds to a DOI url.
     * @since 3.6
     */
    default String getDOINumberFromDOIUrlOrNull(String url) {
        try {
            return getDOINumberFromDOIUrl(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the DOI URL from the given DOI number.
     * A DOI URL is based on the standard {@link "https://doi.org"} to which
     * the DOI number is added.
     *
     * @param number the DOI number.
     * @return the DOI URL.
     */
    URL getDOIUrlFromDOINumber(String number);

}
