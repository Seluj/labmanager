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
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DefaultPersonFieldFactory implements PersonFieldFactory {

    private final PersonService personService;

    private final PersonEditorFactory personEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor.
     *
     * @param personService       the service for accessing the person JPA entities.
     * @param personEditorFactory the factory for creating the person editors.
     * @param authenticatedUser   the user that is currently authenticated.
     */
    public DefaultPersonFieldFactory(@Autowired PersonService personService,
                                     @Autowired PersonEditorFactory personEditorFactory,
                                     @Autowired AuthenticatedUser authenticatedUser) {
        this.personService = personService;
        this.personEditorFactory = personEditorFactory;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public SinglePersonNameField createSingleNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                                       SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback) {
        return new SinglePersonNameField(this.personService, creationWithUiCallback, creationWithoutUiCallback);
    }

    @Override
    public SinglePersonNameField createSingleNameField(String creationTitle, Logger logger) {
        return new SinglePersonNameField(this.personService, this.personEditorFactory, this.authenticatedUser, creationTitle, logger);
    }

    @Override
    public MultiPersonNameField createMultiNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                                     SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback, Consumer<Person> initializer) {
        return new MultiPersonNameField(this.personService, creationWithUiCallback, creationWithoutUiCallback, initializer);
    }

    @Override
    public MultiPersonNameField createMultiNameField(SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                                     SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback) {
        return new MultiPersonNameField(this.personService, creationWithUiCallback, creationWithoutUiCallback);
    }

    @Override
    public MultiPersonNameField createMultiNameField(String creationTitle, Logger logger, Consumer<Person> initializer) {
        return new MultiPersonNameField(this.personService, this.personEditorFactory, this.authenticatedUser, creationTitle, logger, initializer);
    }

    @Override
    public MultiPersonNameField createMultiNameField(String creationTitle, Logger logger) {
        return new MultiPersonNameField(this.personService, this.personEditorFactory, this.authenticatedUser, creationTitle, logger);
    }

}
