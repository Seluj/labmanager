package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.manager;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.utils.localStorage.AbstractLocalStorageManager;
import fr.utbm.ciad.labmanager.utils.localStorage.factory.LocalStorageItemFactory;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.AbstractDashboardComponentItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;

/**
 * Abstract class for managing items representing components that can be store locally.
 *
 * @param <T> The DashBoardComponentItem storing the component data
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class ComponentLocalStorageManager<T extends AbstractDashboardComponentItem> extends AbstractLocalStorageManager<T> {

    /**
     * Constructor
     *
     * @param factory the factory used to create items of type T
     */
    public ComponentLocalStorageManager(LocalStorageItemFactory<T> factory) {
        super(factory);
    }


    /**
     * Adds a component item to the local storage.
     *
     * @param index         the index used to create the item
     * @param component     the component used to create the item
     * @param componentType the type of the component
     */
    public void add(int index, Component component, DashboardComponentType componentType) {
        add(createItem(index, component, componentType));
    }

    /**
     * Removes a component item from the local storage based on its index.
     *
     * @param index the index used to create the item
     */
    public void remove(int index) {
        remove(createItem(index));
    }

    /**
     * Method to create an instance of T based on index and component.
     *
     * @param index         the index of the item
     * @param component     the component of the item
     * @param componentType the type of the component
     * @return an instance of T
     */
    protected abstract T createItem(int index, Component component, DashboardComponentType componentType);

    /**
     * Method to create an instance of T based on index.
     *
     * @param index the index of the item
     * @return an instance of T
     */
    protected abstract T createItem(int index);
}
