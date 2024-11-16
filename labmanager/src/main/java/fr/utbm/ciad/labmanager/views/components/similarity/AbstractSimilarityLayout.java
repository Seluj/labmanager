package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

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
        //TODO : Put translation
        Button button = new Button("Lancer la vérification");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        button.addClickListener(event -> {
            progressBar.setVisible(true);
            new Thread(() -> {
                try {

                    getUI().ifPresent(ui -> ui.access(() -> {
                        createGrids();

                        progressBar.setVisible(false);

                        Notification.show(getTranslation("views.merge.notification.grid"));
                    }));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        });

        add(button);
        add(progressBar);
    }


    /** Create the grids. Calls the service to get the duplicates and creates a grid for each group.
     */
    public void createGrids() {

        try {
            List<Set<T>> similarityGroups = getDuplicates();
            Iterator<Set<T>> iterator = similarityGroups.iterator();
            while(iterator.hasNext()) {
                Set<T> group = iterator.next();
                Grid<T> grid = new Grid<>();
                grid.setItems(group);
                setGridHeaders(grid);
                grid.addColumn(createButton(grid, grids)).setHeader("Ultra Merge");
                grid.setAllRowsVisible(true);
                grids.add(grid);
                add(grid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static class ConferenceSimilarityLayout {
    }
}
