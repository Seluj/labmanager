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

package fr.utbm.ciad.labmanager.data;

import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import jakarta.persistence.AttributeConverter;

/**
 * JPA converter for attributes of type {@link PhoneNumber}.
 * This converter uses a "fast" method for serializing the phone number (not JSON).
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PhoneNumberJPAConverter implements AttributeConverter<PhoneNumber, String> {

    @Override
    public String convertToDatabaseColumn(PhoneNumber attribute) {
        if (attribute != null) {
            return attribute.serialize();
        }
        return null;
    }

    @Override
    public PhoneNumber convertToEntityAttribute(String dbData) {
        try {
            return PhoneNumber.unserialize(dbData);
        } catch (IllegalArgumentException ex) {
            //
        }
        return null;
    }

}
