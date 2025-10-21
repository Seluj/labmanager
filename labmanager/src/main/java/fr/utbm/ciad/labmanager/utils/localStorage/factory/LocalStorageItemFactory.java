package fr.utbm.ciad.labmanager.utils.localStorage.factory;

import fr.utbm.ciad.labmanager.utils.localStorage.LocalStorageItem;

/**
 * Factory interface for creating LocalStorageItem instances from a string representation (e.g., JSON).
 *
 * @param <T> The type of the item to be created.
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface LocalStorageItemFactory<T extends LocalStorageItem> {

    /**
     * Creates an item of type T from a string representation (e.g., JSON).
     *
     * @param item the string representation of the item (e.g., JSON) to be converted into an instance of T
     * @return the created item of type T
     */
    T createItem(String item);
}
