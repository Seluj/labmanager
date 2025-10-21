package fr.utbm.ciad.labmanager.views.components.dashboard.cell;

import com.vaadin.flow.component.dnd.DropTarget;

/**
 * A specialized cell that supports drag-and-drop functionality by handling drag events.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DropCell extends AdaptiveCell {

    private static String borderColorWhenDrag = "#bfbfbf";
    private static String backgroundColorWhenEmpty = "#d2ffc4";
    private static String backgroundColorWhenFull = "#ffc4c4";
    private final DropTarget<DropCell> dropTarget;
    private boolean isRecover = true;

    /**
     * Default Constructor
     */
    public DropCell() {
        this(0);
    }

    /**
     * Constructor with defaults colors
     *
     * @param index the index of the cell.
     */
    public DropCell(Integer index) {
        super(index);
        dropTarget = DropTarget.create(this);
    }

    /**
     * Constructor
     *
     * @param index       the index of the cell.
     * @param borderColor the color of the cell's border.
     */
    public DropCell(int index, String borderColor) {
        this(index);
        setBorderColor(borderColor);
    }

    /**
     * Constructor
     *
     * @param index                    the index of the cell.
     * @param borderColor              the default border color.
     * @param borderColorWhenDrag      the border color during drag events.
     * @param backgroundColorWhenEmpty the background color when the cell is empty.
     * @param backgroundColorWhenFull  the background color when the cell is full.
     */
    public DropCell(Integer index,
                    String borderColor,
                    String borderColorWhenDrag,
                    String backgroundColorWhenEmpty,
                    String backgroundColorWhenFull) {
        this(index, borderColor);
        DropCell.borderColorWhenDrag = borderColorWhenDrag;
        DropCell.backgroundColorWhenEmpty = backgroundColorWhenEmpty;
        DropCell.backgroundColorWhenFull = backgroundColorWhenFull;

        setCellStyle();
    }

    @Override
    public void emptyCell() {
        super.emptyCell();
        if (!isRecover) {
            isRecover = true;
        }
    }

    /**
     * Checks if the cell is currently recover by a component
     * (does contain any component or is covered by any component).
     */
    public boolean isRecover() {
        return isRecover;
    }

    /**
     * Sets the covertness state of the cell.
     *
     * @param isRecover true to mark the cell as recover; false otherwise.
     */
    public void setRecover(boolean isRecover) {
        this.isRecover = isRecover;
    }

    /**
     * Retrieves the drop target associated with this cell.
     *
     * @return the DropTarget for the cell.
     */
    public DropTarget<DropCell> getDropTarget() {
        return dropTarget;
    }

    /**
     * Displays the cell borders, using a different style depending on drag events.
     *
     * @param isDragging true if a drag event is active; false otherwise.
     */
    public void showBorders(boolean isDragging) {
        if (isDragging) {
            changeToBorderColorWhenDrag();
        } else {
            changeToBorderColor();
        }
    }

    /**
     * Hides the cell borders, using a different style depending on drag events.
     *
     * @param isDragging true if a drag event is active; false otherwise.
     */
    public void hideBorders(boolean isDragging) {
        if (isDragging) {
            changeToBorderColorWhenDrag();
        } else {
            getStyle().set("border", "none");
        }
    }

    /**
     * Colors the cell depending on the provided conditions.
     *
     * @param color true to color the cell when a component is dragged on it,
     *              false to reset its background.
     * @param error true if an error state should be applied (color with the backgroundColorWhenFull color),
     *              false otherwise.
     */
    public void colorCell(boolean color, boolean error) {
        if (color) {
            if (isRecover && !error) {
                changeToBackgroundColorWhenEmpty();
            } else {
                changeToBackgroundColorWhenFull();
            }
        } else {
            getStyle().setBackgroundColor("transparent");
        }
    }

    /**
     * Changes the border color of the cell to its default color.
     */
    private void changeToBorderColor() {
        getStyle().set("border", "1px dashed " + getBorderColor());
    }

    /**
     * Changes the border color of the cell to the color when a drag event is active.
     */
    private void changeToBorderColorWhenDrag() {
        getStyle().set("border", "1px dashed " + borderColorWhenDrag);
    }

    /**
     * Changes the border color of the empty cell to the corresponding color when a component is dragged on it
     */
    private void changeToBackgroundColorWhenEmpty() {
        getStyle().setBackgroundColor(backgroundColorWhenEmpty);
    }

    /**
     * Changes the border color of the full cell to the corresponding color when a component is dragged on it
     */
    private void changeToBackgroundColorWhenFull() {
        getStyle().setBackgroundColor(backgroundColorWhenFull);
    }
}