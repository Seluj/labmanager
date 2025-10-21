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

package fr.utbm.ciad.labmanager.data.publication.comparators;

import fr.utbm.ciad.labmanager.data.publication.Publication;

import java.util.Comparator;

/**
 * Comparator of publications. The order of the publication depends on the implementation
 * of this interface.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface PublicationComparator extends Comparator<Publication> {

    /**
     * Compute and replies the similarity between the publications.
     *
     * @param publication1 the first publication.
     * @param publication2 the second publication.
     * @return the level of similarity. {@code 0} means that the publications are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarity(Publication publication1, Publication publication2);

    /**
     * Replies the similarity level to consider for assuming that two publications are similar.
     *
     * @return the minimum level of similarity. {@code 0} means that the publication are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarityLevel();

    /**
     * Replies the similarity level to consider for assuming that two publications are similar.
     *
     * @param similarityLevel the minimum level of similarity. {@code 0} means that the publications are not
     *                        similar, and {@code 1} means that they are totally equal.
     */
    void setSimilarityLevel(double similarityLevel);

    /**
     * Check publication similarity between the publications.
     *
     * @param publication1 the first publication.
     * @param publication2 the second publication.
     * @return {@code true} if the two given publications are similar.
     */
    default boolean isSimilar(Publication publication1, Publication publication2) {
        final var similarity = getSimilarity(publication1, publication2);
        return similarity > getSimilarityLevel();
    }

}
