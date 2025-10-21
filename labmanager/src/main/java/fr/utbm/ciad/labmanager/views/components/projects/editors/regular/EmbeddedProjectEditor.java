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

package fr.utbm.ciad.labmanager.views.components.projects.editors.regular;

import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.projects.fields.ProjectFieldFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of project information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited project.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedProjectEditor extends AbstractProjectEditor {

    private static final long serialVersionUID = 1553551688880862732L;

    /**
     * Constructor.
     *
     * @param context                       the context for editing the entity.
     * @param projectCreationStatusComputer the tool for computer the creation status for the scientific projects.
     * @param projectFieldFactory           the factory for creating the project fields.
     * @param axisEditorFactory             the factory for creating the scientific axis editors.
     * @param organizationService           the service for accessing the JPA entities for research organizations.
     * @param addressService                the service for accessing the JPA entities for organization addresses.
     * @param axisService                   the service for accessing the JPA entities for scientific axes.
     * @param axisEditorFactory             the factory for creating the scientific axis editors.
     * @param organizationEditorFactory     the factory for creating the organization editors.
     * @param organizationFieldFactory      the factory for creating the organization fields.
     * @param fileManager                   the manager of files at the server-side.
     * @param authenticatedUser             the connected user.
     * @param messages                      the accessor to the localized messages (Spring layer).
     * @since 4.0
     */
    public EmbeddedProjectEditor(EntityEditingContext<Project> context,
                                 EntityCreationStatusComputer<Project> projectCreationStatusComputer,
                                 ProjectFieldFactory projectFieldFactory,
                                 ResearchOrganizationService organizationService, OrganizationAddressService addressService,
                                 ScientificAxisService axisService, ScientificAxisEditorFactory axisEditorFactory,
                                 OrganizationEditorFactory organizationEditorFactory,
                                 OrganizationFieldFactory organizationFieldFactory,
                                 DownloadableFileManager fileManager, AuthenticatedUser authenticatedUser,
                                 MessageSourceAccessor messages) {
        super(context, projectCreationStatusComputer, false, projectFieldFactory, organizationService, addressService,
                axisService, axisEditorFactory, organizationEditorFactory, organizationFieldFactory,
                fileManager, authenticatedUser, messages, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
