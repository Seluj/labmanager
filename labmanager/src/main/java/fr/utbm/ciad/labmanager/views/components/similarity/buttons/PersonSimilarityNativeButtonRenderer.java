package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.components.similarity.AbstractSimilarityLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonSimilarityNativeButtonRenderer extends AbstractSimilarityNativeButtonRenderer<Person> {

    private final PersonMergingService personMergingService;

    public PersonSimilarityNativeButtonRenderer(@Autowired PersonMergingService personMergingService, String label,
                                                Grid<Person> grid, List<Grid<Person>> grids,
                                                AbstractSimilarityLayout<Person> layout) {
        super(label, grid, grids, layout);
        this.personMergingService = personMergingService;
    }

    @Override
    public VerticalLayout buildCheckboxMap(List<Person> entityInGrid) {
        VerticalLayout checkboxLayout = new VerticalLayout();
        Map<Person, Checkbox> checkboxMap = new HashMap<>();

        // Ajouter une checkbox pour chaque personne dans la liste
        for (Person p : entityInGrid) {
            Checkbox checkbox = new Checkbox(p.getFullName());
            checkboxLayout.add(checkbox);
            checkboxMap.put(p, checkbox);
        }
        setCheckboxMap(checkboxMap);
        return checkboxLayout;
    }

    @Override
    public void launchMerge(List<Person> selectedEntities, Person entity) {
        try {
            personMergingService.mergePersons(selectedEntities, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public H2 setHeader(Person entity) {
        H2 h = new H2(" ---> " + entity.getFullName());
        h.getStyle().set("font-size", "1.5em").set("font-weight", "bold");
        return h;
    }
}
