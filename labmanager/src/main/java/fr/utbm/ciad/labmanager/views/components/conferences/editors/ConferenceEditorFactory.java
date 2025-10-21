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

package fr.utbm.ciad.labmanager.views.components.conferences.editors;

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import org.slf4j.Logger;

/**
 * Factory that is providing a conference editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface ConferenceEditorFactory {

    /**
     * Replies the editing context for the given conference.
     *
     * @param conference the conference to be edited.
     * @param logger     the logger to be used.
     * @return the editing context.
     */
    EntityEditingContext<Conference> createContextFor(Conference conference, Logger logger);

    /**
     * Create an editor that may be used for creating a new conference.
     *
     * @param context the editing context for the conference.
     * @param logger  the logger to be used.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Conference> createAdditionEditor(EntityEditingContext<Conference> context);

    /**
     * Create an editor that may be used for creating a new conference.
     *
     * @param conference the conference to be edited.
     * @param logger     the logger to be used.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Conference> createAdditionEditor(Conference conference, Logger logger) {
        final var context = createContextFor(conference, logger);
        return createAdditionEditor(context);
    }

    /**
     * Create an editor that may be used for updating an existing conference.
     *
     * @param context the editing context for the conference.
     * @param logger  the logger to be used.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Conference> createUpdateEditor(EntityEditingContext<Conference> context);

    /**
     * Create an editor that may be used for creating an exsiting conference.
     *
     * @param conference the conference to be edited.
     * @param logger     the logger to be used.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Conference> createUpdateEditor(Conference conference, Logger logger) {
        final var context = createContextFor(conference, logger);
        return createAdditionEditor(context);
    }

}
