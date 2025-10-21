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

package fr.utbm.ciad.labmanager.views.appviews.journals;

import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import fr.utbm.ciad.labmanager.views.components.journals.views.StandardJournalListView;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * List all the journals.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "journals", layout = MainLayout.class)
@PermitAll
public class JournalsListView extends StandardJournalListView implements HasDynamicTitle {

    private static final long serialVersionUID = 4004385277053773899L;

    private MenuItem updateRankingsButton;

    /**
     * Constructor.
     *
     * @param authenticatedUser    the connected user.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     * @param journalService       the service for accessing the journals.
     * @param journalEditorFactory the factory for creating journal editors.
     */
    public JournalsListView(
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages,
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired JournalService journalService,
            @Autowired JournalEditorFactory journalEditorFactory) {
        super(authenticatedUser, messages, loggerFactory, journalService, journalEditorFactory);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.journals.journals.list"); //$NON-NLS-1$
    }

    @Override
    protected MenuBar createMenuBar() {
        var menu = super.createMenuBar();
        if (menu == null) {
            menu = new MenuBar();
            menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        }

        this.updateRankingsButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SYNC_ALT_SOLID, null, null, it -> openRankingsUpdateWizard());

        return menu;
    }

    /**
     * Open the wizard for updating the journal rankings.
     */
    protected void openRankingsUpdateWizard() {
        final var selection = getGrid().getSelectedItems();
        if (selection != null && !selection.isEmpty()) {
            final var identifiers = AbstractLabManagerWizard.buildQueryParameters(selection);
            getUI().ifPresent(ui -> ui.navigate(JournalRankingUpdaterWizard.class, identifiers));
        } else {
            getUI().ifPresent(ui -> ui.navigate(JournalRankingUpdaterWizard.class));
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        if (this.updateRankingsButton != null) {
            ComponentFactory.setIconItemText(this.updateRankingsButton, getTranslation("views.journals.updateRankings")); //$NON-NLS-1$
        }
    }

}
