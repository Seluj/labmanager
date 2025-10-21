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

package fr.utbm.ciad.labmanager.utils.io.vcard;

import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.springframework.http.MediaType;

/**
 * A builder of a Virtual Card File (VCF).
 * <p>Virtual Card Format (VCF) or vCard is a digital file format for storing contact information.
 * The format is widely used for data interchange among popular information exchange applications.
 * A single VCF file can contain contact information for one or multiple contacts.
 * A VCF file usually contains information such as contact’s name, address, phone number, email,
 * birthday, photographs and audio in addition to a number of other fields.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public interface VcardBuilder {

    /**
     * Mime type for a Vcard.
     *
     * @see #VCARD_MIME_TYPE
     */
    String VCARD_MIME_TYPE_VALUE = "text/vcard"; //$NON-NLS-1$

    /**
     * Mime type for a Vcard.
     *
     * @see #VCARD_MIME_TYPE_VALUE
     */
    MediaType VCARD_MIME_TYPE = MediaType.parseMediaType(VCARD_MIME_TYPE_VALUE + ";charset=utf-8"); //$NON-NLS-1$

    /**
     * Build the Vcard for the given person.
     *
     * @param person       the person for who the Vcard should be built.
     * @param organization the organization to consider for generating the card. If it not provided, the first available organization
     *                     will be used from the active memberships.
     * @return the content of the Vcard.
     */
    String build(Person person, ResearchOrganization organization);

}
