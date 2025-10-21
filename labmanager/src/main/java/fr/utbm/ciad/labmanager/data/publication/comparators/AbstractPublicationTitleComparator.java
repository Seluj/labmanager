package fr.utbm.ciad.labmanager.data.publication.comparators;

import fr.utbm.ciad.labmanager.utils.AbstractNormalizableStringComparator;

/**
 * Abstract implementation of utiferlin$
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPublicationTitleComparator extends AbstractNormalizableStringComparator implements PublicationTitleComparator {

    /**
     * Computes the similarity between two titles using the string similarity computer created by createStringSimilarityComputer.
     *
     * @param title1 The first title.
     * @param title2 The second title.
     * @return The similarity between the two titles.
     */
    @Override
    public double getSimilarity(String title1, String title2) {
        final var normedTitle1 = normalizeString(title1);
        final var normedTitle2 = title1 != title2 ? normalizeString(title2) : normedTitle1;

        final var similarityComputer = getStringSimilarityComputer();
        return getSimilarity(similarityComputer, normedTitle1, normedTitle2);
    }

}
