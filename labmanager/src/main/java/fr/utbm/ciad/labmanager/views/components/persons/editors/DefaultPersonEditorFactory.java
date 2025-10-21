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

package fr.utbm.ciad.labmanager.views.components.persons.editors;

import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.services.user.UserService.UserEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.EmbeddedPersonEditor;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.PersonCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.persons.editors.wizard.EmbeddedPersonEditorWizard;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a person editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultPersonEditorFactory implements PersonEditorFactory {

    private final PersonCreationStatusComputer personCreationStatusComputer;

    private final PersonService personService;

    private final UserService userService;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param personCreationStatusComputer the tool for computer the creation status for the persons.
     * @param personService                the service for accessing to the person entities.
     * @param userService                  the service for accessing to the user entities.
     * @param authenticatedUser            the connected user.
     * @param messages                     the accessor to the localized messages (Spring layer).
     */
    public DefaultPersonEditorFactory(
            @Autowired PersonCreationStatusComputer personCreationStatusComputer,
            @Autowired PersonService personService,
            @Autowired UserService userService,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.personCreationStatusComputer = personCreationStatusComputer;
        this.personService = personService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public UserEditingContext createUserContextFor(Person person, Logger logger) {
        final var personContext = this.personService.startEditing(person, logger);
        final var user = this.userService.getUserFor(person);
        final var userContext = this.userService.startEditing(user, personContext);
        return userContext;
    }

    @Override
    public AbstractEntityEditor<Person> createAdditionEditor(UserEditingContext userContext) {
        return new EmbeddedPersonEditorWizard(userContext, this.personCreationStatusComputer, this.authenticatedUser, this.messages, this.personService);
    }

    @Override
    public AbstractEntityEditor<Person> createUpdateEditor(UserEditingContext userContext) {
        return new EmbeddedPersonEditor(userContext, this.personCreationStatusComputer, this.personService, this.authenticatedUser, this.messages);
    }

}
