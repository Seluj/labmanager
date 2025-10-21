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

package fr.utbm.ciad.labmanager.views.components.addons.ranking;

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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import fr.utbm.ciad.labmanager.data.QualityAnnualIndicators;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract implementation of a Vaadin component for input an annual rankings.
 *
 * @param <R> the type of the entity that stores the ranking information.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractAnnualRankingField<R extends QualityAnnualIndicators> extends CustomField<Map<Integer, R>>
        implements LocaleChangeObserver {

    private static final long serialVersionUID = -8061046932945837202L;

    private static final int MAX_GRID_HEIGHT = 200;

    private static final String SHADE_COLOR = "var(--lumo-shade-40pct)"; //$NON-NLS-1$

    private final Grid<DataItem<R>> grid;

    private final Column<DataItem<R>> yearColumn;

    private final MenuItem editButton;

    private final MenuItem addBeforeButton;

    private final MenuItem addAfterButton;

    private final MenuItem removeButton;

    private Map<Integer, R> data = new TreeMap<>();

    /**
     * Constructor.
     */
    public AbstractAnnualRankingField() {
        final var menuBar = new MenuBar();
        this.editButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.PEN_ALT_SOLID, null, null);
        this.editButton.setEnabled(false);

        this.addBeforeButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.ARROW_ALT_CIRCLE_UP_SOLID, null, null);
        this.addBeforeButton.setEnabled(true);

        this.addAfterButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.ARROW_ALT_CIRCLE_DOWN_SOLID, null, null);
        this.addAfterButton.setEnabled(false);

        this.removeButton = ComponentFactory.addIconItem(menuBar, LineAwesomeIcon.TRASH_ALT_SOLID, null, null);
        this.removeButton.setEnabled(false);

        this.grid = new Grid<>();
        this.grid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_ROW_BORDERS);

        this.yearColumn = this.grid.addColumn(item -> item.getYearString())
                .setAutoWidth(true);

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
        this.editButton.addClickListener(event -> editSelection());
        this.addBeforeButton.addClickListener(event -> addRankingBeforeSelection());
        this.addAfterButton.addClickListener(event -> addRankingAfterSelection());
        this.removeButton.addClickListener(event -> removeSelectedRanking());

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
     * Replies the default reference year for annual ranking.
     *
     * @return the default reference year.
     */
    public static int getDefaultReferenceYear() {
        return LocalDate.now().getYear() - 1;
    }

    private void updateMenuItemEnablingState(boolean hasSelection) {
        this.editButton.setEnabled(hasSelection || this.grid.getListDataView().getItemCount() == 0);
        this.addBeforeButton.setEnabled(hasSelection || this.grid.getListDataView().getItemCount() == 0);
        this.addAfterButton.setEnabled(hasSelection);
        this.removeButton.setEnabled(hasSelection);
    }

    private <Q> ComboBox<Q> createBaseComboEditor(ListDataProvider<Q> dataProvider, Q notRankedValue) {
        final var cellEditor = new ComboBox<Q>();
        cellEditor.setItems(dataProvider)
                .setIdentifierProvider(it -> {
                    if (it == null) {
                        return ""; //$NON-NLS-1$
                    }
                    return it.toString();
                });
        cellEditor.setItemLabelGenerator(it -> {
            if (it == null) {
                return getTranslation("views.rankings.inherit_information"); //$NON-NLS-1$
            }
            if (it == notRankedValue) {
                return getTranslation("views.rankings.no_ranking"); //$NON-NLS-1$
            }
            return it.toString();
        });
        cellEditor.setManualValidation(true);
        cellEditor.setAllowCustomValue(false);
        cellEditor.setClearButtonVisible(true);
        cellEditor.setPlaceholder(getTranslation("views.rankings.inherit_information")); //$NON-NLS-1$
        return cellEditor;
    }

    /**
     * Create a combo box for editing a quartile. This function is a factory function
     * that should be invoked when build the grid editor for the rankings.
     *
     * @return the combo box.
     */
    protected ComboBox<QuartileRanking> createBaseQuartileEditor() {
        final var dataProvider = new ListDataProvider<>(Arrays.asList(
                null,
                QuartileRanking.Q1,
                QuartileRanking.Q2,
                QuartileRanking.Q3,
                QuartileRanking.Q4,
                QuartileRanking.NR));
        return createBaseComboEditor(dataProvider, QuartileRanking.NR);
    }

    /**
     * Create a combo box for editing a core index. This function is a factory function
     * that should be invoked when build the grid editor for the rankings.
     *
     * @return the combo box.
     */
    protected ComboBox<CoreRanking> createBaseCoreIndexEditor() {
        final var dataProvider = new ListDataProvider<>(Arrays.asList(
                null,
                CoreRanking.A_STAR_STAR,
                CoreRanking.A_STAR,
                CoreRanking.A,
                CoreRanking.B,
                CoreRanking.C,
                CoreRanking.D,
                CoreRanking.NR));
        return createBaseComboEditor(dataProvider, CoreRanking.NR);
    }

    /**
     * Create a decimal field for editing an decimal indicator. This function is a factory function
     * that should be invoked when build the grid editor for the rankings.
     *
     * @return the decimal field.
     */
    protected NumberField createBaseFloatEditor() {
        final var cellEditor = new NumberField();
        cellEditor.setManualValidation(true);
        cellEditor.setClearButtonVisible(true);
        cellEditor.setPlaceholder(getTranslation("views.rankings.inherit_information")); //$NON-NLS-1$
        return cellEditor;
    }

    /**
     * Replies the grid editor.
     *
     * @return the editor
     */
    protected Editor<DataItem<R>> getGridEditor() {
        return this.grid.getEditor();
    }

    /**
     * Replies the grid.
     *
     * @return the grid
     */
    protected Grid<DataItem<R>> getGrid() {
        return this.grid;
    }

    /**
     * Invoked when ranking information must be edited for the current selection.
     *
     * @param item the item to edit.
     */
    protected void edit(DataItem<R> item) {
        final var editor = this.grid.getEditor();
        if (editor.isOpen()) {
            editor.save();
        }
        final Binder<DataItem<R>> editorBinder = new Binder<>();
        editor.setBinder(editorBinder);
        editor.editItem(item);
    }

    private void edit(DataItem<R> item, Column<DataItem<R>> column) {
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
     * Factory method for creating an empty collection of indicators.
     *
     * @return the empty collection of indicators.
     */
    protected abstract R createIndicatorInstance();

    private void addRanking(DataItem<R> previous, DataItem<R> next, Integer year) {
        final var indicators = createIndicatorInstance();
        indicators.setReferenceYear(year.intValue());
        this.data.put(year, indicators);

        final var dataItem = new DataItem<>(indicators, previous);
        if (next != null) {
            next.setPreviousItem(dataItem);
        }
        if (previous != null) {
            this.grid.getListDataView().addItemBefore(dataItem, previous);
        } else if (next != null) {
            this.grid.getListDataView().addItemAfter(dataItem, next);
        } else {
            this.grid.getListDataView().addItem(dataItem);
        }
        this.grid.getListDataView().refreshAll();
        this.grid.select(dataItem);
    }

    /**
     * Invoked when ranking information must be added before the current selection.
     */
    protected void addRankingBeforeSelection() {
        if (this.grid.getListDataView().getItemCount() == 0) {
            addRanking(null, null, Integer.valueOf(getDefaultReferenceYear()));
        } else {
            final var selectedItems = this.grid.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                var item = selectedItems.iterator().next();
                var year = item.getYear();

                var previousOpt = this.grid.getListDataView().getPreviousItem(item);
                while (previousOpt.isPresent() && previousOpt.get().getYear().intValue() == year.intValue() + 1) {
                    item = previousOpt.get();
                    year = item.getYear();
                    previousOpt = this.grid.getListDataView().getPreviousItem(item);
                }

                while (this.data.containsKey(year)) {
                    year = Integer.valueOf(year.intValue() + 1);
                }

                addRanking(item, this.grid.getListDataView().getPreviousItem(item).orElse(null), year);
            }
        }
    }

    /**
     * Invoked when ranking information must be added after the current selection.
     */
    protected void addRankingAfterSelection() {
        final var items = this.grid.getSelectedItems();
        if (!items.isEmpty()) {
            var item = items.iterator().next();
            var year = item.getYear();

            var nextOpt = this.grid.getListDataView().getNextItem(item);
            while (nextOpt.isPresent() && nextOpt.get().getYear().intValue() == year.intValue() - 1) {
                item = nextOpt.get();
                year = item.getYear();
                nextOpt = this.grid.getListDataView().getNextItem(item);
            }

            while (this.data.containsKey(year)) {
                year = Integer.valueOf(year.intValue() - 1);
            }

            addRanking(this.grid.getListDataView().getNextItem(item).orElse(null), item, year);
        }
    }

    /**
     * Invoked when selected ranking information must be deleted.
     */
    protected void removeSelectedRanking() {
        final var items = this.grid.getSelectedItems();
        if (!items.isEmpty()) {
            boolean changed = false;
            for (final var item : items) {
                if (item.indicators.isSignificant()) {
                    changed = true;
                }
                this.data.remove(item.getYear());
            }
            this.grid.getListDataView().removeItems(items).refreshAll();
            if (changed) {
                updateValue();
            }
        }
    }

    /**
     * Utility function for building the label of a quality indicator. This function is invoked
     * for displaying the quality indicators in the grid cells. It shows the value of the indicator
     * when it is provided for the item, or display the inherited value, i.e., the value that is
     * defined for the previous year in the ranking field.
     *
     * @param item     the item to show.
     * @param label    the function that replies the label to display for the item.
     * @param previous the function that relies the previous data item in the ranking field.
     * @return
     */
    protected Span getIndicatorLabel(DataItem<R> item, Function<DataItem<R>, String> label, Function<DataItem<R>, DataItem<R>> previous) {
        final var span = new Span();
        var value = label.apply(item);
        if (value == null) {
            value = getTranslation("views.rankings.no_ranking"); //$NON-NLS-1$
        }
        if (value.length() > 0) {
            span.setText(value);
        } else {
            final var recent = previous.apply(item);
            if (recent != null) {
                value = label.apply(recent);
                if (value == null) {
                    value = getTranslation("views.rankings.no_ranking"); //$NON-NLS-1$
                }
                if (value.length() > 0) {
                    span.setText(value);
                    span.getStyle().setColor(SHADE_COLOR);
                }
            }
        }
        return span;
    }

    @Override
    protected Map<Integer, R> generateModelValue() {
        return this.data.entrySet().stream()
                .filter(it -> it.getValue().isSignificant())
                .collect(Collectors.toMap(
                        it -> it.getKey(),
                        it -> it.getValue()));
    }

    @Override
    protected void setPresentationValue(Map<Integer, R> newPresentationValue) {
        this.data = new TreeMap<>((a, b) -> a.compareTo(b));
        if (newPresentationValue != null && !newPresentationValue.isEmpty()) {
            this.data.putAll(newPresentationValue);
        }
        final var viewData = new ArrayList<DataItem<R>>();
        DataItem<R> previous = null;
        for (final var indicators : this.data.values()) {
            final var item = new DataItem<>(indicators, previous);
            viewData.add(0, item);
            previous = item;
        }
        this.grid.setItems(viewData);
        updateMenuItemEnablingState(false);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.yearColumn.setHeader(getTranslation("views.year")); //$NON-NLS-1$

        this.editButton.setAriaLabel(getTranslation("views.rankings.edit")); //$NON-NLS-1$
        this.addBeforeButton.setAriaLabel(getTranslation("views.rankings.add_before")); //$NON-NLS-1$
        this.addAfterButton.setAriaLabel(getTranslation("views.rankings.add_after")); //$NON-NLS-1$
        this.removeButton.setAriaLabel(getTranslation("views.rankings.remove")); //$NON-NLS-1$
    }

    /**
     * Vaadin component for input an annual ranking.
     *
     * @param <R> the type of the entity that stores the ranking information.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class DataItem<R extends QualityAnnualIndicators> implements Serializable {

        private static final long serialVersionUID = -7088062576450174766L;

        private static final DecimalFormat IMPACT_FACTOR_FORMAT = new DecimalFormat("#0.00"); //$NON-NLS-1$

        private final R indicators;

        private DataItem<R> previousItem;

        /**
         * Constructor.
         *
         * @param indicators   the indicators.
         * @param previousItem the previous item.
         */
        DataItem(R indicators, DataItem<R> previousItem) {
            assert indicators != null;
            this.indicators = indicators;
            this.previousItem = previousItem;
        }

        /**
         * Utility function for formating a quartile for the ranking editor field.
         *
         * @param quartile the quartile to format.
         * @return the string representation of the quartile.
         */
        public static String getQIndexString(QuartileRanking quartile) {
            if (quartile != null) {
                if (quartile == QuartileRanking.NR) {
                    return null;
                }
                return quartile.toString();
            }
            return ""; //$NON-NLS-1$
        }

        /**
         * Utility function for formating a core index for the ranking editor field.
         *
         * @param core the core index to format.
         * @return the string representation of the core index.
         */
        public static String getCoreIndexString(CoreRanking core) {
            if (core != null) {
                if (core == CoreRanking.NR) {
                    return null;
                }
                return core.toString();
            }
            return ""; //$NON-NLS-1$
        }

        /**
         * Utility function for formating an impact factor for the ranking editor field.
         *
         * @param factor the factor to format.
         * @return the string representation of the factor.
         */
        public static String getImpactFactorString(float factor) {
            if (factor > 0f) {
                return IMPACT_FACTOR_FORMAT.format(factor);
            }
            return ""; //$NON-NLS-1$
        }

        /**
         * Replies the indicators.
         *
         * @return the indicators.
         */
        public R getIndicators() {
            return this.indicators;
        }

        /**
         * Replies the direct previous item.
         *
         * @return the direct previous item.
         */
        public DataItem<R> getPreviousItem() {
            return this.previousItem;
        }

        /**
         * Change the previous item.
         *
         * @param previous the new previous item.
         */
        void setPreviousItem(DataItem<R> previous) {
            this.previousItem = previous;
        }

        /**
         * Replies the previous data item that has a quality indicator extracted with the given function.
         *
         * @param extractor the function that enable to extract the quality indicator.
         * @return the previous indicator's item, or {@code null} if there is no previous item.
         */
        public DataItem<R> getPreviousItem(Predicate<R> extractor) {
            var item = this.previousItem;
            while (item != null) {
                final var hasIndicator = extractor.test(item.indicators);
                if (hasIndicator) {
                    return item;
                }
                item = item.previousItem;
            }
            return null;
        }

        /**
         * Replies the string representation of the year.
         *
         * @return the year.
         */
        public String getYearString() {
            return Integer.toString(this.indicators.getReferenceYear());
        }

        /**
         * Replies the year.
         *
         * @return the year.
         */
        public Integer getYear() {
            return Integer.valueOf(this.indicators.getReferenceYear());
        }

    }

}
