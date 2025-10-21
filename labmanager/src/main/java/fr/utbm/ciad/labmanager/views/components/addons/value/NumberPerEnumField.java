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

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.AbstractNumberField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.DoubleToFloatConverter;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Vaadin component for input a number per enumeration constant.
 *
 * @param <E> the type of the enumeration elements.
 * @param <N> the type of the number elements.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class NumberPerEnumField<E extends Enum<E>, N extends Number> extends CustomField<Map<E, N>>
        implements LocaleChangeObserver {

    private static final long serialVersionUID = -4701417295811010186L;

    private static final int MAX_GRID_HEIGHT = 150;

    private final Converter<Double, N> doubleConverter;

    private final Converter<Integer, N> integerConverter;

    private final Grid<DataItem<E, N>> grid;

    private final Column<DataItem<E, N>> enumColumn;

    private final Column<DataItem<E, N>> numberColumn;

    private final MenuItem editButton;

    private final MenuItem addButton;

    private final MenuItem removeButton;

    private final String enumColumnHeaderKey;

    private final String numberColumnHeaderKey;

    private final String editButtonKey;

    private final String addButtonKey;

    private final String removeButtonKey;

    private final Consumer<ComboBox<E>> comboFiller;

    private ItemLabelGenerator<E> comboItemLabelGenerator;

    /**
     * Constructor.
     *
     * @param enumColumnHeaderKey   the translation key for the title of the enum value's column.
     * @param numberColumnHeaderKey the translation key for the title of the number value's column.
     * @param editButtonKey         the translation key for the label of the edition's button.
     * @param addButtonKey          the translation key for the label of the addition's button.
     * @param removeButtonKey       the translation key for the label of the removal's button.
     * @param comboFiller           the callback for filling the enum value combo box.
     * @param doubleConverter       the value converter from double to the number.
     * @param integerConverter      the value converter from integer to the number.
     */
    protected NumberPerEnumField(
            String enumColumnHeaderKey, String numberColumnHeaderKey, String editButtonKey,
            String addButtonKey, String removeButtonKey, Consumer<ComboBox<E>> comboFiller,
            Converter<Double, N> doubleConverter, Converter<Integer, N> integerConverter) {
        this.enumColumnHeaderKey = enumColumnHeaderKey;
        this.numberColumnHeaderKey = numberColumnHeaderKey;
        this.editButtonKey = editButtonKey;
        this.addButtonKey = addButtonKey;
        this.removeButtonKey = removeButtonKey;
        this.comboFiller = comboFiller;
        this.doubleConverter = doubleConverter;
        this.integerConverter = integerConverter;

        final var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);

        this.addButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.PLUS_SOLID, null, null);
        this.addButton.setEnabled(true);

        this.editButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.PEN_ALT_SOLID, null, null);
        this.editButton.setEnabled(false);

        this.removeButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.TRASH_ALT_SOLID, null, null);
        this.removeButton.setEnabled(false);

        this.grid = new Grid<>();
        this.grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_ROW_BORDERS);

        this.enumColumn = this.grid.addColumn(item -> getEnumValueLabel(item.getEnumValue()))
                .setAutoWidth(true)
                .setEditorComponent(this::createEnumValueEditor);

        this.numberColumn = this.grid.addColumn(item -> getNumberValueLabel(item.getNumberValue()))
                .setAutoWidth(true)
                .setEditorComponent(this::createNumberValueEditor);

        this.grid.setMaxHeight(MAX_GRID_HEIGHT, Unit.PIXELS);
        this.grid.setSelectionMode(SelectionMode.SINGLE);

        final var vl = new VerticalLayout(menuBar, this.grid);
        vl.setSpacing(false);
        add(vl);

        setInvalid(false);

        // Add listener on selection change
        this.grid.addSelectionListener(event -> {
            final var hasSelection = !event.getAllSelectedItems().isEmpty();
            updateMenuItemEnablingState(hasSelection);
        });

        // Add callbacks for menu bar
        this.addButton.addClickListener(event -> addAfterSelectionOrAtEnd());
        this.removeButton.addClickListener(event -> removeSelection());
        this.editButton.addClickListener(event -> editSelection());

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

    /**
     * Create a input field that enables to input double value per enum constant.
     *
     * @param enumColumnHeaderKey   the translation key for the title of the enum value's column.
     * @param numberColumnHeaderKey the translation key for the title of the number value's column.
     * @param editButtonKey         the translation key for the label of the edition's button.
     * @param addButtonKey          the translation key for the label of the addition's button.
     * @param removeButtonKey       the translation key for the label of the removal's button.
     * @param comboFiller           the callback for filling the enum value combo box.
     * @return the input field.
     */
    public static <E extends Enum<E>> NumberPerEnumField<E, Double> forDouble(String enumColumnHeaderKey, String numberColumnHeaderKey, String editButtonKey,
                                                                              String addButtonKey, String removeButtonKey, Consumer<ComboBox<E>> comboFiller) {
        return new NumberPerEnumField<>(enumColumnHeaderKey, numberColumnHeaderKey, editButtonKey, addButtonKey, removeButtonKey, comboFiller,
                new Converter<Double, Double>() {
                    private static final long serialVersionUID = -901305864142872808L;

                    @Override
                    public Result<Double> convertToModel(Double value, ValueContext context) {
                        return Result.ok(value);
                    }

                    @Override
                    public Double convertToPresentation(Double value, ValueContext context) {
                        return value;
                    }
                }, null);
    }

    /**
     * Create a input field that enables to input float value per enum constant.
     *
     * @param enumColumnHeaderKey   the translation key for the title of the enum value's column.
     * @param numberColumnHeaderKey the translation key for the title of the number value's column.
     * @param editButtonKey         the translation key for the label of the edition's button.
     * @param addButtonKey          the translation key for the label of the addition's button.
     * @param removeButtonKey       the translation key for the label of the removal's button.
     * @param comboFiller           the callback for filling the enum value combo box.
     * @return the input field.
     */
    public static <E extends Enum<E>> NumberPerEnumField<E, Float> forFloat(String enumColumnHeaderKey, String numberColumnHeaderKey, String editButtonKey,
                                                                            String addButtonKey, String removeButtonKey, Consumer<ComboBox<E>> comboFiller) {
        return new NumberPerEnumField<>(enumColumnHeaderKey, numberColumnHeaderKey, editButtonKey, addButtonKey, removeButtonKey, comboFiller, new DoubleToFloatConverter(), null);
    }

    /**
     * Create a input field that enables to input integer value per enum constant.
     *
     * @param enumColumnHeaderKey   the translation key for the title of the enum value's column.
     * @param numberColumnHeaderKey the translation key for the title of the number value's column.
     * @param editButtonKey         the translation key for the label of the edition's button.
     * @param addButtonKey          the translation key for the label of the addition's button.
     * @param removeButtonKey       the translation key for the label of the removal's button.
     * @param comboFiller           the callback for filling the enum value combo box.
     * @return the input field.
     */
    public static <E extends Enum<E>> NumberPerEnumField<E, Integer> forInteger(String enumColumnHeaderKey, String numberColumnHeaderKey, String editButtonKey,
                                                                                String addButtonKey, String removeButtonKey, Consumer<ComboBox<E>> comboFiller) {
        return new NumberPerEnumField<>(enumColumnHeaderKey, numberColumnHeaderKey, editButtonKey, addButtonKey, removeButtonKey, comboFiller, null,
                new Converter<Integer, Integer>() {
                    private static final long serialVersionUID = -4156730401432877076L;

                    @Override
                    public Result<Integer> convertToModel(Integer value, ValueContext context) {
                        return Result.ok(value);
                    }

                    @Override
                    public Integer convertToPresentation(Integer value, ValueContext context) {
                        return value;
                    }
                });
    }

    /**
     * Change the height of the grid. This function differs to {@link #setHeight(float, Unit)} in the fact
     * it changes the height of the inner grid and not of the global component.
     *
     * @param height the height value.
     * @param unit   the height unit.
     */
    public void setGridHeight(float height, Unit unit) {
        this.grid.setHeight(height, unit);
    }

    /**
     * Change the renderer for the enum values. By default, the names of the enumeration items are used.
     *
     * @param renderer the renderer of the enum value cells.
     * @see #setEnumValueItemLabelGenerator(ItemLabelGenerator)
     */
    public void setEnumValueRenderer(Renderer<DataItem<E, N>> renderer) {
        this.comboItemLabelGenerator = null;
        if (renderer == null) {
            this.enumColumn.setRenderer(new TextRenderer<>(item -> getEnumValueLabel(item.getEnumValue())));
        } else {
            this.enumColumn.setRenderer(renderer);
        }
    }

    /**
     * Change the renderer for the enum values. By default, the names of the enumeration items are used.
     *
     * @param renderer the renderer of the enum value cells.
     * @see #setEnumValueRenderer(Renderer)
     */
    public void setEnumValueItemLabelGenerator(ItemLabelGenerator<E> renderer) {
        this.comboItemLabelGenerator = it -> it == null ? getEnumValueLabel(it) : renderer.apply(it);
        if (this.comboItemLabelGenerator == null) {
            this.enumColumn.setRenderer(new TextRenderer<>(item -> getEnumValueLabel(item.getEnumValue())));
        } else {
            this.enumColumn.setRenderer(new TextRenderer<>(item -> this.comboItemLabelGenerator.apply(item.getEnumValue())));
        }
    }

    /**
     * Replies the default string label for the given enum value.
     *
     * @param enumValue the enum value.
     * @return the label for the enum value.
     */
    protected String getEnumValueLabel(E enumValue) {
        if (enumValue == null) {
            return ""; //$NON-NLS-1$
        }
        return enumValue.toString();
    }

    /**
     * Replies the string label for the given number value.
     *
     * @param numberValue the number value.
     * @return the label for the number value.
     */
    protected String getNumberValueLabel(N numberValue) {
        if (numberValue == null) {
            return ""; //$NON-NLS-1$
        }
        return numberValue.toString();
    }

    private void updateMenuItemEnablingState(boolean hasSelection) {
        this.addButton.setEnabled(true);
        this.editButton.setEnabled(hasSelection);
        this.removeButton.setEnabled(hasSelection);
    }

    /**
     * Invoked for creating the editor of the enum value. This function is a factory function
     * that should be invoked when build the grid editor for the enum value.
     *
     * @param item the edited item.
     * @return the editor.
     */
    protected ComboBox<E> createEnumValueEditor(DataItem<E, N> item) {
        final var cellEditor = new ComboBox<E>();
        this.comboFiller.accept(cellEditor);
        cellEditor.getListDataView()
                .setIdentifierProvider(it -> {
                    if (it == null) {
                        return ""; //$NON-NLS-1$
                    }
                    return it.toString();
                });
        if (this.comboItemLabelGenerator == null) {
            cellEditor.setItemLabelGenerator(this::getEnumValueLabel);
        } else {
            cellEditor.setItemLabelGenerator(this.comboItemLabelGenerator);
        }
        cellEditor.setManualValidation(true);
        cellEditor.setAllowCustomValue(false);
        cellEditor.setClearButtonVisible(true);

        final var binder = getGridEditor().getBinder();
        binder.forField(cellEditor).bind(DataItem::getEnumValue, DataItem::setEnumValue);
        return cellEditor;
    }

    /**
     * Create a number field. This function is a factory function
     * that should be invoked when build the grid editor for the number value.
     *
     * @param item the edited item.
     * @return the number field.
     */
    protected AbstractNumberField<?, ?> createNumberValueEditor(DataItem<E, N> item) {
        if (this.doubleConverter != null) {
            final var cellEditor = new NumberField();
            cellEditor.setManualValidation(true);
            cellEditor.setClearButtonVisible(true);
            final var binder = getGridEditor().getBinder();
            binder.forField(cellEditor)
                    .withValidator(new DoubleRangeValidator(getTranslation("views.enum_number_field.error"), Double.valueOf(0.), null)) //$NON-NLS-1$
                    .withConverter(this.doubleConverter)
                    .bind(DataItem::getNumberValue, DataItem::setNumberValue);
            return cellEditor;
        }

        if (this.integerConverter != null) {
            final var cellEditor = new IntegerField();
            cellEditor.setManualValidation(true);
            cellEditor.setClearButtonVisible(true);
            final var binder = getGridEditor().getBinder();
            binder.forField(cellEditor)
                    .withValidator(new IntegerRangeValidator(getTranslation("views.enum_number_field.error"), Integer.valueOf(0), null)) //$NON-NLS-1$
                    .withConverter(this.integerConverter)
                    .bind(DataItem::getNumberValue, DataItem::setNumberValue);
            return cellEditor;
        }

        throw new IllegalStateException();
    }

    /**
     * Replies the grid editor.
     *
     * @return the editor
     */
    protected Editor<DataItem<E, N>> getGridEditor() {
        return this.grid.getEditor();
    }

    /**
     * Replies the grid.
     *
     * @return the grid
     */
    protected Grid<DataItem<E, N>> getGrid() {
        return this.grid;
    }

    /**
     * Invoked when ranking information must be edited for the current selection.
     *
     * @param item the item to edit.
     */
    protected void edit(DataItem<E, N> item) {
        final var editor = this.grid.getEditor();
        if (editor.isOpen()) {
            editor.save();
        }
        final Binder<DataItem<E, N>> editorBinder = new Binder<>();
        editor.setBinder(editorBinder);
        editor.editItem(item);
    }

    private void edit(DataItem<E, N> item, Column<DataItem<E, N>> column) {
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

    private void addItem(DataItem<E, N> previous) {
        final var dataItem = new DataItem<E, N>();
        if (previous != null) {
            this.grid.getListDataView().addItemAfter(dataItem, previous);
        } else {
            this.grid.getListDataView().addItem(dataItem);
        }
        this.grid.getListDataView().refreshAll();
        this.grid.select(dataItem);
        edit(dataItem);
    }

    /**
     * Invoked when information must be added after the current selection or at the end of the list.
     */
    protected void addAfterSelectionOrAtEnd() {
        var added = false;
        final var selectedItems = this.grid.getSelectedItems();
        if (!selectedItems.isEmpty()) {
            var item = selectedItems.iterator().next();
            addItem(item);
            added = true;
        }
        if (!added) {
            addItem(null);
        }
    }

    /**
     * Invoked when selected ranking information must be deleted.
     */
    protected void removeSelection() {
        final var items = this.grid.getSelectedItems();
        if (!items.isEmpty()) {
            this.grid.getListDataView().removeItems(items).refreshAll();
            updateValue();
        }
    }

    @Override
    protected Map<E, N> generateModelValue() {
        return this.grid.getListDataView().getItems()
                .filter(it -> it.getEnumValue() != null && it.getNumberValue() != null)
                .collect(Collectors.toMap(
                        it -> it.getEnumValue(),
                        it -> it.getNumberValue()));
    }

    @Override
    protected void setPresentationValue(Map<E, N> newPresentationValue) {
        if (newPresentationValue != null && !newPresentationValue.isEmpty()) {
            final var viewData = newPresentationValue.entrySet().stream()
                    .map(it -> new DataItem<>(it.getKey(), it.getValue()))
                    .collect(Collectors.toList());
            this.grid.setItems(viewData);
        } else {
            this.grid.setItems(new ArrayList<>());
        }
        updateMenuItemEnablingState(false);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.enumColumn.setHeader(getTranslation(this.enumColumnHeaderKey));
        this.numberColumn.setHeader(getTranslation(this.numberColumnHeaderKey));
        this.editButton.setAriaLabel(getTranslation(this.editButtonKey));
        this.addButton.setAriaLabel(getTranslation(this.addButtonKey));
        this.removeButton.setAriaLabel(getTranslation(this.removeButtonKey));
    }

    /**
     * Vaadin component for input an input for enum-number input.
     *
     * @param <E> the type of the enumeration element.
     * @param <N> the type of the number element.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public static class DataItem<E extends Enum<E>, N extends Number> implements Serializable {

        private static final long serialVersionUID = 693779807932850902L;

        private E enumValue;

        private N numberValue;

        /**
         * Constructor.
         *
         * @param enumValue   the enum value.
         * @param numberValue the number value.
         */
        DataItem(E enumValue, N numberValue) {
            this.enumValue = enumValue;
            this.numberValue = numberValue;
        }

        /**
         * Constructor.
         */
        DataItem() {
            //
        }

        /**
         * Replies the enum value.
         *
         * @return the enum value.
         */
        public E getEnumValue() {
            return this.enumValue;
        }

        /**
         * Change the enum value.
         *
         * @param value the enum value.
         */
        void setEnumValue(E value) {
            this.enumValue = value;
        }

        /**
         * Replies the number value.
         *
         * @return the number value.
         */
        public N getNumberValue() {
            return this.numberValue;
        }

        /**
         * Change the number value.
         *
         * @param value the number value.
         */
        void setNumberValue(N value) {
            this.numberValue = value;
        }

    }

}
