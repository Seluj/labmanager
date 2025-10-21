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

package fr.utbm.ciad.labmanager.views.components.supervisions.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.supervisions.editors.SupervisionEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * List all the supervisions.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardSupervisionListView extends AbstractEntityListView<Supervision> {

    private static final long serialVersionUID = -7908569503954366686L;

    private final SupervisionDataProvider dataProvider;

    private final SupervisionService supervisionService;

    private final SupervisionEditorFactory supervisionEditorFactory;

    private Column<Supervision> supervisedPersonColumn;

    private Column<Supervision> typeColumn;

    private Column<Supervision> dateColumn;

    private Column<Supervision> supervisorsColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser        the connected user.
     * @param messages                 the accessor to the localized messages (spring layer).
     * @param loggerFactory            the factory to be used for the composite logger.
     * @param supervisionService       the service for accessing the supervisions.
     * @param supervisionEditorFactory the factory for creating the person supervision editors.
     * @since 4.0
     */
    public StandardSupervisionListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            SupervisionService supervisionService, SupervisionEditorFactory supervisionEditorFactory) {
        super(Supervision.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.supervision.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.supervision.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.supervision.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.supervision.delete_error")); //$NON-NLS-1$
        this.supervisionService = supervisionService;
        this.supervisionEditorFactory = supervisionEditorFactory;
        this.dataProvider = (ps, query, filters) -> ps.getAllSupervisions(query, filters, this::initializeEntityFromJPA);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private void initializeEntityFromJPA(Supervision entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        Hibernate.initialize(entity.getSupervisedPerson());
        Hibernate.initialize(entity.getSupervisors());
        entity.getSupervisors().forEach(it -> {
            Hibernate.initialize(it.getSupervisor());
        });
    }

    @Override
    protected AbstractFilters<Supervision> createFilters() {
        return new SupervisionFilters(getAuthenticatedUser(), this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<Supervision> grid) {
        this.supervisedPersonColumn = grid.addColumn(new ComponentRenderer<>(this::createSupervisedPersonComponent))
                .setAutoWidth(true);
        this.typeColumn = grid.addColumn(this::getTypeLabel)
                .setAutoWidth(false);
        this.dateColumn = grid.addColumn(this::getDateLabel)
                .setAutoWidth(false)
                .setSortProperty("supervisedPerson.memberToWhen", "defenseDate"); //$NON-NLS-1$ //$NON-NLS-2$
        this.supervisorsColumn = grid.addColumn(new ComponentRenderer<>(this::createSupervisorsComponent))
                .setAutoWidth(true);
        return isAdminRole();
    }

    private Component createSupervisedPersonComponent(Supervision supervision) {
        final var membership = supervision.getSupervisedPerson();
        if (membership != null) {
            final var person = membership.getPerson();
            if (person != null) {
                return ComponentFactory.newPersonAvatar(person);
            }
        }
        return new Span();
    }

    private String getTypeLabel(Supervision supervision) {
        final var membership = supervision.getSupervisedPerson();
        if (membership != null) {
            final var person = membership.getPerson();
            final var gender = person == null ? null : person.getGender();
            return membership.getMemberStatus().getLabel(getMessageSourceAccessor(), gender, false, getLocale());
        }
        return ""; //$NON-NLS-1$
    }

    private String getDateLabel(Supervision supervision) {
        return supervision.getYearRange().toString();
    }

    private Component createSupervisorsComponent(Supervision supervision) {
        final var supervisors = supervision.getSupervisors();
        if (supervisors != null && !supervisors.isEmpty()) {
            final var layout = new HorizontalLayout();
            layout.setSpacing(false);
            layout.setPadding(false);
            for (final var supervisor : supervisors) {
                final var supervisorComponent = ComponentFactory.newPersonAvatar(supervisor.getSupervisor());
                layout.add(supervisorComponent);
            }
            return layout;
        }
        return new Span();
    }

    @Override
    protected List<Column<Supervision>> getInitialSortingColumns() {
        return Arrays.asList(this.dateColumn, this.supervisedPersonColumn);
    }

    @Override
    protected SortDirection getInitialSortingDirection(Column<Supervision> column) {
        if (column == this.dateColumn) {
            return SortDirection.DESCENDING;
        }
        return SortDirection.ASCENDING;
    }

    @Override
    protected FetchCallback<Supervision, Void> getFetchCallback(AbstractFilters<Supervision> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.supervisionService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openSupervisionEditor(new Supervision(), getTranslation("views.supervision.add_supervision"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(Supervision supervision) {
        openSupervisionEditor(supervision, getTranslation("views.supervision.edit_supervision", supervision.getTitle()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a supervision.
     *
     * @param supervision the supervision to edit.
     * @param title       the title of the editor.
     * @param isCreation  indicates if the editor is for creating or updating the entity.
     */
    protected void openSupervisionEditor(Supervision supervision, String title, boolean isCreation) {
        final AbstractEntityEditor<Supervision> editor;
        if (isCreation) {
            editor = this.supervisionEditorFactory.createAdditionEditor(supervision, getLogger());
        } else {
            editor = this.supervisionEditorFactory.createUpdateEditor(supervision, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Supervision> refreshAll = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.supervisionService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshGrid();
        };
        final SerializableBiConsumer<Dialog, Supervision> refreshOne = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.supervisionService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshItem(entity);
        };
        ComponentFactory.openEditionModalDialog(title, editor, false,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<Supervision> createDeletionContextFor(Set<Supervision> entities) {
        return this.supervisionService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.supervisedPersonColumn.setHeader(getTranslation("views.supervised_person")); //$NON-NLS-1$
        this.typeColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
        this.dateColumn.setHeader(getTranslation("views.period")); //$NON-NLS-1$
        this.supervisorsColumn.setHeader(getTranslation("views.supervisors")); //$NON-NLS-1$
    }

    /**
     * Provider of data for supervisions to be displayed in the list of supervisions view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface SupervisionDataProvider {

        /**
         * Fetch supervisions data.
         *
         * @param supervisionService the service to have access to the JPA.
         * @param pageRequest        the request for paging the data.
         * @param filters            the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<Supervision> fetch(SupervisionService supervisionService, PageRequest pageRequest, AbstractFilters<Supervision> filters);

    }

    /**
     * UI and JPA filters for {@link StandardSupervisionListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class SupervisionFilters extends AbstractAuthenticatedUserDataFilters<Supervision> {

        private static final long serialVersionUID = 829919544629910175L;

        private Checkbox includeTypes;

        /**
         * Constructor.
         *
         * @param user     the connected user, or {@code null} if the filter does not care about a connected user.
         * @param onSearch the callback function for running the filtering.
         */
        public SupervisionFilters(AuthenticatedUser user, Runnable onSearch) {
            super(user, onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeTypes = new Checkbox(true);

            options.add(this.includeTypes);
        }

        @Override
        protected void resetFilters() {
            this.includeTypes.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForAuthenticatedUser(Root<Supervision> root, CriteriaQuery<?> query,
                                                               CriteriaBuilder criteriaBuilder, Person user) {
            final var crit1 = criteriaBuilder.equal(root.get("supervisedPerson").get("person"), user); //$NON-NLS-1$ //$NON-NLS-2$
            final var crit2 = criteriaBuilder.equal(root.get("supervisors").get("supervisor"), user); //$NON-NLS-1$ //$NON-NLS-2$
            return criteriaBuilder.or(crit1, crit2);
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Supervision> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeTypes.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("types")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeTypes.setLabel(getTranslation("views.filters.include_types")); //$NON-NLS-1$
        }

    }

}
