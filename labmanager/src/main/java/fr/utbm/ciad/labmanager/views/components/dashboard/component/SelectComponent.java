package fr.utbm.ciad.labmanager.views.components.dashboard.component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Manages the creation of a component with a specified name and provides the ability to apply custom styling to it.
 *
 * @param <T> The type of the component.
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class SelectComponent<T> {
    private final String name;
    private final Supplier<T> componentSupplier;
    private final Consumer<T> styleConsumer;

    /**
     * Default Constructor
     */
    public SelectComponent() {
        name = "";
        componentSupplier = null;
        styleConsumer = null;
    }

    /**
     * Constructor
     *
     * @param name              The name of the component.
     * @param componentSupplier A supplier to provide the component instance.
     * @param styleConsumer     A consumer to apply styles to the component, can be null.
     */
    public SelectComponent(String name, Supplier<T> componentSupplier, Consumer<T> styleConsumer) {
        this.name = name;
        this.componentSupplier = componentSupplier;
        this.styleConsumer = styleConsumer;
    }

    /**
     * Constructor
     *
     * @param name              The name of the component.
     * @param componentSupplier A supplier to provide the component instance.
     */
    public SelectComponent(String name, Supplier<T> componentSupplier) {
        this(name, componentSupplier, null);
    }

    /**
     * Retrieves the component provided by the supplier.
     * If a style consumer is defined, it applies styling to the component.
     *
     * @return The component instance or null if the supplier is not defined.
     */
    public T getComponent() {
        if (componentSupplier != null) {
            T component = componentSupplier.get();
            if (styleConsumer != null) {
                styleConsumer.accept(component);
            }
            return component;
        }
        return null;
    }

    /**
     * Returns the name of the component.
     *
     * @return The name of the component.
     */
    public String getName() {
        return name;
    }
}
