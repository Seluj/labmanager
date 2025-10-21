package fr.utbm.ciad.labmanager.views.components.dashboard.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.dom.Style;
import fr.utbm.ciad.labmanager.views.components.dashboard.cell.DropCell;
import fr.utbm.ciad.labmanager.views.components.dashboard.grid.observer.GridObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a grid layout with a fixed number of rows and columns,
 * where each cell is represented as a DropCell.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
//TODO Use AbstractCell here
public abstract class AbstractGrid extends FlexLayout implements Grid {

    private static String borderColor;

    private final int rows;
    private final int columns;
    private final List<DropCell> cells = new ArrayList<>();
    private final List<GridObserver> observers = new ArrayList<>();
    private double cellSize = 50;

    /**
     * Default Constructor
     */
    public AbstractGrid() {
        this(30, 30, "#bfbfbf");
    }

    /**
     * Constructor
     *
     * @param rows        the number of rows in the grid.
     * @param columns     the number of columns in the grid.
     * @param borderColor the default border color for the grid and its cells.
     */
    public AbstractGrid(int rows, int columns, String borderColor) {
        this.rows = rows;
        this.columns = columns;
        AbstractGrid.borderColor = borderColor;
        initializeGrid();
    }

    @Override
    public void addComponent(DropCell cell, Component component) {
        cell.addComponent(component);
        notifyComponentAdded(component);
    }

    @Override
    public void addNewComponent(DropCell cell, Component component) {
        addComponent(cell, component);
        notifyNewComponentAdded(component);
    }

    @Override
    public void removeComponent(DropCell cell, Component component) {
        if (cell.contains(component)) {
            notifyComponentRemoved(component);
            cell.emptyCell();
        }
    }

    @Override
    public void removeComponent(Component component) {
        component.getParent().ifPresent(parent -> {
            if (parent instanceof DropCell cell) {
                removeComponent(cell, component);
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return getCells().stream().allMatch(DropCell::isRecover);
    }

    @Override
    public List<DropCell> getCells() {
        return cells;
    }

    /**
     * Initializes the grid by configuring its layout, style and listeners.
     */
    protected void initializeGrid() {
        setGridStyle();
        createCells();
    }

    /**
     * Applies the styling configuration for the grid.
     * This can include properties like size, spacing, borders...
     */
    protected void setGridStyle() {
        setFlexDirection(FlexDirection.ROW);
        getStyle().setBackgroundColor("rgba(255, 255, 255, 0.5)")
                .setDisplay(Style.Display.GRID)
                .set("grid-template-columns", "repeat(" + columns + ", 1fr)")
                .set("grid-template-rows", "repeat(" + rows + ", 1fr)")
                .set("gap", "0px");
    }

    /**
     * Creates all cells for the grid.
     * This method handle the generation and placement of cells within the grid.
     */
    protected void createCells() {
        int cellCount = rows * columns;
        cells.clear();
        removeAll();
        for (int i = 0; i < cellCount; i++) {
            DropCell cell = createCell(i);
            cells.add(cell);
            add(cell);
        }
    }

    /**
     * Creates a new cell to be placed in the grid.
     *
     * @param index the index of the cell to be created.
     * @return an instance of AbstractCell representing the created cell.
     */
    protected abstract DropCell createCell(int index);

    /**
     * Adds a component to the first available empty cell in the grid.
     *
     * @param component the component to add.
     */
    public void addComponentInFirstEmptyCell(Component component) {
        DropCell cell = findFirstEmptyCell(component);
        if (cell != null) {
            addNewComponent(cell, component);
            cell.setRecover(false);
        }
    }

    /**
     * Finds the first empty cell in the grid.
     *
     * @param component the component that is to be placed in the grid (optional for future use).
     * @return the first empty DropCell, or null if all cells are occupied.
     */
    public DropCell findFirstEmptyCell(Component component) {
        for (DropCell cell : getCells()) {
            if (cell.isRecover()) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Retrieves the number of columns in the grid.
     *
     * @return the number of columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Retrieves the number of rows in the grid.
     *
     * @return the number of rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Retrieves the current border color for the grid.
     *
     * @return the border color as a String.
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * Retrieves the current size of the cells.
     *
     * @return the size of the cells.
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * Sets the size of the cells in the grid.
     *
     * @param cellSize the new size for the cells.
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }


    /**
     * Adds a GridObserver to the list of observers.
     *
     * @param observer the observer to be added
     */
    public void addObserver(GridObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an from the list of observers.
     *
     * @param observer the observer to be removed
     */
    public void removeObserver(GridObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a component has been added to the grid.
     *
     * @param component the component that was added
     */
    protected void notifyComponentAdded(Component component) {
        for (GridObserver observer : observers) {
            observer.onComponentAdded(component);
        }
    }

    /**
     * Notifies all observers that a new component has been added to the grid.
     *
     * @param component the new component that was added
     */
    protected void notifyNewComponentAdded(Component component) {
        for (GridObserver observer : observers) {
            observer.onNewComponentAdded(component);
        }
    }

    /**
     * Notifies all observers about changes to a new component.
     *
     * @param component the new component that has changes
     */
    protected void notifyNewComponentChanges(Component component) {
        for (GridObserver observer : observers) {
            observer.onComponentChanges(component);
        }
    }

    /**
     * Notifies all observers that a component has been removed from the grid.
     *
     * @param component the component that was removed
     */
    protected void notifyComponentRemoved(Component component) {
        for (GridObserver observer : observers) {
            observer.onComponentRemoved(component);
        }
    }

}