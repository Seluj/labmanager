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

package fr.utbm.ciad.labmanager.data.organization;

import fr.utbm.ciad.labmanager.utils.Comparators;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of research organizations on their names first.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
@Primary
public class ResearchOrganizationNameComparator implements Comparator<ResearchOrganization> {

    private static int compareCountry(CountryCode c0, CountryCode c1) {
        if (c0 == c1) {
            return 0;
        }
        if (c0 == null) {
            return Integer.MIN_VALUE;
        }
        if (c1 == null) {
            return Integer.MAX_VALUE;
        }
        return c0.compareTo(c1);
    }

    @Override
    public int compare(ResearchOrganization o1, ResearchOrganization o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = StringUtils.compareIgnoreCase(o1.getAcronym(), o2.getAcronym());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getName(), o2.getName());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Comparators.compare(o1.getType(), o2.getType());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getDescription(), o2.getDescription());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Boolean.compare(o1.isMajorOrganization(), o2.isMajorOrganization());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getRnsr(), o2.getRnsr());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getNationalIdentifier(), o2.getNationalIdentifier());
        if (cmp != 0) {
            return cmp;
        }
        return compareCountry(o1.getCountry(), o2.getCountry());
    }

}
