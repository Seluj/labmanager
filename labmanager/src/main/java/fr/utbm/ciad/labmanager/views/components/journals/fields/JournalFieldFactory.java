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
import org.slf4j.Logger;

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
public interface JournalFieldFactory {

    /**
     * Create a field for entering the name of a journal.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new journal using an UI, e.g., an editor. The first argument is the new journal entity.
     *                                  The second argument is a lambda that must be invoked to inject the new journal in the {@code SingleJournalNameField}.
     *                                  This second lambda takes the created journal.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new journal without using an UI. The first argument is the new journal entity.
     *                                  The second argument is a lambda that must be invoked to inject the new journal in the {@code SingleJournalNameField}.
     *                                  This second lambda takes the created journal.
     * @return the field, never {@code null}.
     */
    SingleJournalNameField createSingleNameField(SerializableBiConsumer<Journal, Consumer<Journal>> creationWithUiCallback,
                                                 SerializableBiConsumer<Journal, Consumer<Journal>> creationWithoutUiCallback);

    /**
     * Create a field for entering the name of a journal.
     *
     * @param creationTitle the title of the dialog box for creating the journal.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @return the field, never {@code null}.
     */
    SingleJournalNameField createSingleNameField(String creationTitle, Logger logger);

}
