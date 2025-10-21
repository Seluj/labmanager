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

package fr.utbm.ciad.labmanager.views.components.projects.views;

import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.views.appviews.publications.PublicationImportWizard;
import fr.utbm.ciad.labmanager.views.components.addons.logger.ContextualLoggerFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.PublicationEditorFactory;
import fr.utbm.ciad.labmanager.views.components.publications.views.AbstractPublicationListView;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * List all the patents and registrations of software.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardPatentListView extends AbstractPublicationListView {

    private static final long serialVersionUID = -4459352094796317008L;

    private static final PublicationType[] SUPPORTED_PUBLICATION_TYPES = {
            PublicationType.EUROPEAN_PATENT,
            PublicationType.INTERNATIONAL_PATENT,
            PublicationType.NATIONAL_PATENT
    };

    /**
     * Constructor.
     *
     * @param authenticatedUser        the connected user.
     * @param messages                 the accessor to the localized messages (spring layer).
     * @param loggerFactory            the factory to be used for the composite logger.
     * @param publicationService       the service for accessing the publications.
     * @param publicationEditorFactory the factory for creating publication editors.
     * @param journalService           the service for accessing the JPA entities for journal.
     * @param organizationService      the service for accessing the JPA entities for research organization.
     */
    public StandardPatentListView(
            AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, ContextualLoggerFactory loggerFactory, PublicationService publicationService,
            PublicationEditorFactory publicationEditorFactory, JournalService journalService, ResearchOrganizationService organizationService) {
        super(authenticatedUser, messages, loggerFactory, publicationService, publicationEditorFactory, journalService, organizationService,
                PublicationImportWizard.class,
                ConstructionPropertiesBuilder.create()
                        .map(PROP_DELETION_TITLE_MESSAGE, "views.patents.delete.title") //$NON-NLS-1$
                        .map(PROP_DELETION_MESSAGE, "views.patents.delete.message") //$NON-NLS-1$
                        .map(PROP_DELETION_SUCCESS_MESSAGE, "views.patents.delete.success_message") //$NON-NLS-1$
                        .map(PROP_DELETION_ERROR_MESSAGE, "views.patents.delete.error_message") //$NON-NLS-1$
                        .map(PROP_AUTHORS_COLUMN_NAME, "views.authors") //$NON-NLS-1$
                        .map(PROP_PERSON_CREATION_LABEL, "views.publication.new_author") //$NON-NLS-1$
                        .map(PROP_PERSON_FIELD_LABEL, "views.publication.authors") //$NON-NLS-1$
                        .map(PROP_PERSON_FIELD_HELPER, "views.publication.authors.helper") //$NON-NLS-1$
                        .map(PROP_PERSON_FIELD_NULL_ERROR, "views.publication.authors.error.null") //$NON-NLS-1$
                        .map(PROP_PERSON_FIELD_DUPLICATE_ERROR, "views.publication.authors.error.duplicate")); //$NON-NLS-1$
        setDataProvider((service, pageRequest, filters) -> {
            return publicationService.getAllPublications(pageRequest, createJpaFilters(filters),
                    this::initializeEntityFromJPA);
        });
        postInitializeFilters();
        initializeDataInGrid(getGrid(), getFilters());
    }

    @Override
    protected Stream<PublicationType> getSupportedPublicationTypes() {
        return Arrays.asList(SUPPORTED_PUBLICATION_TYPES).stream();
    }

}
