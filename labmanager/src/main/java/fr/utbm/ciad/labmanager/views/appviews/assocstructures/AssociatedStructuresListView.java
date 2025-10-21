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

package fr.utbm.ciad.labmanager.views.appviews.assocstructures;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.assostructure.AssociatedStructureService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.assocstructures.editors.AssociatedStructureEditorFactory;
import fr.utbm.ciad.labmanager.views.components.assocstructures.views.StandardAssociatedStructureListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * List all the associated structures.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "assocstructures", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class AssociatedStructuresListView extends StandardAssociatedStructureListView implements HasDynamicTitle {

    private static final long serialVersionUID = -2694471588623830169L;

    /**
     * Constructor.
     *
     * @param authenticatedUser                the connected user.
     * @param messages                         the accessor to the localized messages (spring layer).
     * @param loggerFactory                    the factory to be used for the composite logger.
     * @param structureService                 the service for accessing the associated structures.
     * @param associatedStructureEditorFactory the factory for creating the associated-structure editors.
     */
    public AssociatedStructuresListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired AssociatedStructureService structureService,
            @Autowired AssociatedStructureEditorFactory associatedStructureEditorFactory) {
        super(authenticatedUser, messages, loggerFactory, structureService, associatedStructureEditorFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.associated_structure.structures.list"); //$NON-NLS-1$
    }

}
