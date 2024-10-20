package fr.utbm.ciad.labmanager.views.components.similarity;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonMergingService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/** Represent a layout for the Person similarity options.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PersonSimilarityLayout extends AbstractSimilarityLayout<Person> {

    private final PersonMergingService personMergingService;

    private final PersonNameComparator personNameComparator;

    /** Constructor.
     * @param personMergingService the person merging service
     * @param personNameComparator the person name comparator
     */
    public PersonSimilarityLayout(PersonMergingService personMergingService, PersonNameComparator personNameComparator) {
        super();
        this.personMergingService = personMergingService;
        this.personNameComparator = personNameComparator;

    }

    /** Create the grids. Calls the person service to get the person duplicates and creates a grid for each group.
     */
    @Override
    public void createGrids() {

        try {
            List<Set<Person>> similarityGroups = personMergingService.getPersonDuplicates(null,null);
            Iterator<Set<Person>> iterator = similarityGroups.iterator();
            while(iterator.hasNext()) {
                Set<Person> group = iterator.next();
                int size = group.size()*68;
                Grid<Person> grid = new Grid<>();
                grid.setItems(group);
                grid.addColumn(Person::getFirstName).setHeader("First Name");
                grid.addColumn(Person::getLastName).setHeader("Last Name");
                grid.addColumn(Person::getOrganizations).setHeader("Organization");
                grid.addColumn(Person::getAuthorshipNumber).setHeader("Publications");

                NativeButtonRenderer<Person> littleMergeButtonRenderer = new NativeButtonRenderer<>(">");
                littleMergeButtonRenderer.addItemClickListener(event -> {
                    Person person = (Person) event;
                    System.out.println("Little merge for " + person);

                    List<Person> personsInGrid = new ArrayList<>();
                    grid.getDataProvider().fetch(new Query<>()).filter(p -> !p.equals(person)).forEach(personsInGrid::add);

                    if (!grid.getSelectedItems().isEmpty()) {
                        Person selectedPerson = grid.getSelectedItems().iterator().next();

                        if (selectedPerson != null && selectedPerson != person) {
                            // Création de la boîte de dialogue pour confirmation
                            Dialog confirmationDialog = new Dialog();
                            H2 header = new H2(selectedPerson.getFullName() + " ---> " + person.getFullName());
                            header.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

                            Paragraph confirmationText = new Paragraph(getTranslation("views.merge.fusion", selectedPerson.getFullName(), person.getFullName()));

                            // Bouton pour confirmer l'action
                            Button confirmButton = new Button(getTranslation("views.confirm"), confirmEvent -> {
                                try {
                                    // Fusionner les personnes après confirmation
                                    List<Person> persons = new ArrayList<>();
                                    persons.add(selectedPerson);
                                    personMergingService.mergePersons(persons, person);
                                    System.out.println("Merged " + selectedPerson + " with " + person);

                                    personsInGrid.remove(selectedPerson);
                                    if (personsInGrid.isEmpty()) {
                                        grids.remove(grid);
                                        remove(grid);
                                    } else {
                                        grid.setItems(personsInGrid);
                                        grid.getDataProvider().refreshAll();
                                    }

                                    // Fermer la boîte de dialogue après la fusion
                                    confirmationDialog.close();
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

                NativeButtonRenderer<Person> ultraMergeButtonRenderer = new NativeButtonRenderer<>(">>");
                ultraMergeButtonRenderer.addItemClickListener(event -> {
                    Person person = event;
                    System.out.println("Ultra merge for " + person);

                    List<Person> personsInGrid = new ArrayList<>();
                    grid.getDataProvider().fetch(new Query<>()).filter(p -> !p.equals(person)).forEach(personsInGrid::add);

                    // Afficher ou traiter les personnes récupérées dans la grille
                    System.out.println("Persons in grid:");
                    personsInGrid.forEach(System.out::println);

                    if(!personsInGrid.isEmpty()){

                        Dialog confirmationDialog = new Dialog();
                        H2 header = new H2( " ---> " + person.getFullName());
                        header.getStyle().set("font-size", "1.5em").set("font-weight", "bold");

                        Paragraph confirmationText = new Paragraph(getTranslation("views.merge.list"));
                        confirmationDialog.add(header, confirmationText);


                        // Bouton pour confirmer l'action
                        Button confirmButton = new Button(getTranslation("views.confirm"), confirmEvent -> {
                            try {
                                // Fusionner les personnes après confirmation
                                personMergingService.mergePersons(personsInGrid, person);
                                System.out.println("Merged " + personsInGrid + " with " + person);
                                grids.remove(grid);
                                remove(grid);
                                // Fermer la boîte de dialogue après la fusion
                                confirmationDialog.close();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });

                        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

                        Button cancelButton = new Button(getTranslation("views.cancel"), cancelEvent -> confirmationDialog.close());
                        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

                        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);

                        VerticalLayout dialogLayout = new VerticalLayout(header, confirmationText);
                        personsInGrid.forEach(p -> dialogLayout.add(new Text(p.getFullName() + " ")));
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
            e.printStackTrace();
        }


    }

}
