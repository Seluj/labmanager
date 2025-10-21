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

import com.google.common.base.Strings;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractMultiEntityNameField;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation of a field for entering the names of persons, with auto-completion from the person JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class MultiPersonNameField extends AbstractMultiEntityNameField<Person> {

    private static final long serialVersionUID = 7107742910744624635L;

    private final PersonNameParser nameParser;

    /**
     * Constructor.
     *
     * @param personService             the service for accessing the person JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param initializer               the initializer of the loaded persons. It may be {@code null}.
     */
    public MultiPersonNameField(
            PersonService personService,
            SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
            SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback,
            Consumer<Person> initializer) {
        super(
                combo -> {
                    combo.setRenderer(new ComponentRenderer<>(ComponentFactory::newPersonAvatar));
                    combo.setItemLabelGenerator(it -> it.getFullName());
                },
                combo -> {
                    if (initializer == null) {
                        combo.setItems(query -> personService.getAllPersons(
                                VaadinSpringDataHelpers.toSpringPageRequest(query),
                                createPersonFilter(query.getFilter())).stream());
                    } else {
                        combo.setItems(query -> personService.getAllPersons(
                                VaadinSpringDataHelpers.toSpringPageRequest(query),
                                createPersonFilter(query.getFilter()),
                                initializer).stream());
                    }
                },
                creationWithUiCallback,
                creationWithoutUiCallback);
        this.nameParser = personService.getNameParser();
    }

    /**
     * Constructor.
     *
     * @param personService             the service for accessing the person JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new person using an UI, e.g., an editor. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new person without using an UI. The first argument is the new person entity.
     *                                  The second argument is a lambda that must be invoked to inject the new person in the {@code SinglePersonNameField}.
     *                                  This second lambda takes the created person.
     */
    public MultiPersonNameField(PersonService personService, SerializableBiConsumer<Person, Consumer<Person>> creationWithUiCallback,
                                SerializableBiConsumer<Person, Consumer<Person>> creationWithoutUiCallback) {
        this(personService, creationWithUiCallback, creationWithoutUiCallback, null);
    }

    /**
     * Constructor.
     *
     * @param personService       the service for accessing the person JPA entities.
     * @param personEditorFactory the factory for creating the person editors.
     * @param authenticatedUser   the user that is currently authenticated.
     * @param creationTitle       the title of the dialog box for creating the person.
     * @param logger              the logger for abnormal messages to the lab manager administrator.
     * @param initializer         the initializer of the loaded persons. It may be {@code null}.
     */
    public MultiPersonNameField(PersonService personService, PersonEditorFactory personEditorFactory, AuthenticatedUser authenticatedUser,
                                String creationTitle, Logger logger, Consumer<Person> initializer) {
        this(personService,
                (newPerson, saver) -> {
                    final var editor = personEditorFactory.createAdditionEditor(newPerson, logger);
                    ComponentFactory.openEditionModalDialog(creationTitle, editor, true,
                            (dialog, changedPerson) -> saver.accept(changedPerson),
                            null);
                },
                (newPerson, saver) -> {
                    try {
                        final var creationContext = personService.startEditing(newPerson, logger);
                        creationContext.save();
                        saver.accept(creationContext.getEntity());
                    } catch (Throwable ex) {
                        logger.warn("Error when creating a person: " + ex.getLocalizedMessage() + "\n-> " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
                        ComponentFactory.showErrorNotification(personService.getMessageSourceAccessor().getMessage("views.persons.creation_error", new Object[]{ex.getLocalizedMessage()})); //$NON-NLS-1$
                    }
                },
                initializer);
    }

    /**
     * Constructor.
     *
     * @param personService       the service for accessing the person JPA entities.
     * @param personEditorFactory the factory for creating the person editors.
     * @param authenticatedUser   the user that is currently authenticated.
     * @param creationTitle       the title of the dialog box for creating the person.
     * @param logger              the logger for abnormal messages to the lab manager administrator.
     */
    public MultiPersonNameField(PersonService personService, PersonEditorFactory personEditorFactory, AuthenticatedUser authenticatedUser,
                                String creationTitle, Logger logger) {
        this(personService, personEditorFactory, authenticatedUser, creationTitle, logger, null);
    }

    private static Specification<Person> createPersonFilter(Optional<String> filter) {
        if (filter.isPresent()) {
            return (root, query, criteriaBuilder) ->
                    ComponentFactory.newPredicateContainsOneOf(filter.get(), root, query, criteriaBuilder,
                            (keyword, predicates, root0, criteriaBuilder0) -> {
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), keyword)); //$NON-NLS-1$
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), keyword)); //$NON-NLS-1$
                            });
        }
        return null;
    }

    @Override
    protected Person createNewEntity(String customName) {
        final String firstName;
        final String lastName;
        if (!Strings.isNullOrEmpty(customName)) {
            final var parser = this.nameParser;
            firstName = parser.parseFirstName(customName);
            lastName = parser.parseLastName(customName);
        } else {
            firstName = null;
            lastName = null;
        }

        final var newPerson = new Person();
        newPerson.setFirstName(firstName);
        newPerson.setLastName(lastName);
        return newPerson;
    }

}
