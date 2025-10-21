package fr.utbm.ciad.wprest.publications;

import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.wprest.data.PersonOnWebsite;
import fr.utbm.ciad.wprest.publications.data.dto.PublicationsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing publication-related operations.
 *
 * <p>This controller provides endpoints for accessing and manipulating
 * publication-related data, including retrieving information about the publications for a person or an organization</p>
 *
 * <p>Base URL: /api/v{majorVersion}/publications</p>
 *
 *
 * <p>This class is annotated with {@link RestController} and handles
 * HTTP requests mapped to the /api/v{majorVersion}/publications endpoint.
 * The version of the API is determined by the constant
 * {@link Constants#MANAGER_MAJOR_VERSION}.</p>
 */
@Transactional
@RestController
@RequestMapping("/api/v" + Constants.MANAGER_MAJOR_VERSION + "/publications")
public class PublicationRestService {

    PersonService personService;
    PublicationService publicationService;
    ResearchOrganizationService researchOrganizationService;

    public PublicationRestService(@Autowired PersonService personService,
                                  @Autowired PublicationService publicationService,
                                  @Autowired ResearchOrganizationService researchOrganizationService
    ) {
        this.personService = personService;
        this.publicationService = publicationService;
        this.researchOrganizationService = researchOrganizationService;
    }


    /**
     * Retrieves a list of publications for a specified person, allowing for optional filtering by year, language, and keywords.
     *
     * <p>This endpoint allows clients to filter the publications based on the specified parameters. If the optional parameters
     * are not provided, all publications for the specified person will be returned.</p>
     *
     * @param id       the ID of the user or either
     * @param pageId   the webpage_id of the user
     * @param year     (optional) the year of publication to filter results. If not provided, all years will be included
     * @param language (optional) the language of publication to filter results. If not provided, all languages will be included
     * @param keywords (optional) a comma-separated list of keywords to filter results. If not provided, all keywords will be included
     * @return a ResponseEntity containing a list of PublicationsDTO objects that match the specified filters, or an appropriate error response
     * if the person is not found or no publications match the criteria.
     * @see Publication
     * @see PublicationLanguage
     * @see PublicationType
     * @see Person
     */
    @Operation(summary = "Gets the publications of the person", description = "Gets the publications of the person, either public or private", tags = {"Publication API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The publications of the person", content = @Content(schema = @Schema(implementation = PublicationsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and pageId are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no person is found with the provided ID or pageId.")
    })
    @GetMapping("/persons")

    public ResponseEntity<List<PublicationsDTO>> getPersonPublications(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String pageId,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String keywords
    ) {
        if ((id == null && pageId == null) || (id != null && pageId != null)) {
            return ResponseEntity.badRequest().build();
        }

        Person p = getPublicationPerson(id, pageId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        List<Publication> publicationList = publicationService.getPublicationsByPersonId(p.getId());
        List<PublicationsDTO> publications = getAndfilterPublicationsDataFrom(publicationList, year, language, keywords);

        return ResponseEntity.ok(publications);
    }

    /**
     * Retrieves a list of publications for a specified organization, allowing for optional filtering by year, language, and keywords.
     *
     * <p>This endpoint allows clients to filter the publications based on the specified parameters. If the optional parameters
     * are not provided, all publications for the specified organization will be returned.</p>
     *
     * @param id       the ID of the organization (optional)
     * @param acronym  the acronym of the organization (optional)
     * @param year     (optional) the year of publication to filter results. If not provided, all years will be included
     * @param language (optional) the language of publication to filter results. If not provided, all languages will be included
     * @param keywords (optional) a comma-separated list of keywords to filter results. If not provided, all keywords will be included
     * @return a ResponseEntity containing a list of PublicationsDTO objects that match the specified filters, or an appropriate error response
     * if the organization is not found or no publications match the criteria.
     */
    @Operation(summary = "Gets the publications of the organization", description = "Gets the publications of the organization, either public or private", tags = {"Publication API"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The publications of the organization", content = @Content(schema = @Schema(implementation = PublicationsDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request if both or neither id and acronym are provided"),
            @ApiResponse(responseCode = "404", description = "Not Found if no organization is found with the provided ID or acronym.")
    })
    @GetMapping("/organizations")

    public ResponseEntity<List<PublicationsDTO>> getOrganizationsPublications(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String acronym,
            @RequestParam(required = false) Long year,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false, defaultValue = "false") Boolean subOrganizations,
            @RequestParam(required = false, defaultValue = "false") Boolean filterActive
    ) {

        if ((id == null && acronym == null) || (id != null && acronym != null)) {
            return ResponseEntity.badRequest().build();
        }

        ResearchOrganization organization = getPublicationOrganization(id, acronym);

        if (organization == null) {
            return ResponseEntity.notFound().build();
        }


        Set<Publication> publications = publicationService.getPublicationsByOrganizationId(organization.getId(), subOrganizations, filterActive);
        List<PublicationsDTO> publicationsDTOList = getAndfilterPublicationsDataFrom(publications, year, language, keywords);

        return ResponseEntity.ok(publicationsDTOList);
    }

    /**
     * Filters a list of publications with the provided filters
     *
     * @param publicationList - the list of publication to filter
     * @param year            - the year to filter
     * @param language        - the language to filter
     * @param keywords        - the list of keywords to filter
     * @return The filtered publications
     */

    public List<PublicationsDTO> getAndfilterPublicationsDataFrom(Collection<Publication> publicationList,
                                                                  Long year,
                                                                  String language,
                                                                  String keywords) {
        List<PublicationsDTO> publications = new ArrayList<>();

        for (Publication publication : publicationList) {
            if (filterPublication(publication, year, language, keywords)) {
                continue;
            }

            // get keywords as list
            List<String> containedKeywordsList = new ArrayList<>();
            if (publication.getKeywords() != null) {
                containedKeywordsList = Arrays.asList(publication.getKeywords().split("[,;]"));
            }


            //get the list of authors name and webpageId
            List<Person> authorsList = new ArrayList<>(publication.getAuthors());
            List<PersonOnWebsite> authorsPersons = authorsList.stream()
                    .map(author -> new PersonOnWebsite(author.getFullName(), author.getWebPageId()))
                    .collect(Collectors.toList());


            String title = publication.getTitle();
            LocalDate publicationDate = publication.getPublicationDate();
            PublicationType publicationType = publication.getType();
            String abstractText = publication.getAbstractText();
            String pdfUrl = publication.getPathToDownloadablePDF();
            String doi = publication.getDOI();
            String issn = publication.getISSN();

            publications.add(new PublicationsDTO(title, doi, issn, publicationDate, publicationType, authorsPersons, abstractText, pdfUrl,
                    publication.getMajorLanguage(), containedKeywordsList));
        }

        return publications;
    }

    /**
     * Returns a person entity based on either a person ID or a web page ID.
     * If both are absent, returns null.
     *
     * @param id     an optional parameter for the person ID
     * @param pageId an optional parameter for the person's web page ID
     * @return the Person entity or null if not found
     * @see Person
     */
    public Person getPublicationPerson(Long id, String pageId) {
        Person person = null;
        if (id != null) {
            person = personService.getPersonById(id);
        } else if (pageId != null) {
            person = personService.getPersonByWebPageId(pageId);
        }

        return person;
    }

    /**
     * Returns a research organization entity based on either a research organization ID or acronym.
     * If both are absent, returns null.
     *
     * @param id      an optional parameter for the research organization ID
     * @param acronym an optional parameter for the research organization acronym
     * @return the ResearchOrganization entity or null if not found
     * @see ResearchOrganization
     */
    public ResearchOrganization getPublicationOrganization(Long id, String acronym) {
        Optional<ResearchOrganization> optionalResearchOrganization = Optional.empty();

        if (id != null) {
            optionalResearchOrganization = researchOrganizationService.getResearchOrganizationById(id);
        } else if (acronym != null) {
            optionalResearchOrganization = researchOrganizationService.getResearchOrganizationByAcronym(acronym);
        }

        return optionalResearchOrganization.orElse(null);
    }

    /**
     * Applies filters to the given publication. Might be useful to directly request database (using a custom SQL query)
     *
     * @param publication the publication to filter
     * @param year        the year of publication to filter results. If not provided, all years will be included
     * @param language    the language of publication to filter results. If not provided, all languages will be included
     * @param keywords    a comma-separated list of keywords to filter results. If not provided, all keywords will be included
     * @return true if the publication should be excluded, false otherwise
     */
    public boolean filterPublication(Publication publication, Long year, String language, String keywords) {
        //if there is no filter, return false
        if (year == null && language == null && keywords == null) {
            return false;
        }

        // no publication means that we want to ignore the publication
        if (publication == null) {
            return true;
        }

        // Filter by year
        if (year != null && (publication.getPublicationDate() == null || year.intValue() != publication.getPublicationYear())) {
            return true;
        }

        // Filter by language spoken
        if (language != null) {
            PublicationLanguage spokenLanguage = publication.getMajorLanguage();
            if (!spokenLanguage.equals(PublicationLanguage.valueOfCaseInsensitive(language, PublicationLanguage.OTHER))) {
                return true;
            }
        }

        // Filter by keywords (OR operator)
        if (keywords != null && publication.getKeywords() != null) {
            String[] splitKeywords = keywords.split("[ ,;]");
            boolean found = false;

            for (String keyword : splitKeywords) {
                if (publication.getKeywords().toUpperCase(Locale.ROOT).contains(keyword.toUpperCase(Locale.ROOT))) {
                    found = true;
                    break;
                }
            }

            return !found;
        }

        return false;
    }
}
