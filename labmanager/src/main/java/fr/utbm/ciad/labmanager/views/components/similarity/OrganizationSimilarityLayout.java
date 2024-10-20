package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationMergingService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Represent a layout for the Organization similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class OrganizationSimilarityLayout extends AbstractSimilarityLayout<ResearchOrganization> {

    private OrganizationMergingService organizationMergingService;

    private OrganizationNameComparator nameComparator;

    /**
     * Constructor.
     *
     * @param organizationMergingService the person merging service
     * @param nameComparator             the person name comparator
     */
    public OrganizationSimilarityLayout(OrganizationMergingService organizationMergingService, OrganizationNameComparator nameComparator) {
        super();
        this.organizationMergingService = organizationMergingService;
        this.nameComparator = nameComparator;

    }

    @Override
    public void createGrids() {
        try {
            List<Set<ResearchOrganization>> similarityGroups = organizationMergingService.getOrganizationDuplicates(null, null);
            Iterator<Set<ResearchOrganization>> iterator = similarityGroups.iterator();
            while (iterator.hasNext()) {
                Set<ResearchOrganization> group = iterator.next();
                int size = group.size() * 68;
                Grid<ResearchOrganization> grid = new Grid<>();
                grid.setItems(group);
                grid.addColumn(ResearchOrganization::getName).setHeader(getTranslation("views.name"));
                grid.addColumn(ResearchOrganization::getCountry).setHeader(getTranslation("views.outgoing_invitation.country"));


                NativeButtonRenderer<ResearchOrganization> littleMergeButtonRenderer = new NativeButtonRenderer<>(">");
                littleMergeButtonRenderer.addItemClickListener(event -> {
                    ResearchOrganization researchOrganization = event;
                    System.out.println("Little merge for " + researchOrganization);

                    List<ResearchOrganization> researchOrganizationInGrid = new ArrayList<>();
                    grid.getDataProvider().fetch(new Query<>()).filter(p -> !p.equals(researchOrganization)).forEach(researchOrganizationInGrid::add);

                    if (!grid.getSelectedItems().isEmpty()) {
                        ResearchOrganization selectedResearchOrganization = grid.getSelectedItems().iterator().next();

                        if (selectedResearchOrganization != null && selectedResearchOrganization != researchOrganization) {
                            // Création de la boîte de dialogue pour confirmation
                            Dialog confirmationDialog = new Dialog();
                            H2 header = new H2(selectedResearchOrganization.getName() + " ---> " + researchOrganization.getName());
                            header.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

                            Paragraph confirmationText = new Paragraph(getTranslation("views.merge.fusion",
                                    selectedResearchOrganization.getName(),
                                    researchOrganization.getName()
                            ));

                            // Bouton pour confirmer l'action
                            Button confirmButton = new Button(getTranslation("views.confirm"), confirmEvent -> {
                                try {
                                    // Fusionner les personnes après confirmation
                                    List<ResearchOrganization> researchOrganizations = new ArrayList<>();
                                    researchOrganizations.add(selectedResearchOrganization);
                                    organizationMergingService.mergeOrganizations(researchOrganizations, researchOrganization);
                                    System.out.println("Merged " + selectedResearchOrganization + " with " + researchOrganization);

                                    researchOrganizationInGrid.remove(selectedResearchOrganization);
                                    if (researchOrganizationInGrid.isEmpty()) {
                                        grids.remove(grid);
                                        remove(grid);
                                    } else {
                                        grid.setItems(researchOrganizationInGrid);
                                        grid.getDataProvider().refreshAll();
                                    }

                                    // Fermer la boîte de dialogue après la fusion
                                    confirmationDialog.close();
                                    getUI().ifPresent(ui -> ui.access(() -> {
                                        Notification.show(getTranslation("views.merge.notification.success"));
                                    }));

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                            // Bouton pour annuler l'action
                            Button cancelButton = new Button(getTranslation("views.cancel"), cancelEvent -> confirmationDialog.close());
                            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                            // Ajout des boutons à la boîte de dialogue
                            HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);

                            VerticalLayout dialogLayout = new VerticalLayout(header, confirmationText);

                            confirmationDialog.add(dialogLayout);
                            confirmationDialog.getFooter().add(buttonsLayout);
                            // Afficher la boîte de dialogue
                            confirmationDialog.open();
                        }
                    }
                });

                NativeButtonRenderer<ResearchOrganization> ultraMergeButtonRenderer = new NativeButtonRenderer<>(">>");
                ultraMergeButtonRenderer.addItemClickListener(event -> {
                    ResearchOrganization researchOrganization = event;
                    System.out.println("Ultra merge for " + researchOrganization);

                    List<ResearchOrganization> researchOrganizationInGrid = new ArrayList<>();
                    grid.getDataProvider().fetch(new Query<>()).filter(p -> !p.equals(researchOrganization)).forEach(researchOrganizationInGrid::add);

                    // Afficher ou traiter les personnes récupérées dans la grille
                    System.out.println("Persons in grid:");
                    researchOrganizationInGrid.forEach(System.out::println);

                    if (!researchOrganizationInGrid.isEmpty()) {

                        Dialog confirmationDialog = new Dialog();
                        H2 header = new H2(" ---> " + researchOrganization.getName());
                        header.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

                        Paragraph confirmationText = new Paragraph(getTranslation("views.merge.list"));
                        confirmationDialog.add(header, confirmationText);


                        // Bouton pour confirmer l'action
                        Button confirmButton = new Button(getTranslation("views.confirm"), confirmEvent -> {
                            try {
                                // Fusionner les personnes après confirmation
                                organizationMergingService.mergeOrganizations(researchOrganizationInGrid, researchOrganization);
                                System.out.println("Merged " + researchOrganizationInGrid + " with " + researchOrganization);
                                grids.remove(grid);
                                remove(grid);
                                // Fermer la boîte de dialogue après la fusion
                                confirmationDialog.close();
                                getUI().ifPresent(ui -> ui.access(() -> {
                                    Notification.show(getTranslation("views.merge.notification.success"));
                                }));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                        Button cancelButton = new Button(getTranslation("views.cancel"), cancelEvent -> confirmationDialog.close());
                        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);

                        VerticalLayout dialogLayout = new VerticalLayout(header, confirmationText);
                        researchOrganizationInGrid.forEach(p -> dialogLayout.add(new Paragraph(p.getName() + " ")));
                        confirmationDialog.add(dialogLayout);
                        confirmationDialog.getFooter().add(buttonsLayout);

                        confirmationDialog.open();

                    }
                });

                grid.addColumn(littleMergeButtonRenderer).setHeader("Little Merge");
                grid.addColumn(ultraMergeButtonRenderer).setHeader("Ultra Merge");
                grid.setHeight(size + "px");
                grids.add(grid);
                add(grid);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
