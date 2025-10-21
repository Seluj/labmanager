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

package fr.utbm.ciad.labmanager.views.components.teaching.views;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.teaching.TeachingService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.avatars.AvatarItem;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.teaching.editors.TeachingActivityEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.arakhne.afc.vmutil.FileSystem;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * List all the teaching activites.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardTeachingActivitiesListView extends AbstractEntityListView<TeachingActivity> {

    private static final long serialVersionUID = -2664722351226443633L;

    private final TeachingActivityDataProvider dataProvider;

    private final TeachingService teachingService;

    private final TeachingActivityEditorFactory teachingEditorFactory;
    private final DownloadableFileManager fileManager;
    private Column<TeachingActivity> titleColumn;
    private Column<TeachingActivity> levelColumn;
    private Column<TeachingActivity> universityColumn;
    private Column<TeachingActivity> countryColumn;
    private Column<TeachingActivity> periodColumn;
    private Column<TeachingActivity> teacherColumn;

    /**
     * Constructor.
     *
     * @param fileManager           the manager of the downloadable files.
     * @param authenticatedUser     the connected user.
     * @param messages              the accessor to the localized messages (spring layer).
     * @param loggerFactory         the factory to be used for the composite logger.
     * @param teachingService       the service for accessing the teaching activities.
     * @param teachingEditorFactory the factory for creating the teaching activity editors.
     */
    public StandardTeachingActivitiesListView(
            DownloadableFileManager fileManager,
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            TeachingService teachingService, TeachingActivityEditorFactory teachingEditorFactory) {
        super(TeachingActivity.class, authenticatedUser, messages, loggerFactory,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.teaching_activities.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.teaching_activities.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.teaching_activities.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.teaching_activities.delete_error")); //$NON-NLS-1$
        this.fileManager = fileManager;
        this.teachingService = teachingService;
        this.teachingEditorFactory = teachingEditorFactory;
        this.dataProvider = (ps, query, filters) -> ps.getAllActivities(query, filters, this::initializeEntityFromJPA);
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    private void initializeEntityFromJPA(TeachingActivity entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        Hibernate.initialize(entity.getPerson());
        Hibernate.initialize(entity.getUniversity());
    }

    @Override
    protected AbstractFilters<TeachingActivity> createFilters() {
        return new TeachingActivityFilters(getAuthenticatedUser(), this::refreshGrid);
    }

    private Component createNameComponent(TeachingActivity activity) {
        final var code = activity.getCode();
        final var title = activity.getTitle();
        final var slides = activity.getPathToSlides();

        final var avatar = new AvatarItem();
        avatar.setHeading(title);
        if (!Strings.isNullOrEmpty(code)) {
            avatar.setDescription(code);
        }
        var slidesFile = FileSystem.convertStringToFile(slides);
        var needPicture = true;
        if (slidesFile != null) {
            var picture = this.fileManager.toThumbnailFilename(slidesFile);
            picture = this.fileManager.normalizeForServerSide(picture);
            if (picture != null) {
                avatar.setAvatarResource(ComponentFactory.newStreamImage(picture));
                needPicture = false;
            }
        }
        if (needPicture) {
            avatar.setAvatarResource(ComponentFactory.newEmptyBackgroundStreamImage());
        }
        return avatar;
    }

    private String getLevelStudentTypeDegree(TeachingActivity activity) {
        final var level = activity.getLevel();
        final var studentType = activity.getStudentType();
        final var degree = activity.getDegree();
        final var label = new StringBuilder();
        label.append(level.getLabel(getMessageSourceAccessor(), getLocale()));
        label.append(" - "); //$NON-NLS-1$
        label.append(studentType.getLabel(getMessageSourceAccessor(), getLocale()));
        if (Strings.isNullOrEmpty(degree)) {
            label.append(" - "); //$NON-NLS-1$
            label.append(degree);
        }
        return label.toString();
    }

    private Component createUniversityComponent(TeachingActivity activity) {
        final var university = activity.getUniversity();
        if (university != null) {
            return ComponentFactory.newOrganizationAvatar(university, this.fileManager);
        }
        return new Span();
    }

    private Component createTeacherComponent(TeachingActivity activity) {
        final var person = activity.getPerson();
        if (person != null) {
            return ComponentFactory.newPersonAvatar(person);
        }
        return new Span();
    }

    private String getPeriodLabel(TeachingActivity activity) {
        final var startDate = activity.getStartDate();
        final var endDate = activity.getStartDate();
        if (startDate != null) {
            final var sy = startDate.getYear();
            if (endDate != null) {
                final var ey = endDate.getYear();
                if (sy != ey) {
                    return sy + "-" + ey; //$NON-NLS-1$
                }
            }
            return Integer.toString(sy);
        } else if (endDate != null) {
            final var ey = endDate.getYear();
            return Integer.toString(ey);
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    protected boolean createGridColumns(Grid<TeachingActivity> grid) {
        this.titleColumn = grid.addColumn(new ComponentRenderer<>(this::createNameComponent))
                .setAutoWidth(true)
                .setFrozen(true)
                .setSortProperty("code", "title"); //$NON-NLS-1$ //$NON-NLS-2$
        this.teacherColumn = grid.addColumn(new ComponentRenderer<>(this::createTeacherComponent))
                .setAutoWidth(true);
        this.levelColumn = grid.addColumn(this::getLevelStudentTypeDegree)
                .setAutoWidth(true);
        this.universityColumn = grid.addColumn(new ComponentRenderer<>(this::createUniversityComponent))
                .setAutoWidth(true);
        this.countryColumn = grid.addColumn(new ComponentRenderer<>(this::createCountryComponent))
                .setAutoWidth(true);
        this.periodColumn = grid.addColumn(this::getPeriodLabel)
                .setAutoWidth(true)
                .setSortProperty("startDate", "endDate"); //$NON-NLS-1$ //$NON-NLS-2$
        // Create the hover tool bar only if administrator role
        return isAdminRole();
    }

    private Component createCountryComponent(TeachingActivity activity) {
        final var university = activity.getUniversity();
        if (university != null) {
            final var country = university.getCountry();
            if (country != null) {
                final var name = new Span(country.getDisplayCountry(getLocale()));
                name.getStyle().set("margin-left", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$

                final var flag = new CountryFlag(country);
                flag.setSizeFromHeight(1, Unit.REM);

                final var layout = new HorizontalLayout(flag, name);
                layout.setSpacing(false);
                layout.setAlignItems(Alignment.CENTER);

                return layout;
            }
        }
        return new Span();
    }

    @Override
    protected List<Column<TeachingActivity>> getInitialSortingColumns() {
        return Collections.singletonList(this.titleColumn);
    }

    @Override
    protected FetchCallback<TeachingActivity, Void> getFetchCallback(AbstractFilters<TeachingActivity> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.teachingService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        openActivityEditor(new TeachingActivity(), getTranslation("views.teaching_activities.add_activity"), true); //$NON-NLS-1$
    }

    @Override
    protected void edit(TeachingActivity activity) {
        openActivityEditor(activity, getTranslation("views.teaching_activities.edit_activity", activity.getCodeOrTitle()), false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a teaching activity.
     *
     * @param activity   the teaching activity to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openActivityEditor(TeachingActivity activity, String title, boolean isCreation) {
        final AbstractEntityEditor<TeachingActivity> editor;
        if (isCreation) {
            editor = this.teachingEditorFactory.createAdditionEditor(activity, getLogger());
        } else {
            editor = this.teachingEditorFactory.createUpdateEditor(activity, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, TeachingActivity> refreshAll = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.teachingService.inSession(session -> {
                session.load(entity, Long.valueOf(entity.getId()));
                initializeEntityFromJPA(entity);
            });
            refreshGrid();
        };
        final SerializableBiConsumer<Dialog, TeachingActivity> refreshOne = (dialog, entity) -> {
            // The person should be loaded because it was not loaded before
            this.teachingService.inSession(session -> {
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
    protected EntityDeletingContext<TeachingActivity> createDeletionContextFor(Set<TeachingActivity> entities) {
        return this.teachingService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.titleColumn.setHeader(getTranslation("views.title")); //$NON-NLS-1$
        this.levelColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
        this.universityColumn.setHeader(getTranslation("views.university")); //$NON-NLS-1$
        this.countryColumn.setHeader(getTranslation("views.country")); //$NON-NLS-1$
        this.periodColumn.setHeader(getTranslation("views.period")); //$NON-NLS-1$
        this.teacherColumn.setHeader(getTranslation("views.teacher")); //$NON-NLS-1$
    }

    /**
     * Provider of data for teaching activities to be displayed in the list of activities view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface TeachingActivityDataProvider {

        /**
         * Fetch teaching activity data.
         *
         * @param teachingService the service to have access to the JPA.
         * @param pageRequest     the request for paging the data.
         * @param filters         the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<TeachingActivity> fetch(TeachingService teachingService, PageRequest pageRequest, AbstractFilters<TeachingActivity> filters);

    }

    /**
     * UI and JPA filters for {@link StandardTeachingActivitiesListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class TeachingActivityFilters extends AbstractAuthenticatedUserDataFilters<TeachingActivity> {

        private static final long serialVersionUID = -8070684808016589969L;

        private Checkbox includeCodesTitles;

        private Checkbox includeDegreesLevelsStudentTypes;

        private Checkbox includeUniversities;

        private Checkbox includePeriods;

        private Checkbox includePersons;

        /**
         * Constructor.
         *
         * @param user     the connected user, or {@code null} if the filter does not care about a connected user.
         * @param onSearch the callback function for running the filtering.
         */
        public TeachingActivityFilters(AuthenticatedUser user, Runnable onSearch) {
            super(user, onSearch);
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeCodesTitles = new Checkbox(true);
            this.includeDegreesLevelsStudentTypes = new Checkbox(true);
            this.includeUniversities = new Checkbox(true);
            this.includePeriods = new Checkbox(true);
            this.includePersons = new Checkbox(true);

            options.add(this.includeCodesTitles, this.includeDegreesLevelsStudentTypes,
                    this.includeUniversities, this.includePeriods, this.includePersons);
        }

        @Override
        protected void resetFilters() {
            this.includeCodesTitles.setValue(Boolean.TRUE);
            this.includeDegreesLevelsStudentTypes.setValue(Boolean.TRUE);
            this.includeUniversities.setValue(Boolean.TRUE);
            this.includePeriods.setValue(Boolean.TRUE);
            this.includePersons.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForAuthenticatedUser(Root<TeachingActivity> root, CriteriaQuery<?> query,
                                                               CriteriaBuilder criteriaBuilder, Person user) {
            return criteriaBuilder.equal(root.get("person"), user); //$NON-NLS-1$
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<TeachingActivity> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeCodesTitles.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywords)); //$NON-NLS-1$
            }
            if (this.includeDegreesLevelsStudentTypes.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("level")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("degree")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("studentType")), keywords)); //$NON-NLS-1$
            }
            if (this.includeUniversities.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("university")), keywords)); //$NON-NLS-1$
            }
            if (this.includePeriods.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("startDate")), keywords)); //$NON-NLS-1$
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("endDate")), keywords)); //$NON-NLS-1$
            }
            if (this.includePersons.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("person")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeCodesTitles.setLabel(getTranslation("views.filters.include_titles")); //$NON-NLS-1$
            this.includeDegreesLevelsStudentTypes.setLabel(getTranslation("views.filters.include_types")); //$NON-NLS-1$
            this.includeUniversities.setLabel(getTranslation("views.filters.include_universities")); //$NON-NLS-1$
            this.includePeriods.setLabel(getTranslation("views.filters.include_periods")); //$NON-NLS-1$
            this.includePersons.setLabel(getTranslation("views.filters.include_persons")); //$NON-NLS-1$
        }

    }

}
