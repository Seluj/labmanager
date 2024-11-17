package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.organization.OrganizationMergingService;
import fr.utbm.ciad.labmanager.views.components.similarity.AbstractSimilarityLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizationSimilarityNativeButtonRenderer extends AbstractSimilarityNativeButtonRenderer<ResearchOrganization> {

    private final OrganizationMergingService organizationMergingService;

    public OrganizationSimilarityNativeButtonRenderer(@Autowired OrganizationMergingService organizationMergingService,
                                                      String label, Grid<ResearchOrganization> grid,
                                                      List<Grid<ResearchOrganization>> grids, AbstractSimilarityLayout<ResearchOrganization> layout) {
        super(label, grid, grids, layout);
        this.organizationMergingService = organizationMergingService;
    }

    /**
     * Set the header.
     *
     * @param entity the entity
     * @return the header
     */
    @Override
    public H2 setHeader(ResearchOrganization entity) {
        H2 h = new H2(" ---> " + entity.getName());
        h.getStyle().set("font-size", "1.5em").set("font-weight", "bold");
        return h;
    }

    /**
     * Build the checkbox map.
     *
     * @param entityInGrid the entity in the grid.
     * @return the vertical layout
     */
    @Override
    public VerticalLayout buildCheckboxMap(List<ResearchOrganization> entityInGrid) {
        VerticalLayout checkboxLayout = new VerticalLayout();
        Map<ResearchOrganization, Checkbox> checkboxMap = new HashMap<>();

        for (ResearchOrganization o : entityInGrid) {
            Checkbox checkbox = new Checkbox(o.getName());
            checkboxLayout.add(checkbox);
            checkboxMap.put(o, checkbox);
        }
        setCheckboxMap(checkboxMap);
        return checkboxLayout;
    }

    /**
     * Launch the merge.
     *
     * @param selectedEntities the selected entities
     * @param entity           the entity
     */
    @Override
    public void launchMerge(List<ResearchOrganization> selectedEntities, ResearchOrganization entity) {
        try {
            organizationMergingService.mergeOrganizations(selectedEntities, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
