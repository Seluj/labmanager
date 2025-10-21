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

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.services.user.UserService.UserEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.AbstractPersonEditor;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.PersonCreationStatusComputer;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Collections;

/**
 * Enable to edit the personal information for the user.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "myprofile", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public final class MyProfileView extends AbstractPersonEditor implements HasDynamicTitle {

    private static final long serialVersionUID = 738063190104767506L;

    private Button saveButton;

    private Button validateButton;

    private MenuItem updateRankingsButton;

    /**
     * Constructor.
     *
     * @param personService                the service to access to the person JPA.
     * @param personCreationStatusComputer the tool for computer the creation status for the persons.
     * @param userService                  the service to access to the user JPA.
     * @param authenticatedUser            the connected user.
     * @param messages                     the accessor to the localized messages (spring layer)
     * @param loggerFactory                the factory of the loggers.
     */
    public MyProfileView(@Autowired PersonService personService,
                         @Autowired PersonCreationStatusComputer personCreationStatusComputer,
                         @Autowired UserService userService,
                         @Autowired AuthenticatedUser authenticatedUser,
                         @Autowired MessageSourceAccessor messages,
                         @Autowired ContextualLoggerFactory loggerFactory) {
        super(
                createEditingContext(personService, userService, authenticatedUser, loggerFactory),
                personCreationStatusComputer, personService, true, authenticatedUser,
                messages, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

    private static UserEditingContext createEditingContext(
            PersonService personService, UserService userService,
            AuthenticatedUser authenticatedUser, ContextualLoggerFactory loggerFactory) {
        final var user = authenticatedUser.get().get();
        final var person = user.getPerson();
        final var staticLogger = loggerFactory.getLogger(MyProfileView.class.getName(),
                AuthenticatedUser.getUserName(authenticatedUser));
        return userService.startEditing(user, personService.startEditing(person, staticLogger));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.editor_title.user"); //$NON-NLS-1$
    }

    @Override
    protected Details createIndexesComponents(VerticalLayout receiver) {
        final var details = super.createIndexesComponents(receiver);

        final var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        details.addComponentAsFirst(menu);

        this.updateRankingsButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SYNC_ALT_SOLID, null, null, it -> openRankingsUpdateWizard());

        return details;
    }

    /**
     * Open the wizard for updating the person rankings.
     */
    private void openRankingsUpdateWizard() {
        final var identifiers = AbstractLabManagerWizard.buildQueryParameters(Collections.singletonList(getEditedEntity()), MyProfileView.class);
        getUI().ifPresent(ui -> ui.navigate(PersonRankingUpdaterWizard.class, identifiers));
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        super.createEditorContent(rootContainer);

        rootContainer.setSpacing(true);

        final var bar = new HorizontalLayout();
        rootContainer.add(bar);

        this.validateButton = null;
        if (isBaseAdmin()) {
            this.validateButton = new Button("", event -> { //$NON-NLS-1$
                validateByOrganizationalStructureManager();
                if (isValidData()) {
                    save();
                } else {
                    notifyInvalidity();
                }
            });
            this.validateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            this.validateButton.setIcon(LineAwesomeIcon.CHECK_DOUBLE_SOLID.create());
            bar.add(this.validateButton);
        }

        this.saveButton = new Button("", event -> { //$NON-NLS-1$
            if (isValidData()) {
                save();
                MainLayout.refreshReporting();
            } else {
                notifyInvalidity();
            }
        });
        this.saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.saveButton.addClickShortcut(Key.ENTER);
        this.saveButton.setIcon(LineAwesomeIcon.SAVE_SOLID.create());
        bar.add(this.saveButton);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        ComponentFactory.setIconItemText(this.updateRankingsButton, getTranslation("views.persons.updateRankings")); //$NON-NLS-1$
        this.saveButton.setText(getTranslation("views.save")); //$NON-NLS-1$
        this.validateButton.setText(getTranslation("views.validate")); //$NON-NLS-1$
    }

}
