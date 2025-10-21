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

package fr.utbm.ciad.labmanager.views.components.publications.editors.regular;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.publication.*;
import fr.utbm.ciad.labmanager.data.publication.type.*;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringToDoiConverter;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringToKeywordsConverter;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.pdf.ServerSideUploadablePdfField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.*;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
import fr.utbm.ciad.labmanager.views.components.conferences.fields.ConferenceFieldFactory;
import fr.utbm.ciad.labmanager.views.components.conferences.fields.SingleConferenceNameField;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import fr.utbm.ciad.labmanager.views.components.journals.fields.JournalFieldFactory;
import fr.utbm.ciad.labmanager.views.components.journals.fields.SingleJournalNameField;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.MultiPersonNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

/**
 * Abstract implementation for the editor of the information related to a publication whatever the type of publication.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractPublicationEditor extends AbstractEntityEditor<Publication> {

    /**
     * Key for the property that is the label for person creation.
     */
    public static final String PROP_PERSON_CREATION = "personCreationLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the label for person field.
     */
    public static final String PROP_PERSON_FIELD_LABEL = "personFieldLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the helper for person field.
     */
    public static final String PROP_PERSON_FIELD_HELPER = "personFieldHelperLabelKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the null error for person field.
     */
    public static final String PROP_PERSON_FIELD_NULL_ERROR = "personNullErrorKey"; //$NON-NLS-1$
    /**
     * Key for the property that is the duplicate error for person field.
     */
    public static final String PROP_PERSON_FIELD_DUPLICATE_ERROR = "personDuplicateErrorKey"; //$NON-NLS-1$
    private static final long serialVersionUID = 6119323451118628960L;
    protected final boolean enableTypeSelector;

    protected final boolean mandatoryAbstractText;
    protected final DownloadableFileManager fileManager;
    protected final PersonService personService;
    protected final PersonEditorFactory personEditorFactory;
    protected final PersonFieldFactory personFieldFactory;
    protected final JournalService journalService;
    protected final JournalEditorFactory journalEditorFactory;
    protected final JournalFieldFactory journalFieldFactory;
    protected final ConferenceService conferenceService;
    protected final ConferenceEditorFactory conferenceEditorFactory;
    protected final ConferenceFieldFactory conferenceFieldFactory;
    protected final PublicationService publicationService;
    protected final UserService userService;
    protected final ScientificAxisService axisService;
    protected final ScientificAxisEditorFactory axisEditorFactory;
    protected final PublicationFieldBuilder fieldBuilder;
    private final boolean openDoiDetails;
    protected PublicationType[] supportedTypes;

    protected DetailsWithErrorMark generalDetails;

    protected FormLayout generalLayout;

    protected ComboBox<PublicationType> type;

    protected TextField title;

    protected MultiPersonNameField authors;

    protected DatePicker publicationDate;

    protected DetailsWithErrorMark identificationDetails;

    protected FormLayout identificationLayout;

    protected TextField doi;

    protected TextField halId;

    protected TextField dblpUrl;

    protected TextField isbn;

    protected TextField issn;

    protected DetailsWithErrorMark contentDetails;

    protected MarkdownField abstractText;

    protected TextField keywords;

    protected ComboBox<PublicationLanguage> language;

    protected DetailsWithErrorMark resourcesDetails;

    protected ServerSideUploadablePdfField uploadPdf;

    protected TextField videoUrl;

    protected ServerSideUploadablePdfField uploadAward;

    protected TextField extraUrl;

    protected DetailsWithErrorMark referencesDetails;

    protected ComboListField<ScientificAxis> scientificAxes;

    protected ToggleButton manualValidationForced;

    /**
     * Constructor.
     *
     * @param context                           the context for editing the entity.
     * @param initialPublicationStatus          the initial status of the publication.
     * @param supportedTypes                    list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param relinkEntityWhenSaving            indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                          be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param enableTypeSelector                indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText             indicates if the abstract text is considered as mandatory or not.
     * @param publicationCreationStatusComputer the tool for computer the creation status for the publication.
     * @param fileManager                       the manager of files at the server-side.
     * @param publicationService                the service for accessing the JPA entities for publications.
     * @param personService                     the service for accessing the JPA entities for persons.
     * @param personEditorFactory               the factory for creating the person editors.
     * @param personFieldFactory                the factory for creating the person fields.
     * @param userService                       the service for accessing the JPA entities for users.
     * @param journalService                    the service for accessing the JPA entities for journal.
     * @param journalEditorFactory              the factory for creating journal  editors.
     * @param journalFieldFactory               the factory for creating journal fields.
     * @param conferenceService                 the service for accessing the JPA entities for conference.
     * @param conferenceEditorFactory           the factory for creating the conference editors.
     * @param conferenceFieldFactory            the factory for creating the conference fields.
     * @param axisService                       service for accessing to the JPA entities of scientific axes.
     * @param axisEditorFactory                 the factory for creating scientific axis editors.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     * @param properties                        specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractPublicationEditor(EntityEditingContext<Publication> context,
                                     EntityCreationStatus initialPublicationStatus,
                                     PublicationType[] supportedTypes,
                                     boolean relinkEntityWhenSaving, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                     EntityCreationStatusComputer<Publication> publicationCreationStatusComputer,
                                     DownloadableFileManager fileManager, PublicationService publicationService,
                                     PersonService personService, PersonEditorFactory personEditorFactory, PersonFieldFactory personFieldFactory,
                                     UserService userService, JournalService journalService, JournalEditorFactory journalEditorFactory, JournalFieldFactory journalFieldFactory,
                                     ConferenceService conferenceService, ConferenceEditorFactory conferenceEditorFactory, ConferenceFieldFactory conferenceFieldFactory, ScientificAxisService axisService,
                                     ScientificAxisEditorFactory axisEditorFactory, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                     ConstructionPropertiesBuilder properties) {
        super(Publication.class, authenticatedUser, messages,
                publicationCreationStatusComputer, context, initialPublicationStatus, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.publication.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.publication.administration.validated_publication")); //$NON-NLS-1$
        this.supportedTypes = supportedTypes;
        // Sort types by their natural order, that corresponds to the weight of the type
        Arrays.sort(this.supportedTypes, (a, b) -> Integer.compare(a.ordinal(), b.ordinal()));
        this.enableTypeSelector = enableTypeSelector;
        this.mandatoryAbstractText = mandatoryAbstractText;
        this.publicationService = publicationService;
        this.fileManager = fileManager;
        this.personService = personService;
        this.personEditorFactory = personEditorFactory;
        this.personFieldFactory = personFieldFactory;
        this.userService = userService;
        this.journalService = journalService;
        this.journalEditorFactory = journalEditorFactory;
        this.journalFieldFactory = journalFieldFactory;
        this.conferenceService = conferenceService;
        this.conferenceEditorFactory = conferenceEditorFactory;
        this.conferenceFieldFactory = conferenceFieldFactory;
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;

        // Special case when the publication is a PrePublication (or fake publication)
        final var currentEntity = getEditingContext().getEntity();
        if (currentEntity.isFakeEntity()) {
            assert this.supportedTypes.length > 0;
            final var type = this.supportedTypes[0];
            final var newPublication = this.publicationService.transform(currentEntity, type);
            getEditingContext().setEntity(newPublication);
        }

        // Must be the latest initialization for ensuring that all the class's fields are initialized before creating the dynamic field builder
        this.fieldBuilder = createDynamicFieldBuilder();

        final var session = VaadinService.getCurrentRequest().getWrappedSession();
        final String key = ViewConstants.OPEN_DEFAULT_DOI_DETAILS;
        final var attr = session.getAttribute(key);
        boolean b = false;
        if (attr != null) {
            if (attr instanceof Boolean bvalue) {
                b = bvalue.booleanValue();
            } else if (attr instanceof String svalue) {
                b = Boolean.parseBoolean(svalue);
            }
            session.removeAttribute(key);
        }
        this.openDoiDetails = b;
    }

    /**
     * Create the instance of the dynamic field builder.
     *
     * @return the dynamic field builder.
     */
    protected PublicationFieldBuilder createDynamicFieldBuilder() {
        return new PublicationFieldBuilder();
    }

    /**
     * Update the form field according to the type of publication.
     *
     * @param oldType     the previous type. It may be {@code null}.
     * @param currentType the type that is currently selected. It is never {@code null}.
     */
    protected void updateFormContent(PublicationType oldType, PublicationType currentType) {
        assert oldType != null;
        assert currentType != null;

        if (oldType.isCompatibleWith(currentType)) {
            // No need to change the fields
            getEditedEntity().setType(currentType);
            return;
        }

        final var entity = getEditedEntity();

        if (entity.getType().isCompatibleWith(currentType)) {
            // No need to change the fields
            getEditedEntity().setType(currentType);
            return;
        }

        unlinkBeans(true);
        final var newPublication = this.publicationService.transform(entity, currentType);
        getEditingContext().setEntity(newPublication);
        this.fieldBuilder.updateGeneralDetails(newPublication);
        this.fieldBuilder.updateIdentificationDetails(newPublication);
        this.fieldBuilder.localeChange();
        linkBeans();
        getEntityDataBinder().validate();
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createTypeSelector(rootContainer);
        createGeneralDetails(rootContainer);
        createIdentificationDetails(rootContainer);
        createContentDetails(rootContainer);
        createResourceDetails(rootContainer);
        createReferenceDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer,
                    content -> {
                        this.manualValidationForced = new ToggleButton();
                        content.add(this.manualValidationForced, 2);
                        getEntityDataBinder().forField(this.manualValidationForced)
                                .bind(Publication::getManualValidationForced, Publication::setManualValidationForced);
                    },
                    it -> it.bind(Publication::isValidated, Publication::setValidated));
        }

        // initialize the form with the fields that corresponds to the publication type
        final var entity = getEditedEntity();
        this.fieldBuilder.updateGeneralDetails(entity);
        this.fieldBuilder.updateIdentificationDetails(entity);

        // Dynamically update the form fields according to the publication type
        this.type.addValueChangeListener(it -> {
            // Type may be null when the type of publication is changed
            // It is a temporary state that should be ignored
            final var newType = it.getValue();
            if (newType != null) {
                updateFormContent(it.getOldValue(), newType);
            }
        });
    }

    /**
     * Create the publication type selector at the top of the form.
     *
     * @param rootContainer the container.
     */
    protected void createTypeSelector(VerticalLayout rootContainer) {
        this.type = new ComboBox<>();
        this.type.setRequired(true);
        this.type.setPrefixComponent(VaadinIcon.COMBOBOX.create());
        this.type.setItems(this.supportedTypes);
        this.type.setItemLabelGenerator(this::getTypeLabel);
        this.type.setWidthFull();
        rootContainer.add(this.type);

        this.type.setEnabled(this.enableTypeSelector);

        // Update the initial value if the type selector when the editor is attached.
        this.type.setValue(getEditedEntity().getType());
    }

    private String getTypeLabel(PublicationType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the general details.
     *
     * @param rootContainer the container.
     */
    protected void createGeneralDetails(VerticalLayout rootContainer) {
        this.generalLayout = ComponentFactory.newColumnForm(2);

        this.title = new TextField();
        this.title.setPrefixComponent(VaadinIcon.HASH.create());
        this.title.setRequired(true);
        this.title.setClearButtonVisible(true);
        this.generalLayout.add(this.title, 2);

        final var props = getProperties();

        this.authors = this.personFieldFactory.createMultiNameField(getTranslation(props.get(PROP_PERSON_CREATION)), getLogger(),
                // Force the loading of the JPA entity's components
                it -> {
                    // Loading the authorships is needed when the person is added as author.
                    Hibernate.initialize(it.getAuthorships());
                });
        this.authors.setRequiredIndicatorVisible(true);
        this.authors.setPrefixComponent(VaadinIcon.USERS.create());
        this.generalLayout.add(this.authors, 2);

        this.publicationDate = new DatePicker();
        this.publicationDate.setRequiredIndicatorVisible(true);
        this.publicationDate.setPrefixComponent(VaadinIcon.DATE_INPUT.create());
        this.publicationDate.setClearButtonVisible(true);
        this.generalLayout.add(this.publicationDate, 1);

        this.generalDetails = createDetailsWithErrorMark(rootContainer, this.generalLayout, "general", false, this.openDoiDetails); //$NON-NLS-1$

        getEntityDataBinder().forField(this.title)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.title, this.generalDetails))
                .bind(Publication::getTitle, Publication::setTitle);
        getEntityDataBinder().forField(this.authors)
                .withValidator(new DisjointEntityIterableValidator<>(
                        getTranslation(props.get(PROP_PERSON_FIELD_NULL_ERROR)),
                        getTranslation(props.get(PROP_PERSON_FIELD_DUPLICATE_ERROR)),
                        true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.authors, this.generalDetails))
                .bind(Publication::getAuthors, Publication::setTemporaryAuthors);
        getEntityDataBinder().forField(this.publicationDate)
                .withValidator(new NotNullDateValidator(getTranslation("views.publication.date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.publicationDate, this.generalDetails))
                .bind(this::getPublicationDate, Publication::setPublicationDate);
    }

    private LocalDate getPublicationDate(Publication publication) {
        var date = publication.getPublicationDate();
        if (date == null) {
            date = LocalDate.of(publication.getPublicationYear(), 12, 31);
        }
        return date;
    }

    /**
     * Create the section for editing the identification details.
     *
     * @param rootContainer the container.
     */
    protected void createIdentificationDetails(VerticalLayout rootContainer) {
        this.identificationLayout = ComponentFactory.newColumnForm(2);

        this.doi = ComponentFactory.newClickableIconTextField(DOI_BASE_URL, DOI_ICON);
        this.doi.setPrefixComponent(VaadinIcon.HASH.create());
        this.doi.setClearButtonVisible(true);
        this.identificationLayout.add(this.doi, 2);

        this.halId = ComponentFactory.newClickableIconTextField(HAL_BASE_URL, HAL_ICON);
        this.halId.setPrefixComponent(VaadinIcon.HASH.create());
        this.halId.setClearButtonVisible(true);
        this.identificationLayout.add(this.halId, 2);

        this.dblpUrl = ComponentFactory.newClickableIconTextField(DBLP_BASE_URL, DBLP_ICON);
        this.dblpUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        this.dblpUrl.setClearButtonVisible(true);
        this.identificationLayout.add(this.dblpUrl, 2);

        if (isIsbnIssnEnabled()) {
            this.issn = new TextField();
            this.issn.setPrefixComponent(VaadinIcon.HASH.create());
            this.issn.setClearButtonVisible(true);
            this.identificationLayout.add(this.issn, 1);

            this.isbn = new TextField();
            this.isbn.setPrefixComponent(VaadinIcon.HASH.create());
            this.isbn.setClearButtonVisible(true);
            this.identificationLayout.add(this.isbn, 1);
        } else {
            this.issn = null;
            this.isbn = null;
        }

        this.identificationDetails = createDetailsWithErrorMark(rootContainer, this.identificationLayout, "identification", this.openDoiDetails, this.openDoiDetails); //$NON-NLS-1$

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.doi)
                .withConverter(new StringToDoiConverter())
                .withValidator(new DoiValidator(getTranslation("views.publication.doi.error"), null, true)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.doi, this.identificationDetails))
                .bind(Publication::getDOI, Publication::setDOI);
        getEntityDataBinder().forField(this.halId)
                .withConverter(new StringTrimer())
                .withValidator(new HalIdValidator(getTranslation("views.publication.halId.error"), null, true)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.halId, this.identificationDetails))
                .bind(Publication::getHalId, Publication::setHalId);
        getEntityDataBinder().forField(this.dblpUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.dblpUrl, this.identificationDetails))
                .bind(Publication::getDblpURL, Publication::setDblpURL);
        if (this.issn != null) {
            getEntityDataBinder().forField(this.issn)
                    .withConverter(new StringTrimer())
                    .withValidator(new IssnValidator(getTranslation("views.publication.issn.error"), true)) //$NON-NLS-1$
                    .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.issn, this.identificationDetails))
                    .bind(Publication::getISSN, Publication::setISSN);
        }
        if (this.isbn != null) {
            getEntityDataBinder().forField(this.isbn)
                    .withConverter(new StringTrimer())
                    .withValidator(new IsbnValidator(getTranslation("views.publication.isbn.error"), true)) //$NON-NLS-1$
                    .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.isbn, this.identificationDetails))
                    .bind(Publication::getISBN, Publication::setISBN);
        }
    }

    /**
     * Replies if the ISBN and ISSN fields are activited for the current publication.
     *
     * @return {@code true} if the two fields are activited.
     */
    protected boolean isIsbnIssnEnabled() {
        final var entity = getEditedEntity();
        return !(entity instanceof JournalBasedPublication || entity instanceof ConferenceBasedPublication);
    }

    /**
     * Create the section for editing the content details.
     *
     * @param rootContainer the container.
     */
    protected void createContentDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.abstractText = new MarkdownField();
        this.abstractText.setRequiredIndicatorVisible(true);
        content.add(this.abstractText, 2);

        this.keywords = new TextField();
        this.keywords.setPrefixComponent(VaadinIcon.KEY_O.create());
        this.keywords.setClearButtonVisible(true);
        content.add(this.keywords, 2);

        this.language = new ComboBox<>();
        this.language.setItems(PublicationLanguage.values());
        this.language.setItemLabelGenerator(this::getLanguageLabel);
        this.language.setPrefixComponent(VaadinIcon.TEXT_LABEL.create());
        this.language.setValue(PublicationLanguage.getDefault());
        content.add(this.language, 1);

        this.contentDetails = createDetailsWithErrorMark(rootContainer, content, "content", false, this.openDoiDetails); //$NON-NLS-1$

        final var abstractFieldBinder = getEntityDataBinder().forField(this.abstractText)
                .withConverter(new StringTrimer());
        if (this.mandatoryAbstractText) {
            abstractFieldBinder.withValidator(new NotEmptyStringValidator(getTranslation("views.publication.abstract_text.error"))) //$NON-NLS-1$
                    .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.abstractText, this.contentDetails));
        }
        abstractFieldBinder.bind(Publication::getAbstractText, Publication::setAbstractText);
        getEntityDataBinder().forField(this.keywords)
                .withConverter(new StringToKeywordsConverter())
                .bind(Publication::getKeywords, Publication::setKeywords);
        getEntityDataBinder().forField(this.language).bind(Publication::getMajorLanguage, Publication::setMajorLanguage);
    }

    private String getLanguageLabel(PublicationLanguage language) {
        return language.getLabel(getLocale());
    }

    /**
     * Create the section for editing the resource details.
     *
     * @param rootContainer the container.
     */
    protected void createResourceDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.uploadPdf = new ServerSideUploadablePdfField(this.fileManager,
                ext -> this.fileManager.makePdfFilename(getEditedEntity().getId()),
                () -> getLogger());
        this.uploadPdf.setClearButtonVisible(true);
        content.add(this.uploadPdf, 2);

        this.videoUrl = new TextField();
        this.videoUrl.setPrefixComponent(VaadinIcon.MOVIE.create());
        content.add(this.videoUrl, 1);

        this.uploadAward = new ServerSideUploadablePdfField(this.fileManager,
                ext -> this.fileManager.makeAwardFilename(getEditedEntity().getId()),
                () -> getLogger());
        this.uploadAward.setClearButtonVisible(true);
        content.add(this.uploadAward, 2);

        this.extraUrl = new TextField();
        this.extraUrl.setPrefixComponent(VaadinIcon.INFO_CIRCLE.create());
        content.add(this.extraUrl, 1);

        this.resourcesDetails = createDetailsWithErrorMark(rootContainer, content, "resources", false, this.openDoiDetails); //$NON-NLS-1$

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.uploadPdf)
                .withConverter(new StringTrimer())
                .bind(Publication::getPathToDownloadablePDF, Publication::setPathToDownloadablePDF);
        getEntityDataBinder().forField(this.videoUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.videoUrl, this.resourcesDetails))
                .bind(Publication::getVideoURL, Publication::setVideoURL);
        getEntityDataBinder().forField(this.uploadAward)
                .withConverter(new StringTrimer())
                .bind(Publication::getPathToDownloadableAwardCertificate, Publication::setPathToDownloadableAwardCertificate);
        getEntityDataBinder().forField(this.extraUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.extraUrl, this.resourcesDetails))
                .bind(Publication::getExtraURL, Publication::setExtraURL);
    }

    /**
     * Create the section for editing the reference details.
     *
     * @param rootContainer the container.
     */
    protected void createReferenceDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.scientificAxes = new ComboListField<>(ComponentFactory.toSerializableComparator(new ScientificAxisComparator()), this::openScientificAxisEditor);
        this.scientificAxes.setEntityRenderers(
                it -> it.getAcronymAndName(),
                new ComponentRenderer<>(this::createScientificAxisNameComponent),
                new ComponentRenderer<>(this::createScientificAxisNameComponent));
        this.scientificAxes.setAvailableEntities(query -> {
            return this.axisService.getAllScientificAxes(
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    null).stream();
        });
        content.add(this.scientificAxes, 2);

        this.referencesDetails = createDetailsWithErrorMark(rootContainer, content, "references", false, this.openDoiDetails); //$NON-NLS-1$

        getEntityDataBinder().forField(this.scientificAxes).bind(Publication::getScientificAxes, Publication::setScientificAxes);
    }

    private Component createScientificAxisNameComponent(ScientificAxis axis) {
        return new Span(axis.getAcronymAndName());
    }

    /**
     * Invoked for creating a new scientific axis.
     *
     * @param saver the callback that is invoked when the scientific axis is saved as JPA entity.
     */
    protected void openScientificAxisEditor(Consumer<ScientificAxis> saver) {
        final var newAxis = new ScientificAxis();
        final var editor = this.axisEditorFactory.createAdditionEditor(newAxis, getLogger());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.membership.scientific_axes.create"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    @Override
    protected void doSave() throws Exception {
        getEditingContext().save(this.uploadPdf, this.uploadAward);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.publication.save_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.publication.validation_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.publication.save_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.publication.validation_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.publication.delete_success2", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.publication.delete_error2", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        final var props = getProperties();

        if (this.generalDetails != null) {
            this.generalDetails.setSummaryText(getTranslation("views.publication.general_informations")); //$NON-NLS-1$
        }
        this.type.setLabel(getTranslation("views.publication.type")); //$NON-NLS-1$
        this.type.setHelperText(getTranslation("views.publication.type.helper")); //$NON-NLS-1$);
        this.type.setItemLabelGenerator(this::getTypeLabel);
        this.title.setLabel(getTranslation("views.publication.title")); //$NON-NLS-1$
        this.authors.setLabel(getTranslation(props.get(PROP_PERSON_FIELD_LABEL)));
        this.authors.setHelperText(getTranslation(props.get(PROP_PERSON_FIELD_HELPER)));
        this.publicationDate.setLocale(event.getLocale());
        this.publicationDate.setLabel(getTranslation("views.publication.date")); //$NON-NLS-1$
        this.publicationDate.setHelperText(getTranslation("views.publication.date.helper")); //$NON-NLS-1$

        if (this.identificationDetails != null) {
            this.identificationDetails.setSummaryText(getTranslation("views.publication.identification_informations")); //$NON-NLS-1$
        }
        this.doi.setLabel(getTranslation("views.publication.doi")); //$NON-NLS-1$
        this.doi.setHelperText(getTranslation("views.publication.doi.helper")); //$NON-NLS-1$
        this.halId.setLabel(getTranslation("views.publication.halId")); //$NON-NLS-1$
        this.halId.setHelperText(getTranslation("views.publication.halId.helper")); //$NON-NLS-1$
        this.dblpUrl.setLabel(getTranslation("views.publication.dblp")); //$NON-NLS-1$
        if (this.issn != null) {
            this.issn.setLabel(getTranslation("views.publication.issn")); //$NON-NLS-1$
        }
        if (this.isbn != null) {
            this.isbn.setLabel(getTranslation("views.publication.isbn")); //$NON-NLS-1$
        }

        if (this.contentDetails != null) {
            this.contentDetails.setSummaryText(getTranslation("views.publication.content_informations")); //$NON-NLS-1$
        }
        this.abstractText.setLabel(getTranslation("views.publication.abstract_text")); //$NON-NLS-1$
        this.abstractText.setHelperText(getTranslation("views.publication.abstract_text.helper")); //$NON-NLS-1$
        this.keywords.setLabel(getTranslation("views.publication.keywords")); //$NON-NLS-1$
        this.keywords.setHelperText(getTranslation("views.publication.keywords.helper")); //$NON-NLS-1$
        this.language.setLabel(getTranslation("views.publication.major_language")); //$NON-NLS-1$
        this.language.setHelperText(getTranslation("views.publication.major_language.helper")); //$NON-NLS-1$);
        this.language.setItemLabelGenerator(this::getLanguageLabel);

        if (this.resourcesDetails != null) {
            this.resourcesDetails.setSummaryText(getTranslation("views.publication.resources_informations")); //$NON-NLS-1$
        }
        this.uploadPdf.setLabel(getTranslation("views.publication.uploaded_pdf")); //$NON-NLS-1$
        this.videoUrl.setLabel(getTranslation("views.publication.video_url")); //$NON-NLS-1$
        this.uploadAward.setLabel(getTranslation("views.publication.uploaded_award")); //$NON-NLS-1$
        this.extraUrl.setLabel(getTranslation("views.publication.extra_url")); //$NON-NLS-1$

        if (this.referencesDetails != null) {
            this.referencesDetails.setSummaryText(getTranslation("views.publication.references_informations")); //$NON-NLS-1$
        }
        this.scientificAxes.setLabel(getTranslation("views.publication.scientific_axes")); //$NON-NLS-1$
        this.scientificAxes.setAdditionTooltip(getTranslation("views.publication.scientific_axes.insert")); //$NON-NLS-1$
        this.scientificAxes.setDeletionTooltip(getTranslation("views.publication.scientific_axes.delete")); //$NON-NLS-1$
        this.scientificAxes.setCreationTooltip(getTranslation("views.publication.scientific_axes.create")); //$NON-NLS-1$

        if (this.manualValidationForced != null) {
            this.manualValidationForced.setLabel(getTranslation("views.publication.manual_validation_forced")); //$NON-NLS-1$
        }

        this.fieldBuilder.localeChange(event);
    }

    /**
     * Implementation of a builder for dynamic edition fields.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @see AbstractPublicationEditor
     * @since 4.0
     */
    protected class PublicationFieldBuilder implements LocaleChangeObserver {

        private static final long serialVersionUID = 7150863461477794371L;

        private SingleJournalNameField journal;

        private SingleConferenceNameField conference;

        private IntegerField conferenceOccurrenceNumber;

        private TextField chapterNumber;

        private TextField bookTitle;

        private TextField howPublished;

        private TextField institution;

        private TextField documentNumber;

        private TextField documentType;

        private TextField patentNumber;

        private TextField patentType;

        private TextField reportNumber;

        private TextField reportType;

        private TextField publisher;

        private TextField organization;

        private TextField address;

        private TextField series;

        private TextField edition;

        private TextField volume;

        private TextField number;

        private TextField pages;

        private TextField editors;

        /**
         * Constructor.
         */
        public PublicationFieldBuilder() {
            //
        }

        private static <ST extends Publication, V> ValueProvider<Publication, V> getter(Class<ST> type, ValueProvider<ST, V> getter) {
            return it -> getter.apply(type.cast(it));
        }

        private static <ST extends Publication, V> Setter<Publication, V> setter(Class<ST> type, Setter<ST, V> setter) {
            return (entity, value) -> setter.accept(type.cast(entity), value);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            localeChange();
        }

        /**
         * Change the localized text according to the given locale
         */
        public void localeChange() {
            if (this.journal != null) {
                this.journal.setLabel(getTranslation("views.publication.journal")); //$NON-NLS-1$
                this.journal.setHelperText(getTranslation("views.publication.journal.helper")); //$NON-NLS-1$
            }
            if (this.conference != null) {
                this.conference.setLabel(getTranslation("views.publication.conference")); //$NON-NLS-1$
                this.conference.setHelperText(getTranslation("views.publication.conference.helper")); //$NON-NLS-1$
            }
            if (this.conferenceOccurrenceNumber != null) {
                this.conferenceOccurrenceNumber.setLabel(getTranslation("views.publication.conference.occurrence_number")); //$NON-NLS-1$
                this.conferenceOccurrenceNumber.setHelperText(getTranslation("views.publication.conference.occurrence_number.helper")); //$NON-NLS-1$
            }
            if (this.publisher != null) {
                this.publisher.setLabel(getTranslation("views.publication.book.publisher")); //$NON-NLS-1$
            }
            if (this.chapterNumber != null) {
                this.chapterNumber.setLabel(getTranslation("views.publication.book_chapter.number")); //$NON-NLS-1$
                this.chapterNumber.setHelperText(getTranslation("views.publication.book_chapter.number.helper")); //$NON-NLS-1$
            }
            if (this.bookTitle != null) {
                this.bookTitle.setLabel(getTranslation("views.publication.book_chapter.book_title")); //$NON-NLS-1$
                this.bookTitle.setHelperText(getTranslation("views.publication.book_chapter.book_title.helper")); //$NON-NLS-1$
            }
            if (this.howPublished != null) {
                this.howPublished.setLabel(getTranslation("views.publication.misc_document.how_published")); //$NON-NLS-1$
                this.howPublished.setHelperText(getTranslation("views.publication.misc_document.how_published.helper")); //$NON-NLS-1$
            }
            if (this.documentNumber != null) {
                this.documentNumber.setLabel(getTranslation("views.publication.misc_document.number")); //$NON-NLS-1$
                this.documentNumber.setHelperText(getTranslation("views.publication.misc_document.number.helper")); //$NON-NLS-1$
            }
            if (this.documentType != null) {
                this.documentType.setLabel(getTranslation("views.publication.misc_document.type")); //$NON-NLS-1$
                this.documentType.setHelperText(getTranslation("views.publication.misc_document.type.helper")); //$NON-NLS-1$
            }
            if (this.institution != null) {
                this.institution.setLabel(getTranslation("views.publication.all.institution")); //$NON-NLS-1$
                this.institution.setHelperText(getTranslation("views.publication.all.institution.helper")); //$NON-NLS-1$
            }
            if (this.patentNumber != null) {
                this.patentNumber.setLabel(getTranslation("views.publication.patent.number")); //$NON-NLS-1$
                this.patentNumber.setHelperText(getTranslation("views.publication.patent.number.helper")); //$NON-NLS-1$
            }
            if (this.patentType != null) {
                this.patentType.setLabel(getTranslation("views.publication.patent.type")); //$NON-NLS-1$
                this.patentType.setHelperText(getTranslation("views.publication.patent.type.helper")); //$NON-NLS-1$
            }
            if (this.reportNumber != null) {
                this.reportNumber.setLabel(getTranslation("views.publication.report.number")); //$NON-NLS-1$
                this.reportNumber.setHelperText(getTranslation("views.publication.report.number.helper")); //$NON-NLS-1$
            }
            if (this.reportType != null) {
                this.reportType.setLabel(getTranslation("views.publication.report.type")); //$NON-NLS-1$
                this.reportType.setHelperText(getTranslation("views.publication.report.type.helper")); //$NON-NLS-1$
            }
            if (this.edition != null) {
                this.edition.setLabel(getTranslation("views.publication.all.edition")); //$NON-NLS-1$
            }
            if (this.series != null) {
                this.series.setLabel(getTranslation("views.publication.all.series")); //$NON-NLS-1$
            }
            if (this.volume != null) {
                this.volume.setLabel(getTranslation("views.publication.all.volume")); //$NON-NLS-1$
            }
            if (this.number != null) {
                this.number.setLabel(getTranslation("views.publication.all.number")); //$NON-NLS-1$
            }
            if (this.pages != null) {
                this.pages.setLabel(getTranslation("views.publication.all.pages")); //$NON-NLS-1$
            }
            if (this.organization != null) {
                this.organization.setLabel(getTranslation("views.publication.all.organization")); //$NON-NLS-1$
            }
            if (this.address != null) {
                this.address.setLabel(getTranslation("views.publication.all.address")); //$NON-NLS-1$
            }
            if (this.editors != null) {
                this.editors.setLabel(getTranslation("views.publication.all.editors")); //$NON-NLS-1$
            }
        }

        @SuppressWarnings("synthetic-access")
        private void unbindGeneralFields(Publication publication) {
            final var binder = getEntityDataBinder();
            if (this.journal != null) {
                binder.removeBinding(this.journal);
            }
            if (this.conference != null) {
                binder.removeBinding(this.conference);
            }
            if (this.conferenceOccurrenceNumber != null) {
                binder.removeBinding(this.conferenceOccurrenceNumber);
            }
            if (this.publisher != null) {
                binder.removeBinding(this.publisher);
            }
            if (this.chapterNumber != null) {
                binder.removeBinding(this.chapterNumber);
            }

            if (this.bookTitle != null) {
                binder.removeBinding(this.bookTitle);
            }
            if (this.howPublished != null) {
                binder.removeBinding(this.howPublished);
            }
            if (this.documentNumber != null) {
                binder.removeBinding(this.documentNumber);
            }
            if (this.documentType != null) {
                binder.removeBinding(this.documentType);
            }
            if (this.institution != null) {
                binder.removeBinding(this.institution);
            }
            if (this.patentNumber != null) {
                binder.removeBinding(this.patentNumber);
            }
            if (this.patentType != null) {
                binder.removeBinding(this.patentType);
            }
            if (this.reportNumber != null) {
                binder.removeBinding(this.reportNumber);
            }
            if (this.reportType != null) {
                binder.removeBinding(this.reportType);
            }
            if (AbstractPublicationEditor.this.generalDetails != null) {
                AbstractPublicationEditor.this.generalDetails.removeAllStatuses();
            }
        }

        private void removeGeneralFields(Publication publication) {
            if (!(publication instanceof AbstractJournalBasedPublication)) {
                if (this.journal != null) {
                    this.journal.removeFromParent();
                    this.journal = null;
                }
            }

            if (!(publication instanceof AbstractConferenceBasedPublication)) {
                if (this.conference != null) {
                    this.conference.removeFromParent();
                    this.conference = null;
                }
                if (this.conferenceOccurrenceNumber != null) {
                    this.conferenceOccurrenceNumber.removeFromParent();
                    this.conferenceOccurrenceNumber = null;
                }
            }

            if (!(publication instanceof Book) && !(publication instanceof BookChapter)
                    && !(publication instanceof MiscDocument) && this.publisher != null) {
                this.publisher.removeFromParent();
                this.publisher = null;
            }

            if (!(publication instanceof BookChapter)) {
                if (this.chapterNumber != null) {
                    this.chapterNumber.removeFromParent();
                    this.chapterNumber = null;
                }
                if (this.bookTitle != null) {
                    this.bookTitle.removeFromParent();
                    this.bookTitle = null;
                }
            }

            if (!(publication instanceof MiscDocument)) {
                if (this.howPublished != null) {
                    this.howPublished.removeFromParent();
                    this.howPublished = null;
                }
                if (this.documentNumber != null) {
                    this.documentNumber.removeFromParent();
                    this.documentNumber = null;
                }
                if (this.documentType != null) {
                    this.documentType.removeFromParent();
                    this.documentType = null;
                }
            }

            if (!(publication instanceof Patent) && !(publication instanceof Report)
                    && !(publication instanceof Thesis) && this.institution != null) {
                this.institution.removeFromParent();
                this.institution = null;
            }

            if (!(publication instanceof Patent)) {
                if (this.patentNumber != null) {
                    this.patentNumber.removeFromParent();
                    this.patentNumber = null;
                }
                if (this.patentType != null) {
                    this.patentType.removeFromParent();
                    this.patentType = null;
                }
            }

            if (!(publication instanceof Report)) {
                if (this.reportNumber != null) {
                    this.reportNumber.removeFromParent();
                    this.reportNumber = null;
                }
                if (this.reportType != null) {
                    this.reportType.removeFromParent();
                    this.reportType = null;
                }
            }
        }

        private void addGeneralFields(Publication publication) {
            if (publication instanceof AbstractJournalBasedPublication) {
                if (this.journal == null) {
                    this.journal = AbstractPublicationEditor.this.journalFieldFactory.createSingleNameField(
                            getTranslation("views.publication.journal.new_journal"), getLogger()); //$NON-NLS-1$
                    this.journal.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                    this.journal.setRequiredIndicatorVisible(true);

                    AbstractPublicationEditor.this.generalLayout.addComponentAtIndex(2, this.journal);
                    AbstractPublicationEditor.this.generalLayout.setColspan(this.journal, 2);
                }
            } else if (publication instanceof AbstractConferenceBasedPublication) {
                if (this.conference == null) {
                    this.conference = AbstractPublicationEditor.this.conferenceFieldFactory.createSingleNameField(
                            getTranslation("views.publication.conference.new_conference"), getLogger()); //$NON-NLS-1$
                    this.conference.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                    this.conference.setRequiredIndicatorVisible(true);

                    AbstractPublicationEditor.this.generalLayout.addComponentAtIndex(2, this.conference);
                    AbstractPublicationEditor.this.generalLayout.setColspan(this.conference, 1);
                }
                if (this.conferenceOccurrenceNumber == null) {
                    this.conferenceOccurrenceNumber = new IntegerField();
                    this.conferenceOccurrenceNumber.setPrefixComponent(VaadinIcon.HASH.create());
                    this.conferenceOccurrenceNumber.setMin(0);
                    this.conferenceOccurrenceNumber.setMax(1000);

                    AbstractPublicationEditor.this.generalLayout.addComponentAtIndex(3, this.conferenceOccurrenceNumber);
                    AbstractPublicationEditor.this.generalLayout.setColspan(this.conferenceOccurrenceNumber, 1);
                }
            } else if (publication instanceof Book) {
                this.publisher = new TextField();
                this.publisher.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.publisher.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.publisher, 2);
            } else if (publication instanceof BookChapter) {
                this.chapterNumber = new TextField();
                this.chapterNumber.setPrefixComponent(VaadinIcon.HASH.create());
                this.chapterNumber.setClearButtonVisible(true);

                this.bookTitle = new TextField();
                this.bookTitle.setPrefixComponent(VaadinIcon.BOOK.create());
                this.bookTitle.setRequiredIndicatorVisible(true);
                this.bookTitle.setClearButtonVisible(true);

                this.publisher = new TextField();
                this.publisher.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.publisher.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.chapterNumber, 1);
                AbstractPublicationEditor.this.generalLayout.add(this.bookTitle, 1);
                AbstractPublicationEditor.this.generalLayout.add(this.publisher, 2);
            } else if (publication instanceof MiscDocument) {
                this.howPublished = new TextField();
                this.howPublished.setPrefixComponent(VaadinIcon.BOOK.create());
                this.howPublished.setRequiredIndicatorVisible(true);
                this.howPublished.setClearButtonVisible(true);

                this.documentNumber = new TextField();
                this.documentNumber.setPrefixComponent(VaadinIcon.HASH.create());
                this.documentNumber.setClearButtonVisible(true);

                this.documentType = new TextField();
                this.documentType.setPrefixComponent(VaadinIcon.ALT.create());
                this.documentType.setClearButtonVisible(true);

                this.publisher = new TextField();
                this.publisher.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.publisher.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.howPublished, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.documentNumber, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.documentType, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.publisher, 2);
            } else if (publication instanceof Patent) {
                this.institution = new TextField();
                this.institution.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.institution.setRequiredIndicatorVisible(true);
                this.institution.setClearButtonVisible(true);

                this.patentNumber = new TextField();
                this.patentNumber.setPrefixComponent(VaadinIcon.HASH.create());
                this.patentNumber.setRequiredIndicatorVisible(true);
                this.patentNumber.setClearButtonVisible(true);

                this.patentType = new TextField();
                this.patentType.setPrefixComponent(VaadinIcon.ALT.create());
                this.patentType.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.institution, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.patentNumber, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.patentType, 2);
            } else if (publication instanceof Report) {
                this.institution = new TextField();
                this.institution.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.institution.setRequiredIndicatorVisible(true);
                this.institution.setClearButtonVisible(true);

                this.reportNumber = new TextField();
                this.reportNumber.setPrefixComponent(VaadinIcon.HASH.create());
                this.reportNumber.setClearButtonVisible(true);

                this.reportType = new TextField();
                this.reportType.setPrefixComponent(VaadinIcon.ALT.create());
                this.reportType.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.institution, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.reportNumber, 2);
                AbstractPublicationEditor.this.generalLayout.add(this.reportType, 2);
            } else if (publication instanceof Thesis) {
                this.institution = new TextField();
                this.institution.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.institution.setRequiredIndicatorVisible(true);
                this.institution.setClearButtonVisible(true);

                AbstractPublicationEditor.this.generalLayout.add(this.institution, 2);
            }
        }

        @SuppressWarnings("synthetic-access")
        private void bindGeneralFields(Publication publication) {
            final var binder = getEntityDataBinder();
            if (publication instanceof AbstractJournalBasedPublication) {
                assert this.journal != null;
                binder.forField(this.journal)
                        .withValidator(new NotNullEntityValidator<>(getTranslation("views.publication.journal.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.journal, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(AbstractJournalBasedPublication.class, AbstractJournalBasedPublication::getJournal), setter(AbstractJournalBasedPublication.class, AbstractJournalBasedPublication::setJournal));
            } else if (publication instanceof AbstractConferenceBasedPublication) {
                assert this.conference != null;
                binder.forField(this.conference)
                        .withValidator(new NotNullEntityValidator<>(getTranslation("views.publication.conference.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.conference, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(AbstractConferenceBasedPublication.class, AbstractConferenceBasedPublication::getConference), setter(AbstractConferenceBasedPublication.class, AbstractConferenceBasedPublication::setConference));
                assert this.conferenceOccurrenceNumber != null;
                binder.forField(this.conferenceOccurrenceNumber)
                        .bind(getter(AbstractConferenceBasedPublication.class, AbstractConferenceBasedPublication::getConferenceOccurrenceNumber), setter(AbstractConferenceBasedPublication.class, AbstractConferenceBasedPublication::setConferenceOccurrenceNumber));
            } else if (publication instanceof Book) {
                assert this.publisher != null;
                binder.forField(this.publisher)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getPublisher), setter(Book.class, Book::setPublisher));
            } else if (publication instanceof BookChapter) {
                assert this.chapterNumber != null;
                binder.forField(this.chapterNumber)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getChapterNumber), setter(BookChapter.class, BookChapter::setChapterNumber));
                assert this.bookTitle != null;
                binder.forField(this.bookTitle)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.book_chapter.book_title.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.bookTitle, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(BookChapter.class, BookChapter::getBookTitle), setter(BookChapter.class, BookChapter::setBookTitle));
                assert this.publisher != null;
                binder.forField(this.publisher)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getPublisher), setter(BookChapter.class, BookChapter::setPublisher));
            } else if (publication instanceof MiscDocument) {
                assert this.howPublished != null;
                binder.forField(this.howPublished)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.misc_document.how_published.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.howPublished, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(MiscDocument.class, MiscDocument::getHowPublished), setter(MiscDocument.class, MiscDocument::setHowPublished));
                assert this.documentNumber != null;
                binder.forField(this.documentNumber)
                        .withConverter(new StringTrimer())
                        .bind(getter(MiscDocument.class, MiscDocument::getDocumentNumber), setter(MiscDocument.class, MiscDocument::setDocumentNumber));
                assert this.documentType != null;
                binder.forField(this.documentType)
                        .withConverter(new StringTrimer())
                        .bind(getter(MiscDocument.class, MiscDocument::getDocumentType), setter(MiscDocument.class, MiscDocument::setDocumentType));
                assert this.publisher != null;
                binder.forField(this.publisher)
                        .withConverter(new StringTrimer())
                        .bind(getter(MiscDocument.class, MiscDocument::getPublisher), setter(MiscDocument.class, MiscDocument::setPublisher));
            } else if (publication instanceof Patent) {
                assert this.institution != null;
                binder.forField(this.institution)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.all.institution.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.institution, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(Patent.class, Patent::getInstitution), setter(Patent.class, Patent::setInstitution));
                assert this.patentNumber != null;
                binder.forField(this.patentNumber)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.patent.number.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.patentNumber, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(Patent.class, Patent::getPatentNumber), setter(Patent.class, Patent::setPatentNumber));
                assert this.patentType != null;
                binder.forField(this.patentType)
                        .withConverter(new StringTrimer())
                        .bind(getter(Patent.class, Patent::getPatentType), setter(Patent.class, Patent::setPatentType));
            } else if (publication instanceof Report) {
                assert this.institution != null;
                binder.forField(this.institution)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.all.institution.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.institution, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(Report.class, Report::getInstitution), setter(Report.class, Report::setInstitution));
                assert this.reportNumber != null;
                binder.forField(this.reportNumber)
                        .withConverter(new StringTrimer())
                        .bind(getter(Report.class, Report::getReportNumber), setter(Report.class, Report::setReportNumber));
                assert this.reportType != null;
                binder.forField(this.reportType)
                        .withConverter(new StringTrimer())
                        .bind(getter(Report.class, Report::getReportType), setter(Report.class, Report::setReportType));
            } else if (publication instanceof Thesis) {
                assert this.institution != null;
                binder.forField(this.institution)
                        .withConverter(new StringTrimer())
                        .withValidator(new NotEmptyStringValidator(getTranslation("views.publication.all.institution.error"))) //$NON-NLS-1$
                        .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.institution, AbstractPublicationEditor.this.generalDetails))
                        .bind(getter(Thesis.class, Thesis::getInstitution), setter(Thesis.class, Thesis::setInstitution));
            }
        }

        /**
         * Update the form for the general details.
         *
         * @param publication the publication to edit.
         */
        public void updateGeneralDetails(Publication publication) {
            unbindGeneralFields(publication);
            removeGeneralFields(publication);
            addGeneralFields(publication);
            bindGeneralFields(publication);
        }

        @SuppressWarnings("synthetic-access")
        private void unbindIdentificationFields(Publication publication) {
            final var binder = getEntityDataBinder();
            if (this.edition != null) {
                binder.removeBinding(this.edition);
            }
            if (this.series != null) {
                binder.removeBinding(this.series);
            }
            if (this.volume != null) {
                binder.removeBinding(this.volume);
            }
            if (this.number != null) {
                binder.removeBinding(this.number);
            }
            if (this.pages != null) {
                binder.removeBinding(this.pages);
            }
            if (this.organization != null) {
                binder.removeBinding(this.organization);
            }
            if (this.address != null) {
                binder.removeBinding(this.address);
            }
            if (AbstractPublicationEditor.this.identificationDetails != null) {
                AbstractPublicationEditor.this.identificationDetails.removeAllStatuses();
            }
        }

        private void removeIdentificationFields(Publication publication) {
            if (!(publication instanceof Book) && !(publication instanceof BookChapter)) {
                if (this.edition != null) {
                    this.edition.removeFromParent();
                    this.edition = null;
                }
            }
            if (!(publication instanceof ConferencePaper) && !(publication instanceof JournalPaper)
                    && !(publication instanceof Book) && !(publication instanceof BookChapter)) {
                if (this.series != null) {
                    this.series.removeFromParent();
                    this.series = null;
                }
            }
            if (!(publication instanceof ConferencePaper) && !(publication instanceof JournalPaper)
                    && !(publication instanceof JournalEdition) && !(publication instanceof Book)
                    && !(publication instanceof BookChapter)) {
                if (this.volume != null) {
                    this.volume.removeFromParent();
                    this.volume = null;
                }

                if (this.number != null) {
                    this.number.removeFromParent();
                    this.number = null;
                }

                if (this.pages != null) {
                    this.pages.removeFromParent();
                    this.pages = null;
                }
            }
            if (!(publication instanceof ConferencePaper) && !(publication instanceof KeyNote)
                    && !(publication instanceof MiscDocument)) {
                if (this.organization != null) {
                    this.organization.removeFromParent();
                    this.organization = null;
                }
            }

            if (!(publication instanceof ConferencePaper) && !(publication instanceof KeyNote)
                    && !(publication instanceof MiscDocument) && !(publication instanceof Patent)
                    && !(publication instanceof Report) && !(publication instanceof Thesis)) {
                if (this.address != null) {
                    this.address.removeFromParent();
                    this.address = null;
                }
            }
        }

        private void addIdentificationFields(Publication publication) {
            if (publication instanceof ConferencePaper) {
                this.series = new TextField();
                this.series.setPrefixComponent(VaadinIcon.BOOK.create());
                this.series.setClearButtonVisible(true);

                this.volume = new TextField();
                this.volume.setPrefixComponent(VaadinIcon.HASH.create());
                this.volume.setClearButtonVisible(true);

                this.number = new TextField();
                this.number.setPrefixComponent(VaadinIcon.HASH.create());
                this.number.setClearButtonVisible(true);

                this.pages = new TextField();
                this.pages.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.pages.setClearButtonVisible(true);

                this.organization = new TextField();
                this.organization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.organization.setClearButtonVisible(true);

                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.series, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.volume, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.number, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.pages, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.organization, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            } else if (publication instanceof JournalPaper) {
                this.series = new TextField();
                this.series.setPrefixComponent(VaadinIcon.BOOK.create());
                this.series.setClearButtonVisible(true);

                this.volume = new TextField();
                this.volume.setPrefixComponent(VaadinIcon.HASH.create());
                this.volume.setClearButtonVisible(true);

                this.number = new TextField();
                this.number.setPrefixComponent(VaadinIcon.HASH.create());
                this.number.setClearButtonVisible(true);

                this.pages = new TextField();
                this.pages.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.pages.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.series, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.volume, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.number, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.pages, 2);
            } else if (publication instanceof JournalEdition) {
                this.volume = new TextField();
                this.volume.setPrefixComponent(VaadinIcon.HASH.create());
                this.volume.setClearButtonVisible(true);

                this.number = new TextField();
                this.number.setPrefixComponent(VaadinIcon.HASH.create());
                this.number.setClearButtonVisible(true);

                this.pages = new TextField();
                this.pages.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.pages.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.volume, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.number, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.pages, 2);
            } else if (publication instanceof Book) {
                this.editors = new TextField();
                this.editors.setPrefixComponent(VaadinIcon.USERS.create());
                this.editors.setClearButtonVisible(true);

                this.edition = new TextField();
                this.edition.setPrefixComponent(VaadinIcon.HASH.create());
                this.edition.setClearButtonVisible(true);

                this.series = new TextField();
                this.series.setPrefixComponent(VaadinIcon.BOOK.create());
                this.series.setClearButtonVisible(true);

                this.volume = new TextField();
                this.volume.setPrefixComponent(VaadinIcon.HASH.create());
                this.volume.setClearButtonVisible(true);

                this.number = new TextField();
                this.number.setPrefixComponent(VaadinIcon.HASH.create());
                this.number.setClearButtonVisible(true);

                this.pages = new TextField();
                this.pages.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.pages.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.edition, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.series, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.volume, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.number, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.pages, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.editors, 2);
            } else if (publication instanceof BookChapter) {
                this.editors = new TextField();
                this.editors.setPrefixComponent(VaadinIcon.USERS.create());
                this.editors.setClearButtonVisible(true);

                this.edition = new TextField();
                this.edition.setPrefixComponent(VaadinIcon.HASH.create());
                this.edition.setClearButtonVisible(true);

                this.series = new TextField();
                this.series.setPrefixComponent(VaadinIcon.BOOK.create());
                this.series.setClearButtonVisible(true);

                this.volume = new TextField();
                this.volume.setPrefixComponent(VaadinIcon.HASH.create());
                this.volume.setClearButtonVisible(true);

                this.number = new TextField();
                this.number.setPrefixComponent(VaadinIcon.HASH.create());
                this.number.setClearButtonVisible(true);

                this.pages = new TextField();
                this.pages.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
                this.pages.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.edition, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.series, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.volume, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.number, 1);
                AbstractPublicationEditor.this.identificationLayout.add(this.pages, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.editors, 2);
            } else if (publication instanceof KeyNote) {
                this.editors = new TextField();
                this.editors.setPrefixComponent(VaadinIcon.USERS.create());
                this.editors.setClearButtonVisible(true);

                this.organization = new TextField();
                this.organization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.organization.setClearButtonVisible(true);

                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.editors, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.organization, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            } else if (publication instanceof MiscDocument) {
                this.organization = new TextField();
                this.organization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
                this.organization.setClearButtonVisible(true);

                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.organization, 2);
                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            } else if (publication instanceof Patent) {
                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            } else if (publication instanceof Report) {
                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            } else if (publication instanceof Thesis) {
                this.address = new TextField();
                this.address.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
                this.address.setClearButtonVisible(true);

                AbstractPublicationEditor.this.identificationLayout.add(this.address, 2);
            }
        }

        @SuppressWarnings("synthetic-access")
        private void bindIdentificationFields(Publication publication) {
            final var binder = getEntityDataBinder();
            if (publication instanceof ConferencePaper) {
                assert this.series != null;
                binder.forField(this.series)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getSeries), setter(ConferencePaper.class, ConferencePaper::setSeries));
                assert this.volume != null;
                binder.forField(this.volume)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getVolume), setter(ConferencePaper.class, ConferencePaper::setVolume));
                assert this.number != null;
                binder.forField(this.number)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getNumber), setter(ConferencePaper.class, ConferencePaper::setNumber));
                assert this.pages != null;
                binder.forField(this.pages)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getPages), setter(ConferencePaper.class, ConferencePaper::setPages));
                assert this.organization != null;
                binder.forField(this.organization)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getOrganization), setter(ConferencePaper.class, ConferencePaper::setOrganization));
                assert this.address != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(ConferencePaper.class, ConferencePaper::getAddress), setter(ConferencePaper.class, ConferencePaper::setAddress));
            } else if (publication instanceof JournalPaper) {
                assert this.series != null;
                binder.forField(this.series)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalPaper.class, JournalPaper::getSeries), setter(JournalPaper.class, JournalPaper::setSeries));
                assert this.volume != null;
                binder.forField(this.volume)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalPaper.class, JournalPaper::getVolume), setter(JournalPaper.class, JournalPaper::setVolume));
                assert this.number != null;
                binder.forField(this.number)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalPaper.class, JournalPaper::getNumber), setter(JournalPaper.class, JournalPaper::setNumber));
                assert this.pages != null;
                binder.forField(this.pages)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalPaper.class, JournalPaper::getPages), setter(JournalPaper.class, JournalPaper::setPages));
            } else if (publication instanceof JournalEdition) {
                assert this.volume != null;
                binder.forField(this.volume)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalEdition.class, JournalEdition::getVolume), setter(JournalEdition.class, JournalEdition::setVolume));
                assert this.number != null;
                binder.forField(this.number)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalEdition.class, JournalEdition::getNumber), setter(JournalEdition.class, JournalEdition::setNumber));
                assert this.pages != null;
                binder.forField(this.pages)
                        .withConverter(new StringTrimer())
                        .bind(getter(JournalEdition.class, JournalEdition::getPages), setter(JournalEdition.class, JournalEdition::setPages));
            } else if (publication instanceof Book) {
                assert this.edition != null;
                binder.forField(this.edition)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getEdition), setter(Book.class, Book::setEdition));
                assert this.series != null;
                binder.forField(this.series)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getSeries), setter(Book.class, Book::setSeries));
                assert this.volume != null;
                binder.forField(this.volume)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getVolume), setter(Book.class, Book::setVolume));
                assert this.number != null;
                binder.forField(this.number)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getNumber), setter(Book.class, Book::setNumber));
                assert this.pages != null;
                binder.forField(this.pages)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getPages), setter(Book.class, Book::setPages));
                assert this.editors != null;
                binder.forField(this.editors)
                        .withConverter(new StringTrimer())
                        .bind(getter(Book.class, Book::getEditors), setter(Book.class, Book::setEditors));
            } else if (publication instanceof BookChapter) {
                assert this.edition != null;
                binder.forField(this.edition)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getEdition), setter(BookChapter.class, BookChapter::setEdition));
                assert this.series != null;
                binder.forField(this.series)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getSeries), setter(BookChapter.class, BookChapter::setSeries));
                assert this.volume != null;
                binder.forField(this.volume)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getVolume), setter(BookChapter.class, BookChapter::setVolume));
                assert this.number != null;
                binder.forField(this.number)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getNumber), setter(BookChapter.class, BookChapter::setNumber));
                assert this.pages != null;
                binder.forField(this.pages)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getPages), setter(BookChapter.class, BookChapter::setPages));
                assert this.editors != null;
                binder.forField(this.editors)
                        .withConverter(new StringTrimer())
                        .bind(getter(BookChapter.class, BookChapter::getEditors), setter(BookChapter.class, BookChapter::setEditors));
            } else if (publication instanceof KeyNote) {
                assert this.organization != null;
                binder.forField(this.editors)
                        .withConverter(new StringTrimer())
                        .bind(getter(KeyNote.class, KeyNote::getEditors), setter(KeyNote.class, KeyNote::setEditors));
                assert this.organization != null;
                binder.forField(this.organization)
                        .withConverter(new StringTrimer())
                        .bind(getter(KeyNote.class, KeyNote::getOrganization), setter(KeyNote.class, KeyNote::setOrganization));
                assert this.address != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(KeyNote.class, KeyNote::getAddress), setter(KeyNote.class, KeyNote::setAddress));
            } else if (publication instanceof MiscDocument) {
                assert this.organization != null;
                binder.forField(this.organization)
                        .withConverter(new StringTrimer())
                        .bind(getter(MiscDocument.class, MiscDocument::getOrganization), setter(MiscDocument.class, MiscDocument::setOrganization));
                assert this.address != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(MiscDocument.class, MiscDocument::getAddress), setter(MiscDocument.class, MiscDocument::setAddress));
            } else if (publication instanceof Patent) {
                assert this.address != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(Patent.class, Patent::getAddress), setter(Patent.class, Patent::setAddress));
            } else if (publication instanceof Report) {
                assert this.address != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(Report.class, Report::getAddress), setter(Report.class, Report::setAddress));
            } else if (publication instanceof Thesis) {
                assert this.pages != null;
                binder.forField(this.address)
                        .withConverter(new StringTrimer())
                        .bind(getter(Thesis.class, Thesis::getAddress), setter(Thesis.class, Thesis::setAddress));
            }
        }

        /**
         * Update the form for the identification details.
         *
         * @param publication the publication to edit.
         */
        public void updateIdentificationDetails(Publication publication) {
            unbindIdentificationFields(publication);
            removeIdentificationFields(publication);
            addIdentificationFields(publication);
            bindIdentificationFields(publication);
        }

    }

}
