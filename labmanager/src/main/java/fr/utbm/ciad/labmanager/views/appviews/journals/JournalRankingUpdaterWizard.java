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

package fr.utbm.ciad.labmanager.views.appviews.journals;

import com.vaadin.componentfactory.ToggleButton;
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
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.journal.JournalService.JournalRankingUpdateInformation;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionProvider;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.appviews.journals.JournalRankingUpdate.JournalNewInformation;
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
 * Wizard for updating the journal rankings.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "updatejournalranking", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class JournalRankingUpdaterWizard extends AbstractLabManagerWizard<JournalRankingUpdate> {

    private static final long serialVersionUID = -3604347983449875564L;

    /**
     * Constructor.
     *
     * @param loggerFactory  the factory of the loggers.
     * @param journalService the service for accessing the journal entities.
     */
    public JournalRankingUpdaterWizard(
            @Autowired ContextualLoggerFactory loggerFactory,
            @Autowired JournalService journalService) {
        this(loggerFactory, journalService,
                defaultWizardConfiguration(null, false),
                new JournalRankingUpdate());
    }

    /**
     * Constructor.
     *
     * @param loggerFactory  the factory of the loggers.
     * @param journalService the service for accessing the journal entities.
     * @param properties     the properties of the wizard.
     * @param context        the data context.
     */
    protected JournalRankingUpdaterWizard(
            ContextualLoggerFactory loggerFactory, JournalService journalService,
            WizardConfigurationProperties properties, JournalRankingUpdate context) {
        this(properties, loggerFactory, context, Arrays.asList(
                new JournalInputWizardStep(context),
                new JournalRankLoadingWizardStep(context, journalService),
                new JournalRankDownloadWizardStep(context, journalService),
                new JournalRankingSummaryWizardStep(context),
                new JournalRankSavingWizardStep(context, journalService)));
    }

    private JournalRankingUpdaterWizard(WizardConfigurationProperties properties,
                                        ContextualLoggerFactory loggerFactory, JournalRankingUpdate context,
                                        List<WizardStep<JournalRankingUpdate>> steps) {
        super(properties, loggerFactory, context, steps);
    }

    private static boolean isEnabled(ToggleButton button) {
        assert button != null;
        final var value = button.getValue();
        return value == null || value.booleanValue();
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
    protected static class JournalInputWizardStep extends AbstractLabManagerFormWizardStep<JournalRankingUpdate> {

        private static final long serialVersionUID = 5165672963156038933L;

        private ComboBox<Integer> yearPicker;

        private ToggleButton wosEnable;

        private ToggleButton scimagoEnable;

        private ToggleButton impactFactorsEnable;

        /**
         * Constructor.
         *
         * @param context the data context.
         */
        public JournalInputWizardStep(JournalRankingUpdate context) {
            super(context, ComponentFactory.getTranslation("views.journals.updateRankings.step1.title"), 1); //$NON-NLS-1$
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            if (super.isValid()) {
                return isEnabled(this.wosEnable) || isEnabled(this.scimagoEnable) || isEnabled(this.impactFactorsEnable);
            }
            return false;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            this.wosEnable = new ToggleButton();
            this.wosEnable.setWidthFull();
            this.wosEnable.addValueChangeListener(it -> updateButtonStateForNextStep());
            form.add(this.wosEnable, 2);

            this.scimagoEnable = new ToggleButton();
            this.scimagoEnable.setWidthFull();
            this.scimagoEnable.addValueChangeListener(it -> updateButtonStateForNextStep());
            form.add(this.scimagoEnable, 2);

            this.impactFactorsEnable = new ToggleButton();
            this.impactFactorsEnable.setWidthFull();
            this.impactFactorsEnable.addValueChangeListener(it -> updateButtonStateForNextStep());
            form.add(this.impactFactorsEnable, 2);

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
                    .withValidator(new NotNullValueValidator<>(ComponentFactory.getTranslation("views.journals.updateRankings.step1.year.error"))) //$NON-NLS-1$
                    .bind(JournalRankingUpdate::getYear, JournalRankingUpdate::setYear);
            this.binder.forField(this.wosEnable).bind(JournalRankingUpdate::getWosEnable, JournalRankingUpdate::setWosEnable);
            this.binder.forField(this.scimagoEnable).bind(JournalRankingUpdate::getScimagoEnable, JournalRankingUpdate::setScimagoEnable);
            this.binder.forField(this.impactFactorsEnable).bind(JournalRankingUpdate::getImpactFactorsEnable, JournalRankingUpdate::setImpactFactorsEnable);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            if (this.yearPicker != null) {
                this.yearPicker.setLabel(ComponentFactory.getTranslation(event.getLocale(), "views.journals.updateRankings.step1.year")); //$NON-NLS-1$
                this.yearPicker.setHelperText(ComponentFactory.getTranslation(event.getLocale(), "views.journals.updateRankings.step1.year.help")); //$NON-NLS-1$
            }
            if (this.wosEnable != null) {
                this.wosEnable.setLabel(ComponentFactory.getTranslation(event.getLocale(), "views.journals.updateRankings.step1.enable_wos")); //$NON-NLS-1$
            }
            if (this.scimagoEnable != null) {
                this.scimagoEnable.setLabel(ComponentFactory.getTranslation(event.getLocale(), "views.journals.updateRankings.step1.enable_scimago")); //$NON-NLS-1$
            }
            if (this.impactFactorsEnable != null) {
                this.impactFactorsEnable.setLabel(ComponentFactory.getTranslation(event.getLocale(), "views.journals.updateRankings.step1.enable_impact_factors")); //$NON-NLS-1$
            }
        }

    }

    /**
     * Wizard step for journal rank loading from the database.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class JournalRankLoadingWizardStep extends AbstractLabManagerProgressionWizardStep<JournalRankingUpdate> {

        private static final long serialVersionUID = 4363978455050034273L;

        private final JournalService journalService;

        /**
         * Constructor.
         *
         * @param context        the data to be shared between the wizard steps.
         * @param journalService the service for accessing to the journals' JPA entities.
         */
        public JournalRankLoadingWizardStep(JournalRankingUpdate context, JournalService journalService) {
            super(context, ComponentFactory.getTranslation("views.journals.updateRankings.step2.title"), 2, 1, true, false);//$NON-NLS-1$
            this.journalService = journalService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.journals.updateRankings.step2.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.journals.updateRankings.step2.comment"))); //$NON-NLS-1$
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var terminationMessage = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step2.journal_read"); //$NON-NLS-1$
            return () -> {
                final var identifiers = getContext().getEntityIdentifiers();
                progression.increment(5);
                final Consumer<Journal> callback = it -> {
                    // Force loading of quality indicators for the journals
                    Hibernate.initialize(it.getQualityIndicators());
                };
                final List<Journal> journals;
                if (identifiers == null || identifiers.isEmpty()) {
                    journals = this.journalService.getAllJournals(callback);
                } else {
                    journals = this.journalService.getAllJournals((root, query, criteriaBuilder) -> {
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
                getContext().setJournals(journals);
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
    protected static class JournalRankDownloadWizardStep extends AbstractLabManagerProgressionWizardStep<JournalRankingUpdate> {

        private static final long serialVersionUID = 2565280216469508988L;

        private final JournalService journalService;

        /**
         * Constructor.
         *
         * @param context        the data to be shared between the wizard steps.
         * @param journalService the service for accessing to the journals' JPA entities.
         */
        public JournalRankDownloadWizardStep(JournalRankingUpdate context, JournalService journalService) {
            super(context, ComponentFactory.getTranslation("views.journals.updateRankings.step3.title"), 3, 2, true, false);//$NON-NLS-1$
            this.journalService = journalService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.journals.updateRankings.step3.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.journals.updateRankings.step3.comment"))); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            Image image;
            switch (taskNo) {
                case 0:
                    image = new Image(ComponentFactory.newStreamImage(ViewConstants.SCIMAGO_ICON), ViewConstants.SCIMAGO_BASE_URL);
                    break;
                case 1:
                    image = new Image(ComponentFactory.newStreamImage(ViewConstants.WOS_ICON), ViewConstants.WOSINFO_BASE_URL);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
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
            switch (taskNo) {
                case 0:
                    final var pattern0 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step3.download_scimago"); //$NON-NLS-1$
                    final var extendedProgression0 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern0, it));
                    final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step3.scimago_downloaded"); //$NON-NLS-1$
                    return () -> {
                        if (getContext().getScimagoEnable()) {
                            getContext().clearScimagoRankings();
                            this.journalService.downloadJournalIndicatorsFromScimago(
                                    getContext().getYear(), getContext().getJournals(),
                                    getLogger(), extendedProgression0,
                                    (referenceYear, journalId, scientificField, oldQuartile, newQuartiles) -> getContext().addScimagoRanking(journalId, oldQuartile, newQuartiles));
                        } else {
                            progression.end();
                        }
                        return terminationMessage0;
                    };
                case 1:
                    final var pattern1 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step3.download_wos"); //$NON-NLS-1$
                    final var extendedProgression1 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern1, it));
                    final var terminationMessage1 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step3.wos_downloaded"); //$NON-NLS-1$
                    return () -> {
                        final var ewos = getContext().getWosEnable();
                        final var eif = getContext().getImpactFactorsEnable();
                        if (ewos || eif) {
                            if (ewos) {
                                getContext().clearWosRankings();
                            }
                            if (eif) {
                                getContext().clearImpactFactors();
                            }
                            this.journalService.downloadJournalIndicatorsFromWoS(
                                    getContext().getYear(), getContext().getJournals(),
                                    getLogger(), extendedProgression1,
                                    (referenceYear, journalId, scientificField, oldQuartile, newQuartiles, oldImpact, newImpact) -> {
                                        if (ewos) {
                                            getContext().addWosRanking(journalId, oldQuartile, newQuartiles);
                                        }
                                        if (eif) {
                                            getContext().addImpactFactor(journalId, oldImpact, newImpact);
                                        }
                                    });
                        } else {
                            progression.end();
                        }
                        return terminationMessage1;
                    };
                default:
                    throw new IllegalArgumentException();
            }
        }

    }

    /**
     * Wizard step to summarize the updates of the journal ranking informations.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class JournalRankingSummaryWizardStep extends AbstractLabManagerWizardStep<JournalRankingUpdate> {

        private static final long serialVersionUID = -5461457655161100043L;

        private Grid<JournalNewInformation> grid;

        private Column<JournalNewInformation> nameColumn;

        private Column<JournalNewInformation> publisherColumn;

        private Column<JournalNewInformation> issnColumn;

        private Column<JournalNewInformation> scimagoColumn;

        private Column<JournalNewInformation> wosColumn;

        private Column<JournalNewInformation> impactFactorColumn;

        /**
         * Constructor.
         *
         * @param context the data context.
         */
        public JournalRankingSummaryWizardStep(JournalRankingUpdate context) {
            super(context, ComponentFactory.getTranslation("views.journals.updateRankings.step4.title"), 4); //$NON-NLS-1$
        }

        private static String toString(QuartileRanking oldQuartile, QuartileRanking newQuartile) {
            if (oldQuartile != null && newQuartile != null) {
                return oldQuartile.name() +
                        " \u2B95 " + //$NON-NLS-1$
                        newQuartile.name();
            }
            return ""; //$NON-NLS-1$
        }

        private static String toString(Float oldFactor, Float newFactor) {
            if (oldFactor != null && newFactor != null) {
                return String.format("%1.3f", oldFactor) + //$NON-NLS-1$
                        " \u2B95 " + //$NON-NLS-1$
                        String.format("%1.3f", newFactor) //$NON-NLS-1$
                        ;
            }
            return ""; //$NON-NLS-1$
        }

        @Override
        public Div getLayout() {
            this.grid = new Grid<>(JournalNewInformation.class, false);
            this.grid.setPageSize(ViewConstants.GRID_PAGE_SIZE);
            this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
            this.grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
            this.grid.setSelectionMode(SelectionMode.NONE);

            this.nameColumn = this.grid.addColumn(info -> info.journal().getJournalName())
                    .setAutoWidth(true).setSortable(true);
            this.publisherColumn = this.grid.addColumn(info -> info.journal().getPublisher())
                    .setAutoWidth(true).setSortable(true);
            this.issnColumn = this.grid.addColumn(info -> info.journal().getISSN())
                    .setAutoWidth(true).setSortable(true);
            if (getContext().getScimagoEnable()) {
                this.scimagoColumn = this.grid.addColumn(info -> toString(info.oldScimago(), info.newScimago()))
                        .setAutoWidth(true).setSortable(true);
            } else {
                this.scimagoColumn = null;
            }
            if (getContext().getWosEnable()) {
                this.wosColumn = this.grid.addColumn(info -> toString(info.oldWos(), info.newWos()))
                        .setAutoWidth(true).setSortable(true);
            } else {
                this.wosColumn = null;
            }
            if (getContext().getImpactFactorsEnable()) {
                this.impactFactorColumn = this.grid.addColumn(info -> toString(info.oldImpactFactor(), info.newImpactFactor()))
                        .setAutoWidth(true).setSortable(true);
            } else {
                this.impactFactorColumn = null;
            }

            this.grid.sort(GridSortOrder.asc(this.nameColumn).build());

            this.grid.setItems(new ListDataProvider<>(getContext().getJournalUpdates().toList()));

            return new Div(this.grid);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            if (this.nameColumn != null) {
                this.nameColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.journal_name")); //$NON-NLS-1$
            }
            if (this.publisherColumn != null) {
                this.publisherColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.publisher")); //$NON-NLS-1$
            }
            if (this.issnColumn != null) {
                this.issnColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.issn")); //$NON-NLS-1$
            }
            if (this.scimagoColumn != null) {
                this.scimagoColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.scimago")); //$NON-NLS-1$
            }
            if (this.wosColumn != null) {
                this.wosColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.wos")); //$NON-NLS-1$
            }
            if (this.impactFactorColumn != null) {
                this.impactFactorColumn.setHeader(ComponentFactory.getTranslation("views.journals.updateRankings.step4.impactFactor")); //$NON-NLS-1$
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
    protected static class JournalRankSavingWizardStep extends AbstractLabManagerProgressionWizardStep<JournalRankingUpdate> {

        private static final long serialVersionUID = 9014929025845066499L;

        private final JournalService journalService;

        /**
         * Constructor.
         *
         * @param context        the data to be shared between the wizard steps.
         * @param journalService the service for accessing to the journals' JPA entities.
         */
        public JournalRankSavingWizardStep(JournalRankingUpdate context, JournalService journalService) {
            super(context, ComponentFactory.getTranslation("views.journals.updateRankings.step5.title"), 5, 1, false, true);//$NON-NLS-1$
            this.journalService = journalService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.journals.updateRankings.step5.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.journals.updateRankings.step5.comment"))); //$NON-NLS-1$
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
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step5.saving"); //$NON-NLS-1$
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.journals.updateRankings.step5.end"); //$NON-NLS-1$
            return () -> {
                final var context = getContext();
                this.journalService.updateJournalIndicators(context.getYear(),
                        context.getJournalUpdates().map(it -> {
                            final var journal = it.journal();
                            final var scimago = it.newScimago();
                            final var wos = it.newWos();
                            final var factor = it.newImpactFactor();
                            return new JournalRankingUpdateInformation(journal, scimago, wos, factor);
                        }).toList(),
                        context.getScimagoEnable(), context.getWosEnable(), context.getImpactFactorsEnable(),
                        getLogger(), extendedProgression0);
                return terminationMessage0;
            };
        }

    }

}
