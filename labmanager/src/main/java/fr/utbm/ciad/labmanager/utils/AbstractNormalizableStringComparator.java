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

package fr.utbm.ciad.labmanager.utils;

import com.google.common.base.Strings;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

import java.text.Normalizer;

/**
 * Abstract implementation of utilities for comparing strings that may be normalized.
 * A normalized string is case insensitive and accent-insensitive.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractNormalizableStringComparator {

    private double similaritylevel;

    private NormalizedStringSimilarity similarityComputer;

    /**
     * Replies the similarity level to consider for assuming that two names are similar.
     *
     * @return the minimum level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    public double getSimilarityLevel() {
        return this.similaritylevel;
    }

    /**
     * Change the similarity level to consider for assuming that two names are similar.
     *
     * @param similarityLevel the minimum level of similarity. {@code 0} means that the names are not
     *                        similar, and {@code 1} means that they are totally equal.
     */
    public void setSimilarityLevel(double similarityLevel) {
        if (similarityLevel < 0.0) {
            this.similaritylevel = 0.0;
        } else {
            this.similaritylevel = Math.min(similarityLevel, 1.0);
        }
    }

    /**
     * Replies the internal similarity computer.
     *
     * @return the internal similarity computer.
     */
    public NormalizedStringSimilarity getStringSimilarityComputer() {
        if (this.similarityComputer == null) {
            this.similarityComputer = createStringSimilarityComputer();
        }
        return this.similarityComputer;
    }

    /**
     * Create an instance of a string similarity computer.
     * This is a factory method.
     *
     * @return the string similarity computer.
     */
    protected abstract NormalizedStringSimilarity createStringSimilarityComputer();

    /**
     * Replies the similarity of the two strings. This function does not convert to normalized string its inputs
     *
     * @param matcher the string similarity computer to be used.
     * @param str1    the first string to compare.
     * @param str2    the second string to compare.
     * @return the level of similarity. {@code 0} means that the strings are not
     * similar, and {@code 1} means that they are totally equal.
     */
    @SuppressWarnings("static-method")
    protected double getSimilarity(NormalizedStringSimilarity matcher, String str1, String str2) {
        if (Strings.isNullOrEmpty(str1) || Strings.isNullOrEmpty(str2)) {
            return 1.0;
        }
        return matcher.similarity(str1, str2);
    }

    /**
     * Normalize the string to obtain a string without upper-cases and accents
     *
     * @param source the source string to normalize
     * @return the normalize string.
     */
    @SuppressWarnings("static-method")
    protected String normalizeString(String source) {
        if (source == null) {
            return ""; //$NON-NLS-1$
        }
        String nsource = Normalizer.normalize(source.trim().toLowerCase(), Normalizer.Form.NFD);
        nsource = nsource.replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""); //$NON-NLS-1$ //$NON-NLS-2$
        nsource = nsource.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
        return nsource;
    }

}
