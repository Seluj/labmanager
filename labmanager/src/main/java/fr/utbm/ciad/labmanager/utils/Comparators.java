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

package fr.utbm.ciad.labmanager.utils;

import java.time.LocalDate;

/**
 * Utilities functions for comparing various types of objects
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class Comparators {

    private Comparators() {
        //
    }

    /**
     * Null-safe comparison the two date ranges.
     * A start date equal to {@code null} is accepted as equivalent to
     * the Big Bang date (far in the past).
     * A end date equal to {@code null} is accepted as equivalent to
     * the Big Crunch date (far in the future).
     *
     * @param s0 the start date of the first range.
     * @param e0 the end date of the first range.
     * @param s1 the start date of the first range.
     * @param e1 the end date of the first range.
     * @return 0 if {@code b0} = {@code b1}, negative if {@code b0} is {@code null} or {@code b0} &lt; {@code b1}, positive elsewhere.
     */
    public static int compareDateRange(LocalDate s0, LocalDate e0, LocalDate s1, LocalDate e1) {
        var cmp = compareEndDate(e0, e1);
        if (cmp != 0) {
            return cmp;
        }
        return compareStartDate(s0, s1);
    }

    /**
     * Null-safe comparison the two dates. A date equal to {@code null} is accepted as equivalent to
     * the Big Crunch date (far in the future).
     *
     * @param d0 the first value.
     * @param d1 the second value.
     * @return 0 if {@code d0} = {@code d1}, negative if {@code d0} is {@code null} or {@code d0} &lt; {@code d1}, positive elsewhere.
     * @see #compareStartDate(LocalDate, LocalDate)
     * @see #compareEndDate(LocalDate, LocalDate)
     * @see #compareDateRangeReverse(LocalDate, LocalDate, LocalDate, LocalDate)
     */
    public static int compareDateReverse(LocalDate d0, LocalDate d1) {
        if (d0 == d1) {
            return 0;
        }
        if (d0 == null) {
            return Integer.MIN_VALUE;
        }
        if (d1 == null) {
            return Integer.MAX_VALUE;
        }
        return d0.compareTo(d1);
    }

    /**
     * Null-safe comparison the two dates as they are start dates of a membership.
     * A date equal to {@code null} is accepted as equivalent to the Big Bang date
     * (far in the past).
     *
     * @param d0 the first value.
     * @param d1 the second value.
     * @return 0 if {@code d0} = {@code d1}, negative if {@code d0} is {@code null} or {@code d0} &lt; {@code d1}, positive elsewhere.
     * @see #compareDateReverse(LocalDate, LocalDate)
     * @see #compareEndDate(LocalDate, LocalDate)
     * @see #compareDateRangeReverse(LocalDate, LocalDate, LocalDate, LocalDate)
     */
    public static int compareStartDate(LocalDate d0, LocalDate d1) {
        if (d0 == d1) {
            return 0;
        }
        if (d0 == null) {
            return Integer.MIN_VALUE;
        }
        if (d1 == null) {
            return Integer.MAX_VALUE;
        }
        return d0.compareTo(d1);
    }

    /**
     * Null-safe comparison the two dates as they are end dates of a membership.
     * A date equal to {@code null} is accepted as equivalent to the Big Crunch date (far in the future).
     *
     * @param d0 the first value.
     * @param d1 the second value.
     * @return 0 if {@code d0} = {@code d1}, negative if {@code d1} is {@code null} or {@code d0} &lt; {@code d1}, positive elsewhere.
     * @see #compareDateReverse(LocalDate, LocalDate)
     * @see #compareStartDate(LocalDate, LocalDate)
     * @see #compareDateRangeReverse(LocalDate, LocalDate, LocalDate, LocalDate)
     */
    public static int compareEndDate(LocalDate d0, LocalDate d1) {
        if (d0 == d1) {
            return 0;
        }
        if (d0 == null) {
            return Integer.MAX_VALUE;
        }
        if (d1 == null) {
            return Integer.MIN_VALUE;
        }
        return d0.compareTo(d1);
    }

    /**
     * Null-safe comparison the two responsabilities.
     *
     * @param g0 the first value.
     * @param s1 the second value.
     * @return 0 if {@code b0} = {@code b1}, negative if {@code b0} is {@code null} or {@code b0} &lt; {@code b1}, positive elsewhere.
     */
    public static <T extends Comparable<? super T>> int compare(T g0, T s1) {
        if (g0 == s1) {
            return 0;
        }
        if (g0 == null) {
            return Integer.MIN_VALUE;
        }
        if (s1 == null) {
            return Integer.MAX_VALUE;
        }
        return g0.compareTo(s1);
    }

}


