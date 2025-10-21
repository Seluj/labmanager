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

package fr.utbm.ciad.labmanager.views.appviews.culture;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.culture.views.StandardCultureDisseminationListView;
import fr.utbm.ciad.labmanager.views.components.publications.editors.PublicationEditorFactory;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * List all the actions for scientific culture dissemination.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "scientificCultureActions", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class ScientificCultureActionsListView extends StandardCultureDisseminationListView implements HasDynamicTitle {

    private static final long serialVersionUID = 1664385539083649671L;

    /**
     * Constructor.
     *
     * @param authenticatedUser        the connected user.
     * @param messages                 the accessor to the localized messages (spring layer).
     * @param loggerFactory            the factory to be used for the composite logger.
     * @param publicationService       the service for accessing the scientific publications.
     * @param publicationEditorFactory the factory for creating publication editors.
     * @param journalService           the service for accessing the JPA entities for journal.
     * @param organizationService      the service for accessing the JPA entities for research organization.
     */
    public ScientificCultureActionsListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired PublicationService publicationService,
            @Autowired PublicationEditorFactory publicationEditorFactory,
            @Autowired JournalService journalService,
            @Autowired ResearchOrganizationService organizationService) {
        super(authenticatedUser, messages, loggerFactory, publicationService, publicationEditorFactory, journalService, organizationService);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.scientific_culture_actions.list"); //$NON-NLS-1$
    }

}
