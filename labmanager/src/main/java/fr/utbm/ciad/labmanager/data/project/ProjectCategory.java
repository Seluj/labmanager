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

package fr.utbm.ciad.labmanager.data.project;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Describe the category of a project.
 * The order of the items (their ordinal numbers) is from the less important to the more important.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
public enum ProjectCategory {

    /**
     * Project that is open source, freely available on Internet.
     */
    OPEN_SOURCE {
        @Override
        public boolean isContractualProject() {
            return false;
        }
    },

    /**
     * Project that is auto-funding.
     */
    AUTO_FUNDING {
        @Override
        public boolean isContractualProject() {
            return false;
        }
    },

    /**
     * Project with not academic partner.
     */
    NOT_ACADEMIC_PROJECT {
        @Override
        public boolean isContractualProject() {
            return true;
        }
    },

    /**
     * Project in a competitive call.
     */
    COMPETITIVE_CALL_PROJECT {
        @Override
        public boolean isContractualProject() {
            return true;
        }
    };

    private static final String MESSAGE_PREFIX = "projectCategory."; //$NON-NLS-1$

    /**
     * Replies the category that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the category, to search for.
     * @return the category.
     * @throws IllegalArgumentException if the given name does not corresponds to a category.
     */
    public static ProjectCategory valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid category: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the category in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the category in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the ordinal number of this item in reverse order.
     *
     * @return the ordinal from the end
     * @see #ordinal()
     * @since 3.0
     */
    public int reverseOrdinal() {
        return values().length - ordinal() - 1;
    }

    /**
     * Replies if the category of project has a contract (public or private).
     *
     * @return {@code true} if the project category needs a contract or a consortium agreement.
     * @since 3.6
     */
    public abstract boolean isContractualProject();

}
