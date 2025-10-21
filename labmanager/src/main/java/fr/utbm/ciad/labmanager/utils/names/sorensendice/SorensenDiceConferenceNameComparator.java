package fr.utbm.ciad.labmanager.utils.names.sorensendice;

import fr.utbm.ciad.labmanager.utils.names.AbstractConferenceNameComparator;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing conference names using the Sorensen Dice algorithm.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
@Primary
public class SorensenDiceConferenceNameComparator extends AbstractConferenceNameComparator {

    private static final double SIMILARITY_LEVEL = 0.7;

    /**
     * Constructor with default similarity level.
     */
    public SorensenDiceConferenceNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new SorensenDice();
    }
}
