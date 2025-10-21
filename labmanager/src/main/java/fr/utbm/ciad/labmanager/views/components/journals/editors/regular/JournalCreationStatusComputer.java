package fr.utbm.ciad.labmanager.views.components.journals.editors.regular;

import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tool for computing the status or the similarity of a journal compared to the content of the database.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class JournalCreationStatusComputer implements EntityCreationStatusComputer<Journal> {

    private static final long serialVersionUID = 7688071268978259592L;

    private final JournalService journalService;

    /**
     * Constructor.
     *
     * @param journalService the service for accessing the JPA entities of the journals.
     */
    public JournalCreationStatusComputer(@Autowired JournalService journalService) {
        this.journalService = journalService;
    }

    @Override
    public EntityCreationStatus computeEntityCreationStatusFor(Journal entity) {
        if (entity != null) {
            final var journal = this.journalService.getJournalBySimilarNameAndSimilarPublisher(entity.getJournalName(), entity.getPublisher());
            if (journal.isEmpty()) {
                return EntityCreationStatus.NO_ERROR;
            }
            final var id = journal.get().getId();
            if (id == entity.getId()) {
                return EntityCreationStatus.NO_ERROR;
            }
            return EntityCreationStatus.SAME_TITLE_AND_PUBLISHER;
        }
        return EntityCreationStatus.NO_ERROR;
    }

}
