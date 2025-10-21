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

package fr.utbm.ciad.labmanager.data.user;

import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of application users. The order of the users depends on the implementation
 * of this interface.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
@Primary
public class UserComparator implements Comparator<User> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of persons.
     */
    public UserComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    /**
     * Null-safe comparison the two roles.
     *
     * @param v0 the first value.
     * @param v1 the second value.
     * @return the result of the comparison.
     */
    protected static int compareRole(UserRole v0, UserRole v1) {
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

    @Override
    public int compare(User a1, User a2) {
        if (a1 == a2) {
            return 0;
        }
        if (a1 == null) {
            return Integer.MIN_VALUE;
        }
        if (a2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = compareRole(a1.getRole(), a2.getRole());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(a1.getLogin(), a2.getLogin());
        if (cmp != 0) {
            return cmp;
        }
        return this.personComparator.compare(a1.getPerson(), a2.getPerson());
    }

}
