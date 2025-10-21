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

import com.google.common.base.Strings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utilities for text.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 * @deprecated no replacement.
 */
@Deprecated(since = "4.0", forRemoval = true)
public final class TextUtils {

    private final static Set<Character> APOSTROPHE = new TreeSet<>(Arrays.asList(
            Character.valueOf('a'), Character.valueOf('e'), Character.valueOf('i'),
            Character.valueOf('o'), Character.valueOf('u'), Character.valueOf('y'),
            Character.valueOf('h')));

    private TextUtils() {
        //
    }

    /**
     * Replies if the given string should be prefixed by a string with the {@code '}
     * character is some language.
     *
     * @param text the text to check.
     * @return {@code true} if the given string should be prefixed by the {@code '} character.
     */
    public static boolean isApostrophable(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return false;
        }
        final var c = Character.toLowerCase(text.charAt(0));
        return APOSTROPHE.contains(Character.valueOf(c));
    }

    /**
     * Replies the preferred string representation of the given number.
     *
     * @param number the number to format.
     * @param unit   the unit to user for formatting.
     * @param locale the locale to use.
     * @return the string representation of the number.
     */
    public static String formatNumber(Number number, Unit unit, Locale locale) {
        if (number != null) {
            final var cvalue = unit == null ? number : unit.convertFromUnit(number);
            final var symbols = new DecimalFormatSymbols(locale == null ? Locale.US : locale);
            final var format = new DecimalFormat("#0.#", symbols); //$NON-NLS-1$
            return format.format(cvalue);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Safe case-insensitive comparison of strings.
     *
     * @param a the first string.
     * @param b the second string.
     * @return negative if {@code a} is lower than {@code b}; position if {@code a} is greater than {@code b};
     * otherwise {@code 0}.
     * @since 3.6
     */
    public static int compareIgnoreCase(String a, String b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareToIgnoreCase(b);
    }

}
