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

package fr.utbm.ciad.labmanager.views.components.journals.editors;

import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import org.slf4j.Logger;

/**
 * Factory that is providing a journal editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface JournalEditorFactory {

    /**
     * Replies the editing context for the given journal.
     *
     * @param journal the journal to be edited.
     * @param logger  the logger to be associated to the context.
     * @return the editing context.
     */
    EntityEditingContext<Journal> createContextFor(Journal journal, Logger logger);

    /**
     * Create an editor that may be used for creating a new journal.
     *
     * @param context the editing context for the conference.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Journal> createAdditionEditor(EntityEditingContext<Journal> context);

    /**
     * Create an editor that may be used for creating a new journal.
     *
     * @param journal the journal to be edited.
     * @param logger  the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Journal> createAdditionEditor(Journal journal, Logger logger) {
        final var context = createContextFor(journal, logger);
        return createAdditionEditor(context);
    }

    /**
     * Create an editor that may be used for updating an existing journal.
     *
     * @param context the editing context for the conference.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Journal> createUpdateEditor(EntityEditingContext<Journal> context);

    /**
     * Create an editor that may be used for updating an existing journal.
     *
     * @param journal the journal to be edited.
     * @param logger  the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Journal> createUpdateEditor(Journal journal, Logger logger) {
        final var context = createContextFor(journal, logger);
        return createUpdateEditor(context);
    }

}
