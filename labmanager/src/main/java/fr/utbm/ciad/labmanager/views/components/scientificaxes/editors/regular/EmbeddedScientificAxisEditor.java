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

package fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.regular;

import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of scientific axis information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited axis.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedScientificAxisEditor extends AbstractScientificAxisEditor {

    private static final long serialVersionUID = -4704487014166974489L;

    /**
     * Constructor.
     *
     * @param context                    the editing context for the scientific axis.
     * @param axisCreationStatusComputer the tool for computer the creation status for the scientific axes.
     * @param authenticatedUser          the connected user.
     * @param messages                   the accessor to the localized messages (Spring layer).
     */
    public EmbeddedScientificAxisEditor(EntityEditingContext<ScientificAxis> context,
                                        EntityCreationStatusComputer<ScientificAxis> axisCreationStatusComputer,
                                        AuthenticatedUser authenticatedUser, MessageSourceAccessor messages) {
        super(context, axisCreationStatusComputer, false, authenticatedUser, messages,
                ConstructionPropertiesBuilder.create());
        createEditorContentAndLinkBeans();
    }

}
