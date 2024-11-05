package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.journal.JournalMergingService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationMergingService;
import fr.utbm.ciad.labmanager.utils.names.JournalNameOrPublisherComparator;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.AbstractSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.JournalSimilarityNativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.OrganizationSimilarityNativeButtonRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JournalSimilarityLayout extends AbstractSimilarityLayout<Journal> {

    private JournalMergingService  journalMergingService;

    private JournalNameOrPublisherComparator nameComparator;

    public JournalSimilarityLayout(JournalMergingService journalMergingService, JournalNameOrPublisherComparator nameComparator) {
        super();
        this.journalMergingService = journalMergingService;
        this.nameComparator = nameComparator;
    }

    @Override
    public List<Set<Journal>> getDuplicates() {
        List<Set<Journal>> journals = new ArrayList<>();
        try {
            journals = journalMergingService.getJournalDuplicates(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return journals;
    }

    @Override
    public void setGridHeaders(Grid<Journal> grid) {
        grid.addColumn(Journal::getJournalName).setHeader("Name");
        grid.addColumn(Journal::getPublisher).setHeader("Publisher");
    }

    @Override
    public AbstractSimilarityNativeButtonRenderer<Journal> createButton(Grid<Journal> grid, List<Grid<Journal>> grids) {
        return new JournalSimilarityNativeButtonRenderer(journalMergingService, ">>", grid, grids, this);
    }
}
