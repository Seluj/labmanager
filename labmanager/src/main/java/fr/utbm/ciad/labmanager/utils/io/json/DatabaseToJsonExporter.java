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
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureRepository;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceRepository;
import fr.utbm.ciad.labmanager.data.indicator.GlobalIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationRepository;
import fr.utbm.ciad.labmanager.data.journal.JournalRepository;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipRepository;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.PersonRepository;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationRepository;
import fr.utbm.ciad.labmanager.data.project.ProjectRepository;
import fr.utbm.ciad.labmanager.data.publication.ConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.JournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationRepository;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisRepository;
import fr.utbm.ciad.labmanager.data.supervision.SupervisionRepository;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityRepository;
import fr.utbm.ciad.labmanager.data.user.UserRepository;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import org.apache.commons.lang3.tuple.Pair;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

/**
 * Exporter of JSON data from the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see DatabaseToZipExporter
 * @since 2.0.0
 */
@Component
public class DatabaseToJsonExporter extends JsonTool {

    private static final long serialVersionUID = 426529408893181686L;

    private final OrganizationAddressRepository addressRepository;

    private final ResearchOrganizationRepository organizationRepository;

    private final PersonRepository personRepository;

    private final MembershipRepository organizationMembershipRepository;

    private final JournalRepository journalRepository;

    private final ConferenceRepository conferenceRepository;

    private final PublicationRepository publicationRepository;

    private final JuryMembershipRepository juryMembershipRepository;

    private final SupervisionRepository supervisionRepository;

    private final PersonInvitationRepository invitationRepository;

    private final GlobalIndicatorsRepository globalIndicatorsRepository;

    private final ProjectRepository projectRepository;

    private final AssociatedStructureRepository structureRepository;

    private final TeachingActivityRepository teachingRepository;

    private final ScientificAxisRepository scientificAxisRepository;

    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param messages                         the accessor to the localized messages.
     * @param addressRepository                the accessor to the organization address repository.
     * @param organizationRepository           the accessor to the organization repository.
     * @param personRepository                 the accessor to the person repository.
     * @param organizationMembershipRepository the accessor to the organization membership repository.
     * @param journalRepository                the accessor to the journal repository.
     * @param conferenceRepository             the accessor to the conference repository.
     * @param publicationRepository            the accessor to the repository of the publications.
     * @param juryMembershipRepository         the accessor to the jury membership repository.
     * @param supervisionRepository            the accessor to the supervision repository.
     * @param invitationRepository             the accessor to the invitation repository.
     * @param globalIndicatorsRepository       the accessor to the global indicators.
     * @param projectRepository                the accessor to the projects.
     * @param structureRepository              the accessor to the associated structures.
     * @param teachingRepository               the accessor to the teaching activities.
     * @param scientificAxisRepository         the accessor to the scientific axes.
     * @param userRepository                   the accessor to the application users.
     */
    public DatabaseToJsonExporter(
            @Autowired MessageSourceAccessor messages,
            @Autowired OrganizationAddressRepository addressRepository,
            @Autowired ResearchOrganizationRepository organizationRepository,
            @Autowired PersonRepository personRepository,
            @Autowired MembershipRepository organizationMembershipRepository,
            @Autowired JournalRepository journalRepository,
            @Autowired ConferenceRepository conferenceRepository,
            @Autowired PublicationRepository publicationRepository,
            @Autowired JuryMembershipRepository juryMembershipRepository,
            @Autowired SupervisionRepository supervisionRepository,
            @Autowired PersonInvitationRepository invitationRepository,
            @Autowired GlobalIndicatorsRepository globalIndicatorsRepository,
            @Autowired ProjectRepository projectRepository,
            @Autowired AssociatedStructureRepository structureRepository,
            @Autowired TeachingActivityRepository teachingRepository,
            @Autowired ScientificAxisRepository scientificAxisRepository,
            @Autowired UserRepository userRepository) {
        super(messages);
        this.addressRepository = addressRepository;
        this.organizationRepository = organizationRepository;
        this.personRepository = personRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.journalRepository = journalRepository;
        this.conferenceRepository = conferenceRepository;
        this.publicationRepository = publicationRepository;
        this.juryMembershipRepository = juryMembershipRepository;
        this.supervisionRepository = supervisionRepository;
        this.invitationRepository = invitationRepository;
        this.globalIndicatorsRepository = globalIndicatorsRepository;
        this.projectRepository = projectRepository;
        this.structureRepository = structureRepository;
        this.teachingRepository = teachingRepository;
        this.scientificAxisRepository = scientificAxisRepository;
        this.userRepository = userRepository;
    }

    /**
     * Run the exporter.
     *
     * @param locale      the locale to use for the messages.
     * @param logger      the logger to use for put a message in the log.
     * @param progression the progression indicator.
     * @return the JSON content or {@code null} if empty.
     * @throws Exception if there is problem for exporting.
     */
    public Map<String, Object> exportFromDatabase(Locale locale, Logger logger, Progression progression) throws Exception {
        return exportFromDatabase(null, null, locale, logger, progression);
    }

    /**
     * Run the exporter.
     *
     * @param similarPublicationProvider a provider of a publication that is similar to a given publication.
     *                                   If this argument is not {@code null} and if it replies a similar publication, the information in this
     *                                   similar publication is used to complete the JSON file that is initially filled up with the source publication.
     * @param extraPublicationProvider   this provider gives publications that must be exported into the JSON that are
     *                                   not directly extracted from the database. If this argument is {@code null}, no extra publication is exported.
     * @param locale                     the locale to use for the messages.
     * @param logger                     the logger to use for put a message in the log.
     * @param progression                the progression indicator.
     * @return the JSON content or {@code null} if empty.
     * @throws Exception if there is problem for exporting.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> exportFromDatabase(SimilarPublicationProvider similarPublicationProvider,
                                                  ExtraPublicationProvider extraPublicationProvider, Locale locale, Logger logger, Progression progression) throws Exception {
        final var progressionInstance = progression == null ? new DefaultProgression() : progression;
        progressionInstance.setProperties(0, 0, 100, false);
        final var mapper = JsonUtils.createMapper();
        final var obj = exportFromDatabaseToJsonObject(mapper.getNodeFactory(), similarPublicationProvider, extraPublicationProvider,
                locale, logger, progressionInstance.subTask(98));
        if (obj != null) {
            final var tree = mapper.treeToValue(obj, Map.class);
            progressionInstance.end();
            return tree;
        }
        progressionInstance.end();
        return null;
    }

    /**
     * Run the exporter for creating JSON objects.
     *
     * @param factory     the factory of nodes.
     * @param locale      the locale to use for the messages.
     * @param logger      the logger to use for put a message in the log.
     * @param progression the progression indicator.
     * @return the JSON content.
     * @throws Exception if there is problem for exporting.
     */
    public JsonNode exportFromDatabaseToJsonObject(JsonNodeCreator factory, Locale locale, Logger logger, Progression progression) throws Exception {
        return exportFromDatabaseToJsonObject(factory, null, null, locale, logger, progression);
    }

    /**
     * Run the exporter for creating JSON objects.
     *
     * @param factory                    the factory of nodes.
     * @param similarPublicationProvider a provider of a publication that is similar to a given publication.
     *                                   If this argument is not {@code null} and if it replies a similar publication, the information in this
     *                                   similar publication is used to complete the JSON file that is initially filled up with the source publication.
     * @param extraPublicationProvider   this provider gives publications that must be exported into the JSON that are
     *                                   not directly extracted from the database. If this argument is {@code null}, no extra publication is exported.
     * @param locale                     the locale to use for the messages.
     * @param logger                     the logger to use for put a message in the log.
     * @param progression                the progression indicator.
     * @return the JSON content.
     * @throws Exception if there is problem for exporting.
     */
    public JsonNode exportFromDatabaseToJsonObject(JsonNodeCreator factory, SimilarPublicationProvider similarPublicationProvider,
                                                   ExtraPublicationProvider extraPublicationProvider, Locale locale, Logger logger, Progression progression) throws Exception {
        final var progressionInstance = progression == null ? new DefaultProgression() : progression;
        progressionInstance.setProperties(0, 0, 16, false, getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.global_indicators", locale)); //$NON-NLS-1$
        final var root = factory.objectNode();
        final var repository = new HashMap<Object, String>();
        exportGlobalIndicators(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.addresses", locale)); //$NON-NLS-1$
        exportAddresses(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.organizations", locale)); //$NON-NLS-1$
        exportOrganizations(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.persons", locale)); //$NON-NLS-1$
        exportPersons(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.memberships", locale)); //$NON-NLS-1$
        exportOrganizationMemberships(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.journals", locale)); //$NON-NLS-1$
        exportJournals(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.conferences", locale)); //$NON-NLS-1$
        exportConferences(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.publications", locale)); //$NON-NLS-1$
        exportPublications(root, repository, similarPublicationProvider, extraPublicationProvider, logger);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.jury_memberships", locale)); //$NON-NLS-1$
        exportJuryMemberships(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.supervisions", locale)); //$NON-NLS-1$
        exportSupervisions(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.invitations", locale)); //$NON-NLS-1$
        exportInvitations(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.projects", locale)); //$NON-NLS-1$
        exportProjects(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.associated_structures", locale)); //$NON-NLS-1$
        exportAssociatedStructures(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.teaching_activities", locale)); //$NON-NLS-1$
        exportTeachingActivities(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.scientific_axes", locale)); //$NON-NLS-1$
        exportScientificAxes(root, repository);
        progressionInstance.increment(getMessageSourceAccessor().getMessage("DatabaseToJsonExporter.exporting.users", locale)); //$NON-NLS-1$
        exportApplicationUsers(root, repository);
        progressionInstance.end();
        if (root.size() > 0) {
            root.set(LAST_CHANGE_FIELDNAME, factory.textNode(LocalDate.now().toString()));
            return root;
        }
        return null;
    }

    /**
     * Export the given object to the receiver.
     *
     * @param receiver    the receiver of JSON.
     * @param id          the identifier.
     * @param object      the object to export.
     * @param factory     the factory of nodes.
     * @param complements the objects that are of the same type of {@code object} but that could be used for obtaining additional data
     *                    that is missed from the original object.
     * @throws Exception if there is problem for exporting.
     */
    @SuppressWarnings("static-method")
    protected void exportObject(JsonNode receiver, String id, Object object, JsonNodeCreator factory, List<?> complements) throws Exception {
        if (object != null) {
            final var rec = (ObjectNode) receiver;
            if (!Strings.isNullOrEmpty(id)) {
                rec.set(ID_FIELDNAME, factory.textNode(id));
            }
            final var meths = findGetterMethods(object.getClass());
            for (final var entry : meths.entrySet()) {
                final var method = entry.getValue();
                var objValue = convertValue(method.invoke(object));
                if (objValue == null && complements != null && !complements.isEmpty()) {
                    final var iterator = complements.iterator();
                    while (iterator.hasNext() && objValue == null) {
                        final var complement = iterator.next();
                        objValue = convertValue(method.invoke(complement));
                    }
                }
                if (objValue instanceof String castValue) {
                    rec.set(entry.getKey(), factory.textNode(castValue));
                } else if (objValue instanceof Byte castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof Short castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof Integer castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof Long castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof Float castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof BigDecimal castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof BigInteger castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue));
                } else if (objValue instanceof Number castValue) {
                    rec.set(entry.getKey(), factory.numberNode(castValue.doubleValue()));
                } else if (objValue instanceof Boolean castValue) {
                    rec.set(entry.getKey(), factory.booleanNode(castValue.booleanValue()));
                } else if (objValue instanceof Character castValue) {
                    rec.set(entry.getKey(), factory.textNode(castValue.toString()));
                }
            }
        }
    }

    /**
     * Export the configuration for global indicatorsto the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportGlobalIndicators(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var indicators = this.globalIndicatorsRepository.findAll();
        if (!indicators.isEmpty()) {
            final var node = root.objectNode();
            final var ind = indicators.get(0);
            final var array = node.arrayNode();
            for (final var key : ind.getVisibleIndicatorKeyList()) {
                array.add(key);
            }
            if (array.size() > 0) {
                node.set(VISIBLEGLOBALINDICATORS_KEY, array);
            }
            if (node.size() > 0) {
                root.set(GLOBALINDICATORS_SECTION, node);
            }
        }
    }

    /**
     * Export the organization addresses to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportAddresses(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var addresses = this.addressRepository.findAll();
        if (!addresses.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            final var nodes = new TreeMap<Long, ObjectNode>();
            for (final var address : addresses) {
                final var jsonAddress = array.objectNode();

                final var id = ORGANIZATIONADDRESS_ID_PREFIX + i;
                exportObject(jsonAddress, id, address, jsonAddress, null);

                if (jsonAddress.size() > 0) {
                    repository.put(address, id);
                    nodes.put(Long.valueOf(address.getId()), jsonAddress);
                    array.add(jsonAddress);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(ORGANIZATIONADDRESSES_SECTION, array);
            }
        }
    }

    /**
     * Export the research organizations to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportOrganizations(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var organizations = this.organizationRepository.findAll();
        if (!organizations.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            final var nodes = new TreeMap<Long, ObjectNode>();
            for (final var organization : organizations) {
                final var jsonOrganization = array.objectNode();

                final var id = RESEARCHORGANIZATION_ID_PREFIX + i;
                exportObject(jsonOrganization, id, organization, jsonOrganization, null);

                if (jsonOrganization.size() > 0) {
                    repository.put(organization, id);
                    nodes.put(Long.valueOf(organization.getId()), jsonOrganization);
                    array.add(jsonOrganization);
                    ++i;
                }
            }
            // Export the addresses of the organization
            for (final var organization : organizations) {
                if (!organization.getAddresses().isEmpty()) {
                    final var jsonAddresses = array.arrayNode();
                    for (final var adr : organization.getAddresses()) {
                        final var id = repository.get(adr);
                        if (Strings.isNullOrEmpty(id)) {
                            throw new IllegalStateException("Address not found: " + adr.getName()); //$NON-NLS-1$
                        }
                        final var ref = createReference(id, jsonAddresses);
                        if (ref != null) {
                            jsonAddresses.add(ref);
                        }
                    }
                    if (!jsonAddresses.isEmpty()) {
                        final var objNode = nodes.get(Long.valueOf(organization.getId()));
                        if (objNode == null) {
                            throw new IllegalStateException("No JSON node created for organization: " + organization.getAcronymOrName()); //$NON-NLS-1$
                        }
                        objNode.set(ADDRESSES_KEY, jsonAddresses);
                    }
                }
            }
            // Export the super organization for enabling the building of the organization hierarchy
            // This loop is externalized to be sure all the organizations are in the repository.
            for (final var organization : organizations) {
                if (!organization.getSuperOrganizations().isEmpty()) {
                    final var objNode = nodes.get(Long.valueOf(organization.getId()));
                    if (objNode == null) {
                        throw new IllegalStateException("No JSON node created for organization: " + organization.getAcronymOrName()); //$NON-NLS-1$
                    }
                    final var superOrgasNode = objNode.arrayNode();
                    for (final var superOrga : organization.getSuperOrganizations()) {
                        final var id = repository.get(superOrga);
                        if (Strings.isNullOrEmpty(id)) {
                            throw new IllegalStateException("Organization not found: " + superOrga.getAcronymOrName()); //$NON-NLS-1$
                        }
                        addReference(superOrgasNode, id);
                    }
                    if (superOrgasNode.size() > 0) {
                        objNode.set(SUPER_ORGANIZATIONS_KEY, superOrgasNode);
                    }
                }
            }
            if (array.size() > 0) {
                root.set(RESEARCHORGANIZATIONS_SECTION, array);
            }
        }
    }

    /**
     * Export the persons to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportPersons(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var persons = this.personRepository.findAll();
        if (!persons.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var person : persons) {
                final var jsonPerson = array.objectNode();

                final var id = PERSON_ID_PREFIX + i;
                exportObject(jsonPerson, id, person, jsonPerson, null);

                // Phone numbers must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                exportPhoneNumber(jsonPerson, OFFICE_PHONE_NUMBER_KEY, person.getOfficePhone());
                exportPhoneNumber(jsonPerson, MOBILE_PHONE_NUMBER_KEY, person.getMobilePhone());

                if (jsonPerson.size() > 0) {
                    repository.put(person, id);
                    array.add(jsonPerson);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(PERSONS_SECTION, array);
            }
        }
    }

    /**
     * Export a phone number.
     *
     * @param receiver the receiver of the phone number.
     * @param key      the name of the JSON key to create.
     * @param number   the phone number to export.
     */
    @SuppressWarnings("static-method")
    protected void exportPhoneNumber(ObjectNode receiver, String key, PhoneNumber number) {
        if (number != null) {
            final var numberNode = receiver.putObject(key);
            final var country = convertValue(number.getCountry());
            if (country != null) {
                numberNode.put(COUNTRY_KEY, country.toString());
            }
            numberNode.put(NUMBER_KEY, number.getLocalNumber());
        }
    }

    /**
     * Export the organization memberships to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportOrganizationMemberships(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var memberships = this.organizationMembershipRepository.findAll();
        if (!memberships.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var membership : memberships) {
                final String adrId;
                if (membership.getOrganizationAddress() != null) {
                    adrId = repository.get(membership.getOrganizationAddress());
                } else {
                    adrId = null;
                }
                final var personId = repository.get(membership.getPerson());
                final var organizationId = repository.get(membership.getDirectResearchOrganization());
                if (!Strings.isNullOrEmpty(personId) && !Strings.isNullOrEmpty(organizationId)) {
                    final var jsonMembership = array.objectNode();

                    final var id = MEMBERSHIP_ID_PREFIX + i;
                    exportObject(jsonMembership, id, membership, jsonMembership, null);

                    // Address, person and organization must be added explicitly because the "exportObject" function
                    // ignore the getter functions for all.
                    if (!Strings.isNullOrEmpty(adrId)) {
                        addReference(jsonMembership, ADDRESS_KEY, adrId);
                    }
                    addReference(jsonMembership, PERSON_KEY, personId);
                    addReference(jsonMembership, RESEARCHORGANIZATION_KEY, organizationId);

                    final var superOrganizationId = repository.get(membership.getSuperResearchOrganization());
                    if (!Strings.isNullOrEmpty(superOrganizationId)) {
                        addReference(jsonMembership, SUPER_ORGANIZATION_KEY, superOrganizationId);
                    }

                    if (jsonMembership.size() > 0) {
                        repository.put(membership, id);
                        array.add(jsonMembership);
                        ++i;
                    }
                }
            }
            if (array.size() > 0) {
                root.set(ORGANIZATION_MEMBERSHIPS_SECTION, array);
            }
        }
    }

    /**
     * Export the journals to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportJournals(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var journals = this.journalRepository.findAll();
        if (!journals.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var journal : journals) {
                final var jsonJournal = array.objectNode();

                final var id = JOURNAL_ID_PREFIX + i;
                exportObject(jsonJournal, id, journal, jsonJournal, null);

                // Add the publication indicators by hand because they are not exported implicitly by
                // the "exportObject" function
                final var indicatorMap = jsonJournal.objectNode();
                for (final var indicators : journal.getQualityIndicators().values()) {
                    final var jsonIndicator = indicatorMap.objectNode();
                    exportObject(jsonIndicator, null, indicators, jsonIndicator, null);
                    // Remove the year because it is not necessary into the JSON map as value and the year is the key.
                    jsonIndicator.remove(REFERENCEYEAR_KEY);
                    if (jsonIndicator.size() > 0) {
                        indicatorMap.set(Integer.toString(indicators.getReferenceYear()), jsonIndicator);
                    }
                }
                if (indicatorMap.size() > 0) {
                    jsonJournal.set(QUALITYINDICATORSHISTORY_KEY, indicatorMap);
                }

                if (jsonJournal.size() > 0) {
                    repository.put(journal, id);
                    array.add(jsonJournal);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(JOURNALS_SECTION, array);
            }
        }
    }

    /**
     * Export the conferences to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportConferences(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var conferences = this.conferenceRepository.findAll();
        if (!conferences.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            final var enclosingConferences = new ArrayList<Pair<ObjectNode, Conference>>();
            for (final var conference : conferences) {
                final var jsonConference = array.objectNode();

                final var id = CONFERENCE_ID_PREFIX + i;
                exportObject(jsonConference, id, conference, jsonConference, null);

                // Add the quality indicators by hand because they are not exported implicitly by
                // the "exportObject" function
                final var indicatorMap = jsonConference.objectNode();
                for (final var indicators : conference.getQualityIndicators().values()) {
                    final var jsonIndicator = indicatorMap.objectNode();
                    exportObject(jsonIndicator, null, indicators, jsonIndicator, null);
                    // Remove the year because it is not necessary into the JSON map as value and the year is the key.
                    jsonIndicator.remove(REFERENCEYEAR_KEY);
                    if (jsonIndicator.size() > 0) {
                        indicatorMap.set(Integer.toString(indicators.getReferenceYear()), jsonIndicator);
                    }
                }
                if (indicatorMap.size() > 0) {
                    jsonConference.set(QUALITYINDICATORSHISTORY_KEY, indicatorMap);
                }

                // Differ the creation of the reference to the enclosing conference to
                // be sure that all the conference nodes are created before creating the links
                if (conference.getEnclosingConference() != null) {
                    enclosingConferences.add(Pair.of(jsonConference, conference.getEnclosingConference()));
                }

                if (jsonConference.size() > 0) {
                    repository.put(conference, id);
                    array.add(jsonConference);
                    ++i;
                }
            }

            // Export the enclosing conferences for enabling the building of the conference hierarchy.
            for (final var pair : enclosingConferences) {
                final var node = pair.getLeft();
                final var enclosingConference = pair.getRight();
                final var id = repository.get(enclosingConference);
                if (Strings.isNullOrEmpty(id)) {
                    throw new IllegalStateException("Conference not found: " + enclosingConference.getAcronymOrName()); //$NON-NLS-1$
                }
                addReference(node, ENCLOSING_CONFERENCE_KEY, id);
            }

            if (array.size() > 0) {
                root.set(CONFERENCES_SECTION, array);
            }
        }
    }

    /**
     * Export the publications to the given JSON root element.
     *
     * @param root                       the receiver of the JSON elements.
     * @param repository                 the repository of elements that maps an object to its JSON id.
     * @param similarPublicationProvider a provider of a publication that is similar to a given publication.
     *                                   If this argument is not {@code null} and if it replies a similar publication, the information in this
     *                                   similar publication is used to complete the JSON file that is initially filled up with the source publication.
     * @param extraPublicationProvider   this provider gives publications that must be exported into the JSON that are
     *                                   not directly extracted from the database. If this argument is {@code null}, no extra publication is exported.
     * @param logger                     the logger to use for put a message in the log.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportPublications(ObjectNode root, Map<Object, String> repository, SimilarPublicationProvider similarPublicationProvider,
                                      ExtraPublicationProvider extraPublicationProvider, Logger logger) throws Exception {
        final var publications = this.publicationRepository.findAll();
        final var array = root.arrayNode();
        int i = 0;
        if (!publications.isEmpty()) {
            for (final var publication : publications) {
                final var jsonPublication = array.objectNode();
                final var id = exportPublication(i, publication, root, jsonPublication, repository, similarPublicationProvider, logger);
                if (jsonPublication.size() > 0) {
                    jsonPublication.set(HIDDEN_INTERNAL_DATA_SOURCE_KEY, jsonPublication.textNode(HIDDEN_INTERNAL_DATABASE_SOURCE_VALUE));
                    repository.put(publication, id);
                    array.add(jsonPublication);
                    ++i;
                }
            }
        }
        logger.info("Exporting " + array.size() + " publications from the database."); //$NON-NLS-1$ //$NON-NLS-2$
        if (extraPublicationProvider != null) {
            for (final var publication : extraPublicationProvider.getPublications()) {
                final var jsonPublication = array.objectNode();
                final var id = exportPublication(i, publication, root, jsonPublication, repository, null, logger);
                if (jsonPublication.size() > 0) {
                    jsonPublication.set(HIDDEN_INTERNAL_DATA_SOURCE_KEY, jsonPublication.textNode(HIDDEN_INTERNAL_EXTERNAL_SOURCE_VALUE));
                    repository.put(publication, id);
                    array.add(jsonPublication);
                    ++i;
                }
            }
            logger.info("Exporting " + extraPublicationProvider.getPublications().size() + " extra publications from the BibTeX."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (array.size() > 0) {
            root.set(PUBLICATIONS_SECTION, array);
        }
    }

    /**
     * Export the publications to the given JSON root element.
     *
     * @param index                      the index of the publication in the list of publications.
     * @param publication                the publication to export.
     * @param root                       the JSON root.
     * @param jsonPublication            the receiver of the JSON elements.
     * @param repository                 the repository of elements that maps an object to its JSON id.
     * @param similarPublicationProvider a provider of a publication that is similar to a given publication.
     *                                   If this argument is not {@code null} and if it replies a similar publication, the information in this
     *                                   similar publication is used to complete the JSON file that is initially filled up with the source publication.
     * @param logger                     the logger to use for put a message in the log.
     * @return the identifier of the exported publication.
     * @throws Exception if there is problem for exporting.
     */
    protected String exportPublication(int index, Publication publication, ObjectNode root, ObjectNode jsonPublication,
                                       Map<Object, String> repository, SimilarPublicationProvider similarPublicationProvider, Logger logger) throws Exception {
        // Add missed information from any similar publication
        final List<Publication> similarPublications;
        if (similarPublicationProvider != null) {
            similarPublications = similarPublicationProvider.get(publication);
            if (similarPublications != null && !similarPublications.isEmpty()) {
                logger.info("Found similar publication(s) for: " + publication.getTitle()); //$NON-NLS-1$
            }
        } else {
            similarPublications = Collections.emptyList();
        }

        final var id = PUBLICATION_ID_PREFIX + index;
        exportObject(jsonPublication, id, publication, jsonPublication, similarPublications);

        // Add the database identifier for information
        if (publication.getId() > 0) {
            jsonPublication.set(DATABASE_ID_FIELDNAME, jsonPublication.numberNode(publication.getId()));
        }

        // Add the authors by hand because they are not exported implicitly by
        // the "exportObject" function.
        // It is due to the reference to person entities.
        final var authorArray = jsonPublication.arrayNode();
        for (final var author : publication.getAuthors()) {
            var authorId = repository.get(author);
            if (Strings.isNullOrEmpty(authorId)) {
                // Author not found in the repository. It is a behavior that may
                // occur when the authors are provided by a BibTeX source and the
                // person is not yet known.
                authorId = createPerson(root, repository, author);
                if (Strings.isNullOrEmpty(authorId)) {
                    // Unexpected behavior. But add the name as text to have it inside the output.
                    authorArray.add(author.getFullName());
                } else {
                    authorArray.add(createReference(authorId, authorArray));
                }
            } else {
                authorArray.add(createReference(authorId, authorArray));
            }
        }
        if (authorArray.size() > 0) {
            jsonPublication.set(AUTHORS_KEY, authorArray);
        }

        // Add the journal by hand because they are not exported implicitly by
        // the "exportObject" function
        // It is due to the reference to journal entities.
        if (publication instanceof JournalBasedPublication jbp) {
            final var journal = jbp.getJournal();
            if (journal != null) {
                final var journalId = repository.get(journal);
                if (Strings.isNullOrEmpty(journalId)) {
                    // Journal not found in the repository. It is an unexpected behavior but
                    // the name of the journal is output to JSON
                    jsonPublication.set(JOURNAL_KEY, jsonPublication.textNode(journal.getJournalName()));
                } else {
                    jsonPublication.set(JOURNAL_KEY, createReference(journalId, jsonPublication));
                }
            }
        }

        // Add the conference by hand because they are not exported implicitly by
        // the "exportObject" function
        // It is due to the reference to journal entities.
        if (publication instanceof ConferenceBasedPublication cbp) {
            final var conference = cbp.getConference();
            if (conference != null) {
                final var conferenceId = repository.get(conference);
                if (Strings.isNullOrEmpty(conferenceId)) {
                    // Conference not found in the repository. It is an unexpected behavior but
                    // the name of the conference is output to JSON
                    jsonPublication.set(CONFERENCE_KEY, jsonPublication.textNode(conference.getName()));
                } else {
                    jsonPublication.set(CONFERENCE_KEY, createReference(conferenceId, jsonPublication));
                }
            }
        }

        return id;
    }

    private String createPerson(ObjectNode root, Map<Object, String> repository, Person person) throws Exception {
        final var personNode = (ArrayNode) root.get(PERSONS_SECTION);
        if (personNode != null) {
            final var jsonPerson = personNode.objectNode();
            final var id = PERSON_ID_PREFIX + personNode.size();
            exportObject(jsonPerson, id, person, jsonPerson, null);
            repository.put(person, id);
            personNode.add(jsonPerson);
            return id;
        }
        return null;
    }

    /**
     * Export the jury memberships to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportJuryMemberships(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var memberships = this.juryMembershipRepository.findAll();
        if (!memberships.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var membership : memberships) {
                final var jsonMembership = array.objectNode();

                final var id = JURY_MEMBERSHIP_ID_PREFIX + i;
                exportObject(jsonMembership, id, membership, jsonMembership, null);

                // Persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                final var personId = repository.get(membership.getPerson());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonMembership, PERSON_KEY, personId);
                }
                final var candidateId = repository.get(membership.getCandidate());
                if (!Strings.isNullOrEmpty(candidateId)) {
                    addReference(jsonMembership, CANDIDATE_KEY, candidateId);
                }
                final var promoterArray = jsonMembership.arrayNode();
                for (final var promoter : membership.getPromoters()) {
                    final var promoterId = repository.get(promoter);
                    promoterArray.add(createReference(promoterId, jsonMembership));
                }
                if (promoterArray.size() > 0) {
                    jsonMembership.set(PROMOTERS_KEY, promoterArray);
                }
                if (jsonMembership.size() > 0) {
                    array.add(jsonMembership);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(JURY_MEMBERSHIPS_SECTION, array);
            }
        }
    }

    /**
     * Export the supervisions to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportSupervisions(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var supervisions = this.supervisionRepository.findAll();
        if (!supervisions.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var supervision : supervisions) {
                final var jsonSupervision = array.objectNode();

                final var id = SUPERVISION_ID_PREFIX + i;
                exportObject(jsonSupervision, id, supervision, jsonSupervision, null);

                // Persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                final var personId = repository.get(supervision.getSupervisedPerson());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonSupervision, PERSON_KEY, personId);
                }
                final var supervisorsArray = jsonSupervision.arrayNode();
                for (final var supervisorDesc : supervision.getSupervisors()) {
                    final var supervisorDescNode = supervisorsArray.objectNode();
                    final var supervisorId = repository.get(supervisorDesc.getSupervisor());
                    supervisorDescNode.set(PERSON_KEY, createReference(supervisorId, supervisorDescNode));
                    supervisorDescNode.set(PERCENT_KEY, supervisorDescNode.numberNode(supervisorDesc.getPercentage()));
                    supervisorDescNode.set(TYPE_KEY, supervisorDescNode.textNode(supervisorDesc.getType().name()));
                    supervisorsArray.add(supervisorDescNode);
                }
                if (supervisorsArray.size() > 0) {
                    jsonSupervision.set(SUPERVISORS_KEY, supervisorsArray);
                }
                if (jsonSupervision.size() > 0) {
                    array.add(jsonSupervision);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(SUPERVISIONS_SECTION, array);
            }
        }
    }

    /**
     * Export the invitations to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportInvitations(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var invitations = this.invitationRepository.findAll();
        if (!invitations.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var invitation : invitations) {
                final var jsonInvitation = array.objectNode();

                final var id = INVITATION_ID_PREFIX + i;
                exportObject(jsonInvitation, id, invitation, jsonInvitation, null);

                // Persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                var personId = repository.get(invitation.getGuest());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonInvitation, GUEST_KEY, personId);
                }
                personId = repository.get(invitation.getInviter());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonInvitation, INVITER_KEY, personId);
                }
                if (jsonInvitation.size() > 0) {
                    array.add(jsonInvitation);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(INVITATIONS_SECTION, array);
            }
        }
    }

    /**
     * Export the projects to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportProjects(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var projects = this.projectRepository.findAll();
        if (!projects.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var project : projects) {
                final var jsonProject = array.objectNode();

                final var id = PROJECT_ID_PREFIX + i;
                exportObject(jsonProject, id, project, jsonProject, null);

                // Organizations and persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                final var budgets = project.getBudgets();
                if (budgets != null && !budgets.isEmpty()) {
                    final var budgetNode = jsonProject.arrayNode();
                    for (final var budget : budgets) {
                        final var node = budgetNode.addObject();
                        node.put(FUNDING_KEY, budget.getFundingScheme().name());
                        if (budget.getBudget() > 0f) {
                            node.put(BUDGET_KEY, Float.valueOf(budget.getBudget()));
                        }
                        if (!Strings.isNullOrEmpty(budget.getFundingReference())) {
                            node.put(GRANT_KEY, budget.getFundingReference());
                        }
                    }
                    if (!budgetNode.isEmpty()) {
                        jsonProject.set(BUDGETS_KEY, budgetNode);
                    }
                }

                final var coordinatorId = repository.get(project.getCoordinator());
                if (!Strings.isNullOrEmpty(coordinatorId)) {
                    addReference(jsonProject, COORDINATOR_KEY, coordinatorId);
                }
                final var localOrganizationId = repository.get(project.getLocalOrganization());
                if (!Strings.isNullOrEmpty(localOrganizationId)) {
                    addReference(jsonProject, LOCAL_ORGANIZATION_KEY, localOrganizationId);
                }
                final var superOrganizationId = repository.get(project.getSuperOrganization());
                if (!Strings.isNullOrEmpty(superOrganizationId)) {
                    addReference(jsonProject, SUPER_ORGANIZATION_KEY, superOrganizationId);
                }
                final var learOrganizationId = repository.get(project.getLearOrganization());
                if (!Strings.isNullOrEmpty(learOrganizationId)) {
                    addReference(jsonProject, LEAR_ORGANIZATION_KEY, learOrganizationId);
                }
                final var otherPartners = project.getOtherPartners();
                if (!otherPartners.isEmpty()) {
                    final var jsonPartners = jsonProject.arrayNode();
                    for (final var partner : otherPartners) {
                        final var partnerId = repository.get(partner);
                        if (!Strings.isNullOrEmpty(partnerId)) {
                            jsonPartners.add(createReference(partnerId, jsonPartners));
                        }
                    }
                    if (!jsonPartners.isEmpty()) {
                        jsonProject.set(OTHER_PARTNERS_KEY, jsonPartners);
                    }
                }
                final var participants = project.getParticipants();
                if (!participants.isEmpty()) {
                    final var jsonParticipants = jsonProject.arrayNode();
                    for (final var participant : participants) {
                        final var participantId = repository.get(participant.getPerson());
                        if (!Strings.isNullOrEmpty(participantId)) {
                            final var jsonParticipant = jsonParticipants.objectNode();
                            jsonParticipant.set(PERSON_KEY, createReference(participantId, jsonParticipant));
                            jsonParticipant.put(ROLE_KEY, participant.getRole().name());
                            jsonParticipants.add(jsonParticipant);
                        }
                    }
                    if (!jsonParticipants.isEmpty()) {
                        jsonProject.set(PARTICIPANTS_KEY, jsonParticipants);
                    }
                }
                exportStringList(jsonProject, VIDEO_URLS_KEY, project.getVideoURLs());
                exportStringList(jsonProject, PATHS_TO_IMAGES_KEY, project.getPathsToImages());
                if (jsonProject.size() > 0) {
                    repository.put(project, id);
                    array.add(jsonProject);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(PROJECTS_SECTION, array);
            }
        }
    }

    /**
     * Export the given list of string into a JSON field.
     *
     * @param receiver  the JSON receiver.
     * @param fieldName the field name.
     * @param data      the data to export.
     */
    @SuppressWarnings("static-method")
    protected void exportStringList(ObjectNode receiver, String fieldName, List<String> data) {
        if (data != null && !data.isEmpty()) {
            final var jsonArray = receiver.arrayNode();
            for (final var element : data) {
                if (!Strings.isNullOrEmpty(element)) {
                    jsonArray.add(element);
                }
            }
            if (!jsonArray.isEmpty()) {
                receiver.set(fieldName, jsonArray);
            }
        }
    }

    /**
     * Export the associated structures to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     * @since 3.2
     */
    protected void exportAssociatedStructures(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var structures = this.structureRepository.findAll();
        if (!structures.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var structure : structures) {
                final var jsonStructure = array.objectNode();

                final var id = ASSOCIATED_STRUCTURE_ID_PREFIX + i;
                exportObject(jsonStructure, id, structure, jsonStructure, null);

                // Several fields must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.

                final var fundingOrganizationId = repository.get(structure.getFundingOrganization());
                if (!Strings.isNullOrEmpty(fundingOrganizationId)) {
                    addReference(jsonStructure, FUNDING_KEY, fundingOrganizationId);
                }

                final var holders = structure.getHolders();
                if (holders != null && !holders.isEmpty()) {
                    final var holderNode = jsonStructure.arrayNode();
                    for (final var holder : holders) {
                        final var node = holderNode.addObject();
                        final var personId = repository.get(holder.getPerson());
                        if (!Strings.isNullOrEmpty(personId)) {
                            addReference(node, PERSON_KEY, personId);
                        }
                        node.put(ROLE_KEY, holder.getRole().name());
                        if (!Strings.isNullOrEmpty(holder.getRoleDescription())) {
                            node.put(ROLE_DESCRIPTION_KEY, holder.getRoleDescription());
                        }
                        final var organizationId = repository.get(holder.getOrganization());
                        if (!Strings.isNullOrEmpty(organizationId)) {
                            addReference(node, ORGANIZATION_KEY, organizationId);
                        }
                        final var superOrganizationId = repository.get(holder.getSuperOrganization());
                        if (!Strings.isNullOrEmpty(superOrganizationId)) {
                            addReference(node, SUPER_ORGANIZATION_KEY, superOrganizationId);
                        }
                    }
                    if (!holderNode.isEmpty()) {
                        jsonStructure.set(HOLDERS_KEY, holderNode);
                    }
                }

                final var projects = structure.getProjects();
                if (projects != null && !projects.isEmpty()) {
                    final var projectNode = jsonStructure.arrayNode();
                    for (final var project : projects) {
                        final String projectId = repository.get(project);
                        addReference(projectNode, projectId);
                    }
                    if (!projectNode.isEmpty()) {
                        jsonStructure.set(PROJECTS_KEY, projectNode);
                    }
                }
                if (jsonStructure.size() > 0) {
                    array.add(jsonStructure);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(ASSOCIATED_STRUCTURES_SECTION, array);
            }
        }
    }

    /**
     * Export the teaching activities to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     * @since 3.4
     */
    protected void exportTeachingActivities(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var activities = this.teachingRepository.findAll();
        if (!activities.isEmpty()) {
            final var array = root.arrayNode();
            int i = 0;
            for (final var activity : activities) {
                final var jsonActivity = array.objectNode();

                final var id = TEACHING_ACTIVITY_ID_PREFIX + i;
                exportObject(jsonActivity, id, activity, jsonActivity, null);

                // Organizations and persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                final var personId = repository.get(activity.getPerson());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonActivity, PERSON_KEY, personId);
                }

                final var universityId = repository.get(activity.getUniversity());
                if (!Strings.isNullOrEmpty(universityId)) {
                    addReference(jsonActivity, UNIVERSITY_KEY, universityId);
                }

                final var annualWorkPerType = activity.getAnnualWorkPerType();
                if (annualWorkPerType != null && !annualWorkPerType.isEmpty()) {
                    final var hoursNode = jsonActivity.arrayNode();
                    for (final var hours : annualWorkPerType.entrySet()) {
                        final var node = hoursNode.addObject();
                        final var value = hours.getValue();
                        if (value != null && value.floatValue() > 0f) {
                            node.put(TYPE_KEY, hours.getKey().name());
                            node.put(HOURS_KEY, value);
                        }
                    }
                    if (!hoursNode.isEmpty()) {
                        jsonActivity.set(ANNUAL_HOURS_KEY, hoursNode);
                    }
                }
                if (jsonActivity.size() > 0) {
                    repository.put(activity, id);
                    array.add(jsonActivity);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(TEACHING_ACTIVITY_SECTION, array);
            }
        }
    }

    /**
     * Export the scientific axes to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     * @since 3.4
     */
    protected void exportScientificAxes(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var axes = this.scientificAxisRepository.findAll();
        if (!axes.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var axis : axes) {
                final var jsonAxis = array.objectNode();

                final var id = SCIENTFIC_AXIS_ID_PREFIX + i;
                exportObject(jsonAxis, id, axis, jsonAxis, null);

                // Other JPA entities must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.

                final var projects = axis.getProjects();
                if (projects != null && !projects.isEmpty()) {
                    final var projectArray = jsonAxis.putArray(PROJECTS_KEY);
                    for (final var project : projects) {
                        final var projectId = repository.get(project);
                        if (!Strings.isNullOrEmpty(projectId)) {
                            addReference(projectArray, projectId);
                        }
                    }
                }

                final var publications = axis.getPublications();
                if (publications != null && !publications.isEmpty()) {
                    final var publicationArray = jsonAxis.putArray(PUBLICATIONS_KEY);
                    for (final var publication : publications) {
                        final var publicationId = repository.get(publication);
                        if (!Strings.isNullOrEmpty(publicationId)) {
                            addReference(publicationArray, publicationId);
                        }
                    }
                }

                final var memberships = axis.getMemberships();
                if (memberships != null && !memberships.isEmpty()) {
                    final var membershipArray = jsonAxis.putArray(MEMBERSHIPS_KEY);
                    for (final var membership : memberships) {
                        final var membershipId = repository.get(membership);
                        if (!Strings.isNullOrEmpty(membershipId)) {
                            addReference(membershipArray, membershipId);
                        }
                    }
                }

                if (jsonAxis.size() > 0) {
                    repository.put(axis, id);
                    array.add(jsonAxis);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(SCIENTIFIC_AXIS_SECTION, array);
            }
        }
    }

    /**
     * Export the application users to the given JSON root element.
     *
     * @param root       the receiver of the JSON elements.
     * @param repository the repository of elements that maps an object to its JSON id.
     * @throws Exception if there is problem for exporting.
     */
    protected void exportApplicationUsers(ObjectNode root, Map<Object, String> repository) throws Exception {
        final var users = this.userRepository.findAll();
        if (!users.isEmpty()) {
            final var array = root.arrayNode();
            var i = 0;
            for (final var user : users) {
                final var jsonUser = array.objectNode();

                final var id = APPLICATION_USER_ID_PREFIX + i;
                exportObject(jsonUser, id, user, jsonUser, null);

                // Persons must be added explicitly because the "exportObject" function
                // ignore the getter functions for all.
                final var personId = repository.get(user.getPerson());
                if (!Strings.isNullOrEmpty(personId)) {
                    addReference(jsonUser, PERSON_KEY, personId);
                }

                if (jsonUser.size() > 0) {
                    repository.put(user, id);
                    array.add(jsonUser);
                    ++i;
                }
            }
            if (array.size() > 0) {
                root.set(APPLICATION_USERS_SECTION, array);
            }
        }
    }

}
