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

import java.io.Serializable;

/**
 * Range of integer values.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public class IntegerRange implements Serializable {

    private static final long serialVersionUID = 5475666225792640252L;

    private final String separator;

    private final Integer min;

    private final Integer max;

    /**
     * Constructor.
     *
     * @param separator the seperator for rendering the range.
     * @param values    values to put in the range.
     */
    public IntegerRange(String separator, Iterable<Integer> values) {
        this.separator = separator;
        Integer mn = null;
        Integer mx = null;
        for (final var value : values) {
            if (value != null) {
                if (mn == null || value.intValue() < mn.intValue()) {
                    mn = value;
                }
                if (mx == null || value.intValue() > mx.intValue()) {
                    mx = value;
                }
            }
        }
        this.min = mn;
        this.max = mx;
    }

    /**
     * Constructor.
     *
     * @param separator the seperator for rendering the range.
     * @param values    values to put in the range.
     */
    public IntegerRange(String separator, int... values) {
        this.separator = separator;
        Integer mn = null;
        Integer mx = null;
        for (final var value : values) {
            if (mn == null || value < mn.intValue()) {
                mn = Integer.valueOf(value);
            }
            if (mx == null || value > mx.intValue()) {
                mx = Integer.valueOf(value);
            }
        }
        this.min = mn;
        this.max = mx;
    }

    /**
     * Constructor.
     *
     * @param values values to put in the range.
     */
    public IntegerRange(Iterable<Integer> values) {
        this("-", values); //$NON-NLS-1$
    }

    /**
     * Constructor.
     *
     * @param values values to put in the range.
     */
    public IntegerRange(int... values) {
        this("-", values); //$NON-NLS-1$
    }

    /**
     * Replies the range start (min value).
     *
     * @return the min value or {@code null} if unknown.
     */
    public Integer getMin() {
        return this.min;
    }

    /**
     * Replies the range end (max value).
     *
     * @return the max value or {@code null} if unknown.
     */
    public Integer getMax() {
        return this.max;
    }

    @Override
    public String toString() {
        final var mn = getMin();
        final var mx = getMax();
        if (mn == null) {
            if (mx == null) {
                return ""; //$NON-NLS-1$
            }
            return mx.toString();
        }
        if (mx == null || mn.intValue() == mx.intValue()) {
            return mn.toString();
        }
        String b = mn +
                this.separator +
                mx;
        return b;
    }

}
