package fr.utbm.ciad.labmanager.views.components.charts.layout;

import com.storedobject.chart.SOChart;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.addons.value.YearRange;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.observer.ChartObserver;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryChart;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract implementation of the layout for displaying charts about publication categories.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPublicationCategoryLayout<T extends PublicationCategoryChart> extends AbstractChartLayout {

    private final PublicationService publicationService;
    private final MultiSelectComboBox multiSelectComboBox;
    private final HorizontalLayout chartHorizontalLayout;
    private final YearRange yearRange;
    private final List<ChartObserver> observers = new ArrayList<>();
    private PublicationCategoryChartFactory<T> factory;
    private Button validateButton;
    private T chart;
    private SOChart soChart;
    private String chartWidth;
    private String chartHeight;
    private boolean isFormHidden = false;

    public AbstractPublicationCategoryLayout(@Autowired PublicationService publicationService,
                                             PublicationCategoryChartFactory<T> factory,
                                             Set<String> multiSelectComboBoxItems,
                                             Integer yearRangeStartValue,
                                             Integer yearRangeEndValue,
                                             boolean generateChart) {
        this(publicationService, factory);

        if (multiSelectComboBoxItems != null && !multiSelectComboBoxItems.isEmpty()) {
            multiSelectComboBox.setValue(multiSelectComboBoxItems);
        }
        if (yearRangeStartValue != null) {
            yearRange.getStart().setValue(yearRangeStartValue);
        }
        if (yearRangeEndValue != null) {
            yearRange.getEnd().setValue(yearRangeEndValue);
        }
        if (multiSelectComboBoxItems != null && yearRangeStartValue != null && generateChart) {
            createChart();
        }
    }

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @param factory            the factory for creating publication category charts.
     */
    public AbstractPublicationCategoryLayout(@Autowired PublicationService publicationService, PublicationCategoryChartFactory<T> factory) {
        super();
        this.factory = factory;
        this.publicationService = publicationService;
        this.factory = getPublicationCategoryChartFactory();

        chart = this.factory.create(this.publicationService);

        chartHorizontalLayout = new HorizontalLayout();
        chartHorizontalLayout.setWidthFull();

        HorizontalLayout validationHorizontalLayout = new HorizontalLayout();
        validationHorizontalLayout.setWidthFull();

        yearRange = new YearRange(this.publicationService);
        yearRange.addValueChangeListener(e -> notifyObservers());

        multiSelectComboBox = new MultiSelectComboBox<>();
        multiSelectComboBox.setItems(this.publicationService.getAllCategories());
        multiSelectComboBox.setWidth(1000, Unit.PIXELS);
        multiSelectComboBox.addValueChangeListener(e -> {
            Set<String> oldValue = (Set<String>) e.getOldValue();
            Set<String> newValue = (Set<String>) e.getValue();

            Set<String> addedItems = new HashSet<>(newValue);
            addedItems.removeAll(oldValue);

            Set<String> removedItems = new HashSet<>(oldValue);
            removedItems.removeAll(newValue);

            if (!addedItems.isEmpty()) {
                validateButton.setEnabled(true);
            }

            if (!removedItems.isEmpty()) {
                if (multiSelectComboBox.getSelectedItems().isEmpty()) {
                    validateButton.setEnabled(false);
                }
            }
            notifyObservers();
        });

        validateButton = new Button(getTranslation("views.charts.create"));
        validateButton.setEnabled(false);
        validateButton.addClickListener(e -> {
            createChart();
            validateButton.setVisible(false);
        });

        validationHorizontalLayout.add(multiSelectComboBox);
        validationHorizontalLayout.add(validateButton);

        add(chartHorizontalLayout);
        VerticalLayout form = new VerticalLayout(yearRange, validationHorizontalLayout);
        form.setPadding(false);
        add(form);

    }

    private void createChart(Set<String> multiSelectComboBoxItems, Integer yearRangeStartValue, Integer yearRangeEndValue) {

        this.chart = factory.create(this.publicationService);
        if (yearRange.getEnd().isEmpty()) {
            chart.setYear(yearRangeStartValue);
        } else {
            chart.setPeriod(yearRangeStartValue, yearRangeEndValue);
        }

        for (String item : multiSelectComboBoxItems) {
            this.chart.addData(item);
        }

        soChart = this.chart.createChart();
        soChart.setSize(chartWidth, chartHeight);
        chartHorizontalLayout.add(soChart);
        if (isFormHidden) {
            yearRange.setVisible(false);
            multiSelectComboBox.setVisible(false);
        }

        notifyObservers();
    }

    private void createChart() {
        createChart(multiSelectComboBox.getSelectedItems(), yearRange.getChosenStartValue(), yearRange.getChosenEndValue());
    }

    /**
     * Replies the publication category chart factory.
     *
     * @return the publication category chart factory.
     */
    public PublicationCategoryChartFactory getPublicationCategoryChartFactory() {
        return factory;
    }

    @Override
    public void refreshChart() {
        if (soChart != null) {
            chartHorizontalLayout.remove(soChart);
            createChart();
        }
    }

    /**
     * Sets the size of the chart
     *
     * @param width  the width to set for the chart.
     * @param height the height to set for the chart.
     */
    public void setChartSize(String width, String height) {
        this.chartHeight = height;
        this.chartWidth = width;
        if (soChart != null) {
            soChart.setSize(width, height);
        }
    }

    /**
     * Sets the size of the layout
     *
     * @param width  the width to set for the layout.
     * @param height the height to set for the layout.
     */
    public void setSize(String width, String height) {
        getStyle().setWidth(width).setHeight(height);
    }

    /**
     * Toggles the visibility of the form that contains the year range and category selection.
     *
     * @param formHidden true to hide the form, false to show it.
     */
    public void setFormHidden(boolean formHidden) {
        this.isFormHidden = formHidden;
    }

    /**
     * Removes the current chart from the layout and resets the visibility
     * of the year range and multi-select combo box. Also resets the button text
     * to indicate the edit mode.
     */
    public void removeChart() {
        if (soChart != null) {
            chartHorizontalLayout.remove(soChart);
            soChart = null;
        }
        yearRange.setVisible(true);
        multiSelectComboBox.setVisible(true);
        validateButton.setVisible(true);
        notifyObservers();
    }

    /**
     * Checks if a chart has been generated
     *
     * @return true if a chart has been generated, false otherwise
     */
    public boolean isChartGenerated() {
        return soChart != null;
    }

    public Integer getYearRangeStartValue() {
        return yearRange.getChosenStartValue();
    }

    public Integer getYearRangeEndValue() {
        return yearRange.getChosenEndValue();
    }

    public Set<String> getMultiSelectComboBoxItems() {
        return multiSelectComboBox.getSelectedItems();
    }


    public void addObserver(ChartObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ChartObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (ChartObserver observer : observers) {
            observer.onChartGenerated();
        }
    }
}
