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

package fr.utbm.ciad.labmanager.data.jury;

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;

/**
 * Comparator of jury memberships. First the years are considered in the
 * sort, Then, types and the persons are used from the highest to the lowest.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class JuryMembershipComparator implements Comparator<JuryMembership> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of persons names.
     */
    public JuryMembershipComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    private static int compareCountries(CountryCode c1, CountryCode c2) {
        if (c1 == c2) {
            return 0;
        }
        if (c1 == null) {
            return Integer.MIN_VALUE;
        }
        if (c2 == null) {
            return Integer.MAX_VALUE;
        }
        // France has lower priority
        final var f1 = c1.isFrance();
        final var f2 = c2.isFrance();
        if (f1 == f2) {
            return c1.compareTo(c2);
        }
        if (f1) {
            return 1;
        }
        return -1;
    }

    private static int compareDates(LocalDate d1, LocalDate d2) {
        if (d1 == d2) {
            return 0;
        }
        if (d1 == null) {
            return Integer.MIN_VALUE;
        }
        if (d2 == null) {
            return Integer.MAX_VALUE;
        }
        return -d1.compareTo(d2);
    }

    private static int compareDefenseTypes(JuryType t1, JuryType t2) {
        if (t1 == t2) {
            return 0;
        }
        if (t1 == null) {
            return Integer.MIN_VALUE;
        }
        if (t2 == null) {
            return Integer.MAX_VALUE;
        }
        return t1.compareTo(t2);
    }

    private static int compareMembershipTypes(JuryMembershipType t1, JuryMembershipType t2) {
        if (t1 == t2) {
            return 0;
        }
        if (t1 == null) {
            return Integer.MIN_VALUE;
        }
        if (t2 == null) {
            return Integer.MAX_VALUE;
        }
        return t1.compareTo(t2);
    }

    @Override
    public int compare(JuryMembership o1, JuryMembership o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = compareDates(o1.getDate(), o2.getDate());
        if (n != 0) {
            return n;
        }
        n = compareMembershipTypes(o1.getType(), o2.getType());
        if (n != 0) {
            return n;
        }
        n = compareCountries(o1.getCountry(), o2.getCountry());
        if (n != 0) {
            return n;
        }
        n = compareDefenseTypes(o1.getDefenseType(), o2.getDefenseType());
        if (n != 0) {
            return n;
        }
        return this.personComparator.compare(o1.getPerson(), o2.getPerson());
    }

}
