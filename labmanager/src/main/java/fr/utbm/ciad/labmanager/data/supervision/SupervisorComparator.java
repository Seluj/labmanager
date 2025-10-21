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

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of supervisors.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
@Component
@Primary
public class SupervisorComparator implements Comparator<Supervisor> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of persons names.
     */
    public SupervisorComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    private static int compareSupervisorTypes(SupervisorType t1, SupervisorType t2) {
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
    public int compare(Supervisor s1, Supervisor s2) {
        if (s1 == s2) {
            return 0;
        }
        if (s1 == null) {
            return Integer.MIN_VALUE;
        }
        if (s2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = compareSupervisorTypes(s1.getType(), s2.getType());
        if (cmp != 0) {
            return cmp;
        }
        // Higher percentage first
        cmp = Integer.compare(s2.getPercentage(), s1.getPercentage());
        if (cmp != 0) {
            return cmp;
        }
        return this.personComparator.compare(s1.getSupervisor(), s2.getSupervisor());
    }

}
