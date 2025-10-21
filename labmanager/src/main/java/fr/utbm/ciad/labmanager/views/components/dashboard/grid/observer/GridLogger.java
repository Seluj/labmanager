package fr.utbm.ciad.labmanager.views.components.dashboard.grid.observer;

import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

/**
 * Observes component-related event in a grid by defining actions to take when a changes to a component occur.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class GridLogger implements GridObserver {

    private Consumer<Component> addCallback;
    private Consumer<Component> addNewCallback;
    private Consumer<Component> changesCallback;
    private Consumer<Component> removeCallback;

    /**
     * Default Constructor
     */
    public GridLogger() {
    }

    /**
     * Constructor
     *
     * @param addCallback     the action to perform when a component is added
     * @param addNewCallback  the action to perform when a component is added
     * @param changesCallback the action to perform when changes occur to component
     * @param removeCallback  the action to perform when a component is removed
     */
    public GridLogger(Consumer<Component> addCallback, Consumer<Component> addNewCallback, Consumer<Component> changesCallback, Consumer<Component> removeCallback) {
        this.addCallback = addCallback;
        this.addNewCallback = addNewCallback;
        this.changesCallback = changesCallback;
        this.removeCallback = removeCallback;
    }

    @Override
    public void onComponentAdded(Component component) {
        addCallback.accept(component);
    }

    @Override
    public void onNewComponentAdded(Component component) {
        addNewCallback.accept(component);
    }

    @Override
    public void onComponentChanges(Component component) {
        changesCallback.accept(component);
    }

    @Override
    public void onComponentRemoved(Component component) {
        removeCallback.accept(component);
    }
}
