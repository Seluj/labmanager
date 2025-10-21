package fr.utbm.ciad.labmanager.views.components.journals.editors.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerFormWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Wizard for adding a journal.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class JournalEditorComponentWizard extends AbstractLabManagerWizard<Journal> {

    /**
     * Constructor.
     *
     * @param descriptionDetailComponents    the description detail components.
     * @param rankingInformationComponents   the ranking information components.
     * @param publisherInformationComponents the publisher information components.
     */
    public JournalEditorComponentWizard(ContextualLoggerFactory loggerFactory,
                                        VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents) {
        this(defaultWizardConfiguration(null, false), loggerFactory,
                new Journal(), descriptionDetailComponents, rankingInformationComponents, publisherInformationComponents);
    }

    /**
     * Constructor.
     *
     * @param descriptionDetailComponents    the description detail components.
     * @param rankingInformationComponents   the ranking information components.
     * @param publisherInformationComponents the publisher information components.
     * @param administrationComponents       the administration detail components.
     */
    public JournalEditorComponentWizard(ContextualLoggerFactory loggerFactory,
                                        VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents, VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false), loggerFactory,
                new Journal(), descriptionDetailComponents, rankingInformationComponents, publisherInformationComponents, administrationComponents);
    }

    /**
     * Constructor.
     *
     * @param properties                     the wizard configuration properties.
     * @param context                        the context.
     * @param descriptionDetailComponents    the description detail components.
     * @param rankingInformationComponents   the ranking information components.
     * @param publisherInformationComponents the publisher information components.
     */
    protected JournalEditorComponentWizard(WizardConfigurationProperties properties, ContextualLoggerFactory loggerFactory,
                                           Journal context, VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents) {
        super(properties, loggerFactory, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new RankingInformationComponent(context, rankingInformationComponents),
                new PublisherInformationComponent(context, publisherInformationComponents)
        ));
    }

    /**
     * Constructor.
     *
     * @param properties                     the wizard configuration properties.
     * @param context                        the context.
     * @param descriptionDetailComponents    the description detail components.
     * @param rankingInformationComponents   the ranking information components.
     * @param publisherInformationComponents the publisher information components.
     * @param administrationComponents       the administration detail components.
     */
    protected JournalEditorComponentWizard(WizardConfigurationProperties properties, ContextualLoggerFactory loggerFactory,
                                           Journal context, VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents, VerticalLayout administrationComponents) {
        super(properties, loggerFactory, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new RankingInformationComponent(context, rankingInformationComponents),
                new PublisherInformationComponent(context, publisherInformationComponents),
                new JournalAdministration(context, administrationComponents)
        ));
    }

    public boolean isNewEntity() {
        return true;
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
    protected static class DescriptionDetailComponent extends AbstractLabManagerFormWizardStep<Journal> {

        private final VerticalLayout content;

        public DescriptionDetailComponent(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.description_informations"), 1);
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

            return !title.isEmpty();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            List<Component> components = content.getChildren().toList();
            TextField title = (TextField) components.get(0).getChildren().toList().get(0);

            title.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            form.add(content);
        }

    }

    /**
     * Wizard step to input ranking details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class RankingInformationComponent extends AbstractLabManagerFormWizardStep<Journal> {

        private final VerticalLayout content;

        public RankingInformationComponent(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.ranking_informations"), 2);
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
     * Wizard step to input publisher informations.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PublisherInformationComponent extends AbstractLabManagerFormWizardStep<Journal> {

        private final VerticalLayout content;

        public PublisherInformationComponent(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.publisher_informations"), 3);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            TextField publisherName = (TextField) components.get(0).getChildren().toList().get(0);

            return !publisherName.isEmpty();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            List<Component> components = content.getChildren().toList();
            TextField publisherName = (TextField) components.get(0).getChildren().toList().get(0);

            publisherName.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
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
    protected static class JournalAdministration extends AbstractLabManagerFormWizardStep<Journal> {

        private final VerticalLayout content;

        public JournalAdministration(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.administration_details"), 4);
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

}
