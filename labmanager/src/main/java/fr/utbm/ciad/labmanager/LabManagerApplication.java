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

package fr.utbm.ciad.labmanager;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@OpenAPIDefinition(
        info = @Info(
                title = "API LabManager",
                version = "4.0",
                description = "LabManager API Documentation"
        )
)
@SpringBootApplication
@ComponentScan(basePackages = {"fr.utbm.ciad.labmanager", "fr.utbm.ciad.wprest"})
@Async
@Push
@Theme(value = ViewConstants.DEFAULT_BACKEND_THEME)
public class LabManagerApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    private static final long serialVersionUID = 3672131574382734484L;

    /**
     * Starting point for the LabManaer application.
     *
     * @param args the commande-line arguments.
     */
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        SpringApplication.run(LabManagerApplication.class, args);
    }

    private static String makeFavIconPath(String fileExtension) {
        return "themes/" + ViewConstants.DEFAULT_BACKEND_THEME + "/icons/favicon." + fileExtension; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addLink("shortcut icon", makeFavIconPath("ico")); //$NON-NLS-1$ //$NON-NLS-2$
        settings.addFavIcon("icon", makeFavIconPath("svg"), "92x92"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
