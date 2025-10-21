package fr.utbm.ciad.wprest.projects.data.dto;

import fr.utbm.ciad.wprest.data.DateRange;
import fr.utbm.ciad.wprest.projects.data.ProjectLinksData;
import fr.utbm.ciad.wprest.projects.data.ProjectOrganizationData;
import fr.utbm.ciad.wprest.projects.data.ProjectParticipantData;

import java.util.List;

/**
 * Describes the information related to the page of a project
 *
 * @param acronym       - the acronym of the project
 * @param title         - the title of the project
 * @param description   - the description of the project
 * @param date          - the date of the project (start and end)
 * @param organizations - the organizations of the project
 * @param participants  - the participants of the project
 * @param images        - the images URIs of the project
 * @param logo          - the logo URI
 * @param links         - the links of the project
 * @param webpageId     - the webpageId (webpage URL)
 * @param openSource    - true if the project is open source
 * @param isDone        - true if the project is done
 */
public record ProjectDataDto(String acronym,
                             String title,
                             String description,
                             DateRange date,
                             ProjectOrganizationData organizations,
                             List<ProjectParticipantData> participants,
                             List<String> images,
                             String logo,
                             ProjectLinksData links,
                             String webpageId,
                             boolean openSource,
                             boolean isDone) {
}
