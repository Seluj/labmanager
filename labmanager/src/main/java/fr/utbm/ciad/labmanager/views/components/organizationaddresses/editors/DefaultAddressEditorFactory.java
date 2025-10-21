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

package fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors;

import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.regular.EmbeddedAddressEditor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing an address editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultAddressEditorFactory implements AddressEditorFactory {

    private final OrganizationAddressService addressService;

    private final DownloadableFileManager fileManager;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructors.
     *
     * @param addressService    the service for accessing the JPA entities of the organization addresses.
     * @param fileManager       the manager of files at the server-side.
     * @param authenticatedUser the connected user.
     * @param messages          the accessor to the localized messages (Spring layer).
     */
    public DefaultAddressEditorFactory(
            @Autowired OrganizationAddressService addressService,
            @Autowired DownloadableFileManager fileManager,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.addressService = addressService;
        this.fileManager = fileManager;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<OrganizationAddress> createContextFor(OrganizationAddress address, Logger logger) {
        return this.addressService.startEditing(address, logger);
    }

    @Override
    public AbstractEntityEditor<OrganizationAddress> createAdditionEditor(EntityEditingContext<OrganizationAddress> context) {
        return new EmbeddedAddressEditor(context, null, this.fileManager, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<OrganizationAddress> createUpdateEditor(EntityEditingContext<OrganizationAddress> context) {
        return new EmbeddedAddressEditor(context, null, this.fileManager, this.authenticatedUser, this.messages);
    }

}
