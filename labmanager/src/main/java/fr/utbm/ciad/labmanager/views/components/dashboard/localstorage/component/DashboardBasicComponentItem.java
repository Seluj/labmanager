package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.component.DashboardBasicComponentFactory;

/**
 * Class defining items describing dashBoard components that can be store locally
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardBasicComponentItem extends AbstractDashboardComponentItem {

    /**
     * Default Constructor
     */
    public DashboardBasicComponentItem() {
        super(new DashboardBasicComponentFactory());
    }

    /**
     * Constructor
     *
     * @param index the index of the component in the dashboard
     */
    public DashboardBasicComponentItem(int index) {
        super(new DashboardBasicComponentFactory(), index);
    }

    /**
     * Constructor
     *
     * @param index         the index of the component
     * @param componentType the type of the component
     * @param width         the width of the component
     * @param height        the height of the component
     */
    public DashboardBasicComponentItem(int index, DashboardComponentType componentType, String width, String height) {
        super(new DashboardBasicComponentFactory(), index, componentType, width, height);
    }

    /**
     * Constructor to initialize the item from attributes of a component.
     *
     * @param index     the index of the component
     * @param component the existing component to extract attributes
     */
    public DashboardBasicComponentItem(int index, Component component, DashboardComponentType componentType) {
        super(new DashboardBasicComponentFactory(), index, component, componentType);
    }

    /**
     * Constructor to create a new DashBoardComponentItem from an existing one.
     *
     * @param item the existing DashBoardComponentItem to copy properties from
     */
    public DashboardBasicComponentItem(DashboardBasicComponentItem item) {
        super(new DashboardBasicComponentFactory(), item);
    }

    @Override
    public void createId(String idPart) {
        setId("DashBoardComponent_" + idPart);
    }
}
