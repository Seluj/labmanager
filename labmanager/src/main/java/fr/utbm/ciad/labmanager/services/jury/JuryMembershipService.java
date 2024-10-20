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

package fr.utbm.ciad.labmanager.services.jury;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipRepository;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipType;
import fr.utbm.ciad.labmanager.data.jury.JuryType;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import jakarta.transaction.Transactional;
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

/** Service for the jury memberships.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0
 */
@Service
public class JuryMembershipService extends AbstractEntityService<JuryMembership> {

	private static final long serialVersionUID = 690837525262871129L;

	private JuryMembershipRepository membershipRepository;

	private PersonService personService;

	private PersonNameParser nameParser;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param membershipRepository the jury membership repository.
	 * @param personService the service for accessing the persons.
	 * @param nameParser the parser of person names.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public JuryMembershipService(
			@Autowired JuryMembershipRepository membershipRepository,
			@Autowired PersonService personService,
			@Autowired PersonNameParser nameParser,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.membershipRepository = membershipRepository;
		this.personService = personService;
		this.nameParser = nameParser;
	}

	/** Replies the list of all the jury memberships.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the axes.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the list of all the jury memberships.
	 * @since 4.0
	 */
	@Transactional
	public Page<JuryMembership> getAllJuryMemberships(Pageable pageable, Specification<JuryMembership> filter, Consumer<JuryMembership> callback) {
		final var page = this.membershipRepository.findAll(filter, pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the list of jury memberships for the person with the given identifier.
	 *
	 * @param personId the identifier of the person.
	 * @return the list of jury memberships.
	 * @Deprecated no replacement.
	 */
	public List<JuryMembership> getMembershipsForPerson(long personId) {
		return this.membershipRepository.findAllByPersonId(personId);
	}

	/** Replies the list of jury memberships for the candidate with the given identifier.
	 *
	 * @param candidateId the identifier of the candidate.
	 * @return the list of jury memberships.
	 * @Deprecated no replacement.
	 */
	public List<JuryMembership> getMembershipsForCandidate(long candidateId) {
		return this.membershipRepository.findAllByCandidateId(candidateId);
	}

	/** Replies the list of jury memberships for the promoter with the given identifier.
	 *
	 * @param promoterId the identifier of the promoter.
	 * @return the list of jury memberships.
	 * @Deprecated no replacement.
	 */
	public List<JuryMembership> getMembershipsForPromoter(long promoterId) {
		return this.membershipRepository.findAllByPromotersId(promoterId);
	}

	/** Add a jury membership.
	 *
	 * @param person the identifier of the person who is involved in the jury.
	 * @param date the date of the defense. 
	 * @param membershipType the type of membership.
	 * @param defenseType the type of defense.
	 * @param title the title of the evaluated works.
	 * @param candidate the name of the candidate. It is a database identifier (for known person) or full name
	 *     (for unknown person).
	 * @param university the name of the university in which the defense was done.
	 * @param country the country of the university.
	 * @param promoters the list of promoters/directors. It is a list of database identifiers (for known persons) or full names
	 *     (for unknown persons).
	 * @return the created jury membership.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public JuryMembership addJuryMembership(long person, LocalDate date, JuryMembershipType membershipType,
			JuryType defenseType, String title, String candidate, String university, CountryCode country,
			List<String> promoters) {
		final var personObj = this.personService.getPersonById(person);
		if (personObj != null) {
			final var mem = new JuryMembership();
			updateJuryMembershipWithoutSaving(mem, personObj, date, membershipType, defenseType, title, candidate, university, country, promoters);
			return this.membershipRepository.save(mem);
		}
		throw new IllegalArgumentException("Person not found with id: " + person); //$NON-NLS-1$
	}

	/** Update the jury membership with the given identifier.
	 *
	 * @param membershipId the identifier of the jury membership to be updated.
	 * @param person the identifier of the person who is involved in the jury.
	 * @param date the date of the defense. 
	 * @param membershipType the type of membership.
	 * @param defenseType the type of defense.
	 * @param title the title of the evaluated works.
	 * @param candidate the name of the candidate. It is a database identifier (for known person) or full name
	 *     (for unknown person).
	 * @param university the name of the university in which the defense was done.
	 * @param country the country of the university.
	 * @param promoters the list of promoters/directors. It is a list of database identifiers (for known persons) or full names
	 *     (for unknown persons).
	 * @return the updated jury membership.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public JuryMembership updateJuryMembership(long membershipId, int person, LocalDate date, JuryMembershipType membershipType,
			JuryType defenseType, String title, String candidate, String university, CountryCode country,
			List<String> promoters) {
		final var optMembership = this.membershipRepository.findById(Long.valueOf(membershipId));
		if (optMembership.isEmpty()) {
			throw new IllegalArgumentException("Jury membership not found with id: " + membershipId); //$NON-NLS-1$
		}
		final var membership = optMembership.get();
		final var personObj = this.personService.getPersonById(person);
		if (personObj == null) {
			throw new IllegalArgumentException("Person not found with id: " + person); //$NON-NLS-1$
		}
		updateJuryMembershipWithoutSaving(membership, personObj, date, membershipType, defenseType, title, candidate, university, country, promoters);
		return this.membershipRepository.save(membership);
	}

	/** Update the jury membership with the given identifier.
	 *
	 * @param membership the jury membership to be updated.
	 * @param person the person who is involved in the jury.
	 * @param date the date of the defense. 
	 * @param membershipType the type of membership.
	 * @param defenseType the type of defense.
	 * @param title the title of the evaluated works.
	 * @param candidate the name of the candidate. It is a database identifier (for known person) or full name
	 *     (for unknown person).
	 * @param university the name of the university in which the defense was done.
	 * @param country the country of the university.
	 * @param promoters the list of promoters/directors. It is a list of database identifiers (for known persons) or full names
	 *     (for unknown persons).
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void updateJuryMembershipWithoutSaving(JuryMembership membership, Person person, LocalDate date, JuryMembershipType membershipType,
			JuryType defenseType, String title, String candidate, String university, CountryCode country,
			List<String> promoters) {
		if (defenseType == JuryType.BAC && membershipType != JuryMembershipType.PRESIDENT) {
			throw new IllegalStateException("Unsupported type of jury membership for " + defenseType.name() + " and " + membershipType.name()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		//
		final var logger = LoggerFactory.getLogger(getClass());
		membership.setPerson(person);
		membership.setDate(date);
		membership.setType(membershipType);
		membership.setDefenseType(defenseType);
		membership.setTitle(title);
		membership.setCandidate(extractPerson(candidate, true, this.personService, this.nameParser, logger));
		membership.setUniversity(university);
		membership.setCountry(country);
		final var promoterArray = new ArrayList<Person>();
		forEarchPerson(promoters, true, this.personService, this.nameParser, logger, it -> promoterArray.add(it));
		membership.setPromoters(promoterArray);
	}

	/** Delete the jury membership with the given identifier.
	 *
	 * @param membershipId the identifier of the jury membership to be deleted.
	 * @throws Exception in case of error.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Transactional
	public void removeJuryMembership(long membershipId) throws Exception {
		final var mid = Long.valueOf(membershipId);
		final var optMbr = this.membershipRepository.findById(mid);
		if (optMbr.isEmpty()) {
			throw new IllegalStateException("Jury membership not found with id: " + membershipId); //$NON-NLS-1$
		}
		this.membershipRepository.deleteById(mid);
	}

	/** Save the given jury membership into the database.
	 *
	 * @param membership the jury membership to save.
	 * @Deprecated no replacement.
	 */
	public void save(JuryMembership membership) {
		this.membershipRepository.save(membership);
	}

	/** Replies if the given identifier is for a person who is participating to a jury.
	 * This participation may be as candidate or member of the jury itself.
	 *
	 * @param id the identifier of the person.
	 * @return {@code true} if the person is candidate or member of a jury.
	 * @since 3.6
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean isInvolved(long id) {
		return !this.membershipRepository.findAllByPersonId(id).isEmpty()
				|| !this.membershipRepository.findAllByCandidateId(id).isEmpty()
				|| !this.membershipRepository.findAllByPromotersId(id).isEmpty();
	}

	@Override
	public EntityEditingContext<JuryMembership> startEditing(JuryMembership jury, Logger logger) {
		assert jury != null;
		logger.info("Starting the edition of the jury membership: " + jury); //$NON-NLS-1$
		// Force loading of the persons and universities that may be edited at the same time as the rest of the journal properties
		inSession(session -> {
			if (jury.getId() != 0l) {
				session.load(jury, Long.valueOf(jury.getId()));
				Hibernate.initialize(jury.getPerson());
				Hibernate.initialize(jury.getCandidate());
				Hibernate.initialize(jury.getPromoters());
			}
		});
		return new EditingContext(jury, logger);
	}

	@Override
	public EntityDeletingContext<JuryMembership> startDeletion(Set<JuryMembership> memberships, Logger logger) {
		assert memberships != null && !memberships.isEmpty();
		return new DeletingContext(memberships, logger);
	}

	/** Context for editing a {@link JuryMembership}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<JuryMembership> {

		private static final long serialVersionUID = 4365799320652677325L;

		/** Constructor.
		 *
		 * @param jury the edited jury membership.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(JuryMembership jury, Logger logger) {
			super(jury, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = JuryMembershipService.this.membershipRepository.save(this.entity);
			getLogger().info("Saved jury membership: " + this.entity); //$NON-NLS-1$
		}

		@Override
		public EntityDeletingContext<JuryMembership> createDeletionContext() {
			return JuryMembershipService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link JuryMembership}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<JuryMembership> {

		private static final long serialVersionUID = -638689738898684619L;

		/** Constructor.
		 *
		 * @param memberships the jury memberships to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<JuryMembership> memberships, Logger logger) {
			super(memberships, logger);
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			JuryMembershipService.this.membershipRepository.deleteAllById(identifiers);
			getLogger().info("Deleted jury memberships: " + identifiers); //$NON-NLS-1$
		}

	}

}
