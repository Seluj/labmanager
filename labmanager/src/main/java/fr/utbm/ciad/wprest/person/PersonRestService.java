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
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.data.supervision.Supervisor;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorType;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import fr.utbm.ciad.wprest.data.DateRange;
import fr.utbm.ciad.wprest.data.PersonOnWebsite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing person-related operations.
 *
 * <p>This controller provides endpoints for accessing and manipulating
 * person-related data, including retrieving information about persons,
 * managing their publications, and handling invitations.</p>
 *
 * <p>Base URL: /api/v{majorVersion}/person</p>
 *
 * <p>Use the person ID to request data for a person.</p>
 *
 * <p>This class is annotated with {@link RestController} and handles
 * HTTP requests mapped to the /api/v{majorVersion}/person endpoint.
 * The version of the API is determined by the constant
 * {@link Constants#MANAGER_MAJOR_VERSION}.</p>
 */
@RestController
@RequestMapping("/api/v" + Constants.MANAGER_MAJOR_VERSION + "/persons")
public class PersonRestService {

    private final PersonService personService;
    private final PublicationService publicationService;
    private final SupervisionService supervisionService;

    public PersonRestService(
            @Autowired PersonService personService,
            @Autowired PublicationService publicationService,
            @Autowired SupervisionService supervisionService) {
        this.personService = personService;
        this.publicationService = publicationService;
        this.supervisionService = supervisionService;
    }

    /**
     * Retrieves the JSON data for a user's information card based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the card data if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/card")
    @Transactional
    public ResponseEntity<PersonCardDTO> getPersonCard(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        String firstName = p.getFirstName();
        String lastName = p.getLastName();
        String email = p.getEmail();
        PhoneNumber mobilePhone = p.getMobilePhone();
        PhoneNumber officePhone = p.getOfficePhone();
        String room = p.getOfficeRoom();

        String webpage = p.getWebPageId();
        URL photo = p.getPhotoURL();

        PersonService.PersonRankingUpdateInformation rankingUpdateInformation = personService.getPersonRankingUpdateInformation(p);
        PersonService.PersonLinks personLinks = personService.getPersonLinks(p);

        PersonCardDTO card = new PersonCardDTO(firstName, lastName, email, photo, mobilePhone, officePhone, room, rankingUpdateInformation, personLinks, webpage);

        return ResponseEntity.ok(card);
    }

    /**
     * Retrieves the JSON data for a user's biography based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the biography of the user if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/biography")
    @Transactional
    public ResponseEntity<PersonBiographyDTO> getPersonBiography(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isPrivateBiography = p.getPrivateBiography();
        String biographyContent = isPrivateBiography ? " " : p.getBiography();

        PersonBiographyDTO biography = new PersonBiographyDTO(isPrivateBiography, biographyContent);
        return ResponseEntity.ok(biography);
    }


    /**
     * Retrieves a list of publications for a specified person, allowing for optional filtering by year, language, and keywords.
     *
     * <p>This endpoint allows clients to filter the publications based on the specified parameters. If the optional parameters
     * are not provided, all publications for the specified person will be returned.</p>
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @param year     (optional) the year of publication to filter results. If not provided, all years will be included
     * @param language (optional) the language of publication to filter results. If not provided, all languages will be included
     * @param keywords (optional) a comma-separated list of keywords to filter results. If not provided, all keywords will be included
     * @return a ResponseEntity containing a list of PublicationsDTO objects that match the specified filters, or an appropriate error response
     * if the person is not found or no publications match the criteria.
     */
    @GetMapping("/publications")
    @Transactional
    public ResponseEntity<List<PublicationsDTO>> getPersonPublications(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId,
            @RequestParam(required = false) Optional<Long> year,
            @RequestParam(required = false) Optional<String> language,
            @RequestParam(required = false) Optional<String> keywords
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        List<PublicationsDTO> publications = new ArrayList<>();

        List<Publication> publicationList = new ArrayList<>(publicationService.getPublicationsByPersonId(p.getId()));
        for (Publication publication : publicationList) {

            //filter by year
            if (year.isPresent()) {
                if (publication.getPublicationDate() == null || year.get().intValue() != publication.getPublicationYear()) {
                    continue;
                }
            }

            //filter by language spoken
            PublicationLanguage spokenLanguage = publication.getMajorLanguage();
            if (language.isPresent()) {
                if (spokenLanguage != PublicationLanguage.valueOfCaseInsensitive(language.get(), PublicationLanguage.OTHER)) {
                    continue;
                }
            }

            //filter by keywords (OR operator)
            String containedKeywords = publication.getKeywords();

            if (keywords.isPresent()) {
                if (containedKeywords == null) {
                    continue;
                }

                String[] split = keywords.get().split("[ ,;]");
                boolean found = false;
                for (String keyword : split) {
                    if (containedKeywords.toUpperCase(Locale.ROOT).contains(keyword.toUpperCase(Locale.ROOT))) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    continue;
                }
            }

            List<String> containedKeywordsList = new ArrayList<>();
            if (publication.getKeywords() != null) {
                String[] split = containedKeywords.split("[,;]");
                containedKeywordsList = Arrays.asList(split);
            }

            String title = publication.getTitle();
            LocalDate publicationDate = publication.getPublicationDate();

            PublicationType publicationType = publication.getType();

            List<PersonOnWebsite> authors = new ArrayList<>();
            publication.getAuthors().forEach(author -> authors.add(new PersonOnWebsite(author.getFullName(), author.getWebPageId())));

            String abstractText = publication.getAbstractText();
            String pdfUrl = publication.getPathToDownloadablePDF();

            String doi = publication.getDOI();
            String issn = publication.getISSN();

            publications.add(new PublicationsDTO(title, doi, issn, publicationDate, publicationType, authors, abstractText, pdfUrl, spokenLanguage, containedKeywordsList));
        }

        return ResponseEntity.ok(publications);
    }


    /**
     * Retrieves the invitations when the user is a guest based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/guestInvitations")
    @Transactional
    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonGuestInvitations(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> invitations = new HashSet<>(p.getGuestInvitations());

        return ResponseEntity.ok(getInvitationsDTOEntity(invitations));
    }


    /**
     * Retrieves the invitations when the user is the inviter. based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/inviterInvitations")
    @Transactional
    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonInviterInvitations(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> invitations = new HashSet<>(p.getInviterInvitations());

        return ResponseEntity.ok(getInvitationsDTOEntity(invitations));
    }

    /**
     * Retrieves all invitations concerning the user based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/invitations")
    @Transactional
    public ResponseEntity<PersonInvitationsDTO> getAllPersonInvitations(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> guestInvitations = new HashSet<>(p.getGuestInvitations());
        Set<PersonInvitation> inviterInvitations = new HashSet<>(p.getInviterInvitations());

        PersonInvitationsDTO allInvitations = new PersonInvitationsDTO(getInvitationsDTOEntity(guestInvitations), getInvitationsDTOEntity(inviterInvitations));

        return ResponseEntity.ok(allInvitations);
    }

    /**
     * Retrieves all jurys concerning the user based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/jurys")
    @Transactional
    public ResponseEntity<Set<PersonJuryMembershipDTO>> getPersonJuryMemberships(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonJuryMembershipDTO> jurys = new HashSet<>();

        Set<JuryMembership> participationJurys = new HashSet<>(p.getParticipationJurys());
        participationJurys.forEach(jury -> {
            String title = jury.getTitle();
            int year = jury.getDate().getYear();

            String candidateName = jury.getCandidate().getFullName();
            String candidateWebpageId = jury.getCandidate().getWebPageId();

            PersonOnWebsite candidate = new PersonOnWebsite(candidateName, candidateWebpageId);

            List<PersonOnWebsite> promotersOrDirectorsNames = new ArrayList<>();

            List<Person> promotersOrDirectors = new ArrayList<>(jury.getPromoters());

            promotersOrDirectors.forEach(person -> {
                String name = person.getFullName();
                String webpageId = person.getWebPageId();

                promotersOrDirectorsNames.add(new PersonOnWebsite(name, webpageId));
            });

            String universityName = jury.getUniversity();
            String universityCountry = jury.getCountryDisplayName();
            boolean inFrance = (universityCountry.equalsIgnoreCase("France"));

            UniversityData university = new UniversityData(universityName, universityCountry, inFrance);

            JuryType type = jury.getDefenseType();

            jurys.add(new PersonJuryMembershipDTO(title, year, candidate, promotersOrDirectorsNames, type, university));
        });

        return ResponseEntity.ok(jurys);
    }

    /**
     * Retrieves all supervisions <u>supervised</u> based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/supervisions")
    @Transactional
    public ResponseEntity<List<SupervisionsDTO>> getPersonSupervisions(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId
    ) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        List<SupervisionsDTO> supervisions = new ArrayList<>();

        List<Supervision> supervised = supervisionService.getSupervisionsForSupervisor(p.getId());
        supervised.forEach(supervision -> {
            String title = supervision.getTitle();

            String supervisedPersonName = supervision.getSupervisedPerson().getPerson().getFullName();
            String supervisedPersonWebPageId = supervision.getSupervisedPerson().getPerson().getWebPageId();

            PersonOnWebsite supervisedPerson = new PersonOnWebsite(supervisedPersonName, supervisedPersonWebPageId);

            int year = supervision.getYear();

            PersonOrganizationData organisation = getOrganizationDataForSupervision(supervision);

            //get all supervisors name and type
            List<Supervisor> supervisors = new ArrayList<>(supervision.getSupervisors());
            List<SupervisedPersonSupervisorData> supervisorsData = new ArrayList<>();

            supervisors.forEach(supervisor -> {
                String name = supervisor.getSupervisor().getFullName();
                String webpageId = supervisor.getSupervisor().getWebPageId();
                SupervisorType type = supervisor.getType();

                PersonOnWebsite person = new PersonOnWebsite(name, webpageId);

                supervisorsData.add(new SupervisedPersonSupervisorData(person, type));
            });

            //filter to get the type of supervision for the requested person
            Optional<SupervisedPersonSupervisorData> personSupervisorData = supervisorsData.stream()
                    .filter(data -> data.person.name().equals(p.getFullName()))
                    .findFirst();

            SupervisorType supervisionType = null;
            if (personSupervisorData.isPresent()) {
                supervisionType = personSupervisorData.get().type();
            }

            supervisions.add(new SupervisionsDTO(title, supervisedPerson, year, supervisionType, organisation, supervisorsData));
        });

        return ResponseEntity.ok(supervisions);
    }


    /**
     * Retrieves a set of person memberships based on the given ID or page ID.
     * Returns a bad request if both or neither are provided.
     *
     * @param id the ID of the user or either
     * @param pageId the webpage_id of the user
     * @return a response entity containing a set of PersonMembershipDTO or an error status
     */
    @GetMapping("/memberships")
    @Transactional
    public ResponseEntity<Set<PersonMembershipDTO>> getMemberships(
            @RequestParam(required = false) Optional<Long> id,
            @RequestParam(required = false) Optional<String> pageId) {
        if (id.isPresent() == pageId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPersonFromRequest(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<Membership> memberships = new HashSet<>(p.getMemberships());

        Set<PersonMembershipDTO> personMembershipDTOS = new HashSet<>();

        memberships.forEach(membership -> {
            MemberStatus status = membership.getMemberStatus();

            ResearchOrganization organization = membership.getDirectResearchOrganization();
            OrganizationData organizationData = getOrganisationData(organization);

            personMembershipDTOS.add(new PersonMembershipDTO(status, organizationData));
        });

        return ResponseEntity.ok(personMembershipDTOS);
    }

    /**
     * Transforms a set of PersonInvitation entities into a map of PersonInvitationType
     * with corresponding sets of PersonInvitationData.
     *
     * @param invitations a set of PersonInvitation entities
     * @return a map where keys are PersonInvitationType and values are sets of PersonInvitationData
     */
    @Transactional
    public Map<PersonInvitationType, Set<PersonInvitationData>> getInvitationsDTOEntity(Set<PersonInvitation> invitations) {
        Map<PersonInvitationType, Set<PersonInvitationData>> personInvitations = new HashMap<>();

        invitations.forEach(it -> {

            PersonInvitationType type = it.getType();
            DateRange dates = new DateRange(it.getStartDate(), it.getEndDate());

            personInvitations.computeIfAbsent(type, k -> new HashSet<>());

            String guestName = it.getGuest().getFullName();
            String guestWebpageId = it.getGuest().getWebPageId();

            PersonOnWebsite guestData = new PersonOnWebsite(guestName, guestWebpageId);

            String universityName = it.getUniversity();
            String universityCountry = it.getCountryDisplayName();
            boolean inFrance = (it.getCountryDisplayName().equalsIgnoreCase("France"));

            UniversityData university = new UniversityData(universityName, universityCountry, inFrance);

            personInvitations.get(type).add(new PersonInvitationData(it.getTitle(), guestData, university, dates));
        });

        return personInvitations;
    }

    /**
     * Retrieves organization data for a given supervision, including both
     * direct and super organizations if available.
     *
     * @param supervision the supervision entity
     * @return a PersonOrganizationData object containing organization details for the supervision
     */
    private PersonOrganizationData getOrganizationDataForSupervision(Supervision supervision) {
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
     */
    public Person getPersonFromRequest(Optional<Long> id, Optional<String> pageId) {
        return id.map(personService::getPersonById)
                .orElseGet(() -> pageId.map(personService::getPersonByWebPageId).orElse(null));
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
            Set<OrganizationAddress> organizationAddresses = new HashSet<>(researchOrganization.getAddresses());
            Set<String> organizationAddressesName = organizationAddresses.stream()
                    .map(OrganizationAddress::getFullAddress)
                    .collect(Collectors.toSet());

            String organizationName = researchOrganization.getName();
            String organizationCountry = researchOrganization.getCountry().getDisplayCountry();
            String organizationUrl = researchOrganization.getOrganizationURL();

            return new OrganizationData(organizationName, organizationUrl, organizationAddressesName, organizationCountry);
        }

        return null;
    }


    /**
     * Data Transfer Object (DTO) representing a person's card containing their personal and contact information.
     *
     * @param firstName   the first name of the person
     * @param lastName    the last name of the person
     * @param email       the email address of the person
     * @param mobilePhone the mobile phone number of the person
     * @param officePhone the office phone number of the person
     * @param room        the office room number or location of the person
     * @param ranking     the ranking information of the person
     * @param links       the links related to the person's profile
     * @param webpageId   the id of the webpage related to the person's profile if any
     */
    public record PersonCardDTO(String firstName,
                                String lastName,
                                String email,
                                URL photo,
                                PhoneNumber mobilePhone,
                                PhoneNumber officePhone,
                                String room,
                                PersonService.PersonRankingUpdateInformation ranking,
                                PersonService.PersonLinks links,
                                String webpageId) {
    }

    /**
     * Data Transfer Object (DTO) representing a person's biography.
     *
     * @param isPrivate        indicates if the biography is private
     * @param biographyContent the content of the biography if the biography is not private
     */
    public record PersonBiographyDTO(boolean isPrivate,
                                     String biographyContent) {
    }

    /**
     * Data Transfer Object (DTO) representing invitations related to a person, including both guest and inviter invitations data.
     *
     * @param guestInvitations   a map of guest invitations categorized by invitation type
     * @param inviterInvitations a map of invitations where the person is the inviter, categorized by invitation type
     */
    public record PersonInvitationsDTO(Map<PersonInvitationType, Set<PersonInvitationData>> guestInvitations,
                                       Map<PersonInvitationType, Set<PersonInvitationData>> inviterInvitations) {
    }

    /**
     * Data Transfer Object (DTO) representing a publication's information.
     *
     * @param title           the title of the publication
     * @param doi             the doi of the publication
     * @param issn            the issn of the publication
     * @param publicationDate the date the publication was released
     * @param publicationType the type of the publication (e.g., journal article, book, etc.)
     * @param persons         a list of names of persons associated with the publication
     * @param abstractText    a brief summary of the publication's content
     * @param pdfUrl          the URL link to the PDF version of the publication
     * @param language        the language in which the publication is written
     * @param keywords        a list of keywords related to the publication
     */
    public record PublicationsDTO(String title,
                                  String doi,
                                  String issn,
                                  LocalDate publicationDate,
                                  PublicationType publicationType,
                                  List<PersonOnWebsite> persons,
                                  String abstractText,
                                  String pdfUrl,
                                  PublicationLanguage language,
                                  List<String> keywords) {
    }

    /**
     * Data Transfer Object (DTO) representing supervision information for a research project.
     *
     * @param name                 the name and webpageId of the supervision
     * @param supervisedPerson     the person being supervised
     * @param year                 the year of supervision
     * @param supervisionType      the type of supervision
     * @param researchOrganization the research organization involved
     * @param supervisors          a list of supervisors for the supervised person
     */
    public record SupervisionsDTO(String name,
                                  PersonOnWebsite supervisedPerson,
                                  int year,
                                  SupervisorType supervisionType,
                                  PersonOrganizationData researchOrganization,
                                  List<SupervisedPersonSupervisorData> supervisors) {
    }

    /**
     * Data Transfer Object (DTO) representing a person's membership in a jury.
     *
     * @param title      the title of the jury membership
     * @param year       the year of the jury membership
     * @param candidate  the name and webpageId of the candidate being evaluated
     * @param directors  a list of directors involved in the jury
     * @param university the university associated with the jury
     * @param type       the type of jury
     */
    public record PersonJuryMembershipDTO(String title,
                                          int year,
                                          PersonOnWebsite candidate,
                                          List<PersonOnWebsite> directors,
                                          JuryType type,
                                          UniversityData university) {
    }

    /**
     * Data Transfer Object (DTO) representing a person's membership in an organization.
     *
     * @param status the status of the person in the organization
     * @param organization the data of the organization
     */
    public record PersonMembershipDTO(MemberStatus status, OrganizationData organization) {
    }

    /**
     * Describes an invitation for a person.
     *
     * @param title      the title of the invitation
     * @param guest      the name and webpageId of the supervisor
     * @param university the university associated with the invitation
     * @param dates      the date range for the invitation
     */
    public record PersonInvitationData(String title,
                                       PersonOnWebsite guest,
                                       UniversityData university,
                                       DateRange dates) {
    }

    /**
     * Describe basic information about a university
     * @param name the name of the university
     * @param country the country of the university
     * @param inFrance whether the university is in France or not
     */
    public record UniversityData(String name, String country, boolean inFrance) {
    }

    /**
     * Describes information about a person's organization.
     *
     * @param directResearchOrganization the person's direct research organization
     * @param superResearchOrganization  the person's supervising research organization
     */
    public record PersonOrganizationData(OrganizationData directResearchOrganization,
                                         OrganizationData superResearchOrganization) {
    }

    /**
     * Describes basic information about an organization.
     * @param name the name of the organization
     * @param url the website of the organization
     * @param addresses the different addresses of the organization
     * @param country the country of the organization
     */
    public record OrganizationData(String name, String url, Set<String> addresses, String country) {
    }

    /**
     * Describes information about a person's supervisor.
     *
     * @param person the name and webpageId of the supervisor
     * @param type   the type of supervision (e.g., primary, co-supervisor)
     */
    public record SupervisedPersonSupervisorData(PersonOnWebsite person,
                                                 SupervisorType type) {
    }
}