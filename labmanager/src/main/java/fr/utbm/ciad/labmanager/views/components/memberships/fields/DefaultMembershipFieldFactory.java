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

package fr.utbm.ciad.labmanager.views.components.memberships.fields;

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.views.components.memberships.editors.MembershipEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Factory for building the fields related to the person memberships.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultMembershipFieldFactory implements MembershipFieldFactory {

    private final MembershipService membershipService;

    private final MembershipEditorFactory membershipEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor.
     *
     * @param membershipService       the service for accessing the membership JPA entities.
     * @param membershipEditorFactory the factory for creating the person membership editors.
     * @param authenticatedUser       the user that is currently authenticated.
     */
    public DefaultMembershipFieldFactory(
            @Autowired MembershipService membershipService,
            @Autowired MembershipEditorFactory membershipEditorFactory,
            @Autowired AuthenticatedUser authenticatedUser) {
        this.membershipService = membershipService;
        this.membershipEditorFactory = membershipEditorFactory;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public SingleMembershipNameField createSingleNameField(Supplier<Locale> locale,
                                                           SerializableBiConsumer<Membership, Consumer<Membership>> creationWithUiCallback,
                                                           SerializableBiConsumer<Membership, Consumer<Membership>> creationWithoutUiCallback,
                                                           Consumer<Membership> entityInitializer) {
        return new SingleMembershipNameField(this.membershipService, locale, creationWithUiCallback, creationWithoutUiCallback, entityInitializer);
    }

    @Override
    public SingleMembershipNameField createSingleNameField(String creationTitle, Logger logger, Supplier<Locale> locale,
                                                           Consumer<Membership> entityInitializer) {
        return new SingleMembershipNameField(this.membershipService, this.membershipEditorFactory, this.authenticatedUser,
                creationTitle, logger, locale, entityInitializer);
    }

}
