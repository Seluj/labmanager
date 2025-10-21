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

package fr.utbm.ciad.labmanager.views.components.memberships.editors;

import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import org.slf4j.Logger;

/**
 * Factory that is providing a person membership editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface MembershipEditorFactory {

    /**
     * Replies the editing context for the given person membership.
     *
     * @param membership the person membership to be edited.
     * @param logger     the logger to be associated to the context.
     * @return the editing context.
     */
    EntityEditingContext<Membership> createContextFor(Membership membership, Logger logger);

    /**
     * Create an editor that may be used for creating a new person membership.
     *
     * @param context              the context for editing the entity.
     * @param editAssociatedPerson indicates if the associated person could be edited or not.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Membership> createAdditionEditor(EntityEditingContext<Membership> context, boolean editAssociatedPerson);

    /**
     * Create an editor that may be used for creating a new person membership.
     *
     * @param membership           the person membership to be edited.
     * @param logger               the logger to be associated to the context.
     * @param editAssociatedPerson indicates if the associated person could be edited or not.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Membership> createAdditionEditor(Membership membership, Logger logger, boolean editAssociatedPerson) {
        final var context = createContextFor(membership, logger);
        return createAdditionEditor(context, editAssociatedPerson);
    }

    /**
     * Create an editor that may be used for updating an existing person membership.
     *
     * @param context              the context for editing the entity.
     * @param editAssociatedPerson indicates if the associated person could be edited or not.
     * @return the editor, never {@code null}.
     */
    AbstractEntityEditor<Membership> createUpdateEditor(EntityEditingContext<Membership> context, boolean editAssociatedPerson);

    /**
     * Create an editor that may be used for updating an existing person membership.
     *
     * @param membership           the person membership to be edited.
     * @param logger               the logger to be associated to the context.
     * @param editAssociatedPerson indicates if the associated person could be edited or not.
     * @return the editor, never {@code null}.
     */
    default AbstractEntityEditor<Membership> createUpdateEditor(Membership membership, Logger logger, boolean editAssociatedPerson) {
        final var context = createContextFor(membership, logger);
        return createUpdateEditor(context, editAssociatedPerson);
    }

}
