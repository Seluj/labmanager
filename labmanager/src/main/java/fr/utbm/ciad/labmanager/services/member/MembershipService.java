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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.vaadin.flow.internal.LocaleUtil;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.PersonRepository;
import fr.utbm.ciad.labmanager.data.member.Responsibility;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationRepository;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for the memberships to research organizations.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class MembershipService extends AbstractEntityService<Membership> {

	private static final long serialVersionUID = 5648070897311895764L;

	private ResearchOrganizationRepository organizationRepository;

	private MembershipRepository membershipRepository;

	private PersonRepository personRepository;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param organizationRepository the organization repository.
	 * @param membershipRepository the membership repository.
	 * @param personRepository the person repository.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public MembershipService(
			@Autowired ResearchOrganizationRepository organizationRepository,
			@Autowired MembershipRepository membershipRepository,
			@Autowired PersonRepository personRepository,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory) {			
		super(messages, constants, sessionFactory);
		this.organizationRepository = organizationRepository;
		this.membershipRepository = membershipRepository;
		this.personRepository = personRepository;
	}

	/** Replies the list of all the memberships.
	 *
	 * @return the list of all the memberships.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Membership> getAllMemberships() {
		return this.membershipRepository.findAll();
	}

	/** Replies the list of all the memberships that correspond to a supervisable position.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the memberships.
	 * @param callback the initializer that is applied on each loaded entity.
	 * @return the list of all the memberships.
	 * @since 4.0
	 */
	@Transactional
	public Page<Membership> getSupervisableMemberships(Pageable pageable, Specification<Membership> filter, Consumer<Membership> callback) {
		final var page = this.membershipRepository.findAll(SupervisablePositionSpecification.SINGLETON.and(filter), pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies the list that is composed by a single memberships per person.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of the memberships.
	 * @param callback the initializer for all the loaded persons.
	 * @return the list of all the memberships.
	 * @since 4.0
	 */
	@Transactional
	public Page<Person> getAllPersonsWithMemberships(Pageable pageable, Specification<Membership> filter, Consumer<Person> callback) {
		final var persons = this.membershipRepository.findDistinctPerson(pageable, filter);
		if (callback != null) {
			for (final var person : persons) {
				callback.accept(person);
			}
		}
		return persons;
	}

	/** Replies the membership of the given person.
	 *
	 * @param personId the identifier of the person.
	 * @param pageable the manager of pages.
	 * @param filter the filter of the memberships.
	 * @return the memberships of the person.
	 * @since 4.0
	 */
	@Transactional(readOnly = true)
	public Page<Membership> getMembershipsForPerson(long personId, Pageable pageable, Specification<Membership> filter) {
		final var criteria = new PersonIdentifierSpecification(personId);
		final Specification<Membership> filter0 = filter == null ? criteria : criteria.and(filter);
		return this.membershipRepository.findAll(filter0, pageable);
	}

	/** Replies the members of the organization.
	 * 
	 * @param organization the organization to use.
	 * @param memberFilter describes how members are filtered.
	 * @param statusFilter indicates how to filter according to the member status.
	 * @return the list of the members.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@SuppressWarnings("static-method")
	public List<Membership> getOrganizationMembers(ResearchOrganization organization, MemberFiltering memberFilter,
			Predicate<MemberStatus> statusFilter) {
		if (organization != null) {
			var stream = organization.getDirectOrganizationMemberships().stream();
			if (memberFilter != null) {
				switch (memberFilter) {
				case ACTIVES:
					stream = stream.filter(it -> it.isActive());
					break;
				case FORMERS:
					stream = stream.filter(it -> it.isFormer());
					break;
				case FUTURES:
					stream = stream.filter(it -> it.isFuture());
					break;
				case ALL:
				default:
					//
				}
			}
			if (statusFilter != null) {
				stream = stream.filter(it -> statusFilter.test(it.getMemberStatus()));
			}
			return stream.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/** Replies the members of the organization.
	 * 
	 * @param identifier the identifier of the organization to explore.
	 * @param memberFilter describes how members are filtered.
	 * @param statusFilter indicates how to filter according to the member status.
	 * @return the list of the members.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Membership> getOrganizationMembers(long identifier, MemberFiltering memberFilter,
			Predicate<MemberStatus> statusFilter) {
		final var optOrg = this.organizationRepository.findById(Long.valueOf(identifier));
		if (optOrg.isPresent()) {
			return getOrganizationMembers(optOrg.get(), memberFilter, statusFilter);
		}
		return Collections.emptyList();
	}

	/** Replies the list of the organizations that are not those with the given name.
	 *
	 * @param members the members to find into the organizations.
	 * @param currentOrganizationName the current organization name in order to find *others* organizations.
	 * @return map with the member identifiers as keys and the list of the other organizations as values.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@SuppressWarnings("static-method")
	public Map<Long, List<ResearchOrganization>> getOtherOrganizationsForMembers(Collection<Membership> members,
			String currentOrganizationName) {
		if (currentOrganizationName != null) {
			return getOtherOrganizationsForMembers(members,
					it -> !Objects.equals(currentOrganizationName, it.getDirectResearchOrganization().getName()));
		}
		return Collections.emptyMap();
	}

	/** Replies the list of the organizations that are not those with the given name.
	 *
	 * @param members the members to find into the organizations.
	 * @param currentOrganizationId the current organization name in order to find *others* organizations.
	 * @return map with the member identifiers as keys and the list of the other organizations as values.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@SuppressWarnings("static-method")
	public Map<Long, List<ResearchOrganization>> getOtherOrganizationsForMembers(Collection<Membership> members,
			long currentOrganizationId) {
		return getOtherOrganizationsForMembers(members,
				it -> currentOrganizationId != it.getDirectResearchOrganization().getId());
	}

	private static Map<Long, List<ResearchOrganization>> getOtherOrganizationsForMembers(Collection<Membership> members,
			Predicate<? super Membership> organizationSelector) {
		if (members != null) {
			// Get the memberships in other organizations for the given members
			final var stream = members.stream().filter(organizationSelector);
			// Group by member id and extract only the research organizations
			final var map = stream.collect(Collectors.groupingBy(
					it -> Long.valueOf(it.getPerson().getId()),
					TreeMap::new,
					Collectors.mapping(Membership::getDirectResearchOrganization, Collectors.toList())));
			return map;
		}
		return Collections.emptyMap();
	}

	/** Replies the membership that corresponds to the given organization and member identifiers.
	 * 
	 * @param organizationId the identifier of the organization.
	 * @param memberId the identifier of the member.
	 * @return the membership or {@code null}.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Membership getMembership(long organizationId, long memberId) {
		final var res = this.membershipRepository.findDistinctByResearchOrganizationIdAndPersonId(
				organizationId, memberId);
		if (res.isPresent()) {
			return res.get();
		}
		return null;
	}

	/** Replies the memberships that corresponds to the given organization and member identifiers.
	 * 
	 * @param organizationId the identifier of the organization.
	 * @param memberId the identifier of the member.
	 * @return the memberships, never {@code null}.
	 */
	public Set<Membership> getMemberships(long organizationId, long memberId) {
		return this.membershipRepository.findByResearchOrganizationIdAndPersonId(organizationId, memberId);
	}

	/** Replies the membership of the given person.
	 *
	 * @param memberId the identifier of the person.
	 * @return the memberships of the person.
	 */
	public List<Membership> getMembershipsForPerson(long memberId) {
		return this.membershipRepository.findAllByPersonId(memberId);
	}

	/** Create the membership that corresponds to the given organization and member identifiers.
	 * This function may test if an active membership already exists in the organization for the given
	 * person. In this case, and if the argument {@code forceCreation} is set to {@code false},
	 * then the function does not create the membership and replies the existing membership.
	 * 
	 * @param organizationId the identifier of the organization.
	 * @param superOrganizationId the identifier of the super organization.
	 * @param organizationAddressId the identifier of the organization address, if known.
	 * @param personId the identifier of the member.
	 * @param startDate the beginning of the membership.
	 * @param endDate the end of the membership.
	 * @param memberStatus the status of the person in the membership.
	 * @param permanentPosition indicates if the position is permanent.
	 * @param responsibility the responsibility of the person during the membership period.
	 * @param cnuSection the section of the CNU to which this membership belongs to.
	 * @param conrsSection the section of the CoNRS to which this membership belongs to.
	 * @param frenchBap the type of job for a not-researcher staff.
	 * @param isMainPosition indicates if the membership is mark as a main position.
	 * @param axes the scientific axes to which the project is associated to.
	 * @param forceCreation indicates if the membership must be created even if there is an active membership
	 *     in the same organization.
	 * @return a pair that contains the membership (created or not) and a boolean flag indicating
	 *     if the replied membership is a new membership or not.
	 * @throws Exception if the creation cannot be done.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Pair<Membership, Boolean> addMembership(long organizationId, Long superOrganizationId, Long organizationAddressId, long personId,
			LocalDate startDate, LocalDate endDate,
			MemberStatus memberStatus, boolean permanentPosition,
			Responsibility responsibility, CnuSection cnuSection, ConrsSection conrsSection,
			FrenchBap frenchBap, boolean isMainPosition, List<ScientificAxis> axes, boolean forceCreation) throws Exception {
		assert memberStatus != null;
		final var optOrg = this.organizationRepository.findById(Long.valueOf(organizationId));
		if (optOrg.isPresent()) {
			final var optPerson = this.personRepository.findById(Long.valueOf(personId));
			if (optPerson.isPresent()) {
				final var person = optPerson.get();
				if (!forceCreation) {
					// We don't need to add the membership if the person is already involved in the organization
					final var ro = person.getMemberships().stream().filter(
							it -> it.isActiveIn(startDate, endDate) && it.getDirectResearchOrganization().getId() == organizationId).findAny();
					if (ro.isPresent()) {
						final var activeMembership = ro.get();
						final var sd = activeMembership.getMemberSinceWhen();
						if (endDate == null || sd == null || !endDate.isBefore(sd)) {
							return Pair.of(ro.get(), Boolean.FALSE);
						}
					}
				}
				final var organization = optOrg.get();
				OrganizationAddress address = null;
				if (organizationAddressId != null && organizationAddressId.intValue() != 0) {
					final var optAdr = organization.getAddresses().stream().filter(it -> organizationAddressId.intValue() == it.getId()).findAny();
					if (optAdr.isPresent()) {
						address = optAdr.get();
					}
				}
				Optional<ResearchOrganization> superOptOrg = Optional.empty();
				if (superOrganizationId != null) {
					superOptOrg = this.organizationRepository.findById(Long.valueOf(superOrganizationId.longValue()));
				}
				
				final var mem = new Membership();
				mem.setPerson(person);
				mem.setDirectResearchOrganization(organization);
				if (superOptOrg.isPresent()) {
					mem.setSuperResearchOrganization(superOptOrg.get());
				}
				mem.setOrganizationAddress(address);
				mem.setMemberSinceWhen(startDate);
				mem.setMemberToWhen(endDate);
				mem.setMemberStatus(memberStatus);
				mem.setPermanentPosition(permanentPosition);
				mem.setResponsibility(responsibility);
				mem.setCnuSection(cnuSection);
				mem.setConrsSection(conrsSection);
				mem.setFrenchBap(frenchBap);
				mem.setMainPosition(isMainPosition);
				mem.setScientificAxes(axes);
				this.membershipRepository.save(mem);
				return Pair.of(mem, Boolean.TRUE);
			}
			throw new IllegalArgumentException("Person not found with id: " + personId); //$NON-NLS-1$
		}
		throw new IllegalArgumentException("Organization not found with id: " + organizationId); //$NON-NLS-1$
	}

	/** Update the membership that corresponds to the given membership identifier identifiers.
	 * 
	 * @param membershipId the identifier of the membership to update.
	 * @param organizationId the identifier of the organization. If it is {@code null}, the organization should not change.
	 * @param superOrganizationId the identifier of the super organization. If it is {@code null}, the organization should not change.
	 * @param organizationAddressId the identifier of the organization address, if known.
	 * @param startDate the new beginning of the membership.
	 * @param endDate the new end of the membership.
	 * @param memberStatus the new status of the person in the membership.
	 * @param permanentPosition indicates if the position is permanent or not.
	 * @param responsibility the responsibility of the person during the membership period.
	 * @param cnuSection the new CNU section, or {@code null} if unknown.
	 * @param conrsSection the section of the CoNRS to which this membership belongs to.
	 * @param frenchBap the type of job for a not-researcher staff.
	 * @param isMainPosition indicates if the membership is mark as a main position.
	 * @param axes the scientific axes to which the project is associated to.
	 * @return the updated membership.
	 * @throws Exception if the given identifiers cannot be resolved to JPA entities.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Membership updateMembershipById(long membershipId, Long organizationId, Long superOrganizationId,
			Long organizationAddressId, LocalDate startDate, LocalDate endDate,
			MemberStatus memberStatus, boolean permanentPosition, Responsibility responsibility,
			CnuSection cnuSection, ConrsSection conrsSection, FrenchBap frenchBap,
			boolean isMainPosition, List<ScientificAxis> axes) throws Exception {
		final var res = this.membershipRepository.findById(Long.valueOf(membershipId));
		if (res.isPresent()) {
			final var membership = res.get();
			if (superOrganizationId != null) {
				final var res0 = this.organizationRepository.findById(superOrganizationId);
				if (res0.isEmpty()) {
					throw new IllegalArgumentException("Cannot find organization with id: " + superOrganizationId); //$NON-NLS-1$
				}
				membership.setSuperResearchOrganization(res0.get());
			}
			if (organizationId != null) {
				final var res0 = this.organizationRepository.findById(organizationId);
				if (res0.isEmpty()) {
					throw new IllegalArgumentException("Cannot find organization with id: " + organizationId); //$NON-NLS-1$
				}
				membership.setDirectResearchOrganization(res0.get());
				OrganizationAddress address = null;
				if (organizationAddressId != null && organizationAddressId.intValue() != 0) {
					final var optAdr = res0.get().getAddresses().stream().filter(it -> organizationAddressId.intValue() == it.getId()).findAny();
					if (optAdr.isPresent()) {
						address = optAdr.get();
					}
				}
				membership.setOrganizationAddress(address);
			}
			membership.setMemberSinceWhen(startDate);
			membership.setMemberToWhen(endDate);
			if (memberStatus != null) {
				membership.setMemberStatus(memberStatus);
			}
			membership.setPermanentPosition(permanentPosition);
			membership.setResponsibility(responsibility);
			membership.setCnuSection(cnuSection);
			membership.setConrsSection(conrsSection);
			membership.setFrenchBap(frenchBap);
			membership.setMainPosition(isMainPosition);
			membership.setScientificAxes(axes);
			this.membershipRepository.save(membership);
			return membership;
		}
		throw new IllegalArgumentException("Cannot find membership with id: " + membershipId); //$NON-NLS-1$
	}

	/** Delete the membership with the given identifier.
	 *
	 * @param membershipId the identifier of the membership to be deleted.
	 * @throws Exception in case of error.
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	@Transactional
	public void removeMembership(long membershipId) throws Exception {
		final var mid = Long.valueOf(membershipId);
		final var optMbr = this.membershipRepository.findById(mid);
		if (optMbr.isEmpty()) {
			throw new IllegalStateException("Membership not found with id: " + membershipId); //$NON-NLS-1$
		}
		final var mbr = optMbr.get();
		final var person = mbr.getPerson();
		if (person != null) {
			person.getMemberships().remove(mbr);
			mbr.setPerson(null);
		}
		final var organization = mbr.getDirectResearchOrganization();
		if (organization != null) {
			organization.getDirectOrganizationMemberships().remove(mbr);
			mbr.setDirectResearchOrganization(null);
		}
		mbr.setScientificAxes(null);
		this.membershipRepository.deleteById(mid);
	}

	/** Replies the persons in the organization of the given identifier.
	 * This function does not consider the suborganizations.
	 * The function {@link #getMembersOf(long)} provides the members for an organization and the associated
	 * suborganizations.
	 * 
	 * @param organizationId the identifier of the organization.
	 * @return the persons.
	 * @see #getMembersOf(long)
	 */
	public Set<Person> getDirectMembersOf(long organizationId) {
		return this.personRepository.findDistinctByMembershipsResearchOrganizationId(organizationId);
	}

	/** Replies the persons in the organization of the given identifier and its suborganizations.
	 * The function {@link #getDirectMembersOf(long)} provides the members for an organization and not of the associated
	 * suborganizations.
	 * 
	 * @param organizationId the identifier of the organization.
	 * @return the persons.
	 * @see #getDirectMembersOf(long)
	 */
	public Set<Person> getMembersOf(long organizationId) {
		final var optOrg = this.organizationRepository.findById(Long.valueOf(organizationId));
		if (optOrg.isPresent()) {
			final var organization = optOrg.get();

			final var persons = new HashSet<Person>();

			final var organizationCandidates = new LinkedList<ResearchOrganization>();
			organizationCandidates.add(organization);

			do {
				final var currentOrganization = organizationCandidates.removeFirst();
				persons.addAll(getDirectMembersOf(currentOrganization.getId()));
				organizationCandidates.addAll(currentOrganization.getSubOrganizations());
			} while (!organizationCandidates.isEmpty());

			return persons;
		}
		return Collections.emptySet();
	}

	/** Replies the persons in the organization of the given name, with the given status.
	 * 
	 * @param organizationName the name of the organization.
	 * @param status the person status.
	 * @return the persons
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Set<Person> getPersonsByOrganizationNameStatus(String organizationName, MemberStatus status) {
		return this.personRepository.findDistinctByMembershipsResearchOrganizationNameAndMembershipsMemberStatus(
				organizationName, status);
	}

	/** Replies the persons in the organization of the given acronym, with the given status.
	 * 
	 * @param organizationAcronym the acronym of the organization.
	 * @param status the person status.
	 * @return the persons
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public Set<Person> getPersonsByOrganizationAcronymStatus(String organizationAcronym, MemberStatus status) {
		return this.personRepository.findDistinctByMembershipsResearchOrganizationAcronymAndMembershipsMemberStatus(
				organizationAcronym, status);
	}

	/** Replies the memberships that have the given identifiers.
	 *
	 * @param identifiers the list of identifiers to search for.
	 * @return the list of memberships for the given identifiers.
	 * @since 3.5
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Membership> getMembershipsByIds(List<Long> identifiers) {
		var memberships = this.membershipRepository.findAllById(identifiers);
		if (memberships.size() != identifiers.size()) {
			throw new IllegalArgumentException("Membership not found"); //$NON-NLS-1$
		}
		return memberships;
	}

	/** Replies the memberships with a maximum age whatever the organization.
	 *
	 * @param maxAge the maximum age in years.
	 * @return the memberships.
	 * @since 3.5
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public List<Membership> getMembershipsOfAge(int maxAge) {
		final var startDate = LocalDate.of(LocalDate.now().getYear() - maxAge, 1, 1);
		final var endDate = LocalDate.now();
		return this.membershipRepository.findAll().parallelStream()
				.filter(it -> it.isActiveIn(startDate, endDate))
				.collect(Collectors.toList());
	}

	/** Replies if the given identifier is the one of a member of an organization.
	 *
	 * @param id the identifier of a person.
	 * @return {@code true} if the person is member of an organization.
	 * @since 3.6
	 * @Deprecated no replacement.
	 */
	@Deprecated(since = "4.0", forRemoval = true)
	public boolean isMember(long id) {
		return !this.membershipRepository.findAllByPersonId(id).isEmpty();
	}

	/** Replies the active memberships for the given person.
	 *
	 * @param person the person.
	 * @param membershipComparator the comparator to use for sorting the memberships.
	 * @return the list of the active memberships.
	 */
	public List<String> getActiveMembershipsForPerson(Person person, Comparator<Membership> membershipComparator) {
		List<String> labels = new ArrayList<>();
       Iterator<Membership> memberships = new MembershipIterator(person, this, membershipComparator);
       while (memberships.hasNext()){
           final var mbr = memberships.next();
           final var organization = mbr.getDirectResearchOrganization();
           labels.add(organization.getAcronymOrName());
       }
       return labels;
	}

	/** Replies the role of the person in the organization.
	 *
	 * @param person the person.
	 * @param membershipComparator the comparator to use for sorting the memberships.
	 * @return the role of the person in the organization.
	 */
	public String getRoleForPerson(Person person, Comparator<Membership> membershipComparator) {
		Iterator<Membership> memberships = new MembershipIterator(person, this, membershipComparator);
		while (memberships.hasNext()){
			Membership membership = memberships.next();
			if (membership.isActive())
				return membership.getMemberStatus().getLabel(getMessageSourceAccessor(), person.getGender(), false, LocaleUtil.getLocale(LocaleUtil::getI18NProvider));
		}
		return null;
   	}

	/** Replies the list of the membership in the organization.
	 *
	 * @param organization the person id.
	 * @return the list of the person in the organization.
	 */
	public List<Membership> getByResearchOrganization(ResearchOrganization organization) {
		return this.membershipRepository.findDistinctByResearchOrganization(organization);
	}

	/** Replies the list of the membership in the super organization.
	 *
	 * @param organization the person id.
	 * @return the list of the person in the organization.
	 */
	public List<Membership> getBySuperResearchOrganization(ResearchOrganization organization) {
		return this.membershipRepository.findDistinctBySuperResearchOrganization(organization);
	}


	@Override
	public EntityEditingContext<Membership> startEditing(Membership membership, Logger logger) {
		assert membership != null;
		logger.info("Starting the edition of the organization membership: " + membership); //$NON-NLS-1$
		// Force loading of the persons and universities that may be edited at the same time as the rest of the journal properties
		inSession(session -> {
			if (membership.getId() != 0l) {
				session.load(membership, Long.valueOf(membership.getId()));
				Hibernate.initialize(membership.getPerson());

				Hibernate.initialize(membership.getDirectResearchOrganization());

				if (membership.getDirectResearchOrganization() != null) {
					Hibernate.initialize(membership.getDirectResearchOrganization().getAddresses());
				}

				Hibernate.initialize(membership.getSuperResearchOrganization());

				if(membership.getSuperResearchOrganization() != null){
					Hibernate.initialize(membership.getSuperResearchOrganization().getAddresses());
				}

				Hibernate.initialize(membership.getOrganizationAddress());

				Hibernate.initialize(membership.getScientificAxes());
			}
		});
		return new EditingContext(membership, logger);
	}

	@Override
	public EntityDeletingContext<Membership> startDeletion(Set<Membership> memberships, Logger logger) {
		assert memberships != null && !memberships.isEmpty();
		logger.info("Starting the deletion of the organization memberships: " + memberships); //$NON-NLS-1$
		// Force loading of the memberships and authorships
		inSession(session -> {
			for (final var membership : memberships) {
				if (membership.getId() != 0l) {
					session.load(membership, Long.valueOf(membership.getId()));
					Hibernate.initialize(membership.getSupervision());
				}
			}
		});
		return new DeletingContext(memberships, logger);
	}

	/** Context for editing a {@link Membership}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityEditingContext<Membership> {

		private static final long serialVersionUID = -4782495321040453426L;

		/** Constructor.
		 *
		 * @param membership the edited membership.
		 * @param logger the logger to be used.
		 */
		protected EditingContext(Membership membership, Logger logger) {
			super(membership, logger);
		}

		@Override
		public void save(HasAsynchronousUploadService... components) throws IOException {
			this.entity = MembershipService.this.membershipRepository.save(this.entity);
			getLogger().info("Saved organization membership: " + this.entity); //$NON-NLS-1$
		}

		@Override
		public EntityDeletingContext<Membership> createDeletionContext() {
			return MembershipService.this.startDeletion(Collections.singleton(this.entity), getLogger());
		}

	}

	/** Context for deleting a {@link Membership}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<Membership> {

		private static final long serialVersionUID = 6636206768055796017L;

		/** Constructor.
		 *
		 * @param memberships the memberships to delete.
		 * @param logger the logger to be used.
		 */
		protected DeletingContext(Set<Membership> memberships, Logger logger) {
			super(memberships, logger);
		}

		@Override
		protected DeletionStatus computeDeletionStatus() {
			for(final var entity : getEntities()) {
				if (entity.getSupervision() != null) {
					return MembershipDeletionStatus.SUPERVISION;
				}
			}
			return DeletionStatus.OK;
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			MembershipService.this.membershipRepository.deleteAllById(identifiers);
			getLogger().info("Deleted organization memberships: " + identifiers); //$NON-NLS-1$
		}

	}

	/** Specification that is validating with a person identifier.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	private static class PersonIdentifierSpecification implements Specification<Membership> {

		private static final long serialVersionUID = -4832779518488220502L;

		private final Long identifier;
		
		/** Constructor.
		 *
		 * @param identifier the person identifier to match.
		 */
		PersonIdentifierSpecification(long identifier) {
			this.identifier = Long.valueOf(identifier);
		}

		@Override
		public jakarta.persistence.criteria.Predicate toPredicate(Root<Membership> root,
				CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.equal(root.get("person").get("id"), this.identifier); //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	/** Specification that is validating a supervisable membership.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	private static class SupervisablePositionSpecification implements Specification<Membership> {

		private static final long serialVersionUID = 8875171739971287552L;

		/** Singleton for this criteria.
		 */
		public static final SupervisablePositionSpecification SINGLETON = new SupervisablePositionSpecification();

		private static List<MemberStatus> SUPERVISABLE_STATUSES = Arrays.asList(MemberStatus.values()).stream().filter(it -> it.isSupervisable()).toList();

		private SupervisablePositionSpecification() {
			//
		}
		
		@Override
		public jakarta.persistence.criteria.Predicate toPredicate(Root<Membership> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			final var predicates = SUPERVISABLE_STATUSES.stream().map(it -> criteriaBuilder.equal(root.get("memberStatus"), it)); //$NON-NLS-1$
			final var conditions = predicates.toArray(it -> new jakarta.persistence.criteria.Predicate[it]);
			return criteriaBuilder.or(conditions);
		}

	}

	/** Iterator on the memberships of a person.
	 * This iterator is filtering the memberships in order to reply only the active memberships.
	 *
	 */
	private static class MembershipIterator implements Iterator<Membership> {
		private final Iterator<Membership> base;

		private boolean foundActive;

		private Membership next;

		private MembershipIterator(Person person, MembershipService membershipService, Comparator<Membership> membershipComparator){
			this.base = membershipService.getMembershipsForPerson(person.getId()).stream()
					.filter(it -> !it.isFuture()).sorted(membershipComparator).iterator();
			searchNext();
		}

		private void searchNext() {
			this.next = null;
			if (this.base.hasNext()) {
				final var mbr = this.base.next();
				if (!mbr.isFormer() || !this.foundActive) {
					this.foundActive = true;
					this.next = mbr;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@Override
		public Membership next() {
			final var currentNext = this.next;
			searchNext();
			return currentNext;
		}

	}

}
