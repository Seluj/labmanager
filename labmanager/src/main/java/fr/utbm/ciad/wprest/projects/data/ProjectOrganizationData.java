package fr.utbm.ciad.wprest.projects.data;

import java.util.List;

/**
 * Describes the organization of a project
 *
 * @param superOrganization - the super organization name
 * @param learOrganization  - the lear organization name
 * @param localOrganization - the local organization name
 * @param partners          - the list of partners names
 */
public record ProjectOrganizationData(String superOrganization,
                                      String learOrganization,
                                      String localOrganization,
                                      List<String> partners) {
}
