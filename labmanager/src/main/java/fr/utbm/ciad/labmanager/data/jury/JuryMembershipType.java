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

package fr.utbm.ciad.labmanager.data.jury;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.member.Gender;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Type of membership for a jury.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public enum JuryMembershipType {

    /**
     * President of the jury.
     */
    PRESIDENT,

    /**
     * Reviewer.
     */
    REVIEWER,

    /**
     * Examiner.
     */
    EXAMINER,

    /**
     * Invited person.
     */
    INVITED_PERSON;

    private static final String MESSAGE_PREFIX = "juryMembershipType."; //$NON-NLS-1$

    /**
     * Replies the jury membership that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the membership, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a membership.
     */
    public static JuryMembershipType valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid jury membership type: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the status in the current language.
     *
     * @param messages the accessors to the localized names.
     * @param gender   the gender of the person who has the position of this type.
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Gender gender, Locale locale) {
        var g = gender;
        if (g == null || g == Gender.NOT_SPECIFIED) {
            g = Gender.OTHER;
        }
        final var label = messages.getMessage(MESSAGE_PREFIX + name() + "_" + g.name(), locale); //$NON-NLS-1$
        return Strings.nullToEmpty(label);
    }

}
