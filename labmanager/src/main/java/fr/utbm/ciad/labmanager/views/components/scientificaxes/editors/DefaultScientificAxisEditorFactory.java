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

package fr.utbm.ciad.labmanager.views.components.scientificaxes.editors;

import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.regular.EmbeddedScientificAxisEditor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a scientific axis editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultScientificAxisEditorFactory implements ScientificAxisEditorFactory {

    private final ScientificAxisService axisService;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param axisCreationStatusComputer the tool for computer the creation status for the scientific axes.
     * @param axisService                the service for accessing the JPA entity for scientific axes.
     * @param authenticatedUser          the connected user.
     * @param messages                   the accessor to the localized messages (Spring layer).
     * @param loggerFactory              the factory to be used for the composite logger.
     */
    public DefaultScientificAxisEditorFactory(
            @Autowired ScientificAxisService axisService,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.axisService = axisService;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<ScientificAxis> createContextFor(ScientificAxis axis, Logger logger) {
        return this.axisService.startEditing(axis, logger);
    }

    @Override
    public AbstractEntityEditor<ScientificAxis> createAdditionEditor(EntityEditingContext<ScientificAxis> context) {
        return new EmbeddedScientificAxisEditor(context, null, this.authenticatedUser, this.messages);
    }

    @Override
    public AbstractEntityEditor<ScientificAxis> createUpdateEditor(EntityEditingContext<ScientificAxis> context) {
        return new EmbeddedScientificAxisEditor(context, null, this.authenticatedUser, this.messages);
    }

}
