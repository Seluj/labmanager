package fr.utbm.ciad.labmanager.views.components.persons.editors.regular;

import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tool for computing the status or the similarity of a person compared to the content of the database.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class PersonCreationStatusComputer implements EntityCreationStatusComputer<Person> {

    private static final long serialVersionUID = 6668122514017399002L;

    private final PersonService personService;

    /**
     * Constructor.
     *
     * @param personService the service for accessing the JPA entities of the persons.
     */
    public PersonCreationStatusComputer(@Autowired PersonService personService) {
        this.personService = personService;
    }

    @Override
    public EntityCreationStatus computeEntityCreationStatusFor(Person entity) {
        if (entity != null) {
            final var id = this.personService.getPersonIdBySimilarName(entity.getLastName(), entity.getFirstName());
            if (id == 0) {
                return EntityCreationStatus.NO_ERROR;
            }
            if (id == entity.getId()) {
                return EntityCreationStatus.NO_ERROR;
            }
            return EntityCreationStatus.SAME_NAME;
        }
        return EntityCreationStatus.NO_ERROR;
    }

}
