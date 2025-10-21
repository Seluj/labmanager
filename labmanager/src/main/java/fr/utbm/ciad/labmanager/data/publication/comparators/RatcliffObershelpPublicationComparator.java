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

package fr.utbm.ciad.labmanager.data.publication.comparators;

import info.debatty.java.stringsimilarity.RatcliffObershelp;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/**
 * Comparator of publications. For comparison, the order of the publication is based on the
 * type of publication, the year, the authors, the identifier.
 * For similarity, the Ratcliff/Obershelp algorithm is used.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
public class RatcliffObershelpPublicationComparator extends AbstractPublicationComparator {

    private static final double SIMILARITY_LEVEL = 0.9;

    /**
     * Constructor.
     */
    public RatcliffObershelpPublicationComparator() {
        super(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new RatcliffObershelp();
    }

}
