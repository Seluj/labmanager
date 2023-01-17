/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.repository.project;

import java.util.List;

import fr.ciadlab.labmanager.entities.project.Project;
import fr.ciadlab.labmanager.entities.project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** JPA repository for project declaration.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
public interface ProjectRepository extends JpaRepository<Project, Integer> {

	/** Replies all the projects that match the different organization identifiers.
	 * The identifier is compared to, the coordinator, the local organization,
	 * or the super organization.
	 *
	 * @param id the identifier for the organization.
	 * @return the list of projects.
	 */
	@Query("SELECT DISTINCT p FROM Project p WHERE p.coordinator.id = :id OR p.localOrganization.id = :id OR p.superOrganization.id = :id")
	List<Project> findDistinctOrganizationProjects(@Param("id") Integer id);

	/** Replies all the projects that match the organization identifier, the confidential
	 * flag and the project status.
	 * The identifier is compared to, the coordinator, the local organization,
	 * or the list of other partners.
	 *
	 * @param id the identifier for the organization.
	 * @param confidential indicates the expected confidentiality flag for the projects.
	 * @param status indicates the expected status of the projects
	 * @return the list of projects.
	 */
	@Query("SELECT DISTINCT p FROM Project p WHERE (p.coordinator.id = :id OR p.localOrganization.id = :id OR p.superOrganization.id = :id) AND p.confidential = :confidential AND p.status = :status")
	List<Project> findDistinctOrganizationProjects(
			@Param("confidential") Boolean confidential, @Param("status") ProjectStatus status, @Param("id") Integer id);

	/** Replies all the projects that match the person identifier.
	 * The identifier is compared to the participant list, the local organization.
	 *
	 * @param id the identifier for the person.
	 * @return the list of projects.
	 */
	@Query("SELECT DISTINCT p FROM Project p, ProjectMember m WHERE m.person.id = :id AND m MEMBER OF p.participants")
	List<Project> findDistinctPersonProjects(@Param("id") Integer id);

	/** Replies all the projects that match the person identifier, the confidential
	 * flag and the project status.
	 * The identifier is compared to the participant list, the local organization.
	 *
	 * @param confidential indicates the expected confidentiality flag for the projects.
	 * @param status indicates the expected status of the projects
	 * @param id the identifier for the person.
	 * @return the list of projects.
	 */
	@Query("SELECT DISTINCT p FROM Project p, ProjectMember m WHERE m.person.id = :id AND m MEMBER OF p.participants AND p.confidential = :confidential AND p.status = :status")
	List<Project> findDistinctPersonProjects(
			@Param("confidential") Boolean confidential, @Param("status") ProjectStatus status, @Param("id") Integer id);

	/** Replies all the projects according to their confidentiality and status.
	 *
	 * @param confidential indicates the expected confidentiality flag for the projects.
	 * @param status indicates the expected status of the projects
	 * @return the list of projects.
	 */
	List<Project> findDistinctByConfidentialAndStatus(Boolean confidential, ProjectStatus status);

}
