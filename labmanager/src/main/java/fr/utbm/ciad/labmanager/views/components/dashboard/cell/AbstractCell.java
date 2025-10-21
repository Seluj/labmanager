package fr.utbm.ciad.labmanager.views.components.dashboard.cell;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import java.util.Optional;

/**
 * An abstract representation of a cell in a layout (a grid for example).
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractCell extends FlexLayout implements Cell {

    private static String borderColor;
    private final Integer index;
    private Component component;

    /**
     * Default Constructor
     */
    public AbstractCell() {
        this(0);
    }

    /**
     * Constructor with a default cell's border
     *
     * @param index the index of the cell.
     */
    public AbstractCell(Integer index) {
        this(index, "#f2f2f2");
    }

    /**
     * Constructor
     *
     * @param index       the index of the cell.
     * @param borderColor the color of the cell's border.
     */
    public AbstractCell(Integer index,
                        String borderColor) {
        this.component = null;
        this.index = index;
        AbstractCell.borderColor = borderColor;

        setCellStyle();
    }

    /**
     * Retrieves the border color shared by all instances of AbstractCell.
     *
     * @return the static border color.
     */
    public static String getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the border color shared by all instances of AbstractCell.
     *
     * @param borderColor the border color to set
     */
    public static void setBorderColor(String borderColor) {
        AbstractCell.borderColor = borderColor;
    }

    @Override
    public void addComponent(Component component) {
        if (!isEmpty()) {
            emptyCell();
        }
        add(component);
        this.component = component;
    }

    @Override
    public void emptyCell() {
        if (containsComponent()) {
            removeAll();
            component = null;
        }
    }

    @Override
    public boolean isEmpty() {
        return !containsComponent();
    }

    /**
     * Applies the style settings to the cell.
     * Implementations should define specific styling logic.
     */
    protected abstract void setCellStyle();

    /**
     * Checks if the cell contains any component.
     *
     * @return true if the cell contains a component; false otherwise.
     */
    public boolean containsComponent() {
        return !(this.component == null);
    }

    /**
     * Checks if the cell contains a specific component.
     *
     * @param component the component to check for.
     * @return true if the cell contains the specified component; false otherwise.
     */
    public boolean contains(Component component) {
        return this.component != null && this.component.equals(component);
    }

    /**
     * Retrieves the child component contained in the cell, if any.
     *
     * @return an Optional containing the child component, or empty if the cell is empty.
     */
    public Optional<Component> getChild() {
        return getChildren().findFirst();
    }

    /**
     * Get the component contained in the cell
     *
     * @return the component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Retrieves the index of the cell.
     *
     * @return the index of the cell.
     */
    public int getIndex() {
        return index;
    }
}