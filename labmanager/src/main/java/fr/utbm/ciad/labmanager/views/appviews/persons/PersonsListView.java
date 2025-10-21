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

package fr.utbm.ciad.labmanager.views.appviews.persons;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import fr.utbm.ciad.labmanager.views.components.persons.views.StandardPersonListView;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * List all the persons.
 *
 * @author $Author: sgalland$
 * @author $Author: callaire$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "persons", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class PersonsListView extends StandardPersonListView implements HasDynamicTitle {

    private static final long serialVersionUID = 1616874715478139507L;

    private MenuItem updateRankingsButton;

    private MenuItem switchView;


    /**
     * Constructor.
     *
     * @param personService        the service for accessing the person JPA.
     * @param personEditorFactory  the factory for creating the person editors.
     * @param userService          the service for accessing the user JPA.
     * @param organizationService  the service for accessing to the research organizations.
     * @param membershipService    the service for accessing the membership JPA.
     * @param membershipComparator the comparator that must be used for comparing the memberships. It is assumed that
     *                             the memberships are sorted in reverse chronological order first.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     * @param authenticatedUser    the connected user.
     */
    public PersonsListView(
            @Autowired PersonService personService,
            @Autowired PersonEditorFactory personEditorFactory,
            @Autowired UserService userService,
            @Autowired MembershipService membershipService,
            @Autowired ResearchOrganizationService organizationService,
            @Autowired ChronoMembershipComparator membershipComparator,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory) {
        super(personService, userService, membershipService, organizationService, membershipComparator,
                (ps, query, filters) -> ps.getAllPersons(query, filters),
                personEditorFactory, authenticatedUser, messages, loggerFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.list_title.all"); //$NON-NLS-1$
    }

    @Override
    protected MenuBar createMenuBar() {
        var menu = super.createMenuBar();
        if (menu == null) {
            menu = new MenuBar();
            menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        }
        this.updateRankingsButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SYNC_ALT_SOLID, null, null, it -> openRankingsUpdateWizard());
        this.switchView = ComponentFactory.addIconItem(menu, LineAwesomeIcon.ID_CARD_SOLID, getTranslation("views.persons.switch_to_card_view"), null, it -> getUI().ifPresent(ui -> ui.navigate(PersonsCardView.class))); //$NON-NLS-1$
        return menu;
    }

    /**
     * Open the wizard for updating the person rankings.
     */
    protected void openRankingsUpdateWizard() {
        final var selection = getGrid().getSelectedItems();
        if (selection != null && !selection.isEmpty()) {
            final var identifiers = AbstractLabManagerWizard.buildQueryParameters(selection);
            getUI().ifPresent(ui -> ui.navigate(PersonRankingUpdaterWizard.class, identifiers));
        } else {
            getUI().ifPresent(ui -> ui.navigate(PersonRankingUpdaterWizard.class));
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        if (this.updateRankingsButton != null) {
            ComponentFactory.setIconItemText(this.updateRankingsButton, getTranslation("views.persons.updateRankings")); //$NON-NLS-1$
        }
        if (this.switchView != null) {
            ComponentFactory.setIconItemText(this.switchView, getTranslation("views.persons.switch_to_card_view")); //$NON-NLS-1$
        }
    }

}
