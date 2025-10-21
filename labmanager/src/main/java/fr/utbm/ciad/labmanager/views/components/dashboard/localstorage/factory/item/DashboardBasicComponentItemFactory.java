package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.utbm.ciad.labmanager.utils.localStorage.factory.LocalStorageItemFactory;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardBasicComponentItem;

/**
 * Factory class for creating DashBoardComponentItem instances from a string representation (e.g., JSON).
 * Provides the logic for deserializing a JSON string into a DashBoardComponentItem object.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardBasicComponentItemFactory implements LocalStorageItemFactory<DashboardBasicComponentItem> {

    @Override
    public DashboardBasicComponentItem createItem(String jsonValue) {
        if (jsonValue == null) {
            return new DashboardBasicComponentItem();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        DashboardBasicComponentItem item;
        try {
            item = new DashboardBasicComponentItem(objectMapper.readValue(jsonValue, DashboardBasicComponentItem.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new DashboardBasicComponentItem(item);
    }
}
