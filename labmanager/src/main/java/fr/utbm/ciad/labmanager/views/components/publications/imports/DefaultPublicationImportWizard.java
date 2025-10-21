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

package fr.utbm.ciad.labmanager.views.components.publications.imports;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style.AlignSelf;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionBiFunction;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionProvider;
import fr.utbm.ciad.labmanager.utils.io.bibtex.BibTeXConstants;
import fr.utbm.ciad.labmanager.utils.io.ris.RISConstants;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressExtension;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.generic.GenericUploadableFilesField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.generic.UploadBuffer;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerFormWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerProgressionWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizardStep;
import fr.utbm.ciad.labmanager.views.components.publications.editors.PublicationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.PublicationCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.publications.imports.ImportData.QualifiedPublication;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import io.overcoded.vaadin.wizard.config.WizardContentConfigurationProperties;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Wizard for importing publications.
 *
 * @author $Author: sgalland$
 * @author $Author: pschneiderlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DefaultPublicationImportWizard extends AbstractLabManagerWizard<ImportData> {

    private static final long serialVersionUID = -4042707970730335643L;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the JPA entities of publications.
     * @param editorFactory      the factory for the publication editors.
     * @param statusComputer     the computer of the publication status.
     * @param messages           the accessor to the localized messages.
     * @param loggerFactory      the factory to be used for the composite logger.
     */
    public DefaultPublicationImportWizard(PublicationService publicationService, PublicationEditorFactory editorFactory,
                                          PublicationCreationStatusComputer statusComputer, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory) {
        this(publicationService, editorFactory, statusComputer, messages, loggerFactory,
                defaultWizardConfiguration(null, false),
                new ImportData());
    }

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the JPA entities of publications.
     * @param editorFactory      the factory for the publication editors.
     * @param statusComputer     the computer of the publication status.
     * @param messages           the accessor to the localized messages.
     * @param properties         the properties of the wizard.
     * @param context            the data context.
     */
    protected DefaultPublicationImportWizard(PublicationService publicationService, PublicationEditorFactory editorFactory,
                                             PublicationCreationStatusComputer statusComputer, MessageSourceAccessor messages,
                                             ContextualLoggerFactory loggerFactory, WizardConfigurationProperties properties, ImportData context) {
        this(properties, context, loggerFactory, Arrays.asList(
                new FileUploadStep(context),
                new FileReadingStep(context, publicationService, statusComputer),
                new SummaryValidationStep(context, messages, editorFactory),
                new PublicationSavingStep(context, publicationService)));
    }

    private DefaultPublicationImportWizard(WizardConfigurationProperties properties, ImportData context,
                                           ContextualLoggerFactory loggerFactory, List<WizardStep<ImportData>> steps) {
        super(properties, loggerFactory, context, steps);
    }

    /**
     * Wizard step to input the files to import.
     *
     * @author $Author: sgalland$
     * @author $Author: pschneiderlin$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class FileUploadStep extends AbstractLabManagerFormWizardStep<ImportData> {

        private static final long serialVersionUID = -6523928192461900353L;

        private GenericUploadableFilesField upload;

        /**
         * Constructor.
         *
         * @param context the wizard context.
         */
        public FileUploadStep(ImportData context) {
            super(context, ComponentFactory.getTranslation("views.publication.import.step1.title"), 1); //$NON-NLS-1$
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            return this.upload != null && this.upload.hasUploadedData();
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void onStepActivated() {
            getContext().getUploadBuffers().clear();
            super.onStepActivated();
        }

        @Override
        protected void createForm(FormLayout form) {
            this.upload = new GenericUploadableFilesField(
                    () -> getLogger(),
                    BibTeXConstants.FILENAME_EXTENSION, RISConstants.FILENAME_EXTENSION);
            this.upload.setLabel(ComponentFactory.getTranslation("views.publication.import.step1.label")); //$NON-NLS-1$
            this.upload.addSucceededListener(it -> {
                updateButtonStateForNextStep();
            });
            this.upload.addFileRemovedListener(it -> {
                updateButtonStateForNextStep();
            });
            form.add(this.upload, 2);
            this.binder.forField(this.upload).bind(ImportData::getUploadBuffers, ImportData::setUploadBuffers);
        }

    }

    /**
     * Wizard step extracting the publications from the selected input files in the previous wizard step.
     *
     * @author $Author: sgalland$
     * @author $Author: pschneiderlin$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class FileReadingStep extends AbstractLabManagerProgressionWizardStep<ImportData> {

        private static final long serialVersionUID = -5299969892527511594L;

        private final PublicationService publicationService;

        private final PublicationCreationStatusComputer statusComputer;

        /**
         * Constructor.
         *
         * @param context            the wizard context.
         * @param publicationService the service for accessing the JPA entities of publications.
         * @param statusComputer     the computer of the publication status to be used.
         */
        public FileReadingStep(ImportData context, PublicationService publicationService, PublicationCreationStatusComputer statusComputer) {
            super(context, ComponentFactory.getTranslation("views.publication.import.step2.title"), 2, 2, true, false); //$NON-NLS-1$
            this.publicationService = publicationService;
            this.statusComputer = statusComputer;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.publication.import.step2.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            Image image;
            switch (taskNo) {
                case 0:
                    // BibTeX
                    final var icon0 = ComponentFactory.newStreamImage(ViewConstants.IMPORT_BIBTEX_BLACK_ICON);
                    image = new Image(icon0, ""); //$NON-NLS-1$
                    break;
                case 1:
                    // RIS
                    final var icon1 = ComponentFactory.newStreamImage(ViewConstants.IMPORT_RIS_BLACK_ICON);
                    image = new Image(icon1, ""); //$NON-NLS-1$
                    break;
                default:
                    throw new IllegalStateException();
            }
            image.setMinHeight(ViewConstants.ICON_SIZE, Unit.POINTS);
            image.setMaxHeight(ViewConstants.MAX_ICON_SIZE, Unit.POINTS);
            image.setMinWidth(ViewConstants.ICON_SIZE, Unit.POINTS);
            image.setMaxWidth(ViewConstants.MAX_ICON_SIZE, Unit.POINTS);
            final var style = image.getStyle();
            style.setAlignSelf(AlignSelf.BASELINE);
            style.setMarginRight("var(--lumo-space-s)"); //$NON-NLS-1$
            return image;
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.publication.import.step2.reading_file"); //$NON-NLS-1$
            final var progress0 = progression == null ? new DefaultProgression() : progression;
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progress0, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.publication.import.step2.files_downloaded"); //$NON-NLS-1$
            final List<UploadBuffer> buffers;
            final SerializableExceptionBiFunction<Reader, Progression, List<Publication>> readerFunction;
            switch (taskNo) {
                case 0:
                    buffers = getContext().getUploadBuffersForExtension(BibTeXConstants.FILENAME_EXTENSION);
                    readerFunction = (reader, progress2) -> {
                        return this.publicationService.readPublicationsFromBibTeX(reader, true, false, true, true, true, progress2);
                    };
                    break;
                case 1:
                    buffers = getContext().getUploadBuffersForExtension(RISConstants.FILENAME_EXTENSION);
                    readerFunction = (reader, progress2) -> this.publicationService.readPublicationsFromRIS(reader, true, false, true, true, true, null, progress2);
                    break;
                default:
                    throw new IllegalStateException();
            }
            return () -> {
                extendedProgression0.setProperties(0, 0, buffers.size() + 1, false);
                getContext().getQualifiedPublications().clear();
                extendedProgression0.increment();
                for (final var buffer : buffers) {
                    final var progress1 = extendedProgression0.subTask(1);
                    progress1.setComment(buffer.getFileName());
                    try (final var reader = new BufferedReader(new InputStreamReader(buffer.getInputStream()))) {
                        final var pubs = readerFunction.apply(reader, progress1);
                        if (pubs != null && !pubs.isEmpty()) {
                            for (final var publication : pubs) {
                                final var status = this.statusComputer.computeEntityCreationStatusFor(publication);
                                getContext().getQualifiedPublications().add(new QualifiedPublication(publication, status));
                            }
                        }
                    } finally {
                        progress1.end();
                    }
                }
                return terminationMessage0;
            };
        }

    }


    /**
     * Wizard step to summarize and validate the imported pubications.
     *
     * @author $Author: sgalland$
     * @author $Author: pschneiderlin$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class SummaryValidationStep extends AbstractLabManagerWizardStep<ImportData> {

        private static final long serialVersionUID = 1066158190823213276L;

        private final MessageSourceAccessor messages;

        private final WizardContentConfigurationProperties properties;

        private final PublicationEditorFactory editorFactory;

        private final String personCreationLabelKey;

        private final String personFieldLabelKey;

        private final String personFieldHelperLabelKey;

        private final String personNullErrorKey;

        private final String personDuplicateErrorKey;

        private Grid<AbstractEntityEditor<Publication>> grid;

        /**
         * Constructor.
         *
         * @param context       the data context.
         * @param messages      the accessor to the localized messages.
         * @param editorFactory the factory for the publication editors.
         * @param logger        the logger to be used in this step.
         */
        public SummaryValidationStep(ImportData context, MessageSourceAccessor messages, PublicationEditorFactory editorFactory) {
            this(context,
                    new WizardContentConfigurationProperties(),
                    messages, editorFactory,
                    "views.publication.new_author", //$NON-NLS-1$
                    "views.publication.authors", //$NON-NLS-1$
                    "views.publication.authors.helper", //$NON-NLS-1$
                    "views.publication.authors.error.null", //$NON-NLS-1$
                    "views.publication.authors.error.duplicate"); //$NON-NLS-1$
        }

        /**
         * Constructor.
         *
         * @param context                   the data context.
         * @param properties                the wieard's properties.
         * @param messages                  the accessor to the localized messages.
         * @param editorFactory             the factory for the publication editors.
         * @param personCreationLabelKey    the key that is used for retrieving the text for creating a new person and associating it to the publication.
         * @param personFieldLabelKey       the key that is used for retrieving the text for the label of the author/editor field.
         * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
         * @param personNullErrorKey        the key that is used for retrieving the text of the author/editor null error.
         * @param personDuplicateErrorKey   the key that is used for retrieving the text of the author/editor duplicate error.
         */
        public SummaryValidationStep(ImportData context, WizardContentConfigurationProperties properties, MessageSourceAccessor messages, PublicationEditorFactory editorFactory,
                                     String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                     String personNullErrorKey, String personDuplicateErrorKey) {
            super(context, ComponentFactory.getTranslation("views.publication.import.step3.title"), 3); //$NON-NLS-1$
            this.properties = properties;
            this.messages = messages;
            this.editorFactory = editorFactory;
            this.personCreationLabelKey = personCreationLabelKey;
            this.personFieldLabelKey = personFieldLabelKey;
            this.personFieldHelperLabelKey = personFieldHelperLabelKey;
            this.personNullErrorKey = personNullErrorKey;
            this.personDuplicateErrorKey = personDuplicateErrorKey;
        }

        private static PublicationType[] getSupportedPublicationTypeArray() {
            return PublicationType.values();
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public Div getLayout() {
            final var html = "<div>" + //$NON-NLS-1$
                    ComponentFactory.getTranslation("views.publication.import.step3.column.instructions") + //$NON-NLS-1$
                    "</div>"; //$NON-NLS-1$
            final var informationBox = AbstractLabManagerWizard.createInformationBox(new Html(html), this.properties);

            final var locale = AbstractLabManagerWizard.getWizard(this, DefaultPublicationImportWizard.class).get().getLocale();

            this.grid = new Grid<>();
            // Status column
            this.grid.addColumn(new ComponentRenderer<>(Span::new, this::createPublicationStatusIndicator))
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.status")) //$NON-NLS-1$
                    .setTooltipGenerator(this::createPublicationStatusTooltip)
                    .setResizable(true)
                    .setAutoWidth(true);
            // Title column
            this.grid.addColumn(this::createPublicationTitleLabel)
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.title")) //$NON-NLS-1$
                    .setTooltipGenerator(this::createPublicationTitleTooltip)
                    .setResizable(true)
                    .setFlexGrow(1)
                    .setAutoWidth(true);
            // Authors column
            this.grid.addColumn(this::createPublicationAuthorLabel)
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.authors")) //$NON-NLS-1$
                    .setTooltipGenerator(this::createPublicationAuthorTooltip)
                    .setResizable(true)
                    .setFlexGrow(1)
                    .setAutoWidth(true);
            // Type column
            this.grid.addColumn(editor -> createPublicationTypeLabel(editor, locale))
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.type")) //$NON-NLS-1$
                    .setTooltipGenerator(editor -> createPublicationTypeTooltip(editor, locale))
                    .setResizable(true);
            // Category column
            this.grid.addColumn(this::createPublicationCategoryLabel)
                    .setTooltipGenerator(publication -> createPublicationCategoryTooltip(publication, locale))
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.category")); //$NON-NLS-1$
            // Publication details column
            this.grid.addColumn(this::createPublicationDetailsLabel)
                    .setHeader(ComponentFactory.getTranslation("views.publication.import.step3.column.details")) //$NON-NLS-1$
                    .setTooltipGenerator(this::createPublicationDetailsTooltip)
                    .setResizable(true)
                    .setFlexGrow(1)
                    .setAutoWidth(true);

            final var editors = getContext().getQualifiedPublications().stream().map(this::createPublicationUpdateEditor).toList();
            this.grid.setItems(editors);

            this.grid.addItemDoubleClickListener(event -> openPublicationEditorFor(event.getItem()));

            return new Div(informationBox, this.grid);
        }

        private void createPublicationStatusIndicator(Span span, AbstractEntityEditor<Publication> editor) {
            final var status = editor.getEntityCreationStatus();
            final String theme;
            final String text;
            if (status.isError()) {
                theme = "badge error primary"; //$NON-NLS-1$
                text = span.getTranslation("views.publication.import.step3.status.duplicate"); //$NON-NLS-1$
            } else if (status.isWarning()) {
                theme = "badge success"; //$NON-NLS-1$
                text = span.getTranslation("views.publication.import.step3.status.warning"); //$NON-NLS-1$
            } else {
                theme = "badge success primary"; //$NON-NLS-1$
                text = span.getTranslation("views.publication.import.step3.status.importable"); //$NON-NLS-1$
            }
            span.getElement().setAttribute("theme", theme); //$NON-NLS-1$
            span.setText(text);
        }

        private String createPublicationStatusTooltip(AbstractEntityEditor<Publication> editor) {
            final var status = editor.getEntityCreationStatus();
            if (status.isError() || status.isWarning()) {
                return status.getErrorMessage();
            }
            return null;
        }

        private String createPublicationTitleLabel(AbstractEntityEditor<Publication> editor) {
            return editor.getEditedEntity().getTitle();
        }

        private String createPublicationTitleTooltip(AbstractEntityEditor<Publication> editor) {
            return editor.getEditedEntity().getTitle();
        }

        private String createPublicationAuthorLabel(AbstractEntityEditor<Publication> editor) {
            // TODO This list is computed for the cell label and the tooltip. Find a way to compute it only once
            return editor.getEditedEntity().getAuthors().stream().map(Person::getFullName).collect(Collectors.joining(", ")); //$NON-NLS-1$
        }

        private String createPublicationAuthorTooltip(AbstractEntityEditor<Publication> editor) {
            // TODO This list is computed for the cell label and the tooltip. Find a way to compute it only once
            return editor.getEditedEntity().getAuthors().stream().map(Person::getFullName).collect(Collectors.joining(", ")); //$NON-NLS-1$
        }

        private String createPublicationTypeLabel(AbstractEntityEditor<Publication> editor, Locale locale) {
            return editor.getEditedEntity().getType().getLabel(this.messages, locale);
        }

        private String createPublicationTypeTooltip(AbstractEntityEditor<Publication> editor, Locale locale) {
            return editor.getEditedEntity().getType().getLabel(this.messages, locale);
        }

        private String createPublicationCategoryLabel(AbstractEntityEditor<Publication> editor) {
            return editor.getEditedEntity().getType().getCategory(false).getAcronym();
        }

        private String createPublicationCategoryTooltip(AbstractEntityEditor<Publication> editor, Locale locale) {
            return editor.getEditedEntity().getType().getCategory(false).getLabel(this.messages, locale);
        }

        private String createPublicationDetailsLabel(AbstractEntityEditor<Publication> editor) {
            return editor.getEditedEntity().getWherePublishedShortDescription();
        }

        private String createPublicationDetailsTooltip(AbstractEntityEditor<Publication> editor) {
            return editor.getEditedEntity().getWherePublishedShortDescription();
        }

        private void openPublicationEditorFor(AbstractEntityEditor<Publication> entity) {
            ComponentFactory.openEditionModalDialog(ComponentFactory.getTranslation("views.publication.import.step3.column.edit"), //$NON-NLS-1$
                    entity, true, false,
                    (dialog, item) -> {
                        // Refresh item
                        this.grid.getListDataView().refreshItem(entity);
                        // Refresh buttons
                        updateButtonStateForNextStep();
                    },
                    null);
        }

        @Override
        public boolean isValid() {
            return this.grid.getListDataView().getItems().anyMatch(it -> !it.getEntityCreationStatus().isError());
        }

        @Override
        public boolean commit() {
            final var list = getContext().getImportablePublications();
            final var inputList = this.grid.getListDataView().getItems().filter(it -> !it.getEntityCreationStatus().isError()).map(it -> it.getEditedEntity()).toList();
            list.clear();
            list.addAll(inputList);
            return true;
        }

        private AbstractEntityEditor<Publication> createPublicationUpdateEditor(QualifiedPublication qualifiedPublication) {
            return this.editorFactory.createAdditionEditor(
                    qualifiedPublication.publication(),
                    qualifiedPublication.status(),
                    getSupportedPublicationTypeArray(),
                    true, false,
                    this.personCreationLabelKey,
                    this.personFieldLabelKey,
                    this.personFieldHelperLabelKey,
                    this.personNullErrorKey,
                    this.personDuplicateErrorKey,
                    getLogger());
        }

    }

    /**
     * Wizard step saving the publications from the importable publications in the previous wizard step.
     *
     * @author $Author: sgalland$
     * @author $Author: pschneiderlin$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PublicationSavingStep extends AbstractLabManagerProgressionWizardStep<ImportData> {

        private static final long serialVersionUID = -746055790359217141L;

        private final PublicationService publicationService;

        /**
         * Constructor.
         *
         * @param context            the wizard context.
         * @param publicationService the service for accessing the JPA entities of publications.
         * @param logger             the logger to be used in this step.
         */
        public PublicationSavingStep(ImportData context, PublicationService publicationService) {
            super(context, ComponentFactory.getTranslation("views.publication.import.step4.title"), 4, 1, false, true); //$NON-NLS-1$
            this.publicationService = publicationService;
        }

        private static String toPublicationString(Publication publication) {
            String buffer = "\"" + //$NON-NLS-1$
                    publication.getTitle() +
                    "\". " + //$NON-NLS-1$
                    publication.getWherePublishedShortDescription();
            return buffer;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.publication.import.step4.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            final var icon = LineAwesomeIcon.FILE_IMPORT_SOLID.create();
            icon.setSize(ViewConstants.ICON_SIZE_STR);
            final var style = icon.getStyle();
            style.setAlignSelf(AlignSelf.BASELINE);
            style.setMarginRight("var(--lumo-space-s)"); //$NON-NLS-1$
            return icon;
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.publication.import.step4.saving_publication"); //$NON-NLS-1$
            final var progress0 = progression == null ? new DefaultProgression() : progression;
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progress0, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.publication.import.step4.publications_saved"); //$NON-NLS-1$
            return () -> {
                final var publications = getContext().getImportablePublications();
                extendedProgression0.setProperties(0, 0, publications.size(), false);
                for (final var publication : publications) {
                    final var comment = toPublicationString(publication);
                    extendedProgression0.setComment(comment);
                    try {
                        final var savingContext = this.publicationService.startEditing(publication, getLogger());
                        savingContext.save();
                    } catch (Throwable ex) {
                        throw new RuntimeException(comment, ex);
                    } finally {
                        extendedProgression0.increment();
                    }
                }
                return terminationMessage0;
            };
        }

    }

}
