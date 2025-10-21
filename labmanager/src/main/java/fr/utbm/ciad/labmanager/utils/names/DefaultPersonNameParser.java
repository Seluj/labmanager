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

package fr.utbm.ciad.labmanager.utils.names;

import com.google.common.base.Strings;
import com.ibm.icu.text.Normalizer2;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.text.WordUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Utilities for parsing person names with the format {@code FIRST LAST} or {@code LAST, FIRST}.
 * In the first format, the two components are separated by white spaces.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class DefaultPersonNameParser implements PersonNameParser {

    /**
     * List of characters that are considered as delimiters for words when formatting names.
     *
     * @see #formatNameForDisplay(String)
     */
    public static final char[] NAME_DELIMITERS = new char[]{
            ' ', '\u00A0', '\t', '\n', '\r', '\f', '.', '-', '_'
    };

    private static final char FORMAT_2_SEPARATOR = ',';

    private static final String NAME_FORMAT_0 = "([^,]+?)(?:\\s*,\\s*([^,]+?))?\\s*,\\s*([^,]+?)"; //$NON-NLS-1$

    private static final Pattern NAME_PATTERN_0 = Pattern.compile(NAME_FORMAT_0);

    private static final String NAME_SEPARATOR_0 = "\\s+AND\\s+"; //$NON-NLS-1$

    private static final Pattern NAME_SEPARATOR_PATTERN_0 = Pattern.compile(NAME_SEPARATOR_0, Pattern.CASE_INSENSITIVE);

    private static final String AND_SEPARATED_FORMAT_0 = NAME_FORMAT_0 + "(?:" + NAME_SEPARATOR_0 + NAME_FORMAT_0 + ")*"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final Pattern AND_WITH_COMMA_SEPARATED_PATTERN = Pattern.compile(AND_SEPARATED_FORMAT_0, Pattern.CASE_INSENSITIVE);

    private static final String NAME_FORMAT_1 = "([^,]+?)\\s+([^,]+?)"; //$NON-NLS-1$

    private static final Pattern NAME_PATTERN_1 = Pattern.compile(NAME_FORMAT_1);

    private static final String NAME_SEPARATOR_1 = "\\s*,\\s*"; //$NON-NLS-1$

    private static final Pattern NAME_SEPARATOR_PATTERN_1 = Pattern.compile(NAME_SEPARATOR_1);

    private static final String COMMA_SEPARATED_FORMAT = NAME_FORMAT_1 + "(?:" + NAME_SEPARATOR_1 + NAME_FORMAT_1 + ")*"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final Pattern COMMA_SEPARATED_PATTERN = Pattern.compile(COMMA_SEPARATED_FORMAT);

    private static final String AND_SEPARATED_FORMAT_1 = NAME_FORMAT_1 + "(?:" + NAME_SEPARATOR_0 + NAME_FORMAT_1 + ")*"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final Pattern AND_WITHOUT_COMMA_SEPARATED_PATTERN = Pattern.compile(AND_SEPARATED_FORMAT_1, Pattern.CASE_INSENSITIVE);

    private static int extractNames(String text, NameCallback callback, Pattern nameSeparator) {
        var nb = 0;
        final var names = nameSeparator.split(text);
        for (final var fn : names) {
            // Check the format: Last, von, First
            final var n0 = NAME_PATTERN_0.matcher(fn);
            if (n0.matches()) {
                final var lastname = n0.group(1);
                final var von = n0.group(2);
                final var firstname = n0.group(3);
                if (!Strings.isNullOrEmpty(firstname) && !Strings.isNullOrEmpty(lastname)) {
                    callback.name(firstname, Strings.emptyToNull(von), lastname, nb);
                    ++nb;
                } else {
                    throw new IllegalArgumentException("Unrecognized name format for: " + fn); //$NON-NLS-1$
                }
            } else {
                // Check the format: First Last (no von part)
                final var n1 = NAME_PATTERN_1.matcher(fn);
                if (n1.matches()) {
                    final var lastname = n1.group(2);
                    final var firstname = n1.group(1);
                    if (!Strings.isNullOrEmpty(firstname) && !Strings.isNullOrEmpty(lastname)) {
                        callback.name(firstname, null, lastname, nb);
                        ++nb;
                    } else {
                        throw new IllegalArgumentException("Unrecognized name format for: " + fn); //$NON-NLS-1$
                    }
                } else {
                    throw new IllegalArgumentException("Unrecognized name format for: " + fn); //$NON-NLS-1$
                }
            }
        }
        return nb;
    }

    private static void forEachComponent(String name, Function<String, Boolean> consumer) {
        final var words = name.split("[\\s\u00A0]+"); //$NON-NLS-1$
        for (final var word : words) {
            final var subwords = word.split("[\\-]+"); //$NON-NLS-1$
            int max = subwords.length;
            if (subwords.length > 1 && "".equals(subwords[subwords.length - 1])) { //$NON-NLS-1$
                --max;
            }
            final var components0 = new ArrayList<String>();
            for (var i = 0; i < max; ++i) {
                if (subwords[i].length() > 0) {
                    final var particles = subwords[i].split("[.]+"); //$NON-NLS-1$
                    for (final var particle : particles) {
                        final String cmp;
                        if (particle.length() == 1) {
                            cmp = WordUtils.capitalizeFully(particle + "."); //$NON-NLS-1$
                        } else {
                            cmp = WordUtils.capitalizeFully(particle);
                        }
                        components0.add(cmp);
                    }
                }
            }
            if (max > 1) {
                final var buf = new StringBuilder();
                var first = true;
                for (final var cmp : components0) {
                    if (first) {
                        first = false;
                    } else {
                        buf.append("-"); //$NON-NLS-1$
                    }
                    buf.append(cmp);
                }
                final var ret = consumer.apply(buf.toString());
                if (ret != null && !ret.booleanValue()) {
                    return;
                }
            } else {
                for (final var cmp : components0) {
                    final var ret = consumer.apply(cmp);
                    if (ret != null && !ret.booleanValue()) {
                        return;
                    }
                }
            }
        }
    }

    private static Set<String> fillWithWords(Set<String> cases, String word, boolean enableShortNames, boolean progressiveBuilding) {
        assert !Strings.isNullOrEmpty(word);
        final var result = new TreeSet<String>();
        for (final var candidate : cases) {
            if (progressiveBuilding) {
                result.add(candidate);
            }
            result.add(candidate + " " + word); //$NON-NLS-1$
            if (enableShortNames) {
                result.add(candidate + " " + word.charAt(0)); //$NON-NLS-1$
            }
        }
        return result;
    }

    @Override
    public int parseNames(String text, NameCallback callback) {
        assert callback != null;
        var nb = 0;
        if (!Strings.isNullOrEmpty(text)) {
            var matcher = AND_WITH_COMMA_SEPARATED_PATTERN.matcher(text);
            if (matcher.matches()) {
                nb = extractNames(text, callback, NAME_SEPARATOR_PATTERN_0);
            } else {
                matcher = AND_WITHOUT_COMMA_SEPARATED_PATTERN.matcher(text);
                if (matcher.matches()) {
                    nb = extractNames(text, callback, NAME_SEPARATOR_PATTERN_0);
                } else {
                    matcher = COMMA_SEPARATED_PATTERN.matcher(text);
                    if (matcher.matches()) {
                        nb = extractNames(text, callback, NAME_SEPARATOR_PATTERN_1);
                    } else {
                        throw new IllegalArgumentException("Invalid format of the list of names: " + text); //$NON-NLS-1$
                    }
                }
            }
        }
        return nb;
    }

    @Override
    public String parseFirstName(String fullName) {
        var fn = Strings.nullToEmpty(fullName).trim();
        if (!Strings.isNullOrEmpty(fn)) {
            var index = fn.indexOf(FORMAT_2_SEPARATOR);
            final var name = new StringBuilder();
            if (index >= 0) {
                fn = fn.substring(index + 1).trim();
                final var cont = new MutableBoolean(false);
                forEachComponent(fn, it -> {
                    if (cont.booleanValue()) {
                        name.append(" "); //$NON-NLS-1$
                    }
                    name.append(it);
                    cont.setTrue();
                    return Boolean.TRUE;
                });
            } else {
                final var cont = new MutableBoolean(false);
                final var cmp0 = new MutableObject<String>(null);
                forEachComponent(fn, it -> {
                    if (!it.endsWith(".") && cont.booleanValue()) { //$NON-NLS-1$
                        cmp0.setValue(it);
                    } else {
                        if (cont.booleanValue()) {
                            name.append(" "); //$NON-NLS-1$
                        }
                        name.append(it);
                    }
                    cont.setTrue();
                    return Boolean.valueOf(cmp0.getValue() == null);
                });
            }
            final var firstName = name.toString();
            if (!Strings.isNullOrEmpty(firstName)) {
                return firstName;
            }
            return Strings.emptyToNull(fn);
        }
        return null;
    }

    @Override
    public String parseLastName(String fullName) {
        var fn = Strings.nullToEmpty(fullName).trim();
        if (!Strings.isNullOrEmpty(fn)) {
            var index = fn.indexOf(FORMAT_2_SEPARATOR);
            final var name = new StringBuilder();
            if (index >= 0) {
                fn = fn.substring(0, index).trim();
                final var cont = new MutableBoolean(false);
                forEachComponent(fn, it -> {
                    if (cont.booleanValue()) {
                        name.append(" "); //$NON-NLS-1$
                    }
                    name.append(it);
                    cont.setTrue();
                    return Boolean.TRUE;
                });
            } else {
                final var cont = new MutableBoolean(false);
                final var inFirstName = new MutableBoolean(true);
                forEachComponent(fn, it -> {
                    if (!it.endsWith(".") || !inFirstName.booleanValue()) { //$NON-NLS-1$
                        inFirstName.setFalse();
                        if (cont.booleanValue()) {
                            if (name.length() > 0) {
                                name.append(" "); //$NON-NLS-1$
                            }
                            name.append(it);
                        }
                    }
                    cont.setTrue();
                    return Boolean.TRUE;
                });
            }
            final var lastName = name.toString();
            if (!Strings.isNullOrEmpty(lastName)) {
                return lastName;
            }
        }
        return null;
    }

    @Override
    public String formatNameForDisplay(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            final var tname = name.trim();
            if (!Strings.isNullOrEmpty(tname)) {
                return WordUtils.capitalizeFully(tname, NAME_DELIMITERS);
            }
            return tname;
        }
        return name;
    }

    @Override
    public String normalizeName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            final var normalizedName0 = name.trim();
            if (!Strings.isNullOrEmpty(normalizedName0)) {
                // Remove dashes, e.g., "A-B" -> "A B"
                final var normalizedName1 = normalizedName0.replaceAll("\\s*\\-\\s*", " "); //$NON-NLS-1$ //$NON-NLS-2$
                // Remove the dots, e.g., "N." -> "N"
                final var normalizedName2 = normalizedName1.replaceAll("(\\S)\\.", "$1 "); //$NON-NLS-1$ //$NON-NLS-2$
                // Normalize white-spaces, e.g., "    " -> " "
                final var normalizedName3 = normalizedName2.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
                // Remove accents and special characters
                final var normalizer = Normalizer2.getNFKDInstance();
                String normalizedName4;
                if (!normalizer.isNormalized(normalizedName3)) {
                    normalizedName4 = normalizer.normalize(normalizedName3);
                } else {
                    normalizedName4 = normalizedName3;
                }
                normalizedName4 = normalizedName4.replaceAll("[^\\p{ASCII}]", ""); //$NON-NLS-1$ //$NON-NLS-2$
                // Upper-case the name
                return normalizedName4.toUpperCase();
            }
        }
        return null;
    }

    @Override
    public boolean isShortName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            final var normalizedName = normalizeName(name);
            if (!Strings.isNullOrEmpty(normalizedName)) {
                final var words = normalizedName.split("\\s+"); //$NON-NLS-1$
                if (words.length > 0) {
                    for (final var word : words) {
                        if (word.length() > 1) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<String> getNormalizedNamesFor(String name, boolean enableShortNames, boolean progressiveBuilding) {
        Set<String> cases = new TreeSet<>();
        final var normalizedName = normalizeName(name);
        if (!Strings.isNullOrEmpty(normalizedName)) {
            // Generate the cases
            final var words = normalizedName.split("\\s+"); //$NON-NLS-1$
            if (words.length > 0) {
                cases.add(words[0]);
                if (enableShortNames) {
                    cases.add(Character.toString(words[0].charAt(0)));
                }
                for (int i = 1; i < words.length; ++i) {
                    cases = fillWithWords(cases, words[i], enableShortNames, progressiveBuilding);
                }
            }
        }
        return cases;
    }

}
