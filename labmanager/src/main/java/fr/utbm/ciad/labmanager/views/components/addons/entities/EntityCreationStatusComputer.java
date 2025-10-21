package fr.utbm.ciad.labmanager.views.components.addons.entities;

import java.io.Serializable;

/**
 * Tool for computing the status or the similarity of an entity compared to the content of the database.
 *
 * @param <T> the type of the entities associated to this computer.
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@FunctionalInterface
public interface EntityCreationStatusComputer<T> extends Serializable {

    /**
     * Tool for building the specification of a person's card.
     *
     * @param <T> the type of the entities associated to this computer.
     * @return the computer that computes always {@link EntityCreationStatus#NO_ERROR}.
     */
    static <T> EntityCreationStatusComputer<T> getNoErrorEntityCreationStatusComputer() {
        return entity -> EntityCreationStatus.NO_ERROR;
    }

    /**
     * Compute the status of the entity before it is created or import in the database.
     *
     * @param entity the entity.
     * @return the status, never {@code null}.
     */
    EntityCreationStatus computeEntityCreationStatusFor(T entity);

}
