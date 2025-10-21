package fr.utbm.ciad.wprest.person;

import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationType;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.jury.JuryType;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.data.supervision.Supervisor;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorType;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import fr.utbm.ciad.wprest.data.DateRange;
import fr.utbm.ciad.wprest.data.PersonOnWebsite;
import fr.utbm.ciad.wprest.organization.data.OrganizationData;
import fr.utbm.ciad.wprest.person.data.PersonInvitationData;
import fr.utbm.ciad.wprest.person.data.PersonOrganizationData;
import fr.utbm.ciad.wprest.person.data.SupervisedPersonSupervisorData;
import fr.utbm.ciad.wprest.person.data.UniversityData;
import fr.utbm.ciad.wprest.person.data.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing person-related operations.
 *
 * <p>This controller provides endpoints for accessing and manipulating
 * person-related data, including retrieving information about persons,
 * and handling invitations.</p>
 *
 * <p>Base URL: /api/v{majorVersion}/persons</p>
 *
 * <p>Use the person ID or webpageId to request data for a person.</p>
 *
 * <p>This class is annotated with {@link RestController} and handles
 * HTTP requests mapped to the /api/v{majorVersion}/persons endpoint.
 * The version of the API is determined by the constant
 * {@link Constants#MANAGER_MAJOR_VERSION}.</p>
 */
@Transactional
@RestController
@RequestMapping("/api/v" + Constants.MANAGER_MAJOR_VERSION + "/persons")
public class PersonRestService {

    private final PersonService personService;
    private final SupervisionService supervisionService;

    public PersonRestService(
            @Autowired PersonService personService,
            @Autowired SupervisionService supervisionService) {
        this.personService = personService;
        this.supervisionService = supervisionService;
    }

    @Operation(summary = "Gets the card of the user", description = "Gets the information of the user that can be displayed in a card", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The information of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/card")

    public ResponseEntity<PersonCardDTO> getPersonCard(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {
        Person person = getPersonFromRequest(id, pageId);

        ResponseEntity<PersonCardDTO> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        PersonCardDTO card = getPersonCardDto(person);
        return ResponseEntity.ok(card);
    }

    /**
     * Retrieves the JSON data for a user's biography based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the biography of the user if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets the biography of the user", description = "Gets the biography of the user, either public or private", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The biography of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/biography")

    public ResponseEntity<PersonBiographyDTO> getPersonBiography(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {
        Person person = getPersonFromRequest(id, pageId);

        ResponseEntity<PersonBiographyDTO> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        boolean isPrivateBiography = person.getPrivateBiography();
        String biographyContent = isPrivateBiography ? " " : person.getBiography();

        PersonBiographyDTO biography = new PersonBiographyDTO(isPrivateBiography, biographyContent);
        return ResponseEntity.ok(biography);
    }

    /**
     * Retrieves the invitations when the user is a guest based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets the invitations of the user as guest", description = "Gets the invitations of the user as guest, either public or private", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The invitations of the user as guest"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/guestInvitations")

    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonGuestInvitations(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {

        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        return ResponseEntity.ok(getInvitationsDTOEntity(person.getGuestInvitations()));
    }


    /**
     * Retrieves the invitations when the user is the inviter. based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets the invitations of the user as inviter", description = "Gets the invitations of the user as inviter, either public or private", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The invitations of the user as inviter"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/inviterInvitations")

    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonInviterInvitations(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {
        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        Set<PersonInvitation> invitations = person.getInviterInvitations();

        return ResponseEntity.ok(getInvitationsDTOEntity(invitations));
    }

    /**
     * Retrieves all invitations concerning the user based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets all the invitations of the user", description = "Gets all the invitations of the user, either as guest or inviter", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The invitations of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/invitations")

    public ResponseEntity<PersonInvitationsDTO> getAllPersonInvitations(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {
        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<PersonInvitationsDTO> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        Set<PersonInvitation> guestInvitations = person.getGuestInvitations();
        Set<PersonInvitation> inviterInvitations = person.getInviterInvitations();

        PersonInvitationsDTO allInvitations = new PersonInvitationsDTO(getInvitationsDTOEntity(guestInvitations), getInvitationsDTOEntity(inviterInvitations));

        return ResponseEntity.ok(allInvitations);
    }

    /**
     * Retrieves all jurys concerning the user based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets the jurys of the user", description = "Gets the jurys of the user", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The jurys of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/jurys")

    public ResponseEntity<Set<PersonJuryMembershipDTO>> getPersonJuryMemberships(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {
        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<Set<PersonJuryMembershipDTO>> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        Set<PersonJuryMembershipDTO> jurys = person.getParticipationJurys().stream()
                .map(this::getJuryMembershipDataFrom)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(jurys);
    }

    /**
     * Retrieves all supervisions <u>supervised</u> based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @Operation(summary = "Gets all supervisions supervised by the user", description = "Gets all supervisions supervised by the user, either public or private", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The supervisions of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/supervisions")

    public ResponseEntity<List<SupervisionsDTO>> getPersonSupervisions(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId
    ) {

        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<List<SupervisionsDTO>> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        List<SupervisionsDTO> supervisions = supervisionService.getSupervisionsForSupervisor(person.getId()).stream()
                .map(supervision -> this.getSupervisionsDataFrom(supervision, person))
                .collect(Collectors.toList());

        return ResponseEntity.ok(supervisions);
    }


    /**
     * Retrieves a set of person memberships based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id     the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return a response entity containing a set of PersonMembershipDTO or an error status
     */
    @Operation(summary = "Gets the memberships of the user", description = "Gets the memberships of the user", tags = {"Person API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The memberships of the user"),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no user is found with the provided ID or pageId.")
    })
    @GetMapping("/memberships")

    public ResponseEntity<Set<PersonMembershipDTO>> getMemberships(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId) {

        Person person = getPersonFromRequest(id, pageId);
        ResponseEntity<Set<PersonMembershipDTO>> errorPage = getErrorPage(id, pageId, person);
        if (errorPage != null) {
            return errorPage;
        }

        Set<PersonMembershipDTO> personMembershipDTOS = person.getMemberships().stream()
                .map(this::getMembershipDataFrom)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(personMembershipDTOS);
    }

    /**
     * Transforms a set of PersonInvitation entities into a map of PersonInvitationType
     * with corresponding sets of PersonInvitationData.
     *
     * @param invitations a set of PersonInvitation entities
     * @return a map where keys are PersonInvitationType and values are sets of PersonInvitationData
     */

    public Map<PersonInvitationType, Set<PersonInvitationData>> getInvitationsDTOEntity(Set<PersonInvitation> invitations) {
        Map<PersonInvitationType, Set<PersonInvitationData>> personInvitations = new HashMap<>();

        for (PersonInvitation invitation : invitations) {
            PersonInvitationType type = invitation.getType();
            DateRange dates = new DateRange(invitation.getStartDate(), invitation.getEndDate());

            personInvitations.computeIfAbsent(type, k -> new HashSet<>());

            String guestName = invitation.getGuest().getFullName();
            String guestWebpageId = invitation.getGuest().getWebPageId();

            PersonOnWebsite guestData = new PersonOnWebsite(guestName, guestWebpageId);

            String universityName = invitation.getUniversity();
            String universityCountry = invitation.getCountryDisplayName();
            boolean inFrance = (invitation.getCountryDisplayName().equalsIgnoreCase("France"));

            UniversityData university = new UniversityData(universityName, universityCountry, inFrance);

            personInvitations.get(type).add(new PersonInvitationData(invitation.getTitle(), guestData, university, dates));

        }

        return personInvitations;
    }

    /**
     * Retrieves organization data for a given supervision, including both
     * direct and super organizations if available.
     *
     * @param supervision the supervision entity
     * @return a PersonOrganizationData object containing organization details for the supervision
     */
    public PersonOrganizationData getOrganizationDataForSupervision(Supervision supervision) {
        OrganizationData directOrganization = null;
        OrganizationData superOrganization = null;

        if (supervision.getSupervisedPerson().getDirectResearchOrganization() != null) {
            directOrganization = getOrganisationData(supervision.getSupervisedPerson().getDirectResearchOrganization());
        }

        if (supervision.getSupervisedPerson().getDirectResearchOrganization() != null) {
            superOrganization = getOrganisationData(supervision.getSupervisedPerson().getSuperResearchOrganization());
        }

        return new PersonOrganizationData(directOrganization, superOrganization);
    }

    /**
     * Retrieves a person entity using either an ID or a web page ID.
     * If both are absent, returns null.
     *
     * @param id     an optional parameter for the person ID
     * @param pageId an optional parameter for the person's web page ID
     * @return the Person entity or null if not found
     * @see Person
     */
    public Person getPersonFromRequest(Long id, String pageId) {
        if (id != null) {
            return personService.getPersonById(id);
        } else if (pageId != null) {
            return personService.getPersonByWebPageId(pageId);
        }

        return null;
    }

    /**
     * Builds a ResponseEntity error object if the given parameters are invalid
     * or if the person entity is null.
     *
     * @param id     an optional parameter for the person ID
     * @param pageId an optional parameter for the person's web page ID
     * @param person the person entity, null if not found
     * @return a ResponseEntity containing an error status code (400 or 404)
     * if the parameters are invalid or if the person is null, null otherwise
     */
    public <T> ResponseEntity<T> getErrorPage(Long id, String pageId, Person person) {
        if ((id == null && pageId == null) || (id != null && pageId != null)) {
            return ResponseEntity.badRequest().build();
        }

        if (person == null) {
            return ResponseEntity.notFound().build();
        }

        return null;
    }

    /**
     * Retrieves organization data including the name, URL, addresses, and country
     * based on the provided research organization.
     *
     * @param researchOrganization the ResearchOrganization entity
     * @return an OrganizationData object containing details about the research organization
     */
    public OrganizationData getOrganisationData(ResearchOrganization researchOrganization) {
        if (researchOrganization != null) {
            Set<OrganizationAddress> organizationAddresses = researchOrganization.getAddresses();

            Set<String> organizationAddressesName = new HashSet<>();
            for (OrganizationAddress address : organizationAddresses) {
                organizationAddressesName.add(address.getFullAddress());
            }

            String organizationName = researchOrganization.getName();
            String organizationCountry = researchOrganization.getCountry().getDisplayCountry();
            String organizationUrl = researchOrganization.getOrganizationURL();

            return new OrganizationData(organizationName, organizationUrl, organizationAddressesName, organizationCountry);
        }

        return null;
    }


    /**
     * Extracts the supervisor data including the name, webpageId, and supervisor type
     * based on the provided list of supervisors.
     *
     * @param supervisors the list of supervisors
     * @return the list of sata associated to the supervisors
     */
    public List<SupervisedPersonSupervisorData> getSupervisorsDataFrom(List<Supervisor> supervisors) {
        List<SupervisedPersonSupervisorData> supervisorsData = new ArrayList<>();

        for (Supervisor supervisor : supervisors) {
            String name = supervisor.getSupervisor().getFullName();
            String webpageId = supervisor.getSupervisor().getWebPageId();
            SupervisorType type = supervisor.getType();

            PersonOnWebsite person = new PersonOnWebsite(name, webpageId);

            supervisorsData.add(new SupervisedPersonSupervisorData(person, type));
        }

        return supervisorsData;
    }

    /**
     * Constructs a PersonJuryMembershipDTO containing details of a jury membership.
     *
     * @param jury the JuryMembership entity containing the jury details
     * @return a PersonJuryMembershipDTO object populated with the jury membership's title, year, candidate,
     * directors or promoters, university, and type
     */
    public PersonJuryMembershipDTO getJuryMembershipDataFrom(JuryMembership jury) {
        String title = jury.getTitle();
        int year = jury.getDate().getYear();

        String candidateName = jury.getCandidate().getFullName();
        String candidateWebpageId = jury.getCandidate().getWebPageId();

        PersonOnWebsite candidate = new PersonOnWebsite(candidateName, candidateWebpageId);

        List<PersonOnWebsite> promotersOrDirectorsNames = new ArrayList<>();

        List<Person> promotersOrDirectors = jury.getPromoters();

        for (Person person : promotersOrDirectors) {
            String name = person.getFullName();
            String webpageId = person.getWebPageId();

            promotersOrDirectorsNames.add(new PersonOnWebsite(name, webpageId));
        }

        String universityName = jury.getUniversity();
        String universityCountry = jury.getCountryDisplayName();
        boolean inFrance = (universityCountry.equalsIgnoreCase("France"));

        UniversityData university = new UniversityData(universityName, universityCountry, inFrance);

        JuryType type = jury.getDefenseType();

        return new PersonJuryMembershipDTO(title, year, candidate, promotersOrDirectorsNames, type, university);
    }

    /**
     * Constructs a SupervisionsDTO containing details of a supervision.
     *
     * @param supervision the Supervision entity containing the supervision details
     * @param person      the Person entity for whom the supervision details should be retrieved
     * @return a SupervisionsDTO object populated with the supervision's title, year, supervised person,
     * organization, supervisors, and type of supervision
     */
    public SupervisionsDTO getSupervisionsDataFrom(Supervision supervision, Person person) {
        String title = supervision.getTitle();
        int year = supervision.getYear();

        String supervisedPersonName = supervision.getSupervisedPerson().getPerson().getFullName();
        String supervisedPersonWebPageId = supervision.getSupervisedPerson().getPerson().getWebPageId();
        PersonOnWebsite supervisedPerson = new PersonOnWebsite(supervisedPersonName, supervisedPersonWebPageId);

        PersonOrganizationData organisation = getOrganizationDataForSupervision(supervision);

        List<Supervisor> supervisors = supervision.getSupervisors();
        List<SupervisedPersonSupervisorData> supervisorsData = getSupervisorsDataFrom(supervisors);

        //filter with streams to get the type of supervision for the requested person
        Optional<SupervisedPersonSupervisorData> personSupervisorData = supervisorsData.stream()
                .filter(data -> data.person().name().equals(person.getFullName()))
                .findFirst();

        SupervisorType supervisionType = null;
        if (personSupervisorData.isPresent()) {
            supervisionType = personSupervisorData.get().type();
        }

        return new SupervisionsDTO(title, supervisedPerson, year, supervisionType, organisation, supervisorsData);
    }

    /**
     * Constructs a PersonMembershipDTO containing details of a person's membership.
     *
     * @param membership the Membership entity containing the person's membership details
     * @return a PersonMembershipDTO object populated with the membership's status and organization data
     */
    public PersonMembershipDTO getMembershipDataFrom(Membership membership) {
        MemberStatus status = membership.getMemberStatus();

        ResearchOrganization organization = membership.getDirectResearchOrganization();
        OrganizationData organizationData = getOrganisationData(organization);

        return new PersonMembershipDTO(status, organizationData);
    }

    public PersonCardDTO getPersonCardDto(Person person) {
        if (person == null) {
            return null;
        }

        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        String email = person.getEmail();
        PhoneNumber mobilePhone = person.getMobilePhone();
        PhoneNumber officePhone = person.getOfficePhone();
        String room = person.getOfficeRoom();

        String webpage = person.getWebPageId();
        URL photo = person.getPhotoURL();

        PersonService.PersonRankingUpdateInformation rankingUpdateInformation = personService.getPersonRankingUpdateInformation(person);
        PersonService.PersonLinks personLinks = personService.getPersonLinks(person);

        return new PersonCardDTO(firstName, lastName, email, photo, mobilePhone, officePhone, room, rankingUpdateInformation, personLinks, webpage);
    }
}