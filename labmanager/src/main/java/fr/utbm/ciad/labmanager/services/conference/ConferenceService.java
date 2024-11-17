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

package fr.utbm.ciad.labmanager.services.conference;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.IdentifiableEntityComparator;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.conference.ConferenceQualityAnnualIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.conference.ConferenceRepository;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.io.coreportal.CorePortal;
import fr.utbm.ciad.labmanager.utils.io.coreportal.CorePortal.CorePortalConference;
import fr.utbm.ciad.labmanager.utils.names.ConferenceNameComparator;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.hibernate.Hibernate;
import org.hibernate.Session;
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

/** Service related to the conferences.
 * 
 * @author $Author: sgalland$
 * @author $Author: anoubli$
 * @author $Author: bpdj$
 * @author $Author: pgoubet$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Service
public class ConferenceService extends AbstractEntityService<Conference> {

	private static final long serialVersionUID = -35096440379483385L;

	private final ConferenceRepository conferenceRepository;

	private final ConferenceQualityAnnualIndicatorsRepository indicatorsRepository;

	private final CorePortal corePortal;

	private final ConferenceNameComparator conferenceNameComparator;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param conferenceRepository the journal repository.
	 * @param indicatorsRepository the repository for accessing to the quality indicators.
	 * @param corePortal the accessor to the online CORE portal.
	 * @param conferenceNameComparator the comparator of conferences based on their names.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public ConferenceService(
			@Autowired ConferenceRepository conferenceRepository,
			@Autowired ConferenceQualityAnnualIndicatorsRepository indicatorsRepository,
			@Autowired CorePortal corePortal,
			@Autowired ConferenceNameComparator conferenceNameComparator,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.conferenceRepository = conferenceRepository;
		this.indicatorsRepository = indicatorsRepository;
		this.corePortal = corePortal;
		this.conferenceNameComparator = conferenceNameComparator;
	}

	/** Save the given conference or create a JPA entity if the given object is a fake.
	 *
	 * @param conference the conference entity to save.
	 * @param logger the logger to be used.
	 * @return the saved entity.
	 * @see IdentifiableEntity#isFakeEntity()
	 * @since 4.0
	 */
	public Conference saveOrCreateIfFake(Conference conference, Logger logger) {
		var sconference = conference;
		if (sconference != null) {
			if (sconference.isFakeEntity()) {
				sconference = new Conference(sconference);
			}
			logger.info("Saving conference into the database: " + sconference.toString()); //$NON-NLS-1$
			this.conferenceRepository.save(sconference);
		}
		return sconference;
	}

	/** Replies all the conferences for the database.
	 *
	 * @return the list of conferences.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Conference> getAllConferences() {
		return this.conferenceRepository.findAll();
	}

	/** Replies all the conferences for the database.
	 *
	 * @return the list of conferences.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @since 4.0
	 */
	public List<Conference> getAllConferences(Consumer<Conference> callback) {
		final var list = this.conferenceRepository.findAll();
		if (callback != null) {
			list.forEach(callback);
		}
		return list;
	}

	/** Replies all the conferences for the database.
	 *
	 * @param filter the filter of conferences.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the list of conferences.
	 * @since 4.0
	 */
	public List<Conference> getAllConferences(Specification<Conference> filter, Consumer<Conference> callback) {
		final var list = this.conferenceRepository.findAll(filter);
		if (callback != null) {
			list.forEach(callback);
		}
		return list;
	}

	/** Replies all the conferences for the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of conferences.
	 * @return the list of conferences.
	 * @since 4.0
	 */
	public Page<Conference> getAllConferences(Pageable pageable, Specification<Conference> filter) {
		return this.conferenceRepository.findAll(filter, pageable);
	}

	/** Replies all the conferences for the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of conferences.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the list of conferences.
	 * @since 4.0
	 */
	@Transactional
	public Page<Conference> getAllConferences(Pageable pageable, Specification<Conference> filter, Consumer<Conference> callback) {
		final var page = this.conferenceRepository.findAll(filter, pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the conference with the given identifier.
	 *
	 * @param identifier the identifier of the conference.
	 * @return the conference or {@code null} if it has no conference with the given identifier.
	 */
	public Conference getConferenceById(long identifier) {
		final var opt = this.conferenceRepository.findById(Long.valueOf(identifier));
		if (opt.isPresent()) {
			return opt.get();
		}
		return null;
	}

	/** Replies the first conference that has a similar name or acronym.
	 *
	 * @param name the conference name to search for. It must be neither {@code null} nor empty. 
	 * @param acronym the conference acronym to search for. It must be neither {@code null} nor empty.
	 * @return the conference or nothing, never {@code null}.
	 * @since 4.0
	 */
	public Optional<Conference> getConferenceBySimilarNameAndAcronym(String name, String acronym) {
		if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(acronym)) {
			for (Conference conference : this.conferenceRepository.findAll()) {
				if (this.conferenceNameComparator.isSimilar(name, acronym, conference.getName(), conference.getAcronym())) {
					return Optional.of(conference);
				}
			}
		}
		return Optional.empty();
	}

	/** Create a conference.
	 *
	 * @param validated indicates if the conference is validated by a local authority.
	 * @param acronym the acronym of the conference.
	 * @param name the name of the conference.
	 * @param publisher the name of the publisher of the journal.
	 * @param isbn the ISBN number for the journal.
	 * @param issn the ISSN number for the journal.
	 * @param openAccess indicates if the journal is open access or not.
	 * @param conferenceUrl the URL to the page of the conference.
	 * @param coreId the identifier of the conference on the CORE website.
	 * @param enclosingConference the identifier of the conference that is enclosing the current conference.
	 * @return the updated conference.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Optional<Conference> createConference(boolean validated, String acronym, String name,
			String publisher, String isbn, String issn, Boolean openAccess, String conferenceUrl, String coreId,
			Long enclosingConference) {
		final var conference = new Conference();
		try {
			Conference enclosingConferenceObj = null;
			if (enclosingConference != null && enclosingConference.intValue() >= 0) {
				final var enc = this.conferenceRepository.findById(enclosingConference);
				if (enc.isPresent()) {
					enclosingConferenceObj = enc.get();
				}
			}
			updateConference(conference,
					validated, acronym, name, publisher, isbn, issn, openAccess, conferenceUrl, coreId, enclosingConferenceObj);
		} catch (Throwable ex) {
			// Delete created conference
			if (conference.getId() != 0) {
				try {
					removeConference(conference.getId());
				} catch (Throwable ex0) {
					// Silent
				}
			}
			//getLogger().error(ex.getLocalizedMessage(), ex);
			throw ex;
		}
		return Optional.of(conference);
	}

	/** Update the information of a conference.
	 *
	 * @param identifier the identifier of the conference for which information must be updated.
	 * @param validated indicates if the conference is validated by a local authority.
	 * @param acronym the acronym of the conference.
	 * @param name the name of the conference.
	 * @param publisher the name of the publisher of the journal.
	 * @param isbn the ISBN number for the journal.
	 * @param issn the ISSN number for the journal.
	 * @param openAccess indicates if the journal is open access or not.
	 * @param conferenceUrl the URL to the page of the conference.
	 * @param coreId the identifier of the conference on the CORE website.
	 * @param enclosingConference the identifier of the conference that is enclosing the current conference.
	 * @return the updated conference.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Optional<Conference> updateConference(long identifier, boolean validated, String acronym, String name,
			String publisher, String isbn, String issn, Boolean openAccess, String conferenceUrl, String coreId,
			Long enclosingConference) {
		final Optional<Conference> res;
		if (identifier >= 0) {
			res = this.conferenceRepository.findById(Long.valueOf(identifier));
		} else {
			res = Optional.empty();
		}
		if (res.isPresent()) {
			Conference enclosingConferenceObj = null;
			if (enclosingConference != null && enclosingConference.intValue() >= 0 && enclosingConference.intValue() != identifier) {
				final Optional<Conference> enc = this.conferenceRepository.findById(enclosingConference);
				if (enc.isPresent()) {
					enclosingConferenceObj = enc.get();
				}
			}
			updateConference(res.get(),
					validated, acronym, name, publisher, isbn, issn, openAccess, conferenceUrl, coreId,
					enclosingConferenceObj);
		}
		return res;
	}

	/** Update the information of a conference.
	 *
	 * @param identifier the identifier of the conference for which information must be updated.
	 * @param validated indicates if the conference is validated by a local authority.
	 * @param acronym the acronym of the conference.
	 * @param name the name of the conference.
	 * @param publisher the name of the publisher of the journal.
	 * @param isbn the ISBN number for the journal.
	 * @param issn the ISSN number for the journal.
	 * @param openAccess indicates if the journal is open access or not.
	 * @param conferenceUrl the URL to the page of the conference.
	 * @param coreId the identifier of the conference on the CORE website.
	 * @param enclosingConference the reference to the enclosing conference.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	protected void updateConference(Conference conference, boolean validated, String acronym, String name,
			String publisher, String isbn, String issn, Boolean openAccess, String conferenceUrl, String coreId,
			Conference enclosingConference) {
		conference.setAcronym(acronym);
		conference.setConferenceURL(conferenceUrl);
		conference.setCoreId(coreId);
		conference.setISBN(isbn);
		conference.setISSN(issn);
		conference.setName(name);
		conference.setOpenAccess(openAccess);
		conference.setPublisher(publisher);
		conference.setValidated(validated);
		this.conferenceRepository.save(conference);
		conference.setEnclosingConference(enclosingConference);
		this.conferenceRepository.save(conference);
	}

	/** Delete the conference with the given identifier
	 * 
	 * @param identifier the identifier of the conference to be removed.
	 * @Deprecated no replacement.
	 */
	@Transactional
	public void removeConference(long identifier) {
		final Long id = Long.valueOf(identifier);
		final Optional<Conference> conferenceRef = this.conferenceRepository.findById(id);
		if (conferenceRef.isPresent()) {
			this.conferenceRepository.deleteById(id);
		}
	}

	/** Replies the conference with corresponds to the given name or acronym.
	 *
	 * @param conference the name or acronym of the conference.
	 * @return the conference or {@code null}.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Conference getConferenceByName(String conference) {
		if (!Strings.isNullOrEmpty(conference)) {
			final Optional<Conference> opt = this.conferenceRepository.findByAcronymOrName(conference);
			if (opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}

	/** Replies the conferences with the given name.
	 *
	 * @param name the name to search for.
	 * @return the conferences.
	 */
	public Set<Conference> getConferencesByName(String name) {
		return this.conferenceRepository.findDistinctByAcronymOrName(name);
	}

	/** Save the given quality indicators for the conference.
	 *
	 * @param conference the conference.
	 * @param year the reference year of the quality indicators.
	 * @param coreIndex the CORE index.
	 * @return the indicators.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public ConferenceQualityAnnualIndicators setQualityIndicators(Conference conference, int year, CoreRanking coreIndex) {
		final ConferenceQualityAnnualIndicators indicators = conference.setCoreIndexByYear(year, coreIndex);
		this.indicatorsRepository.save(indicators);
		this.conferenceRepository.save(conference);
		return indicators;
	}

	/** Delete the quality indicators for the given conference and year.
	 *
	 * @param conference the conference.
	 * @param year the reference year.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void deleteQualityIndicators(Conference conference, int year) {
		final ConferenceQualityAnnualIndicators indicators = conference.getQualityIndicators().remove(Integer.valueOf(year));
		if (indicators != null) {
			this.indicatorsRepository.delete(indicators);
			this.conferenceRepository.save(conference);
		}
	}

	/** Replies the URL of conference on CORE.
	 *
	 * @param id the identifier of the conference on CORE.
	 * @return the URL of the conference on the CORE portal website.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public URL getCoreURLByConferenceId(String id) {
		try {
			return this.corePortal.getConferenceUrl(id);
		} catch (Throwable ex) {
			//getLogger().warn(ex.getLocalizedMessage(), ex);
			return null;
		}
	}

	/** Compute the updates for the conference rankings.
	 *
	 * @param year the reference year.
	 * @param locale the locale to use.
	 * @param progression the progress indicator.
	 * @param callback the call back that is invoked on all the conferences with updates.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void computeConferenceRankingIndicatorUpdates(int year, Locale locale, DefaultProgression progression, BiConsumer<Conference, Map<String, Object>> callback) {
		final Set<Conference> treatedIdentifiers = new TreeSet<>(new IdentifiableEntityComparator());
		inSession(session -> {
			final List<Conference> conferences = this.conferenceRepository.findAll();
			final Progression progress = progression == null ? new DefaultProgression() : progression;
			progress.setProperties(0, 0, conferences.size(), false);
			for (final Conference conference: conferences) {
				if (treatedIdentifiers.add(conference)) {
					progress.setComment(getMessage(locale, "conferenceService.GetConferenceIndicatorUpdatesFor", conference.getNameOrAcronym())); //$NON-NLS-1$
					final Map<String, Object> newConferenceIndicators = new HashMap<>();
					readCorePortalIndicators(session, year, conference, newConferenceIndicators, LoggerFactory.getLogger(getClass()));
					if (!newConferenceIndicators.isEmpty()) {
						callback.accept(conference, newConferenceIndicators);
					}
				}
				progress.increment();
			}
			progress.end();
		});
	}

	private void readCorePortalIndicators(Session session, int year, Conference conference, Map<String, Object> newIndicators, Logger logger) {
		logger.info("Get CORE indicators for " + conference.getNameOrAcronym()); //$NON-NLS-1$
		session.beginTransaction();
		final String id = conference.getCoreId();
		if (!Strings.isNullOrEmpty(id)) {
			CorePortalConference indicators = null;
			try {
				indicators = this.corePortal.getConferenceRanking(year, id, null);
			} catch (Throwable ex) {
				logger.debug(ex.getLocalizedMessage(), ex);
				indicators = null;
			}
			if (indicators != null) {
				if (indicators.ranking != null) {
					newIndicators.put("coreRanking", indicators.ranking.name()); //$NON-NLS-1$
				}
			}
			final CoreRanking currentCore = conference.getCoreIndexByYear(year);
			newIndicators.put("currentCoreRanking", currentCore.name()); //$NON-NLS-1$
		}
		session.getTransaction().commit();
	}

	/** Update the conference indicators according to the given inputs.
	 *
	 * @param referenceYear the reference year.
	 * @param conferenceUpdates the streams that describes the updates.
	 * @param logger the logger that must be used.
	 * @param progress the progression monitor.
	 * @throws Exception if the journal information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional
	public void updateConferenceIndicators(int referenceYear, Collection<ConferenceRankingUpdateInformation> conferenceUpdates,
			Logger logger, Progression progress) {
		logger.info("Updating conferences' ranking indicators for year " + referenceYear); //$NON-NLS-1$
		progress.setProperties(0, 0, conferenceUpdates.size() + 1, false);
		final var conferences = new ArrayList<Conference>();
		conferenceUpdates.forEach(info -> {
			final var conference = info.conference();
			progress.setComment(conference.getAcronymAndName());
			if (info.ranking() != null) {
				conference.setCoreIndexByYear(referenceYear, info.ranking());
			}
			conferences.add(conference);
			progress.increment();
		});
		this.conferenceRepository.saveAll(conferences);
		progress.end();
	}

	/** Save conference indicators. If a conference is not mentioned in the given map, its associated indicators will be not changed.
	 *
	 * @param year the reference year.
	 * @param changes the changes to apply.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void updateConferenceIndicators(int year, Map<Long, CoreRanking> changes) {
		if (changes != null) {
			for (final Entry<Long, CoreRanking> entry : changes.entrySet()) {
				final Optional<Conference> conference = this.conferenceRepository.findById(entry.getKey());
				if (conference.isEmpty()) {
					throw new IllegalArgumentException("Conference not found: " + entry.getKey()); //$NON-NLS-1$
				}
				final CoreRanking indicator = entry.getValue();
				if (indicator != null) {
					final Conference conf = conference.get();
					conf.setCoreIndexByYear(year, indicator);
					this.conferenceRepository.save(conf);
				}
			}
		}
	}

	/** Download the conference indicators for the given reference year for the CORE portail.
	 * This function uses the {@link CorePortalPlatform} tool for downloading indicators.
	 *
	 * @param referenceYear the reference year.
	 * @param conferences the list of conferences for which the indicators should be downloaded.
	 * @param logger the logger to be used.
	 * @param progress the progression monitor.
	 * @param consumer the consumer of the conference ranking information.
	 * @throws Exception if the conference information cannot be downloaded.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public void downloadConferenceIndicatorsFromCore(int referenceYear, List<Conference> conferences, Logger logger, Progression progress, ConferenceRankingConsumer consumer) throws Exception {
		logger.info("Downloading the conferences' ranking indicators from the CORE Portal for year " + referenceYear); //$NON-NLS-1$
		final var progress0 = progress == null ? new DefaultProgression() : progress; 
		progress0.setProperties(0, 0, conferences.size() * 2, false);
		for (final var conference : conferences) {
			progress0.setComment(conference.getAcronymAndName());
			if (!Strings.isNullOrEmpty(conference.getCoreId())) {
				final var lastRanking = conference.getCoreIndexByYear(referenceYear);
				final var rankings = this.corePortal.getConferenceRanking(referenceYear, conference.getCoreId(), progress0.subTask(1));
				progress0.ensureNoSubTask();
				if (rankings != null) {
					if (rankings.ranking == null) {
						consumer.consume(referenceYear, conference.getId(), lastRanking, CoreRanking.NR);
					} else {
						consumer.consume(referenceYear, conference.getId(), lastRanking, rankings.ranking);
					}
				} else {
					consumer.consume(referenceYear, conference.getId(), lastRanking, CoreRanking.NR);
				}
				progress0.increment();
			} else {
				progress0.subTask(2);
			}
		}
		progress0.end();
	}

	/** Replies the conference quality indicators for the given conference identifier.
	 *
	 * @param conferenceId the identifier of the conference.
	 * @return the quality indicators.
	 */
	public List<ConferenceQualityAnnualIndicators> getConferenceQualityIndicatorsByJournalId(Long conferenceId) {
		return this.indicatorsRepository.findByConferenceId(conferenceId);
	}

	/** Replies the conference that is enclosing the given conference.
	 *
	 * @param conference the conference.
	 * @return the enclosing conference or {@code null} if the given conference is not enclosed by another conference.
	 */
	public Set<Conference> getEnclosedConferences(Conference source) {
		return this.conferenceRepository.findByEnclosingConference(source);
	}

	@Override
	public EntityEditingContext<Conference> startEditing(Conference conference, Logger logger) {
		assert conference != null;
		logger.info("Starting the edition of the conference " + conference); //$NON-NLS-1$
		// Force loading of the quality indicators that may be edited at the same time as the rest of the conference properties
		inSession(session -> {
			if (conference.getId() != 0l) {
				session.load(conference, Long.valueOf(conference.getId()));
				Hibernate.initialize(conference.getQualityIndicators());
			}
		});
		return new EditingContext(conference, logger);
	}

	@Override
	public EntityDeletingContext<Conference> startDeletion(Set<Conference> conferences, Logger logger) {
		assert conferences != null && !conferences.isEmpty();
		logger.info("Starting the deletion of the conferences " + conferences); //$NON-NLS-1$
		// Force loading of the publishers papers and quality indicators
		inSession(session -> {
			for (final var conference : conferences) {
				if (conference.getId() != 0l) {
					session.load(conference, Long.valueOf(conference.getId()));
					Hibernate.initialize(conference.getPublishedPapers());
					Hibernate.initialize(conference.getQualityIndicators());
				}
			}
		});
		return new DeletingContext(conferences, logger);
	}

	/** Context for editing a {@link Conference}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<Conference> {

		private static final long serialVersionUID = -7122364187938515699L;

		/** Constructor.
		 *
		 * @param conference the edited conference.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(Conference conference, Logger logger) {
			super(conference, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = ConferenceService.this.conferenceRepository.save(this.entity);
			getLogger().info("Saved conference: " + this.entity); //$NON-NLS-1$
		}

		@Override
		public EntityDeletingContext<Conference> createDeletionContext() {
			return ConferenceService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link Conference}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<Conference> {

		private static final long serialVersionUID = -8677274787631928182L;

		/** Constructor.
		 *
		 * @param conferences the conferences to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<Conference> conferences, Logger logger) {
			super(conferences, logger);
		}

		@Override
		protected DeletionStatus computeDeletionStatus() {
			for(final var entity : getEntities()) {
				if (!entity.getPublishedPapers().isEmpty()) {
					return ConferenceDeletionStatus.ARTICLE;
				}
			}
			return DeletionStatus.OK;
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			// Remove the quality indicators
			getLogger().info("Removing the conferences' indicators: " + identifiers); //$NON-NLS-1$
			final List<Conference> updatedConferences = new ArrayList<>();
			for (final var conference : getEntities())  {
				if (!conference.getQualityIndicators().isEmpty()) {
					conference.getQualityIndicators().clear();
					updatedConferences.add(conference);
				}
			}
			if (!updatedConferences.isEmpty()) {
				ConferenceService.this.conferenceRepository.saveAllAndFlush(updatedConferences);
			}
			//
			ConferenceService.this.conferenceRepository.deleteAllById(identifiers);
		}

	}

	/** Consumer of conference ranking information with ranking scores.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public interface ConferenceRankingConsumer extends Serializable {

		/** Invoked when a conference ranking is discovered from the source.
		 * 
		 * @param referenceYear the reference year.
		 * @param conferenceId the identifier of the conference.
		 * @param oldRanking the previously know ranking.
		 * @param newRanking the current ranking.
		 */
		void consume(int referenceYear, long conferenceId, CoreRanking oldRanking, CoreRanking newRanking);

	}

	/** Description of the information for a conference.
	 * 
	 * @param conference the conference. 
	 * @param ranking the new CORE ranking, or {@code null} to avoid ranking change. 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public record ConferenceRankingUpdateInformation(Conference conference, CoreRanking ranking) {
		//
	}

}
