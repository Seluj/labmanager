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
import org.slf4j.Logger;

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
public interface MembershipFieldFactory {

    /**
     * Create a field for entering the name of a single membership.
     *
     * @param locale                    the locale used of showing the messages.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new membership using an UI, e.g., an editor. The first argument is the new membership entity.
     *                                  The second argument is a lambda that must be invoked to inject the new membership in the {@code SingleMembershipNameField}.
     *                                  This second lambda takes the created membership.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new membership without using an UI. The first argument is the new membership entity.
     *                                  The second argument is a lambda that must be invoked to inject the new membership in the {@code SingleMembershipNameField}.
     *                                  This second lambda takes the created membership.
     * @param entityInitializer         the callback function for initializing the properties of each loaded membership.
     * @return the field, never {@code null}.
     */
    SingleMembershipNameField createSingleNameField(Supplier<Locale> locale,
                                                    SerializableBiConsumer<Membership, Consumer<Membership>> creationWithUiCallback,
                                                    SerializableBiConsumer<Membership, Consumer<Membership>> creationWithoutUiCallback, Consumer<Membership> entityInitializer);

    /**
     * Create a field for entering the name of a single membership.
     *
     * @param creationTitle     the title of the dialog box for creating the person.
     * @param logger            the logger for abnormal messages to the lab manager administrator.
     * @param locale            the locale used of showing the messages.
     * @param entityInitializer the callback function for initializing the properties of each loaded research organization.
     * @return the field, never {@code null}.
     */
    SingleMembershipNameField createSingleNameField(String creationTitle, Logger logger, Supplier<Locale> locale,
                                                    Consumer<Membership> entityInitializer);

}
