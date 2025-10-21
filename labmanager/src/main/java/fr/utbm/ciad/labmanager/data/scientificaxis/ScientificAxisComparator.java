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

package fr.utbm.ciad.labmanager.data.scientificaxis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Comparator of scientific axes.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.5
 */
@Component
@Primary
public class ScientificAxisComparator implements Comparator<ScientificAxis> {

    /**
     * Constructor.
     */
    public ScientificAxisComparator() {
        //
    }

    /**
     * Null-safe comparison the two dates.
     *
     * @param s0 the first value.
     * @param s1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareDate(LocalDate d0, LocalDate d1) {
        if (d0 == d1) {
            return 0;
        }
        if (d0 == null) {
            return Integer.MIN_VALUE;
        }
        if (d1 == null) {
            return Integer.MAX_VALUE;
        }
        return d1.compareTo(d0);
    }

    @Override
    public int compare(ScientificAxis o1, ScientificAxis o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = compareDate(o1.getEndDate(), o2.getEndDate());
        if (n != 0) {
            return n;
        }
        n = compareDate(o1.getStartDate(), o2.getStartDate());
        if (n != 0) {
            return n;
        }
        n = StringUtils.compareIgnoreCase(o1.getAcronym(), o2.getAcronym());
        if (n != 0) {
            return n;
        }
        return StringUtils.compareIgnoreCase(o1.getName(), o2.getName());
    }

}


