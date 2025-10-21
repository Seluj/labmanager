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

package fr.utbm.ciad.labmanager.views.appviews.about;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * The view that show the application informations.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "about", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AboutView extends AbstractLoggerComposite<HorizontalLayout> implements HasDynamicTitle {

    private static final long serialVersionUID = -3953932858588108431L;

    private static final int PERCENTAGE_IMAGE_SIZE = 60;

    private static final String IMAGE_MARGINS = "0px 0px 50px 0px"; //$NON-NLS-1$

    private static final String COPYRIGHT_MARGINS = "50px 0px 0px 0px"; //$NON-NLS-1$

    private final String applicationName;

    /**
     * Constructor.
     *
     * @param applicationName          the name of the current application.
     * @param applicationImage         the resource path to the image of the current application.
     * @param applicationCopyrightText the text of the copyright for the current application.
     * @param loggerFactory            the factory to be used for the composite logger.
     */
    public AboutView(@Value("${labmanager.application-name}") String applicationName,
                     @Value("${labmanager.application-image}") String applicationImage,
                     @Value("${labmanager.application-copyright}") String applicationCopyrightText,
                     @Autowired ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);
        this.applicationName = applicationName;
        getContent().setSizeFull();
        getContent().setSpacing(false);
        getContent().getStyle().set("flex-grow", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        getContent().setJustifyContentMode(JustifyContentMode.CENTER);
        getContent().setAlignItems(Alignment.CENTER);

        final var vlayout = new VerticalLayout();
        vlayout.setSizeUndefined();
        vlayout.setSpacing(true);
        vlayout.getStyle().set("flex-grow", "1"); //$NON-NLS-1$ //$NON-NLS-2$
        vlayout.setJustifyContentMode(JustifyContentMode.CENTER);
        vlayout.setAlignItems(Alignment.CENTER);

        final var imageResource = ComponentFactory.newStreamImage(applicationImage);
        final var image = new Image(imageResource, applicationImage);
        image.setMinWidth(PERCENTAGE_IMAGE_SIZE, Unit.PERCENTAGE);
        image.setMaxWidth(PERCENTAGE_IMAGE_SIZE, Unit.PERCENTAGE);
        image.getStyle().setMargin(IMAGE_MARGINS);

        final var title = new H1(this.applicationName);

        final var version = new H3(getTranslation("views.about.version", Constants.MANAGER_VERSION, Constants.MANAGER_BUILD_ID)); //$NON-NLS-1$

        final var copyright = new HorizontalLayout(VaadinIcon.COPYRIGHT.create(),
                new Text(applicationCopyrightText));
        copyright.getStyle().setMargin(COPYRIGHT_MARGINS);
        copyright.setJustifyContentMode(JustifyContentMode.CENTER);
        copyright.setAlignItems(Alignment.CENTER);

        vlayout.add(image, title, version, copyright);
        getContent().add(vlayout);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.about.list", this.applicationName); //$NON-NLS-1$
    }

}
