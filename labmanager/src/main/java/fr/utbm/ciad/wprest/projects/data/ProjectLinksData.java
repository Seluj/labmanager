package fr.utbm.ciad.wprest.projects.data;

import java.util.List;
import java.util.Map;

/**
 * Describes the links associated to a project
 *
 * @param projectUrl    - the project url on the website
 * @param videoLinks    - the list of videos linked to the project
 * @param partnersLinks - a map which maps each partner name to its url
 */
public record ProjectLinksData(String projectUrl,
                               List<String> videoLinks,
                               Map<String, String> partnersLinks) {
}
