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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolder;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolderRepository;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import fr.utbm.ciad.labmanager.data.member.PersonRepository;
import fr.utbm.ciad.labmanager.data.project.ProjectMember;
import fr.utbm.ciad.labmanager.data.project.ProjectMemberRepository;
import fr.utbm.ciad.labmanager.data.publication.Authorship;
import fr.utbm.ciad.labmanager.data.publication.AuthorshipRepository;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationRepository;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityRepository;
import fr.utbm.ciad.labmanager.services.AbstractService;
import fr.utbm.ciad.labmanager.services.invitation.PersonInvitationService;
import fr.utbm.ciad.labmanager.services.jury.JuryMembershipService;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.services.teaching.TeachingService;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import org.apache.commons.compress.harmony.archive.internal.nls.Messages;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/** Service for the merging persons.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @Deprecated no replacement.
 */
@Service
public class PersonMergingService extends AbstractService {

	private static final long serialVersionUID = 0L;

	private final PersonRepository personRepository;

	private final PersonService personService;

	private final MembershipService organizationMembershipService;

	private final MembershipRepository organizationMembershipRepository;

	private final JuryMembershipService juryMembershipService;

	private final SupervisionService supervisionService;

	private final PersonInvitationService invitationService;

	private final ProjectMemberRepository projectMemberRepository;

	private final AssociatedStructureHolderRepository structureHolderRepository;

	private final AuthorshipRepository authorshipRepository;

    private final TeachingService teachingService;

    private final TeachingActivityRepository teachingRepository;

    private PersonNameComparator nameComparator;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param personRepository the person repository.
	 * @param personService the person service.
	 * @param organizationMembershipService the service for managing the organization memberships.
	 * @param organizationMembershipRepository the repository for managing the organization memberships.
	 * @param juryMembershipService the service for managing the jury memberships.
	 * @param supervisionService the service for managing the supervisions.
	 * @param invitationService the service for managing the invitations.
	 * @param projectMemberRepository the repository for accessing the project members.
	 * @param structureHolderRepository the repository for accessing the structure holders.
	 * @param nameComparator the comparator of person names.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the factory of JPA session.
	 */
	public PersonMergingService(
			@Autowired PersonRepository personRepository,
			@Autowired PersonService personService,
			@Autowired MembershipService organizationMembershipService,
			@Autowired MembershipRepository organizationMembershipRepository,
			@Autowired JuryMembershipService juryMembershipService,
			@Autowired SupervisionService supervisionService,
			@Autowired PersonInvitationService invitationService,
			@Autowired ProjectMemberRepository projectMemberRepository,
			@Autowired AssociatedStructureHolderRepository structureHolderRepository,
			@Autowired PersonNameComparator nameComparator,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory,
			@Autowired AuthorshipRepository publicationRepository,
			@Autowired TeachingService teachingService,
			@Autowired TeachingActivityRepository teachingRepository) {
		super(messages, constants, sessionFactory);
		this.personRepository = personRepository;
		this.personService = personService;
		this.organizationMembershipService = organizationMembershipService;
		this.organizationMembershipRepository = organizationMembershipRepository;
		this.juryMembershipService = juryMembershipService;
		this.supervisionService = supervisionService;
		this.invitationService = invitationService;
		this.projectMemberRepository = projectMemberRepository;
		this.structureHolderRepository = structureHolderRepository;
		this.nameComparator = nameComparator;
		this.authorshipRepository = publicationRepository;
        this.teachingService = teachingService;
        this.teachingRepository = teachingRepository;
    }


	/** Replies the duplicate person names.
	 * The replied list contains groups of persons who have similar names.
	 *
	 * @param comparator comparator of persons that is used for sorting the groups of duplicates. If it is {@code null},
	 *      a {@link PersonComparator} is used.
	 * @param callback the callback invoked during the building.
	 * @return the duplicate persons that is finally computed.
	 * @throws Exception if a problem occurred during the building.
	 */
	public List<Set<Person>> getPersonDuplicates(Comparator<? super Person> comparator, PersonDuplicateCallback callback,
												 double threshold) throws Exception {
		// Each list represents a group of authors that could be duplicate
		final var matchingAuthors = new ArrayList<Set<Person>>();

		// Copy the list of authors into another list in order to enable its
		// modification during the function's process
		final var authorsList = new ArrayList<>(this.personRepository.findAll());

		final Comparator<? super Person> theComparator = comparator == null ? EntityUtils.getPreferredPersonComparator() : comparator;

		final var total = authorsList.size();
		// Notify the callback
		if (callback != null) {
			callback.onDuplicate(0, 0, total);
		}
		var duplicateCount = 0;

		nameComparator.setSimilarityLevel(threshold);
		for (var i = 0; i < authorsList.size() - 1; ++i) {
			final var referencePerson = authorsList.get(i);

			final var currentMatching = new TreeSet<Person>(theComparator);
			currentMatching.add(referencePerson);

			final ListIterator<Person> iterator2 = authorsList.listIterator(i + 1);
			while (iterator2.hasNext()) {
				final var otherPerson = iterator2.next();
				if (this.nameComparator.isSimilar(
						referencePerson.getFirstName(), referencePerson.getLastName(),
						otherPerson.getFirstName(), otherPerson.getLastName())) {
					currentMatching.add(otherPerson);
					++duplicateCount;
					// Consume the other person to avoid to be treated twice times
					iterator2.remove();
				}
			}
			if (currentMatching.size() > 1) {
				matchingAuthors.add(currentMatching);
			}
			// Notify the callback
			if (callback != null) {
				callback.onDuplicate(i, duplicateCount, total);
			}
		}

		return matchingAuthors;
	}

	/** Merge the persons and authorships by replacing those with an old author name by those with the new author name.
	 * This function enables to group the publications that are attached to two different author names
	 * and select one of the name as the final author name.
	 *
	 * @param source the list of the identifiers of the persons to remove and replace by the target person.
	 * @param target the identifier of the target person who should replace the source persons.
	 * @throws Exception if the merging cannot be completed.
	 */
	public void mergePersonsById(Collection<Long> source, Long target) throws Exception {
		assert target != null;
		assert source != null;
		final var optTarget = this.personRepository.findById(target);
		if (optTarget.isPresent()) {
			final var targetPerson = optTarget.get();
			final var sourcePersons = this.personRepository.findAllById(source);
			if (sourcePersons.size() != source.size()) {
				for (final var sp : sourcePersons) {
					if (!source.contains(Long.valueOf(sp.getId()))) {
						throw new IllegalArgumentException("Source person not found with identifier: " + sp.getId()); //$NON-NLS-1$
					}
				}
				throw new IllegalArgumentException("Source person not found"); //$NON-NLS-1$
			}
			mergePersons(sourcePersons, targetPerson);
		} else {
			throw new IllegalArgumentException("Target person not found with identifier: " + target); //$NON-NLS-1$
		}
	}

	/** Merge the persons and authorships by replacing those with an old author name by those with the new author name.
	 * This function enables to group the publications that are attached to two different author names
	 * and select one of the name as the final author name.
	 *
	 * @param sources the list of persons to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @throws Exception if the merging cannot be completed.
	 */
	public void mergePersons(Iterable<Person> sources, Person target) throws Exception {
		assert target != null;
		assert sources != null;
		boolean changed = false;
		for (final var source : sources) {
			if (source.getId() != target.getId()) {
				//getLogger().info("Reassign to " + target.getFullName() + " the elements of " + source.getFullName()); //$NON-NLS-1$ //$NON-NLS-2$
				var lchange = reassignPersonProperties(source, target);
				lchange = reassignPublicationsV2(source, target);
				lchange = reassignOrganizationMemberships(source, target) || lchange;
				lchange = reassignJuryMemberships(source, target) || lchange;
				lchange = reassignSupervisions(source, target) || lchange;
				lchange = reassignInvitations(source, target) || lchange;
				lchange = reassignProjects(source, target) || lchange;
				lchange = reassignAssociatedStructures(source, target) || lchange;
				lchange = reassignTeachingActivities(source, target) || lchange;

				this.personService.removePerson(source.getId());
				changed = changed || lchange;
			}
		}
		if (changed) {
			this.personRepository.save(target);
		}
	}


	/** Re-assign the publication attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if publication has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	@SuppressWarnings("static-method")
	protected boolean reassignPublications(Person source, Person target) throws Exception {
		final var autPubs = source.getAuthorships();
		final var iterator = autPubs.iterator();
		var changed = false;
		while (iterator.hasNext()) {
			final var authorship = iterator.next();
			// Test if the target is co-author. If yes, don't do re-assignment to avoid
			// the same person multiple times as author.
			if (!authorship.getPublication().getAuthors().contains(target)) {
				authorship.setPerson(target);
				iterator.remove();
				target.getAuthorships().add(authorship);
				changed = true;
			}
		}
		return changed;
	}

	/** Re-assign the properties attached to the source person to the target person. There are attached only if
	 * the target person has null properties.
	 *
	 * @param source the personn to remove and replace by the target person.
	 * @param target the target person which should replace the source person.
	 * @return {@code true} if person properties has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignPersonProperties(Person source, Person target){

		boolean changed = false;

		if (target.getFirstName() == null && source.getFirstName() != null) {
			target.setFirstName(source.getFirstName());
			changed = true;
		}

		if (target.getLastName() == null && source.getLastName() != null) {
			target.setLastName(source.getLastName());
			changed = true;
		}

		if (target.getGender() == null && source.getGender() != null) {
			target.setGender(source.getGender());
			changed = true;
		}

		if (target.getPrimaryEmail() == null && source.getPrimaryEmail() != null) {
			target.setPrimaryEmail(source.getPrimaryEmail());
			changed = true;
		}

		if (target.getSecondaryEmail() == null && source.getSecondaryEmail() != null) {
			target.setSecondaryEmail(source.getSecondaryEmail());
			changed = true;
		}

		if (target.getWebPageNaming() == null && source.getWebPageNaming() != null) {
			target.setWebPageNaming(source.getWebPageNaming());
			changed = true;
		}

		if (target.getWebPageId() == null && source.getWebPageId() != null) {
			target.setWebPageId(source.getWebPageId());
			changed = true;
		}

		if (target.getORCID() == null && source.getORCID() != null) {
			target.setORCID(source.getORCID());
			changed = true;
		}

		if (target.getAcademiaURL() == null && source.getAcademiaURL() != null) {
			target.setAcademiaURL(source.getAcademiaURL());
			changed = true;
		}

		if (target.getOfficePhone() == null && source.getOfficePhone() != null) {
			target.setOfficePhone(source.getOfficePhone());
			changed = true;
		}

		if (target.getMobilePhone() == null && source.getMobilePhone() != null) {
			target.setMobilePhone(source.getMobilePhone());
			changed = true;
		}

		if (target.getOfficeRoom() == null && source.getOfficeRoom() != null) {
			target.setOfficeRoom(source.getOfficeRoom());
			changed = true;
		}

		if (target.getCordisURL() == null && source.getCordisURL() != null) {
			target.setCordisURL(source.getCordisURL());
			changed = true;
		}

		if (target.getDblpURL() == null && source.getDblpURL() != null) {
			target.setDblpURL(source.getDblpURL());
			changed = true;
		}

		if (target.getFacebookId() == null && source.getFacebookId() != null) {
			target.setFacebookId(source.getFacebookId());
			changed = true;
		}

		if (target.getGithubId() == null && source.getGithubId() != null) {
			target.setGithubId(source.getGithubId());
			changed = true;
		}

		if (target.getLinkedInId() == null && source.getLinkedInId() != null) {
			target.setLinkedInId(source.getLinkedInId());
			changed = true;
		}

		if (target.getResearcherId() == null && source.getResearcherId() != null) {
			target.setResearcherId(source.getResearcherId());
			changed = true;
		}

		if (target.getScopusId() == null && source.getScopusId() != null) {
			target.setScopusId(source.getScopusId());
			changed = true;
		}

		if (target.getGoogleScholarId() == null && source.getGoogleScholarId() != null) {
			target.setGoogleScholarId(source.getGoogleScholarId());
			changed = true;
		}

		if (target.getGoogleScholarId() == null && source.getGoogleScholarId() != null) {
			target.setGoogleScholarId(source.getGoogleScholarId());
			changed = true;
		}

		if (target.getIdhal() == null && source.getIdhal() != null) {
			target.setIdhal(source.getIdhal());
			changed = true;
		}

		if (target.getResearchGateId() == null && source.getResearchGateId() != null) {
			target.setResearchGateId(source.getResearchGateId());
			changed = true;
		}

		if (target.getAdScientificIndexId() == null && source.getAdScientificIndexId() != null) {
			target.setAdScientificIndexId(source.getAdScientificIndexId());
			changed = true;
		}

		if (target.getGoogleScholarHindex() == 0 && source.getGoogleScholarHindex() != 0) {
			target.setGoogleScholarHindex(source.getGoogleScholarHindex());
			changed = true;
		}

		if (target.getWosHindex() == 0 && source.getWosHindex() != 0) {
			target.setWosHindex(source.getWosHindex());
			changed = true;
		}

		if (target.getScopusHindex() == 0 && source.getScopusHindex() != 0) {
			target.setScopusHindex(source.getScopusHindex());
			changed = true;
		}

		if (target.getGoogleScholarCitations() == 0 && source.getGoogleScholarCitations() != 0) {
			target.setGoogleScholarCitations(source.getGoogleScholarCitations());
			changed = true;
		}

		if (target.getWosCitations() == 0 && source.getWosCitations() != 0) {
			target.setWosCitations(source.getWosCitations());
			changed = true;
		}

		if (target.getScopusCitations() == 0 && source.getScopusCitations() != 0) {
			target.setScopusCitations(source.getScopusCitations());
			changed = true;
		}

		if (target.getGravatarId() == null && source.getGravatarId() != null) {
			target.setGravatarId(source.getGravatarId());
			changed = true;
		}

		if (target.getBiography() == null && source.getBiography() != null) {
			target.setBiography(source.getBiography());
			changed = true;
		}

		return changed;
	}

	/** Re-assign the publication attached to the source person to the target person.
	 *
	 * @param source the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if publication has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	@SuppressWarnings("static-method")
	protected boolean reassignPublicationsV2(Person source, Person target) throws Exception {

		List<Authorship> autPubs = authorshipRepository.findByPersonId(source.getId());
		final var iterator = autPubs.iterator();

		var changed = false;
		while (iterator.hasNext()) {
			final var authorship = iterator.next();
			// Test if the target is co-author. If yes, don't do re-assignment to avoid
			// the same person multiple times as author.
			if (authorshipRepository.findByPersonIdAndPublicationId(target.getId(), authorship.getPublication().getId()).isEmpty()) {
				authorship.setPerson(target);
				iterator.remove();
				target.getAuthorships().add(authorship);
				changed = true;
			}
		}
		return changed;
	}

	/** Re-assign the organization memberships attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if organization membership has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignOrganizationMemberships(Person source, Person target) throws Exception {
		boolean changed = false;
		for (final var membership : this.organizationMembershipService.getMembershipsForPerson(source.getId())) {
			source.getMemberships().remove(membership);
			membership.setPerson(target);
			target.getMemberships().add(membership);
			this.organizationMembershipRepository.save(membership);
			changed = true;
		}
		return changed;
	}

	/** Re-assign the jury memberships attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if jury membership has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignJuryMemberships(Person source, Person target) throws Exception {
		final var changed = new TreeSet<>(EntityUtils.getPreferredJuryMembershipComparator());
		for (final var jmembership : this.juryMembershipService.getMembershipsForPerson(source.getId())) {
			jmembership.setPerson(target);
			changed.add(jmembership);
		}
		for (final var jmembership : this.juryMembershipService.getMembershipsForCandidate(source.getId())) {
			jmembership.setCandidate(target);
			changed.add(jmembership);
		}
		for (final var jmembership : this.juryMembershipService.getMembershipsForPromoter(source.getId())) {
			final List<Person> list = new ArrayList<>(jmembership.getPromoters());
			list.remove(source);
			list.add(target);
			jmembership.setPromoters(list);
			changed.add(jmembership);
		}
		if (changed.isEmpty()) {
			return false;
		}
		for (final var mbr : changed) {
			this.juryMembershipService.save(mbr);
		}
		return true;
	}

	/** Re-assign the supervisions attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if supervision has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignSupervisions(Person source, Person target) throws Exception {
		final var changed = new TreeSet<>(EntityUtils.getPreferredSupervisorComparator());
		// Do not need to change the supervised person's membership because it is done by reassignJuryMemberships()
		//
		for (final var supervision : this.supervisionService.getSupervisionsForSupervisor(source.getId())) {
			for (final var supervisor : supervision.getSupervisors()) {
				if (supervisor.getSupervisor().getId() == source.getId()) {
					supervisor.setSupervisor(target);
					changed.add(supervisor);
				}
			}
		}
		//
		if (changed.isEmpty()) {
			return false;
		}
		for (final var sup : changed) {
			this.supervisionService.save(sup);
		}
		return true;
	}

	/** Re-assign the invitations attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if invitation has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignInvitations(Person source, Person target) throws Exception {
		final var changed = new TreeSet<>(EntityUtils.getPreferredPersonInvitationComparator());
		for (final var invitation : this.invitationService.getInvitationsForPerson(source.getId())) {
			if (invitation.getGuest().getId() == source.getId()) {
				invitation.setGuest(target);
				changed.add(invitation);
			}
			if (invitation.getInviter().getId() == source.getId()) {
				invitation.setInviter(target);
				changed.add(invitation);
			}
		}
		//
		if (changed.isEmpty()) {
			return false;
		}
		for (final var inv : changed) {
			this.invitationService.save(inv);
		}
		return true;
	}

	/** Re-assign the project members attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if project has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignProjects(Person source, Person target) throws Exception {
		final var changed = new ArrayList<ProjectMember>();
		for (final var member : this.projectMemberRepository.findDistinctByPersonId(source.getId())) {
			if (member.getPerson().getId() == source.getId()) {
				member.setPerson(target);
				changed.add(member);
			}
		}
		//
		if (changed.isEmpty()) {
			return false;
		}
		for (final var mbr : changed) {
			this.projectMemberRepository.save(mbr);
		}
		return true;
	}

	/** Re-assign the associated structure's holders attached to the source person to the target person.
	 * 
	 * @param sources the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if associated structure has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignAssociatedStructures(Person source, Person target) throws Exception {
		final var changed = new ArrayList<AssociatedStructureHolder>();
		for (final var holder : this.structureHolderRepository.findDistinctByPersonId(source.getId())) {
			if (holder.getPerson().getId() == source.getId()) {
				holder.setPerson(target);
				changed.add(holder);
			}
		}
		//
		if (changed.isEmpty()) {
			return false;
		}
		for (final var holder : changed) {
			this.structureHolderRepository.save(holder);
		}
		return true;
	}

	/** Re-assign the teaching activities attached to the source person to the target person.
	 *
	 * @param source the person to remove and replace by the target person.
	 * @param target the target person who should replace the source persons.
	 * @return {@code true} if associated structure has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	private boolean reassignTeachingActivities(Person source, Person target) {
		boolean changed = false;

		List<TeachingActivity> teachingActivities = teachingService.getActivitiesByPersonId(source);

		for(TeachingActivity activity : teachingActivities) {
			activity.setPerson(target);
			teachingRepository.save(activity);
			changed = true;
		}

		return changed;
	}

	/** Callback that is invoked when building the list of duplicate persons.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 2.2
	 */
	@FunctionalInterface
	public interface PersonDuplicateCallback {

		/** Invoked for each person.
		 *
		 * @param index the position of the reference person in the list of persons. It represents the progress of the treatment
		 *     of each person.
		 * @param duplicateCount the count of discovered duplicates.
		 * @param total the total number of persons in the list.
		 * @throws Exception if there is an error during the callback treatment. This exception is forwarded to the
		 *     caller of the function that has invoked this callback.
		 */
		void onDuplicate(int index, int duplicateCount, int total) throws Exception;

	}

}
