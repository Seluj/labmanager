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

package fr.utbm.ciad.labmanager.services.scientificaxis;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.project.ProjectRepository;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationRepository;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service for scientific axes.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.5
 */
@Service
public class ScientificAxisService extends AbstractEntityService<ScientificAxis> {

    private static final long serialVersionUID = -873452182614369429L;

    private final ScientificAxisRepository scientificAxisRepository;

    private final PublicationRepository publicationRepository;

    private final MembershipRepository membershipRepository;

    private final ProjectRepository projectRepository;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param scientificAxisRepository the repository for accessing the scientific axes.
     * @param publicationRepository    the repository for accessing the publications.
     * @param membershipRepository     the repository for accessing the memberships.
     * @param projectRepository        the repository for accessing the projects.
     * @param messages                 the provider of localized messages.
     * @param constants                the accessor to the live constants.
     * @param sessionFactory           the Hibernate session factory.
     */
    public ScientificAxisService(
            @Autowired ScientificAxisRepository scientificAxisRepository,
            @Autowired PublicationRepository publicationRepository,
            @Autowired MembershipRepository membershipRepository,
            @Autowired ProjectRepository projectRepository,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(messages, constants, sessionFactory);
        this.scientificAxisRepository = scientificAxisRepository;
        this.publicationRepository = publicationRepository;
        this.membershipRepository = membershipRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Replies the list of all the scientific axes.
     *
     * @return the list of all the scientific axes.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public List<ScientificAxis> getAllScientificAxes() {
        return this.scientificAxisRepository.findAll();
    }

    /**
     * Replies the list of all the scientific axes.
     *
     * @param pageable the manager of pages.
     * @param filter   the filter of the axes.
     * @return the list of all the scientific axes.
     * @since 4.0
     */
    public Page<ScientificAxis> getAllScientificAxes(Pageable pageable, Specification<ScientificAxis> filter) {
        return this.scientificAxisRepository.findAll(filter, pageable);
    }

    /**
     * Replies the scientific axis that has the given identifier.
     *
     * @param id the identifier of the axis.
     * @return the axis with the gien identifier, or {@code null} if there is no axis with this id.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public ScientificAxis getScientificAxisById(long id) {
        return this.scientificAxisRepository.findById(Long.valueOf(id)).orElse(null);
    }

    /**
     * Delete the scientific axis with the given identifier.
     *
     * @param identifier the identifier of the axis to be deleted.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    @Transactional
    public void removeScientificAxis(long identifier) {
        final var id = Long.valueOf(identifier);
        final var axisOpt = this.scientificAxisRepository.findById(id);
        if (axisOpt.isPresent()) {
            final var axis = axisOpt.get();
            //
            axis.setProjects(null);
            this.scientificAxisRepository.deleteById(id);
        }
    }

    /**
     * Create a scientific axis.
     *
     * @param validated    indicates if the axis is validated by a local authority.
     * @param acronym      the short name of acronym of the scientific axis.
     * @param name         the name of the scientific axis.
     * @param startDate    the start date of the scientific axis in format {@code YYY-MM-DD}.
     * @param endDate      the start date of the scientific axis in format {@code YYY-MM-DD}, or {@code null} if none.
     * @param projects     the list of associated projects.
     * @param publications the list of associated publications.
     * @param memberships  the list of associated memberships.
     * @return the reference to the created axis.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public Optional<ScientificAxis> createScientificAxis(boolean validated, String acronym, String name,
                                                         LocalDate startDate, LocalDate endDate, List<Project> projects, List<Publication> publications,
                                                         List<Membership> memberships) {
        final var axis = new ScientificAxis();
        try {
            updateScientificAxis(axis, validated, acronym, name, startDate, endDate, projects, publications, memberships);
        } catch (Throwable ex) {
            // Delete created axis
            if (axis.getId() != 0) {
                try {
                    removeScientificAxis(axis.getId());
                } catch (Throwable ex0) {
                    // Silent
                }
            }
            //getLogger().error(ex.getLocalizedMessage(), ex);
            throw ex;
        }
        return Optional.of(axis);
    }

    /**
     * Update a scientific axis.
     *
     * @param axisId       the identifier of the scientific axis to be updated.
     * @param validated    indicates if the axis is validated by a local authority.
     * @param acronym      the short name of acronym of the scientific axis.
     * @param name         the name of the scientific axis.
     * @param startDate    the start date of the scientific axis in format {@code YYY-MM-DD}.
     * @param endDate      the start date of the scientific axis in format {@code YYY-MM-DD}, or {@code null} if none.
     * @param projects     the list of associated projects.
     * @param publications the list of associated publications.
     * @param memberships  the list of associated memberships.
     * @return the reference to the created axis.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public Optional<ScientificAxis> updateScientificAxis(long axisId, boolean validated, String acronym,
                                                         String name, LocalDate startDate, LocalDate endDate, List<Project> projects,
                                                         List<Publication> publications, List<Membership> memberships) {
        final Optional<ScientificAxis> res;
        if (axisId >= 0) {
            res = this.scientificAxisRepository.findById(Long.valueOf(axisId));
        } else {
            res = Optional.empty();
        }
        if (res.isPresent()) {
            updateScientificAxis(res.get(), validated, acronym, name, startDate, endDate, projects, publications, memberships);
        }
        return res;
    }

    /**
     * Update a scientific axis.
     *
     * @param axis         the scientific axis to be updated.
     * @param validated    indicates if the axis is validated by a local authority.
     * @param acronym      the short name of acronym of the scientific axis.
     * @param name         the name of the scientific axis.
     * @param startDate    the start date of the scientific axis in format {@code YYY-MM-DD}.
     * @param endDate      the start date of the scientific axis in format {@code YYY-MM-DD}, or {@code null} if none.
     * @param projects     the list of associated projects.
     * @param publications the list of associated publications.
     * @param memberships  the list of associated memberships.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    protected void updateScientificAxis(ScientificAxis axis, boolean validated, String acronym,
                                        String name, LocalDate startDate, LocalDate endDate, List<Project> projects,
                                        List<Publication> publications, List<Membership> memberships) {
        axis.setValidated(validated);
        axis.setAcronym(acronym);
        axis.setName(name);
        axis.setStartDate(startDate);
        axis.setEndDate(endDate);
        this.scientificAxisRepository.save(axis);

        axis.setProjects(projects);
        this.scientificAxisRepository.save(axis);

        updateMemberships(axis, memberships);
        updatePublications(axis, publications);
        updateProjects(axis, projects);
    }

    @Deprecated(since = "4.0", forRemoval = true)
    private void updateMemberships(ScientificAxis axis, List<Membership> memberships) {
        axis.setMemberships(memberships);
        for (final var mbr : memberships) {
            mbr.getScientificAxes().add(axis);
        }
        this.membershipRepository.saveAll(memberships);
    }

    @Deprecated(since = "4.0", forRemoval = true)
    private void updatePublications(ScientificAxis axis, List<Publication> publications) {
        axis.setPublications(publications);
        for (final var pub : publications) {
            pub.getScientificAxes().add(axis);
        }
        this.publicationRepository.saveAll(publications);
    }

    @Deprecated(since = "4.0", forRemoval = true)
    private void updateProjects(ScientificAxis axis, List<Project> projects) {
        axis.setProjects(projects);
        for (final var prj : projects) {
            prj.getScientificAxes().add(axis);
        }
        this.projectRepository.saveAll(projects);
    }

    /**
     * Replies the scientific axes that have the given identifiers.
     *
     * @param identifiers the identifiers.
     * @return the axes.
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public List<ScientificAxis> getScientificAxesFor(List<Long> identifiers) {
        final var axes = this.scientificAxisRepository.findAllById(identifiers);
        if (axes.size() != identifiers.size()) {
            throw new IllegalArgumentException("Scientific axis not found"); //$NON-NLS-1$
        }
        return axes;
    }

    @Override
    public EntityEditingContext<ScientificAxis> startEditing(ScientificAxis axis, Logger logger) {
        assert axis != null;
        return new EditingContext(axis, logger);
    }

    @Override
    public EntityDeletingContext<ScientificAxis> startDeletion(Set<ScientificAxis> axes, Logger logger) {
        assert axes != null && !axes.isEmpty();
        // Force loading of the linked entities
        inSession(session -> {
            for (final var axis : axes) {
                if (axis.getId() != 0L) {
                    session.load(axis, Long.valueOf(axis.getId()));
                    Hibernate.initialize(axis.getMemberships());
                    Hibernate.initialize(axis.getProjects());
                    Hibernate.initialize(axis.getPublications());
                }
            }
        });
        return new DeletingContext(axes, logger);
    }

    /**
     * Context for editing a {@link ScientificAxis}.
     * This context is usually defined when the entity is associated to
     * external resources in the server file system.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class EditingContext extends AbstractEntityEditingContext<ScientificAxis> {

        private static final long serialVersionUID = 3560524631927901367L;

        /**
         * Constructor.
         *
         * @param axis   the edited scientific axis.
         * @param logger the logger to be used.
         */
        EditingContext(ScientificAxis axis, Logger logger) {
            super(axis, logger);
        }

        @Override
        public void save(HasAsynchronousUploadService... components) throws IOException {
            this.entity = ScientificAxisService.this.scientificAxisRepository.save(this.entity);
        }

        @Override
        public EntityDeletingContext<ScientificAxis> createDeletionContext() {
            return ScientificAxisService.this.startDeletion(Collections.singleton(this.entity), getLogger());
        }

    }

    /**
     * Context for deleting a {@link ScientificAxis}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class DeletingContext extends AbstractEntityDeletingContext<ScientificAxis> {

        private static final long serialVersionUID = 5797259507045624690L;

        /**
         * Constructor.
         *
         * @param axes   the scientific axes to delete.
         * @param logger the logger to be used.
         */
        protected DeletingContext(Set<ScientificAxis> axes, Logger logger) {
            super(axes, logger);
        }

        @Override
        protected DeletionStatus computeDeletionStatus() {
            for (final var entity : getEntities()) {
                if (!entity.getPublications().isEmpty()) {
                    return ScientificAxisDeletionStatus.PUBLICATION;
                }
                if (!entity.getProjects().isEmpty()) {
                    return ScientificAxisDeletionStatus.PROJECT;
                }
                if (!entity.getMemberships().isEmpty()) {
                    return ScientificAxisDeletionStatus.MEMBERSHIP;
                }
            }
            return DeletionStatus.OK;
        }

        @Override
        protected void deleteEntities(Collection<Long> identifiers) throws Exception {
            ScientificAxisService.this.scientificAxisRepository.deleteAllById(identifiers);
        }

    }

}
