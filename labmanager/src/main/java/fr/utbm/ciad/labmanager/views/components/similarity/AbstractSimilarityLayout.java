package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.services.AbstractService;

import java.util.List;

/** Represent an Abstract for a specific layout for the similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractSimilarityLayout<T> extends VerticalLayout implements SimilarityLayout {

    protected List<Grid<T>> grids;

    /** Constructor.
     */
    public AbstractSimilarityLayout() {

        setWidthFull();
        add(new Button("Check", event -> createGrids()));
        grids = new java.util.ArrayList<>();
    }

}
