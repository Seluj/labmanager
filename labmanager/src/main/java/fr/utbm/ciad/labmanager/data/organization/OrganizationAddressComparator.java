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

package fr.utbm.ciad.labmanager.data.organization;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Comparator of organization addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class OrganizationAddressComparator implements Comparator<OrganizationAddress> {

    @Override
    public int compare(OrganizationAddress o1, OrganizationAddress o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return Integer.MIN_VALUE;
        }
        if (o2 == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = StringUtils.compareIgnoreCase(o1.getName(), o2.getName());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getComplement(), o2.getComplement());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getStreet(), o2.getStreet());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getZipCode(), o2.getZipCode());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(o1.getCity(), o2.getCity());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compare(o1.getMapCoordinates(), o2.getMapCoordinates());
        if (cmp != 0) {
            return cmp;
        }
        return StringUtils.compare(o1.getGoogleMapLink(), o2.getGoogleMapLink());
    }

}
