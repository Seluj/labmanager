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

package fr.utbm.ciad.labmanager.data.assostructure;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Comparator of associated structures.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Component
@Primary
public class AssociatedStructureComparator implements Comparator<AssociatedStructure> {

    private final ResearchOrganizationComparator organizationComparator;

    /**
     * Constructor.
     *
     * @param organizationComparator the comparator of organizations.
     */
    public AssociatedStructureComparator(ResearchOrganizationComparator organizationComparator) {
        this.organizationComparator = organizationComparator;
    }

    /**
     * Null-safe comparison the two strings.
     *
     * @param v0 the first value.
     * @param v1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareString(String v0, String v1) {
        if (v0 == v1) {
            return 0;
        }
        if (v0 == null) {
            return Integer.MIN_VALUE;
        }
        if (v1 == null) {
            return Integer.MAX_VALUE;
        }
        return v0.compareTo(v1);
    }

    /**
     * Null-safe comparison the two structure types.
     *
     * @param v0 the first value.
     * @param v1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareType(AssociatedStructureType v0, AssociatedStructureType v1) {
        if (v0 == v1) {
            return 0;
        }
        if (v0 == null) {
            return Integer.MIN_VALUE;
        }
        if (v1 == null) {
            return Integer.MAX_VALUE;
        }
        return v0.compareTo(v1);
    }

    /**
     * Null-safe comparison the two dates.
     *
     * @param s0 the first value.
     * @param s1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareDate(LocalDate d0, LocalDate d1) {
        if (d0 == d1) {
            return 0;
        }
        if (d0 == null) {
            return Integer.MIN_VALUE;
        }
        if (d1 == null) {
            return Integer.MAX_VALUE;
        }
        return d1.compareTo(d0);
    }

    @Override
    public int compare(AssociatedStructure o1, AssociatedStructure o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = compareString(o1.getAcronym(), o2.getAcronym());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareString(o1.getName(), o2.getName());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareDate(o1.getCreationDate(), o2.getCreationDate());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareType(o1.getType(), o2.getType());
        if (cmp != 0) {
            return cmp;
        }
        cmp = this.organizationComparator.compare(o1.getFundingOrganization(), o2.getFundingOrganization());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Float.compare(o1.getBudget(), o2.getBudget());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getCreationDuration(), o2.getCreationDuration());
        if (cmp != 0) {
            return cmp;
        }
        return compareString(o1.getDescription(), o2.getDescription());
    }

}
