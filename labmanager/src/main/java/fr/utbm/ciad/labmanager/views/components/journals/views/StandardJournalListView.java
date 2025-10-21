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

package fr.utbm.ciad.labmanager.views.components.journals.views;

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
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the journals.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardJournalListView extends AbstractEntityListView<Journal> {

    private static final long serialVersionUID = -284476504810879812L;

    private final JournalDataProvider dataProvider;

    private final int referenceYear;

    private final JournalService journalService;

    private final JournalEditorFactory journalEditorFactory;

    private Column<Journal> nameColumn;

    private Column<Journal> publisherColumn;

    private Column<Journal> scimagoRankingColumn;

    private Column<Journal> wosRankingColumn;

    private Column<Journal> impactFactorColumn;

    private Column<Journal> paperCountColumn;

    private Column<Journal> validationColumn;

    /**
     * Constructor.
     *
     * @param authenticatedUser    the connected user.
     * @param messages             the accessor to the localized messages (spring layer).
     * @param loggerFactory        the factory to be used for the composite logger.
     * @param journalService       the service for accessing the journals.
     * @param journalEditorFactory the factory for creating journal editors.
     */
    public StandardJournalListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            JournalService journalService, JournalEditorFactory journalEditorFactory) {
        super(Journal.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.journals.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.journals.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.journals.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.journals.delete_error")); //$NON-NLS-1$
        this.journalService = journalService;
        this.journalEditorFactory = journalEditorFactory;
        // The reference year cannot be the current year because ranking of journals is not done
        this.referenceYear = LocalDate.now().getYear() - 1;
        this.dataProvider = (ps, query, filters) -> ps.getAllJournals(query, filters, this::initializeEntityFromJPA);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private static String getPaperCount(Journal journal) {
        return Integer.toString(journal.getPublishedPapers().size());
    }

    private void initializeEntityFromJPA(Journal entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        entity.getPublishedPapers().size();
        entity.getScimagoQIndexByYear(this.referenceYear);
        entity.getWosQIndexByYear(this.referenceYear);
    }

    @Override
    protected AbstractFilters<Journal> createFilters() {
        return new JournalFilters(this::refreshGrid);
    }

    @Override
    protected boolean createGridColumns(Grid<Journal> grid) {
        this.nameColumn = grid.addColumn(journal -> journal.getJournalName())
                .setAutoWidth(true)
                .setFrozen(true)
                .setSortProperty("journalName"); //$NON-NLS-1$
        this.publisherColumn = grid.addColumn(journal -> journal.getPublisher())
                .setAutoWidth(true)
                .setSortProperty("publisher"); //$NON-NLS-1$
        this.scimagoRankingColumn = grid.addColumn(new ComponentRenderer<>(this::getScimagoQuartile))
                .setAutoWidth(false);
        this.wosRankingColumn = grid.addColumn(new ComponentRenderer<>(this::getWosQuartile))
                .setAutoWidth(false);
        this.impactFactorColumn = grid.addColumn(new ComponentRenderer<>(this::getImpactFactor))
                .setAutoWidth(false);
        this.paperCountColumn = grid.addColumn(journal -> getPaperCount(journal))
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

    private Component getScimagoQuartile(Journal journal) {
        final var rank = QuartileRanking.normalize(journal.getScimagoQIndexByYear(this.referenceYear));
        final var span = new Span();
        if (rank != QuartileRanking.NR) {
            span.setText(rank.toString());
            final var id = journal.getScimagoId();
            final var category = journal.getScimagoCategory();
            if (Strings.isNullOrEmpty(id)) {
                if (Strings.isNullOrEmpty(category)) {
                    span.setTitle(getTranslation("views.journals.ranking_details0", Integer.toString(this.referenceYear))); //$NON-NLS-1$
                } else {
                    span.setTitle(getTranslation("views.journals.ranking_details1", Integer.toString(this.referenceYear), category)); //$NON-NLS-1$
                }
            } else {
                if (Strings.isNullOrEmpty(category)) {
                    span.setTitle(getTranslation("views.journals.ranking_details2", Integer.toString(this.referenceYear), id)); //$NON-NLS-1$
                } else {
                    span.setTitle(getTranslation("views.journals.ranking_details3", Integer.toString(this.referenceYear), id, category)); //$NON-NLS-1$
                }
            }
            if (Strings.isNullOrEmpty(id)) {
                span.getStyle().setColor("var(--lumo-error-color-50pct)"); //$NON-NLS-1$
            }
        }
        return span;
    }

    private Component getWosQuartile(Journal journal) {
        final var rank = QuartileRanking.normalize(journal.getWosQIndexByYear(this.referenceYear));
        final var span = new Span();
        if (rank != QuartileRanking.NR) {
            span.setText(rank.toString());
            final var id = journal.getWosId();
            final var category = journal.getWosCategory();
            if (Strings.isNullOrEmpty(id)) {
                if (Strings.isNullOrEmpty(category)) {
                    span.setTitle(getTranslation("views.journals.ranking_details0", Integer.toString(this.referenceYear))); //$NON-NLS-1$
                } else {
                    span.setTitle(getTranslation("views.journals.ranking_details1", Integer.toString(this.referenceYear), category)); //$NON-NLS-1$
                }
            } else {
                if (Strings.isNullOrEmpty(category)) {
                    span.setTitle(getTranslation("views.journals.ranking_details2", Integer.toString(this.referenceYear), id)); //$NON-NLS-1$
                } else {
                    span.setTitle(getTranslation("views.journals.ranking_details3", Integer.toString(this.referenceYear), id, category)); //$NON-NLS-1$
                }
            }
            if (Strings.isNullOrEmpty(id)) {
                span.getStyle().setColor("var(--lumo-error-color-50pct)"); //$NON-NLS-1$
            }
        }
        return span;
    }

    private Component getImpactFactor(Journal journal) {
        final var impactFactor = journal.getImpactFactorByYear(this.referenceYear);
        final var span = new Span();
        if (impactFactor > 0f) {
            final var fmt = new DecimalFormat("#0.00"); //$NON-NLS-1$
            span.setText(fmt.format(impactFactor));
        }
        return span;
    }

    @Override
    protected List<Column<Journal>> getInitialSortingColumns() {
        return Collections.singletonList(this.nameColumn);
    }

    @Override
    protected FetchCallback<Journal, Void> getFetchCallback(AbstractFilters<Journal> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.journalService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openJournalEditor(new Journal(), getTranslation("views.journals.add_journal"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(Journal journal) {
        openJournalEditor(journal, getTranslation("views.journals.edit_journal", journal.getJournalName()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of an journal.
     *
     * @param journal    the journal to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor must be open for the creation of a journal entity or for the update of an existing journal entity.
     */
    protected void openJournalEditor(Journal journal, String title, boolean isCreation) {
        final AbstractEntityEditor<Journal> editor;
        if (isCreation) {
            editor = this.journalEditorFactory.createAdditionEditor(journal, getLogger());
        } else {
            editor = this.journalEditorFactory.createUpdateEditor(journal, getLogger());
        }
        openJournalEditor(editor, title);
    }

    /**
     * Show the editor of an journal.
     *
     * @param editor the editor to show.
     * @param title  the title of the editor.
     */
    private void openJournalEditor(AbstractEntityEditor<Journal> editor, String title) {
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Journal> refreshAll = (dialog, entity) -> {
            // The number of papers should be loaded because it was not loaded before
            this.journalService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshGrid();
        };
        final SerializableBiConsumer<Dialog, Journal> refreshOne = (dialog, entity) -> {
            // The number of papers should be loaded because it was not loaded before
            this.journalService.inSession(session -> {
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
    protected EntityDeletingContext<Journal> createDeletionContextFor(Set<Journal> entities) {
        return this.journalService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.nameColumn.setHeader(getTranslation("views.name")); //$NON-NLS-1$
        this.publisherColumn.setHeader(getTranslation("views.publisher")); //$NON-NLS-1$
        this.scimagoRankingColumn.setHeader(getTranslation("views.journals.scimagoRanking")); //$NON-NLS-1$
        this.wosRankingColumn.setHeader(getTranslation("views.journals.wosRanking")); //$NON-NLS-1$
        this.impactFactorColumn.setHeader(getTranslation("views.journals.impactFactor")); //$NON-NLS-1$
        this.paperCountColumn.setHeader(getTranslation("views.journals.paperCount")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
    }

    /**
     * Provider of data for journals to be displayed in the list of journals view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface JournalDataProvider {

        /**
         * Fetch address data.
         *
         * @param journalService the service to have access to the JPA.
         * @param pageRequest    the request for paging the data.
         * @param filters        the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<Journal> fetch(JournalService journalService, PageRequest pageRequest, AbstractFilters<Journal> filters);

    }

    /**
     * UI and JPA filters for {@link StandardJournalListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class JournalFilters extends AbstractFilters<Journal> {

        private static final long serialVersionUID = -2926094704107561306L;

        private Checkbox includeNames;

        private Checkbox includePublishers;

        /**
         * Constructor.
         *
         * @param onSearch
         */
        public JournalFilters(Runnable onSearch) {
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
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Journal> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeNames.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("journalName")), keywords)); //$NON-NLS-1$
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
