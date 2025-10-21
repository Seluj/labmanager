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

package fr.utbm.ciad.labmanager.views.components.organizations.editors.wizard;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.AddressEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of organization information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited organization. This editor is a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedOrganizationEditorWizard extends AbstractOrganizationEditorWizard {

    // FIXME: Update this wizard to be consistent and bug free

    private static final long serialVersionUID = -5119278327562827799L;

    /**
     * Constructor.
     *
     * @param context                            the context for editing the entity.
     * @param organizationCreationStatusComputer the tool for computer the creation status for the research organizations.
     * @param fileManager                        the manager of files at the server-side.
     * @param authenticatedUser                  the connected user.
     * @param messages                           the accessor to the localized messages (Spring layer).
     * @param organizationService                the service for accessing the organizations.
     * @param addressService                     the service for accessing the organization addresses.
     * @param organizationEditorFactory          the factory of the organization editor.
     * @param addressEditorFactory               the factory of the organization address editor.
     */
    public EmbeddedOrganizationEditorWizard(EntityEditingContext<ResearchOrganization> context,
                                            EntityCreationStatusComputer<ResearchOrganization> organizationCreationStatusComputer,
                                            DownloadableFileManager fileManager,
                                            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                            ResearchOrganizationService organizationService,
                                            OrganizationAddressService addressService,
                                            OrganizationEditorFactory organizationEditorFactory,
                                            AddressEditorFactory addressEditorFactory) {
        super(context, organizationCreationStatusComputer,
                false, fileManager, authenticatedUser, messages, organizationService, addressService,
                organizationEditorFactory, addressEditorFactory, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
