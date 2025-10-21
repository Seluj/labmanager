package fr.utbm.ciad.labmanager.views.components.organizations.editors.regular;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Tool for computing the status or the similarity of a research organization compared to the content of the database.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class OrganizationCreationStatusComputer implements EntityCreationStatusComputer<ResearchOrganization> {

    private static final long serialVersionUID = -9218123034184012732L;

    private final ResearchOrganizationService organizationService;

    /**
     * Constructor.
     *
     * @param organizationService the service for accessing the JPA entities of the research organizations.
     */
    public OrganizationCreationStatusComputer(@Autowired ResearchOrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    public EntityCreationStatus computeEntityCreationStatusFor(ResearchOrganization entity) {
        if (entity != null) {
            final var orga = this.organizationService.getResearchOrganizationBySimilarAcronymOrName(entity.getAcronym(), entity.getName());
            if (orga.isEmpty()) {
                return EntityCreationStatus.NO_ERROR;
            }
            final var id = orga.get().getId();
            if (id == entity.getId()) {
                return EntityCreationStatus.NO_ERROR;
            }
            return EntityCreationStatus.SAME_NAME_AND_ACRONYM;
        }
        return EntityCreationStatus.NO_ERROR;
    }

}
