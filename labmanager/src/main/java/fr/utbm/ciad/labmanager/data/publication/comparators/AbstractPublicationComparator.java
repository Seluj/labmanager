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

import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.AbstractNormalizableStringComparator;

/**
 * Abstract comparator of publications based on a normalized similarity.
 * For comparison, the order of the publication is based on the
 * type of publication, the year, the authors, the identifier.
 * For similarity, an algorithm is used that is defined in sub-classes.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPublicationComparator extends AbstractNormalizableStringComparator implements PublicationComparator {

    /**
     * Constructor.
     *
     * @param defaultSimilarity the default similarity.
     */
    public AbstractPublicationComparator(double defaultSimilarity) {
        setSimilarityLevel(defaultSimilarity);
    }

    private static String getPublicationDescription(Publication publication) {
        final var buf = new StringBuilder();
        buf.append(publication.getPublicationYear());
        buf.append(',');
        if (publication.getType() != null) {
            buf.append(publication.getType().ordinal());
        } else {
            buf.append('0');
        }
        buf.append(',');
        buf.append(publication.getTitle());
        buf.append(',');
        buf.append(publication.getWherePublishedShortDescription());
        buf.append(',');
        if (publication.getDOI() != null) {
            buf.append(publication.getDOI());
        }
        buf.append(',');
        if (publication.getISBN() != null) {
            buf.append(publication.getISBN());
        }
        buf.append(',');
        if (publication.getISSN() != null) {
            buf.append(publication.getISSN());
        }
        return buf.toString();
    }

    @Override
    public int compare(Publication o1, Publication o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = o1.getType().compareTo(o2.getType());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getPublicationYear(), o2.getPublicationYear());
        if (cmp != 0) {
            return cmp;
        }
        return EntityUtils.getPreferredPersonListComparator().compare(o1.getAuthors(), o2.getAuthors());
    }

    private double similarity(String s1, String s2) {
        if (s1 == s2) {
            return 1.0;
        }
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        return getStringSimilarityComputer().similarity(s1, s2);
    }

    @Override
    public double getSimilarity(Publication publication1, Publication publication2) {
        if (publication1 == publication2) {
            return 1.0;
        }
        if (publication1 == null || publication2 == null) {
            return 0.0;
        }
        final var desc1 = getPublicationDescription(publication1);
        final var desc2 = getPublicationDescription(publication2);
        return similarity(desc1, desc2);
    }

}
