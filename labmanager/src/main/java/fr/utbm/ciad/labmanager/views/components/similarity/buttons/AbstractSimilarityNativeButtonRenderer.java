package fr.utbm.ciad.labmanager.views.components.similarity.buttons;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import fr.utbm.ciad.labmanager.views.components.similarity.AbstractSimilarityLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory.getTranslation;

/** Represent an abstract of a specific native button renderer for similarity.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractSimilarityNativeButtonRenderer<T> extends NativeButtonRenderer<T> implements SimilarityNativeButtonRenderer<T> {

    private Map<T, Checkbox> checkboxMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param label  the label
     * @param grid   the grid
     * @param grids  the grids
     * @param layout the layout
     */
    public AbstractSimilarityNativeButtonRenderer(String label, Grid<T> grid, List<Grid<T>> grids, AbstractSimilarityLayout<T> layout) {
        super(label);
        addItemClickListener(event -> {
            T entity = event;

            List<T> entityInGrid = new ArrayList<>();
            grid.getDataProvider().fetch(new Query<>()).filter(p -> !p.equals(entity)).forEach(entityInGrid::add);

            if (!entityInGrid.isEmpty()) {
                Dialog confirmationDialog = new Dialog();

                Paragraph confirmationText = new Paragraph(getTranslation("views.merge.list"));
                confirmationDialog.add(setHeader(entity), confirmationText);

                confirmationDialog.add(buildCheckboxMap(entityInGrid));

                // Bouton pour confirmer l'action
                Button confirmButton = new Button(getTranslation("views.confirm"), confirmEvent -> {
                    try {
                        // Créer une liste des personnes sélectionnées
                        List<T> selectedEntities= new ArrayList<>();
                        for (Map.Entry<T, Checkbox> entry : checkboxMap.entrySet()) {
                            if (entry.getValue().getValue()) { // Si la checkbox est cochée
                                selectedEntities.add(entry.getKey());
                            }
                        }

                        if (!selectedEntities.isEmpty()) {

                            launchMerge(selectedEntities, entity);
                            System.out.println("Merged " + selectedEntities + " with " + entity);
                            grids.remove(grid);
                            layout.remove(grid);
                            confirmationDialog.close();
                            layout.getUI().ifPresent(ui -> ui.access(() -> {
                                Notification.show(getTranslation("views.merge.notification.success"));
                            }));
                        } else {
                            Notification.show(getTranslation("views.merge.notification.noselection"));
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                // Bouton pour annuler l'action
                Button cancelButton = new Button(getTranslation("views.cancel"), cancelEvent -> {
                    confirmationDialog.close();
                });
                cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
                confirmationDialog.add(buttonLayout);

                confirmationDialog.open();
            }
        });
    }

    /**
     * Set the checkbox Map.
     *
     * @param checkboxMap the checkbox map
     */
    protected void setCheckboxMap(Map<T, Checkbox> checkboxMap) {
        this.checkboxMap = checkboxMap;
    }
}
