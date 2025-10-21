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

package fr.utbm.ciad.labmanager.data.teaching;

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Comparator of teaching activities. The order of the activities depends on the implementation
 * of this interface.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
@Component
@Primary
public class TeachingActivityComparator implements Comparator<TeachingActivity> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of persons.
     */
    public TeachingActivityComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    /**
     * Null-safe comparison the two teaching activity's levels.
     *
     * @param v0 the first value.
     * @param v1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareLevel(TeachingActivityLevel v0, TeachingActivityLevel v1) {
        if (v0 == v1) {
            return 0;
        }
        if (v0 == null) {
            return Integer.MIN_VALUE;
        }
        if (v1 == null) {
            return Integer.MAX_VALUE;
        }
        return v0.compareTo(v1);
    }

    /**
     * Null-safe comparison the two student types.
     *
     * @param v0 the first value.
     * @param v1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareStudentType(StudentType v0, StudentType v1) {
        if (v0 == v1) {
            return 0;
        }
        if (v0 == null) {
            return Integer.MIN_VALUE;
        }
        if (v1 == null) {
            return Integer.MAX_VALUE;
        }
        return v0.compareTo(v1);
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
    public int compare(TeachingActivity a1, TeachingActivity a2) {
        if (a1 == a2) {
            return 0;
        }
        if (a1 == null) {
            return Integer.MIN_VALUE;
        }
        if (a2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = this.personComparator.compare(a1.getPerson(), a2.getPerson());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareLevel(a1.getLevel(), a2.getLevel());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareDate(a1.getEndDate(), a2.getEndDate());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareDate(a1.getStartDate(), a2.getStartDate());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareStudentType(a1.getStudentType(), a2.getStudentType());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(a1.getCode(), a2.getCode());
        if (cmp != 0) {
            return cmp;
        }
        return StringUtils.compare(a1.getTitle(), a2.getTitle());
    }

}
