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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Comparator of lists of persons.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class PersonListComparator implements Comparator<List<Person>> {

    private final PersonComparator personComparator;

    /**
     * Constructor.
     *
     * @param personComparator the comparator of person names.
     */
    public PersonListComparator(@Autowired PersonComparator personComparator) {
        this.personComparator = personComparator;
    }

    @Override
    public int compare(List<Person> o1, List<Person> o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        final var it1 = o1.iterator();
        final var it2 = o2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            final var p1 = it1.next();
            final var p2 = it2.next();
            final var cmp = this.personComparator.compare(p1, p2);
            if (cmp != 0) {
                return cmp;
            }
        }
        if (it1.hasNext()) {
            return 1;
        }
        if (it2.hasNext()) {
            return -1;
        }
        return 0;
    }

}
