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
 * Describe the type of activity that is related to a project.
 *
 * @author $Author: sgalland$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://anr.fr/fileadmin/documents/2017/guide-CIR-2017.pdf"
 * @since 3.0
 */
public enum ProjectActivityType {

    /**
     * Activity is fundamental research for acquiring new knowledge.
     */
    FUNDAMENTAL_RESEARCH,

    /**
     * Activity is applied research for acquiring new knowledge in an application domain.
     */
    APPLIED_RESEARCH,

    /**
     * Activity is experimental development that applies technical elements for: (i) the creation
     * of new software product, (ii) the creation of new methods, processes, services or systems,
     * (iii) the substantial improvement of existing elements.
     */
    EXPERIMENTAL_DEVELOPMENT;

    private static final String MESSAGE_PREFIX = "projectActivityType."; //$NON-NLS-1$

    /**
     * Replies the type of activity that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the activity type, to search for.
     * @return the type of activity.
     * @throws IllegalArgumentException if the given name does not corresponds to a type of activity.
     */
    public static ProjectActivityType valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid activity type: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the activity type in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the activity type in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
