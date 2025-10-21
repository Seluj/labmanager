package fr.utbm.ciad.labmanager.views.components.conferences.editors.regular;

import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tool for computing the status or the similarity of a conference compared to the content of the database.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class ConferenceCreationStatusComputer implements EntityCreationStatusComputer<Conference> {

    private static final long serialVersionUID = 7553221191716845910L;

    private final ConferenceService conferenceService;

    /**
     * Constructor.
     *
     * @param conferenceService the service for accessing the JPA entities of the conferences.
     */
    public ConferenceCreationStatusComputer(@Autowired ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @Override
    public EntityCreationStatus computeEntityCreationStatusFor(Conference entity) {
        if (entity != null) {
            final var conference = this.conferenceService.getConferenceBySimilarNameAndAcronym(entity.getName(), entity.getAcronym());
            if (conference.isEmpty()) {
                return EntityCreationStatus.NO_ERROR;
            }
            final var id = conference.get().getId();
            if (id == entity.getId()) {
                return EntityCreationStatus.NO_ERROR;
            }
            return EntityCreationStatus.SAME_NAME_AND_ACRONYM;
        }
        return EntityCreationStatus.NO_ERROR;
    }

}
