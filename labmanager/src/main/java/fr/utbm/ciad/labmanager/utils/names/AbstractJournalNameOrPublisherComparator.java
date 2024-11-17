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
		final var normedName2 = normalizeString(name2);
		final var normedPublisher2 = normalizeString(publisher2);

		final var similarityComputer = getStringSimilarityComputer();
        final var s1 = similarityComputer.similarity(normedName1, normedName2);
        final var s2 = similarityComputer.similarity(normedPublisher1, normedPublisher2);

        return s1;
    }

}
