package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.PersonSimilarityNativeButtonRenderer;

import java.util.*;

/** Represent a layout for the Person similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PersonSimilarityLayout extends AbstractSimilarityLayout<Person> {

    private final PersonMergingService personMergingService;

    private final PersonNameComparator personNameComparator;

    /** Constructor.
     * @param personMergingService the person merging service
     * @param personNameComparator the person name comparator
     */
    public PersonSimilarityLayout(PersonMergingService personMergingService, PersonNameComparator personNameComparator) {
        super();
        this.personMergingService = personMergingService;
        this.personNameComparator = personNameComparator;

    }

    @Override
    public List<Set<Person>> getDuplicates(double threshold) {
        List<Set<Person>> persons = new ArrayList<>();
        try {
            persons = personMergingService.getPersonDuplicates(null, null, threshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persons;
    }

    @Override
    public void setGridHeaders(Grid<Person> grid) {
        grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getLastName).setHeader("Last Name");
        grid.addColumn(Person::getOrganizations).setHeader("Organization");
        grid.addColumn(Person::getAuthorshipNumber).setHeader("Publications");
    }

    @Override
    public PersonSimilarityNativeButtonRenderer createButton(Grid<Person> grid, List<Grid<Person>> grids) {
        return new PersonSimilarityNativeButtonRenderer(personMergingService, ">>", grid, grids, this);
    }


}
