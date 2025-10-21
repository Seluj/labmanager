package fr.utbm.ciad.labmanager.views.appviews.charts;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerVerticalLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryBarChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryNightingaleRoseChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryPieChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryBarChart;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryNightingaleRoseChart;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryPieChart;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Display the charts
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "charts", layout = MainLayout.class)
@PermitAll
public class ChartsView extends AbstractLoggerVerticalLayout {

    private final TabSheet tabSheet;

    private final PublicationCategoryChartFactory<PublicationCategoryBarChart> barChartFactory;

    private final PublicationCategoryChartFactory<PublicationCategoryPieChart> pieChartFactory;

    private final PublicationCategoryChartFactory<PublicationCategoryNightingaleRoseChart> nightingaleChartFactory;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @param loggerFactory      the factory to be used for the composite logger.
     */
    public ChartsView(@Autowired PublicationService publicationService,
                      @Autowired ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);

        barChartFactory = new PublicationCategoryBarChartFactory();
        pieChartFactory = new PublicationCategoryPieChartFactory();
        nightingaleChartFactory = new PublicationCategoryNightingaleRoseChartFactory();

        PublicationCategoryLayout<PublicationCategoryBarChart> barChart = new PublicationCategoryLayout<>(publicationService, barChartFactory);
        PublicationCategoryLayout<PublicationCategoryPieChart> pieChart = new PublicationCategoryLayout<>(publicationService, pieChartFactory);
        PublicationCategoryLayout<PublicationCategoryNightingaleRoseChart> nightingaleChart = new PublicationCategoryLayout<>(publicationService, nightingaleChartFactory);

        tabSheet = new TabSheet();

        tabSheet.add("BarChart",
                new Div(barChart));
        tabSheet.add("PieChart",
                new Div(pieChart));
        tabSheet.add("NightingaleRoseChart",
                new Div(nightingaleChart));
        add(tabSheet);

    }

}