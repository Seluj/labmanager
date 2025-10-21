package fr.utbm.ciad.labmanager.utils.localStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.page.WebStorage;
import fr.utbm.ciad.labmanager.utils.localStorage.factory.LocalStorageItemFactory;

import java.util.function.Consumer;

/**
 * Abstract class for managing items that can be store locally.
 *
 * @param <T> The LocalStorageItem containing the data to store locally
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLocalStorageManager<T extends LocalStorageItem> implements LocalStorageManager<T> {

    LocalStorageItemFactory<T> factory;

    /**
     * Constructor
     *
     * @param factory the factory used to create items of type T
     */
    public AbstractLocalStorageManager(LocalStorageItemFactory<T> factory) {
        this.factory = factory;
    }

    @Override
    public void getItem(String id, Consumer<T> onComplete) {
        WebStorage.getItem(id, jsonValue -> {
            T newItem = null;
            if (jsonValue != null) {
                newItem = factory.createItem(jsonValue);
            }
            onComplete.accept(newItem);
        });
    }

    @Override
    public void add(T item) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(item);
            WebStorage.setItem(item.getId(), json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(T item) {
        remove(item.getId());
    }

    @Override
    public void remove(String id) {
        WebStorage.removeItem(id);
    }
}
