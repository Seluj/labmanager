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

package fr.utbm.ciad.labmanager.views.components.invitations.editors.regular;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationType;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.validators.DisjointEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullDateValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEnumerationValidator;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;

/**
 * Abstract implementation for the editor of the information related to an incoming invitation.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractIncomingInvitationEditor extends AbstractEntityEditor<PersonInvitation> {

    private static final long serialVersionUID = -4918938392514733579L;
    private final PersonFieldFactory personFieldFactory;
    private DetailsWithErrorMark guestDetails;
    private SinglePersonNameField guest;
    private TextField university;
    private ComboBox<CountryCode> country;
    private DetailsWithErrorMark inviterDetails;
    private SinglePersonNameField inviter;
    private DetailsWithErrorMark informationDetails;
    private ComboBox<PersonInvitationType> type;
    private DatePicker startDate;
    private DatePicker endDate;
    private TextField title;

    /**
     * Constructor.
     *
     * @param context                          the editing context for the person invitation.
     * @param invitationCreationStatusComputer the tool for computer the creation status for the person invitations.
     * @param relinkEntityWhenSaving           indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                         be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param personFieldFactory               the factory for creating the person fields.
     * @param authenticatedUser                the connected user.
     * @param messages                         the accessor to the localized messages (Spring layer).
     * @param properties                       specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractIncomingInvitationEditor(EntityEditingContext<PersonInvitation> context,
                                            EntityCreationStatusComputer<PersonInvitation> invitationCreationStatusComputer,
                                            boolean relinkEntityWhenSaving, PersonFieldFactory personFieldFactory, AuthenticatedUser authenticatedUser,
                                            MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        super(PersonInvitation.class, authenticatedUser, messages, invitationCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .mapToNull(PROP_ADMIN_SECTION)
                        .mapToNull(PROP_ADMIN_VALIDATION_BOX));
        this.personFieldFactory = personFieldFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createGuestDetails(rootContainer);
        createInviterDetails(rootContainer);
        createInformationDetails(rootContainer);
    }

    /**
     * Create the section for editing the description of the guest.
     *
     * @param rootContainer the container.
     */
    protected void createGuestDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.guest = this.personFieldFactory.createSingleNameField(getTranslation("views.incoming_invitation.new_guest"), getLogger()); //$NON-NLS-1$
        this.guest.setRequiredIndicatorVisible(true);
        this.guest.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.guest, 2);

        this.university = new TextField();
        this.university.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        this.university.setRequired(true);
        this.university.setClearButtonVisible(true);
        content.add(this.university, 1);

        this.country = ComponentFactory.newCountryComboBox(getLocale());
        this.country.setRequired(true);
        content.add(this.country, 1);

        this.guestDetails = createDetailsWithErrorMark(rootContainer, content, "guest", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.guest)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.incoming_invitation.guest.error.null"), //$NON-NLS-1$
                        getTranslation("views.incoming_invitation.guest.error.disjoint"), //$NON-NLS-1$
                        this::checkGuestUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.guest, this.guestDetails))
                .bind(PersonInvitation::getGuest, PersonInvitation::setGuest);
        getEntityDataBinder().forField(this.university)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.incoming_invitation.university.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.university, this.guestDetails))
                .bind(PersonInvitation::getUniversity, PersonInvitation::setUniversity);
        getEntityDataBinder().forField(this.country)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.incoming_invitation.country.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.country, this.guestDetails))
                .bind(PersonInvitation::getCountry, PersonInvitation::setCountry);
    }

    private boolean checkGuestUnicity(Person guest) {
        assert guest != null;
        final var inviter = getEditedEntity().getInviter();
        return !guest.equals(inviter);
    }

    /**
     * Create the section for editing the description of the inviter.
     *
     * @param rootContainer the container.
     */
    protected void createInviterDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.inviter = this.personFieldFactory.createSingleNameField(getTranslation("views.incoming_invitation.new_inviter"), getLogger()); //$NON-NLS-1$
        this.inviter.setRequiredIndicatorVisible(true);
        this.inviter.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.inviter, 2);

        this.inviterDetails = createDetailsWithErrorMark(rootContainer, content, "inviter"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.inviter)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.incoming_invitation.inviter.error.null"), //$NON-NLS-1$
                        getTranslation("views.incoming_invitation.inviter.error.disjoint"), //$NON-NLS-1$
                        this::checkInviterUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.inviter, this.inviterDetails))
                .bind(PersonInvitation::getInviter, PersonInvitation::setInviter);
    }

    private boolean checkInviterUnicity(Person inviter) {
        assert inviter != null;
        final var guest = getEditedEntity().getGuest();
        return !inviter.equals(guest);
    }

    /**
     * Create the section for editing the description of the invitation.
     *
     * @param rootContainer the container.
     */
    protected void createInformationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.type = new ComboBox<>();
        this.type.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        this.type.setRequired(true);
        this.type.setItems(Arrays.stream(PersonInvitationType.values()).filter(it -> !it.isOutgoing()).toList());
        this.type.setItemLabelGenerator(this::getTypeLabel);
        content.add(this.type, 2);

        this.startDate = new DatePicker();
        this.startDate.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
        this.startDate.setRequired(true);
        this.startDate.setClearButtonVisible(true);
        content.add(this.startDate, 1);

        this.endDate = new DatePicker();
        this.endDate.setPrefixComponent(VaadinIcon.SIGN_IN_ALT.create());
        this.endDate.setRequired(true);
        this.endDate.setClearButtonVisible(true);
        content.add(this.endDate, 1);

        this.title = new TextField();
        this.title.setPrefixComponent(VaadinIcon.HASH.create());
        this.title.setRequired(true);
        this.title.setClearButtonVisible(true);
        content.add(this.title, 2);

        this.informationDetails = createDetailsWithErrorMark(rootContainer, content, "information"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.type)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.incoming_invitation.type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.type, this.informationDetails))
                .bind(PersonInvitation::getType, PersonInvitation::setType);
        getEntityDataBinder().forField(this.startDate)
                .withValidator(new NotNullDateValidator(getTranslation("views.incoming_invitation.start_date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.startDate, this.informationDetails))
                .bind(PersonInvitation::getStartDate, PersonInvitation::setStartDate);
        getEntityDataBinder().forField(this.endDate)
                .withValidator(new NotNullDateValidator(getTranslation("views.incoming_invitation.end_date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.endDate, this.informationDetails))
                .bind(PersonInvitation::getEndDate, PersonInvitation::setEndDate);
        getEntityDataBinder().forField(this.title)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.incoming_invitation.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.startDate, this.informationDetails))
                .bind(PersonInvitation::getTitle, PersonInvitation::setTitle);
    }

    private String getTypeLabel(PersonInvitationType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.incoming_invitation.save_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.incoming_invitation.validation_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.incoming_invitation.delete_success2", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.incoming_invitation.save_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.incoming_invitation.validation_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.incoming_invitation.delete_error2", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.guestDetails.setSummaryText(getTranslation("views.incoming_invitation.guest_details")); //$NON-NLS-1$
        this.guest.setLabel(getTranslation("views.incoming_invitation.guest")); //$NON-NLS-1$
        this.guest.setHelperText(getTranslation("views.incoming_invitation.guest.help")); //$NON-NLS-1$
        this.university.setLabel(getTranslation("views.incoming_invitation.university")); //$NON-NLS-1$
        this.country.setLabel(getTranslation("views.incoming_invitation.country")); //$NON-NLS-1$
        ComponentFactory.updateCountryComboBoxItems(this.country, getLocale());

        this.inviterDetails.setSummaryText(getTranslation("views.incoming_invitation.inviter_details")); //$NON-NLS-1$
        this.inviter.setLabel(getTranslation("views.incoming_invitation.inviter")); //$NON-NLS-1$
        this.inviter.setHelperText(getTranslation("views.incoming_invitation.inviter.help")); //$NON-NLS-1$

        this.informationDetails.setSummaryText(getTranslation("views.incoming_invitation.information_details")); //$NON-NLS-1$
        this.type.setLabel(getTranslation("views.incoming_invitation.type")); //$NON-NLS-1$
        this.startDate.setLabel(getTranslation("views.incoming_invitation.start_date")); //$NON-NLS-1$
        this.endDate.setLabel(getTranslation("views.incoming_invitation.end_date")); //$NON-NLS-1$
        this.title.setLabel(getTranslation("views.incoming_invitation.title")); //$NON-NLS-1$
    }

}
