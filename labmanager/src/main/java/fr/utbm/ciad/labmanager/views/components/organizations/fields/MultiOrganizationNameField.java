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

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractMultiEntityNameField;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation of a field for entering the names of research organizations, with auto-completion from the person JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class MultiOrganizationNameField extends AbstractMultiEntityNameField<ResearchOrganization> {

    private static final long serialVersionUID = -3563272751149407935L;

    /**
     * Constructor.
     *
     * @param organizationService       the service for accessing the organization JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new organization using an UI, e.g., an editor. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new organization without using an UI. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param initializer               the initializer of the loaded organizations. It may be {@code null}.
     */
    public MultiOrganizationNameField(ResearchOrganizationService organizationService,
                                      SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
                                      SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback,
                                      Consumer<ResearchOrganization> initializer) {
        super(
                combo -> {
                    combo.setRenderer(new ComponentRenderer<>(createOrganizationRender(organizationService.getFileManager())));
                    combo.setItemLabelGenerator(it -> it.getAcronymAndName());
                },
                combo -> {
                    if (initializer == null) {
                        combo.setItems(query -> organizationService.getAllResearchOrganizations(
                                VaadinSpringDataHelpers.toSpringPageRequest(query),
                                createOrganizationFilter(query.getFilter())).stream());
                    } else {
                        combo.setItems(query -> organizationService.getAllResearchOrganizations(
                                VaadinSpringDataHelpers.toSpringPageRequest(query),
                                createOrganizationFilter(query.getFilter()), initializer).stream());
                    }
                },
                creationWithUiCallback, creationWithoutUiCallback);
    }

    /**
     * Constructor.
     *
     * @param organizationService       the service for accessing the organization JPA entities.
     * @param creationWithUiCallback    a lambda that is invoked for creating a new organization using an UI, e.g., an editor. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     * @param creationWithoutUiCallback a lambda that is invoked for creating a new organization without using an UI. The first argument is the new organization entity.
     *                                  The second argument is a lambda that must be invoked to inject the new organization in the {@code MultiOrganizationNameField}.
     *                                  This second lambda takes the created organization.
     */
    public MultiOrganizationNameField(ResearchOrganizationService organizationService,
                                      SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithUiCallback,
                                      SerializableBiConsumer<ResearchOrganization, Consumer<ResearchOrganization>> creationWithoutUiCallback) {
        this(organizationService, creationWithUiCallback, creationWithoutUiCallback, null);
    }

    /**
     * Constructor.
     *
     * @param organizationService       the service for accessing the organization JPA entities.
     * @param organizationEditorFactory the factory for creating the organization editors.
     * @param authenticatedUser         the user that is currently authenticated.
     * @param creationTitle             the title of the dialog box for creating the organization.
     * @param logger                    the logger for abnormal messages to the lab manager administrator.
     * @param initializer               the initializer of the loaded organizations. It may be {@code null}.
     */
    public MultiOrganizationNameField(ResearchOrganizationService organizationService, OrganizationEditorFactory organizationEditorFactory,
                                      AuthenticatedUser authenticatedUser, String creationTitle, Logger logger, Consumer<ResearchOrganization> initializer) {
        this(organizationService,
                (newOrganization, saver) -> {
                    final var editor = organizationEditorFactory.createAdditionEditor(newOrganization, logger);
                    ComponentFactory.openEditionModalDialog(creationTitle, editor, true,
                            (dialog, changedOrganization) -> saver.accept(changedOrganization),
                            null);
                },
                (newOrganization, saver) -> {
                    try {
                        final var creationContext = organizationService.startEditing(newOrganization, logger);
                        creationContext.save();
                        saver.accept(creationContext.getEntity());
                    } catch (Throwable ex) {
                        logger.warn("Error when creating a organization: " + ex.getLocalizedMessage() + "\n-> " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$ //$NON-NLS-2$
                        ComponentFactory.showErrorNotification(organizationService.getMessageSourceAccessor().getMessage("views.organizations.creation_error", new Object[]{ex.getLocalizedMessage()})); //$NON-NLS-1$
                    }
                },
                initializer);
    }

    /**
     * Constructor.
     *
     * @param organizationService       the service for accessing the person JPA entities.
     * @param organizationEditorFactory the factory for creating the organization editors.
     * @param authenticatedUser         the user that is currently authenticated.
     * @param creationTitle             the title of the dialog box for creating the person.
     * @param logger                    the logger for abnormal messages to the lab manager administrator.
     */
    public MultiOrganizationNameField(ResearchOrganizationService organizationService, OrganizationEditorFactory organizationEditorFactory, AuthenticatedUser authenticatedUser,
                                      String creationTitle, Logger logger) {
        this(organizationService, organizationEditorFactory, authenticatedUser, creationTitle, logger, null);
    }

    private static SerializableFunction<ResearchOrganization, Component> createOrganizationRender(FileManager fileManager) {
        if (fileManager == null) {
            return ComponentFactory::newOrganizationAvatar;
        }
        return organization -> ComponentFactory.newOrganizationAvatar(organization, fileManager);
    }

    private static Specification<ResearchOrganization> createOrganizationFilter(Optional<String> filter) {
        if (filter.isPresent()) {
            return (root, query, criteriaBuilder) ->
                    ComponentFactory.newPredicateContainsOneOf(filter.get(), root, query, criteriaBuilder,
                            (keyword, predicates, root0, criteriaBuilder0) -> {
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keyword)); //$NON-NLS-1$
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword)); //$NON-NLS-1$
                            });
        }
        return null;
    }

    @Override
    protected ResearchOrganization createNewEntity(String customName) {
        final var newOrganization = new ResearchOrganization();

        if (!Strings.isNullOrEmpty(customName)) {
            final var parts = customName.split("\\s*" + EntityConstants.ACRONYM_NAME_SEPARATOR + "\\s*", 2); //$NON-NLS-1$ //$NON-NLS-2$
            if (parts.length > 1) {
                newOrganization.setAcronym(parts[0]);
                newOrganization.setName(parts[1]);
            } else {
                newOrganization.setName(customName);
            }
        }

        return newOrganization;
    }

}
