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

package fr.utbm.ciad.labmanager.views.components.organizations.editors.regular;

import com.google.common.base.Strings;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationNameComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationType;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
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
import fr.utbm.ciad.labmanager.views.components.addons.uploads.image.ServerSideUploadableImageField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.addons.value.LeftRightListsField;
import fr.utbm.ciad.labmanager.views.components.organizationaddresses.editors.AddressEditorFactory;
import fr.utbm.ciad.labmanager.views.components.organizations.editors.OrganizationEditorFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.domain.Specification;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.DBLP_BASE_URL;
import static fr.utbm.ciad.labmanager.views.ViewConstants.DBLP_ICON;

/**
 * Abstract implementation for the editor of the information related to an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractOrganizationEditor extends AbstractEntityEditor<ResearchOrganization> {

    private static final long serialVersionUID = -4405231976086480507L;
    protected final DownloadableFileManager fileManager;
    protected final OrganizationAddressService addressService;
    protected final AddressEditorFactory addressEditorFactory;
    protected final ResearchOrganizationService organizationService;
    protected final OrganizationEditorFactory organizationEditorFactory;
    protected DetailsWithErrorMark descriptionDetails;
    protected TextField acronym;
    protected TextField name;
    protected ComboBox<ResearchOrganizationType> type;
    protected Details geographyDetails;
    protected LeftRightListsField<OrganizationAddress> addresses;
    protected ComboBox<CountryCode> country;
    protected Details identificationDetails;
    protected TextField nationalIdentifier;
    protected TextField rnsr;
    protected Details superStructureDetails;
    protected ComboListField<ResearchOrganization> superStructures;
    protected DetailsWithErrorMark communicationDetails;
    protected TextField organizationUrl;
    protected MarkdownField description;
    protected ServerSideUploadableImageField logo;
    protected ToggleButton majorOrganization;

    /**
     * Constructor.
     *
     * @param context                            the context for editing the entity.
     * @param organizationCreationStatusComputer the tool for computer the creation status for the research organizations.
     * @param relinkEntityWhenSaving             indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                           be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param fileManager                        the manager of the file system at server-side.
     * @param authenticatedUser                  the connected user.
     * @param messages                           the accessor to the localized messages (Spring layer).
     * @param organizationService                the service for accessing the organizations.
     * @param addressService                     the service for accessing the organization addresses.
     * @param organizationEditorFactory          the factory of the organization editor.
     * @param addressEditorFactory               the factory of the organization address editor.
     * @param properties                         specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractOrganizationEditor(EntityEditingContext<ResearchOrganization> context,
                                      EntityCreationStatusComputer<ResearchOrganization> organizationCreationStatusComputer, boolean relinkEntityWhenSaving,
                                      DownloadableFileManager fileManager, AuthenticatedUser authenticatedUser,
                                      MessageSourceAccessor messages, ResearchOrganizationService organizationService, OrganizationAddressService addressService,
                                      OrganizationEditorFactory organizationEditorFactory, AddressEditorFactory addressEditorFactory,
                                      ConstructionPropertiesBuilder properties) {
        super(ResearchOrganization.class, authenticatedUser, messages,
                organizationCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.organizations.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.organizations.administration.validated_organization")); //$NON-NLS-1$
        this.fileManager = fileManager;
        this.organizationService = organizationService;
        this.addressService = addressService;
        this.organizationEditorFactory = organizationEditorFactory;
        this.addressEditorFactory = addressEditorFactory;
    }

    /**
     * Replies the factory for the organization editor.
     *
     * @return the factory.
     * @since 4.0
     */
    protected OrganizationEditorFactory getOrganizationEditorFactory() {
        return this.organizationEditorFactory;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        createGeographicalDetails(rootContainer);
        createIdentificationDetails(rootContainer);
        createSuperStructureDetails(rootContainer);
        createCommunicationDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer,
                    content -> {
                        this.majorOrganization = new ToggleButton();
                        content.add(this.majorOrganization, 1);

                        getEntityDataBinder().forField(this.majorOrganization)
                                .bind(ResearchOrganization::isMajorOrganization, ResearchOrganization::setMajorOrganization);
                    },
                    it -> it.bind(ResearchOrganization::isValidated, ResearchOrganization::setValidated));
        }
    }

    /**
     * Create the section for editing the description of the organization.
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
        this.type.setPrefixComponent(VaadinIcon.DIPLOMA.create());
        this.type.setItems(ResearchOrganizationType.values());
        this.type.setItemLabelGenerator(this::getTypeLabel);
        this.type.setValue(ResearchOrganizationType.DEFAULT);
        content.add(this.type, 2);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.organizations.acronym.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.acronym, this.descriptionDetails))
                .bind(ResearchOrganization::getAcronym, ResearchOrganization::setAcronym);
        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.organizations.name.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.name, this.descriptionDetails))
                .bind(ResearchOrganization::getName, ResearchOrganization::setName);
        getEntityDataBinder().forField(this.type).bind(ResearchOrganization::getType, ResearchOrganization::setType);
    }

    /**
     * Create the section for editing the geograpihcal information of the organization.
     *
     * @param rootContainer the container.
     */
    protected void createGeographicalDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.addresses = new LeftRightListsField<>(ComponentFactory.toSerializableComparator(EntityUtils.getPreferredOrganizationAddressComparator()), this::openAddressEditor);
        this.addresses.setEntityLabelGenerator(it -> it.getName());
        this.addresses.setAvailableEntities(this.addressService.getAllAddresses());
        content.add(this.addresses, 2);

        this.country = ComponentFactory.newCountryComboBox(getLocale());
        this.country.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(this.country, 1);

        this.geographyDetails = createDetailsWithErrorMark(rootContainer, content, "geography"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.addresses).bind(ResearchOrganization::getAddresses, ResearchOrganization::setAddresses);
        getEntityDataBinder().forField(this.country).bind(ResearchOrganization::getCountry, ResearchOrganization::setCountry);
    }

    /**
     * Invoked for creating a new organization address.
     *
     * @param saver the callback that is invoked when the address is saved as JPA entity.
     */
    protected void openAddressEditor(Consumer<OrganizationAddress> saver) {
        final var newAddress = new OrganizationAddress();
        final var editor = this.addressEditorFactory.createAdditionEditor(newAddress, getEditingContext().getLogger());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.organizations.create_address"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    /**
     * Create the section for editing the identification of the organization.
     *
     * @param rootContainer the container.
     */
    protected void createIdentificationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.nationalIdentifier = new TextField();
        this.nationalIdentifier.setPrefixComponent(VaadinIcon.HASH.create());
        this.nationalIdentifier.setClearButtonVisible(true);
        content.add(this.nationalIdentifier, 2);

        this.rnsr = new TextField();
        this.rnsr.setPrefixComponent(VaadinIcon.HASH.create());
        this.rnsr.setClearButtonVisible(true);
        content.add(this.rnsr, 2);

        this.identificationDetails = createDetailsWithErrorMark(rootContainer, content, "identification"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.nationalIdentifier)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getNationalIdentifier, ResearchOrganization::setNationalIdentifier);
        getEntityDataBinder().forField(this.rnsr)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getRnsr, ResearchOrganization::setRnsr);
    }

    /**
     * Create the section for editing the super structure of the organization.
     *
     * @param rootContainer the container.
     */
    protected void createSuperStructureDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        final var currentId = getEditedEntity().getId();
        this.superStructures = new ComboListField<>(ComponentFactory.toSerializableComparator(new ResearchOrganizationNameComparator()), this::openOrganizationEditor);
        this.superStructures.setEntityRenderers(
                this::createNameString,
                new ComponentRenderer<>(this::createNameComponent),
                new ComponentRenderer<>(this::createNameComponent));
        this.superStructures.setAvailableEntities(query -> {
            return this.organizationService.getAllResearchOrganizations(
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    new OtherOrganizationSpecification(currentId),
                    null).stream();
        });
        content.add(this.superStructures, 2);

        this.superStructureDetails = createDetailsWithErrorMark(rootContainer, content, "superStructure"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.superStructures).bind(ResearchOrganization::getSuperOrganizations, ResearchOrganization::setSuperOrganizations);
    }


    /**
     * Invoked for creating a new organization.
     *
     * @param saver the callback that is invoked when the organization is saved as JPA entity.
     */
    protected void openOrganizationEditor(Consumer<ResearchOrganization> saver) {
        final var newOrganization = new ResearchOrganization();
        final var editor = getOrganizationEditorFactory().createAdditionEditor(newOrganization, getEditingContext().getLogger());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.organizations.create_organization"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    private String createNameString(ResearchOrganization organization) {
        final var buffer = new StringBuilder();
        final var acronym = organization.getAcronym();
        if (!Strings.isNullOrEmpty(acronym)) {
            buffer.append(acronym);
        }
        final var name = organization.getName();
        if (!Strings.isNullOrEmpty(name)) {
            if (buffer.length() > 0) {
                buffer.append(" - "); //$NON-NLS-1$
            }
            buffer.append(name);
        }
        return buffer.toString();
    }

    private Component createNameComponent(ResearchOrganization organization) {
        return ComponentFactory.newOrganizationAvatar(organization, this.fileManager);
    }

    /**
     * Create the section for editing the communication of the organization.
     *
     * @param rootContainer the container.
     */
    protected void createCommunicationDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.organizationUrl = new TextField();
        this.organizationUrl = ComponentFactory.newClickableIconTextField(DBLP_BASE_URL, DBLP_ICON);
        this.organizationUrl.setClearButtonVisible(true);
        this.organizationUrl.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        content.add(this.organizationUrl, 2);

        this.description = new MarkdownField();
        content.add(this.description, 2);

        this.logo = new ServerSideUploadableImageField(
                this.fileManager,
                ext -> this.fileManager.makeOrganizationLogoFilename(getEditedEntity().getId(), ext),
                () -> getLogger());
        this.logo.setClearButtonVisible(true);
        content.add(this.logo, 2);

        this.communicationDetails = createDetailsWithErrorMark(rootContainer, content, "communication"); //$NON-NLS-1$

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.organizationUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.organizationUrl, this.communicationDetails))
                .bind(ResearchOrganization::getOrganizationURL, ResearchOrganization::setOrganizationURL);
        getEntityDataBinder().forField(this.description)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getDescription, ResearchOrganization::setDescription);
        getEntityDataBinder().forField(this.logo)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getPathToLogo, ResearchOrganization::setPathToLogo);
    }

    private String getTypeLabel(ResearchOrganizationType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    @Override
    protected void doSave() throws Exception {
        getEditingContext().save(this.logo);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.organizations.save_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.organizations.validation_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.organizations.delete_success2", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.organizations.save_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.organizations.validation_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.organizations.delete_error2", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        if (this.descriptionDetails != null) {
            this.descriptionDetails.setSummaryText(getTranslation("views.organizations.description_informations")); //$NON-NLS-1$
        }
        this.acronym.setLabel(getTranslation("views.organizations.acronym")); //$NON-NLS-1$
        this.name.setLabel(getTranslation("views.organizations.name")); //$NON-NLS-1$
        this.name.setHelperText(getTranslation("views.organizations.name.helper")); //$NON-NLS-1$
        this.type.setLabel(getTranslation("views.organizations.type")); //$NON-NLS-1$
        this.type.setHelperText(getTranslation("views.organizations.type.helper")); //$NON-NLS-1$);
        this.type.setItemLabelGenerator(this::getTypeLabel);

        if (this.geographyDetails != null) {
            this.geographyDetails.setSummaryText(getTranslation("views.organizations.geography_informations")); //$NON-NLS-1$
        }
        this.addresses.setLabel(getTranslation("views.organizations.addresses")); //$NON-NLS-1$
        this.addresses.setAdditionTooltip(getTranslation("views.organizations.addresses.insert")); //$NON-NLS-1$
        this.addresses.setDeletionTooltip(getTranslation("views.organizations.addresses.delete")); //$NON-NLS-1$
        this.addresses.setCreationTooltip(getTranslation("views.organizations.addresses.create")); //$NON-NLS-1$
        this.addresses.setAvailableEntityLabel(getTranslation("views.organizations.addresses.available_addresses")); //$NON-NLS-1$
        this.addresses.setSelectedEntityLabel(getTranslation("views.organizations.addresses.selected_addresses")); //$NON-NLS-1$
        this.country.setLabel(getTranslation("views.organizations.country")); //$NON-NLS-1$
        ComponentFactory.updateCountryComboBoxItems(this.country, getLocale());

        if (this.identificationDetails != null) {
            this.identificationDetails.setSummaryText(getTranslation("views.organizations.identification_informations")); //$NON-NLS-1$
        }
        this.nationalIdentifier.setLabel(getTranslation("views.organizations.national_identifier")); //$NON-NLS-1$
        this.nationalIdentifier.setHelperText(getTranslation("views.organizations.national_identifier.helper")); //$NON-NLS-1$
        this.rnsr.setLabel(getTranslation("views.organizations.rnsr")); //$NON-NLS-1$
        this.rnsr.setHelperText(getTranslation("views.organizations.rnsr.helper")); //$NON-NLS-1$

        if (this.superStructureDetails != null) {
            this.superStructureDetails.setSummaryText(getTranslation("views.organizations.super_structure_informations")); //$NON-NLS-1$
        }
        this.superStructures.setLabel(getTranslation("views.organizations.super_structures")); //$NON-NLS-1$
        this.superStructures.setAdditionTooltip(getTranslation("views.organizations.super_structures.insert")); //$NON-NLS-1$
        this.superStructures.setDeletionTooltip(getTranslation("views.organizations.super_structures.delete")); //$NON-NLS-1$
        this.superStructures.setCreationTooltip(getTranslation("views.organizations.super_structures.create")); //$NON-NLS-1$

        if (this.communicationDetails != null) {
            this.communicationDetails.setSummaryText(getTranslation("views.organizations.communication_informations")); //$NON-NLS-1$
        }
        this.organizationUrl.setLabel(getTranslation("views.organizations.url")); //$NON-NLS-1$
        this.description.setLabel(getTranslation("views.organizations.description")); //$NON-NLS-1$
        this.logo.setLabel(getTranslation("views.organizations.logo")); //$NON-NLS-1$

        this.majorOrganization.setLabel(getTranslation("views.organizations.major_organization")); //$NON-NLS-1$
    }

    /**
     * Specification that is validating any organization that has not the given identifier.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public static class OtherOrganizationSpecification implements Specification<ResearchOrganization> {

        private static final long serialVersionUID = 2689698051166694233L;

        private final Long forbiddenId;

        /**
         * Constructor.
         *
         * @param id the identifier of the research organization that is forbidden.
         */
        public OtherOrganizationSpecification(long id) {
            this.forbiddenId = Long.valueOf(id);
        }

        @Override
        public Predicate toPredicate(Root<ResearchOrganization> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            return criteriaBuilder.notEqual(root.get("id"), this.forbiddenId); //$NON-NLS-1$
        }

    }

}
