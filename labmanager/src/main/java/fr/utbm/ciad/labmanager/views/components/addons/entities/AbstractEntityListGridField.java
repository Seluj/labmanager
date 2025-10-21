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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract implementation of a Vaadin component for input a list of entities using values in a grid row.
 *
 * @param <E> the type of the edited entity.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractEntityListGridField<E extends IdentifiableEntity> extends CustomField<List<E>>
        implements LocaleChangeObserver {

    private static final long serialVersionUID = -6361480560230838464L;

    private static final int MAX_GRID_HEIGHT = 200;

    /**
     * Accessor to the localized messages.
     */
    protected final MessageSourceAccessor messages;

    private final String editButtonLabelKey;

    private final Grid<E> grid;

    private final MenuItem editButton;

    private final MenuItem addButton;

    private final MenuItem removeButton;

    private List<E> data = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param messages           accessor to the localized messages.
     * @param editButtonLabelKey the key in the localized messages for the edit button label.
     */
    public AbstractEntityListGridField(MessageSourceAccessor messages, String editButtonLabelKey) {
        this.messages = messages;
        this.editButtonLabelKey = editButtonLabelKey;

        final var menuBar = new MenuBar();
        this.editButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.PEN_ALT_SOLID, null, null);
        this.editButton.setEnabled(false);

        this.addButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.PLUS_CIRCLE_SOLID, null, null);
        this.addButton.setEnabled(true);

        this.removeButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.TRASH_ALT_SOLID, null, null);
        this.removeButton.setEnabled(false);

        this.grid = new Grid<>();
        this.grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.MATERIAL_COLUMN_DIVIDERS);

        createColumns(this.grid);

        this.grid.setMaxHeight(MAX_GRID_HEIGHT, Unit.PIXELS);
        this.grid.setSelectionMode(SelectionMode.SINGLE);

        final var vl = new VerticalLayout(menuBar, this.grid);
        vl.setSpacing(false);
        add(vl);

        // Add listener on selection change
        this.grid.addSelectionListener(event -> {
            final var hasSelection = !event.getAllSelectedItems().isEmpty();
            updateMenuItemEnablingState(hasSelection);
        });

        // Add callbacks for menu bar
        this.editButton.addClickListener(event -> editSelection());
        this.addButton.addClickListener(event -> addEntityInTable());
        this.removeButton.addClickListener(event -> removeEntityFromTable());

        // Open the editor on double click
        this.grid.addItemDoubleClickListener(it -> edit(it.getItem(), it.getColumn()));

        // Close the editor without saving when typing <ESC> key
        Shortcuts.addShortcutListener(this.grid, () -> {
            final var editor = this.grid.getEditor();
            if (editor != null && editor.isOpen()) {
                editor.cancel();
            }
        }, Key.ESCAPE).listenOn(this.grid);

        // Save and close the editor when typing <ENTER> key
        Shortcuts.addShortcutListener(this.grid, () -> {
            final var editor = this.grid.getEditor();
            if (editor != null && editor.isOpen()) {
                editor.save();
            }
        }, Key.ENTER).listenOn(this.grid);

        // Update the edit field when the value in the editor change
        this.grid.getEditor().addSaveListener(it -> updateValue());
    }

    private static <Q> ComboBox<Q> createBaseComboEditor(ListDataProvider<Q> dataProvider) {
        final var cellEditor = new ComboBox<Q>();
        cellEditor.setItems(dataProvider)
                .setIdentifierProvider(it -> {
                    return it.toString();
                });
        cellEditor.setItemLabelGenerator(it -> {
            return it.toString();
        });
        cellEditor.setManualValidation(true);
        cellEditor.setAllowCustomValue(false);
        cellEditor.setClearButtonVisible(true);
        return cellEditor;
    }

    /**
     * Invoked for creating the columns of the grid.
     *
     * @param grid the grid.
     */
    protected abstract void createColumns(Grid<E> grid);

    private void updateMenuItemEnablingState(boolean hasSelection) {
        this.editButton.setEnabled(hasSelection || this.grid.getListDataView().getItemCount() == 0);
        this.removeButton.setEnabled(hasSelection);
    }

    /**
     * Create a combo box for editing an enumeration. This function is a factory function
     * that should be invoked when build the grid editor for the entity.
     *
     * @param <E>  the type of the enum to edit.
     * @param type the type of enumeration.
     * @return the combo box.
     */
    protected <N extends Enum<N>> ComboBox<N> createBaseEnumEditor(Class<N> type) {
        final var dataProvider = new ListDataProvider<>(Arrays.asList(type.getEnumConstants()));
        return createBaseComboEditor(dataProvider);
    }

    /**
     * Create an integer field for editing a number. This function is a factory function
     * that should be invoked when build the grid editor for the entity.
     *
     * @return the integer field.
     */
    @SuppressWarnings("static-method")
    protected IntegerField createBaseIntegerEditor() {
        final var cellEditor = new IntegerField();
        cellEditor.setManualValidation(true);
        cellEditor.setClearButtonVisible(true);
        return cellEditor;
    }

    /**
     * Create an integer field for editing a number. This function is a factory function
     * that should be invoked when build the grid editor for the entity.
     *
     * @param min the minimal value to be input in the field.
     * @param max the maximal value to be input in the field.
     * @return the integer field.
     */
    protected IntegerField createBaseIntegerEditor(int min, int max) {
        final var cellEditor = createBaseIntegerEditor();
        cellEditor.setMin(min);
        cellEditor.setMax(max);
        return cellEditor;
    }

    /**
     * Replies the grid editor.
     *
     * @return the editor
     */
    protected Editor<E> getGridEditor() {
        return this.grid.getEditor();
    }

    /**
     * Replies the grid.
     *
     * @return the grid
     */
    protected Grid<E> getGrid() {
        return this.grid;
    }

    /**
     * Invoked when entity information must be edited for the current selection.
     *
     * @param item the item to edit.
     */
    protected void edit(E item) {
        final var editor = this.grid.getEditor();
        if (editor.isOpen()) {
            editor.save();
        }
        final Binder<E> editorBinder = new Binder<>();
        editor.setBinder(editorBinder);
        editor.editItem(item);
    }

    private void edit(E item, Column<E> column) {
        edit(item);
        if (column != null) {
            if (column.getEditorComponent() instanceof Focusable<?> focusable) {
                focusable.focus();
            }
        }
    }

    private void editSelection() {
        final var items = this.grid.getSelectedItems();
        if (!items.isEmpty()) {
            var item = items.iterator().next();
            edit(item);
        }
    }

    /**
     * Factory method for creating an empty entity.
     *
     * @return the empty entity.
     */
    protected abstract E createEntityInstance();

    private void addEntityInTable() {
        final var entity = createEntityInstance();
        addEntityInTable(entity);
    }

    /**
     * Invoked when entity information must be added in the grid.
     *
     * @param entity the entity to be added.
     */
    protected void addEntityInTable(E entity) {
        if (this.data.add(entity)) {
            this.grid.getListDataView().addItem(entity);
            this.grid.getListDataView().refreshAll();
            this.grid.select(entity);
        }
    }

    /**
     * Invoked when selected entity information must be deleted.
     */
    protected void removeEntityFromTable() {
        final var items = this.grid.getSelectedItems();
        if (!items.isEmpty()) {
            final var changed = this.data.removeAll(items);
            this.grid.getListDataView().removeItems(items).refreshAll();
            if (changed) {
                updateValue();
            }
        }
    }

    @Override
    protected List<E> generateModelValue() {
        return new ArrayList<>(this.data);
    }

    @Override
    protected void setPresentationValue(List<E> newPresentationValue) {
        this.data = new ArrayList<>(newPresentationValue);
        this.grid.setItems(this.data);
        updateMenuItemEnablingState(false);
    }

    /**
     * Replies a standard validator for the data inside this field.
     *
     * @return the validator or {@code null} if none.
     */
    public Validator<List<E>> newStandardValidator() {
        return null;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.editButton.setAriaLabel(getTranslation(this.editButtonLabelKey));
//		this.addBeforeButton.setAriaLabel(getTranslation("views.rankings.add_before")); //$NON-NLS-1$
//		this.addAfterButton.setAriaLabel(getTranslation("views.rankings.add_after")); //$NON-NLS-1$
//		this.removeButton.setAriaLabel(getTranslation("views.rankings.remove")); //$NON-NLS-1$
    }

}
