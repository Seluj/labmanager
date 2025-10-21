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

package fr.utbm.ciad.labmanager.views.appviews.jurys;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.jury.JuryMembershipService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.jurys.editors.JuryMembershipEditorFactory;
import fr.utbm.ciad.labmanager.views.components.jurys.views.StandardJuryMembershipListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Enable to edit all the jurys in which the persons are involved.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "jurys", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class JuryMembershipsListView extends StandardJuryMembershipListView implements HasDynamicTitle {

    private static final long serialVersionUID = -3613659589555069224L;

    /**
     * Constructor.
     *
     * @param authenticatedUser           the connected user.
     * @param messages                    the accessor to the localized messages (spring layer).
     * @param loggerFactory               the factory to be used for the composite logger.
     * @param membershipService           the service for accessing the jury memberships.
     * @param juryMembershipEditorFactory the factory for creating the jury membership editors.
     */
    public JuryMembershipsListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired JuryMembershipService membershipService,
            @Autowired JuryMembershipEditorFactory juryMembershipEditorFactory) {
        super(authenticatedUser, messages, loggerFactory, membershipService, juryMembershipEditorFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.jury_membership.memberships.list"); //$NON-NLS-1$
    }

}
