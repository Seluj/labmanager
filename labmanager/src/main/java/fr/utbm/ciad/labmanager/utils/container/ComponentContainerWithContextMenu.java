package fr.utbm.ciad.labmanager.utils.container;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import fr.utbm.ciad.labmanager.utils.contextMenu.ContextMenuFactory;

/**
 * An extension of ComponentContainer that includes a context menu for enhanced interaction capabilities.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ComponentContainerWithContextMenu extends ComponentContainer {

    private final ContextMenu contextMenu;

    /**
     * Default Constructor
     */
    public ComponentContainerWithContextMenu() {
        super();
        this.contextMenu = ContextMenuFactory.createContextMenu(ContextMenuFactory.MenuType.BASIC, this);
    }

    /**
     * Constructor
     *
     * @param component the component to be managed within this container.
     * @param menuType  the type of context menu to create and associate with this container.
     */
    public ComponentContainerWithContextMenu(Component component, ContextMenuFactory.MenuType menuType) {
        super(component);
        this.contextMenu = ContextMenuFactory.createContextMenu(menuType, this);
    }

    /**
     * Retrieves the context menu associated with this container.
     *
     * @return the associated context menu instance.
     */
    public ContextMenu getContextMenu() {
        return contextMenu;
    }
}