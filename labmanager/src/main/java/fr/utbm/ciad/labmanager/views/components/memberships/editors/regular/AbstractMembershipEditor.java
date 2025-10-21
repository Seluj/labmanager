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

package fr.utbm.ciad.labmanager.views.components.memberships.editors.regular;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.validators.DisjointEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullDateValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEnumerationValidator;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.hibernate.Hibernate;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Abstract implementation for the editor of the information related to a membership.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractMembershipEditor extends AbstractEntityEditor<Membership> {

    private static final long serialVersionUID = -592763051466628800L;

    private static final String ANONYMOUS = "?"; //$NON-NLS-1$

    private final boolean editAssociatedPerson;
    private final PersonFieldFactory personFieldFactory;
    private final ScientificAxisService axisService;
    private final OrganizationFieldFactory organizationFieldFactory;
    private final ScientificAxisEditorFactory axisEditorFactory;
    private DetailsWithErrorMark employeeDetails;
    private SinglePersonNameField person;
    private DetailsWithErrorMark employerDetails;
    private SingleOrganizationNameField serviceOrganization;
    private SingleOrganizationNameField employerOrganization;
    private ComboBox<OrganizationAddress> organizationAddress;
    private DetailsWithErrorMark positionDetails;
    private ComboBox<MemberStatus> status;
    private DatePicker since;
    private DatePicker to;
    private ToggleButton permanentPosition;
    private DetailsWithErrorMark sectionDetails;
    private ComboBox<CnuSection> cnu;
    private ComboBox<ConrsSection> conrs;
    private ComboBox<FrenchBap> bap;
    private DetailsWithErrorMark activityDetails;
    private ComboListField<ScientificAxis> scientificAxes;
    private ComboBox<Responsibility> responsibility;
    private ToggleButton publicPosition;
    private ResearchOrganization addressReference;

    /**
     * Constructor.
     *
     * @param context                          the editing context for the membership.
     * @param membershipCreationStatusComputer the tool for computer the creation status for the organization memberships.
     * @param editAssociatedPerson             indicates if the associated person could be edited or not.
     * @param relinkEntityWhenSaving           indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                         be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param personFieldFactory               the factory for creating the person fields.
     * @param organizationFieldFactory         the factory for creating the organization fields.
     * @param axisService                      the service for accessing the JPA entities for scientific axes.
     * @param axisEditorFactory                the factory for creating the scientific axis editors.
     * @param authenticatedUser                the connected user.
     * @param messages                         the accessor to the localized messages (Spring layer).
     * @param properties                       specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractMembershipEditor(EntityEditingContext<Membership> context,
                                    EntityCreationStatusComputer<Membership> membershipCreationStatusComputer,
                                    boolean editAssociatedPerson, boolean relinkEntityWhenSaving, PersonFieldFactory personFieldFactory,
                                    OrganizationFieldFactory organizationFieldFactory, ScientificAxisService axisService,
                                    ScientificAxisEditorFactory axisEditorFactory, AuthenticatedUser authenticatedUser,
                                    MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        super(Membership.class, authenticatedUser, messages,
                membershipCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.membership.administration_details") //$NON-NLS-1$
                        .mapToNull(PROP_ADMIN_VALIDATION_BOX));
        this.editAssociatedPerson = editAssociatedPerson;
        this.personFieldFactory = personFieldFactory;
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;
        this.organizationFieldFactory = organizationFieldFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createEmployeeDetails(rootContainer);
        createEmployerDetails(rootContainer);
        createPositionDetails(rootContainer);
        createSectionDetails(rootContainer);
        createActivityDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer,
                    content -> {
                        this.publicPosition = new ToggleButton();
                        content.add(this.publicPosition, 2);
                        getEntityDataBinder().forField(this.publicPosition)
                                .bind(Membership::isMainPosition, Membership::setMainPosition);
                    },
                    null);
        }
    }

    /**
     * Create the section for editing the description of the employee.
     *
     * @param rootContainer the container.
     */
    protected void createEmployeeDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.person = this.personFieldFactory.createSingleNameField(getTranslation("views.membership.new_person"), getLogger()); //$NON-NLS-1$
        this.person.setReadOnly(!this.editAssociatedPerson);
        this.person.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.person, 2);

        this.employeeDetails = createDetailsWithErrorMark(rootContainer, content, "employee", this.editAssociatedPerson); //$NON-NLS-1$

        getEntityDataBinder().forField(this.person)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.membership.person.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.person, this.employeeDetails))
                .bind(Membership::getPerson, this::setPersonSecurely);
    }

    private void setPersonSecurely(Membership membership, Person person) {
        if (this.editAssociatedPerson) {
            membership.setPerson(person);
        }
    }

    /**
     * Create the section for editing the description of the employer.
     *
     * @param rootContainer the container.
     */
    protected void createEmployerDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.serviceOrganization = this.organizationFieldFactory.createSingleNameField(
                getTranslation("views.membership.new_service"), getLogger(), this::initializeOrganizationJPA); //$NON-NLS-1$
        this.serviceOrganization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        this.serviceOrganization.addValueChangeListener(this::onOrganizationChange);
        content.add(this.serviceOrganization, 2);

        this.employerOrganization = this.organizationFieldFactory.createSingleNameField(
                getTranslation("views.membership.new_organization"), getLogger(), this::initializeOrganizationJPA); //$NON-NLS-1$
        this.employerOrganization.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        this.employerOrganization.addValueChangeListener(this::onOrganizationChange);
        content.add(this.employerOrganization, 2);

        this.organizationAddress = new ComboBox<>();
        this.organizationAddress.setClearButtonVisible(true);
        this.organizationAddress.setItems(Collections.emptyList());
        this.organizationAddress.setItemLabelGenerator(it -> it.getName());
        this.organizationAddress.setPrefixComponent(VaadinIcon.BUILDING.create());
        content.add(this.organizationAddress, 2);

        this.employerDetails = createDetailsWithErrorMark(rootContainer, content, "employer", !this.editAssociatedPerson); //$NON-NLS-1$

        getEntityDataBinder().forField(this.serviceOrganization)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.membership.service.error.null"), //$NON-NLS-1$
                        getTranslation("views.membership.service.error.disjoint"), //$NON-NLS-1$
                        this::checkServiceUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.serviceOrganization, this.employerDetails))
                .bind(Membership::getDirectResearchOrganization, Membership::setDirectResearchOrganization);
        getEntityDataBinder().forField(this.employerOrganization)
                .withValidator(new MembershipSuperOrganizationValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.employerOrganization, this.employerDetails))
                .bind(Membership::getSuperResearchOrganization, Membership::setSuperResearchOrganization);
        getEntityDataBinder().forField(this.organizationAddress)
                .withValidator(new OrganizationAddressValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.organizationAddress, this.employerDetails))
                .bind(Membership::getOrganizationAddress, Membership::setOrganizationAddress);
    }

    private void initializeOrganizationJPA(ResearchOrganization organization) {
        Hibernate.initialize(organization.getAddresses());
    }

    private void onOrganizationChange(ComponentValueChangeEvent<CustomField<ResearchOrganization>, ResearchOrganization> event) {
        final ResearchOrganization newReference;
        final var serviceOrganization = this.serviceOrganization.getValue();
        if (serviceOrganization != null && !serviceOrganization.getAddresses().isEmpty()) {
            newReference = serviceOrganization;
        } else {
            final var employerOrganization = this.serviceOrganization.getValue();
            if (employerOrganization != null && !employerOrganization.getAddresses().isEmpty()) {
                newReference = employerOrganization;
            } else {
                newReference = null;
            }
        }
        if (!Objects.equals(newReference, this.addressReference)) {
            this.addressReference = newReference;
            if (newReference != null) {
                this.organizationAddress.setItems(newReference.getAddresses());
            } else {
                this.organizationAddress.setItems(Collections.emptyList());
            }
        }
    }

    private boolean checkServiceUnicity(ResearchOrganization service) {
        assert service != null;
        final var superOrganization = getEditedEntity().getSuperResearchOrganization();
        return !service.equals(superOrganization);
    }

    /**
     * Create the section for editing the description of the position.
     *
     * @param rootContainer the container.
     */
    protected void createPositionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.status = new ComboBox<>();
        this.status.setItems(MemberStatus.values());
        this.status.setItemLabelGenerator(this::getStatusLabel);
        this.status.setRequired(true);
        this.status.setPrefixComponent(VaadinIcon.DOCTOR.create());
        this.status.addValueChangeListener(it -> {
            final var value = it.getValue();
            //
            // Change the enable state of the other fields that
            // depend on the status of the position
            //
            this.permanentPosition.setEnabled(
                    value != null && value.isPermanentPositionAllowed());
            this.cnu.setEnabled(
                    value != null && value.isPermanentPositionAllowed() && value.isResearcher());
            this.conrs.setEnabled(
                    value != null && value.isPermanentPositionAllowed() && value.isResearcher());
            this.bap.setEnabled(
                    value != null && value.isPermanentPositionAllowed() && (value.isTechnicalStaff() || value.isAdministrativeStaff()));
        });
        content.add(this.status, 2);

        this.since = new DatePicker();
        this.since.setPrefixComponent(VaadinIcon.SIGN_IN_ALT.create());
        this.since.setClearButtonVisible(true);
        content.add(this.since, 1);

        this.to = new DatePicker();
        this.to.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
        this.to.setClearButtonVisible(true);
        content.add(this.to, 1);

        this.permanentPosition = new ToggleButton();
        content.add(this.to, 2);

        this.positionDetails = createDetailsWithErrorMark(rootContainer, content, "position"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.status)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.membership.status.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.status, this.positionDetails))
                .bind(Membership::getMemberStatus, Membership::setMemberStatus);
        getEntityDataBinder().forField(this.since)
                .withValidator(new NotNullDateValidator(getTranslation("views.membership.since.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.since, this.positionDetails))
                .bind(Membership::getMemberSinceWhen, Membership::setMemberSinceWhen);
        getEntityDataBinder().forField(this.to)
                .withValidator(new NotNullDateValidator(getTranslation("views.membership.since.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.to, this.positionDetails))
                .bind(Membership::getMemberToWhen, Membership::setMemberToWhen);
        getEntityDataBinder().forField(this.permanentPosition)
                .bind(Membership::isPermanentPosition, Membership::setPermanentPosition);
    }

    /**
     * Replies the gender of the person associated to the membership.
     *
     * @return the gender. It may be {@code null} if the gender cannot be determined.
     */
    protected Gender getPersonGender() {
        final var person = getEditedEntity().getPerson();
        return person == null ? null : person.getGender();
    }

    private String getStatusLabel(MemberStatus status) {
        return status.getLabel(getMessageSourceAccessor(), getPersonGender(), false, getLocale());
    }

    /**
     * Create the section for editing the description of the section.
     *
     * @param rootContainer the container.
     */
    protected void createSectionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.cnu = new ComboBox<>();
        this.cnu.setItems(CnuSection.values());
        this.cnu.setItemLabelGenerator(this::getCnuLabel);
        this.cnu.setPrefixComponent(VaadinIcon.COMPILE.create());
        content.add(this.cnu, 2);

        this.conrs = new ComboBox<>();
        this.conrs.setItems(ConrsSection.values());
        this.conrs.setItemLabelGenerator(this::getConrsLabel);
        this.conrs.setPrefixComponent(VaadinIcon.COMPILE.create());
        content.add(this.conrs, 2);

        this.bap = new ComboBox<>();
        this.bap.setItems(FrenchBap.values());
        this.bap.setItemLabelGenerator(this::getBapLabel);
        this.bap.setPrefixComponent(VaadinIcon.COMPILE.create());
        content.add(this.bap, 2);

        this.sectionDetails = createDetailsWithErrorMark(rootContainer, content, "section"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.cnu)
                .bind(Membership::getCnuSection, Membership::setCnuSection);
        getEntityDataBinder().forField(this.conrs)
                .bind(Membership::getConrsSection, Membership::setConrsSection);
        getEntityDataBinder().forField(this.bap)
                .bind(Membership::getFrenchBap, Membership::setFrenchBap);
    }

    private String getCnuLabel(CnuSection cnu) {
        return cnu.getNumber() +
                " - " + //$NON-NLS-1$
                cnu.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getConrsLabel(ConrsSection conrs) {
        return conrs.getNumber() +
                " - " + //$NON-NLS-1$
                conrs.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getBapLabel(FrenchBap bap) {
        return bap.getShortName() +
                " - " + //$NON-NLS-1$
                bap.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the description of the activities of the person.
     *
     * @param rootContainer the container.
     */
    protected void createActivityDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        if (isBaseAdmin()) {
            this.responsibility = new ComboBox<>();
            this.responsibility.setItems(Responsibility.values());
            this.responsibility.setItemLabelGenerator(this::getResponsibilityLabel);
            this.responsibility.setPrefixComponent(VaadinIcon.TASKS.create());
            content.add(this.responsibility, 2);
        }

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

        this.activityDetails = createDetailsWithErrorMark(rootContainer, content, "activity"); //$NON-NLS-1$

        if (this.responsibility != null) {
            getEntityDataBinder().forField(this.responsibility)
                    .bind(Membership::getResponsibility, Membership::setResponsibility);
        }
        getEntityDataBinder().forField(this.scientificAxes).bind(Membership::getScientificAxes, Membership::setScientificAxes);
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

    private String getResponsibilityLabel(Responsibility responsability) {
        return responsability.getLabel(getMessageSourceAccessor(), getPersonGender(), getLocale());
    }

    @Override
    protected String computeSavingSuccessMessage() {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.save_success", name); //$NON-NLS-1$
    }

    @Override
    protected String computeValidationSuccessMessage() {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.validation_success", name); //$NON-NLS-1$
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.delete_success2", name); //$NON-NLS-1$
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.save_error", name); //$NON-NLS-1$
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.validation_error", name, error.getLocalizedMessage()); //$NON-NLS-1$
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        final var person = getEditedEntity().getPerson();
        final var name = person != null ? person.getFullName() : ANONYMOUS;
        return getTranslation("views.membership.deletion_error2", name, error.getLocalizedMessage()); //$NON-NLS-1$
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.employeeDetails.setSummaryText(getTranslation("views.membership.employee_details")); //$NON-NLS-1$
        this.person.setLabel(getTranslation("views.membership.person")); //$NON-NLS-1$
        if (!this.person.isReadOnly()) {
            this.person.setHelperText(getTranslation("views.membership.person.help")); //$NON-NLS-1$
        }

        this.employerDetails.setSummaryText(getTranslation("views.membership.employer_details")); //$NON-NLS-1$
        this.serviceOrganization.setLabel(getTranslation("views.membership.service")); //$NON-NLS-1$
        this.serviceOrganization.setPlaceholder(getTranslation("views.membership.service.placeholder")); //$NON-NLS-1$
        this.serviceOrganization.setHelperText(getTranslation("views.membership.service.help")); //$NON-NLS-1$
        this.employerOrganization.setLabel(getTranslation("views.membership.organization")); //$NON-NLS-1$
        this.employerOrganization.setPlaceholder(getTranslation("views.membership.organization.placeholder")); //$NON-NLS-1$
        this.employerOrganization.setHelperText(getTranslation("views.membership.organization.help")); //$NON-NLS-1$
        this.organizationAddress.setLabel(getTranslation("views.membership.address")); //$NON-NLS-1$
        this.organizationAddress.setHelperText(getTranslation("views.membership.address.help")); //$NON-NLS-1$

        this.positionDetails.setSummaryText(getTranslation("views.membership.position_details")); //$NON-NLS-1$
        this.status.setLabel(getTranslation("views.membership.status")); //$NON-NLS-1$
        this.status.setItemLabelGenerator(this::getStatusLabel);
        this.since.setLabel(getTranslation("views.membership.since")); //$NON-NLS-1$
        this.to.setLabel(getTranslation("views.membership.to")); //$NON-NLS-1$
        this.permanentPosition.setLabel(getTranslation("views.membership.permanent_position")); //$NON-NLS-1$

        this.sectionDetails.setSummaryText(getTranslation("views.membership.section_details")); //$NON-NLS-1$
        this.cnu.setLabel(getTranslation("views.membership.cnu")); //$NON-NLS-1$
        this.cnu.setHelperText(getTranslation("views.membership.cnu.help")); //$NON-NLS-1$
        this.conrs.setLabel(getTranslation("views.membership.conrs")); //$NON-NLS-1$
        this.conrs.setHelperText(getTranslation("views.membership.conrs.help")); //$NON-NLS-1$
        this.bap.setLabel(getTranslation("views.membership.bap")); //$NON-NLS-1$
        this.bap.setHelperText(getTranslation("views.membership.bap.help")); //$NON-NLS-1$

        this.activityDetails.setSummaryText(getTranslation("views.membership.activity_details")); //$NON-NLS-1$
        if (this.responsibility != null) {
            this.responsibility.setLabel(getTranslation("views.membership.responsibility")); //$NON-NLS-1$
        }
        this.scientificAxes.setLabel(getTranslation("views.membership.scientific_axes")); //$NON-NLS-1$
        this.scientificAxes.setAdditionTooltip(getTranslation("views.membership.scientific_axes.insert")); //$NON-NLS-1$
        this.scientificAxes.setDeletionTooltip(getTranslation("views.membership.scientific_axes.delete")); //$NON-NLS-1$
        this.scientificAxes.setCreationTooltip(getTranslation("views.membership.scientific_axes.create")); //$NON-NLS-1$

        this.publicPosition.setLabel(getTranslation("views.membership.public_position")); //$NON-NLS-1$
    }

    /**
     * A validator for the super organization in organization memberships.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class MembershipSuperOrganizationValidator implements Validator<ResearchOrganization> {

        private static final long serialVersionUID = -5928220978769779047L;

        /**
         * Constructor.
         */
        protected MembershipSuperOrganizationValidator() {
            //
        }

        @Override
        public String toString() {
            return "MembershipSuperOrganizationValidator"; //$NON-NLS-1$
        }

        @Override
        public ValidationResult apply(ResearchOrganization value, ValueContext context) {
            final var service = getEditedEntity().getDirectResearchOrganization();
            if (value != null) {
                if (service.getType().isEmployer()) {
                    if (value.getType().isEmployer()) {
                        return ValidationResult.error(getTranslation("views.membership.organization.error.both_employers")); //$NON-NLS-1$
                    }
                    return ValidationResult.error(getTranslation("views.membership.organization.error.invalid_employer")); //$NON-NLS-1$
                }
                if (!value.getType().isEmployer()) {
                    return ValidationResult.error(getTranslation("views.membership.organization.error.no_employer")); //$NON-NLS-1$
                }
                if (!service.isSubOrganizationOf(value)) {
                    return ValidationResult.error(getTranslation("views.membership.organization.error.no_sub_organization")); //$NON-NLS-1$
                }
            } else {
                if (service == null) {
                    return ValidationResult.error(getTranslation("views.membership.organization.error.no_service")); //$NON-NLS-1$
                }
                if (!service.getType().isEmployer()) {
                    return ValidationResult.error(getTranslation("views.membership.organization.error.no_employer")); //$NON-NLS-1$
                }
            }
            return ValidationResult.ok();
        }

    }

    /**
     * A validator for the organization address in a membership.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class OrganizationAddressValidator implements Validator<OrganizationAddress> {

        private static final long serialVersionUID = -7982111295740316957L;

        /**
         * Constructor.
         */
        protected OrganizationAddressValidator() {
            //
        }

        @Override
        public String toString() {
            return "OrganizationAddressValidator"; //$NON-NLS-1$
        }

        @Override
        public ValidationResult apply(OrganizationAddress value, ValueContext context) {
            if (value != null) {
                final var service = getEditedEntity().getDirectResearchOrganization();
                final var employer = getEditedEntity().getSuperResearchOrganization();
                if (service != null && employer != null) {
                    if (!service.getAddresses().contains(value) && !employer.getAddresses().contains(value)) {
                        ValidationResult.error(getTranslation("views.membership.address.error.invalid_address2")); //$NON-NLS-1$
                    }
                } else if (service != null) {
                    if (!service.getAddresses().contains(value)) {
                        ValidationResult.error(getTranslation("views.membership.address.error.invalid_address0")); //$NON-NLS-1$
                    }
                } else if (employer != null) {
                    if (!employer.getAddresses().contains(value)) {
                        ValidationResult.error(getTranslation("views.membership.address.error.invalid_address1")); //$NON-NLS-1$
                    }
                } else {
                    ValidationResult.error(getTranslation("views.membership.address.error.no_orgnaization")); //$NON-NLS-1$
                }
            }
            return ValidationResult.ok();
        }

    }

}
