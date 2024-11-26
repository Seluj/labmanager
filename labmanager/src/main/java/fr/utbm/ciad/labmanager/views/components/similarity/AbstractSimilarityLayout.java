package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import fr.utbm.ciad.labmanager.views.components.addons.slider.SingleSlider;

import java.util.*;

/** Represent an Abstract for a specific layout for the similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractSimilarityLayout<T> extends VerticalLayout implements SimilarityLayout<T> {

    protected List<Grid<T>> grids;


    /** Constructor.
     */
    public AbstractSimilarityLayout() {
        grids = new java.util.ArrayList<>();

        setWidthFull();

        HorizontalLayout headLayout = new HorizontalLayout();

        Button button = new Button(getTranslation("views.merge.button"));
        SingleSlider slider = new SingleSlider();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        button.addClickListener(event -> {
            progressBar.setVisible(true);
            new Thread(() -> {
                try {

                    getUI().ifPresent(ui -> ui.access(() -> {
                        createGrids(slider.getCurrentValue());

                        progressBar.setVisible(false);

                        Notification.show(getTranslation("views.merge.notification.grid"));
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        });

        headLayout.add(button);
        headLayout.add(slider);
        add(headLayout);
        add(progressBar);
    }


    /** Create the grids. Calls the service to get the duplicates and creates a grid for each group.
     *
     * @param threshold the threshold
     */
    public void createGrids(double threshold) {

        grids.forEach(this::remove);
        try {
            List<Set<T>> similarityGroups = getDuplicates(threshold);
            Iterator<Set<T>> iterator = similarityGroups.iterator();
            while(iterator.hasNext()) {
                Set<T> group = iterator.next();
                Grid<T> grid = new Grid<>();
                grid.setItems(group);
                setGridHeaders(grid);
                grid.addColumn(createButton(grid, grids)).setHeader(getTranslation("views.merge"));
                grid.setAllRowsVisible(true);
                grids.add(grid);
                add(grid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
