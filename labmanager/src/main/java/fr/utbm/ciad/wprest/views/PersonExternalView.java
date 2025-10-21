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

package fr.utbm.ciad.wprest.views;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static fr.utbm.ciad.wprest.views.ExternalViewConstants.*;

/**
 * Provide views through REST API of components for an external front-end.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
//@RestController
//@RequestMapping("/api")
//@CrossOrigin
public class PersonExternalView extends AbstractExternalView {

    private static final long serialVersionUID = -1477001255798590777L;

    private final PersonService personService;

    private final PersonNameParser nameParser;

    private final ResearchOrganizationService organizationService;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param messages            the accessor to the localized messages.
     * @param constants           the constants of the app.
     * @param personService       the person service.
     * @param membershipService   the service for managing the memberships.
     * @param organizationService the service for organizations.
     * @param nameParser          the parser for person names.
     * @param usernameKey         the key string for encrypting the usernames.
     */
    public PersonExternalView(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired PersonService personService,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired PersonNameParser nameParser) {
        super(messages, constants);
        this.personService = personService;
        this.organizationService = organizationService;
        this.nameParser = nameParser;
    }

    /**
     * Show the person's virtual card. This card is a description of the person that could
     * be used for building a Internet page for the person.
     *
     * @param dbId          the database identifier of the person. You should provide one of {@code dbId}, {@code webId} or {@code name}.
     * @param webId         the identifier of the webpage of the person. You should provide one of {@code dbId}, {@code webId} or {@code name}.
     * @param organization  the identifier or the name of the organization to restrict the card to.
     * @param introText     a small text that is output as a status/position.
     * @param photo         indicates if the photo should be shown on the card.
     * @param qrcode        indicates if the QR-code should be shown on the card.
     * @param status        indicates if the member status should be shown on the card.
     * @param admin         indicates if the administrative position should be shown on the card.
     * @param email         indicates if the email should be shown on the card.
     * @param officePhone   indicates if the office phone should be shown on the card.
     * @param mobilePhone   indicates if the mobile phone should be shown on the card.
     * @param officeRoom    indicates if the number of the office room should be shown on the card.
     * @param postalAddress indicates if the postal address should be shown on the card.
     * @param qindexes      indicates if the Q-indexes should be shown on the card.
     * @param links         indicates if the links to external sites should be shown on the card.
     * @param embedded      indicates if the view will be embedded into a larger page, e.g., WordPress page.
     * @param username      the name of the logged-in user.
     * @return the model-view object.
     */
//	@GetMapping("/showPersonCard")
    public ModelAndView showPersonCard(
            @RequestParam(required = false) Integer dbId,
            @RequestParam(required = false) String webId,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String introText,
            @RequestParam(required = false, defaultValue = "true") boolean photo,
            @RequestParam(required = false, defaultValue = "true") boolean qrcode,
            @RequestParam(required = false, defaultValue = "true") boolean status,
            @RequestParam(required = false, defaultValue = "true") boolean admin,
            @RequestParam(required = false, defaultValue = "true") boolean email,
            @RequestParam(required = false, defaultValue = "true") boolean officePhone,
            @RequestParam(required = false, defaultValue = "true") boolean mobilePhone,
            @RequestParam(required = false, defaultValue = "true") boolean officeRoom,
            @RequestParam(required = false, defaultValue = "true") boolean postalAddress,
            @RequestParam(required = false, defaultValue = "true") boolean qindexes,
            @RequestParam(required = false, defaultValue = "true") boolean links,
            @RequestParam(required = false, defaultValue = "false") boolean embedded) {
        final ModelAndView modelAndView = new ModelAndView("showPersonCard"); //$NON-NLS-1$
        initModelViewWithInternalProperties(modelAndView, embedded, false);
        //
        final Person personObj = getPersonWith(dbId, inString(webId), null, this.personService, this.nameParser);
        if (personObj == null) {
            throw new IllegalArgumentException("Person not found"); //$NON-NLS-1$
        }
        //
        final ResearchOrganization organizationObj = getOrganizationWith(inString(organization), this.organizationService);
        //
        Stream<Membership> stream = personObj.getMemberships().stream();
        if (organizationObj == null) {
            stream = stream.filter(it -> !it.isFuture());
        } else {
            stream = stream.filter(it -> !it.isFuture() && it.getDirectResearchOrganization().getId() == organizationObj.getId());
        }
        // Sort the memberships to push the active memberships before the former memberships.
        final Collection<Membership> activeMemberships = new ConcurrentLinkedQueue<>();
        final Collection<Membership> activeResponsibilities = new ConcurrentLinkedQueue<>();
        final AtomicReference<OrganizationAddress> activePostalAddressObj = new AtomicReference<>();
        final Collection<Membership> formerMemberships = new ConcurrentLinkedQueue<>();
        final Collection<Membership> formerResponsibilities = new ConcurrentLinkedQueue<>();
        final AtomicReference<OrganizationAddress> formerPostalAddressObj = new AtomicReference<>();
        final AtomicBoolean foundActive = new AtomicBoolean();
        stream.forEach(it -> {
            if (it.isActive()) {
                foundActive.set(true);
                if (it.getResponsibility() != null) {
                    activeResponsibilities.add(it);
                }
                if (it.isMainPosition()) {
                    if (activePostalAddressObj.get() == null && it.getOrganizationAddress() != null) {
                        activePostalAddressObj.set(it.getOrganizationAddress());
                    }
                    activeMemberships.add(it);
                }
            } else {
                if (it.getResponsibility() != null) {
                    formerResponsibilities.add(it);
                }
                if (it.isMainPosition()) {
                    if (formerPostalAddressObj.get() == null && it.getOrganizationAddress() != null) {
                        formerPostalAddressObj.set(it.getOrganizationAddress());
                    }
                    formerMemberships.add(it);
                }
            }
        });
        //
        final Collection<Membership> memberships;
        final Collection<Membership> responsibilities;
        final OrganizationAddress postalAddressObj;
        if (foundActive.get()) {
            modelAndView.addObject("isFormerMember", Boolean.FALSE); //$NON-NLS-1$
            memberships = activeMemberships;
            responsibilities = activeResponsibilities;
            postalAddressObj = activePostalAddressObj.get();
        } else {
            modelAndView.addObject("isFormerMember", Boolean.TRUE); //$NON-NLS-1$
            memberships = formerMemberships;
            responsibilities = formerResponsibilities;
            postalAddressObj = formerPostalAddressObj.get();
        }
        //
        modelAndView.addObject("person", personObj); //$NON-NLS-1$
        final Map<String, Object> obfuscatedValues = new HashMap<>();
        addObfuscatedEmailFields(obfuscatedValues, personObj.getPrimaryEmail(), null);
        addObfuscatedPhoneFields(obfuscatedValues, personObj.getOfficePhone().toInternationalForm(), "o"); //$NON-NLS-1$
        addObfuscatedPhoneFields(obfuscatedValues, personObj.getMobilePhone().toInternationalForm(), "m"); //$NON-NLS-1$
        addObfuscatedValues(modelAndView, obfuscatedValues);
        modelAndView.addObject("introText", inString(introText)); //$NON-NLS-1$
        modelAndView.addObject("memberships", memberships); //$NON-NLS-1$
        if (postalAddressObj != null) {
            modelAndView.addObject("postalAddress", postalAddressObj); //$NON-NLS-1$
        }
        modelAndView.addObject("responsibilities", responsibilities); //$NON-NLS-1$
        if (qrcode) {
            final UriComponents currentUri = ServletUriComponentsBuilder.fromCurrentContextPath().build();
            final UriBuilder uriBuilder = endpointUriBuilder(PERSON_VCARD_ENDPOINT);
            uriBuilder.scheme(currentUri.getScheme());
            uriBuilder.host(currentUri.getHost());
            uriBuilder.port(currentUri.getPort());
            uriBuilder.queryParam(DBID_ENDPOINT_PARAMETER, Long.toString(personObj.getId()));
            uriBuilder.queryParam(INATTACHMENT_ENDPOINT_PARAMETER, Boolean.TRUE.toString());
            if (organizationObj != null) {
                uriBuilder.queryParam(ORGANIZATION_ENDPOINT_PARAMETER, Long.toString(organizationObj.getId()));
            }
            modelAndView.addObject("vcardURL", uriBuilder.build().toASCIIString()); //$NON-NLS-1$
        }
        modelAndView.addObject("enablePhoto", Boolean.valueOf(photo)); //$NON-NLS-1$
        modelAndView.addObject("enableQrcode", Boolean.valueOf(qrcode)); //$NON-NLS-1$
        modelAndView.addObject("enableStatus", Boolean.valueOf(status)); //$NON-NLS-1$
        modelAndView.addObject("enableAdminPosition", Boolean.valueOf(admin)); //$NON-NLS-1$
        modelAndView.addObject("enableEmail", Boolean.valueOf(email)); //$NON-NLS-1$
        modelAndView.addObject("enableOfficePhone", Boolean.valueOf(officePhone)); //$NON-NLS-1$
        modelAndView.addObject("enableMobilePhone", Boolean.valueOf(mobilePhone)); //$NON-NLS-1$
        modelAndView.addObject("enableOfficeRoom", Boolean.valueOf(officeRoom)); //$NON-NLS-1$
        modelAndView.addObject("enablePostalAddress", Boolean.valueOf(postalAddress)); //$NON-NLS-1$
        modelAndView.addObject("enableQindexes", Boolean.valueOf(qindexes)); //$NON-NLS-1$
        modelAndView.addObject("enableLinks", Boolean.valueOf(links)); //$NON-NLS-1$
        return modelAndView;
    }

}
