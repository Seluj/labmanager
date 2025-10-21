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

package fr.utbm.ciad.labmanager.data.publication;

import com.google.common.base.Strings;

import java.util.Locale;

/**
 * Name of the language in which a research publication is published.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
public enum PublicationLanguage {
    /**
     * Publication is mostly written in English.
     */
    ENGLISH {
        @Override
        public Locale getLocale() {
            return Locale.US;
        }
    },
    /**
     * Publication is mostly written in French.
     */
    FRENCH {
        @Override
        public Locale getLocale() {
            return Locale.FRENCH;
        }
    },
    /**
     * Publication is mostly written in German.
     */
    GERMAN {
        @Override
        public Locale getLocale() {
            return Locale.GERMAN;
        }
    },
    /**
     * Publication is mostly written in Italian.
     */
    ITALIAN {
        @Override
        public Locale getLocale() {
            return Locale.ITALIAN;
        }
    },
    /**
     * Publication is mostly written in a language that is not specified.
     */
    OTHER {
        @Override
        public Locale getLocale() {
            assert getDefault() != this;
            return getDefault().getLocale();
        }

        @Override
        public String getLabel(Locale displayLocale) {
            return "?"; //$NON-NLS-1$
        }
    };

    /**
     * Replies the default publication language.
     *
     * @return the default publication language, i.e. English.
     */
    public static PublicationLanguage getDefault() {
        return ENGLISH;
    }

    /**
     * Replies the publication language that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the language, to search for.
     * @return the type, or the value replied by {@link #getDefault()} if the name does not corresponds to
     * a known language.
     */
    public static PublicationLanguage valueOfCaseInsensitive(String name) {
        return valueOfCaseInsensitive(name, getDefault());
    }

    /**
     * Replies the publication language that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name         the name of the language, to search for.
     * @param defaultValue the default language.
     * @return the type, or the value {@code defaultValue} if the name does not corresponds to
     * a known language.
     * @since 3.8
     */
    public static PublicationLanguage valueOfCaseInsensitive(String name, PublicationLanguage defaultValue) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var lang : values()) {
                if (name.equalsIgnoreCase(lang.name())) {
                    return lang;
                }
            }
        }
        return defaultValue;
    }

    /**
     * Replies the Java locale that corresponds to the publication language.
     *
     * @return the locale.
     */
    public abstract Locale getLocale();

    /**
     * Replies the label of the current language.
     *
     * @param displayLocale the locale for the display.
     * @return the label of the current language.
     */
    public String getLabel(Locale displayLocale) {
        return getLocale().getDisplayLanguage(displayLocale);
    }

}
