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

package fr.utbm.ciad.labmanager.services.supervision;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.data.supervision.SupervisionRepository;
import fr.utbm.ciad.labmanager.data.supervision.Supervisor;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/** Service for the person supervisions.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Service
public class SupervisionService extends AbstractEntityService<Supervision> {

	private static final long serialVersionUID = 433344219674827021L;

	private SupervisionRepository supervisionRepository;

	private SupervisorRepository supervisorRepository;

	private MembershipRepository membershipRepository;

	private PersonService personService;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param supervisionRepository the repository for person supervisions.
	 * @param supervisorRepository the repository for person supervisors.
	 * @param membershipRepository the repository for accessing the organization memberships.
	 * @param personService the service for accessing the persons.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public SupervisionService(
			@Autowired SupervisionRepository supervisionRepository,
			@Autowired SupervisorRepository supervisorRepository,
			@Autowired MembershipRepository membershipRepository,
			@Autowired PersonService personService,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {
		super(messages, constants, sessionFactory);
		this.supervisionRepository = supervisionRepository;
		this.supervisorRepository = supervisorRepository;
		this.membershipRepository = membershipRepository;
		this.personService = personService;
	}

	/** Replies the list of all the supervisions.
	 *
	 * @return the list of all the supervisions.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Supervision> getAllSupervisions() {
		return this.supervisionRepository.findAll();
	}

	/** Replies the list of all the supervisions.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the supervisions.
	 * @param callback the callback that is invoked for initializing each loaded entity.
	 * @return the list of all the supervisions.
	 * @since 4.0
	 */
	@Transactional
	public Page<Supervision> getAllSupervisions(Pageable pageable, Specification<Supervision> filter, Consumer<Supervision> callback) {
		final var page = this.supervisionRepository.findAll(filter, pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies all the supervisions associated to the person with the given identifier, when he/she is one of the supervisors.
	 *
	 * @param supervisorId the identifier of the supervisor.
	 * @return the list of the supervisions for the supervised person.
	 * @deprecated no replacement.
	 */
	public List<Supervision> getSupervisionsForSupervisor(long supervisorId) {
		return this.supervisionRepository.findAllDisctinctBySupervisorsSupervisorId(Long.valueOf(supervisorId));
	}

	/** Replies all the supervisions associated to the person with the given identifier, when he/she is the supervised person.
	 *
	 * @param supervisedPersonId the identifier of the supervised person.
	 * @return the list of the supervisions for the supervised person.
	 * @deprecated no replacement.
	 */
	public List<Supervision> getSupervisionsForSupervisedPerson(long supervisedPersonId) {
		return this.supervisionRepository.findAllBySupervisedPersonPersonId(Long.valueOf(supervisedPersonId));
	}

	/** Replies all the supervisions associated to the membership with the given identifier.
	 *
	 * @param membershipId the identifier of the membership.
	 * @return the list of the supervisions for the membership.
	 * @since 3.6
	 */
	public List<Supervision> getSupervisionsForMembership(long membershipId) {
		return this.supervisionRepository.findAllBySupervisedPersonId(Long.valueOf(membershipId));
	}

	/** Create a supervision of a person.
	 *
	 * @param membership the identifier of the membership that describes the supervised person.
	 * @param supervisors the list of the supervisors. Each supervisor description is a map with the following keys:<ul>
	 *     <li>{@code supervisorId} the identifier of the supervisor.</li>
	 *     <li>{@code supervisorType} the type of the supervisor.</li>
	 *     <li>{@code supervisorPercent} the percentage of implication of the supervisor.</li>
	 *     </ul>
	 * @param abandonment indicates if the works were abandoned by the supervised person.
	 * @param title the title of the works done by the supervised person.
	 * @param fundingScheme the name of the scheme that is used for funding the supervised person.
	 * @param fundingDetails some explanation and details about the funding scheme.
	 * @param defenseDate the date of the defense.
	 * @param positionAfterSupervision an description of the becoming of the supervised person.
	 * @param numberOfAteRPositions the number of ATER positions that were given to the supervised person.
	 * @param jointPosition indicates if the position of the supervised person is in the context of a joint agreement between institutions (co-tutelle, etc.).
	 * @param entrepreneur indicates if the supervised person has also a position of entrepreneur in parallel. 
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void addSupervision(long membership, List<Map<String, String>> supervisors, boolean abandonment,
			String title, FundingScheme fundingScheme, String fundingDetails, LocalDate defenseDate,
			String positionAfterSupervision, int numberOfAteRPositions, boolean jointPosition, boolean entrepreneur) {
		final var supervision = new Supervision();
		updateSupervisionWithoutSavingAndSupervisors(supervision, membership, abandonment, title, fundingScheme,
				fundingDetails, defenseDate, positionAfterSupervision, numberOfAteRPositions, jointPosition, entrepreneur);
		this.supervisionRepository.save(supervision);
		supervision.setSupervisors(makeSupervisorList(supervisors));
		this.supervisionRepository.save(supervision);
	}

	/** Create a supervision of a person.
	 *
	 * @param supervision the identifier of the supervision to be updated.
	 * @param membership the identifier of the membership that describes the supervised person.
	 * @param supervisors the list of the supervisors. Each supervisor description is a map with the following keys:<ul>
	 *     <li>{@code supervisorId} the identifier of the supervisor.</li>
	 *     <li>{@code supervisorType} the type of the supervisor.</li>
	 *     <li>{@code supervisorPercent} the percentage of implication of the supervisor.</li>
	 *     </ul>
	 * @param abandonment indicates if the works were abandoned by the supervised person.
	 * @param title the title of the works done by the supervised person.
	 * @param fundingScheme the name of the scheme that is used for funding the supervised person.
	 * @param fundingDetails some explanation and details about the funding scheme.
	 * @param defenseDate the date of the defense.
	 * @param positionAfterSupervision an description of the becoming of the supervised person.
	 * @param numberOfAteRPositions the number of ATER positions that were given to the supervised person.
	 * @param jointPosition indicates if the position of the supervised person is in the context of a joint agreement between institutions (co-tutelle, etc.).
	 * @param entrepreneur indicates if the supervised person has also a position of entrepreneur in parallel. 
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void updateSupervision(long supervision, long membership, List<Map<String, String>> supervisors, boolean abandonment,
			String title, FundingScheme fundingScheme, String fundingDetails, LocalDate defenseDate,
			String positionAfterSupervision, int numberOfAteRPositions, boolean jointPosition, boolean entrepreneur) {
		final var supervisionOpt = this.supervisionRepository.findById(Long.valueOf(supervision));
		if (supervisionOpt.isPresent()) {
			final var supervisionObj = supervisionOpt.get();
			updateSupervisionWithoutSavingAndSupervisors(supervisionObj, membership, abandonment, title, fundingScheme,
					fundingDetails, defenseDate, positionAfterSupervision, numberOfAteRPositions, jointPosition, entrepreneur);
			this.supervisionRepository.save(supervisionObj);
			supervisionObj.setSupervisors(makeSupervisorList(supervisors));
			this.supervisionRepository.save(supervisionObj);
		} else {
			throw new IllegalArgumentException("Supervision not found with id: " + supervision); //$NON-NLS-1$
		}
	}

	@Deprecated(since = "4.0", forRemoval = true)
	private void updateSupervisionWithoutSavingAndSupervisors(Supervision supervision, long membership, boolean abandonment,
			String title, FundingScheme fundingScheme, String fundingDetails, LocalDate defenseDate,
			String positionAfterSupervision, int numberOfAteRPositions, boolean jointPosition, boolean entrepreneur) {
		assert supervision != null;
		final var membershipObj = this.membershipRepository.findById(Long.valueOf(membership));
		if (membershipObj.isEmpty()) {
			throw new IllegalArgumentException("Membership cannot be found with with: " + membership); //$NON-NLS-1$
		}
		supervision.setSupervisedPerson(membershipObj.get());
		supervision.setTitle(title);
		supervision.setFunding(fundingScheme);
		supervision.setFundingDetails(fundingDetails);
		supervision.setAbandonment(abandonment);
		supervision.setDefenseDate(defenseDate);
		supervision.setPositionAfterSupervision(positionAfterSupervision);
		supervision.setNumberOfAterPositions(numberOfAteRPositions);
		supervision.setJointPosition(jointPosition);
		supervision.setEntrepreneur(entrepreneur);
	}

	@Deprecated(since = "4.0", forRemoval = true)
	private List<Supervisor> makeSupervisorList(List<Map<String, String>> supervisors) {
		final var supervisorList = new ArrayList<Supervisor>();
		for (final var supervisorDesc : supervisors) {
			final var sup = new Supervisor();
			int id;
			try {
				id = Integer.parseInt(supervisorDesc.get("supervisorId")); //$NON-NLS-1$
			} catch (Throwable ex) {
				id = 0;
			}
			if (id == 0) {
				throw new IllegalArgumentException("Person not found with id: " + supervisorDesc.get("supervisorId")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			final var person = this.personService.getPersonById(id);
			if (person == null) {
				throw new IllegalArgumentException("Person not found with id: " + supervisorDesc.get("supervisorId")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sup.setSupervisor(person);
			try {
				sup.setType(supervisorDesc.get("supervisorType")); //$NON-NLS-1$
			} catch (Throwable ex) {
				throw new IllegalArgumentException("Invalid type of supervisor: " + supervisorDesc.get("supervisorType")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			int percent;
			try {
				percent = Integer.parseInt(supervisorDesc.get("percent")); //$NON-NLS-1$
			} catch (Throwable ex) {
				percent = 0;
			}
			if (percent < 0 || percent > 100) {
				throw new IllegalArgumentException("Invalid percentage: " + supervisorDesc.get("percent")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sup.setPercentage(percent);
			supervisorList.add(sup);
		}
		if (supervisorList.isEmpty()) {
			throw new IllegalArgumentException("Missing supervisor"); //$NON-NLS-1$
		}
		return supervisorList;
	}

	/** Remove the supervsion that has the given identifier.
	 *
	 * @param identifier the identifier of the supervision.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Transactional
	public void removeSupervision(long identifier) {
		final var mid = Long.valueOf(identifier);
		final var optSupervision = this.supervisionRepository.findById(mid);
		if (optSupervision.isEmpty()) {
			throw new IllegalStateException("Supervision not found with id: " + identifier); //$NON-NLS-1$
		}
		this.supervisionRepository.deleteById(mid);
	}

	/** Save the given supervision into the database.
	 *
	 * @param supervision the supervision to save.
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public void save(Supervision supervision) {
		this.supervisionRepository.save(supervision);
	}

	/** Save the given supervisor into the database.
	 *
	 * @param supervisor the supervisor to save.
	 * @deprecated no replacement.
	 */
	public void save(Supervisor supervisor) {
		this.supervisorRepository.save(supervisor);
	}

	/** Replies if the given identifier is for a person who participates to a supervision as student or supervisor.
	 *
	 * @param id the identifier of the person.
	 * @return {@code true} if the person is a student or a supervisor involved in the supervision.
	 * @since 3.6
	 * @deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean isInvolved(long id) {
		return !this.supervisionRepository.findAllBySupervisedPersonPersonId(Long.valueOf(id)).isEmpty()
				|| !!this.supervisionRepository.findAllDisctinctBySupervisorsSupervisorId(Long.valueOf(id)).isEmpty();
	}

	@Override
	public EntityEditingContext<Supervision> startEditing(Supervision supervision, Logger logger) {
		assert supervision != null;
		// Force initialization of the internal properties that are needed for editing
		if (supervision.getId() != 0l) {
			inSession(session -> {
				session.load(supervision, Long.valueOf(supervision.getId()));
				Hibernate.initialize(supervision.getSupervisedPerson());
				Hibernate.initialize(supervision.getSupervisors());
				supervision.getSupervisors().forEach(it -> {
					Hibernate.initialize(it.getSupervisor());
				});
			});
		}
		return new EditingContext(supervision, logger);
	}

	@Override
	public EntityDeletingContext<Supervision> startDeletion(Set<Supervision> entities, Logger logger) {
		assert entities != null && !entities.isEmpty();
		return new DeletingContext(entities, logger);
	}

	/** Context for editing a {@link Supervision}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<Supervision> {

		private static final long serialVersionUID = 34682604000233262L;

		/** Constructor.
		 *
		 * @param supervision the edited supervision.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(Supervision supervision, Logger logger) {
			super(supervision, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = SupervisionService.this.supervisionRepository.save(this.entity);
		}

		@Override
		public EntityDeletingContext<Supervision> createDeletionContext() {
			return SupervisionService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link Supervision}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<Supervision> {

		private static final long serialVersionUID = -7704741024661753059L;

		/** Constructor.
		 *
		 * @param supervisions the supervisions to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<Supervision> supervisions, Logger logger) {
			super(supervisions, logger);
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			SupervisionService.this.supervisionRepository.deleteAllById(identifiers);
		}

	}

}
