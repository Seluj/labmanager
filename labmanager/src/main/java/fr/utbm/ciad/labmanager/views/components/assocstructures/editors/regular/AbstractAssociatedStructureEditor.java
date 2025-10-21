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

package fr.utbm.ciad.labmanager.views.components.assocstructures.editors.regular;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.FloatRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureType;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.project.ProjectNameComparator;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.project.ProjectService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.DoubleToFloatConverter;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullDateValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEnumerationValidator;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.assocstructures.fields.AssociatedStructureFieldFactory;
import fr.utbm.ciad.labmanager.views.components.assocstructures.fields.AssociatedStructureHolderListGridField;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.projects.editors.ProjectEditorFactory;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Abstract implementation for the editor of the information related to an associated structure.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractAssociatedStructureEditor extends AbstractEntityEditor<AssociatedStructure> {

    private static final long serialVersionUID = -4585533792941598627L;
    private final ProjectService projectService;
    private final ProjectEditorFactory projectEditorFactory;
    private final ResearchOrganizationService organizationService;
    private final OrganizationFieldFactory organizationFieldFactory;
    private final AssociatedStructureFieldFactory structureFieldFactory;
    private DetailsWithErrorMark descriptionDetails;
    private TextField acronym;
    private TextField name;
    private ComboBox<AssociatedStructureType> type;
    private DetailsWithErrorMark creationDetails;
    private DatePicker creationDate;
    private IntegerField creationDuration;
    private AssociatedStructureHolderListGridField holders;
    private DetailsWithErrorMark fundingDetails;
    private NumberField budget;
    private SingleOrganizationNameField fundingOrganization;
    private DetailsWithErrorMark projectDetails;
    private ComboListField<Project> projects;
    private DetailsWithErrorMark communicationDetails;
    private ToggleButton confidential;
    private MarkdownField description;

    /**
     * Constructor.
     *
     * @param context                         the context for editing the entity.
     * @param structureCreationStatusComputer the tool for computer the creation status for the associated structures.
     * @param relinkEntityWhenSaving          indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                        be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param structureFieldFactory           the factory for creatin the structure fields.
     * @param projectService                  the service for accessing the JPA entities for projects.
     * @param projectEditorFactory            the factory for creating the project editors.
     * @param organizationService             the service for accessing the JPA entities for research organizations.
     * @param authenticatedUser               the connected user.
     * @param organizationFieldFactory        the factory for creating the organization fields.
     * @param messages                        the accessor to the localized messages (Spring layer).
     * @param properties                      specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractAssociatedStructureEditor(EntityEditingContext<AssociatedStructure> context,
                                             EntityCreationStatusComputer<AssociatedStructure> structureCreationStatusComputer,
                                             boolean relinkEntityWhenSaving, AssociatedStructureFieldFactory structureFieldFactory,
                                             ProjectService projectService, ProjectEditorFactory projectEditorFactory,
                                             ResearchOrganizationService organizationService, AuthenticatedUser authenticatedUser,
                                             OrganizationFieldFactory organizationFieldFactory, MessageSourceAccessor messages,
                                             ConstructionPropertiesBuilder properties) {
        super(AssociatedStructure.class, authenticatedUser, messages,
                structureCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.associated_structure.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.associated_structure.administration.validated_structure")); //$NON-NLS-1$
        this.structureFieldFactory = structureFieldFactory;
        this.projectService = projectService;
        this.projectEditorFactory = projectEditorFactory;
        this.organizationService = organizationService;
        this.organizationFieldFactory = organizationFieldFactory;
    }

    private static Specification<Project> createProjectFilter(Optional<String> filter) {
        if (filter.isPresent()) {
            return (root, query, criteriaBuilder) ->
                    ComponentFactory.newPredicateContainsOneOf(filter.get(), root, query, criteriaBuilder,
                            (keyword, predicates, root0, criteriaBuilder0) -> {
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("acronym")), keyword)); //$NON-NLS-1$
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("scientificTitle")), keyword)); //$NON-NLS-1$
                            });
        }
        return null;
    }

    private static void initializeJPA(Project project) {
        Hibernate.initialize(project.getCoordinator());
        Hibernate.initialize(project.getSuperOrganization());
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        createCreationDetails(rootContainer);
        createFundingDetails(rootContainer);
        createProjectDetails(rootContainer);
        createCommunicationDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer, it -> it.bind(AssociatedStructure::isValidated, AssociatedStructure::setValidated));
        }
    }

    /**
     * Create the section for editing the description of the associated structure.
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

        this.name = new TextField();
        this.name.setPrefixComponent(VaadinIcon.HASH.create());
        this.name.setRequired(true);
        this.name.setClearButtonVisible(true);
        content.add(this.name, 2);

        this.type = new ComboBox<>();
        this.type.setPrefixComponent(VaadinIcon.FACTORY.create());
        this.type.setItems(AssociatedStructureType.getAllDisplayTypes(getMessageSourceAccessor(), getLocale()));
        this.type.setItemLabelGenerator(this::getTypeLabel);
        this.type.setValue(AssociatedStructureType.PRIVATE_COMPANY);
        content.add(this.type, 2);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.associated_structure.acronym.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.acronym, this.descriptionDetails))
                .bind(AssociatedStructure::getAcronym, AssociatedStructure::setAcronym);
        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.associated_structure.name.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.name, this.descriptionDetails))
                .bind(AssociatedStructure::getName, AssociatedStructure::setName);
        getEntityDataBinder().forField(this.type)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.associated_structure.type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.type, this.descriptionDetails))
                .bind(AssociatedStructure::getType, AssociatedStructure::setType);
    }

    private String getTypeLabel(AssociatedStructureType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the creation of the associated structure.
     *
     * @param rootContainer the container.
     */
    protected void createCreationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.creationDate = new DatePicker();
        this.creationDate.setPrefixComponent(VaadinIcon.CALENDAR_O.create());
        this.creationDate.setRequired(true);
        this.creationDate.setClearButtonVisible(true);
        content.add(this.creationDate, 1);

        this.creationDuration = new IntegerField();
        this.creationDuration.setPrefixComponent(VaadinIcon.TIMER.create());
        this.creationDuration.setRequired(true);
        this.creationDuration.setClearButtonVisible(true);
        content.add(this.creationDuration, 1);

        this.holders = this.structureFieldFactory.createHolderField(getLogger());
        content.add(this.holders, 2);

        this.creationDetails = createDetailsWithErrorMark(rootContainer, content, "creation"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.creationDate)
                .withValidator(new NotNullDateValidator(getTranslation("views.associated_structure.creation_date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.creationDate, this.creationDetails))
                .bind(AssociatedStructure::getCreationDate, AssociatedStructure::setCreationDate);
        getEntityDataBinder().forField(this.creationDuration)
                .withValidator(new IntegerRangeValidator(getTranslation("views.associated_structure.creation_duration.error"), Integer.valueOf(0), null)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.creationDuration, this.creationDetails))
                .bind(AssociatedStructure::getCreationDuration, AssociatedStructure::setCreationDuration);
        getEntityDataBinder().forField(this.holders)
                .bind(AssociatedStructure::getHolders, AssociatedStructure::setHolders);
    }

    /**
     * Create the section for editing the funding of the associated structure.
     *
     * @param rootContainer the container.
     */
    protected void createFundingDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.budget = new NumberField();
        this.budget.setPrefixComponent(VaadinIcon.EURO.create());
        this.budget.setClearButtonVisible(true);
        content.add(this.budget, 2);

        this.fundingOrganization = this.organizationFieldFactory.createSingleNameField(
                getTranslation("views.associated_structure.new_funding_organization"), getLogger(), null); //$NON-NLS-1$
        this.fundingOrganization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        content.add(this.fundingOrganization, 2);

        this.fundingDetails = createDetailsWithErrorMark(rootContainer, content, "funding"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.budget)
                .withConverter(new DoubleToFloatConverter())
                .withValidator(new FloatRangeValidator(getTranslation("views.associated_structure.budget.error"), Float.valueOf(0f), null)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.budget, this.fundingDetails))
                .bind(AssociatedStructure::getBudget, AssociatedStructure::setBudget);
        getEntityDataBinder().forField(this.fundingOrganization)
                .withValidator(new FundingOrganizationValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.fundingOrganization, this.fundingDetails))
                .bind(AssociatedStructure::getFundingOrganization, AssociatedStructure::setFundingOrganization);
    }

    /**
     * Create the section for editing the projects of the associated structure.
     *
     * @param rootContainer the container.
     */
    protected void createProjectDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.projects = new ComboListField<>(
                ComponentFactory.toSerializableComparator(new ProjectNameComparator()),
                this::openProjectEditor);
        this.projects.setEntityRenderers(
                it -> it.getAcronym(),
                new ComponentRenderer<>(this::createProjectNameComponent),
                new ComponentRenderer<>(this::createProjectNameComponent));
        this.projects.setAvailableEntities(query -> {
            return this.projectService.getAllProjects(
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    createProjectFilter(query.getFilter()),
                    AbstractAssociatedStructureEditor::initializeJPA).stream();
        });
        content.add(this.projects, 2);

        this.projectDetails = createDetailsWithErrorMark(rootContainer, content, "project"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.projects)
                .bind(AssociatedStructure::getProjects, AssociatedStructure::setProjects);
    }

    private Component createProjectNameComponent(Project project) {
        final var coordinator = project.getCoordinator();
        final var superOrganization = project.getSuperOrganization();
        final String label;
        if (coordinator != null) {
            final var buffer = new StringBuilder().append(project.getAcronym()).append(" (") //$NON-NLS-1$
                    .append(coordinator.getAcronym());
            if (superOrganization != null) {
                buffer.append(" - ").append(superOrganization.getAcronym()); //$NON-NLS-1$
            }
            label = buffer.append(")").toString(); //$NON-NLS-1$
        } else if (superOrganization != null) {
            label = project.getAcronym() + " (" + //$NON-NLS-1$
                    superOrganization.getAcronym() + ")"; //$NON-NLS-1$
        } else {
            label = project.getAcronym();
        }
        return new Span(label);
    }

    /**
     * Invoked for creating a new project.
     *
     * @param saver the callback that is invoked when the project is saved as JPA entity.
     */
    protected void openProjectEditor(Consumer<Project> saver) {
        final var newProject = new Project();
        final var editor = this.projectEditorFactory.createAdditionEditor(newProject, getLogger());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.associated_structure.projects.create"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    /**
     * Create the section for editing the communication of the associated structure.
     *
     * @param rootContainer the container.
     */
    protected void createCommunicationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.confidential = new ToggleButton();
        content.add(this.confidential, 2);

        this.description = new MarkdownField();
        content.add(this.description, 2);

        this.communicationDetails = createDetailsWithErrorMark(rootContainer, content, "communication"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.confidential)
                .bind(AssociatedStructure::isConfidential, AssociatedStructure::setConfidential);
        getEntityDataBinder().forField(this.description)
                .withConverter(new StringTrimer())
                .bind(AssociatedStructure::getDescription, AssociatedStructure::setDescription);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.associated_structure.save_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.associated_structure.validation_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.associated_structure.delete_success2", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.associated_structure.save_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.associated_structure.validation_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.associated_structure.delete_error2", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        this.descriptionDetails.setSummaryText(getTranslation("views.associated_structure.description_informations")); //$NON-NLS-1$
        this.acronym.setLabel(getTranslation("views.associated_structure.acronym")); //$NON-NLS-1$
        this.name.setLabel(getTranslation("views.associated_structure.name")); //$NON-NLS-1$
        this.type.setLabel(getTranslation("views.associated_structure.type")); //$NON-NLS-1$
        this.type.setItemLabelGenerator(this::getTypeLabel);

        this.creationDetails.setSummaryText(getTranslation("views.associated_structure.creation_informations")); //$NON-NLS-1$
        this.creationDate.setLabel(getTranslation("views.associated_structure.creation_date")); //$NON-NLS-1$
        this.creationDate.setHelperText(getTranslation("views.associated_structure.creation_date.help")); //$NON-NLS-1$
        this.creationDuration.setLabel(getTranslation("views.associated_structure.creation_duration")); //$NON-NLS-1$
        this.creationDuration.setHelperText(getTranslation("views.associated_structure.creation_duration.help")); //$NON-NLS-1$
        this.holders.setLabel(getTranslation("views.associated_structure.holders")); //$NON-NLS-1$
        this.holders.setHelperText(getTranslation("views.associated_structure.holders.help", //$NON-NLS-1$
                this.organizationService.getApplicationConstants().getDefaultOrganization(),
                this.organizationService.getApplicationConstants().getDefaultSuperOrganization()));

        this.fundingDetails.setSummaryText(getTranslation("views.associated_structure.funding_informations")); //$NON-NLS-1$
        this.budget.setLabel(getTranslation("views.associated_structure.budget")); //$NON-NLS-1$
        this.budget.setHelperText(getTranslation("views.associated_structure.budget.help")); //$NON-NLS-1$
        this.fundingOrganization.setLabel(getTranslation("views.associated_structure.funding_organization")); //$NON-NLS-1$
        this.fundingOrganization.setHelperText(getTranslation("views.associated_structure.funding_organization.help")); //$NON-NLS-1$

        this.projectDetails.setSummaryText(getTranslation("views.associated_structure.project_informations")); //$NON-NLS-1$

        this.communicationDetails.setSummaryText(getTranslation("views.associated_structure.communication_informations")); //$NON-NLS-1$
        this.confidential.setLabel(getTranslation("views.associated_structure.confidential")); //$NON-NLS-1$
        this.description.setLabel(getTranslation("views.associated_structure.description")); //$NON-NLS-1$
        this.description.setHelperText(getTranslation("views.associated_structure.description.help")); //$NON-NLS-1$
    }

    /**
     * A validator that matches funding organization for an associated structure.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private class FundingOrganizationValidator implements Validator<ResearchOrganization> {

        private static final long serialVersionUID = 8523364404465641409L;

        /**
         * Constructor.
         */
        FundingOrganizationValidator() {
            //
        }

        @Override
        public String toString() {
            return "FundingOrganizationValidator"; //$NON-NLS-1$
        }

        /**
         * Returns the error message.
         */
        protected String getErrorMessage() {
            return getTranslation("views.associated_structure.funding_organization.error"); //$NON-NLS-1$
        }

        /**
         * Replies if the given research organization is valid.
         *
         * @param value the value to check.
         * @return {@code true} if the value is valid.
         */
        protected boolean isValid(ResearchOrganization value) {
            if (value == null) {
                final var budget = getEditedEntity().getBudget();
                return budget <= 0f;
            }
            return true;
        }

        @Override
        public ValidationResult apply(ResearchOrganization value, ValueContext context) {
            if (isValid(value)) {
                return ValidationResult.ok();
            }
            return ValidationResult.error(getErrorMessage());
        }

    }

}
