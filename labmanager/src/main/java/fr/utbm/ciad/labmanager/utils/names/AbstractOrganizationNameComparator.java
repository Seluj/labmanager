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

/**
 * Abstract implementation of utilities for comparing organization names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public abstract class AbstractOrganizationNameComparator extends AbstractNormalizableStringComparator implements OrganizationNameComparator {

    @Override
    public double getSimilarity(String acronym1, String name1, String acronym2, String name2) {
        final var normedAcronym1 = normalizeString(acronym1);
        final var normedName1 = normalizeString(name1);
        final var normedAcronym2 = acronym1 != acronym2 ? normalizeString(acronym2) : normedAcronym1;
        final var normedName2 = name1 != name2 ? normalizeString(name2) : normedName1;

        final var similarityComputer = getStringSimilarityComputer();
        final var s1 = getSimilarity(similarityComputer, normedAcronym1, normedAcronym2);
        final var s2 = getSimilarity(similarityComputer, normedName1, normedName2);
        return Math.max(s1, s2);
    }

}
