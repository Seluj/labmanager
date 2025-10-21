package fr.utbm.ciad.wprest.projects;

import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.project.ProjectMember;
import fr.utbm.ciad.labmanager.data.project.ProjectStatus;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.wprest.data.DateRange;
import fr.utbm.ciad.wprest.data.PersonOnWebsite;
import fr.utbm.ciad.wprest.projects.data.ProjectLinksData;
import fr.utbm.ciad.wprest.projects.data.ProjectOrganizationData;
import fr.utbm.ciad.wprest.projects.data.ProjectParticipantData;
import fr.utbm.ciad.wprest.projects.data.dto.ProjectDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing project-related operations.
 *
 * <p>This controller provides endpoints for accessing and manipulating
 * project-related data, including retrieving information about organizations,
 * participants and links related to a project.</p>
 *
 * <p>Base URL: /api/v{majorVersion}/projects</p>
 *
 * <p>This class is annotated with {@link RestController} and handles
 * HTTP requests mapped to the /api/v{majorVersion}/persons endpoint.
 * The version of the API is determined by the constant
 * {@link Constants#MANAGER_MAJOR_VERSION}.</p>
 */
@Transactional
@RestController
@RequestMapping("/api/v" + Constants.MANAGER_MAJOR_VERSION + "/projects")
public class ProjectRestService {

    ProjectService projectService;

    public ProjectRestService(@Autowired ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Get the project with the given id
     *
     * @param id the id of the project
     * @return the data project data
     */
    @Operation(summary = "Gets a project by its id",
            description = "Gets the project with the given id",
            tags = {"Project API"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "The project data",
                            content = @Content(schema = @Schema(implementation = ProjectDataDto.class))),
                    @ApiResponse(responseCode = "404", description = "The project does not exist"),
            })
    @GetMapping
    public ResponseEntity<ProjectDataDto> getProject(
            @RequestParam Long id
    ) {
        Project p = projectService.getProjectById(id);

        if (p == null) {
            return ResponseEntity.notFound().build();
        }

        // cannot fetch a project if it is not public.
        if (p.isConfidential() || p.getStatus() != ProjectStatus.ACCEPTED) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(getProjectData(p));
    }

    /**
     * Get all public projects
     *
     * @return the whole list of projects
     */
    @Operation(summary = "Gets all public projects",
            description = "Gets the whole list of public projects",
            tags = {"Project API"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "The list of public projects",
                            content = @Content(schema = @Schema(implementation = ProjectDataDto.class), array = @ArraySchema(schema = @Schema(implementation = ProjectDataDto.class)))),
                    @ApiResponse(responseCode = "404", description = "No project found"),
            })

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDataDto>> getAllProjects(

    ) {
        List<Project> projects = projectService.getAllProjects();
        return getPublicProjectsFromList(projects);
    }


    /**
     * Get all public projects data from a given list
     *
     * @param projects - the list of projects to extract data from
     * @return - a list of the data associated to each project
     */
    public ResponseEntity<List<ProjectDataDto>> getPublicProjectsFromList(List<Project> projects) {
        List<ProjectDataDto> projectsDtos = projects.stream()
                .filter(project -> !project.isConfidential() && project.getStatus() == ProjectStatus.ACCEPTED)
                .map(this::getProjectData)
                .collect(Collectors.toList());

        return ResponseEntity.ok(projectsDtos);
    }

    /**
     * Get the different organizations names
     *
     * @param project - the project
     * @return - the organization data
     */
    public ProjectOrganizationData getProjectOrganizationData(Project project) {
        ResearchOrganization superOrganization = project.getSuperOrganization();
        String superOrganizationName = superOrganization.getName();
        String learOrganizationName = project.getLearOrganization().getName();
        String localOrganizationName = project.getLocalOrganization().getName();

        List<ResearchOrganization> partners = project.getOtherPartners();
        List<String> partnersNames = new ArrayList<>();
        partners.forEach(partner -> partnersNames.add(partner.getName()));

        return new ProjectOrganizationData(superOrganizationName, learOrganizationName, localOrganizationName, partnersNames);
    }


    /**
     * Gets the information related to the page of a project
     *
     * @param project - the project to get information from.
     * @return - the project information DTO
     */
    public ProjectDataDto getProjectData(Project project) {
        String acronym = project.getAcronym();
        String title = project.getScientificTitle();
        String description = project.getDescription();

        DateRange date = getProjectDates(project);

        ProjectOrganizationData organizationData = getProjectOrganizationData(project);
        List<ProjectParticipantData> participantsData = getParticipantsData(project);

        List<String> images = project.getPathsToImages();
        String logo = project.getPathToLogo();

        ProjectLinksData links = getProjectLinksData(project);

        String webpageId = null;
        if (project.getWebPageURI() != null) {
            webpageId = project.getWebPageURI().toString();
        }

        boolean openSource = project.isOpenSource();
        boolean isDone = date.endDate() != null && (LocalDate.now().isAfter(date.endDate()));

        return new ProjectDataDto(acronym, title, description, date, organizationData, participantsData,
                images, logo, links, webpageId, openSource, isDone);
    }


    /**
     * Returns the date range of the project.
     *
     * @param project the project to get the date range from
     * @return the date range of the project, or null if the project is null
     */
    public DateRange getProjectDates(Project project) {
        if (project == null) {
            return null;
        }

        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();
        return new DateRange(startDate, endDate);
    }

    /**
     * Returns the links associated to a project.
     *
     * @param project the project to get the links from
     * @return the links of the project, or null if the project is null
     */
    public ProjectLinksData getProjectLinksData(Project project) {
        if (project == null) {
            return null;
        }

        String projectURL = project.getProjectURL();
        List<String> videosURL = new ArrayList<>(project.getVideoURLs());

        List<ResearchOrganization> partners = project.getOtherPartners();
        Map<String, String> partnersLinks = new HashMap<>();
        partners.forEach(partner -> partnersLinks.put(partner.getName(), partner.getOrganizationURL()));

        return new ProjectLinksData(projectURL, videosURL, partnersLinks);
    }

    /**
     * Returns the participants of the project.
     *
     * @param project the project to get the participants from
     * @return the list of participants, or an empty list if the project is null
     */
    public List<ProjectParticipantData> getParticipantsData(Project project) {
        if (project == null) {
            return new ArrayList<>();
        }

        List<ProjectMember> participants = project.getParticipants();
        List<ProjectParticipantData> participantsData = new ArrayList<>();

        for (ProjectMember participant : participants) {
            String name = participant.getPerson().getFullName();
            String id = participant.getPerson().getWebPageId();
            PersonOnWebsite person = new PersonOnWebsite(name, id);

            participantsData.add(new ProjectParticipantData(person, participant.getRole()));
        }

        return participantsData;
    }
}
