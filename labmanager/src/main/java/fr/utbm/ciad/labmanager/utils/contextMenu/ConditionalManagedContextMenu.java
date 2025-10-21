package fr.utbm.ciad.labmanager.utils.contextMenu;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Implementation of a managed context menu that will display its items according to conditions
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ConditionalManagedContextMenu extends ManagedContextMenu {

    private final Map<String, Supplier<Boolean>> visibilityConditions = new HashMap<>();

    /**
     * Default Constructor
     */
    public ConditionalManagedContextMenu() {
        super();
        addOpenedChangeListener();
    }

    /**
     * Adds an event that updates the visibility of items when the menu is opened.
     */
    public void addOpenedChangeListener() {
        addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                updateVisibility();
            }
        });
    }

    /**
     * Updates the visibility of menu items based on their respective conditions.
     */
    public void updateVisibility() {
        visibilityConditions.forEach((name, condition) -> {
            MenuItem menuItem = getMenuItem(name);
            if (menuItem != null) {
                menuItem.setVisible(condition.get());
            }
        });
    }

    /**
     * Adds a menu item with a visibility condition.
     *
     * @param name                the unique name of the menu item.
     * @param text                the display text of the menu item.
     * @param listener            the click listener for the menu item.
     * @param visibilityCondition a Supplier that returns true if the item should be visible.
     * @return the MenuItem created
     */
    public MenuItem addItem(String name, String text, ComponentEventListener<ClickEvent<MenuItem>> listener,
                            Supplier<Boolean> visibilityCondition) {
        visibilityConditions.put(name, visibilityCondition);
        return super.addItem(name, text, listener);
    }

    /**
     * Adds an item to the context menu with a visibility condition and associates it with a name that
     * will be the text used when displayed.
     *
     * @param text                the unique name for the menu item, which will be displayed.
     * @param listener            the click listener for the menu item.
     * @param visibilityCondition a Supplier that returns true if the item should be visible.
     * @return the MenuItem created
     */
    public MenuItem addItem(String text, ComponentEventListener<ClickEvent<MenuItem>> listener,
                            Supplier<Boolean> visibilityCondition) {
        return addItem(text, text, listener, visibilityCondition);
    }

    /**
     * Removes a menu item and its associated visibility condition.
     *
     * @param name the unique name of the menu item to be removed.
     */
    @Override
    public void removeMenuItem(String name) {
        super.removeMenuItem(name);
        visibilityConditions.remove(name);
    }

    /**
     * Clears all menu items and their conditions.
     */
    @Override
    public void clearAllMenuItems() {
        super.clearAllMenuItems();
        visibilityConditions.clear();
    }
}