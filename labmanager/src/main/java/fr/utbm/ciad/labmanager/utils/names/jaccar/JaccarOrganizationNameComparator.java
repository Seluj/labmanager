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

package fr.utbm.ciad.labmanager.utils.names.jaccar;

import fr.utbm.ciad.labmanager.utils.names.AbstractOrganizationNameComparator;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing organization names using the Jaccar algorithm.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
public class JaccarOrganizationNameComparator extends AbstractOrganizationNameComparator {

    private static final double SIMILARITY_LEVEL = 0.65;

    /**
     * Constructor.
     */
    public JaccarOrganizationNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }

}
