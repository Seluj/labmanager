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

package fr.utbm.ciad.labmanager.views.components.jurys.editors.regular;

import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of jury membership information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited jury membership.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedJuryMembershipEditor extends AbstractJuryMembershipEditor {

    private static final long serialVersionUID = -7868251550659166877L;

    /**
     * Constructor.
     *
     * @param context                    the editing context for the scientific axis.
     * @param juryCreationStatusComputer the tool for computer the creation status for the jury memberships.
     * @param personFieldFactory         the factory for creating the person fields.
     * @param authenticatedUser          the connected user.
     * @param messages                   the accessor to the localized messages (Spring layer).
     */
    public EmbeddedJuryMembershipEditor(EntityEditingContext<JuryMembership> context,
                                        EntityCreationStatusComputer<JuryMembership> juryCreationStatusComputer,
                                        PersonFieldFactory personFieldFactory, AuthenticatedUser authenticatedUser,
                                        MessageSourceAccessor messages) {
        super(context, juryCreationStatusComputer, false, personFieldFactory, authenticatedUser, messages, ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
