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

package fr.utbm.ciad.labmanager.views.components.assocstructures.views;

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
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.assostructure.AssociatedStructureService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.assocstructures.editors.AssociatedStructureEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the associated structures.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardAssociatedStructureListView extends AbstractEntityListView<AssociatedStructure> {

    private static final long serialVersionUID = 3844606138630969467L;

    private final AssociatedStructureDataProvider dataProvider;

    private final AssociatedStructureService structureService;

    private final AssociatedStructureEditorFactory associatedStructureEditorFactory;

    private Column<AssociatedStructure> nameColumn;

    private Column<AssociatedStructure> dateColumn;

    private Column<AssociatedStructure> validationColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser                the connected user.
     * @param messages                         the accessor to the localized messages (spring layer).
     * @param loggerFactory                    the factory to be used for the composite logger.
     * @param structureService                 the service for accessing the associated structures.
     * @param associatedStructureEditorFactory the factory for creating the associated-structure editors.
     */
    public StandardAssociatedStructureListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            AssociatedStructureService structureService, AssociatedStructureEditorFactory associatedStructureEditorFactory) {
        super(AssociatedStructure.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.associated_structure.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.associated_structure.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.associated_structure.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.associated_structure.delete_error")); //$NON-NLS-1$
        this.structureService = structureService;
        this.associatedStructureEditorFactory = associatedStructureEditorFactory;
        this.dataProvider = (ps, query, filters) -> ps.getAllAssociatedStructures(query, filters);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    @Override
    protected AbstractFilters<AssociatedStructure> createFilters() {
        return new StructureFilters(getAuthenticatedUser(), this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<AssociatedStructure> grid) {
        this.nameColumn = grid.addColumn(AssociatedStructure::getName)
                .setRenderer(new ComponentRenderer<>(this::createNameComponent))
                .setFrozen(true)
                .setAutoWidth(true)
                .setSortProperty("acronym", "name"); //$NON-NLS-1$ //$NON-NLS-2$
        this.dateColumn = grid.addColumn(AssociatedStructure::getCreationDate)
                .setAutoWidth(true)
                .setSortProperty("creationDate"); //$NON-NLS-1$
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

    private Component createNameComponent(AssociatedStructure structure) {
        final var acronym = structure.getAcronym();
        final var name = structure.getName();
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
    protected List<Column<AssociatedStructure>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<AssociatedStructure, Void> getFetchCallback(AbstractFilters<AssociatedStructure> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.structureService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openStructureEditor(new AssociatedStructure(), getTranslation("views.associated_structure.add_structure"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(AssociatedStructure structure) {
        openStructureEditor(structure, getTranslation("views.associated_structure.edit_structure", structure.getAcronymOrName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a structure.
     *
     * @param structure  the structure to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openStructureEditor(AssociatedStructure structure, String title, boolean isCreation) {
        final AbstractEntityEditor<AssociatedStructure> editor;
        if (isCreation) {
            editor = this.associatedStructureEditorFactory.createAdditionEditor(structure, getLogger());
        } else {
            editor = this.associatedStructureEditorFactory.createUpdateEditor(structure, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, AssociatedStructure> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, AssociatedStructure> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<AssociatedStructure> createDeletionContextFor(Set<AssociatedStructure> entities) {
        return this.structureService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.dateColumn.setHeader(getTranslation("views.associated_structure.creation_date.short")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
    }

    /**
     * Provider of data for associated structures to be displayed in the list of structures view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface AssociatedStructureDataProvider {

        /**
         * Fetch associated structure data.
         *
         * @param structureService the service to have access to the JPA.
         * @param pageRequest      the request for paging the data.
         * @param filters          the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<AssociatedStructure> fetch(AssociatedStructureService structureService, PageRequest pageRequest, AbstractFilters<AssociatedStructure> filters);

    }

    /**
     * UI and JPA filters for {@link StandardAssociatedStructureListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class StructureFilters extends AbstractAuthenticatedUserDataFilters<AssociatedStructure> {

        private static final long serialVersionUID = 520337561490106907L;

        private Checkbox includeNames;

        /**
         * Constructor.
         *
         * @param user     the connected user, or {@code null} if the filter does not care about a connected user.
         * @param onSearch the callback function for running the filtering.
         */
        public StructureFilters(AuthenticatedUser user, Runnable onSearch) {
            super(user, onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeNames = new Checkbox(true);

            options.add(this.includeNames);
        }

        @Override
        protected void resetFilters() {
            this.includeNames.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForAuthenticatedUser(Root<AssociatedStructure> root, CriteriaQuery<?> query,
                                                               CriteriaBuilder criteriaBuilder, Person user) {
            return criteriaBuilder.equal(root.get("holders").get("person"), user); //$NON-NLS-1$ //$NON-NLS-2$
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<AssociatedStructure> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
        }

    }

}
