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

package fr.utbm.ciad.labmanager.data.journal;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of journals. First the names are considered in the
 * sort; then, the publisher.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class JournalComparator implements Comparator<Journal> {

    /**
     * Constructor.
     */
    public JournalComparator() {
        //
    }

    private static int compareStr(String s0, String s1) {
        if (s0 == s1) {
            return 0;
        }
        if (s0 == null) {
            return Integer.MIN_VALUE;
        }
        if (s1 == null) {
            return Integer.MAX_VALUE;
        }
        return s0.compareTo(s1);
    }

    @Override
    public int compare(Journal o1, Journal o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var n = compareStr(o1.getJournalName(), o2.getJournalName());
        if (n != 0) {
            return n;
        }
        n = compareStr(o1.getPublisher(), o2.getPublisher());
        if (n != 0) {
            return n;
        }
        n = compareStr(o1.getISBN(), o2.getISBN());
        if (n != 0) {
            return n;
        }
        return compareStr(o1.getISSN(), o2.getISSN());
    }

}


