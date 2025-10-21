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

package fr.utbm.ciad.labmanager.views.components.jurys.editors.regular;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipType;
import fr.utbm.ciad.labmanager.data.jury.JuryType;
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
import fr.utbm.ciad.labmanager.views.components.addons.validators.*;
import fr.utbm.ciad.labmanager.views.components.persons.fields.MultiPersonNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Abstract implementation for the editor of the information related to a jury.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractJuryMembershipEditor extends AbstractEntityEditor<JuryMembership> {

    private static final long serialVersionUID = -5831566011340136766L;
    private final PersonFieldFactory personFieldFactory;
    private DetailsWithErrorMark defenseDetails;
    private SinglePersonNameField candidate;
    private TextField title;
    private ComboBox<JuryType> defenseType;
    private DatePicker date;
    private DetailsWithErrorMark institutionDetails;
    private TextField university;
    private ComboBox<CountryCode> country;
    private DetailsWithErrorMark juryDetails;
    private MultiPersonNameField promoters;
    private SinglePersonNameField person;
    private ComboBox<JuryMembershipType> type;

    /**
     * Constructor.
     *
     * @param context                    the editing context for the jury membership.
     * @param juryCreationStatusComputer the tool for computer the creation status for the jury memberships.
     * @param relinkEntityWhenSaving     indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                   be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param personFieldFactory         the factory for creating the person fields.
     * @param authenticatedUser          the connected user.
     * @param messages                   the accessor to the localized messages (Spring layer).
     * @param properties                 specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractJuryMembershipEditor(EntityEditingContext<JuryMembership> context,
                                        EntityCreationStatusComputer<JuryMembership> juryCreationStatusComputer,
                                        boolean relinkEntityWhenSaving, PersonFieldFactory personFieldFactory, AuthenticatedUser authenticatedUser,
                                        MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        super(JuryMembership.class, authenticatedUser, messages, juryCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .mapToNull(PROP_ADMIN_SECTION)
                        .mapToNull(PROP_ADMIN_VALIDATION_BOX));
        this.personFieldFactory = personFieldFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDefenseDetails(rootContainer);
        createInstitutionDetails(rootContainer);
        createJuryMemberDetails(rootContainer);
    }

    /**
     * Create the section for editing the defense description.
     *
     * @param rootContainer the container.
     */
    protected void createDefenseDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.candidate = this.personFieldFactory.createSingleNameField(getTranslation("views.jury_membership.new_candidate"), getLogger()); //$NON-NLS-1$
        this.candidate.setRequiredIndicatorVisible(true);
        this.candidate.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.candidate, 2);

        this.title = new TextField();
        this.title.setPrefixComponent(VaadinIcon.HASH.create());
        this.title.setRequired(true);
        this.title.setClearButtonVisible(true);
        content.add(this.title, 2);

        this.defenseType = new ComboBox<>();
        this.defenseType.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        this.defenseType.setRequired(true);
        this.defenseType.setItems(JuryType.values());
        this.defenseType.setItemLabelGenerator(this::getDefenseTypeLabel);
        content.add(this.defenseType, 1);

        this.date = new DatePicker();
        this.date.setPrefixComponent(VaadinIcon.CALENDAR_O.create());
        this.date.setRequired(true);
        this.date.setClearButtonVisible(true);
        content.add(this.date, 1);

        this.defenseDetails = createDetailsWithErrorMark(rootContainer, content, "defense", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.candidate)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.jury_membership.candidate.error.null"), //$NON-NLS-1$
                        getTranslation("views.jury_membership.candidate.error.disjoint"), //$NON-NLS-1$
                        this::checkCandidateUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.candidate, this.defenseDetails))
                .bind(JuryMembership::getCandidate, JuryMembership::setCandidate);
        getEntityDataBinder().forField(this.title)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.jury_membership.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.title, this.defenseDetails))
                .bind(JuryMembership::getTitle, JuryMembership::setTitle);
        getEntityDataBinder().forField(this.defenseType)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.jury_membership.defense_type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.defenseType, this.defenseDetails))
                .bind(JuryMembership::getDefenseType, JuryMembership::setDefenseType);
        getEntityDataBinder().forField(this.date)
                .withValidator(new NotNullDateValidator(getTranslation("views.jury_membership.date.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.date, this.defenseDetails))
                .bind(JuryMembership::getDate, JuryMembership::setDate);
    }

    private String getDefenseTypeLabel(JuryType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the institution.
     *
     * @param rootContainer the container.
     */
    protected void createInstitutionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.university = new TextField();
        this.university.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        this.university.setRequired(true);
        this.university.setClearButtonVisible(true);
        content.add(this.university, 2);

        this.country = ComponentFactory.newCountryComboBox(getLocale());
        this.country.setRequired(true);
        content.add(this.country, 1);

        this.institutionDetails = createDetailsWithErrorMark(rootContainer, content, "institution"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.university)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.jury_membership.university.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.university, this.institutionDetails))
                .bind(JuryMembership::getUniversity, JuryMembership::setUniversity);
        getEntityDataBinder().forField(this.country)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.jury_membership.country.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.country, this.institutionDetails))
                .bind(JuryMembership::getCountry, JuryMembership::setCountry);
    }

    /**
     * Create the section for editing the participants to the jury members.
     *
     * @param rootContainer the container.
     */
    protected void createJuryMemberDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.promoters = this.personFieldFactory.createMultiNameField(getTranslation("views.jury_membership.new_promoter"), getLogger()); //$NON-NLS-1$
        this.promoters.setPrefixComponent(VaadinIcon.USERS.create());
        content.add(this.promoters, 2);

        this.person = this.personFieldFactory.createSingleNameField(getTranslation("views.jury_membership.new_participant"), getLogger()); //$NON-NLS-1$
        this.person.setRequiredIndicatorVisible(true);
        this.person.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.person, 2);

        this.type = new ComboBox<>();
        this.type.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        this.type.setRequired(true);
        this.type.setItems(JuryMembershipType.values());
        this.type.setItemLabelGenerator(this::getMembershipTypeLabel);
        content.add(this.type, 1);

        this.juryDetails = createDetailsWithErrorMark(rootContainer, content, "jury"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.promoters)
                .withValidator(new DisjointEntityIterableValidator<>(
                        getTranslation("views.jury_membership.promoters.error.null"), //$NON-NLS-1$
                        getTranslation("views.jury_membership.promoters.error.disjoint"), //$NON-NLS-1$
                        false,
                        this::checkPromoterUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.promoters, this.juryDetails))
                .bind(JuryMembership::getPromoters, JuryMembership::setPromoters);
        getEntityDataBinder().forField(this.person)
                .withValidator(new DisjointEntityValidator<>(
                        getTranslation("views.jury_membership.participant.error.null"), //$NON-NLS-1$
                        getTranslation("views.jury_membership.participant.error.disjoint"), //$NON-NLS-1$
                        this::checkMemberUnicity))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.person, this.juryDetails))
                .bind(JuryMembership::getPerson, JuryMembership::setPerson);
        getEntityDataBinder().forField(this.type)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.jury_membership.membership_type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.type, this.juryDetails))
                .bind(JuryMembership::getType, JuryMembership::setType);
    }

    private String getMembershipTypeLabel(JuryMembershipType type) {
        return type.getLabel(getMessageSourceAccessor(), null, getLocale());
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.jury_membership.save_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.jury_membership.validation_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.jury_membership.delete_success2", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.jury_membership.save_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.jury_membership.validation_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.jury_membership.delete_error2", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    private boolean checkCandidateUnicity(Person candidate) {
        assert candidate != null;
        final var member = getEditedEntity().getPerson();
        if (!candidate.equals(member)) {
            final var list = getEditedEntity().getPromoters();
            return list == null || !list.contains(candidate);
        }
        return false;
    }

    private boolean checkMemberUnicity(Person member) {
        assert member != null;
        final var candidate = getEditedEntity().getCandidate();
        if (!member.equals(candidate)) {
            final var list = getEditedEntity().getPromoters();
            return list == null || !list.contains(member);
        }
        return false;
    }

    private boolean checkPromoterUnicity(Person promoter) {
        assert promoter != null;
        final var candidate = getEditedEntity().getCandidate();
        if (!promoter.equals(candidate)) {
            final var member = getEditedEntity().getPerson();
            return !promoter.equals(member);
        }
        return false;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.defenseDetails.setSummaryText(getTranslation("views.jury_membership.defense_details")); //$NON-NLS-1$
        this.candidate.setLabel(getTranslation("views.jury_membership.candidate")); //$NON-NLS-1$
        this.candidate.setHelperText(getTranslation("views.jury_membership.candidate.help")); //$NON-NLS-1$
        this.title.setLabel(getTranslation("views.jury_membership.title")); //$NON-NLS-1$
        this.defenseType.setLabel(getTranslation("views.jury_membership.defense_type")); //$NON-NLS-1$
        this.defenseType.setItemLabelGenerator(this::getDefenseTypeLabel);
        this.date.setLabel(getTranslation("views.jury_membership.date")); //$NON-NLS-1$

        this.institutionDetails.setSummaryText(getTranslation("views.jury_membership.institution_details")); //$NON-NLS-1$
        this.university.setLabel(getTranslation("views.jury_membership.university")); //$NON-NLS-1$
        this.country.setLabel(getTranslation("views.jury_membership.country")); //$NON-NLS-1$
        ComponentFactory.updateCountryComboBoxItems(this.country, getLocale());

        this.juryDetails.setSummaryText(getTranslation("views.jury_membership.jury_details")); //$NON-NLS-1$
        this.promoters.setLabel(getTranslation("views.jury_membership.promoters")); //$NON-NLS-1$
        this.promoters.setHelperText(getTranslation("views.jury_membership.promoters.help")); //$NON-NLS-1$
        this.person.setLabel(getTranslation("views.jury_membership.participant")); //$NON-NLS-1$
        this.person.setHelperText(getTranslation("views.jury_membership.participant.help")); //$NON-NLS-1$
        this.type.setLabel(getTranslation("views.jury_membership.membership_type")); //$NON-NLS-1$
        this.type.setItemLabelGenerator(this::getMembershipTypeLabel);
    }

}
