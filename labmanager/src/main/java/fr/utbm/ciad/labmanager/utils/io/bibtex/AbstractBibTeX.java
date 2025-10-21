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

package fr.utbm.ciad.labmanager.utils.io.bibtex;

import com.google.common.base.Strings;
import com.ibm.icu.text.Normalizer2;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Random;

/**
 * Abstract implementation of the utilities for BibTeX.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class AbstractBibTeX implements BibTeX {

    /**
     * Field {@code abstract}.
     */
    protected static final String KEY_ABSTRACT_NAME = "abstract"; //$NON-NLS-1$

    /**
     * Field {@code keywords}.
     */
    protected static final String KEY_KEYWORDS_NAME = "keywords"; //$NON-NLS-1$

    /**
     * Field {@code isbn}.
     */
    protected static final String KEY_ISBN_NAME = "isbn"; //$NON-NLS-1$

    /**
     * Field {@code issn}.
     */
    protected static final String KEY_ISSN_NAME = "issn"; //$NON-NLS-1$

    /**
     * Field {@code halid}.
     */
    protected static final String KEY_HALID_NAME = "halid"; //$NON-NLS-1$

    /**
     * Field {@code dblp}.
     */
    protected static final String KEY_DBLP_NAME = "dblp"; //$NON-NLS-1$

    /**
     * Field {@code video}.
     */
    protected static final String KEY_VIDEO_NAME = "_video"; //$NON-NLS-1$

    /**
     * Field {@code language}.
     */
    protected static final String KEY_LANGUAGE_NAME = "_language"; //$NON-NLS-1$

    private final Random random = new Random();

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param messages the accessor to the localized strings.
     */
    public AbstractBibTeX(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    private static boolean containsUppercaseLetterExceptFirst(String text) {
        return text.chars().skip(1).parallel().anyMatch(it -> Character.isUpperCase(it) || Character.isTitleCase(it));
    }

    private static void appendChar(StringBuilder content, char current, boolean afterAccent) {
        switch (current) {
            case '{':
            case '}':
            case '%':
                content.append("\\").append(current); //$NON-NLS-1$
                break;
            case '\\':
                content.append("{\\textbackslash}"); //$NON-NLS-1$
                break;
            case 'i':
            case 'j':
                if (afterAccent) {
                    content.append("\\").append(current); //$NON-NLS-1$
                } else {
                    content.append(current);
                }
                break;
            case '\u0237':
                content.append("\\j"); //$NON-NLS-1$
                break;
            case '\u00C6':
                content.append("\\AE"); //$NON-NLS-1$
                break;
            case '\u00E6':
                content.append("\\ae"); //$NON-NLS-1$
                break;
            case '\u0152':
                content.append("\\OE"); //$NON-NLS-1$
                break;
            case '\u0153':
                content.append("\\oe"); //$NON-NLS-1$
                break;
            default:
                content.append(current);
        }
    }

    private static void convertToTex(StringBuilder content, String text) {
        final var normalizer = Normalizer2.getNFKDInstance();
        final String normalizedString;
        if (!normalizer.isNormalized(text)) {
            normalizedString = normalizer.normalize(text);
        } else {
            normalizedString = text;
        }
        // Accents follow the associated characters in the normalized form.
        final var prev = new MutableInt(0);
        normalizedString.chars().forEach(it -> {
            final var accent = getAccent(it);
            final var current = (char) prev.intValue();
            if (accent != null) {
                if (current != 0) {
                    content.append("{\\").append(accent).append("{"); //$NON-NLS-1$ //$NON-NLS-2$
                    appendChar(content, current, true);
                    content.append("}}"); //$NON-NLS-1$
                }
                prev.setValue(0);
            } else {
                if (current != 0) {
                    appendChar(content, current, false);
                }
                prev.setValue(it);
            }
        });
        if (prev.intValue() != 0) {
            content.append((char) prev.intValue());
        }
    }

    private static String getAccent(int code) {
        // List of accents are: https://en.wikipedia.org/wiki/List_of_Unicode_characters
        switch (code) {
            case 768:
                return "`"; //$NON-NLS-1$
            case 769:
                return "'"; //$NON-NLS-1$
            case 770:
                return "^"; //$NON-NLS-1$
            case 771:
                return "~"; //$NON-NLS-1$
            case 772:
                return "="; //$NON-NLS-1$
            case 773:
                return "textoverline"; //$NON-NLS-1$
            case 774:
                return "u"; //$NON-NLS-1$
            case 775:
                return "."; //$NON-NLS-1$
            case 776:
                return "\""; //$NON-NLS-1$
            case 778:
                return "r"; //$NON-NLS-1$
            case 779:
                return "H"; //$NON-NLS-1$
            case 780:
                return "v"; //$NON-NLS-1$
            case 782:
                return "\""; //$NON-NLS-1$
            case 785:
                return "t"; //$NON-NLS-1$
            case 803:
                return "d"; //$NON-NLS-1$
            case 807:
                return "c"; //$NON-NLS-1$
            case 818:
                return "b"; //$NON-NLS-1$
            default:
                //
        }
        return null;
    }

    /**
     * Replies the accessor to the localized strings.
     *
     * @return the accessor.
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        return this.messages;
    }

    /**
     * Generate an UUID.
     *
     * @return the UUID.
     */
    protected Integer generateUUID() {
        return Integer.valueOf(Math.abs(this.random.nextInt()));
    }

    @Override
    public String toTeXString(String text, boolean protectAcronyms) {
        if (!Strings.isNullOrEmpty(text)) {
            final var buffer = new StringBuilder();
            for (final var word : text.split("[\\s\\h]+")) { //$NON-NLS-1$
                if (buffer.length() > 0) {
                    buffer.append(' ');
                }
                if (protectAcronyms && containsUppercaseLetterExceptFirst(word)) {
                    buffer.append('{');
                    convertToTex(buffer, word);
                    buffer.append('}');
                } else {
                    convertToTex(buffer, word);
                }
            }
            return buffer.toString();
        }
        return Strings.nullToEmpty(null);
    }

}
