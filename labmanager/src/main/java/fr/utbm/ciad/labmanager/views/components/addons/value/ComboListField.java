/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.addons.value;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableComparator;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Component that enables to select a list of entities from a given combo of available entities.
 *
 * @param <T> the type of the edited entity.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ComboListField<T> extends CustomField<Set<T>> {

    private static final long serialVersionUID = -4788430817317673879L;

    private final ComboBox<T> availableEntities;

    private final EmptySetMultiSelectListBox<T> selectedEntities;

    private final MenuItem insertButton;

    private final MenuItem deleteButton;

    private final MenuItem additionButton;

    /**
     * Constructor without the button for adding a new entity.
     *
     * @param comparator the comparator to be used for sorting the entities in the list.
     */
    public ComboListField(SerializableComparator<T> comparator) {
        this(comparator, null);
    }

    /**
     * Constructor.
     *
     * @param comparator             the comparator to be used for sorting the entities in the list.
     * @param entityCreationCallback the callback that is invoked for creating a new entity and add it inside
     *                               the list of selected entities. Argument is a lambda function that must be invoked for
     *                               saving the new entity. The argument of this lambda function is the new entity itself.
     */
    public ComboListField(SerializableComparator<T> comparator, Consumer<Consumer<T>> entityCreationCallback) {
        this.selectedEntities = new EmptySetMultiSelectListBox<>(comparator);

        this.availableEntities = new ComboBox<>();
        this.availableEntities.setWidthFull();

        final var tools = new MenuBar();
        tools.addThemeVariants(MenuBarVariant.LUMO_ICON);

        this.insertButton = ComponentFactory.addIconItem(tools, LineAwesomeIcon.PLUS_SOLID, null, null, it -> doAddition(true));
        this.insertButton.setEnabled(false);

        this.deleteButton = ComponentFactory.addIconItem(tools, LineAwesomeIcon.MINUS_SOLID, null, null, it -> doDeletion(true));
        this.deleteButton.setEnabled(false);

        if (entityCreationCallback != null) {
            final var userCallback = entityCreationCallback;
            final Consumer<T> finalCallback = it -> {
                if (it != null) {
                    addNewEntity(it, true);
                }
            };
            this.additionButton = ComponentFactory.addIconItem(tools, LineAwesomeIcon.PLUS_SQUARE_SOLID, null, null, it -> userCallback.accept(finalCallback));
        } else {
            this.additionButton = null;
        }

        final var additionPanel = new HorizontalLayout();
        additionPanel.add(this.availableEntities, tools);
        additionPanel.setAlignItems(Alignment.CENTER);
        additionPanel.setSpacing(false);
        additionPanel.setPadding(false);

        final var mainPanel = new VerticalLayout();
        mainPanel.add(this.selectedEntities.withScroller(), additionPanel);
        mainPanel.setAlignItems(Alignment.STRETCH);
        mainPanel.setSpacing(false);
        mainPanel.setPadding(false);
        add(mainPanel);

        this.availableEntities.addValueChangeListener(it -> this.insertButton.setEnabled(it.getValue() != null));

        this.selectedEntities.addSelectionListener(it -> this.deleteButton.setEnabled(!it.getAllSelectedItems().isEmpty()));
    }

    /**
     * Change the height of the list. This function differs to {@link #setHeight(float, Unit)} in the fact
     * it changes the height of the inner list and not of the global component.
     *
     * @param height the height value.
     * @param unit   the height unit.
     */
    public void setListHeight(float height, Unit unit) {
        this.selectedEntities.setHeight(height, unit);
    }

    /**
     * Do the insertion of the given item. This function add the given item
     * in the combo box of available entities and in the list of selected entities.
     *
     * @param item             the new item.
     * @param updateFieldValue indicates if the field component is notified
     *                         about a value change. In other words, the function
     *                         {@link #updateValue()} is invoked if this argument is evaluated
     *                         to {@code true} and it is not invoked otherwise.
     */
    @SuppressWarnings("unchecked")
    protected void addNewEntity(T newEntity, boolean updateFieldValue) {
        this.availableEntities.getDataProvider().refreshAll();
        this.selectedEntities.addEntity(newEntity);
        this.selectedEntities.select(newEntity);
        if (updateFieldValue) {
            updateValue();
        }
    }

    /**
     * Add the selected item in the list of available items into the list of selected entities.
     *
     * @param updateFieldValue indicates if the field component is notified
     *                         about a value change. In other words, the function
     *                         {@link #updateValue()} is invoked if this argument is evaluated
     *                         to {@code true} and it is not invoked otherwise.
     */
    protected void doAddition(boolean updateFieldValue) {
        final var selection = this.availableEntities.getValue();
        if (selection != null) {
            this.selectedEntities.addEntity(selection);
            // Clear the combo box selection
            this.availableEntities.setValue(null);
            if (updateFieldValue) {
                updateValue();
            }
        }
    }

    /**
     * Remove the selected items in the list of selected entities.
     *
     * @param updateFieldValue indicates if the field component is notified
     *                         about a value change. In other words, the function
     *                         {@link #updateValue()} is invoked if this argument is evaluated
     *                         to {@code true} and it is not invoked otherwise.
     */
    protected void doDeletion(boolean updateFieldValue) {
        final var selection = this.selectedEntities.getSelectedItems();
        if (selection != null) {
            // Copy the selection for avoiding ConcurrentModificationException
            for (final var entity : new ArrayList<>(selection)) {
                this.selectedEntities.removeEntity(entity);
            }
            if (updateFieldValue) {
                updateValue();
            }
        }
    }

    /**
     * Change the tooltip of the insertion button.
     *
     * @param text the text.
     */
    public void setAdditionTooltip(String text) {
        this.insertButton.setAriaLabel(text);
    }

    /**
     * Change the tooltip of the deletion button.
     *
     * @param text the text.
     */
    public void setDeletionTooltip(String text) {
        this.deleteButton.setAriaLabel(text);
    }

    /**
     * Change the tooltip of the creation button.
     *
     * @param text the text.
     */
    public void setCreationTooltip(String text) {
        if (this.additionButton != null) {
            this.additionButton.setAriaLabel(text);
        }
    }

    /**
     * Change the renderer of the entities in the list of available entities
     * and in the list of selected entities too.
     *
     * @param labelGenerator the generator of labels for the entities.
     */
    public void setEntityLabelGenerator(ItemLabelGenerator<T> labelGenerator) {
        this.availableEntities.setItemLabelGenerator(labelGenerator);
        this.selectedEntities.setItemLabelGenerator(labelGenerator);
    }

    /**
     * Change the renderers of the entities in the list of available entities (left)
     * and in the list of selected entities (right) too.
     * It is recommended to have separate instances of renderer because a renderer may
     * be linked to the container component.
     *
     * @param availableSelectedRenderer the renderer for the selected available entity in the combo box.
     * @param availableDropDownRenderer the renderer for the list of available entities in the drop-down box.
     * @param selectedListRenderer      the renderer for the list of selected entities.
     */
    public void setEntityRenderers(ItemLabelGenerator<T> availableSelectedRenderer, ComponentRenderer<? extends Component, T> availableDropDownRenderer, ComponentRenderer<? extends Component, T> selectedListRenderer) {
        this.availableEntities.setItemLabelGenerator(availableSelectedRenderer);
        this.availableEntities.setRenderer(availableDropDownRenderer);
        this.selectedEntities.setRenderer(selectedListRenderer);
    }

    /**
     * Change the list of items that are proposed as available.
     * The provided list is copied in a in-memory collection.
     *
     * @param items the list of items.
     */
    public void setAvailableEntities(FetchCallback<T, String> items) {
        this.availableEntities.setItems(items);
    }

    /**
     * Change the list of items that are proposed as available.
     * The provided list is copied in a in-memory collection.
     *
     * @param items the list of items.
     */
    public void setAvailableEntities(Collection<T> items) {
        this.availableEntities.setItems(items);
    }

    /**
     * Change the list of items that are proposed as available.
     * The provided list is copied in a in-memory collection.
     *
     * @param items the list of items.
     */
    @SuppressWarnings("unchecked")
    public void setAvailableEntities(T... items) {
        this.availableEntities.setItems(items);
    }

    /**
     * Clear the selected entities and put them back in the available entities.
     */
    protected void clearSelectedEntities() {
        this.selectedEntities.deselectAll();
        //
        this.selectedEntities.clear();
    }

    /**
     * Add the given items in the list of selected items.
     * This function adds only the entities that are inside
     * the list of available entities.
     *
     * @param entities the list of items to add to the list of selected items.
     */
    protected void addSelectedEntities(Set<T> entities) {
        if (entities != null) {
            for (var entity : entities) {
                this.selectedEntities.addEntity(entity);
            }
        }
    }

    /**
     * Remove the given items from the list of selected items.
     *
     * @param entities the list of items to remove from the list of selected items.
     */
    protected void removeSelectedEntities(Set<T> entities) {
        if (entities != null) {
            for (var item : entities) {
                this.selectedEntities.removeEntity(item);
            }
        }
    }

    /**
     * Replies the list of selected items.
     *
     * @return the collection of selected items.
     */
    public Set<T> getSelectedEntities() {
        return this.selectedEntities.getEntities();
    }

    /**
     * Change the list of selected items.
     * The list of available items is updated for reflecting the selection.
     *
     * @param items the list of items.
     */
    public void setSelectedEntities(Set<T> items) {
        clearSelectedEntities();
        addSelectedEntities(items);
        updateValue();
    }

    @Override
    protected final Set<T> generateModelValue() {
        return getSelectedEntities();
    }

    @Override
    protected final void setPresentationValue(Set<T> currentModelValue) {
        clearSelectedEntities();
        addSelectedEntities(currentModelValue);
    }

    /**
     * Multi-selection list with empty set as empty value.
     *
     * @param <T> the type of the edited entity.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private static class EmptySetMultiSelectListBox<T> extends MultiSelectListBox<T> {

        private static final long serialVersionUID = 6939626675803601510L;

        private final SerializableComparator<T> comparator;

        private Set<T> dataStorage;

        private ListDataProvider<T> dataProvider;

        /**
         * Constructor.
         *
         * @param comparator the comparator to be used for sorting the entities in the list.
         */
        EmptySetMultiSelectListBox(SerializableComparator<T> comparator) {
            this.comparator = comparator;
            getStyle().setBackground("var(--lumo-shade-5pct)"); //$NON-NLS-1$
            //
            setEntities(null);
        }

        /**
         * Return the list inside a scroller.
         *
         * @return the scroller.
         */
        public Component withScroller() {
            // Use "height" instead of "max height" or "min height" for enabling the scroller
            setHeight(ViewConstants.DEFAULT_LIST_HEIGHT, Unit.PIXELS);
            setWidthFull();
            return this;
        }

        /**
         * Add the given entity in the list.
         *
         * @param entity the entity to add.
         */
        public void addEntity(T entity) {
            getListDataView().addItem(entity);
        }

        /**
         * Remove the given entity from the list.
         *
         * @param entity the entity to remove.
         * @return {@code true} if the entity was removed from the list. {@code false} if
         * the entity
         */
        public boolean removeEntity(T entity) {
            if (getListDataView().contains(entity)) {
                getListDataView().removeItem(entity);
                return true;
            }
            return false;
        }

        /**
         * Replies the entities that are inside the list.
         *
         * @return the collection of entities in the list.
         */
        Set<T> getEntities() {
            return getListDataView().getItems().collect(Collectors.toSet());
        }

        /**
         * Change the entities that are inside the list.
         *
         * @param entities the new collection of entities.
         */
        void setEntities(Collection<T> entities) {
            final var currentFilter = this.dataProvider == null ? null : this.dataProvider.getFilter();
            this.dataStorage = createStorageFor(entities);
            this.dataProvider = new ListDataProvider<>(this.dataStorage);
            this.dataProvider.setSortComparator(this.comparator);
            if (currentFilter != null) {
                this.dataProvider.setFilter(currentFilter);
            }
            setItems(this.dataProvider);
        }

        /**
         * Create a storage data structure that may be used for the internal lists for
         * containing the given entities.
         *
         * @param entities the entities to put in the created data structure.
         * @return the created data structure.
         */
        Set<T> createStorageFor(Collection<T> entities) {
            final var newCollection = new TreeSet<>(this.comparator);
            if (entities != null) {
                newCollection.addAll(entities);
            }
            return newCollection;
        }

        @Override
        public Set<T> getEmptyValue() {
            return new TreeSet<>(this.comparator);
        }

    }

}
