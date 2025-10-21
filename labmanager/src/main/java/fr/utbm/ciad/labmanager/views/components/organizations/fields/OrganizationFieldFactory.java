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

package fr.utbm.ciad.labmanager.views.components.organizations.fields;

import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.slf4j.Logger;

import java.util.function.Consumer;

/**
 * Factory for building the fields related to the research organizations.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface OrganizationFieldFactory {

    /**
     * Create a field for entering the name of a single organization.
     *
     * @return the field, never {@code null}.
     * @see #createSingleNameField(SerializableBiConsumer, SerializableBiConsumer, Consumer)
     */
    default SingleOrganizationNameField createSingleNameField() {
        return createSingleNameField(
                (SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>>) null,
                null,
                null);
    }

    /**
     * Create a field for entering the name of a single organization.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new organization using an UI, e.g., an editor. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code SingleOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new organization without using an UI. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code SingleOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param entityInitializer         the callback function for initializing the properties of each loaded research organization.
     * @return the field, never {@code null}.
     */
    SingleOrganizationNameField createSingleNameField(SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
                                                      SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback,
                                                      Consumer<ResearchOrganization> entityInitializer);

    /**
     * Create a field for entering the name of a single organization.
     *
     * @param baseOrganization  the base organization from which the super organizations are extracted.
     * @param entityInitializer the callback function for initializing the properties of each loaded research organization.
     * @return the field, never {@code null}.
     */
    SingleOrganizationNameField createSingleNameField(ResearchOrganization baseOrganization, Consumer<ResearchOrganization> entityInitializer);

    /**
     * Create a field for entering the name of a single organization.
     *
     * @param creationTitle     the title of the dialog box for creating the person.
     * @param logger            the logger for abnormal messages to the lab manager administrator.
     * @param entityInitializer the callback function for initializing the properties of each loaded research organization.
     * @return the field, never {@code null}.
     */
    SingleOrganizationNameField createSingleNameField(String creationTitle, Logger logger, Consumer<ResearchOrganization> entityInitializer);

    /**
     * Create a field for entering the names of multiple organizations.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new organization using an UI, e.g., an editor. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new organization without using an UI. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param initializer               the initializer of the loaded organizations. It may be {@code null}.
     * @return the field, never {@code null}.
     */
    MultiOrganizationNameField createMultiNameField(SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
                                                    SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback,
                                                    Consumer<ResearchOrganization> initializer);

    /**
     * Create a field for entering the names of multiple organizations.
     *
     * @param creationWithUiCallback    a lambda that is invoked for creating a new organization using an UI, e.g., an editor. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new organization without using an UI. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @return the field, never {@code null}.
     */
    MultiOrganizationNameField createMultiNameField(SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
                                                    SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback);

    /**
     * Create a field for entering the names of multiple organizations.
     *
     * @param creationTitle the title of the dialog box for creating the organization.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @param initializer   the initializer of the loaded organizations. It may be {@code null}.
     * @return the field, never {@code null}.
     */
    MultiOrganizationNameField createMultiNameField(String creationTitle, Logger logger, Consumer<ResearchOrganization> initializer);

    /**
     * Create a field for entering the names of multiple organizations.
     *
     * @param creationTitle the title of the dialog box for creating the organization.
     * @param logger        the logger for abnormal messages to the lab manager administrator.
     * @return the field, never {@code null}.
     */
    MultiOrganizationNameField createMultiNameField(String creationTitle, Logger logger);

}
