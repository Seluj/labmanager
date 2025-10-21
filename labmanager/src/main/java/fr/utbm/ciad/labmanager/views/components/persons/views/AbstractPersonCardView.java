package fr.utbm.ciad.labmanager.views.components.persons.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractDefaultOrganizationDataFilters;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.AbstractLoggerVerticalLayout;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * Abstract view for showing person's information in a virtual card that could be included in a grid of cards.
 *
 * @author $Author: callaire$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPersonCardView extends AbstractLoggerVerticalLayout implements LocaleChangeObserver {

    private static final long serialVersionUID = -44931810191145637L;

    private final AuthenticatedUser authenticatedUser;

    private final ResearchOrganizationService organizationService;

    private final PersonService personService;

    private final PersonEditorFactory personEditorFactory;

    private final StandardPersonCardGridItemFactory cardFactory;

    private final OrderedList imageContainer;

    private final PaginationComponent paginationComponent;

    private final AbstractDefaultOrganizationDataFilters<Person> filters;

    private final int cardsPerRow;

    private final int numberOfRows;

    /**
     * Constructor.
     *
     * @param cardsPerRow         the number of cards in a row in the grid. It must be greater or equal to 1.
     * @param numberOfRows        the number of rows to show in the cards' viewer. It must be greater or equal to 1.
     * @param initialPageIndex    the index of the card page to show up at startup. It must be greater or equal to zero.
     * @param organizationService the service for accessing the JPA entities of the research organizations.
     * @param personService       the service for accessing the JPA entities of the persons.
     * @param personEditorFactory the factory for creating the person editors.
     * @param cardFactory         the factory for creating the person cards.
     * @param authenticatedUser   the user who is connected to the application.
     * @param loggerFactory       the factory to be used for the composite logger.
     */
    public AbstractPersonCardView(
            int cardsPerRow,
            int numberOfRows,
            int initialPageIndex,
            ResearchOrganizationService organizationService,
            PersonService personService,
            PersonEditorFactory personEditorFactory,
            StandardPersonCardGridItemFactory cardFactory,
            AuthenticatedUser authenticatedUser,
            ContextualLoggerFactory loggerFactory) {
        super(loggerFactory);
        assert cardsPerRow >= 1;
        assert numberOfRows >= 1;
        assert initialPageIndex >= 0;

        this.cardsPerRow = cardsPerRow;
        this.numberOfRows = numberOfRows;
        this.organizationService = organizationService;
        this.personService = personService;
        this.personEditorFactory = personEditorFactory;
        this.cardFactory = cardFactory;
        this.authenticatedUser = authenticatedUser;

        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("padding-left", "75px"); //$NON-NLS-1$ //$NON-NLS-2$
        getStyle().set("padding-right", "75px"); //$NON-NLS-1$ //$NON-NLS-2$

        final var topContainer = new HorizontalLayout();
        topContainer.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        this.filters = createFilters();

        final var menu = createMenuBar();
        if (menu != null) {
            topContainer.add(menu);
        }

        this.imageContainer = new OrderedList();
        this.imageContainer.setWidthFull();
        this.imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID,
                LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        this.imageContainer.getStyle().set("grid-template-columns", //$NON-NLS-1$
                "repeat(" + this.cardsPerRow + ", 1fr)"); //$NON-NLS-1$ //$NON-NLS-2$

        if (this.filters != null) {
            add(this.filters);
        }
        add(topContainer, this.imageContainer);

        this.paginationComponent = createPaginationComponent();
        if (this.paginationComponent != null) {
            add(this.paginationComponent);
        }

        postInitializeFilters();
        fetchCards(initialPageIndex);

        if (this.paginationComponent != null) {
            this.paginationComponent.addPageChangeListener(event -> {
                final var newPageNumber = event.getPageNumber();
                fetchCards(newPageNumber);
                this.paginationComponent.setCurrentPage(newPageNumber);
            });
        }
    }

    /**
     * Invoked to post-initialize the filters after all the associated components have been added.
     * By default, this function initialize the filters if it is of type {@link AbstractDefaultOrganizationDataFilters}.
     * Otherwise, it does nothing.
     */
    protected void postInitializeFilters() {
        final var filters = getFilters();
        if (filters instanceof AbstractDefaultOrganizationDataFilters organizationFilters) {
            organizationFilters.updateDefaultOrganizationIconInToggleButton();
        }
    }

    /**
     * Replies the filters.
     *
     * @return the filters.
     */
    protected AbstractFilters<Person> getFilters() {
        return this.filters;
    }

    /**
     * Refresh the grid with the initial page.
     */
    public void resetGrid() {
        fetchCards(0);
        this.paginationComponent.setCurrentPage(0);
    }

    /**
     * Refresh the grid with the current page.
     */
    public void refreshGrid() {
        fetchCards(this.paginationComponent.getCurrentPage());
    }

    /**
     * Create the filters.
     *
     * @return the filters.
     */
    protected AbstractDefaultOrganizationDataFilters<Person> createFilters() {
        return new PersonFilters(
                () -> this.organizationService.getDefaultOrganization(),
                () -> this.organizationService.getFileManager(),
                this::resetGrid);
    }

    /**
     * Replies if this view is shown by a user that has base administration rights.
     *
     * @return {@code true} if the connected user has bas administration rights.
     */
    protected boolean isAdminRole() {
        return this.authenticatedUser != null && this.authenticatedUser.get().isPresent()
                && this.authenticatedUser.get().get().getRole().hasBaseAdministrationRights();
    }

    /**
     * Create the menu bar.
     *
     * @return the bar.
     */
    protected MenuBar createMenuBar() {
        final var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        if (isAdminRole()) {
            ComponentFactory.addIconItem(menu, LineAwesomeIcon.PLUS_SOLID,
                    getTranslation("views.persons.add_person"), null, //$NON-NLS-1$
                    it -> addPerson());
        }
        ComponentFactory.addIconItem(menu, LineAwesomeIcon.TH_LIST_SOLID,
                getTranslation("views.persons.switch_to_list_view"), null, //$NON-NLS-1$
                it -> getUI().ifPresent(ui -> ui.navigate(getListViewType())));
        return menu;
    }

    /**
     * Replies the name of the view to show the persons in a list.
     *
     * @return the type of the view.
     */
    protected abstract Class<? extends Component> getListViewType();

    /**
     * Replies the sort criteria of the cards.
     *
     * @return the criteria
     */
    @SuppressWarnings("static-method")
    protected Sort getCardSort() {
        return Sort.by("lastName").and(Sort.by("firstName")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void fetchCards(int pageNumber) {
        final var pageable = PageRequest.of(pageNumber, this.cardsPerRow * this.numberOfRows, getCardSort());
        final Page<Person> persons;
        final int numberOfPages;
        if (this.filters != null) {
            numberOfPages = computeTotalNumberOfPages(this.personService.countAllPersons(this.filters));
            persons = this.personService.getAllPersons(pageable, this.filters);
        } else {
            numberOfPages = computeTotalNumberOfPages();
            persons = this.personService.getAllPersons(pageable);
        }
        this.imageContainer.removeAll();
        for (Person person : persons) {
            final var item = this.cardFactory.createCard(person, getLogger(), () -> refreshGrid());
            this.imageContainer.add(item);
        }
        this.paginationComponent.setTotalPages(numberOfPages);
        // Ensure that the card view is showing the correct content
        UI.getCurrent().getPage().executeJs("window.scrollTo(0, 0);"); //$NON-NLS-1$
    }

    private int computeTotalNumberOfPages() {
        return computeTotalNumberOfPages(this.personService.countAllPersons());
    }

    private int computeTotalNumberOfPages(long personCount) {
        return (int) (personCount / (this.cardsPerRow * this.numberOfRows));
    }

    /**
     * Invoked when the user has clicked on the "add person" button.
     */
    protected void addPerson() {
        openPersonEditor(new Person(), getTranslation("views.persons.add_person"), true); //$NON-NLS-1$
    }

    /**
     * Show the editor of a person.
     *
     * @param person     the person to edit.
     * @param title      the title of the editor.
     * @param isCreation indicates if the editor is for creating or updating the entity.
     */
    protected void openPersonEditor(Person person, String title, boolean isCreation) {
        final AbstractEntityEditor<Person> editor;
        if (isCreation) {
            editor = this.personEditorFactory.createAdditionEditor(person, getLogger());
        } else {
            editor = this.personEditorFactory.createUpdateEditor(person, getLogger());
        }
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> refreshGrid();
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if its has been changed in the JPA database
                newEntity ? refreshAll : null,
                newEntity ? null : refreshAll);
    }

    /**
     * Create the pagination component. This function is invoked when the card grid component is created.
     *
     * @return the pagination component.
     */
    protected PaginationComponent createPaginationComponent() {
        return new DefaultPaginationComponent(computeTotalNumberOfPages());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        if (this.filters != null) {
            this.filters.localeChange(event);
        }
        if (this.paginationComponent != null) {
            this.paginationComponent.localeChange(event);
        }
        refreshGrid();
    }

    /**
     * Pagination component for the person card view.
     *
     * @author $Author: callaire$
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static abstract class PaginationComponent extends Div implements LocaleChangeObserver {

        private static final long serialVersionUID = -3690402936089282756L;

        /**
         * Show the next page if it exists.
         */
        public abstract void nextPage();

        /**
         * Show the previous page if it exists.
         */
        public abstract void previousPage();

        /**
         * Replies the index of the current page.
         *
         * @return the page index.
         */
        public abstract int getCurrentPage();

        /**
         * Change the index of the current page.
         *
         * @param index the page index.
         */
        public abstract void setCurrentPage(int index);

        /**
         * Change the total number of pages.
         *
         * @param totalPages the total number of pages.
         */
        public abstract void setTotalPages(int totalPages);

        /**
         * Add a listener on the page change.
         *
         * @param listener the listener.
         */
        public void addPageChangeListener(ComponentEventListener<PageChangeEvent> listener) {
            addListener(PageChangeEvent.class, listener);
        }

    }

    /**
     * Default pagination component for the person card view.
     *
     * @author $Author: callaire$
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class DefaultPaginationComponent extends PaginationComponent {

        private static final long serialVersionUID = 6529859105834969074L;

        private final Button nextButton;

        private final Button previousButton;

        private final Span pageLabel;

        private int currentPage = 0;

        private int totalPages;

        /**
         * Constructor.
         *
         * @param totalPages the total number of pages.
         */
        public DefaultPaginationComponent(int totalPages) {
            this.totalPages = totalPages;
            this.nextButton = new Button(getTranslation("views.pagination.next"), click -> nextPage()); //$NON-NLS-1$
            this.previousButton = new Button(getTranslation("views.pagination.previous"), click -> previousPage()); //$NON-NLS-1$
            this.pageLabel = new Span();
            add(this.previousButton, this.pageLabel, this.nextButton);
            update();
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            this.nextButton.setText(getTranslation("views.pagination.next")); //$NON-NLS-1$
            this.previousButton.setText(getTranslation("views.pagination.previous")); //$NON-NLS-1$
            this.pageLabel.setText(getTranslation("views.pagination.current", Integer.valueOf(this.currentPage + 1), Integer.valueOf(this.totalPages))); //$NON-NLS-1$
        }

        protected void update() {
            this.pageLabel.setText(getTranslation("views.pagination.current", Integer.valueOf(this.currentPage + 1), Integer.valueOf(this.totalPages))); //$NON-NLS-1$
            setVisible(this.totalPages > 1);
            this.nextButton.setEnabled(this.currentPage < (this.totalPages - 1));
            this.previousButton.setEnabled(this.currentPage > 0);
        }

        @Override
        public void nextPage() {
            if (this.currentPage < (this.totalPages - 1)) {
                ++this.currentPage;
                fireEvent(new PageChangeEvent(this, this.currentPage));
            }
        }

        @Override
        public void previousPage() {
            if (this.currentPage > 0) {
                --this.currentPage;
                fireEvent(new PageChangeEvent(this, this.currentPage));
            }
        }

        @Override
        public int getCurrentPage() {
            return this.currentPage;
        }

        @Override
        public void setCurrentPage(int pageNumber) {
            this.currentPage = pageNumber;
            update();
        }

        @Override
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
            update();
        }

    }

    /**
     * Event that corresponds to a page change in a {@link PaginationComponent}.
     *
     * @author $Author: callaire$
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PageChangeEvent extends ComponentEvent<PaginationComponent> {

        private static final long serialVersionUID = 454781210433896343L;

        private final int pageNumber;

        /**
         * Constructor.
         *
         * @param source     the source of the event.
         * @param pageNumber the index of the page that is shown in the pagination component.
         */
        public PageChangeEvent(PaginationComponent source, int pageNumber) {
            super(source, false);
            this.pageNumber = pageNumber;
        }

        /**
         * Replies the page that is shown in tha pagination component.
         *
         * @return the index of the current page.
         */
        public int getPageNumber() {
            return this.pageNumber;
        }

    }

}
