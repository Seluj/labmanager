package fr.utbm.ciad.labmanager.utils.localStorage;

import java.util.function.Consumer;

/**
 * Interface for managing items that can be store locally.
 *
 * @param <T> The LocalStorageItem containing the data to store locally
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface LocalStorageManager<T extends LocalStorageItem> {

    /**
     * Adds a new item to the local storage.
     *
     * @param item the item to be added to the local storage
     */
    void add(T item);

    /**
     * Retrieves an item from local storage based on its ID and executes the provided callback once the item is retrieved.
     *
     * @param id         the unique identifier of the item to retrieve
     * @param onComplete a callback that will be executed when the item is retrieved
     */
    void getItem(String id, Consumer<T> onComplete);

    /**
     * Removes an item from the local storage.
     *
     * @param item the item to be removed from the local storage
     */
    void remove(T item);

    /**
     * Removes an item from the local storage using its unique ID.
     *
     * @param id the unique identifier of the item to be removed
     */
    void remove(String id);

}
