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

package fr.utbm.ciad.labmanager.views.appviews.organizations;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.AddressEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.views.StandardAddressListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * List all the addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "addresses", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class AddressesListView extends StandardAddressListView implements HasDynamicTitle {

    private static final long serialVersionUID = 7405006673440187284L;

    /**
     * Constructor.
     *
     * @param authenticatedUser    the connected user.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     * @param addressService       the service for accessing the addresses.
     * @param addressEditorFactory the factory for creating the address editors.
     */
    public AddressesListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired OrganizationAddressService addressService,
            @Autowired AddressEditorFactory addressEditorFactory) {
        super(authenticatedUser, messages, loggerFactory, addressService, addressEditorFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.organizations.addresses.list"); //$NON-NLS-1$
    }

}
