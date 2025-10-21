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

package fr.utbm.ciad.labmanager.views.components.conferences.fields;

import com.google.common.base.Strings;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractSingleEntityNameField;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation of a field for entering the name of a conference, with auto-completion from the conference JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class SingleConferenceNameField extends AbstractSingleEntityNameField<Conference> {

    private static final long serialVersionUID = -4401817861161260961L;

    /**
     * Constructor.
     *
     * @param conferenceService         the service for accessing the conference JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new conference using an UI, e.g., an editor. The first argument is the new conference entity.
     *                                  The second argument is a lambda that must be invoked to inject the new conference in the {@code SingleConferenceNameField}.
     *                                  This second lambda takes the created conference.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new conference without using an UI. The first argument is the new conference entity.
     *                                  The second argument is a lambda that must be invoked to inject the new conference in the {@code SingleConferenceNameField}.
     *                                  This second lambda takes the created conference.
     */
    public SingleConferenceNameField(ConferenceService conferenceService, SerializableBiConsumer<Conference, Consumer<Conference>> creationWithUiCallback,
                                     SerializableBiConsumer<Conference, Consumer<Conference>> creationWithoutUiCallback) {
        super(
                combo -> {
                    combo.setRenderer(new ComponentRenderer<>(ComponentFactory::newConferenceAvatar));
                    combo.setItemLabelGenerator(it -> it.getAcronymAndName());
                },
                combo -> {
                    combo.setItems(query ->
                            conferenceService.getAllConferences(
                                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                                    createConferenceFilter(query.getFilter())).stream());
                },
                creationWithUiCallback, creationWithoutUiCallback);
    }

    /**
     * Constructor.
     *
     * @param conferenceService       the service for accessing the conference JPA entities.
     * @param conferenceEditorFactory the factory for creating the conference editors.
     * @param authenticatedUser       the user that is currently authenticated.
     * @param creationTitle           the title of the dialog box for creating the person.
     * @param logger                  the logger for abnormal messages to the lab manager administrator.
     */
    public SingleConferenceNameField(ConferenceService conferenceService, ConferenceEditorFactory conferenceEditorFactory, AuthenticatedUser authenticatedUser, String creationTitle, Logger logger) {
        this(conferenceService,
                (newConference, saver) -> {
                    final var editor = conferenceEditorFactory.createAdditionEditor(newConference, logger);
                    ComponentFactory.openEditionModalDialog(creationTitle, editor, true,
                            (dialog, changedConference) -> saver.accept(changedConference),
                            null);
                },
                (newConference, saver) -> {
                    try {
                        final var creationContext = conferenceService.startEditing(newConference, logger);
                        creationContext.save();
                        saver.accept(creationContext.getEntity());
                    } catch (Throwable ex) {
                        logger.warn("Error when creating a conference: " + ex.getLocalizedMessage() + "\n-> " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
                        ComponentFactory.showErrorNotification(conferenceService.getMessageSourceAccessor().getMessage("views.conferences.creation_error", new Object[]{ex.getLocalizedMessage()})); //$NON-NLS-1$
                    }
                });
    }

    private static Specification<Conference> createConferenceFilter(Optional<String> filter) {
        if (filter.isPresent()) {
            return (root, query, criteriaBuilder) ->
                    ComponentFactory.newPredicateContainsOneOf(filter.get(), root, query, criteriaBuilder,
                            (keyword, predicates, root0, criteriaBuilder0) -> {
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keyword)); //$NON-NLS-1$
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword)); //$NON-NLS-1$
                            });
        }
        return null;
    }

    @Override
    protected Conference createNewEntity(String customName) {
        final var newConference = new Conference();

        if (!Strings.isNullOrEmpty(customName)) {
            final var parts = customName.split("\\s*" + EntityConstants.ACRONYM_NAME_SEPARATOR + "\\s*", 2); //$NON-NLS-1$ //$NON-NLS-2$
            if (parts.length > 1) {
                newConference.setAcronym(parts[0]);
                newConference.setName(parts[1]);
            } else {
                newConference.setName(customName);
            }
        }

        return newConference;
    }

}
