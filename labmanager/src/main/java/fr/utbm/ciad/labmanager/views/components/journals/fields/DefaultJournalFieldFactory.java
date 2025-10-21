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

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the journals.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultJournalFieldFactory implements JournalFieldFactory {

    private final JournalService journalService;

    private final JournalEditorFactory journalEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor.
     *
     * @param journalService       the service for accessing the journal JPA entities.
     * @param journalEditorFactory factory for creating the journal editors.
     * @param authenticatedUser    the user that is currently authenticated.
     */
    public DefaultJournalFieldFactory(JournalService journalService, JournalEditorFactory journalEditorFactory, AuthenticatedUser authenticatedUser) {
        this.journalService = journalService;
        this.journalEditorFactory = journalEditorFactory;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public SingleJournalNameField createSingleNameField(
            SerializableBiConsumer<Journal, Consumer<Journal>> creationWithUiCallback,
            SerializableBiConsumer<Journal, Consumer<Journal>> creationWithoutUiCallback) {
        return new SingleJournalNameField(this.journalService, creationWithUiCallback, creationWithoutUiCallback);
    }

    @Override
    public SingleJournalNameField createSingleNameField(String creationTitle, Logger logger) {
        return new SingleJournalNameField(this.journalService, this.journalEditorFactory, this.authenticatedUser,
                creationTitle, logger);
    }

}
