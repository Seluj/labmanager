package fr.utbm.ciad.labmanager.utils.names.jaccar;

import fr.utbm.ciad.labmanager.utils.names.AbstractJournalNameOrPublisherComparator;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing journal names and publishers using the Jaccar algorithm.
 * Similarity used by this comparator is the maximum similarity level for journal names and publishers names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
public class JaccarJournalNameOrPublisherComparator extends AbstractJournalNameOrPublisherComparator {

    private static final double DEFAULT_SIMILARITY_LEVEL = 0.4;

    /**
     * Constructor.
     */
    public JaccarJournalNameOrPublisherComparator() {
        setSimilarityLevel(DEFAULT_SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }
}
