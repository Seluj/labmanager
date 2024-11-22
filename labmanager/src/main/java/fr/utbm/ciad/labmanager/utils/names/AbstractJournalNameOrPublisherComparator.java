package fr.utbm.ciad.labmanager.utils.names;

import fr.utbm.ciad.labmanager.utils.AbstractNormalizableStringComparator;

/**
 * Abstract implementation of utilities for comparing journal names and publishers.
 * Similarity used by this comparator is the maximum similarity level for journal names and publishers names.
 *
 * @author $Author: jferlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractJournalNameOrPublisherComparator extends AbstractNormalizableStringComparator implements JournalNameOrPublisherComparator {

    @Override
    public double getSimilarity(String name1, String publisher1, String name2, String publisher2) {
        final var normedName1 = normalizeString(name1);
		final var normedPublisher1 = normalizeString(publisher1);
        final var normedName2 = name1 != name2 ? normalizeString(name2) : normedName1;
        final var normedPublisher2 = publisher1 != publisher2 ? normalizeString(publisher2) : normedPublisher1;

		final var similarityComputer = getStringSimilarityComputer();
        final var s2 = similarityComputer.similarity(normedPublisher1, normedPublisher2);

        if((normedPublisher1.isEmpty() || normedPublisher2.isEmpty()) || s2 > 0.8){
            return similarityComputer.similarity(normedName1, normedName2);
        }
        return 0;
    }

}
