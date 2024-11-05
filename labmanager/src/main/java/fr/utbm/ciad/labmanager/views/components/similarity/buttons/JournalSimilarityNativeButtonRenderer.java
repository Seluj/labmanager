package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.journal.JournalMergingService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.views.components.similarity.AbstractSimilarityLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JournalSimilarityNativeButtonRenderer extends AbstractSimilarityNativeButtonRenderer<Journal> {


    private final JournalMergingService journalMergingService;


    /**
     * Constructor.
     *
     * @param label  the label
     * @param grid   the grid
     * @param grids  the grids
     * @param layout the layout
     */
    public JournalSimilarityNativeButtonRenderer(JournalMergingService journalMergingService, String label, Grid<Journal> grid, List<Grid<Journal>> grids, AbstractSimilarityLayout<Journal> layout) {
        super(label, grid, grids, layout);
        this.journalMergingService = journalMergingService;
    }


    @Override
    public H2 setHeader(Journal entity) {
        H2 h = new H2(" ---> " + entity.getJournalName());
        h.getStyle().set("font-size", "1.5em").set("font-weight", "bold");
        return h;
    }

    @Override
    public VerticalLayout buildCheckboxMap(List<Journal> entityInGrid) {
        VerticalLayout checkboxLayout = new VerticalLayout();
        Map<Journal, Checkbox> checkboxMap = new HashMap<>();

        // Ajouter une checkbox pour chaque personne dans la liste
        for (Journal j : entityInGrid) {
            Checkbox checkbox = new Checkbox(j.getJournalName());
            checkboxLayout.add(checkbox);
            checkboxMap.put(j, checkbox);
        }
        setCheckboxMap(checkboxMap);
        return checkboxLayout;
    }

    @Override
    public void launchMerge(List<Journal> selectedEntities, Journal entity) {
        try {
            journalMergingService.mergeJournals(selectedEntities, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
