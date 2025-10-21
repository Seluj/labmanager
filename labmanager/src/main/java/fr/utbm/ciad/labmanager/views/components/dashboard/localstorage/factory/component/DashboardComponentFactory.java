package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.component;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentItem;

/**
 * Factory interface for creating components associated with an item that can be stored locally.
 * Defines the method for creating a component based on a given local storage item.
 *
 * @param <T> The type of the item that represents the item used to create the component.
 * @param <S> The possible service needed to create the component.
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface DashboardComponentFactory<T extends DashboardComponentItem, S> {

    /**
     * Creates a component based on the given local storage item and publication service.
     *
     * @param service          the service to be used during the component creation
     * @param localStorageItem the local storage item containing the data needed to create the component
     * @return the created component
     */
    Component createComponent(
            S service,
            T localStorageItem);
}
