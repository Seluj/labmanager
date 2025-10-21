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

package fr.utbm.ciad.labmanager.views.components.journals.editors.regular;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.ranking.JournalAnnualRankingField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IsbnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IssnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import org.springframework.context.support.MessageSourceAccessor;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

/**
 * Abstract implementation for the editor of the information related to a scientific journal.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractJournalEditor extends AbstractEntityEditor<Journal> {

    private static final long serialVersionUID = -6217566556396821734L;

    protected DetailsWithErrorMark descriptionDetails;

    protected TextField name;

    protected RadioButtonGroup<Boolean> openAccess;

    protected TextField journalUrl;

    protected DetailsWithErrorMark publisherDetails;

    protected TextField publisherName;

    protected TextField publisherAddress;

    protected TextField issn;

    protected TextField isbn;

    protected Details rankingDetails;

    protected TextField wosId;

    protected TextField wosCategory;

    protected TextField scimagoId;

    protected TextField scimagoCategory;

    protected JournalAnnualRankingField rankings;

    protected JournalService journalService;

    /**
     * Constructor.
     *
     * @param context                       the editing context for the conference.
     * @param journalCreationStatusComputer the tool for computer the creation status for the journals.
     * @param relinkEntityWhenSaving        indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                      be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param journalService                the service to have access to all the journal entities.
     * @param authenticatedUser             the connected user.
     * @param messages                      the accessor to the localized messages (Spring layer).
     * @param properties                    specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractJournalEditor(EntityEditingContext<Journal> context,
                                 EntityCreationStatusComputer<Journal> journalCreationStatusComputer,
                                 boolean relinkEntityWhenSaving, JournalService journalService,
                                 AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                 ConstructionPropertiesBuilder properties) {
        super(Journal.class, authenticatedUser, messages,
                journalCreationStatusComputer, context, null, relinkEntityWhenSaving,
                properties
                        .map(PROP_ADMIN_SECTION, "views.journals.administration_details") //$NON-NLS-1$
                        .map(PROP_ADMIN_VALIDATION_BOX, "views.journals.administration.validated_journal")); //$NON-NLS-1$
        this.journalService = journalService;
    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        createDescriptionDetails(rootContainer);
        createRankingDetails(rootContainer);
        createPublisherDetails(rootContainer);
        if (isBaseAdmin()) {
            createAdministrationComponents(rootContainer, it -> it.bind(Journal::isValidated, Journal::setValidated));
        }
    }

    /**
     * Create the section for editing the description of the journal.
     *
     * @param rootContainer the container.
     */
    protected void createDescriptionDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.name = new TextField();
        this.name.setPrefixComponent(VaadinIcon.HASH.create());
        this.name.setRequired(true);
        this.name.setClearButtonVisible(true);
        content.add(this.name, 2);

        this.openAccess = new RadioButtonGroup<>();
        this.openAccess.setItems(null, Boolean.TRUE, Boolean.FALSE);
        this.openAccess.setValue(null);
        setOpenAccessRenderer();
        content.add(this.openAccess, 2);

        this.journalUrl = new TextField();
        this.journalUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        this.journalUrl.setClearButtonVisible(true);
        content.add(this.journalUrl, 2);

        this.descriptionDetails = createDetailsWithErrorMark(rootContainer, content, "description", true); //$NON-NLS-1$

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.name.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.name, this.descriptionDetails))
                .bind(Journal::getJournalName, Journal::setJournalName);
        getEntityDataBinder().forField(this.openAccess)
                .bind(Journal::getOpenAccess, Journal::setOpenAccess);
        getEntityDataBinder().forField(this.journalUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.journalUrl, this.descriptionDetails))
                .bind(Journal::getJournalURL, Journal::setJournalURL);
    }

    private void setOpenAccessRenderer() {
        this.openAccess.setRenderer(new ComponentRenderer<>(it -> {
            final Span span = new Span();
            if (it == null) {
                span.setText(getTranslation("views.journals.open_access.indeterminate")); //$NON-NLS-1$
            } else if (it) {
                span.setText(getTranslation("views.journals.open_access.yes")); //$NON-NLS-1$
            } else {
                span.setText(getTranslation("views.journals.open_access.no")); //$NON-NLS-1$
            }
            return span;
        }));
    }

    /**
     * Create the section for editing the publishing information of the journal.
     *
     * @param rootContainer the container.
     */
    protected void createPublisherDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.publisherName = new TextField();
        this.publisherName.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
        this.publisherName.setRequired(true);
        this.publisherName.setClearButtonVisible(true);
        content.add(this.publisherName, 2);

        this.publisherAddress = new TextField();
        this.publisherAddress.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        this.publisherAddress.setRequired(true);
        this.publisherAddress.setClearButtonVisible(true);
        content.add(this.publisherAddress, 2);

        this.issn = new TextField();
        this.issn.setPrefixComponent(VaadinIcon.HASH.create());
        this.issn.setClearButtonVisible(true);
        content.add(this.issn, 1);

        this.isbn = new TextField();
        this.isbn.setPrefixComponent(VaadinIcon.HASH.create());
        this.isbn.setClearButtonVisible(true);
        content.add(this.isbn, 1);

        this.publisherDetails = createDetailsWithErrorMark(rootContainer, content, "publisher"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.publisherName)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.publisher_name.error"))) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.publisherName, this.publisherDetails))
                .bind(Journal::getPublisher, Journal::setPublisher);
        getEntityDataBinder().forField(this.publisherAddress)
                .withConverter(new StringTrimer())
                .bind(Journal::getAddress, Journal::setAddress);
        getEntityDataBinder().forField(this.issn)
                .withConverter(new StringTrimer())
                .withValidator(new IssnValidator(getTranslation("views.journals.issn.error"), true)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.issn, this.publisherDetails))
                .bind(Journal::getISSN, Journal::setISSN);
        getEntityDataBinder().forField(this.isbn)
                .withConverter(new StringTrimer())
                .withValidator(new IsbnValidator(getTranslation("views.journals.isbn.error"), true)) //$NON-NLS-1$
                .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.isbn, this.publisherDetails))
                .bind(Journal::getISBN, Journal::setISBN);
    }

    /**
     * Create the section for editing the ranking information of the journal.
     *
     * @param rootContainer the container.
     */
    protected void createRankingDetails(VerticalLayout rootContainer) {
        final var content = ComponentFactory.newColumnForm(2);

        this.wosId = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wosId.setPrefixComponent(VaadinIcon.HASH.create());
        this.wosId.setClearButtonVisible(true);
        content.add(this.wosId, 2);

        this.wosCategory = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wosCategory.setPrefixComponent(VaadinIcon.FILE_TREE_SMALL.create());
        this.wosCategory.setClearButtonVisible(true);
        content.add(this.wosCategory, 2);

        this.scimagoId = ComponentFactory.newClickableIconTextField(SCIMAGO_BASE_URL, SCIMAGO_ICON);
        this.scimagoId.setPrefixComponent(VaadinIcon.HASH.create());
        this.scimagoId.setClearButtonVisible(true);
        content.add(this.scimagoId, 2);

        this.scimagoCategory = ComponentFactory.newClickableIconTextField(SCIMAGO_BASE_URL, SCIMAGO_ICON);
        this.scimagoCategory.setPrefixComponent(VaadinIcon.FILE_TREE_SMALL.create());
        this.scimagoCategory.setClearButtonVisible(true);
        content.add(this.scimagoCategory, 2);

        this.rankings = new JournalAnnualRankingField();
        content.add(this.rankings, 2);

        this.rankingDetails = createDetails(rootContainer, content, "ranking"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.wosId)
                .withConverter(new StringTrimer())
                .bind(Journal::getWosId, Journal::setWosId);
        getEntityDataBinder().forField(this.wosCategory)
                .withConverter(new StringTrimer())
                .bind(Journal::getWosCategory, Journal::setWosCategory);
        getEntityDataBinder().forField(this.scimagoId)
                .withConverter(new StringTrimer())
                .bind(Journal::getScimagoId, Journal::setScimagoId);
        getEntityDataBinder().forField(this.scimagoCategory)
                .withConverter(new StringTrimer())
                .bind(Journal::getScimagoCategory, Journal::setScimagoCategory);
        getEntityDataBinder().forField(this.rankings)
                .bind(Journal::getQualityIndicators, Journal::setQualityIndicators);
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.journals.save_success", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.journals.validation_success", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.journals.delete_success2", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.journals.save_error", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.journals.validation_error", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.journals.delete_error2", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        if (this.descriptionDetails != null) {
            this.descriptionDetails.setSummaryText(getTranslation("views.journals.description_informations")); //$NON-NLS-1$
        }
        this.name.setLabel(getTranslation("views.journals.name")); //$NON-NLS-1$
        this.openAccess.setLabel(getTranslation("views.journals.open_access")); //$NON-NLS-1$
        // Force the refreshing of the radio button items
        setOpenAccessRenderer();
        this.journalUrl.setLabel(getTranslation("views.journals.url")); //$NON-NLS-1$

        if (this.rankingDetails != null) {
            this.rankingDetails.setSummaryText(getTranslation("views.journals.ranking_informations")); //$NON-NLS-1$
        }
        this.wosId.setLabel(getTranslation("views.journals.wos.id")); //$NON-NLS-1$
        this.wosId.setHelperText(getTranslation("views.journals.wos.id.help")); //$NON-NLS-1$
        this.wosCategory.setLabel(getTranslation("views.journals.wos.category")); //$NON-NLS-1$
        this.wosCategory.setHelperText(getTranslation("views.journals.wos.category.help")); //$NON-NLS-1$
        this.scimagoId.setLabel(getTranslation("views.journals.scimago.id")); //$NON-NLS-1$
        this.scimagoId.setHelperText(getTranslation("views.journals.scimago.id.help")); //$NON-NLS-1$
        this.scimagoCategory.setLabel(getTranslation("views.journals.scimago.category")); //$NON-NLS-1$
        this.scimagoCategory.setHelperText(getTranslation("views.journals.scimago.category.help")); //$NON-NLS-1$
        this.rankings.setLabel(getTranslation("views.journals.rankings")); //$NON-NLS-1$
        this.rankings.setHelperText(getTranslation("views.journals.rankings.help")); //$NON-NLS-1$

        if (this.publisherDetails != null) {
            this.publisherDetails.setSummaryText(getTranslation("views.journals.publisher_informations")); //$NON-NLS-1$
        }
        this.publisherName.setLabel(getTranslation("views.journals.publisher_name")); //$NON-NLS-1$
        this.publisherName.setHelperText(getTranslation("views.journals.publisher_name.help")); //$NON-NLS-1$
        this.publisherAddress.setLabel(getTranslation("views.journals.publisher_address")); //$NON-NLS-1$
        this.issn.setLabel(getTranslation("views.journals.issn")); //$NON-NLS-1$
        this.isbn.setLabel(getTranslation("views.journals.isbn")); //$NON-NLS-1$
    }

}
