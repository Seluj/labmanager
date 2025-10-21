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
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DefaultOrganizationFieldFactory implements OrganizationFieldFactory {

    private final ResearchOrganizationService organizationService;

    private final OrganizationEditorFactory organizationEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    /**
     * Constructor.
     *
     * @param organizationService       the service for accessing the organization JPA entities.
     * @param organizationEditorFactory the factory for creating the organization editors.
     * @param authenticatedUser         the user that is currently authenticated.
     */
    public DefaultOrganizationFieldFactory(
            @Autowired ResearchOrganizationService organizationService,
            @Autowired OrganizationEditorFactory organizationEditorFactory,
            @Autowired AuthenticatedUser authenticatedUser) {
        this.organizationService = organizationService;
        this.organizationEditorFactory = organizationEditorFactory;
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public SingleOrganizationNameField createSingleNameField(
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback,
            Consumer<ResearchOrganization> entityInitializer) {
        return new SingleOrganizationNameField(this.organizationService, creationWithUiCallback, creationWithoutUiCallback, entityInitializer);
    }

    @Override
    public SingleOrganizationNameField createSingleNameField(ResearchOrganization baseOrganization,
                                                             Consumer<ResearchOrganization> entityInitializer) {
        return new SingleOrganizationNameField(this.organizationService, baseOrganization, entityInitializer);
    }

    @Override
    public SingleOrganizationNameField createSingleNameField(String creationTitle, Logger logger, Consumer<ResearchOrganization> entityInitializer) {
        return new SingleOrganizationNameField(this.organizationService, this.organizationEditorFactory, this.authenticatedUser,
                creationTitle, logger, entityInitializer);
    }

    @Override
    public MultiOrganizationNameField createMultiNameField(
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback,
            Consumer<ResearchOrganization> initializer) {
        return new MultiOrganizationNameField(this.organizationService, creationWithUiCallback, creationWithoutUiCallback, initializer);
    }

    @Override
    public MultiOrganizationNameField createMultiNameField(
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
            SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback) {
        return new MultiOrganizationNameField(this.organizationService, creationWithUiCallback, creationWithoutUiCallback);
    }

    @Override
    public MultiOrganizationNameField createMultiNameField(String creationTitle, Logger logger,
                                                           Consumer<ResearchOrganization> initializer) {
        return new MultiOrganizationNameField(this.organizationService, this.organizationEditorFactory,
                this.authenticatedUser, creationTitle, logger, initializer);
    }

    @Override
    public MultiOrganizationNameField createMultiNameField(String creationTitle, Logger logger) {
        return new MultiOrganizationNameField(this.organizationService, this.organizationEditorFactory,
                this.authenticatedUser, creationTitle, logger);
    }

}
