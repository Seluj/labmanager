package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.manager;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardChartItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.item.DashboardChartItemFactory;

/**
 * Class Managing the local storage for DashBoardChartItem objects.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ChartLocalStorageManager extends ComponentLocalStorageManager<DashboardChartItem> {

    /**
     * Default Constructor
     */
    public ChartLocalStorageManager() {
        super(new DashboardChartItemFactory());
    }

    @Override
    protected DashboardChartItem createItem(int index, Component component, DashboardComponentType componentType) {
        return new DashboardChartItem(index, component, componentType);
    }

    @Override
    protected DashboardChartItem createItem(int index) {
        return new DashboardChartItem(index);
    }
}
