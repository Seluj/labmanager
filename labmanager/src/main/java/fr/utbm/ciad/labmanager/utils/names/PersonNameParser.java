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

package fr.utbm.ciad.labmanager.utils.names;

import java.util.Set;

/**
 * Utilities for parsing person names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface PersonNameParser {

    /**
     * Extract the first name from the full name.
     *
     * @param fullName the full name.
     * @return the first name.
     */
    String parseFirstName(String fullName);

    /**
     * Extract the last name from the full name.
     *
     * @param fullName the full name.
     * @return the lat name.
     */
    String parseLastName(String fullName);

    /**
     * Extract the first and last names from the text.
     * The accepted formats for the list of names are:<ul>
     * <li>{@code Lastname, Von, Firstname}</li> (FORMAT 1)
     * <li>{@code Lastname, Firstname}</li> (FORMAT 1)
     * <li>{@code Lastname, Von, Firstname AND Lastname, Von, Firstname}...</li> (FORMAT 1)
     * <li>{@code Lastname, Firstname AND Lastname, Firstname}...</li> (FORMAT 2)
     * <li>{@code Firstname Lastname, Firstname Lastname}...</li> (FORMAT 2)
     * </ul>
     * Any name of person that is equivalent to {@code et al.} is ignored.
     *
     * @param text     the text to analyze.
     * @param callback invoked each time a name is discovered.
     * @return the number of names that are discovered in the given text.
     */
    int parseNames(String text, NameCallback callback);

    /**
     * Format the given name by making upper-case the first letters of the words, and lower-case the other letters.
     *
     * @param name the name.
     * @return the formatted name.
     */
    String formatNameForDisplay(String name);

    /**
     * Normalize the given name. Normalization removes dots and dash characters, and transforms
     * the text to upper-case.
     *
     * @param name the name to normalize
     * @return the normalized name.
     */
    String normalizeName(String name);

    /**
     * Replies if the given name is a sequence of short names. A short name is composed by a single letter
     * that could be followed by a dot character. The names may be separated by white spaces or dash characters.
     *
     * @param name the name to test
     * @return {@code true} if the given name is a sequence of short names.
     */
    boolean isShortName(String name);

    /**
     * Replies the different possible normalized names that could be computed from the given name.
     * <table>
     * <thead>
     * <tr><th>Input</th><th>Cases with short names</th><th>Cases without short names</th></tr>
     * </thead>
     * <tbody>
     * <tr><td>{@code Name}</td><td>{@code Name}, {@code N}</td><td>{@code Name}</td></tr>
     * <tr><td>{@code First Second}</td><td>{@code First Second},
     * {@code F Second}, {@code First S}, {@code F S}</td><td>{@code First Second}</td></tr>
     * <tr><td>{@code First-Second}</td><td>{@code First Second},
     * {@code F Second}, {@code First S}, {@code F S}</td><td>{@code First Second}</td></tr>
     * </tbody>
     * </table>
     *
     * @param name                the name to parse.
     * @param enableShortNames    indicates if the short names should be added to the returned set. A short name is
     *                            a name with a single character.
     * @param progressiveBuilding indicates if, when the nae has multiple subcomponents, the replied list contains
     *                            the subcomponents one by one. For example, if the name contains {@code A B}, the replied list will
     *                            contain {@code [A, A B]}.
     * @return the different syntactic cases.
     */
    Set<String> getNormalizedNamesFor(String name, boolean enableShortNames, boolean progressiveBuilding);

    /**
     * Callback that is invoked when a name is discovered.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    interface NameCallback {

        /**
         * Callback function.
         *
         * @param firstname the discovered firstname.
         * @param von       the discovered von particle.
         * @param lastname  the discovered lastname.
         * @param position  position of the name in the list, started at {@code 0} for the first name.
         */
        void name(String firstname, String von, String lastname, int position);
    }

}
