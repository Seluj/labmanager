package fr.utbm.ciad.labmanager.views.components.charts;

import com.storedobject.chart.SOChart;

/**
 * Abstract implementation of a chart from SOChart library.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractSOChartChart extends SOChart implements Chart {

    private static final long serialVersionUID = 7713188805965434256L;

    public AbstractSOChartChart() {
        setSize("1300px", "500px");
    }
}
