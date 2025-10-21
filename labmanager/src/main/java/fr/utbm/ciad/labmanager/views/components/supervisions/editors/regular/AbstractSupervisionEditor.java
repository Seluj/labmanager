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

package fr.utbm.ciad.labmanager.views.components.supervisions.editors.regular;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEnumerationValidator;
import fr.utbm.ciad.labmanager.views.components.memberships.fields.MembershipFieldFactory;
import fr.utbm.ciad.labmanager.views.components.memberships.fields.SingleMembershipNameField;
import fr.utbm.ciad.labmanager.views.components.supervisions.fields.SupervisionFieldFactory;
import fr.utbm.ciad.labmanager.views.components.supervisions.fields.SupervisorListGridField;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Abstract implementation for the editor of the information related to a supervision.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractSupervisionEditor extends AbstractEntityEditor<Supervision> {

    private static final long serialVersionUID = 7189628237085364285L;

    private final SupervisionFieldFactory supervisionFieldFactory;

    private final MembershipFieldFactory membershipFieldFactory;

    private DetailsWithErrorMark supervisedWorkDetails;

    private SingleMembershipNameField supervisedPerson;

    private TextField title;

    private DetailsWithErrorMark supervisorsDetails;

    private SupervisorListGridField supervisors;

    private DetailsWithErrorMark fundDetails;

    private ComboBox<FundingScheme> funding;

    private TextField fundingDetails;

    private ToggleButton jointPosition;

    private ToggleButton entrepreneur;

    private DetailsWithErrorMark defenseDetails;

    private DatePicker defenseDate;

    private DetailsWithErrorMark afterDefenseDetails;

    private ToggleButton abandonment;

    private TextField positionAfterSupervision;

    private IntegerField numberOfAterPositions;

    /**
     * Constructor.
     *
     * @param context                           the editing context for the supervisison.
     * @param relinkEntityWhenSaving            indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                          be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param supervisionCreationStatusComputer the tool for computer the creation status for the person supervisions.
     * @param membershipFieldFactory            the factory for creating the person membership fields.
     * @param supervisionFieldFactory           the factory for creating the supervision fields.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     * @param properties                        specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractSupervisionEditor(EntityEditingContext<Supervision> context, boolean relinkEntityWhenSaving,
                                     EntityCreationStatusComputer<Supervision> supervisionCreationStatusComputer,
                                     MembershipFieldFactory membershipFieldFactory, SupervisionFieldFactory supervisionFieldFactory,
                                     AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                     ConstructionPropertiesBuilder properties) {
        super(Supervision.class, authenticatedUser, messages,
                supervisionCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .mapToNull(PROP_ADMIN_SECTION)
                        .mapToNull(PROP_ADMIN_VALIDATION_BOX));
        this.membershipFieldFactory = membershipFieldFactory;
        this.supervisionFieldFactory = supervisionFieldFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createSupervisedWorkDetails(rootContainer);
        createSupervisorDetails(rootContainer);
        createFundDetails(rootContainer);
        createDefenseDetails(rootContainer);
        createAfterDefenseDetails(rootContainer);
    }

    /**
     * Create the section for editing the description of the supervised work.
     *
     * @param rootContainer the container.
     */
    protected void createSupervisedWorkDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.supervisedPerson = this.membershipFieldFactory.createSingleNameField(
                getTranslation("views.supervision.new_membership"), getLogger(), () -> getLocale(), //$NON-NLS-1$
                null);
        this.supervisedPerson.setPrefixComponent(VaadinIcon.USER.create());
        this.supervisedPerson.setRequiredIndicatorVisible(true);
        content.add(this.supervisedPerson, 2);

        this.title = new TextField();
        this.title.setPrefixComponent(VaadinIcon.HASH.create());
        this.title.setRequired(true);
        this.title.setClearButtonVisible(true);
        content.add(this.title, 2);

        this.supervisedWorkDetails = createDetailsWithErrorMark(rootContainer, content, "supervisedWork", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.supervisedPerson)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.supervision.supervised_person.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.supervisedPerson, this.supervisedWorkDetails))
                .bind(Supervision::getSupervisedPerson, Supervision::setSupervisedPerson);
        getEntityDataBinder().forField(this.title)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.supervision.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.title, this.supervisedWorkDetails))
                .bind(Supervision::getTitle, Supervision::setTitle);
    }

    /**
     * Create the section for editing the description of the supervisors.
     *
     * @param rootContainer the container.
     */
    protected void createSupervisorDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.supervisors = this.supervisionFieldFactory.createSupervisorField(getLogger());
        content.add(this.supervisors, 2);

        this.supervisorsDetails = createDetailsWithErrorMark(rootContainer, content, "supervisors"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.supervisors)
                .withValidator(this.supervisors.newStandardValidator())
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.supervisors, this.supervisorsDetails))
                .bind(Supervision::getSupervisors, Supervision::setSupervisors);
    }

    /**
     * Create the section for editing the description of the funding.
     *
     * @param rootContainer the container.
     */
    protected void createFundDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.funding = new ComboBox<>();
        this.funding.setRequired(true);
        this.funding.setItems(FundingScheme.values());
        this.funding.setItemLabelGenerator(this::getFundingSchemeLabel);
        this.funding.setPrefixComponent(VaadinIcon.EURO.create());
        this.funding.setClearButtonVisible(true);
        content.add(this.funding, 1);

        this.fundingDetails = new TextField();
        this.fundingDetails.setPrefixComponent(VaadinIcon.INFO.create());
        content.add(this.fundingDetails, 1);

        this.jointPosition = new ToggleButton();
        content.add(this.jointPosition, 1);

        this.entrepreneur = new ToggleButton();
        content.add(this.entrepreneur, 1);

        this.fundDetails = createDetailsWithErrorMark(rootContainer, content, "fund"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.title)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.supervision.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.title, this.supervisedWorkDetails))
                .bind(Supervision::getTitle, Supervision::setTitle);
        getEntityDataBinder().forField(this.funding)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.supervision.funding_scheme.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.funding, this.fundDetails))
                .bind(Supervision::getFunding, Supervision::setFunding);
        getEntityDataBinder().forField(this.fundingDetails)
                .withConverter(new StringTrimer())
                .bind(Supervision::getFundingDetails, Supervision::setFundingDetails);
        getEntityDataBinder().forField(this.jointPosition)
                .bind(Supervision::isJointPosition, Supervision::setJointPosition);
        getEntityDataBinder().forField(this.entrepreneur)
                .bind(Supervision::isEntrepreneur, Supervision::setEntrepreneur);
    }

    private String getFundingSchemeLabel(FundingScheme scheme) {
        return scheme.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the description of the defense.
     *
     * @param rootContainer the container.
     */
    protected void createDefenseDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.defenseDate = new DatePicker();
        this.defenseDate.setPrefixComponent(VaadinIcon.CALENDAR_O.create());
        this.defenseDate.setClearButtonVisible(true);
        content.add(this.defenseDate, 2);

        this.defenseDetails = createDetailsWithErrorMark(rootContainer, content, "defense"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.defenseDate)
                .bind(Supervision::getDefenseDate, Supervision::setDefenseDate);
    }

    /**
     * Create the section for editing the description of the period after the defense.
     *
     * @param rootContainer the container.
     */
    protected void createAfterDefenseDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.abandonment = new ToggleButton();
        content.add(this.abandonment, 2);

        this.positionAfterSupervision = new TextField();
        this.positionAfterSupervision.setPrefixComponent(VaadinIcon.LAPTOP.create());
        this.positionAfterSupervision.setClearButtonVisible(true);
        content.add(this.positionAfterSupervision, 2);

        this.numberOfAterPositions = new IntegerField();
        this.numberOfAterPositions.setMin(0);
        this.numberOfAterPositions.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        this.numberOfAterPositions.setClearButtonVisible(true);
        content.add(this.numberOfAterPositions, 2);

        this.afterDefenseDetails = createDetailsWithErrorMark(rootContainer, content, "future"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.abandonment)
                .bind(Supervision::isAbandonment, Supervision::setAbandonment);
        getEntityDataBinder().forField(this.positionAfterSupervision)
                .withConverter(new StringTrimer())
                .bind(Supervision::getPositionAfterSupervision, Supervision::setPositionAfterSupervision);
        getEntityDataBinder().forField(this.numberOfAterPositions)
                .withValidator(new IntegerRangeValidator(getTranslation("views.supervision.ater_count.error"), Integer.valueOf(0), null)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.numberOfAterPositions, this.afterDefenseDetails))
                .bind(Supervision::getNumberOfAterPositions, Supervision::setNumberOfAterPositions);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.supervision.save_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.supervision.validation_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.supervision.delete_success2", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.supervision.save_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.supervision.validation_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.supervision.delete_error2", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.supervisedWorkDetails.setSummaryText(getTranslation("views.supervision.supervised_work_details")); //$NON-NLS-1$
        this.supervisedPerson.setLabel(getTranslation("views.supervision.supervised_person")); //$NON-NLS-1$
        this.supervisedPerson.setHelperText(getTranslation("views.supervision.supervised_person.help")); //$NON-NLS-1$
        this.title.setLabel(getTranslation("views.supervision.title")); //$NON-NLS-1$

        this.supervisorsDetails.setSummaryText(getTranslation("views.supervision.supervisor_details")); //$NON-NLS-1$
        this.supervisors.setLabel(getTranslation("views.supervision.supervisors")); //$NON-NLS-1$
        this.supervisors.setHelperText(getTranslation("views.supervision.supervisors.help")); //$NON-NLS-1$

        this.fundDetails.setSummaryText(getTranslation("views.supervision.fund_details")); //$NON-NLS-1$
        this.funding.setLabel(getTranslation("views.supervision.funding_scheme")); //$NON-NLS-1$
        this.fundingDetails.setLabel(getTranslation("views.supervision.funding_details")); //$NON-NLS-1$
        this.fundingDetails.setHelperText(getTranslation("views.supervision.funding_details.help")); //$NON-NLS-1$
        this.jointPosition.setLabel(getTranslation("views.supervision.joint_position")); //$NON-NLS-1$
        this.entrepreneur.setLabel(getTranslation("views.supervision.entrepreneur")); //$NON-NLS-1$

        this.defenseDetails.setSummaryText(getTranslation("views.supervision.defense_details")); //$NON-NLS-1$
        this.defenseDate.setLabel(getTranslation("views.supervision.defense_date")); //$NON-NLS-1$

        this.afterDefenseDetails.setSummaryText(getTranslation("views.supervision.after_defense_details")); //$NON-NLS-1$
        this.abandonment.setLabel(getTranslation("views.supervision.abandonment")); //$NON-NLS-1$
        this.positionAfterSupervision.setLabel(getTranslation("views.supervision.position_after_supervision")); //$NON-NLS-1$
        this.positionAfterSupervision.setHelperText(getTranslation("views.supervision.position_after_supervision.help")); //$NON-NLS-1$
        this.numberOfAterPositions.setLabel(getTranslation("views.supervision.ater_count")); //$NON-NLS-1$
        this.numberOfAterPositions.setHelperText(getTranslation("views.supervision.ater_count.help")); //$NON-NLS-1$
    }

}
