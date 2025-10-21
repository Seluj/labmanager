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

package fr.utbm.ciad.labmanager.views.components.assocstructures.editors.regular;

import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.assocstructures.fields.AssociatedStructureFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.projects.editors.ProjectEditorFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of associated structure information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited associated structure.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedAssociatedStructureEditor extends AbstractAssociatedStructureEditor {

    private static final long serialVersionUID = -1397381071432537857L;

    /**
     * Constructor.
     *
     * @param context                         the context for editing the entity.
     * @param structureCreationStatusComputer the tool for computer the creation status for the associated structures.
     * @param structureFieldFactory           the factory for creating the associated structure fields.
     * @param projectService                  the service for accessing the JPA entities for projects.
     * @param projectEditorFactory            the factory for creating the project editors.
     * @param organizationService             the service for accessing the JPA entities for research organizations.
     * @param personService                   the service for accessing the JPA entities for persons.
     * @param authenticatedUser               the connected user.
     * @param organizationFieldFactory        the factory for creating the organization fields.
     * @param messages                        the accessor to the localized messages (Spring layer).
     */
    public EmbeddedAssociatedStructureEditor(EntityEditingContext<AssociatedStructure> context,
                                             EntityCreationStatusComputer<AssociatedStructure> structureCreationStatusComputer,
                                             AssociatedStructureFieldFactory structureFieldFactory,
                                             ProjectService projectService, ProjectEditorFactory projectEditorFactory,
                                             ResearchOrganizationService organizationService,
                                             AuthenticatedUser authenticatedUser, OrganizationFieldFactory organizationFieldFactory,
                                             MessageSourceAccessor messages) {
        super(context, structureCreationStatusComputer, false, structureFieldFactory, projectService, projectEditorFactory, organizationService,
                authenticatedUser, organizationFieldFactory, messages, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
