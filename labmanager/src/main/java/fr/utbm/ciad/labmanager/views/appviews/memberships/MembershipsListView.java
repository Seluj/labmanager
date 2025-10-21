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

package fr.utbm.ciad.labmanager.views.appviews.memberships;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.memberships.editors.MembershipEditorFactory;
import fr.utbm.ciad.labmanager.views.components.memberships.views.StandardMembershipListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Enable to edit the memberships for a person.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "positions", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class MembershipsListView extends StandardMembershipListView implements HasDynamicTitle {

    private static final long serialVersionUID = -557163925957008625L;

    /**
     * Constructor.
     *
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (spring layer).
     * @param loggerFactory           the factory to be used for the composite logger.
     * @param membershipService       the service for accessing the memberships.
     * @param membershipEditorFactory the factory for creating the person membership editors.
     * @param organizationService     the service for accessing the JPA entities for research organizations.
     */
    public MembershipsListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired MembershipService membershipService,
            @Autowired MembershipEditorFactory membershipEditorFactory,
            @Autowired ResearchOrganizationService organizationService) {
        super(authenticatedUser, messages, loggerFactory, membershipService, membershipEditorFactory, organizationService);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.membership.memberships.list"); //$NON-NLS-1$
    }

}
