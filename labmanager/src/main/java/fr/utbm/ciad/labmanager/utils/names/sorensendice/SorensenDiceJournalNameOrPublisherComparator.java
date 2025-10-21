package fr.utbm.ciad.labmanager.utils.names.sorensendice;

import fr.utbm.ciad.labmanager.utils.names.AbstractJournalNameOrPublisherComparator;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing journal names and publishers using the Sorensen Dice algorithm.
 * Similarity used by this comparator is the maximum similarity level for journal names and publishers names.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
@Primary
public class SorensenDiceJournalNameOrPublisherComparator extends AbstractJournalNameOrPublisherComparator {

    private static final double DEFAULT_SIMILARITY_LEVEL = 0.6;

    /**
     * Constructor.
     */
    public SorensenDiceJournalNameOrPublisherComparator() {
        setSimilarityLevel(DEFAULT_SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new SorensenDice();
    }
}
