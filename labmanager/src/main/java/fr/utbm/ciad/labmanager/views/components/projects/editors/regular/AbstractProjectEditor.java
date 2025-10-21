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

package fr.utbm.ciad.labmanager.views.components.projects.editors.regular;

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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.AbstractValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationNameComparator;
import fr.utbm.ciad.labmanager.data.project.*;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.trl.TRL;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.DoubleToFloatWithPrecisionConverter;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.image.ServerSideUploadableImageField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.image.ServerSideUploadableImagesField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.pdf.ServerSideUploadablePdfField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.powerpoint.ServerSideUploadablePowerpointField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.*;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.addons.value.TextListField;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.projects.fields.ProjectBudgetListGridField;
import fr.utbm.ciad.labmanager.views.components.projects.fields.ProjectFieldFactory;
import fr.utbm.ciad.labmanager.views.components.projects.fields.ProjectMemberListGridField;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.MessageSourceAccessor;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Abstract implementation for the editor of the information related to a project.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractProjectEditor extends AbstractEntityEditor<Project> {

    private static final long serialVersionUID = -6206260640976206891L;
    private final ResearchOrganizationService organizationService;
    private final ScientificAxisService axisService;
    private final DownloadableFileManager fileManager;
    private final OrganizationEditorFactory organizationEditorFactory;
    private final OrganizationFieldFactory organizationFieldFactory;
    private final ScientificAxisEditorFactory axisEditorFactory;
    private final ProjectFieldFactory projectFieldFactory;
    private DetailsWithErrorMark descriptionDetails;
    private TextField acronym;
    private TextField scientificTitle;
    private ComboBox<ProjectStatus> projectStatus;
    private DatePicker startDate;
    private IntegerField duration;
    private DetailsWithErrorMark consortiumDetails;
    private SingleOrganizationNameField coordinator;
    private SingleOrganizationNameField localOrganization;
    private SingleOrganizationNameField superOrganization;
    private ComboListField<ResearchOrganization> otherPartners;
    private ProjectMemberListGridField participants;
    private DetailsWithErrorMark fundingDetails;
    private NumberField globalBudget;
    private ProjectBudgetListGridField budgets;
    private SingleOrganizationNameField learOrganization;
    private DetailsWithErrorMark innovationDetails;
    private ToggleButton openSource;
    private ComboBox<ProjectActivityType> activityType;
    private ComboBox<ProjectContractType> contractType;
    private ComboBox<TRL> trl;
    private DetailsWithErrorMark presentationDetails;
    private ToggleButton confidential;
    private MarkdownField description;
    private ServerSideUploadablePdfField requirements;
    private ServerSideUploadablePowerpointField powerpoint;
    private ComboListField<ScientificAxis> scientificAxes;
    private DetailsWithErrorMark communicationDetails;
    private ServerSideUploadableImageField logo;
    private ComboBox<ProjectWebPageNaming> webpageConvention;
    private TextField projectUrl;
    private ServerSideUploadablePdfField pressDocument;
    private ServerSideUploadableImagesField pathsToImages;
    private TextListField<String> videosURLs;

    /**
     * Constructor.
     *
     * @param context                       the context for editing the project.
     * @param projectCreationStatusComputer the tool for computer the creation status for the scientific projects.
     * @param relinkEntityWhenSaving        indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                      be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param projectFieldFactory           the factory for creating the project fields.
     * @param organizationService           the service for accessing the JPA entities for research organizations.
     * @param addressService                the service for accessing the JPA entities for organization addresses.
     * @param axisService                   the service for accessing the JPA entities for scientific axes.
     * @param axisEditorFactory             the factory for creating the scientific axis editors.
     * @param organizationEditorFactory     the factory for creating the organization editors.
     * @param organizationFieldFactory      the factory for creating the organization fields.
     * @param fileManager                   the manager of the downloadable files.
     * @param authenticatedUser             the connected user.
     * @param messages                      the accessor to the localized messages (Spring layer).
     * @param properties                    specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractProjectEditor(EntityEditingContext<Project> context,
                                 EntityCreationStatusComputer<Project> projectCreationStatusComputer,
                                 boolean relinkEntityWhenSaving, ProjectFieldFactory projectFieldFactory,
                                 ResearchOrganizationService organizationService, OrganizationAddressService addressService,
                                 ScientificAxisService axisService, ScientificAxisEditorFactory axisEditorFactory,
                                 OrganizationEditorFactory organizationEditorFactory,
                                 OrganizationFieldFactory organizationFieldFactory,
                                 DownloadableFileManager fileManager, AuthenticatedUser authenticatedUser,
                                 MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        super(Project.class, authenticatedUser, messages,
                projectCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.projects.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.projects.administration.validated_project")); //$NON-NLS-1$
        this.projectFieldFactory = projectFieldFactory;
        this.organizationService = organizationService;
        this.axisService = axisService;
        this.fileManager = fileManager;
        this.organizationEditorFactory = organizationEditorFactory;
        this.organizationFieldFactory = organizationFieldFactory;
        this.axisEditorFactory = axisEditorFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        createConsortiumDetails(rootContainer);
        createFundingDetails(rootContainer);
        createInnovationDetails(rootContainer);
        createPresentationDetails(rootContainer);
        createCommunicationDetails(rootContainer);
        if (isBaseAdmin()) {
            Consumer<FormLayout> builderCallback = null;
            if (isAdvancedAdmin()) {
                builderCallback = content -> {
                    this.webpageConvention = new ComboBox<>();
                    this.webpageConvention.setItems(ProjectWebPageNaming.values());
                    this.webpageConvention.setItemLabelGenerator(this::getWebpageNamingLabel);
                    this.webpageConvention.setValue(ProjectWebPageNaming.UNSPECIFIED);
                    content.add(this.webpageConvention, 1);

                    getEntityDataBinder().forField(this.webpageConvention).bind(Project::getWebPageNaming, Project::setWebPageNaming);
                };
            }
            createAdministrationComponents(rootContainer, builderCallback,
                    it -> it.bind(Project::isValidated, Project::setValidated));
        }
    }

    private String getWebpageNamingLabel(ProjectWebPageNaming naming) {
        return naming.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the description of the project.
     *
     * @param rootContainer the container.
     */
    protected void createDescriptionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.acronym = new TextField();
        this.acronym.setPrefixComponent(VaadinIcon.HASH.create());
        this.acronym.setRequired(true);
        this.acronym.setClearButtonVisible(true);
        content.add(this.acronym, 1);

        this.scientificTitle = new TextField();
        this.scientificTitle.setPrefixComponent(VaadinIcon.HASH.create());
        this.scientificTitle.setRequired(true);
        this.scientificTitle.setClearButtonVisible(true);
        content.add(this.scientificTitle, 2);

        this.projectStatus = new ComboBox<>();
        this.projectStatus.setRequired(true);
        this.projectStatus.setItems(ProjectStatus.values());
        this.projectStatus.setItemLabelGenerator(this::getProjectStatusLabel);
        this.projectStatus.setPrefixComponent(VaadinIcon.ELLIPSIS_CIRCLE_O.create());
        content.add(this.projectStatus, 2);

        this.startDate = new DatePicker();
        this.startDate.setRequired(true);
        this.startDate.setPrefixComponent(VaadinIcon.SIGN_IN_ALT.create());
        this.startDate.setClearButtonVisible(true);
        content.add(this.startDate, 1);

        this.duration = new IntegerField();
        this.duration.setRequired(true);
        this.duration.setMin(0);
        this.duration.setPrefixComponent(VaadinIcon.TIMER.create());
        this.duration.setClearButtonVisible(true);
        content.add(this.duration, 1);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.projects.acronym.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.acronym, this.descriptionDetails))
                .bind(Project::getAcronym, Project::setAcronym);
        getEntityDataBinder().forField(this.scientificTitle)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.projects.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.scientificTitle, this.descriptionDetails))
                .bind(Project::getScientificTitle, Project::setScientificTitle);
        getEntityDataBinder().forField(this.projectStatus)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.projects.status.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.projectStatus, this.descriptionDetails))
                .bind(Project::getStatus, Project::setStatus);
        getEntityDataBinder().forField(this.startDate)
                .withValidator(new NotNullDateValidator(getTranslation("views.projects.start_date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.startDate, this.descriptionDetails))
                .bind(Project::getStartDate, Project::setStartDate);
        getEntityDataBinder().forField(this.duration)
                .withValidator(new IntegerRangeValidator(getTranslation("views.projects.duration.error"), Integer.valueOf(0), null)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.duration, this.descriptionDetails))
                .bind(Project::getDuration, Project::setDuration);
    }

    private String getProjectStatusLabel(ProjectStatus status) {
        return status.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the consortium of the project.
     *
     * @param rootContainer the container.
     */
    protected void createConsortiumDetails(VerticalLayout rootContainer) {
        final var defaultLocalOrganization = this.organizationService.getDefaultOrganization();
        final var defaultSuperOrganization = this.organizationService.getDefaultSuperOrganization();

        final var content = ComponentFactory.newColumnForm(2);

        this.coordinator = this.organizationFieldFactory.createSingleNameField(getTranslation("views.projects.new_organization"), getLogger(), null); //$NON-NLS-1$
        this.coordinator.setPrefixComponent(VaadinIcon.USER_STAR.create());
        this.coordinator.setRequiredIndicatorVisible(true);
        content.add(this.coordinator, 2);

        // Cannot create an organization from the local organization combo
        this.localOrganization = this.organizationFieldFactory.createSingleNameField();
        this.localOrganization.setPrefixComponent(VaadinIcon.OFFICE.create());
        this.localOrganization.setRequiredIndicatorVisible(true);
        if (defaultLocalOrganization != null) {
            this.localOrganization.setValue(defaultLocalOrganization);
        }
        this.localOrganization.setReadOnly(true);
        content.add(this.localOrganization, 1);

        // Cannot create an organization from the super-organization combo
        this.superOrganization = this.organizationFieldFactory.createSingleNameField(defaultLocalOrganization, null);
        this.superOrganization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        this.superOrganization.setRequiredIndicatorVisible(true);
        if (defaultSuperOrganization != null) {
            this.superOrganization.setValue(defaultSuperOrganization);
        }
        content.add(this.superOrganization, 1);

        this.participants = this.projectFieldFactory.createProjectMemberField(getLogger());
        content.add(this.participants, 2);

        this.otherPartners = new ComboListField<>(
                ComponentFactory.toSerializableComparator(new ResearchOrganizationNameComparator()),
                this::openOtherPartnerEditor);
        this.otherPartners.setEntityRenderers(
                it -> it.getAcronymAndName(),
                new ComponentRenderer<>(this::createOtherPartnerNameComponent),
                new ComponentRenderer<>(this::createOtherPartnerNameComponent));
        this.otherPartners.setAvailableEntities(query -> {
            return this.organizationService.getAllResearchOrganizations(
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    null).stream();
        });
        content.add(this.otherPartners, 2);

        this.consortiumDetails = createDetailsWithErrorMark(rootContainer, content, "consortium", false); //$NON-NLS-1$

        getEntityDataBinder().forField(this.coordinator)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.projects.coordinator.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.coordinator, this.consortiumDetails))
                .bind(Project::getCoordinator, Project::setCoordinator);

        getEntityDataBinder().forField(this.localOrganization)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.projects.local_organization.error.null"), //$NON-NLS-1$
                        getTranslation("views.projects.local_organization.error.disjoint"), //$NON-NLS-1$
                        this::checkLocalOrganizationUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.localOrganization, this.consortiumDetails))
                .bind(it -> {
                    // If the project has no local organization, assumes it is the default local organization
                    var value = it.getLocalOrganization();
                    if (value == null) {
                        value = defaultLocalOrganization;
                    }
                    return value;
                }, Project::setLocalOrganization);

        getEntityDataBinder().forField(this.superOrganization)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.projects.super_organization.error.null"), //$NON-NLS-1$
                        getTranslation("views.projects.super_organization.error.disjoint"), //$NON-NLS-1$
                        this::checkSuperOrganizationUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.superOrganization, this.consortiumDetails))
                .bind(Project::getSuperOrganization, Project::setSuperOrganization);

        getEntityDataBinder().forField(this.participants)
                .withValidator(this.participants.newStandardValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.participants, this.consortiumDetails))
                .bind(Project::getParticipants, Project::setParticipants);

        getEntityDataBinder().forField(this.otherPartners)
                .bind(Project::getOtherPartnersRaw, Project::setOtherPartners);
    }

    private Component createOtherPartnerNameComponent(ResearchOrganization partner) {
        return new Span(partner.getAcronymAndName());
    }

    /**
     * Invoked for creating a new research organization for other partners.
     *
     * @param saver the callback that is invoked when the research organization is saved as JPA entity.
     */
    protected void openOtherPartnerEditor(Consumer<ResearchOrganization> saver) {
        final var newOrganization = new ResearchOrganization();
        final var editor = this.organizationEditorFactory.createAdditionEditor(newOrganization, getLogger());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.projects.other_partner.create"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    private boolean checkLocalOrganizationUnicity(ResearchOrganization localOrganization) {
        assert localOrganization != null;
        final var superOrganization = getEditedEntity().getSuperOrganization();
        return !localOrganization.equals(superOrganization);
    }

    private boolean checkSuperOrganizationUnicity(ResearchOrganization superOrganization) {
        assert superOrganization != null;
        final var localOrganization = getEditedEntity().getLocalOrganization();
        return !superOrganization.equals(localOrganization);
    }

    /**
     * Create the section for editing the funding of the project.
     *
     * @param rootContainer the container.
     */
    protected void createFundingDetails(VerticalLayout rootContainer) {
        final var defaultLearOrganization = this.organizationService.getDefaultLearOrganization();

        final var content = ComponentFactory.newColumnForm(2);

        this.budgets = this.projectFieldFactory.createProjectBudgetField();
        content.add(this.budgets, 2);

        this.globalBudget = new NumberField();
        this.globalBudget.setPrefixComponent(VaadinIcon.EURO.create());
        this.globalBudget.setClearButtonVisible(true);
        content.add(this.globalBudget, 2);

        this.learOrganization = this.organizationFieldFactory.createSingleNameField(getTranslation("views.projects.new_organization"), getLogger(), null); //$NON-NLS-1$
        this.learOrganization.setPrefixComponent(VaadinIcon.COIN_PILES.create());
        this.learOrganization.setRequiredIndicatorVisible(true);
        if (defaultLearOrganization != null) {
            this.learOrganization.setValue(defaultLearOrganization);
        }
        content.add(this.learOrganization, 2);

        this.fundingDetails = createDetailsWithErrorMark(rootContainer, content, "funding"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.budgets)
                .withValidator(this.budgets.newStandardValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.budgets, this.fundingDetails))
                .bind(Project::getBudgets, Project::setBudgets);
        getEntityDataBinder().forField(this.globalBudget)
                .withConverter(new DoubleToFloatWithPrecisionConverter(3))
                .withValidator(new GlobalBudgetValidator(
                        getTranslation("views.projects.global_budget.error.null"), //$NON-NLS-1$
                        getTranslation("views.projects.global_budget.error.too_low"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.globalBudget, this.fundingDetails))
                .bind(Project::getGlobalBudget, Project::setGlobalBudget);
        getEntityDataBinder().forField(this.learOrganization)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.projects.lear.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.learOrganization, this.fundingDetails))
                .bind(Project::getLearOrganization, Project::setLearOrganization);
    }

    /**
     * Create the section for editing the innovation elements of the project.
     *
     * @param rootContainer the container.
     */
    protected void createInnovationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.activityType = new ComboBox<>();
        this.activityType.setRequired(true);
        this.activityType.setItems(ProjectActivityType.values());
        this.activityType.setItemLabelGenerator(this::getActivityTypeLabel);
        this.activityType.setPrefixComponent(VaadinIcon.CLUSTER.create());
        content.add(this.activityType, 2);

        this.contractType = new ComboBox<>();
        this.contractType.setRequired(true);
        this.contractType.setItems(ProjectContractType.values());
        this.contractType.setItemLabelGenerator(this::getContractTypeLabel);
        this.contractType.setPrefixComponent(VaadinIcon.HANDSHAKE.create());
        content.add(this.contractType, 2);

        this.trl = new ComboBox<>();
        this.trl.setRequired(true);
        this.trl.setItems(TRL.values());
        this.trl.setItemLabelGenerator(this::getTrlLabel);
        this.trl.setPrefixComponent(VaadinIcon.LIST_OL.create());
        content.add(this.trl, 2);

        this.openSource = new ToggleButton();
        content.add(this.openSource, 2);

        this.innovationDetails = createDetailsWithErrorMark(rootContainer, content, "innovation"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.activityType)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.projects.activity_type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.activityType, this.innovationDetails))
                .bind(Project::getActivityType, Project::setActivityType);
        getEntityDataBinder().forField(this.contractType)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.projects.contract_type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.contractType, this.innovationDetails))
                .bind(Project::getContractType, Project::setContractType);
        getEntityDataBinder().forField(this.trl)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.projects.trl.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.trl, this.innovationDetails))
                .bind(Project::getTRL, Project::setTRL);
        getEntityDataBinder().forField(this.openSource)
                .bind(Project::isOpenSource, Project::setOpenSource);
    }

    private String getActivityTypeLabel(ProjectActivityType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getContractTypeLabel(ProjectContractType type) {
        if (type == ProjectContractType.NOT_SPECIFIED) {
            return type.getLabel(getMessageSourceAccessor(), getLocale());
        }
        return type.name() + " - " + //$NON-NLS-1$
                type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getTrlLabel(TRL trl) {
        return trl.name() + " - " + //$NON-NLS-1$
                trl.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the presentation elements of the project.
     *
     * @param rootContainer the container.
     */
    protected void createPresentationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.confidential = new ToggleButton();
        content.add(this.confidential, 2);

        this.description = new MarkdownField();
        content.add(this.description, 2);

        this.requirements = new ServerSideUploadablePdfField(this.fileManager,
                ext -> this.fileManager.makeProjectScientificRequirementsFilename(getEditedEntity().getId()),
                () -> getLogger());
        this.requirements.setClearButtonVisible(true);
        content.add(this.requirements, 1);

        this.powerpoint = new ServerSideUploadablePowerpointField(this.fileManager,
                ext -> this.fileManager.makeProjectPowerpointFilename(getEditedEntity().getId(), ext),
                () -> getLogger());
        this.powerpoint.setClearButtonVisible(true);
        content.add(this.powerpoint, 1);

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

        this.presentationDetails = createDetailsWithErrorMark(rootContainer, content, "presentation"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.confidential)
                .bind(Project::isConfidential, Project::setConfidential);
        getEntityDataBinder().forField(this.description)
                .withConverter(new StringTrimer())
                .bind(Project::getDescription, Project::setDescription);
        getEntityDataBinder().forField(this.requirements)
                .withConverter(new StringTrimer())
                .bind(Project::getPathToScientificRequirements, Project::setPathToScientificRequirements);
        getEntityDataBinder().forField(this.powerpoint)
                .withConverter(new StringTrimer())
                .bind(Project::getPathToPowerpoint, Project::setPathToPowerpoint);
        getEntityDataBinder().forField(this.scientificAxes)
                .bind(Project::getScientificAxes, Project::setScientificAxes);
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
                getTranslation("views.projects.scientific_axes.create"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    /**
     * Create the section for editing the communication elements of the project.
     *
     * @param rootContainer the container.
     */
    protected void createCommunicationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.logo = new ServerSideUploadableImageField(this.fileManager,
                ext -> this.fileManager.makeProjectLogoFilename(getEditedEntity().getId(), ext),
                () -> getLogger());
        this.logo.setClearButtonVisible(true);
        content.add(this.logo, 1);

        this.projectUrl = new TextField();
        this.projectUrl.setClearButtonVisible(true);
        this.projectUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(this.projectUrl, 2);

        this.pressDocument = new ServerSideUploadablePdfField(this.fileManager,
                ext -> this.fileManager.makeProjectPressDocumentFilename(getEditedEntity().getId()),
                () -> getLogger());
        this.pressDocument.setClearButtonVisible(true);
        content.add(this.pressDocument, 1);

        this.pathsToImages = new ServerSideUploadableImagesField(this.fileManager,
                (index, ext) -> this.fileManager.makeProjectImageFilename(getEditedEntity().getId(), index.intValue(), ext),
                () -> getLogger());
        content.add(this.pathsToImages, 2);

        this.videosURLs = new TextListField<>(
                StringUtils::compare,
                it -> {
                    try {
                        return new URI(it).toURL().toExternalForm();
                    } catch (Throwable ex) {
                        return ""; //$NON-NLS-1$
                    }
                },
                "views.projects.video_urls.error"); //$NON-NLS-1$
        content.add(this.videosURLs, 2);

        this.communicationDetails = createDetailsWithErrorMark(rootContainer, content, "communication"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.logo)
                .withConverter(new StringTrimer())
                .bind(Project::getPathToLogo, Project::setPathToLogo);
        getEntityDataBinder().forField(this.projectUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(getTranslation("views.projects.url.error"), true)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.projectUrl, this.communicationDetails))
                .bind(Project::getProjectURL, Project::setProjectURL);
        getEntityDataBinder().forField(this.pressDocument)
                .withConverter(new StringTrimer())
                .bind(Project::getPathToPressDocument, Project::setPathToPressDocument);
        getEntityDataBinder().forField(this.pathsToImages)
                .bind(Project::getPathsToImages, Project::setPathsToImages);
        getEntityDataBinder().forField(this.videosURLs)
                .bind(Project::getVideoURLs, Project::setVideoURLs);
    }

    @Override
    protected void doSave() throws Exception {
        getEditingContext().save(this.logo, this.pressDocument, this.requirements, this.powerpoint, this.pathsToImages);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.projects.save_success", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.projects.validation_success", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.projects.delete_success2", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.projects.save_error", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.projects.validation_error", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.projects.delete_error2", //$NON-NLS-1$
                getEditedEntity().getAcronymOrScientificTitle(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.descriptionDetails.setSummaryText(getTranslation("views.projects.description_details")); //$NON-NLS-1$
        this.acronym.setLabel(getTranslation("views.projects.acronym")); //$NON-NLS-1$
        this.acronym.setHelperText(getTranslation("views.projects.acronym.help")); //$NON-NLS-1$
        this.scientificTitle.setLabel(getTranslation("views.projects.title")); //$NON-NLS-1$
        this.scientificTitle.setHelperText(getTranslation("views.projects.title.help")); //$NON-NLS-1$
        this.projectStatus.setLabel(getTranslation("views.projects.status")); //$NON-NLS-1$
        this.projectStatus.setHelperText(getTranslation("views.projects.status.help")); //$NON-NLS-1$
        this.projectStatus.setItemLabelGenerator(this::getProjectStatusLabel);
        this.startDate.setLabel(getTranslation("views.projects.start_date")); //$NON-NLS-1$
        this.startDate.setHelperText(getTranslation("views.projects.start_date.help")); //$NON-NLS-1$
        this.duration.setLabel(getTranslation("views.projects.duration")); //$NON-NLS-1$
        this.duration.setHelperText(getTranslation("views.projects.duration.help")); //$NON-NLS-1$

        this.consortiumDetails.setSummaryText(getTranslation("views.projects.consortium_details")); //$NON-NLS-1$
        this.coordinator.setLabel(getTranslation("views.projects.coordinator")); //$NON-NLS-1$
        this.coordinator.setHelperText(getTranslation("views.projects.coordinator.help")); //$NON-NLS-1$
        this.localOrganization.setLabel(getTranslation("views.projects.local_organization")); //$NON-NLS-1$
        this.localOrganization.setHelperText(getTranslation("views.projects.local_organization.help")); //$NON-NLS-1$
        this.superOrganization.setLabel(getTranslation("views.projects.super_organization")); //$NON-NLS-1$
        this.superOrganization.setHelperText(getTranslation("views.projects.super_organization.help")); //$NON-NLS-1$
        this.participants.setLabel(getTranslation("views.projects.members")); //$NON-NLS-1$
        this.otherPartners.setLabel(getTranslation("views.projects.other_partners")); //$NON-NLS-1$
        this.otherPartners.setAdditionTooltip(getTranslation("views.projects.other_partner.insert")); //$NON-NLS-1$
        this.otherPartners.setDeletionTooltip(getTranslation("views.projects.other_partner.delete")); //$NON-NLS-1$
        this.otherPartners.setCreationTooltip(getTranslation("views.projects.other_partner.create")); //$NON-NLS-1$

        this.fundingDetails.setSummaryText(getTranslation("views.projects.funding_details")); //$NON-NLS-1$
        this.budgets.setLabel(getTranslation("views.projects.budgets")); //$NON-NLS-1$
        this.globalBudget.setLabel(getTranslation("views.projects.global_budget")); //$NON-NLS-1$
        this.globalBudget.setSuffixComponent(new Span(getTranslation("views.projects.global_budget.unit"))); //$NON-NLS-1$
        this.globalBudget.setHelperText(getTranslation("views.projects.global_budget.help")); //$NON-NLS-1$
        this.learOrganization.setLabel(getTranslation("views.projects.lear")); //$NON-NLS-1$
        this.learOrganization.setHelperText(getTranslation("views.projects.lear.help")); //$NON-NLS-1$

        this.innovationDetails.setSummaryText(getTranslation("views.projects.innovation_details")); //$NON-NLS-1$
        this.activityType.setLabel(getTranslation("views.projects.activity_type")); //$NON-NLS-1$
        this.activityType.setHelperText(getTranslation("views.projects.activity_type.help")); //$NON-NLS-1$
        this.activityType.setItemLabelGenerator(this::getActivityTypeLabel);
        this.contractType.setLabel(getTranslation("views.projects.contract_type")); //$NON-NLS-1$
        this.contractType.setHelperText(getTranslation("views.projects.contract_type.help")); //$NON-NLS-1$
        this.contractType.setItemLabelGenerator(this::getContractTypeLabel);
        this.trl.setLabel(getTranslation("views.projects.trl")); //$NON-NLS-1$
        this.trl.setHelperText(getTranslation("views.projects.trl.help")); //$NON-NLS-1$
        this.trl.setItemLabelGenerator(this::getTrlLabel);
        this.openSource.setLabel(getTranslation("views.projects.open_source")); //$NON-NLS-1$

        this.presentationDetails.setSummaryText(getTranslation("views.projects.presentation_details")); //$NON-NLS-1$
        this.confidential.setLabel(getTranslation("views.projects.confidential")); //$NON-NLS-1$
        this.description.setLabel(getTranslation("views.projects.description")); //$NON-NLS-1$
        this.description.setHelperText(getTranslation("views.projects.description.help")); //$NON-NLS-1$
        this.requirements.setLabel(getTranslation("views.projects.requirements")); //$NON-NLS-1$
        this.powerpoint.setLabel(getTranslation("views.projects.powerpoint")); //$NON-NLS-1$
        this.scientificAxes.setLabel(getTranslation("views.projects.scientific_axes")); //$NON-NLS-1$
        this.scientificAxes.setAdditionTooltip(getTranslation("views.projects.scientific_axes.insert")); //$NON-NLS-1$
        this.scientificAxes.setDeletionTooltip(getTranslation("views.projects.scientific_axes.delete")); //$NON-NLS-1$
        this.scientificAxes.setCreationTooltip(getTranslation("views.projects.scientific_axes.create")); //$NON-NLS-1$

        this.communicationDetails.setSummaryText(getTranslation("views.projects.communication_details")); //$NON-NLS-1$
        this.logo.setLabel(getTranslation("views.projects.logo")); //$NON-NLS-1$
        this.projectUrl.setLabel(getTranslation("views.projects.url")); //$NON-NLS-1$
        this.projectUrl.setHelperText(getTranslation("views.projects.url.help")); //$NON-NLS-1$
        this.pressDocument.setLabel(getTranslation("views.projects.press_document")); //$NON-NLS-1$
        this.pathsToImages.setLabel(getTranslation("views.projects.images")); //$NON-NLS-1$
        this.pathsToImages.setHelperText(getTranslation("views.projects.images.help")); //$NON-NLS-1$
        this.videosURLs.setLabel(getTranslation("views.projects.video_urls")); //$NON-NLS-1$

        this.webpageConvention.setLabel(getTranslation("views.projects.webpage_convention")); //$NON-NLS-1$
    }

    /**
     * Validator for the global budget of a project.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public class GlobalBudgetValidator extends AbstractValidator<Float> {

        private static final long serialVersionUID = 3784974920132263663L;

        private final String invalidValueErrorMessage;

        /**
         * Constructor.
         *
         * @param rangeErrorMessage        the message to display in case the value does not validate in the given range.
         * @param invalidValueErrorMessage the message to display in case the value is lower to the some of local budgets.
         */
        public GlobalBudgetValidator(String rangeErrorMessage, String invalidValueErrorMessage) {
            super(rangeErrorMessage);
            this.invalidValueErrorMessage = invalidValueErrorMessage;
        }

        @Override
        public ValidationResult apply(Float value, ValueContext context) {
            if (value != null) {
                final var floatValue = value.floatValue();
                if (floatValue < 0f) {
                    return ValidationResult.error(getMessage(value));
                }
                final var localBudgetValue = getEditedEntity().getTotalLocalOrganizationBudget();
                if (floatValue < localBudgetValue) {
                    return ValidationResult.error(getTranslation(this.invalidValueErrorMessage, value, Float.valueOf(localBudgetValue)));
                }
            }
            return ValidationResult.ok();
        }

    }

}
