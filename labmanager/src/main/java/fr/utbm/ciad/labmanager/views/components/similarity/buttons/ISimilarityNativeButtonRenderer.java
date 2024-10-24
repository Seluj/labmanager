package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface ISimilarityNativeButtonRenderer<T> {

    /**
     * Set the header of the button.
     *
     * @param T the header of the button.
     */
    H2 setHeader(T entity);

    VerticalLayout buildCheckboxMap(List<T> entityInGrid);

    void launchMerge(List<T> selectedEntities, T entity);
}
