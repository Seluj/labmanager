package fr.utbm.ciad.labmanager.views.components.dashboard.grid;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.dashboard.cell.DropCell;

import java.util.List;

/**
 * Interface defining the structure and behavior of a grid layout system.
 * Provides methods for creating, initializing, and styling grid cells.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface Grid {

    /**
     * Adds a component to a specific cell.
     *
     * @param cell      the cell to which the component should be added.
     * @param component the component to add.
     */
    void addComponent(DropCell cell, Component component);

    /**
     * Adds a new component to the grid, to a specific cell.
     *
     * @param cell      the cell to which the new component should be added.
     * @param component the new component to add.
     */
    void addNewComponent(DropCell cell, Component component);

    /**
     * Removes a specified component from a given grid cell.
     *
     * @param cell      The cell from which the component is being removed.
     * @param component The draggable component to remove.
     */
    void removeComponent(DropCell cell, Component component);

    /**
     * Removes a given component from its parent cell.
     *
     * @param component The draggable component to remove.
     */
    void removeComponent(Component component);

    /**
     * Checks if all cells in the grid are empty.
     *
     * @return true if all cells are empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieves the list of all cells in the grid.
     *
     * @return a list of AbstractCell objects.
     */
    List<DropCell> getCells();

}