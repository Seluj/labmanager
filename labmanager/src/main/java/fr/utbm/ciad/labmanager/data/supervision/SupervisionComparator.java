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

package fr.utbm.ciad.labmanager.data.supervision;

import fr.utbm.ciad.labmanager.data.member.MembershipComparator;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator of supervisions.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
@Component
@Primary
public class SupervisionComparator implements Comparator<Supervision> {

    private final MembershipComparator membershipComparator;

    private final SupervisorComparator supervisorComparator;

    /**
     * Constructor.
     *
     * @param membershipComparator the comparator of memberships.
     * @param supervisorComparator the comparator of supervisors.
     */
    public SupervisionComparator(
            @Autowired MembershipComparator membershipComparator,
            @Autowired SupervisorComparator supervisorComparator) {
        this.membershipComparator = membershipComparator;
        this.supervisorComparator = supervisorComparator;
    }

    private static int compareStrings(String s1, String s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        return s1.compareTo(s2);
    }

    private static int compareFundingSchemes(FundingScheme s1, FundingScheme s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        return s1.compareTo(s2);
    }

    private static int compareDates(LocalDate s1, LocalDate s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        return s1.compareTo(s2);
    }

    @Override
    public int compare(Supervision s1, Supervision s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = this.membershipComparator.compare(s1.getSupervisedPerson(), s2.getSupervisedPerson());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareSupervisors(s1.getSupervisors(), s2.getSupervisors());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareStrings(s1.getTitle(), s2.getTitle());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareFundingSchemes(s1.getFunding(), s2.getFunding());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareDates(s1.getDefenseDate(), s2.getDefenseDate());
        if (cmp != 0) {
            return cmp;
        }
        cmp = compareStrings(s1.getPositionAfterSupervision(), s2.getPositionAfterSupervision());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(s1.getNumberOfAterPositions(), s2.getNumberOfAterPositions());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Boolean.compare(s1.isJointPosition(), s2.isJointPosition());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Boolean.compare(s1.isEntrepreneur(), s2.isEntrepreneur());
        if (cmp != 0) {
            return cmp;
        }
        return Boolean.compare(s1.isAbandonment(), s2.isAbandonment());
    }

    private int compareSupervisors(List<Supervisor> s1, List<Supervisor> s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        final var it1 = s1.iterator();
        final var it2 = s2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            final var su1 = it1.next();
            final var su2 = it2.next();
            final var cmp = this.supervisorComparator.compare(su1, su2);
            if (cmp != 0) {
                return cmp;
            }
        }
        if (it1.hasNext()) {
            return Integer.MAX_VALUE;
        }
        if (it2.hasNext()) {
            return Integer.MIN_VALUE;
        }
        return 0;
    }

}
