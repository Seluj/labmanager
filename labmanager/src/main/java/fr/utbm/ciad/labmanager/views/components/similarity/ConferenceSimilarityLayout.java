package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.conference.ConferenceMergingService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationMergingService;
import fr.utbm.ciad.labmanager.utils.names.ConferenceNameComparator;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.AbstractSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.ConferenceSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.OrganizationSimilarityNativeButtonRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Represent a layout for the Conference similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ConferenceSimilarityLayout extends AbstractSimilarityLayout<Conference> {

    private ConferenceMergingService conferenceMergingService;

    private ConferenceNameComparator nameComparator;

    /**
     * Constructor.
     *
     * @param conferenceMergingService the person merging service
     * @param nameComparator             the person name comparator
     */
    public ConferenceSimilarityLayout(ConferenceMergingService conferenceMergingService, ConferenceNameComparator nameComparator) {
        super();
        this.conferenceMergingService = conferenceMergingService;
        this.nameComparator = nameComparator;

    }

    /** Get the entity duplicates.
     */
    @Override
    public List<Set<Conference>> getDuplicates() {
        List<Set<Conference>> conferences = new ArrayList<>();
        try {
            conferences = conferenceMergingService.getConferenceDuplicates(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conferences;
    }

    /** Set the grid headers.
     */
    @Override
    public void setGridHeaders(Grid<Conference> grid) {
        grid.addColumn(Conference::getName).setHeader("Name");
        grid.addColumn(Conference::getAcronym).setHeader("Acronym");
    }

    /** Create new button
     */
    @Override
    public AbstractSimilarityNativeButtonRenderer<Conference> createButton(Grid<Conference> grid, List<Grid<Conference>> grids) {
        return new ConferenceSimilarityNativeButtonRenderer(conferenceMergingService, ">>", grid, grids, this);
    }
}
