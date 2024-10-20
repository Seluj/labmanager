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

package fr.utbm.ciad.labmanager.services.invitation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationRepository;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationType;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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

/** Service for the person invitations.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Service
public class PersonInvitationService extends AbstractEntityService<PersonInvitation> {

	private static final long serialVersionUID = -7133335114414082230L;

	private PersonInvitationRepository invitationRepository;

	private PersonService personService;

	private PersonNameParser nameParser;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param invitationRepository the person invitation repository.
	 * @param personService the service for accessing the person.
	 * @param nameParser the parser of person names.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public PersonInvitationService(
			@Autowired PersonInvitationRepository invitationRepository,
			@Autowired PersonService personService,
			@Autowired PersonNameParser nameParser,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.invitationRepository = invitationRepository;
		this.personService = personService;
		this.nameParser = nameParser;
	}

	/** Replies the list of all the outgoing invitations.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the axes.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the list of all the outgoing invitations.
	 * @since 4.0
	 */
	@Transactional
	public Page<PersonInvitation> getAllOutgoingInvitations(Pageable pageable, Specification<PersonInvitation> filter, Consumer<PersonInvitation> callback) {
		final var page = this.invitationRepository.findAll(OutgoingInvitationSpecification.SINGLETON.and(filter), pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the list of all the incoming invitations.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the axes.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the list of all the incoming invitations.
	 * @since 4.0
	 */
	@Transactional
	public Page<PersonInvitation> getAllIncomingInvitations(Pageable pageable, Specification<PersonInvitation> filter, Consumer<PersonInvitation> callback) {
		final var page = this.invitationRepository.findAll(IncomingInvitationSpecification.SINGLETON.and(filter), pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the invitations for the given person whatever if he/she is guest or inviter.
	 *
	 * @param personId the identifier of the person to search for.
	 * @return the invitations for the person.
	 * @Deprecated no replacement.
	 */
	public List<PersonInvitation> getInvitationsForPerson(long personId) {
		return this.invitationRepository.findAllByGuestIdOrInviterId(personId, personId);
	}

	/** Add a person invitation.
	 *
	 * @param person the identifier of the person who is involved in invitation as guest or inviter.
	 * @param guest the name of the guest person.
	 * @param inviter the name of the inviting person.
	 * @param startDate the start date of the invitation. 
	 * @param endDate the end date of the invitation. 
	 * @param type the type of invitation.
	 * @param title the title of the works.
	 * @param university the name of the university of the partner person.
	 * @param country the country of the university.
	 * @return the created invitation.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public PersonInvitation addPersonInvitation(long person,
			String guest, String inviter,
			LocalDate startDate, LocalDate endDate,
			PersonInvitationType type,
			String title, String university, CountryCode country) {
		final var personObj = this.personService.getPersonById(person);
		if (personObj != null) {
			final var inv = new PersonInvitation();
			updatePersonInvitationWithoutSaving(inv, personObj, guest, inviter, startDate, endDate, type, title, university, country);
			return this.invitationRepository.save(inv);
		}
		throw new IllegalArgumentException("Person not found with id: " + person); //$NON-NLS-1$
	}

	/** Update a person invitation.
	 *
	 * @param invitation the identifier of the invitation to be updated.
	 * @param person the identifier of the person who is involved in invitation as guest or inviter.
	 * @param guest the name of the guest person.
	 * @param inviter the name of the inviting person.
	 * @param startDate the start date of the invitation. 
	 * @param endDate the end date of the invitation. 
	 * @param type the type of invitation.
	 * @param title the title of the works.
	 * @param university the name of the university of the partner person.
	 * @param country the country of the university.
	 * @return the created invitation.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public PersonInvitation updatePersonInvitation(
			long invitation,
			long person,
			String guest, String inviter,
			LocalDate startDate, LocalDate endDate,
			PersonInvitationType type,
			String title, String university, CountryCode country) {
		final var optInvitation = this.invitationRepository.findById(Long.valueOf(invitation));
		if (optInvitation.isEmpty()) {
			throw new IllegalArgumentException("Invitation not found with id: " + invitation); //$NON-NLS-1$
		}
		final var invitationObj = optInvitation.get();
		//
		final var personObj = this.personService.getPersonById(person);
		if (personObj == null) {
			throw new IllegalArgumentException("Person not found with id: " + person); //$NON-NLS-1$
		}
		//
		updatePersonInvitationWithoutSaving(invitationObj, personObj, guest, inviter, startDate, endDate, type, title, university, country);
		return this.invitationRepository.save(invitationObj);
	}

	/** Update a person invitation without saving the JPA.
	 *
	 * @param invitation the invitation to be updated.
	 * @param person the person who is involved in invitation as guest or inviter.
	 * @param guest the name of the guest person.
	 * @param inviter the name of the inviting person.
	 * @param startDate the start date of the invitation. 
	 * @param endDate the end date of the invitation. 
	 * @param type the type of invitation.
	 * @param title the title of the works.
	 * @param university the name of the university of the partner person.
	 * @param country the country of the university.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void updatePersonInvitationWithoutSaving(
			PersonInvitation invitation,
			Person person,
			String guest, String inviter,
			LocalDate startDate, LocalDate endDate,
			PersonInvitationType type,
			String title, String university, CountryCode country) {
		final var logger = LoggerFactory.getLogger(getClass());
		final var guestObj = extractPerson(guest, true, this.personService, this.nameParser, logger);
		final var inviterObj = extractPerson(inviter, true, this.personService, this.nameParser, logger);
		if (person.getId() != guestObj.getId() && person.getId() != inviterObj.getId()) {
			throw new IllegalArgumentException("Person with name '" + person.getFullName() + "' is neither guest nor inviter"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		//
		invitation.setType(type);
		invitation.setGuest(guestObj);
		invitation.setInviter(inviterObj);
		invitation.setStartDate(startDate);
		invitation.setEndDate(endDate);
		invitation.setTitle(title);
		invitation.setUniversity(university);
		invitation.setCountry(country);
	}

	/** Delete the person invitation with the given identifier.
	 *
	 * @param invitationId the identifier of the person invitation to be deleted.
	 * @throws Exception in case of error.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Transactional
	public void removePersonInvitation(long invitationId) {
		final var iid = Long.valueOf(invitationId);
		final var optInv = this.invitationRepository.findById(iid);
		if (optInv.isEmpty()) {
			throw new IllegalStateException("Person invitation not found with id: " + invitationId); //$NON-NLS-1$
		}
		this.invitationRepository.deleteById(iid);
	}

	/** Save the given invitation into the database.
	 *
	 * @param invitation the invitation to save.
	 * @Deprecated no replacement.
	 */
	public void save(PersonInvitation invitation) {
		this.invitationRepository.save(invitation);
	}

	/** Replies if the given identifier is for a person who is associated to an invitation as inverter or invitee.
	 * 
	 * @param id the identifier of the person.
	 * @return {@code true} if the person is an inviter or an invitee.
	 * @since 3.6
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean isAssociated(long id) {
		return !this.invitationRepository.findAllByGuestIdOrInviterId(id, id).isEmpty();
	}

	@Override
	public EntityEditingContext<PersonInvitation> startEditing(PersonInvitation invitation, Logger logger) {
		assert invitation != null;
		logger.info("Starting the edition of the person's invitation for " + invitation); //$NON-NLS-1$
		// Force loading of the persons that may be edited at the same time as the rest of the invitation properties
		inSession(session -> {
			if (invitation.getId() != 0l) {
				session.load(invitation, Long.valueOf(invitation.getId()));
				Hibernate.initialize(invitation.getInviter());
				Hibernate.initialize(invitation.getGuest());
			}
		});
		return new EditingContext(invitation, logger);
	}

	@Override
	public EntityDeletingContext<PersonInvitation> startDeletion(Set<PersonInvitation> invitations, Logger logger) {
		assert invitations != null && !invitations.isEmpty();
		logger.info("Starting the deletion of the person's invitations: " + invitations); //$NON-NLS-1$
		return new DeletingContext(invitations, logger);
	}

	/** Context for editing a {@link PersonInvitation}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<PersonInvitation> {

		private static final long serialVersionUID = 44656900669005156L;

		/** Constructor.
		 *
		 * @param invitation the edited invitation.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(PersonInvitation invitation, Logger logger) {
			super(invitation, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = PersonInvitationService.this.invitationRepository.save(this.entity);
			getLogger().info("Saved person's invitation: " + this.entity); //$NON-NLS-1$
		}

		@Override
		public EntityDeletingContext<PersonInvitation> createDeletionContext() {
			return PersonInvitationService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link PersonInvitation}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<PersonInvitation> {

		private static final long serialVersionUID = 3488749520286411999L;

		/** Constructor.
		 *
		 * @param invitations the person invitations to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<PersonInvitation> invitations, Logger logger) {
			super(invitations, logger);
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			PersonInvitationService.this.invitationRepository.deleteAllById(identifiers);
			getLogger().info("Deleted persons' invitations: " + identifiers); //$NON-NLS-1$
		}

	}

	/** Specification that is validating outgoing invitations.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public static class OutgoingInvitationSpecification implements Specification<PersonInvitation> {

		private static final long serialVersionUID = -922262104471108626L;

		/** Singleton for this criteria.
		 */
		public static final OutgoingInvitationSpecification SINGLETON = new OutgoingInvitationSpecification();

		@Override
		public Predicate toPredicate(Root<PersonInvitation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.equal(root.get("type"), PersonInvitationType.OUTGOING_GUEST); //$NON-NLS-1$
		}

	}

	/** Specification that is validating incoming invitations.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	public static class IncomingInvitationSpecification implements Specification<PersonInvitation> {

		private static final long serialVersionUID = 1354215003601539541L;

		/** Singleton for this criteria.
		 */
		public static final IncomingInvitationSpecification SINGLETON = new IncomingInvitationSpecification();

		@Override
		public Predicate toPredicate(Root<PersonInvitation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			final Predicate p0 = criteriaBuilder.equal(root.get("type"), PersonInvitationType.INCOMING_GUEST_PHD_STUDENT); //$NON-NLS-1$
			final Predicate p1 = criteriaBuilder.equal(root.get("type"), PersonInvitationType.INCOMING_GUEST_PROFESSOR); //$NON-NLS-1$
			return criteriaBuilder.or(p0, p1);
		}

	}

}
