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

import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

/**
 * Utilities for comparing person names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface PersonNameComparator {

    /**
     * Replies the internal similarity computer.
     *
     * @return the internal similarity computer.
     * @since 4.0
     */
    NormalizedStringSimilarity getStringSimilarityComputer();

    /**
     * Compute and replies the similarity between the names of two persons.
     * <p>
     * This function supports the short names, e.g., initial for the first name.
     * For example, {@code Stephane Galland} is similar to {@code S Galland} and
     * {@code S. Galland}. And, {@code Jean-Pierre Dupont} is similar to
     * {@code J P Dupont}, {@code J-P Dupont}, {@code J. P. Dupont}, {@code J.-P. Dupont},
     * {@code J. P Dupont}, {@code J.-P Dupont}, {@code J P. Dupont} and {@code J-P. Dupont}.
     *
     * @param fullName1 the first full name.
     * @param fullName2 the second full name.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarity(String fullName1, String fullName2);

    /**
     * Compute and replies the similarity between the names of two persons.
     * <p>
     * This function supports the short names, e.g., initial for the first name.
     * For example, {@code Stephane Galland} is similar to {@code S Galland} and
     * {@code S. Galland}. And, {@code Jean-Pierre Dupont} is similar to
     * {@code J P Dupont}, {@code J-P Dupont}, {@code J. P. Dupont}, {@code J.-P. Dupont},
     * {@code J. P Dupont}, {@code J.-P Dupont}, {@code J P. Dupont} and {@code J-P. Dupont}.
     *
     * @param firstName1 the first name of the first person.
     * @param lastName1  the last name of the first person.
     * @param firstName2 the first name of the second person.
     * @param lastName2  the last name of the second person.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarity(String firstName1, String lastName1, String firstName2, String lastName2);

    /**
     * Replies the similarity level to consider for assuming that two names are similar.
     *
     * @return the minimum level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarityLevel();

    /**
     * Change the similarity level to consider for assuming that two names are similar.
     *
     * @param similarityLevel the minimum level of similarity. {@code 0} means that the names are not
     *                        similar, and {@code 1} means that they are totally equal.
     */
    void setSimilarityLevel(double similarityLevel);

    /**
     * Check name similarity between the names of two persons.
     * <p>
     * This function supports the short names, e.g., initial for the first name.
     * For example, {@code Stephane Galland} is similar to {@code S Galland} and
     * {@code S. Galland}. And, {@code Jean-Pierre Dupont} is similar to
     * {@code J P Dupont}, {@code J-P Dupont}, {@code J. P. Dupont}, {@code J.-P. Dupont},
     * {@code J. P Dupont}, {@code J.-P Dupont}, {@code J P. Dupont} and {@code J-P. Dupont}.
     *
     * @param fullName1 the first full name.
     * @param fullName2 the second full name.
     * @return {@code true} if the two given names are similar.
     */
    default boolean isSimilar(String fullName1, String fullName2) {
        return getSimilarity(fullName1, fullName2) >= getSimilarityLevel();
    }

    /**
     * Check name similarity between the names of two persons.
     * <p>
     * This function supports the short names, e.g., initial for the first name.
     * For example, {@code Stephane Galland} is similar to {@code S Galland} and
     * {@code S. Galland}. And, {@code Jean-Pierre Dupont} is similar to
     * {@code J P Dupont}, {@code J-P Dupont}, {@code J. P. Dupont}, {@code J.-P. Dupont},
     * {@code J. P Dupont}, {@code J.-P Dupont}, {@code J P. Dupont} and {@code J-P. Dupont}.
     *
     * @param firstName1 the first name of the first person.
     * @param lastName1  the last name of the first person.
     * @param firstName2 the first name of the second person.
     * @param lastName2  the last name of the second person.
     * @return {@code true} if the two given names are similar.
     */
    default boolean isSimilar(String firstName1, String lastName1, String firstName2, String lastName2) {
        return getSimilarity(firstName1, lastName1, firstName2, lastName2) >= getSimilarityLevel();
    }

}
