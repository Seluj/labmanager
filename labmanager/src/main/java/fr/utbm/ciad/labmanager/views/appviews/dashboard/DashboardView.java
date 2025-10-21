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

package fr.utbm.ciad.labmanager.views.appviews.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerComposite;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.dashboard.DashboardLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

/**
 * Dashboard for the lab manager application.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class DashboardView extends AbstractLoggerComposite<VerticalLayout> implements HasDynamicTitle {

    private static final long serialVersionUID = -1583805930880620625L;

    /**
     * Constructor.
     *
     * @param loggerFactory the factory to be used for the composite logger.
     */
    public DashboardView(@Autowired PublicationService publicationService,
                         @Autowired ContextualLoggerFactory loggerFactory) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(loggerFactory);

        DashboardLayout dashboardLayout = new DashboardLayout(publicationService);

        final var bt = new Button();
        bt.setText("Show log");
        bt.addClickListener(event -> {
            getLogger().info("Test logger / User name should appear");
        });
        getContent().add(dashboardLayout, bt);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.dashboard.title"); //$NON-NLS-1$
    }
}