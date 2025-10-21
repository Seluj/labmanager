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

/**
 * Utilities for comparing organization names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public interface OrganizationNameComparator {

    /**
     * Compute and replies the similarity between the acronyms and names of two organizations.
     *
     * @param acronym1 the first acronym.
     * @param name1    the first name.
     * @param acronym2 the second acronym.
     * @param name2    the second name.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarity(String acronym1, String name1, String acronym2, String name2);

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
     * Check name similarity between the names and the acronyms of two organizations.
     *
     * @param acronym1 the first acronym.
     * @param name1    the first name.
     * @param acronym2 the second acronym.
     * @param name2    the second name.
     * @return {@code true} if the two given names are similar.
     */
    default boolean isSimilar(String acronym1, String name1, String acronym2, String name2) {
        return getSimilarity(acronym1, name1, acronym2, name2) >= getSimilarityLevel();
    }

}
