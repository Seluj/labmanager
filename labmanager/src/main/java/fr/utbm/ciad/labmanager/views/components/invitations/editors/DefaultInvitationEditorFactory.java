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

package fr.utbm.ciad.labmanager.views.components.invitations.editors;

import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.invitation.PersonInvitationService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.invitations.editors.regular.EmbeddedIncomingInvitationEditor;
import fr.utbm.ciad.labmanager.views.components.invitations.editors.regular.EmbeddedOutgoingInvitationEditor;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a person invitation editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultInvitationEditorFactory implements InvitationEditorFactory {

    private final PersonInvitationService personInvitationService;

    private final PersonFieldFactory personFieldFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructors.
     *
     * @param personInvitationService the service for accessing the JPA entities for person invitations.
     * @param personFieldFactory      the factory for creating the person fields.
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (Spring layer).
     */
    public DefaultInvitationEditorFactory(
            @Autowired PersonInvitationService personInvitationService,
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.personInvitationService = personInvitationService;
        this.personFieldFactory = personFieldFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<PersonInvitation> createContextFor(PersonInvitation invitation, Logger logger) {
        return this.personInvitationService.startEditing(invitation, logger);
    }

    @Override
    public AbstractEntityEditor<PersonInvitation> createIncomingInvitationAdditionEditor(EntityEditingContext<PersonInvitation> context) {
        return new EmbeddedIncomingInvitationEditor(context, null, this.personFieldFactory, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<PersonInvitation> createIncomingInvitationUpdateEditor(EntityEditingContext<PersonInvitation> context) {
        return new EmbeddedIncomingInvitationEditor(context, null, this.personFieldFactory, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<PersonInvitation> createOutgoingInvitationAdditionEditor(EntityEditingContext<PersonInvitation> context) {
        return new EmbeddedOutgoingInvitationEditor(context, null, this.personFieldFactory, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<PersonInvitation> createOutgoingInvitationUpdateEditor(EntityEditingContext<PersonInvitation> context) {
        return new EmbeddedOutgoingInvitationEditor(context, null, this.personFieldFactory, this.authenticatedUser, this.messages);
    }

}
