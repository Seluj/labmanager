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

package fr.utbm.ciad.labmanager.data.member;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Gender of a person.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public enum Gender {
    /**
     * Unknown gender.
     */
    NOT_SPECIFIED {
        @Override
        public boolean hasLabel() {
            return false;
        }
    },
    /**
     * Male.
     */
    MALE {
        @Override
        public boolean hasLabel() {
            return true;
        }
    },
    /**
     * Female.
     */
    FEMALE {
        @Override
        public boolean hasLabel() {
            return true;
        }
    },
    /**
     * Other gender.
     */
    OTHER {
        @Override
        public boolean hasLabel() {
            return true;
        }
    };

    private static final String MESSAGE_PREFIX = "gender."; //$NON-NLS-1$

    private static final String CIVIL_TITLE_POSTFIX = "_title"; //$NON-NLS-1$

    /**
     * Replies the gender that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the gender, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static Gender valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var gender : values()) {
                if (name.equalsIgnoreCase(gender.name())) {
                    return gender;
                }
            }
        }
        throw new IllegalArgumentException("Invalid gender: " + name); //$NON-NLS-1$
    }

    /**
     * Replies if the given gender has a label.
     *
     * @return {@code true} if the given gender as a label.
     */
    public abstract boolean hasLabel();

    /**
     * Replies the label of the gender in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the gender in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the civil title of the gender in the current language.
     *
     * @param messages the accessor to the localized titles.
     * @param locale   the locale to use.
     * @return the civil title or {@code null} if none is applicable.
     */
    public String getCivilTitle(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name() + CIVIL_TITLE_POSTFIX, (String) null, locale);
        return Strings.nullToEmpty(label);
    }

}
