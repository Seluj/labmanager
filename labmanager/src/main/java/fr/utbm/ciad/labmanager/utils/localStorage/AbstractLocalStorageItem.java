package fr.utbm.ciad.labmanager.utils.localStorage;

/**
 * Abstract class providing basic functionality for items that can be stored locally with a unique identifier.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLocalStorageItem implements LocalStorageItem {

    private String id;

    /**
     * Default Constructor
     */
    public AbstractLocalStorageItem() {
        this("");
    }

    /**
     * Constructor that initializes the item with a unique identifier.
     *
     * @param id the unique identifier to assign to the item
     */
    public AbstractLocalStorageItem(String id) {
        createId(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
