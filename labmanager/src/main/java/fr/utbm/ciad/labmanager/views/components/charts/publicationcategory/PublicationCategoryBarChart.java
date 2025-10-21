package fr.utbm.ciad.labmanager.views.components.charts.publicationcategory;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.storedobject.chart.Color.TRANSPARENT;

/**
 * Implementation of a publication category bar chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PublicationCategoryBarChart extends AbstractPublicationCategoryChart {

    private final PublicationService publicationService;
    private final CoordinateSystem rectangularCoordinate;
    private final XAxis xAxis;
    private final YAxis yAxis;
    private final List<Integer> totalPublication;
    private final List<PublicationType> publicationTypes;
    private final Legend legend;
    private Data xValues;
    private List<BarChart> barChartList;
    private CategoryData categoryData;
    private LineChart lineChart;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     */
    public PublicationCategoryBarChart(@Autowired PublicationService publicationService) {
        super(publicationService);


        this.publicationService = publicationService;
        barChartList = new ArrayList<>();

        // xValues
        xValues = new Data();
        categoryData = new CategoryData();


        xValues.setName(getTranslation("views.years"));

        // yValues
        publicationTypes = getPublicationTypeList();
        barChartList = new ArrayList<>();

        totalPublication = new ArrayList<>();


        // Rectangular coordinates
        rectangularCoordinate = new RectangularCoordinate();
        yAxis = new YAxis(DataType.NUMBER);
        xAxis = new XAxis(categoryData);
        xAxis.setName(getTranslation("views.years"));

        rectangularCoordinate.addAxis(xAxis, yAxis);
        rectangularCoordinate.getPosition(true).setTop(Size.percentage(15));

        legend = new Legend();
        legend.getPosition(true).setLeft(Size.percentage(1));
        legend.getPosition(true).setTop(Size.percentage(6));


        disableDefaultLegend();
        add(legend, rectangularCoordinate);
        setSVGRendering();
        setDefaultBackground(TRANSPARENT);
    }


    /**
     * Add a data in the chart, for example, the name of a chosen publication category
     *
     * @param chosenCategory the name of the chosen category.
     */
    public void addData(String chosenCategory) {
        Data data = new Data();
        Integer countTypePublicationV2;
        List<PublicationType> temporaryPublicationTypeList = publicationTypes.stream().filter(publicationType -> Objects.equals(publicationType.getCategory(true).toString(), chosenCategory)).toList();
        Integer totalYearCount = 0;
        for (int x = 0; x < getYears().size(); x++) {
            for (PublicationType publicationType : temporaryPublicationTypeList) {
                countTypePublicationV2 = this.publicationService.countPublicationsByTypeAndYear(publicationType, getYears().get(x));
                totalYearCount += countTypePublicationV2;
                totalPublication.set(x, countTypePublicationV2 + totalPublication.get(x));

            }
            data.add(totalYearCount);
            totalYearCount = 0;
        }


        BarChart barChart = new BarChart(categoryData, data);
        barChart.setName(chosenCategory);
        barChart.setStackName("BC");

        //System.out.println(totalPublication);

        barChartList.add(barChart);

    }

    /**
     * Remove a data in the chart, for example, the name of a chosen publication category
     *
     * @param chosenCategory the name of the chosen category.
     */
    public void removeData(String chosenCategory) {
        BarChart barChart = findBarChart(chosenCategory);
        Integer countTypePublicationV2;
        List<PublicationType> temporaryPublicationTypeList = publicationTypes.stream().filter(publicationType -> Objects.equals(publicationType.getCategory(true).toString(), chosenCategory)).toList();
        for (int x = 0; x < getYears().size(); x++) {
            for (PublicationType publicationType : temporaryPublicationTypeList) {
                countTypePublicationV2 = this.publicationService.countPublicationsByTypeAndYear(publicationType, getYears().get(x));
                totalPublication.set(x, countTypePublicationV2 - totalPublication.get(x));
            }
        }
        barChartList.remove(barChart);
    }

    /**
     * Replies the created chart (from SOChart library). The multiple bar charts needs to be plotted on a Coordinate System
     * in order to be displayed on the UI. Creation of a line chart in order to show the evolution of the number of publication.
     *
     * @return The created chart.
     */
    @Override
    public SOChart createChart() {

        for (BarChart b : barChartList) {
            b.plotOn(rectangularCoordinate);
        }

        Data data = new Data();
        data.addAll(totalPublication);

        lineChart = new LineChart(categoryData, data);
        lineChart.setName("Total of publications");
        lineChart.plotOn(rectangularCoordinate);

        return this;
    }

    /**
     * Method called at the creation of the chart. It precises that a unique year is provided by the user. Initialising
     * the x-axis of the Coordinate System.
     *
     * @param start The year of study.
     */
    @Override
    public void setYear(Integer start) {
        getYears().clear();
        categoryData = new CategoryData();
        xValues = new Data();

        getYears().add(start);

        xValues.add(start);
        categoryData.add(start.toString());

        barChartList.clear();

        for (int i = 0; i < getYears().size(); i++) {
            totalPublication.add(0);
        }

    }

    /**
     * Method called at the creation of the chart. It precises that a unique year is provided by the user. Initialising
     * the x-axis of the Coordinate System.
     *
     * @param start The beginning of the period.
     * @param end   The end of the period.
     */
    @Override
    public void setPeriod(Integer start, Integer end) {
        getYears().clear();
        categoryData = new CategoryData();
        xValues = new Data();

        Integer imp = start;
        while (imp <= end) {
            getYears().add(imp);
            imp++;
        }

        for (Integer year : getYears()) {
            xValues.add(year);
            categoryData.add(year.toString());
        }

        barChartList.clear();

        for (int i = 0; i < getYears().size(); i++) {
            totalPublication.add(0);
        }

    }

    /**
     * Method called for finding a bar chart in barChartList. Used in removeData().
     *
     * @param item The chosen category.
     */
    protected BarChart findBarChart(String item) {
        for (BarChart barChart : barChartList) {
            if (item.equals(barChart.getName())) {
                return barChart;
            }
        }
        return null;
    }


}
