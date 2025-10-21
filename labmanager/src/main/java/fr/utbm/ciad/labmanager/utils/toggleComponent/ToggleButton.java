package fr.utbm.ciad.labmanager.utils.toggleComponent;

import com.vaadin.flow.component.button.Button;

/**
 * A custom button that toggles between two modes, executing specific actions when the mode is changed.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ToggleButton extends Button {

    ToggleTextComponent toggleTextComponent;

    /**
     * Default Constructor
     */
    public ToggleButton() {
        this("Mode 1", null, "Mode 2", null);
    }

    /**
     * Constructs a ToggleButton with custom labels and actions for two modes.
     *
     * @param textFirstMode  the text to display on the button to switch to the first mode.
     * @param firstMode      the action to execute when switching to the first mode.
     * @param textSecondMode the text to display on the button to switch to the second mode.
     * @param secondMode     the action to execute when switching to the second mode.
     */
    public ToggleButton(String textFirstMode, Runnable firstMode, String textSecondMode, Runnable secondMode) {
        toggleTextComponent = new ToggleTextComponent(textFirstMode, firstMode, textSecondMode, secondMode);
        setText(toggleTextComponent.getText());
        addClickListener(event -> {
            toggleTextComponent.changeMode();
            setText(toggleTextComponent.getText());
        });
    }
}