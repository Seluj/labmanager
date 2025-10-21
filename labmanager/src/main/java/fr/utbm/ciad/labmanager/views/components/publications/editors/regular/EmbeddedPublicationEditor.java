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

package fr.utbm.ciad.labmanager.views.components.publications.editors.regular;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.builders.ConstructionPropertiesBuilder;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
import fr.utbm.ciad.labmanager.views.components.conferences.fields.ConferenceFieldFactory;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import fr.utbm.ciad.labmanager.views.components.journals.fields.JournalFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Editor of publication information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited publication.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class EmbeddedPublicationEditor extends AbstractPublicationEditor {

    private static final long serialVersionUID = 7233675942546351403L;

    /**
     * Constructor.
     *
     * @param context                           the context for editing the entity.
     * @param initialPublicationStatus          the initial status of the publication.
     * @param supportedTypes                    list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector                indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText             indicates if the abstract text is considered as mandatory or not.
     * @param publicationCreationStatusComputer the tool for computer the creation status for the publication.
     * @param fileManager                       the manager of files at the server-side.
     * @param publicationService                the service for accessing the JPA entities for publications.
     * @param personService                     the service for accessing the JPA entities for persons.
     * @param personEditorFactory               the factory for creating the person editors.
     * @param personFieldFactory                the factory for creating the person fields.
     * @param userService                       the service for accessing the JPA entities for users.
     * @param journalService                    the service for accessing the JPA entities for journal.
     * @param journalEditorFactory              the factory for creating journal editors.
     * @param journalFieldFactory               the factory for creating journal fields.
     * @param conferenceService                 the service for accessing the JPA entities for conference.
     * @param conferenceEditorFactory           the factory for creating the conference editors.
     * @param conferenceFieldFactory            the factory for creating the conference fields.
     * @param axisService                       service for accessing to the JPA entities of scientific axes.
     * @param axisEditorFactory                 the factory for creating scientific axis editors.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     * @param properties                        specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public EmbeddedPublicationEditor(EntityEditingContext<Publication> context,
                                     EntityCreationStatus initialPublicationStatus,
                                     PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                     EntityCreationStatusComputer<Publication> publicationCreationStatusComputer,
                                     DownloadableFileManager fileManager, PublicationService publicationService,
                                     PersonService personService, PersonEditorFactory personEditorFactory, PersonFieldFactory personFieldFactory, UserService userService,
                                     JournalService journalService, JournalEditorFactory journalEditorFactory, JournalFieldFactory journalFieldFactory,
                                     ConferenceService conferenceService, ConferenceEditorFactory conferenceEditorFactory, ConferenceFieldFactory conferenceFieldFactory,
                                     ScientificAxisService axisService, ScientificAxisEditorFactory axisEditorFactory, AuthenticatedUser authenticatedUser,
                                     MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        super(context, initialPublicationStatus, supportedTypes, false, enableTypeSelector, mandatoryAbstractText, publicationCreationStatusComputer,
                fileManager, publicationService, personService, personEditorFactory, personFieldFactory, userService,
                journalService, journalEditorFactory, journalFieldFactory, conferenceService, conferenceEditorFactory,
                conferenceFieldFactory, axisService, axisEditorFactory, authenticatedUser,
                messages, properties);
        createEditorContentAndLinkBeans();
    }

    /**
     * Constructor.
     *
     * @param context                           the context for editing the entity.
     * @param supportedTypes                    list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
     * @param enableTypeSelector                indicates if the type selector is enabled or disabled.
     * @param mandatoryAbstractText             indicates if the abstract text is considered as mandatory or not.
     * @param publicationCreationStatusComputer the tool for computer the creation status for the publication.
     * @param fileManager                       the manager of files at the server-side.
     * @param publicationService                the service for accessing the JPA entities for publications.
     * @param personService                     the service for accessing the JPA entities for persons.
     * @param personEditorFactory               the factory for creating the person editors.
     * @param personFieldFactory                the factory for creating the person fields.
     * @param userService                       the service for accessing the JPA entities for users.
     * @param journalService                    the service for accessing the JPA entities for journal.
     * @param journalEditorFactory              the factory for creating journal editors.
     * @param journalFieldFactory               the factory for creating journal fields.
     * @param conferenceService                 the service for accessing the JPA entities for conference.
     * @param conferenceEditorFactory           the factory for creating the conference editors.
     * @param conferenceFieldFactory            the factory for creating the conference fields.
     * @param axisService                       service for accessing to the JPA entities of scientific axes.
     * @param axisEditorFactory                 the factory for creating scientific axis editors.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     * @param properties                        specification of properties that may be passed to the construction function {@code #create*}.
     * @since 4.0
     */
    public EmbeddedPublicationEditor(EntityEditingContext<Publication> context,
                                     PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                     EntityCreationStatusComputer<Publication> publicationCreationStatusComputer,
                                     DownloadableFileManager fileManager, PublicationService publicationService,
                                     PersonService personService, PersonEditorFactory personEditorFactory, PersonFieldFactory personFieldFactory, UserService userService,
                                     JournalService journalService, JournalEditorFactory journalEditorFactory, JournalFieldFactory journalFieldFactory,
                                     ConferenceService conferenceService, ConferenceEditorFactory conferenceEditorFactory, ConferenceFieldFactory conferenceFieldFactory,
                                     ScientificAxisService axisService, ScientificAxisEditorFactory axisEditorFactory, AuthenticatedUser authenticatedUser,
                                     MessageSourceAccessor messages, ConstructionPropertiesBuilder properties) {
        this(context, null, supportedTypes, enableTypeSelector, mandatoryAbstractText, publicationCreationStatusComputer,
                fileManager, publicationService, personService, personEditorFactory, personFieldFactory, userService,
                journalService, journalEditorFactory, journalFieldFactory, conferenceService, conferenceEditorFactory,
                conferenceFieldFactory, axisService, axisEditorFactory, authenticatedUser,
                messages, properties);
    }

}
