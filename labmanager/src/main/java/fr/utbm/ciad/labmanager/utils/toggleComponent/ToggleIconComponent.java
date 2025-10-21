package fr.utbm.ciad.labmanager.utils.toggleComponent;

import com.vaadin.flow.component.icon.Icon;

/**
 * A Togglecompo.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ToggleIconComponent extends ToggleComponent {

    private final Icon firstModeIcon;
    private final Icon secondModeIcon;

    /**
     * Constructs a ToggleComponent with custom labels and actions for two modes.
     *
     * @param firstMode  the action to execute when switching to the first mode.
     * @param secondMode the action to execute when switching to the second mode.
     */
    public ToggleIconComponent(Icon firstModeText, Runnable firstMode, Icon secondModeText, Runnable secondMode) {
        super(firstMode, secondMode);
        this.firstModeIcon = firstModeText;
        this.secondModeIcon = secondModeText;
        this.firstModeIcon.setColor("#1b70df");
        this.firstModeIcon.getStyle().setMarginRight("auto").setMarginLeft("auto");
        this.secondModeIcon.setColor("#1b70df");
        this.secondModeIcon.getStyle().setMarginRight("auto").setMarginLeft("auto");
        getStyle()
                .setWidth("25px")
                .setHeight("25px")
                .setPadding("10px");
        displayFirstMode();
    }

    @Override
    protected void displayFirstMode() {
        removeAll();
        add(firstModeIcon);
    }

    @Override
    protected void displaySecondMode() {
        removeAll();
        add(secondModeIcon);
    }

    public Icon getIcon() {
        if (isInFirstMode()) {
            return secondModeIcon;
        } else {
            return firstModeIcon;
        }
    }
}
