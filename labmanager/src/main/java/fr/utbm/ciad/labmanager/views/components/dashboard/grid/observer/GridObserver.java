package fr.utbm.ciad.labmanager.views.components.dashboard.grid.observer;

import com.vaadin.flow.component.Component;

/**
 * Interface for observing component-related event in a grid.
 * Implementing classes will define actions to take when a changes to a component occur.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface GridObserver {

    /**
     * Called when a component has been added to the grid.
     *
     * @param component the component that was added
     */
    void onComponentAdded(Component component);

    /**
     * Called when a new component has been added to the grid.
     *
     * @param component the new component that was added
     */
    void onNewComponentAdded(Component component);

    /**
     * Called when there are changes to a component in the grid.
     *
     * @param component the component that has changes
     */
    void onComponentChanges(Component component);

    /**
     * Called when a component has been removed from the grid.
     *
     * @param component the component that was removed
     */
    void onComponentRemoved(Component component);
}
