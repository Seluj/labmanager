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

package fr.utbm.ciad.labmanager.configuration;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Definition of global constants. Some constants are obtaining from the Spring boot configuration file. That's why they
 * are not defined as global constants, by replied through static getter methods.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
public class ConfigurationConstants {

    /**
     * Default time out for the SSE requests.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final int SSE_TIMEOUT = 1200000;

    /**
     * Hundred.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final int HUNDRED = 100;

    /**
     * Anonymous user name.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ANONYMOUS = "anonymous"; //$NON-NLS-1$

    /**
     * Anonymous user name for developers.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEVELOPER = "developer"; //$NON-NLS-1$

    /**
     * Name of the endpoint for exporting to JSON.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_JSON_ENDPOINT = "exportJson"; //$NON-NLS-1$

    /**
     * Name of the endpoint for exporting to BibTeX.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_BIBTEX_ENDPOINT = "exportBibTeX"; //$NON-NLS-1$

    /**
     * Name of the endpoint for exporting to RIS.
     *
     * @since 3.7
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_RIS_ENDPOINT = "exportRIS"; //$NON-NLS-1$

    /**
     * Name of the endpoint for exporting to OpenDocument Text.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_ODT_ENDPOINT = "exportOdt"; //$NON-NLS-1$

    /**
     * Name of the endpoint for exporting to HTML document.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_HTML_ENDPOINT = "exportHtml"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "id".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ID_ENDPOINT_PARAMETER = "id"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "forAjax".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String FORAJAX_ENDPOINT_PARAMETER = "forAjax"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "includeSuborganization".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String INCLUDESUBORGANIZATION_ENDPOINT_PARAMETER = "includeSuborganizations"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "webId".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String WEBID_ENDPOINT_PARAMETER = "webId"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "publication".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PUBLICATION_ENDPOINT_PARAMETER = "publication"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "journal".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_ENDPOINT_PARAMETER = "journal"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "address".
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ADDRESS_ENDPOINT_PARAMETER = "address"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with DB content.
     * The name must be always lower case.
     *
     * @deprecated See {@code ZipDatabaseConstants}.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_DBCONTENT_ATTACHMENT_BASENAME = "dbcontent"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with DB content and attached files.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_DBCONTENT_FILES_ATTACHMENT_BASENAME = "allContent"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with publications.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_PUBLICATIONS_ATTACHMENT_BASENAME = "publications"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with members.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_MEMBERS_ATTACHMENT_BASENAME = "members"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with Carnot indicators.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_CARNOT_INDICATORS_ATTACHMENT_BASENAME = "carnotIndicators"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with UTBM indicators.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_UTBM_INDICATORS_ATTACHMENT_BASENAME = "utbmIndicators"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with UB indicators.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_UB_INDICATORS_ATTACHMENT_BASENAME = "ubIndicators"; //$NON-NLS-1$

    /**
     * Default basename of the files in attachment with SPIM indicators.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DEFAULT_SPIM_INDICATORS_ATTACHMENT_BASENAME = "spimIndicators"; //$NON-NLS-1$

    /**
     * Name of the endpoint for admin.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ADMIN_ENDPOINT = "admin"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of organizations.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_LIST_ENDPOINT = "organizationList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of organization addresses.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_ADDRESS_LIST_ENDPOINT = "addressList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing an organization.
     *
     * @see #ORGANIZATION_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_EDITING_ENDPOINT = "organizationEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving an organization.
     *
     * @see #ORGANIZATION_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_SAVING_ENDPOINT = "organizationSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting an organization.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_DELETING_ENDPOINT = "deleteOrganization"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing an organization address.
     *
     * @see #ORGANIZATION_ADDRESS_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_ADDRESS_EDITING_ENDPOINT = "addressEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving an organization address.
     *
     * @see #ORGANIZATION_ADDRESS_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_ADDRESS_SAVING_ENDPOINT = "addressSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting an organization address.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ORGANIZATION_ADDRESS_DELETING_ENDPOINT = "deleteAddress"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of persons.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_LIST_ENDPOINT = "personList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing a person.
     *
     * @see #PERSON_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_EDITING_ENDPOINT = "personEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a person.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_DELETING_ENDPOINT = "deletePerson"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a person.
     *
     * @see #PERSON_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_SAVING_ENDPOINT = "personSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of jury memberships.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JURY_MEMBERSHIP_EDITING_ENDPOINT = "juryMembershipEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving jury memberships.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JURY_MEMBERSHIP_SAVING_ENDPOINT = "juryMembershipSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a jury membership.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JURY_MEMBERSHIP_DELETION_ENDPOINT = "deleteJuryMembership"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of person invitations.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_INVITATION_EDITING_ENDPOINT = "personInvitationEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving person invitations.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_INVITATION_SAVING_ENDPOINT = "personInvitationSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a person invitation.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_INVITATION_DELETION_ENDPOINT = "deletePersonInvitation"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of person supervisions.
     *
     * @since 2.1
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SUPERVISION_EDITING_ENDPOINT = "supervisionEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving person supervision.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SUPERVISION_SAVING_ENDPOINT = "supervisionSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a person supervision.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SUPERVISION_DELETION_ENDPOINT = "deleteSupervision"; //$NON-NLS-1$

    /**
     * Name of an endpoint parameter for representing a person.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSON_ENDPOINT_PARAMETER = "person"; //$NON-NLS-1$

    /**
     * Name of an endpoint parameter for representing a goto.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GOTO_ENDPOINT_PARAMETER = "goto"; //$NON-NLS-1$

    /**
     * Name of the name of an endpoint parameter for representing a personId.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PERSONID_ENDPOINT_PARAMETER = "personId"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of journals.
     *
     * @see #JOURNAL_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_LIST_ENDPOINT = "journalList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing a journal.
     *
     * @see #JOURNAL_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_EDITING_ENDPOINT = "journalEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a journal.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_DELETING_ENDPOINT = "deleteJournal"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a journal.
     *
     * @see #JOURNAL_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_SAVING_ENDPOINT = "journalSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of conferences.
     *
     * @see #CONFERENCE_SAVING_ENDPOINT
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CONFERENCE_LIST_ENDPOINT = "conferenceList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing a conference.
     *
     * @see #CONFRENCE_SAVING_ENDPOINT
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CONFERENCE_EDITING_ENDPOINT = "conferenceEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a conference.
     *
     * @see #CONFERENCE_EDITING_ENDPOINT
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CONFERENCE_SAVING_ENDPOINT = "conferenceSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a conference.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CONFERENCE_DELETING_ENDPOINT = "deleteConference"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of publications.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PUBLICATION_LIST_ENDPOINT = "publicationList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing a publication.
     *
     * @see #PUBLICATION_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PUBLICATION_EDITING_ENDPOINT = "publicationEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a publication.
     *
     * @see #PUBLICATION_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PUBLICATION_SAVING_ENDPOINT = "publicationSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a publication.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PUBLICATION_DELETING_ENDPOINT = "deletePublication"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing global indicators.
     *
     * @see #GLOBAL_INDICATORS_SAVING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GLOBAL_INDICATORS_EDITING_ENDPOINT = "globalIndicatorsEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving global indicators.
     *
     * @see #GLOBAL_INDICATORS_EDITING_ENDPOINT
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GLOBAL_INDICATORS_SAVING_ENDPOINT = "globalIndicatorsSave"; //$NON-NLS-1$

    /**
     * Name of the endpoint for reseting the indicator's cache.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String RESET_INDICATOR_CACHE_ENDPOINT = "resetIndicatorCache"; //$NON-NLS-1$

    /**
     * Name of the endpoint for regenerating the thumbnail as a batch task.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String REGENERATE_THUMBNAIL_ASYNC_ENDPOINT = "regenerateThumbnailAsync"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the duplicate names of persons.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String COMPUTE_PERSON_DUPLICATE_NAMES_ENDPOINT = "computePersonDuplicateNames"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the duplicate of organizations.
     *
     * @since 3.2
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String COMPUTE_DUPLICATE_ORGANIZATIONS_ENDPOINT = "computeDuplicateOrganizations"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving the database into a server-side Zip file.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_DATABASE_TO_SERVER_ZIP_BATCH_ENDPOINT = "saveDatabaseToServerZipBatch"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the updates of the persons' indicators.
     *
     * @since 3.5
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GET_JSON_FOR_PERSON_INDICATOR_UPDATES_ENDPOINT = "getJsonForPersonIndicatorUpdates"; //$NON-NLS-1$

    /**
     * Name of the endpoint for selecting a BibTeX file to import.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String IMPORT_BIBTEX_VIEW_ENDPOINT = "showBibTeXImporter"; //$NON-NLS-1$

    /**
     * Name of the endpoint for selecting a RIS file to import.
     *
     * @since 3.8
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String IMPORT_RIS_VIEW_ENDPOINT = "showRISImporter"; //$NON-NLS-1$

    /**
     * Name of the endpoint for reading a BibTeX file and providing a JSON description.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GET_JSON_FROM_BIBTEX_ENDPOINT = "getJsonFromBibTeX"; //$NON-NLS-1$

    /**
     * Name of the endpoint for reading a RIS file and providing a JSON description.
     *
     * @since 3.8
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GET_JSON_FROM_RIS_ENDPOINT = "getJsonFromRIS"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter for checking that an entity is already in the database and
     * marking accordingly the entity in the output.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CHECKINDB_ENDPOINT_PARAMETER = "checkInDb"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a BibTeX file and providing a JSON description.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_BIBTEX_ENDPOINT = "saveBibTeX"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a RIS file and providing a JSON description.
     *
     * @since 3.8
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_RIS_ENDPOINT = "saveRIS"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a membership.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String MEMBERSHIP_DELETION_ENDPOINT = "deleteMembership"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a membership.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String MEMBERSHIP_SAVING_ENDPOINT = "saveMembership"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editing a membership.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String MEMBERSHIP_EDITING_ENDPOINT = "membershipEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for obtaining the JSON of the members of an organization.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String EXPORT_MEMBERS_TO_JSON_ENDPOINT = "exportMembersToJson"; //$NON-NLS-1$

    /**
     * Name of the endpoint for merging the database and a given BibTeX for obtaining a new JSON file.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GET_JSON_FROM_DATABASE_AND_BIBTEX_ENDPOINT = "getJsonFromDatabaseAndBibTeX"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a journal ranking.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_JOURNAL_RANKING_ENDPOINT = "saveJournalRanking"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a journal ranking.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DELETE_JOURNAL_RANKING_ENDPOINT = "deleteJournalRanking"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the journal indicator updates.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String JOURNAL_INDICATOR_UPDATES_ENDPOINT = "getJournalUpdateJson"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving the journal indicator updates.
     *
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_JOURNAL_INDICATOR_UPDATES_ENDPOINT = "saveJournalIndicatorUpdates"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving a conference ranking.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_CONFERENCE_RANKING_ENDPOINT = "saveConferenceRanking"; //$NON-NLS-1$

    /**
     * Name of the endpoint's parameter for a conference.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String CONFERENCE_ENDPOINT_PARAMETER = "conference"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting a conference ranking.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String DELETE_CONFERENCE_RANKING_ENDPOINT = "deleteConferenceRanking"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the updates of the conference' indicators.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String GET_JSON_FOR_CONFERENCE_INDICATOR_UPDATES_ENDPOINT = "getJsonForConferenceIndicatorUpdates"; //$NON-NLS-1$

    /**
     * Name of the {@code year} parameter for endpoints.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String YEAR_ENDPOINT_PARAMETER = "year"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving the conference indicator updates.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_CONFERENCE_INDICATOR_UPDATES_ENDPOINT = "saveConferenceIndicatorUpdates"; //$NON-NLS-1$

    /**
     * Name of the endpoint for computing the orphan entities.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String COMPUTE_ORPHAN_ENTITIES_ENDPOINT = "computeOrphanEntities"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving the person indicator updates.
     *
     * @since 3.3
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SAVE_PERSON_INDICATOR_UPDATES_ENDPOINT = "savePersonIndicatorUpdates"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of projects.
     *
     * @since 3.0
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PROJECT_LIST_ENDPOINT = "projectList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of projects.
     *
     * @since 3.0
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PROJECT_EDITING_ENDPOINT = "projectEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving of projects.
     *
     * @since 3.0
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PROJECT_SAVING_ENDPOINT = "saveProject"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting of projects.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PROJECT_DELETING_ENDPOINT = "deleteProject"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "project".
     *
     * @since 3.0
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String PROJECT_ENDPOINT_PARAMETER = "project"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of associated structures.
     *
     * @since 3.2
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ASSOCIATED_STRUCTURE_LIST_ENDPOINT = "assostructList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of associated structures.
     *
     * @since 3.2
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ASSOCIATED_STRUCTURE_EDITING_ENDPOINT = "assostructEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving of associated structures.
     *
     * @since 3.2
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ASSOCIATED_STRUCTURE_SAVING_ENDPOINT = "saveAssoStruct"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting of associated structures.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ASSOCIATED_STRUCTURE_DELETING_ENDPOINT = "deleteAssoStruct"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "structure".
     *
     * @since 3.2
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String STRUCTURE_ENDPOINT_PARAMETER = "structure"; //$NON-NLS-1$

    /**
     * Name of the endpoint for list of teaching activities.
     *
     * @since 3.4
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String TEACHING_ACTIVITY_LIST_ENDPOINT = "teachingActivityList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of teaching activities.
     *
     * @since 3.4
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String TEACHING_ACTIVITY_EDITING_ENDPOINT = "teachingActivityEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting teaching activities.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String TEACHING_ACTIVITY_DELETING_ENDPOINT = "deleteTeachingActivity"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving of teaching activity.
     *
     * @since 3.4
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String TEACHING_ACTIVITY_SAVING_ENDPOINT = "saveTeachingActivity"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "activity".
     *
     * @since 3.4
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String ACTIVITY_ENDPOINT_PARAMETER = "activity"; //$NON-NLS-1$

    /**
     * Name of the endpoint for the list of scientific axis.
     *
     * @since 3.5
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SCIENTIFIC_AXIS_LIST_ENDPOINT = "scientificAxisList"; //$NON-NLS-1$

    /**
     * Name of the endpoint for editor of scientific axis.
     *
     * @since 3.5
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SCIENTIFIC_AXIS_EDITING_ENDPOINT = "scientificAxisEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for deleting scientific axis.
     *
     * @since 3.6
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SCIENTIFIC_AXIS_DELETING_ENDPOINT = "scientificAxisEditor"; //$NON-NLS-1$

    /**
     * Name of the endpoint for saving of scientific axis.
     *
     * @since 3.5
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String SCIENTIFIC_AXIS_SAVING_ENDPOINT = "saveScientificAxis"; //$NON-NLS-1$

    /**
     * Name of the endpoint parameter "axis".
     *
     * @since 3.5
     * @deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public static final String AXIS_ENDPOINT_PARAMETER = "axis"; //$NON-NLS-1$

    private static final String DEFAULT_SERVER_NAME = "LabManager"; //$NON-NLS-1$

    private static final String DEFAULT_ORGANIZATION = "ResearchOrganization"; //$NON-NLS-1$

    private static final String DEFAULT_SUPER_ORGANIZATION = "ResearchOrganization"; //$NON-NLS-1$

    private static final String DEFAULT_LEAR_ORGANIZATION = "ResearchOrganization"; //$NON-NLS-1$

    @Value("${server.servlet.context-path}")
    private String serverName;

    @Value("${labmanager.default-organization}")
    private String defaultOganization;

    @Value("${labmanager.default-super-organization}")
    private String defaultSuperOganization;

    @Value("${labmanager.default-lear-organization}")
    private String defaultLearOganization;

    /**
     * Replies the name of the Spring boot server that is used for accessing the end-points and online resources.
     *
     * @return the name of the Spring Boot application
     */
    public String getServerName() {
        if (Strings.isNullOrEmpty(this.serverName)) {
            this.serverName = DEFAULT_SERVER_NAME;
        } else {
            while (this.serverName.startsWith("/")) { //$NON-NLS-1$
                this.serverName = this.serverName.substring(1);
            }
        }
        return this.serverName;
    }

    /**
     * Replies the name or acronym of the default organization to consider.
     *
     * @return the name or acronym of the default organization
     */
    public String getDefaultOrganization() {
        if (Strings.isNullOrEmpty(this.defaultOganization)) {
            this.defaultOganization = DEFAULT_ORGANIZATION;
        }
        return this.defaultOganization;
    }

    /**
     * Replies the name or acronym of the default super organization to consider.
     *
     * @return the name or acronym of the default super organization.
     * @since 3.0
     */
    public String getDefaultSuperOrganization() {
        if (Strings.isNullOrEmpty(this.defaultSuperOganization)) {
            this.defaultSuperOganization = DEFAULT_SUPER_ORGANIZATION;
        }
        return this.defaultSuperOganization;
    }

    /**
     * Replies the name or acronym of the default LEAR organization to consider.
     *
     * @return the name or acronym of the default LEAR organization.
     * @since 3.0
     */
    public String getDefaultLearOrganization() {
        if (Strings.isNullOrEmpty(this.defaultLearOganization)) {
            this.defaultLearOganization = DEFAULT_LEAR_ORGANIZATION;
        }
        return this.defaultLearOganization;
    }

}
