package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardBasicComponentItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;

import java.util.Objects;

/**
 * Factory class for creating Components based on a DashBoardComponentItem.
 * Provides the logic for creating specific types of components based on the component type.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardBasicComponentFactory implements DashboardComponentFactory<DashboardBasicComponentItem, Void> {

    @Override
    public Component createComponent(Void service, DashboardBasicComponentItem dashBoardBasicComponentItem) {
        Component component;
        if (Objects.requireNonNull(dashBoardBasicComponentItem.getComponentType()) == DashboardComponentType.NONE) {
            component = new FlexLayout();
        } else {
            component = null;
        }

        if (component != null) {
            component.getStyle()
                    .setWidth(dashBoardBasicComponentItem.getWidth())
                    .setHeight(dashBoardBasicComponentItem.getHeight());
        }
        return component;
    }
}

