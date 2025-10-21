package fr.utbm.ciad.labmanager.views.components.journals.editors.wizard;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.addons.logger.DelegateContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.ranking.JournalAnnualRankingField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IsbnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IssnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import fr.utbm.ciad.labmanager.views.components.journals.editors.regular.AbstractJournalEditor;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

/**
 * Implementation for the editor of the information related to a journal. It is directly linked for
 * using it with a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractJournalEditorWizard extends AbstractJournalEditor {

    private static final long serialVersionUID = 3928100811567654630L;

    private JournalEditorComponentWizard journalEditorComponentWizard;


    /**
     * Constructor.
     *
     * @param context                       the editing context for the conference.
     * @param journalCreationStatusComputer the tool for computer the creation status for the journals.
     * @param relinkEntityWhenSaving        indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                      be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser             the connected user.
     * @param messages                      the accessor to the localized messages (Spring layer).
     * @param properties                    specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public AbstractJournalEditorWizard(AbstractEntityService.EntityEditingContext<Journal> context,
                                       EntityCreationStatusComputer<Journal> journalCreationStatusComputer, boolean relinkEntityWhenSaving,
                                       AuthenticatedUser authenticatedUser, JournalService journalService, MessageSourceAccessor messages,
                                       ConstructionPropertiesBuilder properties) {

        super(context, journalCreationStatusComputer, relinkEntityWhenSaving, journalService, authenticatedUser, messages, properties);

    }

    /**
     * Create the content of the editor.
     * This function should invoke {@link #createAdministrationComponents(VerticalLayout, Consumer, Consumer)}.
     *
     * @param rootContainer the container.
     * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer)
     */
    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        if (isBaseAdmin()) {
            journalEditorComponentWizard = new JournalEditorComponentWizard(
                    new DelegateContextualLoggerFactory(getLogger()),
                    createDescriptionDetails(), createRankingDetails(), createPublisherDetails(),
                    createAdministrationComponents((Consumer<FormLayout>) null, it -> it.bind(Journal::isValidated, Journal::setValidated)));
        }
        journalEditorComponentWizard = new JournalEditorComponentWizard(
                new DelegateContextualLoggerFactory(getLogger()),
                createDescriptionDetails(), createRankingDetails(), createPublisherDetails());

        rootContainer.add(journalEditorComponentWizard);
    }

    /**
     * Create the section for editing the description of the journal.
     *
     * @return The content.
     */
    protected VerticalLayout createDescriptionDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
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


        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.name.error"))) //$NON-NLS-1$
                .bind(Journal::getJournalName, Journal::setJournalName);
        getEntityDataBinder().forField(this.openAccess)
                .bind(Journal::getOpenAccess, Journal::setOpenAccess);
        getEntityDataBinder().forField(this.journalUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Journal::getJournalURL, Journal::setJournalURL);

        verticalLayout.add(content);
        return verticalLayout;
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
     * @return The content.
     */
    protected VerticalLayout createPublisherDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
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

        getEntityDataBinder().forField(this.publisherName)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.publisher_name.error"))) //$NON-NLS-1$
                .bind(Journal::getPublisher, Journal::setPublisher);
        getEntityDataBinder().forField(this.publisherAddress)
                .withConverter(new StringTrimer())
                .bind(Journal::getAddress, Journal::setAddress);
        getEntityDataBinder().forField(this.issn)
                .withConverter(new StringTrimer())
                .withValidator(new IssnValidator(getTranslation("views.journals.issn.error"), true)) //$NON-NLS-1$
                .bind(Journal::getISSN, Journal::setISSN);
        getEntityDataBinder().forField(this.isbn)
                .withConverter(new StringTrimer())
                .withValidator(new IsbnValidator(getTranslation("views.journals.isbn.error"), true)) //$NON-NLS-1$
                .bind(Journal::getISBN, Journal::setISBN);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /**
     * Create the section for editing the ranking information of the journal.
     *
     * @return The content.
     */
    protected VerticalLayout createRankingDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
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

        verticalLayout.add(content);
        return verticalLayout;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
    }
}
