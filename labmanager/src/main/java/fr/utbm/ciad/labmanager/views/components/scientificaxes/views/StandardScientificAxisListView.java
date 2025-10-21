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

package fr.utbm.ciad.labmanager.views.components.scientificaxes.views;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the scientific axes.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardScientificAxisListView extends AbstractEntityListView<ScientificAxis> {

    private static final long serialVersionUID = -1929465946434562506L;

    private final ScientificAxisDataProvider dataProvider;

    private final ScientificAxisService axisService;

    private final ScientificAxisEditorFactory axisEditorFactory;

    private Column<ScientificAxis> nameColumn;

    private Column<ScientificAxis> startDateColumn;

    private Column<ScientificAxis> endDateColumn;

    private Column<ScientificAxis> validationColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser the connected user.
     * @param messages          the accessor to the localized messages (spring layer).
     * @param loggerFactory     the factory to be used for the composite logger.
     * @param axisService       the service for accessing the scientific axes.
     * @param axisEditorFactory the factory for creating scientific axis editors.
     */
    public StandardScientificAxisListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            ScientificAxisService axisService, ScientificAxisEditorFactory axisEditorFactory) {
        super(ScientificAxis.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.scientific_axes.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.scientific_axes.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.scientific_axes.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.scientific_axes.delete_error")); //$NON-NLS-1$
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;
        this.dataProvider = (ps, query, filters) -> ps.getAllScientificAxes(query, filters);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    @Override
    protected AbstractFilters<ScientificAxis> createFilters() {
        return new AxisFilters(this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<ScientificAxis> grid) {
        this.nameColumn = grid.addColumn(address -> address.getName())
                .setRenderer(new ComponentRenderer<>(this::createNameComponent))
                .setAutoWidth(true)
                .setSortProperty("acronym", "name"); //$NON-NLS-1$ //$NON-NLS-2$
        this.startDateColumn = grid.addColumn(new LocalDateRenderer<>(it -> it.getStartDate()))
                .setAutoWidth(true)
                .setSortProperty("startDate"); //$NON-NLS-1$
        this.endDateColumn = grid.addColumn(new LocalDateRenderer<>(it -> it.getEndDate()))
                .setAutoWidth(true)
                .setSortProperty("endDate"); //$NON-NLS-1$
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

    private Component createNameComponent(ScientificAxis axis) {
        final var acronym = axis.getAcronym();
        final var name = axis.getName();
        final var label = new StringBuilder();
        if (Strings.isNullOrEmpty(acronym)) {
            label.append(name);
        } else if (Strings.isNullOrEmpty(name)) {
            label.append(acronym);
        } else {
            label.append(acronym).append(" - ").append(name); //$NON-NLS-1$
        }
        return new Span(label.toString());
    }

    @Override
    protected List<Column<ScientificAxis>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<ScientificAxis, Void> getFetchCallback(AbstractFilters<ScientificAxis> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.axisService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openAxisEditor(new ScientificAxis(), getTranslation("views.scientific_axes.add_axis"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(ScientificAxis axis) {
        openAxisEditor(axis, getTranslation("views.scientific_axes.edit_axis", axis.getAcronymOrName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of an axis.
     *
     * @param axis  the axis to edit.
     * @param title the title of the editor.
     */
    protected void openAxisEditor(ScientificAxis axis, String title, boolean isCreation) {
        final AbstractEntityEditor<ScientificAxis> editor;
        if (isCreation) {
            editor = this.axisEditorFactory.createAdditionEditor(axis, getLogger());
        } else {
            editor = this.axisEditorFactory.createUpdateEditor(axis, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, ScientificAxis> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, ScientificAxis> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<ScientificAxis> createDeletionContextFor(Set<ScientificAxis> entities) {
        return this.axisService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.startDateColumn.setHeader(getTranslation("views.date.start")); //$NON-NLS-1$
        this.endDateColumn.setHeader(getTranslation("views.date.end")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
    }

    /**
     * Provider of data for scientific axes to be displayed in the list of axes view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface ScientificAxisDataProvider {

        /**
         * Fetch scientific axis data.
         *
         * @param axisService the service to have access to the JPA.
         * @param pageRequest the request for paging the data.
         * @param filters     the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<ScientificAxis> fetch(ScientificAxisService axisService, PageRequest pageRequest, AbstractFilters<ScientificAxis> filters);

    }

    /**
     * UI and JPA filters for {@link StandardScientificAxisListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class AxisFilters extends AbstractFilters<ScientificAxis> {

        private static final long serialVersionUID = 7590936731361248312L;

        private Checkbox includeAcronyms;

        private Checkbox includeNames;

        private Checkbox includeDates;

        /**
         * Constructor.
         *
         * @param onSearch the callback function for running the filtering.
         */
        public AxisFilters(Runnable onSearch) {
            super(onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeAcronyms = new Checkbox(true);
            this.includeNames = new Checkbox(true);
            this.includeDates = new Checkbox(true);

            options.add(this.includeAcronyms, this.includeNames, this.includeDates);
        }

        @Override
        protected void resetFilters() {
            this.includeAcronyms.setValue(Boolean.TRUE);
            this.includeNames.setValue(Boolean.TRUE);
            this.includeDates.setValue(Boolean.TRUE);
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<ScientificAxis> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeAcronyms.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keywords)); //$NON-NLS-1$
            }
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywords)); //$NON-NLS-1$
            }
            if (this.includeDates.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get("startDate"))), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.toString(root.get("endDate"))), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeAcronyms.setLabel(getTranslation("views.filters.include_acronyms")); //$NON-NLS-1$
            this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
            this.includeDates.setLabel(getTranslation("views.filters.include_dates")); //$NON-NLS-1$
        }

    }

}
