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

package fr.utbm.ciad.labmanager.views.components.projects.editors;

import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.projects.editors.regular.EmbeddedProjectEditor;
import fr.utbm.ciad.labmanager.views.components.projects.fields.ProjectFieldFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a project editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultProjectEditorFactory implements ProjectEditorFactory {

    private final DownloadableFileManager fileManager;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    private final ResearchOrganizationService organizationService;

    private final OrganizationEditorFactory organizationEditorFactory;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final OrganizationAddressService addressService;

    private final ScientificAxisService axisService;

    private final ScientificAxisEditorFactory axisEditorFactory;

    private final ProjectService projectService;

    private final ProjectFieldFactory projectFieldFactory;

    /**
     * Constructor.
     *
     * @param projectService            the service for accessing the JPA entities for projects.
     * @param organizationService       the service for accessing the JPA entities for research organizations.
     * @param addressService            the service for accessing the JPA entities for organization addresses.
     * @param axisService               the service for accessing the JPA entities for scientific axes.
     * @param axisEditorFactory         the factory for creating the scientific axis editors.
     * @param organizationEditorFactory the factory for creating the organization editors.
     * @param organizationFieldFactory  the factory for creating the organization fields.
     * @param projectFieldFactory       the factory for creating the project fields.
     * @param fileManager               the manager of files at the server-side.
     * @param authenticatedUser         the connected user.
     * @param messages                  the accessor to the localized messages (Spring layer).
     */
    public DefaultProjectEditorFactory(
            @Autowired ProjectService projectService,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired OrganizationAddressService addressService,
            @Autowired ScientificAxisService axisService,
            @Autowired ScientificAxisEditorFactory axisEditorFactory,
            @Autowired OrganizationEditorFactory organizationEditorFactory,
            @Autowired OrganizationFieldFactory organizationFieldFactory,
            @Autowired ProjectFieldFactory projectFieldFactory,
            @Autowired DownloadableFileManager fileManager,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.projectService = projectService;
        this.fileManager = fileManager;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
        this.organizationService = organizationService;
        this.organizationEditorFactory = organizationEditorFactory;
        this.organizationFieldFactory = organizationFieldFactory;
        this.addressService = addressService;
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;
        this.projectFieldFactory = projectFieldFactory;
    }

    @Override
    public EntityEditingContext<Project> createContextFor(Project project, Logger logger) {
        return this.projectService.startEditing(project, logger);
    }

    @Override
    public AbstractEntityEditor<Project> createAdditionEditor(EntityEditingContext<Project> context) {
        return new EmbeddedProjectEditor(context, null, this.projectFieldFactory,
                this.organizationService, this.addressService, this.axisService,
                this.axisEditorFactory, this.organizationEditorFactory, this.organizationFieldFactory,
                this.fileManager, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<Project> createUpdateEditor(EntityEditingContext<Project> context) {
        return new EmbeddedProjectEditor(context, null, this.projectFieldFactory,
                this.organizationService, this.addressService, this.axisService,
                this.axisEditorFactory, this.organizationEditorFactory, this.organizationFieldFactory,
                this.fileManager, this.authenticatedUser, this.messages);
    }

}
