package fr.utbm.ciad.labmanager.views.components.charts.publicationcategory;

import com.storedobject.chart.Size;
import com.storedobject.chart.Toolbox;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.AbstractSOChartChart;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.storedobject.chart.Color.TRANSPARENT;

/**
 * Abstract implementation of a publication category chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPublicationCategoryChart extends AbstractSOChartChart implements PublicationCategoryChart {

    private final List<PublicationType> publicationTypeList;
    private final List<Integer> years;
    protected PublicationService publicationService;
    protected Toolbox toolbox;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     */
    public AbstractPublicationCategoryChart(@Autowired PublicationService publicationService) {

        this.publicationService = publicationService;
        publicationTypeList = this.publicationService.getAllTypes();
        years = new ArrayList<>();

        toolbox = new Toolbox();
        Toolbox.Download toolboxDownload = new Toolbox.Download();
        toolboxDownload.setResolution(15);
        toolbox.addButton(toolboxDownload, new Toolbox.Zoom());
        toolbox.getPosition(true).setLeft(Size.percentage(80));


        disableDefaultLegend();
        setSVGRendering();
        setDefaultBackground(TRANSPARENT);
        add(toolbox);

    }

    /**
     * Replies the list of publication types.
     *
     * @return the list of publication types.
     */
    public List<PublicationType> getPublicationTypeList() {
        return publicationTypeList;
    }

    /**
     * Replies the list of years.
     *
     * @return the list of years.
     */
    public List<Integer> getYears() {
        return years;
    }

}

