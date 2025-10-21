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

/**
 * Quartile is one indicator for ranking scientific journals.
 * A quartile is the ranking of a journal or paper definite by any database based
 * on the impact factor (IF), citation, and indexing of that particular journal.
 * It can divide into four different quadrants starting with Q1, Q2, Q3, and Q4.
 *
 * <p>Two major sources of ranking may be used:<ul>
 * <li>Scimajo Journal Ranking</li>
 * <li>Journal Citatio Reports of Web-of-Science</li>
 * </ul>
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @author $Author: lpascuzzi$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/Journal_ranking"
 * @see "https://www.scimagojr.com/"
 * @see "https://jcr.clarivate.com/"
 */
public enum QuartileRanking {
    /**
     * Not ranked.
     */
    NR,
    /**
     * Fourth quartile.
     */
    Q4,
    /**
     * Third quartile.
     */
    Q3,
    /**
     * Second quartile.
     */
    Q2,
    /**
     * First quartile.
     */
    Q1;

    /**
     * Replies the quartile that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the quartile, to search for.
     * @return the quartile.
     * @throws IllegalArgumentException if the given name does not corresponds to a quartile.
     */
    public static QuartileRanking valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid quartile ranking: " + name); //$NON-NLS-1$
    }

    /**
     * Normalize the given quartile. Normalization ensures that there is no {@code null}
     * value for a quartile. If the given quartile is {@code null}, {@link #NR} is replied.
     *
     * @param quartile the quartile to normalize.
     * @return the normalized quartile, never {@code null}.
     */
    public static QuartileRanking normalize(QuartileRanking quartile) {
        if (quartile == null) {
            return NR;
        }
        return quartile;
    }

}
