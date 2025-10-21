package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;

/**
 * Interface defining items describing dashBoard components that can be store locally
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface DashboardComponentItem {

    /**
     * Create the component described by the item.
     *
     * @param publicationService the service used for publication-related tasks
     * @return the component for this item
     */
    Component createComponent(PublicationService publicationService);

    /**
     * Retrieves the index of the component.
     *
     * @return the index of the component
     */
    int getIndex();

    /**
     * Sets the index of the dashboard component.
     *
     * @param index the index to set
     */
    void setIndex(int index);

    /**
     * Retrieves the width of the component.
     *
     * @return the width as a string (e.g., "200px")
     */
    String getWidth();

    /**
     * Sets the width of the dashboard component.
     *
     * @param width the width to set
     */
    void setWidth(String width);

    /**
     * Retrieves the height of the component.
     *
     * @return the height as a string (e.g., "300px")
     */
    String getHeight();

    /**
     * Sets the height of the dashboard component.
     *
     * @param height the height to set
     */
    void setHeight(String height);

    /**
     * Retrieves the type of the dashboard component.
     *
     * @return the component type
     */
    DashboardComponentType getComponentType();

    /**
     * Sets the type of the dashboard component.
     *
     * @param componentType the type to set
     */
    void setComponentType(DashboardComponentType componentType);
}
