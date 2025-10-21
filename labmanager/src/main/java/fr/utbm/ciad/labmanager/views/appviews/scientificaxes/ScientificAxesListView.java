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

package fr.utbm.ciad.labmanager.views.appviews.scientificaxes;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.views.StandardScientificAxisListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * List all the scientific axes.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "scientificaxes", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class ScientificAxesListView extends StandardScientificAxisListView implements HasDynamicTitle {

    private static final long serialVersionUID = 4918450030888881030L;

    /**
     * Constructor.
     *
     * @param authenticatedUser the connected user.
     * @param messages          the accessor to the localized messages (spring layer).
     * @param loggerFactory     the factory to be used for the composite logger.
     * @param axisService       the service for accessing the scientific axes.
     * @param axisEditorFactory the factory for creating the scientific axis editors.
     */
    public ScientificAxesListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired ScientificAxisService axisService,
            @Autowired ScientificAxisEditorFactory axisEditorFactory) {
        super(authenticatedUser, messages, loggerFactory, axisService, axisEditorFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.scientific_axes.axes.list"); //$NON-NLS-1$
    }

}
