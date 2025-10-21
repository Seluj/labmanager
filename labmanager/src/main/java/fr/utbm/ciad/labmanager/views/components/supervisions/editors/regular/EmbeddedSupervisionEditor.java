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

package fr.utbm.ciad.labmanager.views.components.supervisions.editors.regular;

import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.memberships.fields.MembershipFieldFactory;
import fr.utbm.ciad.labmanager.views.components.supervisions.fields.SupervisionFieldFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of supervision information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited supervision.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedSupervisionEditor extends AbstractSupervisionEditor {

    private static final long serialVersionUID = -2908551063864242284L;

    /**
     * Constructor.
     *
     * @param context                           the editing context for the supervision.
     * @param supervisionCreationStatusComputer the tool for computer the creation status for the person supervisions.
     * @param membershipFieldFactory            the factory for creating the person membership fields.
     * @param supervisionFieldFactory           the factory for creating the supervision fields.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     * @since 4.0
     */
    public EmbeddedSupervisionEditor(EntityEditingContext<Supervision> context,
                                     EntityCreationStatusComputer<Supervision> supervisionCreationStatusComputer,
                                     MembershipFieldFactory membershipFieldFactory, SupervisionFieldFactory supervisionFieldFactory,
                                     AuthenticatedUser authenticatedUser, MessageSourceAccessor messages) {
        super(context, false, supervisionCreationStatusComputer, membershipFieldFactory, supervisionFieldFactory, authenticatedUser, messages,
                ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
