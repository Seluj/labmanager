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

import fr.utbm.ciad.labmanager.utils.AbstractNormalizableStringComparator;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract implementation of utilit
 * <p>
 * stractNormalizableStringComparator;
 * import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
 * import org.springframework.beans.factory.annotation.Autowired;
 * <p>
 * import java.util.Set;
 * import java.utiies for comparing person names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class AbstractPersonNameComparator extends AbstractNormalizableStringComparator implements PersonNameComparator {

    private final PersonNameParser nameParser;

    /**
     * Constructor.
     *
     * @param nameParser the parser for persons' names.
     */
    public AbstractPersonNameComparator(@Autowired PersonNameParser nameParser) {
        this.nameParser = nameParser;
    }

    @Override
    public double getSimilarity(String fullName1, String fullName2) {
        final var first1 = this.nameParser.parseFirstName(fullName1);
        final var last1 = this.nameParser.parseLastName(fullName1);
        final var first2 = this.nameParser.parseFirstName(fullName2);
        final var last2 = this.nameParser.parseLastName(fullName2);
        return getSimilarity(first1, last1, first2, last2);
    }

    @Override
    public double getSimilarity(String firstName1, String lastName1, String firstName2, String lastName2) {
        boolean enableShortNames = this.nameParser.isShortName(firstName1)
                || this.nameParser.isShortName(lastName1)
                || this.nameParser.isShortName(firstName2)
                || this.nameParser.isShortName(lastName2);
        //
        final var first1 = this.nameParser.normalizeName(firstName1);
        final var firsts1 = this.nameParser.getNormalizedNamesFor(firstName1, enableShortNames, true);
        final var last1 = this.nameParser.normalizeName(lastName1);
        final var lasts1 = this.nameParser.getNormalizedNamesFor(lastName1, enableShortNames, false);
        //
        final var first2 = this.nameParser.normalizeName(firstName2);
        final var firsts2 = this.nameParser.getNormalizedNamesFor(firstName2, enableShortNames, true);
        final var last2 = this.nameParser.normalizeName(lastName2);
        final var lasts2 = this.nameParser.getNormalizedNamesFor(lastName2, enableShortNames, false);
        //
        return getSimilarity(
                first1, firsts1,
                last1, lasts1,
                first2, firsts2,
                last2, lasts2);
    }

    /**
     * Replies the similarity of the two names.
     * This function test if the two first names and last names are similar, or
     * if the first first name is similar to the second last name, and the
     * first last name is similar to the second first name.
     *
     * @param firstName1  the first name.
     * @param firstNames1 the set of first names for the first name.
     * @param lastName1   the last names for the first name.
     * @param lastNames1  the set of last names for the last name.
     * @param firstName2  the first names for the second name.
     * @param firstNames2 the set of first names for the second name.
     * @param lastName2   the last names for the second name.
     * @param lastNames2  the set of last names for the second name.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    protected double getSimilarity(
            String firstName1, Set<String> firstNames1,
            String lastName1, Set<String> lastNames1,
            String firstName2, Set<String> firstNames2,
            String lastName2, Set<String> lastNames2) {
        final var similarityComputer = getStringSimilarityComputer();

        // FL vs. FL
        var max = getSimilarity(similarityComputer, firstNames1, lastName1, firstNames2, lastName2);
        if (max >= 1.0) {
            return max;
        }

        // FL vs. LF
        var s = getSimilarity(similarityComputer, firstNames1, lastName1, lastNames2, firstName2);
        if (s >= 1.0) {
            return s;
        }
        if (s > max) {
            max = s;
        }

        // LF vs. LF
        s = getSimilarity(similarityComputer, lastNames1, firstName1, lastNames2, firstName2);
        if (s >= 1.0) {
            return s;
        }
        if (s > max) {
            max = s;
        }

        // LF vs. FL
        s = getSimilarity(similarityComputer, lastNames1, firstName1, firstNames2, lastName2);
        if (s >= 1.0) {
            return s;
        }
        if (s > max) {
            max = s;
        }

        return max;
    }

    protected double getSimilarity(NormalizedStringSimilarity similarityComputer, Set<String> first1, String last1, Set<String> first2, String last2) {
        final var s0 = getSimilarity(similarityComputer, first1, first2);
        final var s1 = getSimilarity(similarityComputer, last1, last2);
        return (s0 + s1) / 2.0;
    }

    /**
     * Replies if the two sets of names are similar.
     *
     * @param matcher the string similarity computer to be used.
     * @param name1   the first name to compare.
     * @param name2   the second name to compare.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    @SuppressWarnings("static-method")
    protected double getSimilarity(NormalizedStringSimilarity matcher, Set<String> name1, Set<String> name2) {
        if (name1.isEmpty() || name2.isEmpty()) {
            return 1.0;
        }
        final Set<String> ens1;
        final Set<String> ens2;
        if (name1.size() <= name2.size()) {
            ens1 = name1;
            ens2 = new TreeSet<>(name2);
        } else {
            ens1 = name2;
            ens2 = new TreeSet<>(name1);
        }
        var mmax = 0.0;
        for (final var n1 : ens1) {
            final var iter2 = ens2.iterator();
            String candidate = null;
            var max = 0.0;
            while (iter2.hasNext()) {
                final var n2 = iter2.next();
                final var similarity = matcher.similarity(n1, n2);
                if (similarity >= 1.0) {
                    return similarity;
                }
                if (similarity > max) {
                    max = similarity;
                    candidate = n2;
                }
            }
            if (candidate != null) {
                ens2.remove(candidate);
                if (max > mmax) {
                    mmax = max;
                }
            }
        }
        return mmax;
    }

}
