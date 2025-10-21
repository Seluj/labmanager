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

package fr.utbm.ciad.labmanager.views.components.publications.editors.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Style.AlignSelf;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionProvider;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressExtension;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerProgressionWizardStep;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.Predicate;
import org.arakhne.afc.progress.Progression;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Wizard for generating al the thumbnail images for the publications.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "generatepublicationthumbnails", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class ThumbnailGeneratorWizard extends AbstractLabManagerWizard<ThumbnailGeneratorData> {

    private static final long serialVersionUID = -5080376607255795017L;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the publication entities.
     * @param loggerFactory      the factory for creating the loggers.
     */
    public ThumbnailGeneratorWizard(@Autowired PublicationService publicationService, @Autowired ContextualLoggerFactory loggerFactory) {
        this(publicationService, loggerFactory,
                defaultWizardConfiguration(null, false),
                new ThumbnailGeneratorData());
    }

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the publication entities.
     * @param properties         the properties of the wizard.
     * @param context            the data context.
     */
    protected ThumbnailGeneratorWizard(PublicationService publicationService, ContextualLoggerFactory loggerFactory,
                                       WizardConfigurationProperties properties, ThumbnailGeneratorData context) {
        this(properties, loggerFactory, context, Arrays.asList(
                new PublicationLoadingWizardStep(context, publicationService),
                new ThumbnailGenerationWizardStep(context, publicationService)));
    }

    private ThumbnailGeneratorWizard(WizardConfigurationProperties properties, ContextualLoggerFactory loggerFactory,
                                     ThumbnailGeneratorData context, List<WizardStep<ThumbnailGeneratorData>> steps) {
        super(properties, loggerFactory, context, steps);
    }

    /**
     * Wizard step for loading the publications from the database.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class PublicationLoadingWizardStep extends AbstractLabManagerProgressionWizardStep<ThumbnailGeneratorData> {

        private static final long serialVersionUID = 5041558072301836676L;

        private final PublicationService publicationService;

        /**
         * Constructor.
         *
         * @param context            the data to be shared between the wizard steps.
         * @param publicationService the service for accessing to the publications' JPA entities.
         */
        public PublicationLoadingWizardStep(ThumbnailGeneratorData context, PublicationService publicationService) {
            super(context, ComponentFactory.getTranslation("views.publications.thumbnailGenerator.step1.title"), 1, 1, true, false);//$NON-NLS-1$
            this.publicationService = publicationService;
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.publications.thumbnailGenerator.step1.comment"))); //$NON-NLS-1$
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var terminationMessage = getWizard().orElseThrow().getTranslation("views.publications.thumbnailGenerator.step1.publication_read"); //$NON-NLS-1$
            return () -> {
                final var identifiers = getContext().getEntityIdentifiers();
                progression.increment(5);
                final List<Publication> publications;
                if (identifiers == null || identifiers.isEmpty()) {
                    publications = this.publicationService.getAllPublications();
                } else {
                    publications = this.publicationService.getAllPublications((root, query, criteriaBuilder) -> {
                        Predicate pred = null;
                        for (final var id : identifiers) {
                            final var p = criteriaBuilder.equal(root.get("id"), id); //$NON-NLS-1$
                            if (pred == null) {
                                pred = p;
                            } else {
                                pred = criteriaBuilder.or(pred, p);
                            }
                        }
                        return pred;
                    });
                }
                progression.increment(90);
                getContext().setPublications(publications);
                return terminationMessage;
            };
        }

    }

    /**
     * Wizard step for generating the thumbnails.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class ThumbnailGenerationWizardStep extends AbstractLabManagerProgressionWizardStep<ThumbnailGeneratorData> {

        private static final long serialVersionUID = 2712494796472353513L;

        private final PublicationService publicationService;

        /**
         * Constructor.
         *
         * @param context            the data to be shared between the wizard steps.
         * @param publicationService the service for accessing to the publications' JPA entities.
         */
        public ThumbnailGenerationWizardStep(ThumbnailGeneratorData context, PublicationService publicationService) {
            super(context, ComponentFactory.getTranslation("views.publications.thumbnailGenerator.step2.title"), 2, 1, false, true);//$NON-NLS-1$
            this.publicationService = publicationService;
        }

        @Override
        public BiFunction<Component, String, String> getBackActionMessageSupplier() {
            return (cmp, tlt) -> ComponentFactory.getTranslation("views.publications.thumbnailGenerator.step2.back.message", tlt); //$NON-NLS-1$
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
            getMajorText().ifPresent(it -> it.setText(ComponentFactory.getTranslation("views.publications.thumbnailGenerator.step2.comment"))); //$NON-NLS-1$
        }

        @Override
        protected Component getProgressIcon(int taskNo) {
            final var image = VaadinIcon.COG.create();
            final var style = image.getStyle();
            style.setAlignSelf(AlignSelf.BASELINE);
            style.setMarginRight("var(--lumo-space-s)"); //$NON-NLS-1$
            return image;
        }

        @Override
        protected SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression) {
            final var pattern0 = getWizard().orElseThrow().getTranslation("views.publications.thumbnailGenerator.step2.generating"); //$NON-NLS-1$
            final var extendedProgression0 = ProgressExtension.withCommentFormatter(progression, it -> MessageFormat.format(pattern0, it));
            final var terminationMessage0 = getWizard().orElseThrow().getTranslation("views.publications.thumbnailGenerator.step2.end"); //$NON-NLS-1$
            final var locale = ComponentFactory.getLocale();
            return () -> {
                final var context = getContext();
                try {
                    this.publicationService.generateThumbnails(context.getPublications(), locale, getLogger(), extendedProgression0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return terminationMessage0;
            };
        }

    }

}
