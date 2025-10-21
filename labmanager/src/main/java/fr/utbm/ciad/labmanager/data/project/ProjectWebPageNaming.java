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
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.Locale;

/**
 * Type of webpage naming for projects. This type describes how the address of a project's webpage could be built up.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
public enum ProjectWebPageNaming {
    /**
     * Naming convention not specified, i.e., no web page.
     */
    UNSPECIFIED {
        @Override
        public String getWebpageIdFor(Project project) {
            return null;
        }
    },

    /**
     * The URL of the project's webpage is: {@code project-<ID>}.
     */
    PROJECT_ID {
        @Override
        public String getWebpageIdFor(Project project) {
            final var id = project != null ? project.getId() : 0;
            if (id != 0) {
                return "project-" + id; //$NON-NLS-1$
            }
            return null;
        }
    },

    /**
     * The URL of the project's webpage is: {@code <ACRONYM>}.
     * Accents are removed, and characters that are not an ASCII letter, digit, or one of
     * {@code _-.} are stripped.
     */
    ACRONYM {
        @Override
        public String getWebpageIdFor(Project project) {
            if (project != null) {
                var acronym = StringUtils.stripAccents(project.getAcronym());
                if (!Strings.isNullOrEmpty(acronym)) {
                    acronym = acronym.toLowerCase();
                    return acronym.replaceAll("[^a-z0-9_\\-\\.]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            return null;
        }
    },

    /**
     * The URL of the project's webpage is: {@code project/<ACRONYM>}.
     * Accents are removed, and characters that are not an ASCII letter, digit, or one of
     * {@code _-.} are stripped.
     */
    PROJECT_ACRONYM {
        @Override
        public String getWebpageIdFor(Project project) {
            if (project != null) {
                var acronym = StringUtils.stripAccents(project.getAcronym());
                if (!Strings.isNullOrEmpty(acronym)) {
                    acronym = acronym.toLowerCase();
                    return "project/" + acronym.replaceAll("[^a-z0-9_\\-\\.]+", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
            return null;
        }
    };

    private static final UriBuilderFactory FACTORY = new DefaultUriBuilderFactory();

    private static final String MESSAGE_PREFIX = "projectwebpagenaming."; //$NON-NLS-1$

    /**
     * Replies the naming that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the naming, to search for.
     * @return the naming.
     * @throws IllegalArgumentException if the given name does not corresponds to a naming.
     */
    public static ProjectWebPageNaming valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid webpage naming: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the status in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the URI of the webpage for the given project.
     *
     * @param project the project.
     * @return the URI, or {@code null}.
     */
    public URI getWebpageURIFor(Project project) {
        final var id = getWebpageIdFor(project);
        if (Strings.isNullOrEmpty(id)) {
            return null;
        }
        try {
            var b = FACTORY.builder();
            b = b.path("/" + id); //$NON-NLS-1$
            return b.build();
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Replies the identifier of the webpage for the given project.
     *
     * @param project the project.
     * @return the identifier.
     */
    public abstract String getWebpageIdFor(Project project);

}
