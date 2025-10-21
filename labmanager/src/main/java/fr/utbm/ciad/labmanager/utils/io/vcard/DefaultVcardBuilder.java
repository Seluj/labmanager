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

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationType;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.util.Locale;

/**
 * A builder of a Virtual Card File (VCF) based on the description on {@link "https://en.wikipedia.org/wiki/VCard"}.
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
 * @see "https://en.wikipedia.org/wiki/VCard"
 * @since 2.0.0
 */
@Component
@Primary
public class DefaultVcardBuilder implements VcardBuilder {

    private static final String VCARD_START = "BEGIN:VCARD\nVERSION:4.0\n"; //$NON-NLS-1$

    private static final String EMAIL = "EMAIL:"; //$NON-NLS-1$

    private static final String URL_PREFIX = "URL:"; //$NON-NLS-1$

    private static final String OFFICE_PHONE = "office,voice"; //$NON-NLS-1$

    private static final String MOBILE_PHONE = "mobile,voice"; //$NON-NLS-1$

    private static final String OFFICE_ROOM = "office,room"; //$NON-NLS-1$

    private static final String NAME = "N:"; //$NON-NLS-1$

    private static final String FULLNAME = "FN:"; //$NON-NLS-1$

    private static final String ORGANIZATION = "ORG:"; //$NON-NLS-1$

    private static final String TITLE = "TITLE:"; //$NON-NLS-1$

    private static final String TEL_TYPE = "TEL;TYPE#"; //$NON-NLS-1$

    private static final String TEL_NUM = ";VALUE#uri:tel:"; //$NON-NLS-1$

    private static final String PHOTO_TYPE = "PHOTO;MEDIATYPE#image/jpeg:"; //$NON-NLS-1$

    private static final String ROLE = "ROLE:"; //$NON-NLS-1$

    private static final String VCARD_END = "END:VCARD\n"; //$NON-NLS-1$

    private static final String ADR_TYPE = "ADR:TYPE="; //$NON-NLS-1$

    private static final String ADR_VALUE = ":"; //$NON-NLS-1$

    private final MessageSourceAccessor messages;

    /**
     * Constructor with injection.
     *
     * @param messages the accessor to the localized messages.
     */
    public DefaultVcardBuilder(@Autowired MessageSourceAccessor messages) {
        this.messages = messages;
    }

    private static void append(StringBuilder vcard, String property, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            vcard.append(property).append(value).append("\n"); //$NON-NLS-1$
        }
    }

    private static void append(StringBuilder vcard, String property, String... value) {
        if (value != null && value.length > 0) {
            vcard.append(property);
            var first = true;
            for (final var val : value) {
                if (first) {
                    first = false;
                } else {
                    vcard.append(";"); //$NON-NLS-1$
                }
                if (!Strings.isNullOrEmpty(val)) {
                    vcard.append(val);
                }
            }
            vcard.append("\n"); //$NON-NLS-1$
        }
    }

    private static void appendTo(StringBuilder buffer, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (buffer.length() > 0) {
                buffer.append(";"); //$NON-NLS-1$
            }
            buffer.append(value);
        }
    }

    private static void appendAdr(StringBuilder vcard, String type, String postOfficeBox, String additionalNumber, String numberStreetAddress,
                                  String locality, String region, String zipCode, String country) {
        final var value = new StringBuilder();
        appendTo(value, postOfficeBox);
        appendTo(value, additionalNumber);
        appendTo(value, numberStreetAddress);
        appendTo(value, locality);
        appendTo(value, region);
        appendTo(value, zipCode);
        appendTo(value, country);
        if (value.length() > 0) {
            vcard.append(ADR_TYPE).append(type).append(ADR_VALUE);
            vcard.append(value).append("\n"); //$NON-NLS-1$
        }
    }

    private static void appendTel(StringBuilder vcard, String type, PhoneNumber number) {
        if (number != null) {
            vcard.append(TEL_TYPE).append(type).append(TEL_NUM)
                    .append(number.toInternationalForm().replaceAll("\\s+", "-")) //$NON-NLS-1$ //$NON-NLS-2$
                    .append("\n"); //$NON-NLS-1$
        }
    }

    private static void appendPhoto(StringBuilder vcard, URL url) {
        if (url != null) {
            vcard.append(PHOTO_TYPE).append(url.toExternalForm()).append("\n"); //$NON-NLS-1$
        }
    }

    private static void append(StringBuilder vcard, String property, URL url) {
        if (url != null) {
            vcard.append(property).append(url.toExternalForm()).append("\n"); //$NON-NLS-1$
        }
    }

    private static void append(StringBuilder vcard, String property, URI uri) {
        if (uri != null) {
            final var currentUri = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            URI nuri;
            try {
                nuri = new URI(
                        currentUri.getScheme(), null,
                        currentUri.getHost(),
                        currentUri.getPort(),
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment());
                vcard.append(property).append(nuri.toURL()).append("\n"); //$NON-NLS-1$
            } catch (Exception ex) {
                //
            }
        }
    }

    private static void append(MessageSourceAccessor messages, StringBuilder vcard, Membership membership, Membership university) {
        if (membership != null) {
            append(vcard, TITLE, membership.getMemberStatus().getLabel(messages, null, false, Locale.US));
            final var org = new StringBuilder();
            if (membership.getSuperResearchOrganization() != null) {
                org.append(membership.getSuperResearchOrganization().getName()).append(";"); //$NON-NLS-1$
            }
            var ro = membership.getDirectResearchOrganization();
            org.append(ro.getName());
            append(vcard, ORGANIZATION, org.toString());
            if (membership.getResponsibility() != null) {
                append(vcard, ROLE, membership.getResponsibility().getLabel(messages, membership.getPerson().getGender(), Locale.US));
            }
        }
    }

    @Override
    public String build(Person person, ResearchOrganization organization) {
        final var vcard = new StringBuilder();
        vcard.append(VCARD_START);
        append(vcard, NAME, person.getLastName(), person.getFirstName(), null, person.getCivilTitle(this.messages, Locale.US));
        append(vcard, FULLNAME, person.getFullName());
        append(vcard, EMAIL, person.getEmail());
        append(vcard, URL_PREFIX, person.getWebPageURI());
        append(vcard, URL_PREFIX, person.getOrcidURL());
        append(vcard, URL_PREFIX, person.getGoogleScholarURL());
        append(vcard, URL_PREFIX, person.getHalURL());
        append(vcard, URL_PREFIX, person.getResearcherIdURL());
        append(vcard, URL_PREFIX, person.getResearchGateURL());
        append(vcard, URL_PREFIX, person.getAdScientificIndexURL());
        append(vcard, URL_PREFIX, person.getLinkedInURL());
        append(vcard, URL_PREFIX, person.getDblpURLObject());
        append(vcard, URL_PREFIX, person.getAcademiaURLObject());
        append(vcard, URL_PREFIX, person.getGithubURL());
        appendTel(vcard, OFFICE_PHONE, person.getOfficePhone());
        appendTel(vcard, MOBILE_PHONE, person.getMobilePhone());
        appendAdr(vcard, OFFICE_ROOM,
                null, // B.O.
                person.getOfficeRoom(),
                null, // Street
                null, // Locality
                null, // Region
                null, // Zip code
                organization == null ? null : organization.getCountryDisplayName());
        Membership universityMembership = null;
        Membership detailMembership = null;
        for (final var membership : person.getActiveMemberships().values()) {
            if (universityMembership == null
                    || membership.getDirectResearchOrganization().getType().compareTo(ResearchOrganizationType.UNIVERSITY) >= 0) {
                universityMembership = membership;
            }
            if (organization != null) {
                if (detailMembership == null || organization.getId() == membership.getDirectResearchOrganization().getId()) {
                    detailMembership = membership;
                }
            } else if (detailMembership == null
                    || detailMembership.getDirectResearchOrganization().getType().compareTo(membership.getDirectResearchOrganization().getType()) > 0) {
                detailMembership = membership;
            }
        }
        if (detailMembership != null) {
            append(this.messages, vcard, detailMembership, universityMembership);
        }
        appendPhoto(vcard, person.getPhotoURL());
        vcard.append(VCARD_END);
        return vcard.toString();
    }

}
