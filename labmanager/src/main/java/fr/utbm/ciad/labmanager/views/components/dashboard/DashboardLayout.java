package fr.utbm.ciad.labmanager.views.components.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.dom.Style;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.toggleComponent.ToggleIconComponent;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryBarChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryNightingaleRoseChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryPieChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.charts.observer.ChartHandler;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryBarChart;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryNightingaleRoseChart;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryPieChart;
import fr.utbm.ciad.labmanager.views.components.dashboard.cell.DropCell;
import fr.utbm.ciad.labmanager.views.components.dashboard.component.DraggableComponent;
import fr.utbm.ciad.labmanager.views.components.dashboard.component.SelectComponent;
import fr.utbm.ciad.labmanager.views.components.dashboard.grid.DropGrid;
import fr.utbm.ciad.labmanager.views.components.dashboard.grid.observer.GridLogger;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardChartItem;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.component.DashboardComponentType;
import fr.utbm.ciad.labmanager.views.components.dashboard.localstorage.manager.ChartLocalStorageManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Implementation a dashboard layout.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DashboardLayout extends VerticalLayout {

    private final DropGrid dropGrid = new DropGrid();

    private final ToggleIconComponent editionButton;
    private final Button clearAllButton = new Button(getTranslation("views.clear_all"));

    private final Select<SelectComponent<DraggableComponent>> componentSelect = new Select<>();

    private final PublicationCategoryChartFactory<PublicationCategoryBarChart> barChartFactory;
    private final PublicationCategoryChartFactory<PublicationCategoryPieChart> pieChartFactory;
    private final PublicationCategoryChartFactory<PublicationCategoryNightingaleRoseChart> nightingaleChartFactory;

    private final ChartLocalStorageManager chartLocalStorageManager = new ChartLocalStorageManager();

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     */
    public DashboardLayout(@Autowired PublicationService publicationService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super();

        GridLogger gridLogger = new GridLogger(
                this::onComponentAdded,
                this::onNewComponentAdded,
                this::saveComponent,
                this::onComponentRemoved);
        dropGrid.addObserver(gridLogger);

        barChartFactory = new PublicationCategoryBarChartFactory();
        pieChartFactory = new PublicationCategoryPieChartFactory();
        nightingaleChartFactory = new PublicationCategoryNightingaleRoseChartFactory();

        editionButton = new ToggleIconComponent(
                new Icon(VaadinIcon.EDIT),
                this::setEditionMode,
                new Icon(VaadinIcon.CLOSE),
                this::removeEditionMode
        );

        createSelect(publicationService);

        add(getHeader(), dropGrid);

        getWebStorageComponents(publicationService);
    }

    /**
     * Sets the editing mode and updates the UI to reflect the change.
     */
    private void setEditionMode() {
        changeEditionMode(true);
    }

    /**
     * Removes the edition mode and updates the UI accordingly.
     */
    private void removeEditionMode() {
        changeEditionMode(false);
    }

    /**
     * Changes the mode of the dashboard between editing and non-editing states.
     *
     * @param editionMode boolean indicating whether the editing mode should be enabled (true) or disabled (false)
     */
    private void changeEditionMode(boolean editionMode) {
        dropGrid.changeEditionMode(editionMode);
        componentSelect.setVisible(editionMode);
        clearAllButton.setVisible(!dropGrid.isEmpty() && editionMode);
    }

    /**
     * Create the Select component used for selecting components.
     * Sets the label, populates items, and defines behavior on component selection.
     */
    private void createSelect(PublicationService publicationService) {
        componentSelect.setLabel(getTranslation("views.add_component"));

        componentSelect.setItems(List.of(
                new SelectComponent<>(
                        "BarChart",
                        () -> new DraggableComponent(new PublicationCategoryLayout<>(publicationService, barChartFactory), DashboardComponentType.BAR_CHART),
                        this::initializeChart),
                new SelectComponent<>(
                        "PieChart",
                        () -> new DraggableComponent(new PublicationCategoryLayout<>(publicationService, pieChartFactory), DashboardComponentType.PIE_CHART),
                        this::initializeChart),
                new SelectComponent<>(
                        "NightingaleChart",
                        () -> new DraggableComponent(new PublicationCategoryLayout<>(publicationService, nightingaleChartFactory), DashboardComponentType.NIGHTINGALE_CHART),
                        this::initializeChart))
        );

        componentSelect.setItemLabelGenerator(SelectComponent::getName);

        componentSelect.addValueChangeListener(event -> {
            SelectComponent<DraggableComponent> selectedItem = event.getValue();
            if (selectedItem != null) {
                dropGrid.addComponentInFirstEmptyCell(selectedItem.getComponent());
                componentSelect.clear();
            }
        });

        componentSelect.setVisible(!dropGrid.isEmpty());
        componentSelect.getStyle().set("grid-column", "1");
        componentSelect.getStyle().setMarginRight("auto");
    }

    /**
     * Initializes the properties of the given PublicationCategoryLayout chart.
     *
     * @param chart the chart (PublicationCategoryLayout) to initialize
     */
    private void initializeChart(PublicationCategoryLayout<?> chart) {
        if (chart != null) {
            chart.setSize("500px", "200px");
            chart.setChartSize("450px", "180px");
            chart.setFormHidden(true);
        }
    }

    /**
     * Initializes the properties of the PublicationCategoryLayout chart contained in the given DraggableComponent.
     *
     * @param component the draggableComponent containing the chart (PublicationCategoryLayout) to initialize
     */
    private void initializeChart(DraggableComponent component) {
        if (component.getComponent() instanceof PublicationCategoryLayout<?> chart) {
            initializeChart(chart);
        }
    }

    /**
     * Creates and configures the header layout for the dashboard.
     * Sets up layout style and alignment, and adds UI components.
     *
     * @return HorizontalLayout the configured header layout.
     */
    private HorizontalLayout getHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.getStyle()
                .setPosition(Style.Position.RELATIVE)
                .setWidth("100%")
                .setHeight("70px");

        editionButton.getIcon().getStyle()
                .setMarginLeft("2px");

        clearAllButton.setVisible(false);
        clearAllButton.addClickListener(event -> {
            dropGrid.emptyGrid();
            clearAllButton.setVisible(false);
        });
        clearAllButton.getStyle()
                .setWidth("150px")
                .setColor("#ba1d16");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(clearAllButton, editionButton);
        horizontalLayout.getStyle()
                .setPosition(Style.Position.ABSOLUTE)
                .setRight("0")
                .setBottom("0")
                .setWidth("fit-content")
                .setJustifyContent(Style.JustifyContent.RIGHT);

        header.add(componentSelect, horizontalLayout);
        return header;
    }

    /**
     * Retrieves the components stored in the web storage and adds them to the grid.
     *
     * @param publicationService the publication service used to create the chart components
     */
    private void getWebStorageComponents(PublicationService publicationService) {
        for (DropCell cell : dropGrid.getCells()) {
            DashboardChartItem item = new DashboardChartItem(cell.getIndex());

            chartLocalStorageManager.getItem(item.getId(), dashBoardChartItem -> {
                if (dashBoardChartItem != null) {
                    DraggableComponent component;
                    component = new DraggableComponent(dashBoardChartItem.createComponent(publicationService), dashBoardChartItem.getComponentType());
                    dropGrid.addNewComponent(cell, component);
                }
            });
        }
    }

    /**
     * Sets up the visual style and behavior of the given DraggableComponent.
     *
     * @param component the DraggableComponent to be set up
     */
    private void setupComponent(DraggableComponent component) {
        component.getStyle().setBackgroundColor("#f2f2f2");
        component.getStyle().setBorder("#bfbfbf");
        addObservertoChartComponent(component);
        addContextMenuToDraggableComponent(component);
    }

    /**
     * Called when a component has been added to the grid.
     *
     * @param component the component that was added
     */
    private void onComponentAdded(Component component) {
        if (editionButton.getMode()) {
            clearAllButton.setVisible(true);
        }
        saveComponent(component);
    }

    /**
     * Called when a new component has been added to the grid.
     *
     * @param component the new component that was added
     */
    private void onNewComponentAdded(Component component) {
        if (component instanceof DraggableComponent draggableComponent) {
            setupComponent(draggableComponent);
            if (!editionButton.getMode()) {
                draggableComponent.setDraggable(false);
            }
        }
    }

    /**
     * Called when a component has been removed from the grid.
     *
     * @param component the component that was removed
     */
    private void onComponentRemoved(Component component) {
        if (dropGrid.getComponents().size() <= 1) {
            clearAllButton.setVisible(false);
        }
        if (component instanceof DraggableComponent draggableComponent) {
            draggableComponent.getParent().ifPresent(parent -> {
                if (parent instanceof DropCell cell) {
                    chartLocalStorageManager.remove(cell.getIndex());
                }
            });
        }
    }

    /**
     * Adds an observer to the PublicationCategoryLayout stored in the given DraggableComponent
     * (if it contains a component of this type, otherwise nothing happen).
     *
     * @param component the DraggableComponent to which the observer will be added
     */
    private void addObservertoChartComponent(DraggableComponent component) {
        if (component.getComponent() instanceof PublicationCategoryLayout<?> publicationCategoryLayout) {
            ChartHandler chartHandler = new ChartHandler(this::saveComponent, component);
            publicationCategoryLayout.addObserver(chartHandler);
        }
    }

    /**
     * Saves the DraggableComponent by storing its data in local storage.
     *
     * @param component the DraggableComponent to be saved
     */
    private void saveComponent(Component component) {
        if (component instanceof DraggableComponent draggableComponent) {
            component.getParent().ifPresent(parent -> {
                if (parent instanceof DropCell dropCell) {
                    chartLocalStorageManager.add(dropCell.getIndex(), draggableComponent.getComponent(), draggableComponent.getComponentType());
                }
            });
        }
    }

    /**
     * Adds a context menu to a component with the deletion option, and the edit chart option when applicable.
     *
     * @param component The component to which the context menu is added.
     */
    private void addContextMenuToDraggableComponent(DraggableComponent component) {
        component.getContextMenu().addItem(getTranslation("views.charts.edit"), event -> editChart(component), () -> component.isChart() && ((PublicationCategoryLayout<?>) component.getComponent()).isChartGenerated());
        component.getContextMenu().addItem(getTranslation("views.delete"), event -> dropGrid.removeComponent(component), component::isDraggable);
    }

    /**
     * Initiates the editing process for the specified chart component.
     *
     * @param component the draggable component containing the chart to be edited.
     */
    private void editChart(DraggableComponent component) {
        ((PublicationCategoryLayout<?>) component.getComponent()).removeChart();
    }
}