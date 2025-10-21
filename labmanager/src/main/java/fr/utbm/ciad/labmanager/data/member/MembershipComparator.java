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

package fr.utbm.ciad.labmanager.data.member;

import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import fr.utbm.ciad.labmanager.utils.Comparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of memberships. First the organizations are considered in the
 * sort, Then, dates are sorted from the highest to the lowest.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class MembershipComparator implements Comparator<Membership> {

    private final PersonComparator personComparator;

    private final ResearchOrganizationComparator organizationComparator;

    private final OrganizationAddressComparator addressComparator;

    /**
     * Constructor.
     *
     * @param personComparator       the comparator of persons names.
     * @param organizationComparator the comparator of research organizations.
     * @param addressComparator      the comparator of organization addresses.
     */
    public MembershipComparator(@Autowired PersonComparator personComparator,
                                @Autowired ResearchOrganizationComparator organizationComparator,
                                @Autowired OrganizationAddressComparator addressComparator) {
        this.personComparator = personComparator;
        this.organizationComparator = organizationComparator;
        this.addressComparator = addressComparator;
    }

    @Override
    public int compare(Membership o1, Membership o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = this.organizationComparator.compare(o1.getDirectResearchOrganization(), o2.getDirectResearchOrganization());
        if (n != 0) {
            return n;
        }
        n = this.addressComparator.compare(o1.getOrganizationAddress(), o2.getOrganizationAddress());
        if (n != 0) {
            return n;
        }
        n = o1.getMemberStatus().compareTo(o2.getMemberStatus());
        if (n != 0) {
            return n;
        }
        n = Comparators.compareDateRange(
                o1.getMemberSinceWhen(), o1.getMemberToWhen(),
                o2.getMemberSinceWhen(), o2.getMemberToWhen());
        if (n != 0) {
            // Reverse the order to get the more recent memberships first
            return -n;
        }
        n = this.personComparator.compare(o1.getPerson(), o2.getPerson());
        if (n != 0) {
            return n;
        }
        n = Comparators.compare(o1.getResponsibility(), o2.getResponsibility());
        if (n != 0) {
            return n;
        }
        n = Comparators.compare(o1.getCnuSection(), o2.getCnuSection());
        if (n != 0) {
            return n;
        }
        n = Comparators.compare(o1.getConrsSection(), o2.getConrsSection());
        if (n != 0) {
            return n;
        }
        n = Comparators.compare(o1.getFrenchBap(), o2.getFrenchBap());
        if (n != 0) {
            return n;
        }
        // Main position order is reversed to put the "true" before the "false"
        return Boolean.compare(o2.isMainPosition(), o1.isMainPosition());
    }

}


