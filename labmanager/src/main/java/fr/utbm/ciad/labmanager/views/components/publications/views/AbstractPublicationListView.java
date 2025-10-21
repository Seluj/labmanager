/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.publications.views;

import com.google.common.base.Strings;
import com.helger.commons.io.stream.StringInputStream;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.IoConstants;
import fr.utbm.ciad.labmanager.utils.io.bibtex.BibTeXConstants;
import fr.utbm.ciad.labmanager.utils.io.od.OpenDocumentConstants;
import fr.utbm.ciad.labmanager.utils.io.ris.RISConstants;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.download.DownloadExtension;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractFilters;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.publications.editors.PublicationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.wizard.ThumbnailGeneratorWizard;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.arakhne.afc.progress.Progression;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * Abstract implementation of a list all the publications whatever the type of publication.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPublicationListView extends AbstractEntityListView<Publication> {

    /**
     * Key for the property that is the name of the import view.
     */
    protected static final String PROP_IMPORT_VIEW = "importView"; //$NON-NLS-1$
    /**
     * Key for the property that is the name of authors column.
     */
    protected static final String PROP_AUTHORS_COLUMN_NAME = "authorsColumnLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the label for the person creation.
     */
    protected static final String PROP_PERSON_CREATION_LABEL = "personCreationLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the label for the person field.
     */
    protected static final String PROP_PERSON_FIELD_LABEL = "personFieldLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the helper text for the person field.
     */
    protected static final String PROP_PERSON_FIELD_HELPER = "personFieldHelperKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the null error message associated to the person field.
     */
    protected static final String PROP_PERSON_FIELD_NULL_ERROR = "personNullErrorKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the duplication error message associated to the person field.
     */
    protected static final String PROP_PERSON_FIELD_DUPLICATE_ERROR = "personDuplicateErrorKey"; //$NON-NLS-1$
    private static final long serialVersionUID = 6591553988384909013L;
    private static final String BIBTEX_FILENAME = "publication.bib"; //$NON-NLS-1$

    private static final String RIS_FILENAME = "publication.ris"; //$NON-NLS-1$

    private static final String ODT_FILENAME = "publication.odt"; //$NON-NLS-1$

    private static final String HTML_FILENAME = "publication.html"; //$NON-NLS-1$

    private static final String JSON_FILENAME = "publication.json"; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$

    private final JournalService journalService;

    private final PublicationService publicationService;

    private final PublicationEditorFactory publicationEditorFactory;

    private final ResearchOrganizationService organizationService;

    private PublicationDataProvider dataProvider;

    private PublicationExporter exporter;

    private Column<Publication> titleColumn;

    private Column<Publication> authorsColumn;

    private Column<Publication> publishingColumn;

    private Column<Publication> typeColumn;

    private Column<Publication> yearColumn;

    private Column<Publication> fileColumn;

    private Column<Publication> validationColumn;

    private MenuItem exportButton;

    private MenuItem importButton;

    private MenuItem regenerateThumbnailButton;

    /**
     * Constructor.
     *
     * @param authenticatedUser         the connected user.
     * @param messages                  the accessor to the localized messages (spring layer).
     * @param loggerFactory             the factory to be used for the composite logger.
     * @param publicationService        the service for accessing the publications.
     * @param publicationEditorFactory  the factory for creating the publication editors.
     * @param journalService            the service for accessing the JPA entities for journal.
     * @param organizationService       the service for accessing the JPA entities for research organization.
     * @param importView                the name of the view for importating publications.
     * @param authorsColumnLabelKey     the key in the localized messages for the messages related to the label of the authors column.
     * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
     * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
     * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
     * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
     * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
     * @param properties                specification of properties that may be passed to the construction function {@link #createFilters()},
     *                                  {@link #createGrid()} and {@link #createMenuBar()} and {@link #createMobileFilters()}.
     * @since 4.0
     */
    public AbstractPublicationListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory,
            PublicationService publicationService, PublicationEditorFactory publicationEditorFactory,
            JournalService journalService, ResearchOrganizationService organizationService,
            Class<? extends Component> importView, ConstructionPropertiesBuilder properties) {
        super(Publication.class, authenticatedUser, messages, loggerFactory,
                properties
                        .map(PROP_IMPORT_VIEW, importView)
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.addresses.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.addresses.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.addresses.delete_success") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.addresses.delete_error")); //$NON-NLS-1$
        this.publicationService = publicationService;
        this.publicationEditorFactory = publicationEditorFactory;
        this.journalService = journalService;
        this.organizationService = organizationService;

        final var session = VaadinService.getCurrentRequest().getWrappedSession();
        final String key = ViewConstants.PUBLICATION_ID_TO_EDIT;
        final var attr = session.getAttribute(key);
        if (attr != null) {
            int publicationId = -1;
            if (attr instanceof Integer ivalue) {
                publicationId = ivalue.intValue();
            } else if (attr instanceof Long lvalue) {
                publicationId = lvalue.intValue();
            } else if (attr instanceof String svalue) {
                publicationId = Integer.parseInt(svalue);
            }
            session.removeAttribute(key);
            if (publicationId >= 0) {
                final var publication = this.publicationService.getPublicationById(publicationId);
                if (publication != null) {
                    edit(publication); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Create the instance of the publication exporter.
     *
     * @return the exporter.
     * @since 4.0
     */
    protected PublicationExporter createPublicationExporter() {
        return new PublicationExporter();
    }

    /**
     * Replies the instance of the publication exporter.
     *
     * @return the exporter.
     * @since 4.0
     */
    protected PublicationExporter getPublicationExporter() {
        if (this.exporter == null) {
            this.exporter = createPublicationExporter();
        }
        return this.exporter;
    }

    /**
     * Change the data provider;.
     *
     * @param dataProvider the data provider.
     */
    protected void setDataProvider(PublicationDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    protected void createHoverMenuBar(Publication entity, MenuBar menuBar) {
        super.createHoverMenuBar(entity, menuBar);
        getPublicationExporter().createHoverMenuBar(entity, menuBar);
    }

    @Override
    protected MenuBar createMenuBar() {
        var menu = super.createMenuBar();
        if (menu == null) {
            menu = new MenuBar();
            menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        }

        this.exportButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.FILE_EXPORT_SOLID, null, null, null);
        this.exportButton.setEnabled(false);
        final var exportSubMenu = this.exportButton.getSubMenu();
        getPublicationExporter().createExportButtons(exportSubMenu);

        final Class<? extends Component> importViewType = getProperties().get(PROP_IMPORT_VIEW);
        if (importViewType != null) {
            this.importButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.FILE_IMPORT_SOLID, null, null,
                    it -> getUI().ifPresent(ui -> ui.navigate(importViewType)));
        }

        this.regenerateThumbnailButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SYNC_ALT_SOLID, null, null, it -> openThumbnailRegenerationWizard());

        return menu;
    }

    /**
     * Open the wizard for regenerating the publication thumbnails.
     */
    protected void openThumbnailRegenerationWizard() {
        final var selection = getGrid().getSelectedItems();
        if (selection != null && !selection.isEmpty()) {
            final var identifiers = AbstractLabManagerWizard.buildQueryParameters(selection);
            getUI().ifPresent(ui -> ui.navigate(ThumbnailGeneratorWizard.class, identifiers));
        } else {
            getUI().ifPresent(ui -> ui.navigate(ThumbnailGeneratorWizard.class));
        }
    }

    private AbstractEntityEditor<Publication> createPublicationUpdateEditor(Publication publication) {
        final var props = getProperties();
        return this.publicationEditorFactory.createUpdateEditor(
                publication,
                getSupportedPublicationTypeArray(),
                true,
                props.get(PROP_PERSON_CREATION_LABEL),
                props.get(PROP_PERSON_FIELD_LABEL),
                props.get(PROP_PERSON_FIELD_HELPER),
                props.get(PROP_PERSON_FIELD_NULL_ERROR),
                props.get(PROP_PERSON_FIELD_DUPLICATE_ERROR),
                getLogger());
    }

    @Override
    protected void onSelectionChange(Set<?> selection) {
        super.onSelectionChange(selection);
        final int size = selection.size();
        this.exportButton.setEnabled(size >= 1);
    }

    /**
     * Replies the types of publications that are supported by this view.
     *
     * @return the types of supported publications.
     */
    protected abstract Stream<PublicationType> getSupportedPublicationTypes();

    /**
     * Replies the types of publications that are supported by this view.
     *
     * @return the types of supported publications.
     */
    protected final PublicationType[] getSupportedPublicationTypeArray() {
        return getSupportedPublicationTypes().toArray(size -> new PublicationType[size]);
    }

    /**
     * Create filters that correspond to the supported types.
     *
     * @param filters the additional filters.
     * @return the full filters.
     * @see #getSupportedPublicationTypes()
     */
    protected Specification<Publication> createJpaFilters(Specification<Publication> filters) {
        final var filters0 = new PublicationTypeSpecification();
        final var filters1 = filters == null ? filters0 : filters0.and(filters);
        return filters1;
    }

    @Override
    protected AbstractFilters<Publication> createFilters() {
        return new PublicationFilters(getAuthenticatedUser(), this::refreshGrid);
    }

    @Override
    protected PublicationFilters getFilters() {
        return (PublicationFilters) super.getFilters();
    }

    /**
     * Initialize the given JPA entity for being displayed in the list.
     *
     * @param entity the entity.
     */
    @SuppressWarnings("static-method")
    protected void initializeEntityFromJPA(Publication entity) {
        // Force the loaded of the lazy data that is needed for rendering the table
        Hibernate.initialize(entity.getAuthorships());
        // Force the loading of all the data that is required for obtaining the place where the publication was published
        entity.getWherePublishedShortDescription();
    }

    @Override
    protected Grid<Publication> createGridInstance() {
        final var grid = super.createGridInstance();
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        return grid;
    }

    @Override
    protected boolean createGridColumns(Grid<Publication> grid) {
        this.titleColumn = grid.addColumn(it -> it.getTitle())
                .setAutoWidth(false)
                .setSortProperty("title"); //$NON-NLS-1$
        this.authorsColumn = grid.addColumn(new ComponentRenderer<>(this::getAuthorsComponent))
                .setAutoWidth(false);
        this.publishingColumn = grid.addColumn(new ComponentRenderer<>(this::getPublishingComponent))
                .setAutoWidth(false);
        this.typeColumn = grid.addColumn(new ComponentRenderer<>(this::getPublicationTypeComponent))
                .setAutoWidth(false)
                .setSortProperty("type"); //$NON-NLS-1$
        this.yearColumn = grid.addColumn(it -> Integer.toString(it.getPublicationYear()))
                .setAutoWidth(false)
                .setSortProperty("publicationYear"); //$NON-NLS-1$
        this.fileColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
                    final var filename = data.getPathToDownloadablePDF();
                    if (Strings.isNullOrEmpty(filename)) {
                        callback.create(BadgeState.ERROR, getTranslation("views.publication.no_pdf"), null); //$NON-NLS-1$
                    } else {
                        callback.create(BadgeState.SUCCESS, getTranslation("views.publication.pdf"), filename); //$NON-NLS-1$
                    }
                }))
                .setAutoWidth(false);
        this.validationColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
                    if (data.isValidated()) {
                        callback.create(BadgeState.SUCCESS, null, getTranslation("views.validated")); //$NON-NLS-1$
                    } else {
                        callback.create(BadgeState.ERROR, null, getTranslation("views.validable")); //$NON-NLS-1$
                    }
                }))
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortProperty("validated") //$NON-NLS-1$
                .setWidth("0%"); //$NON-NLS-1$
        // Create the hover tool bar only if administrator role
        return isAdminRole();
    }

    /**
     * Replies the authors for the given publication.
     *
     * @param publication the publication to read.
     * @return the authors.
     */
    protected Component getAuthorsComponent(Publication publication) {
        final var authors = publication.getAuthors();
        final var list = new StringBuilder();
        for (final var author : authors) {
            if (author != null) {
                if (list.length() > 0) {
                    list.append(", "); //$NON-NLS-1$
                }
                list.append(author.getFullName());
            }
        }
        return new Span(list.toString());
    }

    /**
     * Replies the publishing details for the given publication.
     *
     * @param publication the publication to read.
     * @return the publishing details.
     */
    protected Component getPublishingComponent(Publication publication) {
        final var details = publication.getWherePublishedShortDescription();
        return new Span(details);
    }

    /**
     * Replies the type's label for the given publication.
     *
     * @param publication the publication to read.
     * @return the type's label.
     */
    protected Component getPublicationTypeComponent(Publication publication) {
        final var type = publication.getType();
        final var label = type.getLabel(getMessageSourceAccessor(), getLocale());
        return new Span(label);
    }

    @Override
    protected List<Column<Publication>> getInitialSortingColumns() {
        return Arrays.asList(this.yearColumn, this.typeColumn, this.titleColumn);
    }

    @Override
    protected SortDirection getInitialSortingDirection(Column<Publication> column) {
        if (column == this.yearColumn) {
            return SortDirection.DESCENDING;
        }
        return SortDirection.ASCENDING;
    }

    @Override
    protected FetchCallback<Publication, Void> getFetchCallback(AbstractFilters<Publication> filters) {
        return query -> {
            return this.dataProvider.fetch(
                    this.publicationService,
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    filters).stream();
        };
    }

    @Override
    protected void addEntity() {
        final var now = LocalDate.now();
        final var emptyPublication = this.publicationService.getPrePublicationFactory().createPrePublication(
                getSupportedPublicationTypes().findFirst().get(),
                EMPTY, EMPTY, EMPTY, now, now.getYear(), EMPTY, EMPTY,
                EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, PublicationLanguage.getDefault());
        openPublicationEditor(emptyPublication, getTranslation("views.publication.add_publication"), true, true); //$NON-NLS-1$
    }

    /**
     * Import a new entity with the given editor.
     *
     * @param editor         the editor to use for editing the publication that should be imported.
     * @param fileName       the name of the file from which the publication is imported.
     * @param saveInDatabase indicates if the editor should save the publication in the database.
     * @param refreshAll     the function to call for refreshing all the data.
     * @since 4.0
     */
    protected void importEntity(AbstractEntityEditor<Publication> editor, String fileName, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
        openPublicationEditor(editor, getTranslation("views.publication.import_publication", fileName), saveInDatabase, refreshAll); //$NON-NLS-1$
    }

    @Override
    protected void edit(Publication publication) {
        openPublicationEditor(publication, getTranslation("views.publication.edit_publication", publication.getTitle()), true, false); //$NON-NLS-1$
    }

    /**
     * Show the editor of a publication.
     *
     * @param publication    the publication to edit.
     * @param title          the title of the editor.
     * @param saveInDatabase indicates if the editor should save the publication in the database.
     * @param isCreation     indicates if the editor is opened for creating an entity or updating an exisitng entity.
     */
    protected void openPublicationEditor(Publication publication, String title, boolean saveInDatabase, boolean isCreation) {
        openPublicationEditor(publication, title, saveInDatabase, isCreation, (dialog, entity) -> {
            refreshGrid();
            MainLayout.refreshReporting();
        });
    }

    /**
     * Show the editor of a publication.
     *
     * @param publication    the publication to edit.
     * @param title          the title of the editor.
     * @param saveInDatabase indicates if the editor should save the publication in the database.
     * @param isCreation     indicates if the editor is opened for creating an entity or updating an existing entity.
     * @param refreshAll     the function to call for refreshing all the data.
     */
    protected void openPublicationEditor(Publication publication, String title, boolean saveInDatabase, boolean isCreation, SerializableBiConsumer<Dialog, Publication> refreshAll) {
        final AbstractEntityEditor<Publication> editor;
        if (isCreation) {
            final var props = getProperties();
            editor = this.publicationEditorFactory.createAdditionEditor(
                    publication,
                    getSupportedPublicationTypeArray(),
                    true,
                    props.get(PROP_PERSON_CREATION_LABEL),
                    props.get(PROP_PERSON_FIELD_LABEL),
                    props.get(PROP_PERSON_FIELD_HELPER),
                    props.get(PROP_PERSON_FIELD_NULL_ERROR),
                    props.get(PROP_PERSON_FIELD_DUPLICATE_ERROR),
                    getLogger());
        } else {
            editor = createPublicationUpdateEditor(publication);
        }
        openPublicationEditor(editor, title, saveInDatabase, refreshAll);
    }

    /**
     * Show the editor of a publication.
     *
     * @param editor         the editor to show.
     * @param title          the title of the editor.
     * @param saveInDatabase indicates if the editor should save the publication in the database.
     * @param refreshAll     the function to call for refreshing all the data.
     */
    @SuppressWarnings("static-method")
    protected void openPublicationEditor(AbstractEntityEditor<Publication> editor, String title, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
        final var newEntity = editor.isNewEntity();
        ComponentFactory.openEditionModalDialog(title, "views.check", editor, false, //$NON-NLS-1$
                saveInDatabase,
                // Refresh the "old" item, even if its has been changed in the JPA database
                refreshAll,
                newEntity ? null : refreshAll);
    }

    @Override
    protected EntityDeletingContext<Publication> createDeletionContextFor(Set<Publication> entities) {
        return this.publicationService.startDeletion(entities, getLogger());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.titleColumn.setHeader(getTranslation("views.title")); //$NON-NLS-1$
        this.authorsColumn.setHeader(getTranslation(getProperties().get(PROP_AUTHORS_COLUMN_NAME)));
        this.publishingColumn.setHeader(getTranslation("views.where_published")); //$NON-NLS-1$
        this.typeColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
        this.yearColumn.setHeader(getTranslation("views.year")); //$NON-NLS-1$
        this.fileColumn.setHeader(getTranslation("views.files")); //$NON-NLS-1$
        this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
        if (this.exportButton != null) {
            ComponentFactory.setIconItemText(this.exportButton, getTranslation("views.export")); //$NON-NLS-1$
        }
        getPublicationExporter().localeChange(event);
        if (this.importButton != null) {
            ComponentFactory.setIconItemText(this.importButton, getTranslation("views.import")); //$NON-NLS-1$
        }
        if (this.regenerateThumbnailButton != null) {
            ComponentFactory.setIconItemText(this.regenerateThumbnailButton, getTranslation("views.publications.thumbnailGenerator")); //$NON-NLS-1$
        }
        refreshGrid();
    }

    /**
     * Provider of data for publications to be displayed in the list of publications view.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    protected interface PublicationDataProvider {

        /**
         * Fetch publication data.
         *
         * @param publicationService the service to have access to the JPA.
         * @param pageRequest        the request for paging the data.
         * @param filters            the filters to apply for selecting the data.
         * @return the lazy data page.
         */
        Page<Publication> fetch(PublicationService publicationService, PageRequest pageRequest, AbstractFilters<Publication> filters);

    }

    /**
     * UI and JPA filters for {@link AbstractPublicationListView}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PublicationFilters extends AbstractAuthenticatedUserDataFilters<Publication> {

        private static final long serialVersionUID = -9105749553511198435L;
        private final ToggleButton restrictToMissingDOI;
        private Checkbox includeTitles;
        private Checkbox includeYears;

        /**
         * Constructor.
         *
         * @param user     the connected user, or {@code null} if the filter does not care about a connected user.
         * @param onSearch the callback function for running the filtering.
         */
        public PublicationFilters(AuthenticatedUser user, Runnable onSearch) {
            super(user, onSearch);

            if (user != null) {
                final var session = VaadinService.getCurrentRequest().getWrappedSession();
                final var attr = session.getAttribute(buildPreferenceSectionKeyForMissingDOIFiltering());
                var checked = false;
                if (attr != null) {
                    if (attr instanceof Boolean bvalue) {
                        checked = bvalue.booleanValue();
                    } else if (attr instanceof String svalue) {
                        checked = Boolean.parseBoolean(svalue);
                    }
                }
                this.restrictToMissingDOI = new ToggleButton(checked);
                this.restrictToMissingDOI.addValueChangeListener(it -> onMissingDOIFilteringChange(it.getValue() != null && it.getValue().booleanValue(), onSearch));
            } else {
                this.restrictToMissingDOI = null;
            }

            if (this.restrictToMissingDOI != null) {
                addComponentAtIndex(1, this.restrictToMissingDOI);
            }
        }

        private boolean isRestrictedToMissingDOI() {
            if (this.restrictToMissingDOI != null) {
                final var bvalue = this.restrictToMissingDOI.getValue();
                if (bvalue == null) {
                    return false;
                }
                return bvalue.booleanValue();
            }
            return false;
        }

        private String buildPreferenceSectionKeyForMissingDOIFiltering() {
            return ViewConstants.MISSING_DOI_FILTER;
        }

        /**
         * Invoked when the filtering on the authenticated person has changed.
         *
         * @param missingDOIFilter indicates if the filtering is enable or disable.
         * @param onSearch         the callback function for running the filtering.
         */
        protected void onMissingDOIFilteringChange(boolean missingDOIFilter, Runnable onSearch) {
            final var session0 = VaadinService.getCurrentRequest().getWrappedSession();
            session0.setAttribute(buildPreferenceSectionKeyForMissingDOIFiltering(), Boolean.valueOf(missingDOIFilter));
            if (onSearch != null) {
                onSearch.run();
            }
        }

        @Override
        protected void buildOptionsComponent(HorizontalLayout options) {
            this.includeTitles = new Checkbox(true);
            this.includeYears = new Checkbox(true);

            options.add(this.includeTitles, this.includeYears);
        }

        @Override
        protected void resetFilters() {
            this.includeTitles.setValue(Boolean.TRUE);
            this.includeYears.setValue(Boolean.TRUE);
        }

        @Override
        protected Predicate buildPredicateForAuthenticatedUser(Root<Publication> root, CriteriaQuery<?> query,
                                                               CriteriaBuilder criteriaBuilder, Person user) {
            return criteriaBuilder.equal(root.get("authorships").get("person"), user); //$NON-NLS-1$ //$NON-NLS-2$
        }

        private Predicate buildPredicateForMissingDOI(Root<Publication> root,
                                                      CriteriaBuilder criteriaBuilder, boolean missingDOI) {
            if (missingDOI) {
                return criteriaBuilder.isNull(root.get("doi")); //$NON-NLS-1$
            } else {
                return null;
            }
        }

        @Override
        public Predicate toPredicate(Root<Publication> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            final var keywordFilter = super.toPredicate(root, query, criteriaBuilder);
            if (isRestrictedToMissingDOI()) {
                final var userPredicate = buildPredicateForMissingDOI(root, criteriaBuilder, isRestrictedToMissingDOI());
                if (userPredicate != null) {
                    if (keywordFilter != null) {
                        return criteriaBuilder.and(userPredicate, keywordFilter);
                    }
                    return userPredicate;
                }
            }
            return keywordFilter;
        }

        @Override
        protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Publication> root,
                                     CriteriaBuilder criteriaBuilder) {
            if (this.includeTitles.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywords)); //$NON-NLS-1$
            }
            if (this.includeYears.getValue() == Boolean.TRUE) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.toString(root.get("publicationYear")), keywords)); //$NON-NLS-1$
            }
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            this.includeTitles.setLabel(getTranslation("views.filters.include_titles")); //$NON-NLS-1$
            this.includeYears.setLabel(getTranslation("views.filters.include_years")); //$NON-NLS-1$
            if (this.restrictToMissingDOI != null) {
                this.restrictToMissingDOI.setLabel(getTranslation("views.filters.restrictToMissingDOI")); //$NON-NLS-1$
            }
        }

    }

    /**
     * Specification that is validating publications of specific type.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private class PublicationTypeSpecification implements Specification<Publication> {

        private static final long serialVersionUID = -2002042407216268430L;

        @Override
        public Predicate toPredicate(Root<Publication> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            final var predicates = getSupportedPublicationTypes()
                    .map(it -> criteriaBuilder.equal(root.get("type"), it)) //$NON-NLS-1$
                    .toArray(size -> new Predicate[size]);
            return criteriaBuilder.or(predicates);
        }

    }

    /**
     * Export of publications.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @SuppressWarnings("synthetic-access")
    protected class PublicationExporter implements LocaleChangeObserver {

        private static final long serialVersionUID = 8907712283137690554L;

        private MenuItem exportBibTeXButton;

        private MenuItem exportRisButton;

        private MenuItem exportOdtButton;

        private MenuItem exportHtmlButton;

        private MenuItem exportHalButton;

        private MenuItem exportJsonButton;

        /**
         * Create the export buttons.
         *
         * @param exportSubMenu the receiver of the export buttons.
         */
        public void createExportButtons(SubMenu exportSubMenu) {
            final var bibTexImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
            this.exportBibTeXButton = ComponentFactory.addIconItem(exportSubMenu, bibTexImageResource, null, null, null);
            bindBibTeXExporter(this.exportBibTeXButton, null);

            final var risImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_RIS_BLACK_ICON);
            this.exportRisButton = ComponentFactory.addIconItem(exportSubMenu, risImageResource, null, null, null);
            bindRISExporter(this.exportRisButton, null);

            final var odtImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
            this.exportOdtButton = ComponentFactory.addIconItem(exportSubMenu, odtImageResource, null, null, null);
            bindODTExporter(this.exportOdtButton, null);

            final var htmlImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_HTML_BLACK_ICON);
            this.exportHtmlButton = ComponentFactory.addIconItem(exportSubMenu, htmlImageResource, null, null, null);
            bindHtmlExporter(this.exportHtmlButton, null);

            final var jsonImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_JSON_BLACK_ICON);
            this.exportJsonButton = ComponentFactory.addIconItem(exportSubMenu, jsonImageResource, null, null, null);
            bindJsonExporter(this.exportJsonButton, null);

            final var halImageResource = ComponentFactory.newStreamImage(ViewConstants.HAL_ICON);
            this.exportHalButton = ComponentFactory.addIconItem(exportSubMenu, halImageResource, null, null, null);
            this.exportHalButton.setEnabled(false);
        }

        /**
         * Create buttons in the hover menu bar.
         *
         * @param entity  the selected entity.
         * @param menuBar the receiver of the buttons.
         */
        public void createHoverMenuBar(Publication entity, MenuBar menuBar) {
            final var bibTexImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
            final var bibtexExporter = ComponentFactory.addIconItem(menuBar, bibTexImageResource, null, getTranslation("views.publication.export.bibtex"), null); //$NON-NLS-1$
            bindBibTeXExporter(bibtexExporter, entity);

            final var odtImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
            final var odtExporter = ComponentFactory.addIconItem(menuBar, odtImageResource, null, getTranslation("views.publication.export.odt"), null); //$NON-NLS-1$
            bindBibTeXExporter(odtExporter, entity);
        }

        /**
         * Replies the default configurator for exporters.
         *
         * @return the configurator.
         */
        public ExporterConfigurator createExportConfigurator() {
            final var configuration = new ExporterConfigurator(AbstractPublicationListView.this.journalService, Locale.US);
            final var person = getFilters().getUserRestrictedTo();
            if (person != null) {
                // If the filter indicates that the publications are restricted to a person, the export configurator considers this person
                final var personId = person.getId();
                configuration.selectPerson(it -> it.getId() == personId);
            }
            // The export configurator is associated to the current organization
            final var defaultOrganization = AbstractPublicationListView.this.organizationService.getDefaultOrganization();
            if (defaultOrganization != null) {
                final var organizationId = defaultOrganization.getId();
                configuration.selectOrganization(it -> it.getId() == organizationId);
            }
            return configuration;
        }

        /**
         * Notify the user that the an error was encountered during exporting action.
         *
         * @param error the error.
         */
        public void notifyExportError(Throwable error) {
            final var ui = getUI().orElseThrow();
            ui.access(() -> {
                final var message = getTranslation("views.publication.export.error", error.getLocalizedMessage()); //$NON-NLS-1$
                getLogger().error(message, error);
                ComponentFactory.showErrorNotification(message);
            });
        }

        /**
         * Extend the given item with the exporter for BibTeX.
         *
         * @param item   the component to be extended.
         * @param entity the entity to export or {@code null} if the current grid selection must be used.
         */
        public void bindBibTeXExporter(MenuItem item, Publication entity) {
            final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
            DownloadExtension.extend(item)
                    .withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
                    .withProgressTitle(getTranslation("views.publication.export.bibtex")) //$NON-NLS-1$
                    .withFilename(() -> BIBTEX_FILENAME)
                    .withMimeType(() -> BibTeXConstants.MIME_TYPE_UTF8_VALUE)
                    .withFailureListener(this::notifyExportError)
                    .withInputStreamFactory(progress -> exportBibTeX(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
        }

        /**
         * Export the given publications in a BibTeX file.
         *
         * @param publications the publications to export.
         * @param progression  the progression indicator to be used.
         * @return the input stream that contains the BibTeX data.
         */
        public InputStream exportBibTeX(Set<Publication> publications, Progression progression) {
            if (publications == null || publications.isEmpty()) {
                progression.end();
                notifyNotEntity();
                return null;
            }
            // Force the loading of all the information about each publication
            final var loadedPublications = AbstractPublicationListView.this.publicationService.loadPublicationsInMemory(publications.stream().map(it -> Long.valueOf(it.getId())).toList());
            final var configuration = createExportConfigurator();
            final var content = AbstractPublicationListView.this.publicationService.exportBibTeX(loadedPublications, configuration,
                    progression, getLogger());
            return new StringInputStream(content, Charset.defaultCharset());
        }

        /**
         * Extend the given item with the exporter for RIS.
         *
         * @param item   the component to be extended.
         * @param entity the entity to export or {@code null} if the current grid selection must be used.
         */
        public void bindRISExporter(MenuItem item, Publication entity) {
            final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_RIS_BLACK_ICON);
            DownloadExtension.extend(item)
                    .withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
                    .withProgressTitle(getTranslation("views.publication.export.ris")) //$NON-NLS-1$
                    .withFilename(() -> RIS_FILENAME)
                    .withMimeType(() -> RISConstants.MIME_TYPE_UTF8_VALUE)
                    .withFailureListener(this::notifyExportError)
                    .withInputStreamFactory(progress -> exportRIS(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
        }

        /**
         * Export the given publications in a RIS file.
         *
         * @param publications the publications to export.
         * @param progression  the progression indicator to be used.
         * @return the input stream that contains the RIS data.
         */
        public InputStream exportRIS(Set<Publication> publications, Progression progression) {
            if (publications == null || publications.isEmpty()) {
                progression.end();
                notifyNotEntity();
                return null;
            }
            // Force the loading of all the information about each publication
            final var loadedPublications = AbstractPublicationListView.this.publicationService.loadPublicationsInMemory(publications.stream().map(it -> Long.valueOf(it.getId())).toList());
            final var configuration = createExportConfigurator();
            final var content = AbstractPublicationListView.this.publicationService.exportRIS(loadedPublications, configuration,
                    progression, getLogger());
            return new StringInputStream(content, Charset.defaultCharset());
        }

        /**
         * Extend the given item with the exporter for ODT.
         *
         * @param item   the component to be extended.
         * @param entity the entity to export or {@code null} if the current grid selection must be used.
         */
        public void bindODTExporter(MenuItem item, Publication entity) {
            final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
            DownloadExtension.extend(item)
                    .withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
                    .withProgressTitle(getTranslation("views.publication.export.odt")) //$NON-NLS-1$
                    .withFilename(() -> ODT_FILENAME)
                    .withMimeType(() -> OpenDocumentConstants.ODT_MIME_TYPE_VALUE)
                    .withFailureListener(this::notifyExportError)
                    .withInputStreamFactory(progress -> exportODT(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
        }

        /**
         * Export the given publications in a ODT file.
         *
         * @param publications the publications to export.
         * @param progression  the progression indicator to be used.
         * @return the input stream that contains the ODT data.
         * @throws Exception if the ODT data cannot be generated.
         */
        public InputStream exportODT(Set<Publication> publications, Progression progression) throws Exception {
            if (publications == null || publications.isEmpty()) {
                progression.end();
                notifyNotEntity();
                return null;
            }
            // Force the loading of all the information about each publication
            final var loadedPublications = AbstractPublicationListView.this.publicationService.loadPublicationsInMemory(publications.stream().map(it -> Long.valueOf(it.getId())).toList());
            final var configuration = createExportConfigurator();
            final var content = AbstractPublicationListView.this.publicationService.exportOdt(loadedPublications, configuration,
                    progression, getLogger());
            return new ByteArrayInputStream(content);
        }

        /**
         * Extend the given item with the exporter for HTML.
         *
         * @param item   the component to be extended.
         * @param entity the entity to export or {@code null} if the current grid selection must be used.
         */
        public void bindHtmlExporter(MenuItem item, Publication entity) {
            final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_HTML_BLACK_ICON);
            DownloadExtension.extend(item)
                    .withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
                    .withProgressTitle(getTranslation("views.publication.export.html")) //$NON-NLS-1$
                    .withFilename(() -> HTML_FILENAME)
                    .withMimeType(() -> "text/html") //$NON-NLS-1$
                    .withFailureListener(this::notifyExportError)
                    .withInputStreamFactory(progress -> exportHTML(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
        }

        /**
         * Export the given publications in an HTML file.
         *
         * @param publications the publications to export.
         * @param progression  the progression indicator to be used.
         * @return the input stream that contains the HTML data.
         * @throws Exception if the HTML data cannot be generated.
         */
        public InputStream exportHTML(Set<Publication> publications, Progression progression) throws Exception {
            if (publications == null || publications.isEmpty()) {
                progression.end();
                notifyNotEntity();
                return null;
            }
            // Force the loading of all the information about each publication
            final var loadedPublications = AbstractPublicationListView.this.publicationService.loadPublicationsInMemory(publications.stream().map(it -> Long.valueOf(it.getId())).toList());
            final var configuration = createExportConfigurator();
            final var content = AbstractPublicationListView.this.publicationService.exportHtml(loadedPublications, configuration,
                    progression, getLogger());
            return new StringInputStream(content, Charset.defaultCharset());
        }

        /**
         * Extend the given item with the exporter for JSON.
         *
         * @param item   the component to be extended.
         * @param entity the entity to export or {@code null} if the current grid selection must be used.
         */
        public void bindJsonExporter(MenuItem item, Publication entity) {
            final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_JSON_BLACK_ICON);
            DownloadExtension.extend(item)
                    .withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
                    .withProgressTitle(getTranslation("views.publication.export.json")) //$NON-NLS-1$
                    .withFilename(() -> JSON_FILENAME)
                    .withMimeType(() -> IoConstants.JSON_MIME)
                    .withFailureListener(this::notifyExportError)
                    .withInputStreamFactory(progress -> exportJSON(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
        }

        /**
         * Export the given publications in a JSON file.
         *
         * @param publications the publications to export.
         * @param progression  the progression indicator to be used.
         * @return the input stream that contains the JSON data.
         * @throws Exception if the JSON data cannot be generated.
         */
        public InputStream exportJSON(Set<Publication> publications, Progression progression) throws Exception {
            if (publications == null || publications.isEmpty()) {
                notifyNotEntity();
                return null;
            }
            // Force the loading of all the information about each publication
            final var loadedPublications = AbstractPublicationListView.this.publicationService.loadPublicationsInMemory(publications.stream().map(it -> Long.valueOf(it.getId())).toList());
            final var configuration = createExportConfigurator();
            final var content = AbstractPublicationListView.this.publicationService.exportJson(loadedPublications, configuration,
                    progression, getLogger());
            return new StringInputStream(content, Charset.defaultCharset());
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            if (this.exportBibTeXButton != null) {
                ComponentFactory.setIconItemText(this.exportBibTeXButton, getTranslation("views.publication.export.bibtex")); //$NON-NLS-1$
            }
            if (this.exportRisButton != null) {
                ComponentFactory.setIconItemText(this.exportRisButton, getTranslation("views.publication.export.ris")); //$NON-NLS-1$
            }
            if (this.exportOdtButton != null) {
                ComponentFactory.setIconItemText(this.exportOdtButton, getTranslation("views.publication.export.odt")); //$NON-NLS-1$
            }
            if (this.exportHtmlButton != null) {
                ComponentFactory.setIconItemText(this.exportHtmlButton, getTranslation("views.publication.export.html")); //$NON-NLS-1$
            }
            if (this.exportHalButton != null) {
                ComponentFactory.setIconItemText(this.exportHalButton, getTranslation("views.publication.export.hal")); //$NON-NLS-1$
            }
            if (this.exportJsonButton != null) {
                ComponentFactory.setIconItemText(this.exportJsonButton, getTranslation("views.publication.export.json")); //$NON-NLS-1$
            }
        }

    }

}
