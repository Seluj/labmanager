package fr.utbm.ciad.labmanager.utils.contextMenu;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a managed context menu, which offers more freedom in its usage than a ContextMenu
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ManagedContextMenu extends ContextMenu {

    private final Map<String, MenuItem> items = new HashMap<>();

    /**
     * Default Constructor
     */
    public ManagedContextMenu() {
        super();
    }

    /**
     * Updates the text of an existing menu item.
     *
     * @param name    the unique name of the menu item.
     * @param newText the new text to set for the menu item.
     */
    public void setMenuItemText(String name, String newText) {
        MenuItem menuItem = items.get(name);
        if (menuItem != null) {
            menuItem.setText(newText);
        } else {
            throw new IllegalArgumentException("No menu item found with the name '" + name + "'.");
        }
    }

    /**
     * Retrieves a menu item by its name.
     *
     * @param name the unique name of the menu item.
     * @return the MenuItem instance.
     */
    public MenuItem getMenuItem(String name) {
        return items.get(name);
    }

    /**
     * Adds an menuItem to the context menu and associates it with a name.
     *
     * @param name     the unique name for the menu item.
     * @param text     the display text of the menu item.
     * @param listener the click listener for the menu item.
     * @return the MenuItem created
     */
    public MenuItem addItem(String name, String text, ComponentEventListener<ClickEvent<MenuItem>> listener) {
        if (items.containsKey(name)) {
            throw new IllegalArgumentException("An item with the name '" + name + "' already exists.");
        }
        MenuItem menuItem = super.addItem(text, listener);
        items.put(name, menuItem);
        return menuItem;
    }

    /**
     * Adds an item to the context menu and associates it with a name that will be the text used when displayed.
     *
     * @param text     the unique name for the menu item, which will be displayed.
     * @param listener the click listener for the menu item.
     * @return the MenuItem created
     */
    @Override
    public MenuItem addItem(String text, ComponentEventListener<ClickEvent<MenuItem>> listener) {
        return addItem(text, text, listener);
    }

    /**
     * Removes a menu item by its name.
     *
     * @param name the unique name of the menu item to be removed.
     */
    public void removeMenuItem(String name) {
        MenuItem menuItem = items.remove(name);
        if (menuItem != null) {
            remove(menuItem);
        } else {
            throw new IllegalArgumentException("No menu item found with the name '" + name + "'.");
        }
    }

    /**
     * Clears all menu items.
     */
    public void clearAllMenuItems() {
        items.values().forEach(this::remove);
        items.clear();
    }
}