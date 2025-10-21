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

package fr.utbm.ciad.labmanager.views.components.organizations.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.regular.AbstractOrganizationEditor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the organizations.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardOrganizationListView extends AbstractEntityListView<ResearchOrganization> {

    private static final long serialVersionUID = -2162467602125180982L;

    private final OrganizationDataProvider dataProvider;

    private final ResearchOrganizationService organizationService;
    private final DownloadableFileManager fileManager;
    private final OrganizationEditorFactory organizationEditorFactory;
    private Column<ResearchOrganization> nameColumn;
    private Column<ResearchOrganization> typeColumn;
    private Column<ResearchOrganization> countryColumn;
    private Column<ResearchOrganization> superStructureColumn;
    private Column<ResearchOrganization> subStructureColumn;
    private Column<ResearchOrganization> validationColumn;

    /**
     * Constructor.
     *
     * @param fileManager               the manager of the filenames for the uploaded files.
     * @param authenticatedUser         the connected user.
     * @param messages                  the accessor to the localized messages (spring layer).
     * @param loggerFactory             the factory to be used for the composite logger.
     * @param organizationService       the service for accessing the organizations.
     * @param organizationEditorFactory the factory to be used for creating the organization editors.
     * @sin e4.0
     */
    public StandardOrganizationListView(
            DownloadableFileManager fileManager,
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
            ContextualLoggerFactory loggerFactory, ResearchOrganizationService organizationService,
            OrganizationEditorFactory organizationEditorFactory) {
        super(ResearchOrganization.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.organizations.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.organizations.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.organizations.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.organizations.delete_error")); //$NON-NLS-1$

        this.organizationEditorFactory = organizationEditorFactory;
        this.fileManager = fileManager;
        this.organizationService = organizationService;
        this.dataProvider = (ps, query, filters) -> ps.getAllResearchOrganizations(query, filters,
                it -> {
                    Hibernate.initialize(it.getSubOrganizations());
                });
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    @Override
    protected AbstractFilters<ResearchOrganization> createFilters() {
        return new OrganizationFilters(this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<ResearchOrganization> grid) {
        this.nameColumn = grid.addColumn(new ComponentRenderer<>(this::createNameComponent))
                .setAutoWidth(true)
                .setSortProperty("acronym", "name"); //$NON-NLS-1$ //$NON-NLS-2$
        this.typeColumn = grid.addColumn(organization -> organization.getType().getLabel(getMessageSourceAccessor(), getLocale()))
                .setAutoWidth(true)
                .setSortProperty("type"); //$NON-NLS-1$
        this.countryColumn = grid.addColumn(new ComponentRenderer<>(this::createCountryComponent))
                .setAutoWidth(true)
                .setSortProperty("country"); //$NON-NLS-1$
        this.superStructureColumn = grid.addColumn(new ComponentRenderer<>(this::createSuperStructureNames))
                .setAutoWidth(true)
                .setSortProperty("superOrganizations"); //$NON-NLS-1$
        this.subStructureColumn = grid.addColumn(new ComponentRenderer<>(this::createSubStructureNames))
                .setAutoWidth(true)
                .setSortProperty("subOrganizations"); //$NON-NLS-1$

        this.validationColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
                    if (data.isValidated()) {
                        callback.create(BadgeState.SUCCESS, null, getTranslation("views.validated")); //$NON-NLS-1$
                    } else {
                        callback.create(BadgeState.ERROR, null, getTranslation("views.validable")); //$NON-NLS-1$
                    }
                }))
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortProperty("validated") //$NON-NLS-1$
                .setWidth("0%"); //$NON-NLS-1$
        // Create the hover tool bar only if administrator role
        return isAdminRole();
    }

    private Component createStructureList(Iterable<ResearchOrganization> list) {
        final var iterator = list.iterator();
        if (!iterator.hasNext()) {
            return new Span();
        }
        final var first = ComponentFactory.newOrganizationAvatar(iterator.next(), this.fileManager);
        if (!iterator.hasNext()) {
            return first;
        }
        final var layout = new VerticalLayout();
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.add(first);
        while (iterator.hasNext()) {
            layout.add(ComponentFactory.newOrganizationAvatar(iterator.next(), this.fileManager));
        }
        return layout;
    }

    private Component createSuperStructureNames(ResearchOrganization organization) {
        return createStructureList(organization.getSuperOrganizations());
    }

    private Component createSubStructureNames(ResearchOrganization organization) {
        return createStructureList(organization.getSubOrganizations());
    }

    private Component createCountryComponent(ResearchOrganization organization) {
        final var country = organization.getCountry();

        final var name = new Span(country.getDisplayCountry(getLocale()));
        name.getStyle().set("margin-left", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$

        final var flag = new CountryFlag(country);
        flag.setSizeFromHeight(1, Unit.REM);

        final var layout = new HorizontalLayout(flag, name);
        layout.setSpacing(false);
        layout.setAlignItems(Alignment.CENTER);

        return layout;
    }

    private Component createNameComponent(ResearchOrganization organization) {
        return ComponentFactory.newOrganizationAvatar(organization, this.fileManager);
    }

    @Override
    protected List<Column<ResearchOrganization>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<ResearchOrganization, Void> getFetchCallback(AbstractFilters<ResearchOrganization> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.organizationService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openOrganizationEditor(new ResearchOrganization(), getTranslation("views.organizations.add_organization"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(ResearchOrganization organisation) {
        openOrganizationEditor(organisation, getTranslation("views.organizations.edit_organization", organisation.getAcronymOrName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of an organization.
     *
     * @param organization the organization to edit.
     * @param title        the title of the editor.
     * @param isCreation   indicates if the editor is for the creation of an organization, or for its update.
     */
    protected void openOrganizationEditor(ResearchOrganization organization, String title, boolean isCreation) {
        final AbstractEntityEditor<ResearchOrganization> editor;
        if (isCreation) {
            editor = this.organizationEditorFactory.createAdditionEditor(organization, getLogger());
        } else {
            editor = this.organizationEditorFactory.createUpdateEditor(organization, getLogger());
        }
        openOrganizationEditor(editor, title);
    }

    /**
     * Open the organization editor.
     *
     * @param editor the editor to open
     * @param title  the title of the editor
     * @param <T>    the type of the editor extending {@link AbstractOrganizationEditor}
     */
    private <T extends AbstractEntityEditor<ResearchOrganization>> void openOrganizationEditor(T editor, String title) {
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, ResearchOrganization> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, ResearchOrganization> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<ResearchOrganization> createDeletionContextFor(Set<ResearchOrganization> entities) {
        return this.organizationService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.typeColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
        this.countryColumn.setHeader(getTranslation("views.country")); //$NON-NLS-1$
        this.superStructureColumn.setHeader(getTranslation("views.super_structures")); //$NON-NLS-1$
        this.subStructureColumn.setHeader(getTranslation("views.sub_structures")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
        refreshGrid();
    }

    /**
     * Provider of data for organizations to be displayed in the list of organizations view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface OrganizationDataProvider {

        /**
         * Fetch organization data.
         *
         * @param organizationService the service to have access to the JPA.
         * @param pageRequest         the request for paging the data.
         * @param filters             the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<ResearchOrganization> fetch(ResearchOrganizationService organizationService, PageRequest pageRequest, AbstractFilters<ResearchOrganization> filters);

    }

    /**
     * UI and JPA filters for {@link StandardOrganizationListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class OrganizationFilters extends AbstractFilters<ResearchOrganization> {

        private static final long serialVersionUID = 5584210266375969212L;

        private Checkbox includeNames;

        private Checkbox includeTypes;

        private Checkbox includeCountries;

        /**
         * Constructor.
         *
         * @param onSearch the callback function for running the filtering.
         */
        public OrganizationFilters(Runnable onSearch) {
            super(onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeNames = new Checkbox(true);
            this.includeTypes = new Checkbox(true);
            this.includeCountries = new Checkbox(true);

            options.add(this.includeNames);
            options.add(this.includeTypes);
            options.add(this.includeCountries);
        }

        @Override
        protected void resetFilters() {
            this.includeNames.setValue(Boolean.TRUE);
            this.includeTypes.setValue(Boolean.TRUE);
            this.includeCountries.setValue(Boolean.TRUE);
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<ResearchOrganization> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywords)); //$NON-NLS-1$
            }
            if (this.includeTypes.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), keywords)); //$NON-NLS-1$
            }
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
            this.includeTypes.setLabel(getTranslation("views.filters.include_types")); //$NON-NLS-1$
            this.includeCountries.setLabel(getTranslation("views.filters.include_countries")); //$NON-NLS-1$
        }

    }

}
