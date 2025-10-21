package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.component;

import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryBarChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryNightingaleRoseChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryPieChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardChartItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;

/**
 * Factory class for creating PublicationCategoryLayout components based on a DashBoardChartItem.
 * Provides the logic for creating specific types of chart components based on the component type.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardChartFactory implements DashboardComponentFactory<DashboardChartItem, PublicationService> {

    @Override
    public PublicationCategoryLayout<?> createComponent(PublicationService publicationService, DashboardChartItem dashBoardChartItem) {
        PublicationCategoryChartFactory factory = getPublicationCategoryChartFactory(dashBoardChartItem);

        PublicationCategoryLayout component = new PublicationCategoryLayout<>(publicationService,
                factory,
                dashBoardChartItem.getMultiSelectComboBoxItems(),
                dashBoardChartItem.getYearRangeStartValue(),
                dashBoardChartItem.getYearRangeEndValue(),
                dashBoardChartItem.isChartGenerated());

        component.getStyle()
                .setWidth(dashBoardChartItem.getWidth())
                .setHeight(dashBoardChartItem.getHeight());

        return component;
    }

    /**
     * Retrieves the appropriate PublicationCategoryChartFactory based on the DashBoardChartItem's component type.
     *
     * @param dashBoardChartItem the DashBoardChartItem used to determine the factory to be returned
     * @return the corresponding PublicationCategoryChartFactory for the chart type, or null if the type is not recognized
     */
    private PublicationCategoryChartFactory getPublicationCategoryChartFactory(DashboardChartItem dashBoardChartItem) {
        return switch (dashBoardChartItem.getComponentType()) {
            case DashboardComponentType.BAR_CHART -> new PublicationCategoryBarChartFactory();
            case DashboardComponentType.PIE_CHART -> new PublicationCategoryPieChartFactory();
            case DashboardComponentType.NIGHTINGALE_CHART -> new PublicationCategoryNightingaleRoseChartFactory();
            default -> null;
        };
    }
}

