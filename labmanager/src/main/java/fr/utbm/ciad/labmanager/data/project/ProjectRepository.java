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

package fr.utbm.ciad.labmanager.data.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA repository for project declaration.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    /**
     * Replies all the projects that match the different organization identifiers.
     * The identifier is compared to, the coordinator, the local organization,
     * or the super organization.
     *
     * @param id the identifier for the organization.
     * @return the list of projects.
     */
    @Query("SELECT DISTINCT p FROM Project p WHERE p.coordinator.id = :id OR p.localOrganization.id = :id OR p.superOrganization.id = :id")
    List<Project> findDistinctOrganizationProjects(@Param("id") Long id);

    /**
     * Replies all the projects that match the organization identifier, the confidential
     * flag and the project status.
     * The identifier is compared to, the coordinator, the local organization,
     * or the list of other partners.
     *
     * @param id           the identifier for the organization.
     * @param confidential indicates the expected confidentiality flag for the projects.
     * @param status       indicates the expected status of the projects
     * @return the list of projects.
     */
    @Query("SELECT DISTINCT p FROM Project p WHERE (p.coordinator.id = :id OR p.localOrganization.id = :id OR p.superOrganization.id = :id) AND p.confidential = :confidential AND p.status = :status")
    List<Project> findDistinctOrganizationProjects(
            @Param("confidential") Boolean confidential, @Param("status") ProjectStatus status, @Param("id") Long id);

    /**
     * Replies all the projects that match the person identifier.
     * The identifier is compared to the participant list, the local organization.
     *
     * @param id the identifier for the person.
     * @return the list of projects.
     */
    @Query("SELECT DISTINCT p FROM Project p, ProjectMember m WHERE m.person.id = :id AND m MEMBER OF p.participants")
    Set<Project> findDistinctPersonProjects(@Param("id") Long id);

    /**
     * Replies all the projects that match the person identifier, the confidential
     * flag and the project status.
     * The identifier is compared to the participant list, the local organization.
     *
     * @param confidential indicates the expected confidentiality flag for the projects.
     * @param status       indicates the expected status of the projects
     * @param id           the identifier for the person.
     * @return the list of projects.
     */
    @Query("SELECT DISTINCT p FROM Project p, ProjectMember m WHERE m.person.id = :id AND m MEMBER OF p.participants AND p.confidential = :confidential AND p.status = :status")
    List<Project> findDistinctPersonProjects(
            @Param("confidential") Boolean confidential, @Param("status") ProjectStatus status, @Param("id") Long id);

    /**
     * Replies all the projects according to their confidentiality and status.
     *
     * @param confidential indicates the expected confidentiality flag for the projects.
     * @param status       indicates the expected status of the projects
     * @return the list of projects.
     */
    List<Project> findDistinctByConfidentialAndStatus(Boolean confidential, ProjectStatus status);

    /**
     * Find a project with the given acronym or title. This function is case sensitive.
     *
     * @param acronym         the acronym to search for.
     * @param scientificTitle the title to search for.
     * @return the project.
     * @since 3.6
     */
    Optional<Project> findDistinctByAcronymOrScientificTitle(String acronym, String scientificTitle);

    /**
     * Replies all projects that have a logo that can be displayed on the public projects page.
     *
     * @return the list of projects.
     * @since 4.0
     */
    List<Project> findByPathToLogoIsNotNull();

    /**
     * Replies all projects that have a project website id.
     *
     * @return the list of projects.
     * @since 4.0
     */
    List<Project> findByWebPageNamingIsNotNull();
}
