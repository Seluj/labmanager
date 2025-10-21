package fr.utbm.ciad.labmanager.views.components.publications.editors.regular;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tool for computing the status or the similarity of a publication compared to the content of the database.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class PublicationCreationStatusComputer extends AbstractEntityCreationStatusComputer<Publication> {

    private static final long serialVersionUID = 3681838919251457647L;

    private final PublicationService publicationService;

    /**
     * Constructor.
     *
     * @param publicationService   the service for accessing the JPA entities of the publications.
     * @param personNameComparator the comparator of the person names.
     */
    public PublicationCreationStatusComputer(
            @Autowired PublicationService publicationService,
            @Autowired PersonNameComparator personNameComparator) {
        super(personNameComparator);
        this.publicationService = publicationService;
    }

    @Override
    public EntityCreationStatus computeEntityCreationStatusFor(Publication newEntity) {
        if (newEntity != null) {
            final var publications = this.publicationService.getPublicationsBySimilarTitle(newEntity.getTitle(), it -> {
                Hibernate.initialize(it.getAuthorshipsRaw());
                // Force the loading of all the data that is required for obtaining the place where the publication was published
                it.getPublicationTarget();
            });
            if (!publications.isEmpty()) {
                final var newPublicationTarget = newEntity.getPublicationTarget();
                final var newPublicationAuthors = newEntity.getAuthors();
                for (final var existingPublication : publications) {
                    final var existingPublicationTarget = existingPublication.getPublicationTarget();
                    if (isSimilar(newPublicationTarget, existingPublicationTarget)) {
                        // Same publication target
                        if (!isSimilarPersonList(newPublicationAuthors, existingPublication.getAuthors())) {
                            return EntityCreationStatus.SAME_TITLE_AND_PUBLISHING_VECTOR_BUT_DIFFERENT_AUTHORS;
                        }
                        return EntityCreationStatus.SAME_TITLE_AND_PUBLISHING_VECTOR;
                    }
                }
                return EntityCreationStatus.SAME_TITLE_AND_DIFFERENT_PUBLISHING_VECTORS;
            }
            if (Strings.isNullOrEmpty(newEntity.getAbstractText())) {
                return EntityCreationStatus.MISSED_ABSTRACT_TEXT;
            }
        }
        return EntityCreationStatus.NO_ERROR;
    }

}
