package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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


}
