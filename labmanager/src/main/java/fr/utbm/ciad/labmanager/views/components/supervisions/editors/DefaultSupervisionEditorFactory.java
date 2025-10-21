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

package fr.utbm.ciad.labmanager.views.components.supervisions.editors;

import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.memberships.fields.MembershipFieldFactory;
import fr.utbm.ciad.labmanager.views.components.supervisions.editors.regular.EmbeddedSupervisionEditor;
import fr.utbm.ciad.labmanager.views.components.supervisions.fields.SupervisionFieldFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a person supervision editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultSupervisionEditorFactory implements SupervisionEditorFactory {

    private final SupervisionService supervisionService;

    private final MembershipFieldFactory membershipFieldFactory;

    private final SupervisionFieldFactory supervisionFieldFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param supervisionService      the service for accessing the supervision JPA entities.
     * @param membershipFieldFactory  the factory for creating the person membership fields.
     * @param supervisionFieldFactory the factory for creating the supervision fields.
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (Spring layer).
     */
    public DefaultSupervisionEditorFactory(
            @Autowired SupervisionService supervisionService,
            @Autowired MembershipFieldFactory membershipFieldFactory,
            @Autowired SupervisionFieldFactory supervisionFieldFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.supervisionService = supervisionService;
        this.membershipFieldFactory = membershipFieldFactory;
        this.supervisionFieldFactory = supervisionFieldFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<Supervision> createContextFor(Supervision supervision, Logger logger) {
        return this.supervisionService.startEditing(supervision, logger);
    }

    @Override
    public AbstractEntityEditor<Supervision> createAdditionEditor(EntityEditingContext<Supervision> context) {
        return new EmbeddedSupervisionEditor(context, null, this.membershipFieldFactory,
                this.supervisionFieldFactory, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<Supervision> createUpdateEditor(EntityEditingContext<Supervision> context) {
        return new EmbeddedSupervisionEditor(context, null, this.membershipFieldFactory,
                this.supervisionFieldFactory, this.authenticatedUser, this.messages);
    }

}
