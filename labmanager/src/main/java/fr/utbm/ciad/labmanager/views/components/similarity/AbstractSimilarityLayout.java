package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
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
        grids = new java.util.ArrayList<>();

        setWidthFull();
        Button button = new Button("Check");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        button.addClickListener(event -> {
            createGrids();
        });
        add(button);
    }

}
