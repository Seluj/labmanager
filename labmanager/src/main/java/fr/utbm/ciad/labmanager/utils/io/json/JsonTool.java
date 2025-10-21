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

package fr.utbm.ciad.labmanager.utils.io.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Definition of constants for the JSON tools.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class JsonTool implements Serializable {

    /**
     * Prefix for hidden JSON fields.
     */
    public static final String HIDDEN_FIELD_PREFIX = "_"; //$NON-NLS-1$
    /**
     * Prefix for special JSON fields.
     */
    public static final String SPECIAL_FIELD_PREFIX = "@"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to global indicators.
     */
    public static final String GLOBALINDICATORS_SECTION = "globalIndicators"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to organization addresses.
     */
    public static final String ORGANIZATIONADDRESSES_SECTION = "organizationAddresses"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to research organizations.
     */
    public static final String RESEARCHORGANIZATIONS_SECTION = "researchOrganizations"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to persons.
     */
    public static final String PERSONS_SECTION = "persons"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to application users.
     *
     * @since 4.0
     */
    public static final String APPLICATION_USERS_SECTION = "applicationUsers"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to associated structures.
     *
     * @since 3.2
     */
    public static final String ASSOCIATED_STRUCTURES_SECTION = "associatedStructures"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to memberships.
     */
    public static final String ORGANIZATION_MEMBERSHIPS_SECTION = "memberships"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to journals.
     */
    public static final String JOURNALS_SECTION = "journals"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to conferences.
     *
     * @since 3.6
     */
    public static final String CONFERENCES_SECTION = "conferences"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to publications.
     */
    public static final String PUBLICATIONS_SECTION = "publications"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to jury memberships.
     */
    public static final String JURY_MEMBERSHIPS_SECTION = "juryMemberships"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to supervisions.
     */
    public static final String SUPERVISIONS_SECTION = "supervisions"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to invitations.
     */
    public static final String INVITATIONS_SECTION = "invitations"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to projects.
     *
     * @since 3.0
     */
    public static final String PROJECTS_SECTION = "projects"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to teaching activities.
     *
     * @since 3.4
     */
    public static final String TEACHING_ACTIVITY_SECTION = "teachingActivities"; //$NON-NLS-1$
    /**
     * Main section of the JSON that is dedicated to scientific axes.
     *
     * @since 3.5
     */
    public static final String SCIENTIFIC_AXIS_SECTION = "scientificAxes"; //$NON-NLS-1$
    /**
     * Name of the field that contains the source of the data. Value of this field may be
     * {@code :db} or the name of the source.
     */
    public static final String HIDDEN_INTERNAL_DATA_SOURCE_KEY = HIDDEN_FIELD_PREFIX + "internalDataSource"; //$NON-NLS-1$
    /**
     * Value of the field {@link #HIDDEN_INTERNAL_DATA_SOURCE_KEY} that represents the database source.
     */
    public static final String HIDDEN_INTERNAL_DATABASE_SOURCE_VALUE = ":db"; //$NON-NLS-1$
    /**
     * Value of the field {@link #HIDDEN_INTERNAL_DATA_SOURCE_KEY} that represents an external source.
     */
    public static final String HIDDEN_INTERNAL_EXTERNAL_SOURCE_VALUE = ":ext"; //$NON-NLS-1$
    /**
     * Name of the field that contains the identifier of the data in the database.
     */
    public static final String HIDDEN_INTERNAL_DATA_SOURCE_ID_KEY = HIDDEN_FIELD_PREFIX + "internalDataSourceId"; //$NON-NLS-1$
    /**
     * Name of the field that indicates if an entity could be imported in the database.
     * The reason why an entity cannot be imported is not explicitly explained in this field.
     */
    public static final String HIDDEN_INTERNAL_IMPORTABLE_KEY = HIDDEN_FIELD_PREFIX + "internalImportable"; //$NON-NLS-1$
    /**
     * Name of the field that indicates if a journal must be created for importing a publication in the database.
     */
    public static final String HIDDEN_INTERNAL_NEW_JOURNAL_KEY = HIDDEN_FIELD_PREFIX + "internalNewJournal"; //$NON-NLS-1$
    /**
     * Name of the field that indicates if a conference must be created for importing a publication in the database.
     */
    public static final String HIDDEN_INTERNAL_NEW_CONFERENCE_KEY = HIDDEN_FIELD_PREFIX + "internalNewConference"; //$NON-NLS-1$
    /**
     * Name of the field for the visible global indicators.
     */
    public static final String VISIBLEGLOBALINDICATORS_KEY = "visibleGlobalIndicators"; //$NON-NLS-1$
    /**
     * Name of the field for the year. This field is usually for {@link Publication}.
     *
     * @see #YEAR_KEY
     * @see #REFERENCEYEAR_KEY
     */
    public static final String PUBLICATIONYEAR_KEY = "publicationYear"; //$NON-NLS-1$
    /**
     * Name of the field for the title. This field is usually for {@link Publication}.
     */
    public static final String TITLE_KEY = "title"; //$NON-NLS-1$
    /**
     * Name of the field for the DOI. This field is usually for {@link Publication}.
     */
    public static final String DOI_KEY = "doi"; //$NON-NLS-1$
    /**
     * Name of the field for the ISSN. This field is usually for {@link Publication}.
     */
    public static final String ISSN_KEY = "issn"; //$NON-NLS-1$
    /**
     * Name of the field for the Edition. This field is usually for {@link Publication}.
     */
    public static final String EDITION_KEY = "edition"; //$NON-NLS-1$
    /**
     * Name of the field for the publisher. This field is usually for {@link Publication}.
     */
    public static final String PUBLISHER_KEY = "publisher"; //$NON-NLS-1$
    /**
     * Name of the field for the book title. This field is usually for {@link Publication}.
     */
    public static final String BOOKTITLE_KEY = "bookTitle"; //$NON-NLS-1$
    /**
     * Name of the field for the event name. This field is usually for {@link Publication}.
     */
    public static final String SCIENTIFICEVENTNAME_KEY = "scientificEventName"; //$NON-NLS-1$
    /**
     * Name of the field for the "how published". This field is usually for {@link Publication}.
     */
    public static final String HOWPUBLISHED_KEY = "howPublished"; //$NON-NLS-1$
    /**
     * Name of the field for the institution. This field is usually for {@link Publication}.
     */
    public static final String INSTITUTION_KEY = "institution"; //$NON-NLS-1$
    /**
     * Name of the field for the journal of a journal paper.
     */
    public static final String JOURNAL_KEY = "journal"; //$NON-NLS-1$
    /**
     * Name of the field for the journal of a conference paper.
     *
     * @since 3.6
     */
    public static final String CONFERENCE_KEY = "conference"; //$NON-NLS-1$
    /**
     * Name of the field for indicating of a published was manually validated. This field is usually for {@link Publication}.
     */
    public static final String MANUALVALIDATIONFORCED_KEY = "manualValidationForced"; //$NON-NLS-1$
    /**
     * Prefix for organization address identifiers.
     */
    protected static final String ORGANIZATIONADDRESS_ID_PREFIX = "/adr#"; //$NON-NLS-1$
    /**
     * Prefix for research organization identifiers.
     */
    protected static final String RESEARCHORGANIZATION_ID_PREFIX = "/ro#"; //$NON-NLS-1$
    /**
     * Prefix for membership identifiers.
     */
    protected static final String MEMBERSHIP_ID_PREFIX = "/mbr#"; //$NON-NLS-1$
    /**
     * Prefix for jury membership identifiers.
     */
    protected static final String JURY_MEMBERSHIP_ID_PREFIX = "/jurymbr#"; //$NON-NLS-1$
    /**
     * Prefix for supervision identifiers.
     */
    protected static final String SUPERVISION_ID_PREFIX = "/supervis#"; //$NON-NLS-1$
    /**
     * Prefix for invitation identifiers.
     */
    protected static final String INVITATION_ID_PREFIX = "/invit#"; //$NON-NLS-1$
    /**
     * Prefix for project identifiers.
     */
    protected static final String PROJECT_ID_PREFIX = "/prj#"; //$NON-NLS-1$
    /**
     * Prefix for associated structure identifiers.
     *
     * @since 3.2
     */
    protected static final String ASSOCIATED_STRUCTURE_ID_PREFIX = "/astruct#"; //$NON-NLS-1$
    /**
     * Prefix for project identifiers.
     *
     * @since 3.4
     */
    protected static final String TEACHING_ACTIVITY_ID_PREFIX = "/teach#"; //$NON-NLS-1$
    /**
     * Prefix for scientific axis identifiers.
     *
     * @since 3.5
     */
    protected static final String SCIENTFIC_AXIS_ID_PREFIX = "/sciaxis#"; //$NON-NLS-1$
    /**
     * Prefix for person identifiers.
     */
    protected static final String PERSON_ID_PREFIX = "/pers#"; //$NON-NLS-1$
    /**
     * Prefix for person identifiers.
     *
     * @since 4.0
     */
    protected static final String APPLICATION_USER_ID_PREFIX = "/auser#"; //$NON-NLS-1$
    /**
     * Prefix for journal identifiers.
     */
    protected static final String JOURNAL_ID_PREFIX = "/jour#"; //$NON-NLS-1$
    /**
     * Prefix for journal identifiers.
     *
     * @since 3.6
     */
    protected static final String CONFERENCE_ID_PREFIX = "/conf#"; //$NON-NLS-1$
    /**
     * Prefix for publication identifiers.
     */
    protected static final String PUBLICATION_ID_PREFIX = "/publi#"; //$NON-NLS-1$
    /**
     * The name of the field that is an JSON identifier.
     */
    protected static final String ID_FIELDNAME = SPECIAL_FIELD_PREFIX + "id"; //$NON-NLS-1$
    /**
     * The name of the field that is the date of the last modification of the file.
     */
    protected static final String DATABASE_ID_FIELDNAME = HIDDEN_FIELD_PREFIX + "database-id"; //$NON-NLS-1$
    /**
     * The name of the field that is the date of the last modification of the file.
     */
    protected static final String LAST_CHANGE_FIELDNAME = HIDDEN_FIELD_PREFIX + "last-update-date"; //$NON-NLS-1$
    /**
     * Prefix for for the setter functions.
     */
    protected static final String SETTER_FUNCTION_PREFIX = "set"; //$NON-NLS-1$
    /**
     * Prefix for for the getter functions.
     */
    protected static final String GETTER_FUNCTION_PREFIX = "get"; //$NON-NLS-1$
    /**
     * Prefix for for the "is" (boolean) getter functions.
     */
    protected static final String IS_GETTER_FUNCTION_PREFIX = "is"; //$NON-NLS-1$
    /**
     * Name of the field for the addresses reference.
     */
    protected static final String ADDRESSES_KEY = "addresses"; //$NON-NLS-1$
    /**
     * Name of the field for the address reference.
     */
    protected static final String ADDRESS_KEY = "address"; //$NON-NLS-1$
    /**
     * Name of the field for the person reference.
     */
    protected static final String PERSON_KEY = "person"; //$NON-NLS-1$
    /**
     * Name of the field for the person reference.
     *
     * @since 3.4
     */
    protected static final String UNIVERSITY_KEY = "university"; //$NON-NLS-1$
    /**
     * Name of the field for the guest person reference.
     */
    protected static final String GUEST_KEY = "guest"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a budget.
     *
     * @since 3.2
     */
    protected static final String BUDGET_KEY = "budget"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to budgets.
     *
     * @since 3.2
     */
    protected static final String BUDGETS_KEY = "budgets"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a grant.
     *
     * @since 3.2
     */
    protected static final String GRANT_KEY = "grant"; //$NON-NLS-1$
    /**
     * Old name of the field for the reference to a person email.
     *
     * @since 4.0
     */
    protected static final String OLD_FIELD_EMAIL_KEY = "email"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to an office phone number.
     *
     * @since 4.0
     */
    protected static final String OFFICE_PHONE_NUMBER_KEY = "officePhone"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to an mobile phone number.
     *
     * @since 4.0
     */
    protected static final String MOBILE_PHONE_NUMBER_KEY = "mobilePhone"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to country code.
     *
     * @since 4.0
     */
    protected static final String COUNTRY_KEY = "country"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to number.
     *
     * @since 4.0
     */
    protected static final String NUMBER_KEY = "number"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a coordinator organization.
     *
     * @since 3.0
     */
    protected static final String COORDINATOR_KEY = "coordinator"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a local organization.
     *
     * @since 3.0
     */
    protected static final String LOCAL_ORGANIZATION_KEY = "localOrganization"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a super organization.
     *
     * @since 3.0
     */
    protected static final String SUPER_ORGANIZATION_KEY = "superOrganization"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the super organizations.
     *
     * @since 4.0
     */
    protected static final String SUPER_ORGANIZATIONS_KEY = "superOrganizations"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to an enclosing conference.
     *
     * @since 3.6
     */
    protected static final String ENCLOSING_CONFERENCE_KEY = "enclosingConference"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to a LEAR organization.
     *
     * @since 3.0
     */
    protected static final String LEAR_ORGANIZATION_KEY = "learOrganization"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the other partners of a project.
     *
     * @since 3.0
     */
    protected static final String OTHER_PARTNERS_KEY = "otherPartners"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the participants to a project.
     *
     * @since 3.0
     */
    protected static final String PARTICIPANTS_KEY = "participants"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the role.
     *
     * @since 3.0
     */
    protected static final String ROLE_KEY = "role"; //$NON-NLS-1$
    /**
     * Name of the field for the description of the role.
     *
     * @since 3.2
     */
    protected static final String ROLE_DESCRIPTION_KEY = "roleDescription"; //$NON-NLS-1$
    /**
     * Name of the field for the holders.
     *
     * @since 3.2
     */
    protected static final String HOLDERS_KEY = "holders"; //$NON-NLS-1$
    /**
     * Name of the field for the references to projects.
     *
     * @since 3.2
     */
    protected static final String PROJECTS_KEY = "projects"; //$NON-NLS-1$
    /**
     * Name of the field for the references to publications.
     *
     * @since 3.5
     */
    protected static final String PUBLICATIONS_KEY = "publications"; //$NON-NLS-1$
    /**
     * Name of the field for the references to memberships.
     *
     * @since 3.5
     */
    protected static final String MEMBERSHIPS_KEY = "memberships"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to an organization.
     *
     * @since 3.2
     */
    protected static final String ORGANIZATION_KEY = "organization"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the URLs of the videos for a project.
     *
     * @since 3.0
     */
    protected static final String VIDEO_URLS_KEY = "videoURLs"; //$NON-NLS-1$
    /**
     * Name of the field for the reference to the paths of images that are associated to a project.
     *
     * @since 3.0
     */
    protected static final String PATHS_TO_IMAGES_KEY = "pathsToImages"; //$NON-NLS-1$
    /**
     * Name of the field for the inviter person reference.
     */
    protected static final String INVITER_KEY = "inviter"; //$NON-NLS-1$
    /**
     * Name of the field for a percentage.
     */
    protected static final String PERCENT_KEY = "percent"; //$NON-NLS-1$
    /**
     * Name of the field for the candidate reference.
     */
    protected static final String CANDIDATE_KEY = "candidate"; //$NON-NLS-1$
    /**
     * Name of the field for the promoters' references.
     */
    protected static final String PROMOTERS_KEY = "promoters"; //$NON-NLS-1$
    /**
     * Name of the field for the supervisors' references.
     */
    protected static final String SUPERVISORS_KEY = "supervisors"; //$NON-NLS-1$
    /**
     * Name of the field for the abandonment.
     */
    protected static final String ABANDONMENT_KEY = "abandonment"; //$NON-NLS-1$
    /**
     * Name of the field for the defense date.
     */
    protected static final String DEFENSE_DATE_KEY = "defenseDate"; //$NON-NLS-1$
    /**
     * Name of the field for the number of ATER positions.
     */
    protected static final String ATER_COUNT_KEY = "aterCount"; //$NON-NLS-1$
    /**
     * Name of the field for the becoming of.
     */
    protected static final String BECOMING_KEY = "becoming"; //$NON-NLS-1$
    /**
     * Name of the field for the funding.
     */
    protected static final String FUNDING_KEY = "funding"; //$NON-NLS-1$
    /**
     * Name of the field for the funding.
     *
     * @deprecated Replaced by {@link #FUNDING_KEY}.
     */
    @Deprecated(forRemoval = true, since = "3.2")
    protected static final String FUNDINGSCHEME_KEY = "fundingScheme"; //$NON-NLS-1$
    /**
     * Name of the field for the funding details.
     */
    protected static final String FUNDING_DETAILS_KEY = "fundingDetails"; //$NON-NLS-1$
    /**
     * Name of the field for the research organization reference.
     */
    protected static final String RESEARCHORGANIZATION_KEY = "researchOrganization"; //$NON-NLS-1$
    /**
     * Name of the field for the definition of the journals' annual quality indicators.
     */
    protected static final String QUALITYINDICATORSHISTORY_KEY = "qualityIndicatorsHistory"; //$NON-NLS-1$
    /**
     * Name of the field for the CORE Index.
     */
    protected static final String COREINDEX_KEY = "coreIndex"; //$NON-NLS-1$
    /**
     * Name of the field for the Scimago Q-Index.
     */
    protected static final String SCIMAGOQINDEX_KEY = "scimagoQIndex"; //$NON-NLS-1$
    /**
     * Name of the field for the Web-of-Science Q-Index.
     */
    protected static final String WOSQINDEX_KEY = "wosQIndex"; //$NON-NLS-1$
    /**
     * Name of the field for the journal impact factor.
     */
    protected static final String IMPACTFACTOR_KEY = "impactFactor"; //$NON-NLS-1$
    /**
     * Name of the field for the types.
     */
    protected static final String TYPE_KEY = "type"; //$NON-NLS-1$
    /**
     * Name of the field for the number of hours.
     *
     * @since 3.4
     */
    protected static final String HOURS_KEY = "hours"; //$NON-NLS-1$
    /**
     * Name of the field for the number of hours per year.
     *
     * @since 3.4
     */
    protected static final String ANNUAL_HOURS_KEY = "annuHours"; //$NON-NLS-1$
    /**
     * Name of the field for the authors of a publication.
     */
    protected static final String AUTHORS_KEY = "authors"; //$NON-NLS-1$
    /**
     * Name of the field for the year. This field is usually for {@link Publication}.
     *
     * @see #PUBLICATIONYEAR_KEY
     * @see #REFERENCEYEAR_KEY
     */
    protected static final String YEAR_KEY = "year"; //$NON-NLS-1$
    /**
     * Name of the field for the year. This field is usually for {@link JournalQualityAnnualIndicators}.
     *
     * @see #YEAR_KEY
     * @see #PUBLICATIONYEAR_KEY
     */
    protected static final String REFERENCEYEAR_KEY = "referenceYear"; //$NON-NLS-1$
    /**
     * Name of the field for the major language of a publication.
     *
     * @see #MAJORLANGUAGE_KEY
     */
    protected static final String LANGUAGE_KEY = "language"; //$NON-NLS-1$
    /**
     * Name of the field for the major language of a publication.
     *
     * @see #LANGUAGE_KEY
     */
    protected static final String MAJORLANGUAGE_KEY = "majorLanguage"; //$NON-NLS-1$
    /**
     * Name of the field for the month of a publication.
     */
    protected static final String MONTH_KEY = "month"; //$NON-NLS-1$
    /**
     * Name of the field for the URL of the publication.
     *
     * @see #EXTRAURL_KEY
     */
    protected static final String URL_KEY = "url"; //$NON-NLS-1$
    /**
     * Name of the field for the URL of the publication.
     *
     * @see #URL_KEY
     */
    protected static final String EXTRAURL_KEY = "extraURL"; //$NON-NLS-1$
    /**
     * Name of the field for the first name of a person.
     */
    protected static final String FIRSTNAME_KEY = "firstName"; //$NON-NLS-1$
    /**
     * Name of the field for the last name of a person.
     */
    protected static final String LASTNAME_KEY = "lastName"; //$NON-NLS-1$
    /**
     * Name of the field for the full name of a person.
     */
    protected static final String FULLNAME_KEY = "fullName"; //$NON-NLS-1$
    private static final long serialVersionUID = 2357109361010281995L;
    private final MessageSourceAccessor messages;

    /**
     * Constructor.
     *
     * @param messages the accessor to the localized strings.
     */
    protected JsonTool(MessageSourceAccessor messages) {
        this.messages = messages;
    }

    /**
     * Extract the raw value from the given node.
     *
     * @param node the JSON node.
     * @return the raw value, or {@code null} if the value cannot be obtained.
     */
    protected static Object getRawValue(JsonNode node) {
        if (node != null) {
            if (node.isBoolean()) {
                return Boolean.valueOf(node.booleanValue());
            }
            if (node.isShort()) {
                return Short.valueOf(node.shortValue());
            }
            if (node.isInt()) {
                return Integer.valueOf(node.intValue());
            }
            if (node.isLong()) {
                return Long.valueOf(node.longValue());
            }
            if (node.isFloat()) {
                return Float.valueOf(node.floatValue());
            }
            if (node.isDouble()) {
                return Double.valueOf(node.doubleValue());
            }
            if (node.isBigInteger()) {
                return node.bigIntegerValue();
            }
            if (node.isBigDecimal()) {
                return node.decimalValue();
            }
            if (node.isTextual()) {
                return node.textValue();
            }
        }
        return null;
    }

    /**
     * Extract the raw string value from the given node.
     *
     * @param node the JSON node.
     * @return the raw value, or {@code null} if the value cannot be obtained or the value is empty string.
     * @since 3.0
     */
    protected static String getStringValue(JsonNode node) {
        final var raw = getRawValue(node);
        if (raw != null) {
            return Strings.emptyToNull(raw.toString());
        }
        return null;
    }

    /**
     * Extract the raw number value from the given node.
     *
     * @param node the JSON node.
     * @return the raw value, or {@code null} if the value cannot be obtained.
     * @since 3.0
     */
    protected static Number getNumberValue(JsonNode node) {
        final var raw = getRawValue(node);
        if (raw instanceof Number castValue) {
            return castValue;
        }
        if (raw != null) {
            try {
                return Double.valueOf(raw.toString());
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Find the setter methods that matches the given names.
     *
     * @param type  the type of the object on which the setter function must be invoked.
     * @param names the set of lower-cased names of the setter functions that could serve as candidates for
     *              finding the method.
     * @param value the value to give to the setter function. The value is used for checking the formal parameter's type.
     * @return the method that could be served as setter, or {@code null} if none was found.
     */
    protected static Method findSetterMethod(Class<?> type, Set<String> names, Object value) {
        assert value != null;
        try {
            final var valueType = value.getClass();
            final var methods = Arrays.asList(type.getMethods()).parallelStream().filter(it -> {
                if (it.getParameterCount() == 1 && (names.contains(it.getName().toLowerCase()))) {
                    final Class<?> receiver = it.getParameterTypes()[0];
                    final Class<?> provider = valueType;
                    return receiver.isAssignableFrom(provider);
                }
                return false;
            }).collect(Collectors.toSet());
            final var size = methods.size();
            if (size > 1) {
                throw new IllegalArgumentException("Too many setter function candidates (case insensitive) in type '" //$NON-NLS-1$
                        + type.getName() + "' with names: " + names); //$NON-NLS-1$
            } else if (size == 1) {
                return methods.iterator().next();
            }
        } catch (Throwable ex) {
            //
        }
        return null;
    }

    /**
     * Replies all the getter functions of the given type that have a matching setter function.
     *
     * @param source the source type.
     * @return the map of the methods. Keys are the names of the attributes, and the values are the setter functions.
     */
    protected static Map<String, Method> findGetterMethods(Class<?> source) {
        final var setters = new TreeSet<String>();
        final var getters = new HashMap<String, Method>();
        for (final var meth : source.getMethods()) {
            final var name = meth.getName().toLowerCase();
            if (meth.getParameterCount() == 1
                    && (meth.getParameterTypes()[0].isPrimitive()
                    || Number.class.equals(meth.getParameterTypes()[0])
                    || String.class.equals(meth.getParameterTypes()[0])
                    || Boolean.class.equals(meth.getParameterTypes()[0]))
                    && name.startsWith(SETTER_FUNCTION_PREFIX)) {
                final var attrName = toLowerFirst(meth.getName().substring(SETTER_FUNCTION_PREFIX.length()));
                setters.add(attrName);
            } else if (meth.getParameterCount() == 0
                    && (meth.getReturnType().isPrimitive()
                    || meth.getReturnType().isEnum()
                    || Number.class.isAssignableFrom(meth.getReturnType())
                    || String.class.isAssignableFrom(meth.getReturnType())
                    || Boolean.class.isAssignableFrom(meth.getReturnType())
                    || LocalDate.class.isAssignableFrom(meth.getReturnType())
                    || Character.class.isAssignableFrom(meth.getReturnType()))
                    && (name.startsWith(GETTER_FUNCTION_PREFIX)
                    || name.startsWith(IS_GETTER_FUNCTION_PREFIX))) {
                if (name.startsWith(IS_GETTER_FUNCTION_PREFIX)) {
                    final var attrName = toLowerFirst(meth.getName().substring(IS_GETTER_FUNCTION_PREFIX.length()));
                    getters.put(attrName, meth);
                } else {
                    final var attrName = toLowerFirst(meth.getName().substring(GETTER_FUNCTION_PREFIX.length()));
                    getters.put(attrName, meth);
                }
            }
        }
        //
        getters.remove("id"); //$NON-NLS-1$
        //
        final var iterator = getters.entrySet().iterator();
        while (iterator.hasNext()) {
            final var entry = iterator.next();
            if (!setters.contains(entry.getKey())) {
                iterator.remove();
            }
        }
        //
        return getters;
    }

    private static String toLowerFirst(String name) {
        if (name.length() <= 0) {
            return name.toLowerCase();
        }
        final var pattern = Pattern.compile("^[A-Z]+$"); //$NON-NLS-1$
        final var matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return name.toLowerCase();
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    /**
     * Convert the given value into an equivalent value that could be saved into a JSON.
     *
     * @param value the value to convert.
     * @return the JSON compliant value, or {@code null} if the value cannot be saved into JSON.
     */
    protected static Object convertValue(Object value) {
        if (value != null) {
            final var type = value.getClass();
            if (value instanceof CharSequence castValue) {
                return Strings.emptyToNull(castValue.toString());
            }
            if (type.isPrimitive() || value instanceof Number || value instanceof Boolean) {
                return value;
            }
            if (value instanceof Enum castValue) {
                return castValue.name().toLowerCase();
            }
            if (value instanceof LocalDate castValue) {
                return castValue.toString();
            }
            if (value instanceof Character castValue) {
                return castValue.toString();
            }
        }
        return null;
    }

    /**
     * Create a reference to another entity.
     *
     * @param id      the identifier.
     * @param factory the factory of JSON nodes.
     */
    protected static JsonNode createReference(String id, JsonNodeCreator factory) {
        final var ref = factory.objectNode();
        ref.set(ID_FIELDNAME, factory.textNode(id));
        return ref;
    }

    /**
     * Add entity reference to the given receiver.
     *
     * @param receiver the receiver of JSON.
     * @param key      the key that should receive the reference.
     * @param id       the identifier.
     */
    protected static void addReference(ObjectNode receiver, String key, String id) {
        final var ref = createReference(id, receiver);
        receiver.set(key, ref);
    }

    /**
     * Add entity reference to the given receiver.
     *
     * @param receiver the receiver of JSON.
     * @param id       the identifier.
     * @since 3.2
     */
    protected static void addReference(ArrayNode receiver, String id) {
        final var ref = createReference(id, receiver);
        receiver.add(ref);
    }

    /**
     * Replies the accessor to the localized strings.
     *
     * @return the accessor.
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        return this.messages;
    }

}
