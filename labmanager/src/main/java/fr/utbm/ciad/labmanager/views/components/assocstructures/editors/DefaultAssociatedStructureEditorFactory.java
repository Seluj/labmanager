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

package fr.utbm.ciad.labmanager.views.components.assocstructures.editors;

import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.assostructure.AssociatedStructureService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.assocstructures.editors.regular.EmbeddedAssociatedStructureEditor;
import fr.utbm.ciad.labmanager.views.components.assocstructures.fields.AssociatedStructureFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.projects.editors.ProjectEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a publication editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultAssociatedStructureEditorFactory implements AssociatedStructureEditorFactory {

    private final AssociatedStructureService structureService;

    private final ProjectService projectService;

    private final ProjectEditorFactory projectEditorFactory;

    private final AssociatedStructureFieldFactory structureFieldFactory;

    private final ResearchOrganizationService organizationService;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructors.
     *
     * @param structureService         the service for accessing the associated structure entities.
     * @param projectService           the service for accessing the JPA entities for projects.
     * @param projectEditorFactory     the factory for creating the project editors.
     * @param structureFieldFactory    the factory for creating the associated structure fields.
     * @param organizationService      the service for accessing the JPA entities for research organizations.
     * @param organizationFieldFactory the factory for creating the organization fields.
     * @param authenticatedUser        the connected user.
     * @param messages                 the accessor to the localized messages (Spring layer).
     */
    public DefaultAssociatedStructureEditorFactory(
            @Autowired AssociatedStructureService structureService,
            @Autowired ProjectService projectService,
            @Autowired ProjectEditorFactory projectEditorFactory,
            @Autowired AssociatedStructureFieldFactory structureFieldFactory,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired OrganizationFieldFactory organizationFieldFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.structureService = structureService;
        this.projectService = projectService;
        this.projectEditorFactory = projectEditorFactory;
        this.structureFieldFactory = structureFieldFactory;
        this.organizationService = organizationService;
        this.organizationFieldFactory = organizationFieldFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<AssociatedStructure> createContextFor(AssociatedStructure structure, Logger logger) {
        return this.structureService.startEditing(structure, logger);
    }

    @Override
    public AbstractEntityEditor<AssociatedStructure> createAdditionEditor(EntityEditingContext<AssociatedStructure> context) {
        return new EmbeddedAssociatedStructureEditor(context,
                null,
                this.structureFieldFactory,
                this.projectService, this.projectEditorFactory,
                this.organizationService, this.authenticatedUser,
                this.organizationFieldFactory, this.messages);
    }

    @Override
    public AbstractEntityEditor<AssociatedStructure> createUpdateEditor(EntityEditingContext<AssociatedStructure> context) {
        return new EmbeddedAssociatedStructureEditor(context,
                null,
                this.structureFieldFactory,
                this.projectService, this.projectEditorFactory,
                this.organizationService, this.authenticatedUser,
                this.organizationFieldFactory, this.messages);
    }

}
