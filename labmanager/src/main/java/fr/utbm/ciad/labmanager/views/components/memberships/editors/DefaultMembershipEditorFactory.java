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
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.memberships.editors.regular.EmbeddedMembershipEditor;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a person membership editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultMembershipEditorFactory implements MembershipEditorFactory {

    private final MembershipService membershipService;

    private final PersonFieldFactory personFieldFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    private final OrganizationFieldFactory organizationFieldFactory;

    private final ScientificAxisService axisService;

    private final ScientificAxisEditorFactory axisEditorFactory;

    /**
     * Constructors.
     *
     * @param membershipService        the service for accessing the JPA entities for person memberships.
     * @param personFieldFactory       the factory for creating the person fields.
     * @param organizationFieldFactory the factory for creating the organization fields.
     * @param axisService              the service for accessing the JPA entities for scientific axes.
     * @param axisEditorFactory        the factory for creating the scientific axis editors.
     * @param authenticatedUser        the connected user.
     * @param messages                 the accessor to the localized messages (Spring layer).
     */
    public DefaultMembershipEditorFactory(
            @Autowired MembershipService membershipService,
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired OrganizationFieldFactory organizationFieldFactory,
            @Autowired ScientificAxisService axisService,
            @Autowired ScientificAxisEditorFactory axisEditorFactory,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.membershipService = membershipService;
        this.personFieldFactory = personFieldFactory;
        this.organizationFieldFactory = organizationFieldFactory;
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<Membership> createContextFor(Membership membership, Logger logger) {
        return this.membershipService.startEditing(membership, logger);
    }

    @Override
    public AbstractEntityEditor<Membership> createAdditionEditor(EntityEditingContext<Membership> context, boolean editAssociatedPerson) {
        return new EmbeddedMembershipEditor(
                context,
                null,
                editAssociatedPerson,
                this.personFieldFactory,
                this.organizationFieldFactory,
                this.axisService,
                this.axisEditorFactory,
                this.authenticatedUser,
                this.messages);
    }

    @Override
    public AbstractEntityEditor<Membership> createUpdateEditor(EntityEditingContext<Membership> context, boolean editAssociatedPerson) {
        return new EmbeddedMembershipEditor(
                context,
                null,
                editAssociatedPerson,
                this.personFieldFactory,
                this.organizationFieldFactory,
                this.axisService,
                this.axisEditorFactory,
                this.authenticatedUser,
                this.messages);
    }

}
