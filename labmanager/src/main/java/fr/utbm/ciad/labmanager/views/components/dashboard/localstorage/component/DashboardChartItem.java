package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component;

import com.vaadin.flow.component.Component;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.component.DashboardChartFactory;

import java.util.Set;

/**
 * Class defining items describing dashBoard chart components that can be store locally
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardChartItem extends AbstractDashboardComponentItem {

    private Set<String> multiSelectComboBoxItems;
    private Integer yearRangeStartValue;
    private Integer yearRangeEndValue;
    private boolean chartGenerated;

    /**
     * Default Constructor
     */
    public DashboardChartItem() {
        super(new DashboardChartFactory());
    }

    /**
     * Constructor
     *
     * @param index the index of the component in the dashboard
     */
    public DashboardChartItem(int index) {
        super(new DashboardChartFactory(), index);
    }

    /**
     * Constructor
     *
     * @param index         the index of the component
     * @param componentType the type of the component
     * @param width         the width of the component
     * @param height        the height of the component
     */
    public DashboardChartItem(int index, DashboardComponentType componentType, String width, String height) {
        super(new DashboardChartFactory(), index, componentType, width, height);
    }

    /**
     * Constructor to initialize the item from attributes of a component.
     *
     * @param index     the index of the component
     * @param component the existing component to extract properties from
     */
    public DashboardChartItem(int index, Component component, DashboardComponentType componentType) {
        super(new DashboardChartFactory(), index, component, componentType);
        if (component instanceof PublicationCategoryLayout<?> publicationCategoryLayout) {
            multiSelectComboBoxItems = publicationCategoryLayout.getMultiSelectComboBoxItems();
            yearRangeStartValue = publicationCategoryLayout.getYearRangeStartValue();
            yearRangeEndValue = publicationCategoryLayout.getYearRangeEndValue();
            chartGenerated = publicationCategoryLayout.isChartGenerated();
        }
    }

    /**
     * Constructor to create a new DashBoardComponentItem from an existing one.
     *
     * @param item the existing DashBoardComponentItem to copy properties from
     */
    public DashboardChartItem(DashboardChartItem item) {
        super(new DashboardChartFactory(), item);
        this.multiSelectComboBoxItems = item.getMultiSelectComboBoxItems();
        this.yearRangeStartValue = item.getYearRangeStartValue();
        this.yearRangeEndValue = item.getYearRangeEndValue();
        this.chartGenerated = item.isChartGenerated();
    }

    @Override
    public void createId(String idPart) {
        super.setId("DashBoardChart_" + idPart);
    }

    /**
     * Returns the start year value for the chart's range.
     *
     * @return the start year value for the chart's range
     */
    public Integer getYearRangeStartValue() {
        return yearRangeStartValue;
    }

    /**
     * Returns the end year value for the chart's range.
     *
     * @return the end year value for the chart's range
     */
    public Integer getYearRangeEndValue() {
        return yearRangeEndValue;
    }

    /**
     * Returns the set of multi-select combo box items for the chart.
     *
     * @return the set of multi-select combo box items
     */
    public Set<String> getMultiSelectComboBoxItems() {
        return multiSelectComboBoxItems;
    }

    /**
     * Returns whether the chart has been generated.
     *
     * @return true if the chart is generated, false otherwise
     */
    public boolean isChartGenerated() {
        return chartGenerated;
    }
}

