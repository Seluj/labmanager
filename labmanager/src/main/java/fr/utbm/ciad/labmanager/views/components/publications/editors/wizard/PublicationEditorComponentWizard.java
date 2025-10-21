package fr.utbm.ciad.labmanager.views.components.publications.editors.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerFormWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.journals.fields.SingleJournalNameField;
import fr.utbm.ciad.labmanager.views.components.persons.fields.MultiPersonNameField;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Wizard for adding a publication.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PublicationEditorComponentWizard extends AbstractLabManagerWizard<Publication> {

    /**
     * Constructor.
     *
     * @param productionTypeComponents         the components for the production type.
     * @param minimalInformationComponent      the components for the minimal information.
     * @param worldwideIdentificationComponent the components for the worldwide identification.
     * @param contentInformationComponent      the components for the content information.
     * @param associatedResourcesComponent     the components for the associated resources.
     * @param referenceInformationComponent    the components for the reference information.
     */
    public PublicationEditorComponentWizard(
            ContextualLoggerFactory loggerFactory,
            VerticalLayout productionTypeComponents, VerticalLayout minimalInformationComponent,
            VerticalLayout worldwideIdentificationComponent, VerticalLayout contentInformationComponent,
            VerticalLayout associatedResourcesComponent, VerticalLayout referenceInformationComponent) {
        this(defaultWizardConfiguration(null, false),
                loggerFactory,
                new PublicationImp(), productionTypeComponents, minimalInformationComponent, worldwideIdentificationComponent, contentInformationComponent, associatedResourcesComponent, referenceInformationComponent);
    }

    /**
     * Constructor.
     *
     * @param productionTypeComponents         the components for the production type.
     * @param minimalInformationComponent      the components for the minimal information.
     * @param worldwideIdentificationComponent the components for the worldwide identification.
     * @param contentInformationComponent      the components for the content information.
     * @param associatedResourcesComponent     the components for the associated resources.
     * @param referenceInformationComponent    the components for the reference information.
     * @param administrationComponents         the components for the administration information.
     */
    public PublicationEditorComponentWizard(
            ContextualLoggerFactory loggerFactory,
            VerticalLayout productionTypeComponents, VerticalLayout minimalInformationComponent,
            VerticalLayout worldwideIdentificationComponent, VerticalLayout contentInformationComponent,
            VerticalLayout associatedResourcesComponent, VerticalLayout referenceInformationComponent,
            VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false), loggerFactory,
                new PublicationImp(), productionTypeComponents, minimalInformationComponent, worldwideIdentificationComponent, contentInformationComponent, associatedResourcesComponent, referenceInformationComponent, administrationComponents);
    }

    /**
     * Constructor.
     *
     * @param properties                       the properties of the wizard.
     * @param context                          the context for editing the entity.
     * @param productionTypeComponents         the components for the production type.
     * @param minimalInformationComponent      the components for the minimal information.
     * @param worldwideIdentificationComponent the components for the worldwide identification.
     * @param contentInformationComponent      the components for the content information.
     * @param associatedResourcesComponent     the components for the associated resources.
     * @param referenceInformationComponent    the components for the reference information.
     */
    protected PublicationEditorComponentWizard(WizardConfigurationProperties properties,
                                               ContextualLoggerFactory loggerFactory,
                                               PublicationImp context, VerticalLayout productionTypeComponents, VerticalLayout minimalInformationComponent, VerticalLayout worldwideIdentificationComponent, VerticalLayout contentInformationComponent, VerticalLayout associatedResourcesComponent,
                                               VerticalLayout referenceInformationComponent) {
        super(properties, loggerFactory, context, Arrays.asList(
                new ProductionTypeComponents(context, productionTypeComponents),
                new MinimalInformationComponent(context, minimalInformationComponent),
                new WorldwideIdentificationComponent(context, worldwideIdentificationComponent),
                new ContentInformationComponent(context, contentInformationComponent),
                new AssociatedResourcesComponent(context, associatedResourcesComponent),
                new ReferenceInformationComponent(context, referenceInformationComponent
                )));
    }

    /**
     * Constructor.
     *
     * @param properties                       the properties of the wizard.
     * @param context                          the context for editing the entity.
     * @param productionTypeComponents         the components for the production type.
     * @param minimalInformationComponent      the components for the minimal information.
     * @param worldwideIdentificationComponent the components for the worldwide identification.
     * @param contentInformationComponent      the components for the content information.
     * @param associatedResourcesComponent     the components for the associated resources.
     * @param referenceInformationComponent    the components for the reference information.
     * @param administrationComponents         the components for the administration information.
     */
    protected PublicationEditorComponentWizard(WizardConfigurationProperties properties,
                                               ContextualLoggerFactory loggerFactory,
                                               PublicationImp context, VerticalLayout productionTypeComponents,
                                               VerticalLayout minimalInformationComponent, VerticalLayout worldwideIdentificationComponent, VerticalLayout contentInformationComponent, VerticalLayout associatedResourcesComponent, VerticalLayout referenceInformationComponent, VerticalLayout administrationComponents) {
        super(properties, loggerFactory, context, Arrays.asList(
                new ProductionTypeComponents(context, productionTypeComponents),
                new MinimalInformationComponent(context, minimalInformationComponent),
                new WorldwideIdentificationComponent(context, worldwideIdentificationComponent),
                new ContentInformationComponent(context, contentInformationComponent),
                new AssociatedResourcesComponent(context, associatedResourcesComponent),
                new ReferenceInformationComponent(context, referenceInformationComponent),
                new PublicationAdministration(context, administrationComponents
                )));
    }

    public boolean isNewEntity() {
        return true;
    }

    /**
     * Wizard step to input personal production type details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ProductionTypeComponents extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public ProductionTypeComponents(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.type"), 1);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {

            form.add(content);
        }


    }

    /**
     * Wizard step to input minimal information details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class MinimalInformationComponent extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public MinimalInformationComponent(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.general_informations"), 2);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            TextField title = (TextField) components.get(0).getChildren().toList().get(0);
            MultiPersonNameField authors = (MultiPersonNameField) components.get(0).getChildren().toList().get(1);
            SingleJournalNameField journal = (SingleJournalNameField) components.get(0).getChildren().toList().get(2);
            return !title.isEmpty() && !authors.getValue().isEmpty() && !journal.isEmpty();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            List<Component> components = content.getChildren().toList();
            TextField title = (TextField) components.get(0).getChildren().toList().get(0);
            MultiPersonNameField authors = (MultiPersonNameField) components.get(0).getChildren().toList().get(1);
            SingleJournalNameField journal = (SingleJournalNameField) components.get(0).getChildren().toList().get(2);

            title.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            authors.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            journal.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });


            form.add(content);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
        }

    }

    /**
     * Wizard step to input worldwide identification details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class WorldwideIdentificationComponent extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public WorldwideIdentificationComponent(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.identification_informations"), 3);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }


    }

    /**
     * Wizard step to input content information details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ContentInformationComponent extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public ContentInformationComponent(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.content_informations"), 4);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            MarkdownField markdownField = (MarkdownField) components.get(0).getChildren().toList().get(0);
            return markdownField.getValue() != null && !markdownField.getValue().isEmpty();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            List<Component> components = content.getChildren().toList();
            MarkdownField markdownField = (MarkdownField) components.get(0).getChildren().toList().get(0);

            markdownField.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            form.add(content);
        }

    }

    /**
     * Wizard step to input associated resource details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class AssociatedResourcesComponent extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public AssociatedResourcesComponent(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.resources_informations "), 5);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }


    }

    /**
     * Wizard step to input reference information details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ReferenceInformationComponent extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public ReferenceInformationComponent(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.references_informations"), 6);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }


    }

    /**
     * Wizard step to input administration details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PublicationAdministration extends AbstractLabManagerFormWizardStep<Publication> {

        private final VerticalLayout content;

        public PublicationAdministration(Publication context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.administration_details"), 7);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }


    }

    /**
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public static class PublicationImp extends Publication {

        //TODO Is it the same class as PrePublication?

        public PublicationImp() {
            super();
        }

        @Override
        public boolean isRanked() {
            return false;
        }

        @Override
        public String getWherePublishedShortDescription() {
            return "";
        }

        @Override
        public String getPublicationTarget() {
            return "";
        }
    }

}
