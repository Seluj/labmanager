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
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.Locale;

/**
 * Type of webpage naming for persons. This type describes how the address of a person's webpage could be built up.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public enum WebPageNaming {
    /**
     * Naming convention not specified, i.e., no web page.
     */
    UNSPECIFIED {
        @Override
        public String getWebpageIdFor(Person person) {
            return null;
        }
    },

    /**
     * The URL of the person's webpage is: {@code author-<ID>}.
     */
    AUTHOR_ID {
        @Override
        public String getWebpageIdFor(Person person) {
            return "author-" + person.getId(); //$NON-NLS-1$
        }
    },

    /**
     * The URL of the person's webpage is: {@code <PRIMARY_EMAIL_ID>} where the email of the person is defined by {@code <EMAIL_ID>@<DOMAIN>}.
     */
    EMAIL_ID {
        @Override
        public String getWebpageIdFor(Person person) {
            final var email = person.getPrimaryEmail();
            final var id = StringUtils.substringBefore(email, "@"); //$NON-NLS-1$
            return id;
        }
    },

    /**
     * The URL of the person's webpage is: {@code <FIRST_NAME>_<LAST_NAME>}.
     * Accents are removed, and characters that are not an ASCII letter, digit, or one of
     * {@code _-.} are stripped.
     */
    FIRST_LAST {
        @Override
        public String getWebpageIdFor(Person person) {
            final var first = StringUtils.stripAccents(person.getFirstName());
            final var last = StringUtils.stripAccents(person.getLastName());
            final String ref;
            if (Strings.isNullOrEmpty(last)) {
                if (Strings.isNullOrEmpty(first)) {
                    return null;
                }
                ref = first.toLowerCase();
            } else if (Strings.isNullOrEmpty(first)) {
                ref = last.toLowerCase();
            } else {
                ref = first.toLowerCase() + "_" + last.toLowerCase(); //$NON-NLS-1$
            }
            return ref.replaceAll("[^a-z0-9_\\-\\.]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
    };

    private static final UriBuilderFactory FACTORY = new DefaultUriBuilderFactory();

    private static final String MESSAGE_PREFIX = "webpagenaming."; //$NON-NLS-1$

    /**
     * Replies the naming that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the naming, to search for.
     * @return the naming.
     * @throws IllegalArgumentException if the given name does not corresponds to a naming.
     */
    public static WebPageNaming valueOfCaseInsensitive(String name) {
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
     * Replies the URI of the webpage for the given person.
     *
     * @param person the person.
     * @return the URI, or {@code null}.
     */
    public URI getWebpageURIFor(Person person) {
        final var id = getWebpageIdFor(person);
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
     * Replies the identifier of the webpage for the given person.
     *
     * @param person the person.
     * @return the identifier.
     */
    public abstract String getWebpageIdFor(Person person);

}
