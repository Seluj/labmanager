package fr.utbm.ciad.wprest.person;

import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationType;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.jury.JuryType;
import fr.utbm.ciad.labmanager.data.member.Person;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
@RequestMapping("/api/v" + Constants.MANAGER_MAJOR_VERSION + "/person")
public class PersonRestService {

    private final PersonService personService;
    private final PublicationService publicationService;
    private final SupervisionService supervisionService;

    public PersonRestService(
            @Autowired PersonService personService,
            @Autowired PublicationService publicationService,
            @Autowired SupervisionService supervisionService
    ) {
        this.personService = personService;
        this.publicationService = publicationService;
        this.supervisionService = supervisionService;
    }

    /**
     * Retrieves the JSON data for a user's information card.
     *
     * @param id the ID of the user
     * @return the card data if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/card/{id}")
    public ResponseEntity<PersonCardDTO> getPersonCard(
            @PathVariable("id") long id
    ) {
        Person p = personService.getPersonById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        String firstName = p.getFirstName();
        String lastName = p.getLastName();
        String email = p.getEmail();
        PhoneNumber mobilePhone = p.getMobilePhone();
        PhoneNumber officePhone = p.getOfficePhone();
        String room = p.getOfficeRoom();
        p.getMemberships();

        PersonService.PersonRankingUpdateInformation rankingUpdateInformation = personService.getPersonRankingUpdateInformation(p);
        PersonService.PersonLinks personLinks = personService.getPersonLinks(p);

        PersonCardDTO card = new PersonCardDTO(firstName, lastName, email, mobilePhone, officePhone, room, rankingUpdateInformation, personLinks);

        return ResponseEntity.ok(card);
    }

    /**
     * Retrieves the JSON data for a user's biography.
     * @param id the id of the user
     * @return the biography of the user if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/biography/{id}")
    public ResponseEntity<PersonBiographyDTO> getPersonBiography(
            @PathVariable("id") long id
    ) {
        Person p = personService.getPersonById(id);
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
     * @param id       the ID of the person whose publications are to be retrieved
     * @param year     (optional) the year of publication to filter results. If not provided, all years will be included
     * @param language (optional) the language of publication to filter results. If not provided, all languages will be included
     * @param keywords (optional) a comma-separated list of keywords to filter results. If not provided, all keywords will be included
     * @return a ResponseEntity containing a list of PublicationsDTO objects that match the specified filters, or an appropriate error response
     *         if the person is not found or no publications match the criteria.
     */
    @GetMapping("/publications/{id}")
    @Transactional
    public ResponseEntity<List<PublicationsDTO>> getPersonPublications(
            @PathVariable("id") long id,
            @RequestParam(required = false) Optional<Long> year,
            @RequestParam(required = false) Optional<String> language,
            @RequestParam(required = false) Optional<String> keywords) {

        Person p = personService.getPersonById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }


        List<PublicationsDTO> publications = new ArrayList<>();

        List<Publication> publicationList = new ArrayList<>(publicationService.getPublicationsByPersonId(id));
        for (Publication publication : publicationList) {

            //filter by year
            if (year.isPresent()) {
                if (publication.getPublicationDate() == null || year.get().intValue() != publication.getPublicationYear()) {
                    continue;
                }
            }

            //filter by language spoken
            PublicationLanguage spokenlanguage = publication.getMajorLanguage();
            if (language.isPresent()) {
                if (spokenlanguage != PublicationLanguage.valueOfCaseInsensitive(language.get(), PublicationLanguage.OTHER)) {
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

            List<String> authors = new ArrayList<>();
            publication.getAuthors().forEach(author -> authors.add(author.getFullName()));

            String abstractText = publication.getAbstractText();
            String pdfUrl = publication.getPathToDownloadablePDF();

            publications.add(new PublicationsDTO(title, publicationDate, publicationType, authors, abstractText, pdfUrl, spokenlanguage, containedKeywordsList));
        }

        return ResponseEntity.ok(publications);
    }


    /**
     * Retrieves the invitations when the user is a guest.
     * @param id the id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/guestInvitations")
    @Transactional
    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonGuestInvitations(
            @RequestParam long id
    ) {
        Person p = personService.getPersonById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> invitations = new HashSet<>(p.getGuestInvitations());

        return ResponseEntity.ok(getInvitationsDTOEntity(invitations));
    }


    /**
     * Retrieves the invitations when the user is the inviter.
     * @param id the id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/inviterInvitations/{id}")
    @Transactional
    public ResponseEntity<Map<PersonInvitationType, Set<PersonInvitationData>>> getPersonInviterInvitations(
            @PathVariable("id") long id
    ) {
        Person p = personService.getPersonById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> invitations = new HashSet<>(p.getInviterInvitations());

        return ResponseEntity.ok(getInvitationsDTOEntity(invitations));
    }

    /**
     * Retrieves all invitations concerning the user.
     * @param id the id of the user
     * @return the list of invitations if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/invitations")
    @Transactional
    public ResponseEntity<PersonInvitationsDTO> getAllPersonInvitations(
            @RequestParam long id
    ) {
        Person p = personService.getPersonById(id);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        Set<PersonInvitation> guestInvitations = new HashSet<>(p.getGuestInvitations());
        Set<PersonInvitation> inviterInvitations = new HashSet<>(p.getInviterInvitations());

        PersonInvitationsDTO allInvitations = new PersonInvitationsDTO(getInvitationsDTOEntity(guestInvitations), getInvitationsDTOEntity(inviterInvitations));

        return ResponseEntity.ok(allInvitations);
    }

    /**
     * Retrieves all jurys concerning the user.
     * @param id the id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/jurys/{id}")
    @Transactional
    public ResponseEntity<Set<PersonJuryMembershipDTO>> getPersonJuryMemberships(
            @PathVariable("id") long id
    ) {
        Person p = personService.getPersonById(id);
        Set<PersonJuryMembershipDTO> jurys = new HashSet<>();

        Set<JuryMembership> participationJurys = new HashSet<>(p.getParticipationJurys());
        participationJurys.forEach(jury -> {
            String title = jury.getTitle();
            int year = jury.getDate().getYear();
            String candidate = jury.getCandidate().getFullName();

            List<String> promotersOrDirectorsNames = new ArrayList<>();
            List<Person> promotersOrDirectors = new ArrayList<>(jury.getPromoters());
            promotersOrDirectors.forEach(person -> promotersOrDirectorsNames.add(person.getFullName()));


            String university = jury.getUniversity();
            String country = jury.getCountryDisplayName();

            PersonOrganisationData organisationData = new PersonOrganisationData(university, "", country);

            JuryType type = jury.getDefenseType();

            boolean inFrance = (country.equals("France"));

            jurys.add(new PersonJuryMembershipDTO(title, year, candidate, promotersOrDirectorsNames, organisationData, type, inFrance));
        });

        return ResponseEntity.ok(jurys);
    }

    /**
     * Retrieves all supervisions <u>supervised</u> by the user.
     * @param id the id of the user
     * @return the list of jurys if found, or a {@code 404 error} if no user is found.
     */
    @GetMapping("/supervisions{id}")
    @Transactional
    public ResponseEntity<List<SupervisionsDTO>> getPersonSupervisions(
            @PathVariable("id") long id
    ) {
        List<SupervisionsDTO> supervisions = new ArrayList<>();

        Person p = personService.getPersonById(id);
        List<Supervision> supervised = supervisionService.getSupervisionsForSupervisor(id);
        supervised.forEach(supervision -> {
            String title = supervision.getTitle();
            String supervisedPerson = supervision.getSupervisedPerson().getPerson().getFullName();
            int year = supervision.getYear();

            PersonOrganisationData organisation = getOrganizationDataForSupervision(supervision);

            //get all supervisors name and type
            List<Supervisor> supervisors = new ArrayList<>(supervision.getSupervisors());
            List<SupervisedPersonSupervisorData> supervisorsData = new ArrayList<>();

            supervisors.forEach(supervisor -> {
                String name = supervisor.getSupervisor().getFullName();
                SupervisorType type = supervisor.getType();

                supervisorsData.add(new SupervisedPersonSupervisorData(name, type));
            });

            //filter to get the type of supervision for the requested person
            Optional<SupervisedPersonSupervisorData> personSupervisorData = supervisorsData.stream()
                    .filter(data -> data.name().equals(p.getFullName()))
                    .findFirst();

            SupervisorType supervisionType = null;
            if (personSupervisorData.isPresent()) {
                supervisionType = personSupervisorData.get().type();
            }

            supervisions.add(new SupervisionsDTO(title, supervisedPerson, year, supervisionType, organisation, supervisorsData));
        });


        return ResponseEntity.ok(supervisions);
    }

    @Transactional
    public Map<PersonInvitationType, Set<PersonInvitationData>> getInvitationsDTOEntity(Set<PersonInvitation> invitations) {
        Map<PersonInvitationType, Set<PersonInvitationData>> personInvitations = new HashMap<>();

        invitations.forEach(it -> {

            PersonInvitationType type = it.getType();
            DateRange dates = new DateRange(it.getStartDate(), it.getEndDate());

            personInvitations.computeIfAbsent(type, k -> new HashSet<>());
            personInvitations.get(type).add(new PersonInvitationData(it.getTitle(), it.getGuest().getFullName(), it.getUniversity(), dates));
        });

        return personInvitations;
    }


    private PersonOrganisationData getOrganizationDataForSupervision(Supervision supervision) {

        String researchOrganization = "";
        String country = "";
        if (supervision.getSupervisedPerson().getDirectResearchOrganization() != null) {
            researchOrganization = supervision.getSupervisedPerson().getDirectResearchOrganization().getName();
            country =  supervision.getSupervisedPerson().getDirectResearchOrganization().getCountryDisplayName();
        }

        String researchOrganization2 = "";
        if (supervision.getSupervisedPerson().getSuperResearchOrganization() != null) {
            researchOrganization2 = supervision.getSupervisedPerson().getSuperResearchOrganization().getName();
        }

        return new PersonOrganisationData(researchOrganization, researchOrganization2, country);
    }

    /**
     * Data Transfer Object (DTO) representing a person's card containing their personal and contact information.
     *
     * @param firstName       the first name of the person
     * @param lastName        the last name of the person
     * @param email           the email address of the person
     * @param mobilePhone     the mobile phone number of the person
     * @param officePhone     the office phone number of the person
     * @param room            the office room number or location of the person
     * @param ranking         the ranking information of the person
     * @param links           the links related to the person's profile
     */
    public record PersonCardDTO(String firstName,
                                String lastName,
                                String email,
                                PhoneNumber mobilePhone,
                                PhoneNumber officePhone,
                                String room,
                                PersonService.PersonRankingUpdateInformation ranking,
                                PersonService.PersonLinks links) {}

    /**
     * Data Transfer Object (DTO) representing a person's biography.
     *
     * @param isPrivate        indicates if the biography is private
     * @param biographyContent the content of the biography if the biography is not private
     */
    public record PersonBiographyDTO(boolean isPrivate,
                                     String biographyContent) {}

    /**
     * Data Transfer Object (DTO) representing invitations related to a person, including both guest and inviter invitations data.
     *
     * @param guestInvitations  a map of guest invitations categorized by invitation type
     * @param inviterInvitations a map of invitations where the person is the inviter, categorized by invitation type
     */
    public record PersonInvitationsDTO(Map<PersonInvitationType, Set<PersonInvitationData>> guestInvitations,
                                       Map<PersonInvitationType, Set<PersonInvitationData>> inviterInvitations) {}

    /**
     * Data Transfer Object (DTO) representing a publication's information.
     *
     * @param title           the title of the publication
     * @param publicationDate the date the publication was released
     * @param publicationType the type of the publication (e.g., journal article, book, etc.)
     * @param persons         a list of names of persons associated with the publication
     * @param abstractText    a brief summary of the publication's content
     * @param pdfUrl         the URL link to the PDF version of the publication
     * @param language        the language in which the publication is written
     * @param keywords        a list of keywords related to the publication
     */
    public record PublicationsDTO(String title,
                                  LocalDate publicationDate,
                                  PublicationType publicationType,
                                  List<String> persons,
                                  String abstractText,
                                  String pdfUrl,
                                  PublicationLanguage language,
                                  List<String> keywords) {}

    /**
     * Data Transfer Object (DTO) representing supervision information for a research project.
     *
     * @param name                  the name of the supervision
     * @param supervisedPerson      the person being supervised
     * @param year                  the year of supervision
     * @param supervisionType       the type of supervision
     * @param researchOrganization   the research organization involved
     * @param supervisors            a list of supervisors for the supervised person
     */
    public record SupervisionsDTO(String name,
                                  String supervisedPerson,
                                  int year,
                                  SupervisorType supervisionType,
                                  PersonOrganisationData researchOrganization,
                                  List<SupervisedPersonSupervisorData> supervisors) {}

    /**
     * Data Transfer Object (DTO) representing a person's membership in a jury.
     *
     * @param title           the title of the jury membership
     * @param year            the year of the jury membership
     * @param candidate       the name of the candidate being evaluated
     * @param directors       a list of directors involved in the jury
     * @param university      the university associated with the jury
     * @param type            the type of jury
     * @param inFrance        indicates if the jury is located in France
     */
    public record PersonJuryMembershipDTO(String title,
                                          int year,
                                          String candidate,
                                          List<String> directors,
                                          PersonOrganisationData university,
                                          JuryType type,
                                          boolean inFrance) {}

    /**
     * Describes an invitation for a person.
     *
     * @param title       the title of the invitation
     * @param guestName   the name of the guest receiving the invitation
     * @param university   the university associated with the invitation
     * @param dates       the date range for the invitation
     */
    public record PersonInvitationData(String title,
                                       String guestName,
                                       String university,
                                       DateRange dates) {}

    /**
     * Describes information about a person's organization.
     *
     * @param directResearchOrganization  the person's direct research organization
     * @param superResearchOrganization   the person's supervising research organization
     * @param country                     the country of the organizations
     */
    public record PersonOrganisationData(String directResearchOrganization,
                                         String superResearchOrganization,
                                         String country) {}

    /**
     * Describes information about a person's supervisor.
     *
     * @param name the name of the supervisor
     * @param type the type of supervision (e.g., primary, co-supervisor)
     */
    public record SupervisedPersonSupervisorData(String name,
                                                 SupervisorType type) {}

    /**
     * Simple definition of a range of dates.
     *
     * @param startDate the start date of the range
     * @param endDate   the end date of the range
     */
    public record DateRange(LocalDate startDate,
                            LocalDate endDate) {}

}