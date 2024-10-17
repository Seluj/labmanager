package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PersonSimilarityLayout extends AbstractSimilarityLayout<Person> {

    private final PersonService personService;

    private final PersonNameComparator personNameComparator;

    public PersonSimilarityLayout(PersonService personService, PersonNameComparator personNameComparator) {
        super();
        this.personService = personService;
        this.personNameComparator = personNameComparator;
    }

    public void createGrids() {

        try {
            List<Set<Person>> similarityGroups = personService.getPersonDuplicates(null, null, personNameComparator);
            Iterator<Set<Person>> iterator = similarityGroups.iterator();
            while(iterator.hasNext()) {
                Set<Person> group = iterator.next();
                int size = group.size()*68;
                Grid<Person> grid = new Grid<>();
                grid.setItems(group);
                grid.addColumn(Person::getFirstName).setHeader("First Name");
                grid.addColumn(Person::getLastName).setHeader("Last Name");
                //grid.addColumn(Person::getMemberships).setHeader("Organization");
                grid.addColumn(Person::getAuthorshipNumber).setHeader("Publications");
                grid.addColumn(new NativeButtonRenderer<>(item -> ">")).setHeader("Little Merge");
                grid.addColumn(new NativeButtonRenderer<>(item -> ">>")).setHeader("Ultra Merge");
                grid.setHeight(size + "px");
                grids.add(grid);
                add(grid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
