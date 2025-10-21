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

package fr.utbm.ciad.labmanager.utils.trl;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Technology readiness levels (TRLs) are a method for estimating the maturity of technologies during the acquisition phase
 * of a program. TRLs enable consistent and uniform discussions of technical maturity across different types of technology.
 *
 * @author $Author: sgalland$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/Technology_readiness_level"
 * @since 3.0
 */
public enum TRL {

    /**
     * Basic principles observed.
     */
    TRL1,

    /**
     * Technology concept formulated.
     */
    TRL2,

    /**
     * Experimental proof of concept.
     */
    TRL3,

    /**
     * Technology validated in lab.
     */
    TRL4,

    /**
     * Technology validated in relevant environment (industrially relevant environment in the case of key enabling technologies).
     */
    TRL5,

    /**
     * Technology demonstrated in relevant environment (industrially relevant environment in the case of key enabling technologies).
     */
    TRL6,

    /**
     * System prototype demonstration in operational environment.
     */
    TRL7,

    /**
     * System complete and qualified.
     */
    TRL8,

    /**
     * Actual system proven in operational environment (competitive manufacturing in the case of key enabling technologies; or in space).
     */
    TRL9;

    private static final String MESSAGE_PREFIX = "trl."; //$NON-NLS-1$

    /**
     * Parse the given string for obtaining the TRL.
     *
     * @param stringTrl the string representation of the TRL.
     * @return the TRL or {@code null} if the given string cannot match.
     */
    public static TRL valueOfCaseInsensitive(String stringTrl) {
        if (!Strings.isNullOrEmpty(stringTrl)) {
            for (final var candidate : values()) {
                if (candidate.toString().equalsIgnoreCase(stringTrl)
                        || Integer.toString(candidate.getLevel()).equals(stringTrl)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Replies the label of the project status in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the project status in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the TRL level. It is the ordinal number plus one.
     *
     * @return the TRL level.
     */
    public int getLevel() {
        return ordinal() + 1;
    }

}
