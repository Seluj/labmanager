package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/** Represent an Interface for a specific native button renderer for similarity.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface SimilarityNativeButtonRenderer<T> {

    /**
     * Set the header of the button.
     *
     * @param T the type of the entity.
     * @return the header
     */
    H2 setHeader(T entity);

    /**
     * Build the checkbox map.
     *
     * @param entityInGrid the entity in the grid.
     * @return the vertical layout
     */
    VerticalLayout buildCheckboxMap(List<T> entityInGrid);

    /**
     * Launch the merge.
     *
     * @param selectedEntities the selected entities.
     * @param entity           the entity.
     */
    void launchMerge(List<T> selectedEntities, T entity);
}
