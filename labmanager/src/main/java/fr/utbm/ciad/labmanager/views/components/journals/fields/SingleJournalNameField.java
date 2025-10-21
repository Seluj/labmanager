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

package fr.utbm.ciad.labmanager.views.components.journals.fields;

import com.google.common.base.Strings;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractSingleEntityNameField;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation of a field for entering the name of a journal, with auto-completion from the conference JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class SingleJournalNameField extends AbstractSingleEntityNameField<Journal> {

    private static final long serialVersionUID = 7585597661849638144L;

    /**
     * Constructor.
     *
     * @param journalService            the service for accessing the journal JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new journal using an UI, e.g., an editor. The first argument is the new journal entity.
     *                                  The second argument is a lambda that must be invoked to inject the new journal in the {@code SingleJournalNameField}.
     *                                  This second lambda takes the created journal.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new journal without using an UI. The first argument is the new journal entity.
     *                                  The second argument is a lambda that must be invoked to inject the new journal in the {@code SingleJournalNameField}.
     *                                  This second lambda takes the created journal.
     */
    public SingleJournalNameField(JournalService journalService,
                                  SerializableBiConsumer<Journal, Consumer<Journal>> creationWithUiCallback,
                                  SerializableBiConsumer<Journal, Consumer<Journal>> creationWithoutUiCallback) {
        super(
                combo -> {
                    combo.setRenderer(new ComponentRenderer<>(ComponentFactory::newJournalAvatar));
                    combo.setItemLabelGenerator(it -> {
                        final var buffer = new StringBuilder();
                        buffer.append(it.getJournalName());
                        final var publisher = it.getPublisher();
                        if (!Strings.isNullOrEmpty(publisher)) {
                            buffer.append(" - ").append(publisher); //$NON-NLS-1$
                        }
                        return buffer.toString();
                    });
                },
                combo -> {
                    combo.setItems(query ->
                            journalService.getAllJournals(
                                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                                    createJournalFilter(query.getFilter())).stream());
                },
                creationWithUiCallback, creationWithoutUiCallback);
    }

    /**
     * Constructor.
     *
     * @param journalService       the service for accessing the journal JPA entities.
     * @param journalEditorFactory factory for creating the journal editors.
     * @param authenticatedUser    the user that is currently authenticated.
     * @param creationTitle        the title of the dialog box for creating the journal.
     * @param logger               the logger for abnormal messages to the lab manager administrator.
     */
    public SingleJournalNameField(JournalService journalService, JournalEditorFactory journalEditorFactory,
                                  AuthenticatedUser authenticatedUser, String creationTitle, Logger logger) {
        this(journalService,
                (newJournal, saver) -> {
                    final var editor = journalEditorFactory.createAdditionEditor(newJournal, logger);
                    ComponentFactory.openEditionModalDialog(creationTitle, editor, true,
                            (dialog, changedJournal) -> saver.accept(changedJournal),
                            null);
                },
                (newJournal, saver) -> {
                    try {
                        final var creationContext = journalService.startEditing(newJournal, logger);
                        creationContext.save();
                        saver.accept(creationContext.getEntity());
                    } catch (Throwable ex) {
                        logger.warn("Error when creating a journal: " + ex.getLocalizedMessage() + "\n-> " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
                        ComponentFactory.showErrorNotification(journalService.getMessageSourceAccessor().getMessage("views.journals.creation_error", new Object[]{ex.getLocalizedMessage()})); //$NON-NLS-1$
                    }
                });
    }

    private static Specification<Journal> createJournalFilter(Optional<String> filter) {
        if (filter.isPresent()) {
            return (root, query, criteriaBuilder) ->
                    ComponentFactory.newPredicateContainsOneOf(filter.get(), root, query, criteriaBuilder,
                            (keyword, predicates, root0, criteriaBuilder0) -> {
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("journalName")), keyword)); //$NON-NLS-1$
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher")), keyword)); //$NON-NLS-1$
                            });
        }
        return null;
    }

    @Override
    protected Journal createNewEntity(String customName) {
        final var newJournal = new Journal();

        if (!Strings.isNullOrEmpty(customName)) {
            final var parts = customName.split("\\s*" + EntityConstants.ACRONYM_NAME_SEPARATOR + "\\s*", 2); //$NON-NLS-1$ //$NON-NLS-2$
            if (parts.length > 1) {
                newJournal.setJournalName(parts[0]);
                newJournal.setPublisher(parts[1]);
            } else {
                newJournal.setJournalName(customName);
            }
        }

        return newJournal;
    }

}
