package fr.utbm.ciad.labmanager.views.components.dashboard.cell;

import com.vaadin.flow.dom.Style;

/**
 * An extension of AbstractCell that supports resizing of contained components.
 * Provides additional functionality for adjusting the size of child components proportionally
 * to the size of the cell.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AdaptiveCell extends AbstractCell {

    /**
     * Default Constructor
     */
    public AdaptiveCell() {
        super();
    }

    /**
     * Constructor with a default cell's border
     *
     * @param index the index of the cell.
     */
    public AdaptiveCell(int index) {
        super(index);
    }

    /**
     * Constructor
     *
     * @param index       the index of the cell.
     * @param borderColor the color of the cell's border.
     */
    public AdaptiveCell(int index, String borderColor) {
        super(index, borderColor);
    }

    @Override
    public void setCellStyle() {
        setWidth("100%");
        setHeight("100%");
        getStyle().setBorder("1px solid transparent")
                .setDisplay(Style.Display.FLEX)
                .setAlignItems(Style.AlignItems.FLEX_START)
                .setJustifyContent(Style.JustifyContent.FLEX_START)
                .setPosition(Style.Position.RELATIVE)
                .setPadding("0")
                .setBoxSizing(Style.BoxSizing.BORDER_BOX)
                .setMargin("0")
                .setPadding("0")
                .setDisplay(Style.Display.FLEX);
    }
}