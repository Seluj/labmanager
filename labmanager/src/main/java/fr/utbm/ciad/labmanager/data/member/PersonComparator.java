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

import fr.utbm.ciad.labmanager.utils.Comparators;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of persons.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class PersonComparator implements Comparator<Person> {

    private static int comparePhones(PhoneNumber p1, PhoneNumber p2) {
        if (p1 == null) {
            return Integer.MIN_VALUE;
        }
        return p1.compareTo(p2);
    }

    @Override
    public int compare(Person o1, Person o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = StringUtils.compare(o1.getLastName(), o2.getLastName());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getFirstName(), o2.getFirstName());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Comparators.compare(o1.getGender(), o2.getGender());
        if (cmp != 0) {
            return cmp;
        }
        cmp = comparePhones(o1.getOfficePhone(), o2.getOfficePhone());
        if (cmp != 0) {
            return cmp;
        }
        cmp = comparePhones(o1.getMobilePhone(), o2.getMobilePhone());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getOfficeRoom(), o2.getOfficeRoom());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getPrimaryEmail(), o2.getPrimaryEmail());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getSecondaryEmail(), o2.getSecondaryEmail());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getORCID(), o2.getORCID());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getAcademiaURL(), o2.getAcademiaURL());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getCordisURL(), o2.getCordisURL());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getDblpURL(), o2.getDblpURL());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getFacebookId(), o2.getFacebookId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getGithubId(), o2.getGithubId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getLinkedInId(), o2.getLinkedInId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getResearcherId(), o2.getResearcherId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getScopusId(), o2.getScopusId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getGoogleScholarId(), o2.getGoogleScholarId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getIdhal(), o2.getIdhal());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getResearchGateId(), o2.getResearchGateId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getAdScientificIndexId(), o2.getAdScientificIndexId());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getGoogleScholarHindex(), o2.getGoogleScholarHindex());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getWosHindex(), o2.getWosHindex());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getScopusHindex(), o2.getScopusHindex());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getGoogleScholarCitations(), o2.getGoogleScholarCitations());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getWosCitations(), o2.getWosCitations());
        if (cmp != 0) {
            return cmp;
        }
        cmp = Integer.compare(o1.getScopusCitations(), o2.getScopusCitations());
        if (cmp != 0) {
            return cmp;
        }
        return StringUtils.compare(o1.getGravatarId(), o2.getGravatarId());
    }

}
