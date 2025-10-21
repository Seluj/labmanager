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

package fr.utbm.ciad.labmanager.views.components.organizations.editors;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import org.slf4j.Logger;

/**
 * Factory that is providing an organization editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface OrganizationEditorFactory {

    /**
     * Replies the editing context for the given organization.
     *
     * @param organization the organization to be edited.
     * @param logger       the logger to be associated to the context.
     * @return the editing context.
     */
    EntityEditingContext<ResearchOrganization> createContextFor(ResearchOrganization organization, Logger logger);

    /**
     * Create an editor that may be used for creating a new organization.
     *
     * @param context the context for editing the entity.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<ResearchOrganization> createAdditionEditor(EntityEditingContext<ResearchOrganization> context);

    /**
     * Create an editor that may be used for creating a new organization.
     *
     * @param organization the organization to be edited.
     * @param logger       the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<ResearchOrganization> createAdditionEditor(ResearchOrganization organization, Logger logger) {
        final var context = createContextFor(organization, logger);
        return createAdditionEditor(context);
    }

    /**
     * Create an editor that may be used for updating an existing organization.
     *
     * @param context the context for editing the entity.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<ResearchOrganization> createUpdateEditor(EntityEditingContext<ResearchOrganization> context);

    /**
     * Create an editor that may be used for updating an existing organization.
     *
     * @param organization the organization to be edited.
     * @param logger       the logger to be associated to the context.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<ResearchOrganization> createUpdateEditor(ResearchOrganization organization, Logger logger) {
        final var context = createContextFor(organization, logger);
        return createUpdateEditor(context);
    }

}
