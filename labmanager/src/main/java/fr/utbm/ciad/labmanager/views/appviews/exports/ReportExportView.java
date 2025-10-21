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

package fr.utbm.ciad.labmanager.views.appviews.exports;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.admin.DatabaseService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.DownloadableFileDescription;
import fr.utbm.ciad.labmanager.utils.io.IoConstants;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.download.DownloadBigButton;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import jakarta.annotation.security.PermitAll;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Enable to export annual reports.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "exportreports", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ReportExportView extends AbstractLoggerComposite<FlexLayout> implements HasDynamicTitle, LocaleChangeObserver {

    private static final long serialVersionUID = -5785487000322019291L;

    private final DatabaseService databaseService;

    private final ResearchOrganizationService organizationService;

    private final DownloadBigButton exportUtbmActivityReport;

    private final DownloadBigButton exportSpimActivityReport;

    private final DownloadBigButton exportIcartsActivityReport;

    /**
     * Constructor.
     *
     * @param databaseService     the service for accessing all data from the database.
     * @param organizationService the service for accessing the research organizations.
     * @param loggerFactory       the factory to be used for the composite logger.
     */
    public ReportExportView(@Autowired DatabaseService databaseService, @Autowired ResearchOrganizationService organizationService,
                            @Autowired ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);
        this.databaseService = databaseService;
        this.organizationService = organizationService;

        this.exportUtbmActivityReport = DownloadBigButton.newButtonWithComponent(
                getTranslation("views.export.utbm.activity_report"), //$NON-NLS-1$
                () -> new Image(ComponentFactory.newStreamImage(ViewConstants.UTBM_LOGO), null));
        this.exportUtbmActivityReport.configure()
                .withFilename(() -> "rapport_activite_utbm.xlsx") //$NON-NLS-1$
                .withMimeType(() -> IoConstants.EXCEL_MIME)
                .withFailureListener(this::notifyExportError)
                .withFileDescription(progress -> exportUtbmActivityReport(progress));

        this.exportSpimActivityReport = DownloadBigButton.newButtonWithComponent(
                getTranslation("views.export.spim.activity_report"), //$NON-NLS-1$
                () -> new Image(ComponentFactory.newStreamImage(ViewConstants.SPIM_LOGO), null));
        this.exportSpimActivityReport.configure()
                .withFilename(() -> "rapport_activite_spim.xlsx") //$NON-NLS-1$
                .withMimeType(() -> IoConstants.EXCEL_MIME)
                .withFailureListener(this::notifyExportError)
                .withFileDescription(progress -> exportSpimActivityReport(progress));

        this.exportIcartsActivityReport = DownloadBigButton.newButtonWithComponent(
                getTranslation("views.export.icarts.activity_report"), //$NON-NLS-1$
                () -> new Image(ComponentFactory.newStreamImage(ViewConstants.CARNOT_ARTS_LOGO), null));
        this.exportIcartsActivityReport.configure()
                .withFilename(() -> "rapport_activite_carnot_arts.xlsx") //$NON-NLS-1$
                .withMimeType(() -> IoConstants.EXCEL_MIME)
                .withFailureListener(this::notifyExportError)
                .withFileDescription(progress -> exportIcartsActivityReport(progress));

        final var root = getContent();
        root.setSizeFull();
        root.setFlexWrap(FlexWrap.WRAP);
        root.add(this.exportUtbmActivityReport, this.exportSpimActivityReport, this.exportIcartsActivityReport);
    }

    /**
     * Notify the user that the an error was encountered during exporting action.
     *
     * @param error the error.
     */
    protected void notifyExportError(Throwable error) {
        final var ui = getUI().orElseThrow();
        ui.access(() -> {
            final var message = getTranslation("views.export.export_error", error.getLocalizedMessage()); //$NON-NLS-1$
            getLogger().error(message, error);
            ComponentFactory.showErrorNotification(message);
        });
    }

    /**
     * Replies the locale of this view even if the function is invoked from outside the UI.
     *
     * @return the locale.
     */
    protected Locale getLocaleSafe() {
        return getUI().orElseThrow().getLocale();
    }

    /**
     * Export the UTBM annual activity report.
     *
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    protected DownloadableFileDescription exportUtbmActivityReport(Progression progression) throws Exception {
        // Reference year is the last year that is currently finished
        final var referenceYear = LocalDate.now().getYear() - 1;
        final var referenceOrganization = this.organizationService.getDefaultOrganization();
        return this.databaseService.exportUtbmActivityReport(referenceOrganization.getId(), referenceYear,
                getLocaleSafe(), getLogger(), progression);
    }

    /**
     * Export the SPIM annual activity report.
     *
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    protected DownloadableFileDescription exportSpimActivityReport(Progression progression) throws Exception {
        // Reference year is the last year that is currently finished
        final var referenceYear = LocalDate.now().getYear() - 1;
        final var referenceOrganization = this.organizationService.getDefaultOrganization();
        return this.databaseService.exportSpimActivityReport(referenceOrganization.getId(), referenceYear,
                getLocaleSafe(), getLogger(), progression);
    }

    /**
     * Export the IC ARTS annual activity report.
     *
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    protected DownloadableFileDescription exportIcartsActivityReport(Progression progression) throws Exception {
        // Reference year is the last year that is currently finished
        final var referenceYear = LocalDate.now().getYear() - 1;
        final var referenceOrganization = this.organizationService.getDefaultOrganization();
        return this.databaseService.exportIcartsActivityReport(referenceOrganization.getId(), referenceYear,
                getLocaleSafe(), getLogger(), progression);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.export.reports.view_title"); //$NON-NLS-1$
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.exportUtbmActivityReport.setText(getTranslation("views.export.utbm.activity_report")); //$NON-NLS-1$
        this.exportSpimActivityReport.setText(getTranslation("views.export.spim.activity_report")); //$NON-NLS-1$
        this.exportIcartsActivityReport.setText(getTranslation("views.export.icarts.activity_report")); //$NON-NLS-1$
    }

}
