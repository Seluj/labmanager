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

package fr.utbm.ciad.labmanager.views.components.publications.editors;

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
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.EntityCreationStatus;
import fr.utbm.ciad.labmanager.views.components.conferences.editors.ConferenceEditorFactory;
import fr.utbm.ciad.labmanager.views.components.conferences.fields.ConferenceFieldFactory;
import fr.utbm.ciad.labmanager.views.components.journals.editors.JournalEditorFactory;
import fr.utbm.ciad.labmanager.views.components.journals.fields.JournalFieldFactory;
import fr.utbm.ciad.labmanager.views.components.persons.editors.PersonEditorFactory;
import fr.utbm.ciad.labmanager.views.components.persons.fields.PersonFieldFactory;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.AbstractPublicationEditor;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.EmbeddedPublicationEditor;
import fr.utbm.ciad.labmanager.views.components.publications.editors.regular.PublicationCreationStatusComputer;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.editors.ScientificAxisEditorFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Factory that is providing a publication editor according to the editing context.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class DefaultPublicationEditorFactory implements PublicationEditorFactory {

    private final DownloadableFileManager fileManager;

    private final PublicationService publicationService;

    private final PublicationCreationStatusComputer publicationCreationStatusComputer;

    private final PersonService personService;

    private final PersonEditorFactory personEditorFactory;

    private final PersonFieldFactory personFieldFactory;

    private final UserService userService;

    private final JournalService journalService;

    private final JournalEditorFactory journalEditorFactory;

    private final JournalFieldFactory journalFieldFactory;

    private final ConferenceService conferenceService;

    private final ConferenceEditorFactory conferenceEditorFactory;

    private final ConferenceFieldFactory conferenceFieldFactory;

    private final ScientificAxisService axisService;

    private final ScientificAxisEditorFactory axisEditorFactory;

    private final AuthenticatedUser authenticatedUser;

    private final MessageSourceAccessor messages;

    /**
     * Constructors.
     *
     * @param fileManager                       the manager of files at the server-side.
     * @param publicationService                the service for accessing the JPA entities for publications.
     * @param publicationCreationStatusComputer the tool for computer the creation status for the publication.
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
     * @param axisEditorFactory                 the factory for creating the scientific axis editors.
     * @param authenticatedUser                 the connected user.
     * @param messages                          the accessor to the localized messages (Spring layer).
     */
    public DefaultPublicationEditorFactory(
            @Autowired PublicationCreationStatusComputer publicationCreationStatusComputer,
            @Autowired PublicationService publicationService,
            @Autowired PersonService personService,
            @Autowired PersonEditorFactory personEditorFactory,
            @Autowired PersonFieldFactory personFieldFactory,
            @Autowired UserService userService,
            @Autowired JournalService journalService,
            @Autowired JournalEditorFactory journalEditorFactory,
            @Autowired JournalFieldFactory journalFieldFactory,
            @Autowired ConferenceService conferenceService,
            @Autowired ConferenceEditorFactory conferenceEditorFactory,
            @Autowired ConferenceFieldFactory conferenceFieldFactory,
            @Autowired ScientificAxisService axisService,
            @Autowired ScientificAxisEditorFactory axisEditorFactory,
            @Autowired DownloadableFileManager fileManager,
            @Autowired AuthenticatedUser authenticatedUser,
            @Autowired MessageSourceAccessor messages) {
        this.fileManager = fileManager;
        this.publicationService = publicationService;
        this.publicationCreationStatusComputer = publicationCreationStatusComputer;
        this.personService = personService;
        this.personEditorFactory = personEditorFactory;
        this.personFieldFactory = personFieldFactory;
        this.userService = userService;
        this.journalService = journalService;
        this.journalEditorFactory = journalEditorFactory;
        this.journalFieldFactory = journalFieldFactory;
        this.conferenceService = conferenceService;
        this.conferenceEditorFactory = conferenceEditorFactory;
        this.conferenceFieldFactory = conferenceFieldFactory;
        this.axisService = axisService;
        this.axisEditorFactory = axisEditorFactory;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    @Override
    public EntityEditingContext<Publication> createContextFor(Publication publication, Logger logger) {
        return this.publicationService.startEditing(publication, logger);
    }

    @Override
    public AbstractEntityEditor<Publication> createAdditionEditor(EntityEditingContext<Publication> context,
                                                                  EntityCreationStatus initialPublicationStatus,
                                                                  PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                  String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                  String personNullErrorKey, String personDuplicateErrorKey) {
        return new EmbeddedPublicationEditor(context, initialPublicationStatus, supportedTypes, enableTypeSelector, mandatoryAbstractText, this.publicationCreationStatusComputer, this.fileManager,
                this.publicationService, this.personService, this.personEditorFactory, this.personFieldFactory, this.userService,
                this.journalService, this.journalEditorFactory, this.journalFieldFactory, this.conferenceService,
                this.conferenceEditorFactory, this.conferenceFieldFactory,
                this.axisService, this.axisEditorFactory, this.authenticatedUser, this.messages,
                ConstructionPropertiesBuilder.create()
                        .map(AbstractPublicationEditor.PROP_PERSON_CREATION, personCreationLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_LABEL, personFieldLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_HELPER, personFieldHelperLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_NULL_ERROR, personNullErrorKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_DUPLICATE_ERROR, personDuplicateErrorKey));
    }

    @Override
    public AbstractEntityEditor<Publication> createUpdateEditor(EntityEditingContext<Publication> context,
                                                                PublicationType[] supportedTypes, boolean enableTypeSelector, boolean mandatoryAbstractText,
                                                                String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
                                                                String personNullErrorKey, String personDuplicateErrorKey) {
        return new EmbeddedPublicationEditor(context, supportedTypes, enableTypeSelector, mandatoryAbstractText, this.publicationCreationStatusComputer, this.fileManager,
                this.publicationService, this.personService, this.personEditorFactory, this.personFieldFactory, this.userService,
                this.journalService, this.journalEditorFactory, this.journalFieldFactory, this.conferenceService,
                this.conferenceEditorFactory, this.conferenceFieldFactory,
                this.axisService, this.axisEditorFactory, this.authenticatedUser, this.messages,
                ConstructionPropertiesBuilder.create()
                        .map(AbstractPublicationEditor.PROP_PERSON_CREATION, personCreationLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_LABEL, personFieldLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_HELPER, personFieldHelperLabelKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_NULL_ERROR, personNullErrorKey)
                        .map(AbstractPublicationEditor.PROP_PERSON_FIELD_DUPLICATE_ERROR, personDuplicateErrorKey));
    }

}
