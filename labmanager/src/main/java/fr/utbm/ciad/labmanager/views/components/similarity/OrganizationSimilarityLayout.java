package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.organization.OrganizationMergingService;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.AbstractSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.OrganizationSimilarityNativeButtonRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Represent a layout for the Organization similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OrganizationSimilarityLayout extends AbstractSimilarityLayout<ResearchOrganization> {

    private OrganizationMergingService organizationMergingService;

    private OrganizationNameComparator nameComparator;

    /**
     * Constructor.
     *
     * @param organizationMergingService the person merging service
     * @param nameComparator             the person name comparator
     */
    public OrganizationSimilarityLayout(OrganizationMergingService organizationMergingService, OrganizationNameComparator nameComparator) {
        super();
        this.organizationMergingService = organizationMergingService;
        this.nameComparator = nameComparator;

    }

    /** Get the entity duplicates.
     */
    @Override
    public List<Set<ResearchOrganization>> getDuplicates(double threshold) {
        List<Set<ResearchOrganization>> researchOrganizations = new ArrayList<>();
        try {
            researchOrganizations = organizationMergingService.getOrganizationDuplicates(null, null, threshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return researchOrganizations;
    }

    /** Set the grid headers.
     */
    @Override
    public void setGridHeaders(Grid<ResearchOrganization> grid) {
        grid.addColumn(ResearchOrganization::getName).setHeader("Name");
        grid.addColumn(ResearchOrganization::getAcronym).setHeader("Acronym");
        grid.addColumn(ResearchOrganization::getCountry).setHeader("Country");
    }

    /** Create new button
     */
    @Override
    public AbstractSimilarityNativeButtonRenderer<ResearchOrganization> createButton(Grid<ResearchOrganization> grid, List<Grid<ResearchOrganization>> grids) {
        return new OrganizationSimilarityNativeButtonRenderer(organizationMergingService, ">>", grid, grids, this);
    }
}
