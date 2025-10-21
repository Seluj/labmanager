package fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.factory.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.utbm.ciad.labmanager.utils.localStorage.factory.LocalStorageItemFactory;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardChartItem;

/**
 * Factory class for creating DashBoardChartItem instances from a string representation (e.g., JSON).
 * Provides the logic for deserializing a JSON string into a DashBoardChartItem object.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardChartItemFactory implements LocalStorageItemFactory<DashboardChartItem> {

    @Override
    public DashboardChartItem createItem(String jsonValue) {
        if (jsonValue == null) {
            return new DashboardChartItem();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        DashboardChartItem item;
        try {
            item = new DashboardChartItem(objectMapper.readValue(jsonValue, DashboardChartItem.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new DashboardChartItem(item);
    }
}
