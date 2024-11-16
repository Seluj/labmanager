package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.conference.ConferenceMergingService;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.views.components.similarity.AbstractSimilarityLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represent a Person native button renderer for similarity.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConferenceSimilarityNativeButtonRenderer extends AbstractSimilarityNativeButtonRenderer<Conference> {

    private final ConferenceMergingService conferenceMergingService;

    /** Constructor.
     * @param conferenceMergingService the person merging service
     * @param label the label
     * @param grid the grid
     * @param grids the grids
     * @param layout the layout
     */
    public ConferenceSimilarityNativeButtonRenderer(@Autowired ConferenceMergingService conferenceMergingService, String label,
                                                Grid<Conference> grid, List<Grid<Conference>> grids,
                                                AbstractSimilarityLayout<Conference> layout) {
        super(label, grid, grids, layout);
        this.conferenceMergingService = conferenceMergingService;
    }

    /**
     * Build the checkbox map.
     *
     * @param entityInGrid the entity in the grid.
     * @return the vertical layout
     */
    @Override
    public VerticalLayout buildCheckboxMap(List<Conference> entityInGrid) {
        VerticalLayout checkboxLayout = new VerticalLayout();
        Map<Conference, Checkbox> checkboxMap = new HashMap<>();

        // Ajouter une checkbox pour chaque personne dans la liste
        for (Conference c : entityInGrid) {
                Checkbox checkbox = new Checkbox(c.getName());
            checkboxLayout.add(checkbox);
            checkboxMap.put(c, checkbox);
        }
        setCheckboxMap(checkboxMap);
        return checkboxLayout;
    }

    /**
     * Set the header of the button.
     *
     * @param entity the type of the entity.
     * @return the header
     */
    @Override
    public H2 setHeader(Conference entity) {
        H2 h = new H2(" ---> " + entity.getName());
        h.getStyle().set("font-size", "1.5em").set("font-weight", "bold");
        return h;
    }

    /**
     * Launch the merge.
     *
     * @param selectedEntities the selected entities.
     * @param entity           the entity.
     */
    @Override
    public void launchMerge(List<Conference> selectedEntities, Conference entity) {
        try {
            conferenceMergingService.mergeConferences(selectedEntities, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
