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

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Describe the level of teaching activity.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
public enum TeachingActivityLevel {

    /**
     * Activities are at the doctoral level.
     */
    DOCTORAL_DEGREE,

    /**
     * Activities are at the master level.
     */
    MASTER_DEGREE,

    /**
     * Activities are at the bachelor level (Licence in French).
     */
    BACHELOR_DEGREE,

    /**
     * Activities are at the level of high schools (lycées en French).
     */
    HIGH_SCHOOL_DEGREE;

    private static final String MESSAGE_PREFIX = "teachingActivityLevel."; //$NON-NLS-1$

    /**
     * Replies the level of activity that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the activity level, to search for.
     * @return the level of activity.
     * @throws IllegalArgumentException if the given name does not corresponds to a level of activity.
     */
    public static TeachingActivityLevel valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid activity level: " + name); //$NON-NLS-1$
    }

    /**
     * Replies all the teaching activity levels sorted according to their display names.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the list of all the levels.
     */
    public static List<TeachingActivityLevel> getAllDisplayLevels(MessageSourceAccessor messages, Locale locale) {
        return Arrays.asList(values()).stream().sorted(
                        (a, b) -> a.getLabel(messages, locale).compareToIgnoreCase(b.getLabel(messages, locale)))
                .collect(Collectors.toList());
    }

    /**
     * Replies the label of the activity level in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the activity level in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
