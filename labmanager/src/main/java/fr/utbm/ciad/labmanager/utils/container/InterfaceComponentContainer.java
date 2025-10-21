package fr.utbm.ciad.labmanager.utils.container;

import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public interface InterfaceComponentContainer {

    /**
     * Retrieves the contained component.
     *
     * @return the currently contained component, or null if the container is empty.
     */
    Component getComponent();

    /**
     * Sets the specified component as the contained component of this container.
     * Applies initial styling.
     *
     * @param component the component to set and manage within this container.
     */
    void setComponent(Component component);

    /**
     * Sets the size of the contained component to the specified width and height.
     *
     * @param width  the new width for the component, in pixels.
     * @param height the new height for the component, in pixels.
     */
    void setSize(String width, String height);

    /**
     * Sets the size of the contained component to the specified width and height.
     *
     * @param width  the new width for the component, in pixels.
     * @param height the new height for the component, in pixels.
     */
    void setComponentSize(String width, String height);

    /**
     * Checks whether the container is empty (i.e., no component is currently contained).
     *
     * @return true if the container is empty; false otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieves the instructions to be executed after a component is resized.
     *
     * @return a Consumer that defines the actions to be performed
     * on a ComponentContainer after resizing
     */
    Consumer<ComponentContainer> getAfterResizingInstructions();

    /**
     * Sets the instructions to be executed after a component is resized.
     *
     * @param afterResizingInstructions a Consumer that defines the actions to be performed
     *                                  on a ComponentContainer after resizing
     */
    void setAfterResizingInstructions(Consumer<ComponentContainer> afterResizingInstructions);

    /**
     * Adapts the size of the contained component to match its parent's dimensions,
     * executing the provided callback once the adjustment is complete.
     */
    void adaptComponentSize();
}
