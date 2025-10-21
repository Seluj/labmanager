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

package fr.utbm.ciad.labmanager.views.components.teaching.editors.regular;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Unit;
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
import fr.utbm.ciad.labmanager.data.teaching.*;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.pdf.ServerSideUploadablePdfField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.*;
import fr.utbm.ciad.labmanager.views.components.addons.value.LeftRightListsField;
import fr.utbm.ciad.labmanager.views.components.addons.value.NumberPerEnumField;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.OrganizationFieldFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.fields.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.SinglePersonNameField;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;

/**
 * Abstract implementation for the editor of the information related to a teaching activity.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractTeachingActivityEditor extends AbstractEntityEditor<TeachingActivity> {

    private static final long serialVersionUID = -4154950918131057693L;
    private final DownloadableFileManager fileManager;
    private final PersonFieldFactory personFieldFactory;
    private final OrganizationFieldFactory organizationFieldFactory;
    private DetailsWithErrorMark descriptionDetails;
    private TextField activityCode;
    private TextField activityTitle;
    private SingleOrganizationNameField university;
    private ComboBox<CountryCode> language;
    private DatePicker startDate;
    private DatePicker endDate;
    private DetailsWithErrorMark contentDetails;
    private MarkdownField explanation;
    private LeftRightListsField<PedagogicalPracticeType> pedagogicalPractices;
    private DetailsWithErrorMark degreeDetails;
    private TextField degree;
    private ComboBox<TeachingActivityLevel> level;
    private ComboBox<StudentType> studentType;
    private DetailsWithErrorMark studentsDetails;
    private IntegerField numberOfStudents;
    private NumberPerEnumField<TeachingActivityType, Float> annualWorkPerType;
    private DetailsWithErrorMark teacherDetails;
    private SinglePersonNameField person;
    private ComboBox<TeacherRole> teacherRole;
    private ToggleButton labworkTutorialEtpDifference;
    private DetailsWithErrorMark documentsDetails;
    private ServerSideUploadablePdfField slides;
    private TextField sourceUrl;
    private TextField activityUrl;

    /**
     * Constructor.
     *
     * @param context                        the context for editing the teaching activity.
     * @param relinkEntityWhenSaving         indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                       be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param activityCreationStatusComputer the tool for computer the creation status for the teaching activity.
     * @param fileManager                    the manager of the downloadable files.
     * @param personService                  the service for accessing the JPA entities for persons.
     * @param personEditorFactory            the factory for creating the person editors.
     * @param personFieldFactory             the factory for creating the person fields.
     * @param organizationFieldFactory       the factory for creating the organization fields.
     * @param authenticatedUser              the connected user.
     * @param messages                       the accessor to the localized messages (Spring layer).
     * @param loggerFactory                  the factory to be used for the composite logger.
     * @param properties                     specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractTeachingActivityEditor(EntityEditingContext<TeachingActivity> context, boolean relinkEntityWhenSaving,
                                          EntityCreationStatusComputer<TeachingActivity> activityCreationStatusComputer, DownloadableFileManager fileManager,
                                          PersonFieldFactory personFieldFactory, OrganizationFieldFactory organizationFieldFactory,
                                          AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                          ConstructionPropertiesBuilder properties) {
        super(TeachingActivity.class, authenticatedUser, messages,
                activityCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.teaching_activity.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.teaching_activity.administration.validated_address")); //$NON-NLS-1$
        this.fileManager = fileManager;
        this.personFieldFactory = personFieldFactory;
        this.organizationFieldFactory = organizationFieldFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        createContentDetails(rootContainer);
        createDegreeDetails(rootContainer);
        createStudentDetails(rootContainer);
        createTeacherDetails(rootContainer);
        createDocumentDetails(rootContainer);
    }

    /**
     * Create the section for editing the description of the teaching activity.
     *
     * @param rootContainer the container.
     */
    protected void createDescriptionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.activityCode = new TextField();
        this.activityCode.setPrefixComponent(VaadinIcon.HASH.create());
        this.activityCode.setRequired(true);
        this.activityCode.setClearButtonVisible(true);
        content.add(this.activityCode, 1);

        this.activityTitle = new TextField();
        this.activityTitle.setPrefixComponent(VaadinIcon.HASH.create());
        this.activityTitle.setRequired(true);
        this.activityTitle.setClearButtonVisible(true);
        content.add(this.activityTitle, 1);

        this.language = ComponentFactory.newLanguageComboBox(getLocale());
        this.language.setPrefixComponent(VaadinIcon.COMMENT_ELLIPSIS_O.create());
        content.add(this.language, 2);

        this.startDate = new DatePicker();
        this.startDate.setPrefixComponent(VaadinIcon.SIGN_IN_ALT.create());
        this.startDate.setClearButtonVisible(true);
        content.add(this.startDate, 1);

        this.endDate = new DatePicker();
        this.endDate.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
        this.endDate.setClearButtonVisible(true);
        content.add(this.endDate, 1);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.activityCode)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.teaching_activities.code.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.activityCode, this.descriptionDetails))
                .bind(TeachingActivity::getCode, TeachingActivity::setCode);
        getEntityDataBinder().forField(this.activityTitle)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.teaching_activities.title.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.activityTitle, this.descriptionDetails))
                .bind(TeachingActivity::getTitle, TeachingActivity::setTitle);
        getEntityDataBinder().forField(this.language)
                .withValidator(new LanguageValidator(getTranslation("views.teaching_activities.language.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.language, this.descriptionDetails))
                .bind(TeachingActivity::getLanguage, TeachingActivity::setLanguage);
        getEntityDataBinder().forField(this.startDate)
                .bind(TeachingActivity::getStartDate, TeachingActivity::setStartDate);
        getEntityDataBinder().forField(this.endDate)
                .bind(TeachingActivity::getEndDate, TeachingActivity::setEndDate);
    }

    /**
     * Create the section for editing the content of the teaching activity.
     *
     * @param rootContainer the container.
     */
    protected void createContentDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.explanation = new MarkdownField();
        content.add(this.explanation, 2);

        this.pedagogicalPractices = new LeftRightListsField<>(ComponentFactory.toSerializableComparator(PedagogicalPracticeType.getLabelBasedComparator(getMessageSourceAccessor(), getLocale())));
        this.pedagogicalPractices.setEntityLabelGenerator(this::getPedagogicalPracticeLabel);
        this.pedagogicalPractices.setAvailableEntities(PedagogicalPracticeType.getAllDisplayTypes(getMessageSourceAccessor(), getLocale()));
        this.pedagogicalPractices.setListHeight(150, Unit.PIXELS);
        content.add(this.pedagogicalPractices, 2);

        this.contentDetails = createDetailsWithErrorMark(rootContainer, content, "content"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.explanation)
                .withConverter(new StringTrimer())
                .bind(TeachingActivity::getExplanation, TeachingActivity::setExplanation);
        getEntityDataBinder().forField(this.pedagogicalPractices)
                .bind(TeachingActivity::getPedagogicalPracticeTypes, TeachingActivity::setPedagogicalPracticeTypes);
    }

    /**
     * Create the section for editing the description of the degree.
     *
     * @param rootContainer the container.
     */
    protected void createDegreeDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.university = this.organizationFieldFactory.createSingleNameField(getTranslation("views.teaching_activities.new_university"), getLogger(), null); //$NON-NLS-1$
        this.university.setPrefixComponent(VaadinIcon.INSTITUTION.create());
        content.add(this.university, 2);

        this.degree = new TextField();
        this.degree.setPrefixComponent(VaadinIcon.DIPLOMA_SCROLL.create());
        this.degree.setClearButtonVisible(true);
        content.add(this.degree, 2);

        this.level = new ComboBox<>();
        this.level.setItems(TeachingActivityLevel.values());
        this.level.setItemLabelGenerator(this::getTeachingActivityLevelLabel);
        this.level.setPrefixComponent(VaadinIcon.FORM.create());
        content.add(this.level, 1);

        this.degreeDetails = createDetailsWithErrorMark(rootContainer, content, "degree"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.university)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.teaching_activities.university.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.university, this.degreeDetails))
                .bind(TeachingActivity::getUniversity, TeachingActivity::setUniversity);
        getEntityDataBinder().forField(this.degree)
                .withConverter(new StringTrimer())
                .bind(TeachingActivity::getDegree, TeachingActivity::setDegree);
        getEntityDataBinder().forField(this.level)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.teaching_activities.level.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.level, this.degreeDetails))
                .bind(TeachingActivity::getLevel, TeachingActivity::setLevel);
    }

    /**
     * Create the section for editing the description of the students.
     *
     * @param rootContainer the container.
     */
    protected void createStudentDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.studentType = new ComboBox<>();
        this.studentType.setItems(StudentType.getAllDisplayTypes(getMessageSourceAccessor(), getLocale()));
        this.studentType.setItemLabelGenerator(this::getStudentTypeLabel);
        this.studentType.setPrefixComponent(VaadinIcon.ACADEMY_CAP.create());
        content.add(this.studentType, 1);

        this.numberOfStudents = new IntegerField();
        this.numberOfStudents.setMin(0);
        this.numberOfStudents.setPrefixComponent(VaadinIcon.COINS.create());
        this.numberOfStudents.setClearButtonVisible(true);
        content.add(this.numberOfStudents, 1);

        this.studentsDetails = createDetailsWithErrorMark(rootContainer, content, "students"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.studentType)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.teaching_activities.student_type.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.studentType, this.studentsDetails))
                .bind(TeachingActivity::getStudentType, TeachingActivity::setStudentType);
        getEntityDataBinder().forField(this.numberOfStudents)
                .withValidator(new IntegerRangeValidator(getTranslation("views.teaching_activities.student_count.error"), Integer.valueOf(0), null)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.numberOfStudents, this.studentsDetails))
                .bind(TeachingActivity::getNumberOfStudents, TeachingActivity::setNumberOfStudents);
    }

    private String getTeachingActivityLevelLabel(TeachingActivityLevel level) {
        return level.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getStudentTypeLabel(StudentType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the description of the teachers.
     *
     * @param rootContainer the container.
     */
    protected void createTeacherDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.person = this.personFieldFactory.createSingleNameField(getTranslation("views.teaching_activities.new_teacher"), getLogger()); //$NON-NLS-1$
        this.person.setPrefixComponent(VaadinIcon.USER.create());
        content.add(this.person, 2);

        this.teacherRole = new ComboBox<>();
        this.teacherRole.setItems(TeacherRole.values());
        this.teacherRole.setItemLabelGenerator(this::getTeacherRoleLabel);
        this.teacherRole.setPrefixComponent(VaadinIcon.FORM.create());
        content.add(this.teacherRole, 2);

        this.labworkTutorialEtpDifference = new ToggleButton();
        content.add(this.labworkTutorialEtpDifference, 2);

        final ItemLabelGenerator<TeachingActivityType> renderer = it -> it.getLabel(getMessageSourceAccessor(), getLocale());
        this.annualWorkPerType = NumberPerEnumField.forFloat(
                "views.teaching_activities.annualWorkPerType.type_column", //$NON-NLS-1$
                "views.teaching_activities.annualWorkPerType.hour_column", //$NON-NLS-1$
                "views.teaching_activities.annualWorkPerType.edit", //$NON-NLS-1$
                "views.teaching_activities.annualWorkPerType.add", //$NON-NLS-1$
                "views.teaching_activities.annualWorkPerType.remove", //$NON-NLS-1$
                it -> {
                    it.setItems(Arrays.asList(TeachingActivityType.values()));
                });
        this.annualWorkPerType.setEnumValueItemLabelGenerator(renderer);
        content.add(this.annualWorkPerType, 2);

        this.teacherDetails = createDetailsWithErrorMark(rootContainer, content, "teacher"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.person)
                .withValidator(new NotNullEntityValidator<>(getTranslation("views.teaching_activities.teacher.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.person, this.teacherDetails))
                .bind(TeachingActivity::getPerson, TeachingActivity::setPerson);
        getEntityDataBinder().forField(this.teacherRole)
                .withValidator(new NotNullEnumerationValidator<>(getTranslation("views.teaching_activities.teacher_role.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.teacherRole, this.teacherDetails))
                .bind(TeachingActivity::getRole, TeachingActivity::setRole);
        getEntityDataBinder().forField(this.labworkTutorialEtpDifference)
                .bind(TeachingActivity::isDifferentHetdForTdTp, TeachingActivity::setDifferentHetdForTdTp);
        getEntityDataBinder().forField(this.annualWorkPerType)
                .bind(TeachingActivity::getAnnualWorkPerType, TeachingActivity::setAnnualWorkPerType);
    }

    private String getTeacherRoleLabel(TeacherRole role) {
        return role.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getPedagogicalPracticeLabel(PedagogicalPracticeType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /**
     * Create the section for editing the description of the documents.
     *
     * @param rootContainer the container.
     */
    protected void createDocumentDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.activityUrl = new TextField();
        this.activityUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(this.activityUrl, 1);

        this.sourceUrl = new TextField();
        this.sourceUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(this.sourceUrl, 1);

        this.slides = new ServerSideUploadablePdfField(this.fileManager,
                ext -> this.fileManager.makeTeachingActivitySlidesFilename(getEditedEntity().getId()),
                () -> getLogger());
        this.slides.setClearButtonVisible(true);
        content.add(this.slides, 2);

        this.documentsDetails = createDetailsWithErrorMark(rootContainer, content, "documents"); //$NON-NLS-1$

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.activityUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.activityUrl, this.documentsDetails))
                .bind(TeachingActivity::getActivityUrl, TeachingActivity::setActivityUrl);
        getEntityDataBinder().forField(this.sourceUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.sourceUrl, this.documentsDetails))
                .bind(TeachingActivity::getSourceUrl, TeachingActivity::setSourceUrl);
        getEntityDataBinder().forField(this.slides)
                .withConverter(new StringTrimer())
                .bind(TeachingActivity::getPathToSlides, TeachingActivity::setPathToSlides);
    }

    @Override
    protected void doSave() throws Exception {
        getEditingContext().save(this.slides);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.teaching_activities.save_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.teaching_activities.validation_success", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.teaching_activities.delete_success2", //$NON-NLS-1$
                getEditedEntity().getTitle());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.teaching_activities.save_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.teaching_activities.validation_error", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.teaching_activities.delete_error2", //$NON-NLS-1$
                getEditedEntity().getTitle(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.descriptionDetails.setSummaryText(getTranslation("views.teaching_activities.description_details")); //$NON-NLS-1$
        this.activityCode.setLabel(getTranslation("views.teaching_activities.code")); //$NON-NLS-1$
        this.activityCode.setHelperText(getTranslation("views.teaching_activities.code.help")); //$NON-NLS-1$
        this.activityTitle.setLabel(getTranslation("views.teaching_activities.title")); //$NON-NLS-1$
        this.activityTitle.setHelperText(getTranslation("views.teaching_activities.title.help")); //$NON-NLS-1$
        this.language.setLabel(getTranslation("views.teaching_activities.language")); //$NON-NLS-1$
        this.language.setHelperText(getTranslation("views.teaching_activities.language.help")); //$NON-NLS-1$
        ComponentFactory.updateLanguageComboBoxItems(this.language, getLocale());
        this.startDate.setLocale(event.getLocale());
        this.startDate.setLabel(getTranslation("views.teaching_activities.start_date")); //$NON-NLS-1$
        this.endDate.setLocale(event.getLocale());
        this.endDate.setLabel(getTranslation("views.teaching_activities.end_date")); //$NON-NLS-1$
        this.endDate.setHelperText(getTranslation("views.teaching_activities.end_date.help")); //$NON-NLS-1$

        this.contentDetails.setSummaryText(getTranslation("views.teaching_activities.content_details")); //$NON-NLS-1$
        this.explanation.setLabel(getTranslation("views.teaching_activities.explanation")); //$NON-NLS-1$
        this.explanation.setHelperText(getTranslation("views.teaching_activities.explanation.help")); //$NON-NLS-1$
        this.pedagogicalPractices.setLabel(getTranslation("views.teaching_activities.pedagogical_practices")); //$NON-NLS-1$
        this.pedagogicalPractices.setAdditionTooltip(getTranslation("views.teaching_activities.pedagogical_practices.insert")); //$NON-NLS-1$
        this.pedagogicalPractices.setDeletionTooltip(getTranslation("views.teaching_activities.pedagogical_practices.delete")); //$NON-NLS-1$

        this.degreeDetails.setSummaryText(getTranslation("views.teaching_activities.degree_details")); //$NON-NLS-1$
        this.university.setLabel(getTranslation("views.teaching_activities.university")); //$NON-NLS-1$
        this.university.setPlaceholder(getTranslation("views.teaching_activities.university.placeholder")); //$NON-NLS-1$
        this.university.setHelperText(getTranslation("views.teaching_activities.university.help")); //$NON-NLS-1$
        this.degree.setLabel(getTranslation("views.teaching_activities.degree")); //$NON-NLS-1$
        this.degree.setHelperText(getTranslation("views.teaching_activities.degree.help")); //$NON-NLS-1$
        this.level.setLabel(getTranslation("views.teaching_activities.level")); //$NON-NLS-1$
        this.level.setItemLabelGenerator(this::getTeachingActivityLevelLabel);

        this.studentsDetails.setSummaryText(getTranslation("views.teaching_activities.student_details")); //$NON-NLS-1$
        this.studentType.setLabel(getTranslation("views.teaching_activities.student_type")); //$NON-NLS-1$
        this.studentType.setItemLabelGenerator(this::getStudentTypeLabel);
        this.numberOfStudents.setLabel(getTranslation("views.teaching_activities.student_count")); //$NON-NLS-1$
        this.numberOfStudents.setHelperText(getTranslation("views.teaching_activities.student_count.help")); //$NON-NLS-1$

        this.teacherDetails.setSummaryText(getTranslation("views.teaching_activities.teacher_details")); //$NON-NLS-1$
        this.person.setLabel(getTranslation("views.teaching_activities.teacher")); //$NON-NLS-1$
        this.person.setPlaceholder(getTranslation("views.teaching_activities.teacher.placeholder")); //$NON-NLS-1$
        this.person.setHelperText(getTranslation("views.teaching_activities.teacher.help")); //$NON-NLS-1$
        this.teacherRole.setLabel(getTranslation("views.teaching_activities.teacher_role")); //$NON-NLS-1$
        this.teacherRole.setItemLabelGenerator(this::getTeacherRoleLabel);
        this.labworkTutorialEtpDifference.setLabel(getTranslation("views.teaching_activities.different_etp")); //$NON-NLS-1$
        this.annualWorkPerType.setLabel(getTranslation("views.teaching_activities.annualWorkPerType")); //$NON-NLS-1$
        this.annualWorkPerType.setHelperText(getTranslation("views.teaching_activities.annualWorkPerType.help")); //$NON-NLS-1$

        this.documentsDetails.setSummaryText(getTranslation("views.teaching_activities.document_details")); //$NON-NLS-1$
        this.activityUrl.setLabel(getTranslation("views.teaching_activities.activity_url")); //$NON-NLS-1$
        this.sourceUrl.setLabel(getTranslation("views.teaching_activities.source_url")); //$NON-NLS-1$
        this.slides.setLabel(getTranslation("views.teaching_activities.slides")); //$NON-NLS-1$
    }

}
