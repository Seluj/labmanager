package fr.utbm.ciad.labmanager.views.components.dashboard.grid;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dnd.DropEvent;
import elemental.json.JsonValue;
import fr.utbm.ciad.labmanager.utils.container.ComponentContainer;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.dashboard.cell.DropCell;
import fr.utbm.ciad.labmanager.views.components.dashboard.component.DraggableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DropGrid is an extension of AdaptiveGrid that supports drag-and-drop functionality.
 * It allows components to be dynamically placed, resized, and moved within the grid.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DropGrid extends AdaptiveGrid {
    private static String borderColorWhenDrag = "#bfbfbf";
    private static String backgroundColorWhenEmpty = "#d2ffc4";
    private static String backgroundColorWhenFull = "#ffc4c4";

    private DraggableComponent draggedComponent;

    private String startWidth = "";
    private String startHeight = "";

    /**
     * Default Constructor
     */
    public DropGrid() {
        super();
    }

    /**
     * Constructor
     *
     * @param rows                     number of rows in the grid.
     * @param columns                  number of columns in the grid.
     * @param borderColor              default border color.
     * @param borderColorWhenDrag      border color when a component is being dragged.
     * @param backgroundColorWhenEmpty background color when a marked cell is empty.
     * @param backgroundColorWhenFull  background color when a marked cell is full.
     */
    public DropGrid(int rows, int columns,
                    String borderColor,
                    String borderColorWhenDrag,
                    String backgroundColorWhenEmpty,
                    String backgroundColorWhenFull) {
        super(rows, columns, borderColor);
        DropGrid.borderColorWhenDrag = borderColorWhenDrag;
        DropGrid.backgroundColorWhenEmpty = backgroundColorWhenEmpty;
        DropGrid.backgroundColorWhenFull = backgroundColorWhenFull;
    }

    @Override
    protected void setGridSize(JsonValue width) {
        super.setGridSize(width);
    }

    @Override
    protected void setGridSize(int width) {
        int header = 50;
        if (width > 800) {
            header = 350;
        }
        int intWidth = width - header;

        super.setGridSize(intWidth);
    }

    @Override
    public DropCell createCell(int index) {
        DropCell cell = new DropCell(index, getBorderColor(), borderColorWhenDrag, backgroundColorWhenEmpty, backgroundColorWhenFull);
        setupCellListeners(cell);
        return cell;
    }

    @Override
    public void addComponent(DropCell cell, Component component) {
        super.addComponent(cell, component);
        if (component instanceof DraggableComponent draggableComponent) {
            markCellsAsState(false, getCoveredCells(cell, draggableComponent));
        }
    }

    @Override
    public void addNewComponent(DropCell cell, Component component) {
        super.addNewComponent(cell, component);
        if (component instanceof DraggableComponent draggableComponent) {
            markCellsAsState(false, getCoveredCells(cell, draggableComponent));
        }
    }

    @Override
    public void removeComponent(Component component) {
        setResizing(false);
        if (component instanceof DraggableComponent draggableComponent) {
            draggableComponent.getParent().ifPresent(parent -> {
                if (parent instanceof DropCell cell) {
                    removeComponent(cell, draggableComponent);
                    markCellsAsState(true, getCoveredCells(cell, draggableComponent));
                }
            });
        } else {
            super.removeComponent(component);
        }
    }

    @Override
    public DropCell findFirstEmptyCell(Component component) {
        if (component instanceof ComponentContainer componentContainer) {
            for (DropCell cell : getCells()) {
                if (canBePlaced(componentContainer, cell)) {
                    return cell;
                }
            }
        }
        return null;
    }

    @Override
    protected boolean canBePlaced(ComponentContainer component, DropCell cell) {
        boolean overlapsComponent = false;
        if (component instanceof DraggableComponent draggableComponent) {
            overlapsComponent = !getCoveredCells(cell, draggableComponent)
                    .stream()
                    .allMatch(DropCell::isRecover);
        }
        return super.canBePlaced(component, cell) && !overlapsComponent;
    }

    @Override
    protected void setupComponentListeners(ComponentContainer component) {
        super.setupComponentListeners(component);
        if (component instanceof DraggableComponent draggableComponent) {
            draggableComponent.setAfterDragEventStart(this::addDragStartListener);
            draggableComponent.setAfterDragEventEnd(this::addDragEndListener);
        }
    }

    @Override
    protected void mouseUp(DropCell cell, ComponentContainer component) {
        if (isResizing() && component instanceof DraggableComponent draggableComponent) {
            if (!canBePlaced(draggableComponent, cell)) {
                draggableComponent.setSize(startWidth, startHeight);
            }
            markCellsAsState(false, getCoveredCells(cell, draggableComponent));
            removeCellsColor();
        }
        makeComponentFitInCells(component);
        setResizing(false);
    }

    @Override
    protected void mouseDown(DropCell cell, ComponentContainer component) {
        super.mouseDown(cell, component);
        if (component instanceof DraggableComponent draggableComponent) {
            startWidth = draggableComponent.getComponent().getStyle().get("width");
            startHeight = draggableComponent.getComponent().getStyle().get("height");
            markCellsAsState(true, getCoveredCells(cell, draggableComponent));
        }
    }

    @Override
    protected void afterResizingComponent(Component component) {
        if (isResizing() && component instanceof DraggableComponent draggableComponent) {
            draggableComponent.getParent().ifPresent(parent -> {
                if (parent instanceof DropCell cell) {
                    notifyNewComponentChanges(component);
                    colorCells(cell, draggableComponent);
                }
            });
            addTransparencyToComponents(draggableComponent);
        }
    }

    /**
     * Adds a listener to handle the start of a drag operation for a draggable component.
     *
     * @param component The draggable component to which the drag-start listener is attached.
     */
    private void addDragStartListener(DraggableComponent component) {
        if (component.isDraggable()) {
            draggedComponent = component;
            component.getParent().ifPresent(cell -> markCellsAsState(true, getCoveredCells((DropCell) cell, component)));
            showBorders(true);
            addTransparencyToComponents();
        }
    }

    /**
     * Adds a listener to handle the end of a drag operation for a draggable component.
     *
     * @param component The draggable component to which the drag-end listener is attached.
     */
    private void addDragEndListener(DraggableComponent component) {
        if (component.isDraggable()) {
            hideBorders(true);

            removeCellsColor();

            draggedComponent = null;
        }
    }

    /**
     * Configures the listeners for a DropCell, including drop and drag-enter events.
     *
     * @param cell the DropCell to set up listeners for.
     */
    protected void setupCellListeners(DropCell cell) {

        cell.getDropTarget().addDropListener(event -> handleDrop(event, cell));

        cell.getElement().addEventListener("dragenter", event -> event.getSource().getComponent().ifPresent(
                component -> colorCells(cell, draggedComponent)));
    }

    /**
     * Handles drop events to place a component in a DropCell if valid.
     *
     * @param event the drop event.
     * @param cell  the cell where the drop is occurring.
     */
    private void handleDrop(DropEvent<DropCell> event, DropCell cell) {
        event.getDragSourceComponent().ifPresent(component -> {
            if (component instanceof DraggableComponent draggableComponent) {
                if (canBePlaced(draggableComponent, cell)) {
                    removeComponent(component);
                    addComponent(cell, draggableComponent);
                    if (draggableComponent.getComponent() instanceof PublicationCategoryLayout<?> publicationCategoryLayout) {
                        publicationCategoryLayout.refreshChart();
                    }
                } else {
                    draggableComponent.getParent().ifPresent(parent -> markCellsAsState(
                            false,
                            getCoveredCells((DropCell) parent)
                    ));
                }
            }
        });
    }

    /**
     * Retrieves the list of grid cells covered by the currently dragged component.
     *
     * @param startingCell The starting cell of the dragged component.
     * @return A list of cells covered by the dragged component.
     */
    private List<DropCell> getCoveredCells(DropCell startingCell) {
        return getCoveredCells(startingCell, draggedComponent);
    }

    /**
     * Retrieves the list of grid cells covered by a given component
     *
     * @param startingCell The starting cell of the component.
     * @param component    The draggable component covering cells
     * @return A list of cells covered by the component.
     */
    private List<DropCell> getCoveredCells(DropCell startingCell, DraggableComponent component) {
        int componentWidthInCells = calculateComponentSizeInCells(component, "width");
        int componentHeightInCells = calculateComponentSizeInCells(component, "height");

        int startCellIndex = startingCell.getIndex();
        int startingColumn = startCellIndex % getColumns();

        List<DropCell> coveredCells = new ArrayList<>();
        for (int row = 0; row < componentHeightInCells; row++) {
            for (int col = 0; col < componentWidthInCells; col++) {
                int cellIndex = startCellIndex + row * getColumns() + col;

                if ((startingColumn + col) < getColumns() && cellIndex < getCells().size()) {
                    coveredCells.add(getCells().get(cellIndex));
                }
            }
        }
        if (!coveredCells.isEmpty()) {
            coveredCells.add(startingCell);
        }
        return coveredCells;
    }

    /**
     * Marks all cells as empty or recover.
     *
     * @param state The state to apply on the cell (true for recover, false otherwise)
     * @param cells The cells to be made empty or recover
     */
    private void markCellsAsState(boolean state, List<DropCell> cells) {
        for (DropCell dropCell : cells) {
            dropCell.setRecover(state);
        }
    }

    /**
     * Toggles the grid's edition mode. In edition mode, components can be dragged
     * and edited, and the grid's borders are hidden or shown accordingly.
     *
     * @param editionMode true to enable edition mode; false to disable it.
     */
    public void changeEditionMode(boolean editionMode) {
        if (editionMode) {
            showBorders(false);
        } else {
            hideBorders(false);
        }
        getCellsContainingComponents().forEach(cell -> {
            if (cell.getComponent() instanceof DraggableComponent draggableComponent) {
                draggableComponent.setDraggable(editionMode);
            }
        });
    }

    /**
     * Empty the grid by removing components from all cells that contain one
     */
    public void emptyGrid() {
        getCellsContainingComponents().forEach(cell -> removeComponent(cell.getComponent()));
    }

    /**
     * Resets the visual effects applied to grid cells (e.g., colors) after
     * a drag or resize operation is completed. Restores cells to their default visual state.
     */
    private void removeCellsColor() {
        for (DropCell dropCell : getCells()) {
            dropCell.colorCell(false, true);
        }
        removeTransparencyFromComponents();
    }

    /**
     * Colors cells recovered by a component.
     *
     * @param cell      the starting cell where the component is placed
     * @param component the draggable component being placed in the grid
     */
    private void colorCells(DropCell cell, DraggableComponent component) {
        List<DropCell> coveredCells = getCoveredCells(cell, component);
        boolean isOverTheEdge = isOverTheEdge(cell, component);
        for (DropCell dropCell : getCells()) {
            dropCell.colorCell(coveredCells.contains(dropCell), isOverTheEdge);
        }
    }

    /**
     * Applies transparency to all components in the grid, except for a specified exception.
     *
     * @param exception the component that should not have transparency applied; if null, all components will be affected
     */
    private void addTransparencyToComponents(DraggableComponent exception) {
        for (DropCell dropCell : getCellsContainingComponents()) {
            if (dropCell.getComponent() instanceof DraggableComponent draggableComponent) {
                draggableComponent.setTransparency(true);
                if (exception == null || !Objects.equals(draggableComponent, exception)) {
                    draggableComponent.setToTheFore(false);
                }
            }
        }
    }

    /**
     * Applies transparency to all components in the grid, except for a specified exception.
     */
    private void addTransparencyToComponents() {
        addTransparencyToComponents(null);
    }

    /**
     * Removes transparency from all components in the grid and brings them to the foreground.
     */
    private void removeTransparencyFromComponents() {
        for (DropCell dropCell : getCellsContainingComponents()) {
            if (dropCell.getComponent() instanceof DraggableComponent draggableComponent) {
                draggableComponent.setTransparency(false);
                draggableComponent.setToTheFore(true);
            }

        }
    }

    /**
     * Displays the grid and its cells' borders, using a different style for drag events.
     *
     * @param isDragging true if a drag event is active; false otherwise.
     */
    public void showBorders(boolean isDragging) {
        if (isDragging) {
            changeToBorderColorWhenDrag();
        } else {
            changeToBorderColor();
        }
        getCells().forEach(cell -> {
            if (cell != null) {
                cell.showBorders(isDragging);
            }
        });
    }

    /**
     * Hides the grid and its cells' borders, using a different style for drag events.
     *
     * @param isDragging true if a drag event is active; false otherwise.
     */
    public void hideBorders(boolean isDragging) {
        if (isDragging) {
            changeToBorderColor();
        } else {
            getStyle().set("border", "none");
        }
        getCells().forEach(cell -> {
            if (cell != null) {
                cell.hideBorders(isDragging);
            }
        });
    }

    /**
     * Changes the grid's border color to the color stored into "borderColor"
     */
    private void changeToBorderColor() {
        getStyle().setBorder("1px dashed " + getBorderColor());
    }

    /**
     * Changes the grid's border color to the color stored into "borderColorWhenDrag"
     * for when a drag operation is in progress.
     */
    private void changeToBorderColorWhenDrag() {
        getStyle().setBorder("1px dashed " + borderColorWhenDrag);
    }
}