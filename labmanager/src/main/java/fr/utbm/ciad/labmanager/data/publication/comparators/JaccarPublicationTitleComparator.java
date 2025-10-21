package fr.utbm.ciad.labmanager.data.publication.comparators;

import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing publication names using the Jaccar algorithm.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class JaccarPublicationTitleComparator extends AbstractPublicationTitleComparator {

    private static final double SIMILARITY_LEVEL = 0.65;

    /**
     * Constructor with default similarity level
     */
    public JaccarPublicationTitleComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }

}
