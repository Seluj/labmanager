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

package fr.utbm.ciad.labmanager.views.components.persons.fields;

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.member.Person;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the persons.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface PersonFieldFactory {

    /**
     * Create a field for entering the name of a single person.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @return the field, never {@code null}.
     */
    SinglePersonNameField createSingleNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                                SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback);

    /**
     * Create a field for entering the name of a single person.
     *
     * @param creationTitle the title of the dialog box for creating the person.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @return the field, never {@code null}.
     */
    SinglePersonNameField createSingleNameField(String creationTitle, Logger logger);

    /**
     * Create a field for entering the names of multiple persons.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param initializer               the initializer of the loaded persons. It may be {@code null}.
     * @return the field, never {@code null}.
     */
    MultiPersonNameField createMultiNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                              SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback, Consumer<Person> initializer);

    /**
     * Create a field for entering the names of multiple persons.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @return the field, never {@code null}.
     */
    MultiPersonNameField createMultiNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                              SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback);

    /**
     * Create a field for entering the names of multiple persons.
     *
     * @param creationTitle the title of the dialog box for creating the person.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @param initializer   the initializer of the loaded persons. It may be {@code null}.
     * @return the field, never {@code null}.
     */
    MultiPersonNameField createMultiNameField(String creationTitle, Logger logger, Consumer<Person> initializer);

    /**
     * Create a field for entering the names of multiple persons.
     *
     * @param creationTitle the title of the dialog box for creating the person.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @return the field, never {@code null}.
     */
    MultiPersonNameField createMultiNameField(String creationTitle, Logger logger);

}
