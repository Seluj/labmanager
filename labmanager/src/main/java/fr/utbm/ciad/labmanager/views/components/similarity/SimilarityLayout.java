package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.grid.Grid;
import fr.utbm.ciad.labmanager.views.components.similarity.buttons.AbstractSimilarityNativeButtonRenderer;

import java.util.List;
import java.util.Set;

/** Represent an Interface for a specific layout for the similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface SimilarityLayout<T> {

    /** Create the grids.
     */
    void createGrids();

    /** Get the entity duplicates.
     */
    List<Set<T>> getDuplicates();

    /** Set the grid headers.
     */
    void setGridHeaders(Grid<T> grid);

    /** Create new button
     */
    AbstractSimilarityNativeButtonRenderer<T> createButton(Grid<T> grid, List<Grid<T>> grids);
}
