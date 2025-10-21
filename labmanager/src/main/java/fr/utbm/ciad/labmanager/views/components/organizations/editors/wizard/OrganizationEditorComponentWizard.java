package fr.utbm.ciad.labmanager.views.components.organizations.editors.wizard;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerFormWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;

/**
 * Wizard for adding an organization.
 *
 * @author $Author: erenon$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class OrganizationEditorComponentWizard extends AbstractLabManagerWizard<ResearchOrganization> {

    private static final long serialVersionUID = -8967025706133759750L;

    /**
     * Constructor.
     *
     * @param descriptionDetailComponents    the description detail components.
     * @param geographicalDetailComponents   the geographical detail components.
     * @param identificationDetailComponents the identification detail components.
     * @param superStructureDetailComponents the super structure detail components.
     * @param communicationDetailComponents  the communication detail components.
     */
    public OrganizationEditorComponentWizard(
            ContextualLoggerFactory loggerFactory,
            VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents,
            VerticalLayout identificationDetailComponents, VerticalLayout superStructureDetailComponents,
            VerticalLayout communicationDetailComponents) {
        this(defaultWizardConfiguration(null, false), loggerFactory,
                new ResearchOrganization(), descriptionDetailComponents, geographicalDetailComponents, identificationDetailComponents, superStructureDetailComponents, communicationDetailComponents);
    }

    /**
     * Constructor.
     *
     * @param descriptionDetailComponents    the description detail components.
     * @param geographicalDetailComponents   the geographical detail components.
     * @param identificationDetailComponents the identification detail components.
     * @param superStructureDetailComponents the super structure detail components.
     * @param communicationDetailComponents  the communication detail components.
     * @param administrationComponents       the administration detail components.
     */
    public OrganizationEditorComponentWizard(
            ContextualLoggerFactory loggerFactory,
            VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents,
            VerticalLayout identificationDetailComponents, VerticalLayout superStructureDetailComponents,
            VerticalLayout communicationDetailComponents, VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false), loggerFactory,
                new ResearchOrganization(), descriptionDetailComponents, geographicalDetailComponents, identificationDetailComponents, superStructureDetailComponents, communicationDetailComponents, administrationComponents);
    }

    /**
     * Constructor.
     *
     * @param properties                     the properties.
     * @param context                        the context.
     * @param descriptionDetailComponents    the description detail components.
     * @param geographicalDetailComponents   the geographical detail components.
     * @param identificationDetailComponents the identification detail components.
     * @param superStructureDetailComponents the super structure detail components.
     * @param communicationDetailComponents  the communication detail components.
     */
    protected OrganizationEditorComponentWizard(WizardConfigurationProperties properties,
                                                ContextualLoggerFactory loggerFactory,
                                                ResearchOrganization context, VerticalLayout descriptionDetailComponents,
                                                VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,
                                                VerticalLayout superStructureDetailComponents, VerticalLayout communicationDetailComponents) {
        super(properties, loggerFactory, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new GeographicalDetailComponent(context, geographicalDetailComponents),
                new IdentificationDetailComponent(context, identificationDetailComponents),
                new SuperStructureDetailComponent(context, superStructureDetailComponents),
                new CommunicationDetailComponent(context, communicationDetailComponents)));
    }

    /**
     * Constructor.
     *
     * @param properties                     the properties.
     * @param context                        the context.
     * @param descriptionDetailComponents    the description detail components.
     * @param geographicalDetailComponents   the geographical detail components.
     * @param identificationDetailComponents the identification detail components.
     * @param superStructureDetailComponents the super structure detail components.
     * @param communicationDetailComponents  the communication detail components.
     * @param administrationComponents       the administration detail components.
     */
    protected OrganizationEditorComponentWizard(WizardConfigurationProperties properties,
                                                ContextualLoggerFactory loggerFactory,
                                                ResearchOrganization context, VerticalLayout descriptionDetailComponents,
                                                VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,
                                                VerticalLayout superStructureDetailComponents, VerticalLayout communicationDetailComponents,
                                                VerticalLayout administrationComponents) {
        super(properties, loggerFactory, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new GeographicalDetailComponent(context, geographicalDetailComponents),
                new IdentificationDetailComponent(context, identificationDetailComponents),
                new SuperStructureDetailComponent(context, superStructureDetailComponents),
                new CommunicationDetailComponent(context, communicationDetailComponents),
                new OrganizationAdministration(context, administrationComponents)));
    }

    /**
     * Wizard step to input description details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class DescriptionDetailComponent extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = 7411011360236405995L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public DescriptionDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.description_informations"), 1); //$NON-NLS-1$
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            final var components = this.content.getChildren().toList();

            final var acronym = (TextField) components.get(0).getChildren().toList().get(0);
            final var name = (TextField) components.get(0).getChildren().toList().get(1);

            return !acronym.isEmpty() && !name.isEmpty();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            final var components = this.content.getChildren().toList();
            final var acronym = (TextField) components.get(0).getChildren().toList().get(0);
            final var name = (TextField) components.get(0).getChildren().toList().get(1);

            acronym.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            name.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            form.add(this.content);
        }

    }

    /**
     * Wizard step to input geographical details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class GeographicalDetailComponent extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = 8902836389401424098L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public GeographicalDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.geography_informations"), 2); //$NON-NLS-1$
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
            form.add(this.content);
        }

    }

    /**
     * Wizard step to input identification details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class IdentificationDetailComponent extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = -6926957941646510722L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public IdentificationDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.identification_informations"), 3); //$NON-NLS-1$
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
            form.add(this.content);
        }

    }

    /**
     * Wizard step to input super-structure details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class SuperStructureDetailComponent extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = -4013317155841508735L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public SuperStructureDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.super_structure_informations"), 4); //$NON-NLS-1$
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
            form.add(this.content);
        }

    }

    /**
     * Wizard step to input communication details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class CommunicationDetailComponent extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = 6146649659074562352L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public CommunicationDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.communication_informations"), 5); //$NON-NLS-1$
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
            form.add(this.content);
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
    protected static class OrganizationAdministration extends AbstractLabManagerFormWizardStep<ResearchOrganization> {

        private static final long serialVersionUID = 4082126905982402500L;

        private final VerticalLayout content;

        /**
         * Constructor.
         *
         * @param context the organization to be edited.
         * @param content the form content.
         */
        public OrganizationAdministration(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.administration_details"), 6); //$NON-NLS-1$
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
            form.add(this.content);
        }

    }

}