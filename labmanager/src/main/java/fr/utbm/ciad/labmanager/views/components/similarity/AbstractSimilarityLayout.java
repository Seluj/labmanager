package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.AbstractService;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.AbstractSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.PersonSimilarityNativeButtonRenderer;

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
                int size = group.size()*68;
                Grid<T> grid = new Grid<>();
                grid.setItems(group);
                setGridHeaders(grid);
                grid.addColumn(createButton(grid, grids)).setHeader("Ultra Merge");
                grid.setHeight(size + "px");
                grids.add(grid);
                add(grid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
