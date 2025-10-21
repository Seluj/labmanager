package fr.utbm.ciad.labmanager.views.components.persons.editors.wizard;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.WebPageNaming;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.logger.DelegateContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.phones.PhoneNumberField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.OrcidValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import fr.utbm.ciad.labmanager.views.components.persons.editors.regular.AbstractPersonEditor;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

/**
 * Implementation for the editor of the information related to a person. It is directly linked for
 * using it with a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPersonEditorWizard extends AbstractPersonEditor {

    private PersonEditorComponentWizard personAdditionWizard;


    /**
     * Constructor.
     *
     * @param userContext                  the editing context for the user.
     * @param personCreationStatusComputer the tool for computer the creation status for the persons.
     * @param relinkEntityWhenSaving       indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser            the connected user.
     * @param messages                     the accessor to the localized messages (Spring layer).
     * @param properties                   specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractPersonEditorWizard(UserService.UserEditingContext userContext, EntityCreationStatusComputer<Person> personCreationStatusComputer,
                                      boolean relinkEntityWhenSaving, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, PersonService personService,
                                      ConstructionPropertiesBuilder properties) {
        super(userContext, personCreationStatusComputer, personService, relinkEntityWhenSaving, authenticatedUser, messages, properties);
    }


    /**
     * Create the content of the editor.
     *
     * @param rootContainer the container.
     */
    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        if (isBaseAdmin()) {
            Consumer<FormLayout> builderCallback = null;
            if (isAdvancedAdmin()) {
                builderCallback = content -> {
                    this.webpageConvention = new ComboBox<>();
                    this.webpageConvention.setItems(WebPageNaming.values());
                    this.webpageConvention.setItemLabelGenerator(this::getWebPageNamingLabel);
                    this.webpageConvention.setValue(WebPageNaming.UNSPECIFIED);
                    content.add(this.webpageConvention, 2);

                    this.userLogin = new TextField();
                    this.userLogin.setClearButtonVisible(true);
                    this.userLogin.setPrefixComponent(VaadinIcon.HASH.create());
                    content.add(this.userLogin);

                    this.userRole = new ComboBox<>();
                    this.userRole.setItems(UserRole.values());
                    this.userRole.setItemLabelGenerator(this::getUserRoleLabel);
                    this.userRole.setPrefixComponent(VaadinIcon.MEDAL.create());
                    this.userRole.setValue(UserRole.USER);
                    content.add(this.userRole);

                    getEntityDataBinder().forField(this.webpageConvention).bind(Person::getWebPageNaming, Person::setWebPageNaming);
                    getUserDataBinder().forField(this.userLogin).bind(User::getLogin, User::setLogin);
                    getUserDataBinder().forField(this.userRole).bind(User::getRole, User::setRole);
                };
            }
            personAdditionWizard = new PersonEditorComponentWizard(
                    new DelegateContextualLoggerFactory(getLogger()),
                    createPersonalInformationComponents(), createContactInformationComponents(),
                    createResearcherIdsComponents(), createBiographyComponents(),
                    createIndexesComponents(), createSocialLinksComponents(),
                    createAdministrationComponents(builderCallback, it -> it.bind(Person::isValidated, Person::setValidated)));

        } else {
            personAdditionWizard = new PersonEditorComponentWizard(
                    new DelegateContextualLoggerFactory(getLogger()),
                    createPersonalInformationComponents(), createContactInformationComponents(),
                    createResearcherIdsComponents(), createBiographyComponents(),
                    createIndexesComponents(), createSocialLinksComponents());
        }
        rootContainer.add(personAdditionWizard);
    }

    /**
     * Create the section for editing the description of the person.
     *
     * @return The content.
     */
    protected VerticalLayout createPersonalInformationComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        lastname = new TextField();
        lastname.setRequired(true);
        lastname.setClearButtonVisible(true);
        content.add(lastname);

        firstname = new TextField();
        firstname.setRequired(true);
        firstname.setClearButtonVisible(true);
        content.add(firstname);

        gender = new ComboBox<>();
        gender.setItems(Gender.values());
        gender.setItemLabelGenerator(this::getGenderLabel);
        gender.setValue(Gender.NOT_SPECIFIED);
        content.add(gender, 2);

        nationality = ComponentFactory.newCountryComboBox(getLocale());
        nationality.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(nationality, 2);

        gravatarId = ComponentFactory.newClickableIconTextField(Person.GRAVATAR_ROOT_URL, GRAVATAR_ICON);
        gravatarId.setPrefixComponent(VaadinIcon.CAMERA.create());
        gravatarId.setClearButtonVisible(true);
        gravatarId.addValidationStatusChangeListener(it -> {
            updatePhoto();
        });
        content.add(gravatarId, 2);

        photo = new VerticalLayout();
        photo.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10);
        photo.setAlignItems(FlexComponent.Alignment.CENTER);
        photo.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.add(photo, 2);
        updatePhoto();


        getEntityDataBinder().forField(lastname)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.last_name.error"))) //$NON-NLS-1$
                .bind(Person::getLastName, Person::setLastName);
        getEntityDataBinder().forField(firstname)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.first_name.error"))) //$NON-NLS-1$
                .bind(Person::getFirstName, Person::setFirstName);
        getEntityDataBinder().forField(gender).bind(Person::getGender, Person::setGender);
        getEntityDataBinder().forField(nationality).bind(Person::getNationality, Person::setNationality);
        getEntityDataBinder().forField(gravatarId)
                .withConverter(new StringTrimer())
                .bind(Person::getGravatarId, Person::setGravatarId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /**
     * Create the components for entering the contact informations.
     *
     * @return The content.
     */
    protected VerticalLayout createContactInformationComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(1);

        this.primaryEmail = new TextField();
        this.primaryEmail.setPrefixComponent(VaadinIcon.AT.create());
        this.primaryEmail.setClearButtonVisible(true);
        content.add(this.primaryEmail, 2);

        this.secondaryEmail = new TextField();
        this.secondaryEmail.setPrefixComponent(VaadinIcon.AT.create());
        this.secondaryEmail.setClearButtonVisible(true);
        content.add(this.secondaryEmail, 2);

        this.officePhone = new PhoneNumberField();
        this.officePhone.setClearButtonVisible(true);
        this.officePhone.setPrefixComponent(VaadinIcon.PHONE.create());
        content.add(this.officePhone, 2);

        this.mobilePhone = new PhoneNumberField();
        this.mobilePhone.setClearButtonVisible(true);
        this.mobilePhone.setPrefixComponent(VaadinIcon.MOBILE.create());
        content.add(this.mobilePhone, 2);

        this.officeRoom = new TextField();
        this.officeRoom.setClearButtonVisible(true);
        this.officeRoom.setPrefixComponent(VaadinIcon.OFFICE.create());
        content.add(this.officeRoom, 2);


        getEntityDataBinder().forField(this.primaryEmail)
                .withConverter(new StringTrimer())
                .withValidator(new EmailValidator(getTranslation("views.forms.email.invalid_format"), true)) //$NON-NLS-1$
                .bind(Person::getPrimaryEmail, Person::setPrimaryEmail);
        getEntityDataBinder().forField(this.secondaryEmail)
                .withConverter(new StringTrimer())
                .withValidator(new EmailValidator(getTranslation("views.forms.email.invalid_format"), true)) //$NON-NLS-1$
                .bind(Person::getSecondaryEmail, Person::setSecondaryEmail);
        getEntityDataBinder().forField(this.officePhone)
                .withValidator(this.officePhone.getDefaultValidator())
                .bind(Person::getOfficePhone, Person::setOfficePhone);
        getEntityDataBinder().forField(this.mobilePhone)
                .withValidator(this.mobilePhone.getDefaultValidator())
                .bind(Person::getMobilePhone, Person::setMobilePhone);
        getEntityDataBinder().forField(this.officeRoom)
                .withConverter(new StringTrimer())
                .bind(Person::getOfficeRoom, Person::setOfficeRoom);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /**
     * Create the components for entering the researcher identifiers.
     * The information includes:<ul>
     * <li>ORCID</li>
     * <li>Identifier on Scopus (Elsevier)</li>
     * <li>Identifier on ResearcherID, WoS or Publon</li>
     * <li>Identifier for Google Scholar</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createResearcherIdsComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(2);

        this.orcid = ComponentFactory.newClickableIconTextField(Person.ORCID_BASE_URL, ORCID_ICON);
        this.orcid.setClearButtonVisible(true);
        this.orcid.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.orcid, 2);

        this.wos = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wos.setClearButtonVisible(true);
        this.wos.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.wos, 2);

        this.scopus = ComponentFactory.newClickableIconTextField(Person.SCOPUS_BASE_URL, SCOPUS_ICON);
        this.scopus.setClearButtonVisible(true);
        this.scopus.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.scopus, 2);

        this.gscholar = ComponentFactory.newClickableIconTextField(Person.GSCHOLAR_BASE_URL, GSCHOLAR_ICON);
        this.gscholar.setClearButtonVisible(true);
        this.gscholar.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.gscholar, 2);


        getEntityDataBinder().forField(this.orcid)
                .withConverter(new StringTrimer())
                .withValidator(new OrcidValidator(getTranslation("views.persons.invalid_orcid"), //$NON-NLS-1$
                        getTranslation("views.persons.undesired_orcid"), true)) //$NON-NLS-1$
                .bind(Person::getORCID, Person::setORCID);
        getEntityDataBinder().forField(this.scopus)
                .withConverter(new StringTrimer())
                .bind(Person::getScopusId, Person::setScopusId);
        getEntityDataBinder().forField(this.wos)
                .withConverter(new StringTrimer())
                .bind(Person::getResearcherId, Person::setResearcherId);
        getEntityDataBinder().forField(this.gscholar)
                .withConverter(new StringTrimer())
                .bind(Person::getGoogleScholarId, Person::setGoogleScholarId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /**
     * Create the components for entering the researcher biography.
     *
     * @return The content.
     */
    protected VerticalLayout createBiographyComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(2);

        this.privateBiography = new ToggleButton();
        content.add(this.privateBiography, 2);

        this.biography = new MarkdownField();
        content.add(this.biography, 2);

        getEntityDataBinder().forField(this.privateBiography)
                .bind(Person::getPrivateBiography, Person::setPrivateBiography);
        getEntityDataBinder().forField(this.biography)
                .withConverter(new StringTrimer())
                .bind(Person::getBiography, Person::setBiography);

        verticalLayout.add(content);
        return verticalLayout;
    }

    protected void updatePhoto() {
        final var photoURL = getEditedEntity().getPhotoURL();
        this.photo.removeAll();
        if (photoURL != null) {
            final var url = photoURL.toExternalForm();
            final var image = new Image(url, url);
            image.setMinHeight(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMaxHeight(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMinWidth(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMaxWidth(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            this.photo.add(image);
        } else {
            this.photo = new VerticalLayout();
            String size = ViewConstants.PHOTO_SIZE +
                    Unit.PIXELS.getSymbol();
            final var image = LumoIcon.USER.create();
            image.setSize(size);
            this.photo.add(image);
        }
    }

    /**
     * Create the components for entering the researcher indicators and indexes.
     * The information includes:<ul>
     * <li>H-index on Web-of-Science (WoS)</li>
     * <li>H-index on Scopus</li>
     * <li>H-index on Google Scholar</li>
     * <li>Number of paper citations on Web-of-Science (WoS)</li>
     * <li>Number of paper citations on Scopus</li>
     * <li>Number of paper citations on Google Scholar</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createIndexesComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content0 = ComponentFactory.newColumnForm(1);

        this.wosHindex = new IntegerField();
        this.wosHindex.setMin(0);
        this.wosHindex.setMax(100);
        this.wosHindex.setClearButtonVisible(true);
        this.wosHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.wosHindex);

        this.scopusHindex = new IntegerField();
        this.scopusHindex.setMin(0);
        this.scopusHindex.setMax(100);
        this.scopusHindex.setClearButtonVisible(true);
        this.scopusHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.scopusHindex);

        this.gscholarHindex = new IntegerField();
        this.gscholarHindex.setMin(0);
        this.gscholarHindex.setMax(100);
        this.gscholarHindex.setClearButtonVisible(true);
        this.gscholarHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.gscholarHindex);

        final var content1 = ComponentFactory.newColumnForm(1);

        this.wosCitations = new IntegerField();
        this.wosCitations.setMin(0);
        this.wosCitations.setMax(100000);
        this.wosCitations.setClearButtonVisible(true);
        this.wosCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.wosCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.wosCitations);

        this.scopusCitations = new IntegerField();
        this.scopusCitations.setMin(0);
        this.scopusCitations.setMax(100000);
        this.scopusCitations.setClearButtonVisible(true);
        this.scopusCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.scopusCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.scopusCitations);

        this.gscholarCitations = new IntegerField();
        this.gscholarCitations.setMin(0);
        this.gscholarCitations.setMax(100000);
        this.gscholarCitations.setClearButtonVisible(true);
        this.gscholarCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.gscholarCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.gscholarCitations);

        final var vl = new VerticalLayout(content0, content1);
        vl.setSpacing(false);


        getEntityDataBinder().forField(this.wosHindex).bind(Person::getWosHindex, Person::setWosHindex);
        getEntityDataBinder().forField(this.scopusHindex).bind(Person::getScopusHindex, Person::setScopusHindex);
        getEntityDataBinder().forField(this.gscholarHindex).bind(Person::getGoogleScholarHindex, Person::setGoogleScholarHindex);
        getEntityDataBinder().forField(this.wosCitations).bind(Person::getWosCitations, Person::setWosCitations);
        getEntityDataBinder().forField(this.scopusCitations).bind(Person::getScopusCitations, Person::setScopusCitations);
        getEntityDataBinder().forField(this.gscholarCitations).bind(Person::getGoogleScholarCitations, Person::setGoogleScholarCitations);

        verticalLayout.add(vl);
        return verticalLayout;
    }

    /**
     * Create the components for entering the social links.
     * The information includes:<ul>
     * <li>Identifier on Research Gate</li>
     * <li>Identifier on AD Scientific Index</li>
     * <li>Internet address of the DBLP page</li>
     * <li>Internet address of the Academia.edu</li>
     * <li>Internet address of the EU CORDIS page</li>
     * <li>Identifier on Linked-In</li>
     * <li>Github Identifier</li>
     * <li>Facebook Identifier</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createSocialLinksComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(1);

        this.researchGate = ComponentFactory.newClickableIconTextField(Person.RESEARCHGATE_BASE_URL, RESEARCHGATE_ICON);
        this.researchGate.setClearButtonVisible(true);
        this.researchGate.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.researchGate);

        this.adScientificIndex = ComponentFactory.newClickableIconTextField(Person.ADSCIENTIFICINDEX_BASE_URL, ADSCIENTIFICINDEX_ICON);
        this.adScientificIndex.setClearButtonVisible(true);
        this.adScientificIndex.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.adScientificIndex);

        this.dblp = ComponentFactory.newClickableIconTextField(DBLP_BASE_URL, DBLP_ICON);
        this.dblp.setClearButtonVisible(true);
        this.dblp.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        content.add(this.dblp);

        this.academiaEdu = ComponentFactory.newClickableIconTextField(ACADEMIA_EDU_BASE_URL, ACADEMIA_EDU_ICON);
        this.academiaEdu.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        this.academiaEdu.setClearButtonVisible(true);
        content.add(this.academiaEdu);

        this.euCordis = ComponentFactory.newClickableIconTextField(EU_CORDIS_BASE_URL, EU_CORDIS_ICON);
        this.euCordis.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        this.euCordis.setClearButtonVisible(true);
        content.add(this.euCordis);

        this.linkedin = ComponentFactory.newClickableIconTextField(Person.LINKEDIN_BASE_URL, LINKEDIN_ICON);
        this.linkedin.setClearButtonVisible(true);
        this.linkedin.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.linkedin);

        this.github = ComponentFactory.newClickableIconTextField(Person.GITHUB_BASE_URL, GITHUB_ICON);
        this.github.setClearButtonVisible(true);
        this.github.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.github);

        this.facebook = ComponentFactory.newClickableIconTextField(Person.FACEBOOK_BASE_URL, FACEBOOK_ICON);
        this.facebook.setClearButtonVisible(true);
        this.facebook.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.facebook);


        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.researchGate)
                .withConverter(new StringTrimer())
                .bind(Person::getResearchGateId, Person::setResearchGateId);
        getEntityDataBinder().forField(this.adScientificIndex)
                .withConverter(new StringTrimer())
                .bind(Person::getAdScientificIndexId, Person::setAdScientificIndexId);
        getEntityDataBinder().forField(this.dblp)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getDblpURL, Person::setDblpURL);
        getEntityDataBinder().forField(this.academiaEdu)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getAcademiaURL, Person::setAcademiaURL);
        getEntityDataBinder().forField(this.euCordis)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getCordisURL, Person::setCordisURL);
        getEntityDataBinder().forField(this.linkedin)
                .withConverter(new StringTrimer())
                .bind(Person::getLinkedInId, Person::setLinkedInId);
        getEntityDataBinder().forField(this.github)
                .withConverter(new StringTrimer())
                .bind(Person::getGithubId, Person::setGithubId);
        getEntityDataBinder().forField(this.facebook)
                .withConverter(new StringTrimer())
                .bind(Person::getFacebookId, Person::setFacebookId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

    }

    private String getWebPageNamingLabel(WebPageNaming naming) {
        return naming.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getUserRoleLabel(UserRole role) {
        return role.getLabel(getMessageSourceAccessor(), getLocale());
    }

}
