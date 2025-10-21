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

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the conferences.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface ConferenceFieldFactory {

    /**
     * Create a field for entering the name of a conference.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new conference using an UI, e.g., an editor. The first argument is the new conference entity.
     *                                  The second argument is a lambda that must be invoked to inject the new conference in the {@code SingleConferenceNameField}.
     *                                  This second lambda takes the created conference.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new conference without using an UI. The first argument is the new conference entity.
     *                                  The second argument is a lambda that must be invoked to inject the new conference in the {@code SingleConferenceNameField}.
     *                                  This second lambda takes the created conference.
     * @return the field, never {@code null}.
     */
    SingleConferenceNameField createSingleNameField(SerializableBiConsumer<Conference, Consumer<Conference>> creationWithUiCallback,
                                                    SerializableBiConsumer<Conference, Consumer<Conference>> creationWithoutUiCallback);

    /**
     * Create a field for entering the name of a conference.
     *
     * @param creationTitle the title of the dialog box for creating the person.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @return the field, never {@code null}.
     */
    SingleConferenceNameField createSingleNameField(String creationTitle, Logger logger);

}
