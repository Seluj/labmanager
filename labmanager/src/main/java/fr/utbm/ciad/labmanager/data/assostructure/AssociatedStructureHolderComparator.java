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

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of associated structure holders.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
@Primary
public class AssociatedStructureHolderComparator implements Comparator<AssociatedStructureHolder> {

    private final PersonComparator personComparator;

    private final ResearchOrganizationComparator organizationComparator;

    /**
     * Constructor.
     *
     * @param personComparator       the comparator of persons that is used by this holder comparator.
     * @param organizationComparator the comparator of organizations that is used by this holder comparator.
     */
    public AssociatedStructureHolderComparator(PersonComparator personComparator, ResearchOrganizationComparator organizationComparator) {
        this.personComparator = personComparator;
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
     * Null-safe comparison of the two roles.
     *
     * @param r0 the first value.
     * @param r1 the second value.
     * @return the numeric result of the comparison.
     */
    private static int compareRole(HolderRole r0, HolderRole r1) {
        if (r0 == r1) {
            return 0;
        }
        if (r0 == null) {
            return Integer.MIN_VALUE;
        }
        if (r1 == null) {
            return Integer.MAX_VALUE;
        }
        return r0.compareTo(r1);
    }

    @Override
    public int compare(AssociatedStructureHolder o1, AssociatedStructureHolder o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = this.personComparator.compare(o1.getPerson(), o2.getPerson());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareRole(o1.getRole(), o2.getRole());
        if (cmp != 0) {
            return cmp;
        }
        cmp = this.organizationComparator.compare(o1.getOrganization(), o2.getOrganization());
        if (cmp != 0) {
            return cmp;
        }
        cmp = this.organizationComparator.compare(o1.getSuperOrganization(), o2.getSuperOrganization());
        if (cmp != 0) {
            return cmp;
        }
        return compareString(o1.getRoleDescription(), o2.getRoleDescription());
    }

}
