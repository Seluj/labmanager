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

package fr.utbm.ciad.labmanager.views.appviews.conferences;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService.ConferenceRankingUpdateInformation;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionProvider;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.appviews.conferences.ConferenceRankingUpdate.ConferenceNewInformation;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressExtension;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullValueValidator;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerFormWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerProgressionWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Predicate;
import org.arakhne.afc.progress.Progression;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Wizard for updating the conference rankings.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "updateconferenceranking", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class ConferenceRankingUpdaterWizard extends AbstractLabManagerWizard<ConferenceRankingUpdate> {

    private static final long serialVersionUID = 5701942600437345213L;

    /**
     * Constructor.
     *
     * @param loggerFactory     the factory for creating the loggers.
     * @param conferenceService the service for accessing the conference entities.
     */
    public ConferenceRankingUpdaterWizard(
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired ConferenceService conferenceService) {
        this(loggerFactory, conferenceService,
                defaultWizardConfiguration(null, false),
                new ConferenceRankingUpdate());
    }

    /**
     * Constructor.
     *
     * @param conferenceService the service for accessing the conference entities.
     * @param properties        the properties of the wizard.
     * @param context           the data context.
     */
    protected ConferenceRankingUpdaterWizard(
            ContextualLoggerFactory loggerFactory, ConferenceService conferenceService,
            WizardConfigurationProperties properties, ConferenceRankingUpdate context) {
        this(properties, loggerFactory, context, Arrays.asList(
                new ConferenceInputWizardStep(context),
                new ConferenceRankLoadingWizardStep(context, conferenceService),
                new ConferenceRankDownloadWizardStep(context, conferenceService),
                new ConferenceRankingSummaryWizardStep(context),
                new ConferenceRankSavingWizardStep(context, conferenceService)));
    }

    private ConferenceRankingUpdaterWizard(WizardConfigurationProperties properties,
                                           ContextualLoggerFactory loggerFactory,
                                           ConferenceRankingUpdate context, List<WizardStep<ConferenceRankingUpdate>> steps) {
        super(properties, loggerFactory, context, steps);
    }

    /**
     * Wizard step to input configuration parameters.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceInputWizardStep extends AbstractLabManagerFormWizardStep<ConferenceRankingUpdate> {

        private static final long serialVersionUID = -1563783404374252866L;

        private ComboBox<Integer> yearPicker;

        /**
         * Constructor.
         *
         * @param context the data context.
         */
        public ConferenceInputWizardStep(ConferenceRankingUpdate context) {
            super(context, ComponentFactory.getTranslation("views.conferences.updateRankings.step1.title"), 1); //$NON-NLS-1$
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            final var now = LocalDate.now();
            final var firstYear = now.getYear() - 10;
            final var lastYear = now.getYear() - 1;
            final var selectableYears = new ArrayList<Integer>();
            for (var i = lastYear; i >= firstYear; --i) {
                selectableYears.add(Integer.valueOf(i));
            }
            this.yearPicker = new ComboBox<>("", selectableYears); //$NON-NLS-1$
            this.yearPicker.setRequired(true);
            this.yearPicker.setWidth(6, Unit.EM);
            form.add(this.yearPicker, 1);

            this.binder.forField(this.yearPicker)
                    .withValidator(new NotNullValueValidator<>(ComponentFactory.getTranslation("views.conferences.updateRankings.step1.year.error"))) //$NON-NLS-1$
                    .bind(ConferenceRankingUpdate::getYear, ConferenceRankingUpdate::setYear);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            if (this.yearPicker != null) {
                this.yearPicker.setLabel(ComponentFactory.getTranslation(event.getLocale(), "views.conferences.updateRankings.step1.year")); //$NON-NLS-1$
                this.yearPicker.setHelperText(ComponentFactory.getTranslation(event.getLocale(), "views.conferences.updateRankings.step1.year.help")); //$NON-NLS-1$
            }
        }

    }

    /**
     * Wizard step for conference rank loading from the database.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceRankLoadingWizardStep extends AbstractLabManagerProgressionWizardStep<ConferenceRankingUpdate> {

        private static final long serialVersionUID = -7192963760929837830L;

        private final ConferenceService conferenceService;

        /**
         * Constructor.
         *
         * @param context           the data to be shared between the wizard steps.
         * @param conferenceService the service for accessing to the conferences' JPA entities.
         */
        public ConferenceRankLoadingWizardStep(ConferenceRankingUpdate context, ConferenceService conferenceService) {
            super(context, ComponentFactory.getTranslation("views.conferences.updateRankings.step2.title"), 2, 1, true, false);//$NON-NLS-1$
            this.conferenceService = conferenceService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.conferences.updateRankings.step2.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.conferences.updateRankings.step2.comment"))); //$NON-NLS-1$
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var terminationMessage = getWizard().orElseThrow().getTranslation("views.conferences.updateRankings.step2.conference_read"); //$NON-NLS-1$
            return () -> {
                final var identifiers = getContext().getEntityIdentifiers();
                progression.increment(5);
                final Consumer<Conference> callback = it -> {
                    // Force loading of quality indicators for the conferences
                    Hibernate.initialize(it.getQualityIndicators());
                };
                final List<Conference> conferences;
                if (identifiers == null || identifiers.isEmpty()) {
                    conferences = this.conferenceService.getAllConferences(callback);
                } else {
                    conferences = this.conferenceService.getAllConferences((root, query, criteriaBuilder) -> {
                        Predicate pred = null;
                        for (final var id : identifiers) {
                            final var p = criteriaBuilder.equal(root.get("id"), id); //$NON-NLS-1$
                            if (pred == null) {
                                pred = p;
                            } else {
                                pred = criteriaBuilder.or(pred, p);
                            }
                        }
                        return pred;
                    }, callback);
                }
                progression.increment(90);
                getContext().setConferences(conferences);
                return terminationMessage;
            };
        }

    }

    /**
     * Wizard step for downloading the ranking updates.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceRankDownloadWizardStep extends AbstractLabManagerProgressionWizardStep<ConferenceRankingUpdate> {

        private static final long serialVersionUID = 6901698283852123579L;

        private final ConferenceService conferenceService;

        /**
         * Constructor.
         *
         * @param context           the data to be shared between the wizard steps.
         * @param conferenceService the service for accessing to the conferences' JPA entities.
         */
        public ConferenceRankDownloadWizardStep(ConferenceRankingUpdate context, ConferenceService conferenceService) {
            super(context, ComponentFactory.getTranslation("views.conferences.updateRankings.step3.title"), 3, 1, true, false);//$NON-NLS-1$
            this.conferenceService = conferenceService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.conferences.updateRankings.step3.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.conferences.updateRankings.step3.comment"))); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            final var image = new Image(ComponentFactory.newStreamImage(ViewConstants.CORE_PORTAL_ICON), ViewConstants.CORE_PORTAL_BASE_URL);
            image.setMinHeight(ViewConstants.ICON_SIZE, Unit.POINTS);
            image.setMaxHeight(ViewConstants.MAX_ICON_SIZE, Unit.POINTS);
            image.setMinWidth(ViewConstants.ICON_SIZE, Unit.POINTS);
            image.setMaxWidth(ViewConstants.MAX_ICON_SIZE, Unit.POINTS);
            final var style = image.getStyle();
            style.setAlignSelf(AlignSelf.BASELINE);
            style.setMarginRight("var(--lumo-space-s)"); //$NON-NLS-1$
            return image;
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.conferences.updateRankings.step3.download_core"); //$NON-NLS-1$
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.conferences.updateRankings.step3.core_downloaded"); //$NON-NLS-1$
            return () -> {
                this.conferenceService.downloadConferenceIndicatorsFromCore(
                        getContext().getYear(), getContext().getConferences(),
                        getLogger(),
                        extendedProgression0,
                        (referenceYear, conferenceId, oldRanking, newRanking) -> getContext().addRanking(conferenceId, oldRanking, newRanking));
                return terminationMessage0;
            };
        }

    }

    /**
     * Wizard step to summarize the updates of the conference ranking informations.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceRankingSummaryWizardStep extends AbstractLabManagerWizardStep<ConferenceRankingUpdate> {

        private static final long serialVersionUID = -72237848939790719L;

        private Grid<ConferenceNewInformation> grid;

        private Column<ConferenceNewInformation> nameColumn;

        private Column<ConferenceNewInformation> coreColumn;

        /**
         * Constructor.
         *
         * @param context the data context.
         */
        public ConferenceRankingSummaryWizardStep(ConferenceRankingUpdate context) {
            super(context, ComponentFactory.getTranslation("views.conferences.updateRankings.step4.title"), 4); //$NON-NLS-1$
        }

        private static String toString(CoreRanking oldRanking, CoreRanking newRanking) {
            if (oldRanking != null && newRanking != null) {
                return oldRanking +
                        " \u2B95 " + //$NON-NLS-1$
                        newRanking;
            }
            return ""; //$NON-NLS-1$
        }

        @Override
        public Div getLayout() {
            this.grid = new Grid<>(ConferenceNewInformation.class, false);
            this.grid.setPageSize(ViewConstants.GRID_PAGE_SIZE);
            this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            this.grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
            this.grid.setSelectionMode(SelectionMode.NONE);

            this.nameColumn = this.grid.addColumn(info -> info.conference().getAcronymAndName())
                    .setAutoWidth(true).setSortable(true);
            this.coreColumn = this.grid.addColumn(info -> toString(info.oldRanking(), info.newRanking()))
                    .setAutoWidth(true).setSortable(true);

            this.grid.sort(GridSortOrder.asc(this.nameColumn).build());

            this.grid.setItems(new ListDataProvider<>(getContext().getConferenceUpdates().toList()));

            return new Div(this.grid);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            if (this.nameColumn != null) {
                this.nameColumn.setHeader(ComponentFactory.getTranslation("views.conferences.updateRankings.step4.conference_name")); //$NON-NLS-1$
            }
            if (this.coreColumn != null) {
                this.coreColumn.setHeader(ComponentFactory.getTranslation("views.conferences.updateRankings.step4.core")); //$NON-NLS-1$
            }
        }

        @Override
        public boolean isValid() {
            return this.grid.getListDataView().getItemCount() > 0;
        }

        @Override
        public boolean commit() {
            return true;
        }

    }

    /**
     * Wizard step for saving the ranking updates.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ConferenceRankSavingWizardStep extends AbstractLabManagerProgressionWizardStep<ConferenceRankingUpdate> {

        private static final long serialVersionUID = 6508993951334784831L;

        private final ConferenceService conferenceService;

        /**
         * Constructor.
         *
         * @param context           the data to be shared between the wizard steps.
         * @param conferenceService the service for accessing to the conferences' JPA entities.
         */
        public ConferenceRankSavingWizardStep(ConferenceRankingUpdate context, ConferenceService conferenceService) {
            super(context, ComponentFactory.getTranslation("views.conferences.updateRankings.step5.title"), 5, 1, false, true);//$NON-NLS-1$
            this.conferenceService = conferenceService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.conferences.updateRankings.step5.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.conferences.updateRankings.step5.comment"))); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            final var image = VaadinIcon.COG.create();
            final var style = image.getStyle();
            style.setAlignSelf(AlignSelf.BASELINE);
            style.setMarginRight("var(--lumo-space-s)"); //$NON-NLS-1$
            return image;
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.conferences.updateRankings.step5.saving"); //$NON-NLS-1$
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.conferences.updateRankings.step5.end"); //$NON-NLS-1$
            return () -> {
                final var context = getContext();
                this.conferenceService.updateConferenceIndicators(context.getYear(),
                        context.getConferenceUpdates().map(it -> {
                            final var conference = it.conference();
                            final var ranking = it.newRanking();
                            return new ConferenceRankingUpdateInformation(conference, ranking);
                        }).toList(),
                        getLogger(),
                        extendedProgression0);
                return terminationMessage0;
            };
        }

    }

}
