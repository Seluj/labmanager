package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.services.AbstractService;

import java.util.List;

public abstract class AbstractSimilarityLayout<T> extends VerticalLayout implements SimilarityLayout {

    protected List<Grid<T>> grids;

    public AbstractSimilarityLayout() {

        setWidthFull();
        add(new Button("Check", event -> createGrids()));
        grids = new java.util.ArrayList<>();
    }

}
