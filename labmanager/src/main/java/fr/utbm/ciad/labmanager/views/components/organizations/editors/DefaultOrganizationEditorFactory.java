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

package fr.utbm.ciad.labmanager.views.components.organizations.editors;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.AddressEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.regular.EmbeddedOrganizationEditor;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.regular.OrganizationCreationStatusComputer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing an organization editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultOrganizationEditorFactory implements OrganizationEditorFactory {

    private final OrganizationCreationStatusComputer organizationCreationStatusComputer;

    private final DownloadableFileManager fileManager;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    private final ResearchOrganizationService organizationService;

    private final OrganizationAddressService addressService;

    private final AddressEditorFactory addressEditorFactory;

    /**
     * Constructor.
     *
     * @param organizationCreationStatusComputer the tool for computer the creation status for the research organizations.
     * @param fileManager                        the manager of files at the server-side.
     * @param authenticatedUser                  the connected user.
     * @param messages                           the accessor to the localized messages (Spring layer).
     * @param organizationService                the service for accessing the organizations.
     * @param addressService                     the service for accessing the organization addresses.
     * @param addressEditorFactory               the factory for creating the address editors.
     */
    public DefaultOrganizationEditorFactory(
            @Autowired OrganizationCreationStatusComputer organizationCreationStatusComputer,
            @Autowired DownloadableFileManager fileManager,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired OrganizationAddressService addressService,
            @Autowired AddressEditorFactory addressEditorFactory) {
        this.organizationCreationStatusComputer = organizationCreationStatusComputer;
        this.fileManager = fileManager;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
        this.organizationService = organizationService;
        this.addressService = addressService;
        this.addressEditorFactory = addressEditorFactory;
    }

    @Override
    public EntityEditingContext<ResearchOrganization> createContextFor(ResearchOrganization organization, Logger logger) {
        return this.organizationService.startEditing(organization, logger);
    }

    @Override
    public AbstractEntityEditor<ResearchOrganization> createAdditionEditor(EntityEditingContext<ResearchOrganization> context) {
        return new EmbeddedOrganizationEditor(context, this.organizationCreationStatusComputer, this.fileManager, this.authenticatedUser, this.messages,
                this.organizationService, this.addressService, this, this.addressEditorFactory);
    }

    @Override
    public AbstractEntityEditor<ResearchOrganization> createUpdateEditor(EntityEditingContext<ResearchOrganization> context) {
        return new EmbeddedOrganizationEditor(context, this.organizationCreationStatusComputer, this.fileManager, this.authenticatedUser, this.messages,
                this.organizationService, this.addressService, this, this.addressEditorFactory);
    }

}
