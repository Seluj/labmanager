package fr.utbm.ciad.labmanager.utils.toggleComponent;

import com.vaadin.flow.component.html.Span;

public class ToggleTextComponent extends ToggleComponent {

    private final String firstModeText;
    private final String secondModeText;
    private final Span textDisplay = new Span();

    /**
     * Constructs a ToggleComponent with custom labels and actions for two modes.
     *
     * @param firstMode  the action to execute when switching to the first mode.
     * @param secondMode the action to execute when switching to the second mode.
     */
    public ToggleTextComponent(String firstModeText, Runnable firstMode, String secondModeText, Runnable secondMode) {
        super(firstMode, secondMode);
        this.firstModeText = firstModeText;
        this.secondModeText = secondModeText;
        add(textDisplay);
        displayFirstMode();
    }

    @Override
    protected void displayFirstMode() {
        textDisplay.setText(firstModeText);
    }

    @Override
    protected void displaySecondMode() {
        textDisplay.setText(secondModeText);
    }

    public String getText() {
        if (isInFirstMode()) {
            return secondModeText;
        } else {
            return firstModeText;
        }
    }
}
