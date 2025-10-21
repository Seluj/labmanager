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

package fr.utbm.ciad.labmanager.views.components.conferences.editors.wizard;

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of conference information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited conference. It is a wizard
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedConferenceEditorWizard extends AbstractConferenceEditorWizard {

    private static final long serialVersionUID = -2522520461244264353L;

    /**
     * Constructor.
     *
     * @param context                          the editing context for the conference.
     * @param conferenceCreationStatusComputer the tool for computer the creation status for the conferences.
     * @param conferenceService                the service for accessing the conference entities.
     * @param authenticatedUser                the connected user.
     * @param messages                         the accessor to the localized messages (Spring layer).
     */
    public EmbeddedConferenceEditorWizard(EntityEditingContext<Conference> context,
                                          EntityCreationStatusComputer<Conference> conferenceCreationStatusComputer,
                                          AuthenticatedUser authenticatedUser, ConferenceService conferenceService,
                                          MessageSourceAccessor messages) {
        super(context, conferenceCreationStatusComputer, false, conferenceService, authenticatedUser, messages, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}