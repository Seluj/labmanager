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

package fr.utbm.ciad.labmanager.services.member;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntityComparator;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.publication.AuthorshipRepository;
import fr.utbm.ciad.labmanager.data.publication.PublicationRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.io.gscholar.GoogleScholarPlatform;
import fr.utbm.ciad.labmanager.utils.io.gscholar.GoogleScholarPlatform.GoogleScholarPerson;
import fr.utbm.ciad.labmanager.utils.io.scopus.ScopusPlatform;
import fr.utbm.ciad.labmanager.utils.io.scopus.ScopusPlatform.ScopusPerson;
import fr.utbm.ciad.labmanager.utils.io.wos.WebOfSciencePlatform;
import fr.utbm.ciad.labmanager.utils.io.wos.WebOfSciencePlatform.WebOfSciencePerson;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import org.apache.commons.lang3.mutable.MutableInt;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for managing the persons.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class PersonService extends AbstractEntityService<Person> {

	private static final long serialVersionUID = 6762189546771063987L;

	private PublicationRepository publicationRepository;

	private AuthorshipRepository authorshipRepository;

	private PersonRepository personRepository;

	private PersonNameParser nameParser;

	private GoogleScholarPlatform googlePlatform;

	private ScopusPlatform scopusPlatform;

	private WebOfSciencePlatform wosPlatform;

	private PersonNameComparator personNameComparator;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param publicationRepository the publication repository.
	 * @param authorshipRepository the authorship repository.
	 * @param personRepository the person repository.
	 * @param googlePlatfom the tool for accessing the remote Google Scholar platform.
	 * @param scopusPlatfom the tool for accessing the remote Scopus platform.
	 * @param wosPlatfom the tool for accessing the remote WoS platform.
	 * @param nameParser the parser of person names.
	 * @param personNameComparator the comparator of person names.
	 * @param structureService the service for accessing the associated structures.
	 * @param invitationService the service for accessing the person invitations.
	 * @param juryMembershipService the service for accessing the jury memberships.
	 * @param membershipService the service for accessing the organization memberships.
	 * @param projectService the service for accessing the projects.
	 * @param supervisionService the service for accessing the student supervisions.
	 * @param teachingService the service for accessing the teaching activities.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public PersonService(
			@Autowired PublicationRepository publicationRepository,
			@Autowired AuthorshipRepository authorshipRepository,
			@Autowired PersonRepository personRepository,
			@Autowired GoogleScholarPlatform googlePlatfom,
			@Autowired ScopusPlatform scopusPlatfom,
			@Autowired WebOfSciencePlatform wosPlatfom,
			@Autowired PersonNameParser nameParser,
			@Autowired PersonNameComparator personNameComparator,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.publicationRepository = publicationRepository;
		this.authorshipRepository = authorshipRepository;
		this.personRepository = personRepository;
		this.googlePlatform = googlePlatfom;
		this.scopusPlatform = scopusPlatfom;
		this.wosPlatform = wosPlatfom;
		this.nameParser = nameParser;
		this.personNameComparator = personNameComparator;
	}

	/** Replies the number of persons who are inside the database, whatever their organization.
	 *
	 * @return the total number of persons.
	 * @since 4.0
	 */
	public long countAllPersons() {
		return this.personRepository.count();
	}

	/** Replies the number of persons who are inside the database, and who are fitting the given filter.
	 *
	 * @param filter the filter to apply for counting.
	 * @return the total number of persons.
	 * @since 4.0
	 */
	public long countAllPersons(Specification<Person> filter) {
		return this.personRepository.count(filter);
	}

	/** Replies the name parser that is used by this service.
	 *
	 * @return the name parser.
	 * @since 4.0
	 */
	public PersonNameParser getNameParser() {
		return this.nameParser;
	}

	/** Replies the list of all the persons from the database.
	 *
	 * @return all the persons.
	 */
	public List<Person> getAllPersons() {
		return this.personRepository.findAll();
	}

	/** Replies the list of all the persons from the database.
	 *
	 * @param filter the filter of persons.
	 * @return all the persons.
	 * @since 4.0
	 */
	public List<Person> getAllPersons(Specification<Person> filter) {
		return this.personRepository.findAll(filter);
	}

	/** Replies the list of all the persons from the database.
	 *
	 * @param pageable the manager of pages.
	 * @return all the persons.
	 * @since 4.0
	 */
	public Page<Person> getAllPersons(Pageable pageable) {
		return this.personRepository.findAll(pageable);
	}

	/** Replies the list of all the persons from the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of persons.
	 * @return all the persons.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public Page<Person> getAllPersons(Pageable pageable, Specification<Person> filter) {
		return this.personRepository.findAll(filter, pageable);
	}

	/** Replies the list of all the persons from the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of persons.
	 * @param initializer the initializer for each loaded person.
	 * @return all the persons.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public Page<Person> getAllPersons(Pageable pageable, Specification<Person> filter, Consumer<Person> initializer) {
		final var page = this.personRepository.findAll(filter, pageable);
		if (initializer != null) {
			page.forEach(initializer);
		}
		return page;
	}

	/** Replies the person with the given identifier.
	 *
	 * @param identifier the identifier of the person.
	 * @return the person, or {@code null} if none.
	 */
	public Person getPersonById(long identifier) {
		final var byId = this.personRepository.findById(Long.valueOf(identifier));
		return byId.orElse(null);
	}

	/** Replies the person with the given webpage identifier.
	 *
	 * @param identifier the identifier of the webpage of the person.
	 * @return the person, or {@code null} if none.
	 */
	public Person getPersonByWebPageId(String identifier) {
		final var byId = this.personRepository.findDistinctByWebPageId(identifier);
		return byId.orElse(null);
	}

	/** Create a person in the database by providing only the name of the person.
	 *
	 * @param logger the logger to be used for printing out the messages in the app log.
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the person in the database.
	 * @see #createPerson(String, String, Gender, String, String, String, String, String, String, String, String, String, String, String, String, String, WebPageNaming, int, int)
	 */
	public Person createPerson(Logger logger, String firstName, String lastName) {
		return createPerson(logger,
				false,
				firstName, lastName,
				Gender.NOT_SPECIFIED,
				null, // primary email
				null, // secondary email
				null, // office phone
				null, // mobile phone
				null, // office room
				null, // Gravatar Id
				null, // Orcid
				null, // ResearcherId
				null, // AD Scientific Index
				null, // Scopus Id
				null, // Google Scholar
				null, // Hal Id
				null, // Linked-In Id
				null, // Github Id
				null, // Research Gate Id
				null, // Facebook Id
				null, // DBLP URL
				null, // Academia.edu URL
				null, // EU Cordis URL
				WebPageNaming.UNSPECIFIED,
				0, // Google Scholar H-index
				0, // WoS H-index
				0, // Scopus H-index
				0, // Google Scholar citations
				0, // WoS citations
				0); // Scopus citations
	}

	/** Create a person in the database.
	 *
	 * @param logger the logger to be used for printing out the messages in the app log.
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @param gender the gender.
	 * @param primaryEmail the primary email of the person.
	 * @param secondaryEmail the secondary email of the person.
	 * @param officePhone the phone number at office.
	 * @param mobilePhone the mobile phone number.
	 * @param officeRoom the number of the office room.
	 * @param gravatarId the identifier for obtaining a photo on Gravatar.
	 * @param orcid the ORCID of the person.
	 * @param researcherId the identifier of the person on ResearchId/WOS/Publon.
	 * @param scopusId the identifier of the person on Scopus.
	 * @param scholarId the identifier of the person on Google Scholar.
	 * @param idhal the identifier of the person on HAL.
	 * @param linkedInId the identifier of the person on LinkedIn.
	 * @param githubId the identifier of the person on Github.
	 * @param researchGateId the identifier of the person on ResearchGate.
	 * @param adScientificIndexId the identifier of the person on AD Scientific Index.
	 * @param facebookId the identifier of the person on Facebook.
	 * @param dblpURL the URL of the person's page on DBLP.
	 * @param academiaURL the URL of the person's page on Academia.edu.
	 * @param cordisURL the URL of the person's page on European Commission's Cordis.
	 * @param webPageNaming the type of naming for the person's webpage on the organization server.
	 * @param scholarHindex the Hindex of the person on Google Scholar.
	 * @param wosHindex the Hindex of the person on WOS.
	 * @param scopusHindex the Hindex of the person on Scopus.
	 * @param scholarCitations the number of citations for the person on Google Scholar.
	 * @param wosCitations the number of citations for the person on ResearchId/WOS/Publon.
	 * @param scopusCitations the number of citations for the person on Scopus.
	 * @return the person in the database.
	 * @see #createPerson(String, String)
	 * @see Gender
	 * @since 4.0
	 */
	public Person createPerson(Logger logger, boolean validated, String firstName, String lastName, Gender gender, String primaryEmail, String secondaryEmail, PhoneNumber officePhone,
			PhoneNumber mobilePhone, String officeRoom, String gravatarId, String orcid, String researcherId, String scopusId,
			String scholarId, String idhal, String linkedInId, String githubId, String researchGateId, String adScientificIndexId, String facebookId,
			String dblpURL, String academiaURL, String cordisURL, WebPageNaming webPageNaming,
			int scholarHindex, int wosHindex, int scopusHindex, int scholarCitations, int wosCitations, int scopusCitations) {
		final var res = new Person();
		res.setFirstName(firstName);
		res.setLastName(lastName);
		res.setGender(gender);
		res.setPrimaryEmail(primaryEmail);
		res.setSecondaryEmail(secondaryEmail);
		res.setOfficePhone(officePhone);
		res.setMobilePhone(mobilePhone);
		res.setOfficeRoom(officeRoom);
		res.setGravatarId(gravatarId);
		res.setORCID(orcid);
		res.setResearcherId(researcherId);
		res.setScopusId(scopusId);
		res.setGoogleScholarId(scholarId);
		res.setIdhal(idhal);
		res.setLinkedInId(linkedInId);
		res.setGithubId(githubId);
		res.setResearchGateId(researchGateId);
		res.setAdScientificIndexId(adScientificIndexId);
		res.setFacebookId(facebookId);
		res.setDblpURL(dblpURL);
		res.setAcademiaURL(academiaURL);
		res.setCordisURL(cordisURL);
		res.setWebPageNaming(webPageNaming);
		res.setGoogleScholarHindex(scholarHindex);
		res.setWosHindex(wosHindex);
		res.setScopusHindex(scopusHindex);
		res.setGoogleScholarCitations(scholarCitations);
		res.setWosCitations(wosCitations);
		res.setScopusCitations(scopusCitations);
		res.setValidated(validated);
		this.personRepository.save(res);
		logger.info("Created person in database: " + res); //$NON-NLS-1$
		return res;
	}

	/** Download the person indicators from the WoS platform.
	 * This function uses the {@link WosPlatform} tool for downloading the indicators.
	 *
	 * @param persons the list of persons for who the indicators should be downloaded.
	 * @param logger the logger to be used.
	 * @param progress the progression monitor.
	 * @param consumer the consumer of the person ranking information.
	 * @throws Exception if the person information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public void downloadPersonIndicatorsFromWoS(List<Person> persons, Logger logger,
			Progression progress, PersonRankingConsumer consumer) {
		final var progress0 = progress == null ? new DefaultProgression() : progress; 
		progress0.setProperties(0, 0, persons.size() * 2, false);
		for (final var person : persons) {
			logger.info("Downloading the person's ranking indicators from WoS for: " + person); //$NON-NLS-1$
			progress0.setComment(person.getFullName());
			final var wosURL = person.getResearcherIdURL();
			if (wosURL != null) {
				final var lastHindex = person.getWosHindex();
				final var lastCitations = person.getWosCitations();
				WebOfSciencePerson rankings = null;
				try {
					rankings = this.wosPlatform.getPersonRanking(wosURL, progress0.subTask(1));
				} catch (Throwable ex) {
					rankings = null;
				}
				progress0.ensureNoSubTask();
				if (rankings != null) {
					final var currentHindex = rankings.hindex;
					final var currentCitations = rankings.citations;
					consumer.consume(person.getId(), lastHindex, currentHindex, lastCitations, currentCitations);
				} else {
					consumer.consume(person.getId(), lastHindex, 0, lastCitations, 0);
				}
				progress0.increment();
			} else {
				progress0.subTask(2);
			}
		}
		progress0.end();
	}

	/** Download the person indicators from the Scopus platform.
	 * This function uses the {@link ScopusPlatform} tool for downloading the indicators.
	 *
	 * @param persons the list of persons for who the indicators should be downloaded.
	 * @param logger the logger to be used.
	 * @param progress the progression monitor.
	 * @param consumer the consumer of the person ranking information.
	 * @throws Exception if the person information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public void downloadPersonIndicatorsFromScopus(List<Person> persons, Logger logger,
			Progression progress, PersonRankingConsumer consumer) {
		final var progress0 = progress == null ? new DefaultProgression() : progress; 
		progress0.setProperties(0, 0, persons.size() * 2, false);
		for (final var person : persons) {
			logger.info("Downloading the person's ranking indicators from Scopus for: " + person); //$NON-NLS-1$
			progress0.setComment(person.getFullName());
			final var scopusId = person.getScopusId();
			if (!Strings.isNullOrEmpty(scopusId)) {
				final var lastHindex = person.getScopusHindex();
				final var lastCitations = person.getScopusCitations();
				ScopusPerson rankings = null;
				try {
					rankings = this.scopusPlatform.getPersonRanking(scopusId, progress0.subTask(1));
				} catch (Throwable ex) {
					rankings = null;
				}
				progress0.ensureNoSubTask();
				if (rankings != null) {
					final var currentHindex = rankings.hindex;
					final var currentCitations = rankings.citations;
					consumer.consume(person.getId(), lastHindex, currentHindex, lastCitations, currentCitations);
				} else {
					consumer.consume(person.getId(), lastHindex, 0, lastCitations, 0);
				}
				progress0.increment();
			} else {
				progress0.subTask(2);
			}
		}
		progress0.end();
	}

	/** Download the person indicators from the Google Scholar platform.
	 * This function uses the {@link GoogleScholarPlatform} tool for downloading the indicators.
	 *
	 * @param persons the list of persons for who the indicators should be downloaded.
	 * @param logger the logger to be used.
	 * @param progress the progression monitor.
	 * @param consumer the consumer of the person ranking information.
	 * @throws Exception if the person information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public void downloadPersonIndicatorsFromGoogleScholar(List<Person> persons, Logger logger,
			Progression progress, PersonRankingConsumer consumer) {
		final var progress0 = progress == null ? new DefaultProgression() : progress; 
		progress0.setProperties(0, 0, persons.size() * 2, false);
		for (final var person : persons) {
			logger.info("Downloading the person's ranking indicators from Gogle Scholar for: " + person); //$NON-NLS-1$
			progress0.setComment(person.getFullName());
			final var gsURL = person.getGoogleScholarURL();
			if (gsURL != null) {
				final var lastHindex = person.getGoogleScholarHindex();
				final var lastCitations = person.getGoogleScholarCitations();
				GoogleScholarPerson rankings = null;
				try {
					rankings = this.googlePlatform.getPersonRanking(gsURL, progress0.subTask(1));
				} catch (Throwable ex) {
					rankings = null;
				}
				progress0.ensureNoSubTask();
				if (rankings != null) {
					final var currentHindex = rankings.hindex;
					final var currentCitations = rankings.citations;
					consumer.consume(person.getId(), lastHindex, currentHindex, lastCitations, currentCitations);
				} else {
					consumer.consume(person.getId(), lastHindex, 0, lastCitations, 0);
				}
				progress0.increment();
			} else {
				progress0.subTask(2);
			}
		}
		progress0.end();
	}

	/** Update the persons indicators according to the given inputs.
	 *
	 * @param personUpdates the streams that describes the updates.
	 * @param updateWos indicates if the Web-of-Science indicators are updated.
	 * @param updateScopus indicates if the Scopus indicators are updated.
	 * @param updateGoogleScholar indicates if the Google Scholar are updated.
	 * @param logger the logger to be used.
	 * @param progress the progression monitor.
	 * @throws Exception if the journal information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional
	public void updatePersonIndicators(Collection<PersonRankingUpdateInformation> personUpdates, boolean updateWos, boolean updateScopus,
			boolean updateGoogleScholar, Logger logger, Progression progress) {
		progress.setProperties(0, 0, personUpdates.size() + 1, false);
		final var persons = new ArrayList<Person>();
		personUpdates.forEach(info -> {
			final var person = info.person();
			logger.info("Updating the person's ranking indicators in database for: " + person); //$NON-NLS-1$
			progress.setComment(person.getFullName());
			if (updateWos) {
				if (info.wosHindex() != null) {
					person.setWosHindex(info.wosHindex().intValue());
				}
				if (info.wosCitations() != null) {
					person.setWosCitations(info.wosCitations().intValue());
				}
			}
			if (updateScopus) {
				if (info.scopusHindex() != null) {
					person.setScopusHindex(info.scopusHindex().intValue());
				}
				if (info.scopusCitations() != null) {
					person.setScopusCitations(info.scopusCitations().intValue());
				}
			}
			if (updateGoogleScholar) {
				if (info.googleScholarHindex() != null) {
					person.setGoogleScholarHindex(info.googleScholarHindex().intValue());
				}
				if (info.googleScholarCitations() != null) {
					person.setGoogleScholarCitations(info.googleScholarCitations().intValue());
				}
			}
			persons.add(person);
			progress.increment();
		});
		this.personRepository.saveAll(persons);
		progress.end();
	}

	/** Change the attributes of a person in the database.
	 *
	 * @param identifier the identifier of the person in the database.
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @param gender the gender.
	 * @param email the email of the person.
	 * @param officePhone the phone number at office.
	 * @param mobilePhone the mobile phone number.
	 * @param officeRoom the number of the office room.
	 * @param gravatarId the identifier for obtaining a photo on Gravatar.
	 * @param orcid the ORCID of the person.
	 * @param researcherId the identifier of the person on ResearchId/WOS/Publon.
	 * @param scopusId the identifier of the person on Scopus.
	 * @param scholarId the identifier of the person on Google Scholar.
	 * @param idhal the identifier of the person on HAL.
	 * @param linkedInId the identifier of the person on LinkedIn.
	 * @param githubId the identifier of the person on Github.
	 * @param researchGateId the identifier of the person on ResearchGate.
	 * @param adScientificIndexId the identifier of the person on AD Scientific Index.
	 * @param facebookId the identifier of the person on Facebook.
	 * @param dblpURL the URL of the person's page on DBLP.
	 * @param academiaURL the URL of the person's page on Academia.edu.
	 * @param cordisURL the URL of the person's page on European Commission's Cordis.
	 * @param webPageNaming the type of naming for the person's webpage on the organization server.
	 * @param scholarHindex the Hindex of the person on Google Scholar.
	 * @param wosHindex the Hindex of the person on WOS.
	 * @param scopusHindex the Hindex of the person on Scopus.
	 * @param scholarCitations the number of citations for the person on Google Scholar.
	 * @param wosCitations the number of citations for the person on ResearchId/WOS/Publon.
	 * @param scopusCitations the number of citations for the person on Scopus.
	 * @return the updated person.
	 * @Deprecated no replacement.
	 */
	public Person updatePerson(long identifier, boolean validated, String firstName, String lastName, Gender gender, String email, PhoneNumber officePhone,
			PhoneNumber mobilePhone, String officeRoom, String gravatarId, String orcid, String researcherId, String scopusId, String scholarId,
			String idhal, String linkedInId, String githubId, String researchGateId, String adScientificIndexId, String facebookId, String dblpURL,
			String academiaURL, String cordisURL, WebPageNaming webPageNaming, int scholarHindex, int wosHindex, int scopusHindex,
			int scholarCitations, int wosCitations, int scopusCitations) {
		final var res = this.personRepository.findById(Long.valueOf(identifier));
		if (res.isPresent()) {
			final var person = res.get();
			if (!Strings.isNullOrEmpty(firstName)) {
				person.setFirstName(firstName);
			}
			if (!Strings.isNullOrEmpty(lastName)) {
				person.setLastName(lastName);
			}
			person.setGender(gender);
			person.setPrimaryEmail(email);
			person.setOfficePhone(officePhone);
			person.setMobilePhone(mobilePhone);
			person.setOfficeRoom(officeRoom);
			person.setGravatarId(gravatarId);
			person.setORCID(orcid);
			person.setResearcherId(researcherId);
			person.setScopusId(scopusId);
			person.setGoogleScholarId(scholarId);
			person.setIdhal(idhal);
			person.setLinkedInId(linkedInId);
			person.setGithubId(githubId);
			person.setResearchGateId(researchGateId);
			person.setAdScientificIndexId(adScientificIndexId);
			person.setFacebookId(facebookId);
			person.setDblpURL(dblpURL);
			person.setAcademiaURL(academiaURL);
			person.setCordisURL(cordisURL);
			person.setWebPageNaming(webPageNaming);
			person.setGoogleScholarHindex(scholarHindex);
			person.setWosHindex(wosHindex);
			person.setScopusHindex(scopusHindex);
			person.setGoogleScholarCitations(scholarCitations);
			person.setWosCitations(wosCitations);
			person.setScopusCitations(scopusCitations);
			person.setValidated(validated);
			this.personRepository.save(person);
			return person;
		}
		return null;
	}

	/** Remove the person with the given identifier from the database.
	 *
	 * @param identifier the identifier of the person in the database.
	 * @return the removed person, disconnected from the JPA infrastructure; or {@code null} if
	 *     the identifier does not correspond to a person.
	 * @Deprecated no replacement.
	 */
	@Transactional
	public Person removePerson(long identifier) {
		final var id = Long.valueOf(identifier);
		final var optPerson = this.personRepository.findById(id);
		if (optPerson.isPresent()) {
			final var person = optPerson.get();
			for (final var authorship : person.getAuthorships()) {
				final var rank = authorship.getAuthorRank();
				final var publication = authorship.getPublication();
				authorship.setPerson(null);
				authorship.setPublication(null);
				//When removing an author, reduce the ranks of the other authorships for the pubs he made.
				final var iterator = publication.getAuthorshipsRaw().iterator();
				while (iterator.hasNext()) {
					final var otherAuthorsip = iterator.next();
					if (authorship.getId() != otherAuthorsip.getId()) {
						final var oRank = otherAuthorsip.getAuthorRank();
						if (oRank > rank) {
							otherAuthorsip.setAuthorRank(oRank - 1);
							this.authorshipRepository.save(otherAuthorsip);
						}
					} else {
						iterator.remove();
					}
				}
				this.publicationRepository.save(publication);
			}
			person.deleteAllAuthorships();
			this.personRepository.save(person);
			this.personRepository.deleteById(id);
			return person;
		}
		return null;
	}

	/** Replies the database identifier for the person with the last name and first name.
	 * If there is multiple persons with the same last name and first name, one is replied.
	 * <p>The name matching is exact, i.e., the equality test is used for selecting the persons.
	 * For using a similarity test on the names, see {@link #getPersonIdBySimilarName(String, String)}.
	 *
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the identifier, or {@code 0} if no person has the given name.
	 * @see #getPersonIdBySimilarName(String, String)
	 */
	public long getPersonIdByName(String firstName, String lastName) {
		final var res = this.personRepository.findByFirstNameAndLastName(firstName, lastName);
		if (!res.isEmpty()) {
			return res.iterator().next().getId();
		}
		return 0;
	}

	/** Replies the database identifier for the person with a similar name to the givan last name and givan first name.
	 * If there is multiple persons with similar last name and first name, one is replied.
	 * <p>The name matching is based on similarity of names.
	 * For using a strict equality test on the names, see {@link #getPersonIdByName(String, String)}.
	 *
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the identifier, or {@code 0} if no person has the given name.
	 * @see #getPersonIdByName(String, String)
	 */
	public long getPersonIdBySimilarName(String firstName, String lastName) {
		return getPersonIdBySimilarName(null, firstName, lastName);
	}

	private long getPersonIdBySimilarName(List<Person> allPersons, String firstName, String lastName) {
		final var person = getPersonBySimilarName(allPersons, firstName, lastName);
		if (person != null) {
			return person.getId();
		}
		return 0;
	}

	/** Replies the person with a similar name to the givan last name and givan first name.
	 * If there is multiple persons with similar last name and first name, one is replied.
	 * <p>The name matching is based on similarity of names.
	 * For using a strict equality test on the names, see {@link #getPersonIdByName(String, String)}.
	 *
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the person, or {@code null} if no person has the given name.
	 * @see #getPersonIdBySimilarName(String, String)
	 */
	public Person getPersonBySimilarName(String firstName, String lastName) {
		return getPersonBySimilarName(null, firstName, lastName);
	}

	private Person getPersonBySimilarName(List<Person> allPersons, String firstName, String lastName) {
		if (!Strings.isNullOrEmpty(firstName) || !Strings.isNullOrEmpty(lastName)) {
			final var list = allPersons == null ? this.personRepository.findAll() : allPersons;
			for (final var person : list) {
				if (this.personNameComparator.isSimilar(firstName, lastName, person.getFirstName(), person.getLastName())) {
					return person;
				}
			}
		}
		return null;
	}

	/** Extract the list of the authors.
	 * <p>The format of the list of authors follows the rules of {@link PersonNameParser}.
	 * <p>The returned authors are not saved into the database. It means that if the given
	 * list of authors contains a known author, this person is read from the database.
	 * If the author is unknown, a {@link Person} object is created but not saved into the
	 * database.
	 * 
	 * @param authorText the list of authors to parse.
	 * @param useNameSimilarity indicates of the member search from their name is based on similar names, if {@code true};
	 *     or on exact names, if {@code false}.
	 * @param assignRandomId indicates if a random identifier will be assigned to the created entities.
	 *     If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
	 *     If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
	 * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
	 *     the list of the persons. If {@code false}, the list of persons could contain no organization member.
	 * @return the list of authors.
	 * @see #containsAMember(List)
	 */
	public List<Person> extractPersonsFrom(String authorText, boolean useNameSimilarity, boolean assignRandomId, boolean ensureAtLeastOneMember) {
		final var memberCount = new MutableInt();
		final var persons = new ArrayList<Person>();
		final List<Person> allPersons;
		if (useNameSimilarity) {
			allPersons = this.personRepository.findAll();
		} else {
			allPersons = null;
		}
		this.nameParser.parseNames(authorText, (fn, von, ln, pos) -> {
			// Build last name
			final var firstname = new StringBuilder();
			firstname.append(fn);
			if (!Strings.isNullOrEmpty(von)) {
				firstname.append(" "); //$NON-NLS-1$
				firstname.append(von);
			}
			//
			final long id;
			if (useNameSimilarity) {
				id = getPersonIdBySimilarName(allPersons, firstname.toString(), ln);
			} else {
				id = getPersonIdByName(firstname.toString(), ln);
			}
			Person person = null;
			if (id != 0) {
				person = getPersonById(id);
			}
			if (person == null) {
				person = new Person();
				person.setFirstName(firstname.toString());
				person.setLastName(ln);
				if (assignRandomId) {
					person.setId(generateUUID().intValue());
				}
			} else {
				memberCount.increment();
			}
			persons.add(person);
		});
		if (ensureAtLeastOneMember && memberCount.intValue() <= 0) {
			throw new IllegalArgumentException("The list of the authors does not contain a member of a known research organization."); //$NON-NLS-1$
		}
		return persons;
	}

	/** Replies if the given list of authors contains at least one person who is associated to a research organization,
	 * i.e., with a membership. The name similarity is used for searching the members based on their names.
	 *
	 * @param authors the list of authors. It is a list of database identifiers (for known persons) and full name
	 *     (for unknown persons).
	 * @param useNameSimilarity indicates of the member search from their name is based on similar names, if {@code true};
	 *     or on exact names, if {@code false}.
	 * @return {@code true} if one person with a membership was found.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean containsAMember(List<String> authors, boolean useNameSimilarity) {
		final var idPattern = Pattern.compile("\\d+"); //$NON-NLS-1$
		try {
			for (final var author : authors) {
				if (author != null) {
					var authorId = 0l;
					if (idPattern.matcher(author).matches()) {
						// Numeric value means that the person is known.
						try {
							authorId = Integer.parseInt(author);
						} catch (Throwable ex) {
							// Silent
						}
					}
					if (authorId == 0) {
						// The author seems to be not in the database already. Check it based on the name.
						final var firstName = this.nameParser.parseFirstName(author);
						final var lastName = this.nameParser.parseLastName(author);
						if (useNameSimilarity) {
							authorId = getPersonIdBySimilarName(firstName, lastName);
						} else {
							authorId = getPersonIdByName(firstName, lastName);
						}
					}
					if (authorId != 0) {
						// Check if the given author identifier corresponds to a known person with memberships.
						final var optPers = this.personRepository.findById(Long.valueOf(authorId));
						if (optPers.isPresent() && !optPers.get().getMemberships().isEmpty()) {
							throw new SuccessException();
						}
					}
				}
			}
		} catch (SuccessException ex) {
			return true;
		}
		return false;
	}

	/** Compute and replies the updates of the ranking indicators for all the persons in the given organization.
	 *
	 * <p>CAUTION: The person ranking is considered only if the person is already associated to a positive H-index or
	 * number of citations.
	 *
	 * @param organization the organization in which the persons to be considered are involved.
	 * @param locale the locale to use.
	 * @param progression the progression indicator. 
	 * @param callback the callback for consuming the the map that maps persons to the maps of the updates.
	 *      THe keys are: {@code id}, {@code name}, {@code wosHindex}, {@code currentWosHindex},
	 *      {@code scopusHindex}, {@code currentScopusHindex}, {@code scholarHindex}, {@code currentScholarHindex}.
	 * @throws Exception if is it impossible to access to a remote resource.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void computePersonRankingIndicatorUpdates(ResearchOrganization organization,
			Locale locale,
			Progression progression,
			BiConsumer<Person, Map<String, Object>> callback) throws Exception {
		final var treatedIdentifiers = new TreeSet<>(new IdentifiableEntityComparator());
		final var memberships = organization.getDirectOrganizationMemberships();
		final var progress = progression == null ? new DefaultProgression() : progression;
		progress.setProperties(0, 0, memberships.size(), false);
		final var logger = LoggerFactory.getLogger(getClass());
		for (final var membership : memberships) {
			final var person = membership.getPerson();
			final var subTasks = progress.subTask(1, 0, 3);
			if (treatedIdentifiers.add(person)) {
				progress.setComment(getMessage(locale, "personService.GetPersonIndicatorUpdatesFor", person.getFullNameWithLastNameFirst())); //$NON-NLS-1$
				final var newPersonIndicators = new HashMap<String, Object>();
				readGoogleScholarIndicators(person, newPersonIndicators, logger);
				subTasks.increment();
				readScopusIndicators(person, newPersonIndicators, logger);
				subTasks.increment();
				readWosIndicators(person, newPersonIndicators, logger);
				if (!newPersonIndicators.isEmpty()) {
					callback.accept(person, newPersonIndicators);
				}
			}
			progress.increment();
		}
		progress.end();
	}

	private void readGoogleScholarIndicators(Person person, Map<String, Object> newIndicators, Logger logger) {
		logger.info("Get Google Scholar indicators for " + person); //$NON-NLS-1$
		final var hindexEnabled = person.getGoogleScholarHindex() > 0;
		final var citationsEnabled = person.getGoogleScholarCitations() > 0;
		if (!Strings.isNullOrEmpty(person.getGoogleScholarId()) && (hindexEnabled || citationsEnabled)) {
			final var url = person.getGoogleScholarURL();
			if (url != null) {
				try {
					final var indicators = this.googlePlatform.getPersonRanking(url, null);
					if (indicators != null) {
						if (hindexEnabled) {
							if (indicators.hindex > 0) {
								newIndicators.put("scholarHindex", Integer.valueOf(indicators.hindex)); //$NON-NLS-1$
							}
							newIndicators.put("currentScholarHindex", Integer.valueOf(person.getGoogleScholarHindex())); //$NON-NLS-1$
						}
						if (citationsEnabled) {
							if (indicators.citations > 0) {
								newIndicators.put("scholarCitations", Integer.valueOf(indicators.citations)); //$NON-NLS-1$
							}
							newIndicators.put("currentScholarCitations", Integer.valueOf(person.getGoogleScholarCitations())); //$NON-NLS-1$
						}
					}
				} catch (Throwable ex) {
					//
				}
			}
		}
	}

	private void readScopusIndicators(Person person, Map<String, Object> newIndicators, Logger logger) {
		logger.info("Get Scopus indicators for " + person); //$NON-NLS-1$
		final var hindexEnabled = person.getScopusHindex() > 0;
		final var citationsEnabled = person.getScopusCitations() > 0;
		if (!Strings.isNullOrEmpty(person.getScopusId()) && (hindexEnabled || citationsEnabled)) {
			final var url = person.getScopusURL();
			if (url != null) {
				try {
					final var indicators = this.scopusPlatform.getPersonRanking(url, null);
					if (indicators != null) {
						if (hindexEnabled) {
							if (indicators.hindex > 0) {
								newIndicators.put("scopusHindex", Integer.valueOf(indicators.hindex)); //$NON-NLS-1$
							}
							newIndicators.put("currentScopusHindex", Integer.valueOf(person.getScopusHindex())); //$NON-NLS-1$
						}
						if (citationsEnabled) {
							if (indicators.citations > 0) {
								newIndicators.put("scopusCitations", Integer.valueOf(indicators.citations)); //$NON-NLS-1$
							}
							newIndicators.put("currentScopusCitations", Integer.valueOf(person.getScopusCitations())); //$NON-NLS-1$
						}
					}
				} catch (Throwable ex) {
					//
				}
			}
		}
	}

	private void readWosIndicators(Person person, Map<String, Object> newIndicators, Logger logger) {
		logger.info("Get WoS indicators for " + person); //$NON-NLS-1$
		final var hindexEnabled = person.getWosHindex() > 0;
		final var citationsEnabled = person.getWosCitations() > 0;
		if (!Strings.isNullOrEmpty(person.getResearcherId()) && (hindexEnabled || citationsEnabled)) {
			final var url = person.getResearcherIdURL();
			if (url != null) {
				try {
					final var indicators = this.wosPlatform.getPersonRanking(url, null);
					if (indicators != null) {
						if (hindexEnabled) {
							if (indicators.hindex > 0) {
								newIndicators.put("wosHindex", Integer.valueOf(indicators.hindex)); //$NON-NLS-1$
							}
							newIndicators.put("currentWosHindex", Integer.valueOf(person.getWosHindex())); //$NON-NLS-1$
						}
						if (citationsEnabled) {
							if (indicators.citations > 0) {
								newIndicators.put("wosCitations", Integer.valueOf(indicators.citations)); //$NON-NLS-1$
							}
							newIndicators.put("currentWosCitations", Integer.valueOf(person.getWosCitations())); //$NON-NLS-1$
						}
					}
				} catch (Throwable ex) {
					//
				}
			}
		}
	}

	/** Save person indicators. If a person is not mentionned in the given map, her/his associated indicators will be not changed.
	 *
	 * @param changes the changes to apply.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void setPersonIndicators(Map<Long, PersonIndicators> changes) {
		if (changes != null) {
			for (final var entry : changes.entrySet()) {
				final var person = this.personRepository.findById(entry.getKey());
				if (person.isEmpty()) {
					throw new IllegalArgumentException("Person not found: " + entry.getKey()); //$NON-NLS-1$
				}
				final var indicators = entry.getValue();
				if (indicators != null) {
					var change = false;
					final var pers = person.get();
					if (indicators.wos.hindex >= 0) {
						pers.setWosHindex(indicators.wos.hindex);
						change = true;
					}
					if (indicators.wos.citations >= 0) {
						pers.setWosCitations(indicators.wos.citations);
						change = true;
					}
					if (indicators.scopus.hindex >= 0) {
						pers.setScopusHindex(indicators.scopus.hindex);
						change = true;
					}
					if (indicators.scopus.citations >= 0) {
						pers.setScopusCitations(indicators.scopus.citations);
						change = true;
					}
					if (indicators.scholar.hindex >= 0) {
						pers.setGoogleScholarHindex(indicators.scholar.hindex);
						change = true;
					}
					if (indicators.scholar.citations >= 0) {
						pers.setGoogleScholarCitations(indicators.scholar.citations);
						change = true;
					}
					if (change) {
						this.personRepository.save(pers);
					}
				}
			}
		}
	}

	@Override
	public EntityEditingContext<Person> startEditing(Person person, Logger logger) {
		assert person != null;
		logger.info("Starting the edition of the person: " + person); //$NON-NLS-1$
		return new EditingContext(person, logger);
	}

	@Override
	public EntityDeletingContext<Person> startDeletion(Set<Person> persons, Logger logger) {
		assert persons != null && !persons.isEmpty();
		logger.info("Starting the deletion of the persons: " + persons); //$NON-NLS-1$
		// Force loading of the memberships and authorships
		inSession(session -> {
			for (final var person : persons) {
				if (person.getId() != 0l) {
					session.load(person, Long.valueOf(person.getId()));
					Hibernate.initialize(person.getMemberships());
					Hibernate.initialize(person.getAuthorships());
					Hibernate.initialize(person.getTeachingActivities());
					Hibernate.initialize(person.getParticipationJurys());
					Hibernate.initialize(person.getCandidateJurys());
					Hibernate.initialize(person.getPromoterJurys());
					Hibernate.initialize(person.getGuestInvitations());
					Hibernate.initialize(person.getInviterInvitations());
					Hibernate.initialize(person.getProjectParticipations());
				}
			}
		});
		return new DeletingContext(persons, logger);
	}

	/** Context for editing a {@link Person}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<Person> {
		
		private static final long serialVersionUID = 4177864495115107627L;

		/** Constructor.
		 *
		 * @param person the edited person.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(Person person, Logger logger) {
			super(person, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = PersonService.this.personRepository.save(this.entity);
			getLogger().info("Saved person: " + this.entity); //$NON-NLS-1$
		}

		@Override
		public EntityDeletingContext<Person> createDeletionContext() {
			return PersonService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link Person}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<Person> {

		private static final long serialVersionUID = -4321042497958597399L;

		/** Constructor.
		 *
		 * @param persons the persons to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<Person> persons, Logger logger) {
			super(persons, logger);
		}

		@Override
		protected DeletionStatus computeDeletionStatus() {
			for(final var entity : getEntities()) {
				if (!entity.getMemberships().isEmpty()) {
					return PersonDeletionStatus.MEMBERSHIP;
				}
				if (!entity.getAuthorships().isEmpty()) {
					return PersonDeletionStatus.AUTHORSHIP;
				}
				if (!entity.getTeachingActivities().isEmpty()) {
					return PersonDeletionStatus.TEACHING_ACTIVITY;
				}
				if (!entity.getParticipationJurys().isEmpty()) {
					return PersonDeletionStatus.JURY_MEMBER;
				}
				if (!entity.getCandidateJurys().isEmpty()) {
					return PersonDeletionStatus.JURY_CANDIDATE;
				}
				if (!entity.getPromoterJurys().isEmpty()) {
					return PersonDeletionStatus.JURY_PROMOTER;
				}
				if (!entity.getGuestInvitations().isEmpty()) {
					return PersonDeletionStatus.INVITED_GUEST;
				}
				if (!entity.getInviterInvitations().isEmpty()) {
					return PersonDeletionStatus.PERSON_INVITER;
				}
				if (!entity.getProjectParticipations().isEmpty()) {
					return PersonDeletionStatus.PROJECT_PARTICPANT;
				}
			}
			return DeletionStatus.OK;
		}
		
		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			PersonService.this.personRepository.deleteAllById(identifiers);
			getLogger().info("Deleted persons: " + identifiers); //$NON-NLS-1$
		}

	}

	/** Person indicators
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 5.4
	 */
	public static class PersonIndicators {

		/** WoS indicators.
		 */
		public final WebOfSciencePerson wos;
		
		/** Scopus indicators.
		 */
		public final ScopusPerson scopus;

		/** Google Scholar indicators.
		 */
		public final GoogleScholarPerson scholar;

		/** Constructor.
		 *
		 * @param wosHindex H-index from WoS, or {@code -1} if unknown.
		 * @param wosCitations Citations from WoS, or {@code -1} if unknown.
		 * @param scopusHindex H-index from Scopus, or {@code -1} if unknown.
		 * @param scopusCitations Citations from Scopus, or {@code -1} if unknown.
		 * @param scholarHindex H-index from Google Scholar, or {@code -1} if unknown.
		 * @param scholarCitations Citations from Google Scholar, or {@code -1} if unknown.
		 */
		public PersonIndicators(int wosHindex, int wosCitations, int scopusHindex, int scopusCitations, int scholarHindex, int scholarCitations) {
			this.wos = new WebOfSciencePerson(wosHindex, wosCitations);
			this.scopus = new ScopusPerson(scopusHindex, scopusCitations);
			this.scholar = new GoogleScholarPerson(scholarHindex, scholarCitations);
		}

	}

	/** Internal exception
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class SuccessException extends RuntimeException {

		private static final long serialVersionUID = -2814067417193916087L;

		SuccessException() {
			//
		}

	}

	/** Consumer of person ranking information with H-index and citations.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public interface PersonRankingConsumer extends Serializable {

		/** Invoked when a person ranking is discovered from the source.
		 * 
		 * @param personId the identifier of the person.
		 * @param knownHindex the H-index of the person that is already known. 
		 * @param newHindex the new H-index for the person. 
		 * @param knownCitations the number of citations for the person that is already known. 
		 * @param newCitations the new number of citations for the person. 
		 */
		void consume(long personId, int knownHindex, int newHindex, int knownCitations, int newCitations);

	}

	/** Description of the information for a person.
	 * 
	 * @param person the person. 
	 * @param wosHindex the new WOS H-index, or {@code null} to avoid indicator change.
	 * @param wosCitations the new WOS citations, or {@code null} to avoid indicator change.
	 * @param scopusHindex the new Scopus H-index, or {@code null} to avoid indicator change.
	 * @param scopusCitations the new Scopus citations, or {@code null} to avoid indicator change.
	 * @param googleScholarHindex the new Google Scholar H-index, or {@code null} to avoid indicator change.
	 * @param googleScholarCitations the new Google Scholar citations, or {@code null} to avoid indicator change.
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public record PersonRankingUpdateInformation(Person person, Integer wosHindex, Integer wosCitations,
			Integer scopusHindex, Integer scopusCitations, Integer googleScholarHindex, Integer googleScholarCitations) {
		//
	}



}
