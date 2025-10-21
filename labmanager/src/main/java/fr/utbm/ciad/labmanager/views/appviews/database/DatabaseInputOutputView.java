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

package fr.utbm.ciad.labmanager.views.appviews.database;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.components.start.JsonDatabaseInitializer;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.admin.DatabaseService;
import fr.utbm.ciad.labmanager.utils.io.IoConstants;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.download.DownloadBigButton;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import jakarta.annotation.security.RolesAllowed;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Locale;

/**
 * Enable to manage the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "databaseio", layout = MainLayout.class)
@RolesAllowed({UserRole.ADMIN_GRANT})
@Uses(Icon.class)
public class DatabaseInputOutputView extends AbstractLoggerComposite<FlexLayout> implements HasDynamicTitle, LocaleChangeObserver {

    private static final long serialVersionUID = -4957957381615358038L;

    private final DatabaseService databaseService;

    private final DownloadBigButton exportJson;

    private final DownloadBigButton exportZip;

    /**
     * Constructor.
     *
     * @param databaseService the service for accessing the database tools.
     * @param loggerFactory   the factory to be used for the composite logger.
     */
    public DatabaseInputOutputView(@Autowired DatabaseService databaseService, @Autowired ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);

        this.databaseService = databaseService;

        this.exportJson = DownloadBigButton.newButtonWithComponent(
                getTranslation("views.databases.io.export_json"), //$NON-NLS-1$
                () -> new Image(ComponentFactory.newStreamImage(ViewConstants.EXPORT_JSON_BLACK_ICON), null));
        this.exportJson.configure()
                .withFilename(() -> JsonDatabaseInitializer.INITIALIZATION_JSON_DATA_FILENAME)
                .withMimeType(() -> IoConstants.JSON_MIME)
                .withFailureListener(this::notifyExportError)
                .withInputStreamFactory(progress -> exportJson(progress));

        this.exportZip = DownloadBigButton.newButtonWithIcon(
                getTranslation("views.databases.io.export_zip"), //$NON-NLS-1$
                () -> {
                    final var icon = VaadinIcon.FILE_ZIP.create();
                    icon.setColor("black"); //$NON-NLS-1$
                    return icon;
                });
        this.exportZip.configure()
                .withFilename(() -> JsonDatabaseInitializer.INITIALIZATION_ZIP_DATA_FILENAME)
                .withMimeType(() -> IoConstants.ZIP_MIME)
                .withFailureListener(this::notifyExportError)
                .withInputStreamFactory(progress -> exportZip(progress));

        final var root = getContent();
        root.setSizeFull();
        root.setFlexWrap(FlexWrap.WRAP);
        root.add(this.exportJson, this.exportZip);
    }

    /**
     * Notify the user that the an error was encountered during exporting action.
     *
     * @param error the error.
     */
    protected void notifyExportError(Throwable error) {
        final var ui = getUI().orElseThrow();
        ui.access(() -> {
            final var message = getTranslation("views.databases.io.export_error", error.getLocalizedMessage()); //$NON-NLS-1$
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
     * Export database content to Json.
     *
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    protected InputStream exportJson(Progression progression) throws Exception {
        return this.databaseService.exportJson(getLocaleSafe(), getLogger(), progression);
    }

    /**
     * Export database content to Zip.
     *
     * @param progression the progression indicator.
     * @return the content of the file.
     * @throws Exception the export error.
     */
    protected InputStream exportZip(Progression progression) throws Exception {
        return this.databaseService.exportZip(getLocaleSafe(), getLogger(), progression);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.databases.io.title"); //$NON-NLS-1$
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.exportJson.setText(getTranslation("views.databases.io.export_json")); //$NON-NLS-1$
        this.exportZip.setText(getTranslation("views.databases.io.export_zip")); //$NON-NLS-1$
    }

}
