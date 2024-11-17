package fr.utbm.ciad.labmanager.utils.names;

import fr.utbm.ciad.labmanager.utils.AbstractNormalizableStringComparator;

/**
 * Abstract implementation of utilities for comparing conference names.
 *
 * @author $Author: jferlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$s
 * @since 4.0
 */
public abstract class AbstractConferenceNameComparator extends AbstractNormalizableStringComparator implements ConferenceNameComparator {

	@Override
	public double getSimilarity(String acronym1, String name1, String acronym2, String name2) {
		final var normedAcronym1 = normalizeString(acronym1);
		final var normedName1 = normalizeString(name1);
		final var normedAcronym2 = acronym1 != acronym2 ? normalizeString(acronym2) : normedAcronym1;
		final var normedName2 = name1 != name2 ? normalizeString(name2) : normedName1;

		final var similarityComputer = getStringSimilarityComputer();
        final var s1 = similarityComputer.similarity(normedAcronym1, normedAcronym2);
        final var s2 = similarityComputer.similarity(normedName1, normedName2);

        return Math.max(s1, s2);
    }

}
