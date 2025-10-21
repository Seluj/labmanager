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
import fr.utbm.ciad.labmanager.services.user.UserService.UserEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import org.slf4j.Logger;

/**
 * Factory that is providing a person editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface PersonEditorFactory {

    /**
     * Replies the editing context for the given person.
     *
     * @param person the person to be edited.
     * @param logger the logger to be associated to the context.
     * @return the editing context.
     */
    UserEditingContext createUserContextFor(Person person, Logger logger);

    /**
     * Create an editor that may be used for creating a new person.
     *
     * @param userContext the editing context for the user.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Person> createAdditionEditor(UserEditingContext userContext);

    /**
     * Create an editor that may be used for creating a new person.
     *
     * @param person the person to be edited.
     * @param logger the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Person> createAdditionEditor(Person person, Logger logger) {
        final var userContext = createUserContextFor(person, logger);
        return createAdditionEditor(userContext);
    }

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param userContext the editing context for the user.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Person> createUpdateEditor(UserEditingContext userContext);

    /**
     * Create an editor that may be used for updating an existing person.
     *
     * @param person the person to be edited.
     * @param logger the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Person> createUpdateEditor(Person person, Logger logger) {
        final var userContext = createUserContextFor(person, logger);
        return createUpdateEditor(userContext);
    }

}
