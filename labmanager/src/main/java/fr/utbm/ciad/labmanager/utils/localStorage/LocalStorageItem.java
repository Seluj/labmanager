package fr.utbm.ciad.labmanager.utils.localStorage;

/**
 * Interface representing an item that can be stored locally with a unique identifier.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface LocalStorageItem {

    /**
     * Create and set the unique identifier for the item.
     *
     * @param idPart the unique identifier part used to create the id
     */
    void createId(String idPart);

    /**
     * Retrieves the unique identifier of the item.
     *
     * @return the unique identifier of the item
     */
    String getId();

    /**
     * Set the unique identifier for the item.
     *
     * @param id the unique identifier to set
     */
    void setId(String id);
}
