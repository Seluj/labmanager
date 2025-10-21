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

package fr.utbm.ciad.labmanager.data.conference;

import com.google.common.base.Strings;

import java.util.regex.Pattern;

/**
 * Utilities for managing Scientific or scientific culture dissemination conference.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.8
 */
public final class ConferenceUtils {

    private static final String[] CONFERENCE_NUMBER_POSTFIX = {
            "st",    //$NON-NLS-1$
            "nd",    //$NON-NLS-1$
            "rd",    //$NON-NLS-1$
            "th",    //$NON-NLS-1$
            "ère",    //$NON-NLS-1$
            "ere",    //$NON-NLS-1$
            "er",    //$NON-NLS-1$
            "ème",    //$NON-NLS-1$
            "eme",    //$NON-NLS-1$
            "",    //$NON-NLS-1$
    };

    private static final String[] PREFIXES = {
            "the",    //$NON-NLS-1$
            "a",    //$NON-NLS-1$
            "an",    //$NON-NLS-1$
            "le",    //$NON-NLS-1$
            "la",    //$NON-NLS-1$
            "l'",    //$NON-NLS-1$
            "les",    //$NON-NLS-1$
            "un",    //$NON-NLS-1$
            "une",    //$NON-NLS-1$
            "der",    //$NON-NLS-1$
            "das",    //$NON-NLS-1$
            "die",    //$NON-NLS-1$
            "ein",    //$NON-NLS-1$
            "el",    //$NON-NLS-1$
            "las",    //$NON-NLS-1$
            "los",    //$NON-NLS-1$
            "una",    //$NON-NLS-1$
    };

    private static final String EMPTY_FIELD_PATTERN_STR = "^[*+_:;,.=\\-\\\\]+$"; //$NON-NLS-1$

    private static final Pattern EMPTY_FIELD_PATTERN = Pattern.compile(EMPTY_FIELD_PATTERN_STR);

    private ConferenceUtils() {
        //
    }

    /**
     * Normalize the string by removing the marking characters that are used for representing an empty information.
     *
     * @param value the value to normalize.
     * @return the normalized value.
     */
    public static String normalizeString(String value) {
        if (value != null) {
            var nvalue = value.trim();
            final var matcher = EMPTY_FIELD_PATTERN.matcher(nvalue);
            if (matcher.matches()) {
                return null;
            }
            return Strings.emptyToNull(nvalue);
        }
        return null;
    }

    /**
     * Extract the occurrence number and name of a conference from the given full name.
     *
     * <p>If the argument is equal to {@code 14th International Conference on Artificial Intelligence}, then this function
     * extract the name of the conference, i.e., {@code International Conference on Artificial Intelligence} and
     * the conference occurrence's number is {@code 14}.
     *
     * @param name the name to analyze.
     * @return the conference name's components.
     */
    public static ConferenceNameComponents parseConferenceName(String name) {
        if (name != null) {
            final var nname = normalizeString(name.trim());
            if (!Strings.isNullOrEmpty(nname)) {
                final var patternStr = new StringBuilder();
                for (final var postfix : CONFERENCE_NUMBER_POSTFIX) {
                    patternStr.setLength(0);
                    patternStr.append("^([0-9]+)\\s*");//$NON-NLS-1$
                    patternStr.append(postfix);
                    patternStr.append("\\s+(.*?)$");//$NON-NLS-1$
                    final var pattern = Pattern.compile(patternStr.toString(), Pattern.CASE_INSENSITIVE);
                    final var matcher = pattern.matcher(nname);
                    if (matcher.matches()) {
                        try {
                            final var number = Integer.parseInt(matcher.group(1));
                            return new ConferenceNameComponents(number, matcher.group(2));
                        } catch (Throwable ex) {
                            //
                        }
                    }
                }
            }
            return new ConferenceNameComponents(0, Strings.emptyToNull(nname));
        }
        return new ConferenceNameComponents(0, null);
    }

    /**
     * Remove the prefix article from the given sentence.
     * Examples of prefix articles are "the", "a".
     *
     * @param sentence the sentence to analyze.
     * @return the sentence without prefix article.
     */
    public static String removePrefixArticles(String sentence) {
        var finalSentence = sentence;
        if (!Strings.isNullOrEmpty(sentence)) {
            for (final var prefix : PREFIXES) {
                final var pattern = Pattern.compile("^\\s*" + Pattern.quote(prefix) + "\\s+", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$ //$NON-NLS-2$
                final var matcher = pattern.matcher(finalSentence);
                finalSentence = matcher.replaceFirst(""); //$NON-NLS-1$
            }
            finalSentence = finalSentence.trim();
        }
        return Strings.emptyToNull(finalSentence);
    }

    /**
     * Components of a conference name.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 3.6
     */
    public static class ConferenceNameComponents {

        /**
         * Number of the conference occurrence.
         */
        public final int occurrenceNumber;

        /**
         * Conference name.
         */
        public final String name;

        /**
         * Constructor.
         *
         * @param number number of the conference occurrence.
         * @param name   conference name.
         */
        ConferenceNameComponents(int number, String name) {
            this.occurrenceNumber = number;
            this.name = name;
        }
    }

}
