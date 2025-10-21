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

package fr.utbm.ciad.labmanager.data.project;

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of project members.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Component
@Primary
public class ProjectMemberComparator implements Comparator<ProjectMember> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of persons names.
     */
    public ProjectMemberComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    private static int compareRoles(Role r1, Role r2) {
        if (r1 == r2) {
            return 0;
        }
        if (r1 == null) {
            return Integer.MIN_VALUE;
        }
        if (r2 == null) {
            return Integer.MAX_VALUE;
        }
        return r1.compareTo(r2);
    }

    @Override
    public int compare(ProjectMember o1, ProjectMember o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = compareRoles(o1.getRole(), o2.getRole());
        if (n != 0) {
            return n;
        }
        return this.personComparator.compare(o1.getPerson(), o2.getPerson());
    }

}


