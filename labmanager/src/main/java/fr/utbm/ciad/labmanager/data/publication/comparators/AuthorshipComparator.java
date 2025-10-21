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

import fr.utbm.ciad.labmanager.data.publication.Authorship;

import java.util.Comparator;

/**
 * Comparator of authorships.
 * The order is defined by: <ul>
 * <li>The author rank in ascending order;</li>
 * <li>The persons in ascending order, in ascending order;</li>
 * <li>The database identifiers, in descending order.</li>
 * </ul>
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class AuthorshipComparator implements Comparator<Authorship> {

    /**
     * Singleton of a comparator of authorships.
     */
    public static final Comparator<Authorship> DEFAULT = new AuthorshipComparator();

    @Override
    public int compare(Authorship o1, Authorship o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = Integer.compare(o1.getAuthorRank(), o2.getAuthorRank());
        if (n != 0) {
            return n;
        }
        n = Long.compare(o1.getPerson().getId(), o2.getPerson().getId());
        if (n != 0) {
            return n;
        }
        return Long.compare(o1.getPublication().getId(), o2.getPublication().getId());
    }

}


