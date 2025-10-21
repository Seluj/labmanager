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

package fr.utbm.ciad.labmanager.utils.ranking;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Systems that are considered for ranking journals.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public enum JournalRankingSystem {
    /**
     * Scimago.
     */
    SCIMAGO,
    /**
     * Web of Science.
     */
    WOS;


    private static final String MESSAGE_PREFIX = "journalRankingSystem."; //$NON-NLS-1$

    /**
     * Replies the journal ranking system that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the journal ranking system, to search for.
     * @return the journal ranking system.
     * @throws IllegalArgumentException if the given name does not corresponds to a journal ranking system.
     */
    public static JournalRankingSystem valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var section : values()) {
                if (name.equalsIgnoreCase(section.name())) {
                    return section;
                }
            }
        }
        throw new IllegalArgumentException("Invalid journal ranking system: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the default journal ranking system.
     *
     * @return the default journal ranking system.
     */
    public static JournalRankingSystem getDefault() {
        return JournalRankingSystem.SCIMAGO;
    }

    /**
     * Replies the label of the journal ranking system in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the journal ranking system in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
