package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.manager;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardBasicComponentItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.item.DashboardBasicComponentItemFactory;

/**
 * Class Managing the local storage for DashBoardComponentItem objects.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class BasicComponentLocalStorageManager extends ComponentLocalStorageManager<DashboardBasicComponentItem> {

    /**
     * Default Constructor
     */
    public BasicComponentLocalStorageManager() {
        super(new DashboardBasicComponentItemFactory());
    }

    @Override
    protected DashboardBasicComponentItem createItem(int index, Component component, DashboardComponentType componentType) {
        return new DashboardBasicComponentItem(index, component, componentType);
    }

    @Override
    protected DashboardBasicComponentItem createItem(int index) {
        return new DashboardBasicComponentItem(index);
    }
}
