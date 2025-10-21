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
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DefaultConferenceFieldFactory implements ConferenceFieldFactory {

    private final ConferenceService conferenceService;

    private final ConferenceEditorFactory conferenceEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor.
     *
     * @param conferenceService       the service for accessing the conference JPA entities.
     * @param conferenceEditorFactory factory for creating the conference editors.
     * @param authenticatedUser       the user that is currently authenticated.
     */
    public DefaultConferenceFieldFactory(
            @Autowired ConferenceService conferenceService,
            @Autowired ConferenceEditorFactory conferenceEditorFactory,
            @Autowired AuthenticatedUser authenticatedUser) {
        this.conferenceService = conferenceService;
        this.conferenceEditorFactory = conferenceEditorFactory;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public SingleConferenceNameField createSingleNameField(
            SerializableBiConsumer<Conference, Consumer<Conference>> creationWithUiCallback,
            SerializableBiConsumer<Conference, Consumer<Conference>> creationWithoutUiCallback) {
        return new SingleConferenceNameField(this.conferenceService, creationWithUiCallback, creationWithoutUiCallback);
    }

    @Override
    public SingleConferenceNameField createSingleNameField(String creationTitle, Logger logger) {
        return new SingleConferenceNameField(this.conferenceService, this.conferenceEditorFactory, this.authenticatedUser,
                creationTitle, logger);
    }

}
