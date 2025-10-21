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

package fr.utbm.ciad.labmanager.views.components.conferences.views;

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
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.ranking.AbstractAnnualRankingField;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
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
 * List all the conferences.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardConferenceListView extends AbstractEntityListView<Conference> {

    private static final long serialVersionUID = 7576767630790475598L;

    private final int referenceYear;

    private final ConferenceDataProvider dataProvider;

    private final ConferenceService conferenceService;

    private final ConferenceEditorFactory conferenceEditorFactory;

    private Column<Conference> nameColumn;

    private Column<Conference> publisherColumn;

    private Column<Conference> coreRankingColumn;

    private Column<Conference> paperCountColumn;

    private Column<Conference> validationColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser       the connected user.
     * @param messages                the accessor to the localized messages (spring layer).
     * @param loggerFactory           the factory to be used for the composite logger.
     * @param conferenceService       the service for accessing the sonferences.
     * @param conferenceEditorFactory the factory for creating the conference editors.
     */
    public StandardConferenceListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            ConferenceService conferenceService, ConferenceEditorFactory conferenceEditorFactory) {
        super(Conference.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.conferences.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.conferences.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.conferences.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.conferences.delete_error")); //$NON-NLS-1$
        this.conferenceService = conferenceService;
        this.conferenceEditorFactory = conferenceEditorFactory;
        // The reference year cannot be the current year because ranking of conferences is not done
        this.referenceYear = AbstractAnnualRankingField.getDefaultReferenceYear();
        this.dataProvider = (ps, query, filters) -> ps.getAllConferences(query, filters, this::initializeEntityFromJPA);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private static String getPaperCount(Conference conference) {
        return Integer.toString(conference.getPublishedPapers().size());
    }

    private void initializeEntityFromJPA(Conference entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        entity.getCoreIndexByYear(this.referenceYear);
        entity.getPublishedPapers().size();
    }

    @Override
    protected AbstractFilters<Conference> createFilters() {
        return new ConferenceFilters(this::refreshGrid);
    }

    private String getConferenceName(Conference conference) {
        return conference.getAcronym() + " - " + conference.getName(); //$NON-NLS-1$
    }

    @Override
    protected boolean createGridColumns(Grid<Conference> grid) {
        this.nameColumn = grid.addColumn(this::getConferenceName)
                .setAutoWidth(true)
                .setFrozen(true)
                .setSortProperty("name"); //$NON-NLS-1$
        this.publisherColumn = grid.addColumn(conference -> conference.getPublisher())
                .setAutoWidth(true)
                .setSortProperty("publisher"); //$NON-NLS-1$
        this.coreRankingColumn = grid.addColumn(new ComponentRenderer<>(this::getCoreRanking))
                .setAutoWidth(false);
        this.paperCountColumn = grid.addColumn(conference -> getPaperCount(conference))
                .setAutoWidth(false);
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

    private Component getCoreRanking(Conference conference) {
        final var rank = CoreRanking.normalize(conference.getCoreIndexByYear(this.referenceYear));
        final var span = new Span();
        if (rank != CoreRanking.NR) {
            span.setText(rank.toString());
            final var id = conference.getCoreId();
            if (Strings.isNullOrEmpty(id)) {
                span.setTitle(getTranslation("views.conferences.ranking_details0", Integer.toString(this.referenceYear))); //$NON-NLS-1$
            } else {
                span.setTitle(getTranslation("views.conferences.ranking_details1", Integer.toString(this.referenceYear), id)); //$NON-NLS-1$
            }
            if (Strings.isNullOrEmpty(id)) {
                span.getStyle().setColor("var(--lumo-error-color-50pct)"); //$NON-NLS-1$
            }
        }
        return span;
    }

    @Override
    protected List<Column<Conference>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<Conference, Void> getFetchCallback(AbstractFilters<Conference> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.conferenceService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openConferenceEditor(new Conference(), getTranslation("views.conferences.add_conference"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(Conference conference) {
        openConferenceEditor(conference, getTranslation("views.conferences.edit_conference", conference.getName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a conference.
     *
     * @param conference the conference to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor must be open for creating a new entity or updating an existing one.
     */
    protected void openConferenceEditor(Conference conference, String title, boolean isCreation) {
        final AbstractEntityEditor<Conference> editor;
        if (isCreation) {
            editor = this.conferenceEditorFactory.createAdditionEditor(conference, getLogger());
        } else {
            editor = this.conferenceEditorFactory.createUpdateEditor(conference, getLogger());
        }
        openConferenceEditor(editor, title);
    }

    /**
     * Show the editor of a conference.
     *
     * @param editor the editor to show.
     * @param title  the title of the editor.
     */
    private void openConferenceEditor(AbstractEntityEditor<Conference> editor, String title) {
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Conference> refreshAll = (dialog, entity) -> {
            // The number of papers should be loaded because it was not loaded before
            this.conferenceService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshGrid();
        };
        final SerializableBiConsumer<Dialog, Conference> refreshOne = (dialog, entity) -> {
            // The number of papers should be loaded because it was not loaded before
            this.conferenceService.inSession(session -> {
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
    protected EntityDeletingContext<Conference> createDeletionContextFor(Set<Conference> entities) {
        return this.conferenceService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.publisherColumn.setHeader(getTranslation("views.publisher")); //$NON-NLS-1$
        this.coreRankingColumn.setHeader(getTranslation("views.conferences.coreRanking")); //$NON-NLS-1$
        this.paperCountColumn.setHeader(getTranslation("views.conferences.paperCount")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
    }

    /**
     * Provider of data for conferences to be displayed in the list of conferences view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface ConferenceDataProvider {

        /**
         * Fetch conference data.
         *
         * @param conferenceService the service to have access to the JPA.
         * @param pageRequest       the request for paging the data.
         * @param filters           the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<Conference> fetch(ConferenceService conferenceService, PageRequest pageRequest, AbstractFilters<Conference> filters);

    }

    /**
     * UI and JPA filters for {@link StandardConferenceListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceFilters extends AbstractFilters<Conference> {

        private static final long serialVersionUID = -5029775320994118621L;

        private Checkbox includeNames;

        private Checkbox includePublishers;

        /**
         * Constructor.
         *
         * @param onSearch
         */
        public ConferenceFilters(Runnable onSearch) {
            super(onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeNames = new Checkbox(true);
            this.includePublishers = new Checkbox(true);

            options.add(this.includeNames, this.includePublishers);
        }

        @Override
        protected void resetFilters() {
            this.includeNames.setValue(Boolean.TRUE);
            this.includePublishers.setValue(Boolean.TRUE);
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Conference> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keywords)); //$NON-NLS-1$
            }
            if (this.includePublishers.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeNames.setLabel(getTranslation("views.filters.include_names")); //$NON-NLS-1$
            this.includePublishers.setLabel(getTranslation("views.filters.include_publishers")); //$NON-NLS-1$
        }

    }

}
