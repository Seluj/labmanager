package fr.utbm.ciad.labmanager.views.components.addons.entities;

import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

import java.util.List;

/**
 * Abstract implementation of a tool for computing the status or the similarity of an entity compared to the content of the database.
 *
 * @param <T> the type of the entities associated to this computer.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractEntityCreationStatusComputer<T> implements EntityCreationStatusComputer<T> {

    private static final long serialVersionUID = -7879999552632852271L;

    private static final double SIMILARITY_LEVEL = 0.75;
    private final PersonNameComparator personNameComparator;
    private double stringSimilarityLevel = SIMILARITY_LEVEL;

    /**
     * Constructor.
     *
     * @param personNameComparator the comparator of the person names.
     */
    public AbstractEntityCreationStatusComputer(PersonNameComparator personNameComparator) {
        this.personNameComparator = personNameComparator;
    }

    /**
     * Replies the similarity level to be used for comparing strings.
     *
     * @return the similarity level.
     */
    public double getStringSimilarityLevel() {
        return this.stringSimilarityLevel;
    }

    /**
     * Change the similarity level to be used for comparing strings.
     *
     * @param level the similarity level.
     */
    public void setStringSimilarityLevel(double level) {
        this.stringSimilarityLevel = level;
    }

    /**
     * Replies the internal similarity computer.
     *
     * @return the internal similarity computer.
     */
    protected NormalizedStringSimilarity getStringSimilarityComputer() {
        return this.personNameComparator.getStringSimilarityComputer();
    }

    /**
     * Replies if the two given strings are similar.
     *
     * @param string1 the first string to compare.
     * @param string2 the second string to compare.
     * @return {@code true} if the two strings are similar.
     */
    protected boolean isSimilar(String string1, String string2) {
        if (string1 == string2) {
            return true;
        }
        if (string1 == null || string2 == null) {
            return false;
        }
        final var similarity = getStringSimilarityComputer().similarity(string1, string2);
        return similarity >= getStringSimilarityLevel();
    }

    /**
     * Replies if the two given persons are similar.
     *
     * @param person1 the first person to compare.
     * @param person2 the second person to compare.
     * @return {@code true} if the two persons are similar.
     */
    protected boolean isSimilar(Person person1, Person person2) {
        if (person1 == person2) {
            return true;
        }
        if (person1 == null || person2 == null) {
            return false;
        }
        return this.personNameComparator.isSimilar(person1.getFirstName(), person1.getLastName(), person2.getFirstName(), person2.getLastName());
    }

    /**
     * Replies if the two given lists of persons are similar.
     *
     * @param personList1 the first list of persons to compare.
     * @param personList2 the second list of persons to compare.
     * @return {@code true} if the two lists of persons are similar.
     */
    protected boolean isSimilarPersonList(List<? extends Person> personList1, List<? extends Person> personList2) {
        if (personList1 == personList2) {
            return true;
        }
        if (personList1 != null && personList2 != null && personList1.size() == personList2.size()) {
            final var sz = personList1.size();
            for (int i = 0; i < sz; ++i) {
                try {
                    final var person1 = personList1.get(i);
                    final var person2 = personList2.get(i);
                    if (!isSimilar(person1, person2)) {
                        return false;
                    }
                } catch (Throwable ex) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
