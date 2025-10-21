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
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructure;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolder;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureRepository;
import fr.utbm.ciad.labmanager.data.assostructure.HolderRole;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.conference.ConferenceRepository;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationRepository;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.journal.JournalRepository;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipRepository;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationRepository;
import fr.utbm.ciad.labmanager.data.project.*;
import fr.utbm.ciad.labmanager.data.publication.*;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisRepository;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.data.supervision.SupervisionRepository;
import fr.utbm.ciad.labmanager.data.supervision.Supervisor;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorType;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityRepository;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityType;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.data.user.UserRepository;
import fr.utbm.ciad.labmanager.services.indicator.GlobalIndicatorsService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.xtext.xbase.lib.Functions.Function3;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Importer of JSON data into the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see ZipToDatabaseImporter
 * @since 2.0.0
 */
@Component
public class JsonToDatabaseImporter extends JsonTool {

    private static final long serialVersionUID = 353807006051937542L;
    private final Multimap<String, String> fieldAliases = LinkedListMultimap.create();
    private final SessionFactory sessionFactory;
    private final OrganizationAddressRepository addressRepository;
    private final ResearchOrganizationRepository organizationRepository;
    private final PersonRepository personRepository;
    private final PersonService personService;
    private final MembershipRepository organizationMembershipRepository;
    private final JournalRepository journalRepository;
    private final JournalQualityAnnualIndicatorsRepository journalIndicatorsRepository;
    private final ConferenceRepository conferenceRepository;
    private final ConferenceQualityAnnualIndicatorsRepository conferenceIndicatorsRepository;
    private final PublicationService publicationService;
    private final AuthorshipRepository authorshipRepository;
    private final PersonNameParser personNameParser;
    private final JuryMembershipRepository juryMembershipRepository;
    private final SupervisionRepository supervisionRepository;
    private final PersonInvitationRepository invitationRepository;
    private final GlobalIndicatorsService globalIndicatorsService;
    private final ProjectRepository projectRepository;
    private final AssociatedStructureRepository structureRepository;
    private final TeachingActivityRepository teachingRepository;
    private final ScientificAxisRepository scientificAxisRepository;
    private final UserRepository userRepository;
    private boolean fake;

    /**
     * Constructor.
     *
     * @param messages                         the accessor to the localized strings.
     * @param sessionFactory                   the factory of an hibernate session.
     * @param addressRepository                the accessor to the address repository.
     * @param organizationRepository           the accessor to the organization repository.
     * @param personRepository                 the accessor to the person repository.
     * @param personService                    the accessor to the high-level person services.
     * @param organizationMembershipRepository the accessor to the organization membership repository.
     * @param journalRepository                the accessor to the journal repository.
     * @param journalIndicatorsRepository      the accessor to the repository of the journal quality annual indicators.
     * @param conferenceRepository             the accessor to the conference repository.
     * @param conferenceIndicatorsRepository   the accessor to the repository of the conference quality annual indicators.
     * @param publicationService               the service related to the publications.
     * @param authorshipRepository             the accessor to the authorships.
     * @param personNameParser                 the parser of person names.
     * @param juryMembershipRepository         the repository of jury memberships.
     * @param supervisionRepository            the repository of supervisions.
     * @param invitationRepository             the repository of invitations.
     * @param globalIndicatorsService          the service of the global indicators.
     * @param projectRepository                the repository of the projects.
     * @param structureRepository              the repository of the associated structures.
     * @param teachingRepository               the repository of the teaching activities.
     * @param scientificAxisRepository         the repository of the scientific axes.
     * @param userRepository                   the repository of the application users.
     */
    public JsonToDatabaseImporter(
            @Autowired MessageSourceAccessor messages,
            @Autowired SessionFactory sessionFactory,
            @Autowired OrganizationAddressRepository addressRepository,
            @Autowired ResearchOrganizationRepository organizationRepository,
            @Autowired PersonRepository personRepository,
            @Autowired PersonService personService,
            @Autowired MembershipRepository organizationMembershipRepository,
            @Autowired JournalRepository journalRepository,
            @Autowired JournalQualityAnnualIndicatorsRepository journalIndicatorsRepository,
            @Autowired ConferenceRepository conferenceRepository,
            @Autowired ConferenceQualityAnnualIndicatorsRepository conferenceIndicatorsRepository,
            @Autowired PublicationService publicationService,
            @Autowired AuthorshipRepository authorshipRepository,
            @Autowired PersonNameParser personNameParser,
            @Autowired JuryMembershipRepository juryMembershipRepository,
            @Autowired SupervisionRepository supervisionRepository,
            @Autowired PersonInvitationRepository invitationRepository,
            @Autowired GlobalIndicatorsService globalIndicatorsService,
            @Autowired ProjectRepository projectRepository,
            @Autowired AssociatedStructureRepository structureRepository,
            @Autowired TeachingActivityRepository teachingRepository,
            @Autowired ScientificAxisRepository scientificAxisRepository,
            @Autowired UserRepository userRepository) {
        super(messages);
        this.sessionFactory = sessionFactory;
        this.addressRepository = addressRepository;
        this.organizationRepository = organizationRepository;
        this.personRepository = personRepository;
        this.personService = personService;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.journalRepository = journalRepository;
        this.journalIndicatorsRepository = journalIndicatorsRepository;
        this.conferenceRepository = conferenceRepository;
        this.conferenceIndicatorsRepository = conferenceIndicatorsRepository;
        this.publicationService = publicationService;
        this.authorshipRepository = authorshipRepository;
        this.personNameParser = personNameParser;
        this.juryMembershipRepository = juryMembershipRepository;
        this.supervisionRepository = supervisionRepository;
        this.invitationRepository = invitationRepository;
        this.globalIndicatorsService = globalIndicatorsService;
        this.projectRepository = projectRepository;
        this.structureRepository = structureRepository;
        this.teachingRepository = teachingRepository;
        this.scientificAxisRepository = scientificAxisRepository;
        this.userRepository = userRepository;
        initializeFieldAliases();
    }

    private static <T extends Enum<T>> T getEnum(JsonNode content, String key, Class<T> type) {
        if (content != null && !Strings.isNullOrEmpty(key) && content.isObject()) {
            final var value = content.get(key);
            if (value != null) {
                try {
                    final var name = value.asText();
                    final var constants = type.getEnumConstants();
                    if (constants != null) {
                        for (final var cons : constants) {
                            if (name.equalsIgnoreCase(cons.name())) {
                                return cons;
                            }
                        }
                    }
                } catch (Throwable ex) {
                    //
                }
            }
        }
        return null;
    }

    private static Float getFloat(JsonNode content, String key) {
        if (content != null && !Strings.isNullOrEmpty(key) && content.isObject()) {
            final var value = content.get(key);
            if (value != null) {
                try {
                    final var name = value.asText();
                    if (!Strings.isNullOrEmpty(name)) {
                        return Float.valueOf(name);
                    }
                } catch (Throwable ex) {
                    //
                }
            }
        }
        return null;
    }

    private static int getInt(JsonNode content, String key, int defaultValue) {
        if (content != null && !Strings.isNullOrEmpty(key) && content.isObject()) {
            final var value = content.get(key);
            if (value != null) {
                try {
                    final var strValue = value.asText();
                    if (!Strings.isNullOrEmpty(strValue)) {
                        return Integer.parseInt(strValue);
                    }
                } catch (Throwable ex) {
                    //
                }
            }
        }
        return defaultValue;
    }

    private static String getId(JsonNode content) {
        final var child = content.get(ID_FIELDNAME);
        if (child != null) {
            return Strings.emptyToNull(child.asText());
        }
        return null;
    }

    private static String getRef(JsonNode content) {
        if (content != null && content.isObject()) {
            final var value = content.get(ID_FIELDNAME);
            if (value != null) {
                return value.asText();
            }
        }
        return null;
    }

    private static PhoneNumber getPhoneNumber(JsonNode content, String key) {
        if (content != null && !Strings.isNullOrEmpty(key) && content.isObject()) {
            try {
                final var value = content.get(key);
                if (value != null) {
                    if (value.isObject()) {
                        final var country = getEnum(value, COUNTRY_KEY, CountryCode.class);
                        if (country != null) {
                            final var numberNode = value.get(NUMBER_KEY);
                            if (numberNode != null && numberNode.isTextual()) {
                                final var number = numberNode.textValue();
                                return new PhoneNumber(country, number);
                            }
                        }
                    } else if (value.isTextual()) {
                        return PhoneNumber.parse(value.textValue());
                    }
                }
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    private static Map<String, List<Long>> extractScientificAxes(JsonNode scientificAxes,
                                                                 Map<String, Long> objectIdRepository, String jsonKey) {
        final var axes = new TreeMap<String, List<Long>>();
        if (scientificAxes != null) {
            final var cache = new HashMap<String, Long>();
            scientificAxes.forEach(axisNode -> {
                final var mbrsNode = axisNode.get(jsonKey);
                if (mbrsNode != null && mbrsNode.isArray() && !mbrsNode.isEmpty()) {
                    final var axisId = getId(axisNode);
                    if (Strings.isNullOrEmpty(axisId)) {
                        throw new IllegalArgumentException("Invalid reference to a membership's scientific axis"); //$NON-NLS-1$
                    }
                    var axisDbId = cache.get(axisId);
                    if (axisDbId == null) {
                        axisDbId = objectIdRepository.get(axisId);
                        if (axisDbId == null) {
                            throw new IllegalArgumentException("Invalid reference to a membership's scientific axis"); //$NON-NLS-1$
                        }
                        cache.put(axisId, axisDbId);
                    }
                    final var axisDbId0 = axisDbId;
                    mbrsNode.forEach(mbrNode -> {
                        final var mbrId = getRef(mbrNode);
                        if (Strings.isNullOrEmpty(mbrId)) {
                            throw new IllegalArgumentException("Invalid reference to a membership in the scientific axis: " + axisId); //$NON-NLS-1$
                        }
                        final var listOfAxes = axes.computeIfAbsent(mbrId, it -> new ArrayList<>());
                        listOfAxes.add(axisDbId0);
                    });
                }
            });
        }
        return axes;
    }

    private static Membership findSuperOrganization(Membership serviceMembership, List<MembershipInfo> employers) {
        final var service = serviceMembership.getDirectResearchOrganization();
        final var superOrgs = service.getSuperOrganizations();
        for (final var employer : employers) {
            final var active = serviceMembership.isActiveIn(employer.membership.getMemberSinceWhen(), employer.membership.getMemberToWhen());
            final var directOrg = employer.membership.getDirectResearchOrganization();
            if (active && superOrgs.contains(directOrg)) {
                return employer.membership;
            }
        }
        return null;
    }

    private static void reassignSuperOrgnaizationsInMemberships(Map<Person, PersonMemberships> allData, Map<String, Long> objectIdRepository,
                                                                Logger logger) {
        final var size = allData.size();
        var i = 0;
        for (final var entry : allData.entrySet()) {
            final var person = entry.getKey();
            final var memberships = entry.getValue();
            logger.info("> Relinking memberships " + (i + 1) + "/" + size //$NON-NLS-1$ //$NON-NLS-2$
                    + " for " + person.getFullNameWithLastNameFirst()); //$NON-NLS-1$

            final var includedEmployers = new TreeSet<>(EntityUtils.getPreferredMembershipComparator());

            for (final var service : memberships.services) {
                if (service.membership.getSuperResearchOrganization() == null) {
                    final var superOrganizationMembership = findSuperOrganization(service.membership, memberships.employers);
                    if (superOrganizationMembership != null) {
                        final var superOrganization = superOrganizationMembership.getDirectResearchOrganization();
                        service.membership.setSuperResearchOrganization(superOrganization);
                        if (Hibernate.isInitialized(person.getMemberships())) {
                            person.getMemberships().removeIf(it -> superOrganization.equals(it.getDirectResearchOrganization()));
                        }
                        includedEmployers.add(superOrganizationMembership);
                    }
                }
            }

            final var iterator = memberships.employers.iterator();
            while (iterator.hasNext()) {
                final var element = iterator.next();
                if (includedEmployers.contains(element.membership)) {
                    iterator.remove();
                }
            }

            ++i;
        }
    }

    private static int parseMonthField(JsonNode value) {
        if (value != null) {
            final var text = value.asText();
            if (!Strings.isNullOrEmpty(text)) {
                switch (text.toLowerCase()) {
                    case "jan": //$NON-NLS-1$
                        return 1;
                    case "feb": //$NON-NLS-1$
                        return 2;
                    case "mar": //$NON-NLS-1$
                        return 3;
                    case "apr": //$NON-NLS-1$
                        return 4;
                    case "may": //$NON-NLS-1$
                        return 5;
                    case "jun": //$NON-NLS-1$
                        return 6;
                    case "jul": //$NON-NLS-1$
                        return 7;
                    case "aug": //$NON-NLS-1$
                        return 8;
                    case "sep": //$NON-NLS-1$
                        return 9;
                    case "oct": //$NON-NLS-1$
                        return 10;
                    case "nov": //$NON-NLS-1$
                        return 11;
                    case "dec": //$NON-NLS-1$
                        return 12;
                    default:
                        //
                }
            }
        }
        return 0;
    }

    private static List<String> getStringList(JsonNode node, String fieldName) {
        final var content = new ArrayList<String>();
        if (node != null && !Strings.isNullOrEmpty(fieldName)) {
            final var fieldNode = node.get(fieldName);
            if (fieldNode != null && fieldNode.isArray()) {
                fieldNode.forEach(it -> {
                    content.add(it.asText());
                });
            }
        }
        return content;
    }

    /**
     * Initialize the collection of standard and hard-coded field aliases.
     */
    protected void initializeFieldAliases() {
        addFieldAlias(YEAR_KEY, PUBLICATIONYEAR_KEY);
        addFieldAlias(LANGUAGE_KEY, MAJORLANGUAGE_KEY);
        addFieldAlias(URL_KEY, EXTRAURL_KEY);
    }

    /**
     * Add a alias for a field name. The alias is used for retrieving the setter methods based on the
     * field name. When a setter method is searched for the field {@code f}, the methods {@code setF} and
     *
     * @param sourceName the name of the field to associated to an alias. It is equivalent to {@code f} in the example.
     * @param aliasName  the name of the alias. It is equivalent to {@code alias} in the example.
     * @code{setAlias} are considered as candidates, where {@code alias} is defined as an alias of {@code f}.
     */
    public void addFieldAlias(String sourceName, String aliasName) {
        assert !Strings.isNullOrEmpty(sourceName);
        assert !Strings.isNullOrEmpty(aliasName);
        this.fieldAliases.put(sourceName, aliasName);
    }

    /**
     * Replies the aliases for a field name. The alias is used for retrieving the setter methods based on the
     * field name. When a setter method is searched for the field {@code f}, the methods {@code setF} and
     *
     * @param sourceName the name of the field to associated to an alias. It is equivalent to {@code f} in the example.
     * @return the list of aliases. It contains {@code alias} that is mentionned in the example.
     * @code{setAlias} are considered as candidates, where {@code alias} is defined as an alias of {@code f}.
     */
    public Collection<String> getFieldAliases(String sourceName) {
        assert !Strings.isNullOrEmpty(sourceName);
        return this.fieldAliases.get(sourceName);
    }

    /**
     * Replies if the importer is using a fake saving in the database.
     * If it replies {@code true}, the importer does not save into the database.
     * If it replies {@code false}, the importer does save in the database.
     *
     * @return {@code true} for fake saving.
     */
    public boolean isFake() {
        return this.fake;
    }

    /**
     * Change if the importer is using a fake saving in the database.
     * If it replies {@code true}, the importer does not save into the database.
     * If it replies {@code false}, the importer does save in the database.
     *
     * @param fake {@code true} for fake saving.
     */
    public void setFake(boolean fake) {
        this.fake = fake;
    }

    /**
     * Create the instance of an object that is initialized from the given JSON source.
     *
     * @param <T>             the expected type of object.
     * @param type            the expected type of object.
     * @param source          the JSON source.
     * @param aliasRepository the repository of aliases. This repository is filled up by this function.
     * @param failIfNoSetter  indicates if the function should fail if there is no setter method found for an attribute name in
     *                        the JSON file. The first argument is the attribute name, the second argument is the raw value for
     *                        the attribute, the third argument is the JSON node of the value.
     * @return the object or {@code null}.
     * @throws Exception if the object cannot be created.
     */
    protected <T> T createObject(Class<T> type, JsonNode source,
                                 Map<String, Set<String>> aliasRepository,
                                 Function3<String, Object, JsonNode, Boolean> failIfNoSetter) throws Exception {
        if (!source.isObject()) {
            throw new IllegalArgumentException("Source node for an object must be a Json map."); //$NON-NLS-1$
        }
        final var obj = type.getConstructor().newInstance();
        final var iterator = source.fields();
        while (iterator.hasNext()) {
            final var entry = iterator.next();
            final var key = entry.getKey();
            final var jsonValue = entry.getValue();
            if (!Strings.isNullOrEmpty(key) && !key.startsWith(HIDDEN_FIELD_PREFIX)
                    && !key.startsWith(SPECIAL_FIELD_PREFIX) && !key.equalsIgnoreCase("id")) { //$NON-NLS-1$
                final var aliases = aliasRepository.computeIfAbsent(key, it -> {
                    final var set = new TreeSet<String>();
                    set.add(SETTER_FUNCTION_PREFIX + key.toLowerCase());
                    if (key.startsWith(IS_GETTER_FUNCTION_PREFIX)) {
                        set.add(SETTER_FUNCTION_PREFIX + key.substring(IS_GETTER_FUNCTION_PREFIX.length()).toLowerCase());
                    }
                    for (final var alias : getFieldAliases(key)) {
                        set.add(SETTER_FUNCTION_PREFIX + alias.toLowerCase());
                    }
                    return set;
                });
                final var rawValue = getRawValue(jsonValue);
                final Method method;
                if (rawValue != null) {
                    method = findSetterMethod(type, aliases, rawValue);
                } else {
                    method = null;
                }
                if (method != null) {
                    method.invoke(obj, rawValue);
                } else if (failIfNoSetter != null && failIfNoSetter.apply(key, rawValue, jsonValue).booleanValue()) {
                    throw new IllegalArgumentException("Setter function not found for the attribute: " + key //$NON-NLS-1$
                            + "; with a value of type: " + jsonValue); //$NON-NLS-1$
                }
            }
        }
        return obj;
    }

    /**
     * Run the importer for JSON data source only.
     * This function calls {@link #importJsonFileToDatabase(URL)} and displays an information
     * message.
     *
     * @param url    the URL of the JSON file to read.
     * @param logger the logger to use for put a message in the log.
     * @throws Exception if there is problem for importing.
     * @see #importJsonFileToDatabase(URL)
     */
    public void importDataFileToDatabase(URL url, Logger logger) throws Exception {
        final var stats = importJsonFileToDatabase(url, logger);
        if (stats != null) {
            stats.logSummaryOn(logger);
        }
    }

    /**
     * Run the importer for JSON data source only.
     *
     * @param url    the URL of the JSON file to read.
     * @param logger the logger to use for put a message in the log.
     * @return the import stats.
     * @throws Exception if there is problem for importing.
     * @see #importDataFileToDatabase(URL)
     */
    public Stats importJsonFileToDatabase(URL url, Logger logger) throws Exception {
        try (final var is = url.openStream()) {
            return importJsonFileToDatabase(is, logger);
        }
    }

    /**
     * Run the importer for JSON data source only.
     *
     * @param inputStream the input stream of the JSON file to read.
     * @param logger      the logger to use for put a message in the log.
     * @return the import stats.
     * @throws Exception if there is problem for importing.
     * @see #importDataFileToDatabase(URL)
     */
    public Stats importJsonFileToDatabase(InputStream inputStream, Logger logger) throws Exception {
        final JsonNode content;
        try (final var isr = new InputStreamReader(inputStream)) {
            final var mapper = JsonUtils.createMapper();
            content = mapper.readTree(isr);
        }
        return importJsonFileToDatabase(content, null, logger);
    }

    /**
     * Run the importer for JSON data source only.
     *
     * @param content      the input node of the JSON file to read.
     * @param fileCallback a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger       the logger to use for put a message in the log.
     * @return the import stats.
     * @throws Exception if there is problem for importing.
     * @see #importDataFileToDatabase(URL)
     */
    public Stats importJsonFileToDatabase(JsonNode content, FileCallback fileCallback, Logger logger) throws Exception {
        if (content != null && !content.isEmpty()) {
            final var objectRepository = new TreeMap<String, Long>();
            final var aliasRepository = new TreeMap<String, Set<String>>();

            try (final var session = this.sessionFactory.openSession()) {
                insertGlobalIndicators(session, content.get(GLOBALINDICATORS_SECTION), objectRepository, aliasRepository, logger);
                final var nb6 = insertAddresses(session, content.get(ORGANIZATIONADDRESSES_SECTION), objectRepository, aliasRepository, fileCallback, logger);
                final var nb0 = insertOrganizations(session, content.get(RESEARCHORGANIZATIONS_SECTION), objectRepository, aliasRepository, fileCallback, logger);
                final var nb13 = insertScientificAxes(session, content.get(SCIENTIFIC_AXIS_SECTION), objectRepository, aliasRepository, fileCallback, logger);
                final var nb1 = insertPersons(session, content.get(PERSONS_SECTION), objectRepository, aliasRepository, logger);
                final var nb2 = insertJournals(session, content.get(JOURNALS_SECTION), objectRepository, aliasRepository, logger);
                final var nb14 = insertConferences(session, content.get(CONFERENCES_SECTION), objectRepository, aliasRepository, logger);
                final var scientificAxisNode = content.get(SCIENTIFIC_AXIS_SECTION);
                final var nb3 = insertOrganizationMemberships(session, content.get(ORGANIZATION_MEMBERSHIPS_SECTION),
                        scientificAxisNode, objectRepository, aliasRepository, logger);
                final var added = insertPublications(session, content.get(PUBLICATIONS_SECTION),
                        scientificAxisNode, objectRepository, aliasRepository, fileCallback, logger);
                final var nb4 = added != null ? added.getLeft().intValue() : 0;
                final var nb5 = added != null ? added.getRight().intValue() : 0;
                final var nb7 = insertJuryMemberships(session, content.get(JURY_MEMBERSHIPS_SECTION), objectRepository, aliasRepository, logger);
                final var nb8 = insertSupervisions(session, content.get(SUPERVISIONS_SECTION), objectRepository, aliasRepository, logger);
                final var nb9 = insertInvitations(session, content.get(INVITATIONS_SECTION), objectRepository, aliasRepository, logger);
                final var nb10 = insertProjects(session, content.get(PROJECTS_SECTION),
                        scientificAxisNode, objectRepository, aliasRepository, fileCallback, logger);
                final var nb11 = insertAssociatedStructures(session, content.get(ASSOCIATED_STRUCTURES_SECTION),
                        objectRepository, aliasRepository, fileCallback, logger);
                final var nb12 = insertTeachingActivities(session, content.get(TEACHING_ACTIVITY_SECTION),
                        objectRepository, aliasRepository, fileCallback, logger);
                final var nb15 = insertApplicationUsers(session, content.get(APPLICATION_USERS_SECTION), objectRepository, aliasRepository, logger);
                return new Stats(nb6, nb0, nb2, nb14, nb1, nb5, nb3, nb4, nb7, nb8, nb9, nb10, nb11, nb12, nb13, nb15);
            }
        }
        return new Stats();
    }

    /**
     * Create the global indicators in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param globalIndicators   the global indicators.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @throws Exception if an address cannot be created.
     */
    protected void insertGlobalIndicators(Session session, JsonNode globalIndicators, Map<String, Long> objectIdRepository,
                                          Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        if (globalIndicators != null && !globalIndicators.isEmpty()) {
            logger.info("Inserting global indicators..."); //$NON-NLS-1$
            final var visibleIndicators = globalIndicators.get(VISIBLEGLOBALINDICATORS_KEY);
            if (visibleIndicators != null && visibleIndicators.isArray()) {
                final var keys = new StringBuilder();
                for (final var valueNode : visibleIndicators) {
                    if (keys.length() > 0) {
                        keys.append(","); //$NON-NLS-1$
                    }
                    keys.append(valueNode.asText());
                }
                this.globalIndicatorsService.setVisibleIndicators(keys.toString(), logger);
            }
        }
    }

    /**
     * Create the organization addresses in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param addresses          the list of addresses in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new addresses in the database.
     * @throws Exception if an address cannot be created.
     */
    protected int insertAddresses(Session session, JsonNode addresses, Map<String, Long> objectIdRepository,
                                  Map<String, Set<String>> aliasRepository, FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (addresses != null && !addresses.isEmpty()) {
            logger.info("Inserting " + addresses.size() + " addresses..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (final var adrObject : addresses) {
                logger.info("> Address " + (i + 1) + "/" + addresses.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(adrObject);
                    session.beginTransaction();
                    var adr = createObject(OrganizationAddress.class, adrObject,
                            aliasRepository, null);
                    if (adr != null) {
                        if (!isFake()) {
                            adr = this.addressRepository.save(adr);
                        }
                        // Ensure that attached files are correct
                        if (fileCallback != null) {
                            var publicationChanged = false;
                            if (!Strings.isNullOrEmpty(adr.getPathToBackgroundImage())) {
                                final var ofn = adr.getPathToBackgroundImage();
                                final var fn = fileCallback.addressBackgroundImageFile(adr.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    adr.setPathToBackgroundImage(fn);
                                    publicationChanged = true;
                                }
                            }
                            if (publicationChanged && !isFake()) {
                                this.addressRepository.save(adr);
                            }
                        }
                        ++nbNew;
                        logger.info("  + " + adr.getName() + " (id: " + adr.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(adr.getId()));
                        }
                    }
                    session.getTransaction().commit();
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(ORGANIZATIONADDRESSES_SECTION, i, adrObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the research organizations in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param organizations      the list of organizations in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       the callback for managing filenames.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new organizations in the database.
     * @throws Exception if an organization cannot be created.
     */
    protected int insertOrganizations(Session session, JsonNode organizations, Map<String, Long> objectIdRepository,
                                      Map<String, Set<String>> aliasRepository, FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (organizations != null && !organizations.isEmpty()) {
            final var objectInstanceRepository = new TreeMap<String, ResearchOrganization>();
            logger.info("Inserting " + organizations.size() + " organizations..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            final var superOrgas = new ArrayList<Pair<ResearchOrganization, Set<String>>>();
            for (final var orgaObject : organizations) {
                logger.info("> Organization " + (i + 1) + "/" + organizations.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(orgaObject);
                    session.beginTransaction();
                    var orga = createObject(ResearchOrganization.class, orgaObject,
                            aliasRepository, null);
                    if (orga != null) {
                        // Save addresses
                        final var addressesNode = orgaObject.get(ADDRESSES_KEY);
                        if (addressesNode != null) {
                            final var addrs = new TreeSet<>(EntityUtils.getPreferredOrganizationAddressComparator());
                            for (final var addressRefNode : addressesNode) {
                                final var addressRef = getRef(addressRefNode);
                                if (Strings.isNullOrEmpty(addressRef)) {
                                    throw new IllegalArgumentException("Invalid address reference for organization with id: " + id); //$NON-NLS-1$
                                }
                                final var addressDbId = objectIdRepository.get(addressRef);
                                if (addressDbId == null || addressDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid address reference for organization with id: " + id); //$NON-NLS-1$
                                }
                                final var addressObj = this.addressRepository.findById(addressDbId);
                                if (addressObj.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid address reference for organization with id: " + id); //$NON-NLS-1$
                                }
                                addrs.add(addressObj.get());
                            }
                            orga.setAddresses(addrs);
                        }
                        if (!isFake()) {
                            orga = this.organizationRepository.save(orga);
                        }
                        // Ensure that attached files are correct
                        if (fileCallback != null) {
                            var organizationChanged = false;
                            if (!Strings.isNullOrEmpty(orga.getPathToLogo())) {
                                final var ofn = orga.getPathToLogo();
                                final var fn = fileCallback.organizationLogoFile(orga.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    orga.setPathToLogo(fn);
                                    organizationChanged = true;
                                }
                            }
                            if (organizationChanged && !isFake()) {
                                this.organizationRepository.save(orga);
                            }
                        }
                        ++nbNew;
                        logger.info("  + " + orga.getAcronymOrName() + " (id: " + orga.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectInstanceRepository.put(id, orga);
                            objectIdRepository.put(id, Long.valueOf(orga.getId()));
                        }
                        // Save hierarchical relation with other organization
                        final var superOrgs = new HashSet<String>();
                        final var superOrga = getRef(orgaObject.get(SUPER_ORGANIZATION_KEY));
                        if (!Strings.isNullOrEmpty(superOrga)) {
                            superOrgs.add(superOrga);
                        }
                        final var superOrgasNode = orgaObject.get(SUPER_ORGANIZATIONS_KEY);
                        if (superOrgasNode != null) {
                            for (final var orgaRefNode : superOrgasNode) {
                                final var orgaRef = getRef(orgaRefNode);
                                superOrgs.add(orgaRef);
                            }
                        }
                        if (!superOrgs.isEmpty()) {
                            superOrgas.add(Pair.of(orga, superOrgs));
                        }
                    }
                    session.getTransaction().commit();
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(RESEARCHORGANIZATIONS_SECTION, i, orgaObject, ex);
                }
                ++i;
            }
            // Save hierarchical relations between organizations
            for (final var entry : superOrgas) {
                final var subOrgaInstance = entry.getLeft();
                final var superOrgasInstances = new HashSet<ResearchOrganization>();
                for (final var supId : entry.getRight()) {
                    final var sup = objectInstanceRepository.get(supId);
                    if (sup == null) {
                        throw new IllegalArgumentException("Invalid reference to Json element with id: " + supId); //$NON-NLS-1$
                    }
                    superOrgasInstances.add(sup);
                }
                session.beginTransaction();
                subOrgaInstance.getSuperOrganizations().addAll(superOrgasInstances);
                for (final var sup : superOrgasInstances) {
                    sup.getSubOrganizations().add(subOrgaInstance);
                    logger.info("> Linking organizations: " + subOrgaInstance.getAcronymOrName() + " in " + sup.getAcronymOrName()); //$NON-NLS-1$ //$NON-NLS-2$
                }
                if (!isFake()) {
                    this.organizationRepository.saveAllAndFlush(Iterables.concat(Collections.singletonList(subOrgaInstance), superOrgasInstances));
                }
                session.getTransaction().commit();
            }
        }
        return nbNew;
    }

    /**
     * Create the persons in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param persons            the list of persons in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new persons in the database.
     * @throws Exception if a person cannot be created.
     */
    protected int insertPersons(Session session, JsonNode persons, Map<String, Long> objectIdRepository,
                                Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (persons != null && !persons.isEmpty()) {
            logger.info("Inserting " + persons.size() + " persons..."); //$NON-NLS-1$ //$NON-NLS-2$
            int i = 0;
            for (var personObject : persons) {
                logger.info("> Person " + (i + 1) + "/" + persons.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(personObject);
                    var person = createObject(Person.class, personObject, aliasRepository, null);
                    if (person != null) {
                        // Get the old specification of the email
                        if (Strings.isNullOrEmpty(person.getPrimaryEmail())) {
                            final var oldStyleEmail = getStringValue(personObject.get(OLD_FIELD_EMAIL_KEY));
                            person.setPrimaryEmail(oldStyleEmail);
                        }
                        // Get the phone numbers
                        final var officePhone = getPhoneNumber(personObject, OFFICE_PHONE_NUMBER_KEY);
                        person.setOfficePhone(officePhone);
                        final var mobilePhone = getPhoneNumber(personObject, MOBILE_PHONE_NUMBER_KEY);
                        person.setMobilePhone(mobilePhone);

                        // Finalize import
                        session.beginTransaction();
                        if (!isFake()) {
                            person = this.personRepository.save(person);
                        }
                        ++nbNew;
                        logger.info("  + " + person.getFullName() + " (id: " + person.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(person.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(PERSONS_SECTION, i, personObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    private Map<Person, PersonMemberships> prepareOrganizationMembershipInsertion(JsonNode memberships, JsonNode scientificAxes,
                                                                                  Map<String, Long> objectIdRepository, Map<String, Set<String>> aliasRepository,
                                                                                  List<Pair<Membership, Long>> addressPostProcessing, Logger logger) throws Exception {
        // Extract the scientific axes for each membership
        final var axesOfMemberships = extractScientificAxes(
                scientificAxes, objectIdRepository, MEMBERSHIPS_KEY);
        //
        var i = 0;
        final var allData = new HashMap<Person, PersonMemberships>();
        for (final var membershipObject : memberships) {
            logger.info("> Preparing organization membership " + (i + 1) + "/" + memberships.size()); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                final var id = getId(membershipObject);
                var membership = createObject(Membership.class, membershipObject, aliasRepository, null);
                if (membership != null) {
                    final var adrId = getRef(membershipObject.get(ADDRESS_KEY));
                    if (!Strings.isNullOrEmpty(adrId)) {
                        final var adrDbId = objectIdRepository.get(adrId);
                        if (adrDbId == null || adrDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid address reference for organization membership with id: " + id); //$NON-NLS-1$
                        }
                        addressPostProcessing.add(Pair.of(membership, adrDbId));
                    }
                    //
                    final var personId = getRef(membershipObject.get(PERSON_KEY));
                    if (Strings.isNullOrEmpty(personId)) {
                        throw new IllegalArgumentException("Invalid person reference for organization membership with id: " + id); //$NON-NLS-1$
                    }
                    final var personDbId = objectIdRepository.get(personId);
                    if (personDbId == null || personDbId.intValue() == 0) {
                        throw new IllegalArgumentException("Invalid person reference for organizationm embership with id: " + id); //$NON-NLS-1$
                    }
                    final var targetPerson = this.personRepository.findById(personDbId);
                    if (targetPerson.isEmpty()) {
                        throw new IllegalArgumentException("Invalid person reference for organization membership with id: " + id); //$NON-NLS-1$
                    }
                    //
                    final var orgaId = getRef(membershipObject.get(RESEARCHORGANIZATION_KEY));
                    if (Strings.isNullOrEmpty(orgaId)) {
                        throw new IllegalArgumentException("Invalid organization reference for organization membership with id: " + id); //$NON-NLS-1$
                    }
                    final var orgaDbId = objectIdRepository.get(orgaId);
                    if (orgaDbId == null || orgaDbId.intValue() == 0) {
                        throw new IllegalArgumentException("Invalid organization reference for organization membership with id: " + id); //$NON-NLS-1$
                    }
                    final var targetOrganization = this.organizationRepository.findById(orgaDbId);
                    if (targetOrganization.isEmpty()) {
                        throw new IllegalArgumentException("Invalid organization reference for organization membership with id: " + id); //$NON-NLS-1$
                    }
                    //
                    final var superOrgaId = getRef(membershipObject.get(SUPER_ORGANIZATION_KEY));
                    Optional<ResearchOrganization> targetSuperOrganization = Optional.empty();
                    if (!Strings.isNullOrEmpty(superOrgaId)) {
                        final var superOrgaDbId = objectIdRepository.get(superOrgaId);
                        if (superOrgaDbId == null || superOrgaDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid organization reference for organization membership with id: " + id); //$NON-NLS-1$
                        }
                        targetSuperOrganization = this.organizationRepository.findById(superOrgaDbId);
                        if (targetSuperOrganization.isEmpty()) {
                            throw new IllegalArgumentException("Invalid organization reference for organization membership with id: " + id); //$NON-NLS-1$
                        }
                    }
                    //
                    membership.setPerson(targetPerson.get());
                    membership.setDirectResearchOrganization(targetOrganization.get());
                    if (targetSuperOrganization.isPresent()) {
                        membership.setSuperResearchOrganization(targetSuperOrganization.get());
                    }
                    // Attach scientific axes to the membership
                    final var membershipScientificAxes = axesOfMemberships.get(id);
                    if (membershipScientificAxes != null && !membershipScientificAxes.isEmpty()) {
                        final var axisInstances = this.scientificAxisRepository.findAllById(membershipScientificAxes);
                        membership.setScientificAxes(axisInstances);
                    }
                    //
                    final var data = allData.computeIfAbsent(targetPerson.get(), it -> new PersonMemberships());
                    if (targetOrganization.get().getType().isEmployer()) {
                        data.employers.add(new MembershipInfo(membership, id));
                    } else {
                        data.services.add(new MembershipInfo(membership, id));
                    }
                }
            } catch (Throwable ex) {
                throw new UnableToImportJsonException(ORGANIZATION_MEMBERSHIPS_SECTION, i, membershipObject, ex);
            }
            ++i;
        }
        return allData;
    }

    private int saveOrganizationMemberships(Session session, Map<Person, PersonMemberships> allData,
                                            Map<String, Long> objectIdRepository, Logger logger) throws Exception {
        final var size = allData.size();
        var nbNew = 0;
        var i = 0;
        for (final var membershipPair : allData.entrySet()) {
            logger.info("> Saving organization membership " + (i + 1) + "/" + size); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                final var person = membershipPair.getKey();
                final var memberships = membershipPair.getValue();


                if (!isFake()) {
                    session.beginTransaction();
                    for (final var mbr : memberships.services) {
                        final var newMbr = this.organizationMembershipRepository.save(mbr.membership);
                        ++nbNew;
                        logger.info("  + " + mbr.membership.getDirectResearchOrganization().getAcronymOrName() //$NON-NLS-1$
                                + " - " + person.getFullName() //$NON-NLS-1$
                                + " (id: " + mbr.membership.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(mbr.jsonId)) {
                            objectIdRepository.put(mbr.jsonId, Long.valueOf(newMbr.getId()));
                        }
                    }
                    for (final var mbr : memberships.employers) {
                        final var newMbr = this.organizationMembershipRepository.save(mbr.membership);
                        ++nbNew;
                        logger.info("  + " + mbr.membership.getDirectResearchOrganization().getAcronymOrName() //$NON-NLS-1$
                                + " - " + person.getFullName() //$NON-NLS-1$
                                + " (id: " + mbr.membership.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(mbr.jsonId)) {
                            objectIdRepository.put(mbr.jsonId, Long.valueOf(newMbr.getId()));
                        }
                    }
                    this.personRepository.save(person);
                    session.getTransaction().commit();
                }
            } catch (Throwable ex) {
                throw new UnableToImportJsonException(ORGANIZATION_MEMBERSHIPS_SECTION, i, "", ex); //$NON-NLS-1$
            }
            ++i;
        }
        return nbNew;
    }

    private void postFixingAddresses(Session session, List<Pair<Membership, Long>> addressPostProcessing,
                                     Logger logger) throws Exception {
        if (!addressPostProcessing.isEmpty()) {
            final var size = addressPostProcessing.size();
            var i = 0;
            session.beginTransaction();
            for (final var pair : addressPostProcessing) {
                logger.info("  + Updating membership address " + (i + 1) + "/" + size); //$NON-NLS-1$ //$NON-NLS-2$
                final var membership = pair.getLeft();
                final var targetAddress = this.addressRepository.findById(pair.getRight());
                if (targetAddress.isEmpty()) {
                    throw new IllegalArgumentException("Invalid address reference for organization membership with id: " + pair.getRight()); //$NON-NLS-1$
                }
                membership.setOrganizationAddress(targetAddress.get(), false);
                this.organizationMembershipRepository.save(membership);
                ++i;
            }
            session.getTransaction().commit();
        }
    }

    /**
     * Create the organization memberships in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param memberships        the list of memberships in the Json source.
     * @param scientificAxes     the list of scientific axes of the memberships in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @return the number of new memberships in the database.
     * @throws Exception if a membership cannot be created.
     */
    protected int insertOrganizationMemberships(Session session, JsonNode memberships, JsonNode scientificAxes,
                                                Map<String, Long> objectIdRepository, Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (memberships != null && !memberships.isEmpty()) {
            logger.info("Inserting " + memberships.size() + " organization memberships..."); //$NON-NLS-1$ //$NON-NLS-2$
            //
            final var addressPostProcessing = new ArrayList<Pair<Membership, Long>>();
            //
            // Preparing the memberships
            final var allData = prepareOrganizationMembershipInsertion(memberships, scientificAxes, objectIdRepository,
                    aliasRepository, addressPostProcessing, logger);
            //
            // Relinking the super organizations of the memberships
            reassignSuperOrgnaizationsInMemberships(allData, objectIdRepository, logger);

            //
            // Saving the memberships in the JPA database
            final var n = saveOrganizationMemberships(session, allData, objectIdRepository, logger);
            nbNew += n;

            //
            // Post processing of the addresses for avoiding lazy loading errors
            postFixingAddresses(session, addressPostProcessing, logger);
        }
        return nbNew;
    }

    /**
     * Create the journals in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param journals           the list of journals in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new journals in the database.
     * @throws Exception if a membership cannot be created.
     */
    protected int insertJournals(Session session, JsonNode journals, Map<String, Long> objectIdRepository,
                                 Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (journals != null && !journals.isEmpty()) {
            logger.info("Inserting " + journals.size() + " journals..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var journalObject : journals) {
                logger.info("> Journal " + (i + 1) + "/" + journals.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(journalObject);
                    var journal = createObject(Journal.class, journalObject, aliasRepository, null);
                    if (journal != null) {
                        session.beginTransaction();
                        if (!isFake()) {
                            journal = this.journalRepository.save(journal);
                        }
                        // Create the quality indicators
                        final var history = journalObject.get(QUALITYINDICATORSHISTORY_KEY);
                        if (history != null && !history.isEmpty()) {
                            final var iterator = history.fields();
                            while (iterator.hasNext()) {
                                final var historyEntry = iterator.next();
                                final var year = Integer.parseInt(historyEntry.getKey());
                                String str = null;
                                if (historyEntry.getValue() != null) {
                                    final var n = historyEntry.getValue().get(SCIMAGOQINDEX_KEY);
                                    if (n != null) {
                                        str = n.asText();
                                    }
                                }
                                JournalQualityAnnualIndicators indicators = null;
                                if (!Strings.isNullOrEmpty(str)) {
                                    final var scimago = QuartileRanking.valueOfCaseInsensitive(str);
                                    if (scimago != null) {
                                        indicators = journal.setScimagoQIndexByYear(year, scimago);
                                    }
                                }
                                str = null;
                                if (historyEntry.getValue() != null) {
                                    final var n = historyEntry.getValue().get(WOSQINDEX_KEY);
                                    if (n != null) {
                                        str = n.asText();
                                    }
                                }
                                if (!Strings.isNullOrEmpty(str)) {
                                    final var wos = QuartileRanking.valueOfCaseInsensitive(str);
                                    if (wos != null) {
                                        final var oindicators = indicators;
                                        indicators = journal.setWosQIndexByYear(year, wos);
                                        assert oindicators == null || oindicators == indicators;
                                    }
                                }
                                Number flt = null;
                                if (historyEntry.getValue() != null) {
                                    final var n = historyEntry.getValue().get(IMPACTFACTOR_KEY);
                                    if (n != null) {
                                        flt = Double.valueOf(n.asDouble());
                                    }
                                }
                                if (flt != null) {
                                    final var impactFactor = flt.floatValue();
                                    if (impactFactor > 0) {
                                        final var oindicators = indicators;
                                        indicators = journal.setImpactFactorByYear(year, impactFactor);
                                        assert oindicators == null || oindicators == indicators;
                                    }
                                }
                                if (indicators != null && !isFake()) {
                                    this.journalIndicatorsRepository.save(indicators);
                                }
                            }
                        }
                        // Save again the journal for saving the links to the quality indicators
                        if (!isFake()) {
                            journal = this.journalRepository.save(journal);
                        }
                        ++nbNew;
                        //
                        logger.info("  + " + journal.getJournalName() + " (id: " + journal.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(journal.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(JOURNALS_SECTION, i, journalObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the conferences in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param conferences        the list of conferences in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new conferences in the database.
     * @throws Exception if a membership cannot be created.
     */
    protected int insertConferences(Session session, JsonNode conferences, Map<String, Long> objectIdRepository,
                                    Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (conferences != null && !conferences.isEmpty()) {
            logger.info("Inserting " + conferences.size() + " conferences..."); //$NON-NLS-1$ //$NON-NLS-2$
            int i = 0;
            final var enclosingConferences = new ArrayList<Pair<Conference, String>>();
            for (final var conferenceObject : conferences) {
                logger.info("> Conference " + (i + 1) + "/" + conferences.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(conferenceObject);
                    var conference = createObject(Conference.class, conferenceObject, aliasRepository, null);
                    if (conference != null) {
                        session.beginTransaction();
                        if (!isFake()) {
                            conference = this.conferenceRepository.save(conference);
                        }
                        // Create the quality indicators
                        final var history = conferenceObject.get(QUALITYINDICATORSHISTORY_KEY);
                        if (history != null && !history.isEmpty()) {
                            final var iterator = history.fields();
                            while (iterator.hasNext()) {
                                final var historyEntry = iterator.next();
                                final var year = Integer.parseInt(historyEntry.getKey());
                                String str = null;
                                if (historyEntry.getValue() != null) {
                                    final var n = historyEntry.getValue().get(COREINDEX_KEY);
                                    if (n != null) {
                                        str = n.asText();
                                    }
                                }
                                ConferenceQualityAnnualIndicators indicators = null;
                                if (!Strings.isNullOrEmpty(str)) {
                                    final var core = CoreRanking.valueOfCaseInsensitive(str);
                                    if (core != null) {
                                        indicators = conference.setCoreIndexByYear(year, core);
                                    }
                                }
                                if (indicators != null && !isFake()) {
                                    this.conferenceIndicatorsRepository.save(indicators);
                                }
                            }
                        }
                        // Save the enclosing conferences to a differed creation of the links
                        final var enclConference = getRef(conferenceObject.get(ENCLOSING_CONFERENCE_KEY));
                        if (!Strings.isNullOrEmpty(enclConference)) {
                            enclosingConferences.add(Pair.of(conference, enclConference));
                        }
                        // Save again the conference for saving the links to the quality indicators
                        if (!isFake()) {
                            conference = this.conferenceRepository.save(conference);
                        }
                        ++nbNew;
                        //
                        logger.info("  + " + conference.getNameOrAcronym() + " (id: " + conference.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(conference.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(CONFERENCES_SECTION, i, conferenceObject, ex);
                }
                ++i;
            }
            // Create the links between the conferences
            for (final var pair : enclosingConferences) {
                final var conferenceDbId = objectIdRepository.get(pair.getValue());
                if (conferenceDbId == null || conferenceDbId.intValue() == 0) {
                    throw new IllegalArgumentException("Invalid enclosing conference reference with id: " + pair.getValue()); //$NON-NLS-1$
                }
                final var optConference = this.conferenceRepository.findById(conferenceDbId);
                if (optConference.isEmpty()) {
                    throw new IllegalArgumentException("Invalid enclosing conference reference with id: " + pair.getValue()); //$NON-NLS-1$
                }
                final var subConference = pair.getLeft();
                final var enclosingConference = optConference.get();
                subConference.setEnclosingConference(enclosingConference);
                this.conferenceRepository.save(subConference);
            }
        }
        return nbNew;
    }

    /**
     * Create the publications (and additional authors) in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param publications       the list of publications in the Json source.
     * @param scientificAxes     the list of scientific axes of the publications in the Json source.
     * @param objectIdRepository the repository of the JSON elements with {@code "@id"} field.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the pair of numbers, never {@code null}. The first number is the number of added publication; the
     * second number is the is the number of added persons.
     * @throws Exception if a membership cannot be created.
     */
    protected Pair<Integer, Integer> insertPublications(Session session, JsonNode publications, JsonNode scientificAxes,
                                                        Map<String, Long> objectIdRepository, Map<String, Set<String>> aliasRepository, FileCallback fileCallback,
                                                        Logger logger) throws Exception {
        var nbNewPublications = 0;
        final var nbNewPersons = new MutableInt();
        if (publications != null && !publications.isEmpty()) {
            logger.info("Retreiving the existing publications..."); //$NON-NLS-1$
            // Extract the scientific axes for each membership
            final var axesOfPublications = extractScientificAxes(
                    scientificAxes, objectIdRepository, PUBLICATIONS_KEY);
            //
            logger.info("Inserting " + publications.size() + " publications..."); //$NON-NLS-1$ //$NON-NLS-2$
            int i = 0;
            for (final var publicationObject : publications) {
                logger.info("> Publication " + (i + 1) + "/" + publications.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(publicationObject);
                    final var updatedObjects = new ArrayList<>();
                    final var publication = createPublicationInstance(id,
                            publicationObject, objectIdRepository, aliasRepository, updatedObjects);
                    // Test if the publication is already inside the database
                    session.beginTransaction();
                    // Save the publication
                    if (!isFake()) {
                        this.publicationService.save(publication, logger);
                    }
                    // Ensure that attached files are correct
                    if (fileCallback != null) {
                        var publicationChanged = false;
                        if (!Strings.isNullOrEmpty(publication.getPathToDownloadablePDF())) {
                            final var ofn = publication.getPathToDownloadablePDF();
                            final var fn = fileCallback.publicationPdfFile(publication.getId(), ofn, logger);
                            if (!Objects.equals(ofn, fn)) {
                                publication.setPathToDownloadablePDF(fn);
                                publicationChanged = true;
                            }
                        }
                        if (!Strings.isNullOrEmpty(publication.getPathToDownloadableAwardCertificate())) {
                            final var ofn = publication.getPathToDownloadableAwardCertificate();
                            final var fn = fileCallback.publicationAwardFile(publication.getId(), ofn, logger);
                            if (!Objects.equals(ofn, fn)) {
                                publication.setPathToDownloadableAwardCertificate(fn);
                                publicationChanged = true;
                            }
                        }
                        if (publicationChanged && !isFake()) {
                            this.publicationService.save(publication, logger);
                        }
                    }
                    ++nbNewPublications;
                    if (!Strings.isNullOrEmpty(id)) {
                        objectIdRepository.put(id, Long.valueOf(publication.getId()));
                    }
                    //
                    logger.info("  + " + publication.getTitle() + " (id: " + publication.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                    // Attach authors
                    final var authors = publicationObject.get(AUTHORS_KEY);
                    if (authors == null || authors.isEmpty()) {
                        throw new IllegalArgumentException("No author for publication with id: " + id); //$NON-NLS-1$
                    }
                    var authorRank = 0;
                    final var iterator = authors.elements();
                    while (iterator.hasNext()) {
                        final var authorObject = iterator.next();
                        final var targetAuthor = findOrCreateAuthor(authorObject, objectIdRepository, nbNewPersons);
                        if (targetAuthor == null) {
                            throw new IllegalArgumentException("Invalid author reference for publication with id: " + id); //$NON-NLS-1$
                        }
                        //
                        var authorship = new Authorship();
                        authorship.setPerson(targetAuthor);
                        authorship.setPublication(publication);
                        authorship.setAuthorRank(authorRank);
                        if (!isFake()) {
                            authorship = this.authorshipRepository.save(authorship);
                        }
                        ++authorRank;
                    }
                    session.getTransaction().commit();
                    final var publicationScientificAxes = axesOfPublications.get(id);
                    if (publicationScientificAxes != null && !publicationScientificAxes.isEmpty()) {
                        session.beginTransaction();
                        final var axisInstances = this.scientificAxisRepository.findAllById(publicationScientificAxes);
                        publication.setScientificAxes(axisInstances);
                        if (!isFake()) {
                            this.publicationService.save(publication, logger);
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(PUBLICATIONS_SECTION, i, publicationObject, ex);
                }
                ++i;
            }
        }
        return Pair.of(Integer.valueOf(nbNewPublications), nbNewPersons.toInteger());
    }

    private Publication createPublicationInstance(String id, JsonNode publicationObject, Map<String, Long> objectIdRepository,
                                                  Map<String, Set<String>> aliasRepository, Collection<Object> updatedObjects) throws Exception {
        // Retrieve the elements that characterize the type of the publication
        final var type = getEnum(publicationObject, TYPE_KEY, PublicationType.class);
        if (type == null) {
            throw new IllegalArgumentException("Missing publication type"); //$NON-NLS-1$
        }
        final var publicationClass = type.getInstanceType();
        assert publicationClass != null;

        // Create the publication
        var publication = createObject(publicationClass, publicationObject,
                aliasRepository, (attrName, attrValue, attrNode) -> {
                    // Keys "authors" and "journal" are not directly set. They have a specific
                    // code for associating authors and journals to the publication
                    return Boolean.valueOf(!AUTHORS_KEY.equalsIgnoreCase(attrName) && !JOURNAL_KEY.equalsIgnoreCase(attrName)
                            && !CONFERENCE_KEY.equalsIgnoreCase(attrName) && !MONTH_KEY.equalsIgnoreCase(attrName));
                });
        if (publication == null) {
            throw new IllegalArgumentException("Unable to create the instance of the publication of type: " + publicationClass); //$NON-NLS-1$
        }

        // Attach month if it is provided
        final var month = parseMonthField(publicationObject.get(MONTH_KEY));
        if (month > 0 && month <= 12) {
            final var year = publication.getPublicationYear();
            if (year != 0) {
                final var localDate = LocalDate.of(year, month, 1);
                publication.setPublicationDate(localDate);
            }
        }

        // Attach journal if needed for the type of publication
        if (publication instanceof AbstractJournalBasedPublication journalPaper) {
            final var journalId = getRef(publicationObject.get(JOURNAL_KEY));
            if (Strings.isNullOrEmpty(journalId)) {
                throw new IllegalArgumentException("Invalid journal reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var journalDbId = objectIdRepository.get(journalId);
            if (journalDbId == null || journalDbId.intValue() == 0) {
                throw new IllegalArgumentException("Invalid journal reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var optJournal = this.journalRepository.findById(journalDbId);
            if (optJournal.isEmpty()) {
                throw new IllegalArgumentException("Invalid journal reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var targetJournal = optJournal.get();
            journalPaper.setJournal(targetJournal);
            updatedObjects.add(targetJournal);
        }

        // Attach conference if needed for the type of publication
        if (publication instanceof AbstractConferenceBasedPublication conferencePaper) {
            final var conferenceId = getRef(publicationObject.get(CONFERENCE_KEY));
            if (Strings.isNullOrEmpty(conferenceId)) {
                throw new IllegalArgumentException("Invalid conference reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var conferenceDbId = objectIdRepository.get(conferenceId);
            if (conferenceDbId == null || conferenceDbId.intValue() == 0) {
                throw new IllegalArgumentException("Invalid conference reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var optConference = this.conferenceRepository.findById(conferenceDbId);
            if (optConference.isEmpty()) {
                throw new IllegalArgumentException("Invalid conference reference for publication with id: " + id); //$NON-NLS-1$
            }
            final var targetConference = optConference.get();
            conferencePaper.setConference(targetConference);
            updatedObjects.add(targetConference);
        }

        // Check the minimum set of fields
        if (Strings.isNullOrEmpty(publication.getTitle())) {
            throw new IllegalArgumentException("A publication must have a title"); //$NON-NLS-1$
        }
        if (publication.getPublicationYear() <= 1980) {
            throw new IllegalArgumentException("A publication must have a year of publishing greather to 1980 (value: " //$NON-NLS-1$
                    + publication.getPublicationYear() + ")"); //$NON-NLS-1$
        }
        if (publication.getType() == null) {
            throw new IllegalArgumentException("A publication must have a type"); //$NON-NLS-1$
        }

        return publication;
    }

    private Person findOrCreateAuthor(JsonNode authorObject, Map<String, Long> objectIdRepository, MutableInt nbNewPersons) {
        assert authorObject != null;
        Person targetAuthor = null;
        final var authorId = getRef(authorObject);
        if (!Strings.isNullOrEmpty(authorId)) {
            final var personId = objectIdRepository.get(authorId);
            if (personId != null && personId.intValue() != 0) {
                targetAuthor = this.personService.getPersonById(personId.intValue());
            }
        }
        if (targetAuthor == null) {
            // The author is not a reference to a defined person
            final var authorName = authorObject.asText();
            final var firstName = this.personNameParser.parseFirstName(authorName);
            final var lastName = this.personNameParser.parseLastName(authorName);
            final var optPerson = this.personService.getPersonBySimilarName(firstName, lastName);
            if (optPerson == null) {
                // This is a new person in the database
                var newAuthor = new Person();
                newAuthor.setFirstName(this.personNameParser.formatNameForDisplay(firstName));
                newAuthor.setLastName(this.personNameParser.formatNameForDisplay(lastName));
                if (!isFake()) {
                    newAuthor = this.personRepository.save(newAuthor);
                }
                nbNewPersons.increment();
                targetAuthor = newAuthor;
            } else {
                targetAuthor = optPerson;
            }
        }
        return targetAuthor;
    }

    /**
     * Create the jury memberships in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param memberships        the list of memberships in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new memberships in the database.
     * @throws Exception if a membership cannot be created.
     */
    protected int insertJuryMemberships(Session session, JsonNode memberships, Map<String, Long> objectIdRepository,
                                        Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (memberships != null && !memberships.isEmpty()) {
            logger.info("Inserting " + memberships.size() + " jury memberships..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var membershipObject : memberships) {
                logger.info("> Jury membership " + (i + 1) + "/" + memberships.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(membershipObject);
                    JuryMembership membership = createObject(JuryMembership.class, membershipObject, aliasRepository, null);
                    if (membership != null) {
                        session.beginTransaction();
                        // Person
                        final var personId = getRef(membershipObject.get(PERSON_KEY));
                        if (Strings.isNullOrEmpty(personId)) {
                            throw new IllegalArgumentException("Invalid person reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        final var personDbId = objectIdRepository.get(personId);
                        if (personDbId == null || personDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid person reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        final var targetPerson = this.personRepository.findById(personDbId);
                        if (targetPerson.isEmpty()) {
                            throw new IllegalArgumentException("Invalid person reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        membership.setPerson(targetPerson.get());
                        // Candidate
                        final var candidateId = getRef(membershipObject.get(CANDIDATE_KEY));
                        if (Strings.isNullOrEmpty(candidateId)) {
                            throw new IllegalArgumentException("Invalid candidate reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        final var candidateDbId = objectIdRepository.get(candidateId);
                        if (candidateDbId == null || candidateDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid candidate reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        final var targetCandidate = this.personRepository.findById(candidateDbId);
                        if (targetCandidate.isEmpty()) {
                            throw new IllegalArgumentException("Invalid candidate reference for jury membership with id: " + id); //$NON-NLS-1$
                        }
                        membership.setCandidate(targetCandidate.get());
                        // Promoters
                        final var promotersNode = membershipObject.get(PROMOTERS_KEY);
                        if (promotersNode != null && promotersNode.isArray()) {
                            final var promoters = new ArrayList<Person>();
                            for (final var promoterNode : promotersNode) {
                                final var promoterId = getRef(promoterNode);
                                if (Strings.isNullOrEmpty(promoterId)) {
                                    throw new IllegalArgumentException("Invalid promoter reference for jury membership with id: " + id); //$NON-NLS-1$
                                }
                                final var promoterDbId = objectIdRepository.get(promoterId);
                                if (promoterDbId == null || promoterDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid promoter reference for jury membership with id: " + id); //$NON-NLS-1$
                                }
                                final var targetPromoter = this.personRepository.findById(promoterDbId);
                                if (targetPromoter.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid promoter reference for jury membership with id: " + id); //$NON-NLS-1$
                                }
                                promoters.add(targetPromoter.get());
                            }
                            membership.setPromoters(promoters);
                        }

                        if (!isFake()) {
                            membership = this.juryMembershipRepository.save(membership);
                        }
                        ++nbNew;
                        logger.info("  + " + targetPerson.get().getFullName() //$NON-NLS-1$
                                + " - " + targetCandidate.get().getFullName() //$NON-NLS-1$
                                + " - " + membership.getType().getLabel(getMessageSourceAccessor(), Gender.NOT_SPECIFIED, Locale.US) //$NON-NLS-1$
                                + " (id: " + membership.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(membership.getId()));
                        }

                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(JURY_MEMBERSHIPS_SECTION, i, membershipObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the supervisions in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param supervisions       the list of supervisions in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new supervisions in the database.
     * @throws Exception if a supervision cannot be created.
     */
    protected int insertSupervisions(Session session, JsonNode supervisions, Map<String, Long> objectIdRepository,
                                     Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (supervisions != null && !supervisions.isEmpty()) {
            logger.info("Inserting " + supervisions.size() + " supervisions..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var supervisionObject : supervisions) {
                logger.info("> Supervision " + (i + 1) + "/" + supervisions.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(supervisionObject);
                    var supervision = createObject(Supervision.class, supervisionObject, aliasRepository, null);
                    if (supervision != null) {
                        session.beginTransaction();
                        // Supervised Person
                        final var mbrId = getRef(supervisionObject.get(PERSON_KEY));
                        if (Strings.isNullOrEmpty(mbrId)) {
                            throw new IllegalArgumentException("Invalid membership reference for supervision with id: " + id); //$NON-NLS-1$
                        }
                        final var mbrDbId = objectIdRepository.get(mbrId);
                        if (mbrDbId == null || mbrDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid membership reference for supervision with id: " + id); //$NON-NLS-1$
                        }
                        final var targetMembership = this.organizationMembershipRepository.findById(mbrDbId);
                        if (targetMembership.isEmpty()) {
                            throw new IllegalArgumentException("Invalid membership reference for supervision with id: " + id); //$NON-NLS-1$
                        }
                        supervision.setSupervisedPerson(targetMembership.get());
                        if (!isFake()) {
                            this.supervisionRepository.save(supervision);
                        }
                        // Directors
                        final var supervisorsNode = supervisionObject.get(SUPERVISORS_KEY);
                        if (supervisorsNode != null && supervisorsNode.isArray()) {
                            final var supervisors = new ArrayList<Supervisor>();
                            for (final var supervisorNode : supervisorsNode) {
                                final var supervisorObj = new Supervisor();
                                final var supervisorId = getRef(supervisorNode.get(PERSON_KEY));
                                if (Strings.isNullOrEmpty(supervisorId)) {
                                    throw new IllegalArgumentException("Invalid supervisor reference for supervision with id: " + id); //$NON-NLS-1$
                                }
                                final var supervisorDbId = objectIdRepository.get(supervisorId);
                                if (supervisorDbId == null || supervisorDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid supervisor reference for supervision with id: " + id); //$NON-NLS-1$
                                }
                                final var targetSupervisor = this.personRepository.findById(supervisorDbId);
                                if (targetSupervisor.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid supervisor reference for jury membership with id: " + id); //$NON-NLS-1$
                                }
                                supervisorObj.setSupervisor(targetSupervisor.get());
                                supervisorObj.setType(getEnum(supervisorNode, TYPE_KEY, SupervisorType.class));
                                supervisorObj.setPercentage(getInt(supervisorNode, PERCENT_KEY, 0));
                                supervisors.add(supervisorObj);
                            }
                            supervision.setSupervisors(supervisors);
                            if (!isFake()) {
                                this.supervisionRepository.save(supervision);
                            }
                        }
                        ++nbNew;
                        logger.info("  + " + supervision.getSupervisedPerson().getPerson().getFullName() //$NON-NLS-1$
                                + " - " + supervision.getSupervisedPerson().getShortDescription(getMessageSourceAccessor(), Locale.US) //$NON-NLS-1$
                                + " (id: " + supervision.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(supervision.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(SUPERVISIONS_SECTION, i, supervisionObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the invitations in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param invitations        the list of invitations in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new supervisions in the database.
     * @throws Exception if a supervision cannot be created.
     */
    protected int insertInvitations(Session session, JsonNode invitations, Map<String, Long> objectIdRepository,
                                    Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (invitations != null && !invitations.isEmpty()) {
            logger.info("Inserting " + invitations.size() + " invitations..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var invitationObject : invitations) {
                logger.info("> Invitation " + (i + 1) + "/" + invitations.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(invitationObject);
                    var invitation = createObject(PersonInvitation.class, invitationObject, aliasRepository, null);
                    if (invitation != null) {
                        session.beginTransaction();
                        // Guest
                        final var guestId = getRef(invitationObject.get(GUEST_KEY));
                        if (Strings.isNullOrEmpty(guestId)) {
                            throw new IllegalArgumentException("Invalid guest reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        final var guestDbId = objectIdRepository.get(guestId);
                        if (guestDbId == null || guestDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid guest reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        final var targetGuest = this.personRepository.findById(guestDbId);
                        if (targetGuest.isEmpty()) {
                            throw new IllegalArgumentException("Invalid guest reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        invitation.setGuest(targetGuest.get());
                        // Inviter
                        final var inviterId = getRef(invitationObject.get(INVITER_KEY));
                        if (Strings.isNullOrEmpty(inviterId)) {
                            throw new IllegalArgumentException("Invalid inviter reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        final var inviterDbId = objectIdRepository.get(inviterId);
                        if (inviterDbId == null || inviterDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid inviter reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        final var targetInviter = this.personRepository.findById(inviterDbId);
                        if (targetInviter.isEmpty()) {
                            throw new IllegalArgumentException("Invalid inviter reference for invitation with id: " + id); //$NON-NLS-1$
                        }
                        invitation.setInviter(targetInviter.get());
                        //
                        if (!isFake()) {
                            this.invitationRepository.save(invitation);
                        }
                        ++nbNew;
                        logger.info("  + " + invitation.getGuest().getFullName() //$NON-NLS-1$
                                + " - " + invitation.getInviter().getFullName() //$NON-NLS-1$
                                + " (id: " + invitation.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(invitation.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(INVITATIONS_SECTION, i, invitationObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the projects in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param projects           the list of projects in the Json source.
     * @param scientificAxes     the list of scientific axes of the projects in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new projects in the database.
     * @throws Exception if a project cannot be created.
     */
    @SuppressWarnings("removal")
    protected int insertProjects(Session session, JsonNode projects, JsonNode scientificAxes,
                                 Map<String, Long> objectIdRepository, Map<String, Set<String>> aliasRepository,
                                 FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (projects != null && !projects.isEmpty()) {
            logger.info("Inserting " + projects.size() + " projects..."); //$NON-NLS-1$ //$NON-NLS-2$
            // Extract the scientific axes for each membership
            final var axesOfProjects = extractScientificAxes(
                    scientificAxes, objectIdRepository, PROJECTS_KEY);
            //
            var i = 0;
            for (var projectObject : projects) {
                logger.info("> Project " + (i + 1) + "/" + projects.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(projectObject);
                    var project = createObject(Project.class, projectObject, aliasRepository, null);
                    if (project != null) {
                        session.beginTransaction();

                        // Budgets
                        final var budgetsNode = projectObject.get(BUDGETS_KEY);
                        if (budgetsNode != null) {
                            final var budgetList = new ArrayList<ProjectBudget>();
                            budgetsNode.forEach(it -> {
                                final var budgetObject = new ProjectBudget();
                                var fundingValue = getStringValue(it.get(FUNDING_KEY));
                                // The following reading of the Json node "fundingScheme" is in the code for compatibility with old Json format.
                                if (fundingValue == null) {
                                    fundingValue = getStringValue(it.get(FUNDINGSCHEME_KEY));
                                }
                                if (fundingValue != null) {
                                    try {
                                        final var scheme = FundingScheme.valueOfCaseInsensitive(fundingValue);
                                        budgetObject.setFundingScheme(scheme);
                                    } catch (Throwable ex) {
                                        budgetObject.setFundingScheme(FundingScheme.NOT_FUNDED);
                                    }
                                } else {
                                    budgetObject.setFundingScheme(FundingScheme.NOT_FUNDED);
                                }
                                final var budgetValue = getNumberValue(it.get(BUDGET_KEY));
                                if (budgetValue != null) {
                                    budgetObject.setBudget(budgetValue.floatValue());
                                }
                                final var grantValue = getStringValue(it.get(GRANT_KEY));
                                if (grantValue != null) {
                                    budgetObject.setFundingReference(grantValue);
                                }
                                budgetList.add(budgetObject);
                            });
                            project.setBudgets(budgetList);
                        }

                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Video URLs
                        final var videoURLs = getStringList(projectObject, VIDEO_URLS_KEY);
                        project.setVideoURLs(videoURLs);

                        // Paths to images
                        final var pathsToImages = getStringList(projectObject, PATHS_TO_IMAGES_KEY);
                        project.setPathsToImages(pathsToImages);

                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Ensure that attached files are correct
                        if (fileCallback != null) {
                            var projectChanged = false;
                            if (!Strings.isNullOrEmpty(project.getPathToLogo())) {
                                final var ofn = project.getPathToLogo();
                                final var fn = fileCallback.projectLogoFile(project.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    project.setPathToLogo(fn);
                                    projectChanged = true;
                                }
                            }
                            if (!Strings.isNullOrEmpty(project.getPathToPowerpoint())) {
                                final var ofn = project.getPathToPowerpoint();
                                final var fn = fileCallback.projectPowerpointFile(project.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    project.setPathToPowerpoint(fn);
                                    projectChanged = true;
                                }
                            }
                            if (!Strings.isNullOrEmpty(project.getPathToPressDocument())) {
                                final var ofn = project.getPathToPressDocument();
                                final var fn = fileCallback.projectPressDocumentFile(project.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    project.setPathToPressDocument(fn);
                                    projectChanged = true;
                                }
                            }
                            if (!Strings.isNullOrEmpty(project.getPathToScientificRequirements())) {
                                final var ofn = project.getPathToScientificRequirements();
                                final var fn = fileCallback.projectScientificRequirementsFile(project.getId(), ofn, logger);
                                if (!Objects.equals(ofn, fn)) {
                                    project.setPathToScientificRequirements(fn);
                                    projectChanged = true;
                                }
                            }
                            if (!project.getPathsToImages().isEmpty()) {
                                final var newPaths = new ArrayList<String>();
                                int imageIndex = 0;
                                for (final var path : project.getPathsToImages()) {
                                    final var fn = fileCallback.projectImageFile(project.getId(), imageIndex, path, logger);
                                    if (!Objects.equals(path, fn)) {
                                        newPaths.add(fn);
                                        projectChanged = true;
                                    } else {
                                        newPaths.add(path);
                                    }
                                    ++imageIndex;
                                }
                                project.setPathsToImages(newPaths);
                            }
                            if (projectChanged && !isFake()) {
                                this.projectRepository.save(project);
                            }
                        }

                        // Coordinator
                        final var coordinatorId = getRef(projectObject.get(COORDINATOR_KEY));
                        if (Strings.isNullOrEmpty(coordinatorId)) {
                            throw new IllegalArgumentException("Invalid coordinator reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var coordinatorDbId = objectIdRepository.get(coordinatorId);
                        if (coordinatorDbId == null || coordinatorDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid coordinator reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var targetCoordinator = this.organizationRepository.findById(coordinatorDbId);
                        if (targetCoordinator.isEmpty()) {
                            throw new IllegalArgumentException("Invalid coordinator reference for project with id: " + id); //$NON-NLS-1$
                        }
                        project.setCoordinator(targetCoordinator.get());
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Local organization
                        final var localOrganizationId = getRef(projectObject.get(LOCAL_ORGANIZATION_KEY));
                        if (Strings.isNullOrEmpty(localOrganizationId)) {
                            throw new IllegalArgumentException("Invalid local organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var localOrganizationDbId = objectIdRepository.get(localOrganizationId);
                        if (localOrganizationDbId == null || localOrganizationDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid local organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var targetLocalOrganization = this.organizationRepository.findById(localOrganizationDbId);
                        if (targetLocalOrganization.isEmpty()) {
                            throw new IllegalArgumentException("Invalid local organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        project.setLocalOrganization(targetLocalOrganization.get());
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Super organization
                        final var superOrganizationId = getRef(projectObject.get(SUPER_ORGANIZATION_KEY));
                        if (Strings.isNullOrEmpty(superOrganizationId)) {
                            throw new IllegalArgumentException("Invalid super organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var superOrganizationDbId = objectIdRepository.get(superOrganizationId);
                        if (superOrganizationDbId == null || superOrganizationDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid super organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var targetSuperOrganization = this.organizationRepository.findById(superOrganizationDbId);
                        if (targetSuperOrganization.isEmpty()) {
                            throw new IllegalArgumentException("Invalid super organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        project.setSuperOrganization(targetSuperOrganization.get());
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // LEAR organization
                        final var learOrganizationId = getRef(projectObject.get(LEAR_ORGANIZATION_KEY));
                        if (Strings.isNullOrEmpty(learOrganizationId)) {
                            throw new IllegalArgumentException("Invalid LEAR organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var learOrganizationDbId = objectIdRepository.get(learOrganizationId);
                        if (learOrganizationDbId == null || learOrganizationDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid LEAR organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        final var targetLearOrganization = this.organizationRepository.findById(learOrganizationDbId);
                        if (targetLearOrganization.isEmpty()) {
                            throw new IllegalArgumentException("Invalid LEAR organization reference for project with id: " + id); //$NON-NLS-1$
                        }
                        project.setLearOrganization(targetLearOrganization.get());
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Other partners
                        final var otherPartners = new HashSet<ResearchOrganization>();
                        final var otherPartnersNode = projectObject.get(OTHER_PARTNERS_KEY);
                        if (otherPartnersNode != null) {
                            otherPartnersNode.forEach(otherPartnerNode -> {
                                final var partnerId = getRef(otherPartnerNode);
                                if (Strings.isNullOrEmpty(partnerId)) {
                                    throw new IllegalArgumentException("Invalid partner reference for project with id: " + id); //$NON-NLS-1$
                                }
                                final var partnerDbId = objectIdRepository.get(partnerId);
                                if (partnerDbId == null || partnerDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid partner reference for project with id: " + id); //$NON-NLS-1$
                                }
                                final var targetPartnerOrganization = this.organizationRepository.findById(partnerDbId);
                                if (targetPartnerOrganization.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid partner reference for project with id: " + id); //$NON-NLS-1$
                                }
                                otherPartners.add(targetPartnerOrganization.get());
                            });
                        }
                        project.setOtherPartners(otherPartners);
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        // Participants
                        final var participants = new ArrayList<ProjectMember>();
                        final var participantsNode = projectObject.get(PARTICIPANTS_KEY);
                        if (participantsNode != null) {
                            participantsNode.forEach(participantNode -> {
                                final var participantId = getRef(participantNode.get(PERSON_KEY));
                                if (Strings.isNullOrEmpty(participantId)) {
                                    throw new IllegalArgumentException("Invalid parcticipant reference for project with id: " + id); //$NON-NLS-1$
                                }
                                final var participantDbId = objectIdRepository.get(participantId);
                                if (participantDbId == null || participantDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid participant reference for project with id: " + id); //$NON-NLS-1$
                                }
                                final var targetParticipant = this.personRepository.findById(participantDbId);
                                if (targetParticipant.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid participant reference for project with id: " + id); //$NON-NLS-1$
                                }
                                final var role = Role.valueOfCaseInsensitive(participantNode.get(ROLE_KEY).asText());
                                final var member = new ProjectMember();
                                member.setPerson(targetParticipant.get());
                                member.setRole(role);
                                participants.add(member);
                            });
                        }
                        project.setParticipants(participants);
                        if (!isFake()) {
                            this.projectRepository.save(project);
                        }

                        ++nbNew;
                        logger.info("  + " + project.getAcronymOrScientificTitle() //$NON-NLS-1$
                                + " (id: " + project.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(project.getId()));
                        }
                        session.getTransaction().commit();
                        //
                        final var projectScientificAxes = axesOfProjects.get(id);
                        if (projectScientificAxes != null && !projectScientificAxes.isEmpty()) {
                            session.beginTransaction();
                            final var axisInstances = this.scientificAxisRepository.findAllById(projectScientificAxes);
                            project.setScientificAxes(axisInstances);
                            if (!isFake()) {
                                this.projectRepository.save(project);
                            }
                            session.getTransaction().commit();
                        }
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(PROJECTS_SECTION, i, projectObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the associated structures in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param structures         the list of associated structures in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new associated structures in the database.
     * @throws Exception if an associated structure cannot be created.
     */
    protected int insertAssociatedStructures(Session session, JsonNode structures, Map<String, Long> objectIdRepository,
                                             Map<String, Set<String>> aliasRepository, FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (structures != null && !structures.isEmpty()) {
            logger.info("Inserting " + structures.size() + " associated structures..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var structureObject : structures) {
                logger.info("> Associated Structure " + (i + 1) + "/" + structures.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(structureObject);
                    var structure = createObject(AssociatedStructure.class, structureObject, aliasRepository, null);
                    if (structure != null) {
                        session.beginTransaction();

                        final var fundingOrganizationId = getRef(structureObject.get(FUNDING_KEY));
                        if (Strings.isNullOrEmpty(fundingOrganizationId)) {
                            throw new IllegalArgumentException("Invalid funding organization reference for associated structure with id: " + id); //$NON-NLS-1$
                        }
                        final var fundingOrganizationDbId = objectIdRepository.get(fundingOrganizationId);
                        if (fundingOrganizationDbId == null || fundingOrganizationDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid funding organization reference for associated structure with id: " + id); //$NON-NLS-1$
                        }
                        final var fundingOrganization = this.organizationRepository.findById(fundingOrganizationDbId);
                        if (fundingOrganization.isEmpty()) {
                            throw new IllegalArgumentException("Invalid funding organization reference for associated structure with id: " + id); //$NON-NLS-1$
                        }
                        structure.setFundingOrganization(fundingOrganization.get());
                        if (!isFake()) {
                            this.structureRepository.save(structure);
                        }

                        final var holders = new ArrayList<AssociatedStructureHolder>();
                        final var holdersNode = structureObject.get(HOLDERS_KEY);
                        if (holdersNode != null) {
                            holdersNode.forEach(holderNode -> {
                                final var holderObj = new AssociatedStructureHolder();
                                // Person
                                final var personId = getRef(holderNode.get(PERSON_KEY));
                                if (Strings.isNullOrEmpty(personId)) {
                                    throw new IllegalArgumentException("Invalid holding person reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var personDbId = objectIdRepository.get(personId);
                                if (personDbId == null || personDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid holding person reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var person = this.personRepository.findById(personDbId);
                                if (person.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid holding person reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                holderObj.setPerson(person.get());
                                // Role
                                final var role = getEnum(holderNode, ROLE_KEY, HolderRole.class);
                                if (role == null) {
                                    throw new IllegalArgumentException("Invalid holder role for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                holderObj.setRole(role);
                                // Role description
                                final var roleDescription = getStringValue(holderNode.get(ROLE_DESCRIPTION_KEY));
                                if (!Strings.isNullOrEmpty(roleDescription)) {
                                    holderObj.setRoleDescription(roleDescription);
                                }
                                // Organization
                                final var organizationId = getRef(holderNode.get(ORGANIZATION_KEY));
                                if (Strings.isNullOrEmpty(organizationId)) {
                                    throw new IllegalArgumentException("Invalid holder organization reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var organizationDbId = objectIdRepository.get(organizationId);
                                if (organizationDbId == null || organizationDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid holder organization reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var organization = this.organizationRepository.findById(organizationDbId);
                                if (organization.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid holder organization reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                holderObj.setOrganization(organization.get());
                                // Super organization
                                final var superOrganizationId = getRef(holderNode.get(SUPER_ORGANIZATION_KEY));
                                if (!Strings.isNullOrEmpty(superOrganizationId)) {
                                    final var superOrganizationDbId = objectIdRepository.get(superOrganizationId);
                                    if (superOrganizationDbId != null && superOrganizationDbId.intValue() != 0) {
                                        final var superOrganization = this.organizationRepository.findById(superOrganizationDbId);
                                        if (superOrganization.isPresent()) {
                                            holderObj.setSuperOrganization(superOrganization.get());
                                        }
                                    }
                                }
                                holders.add(holderObj);
                            });
                        }
                        structure.setHolders(holders);
                        if (!isFake()) {
                            this.structureRepository.save(structure);
                        }

                        final var projects = new ArrayList<Project>();
                        final var projectsNode = structureObject.get(PROJECTS_KEY);
                        if (projectsNode != null) {
                            projectsNode.forEach(projectNode -> {
                                final var projectId = getRef(projectNode);
                                if (Strings.isNullOrEmpty(projectId)) {
                                    throw new IllegalArgumentException("Invalid project reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var projectDbId = objectIdRepository.get(projectId);
                                if (projectDbId == null || projectDbId.intValue() == 0) {
                                    throw new IllegalArgumentException("Invalid project reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                final var project = this.projectRepository.findById(projectDbId);
                                if (project.isEmpty()) {
                                    throw new IllegalArgumentException("Invalid project reference for associated structure with id: " + id); //$NON-NLS-1$
                                }
                                projects.add(project.get());
                            });
                        }
                        structure.setProjects(projects);
                        if (!isFake()) {
                            this.structureRepository.save(structure);
                        }

                        ++nbNew;
                        logger.info("  + " + structure.getAcronymOrName() //$NON-NLS-1$
                                + " (id: " + structure.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(structure.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(ASSOCIATED_STRUCTURES_SECTION, i, structureObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the teaching activities in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param activities         the list of teaching activities in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new associated structures in the database.
     * @throws Exception if an associated structure cannot be created.
     */
    protected int insertTeachingActivities(Session session, JsonNode activities, Map<String, Long> objectIdRepository,
                                           Map<String, Set<String>> aliasRepository, FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (activities != null && !activities.isEmpty()) {
            logger.info("Inserting " + activities.size() + " teaching activities..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var activityObject : activities) {
                logger.info("> Teaching activity " + (i + 1) + "/" + activities.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(activityObject);
                    var activity = createObject(TeachingActivity.class, activityObject, aliasRepository, null);
                    if (activity != null) {
                        session.beginTransaction();

                        // Teacher
                        final var personId = getRef(activityObject.get(PERSON_KEY));
                        if (Strings.isNullOrEmpty(personId)) {
                            throw new IllegalArgumentException("Invalid person reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        final var personDbId = objectIdRepository.get(personId);
                        if (personDbId == null || personDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid person reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        final var person = this.personRepository.findById(personDbId);
                        if (person.isEmpty()) {
                            throw new IllegalArgumentException("Invalid person reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        activity.setPerson(person.get());
                        if (!isFake()) {
                            this.teachingRepository.save(activity);
                        }

                        // University
                        final var universityId = getRef(activityObject.get(UNIVERSITY_KEY));
                        if (Strings.isNullOrEmpty(universityId)) {
                            throw new IllegalArgumentException("Invalid university reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        final var universityDbId = objectIdRepository.get(universityId);
                        if (universityDbId == null || universityDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid university reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        final var university = this.organizationRepository.findById(universityDbId);
                        if (university.isEmpty()) {
                            throw new IllegalArgumentException("Invalid university reference for teaching activity with id: " + id); //$NON-NLS-1$
                        }
                        activity.setUniversity(university.get());
                        if (!isFake()) {
                            this.teachingRepository.save(activity);
                        }

                        // Annual hours
                        final var annualHoursMap = new HashMap<TeachingActivityType, Float>();
                        final var annualHoursNode = activityObject.get(ANNUAL_HOURS_KEY);
                        if (annualHoursNode != null) {
                            annualHoursNode.forEach(annualHourNode -> {
                                // Hours
                                final var hours = getFloat(annualHourNode, HOURS_KEY);
                                if (hours == null) {
                                    throw new IllegalArgumentException("Invalid hours for teahing activity with id: " + id); //$NON-NLS-1$
                                }
                                if (hours.floatValue() > 0f) {
                                    // Type
                                    final var type = getEnum(annualHourNode, TYPE_KEY, TeachingActivityType.class);
                                    if (type == null) {
                                        throw new IllegalArgumentException("Invalid activity type for teahing activity with id: " + id); //$NON-NLS-1$
                                    }
                                    //
                                    annualHoursMap.put(type, hours);
                                }
                            });
                        }
                        activity.setAnnualWorkPerType(annualHoursMap);
                        if (!isFake()) {
                            this.teachingRepository.save(activity);
                        }

                        // Ensure that attached files are correct
                        if (fileCallback != null) {
                            var activityChanged = false;
                            if (!Strings.isNullOrEmpty(activity.getPathToSlides())) {
                                final var afn = activity.getPathToSlides();
                                final var fn = fileCallback.teachingActivitySlideFile(activity.getId(), afn, logger);
                                if (!Objects.equals(afn, fn)) {
                                    activity.setPathToSlides(fn);
                                    activityChanged = true;
                                }
                            }
                            if (activityChanged && !isFake()) {
                                this.teachingRepository.save(activity);
                            }
                        }

                        ++nbNew;
                        logger.info("  + " + activity.getCodeOrTitle() //$NON-NLS-1$
                                + " (id: " + activity.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(activity.getId()));
                        }
                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(TEACHING_ACTIVITY_SECTION, i, activityObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the scientific axes in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param axes               the list of scientific axes in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param fileCallback       a tool that is invoked when associated file is detected. It could be {@code null}.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new scientific axes in the database.
     * @throws Exception if a scientific axis cannot be created.
     */
    protected int insertScientificAxes(Session session, JsonNode axes, Map<String, Long> objectIdRepository,
                                       Map<String, Set<String>> aliasRepository, FileCallback fileCallback, Logger logger) throws Exception {
        var nbNew = 0;
        if (axes != null && !axes.isEmpty()) {
            logger.info("Inserting " + axes.size() + " scientific axes..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var axisObject : axes) {
                logger.info("> Scientific axis " + (i + 1) + "/" + axes.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(axisObject);
                    var axis = createObject(ScientificAxis.class, axisObject, aliasRepository, null);
                    if (axis != null) {
                        if (!isFake()) {
                            this.scientificAxisRepository.save(axis);
                        }
                        ++nbNew;
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(axis.getId()));
                        }
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(SCIENTIFIC_AXIS_SECTION, i, axisObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Create the application users in the database.
     *
     * @param session            the JPA session for managing transactions.
     * @param users              the list of application users in the Json source.
     * @param objectIdRepository the mapping from JSON {@code @id} field and the JPA database identifier.
     * @param aliasRepository    the repository of field aliases.
     * @param logger             the logger to use for put a message in the log.
     * @return the number of new users in the database.
     * @throws Exception if an user cannot be created.
     */
    protected int insertApplicationUsers(Session session, JsonNode users, Map<String, Long> objectIdRepository,
                                         Map<String, Set<String>> aliasRepository, Logger logger) throws Exception {
        var nbNew = 0;
        if (users != null && !users.isEmpty()) {
            logger.info("Inserting " + users.size() + " application users..."); //$NON-NLS-1$ //$NON-NLS-2$
            var i = 0;
            for (var userObject : users) {
                logger.info("> User " + (i + 1) + "/" + users.size()); //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    final var id = getId(userObject);
                    var user = createObject(User.class, userObject, aliasRepository, null);
                    if (user != null) {
                        session.beginTransaction();

                        final var personId = getRef(userObject.get(PERSON_KEY));
                        if (Strings.isNullOrEmpty(personId)) {
                            throw new IllegalArgumentException("Invalid person reference for application user with id: " + id); //$NON-NLS-1$
                        }
                        final var personDbId = objectIdRepository.get(personId);
                        if (personDbId == null || personDbId.intValue() == 0) {
                            throw new IllegalArgumentException("Invalid person reference for application user with id: " + id); //$NON-NLS-1$
                        }
                        final var person = this.personRepository.findById(personDbId);
                        if (person.isEmpty()) {
                            throw new IllegalArgumentException("Invalid person reference for application user with id: " + id); //$NON-NLS-1$
                        }
                        user.setPerson(person.get());

                        if (!isFake()) {
                            this.userRepository.save(user);
                        }

                        ++nbNew;
                        logger.info("  + " + user.getLogin() + " (id: " + user.getId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        if (!Strings.isNullOrEmpty(id)) {
                            objectIdRepository.put(id, Long.valueOf(user.getId()));
                        }

                        session.getTransaction().commit();
                    }
                } catch (Throwable ex) {
                    throw new UnableToImportJsonException(APPLICATION_USERS_SECTION, i, userObject, ex);
                }
                ++i;
            }
        }
        return nbNew;
    }

    /**
     * Callback for files that are associated to elements from the JSON.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    public interface FileCallback {

        /**
         * A PDF file was attached to a publication.
         *
         * @param dbId     the identifier of the publication in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         */
        String publicationPdfFile(long dbId, String filename, Logger logger);

        /**
         * An award file was attached to a publication.
         *
         * @param dbId     the identifier of the publication in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         */
        String publicationAwardFile(long dbId, String filename, Logger logger);

        /**
         * A background image file was attached to an address.
         *
         * @param dbId     the identifier of the address in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         */
        String addressBackgroundImageFile(long dbId, String filename, Logger logger);

        /**
         * A logo file was attached to an organization.
         *
         * @param dbId     the identifier of the organization in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.2
         */
        String organizationLogoFile(long dbId, String filename, Logger logger);

        /**
         * A logo file was attached to a project.
         *
         * @param dbId     the identifier of the project in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String projectLogoFile(long dbId, String filename, Logger logger);

        /**
         * An image file was attached to a project.
         *
         * @param dbId     the identifier of the project in the database.
         * @param index    the index of the image in the project.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String projectImageFile(long dbId, int index, String filename, Logger logger);

        /**
         * A scientific requirement file was attached to a project.
         *
         * @param dbId     the identifier of the project in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String projectScientificRequirementsFile(long dbId, String filename, Logger logger);

        /**
         * A press document file was attached to a project.
         *
         * @param dbId     the identifier of the project in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String projectPressDocumentFile(long dbId, String filename, Logger logger);

        /**
         * A PowerPoint file was attached to a project.
         *
         * @param dbId     the identifier of the project in the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String projectPowerpointFile(long dbId, String filename, Logger logger);

        /**
         * A slide file was attached to a teaching activity.
         *
         * @param dbId     the identifier of the teaching activityin the database.
         * @param filename the filename that is specified in the JSON file.
         * @param logger   the logger to be used.
         * @return the fixed filename.
         * @since 3.0
         */
        String teachingActivitySlideFile(long dbId, String filename, Logger logger);

    }

    /**
     * Internal data structure for importing organization memberships.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private record MembershipInfo(Membership membership, String jsonId) {
        //
    }

    /**
     * Internal data structure for importing organization memberships.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private static final class PersonMemberships {

        /**
         * List of organization services.
         */
        final List<MembershipInfo> services = new LinkedList<>();

        /**
         * List of organization employers.
         */
        final List<MembershipInfo> employers = new LinkedList<>();

        /**
         * Constructor.
         */
        PersonMemberships() {
            //
        }

    }

    /**
     * Stats on the import.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 2.0.0
     */
    public static class Stats {

        /**
         * Number of created addresses.
         */
        public final int addresses;

        /**
         * Number of created organizations.
         */
        public final int organizations;

        /**
         * Number of created journals.
         */
        public final int journals;

        /**
         * Number of created conferences.
         */
        public final int conferences;

        /**
         * Number of created persons.
         */
        public final int persons;

        /**
         * Number of created authors.
         */
        public final int authors;

        /**
         * Number of created organization memberships.
         */
        public final int organizationMemberships;

        /**
         * Number of created publications.
         */
        public final int publications;
        /**
         * Number of created jury memberships.
         */
        public final int juryMemberships;
        /**
         * Number of created supervisions.
         */
        public final int supervisions;
        /**
         * Number of created invitations.
         */
        public final int invitations;
        /**
         * Number of created projects.
         *
         * @since 3.0
         */
        public final int projects;
        /**
         * Number of created associated structures.
         *
         * @since 3.2
         */
        public final int associatedStructures;
        /**
         * Number of created teaching activities.
         *
         * @since 3.4
         */
        public final int teachingActivities;
        /**
         * Number of created scientific axes.
         *
         * @since 3.5
         */
        public final int scientificAxes;
        /**
         * Number of created application users.
         *
         * @since 4.0
         */
        public final int applicationUsers;
        /**
         * Number of files that are associated to publications.
         *
         * @since 3.8
         */
        public int publicationAssociatedFiles;

        /**
         * Constructor.
         *
         * @param addresses            the number of created addresses.
         * @param organizations        the number of created organizations.
         * @param journals             the number of created journals.
         * @param conferences          the number of created conferences.
         * @param persons              the number of created persons.
         * @param authors              the number of created authors.
         * @param memberships          the number of created organization memberships.
         * @param publications         the number of created publications.
         * @param juryMemberships      the number of created jury memberships.
         * @param supervisions         the number of supervisions.
         * @param invitations          the number of invitations.
         * @param projects             the number of projects.
         * @param associatedStructures the number of associated structures.
         * @param teachingActivities   the number of teaching activities.
         * @param scientificAxes       the number of scientific axes.
         * @param applicationUsers     the number of application users.
         */
        Stats(int addresses, int organizations, int journals, int conferences, int persons, int authors,
              int memberships, int publications, int juryMemberships, int supervisions, int invitations,
              int projects, int associatedStructures, int teachingActivities, int scientificAxes,
              int applicationUsers) {
            this.addresses = addresses;
            this.organizations = organizations;
            this.journals = journals;
            this.conferences = conferences;
            this.persons = persons;
            this.authors = authors;
            this.organizationMemberships = memberships;
            this.publications = publications;
            this.juryMemberships = juryMemberships;
            this.supervisions = supervisions;
            this.invitations = invitations;
            this.projects = projects;
            this.associatedStructures = associatedStructures;
            this.teachingActivities = teachingActivities;
            this.scientificAxes = scientificAxes;
            this.applicationUsers = applicationUsers;
        }

        /**
         * Constructor.
         */
        Stats() {
            this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        /**
         * Update the number of files that were associated to publications.
         *
         * @param fileCount the number of associated files.
         * @since 3.8
         */
        public void setPublicationAssociatedFileCount(int fileCount) {
            this.publicationAssociatedFiles = fileCount > 0 ? fileCount : 0;
        }

        /**
         * Log the summary of the import on the given logger.
         * This functions outputs the numbers of entities per entity type that were imported
         * into the database.
         *
         * @param logger the receiver of the log messages.
         * @since 3.8
         */
        public void logSummaryOn(Logger logger) {
            logger.info("Summary of imported entities in the database:"); //$NON-NLS-1$
            logger.info(" |-> addresses: " + this.addresses); //$NON-NLS-1$
            logger.info(" |-> organizations: " + this.organizations); //$NON-NLS-1$
            logger.info(" |-> journals: " + this.journals); //$NON-NLS-1$
            logger.info(" |-> conferences: " + this.conferences); //$NON-NLS-1$
            logger.info(" |-> persons: " + this.persons); //$NON-NLS-1$
            if (this.applicationUsers > 0) {
                logger.info(" |    \\-> application users: " + this.applicationUsers); //$NON-NLS-1$
            }
            logger.info(" |-> external authors: " + this.authors); //$NON-NLS-1$
            logger.info(" |-> organization memberships: " + this.organizationMemberships); //$NON-NLS-1$
            logger.info(" |-> publications: " + this.publications); //$NON-NLS-1$
            if (this.publicationAssociatedFiles > 0) {
                logger.info(" |    \\-> associated files: " + this.publicationAssociatedFiles); //$NON-NLS-1$
            }
            logger.info(" |-> jury memberships: " + this.juryMemberships); //$NON-NLS-1$
            logger.info(" |-> person supervisions: " + this.supervisions); //$NON-NLS-1$
            logger.info(" |-> person invitations: " + this.invitations); //$NON-NLS-1$
            logger.info(" |-> projects: " + this.projects); //$NON-NLS-1$
            logger.info(" |-> associated structures: " + this.associatedStructures); //$NON-NLS-1$
            logger.info(" |-> teaching activities: " + this.teachingActivities); //$NON-NLS-1$
            logger.info(" \\-> scientific axes: " + this.scientificAxes); //$NON-NLS-1$
        }

    }

}
