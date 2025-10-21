package fr.utbm.ciad.labmanager.utils.toggleComponent;

import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * A custom component that toggles between two modes, executing specific actions when the mode is changed.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class ToggleComponent extends FlexLayout {

    private final Runnable firstMode;
    private final Runnable secondMode;
    private boolean mode = false;

    /**
     * Default Constructor
     */
    public ToggleComponent() {
        this(null, null);
    }

    /**
     * Constructs a ToggleComponent with custom labels and actions for two modes.
     *
     * @param firstMode  the action to execute when switching to the first mode.
     * @param secondMode the action to execute when switching to the second mode.
     */
    public ToggleComponent(Runnable firstMode, Runnable secondMode) {
        this.firstMode = firstMode;
        this.secondMode = secondMode;
        addClickListener(event -> changeMode());
        getStyle()
                .setBackgroundColor("#f2f2f2")
                .setBorderRadius("10px")
                .setCursor("pointer");
        getElement().addEventListener("mouseover",
                event -> getStyle().setBackgroundColor("#eef2f6"));
        getElement().addEventListener("mouseout",
                event -> getStyle().setBackgroundColor("#f2f2f2"));
        getElement().addEventListener("mousedown",
                event -> getStyle().setBackgroundColor("#e7edf5"));
        getElement().addEventListener("mouseup",
                event -> getStyle().setBackgroundColor("#eef2f6"));

    }

    /**
     * Toggles the button's mode and updates the text and behavior accordingly.
     *
     * @param mode true to first mode; false to second one.
     */
    public void changeMode(boolean mode) {
        if (mode) {
            displaySecondMode();
            firstMode.run();
        } else {
            displayFirstMode();
            secondMode.run();
        }
        if (this.mode != mode) {
            this.mode = mode;
        }
    }

    /**
     * Display on the component the first mode to which the component will switch if clicked
     */
    protected abstract void displayFirstMode();

    /**
     * Display on the component the second mode to which the component will switch if clicked
     */
    protected abstract void displaySecondMode();

    /**
     * Toggles the current mode.
     */
    public void changeMode() {
        changeMode(!mode);
    }

    /**
     * Get the mode state
     *
     * @return the mode state
     */
    public boolean getMode() {
        return mode;
    }

    /**
     * Manually sets the mode state without triggering any associated actions.
     *
     * @param mode true to firstMode mode; false to second one.
     */
    public void setMode(boolean mode) {
        this.mode = mode;
    }

    /**
     * Checks whether the button is currently in first mode.
     *
     * @return true if in first mode; false otherwise.
     */
    public boolean isInFirstMode() {
        return mode;
    }
}