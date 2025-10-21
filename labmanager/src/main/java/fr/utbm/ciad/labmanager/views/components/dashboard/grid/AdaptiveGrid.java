package fr.utbm.ciad.labmanager.views.components.dashboard.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import elemental.json.JsonValue;
import fr.utbm.ciad.labmanager.utils.container.ComponentContainer;
import fr.utbm.ciad.labmanager.views.components.dashboard.cell.DropCell;

import java.util.ArrayList;
import java.util.List;

/**
 * An adaptive implementation of AbstractGrid that adjusts its layout dynamically based on the browser window size.
 * Provides additional functionality for resizing components and responding to browser events.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AdaptiveGrid extends AbstractGrid {

    private boolean isResizing = false;

    /**
     * Default Constructor
     */
    public AdaptiveGrid() {
        super();
    }

    /**
     * Constructor
     *
     * @param rows        the number of rows in the grid.
     * @param columns     the number of columns in the grid.
     * @param borderColor the border color of the cells.
     */
    public AdaptiveGrid(int rows,
                        int columns,
                        String borderColor) {
        super(rows, columns, borderColor);
    }

    @Override
    public void initializeGrid() {
        super.initializeGrid();

        UI.getCurrent().getPage().executeJs("return window.innerWidth;")
                .then(this::setGridSize);

        setupListeners();
    }

    @Override
    public DropCell createCell(int index) {
        return new DropCell();
    }

    @Override
    public void addNewComponent(DropCell cell, Component component) {
        if (component instanceof ComponentContainer componentContainer) {
            setResizing(false);
            setupComponentListeners(componentContainer);
            makeComponentFitInCells(componentContainer);
        }
        super.addNewComponent(cell, component);
    }

    /**
     * Sets the grid size based on a given width.
     *
     * @param width the width to which the grid will be resized as a JsonValue.
     */
    protected void setGridSize(JsonValue width) {
        double doubleWidth = width.asNumber();
        setGridSize((int) doubleWidth);
    }

    /**
     * Sets the grid size based on a given width.
     *
     * @param width the width to which the grid will be resized in pixels.
     */
    protected void setGridSize(int width) {
        getStyle()
                .setWidth(width + "px")
                .setHeight(width + "px");
        double cellSize = getCellSize();
        setCellSize((double) width / getColumns());
        for (DropCell cell : getCellsContainingComponents()) {
            if (cell.getComponent() instanceof ComponentContainer componentContainer) {
                makeComponentFitInCells(cellSize, componentContainer);
            }
        }
    }

    /**
     * Adjusts the dimensions of a component to fit within the grid's cell size.
     *
     * @param component the component whose dimensions will be adjusted
     */
    protected void makeComponentFitInCells(ComponentContainer component) {
        long componentWidth = Math.round(calculateComponentSizeInCells(component, "width") * getCellSize());
        long componentHeight = Math.round(calculateComponentSizeInCells(component, "height") * getCellSize());

        component.setSize(componentWidth, componentHeight);
    }

    /**
     * Adjusts the dimensions of a component to fit within the grid's cell size.
     *
     * @param previousCellSize the size of a cell before the adjustment
     * @param component        the component whose dimensions will be adjusted
     */
    private void makeComponentFitInCells(double previousCellSize, ComponentContainer component) {
        double componentWidth = calculateComponentSizeInCells(previousCellSize, component, "width") * getCellSize();
        double componentHeight = calculateComponentSizeInCells(previousCellSize, component, "height") * getCellSize();

        component.getStyle()
                .setWidth(componentWidth + "px")
                .setHeight(componentHeight + "px");
    }

    /**
     * Retrieves a list of all grid cells that currently contain components.
     *
     * @return A list of grid cells containing components.
     */
    protected List<DropCell> getCellsContainingComponents() {
        List<DropCell> cellsList = new ArrayList<>();
        for (DropCell cell : getCells()) {
            if (cell.containsComponent()) {
                cellsList.add(cell);
            }
        }
        return cellsList;
    }

    public List<Component> getComponents() {
        List<Component> list = new ArrayList<>();
        getCellsContainingComponents().forEach(cell -> list.add(cell.getComponent()));
        return list;
    }

    /**
     * Sets up listeners to handle grid-related events.
     */
    private void setupListeners() {
        UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> setGridSize(event.getWidth()));
    }

    /**
     * Sets up listeners for component-specific behaviors.
     *
     * @param component the component container to add listeners to.
     */
    protected void setupComponentListeners(ComponentContainer component) {
        component.setAfterResizingInstructions(this::afterResizingComponent);
        component.getElement().addEventListener("mouseup", mouseUpEvent -> component.getParent().ifPresent(parent -> {
            if (parent instanceof DropCell cell) {
                mouseUp(cell, component);
            }
        }));
        component.getElement().addEventListener("mousedown", mouseUpEvent -> component.getParent().ifPresent(parent -> {
            if (parent instanceof DropCell cell) {
                mouseDown(cell, component);
            }
        }));
    }

    /**
     * Adds a listener to handle mouse up events for resizing components.
     *
     * @param component the draggable component.
     */
    protected void mouseUp(DropCell cell, ComponentContainer component) {
        makeComponentFitInCells(component);
        setResizing(false);
    }

    /**
     * Adds a listener to handle mouse down events for resizing components.
     *
     * @param component the draggable component.
     */
    protected void mouseDown(DropCell cell, ComponentContainer component) {
        setResizing(true);
    }

    /**
     * Abstract method to handle logic after a container's size has been changed.
     *
     * @param component the component that was resized.
     */
    protected void afterResizingComponent(Component component) {
    }

    /**
     * Determines if a component can be placed in a given cell based on grid boundaries.
     *
     * @param component the component to check.
     * @param cell      the target cell.
     * @return true if the component can fit in the cell, false otherwise.
     */
    protected boolean canBePlaced(ComponentContainer component, DropCell cell) {
        return !isOverTheEdge(cell, component);
    }

    /**
     * Checks if placing a component in a cell would cause it to exceed the grid's boundaries.
     *
     * @param targetCell the target cell.
     * @param component  the component to check.
     * @return true if the component would exceed the grid's boundaries in this cell, false otherwise.
     */
    protected boolean isOverTheEdge(DropCell targetCell, ComponentContainer component) {
        return targetCell.getIndex() % getColumns() + calculateComponentSizeInCells(component, "width") > getColumns();
    }

    protected int calculateComponentSizeInCells(ComponentContainer component, String side) {
        return calculateComponentSizeInCells(getCellSize(), component, side);
    }

    /**
     * Calculates the size of a component in terms of grid cells.
     *
     * @param component the component whose size is being calculated.
     * @param side      the dimension to calculate ("width" or "height").
     * @return the size of the component in grid cells.
     */
    protected int calculateComponentSizeInCells(double cellSize, ComponentContainer component, String side) {
        if (component != null && component.getComponent().getStyle().get(side) != null) {
            int componentSide = Integer.parseInt(component.getComponent().getStyle().get(side).replace("px", ""));
            return Math.round((float) componentSide / (float) cellSize);
        }
        return 0;
    }

    /**
     * Checks if component are currently being resized in the grid.
     *
     * @return true if resizing is in progress, false otherwise.
     */
    public boolean isResizing() {
        return isResizing;
    }

    /**
     * Sets the resizing state of the grid.
     *
     * @param isResizing true to indicate resizing is in progress, false otherwise.
     */
    public void setResizing(boolean isResizing) {
        this.isResizing = isResizing;
    }
}