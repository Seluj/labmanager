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

package fr.utbm.ciad.labmanager.services.organization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolderRepository;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureRepository;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.MembershipRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationRepository;
import fr.utbm.ciad.labmanager.data.project.ProjectRepository;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.teaching.TeachingService;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import org.hibernate.SessionFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

/** Service for the merging organizations.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 * @Deprecated no replacement.
 */
@Service
public class OrganizationMergingService extends AbstractEntityService<ResearchOrganization> {

	private static final long serialVersionUID = 1L;

	private final ResearchOrganizationService organizationService;

	private final ResearchOrganizationRepository organizationRepository;

	private final MembershipService membershipService;

	private final MembershipRepository membershipRepository;

	private final ProjectRepository projectRepository;

	private final AssociatedStructureRepository structureRepository;

	private final AssociatedStructureHolderRepository structureHolderRepository;

	private OrganizationNameComparator nameComparator;

    private final TeachingService teachingService;

    private final TeachingActivityRepository teachingRepository;

    /** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param organizationService the organization service.
	 * @param organizationRepository the organization repository.
	 * @param membershipRepository the repository of the organization memberships.
	 * @param projectRepository the repository of the projects.
	 * @param structureRepository the repository of the associated structures.
	 * @param structureHolderRepository the repository of the associated structures' holders.
	 * @param nameComparator the comparator of organization names.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public OrganizationMergingService(
			@Autowired ResearchOrganizationService organizationService,
			@Autowired ResearchOrganizationRepository organizationRepository,
			@Autowired MembershipService membershipService,
			@Autowired MembershipRepository membershipRepository,
			@Autowired ProjectRepository projectRepository,
			@Autowired AssociatedStructureRepository structureRepository,
			@Autowired AssociatedStructureHolderRepository structureHolderRepository,
			@Autowired OrganizationNameComparator nameComparator,
			@Autowired MessageSourceAccessor messages,
			@Autowired ConfigurationConstants constants,
			@Autowired SessionFactory sessionFactory,
			@Autowired TeachingService teachingService,
			@Autowired TeachingActivityRepository teachingRepository) {
		super(messages, constants, sessionFactory);
		this.organizationService = organizationService;
		this.organizationRepository = organizationRepository;
		this.membershipService = membershipService;
		this.membershipRepository = membershipRepository;
		this.projectRepository = projectRepository;
		this.structureRepository = structureRepository;
		this.structureHolderRepository = structureHolderRepository;
		this.nameComparator = nameComparator;
        this.teachingService = teachingService;
        this.teachingRepository = teachingRepository;
    }

	/** Replies the duplicate organizations.
	 * The replied list contains groups of organizations who have similar acronym or names.
	 *
	 * @param comparator comparator of organizations that is used for sorting the groups of duplicates. If it is {@code null},
	 *      a {@link ResearchOrganizationComparator} is used.
	 * @param callback the callback invoked during the building.
	 * @return the duplicate persons that is finally computed.
	 * @throws Exception if a problem occurred during the building.
	 */
	public List<Set<ResearchOrganization>> getOrganizationDuplicates(Comparator<? super ResearchOrganization> comparator,
																	 OrganizationDuplicateCallback callback,
																	 double threshold) throws Exception {
		// Each list represents a group of organizations that could be duplicate
		final var matchingOrganizations = new ArrayList<Set<ResearchOrganization>>();

		// Copy the list of organizations into another list in order to enable its
		// modification during the function's process
		final var organizationsList = new ArrayList<>(this.organizationService.getAllResearchOrganizations());

		final Comparator<? super ResearchOrganization> theComparator = comparator == null ? EntityUtils.getPreferredResearchOrganizationComparator() : comparator;

		final var total = organizationsList.size();
		// Notify the callback
		if (callback != null) {
			callback.onDuplicate(0, 0, total);
		}
		int duplicateCount = 0;

		nameComparator.setSimilarityLevel(threshold);
		for (var i = 0; i < organizationsList.size() - 1; ++i) {
			final var referenceOrganization = organizationsList.get(i);

			final var currentMatching = new TreeSet<ResearchOrganization>(theComparator);
			currentMatching.add(referenceOrganization);

			final var iterator2 = organizationsList.listIterator(i + 1);
			while (iterator2.hasNext()) {
				final var otherOrganization = iterator2.next();
				if (this.nameComparator.isSimilar(
						referenceOrganization.getAcronym(), referenceOrganization.getName(),
						otherOrganization.getAcronym(), otherOrganization.getName())) {
					currentMatching.add(otherOrganization);
					++duplicateCount;
					// Consume the other person to avoid to be treated twice times
					iterator2.remove();
				}
			}
			if (currentMatching.size() > 1) {
				matchingOrganizations.add(currentMatching);
			}
			// Notify the callback
			if (callback != null) {
				callback.onDuplicate(i, duplicateCount, total);
			}
		}

		return matchingOrganizations;
	}

	/** Merge the entities by replacing those with an old organization by those with the new organization.
	 *
	 * @param source the list of the identifiers of the organizations to remove and replace by the target organization.
	 * @param target the identifier of the target organization which should replace the source organizations.
	 * @throws Exception if the merging cannot be completed.
	 */
	public void mergeOrganizationsById(Collection<Long> source, Long target) throws Exception {
		assert target != null;
		assert source != null;
		final var optTarget = this.organizationRepository.findById(target);
		if (optTarget.isPresent()) {
			final var targetOrganization = optTarget.get();
			final var sourceOrganizations = this.organizationRepository.findAllById(source);
			if (sourceOrganizations.size() != source.size()) {
				for (final var ro : sourceOrganizations) {
					if (!source.contains(Long.valueOf(ro.getId()))) {
						throw new IllegalArgumentException("Source organization not found with identifier: " + ro.getId()); //$NON-NLS-1$
					}
				}
				throw new IllegalArgumentException("Source organization not found"); //$NON-NLS-1$
			}
			mergeOrganizations(sourceOrganizations, targetOrganization);
		} else {
			throw new IllegalArgumentException("Target organization not found with identifier: " + target); //$NON-NLS-1$
		}
	}

	/** Merge the entities by replacing those with an old organization by those with the new organization.
	 *
	 * @param sources the list of organizations to remove and replace by the target organization.
	 * @param target the target organization who should replace the source organizations.
	 * @throws Exception if the merging cannot be completed.
	 */
	public void mergeOrganizations(Iterable<ResearchOrganization> sources, ResearchOrganization target) throws Exception {
		assert target != null;
		assert sources != null;
		var changed = false;
		for (final var source : sources) {
			if (source.getId() != target.getId()) {
				//getLogger().info("Reassign to " + target.getAcronymOrName() + " the elements of " + source.getAcronymOrName()); //$NON-NLS-1$ //$NON-NLS-2$
				var lchange = reassignOrganizationProperties(source, target);
				lchange = reassignOrganizationMemberships(source, target) || lchange;
				lchange = reassignSuperOrganizationMemberships(source, target) || lchange;
				lchange = reassignProjects(source, target) || lchange;
				lchange = reassignAssociatedStructures(source, target) || lchange;
				lchange = reassignTeachingActivities(source, target) || lchange;
				this.organizationService.removeResearchOrganization(source.getId());
				changed = changed || lchange;
			}
		}
		if (changed) {
			this.organizationRepository.save(target);
		}
	}

	/** Re-assign the properties attached to the source organization to the target organization. There are attached only if
	 * the target organization has null properties.
	 *
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization which should replace the source organization.
	 * @return {@code true} if organization properties has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignOrganizationProperties(ResearchOrganization source, ResearchOrganization target){

		boolean changed = false;

		if (target.getAcronym() == null && source.getAcronym() != null) {
			target.setAcronym(source.getAcronym());
			changed = true;
		}

		if (target.getName() == null && source.getName() != null) {
			target.setName(source.getName());
			changed = true;
		}

		if (target.getDescription() == null && source.getDescription() != null) {
			target.setDescription(source.getDescription());
			changed = true;
		}

		if (target.getRnsr() == null && source.getRnsr() != null) {
			target.setRnsr(source.getRnsr());
			changed = true;
		}

		if (target.getCountry() == null && source.getCountry() != null) {
			target.setCountry(source.getCountry());
			changed = true;
		}

		if (target.getType() == null && source.getType() != null) {
			target.setType(source.getType());
			changed = true;
		}

		if (target.getOrganizationURL() == null && source.getOrganizationURL() != null) {
			target.setOrganizationURL(source.getOrganizationURL());
			changed = true;
		}

		if (target.getPathToLogo() == null && source.getPathToLogo() != null) {
			target.setPathToLogo(source.getPathToLogo());
			changed = true;
		}

		return changed;
	}

	/** Re-assign the organization memberships attached to the source organization to the target organization.
	 * 
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization which should replace the source organizations.
	 * @return {@code true} if organization membership has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignOrganizationMemberships(ResearchOrganization source, ResearchOrganization target) throws Exception {
		var changed = false;
		for (final var membership : this.membershipService.getByResearchOrganization(source)) {
			membership.setDirectResearchOrganization(target);
			this.membershipRepository.save(membership);
			changed = true;
		}
		return changed;
	}

	/** Re-assign the super organization memberships attached to the source organization to the target organization.
	 *
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization which should replace the source organizations.
	 * @return {@code true} if organization membership has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignSuperOrganizationMemberships(ResearchOrganization source, ResearchOrganization target) throws Exception {
		var changed = false;
		for (final var membership : this.membershipService.getBySuperResearchOrganization(source)) {
			membership.setSuperResearchOrganization(target);
			this.membershipRepository.save(membership);
			changed = true;
		}
		return changed;
	}

	/** Re-assign the project organizations attached to the source organization to the target organization.
	 * 
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization who should replace the source organizations.
	 * @return {@code true} if project has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignProjects(ResearchOrganization source, ResearchOrganization target) throws Exception {
		final var changed = new TreeSet<>(EntityUtils.getPreferredProjectComparator());
		
		final var sourceId = source.getId();
		
		for (final var project : this.projectRepository.findAll()) {
			if (project.getCoordinator() != null && project.getCoordinator().getId() == sourceId) {
				project.setCoordinator(target);
				changed.add(project);
			}
			if (project.getLocalOrganization() != null && project.getLocalOrganization().getId() == sourceId) {
				project.setLocalOrganization(target);
				changed.add(project);
			}
			if (project.getSuperOrganization() != null && project.getSuperOrganization().getId() == sourceId) {
				project.setSuperOrganization(target);
				changed.add(project);
			}
			if (project.getLearOrganization() != null && project.getLearOrganization().getId() == sourceId) {
				project.setLearOrganization(target);
				changed.add(project);
			}
			final var otherPartners = new TreeSet<>(project.getOtherPartnersRaw());
			var found = false;
			final var iterator = otherPartners.iterator();
			while (iterator.hasNext()) {
				final var orga = iterator.next();
				if (orga != null && orga.getId() == sourceId) {
					found = true;
					iterator.remove();
				}
			}
			if (found) {
				otherPartners.add(target);
				project.setOtherPartners(otherPartners);
				changed.add(project);
			}
		}
		//
		if (changed.isEmpty()) {
			return false;
		}
		for (final var project : changed) {
			this.projectRepository.save(project);
		}
		return true;
	}

	/** Re-assign the associated structure's attached to the source organization to the target organization.
	 * 
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization who should replace the source organizations.
	 * @return {@code true} if associated structure has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignAssociatedStructures(ResearchOrganization source, ResearchOrganization target) throws Exception {
		final var changed = new TreeSet<>(EntityUtils.getPreferredAssociatedStructureComparator());
		final var sourceId = source.getId();
		for (final var structure : this.structureRepository.findAll()) {
			if (structure.getFundingOrganization() != null && structure.getFundingOrganization().getId() == sourceId) {
				structure.setFundingOrganization(target);
				changed.add(structure);
			}
		}
		final var structureChanged = !changed.isEmpty();
		if (structureChanged) {
			for (final var structure : changed) {
				this.structureRepository.save(structure);
			}
		}
		//
		final var changed1 = new TreeSet<>(EntityUtils.getPreferredAssociatedStructureHolderComparator());
		for (final var holder : this.structureHolderRepository.findAll()) {
			if (holder.getOrganization() != null && holder.getOrganization().getId() == sourceId) {
				holder.setOrganization(target);
				changed1.add(holder);
			}
			if (holder.getSuperOrganization() != null && holder.getSuperOrganization().getId() == sourceId) {
				holder.setSuperOrganization(target);
				changed1.add(holder);
			}
		}
		final var holderChanged = !changed1.isEmpty();
		if (holderChanged) {
			for (final var holder : changed1) {
				this.structureHolderRepository.save(holder);
			}
		}
		//
		return structureChanged || holderChanged;
	}

	/** Re-assign the teaching activities attached to the source organization to the target organization.
	 *
	 * @param source the organization to remove and replace by the target organization.
	 * @param target the target organization which should replace the source organizations.
	 * @return {@code true} if teaching activities has changed.
	 * @throws Exception if the change cannot be completed.
	 */
	protected boolean reassignTeachingActivities(ResearchOrganization source, ResearchOrganization target) throws Exception {
		boolean changed = false;

		List<TeachingActivity> teachingActivities = teachingService.getActivitiesByOrganizationId(source);

		for(TeachingActivity activity : teachingActivities) {
			activity.setUniversity(target);
			teachingRepository.save(activity);
			changed = true;
		}

		return changed;
	}

	@Override
	public EntityEditingContext<ResearchOrganization> startEditing(ResearchOrganization entity, Logger logger) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityDeletingContext<ResearchOrganization> startDeletion(Set<ResearchOrganization> entities, Logger logger) {
		throw new UnsupportedOperationException();
	}

	/** Callback that is invoked when building the list of duplicate organizations.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 3.2
	 */
	@FunctionalInterface
	public interface OrganizationDuplicateCallback {

		/** Invoked for each organization.
		 *
		 * @param index the position of the reference organization in the list of organizations. It represents the progress of the treatment
		 *     of each organization.
		 * @param duplicateCount the count of discovered duplicates.
		 * @param total the total number of organizations in the list.
		 * @throws Exception if there is an error during the callback treatment. This exception is forwarded to the
		 *     caller of the function that has invoked this callback.
		 */
		void onDuplicate(int index, int duplicateCount, int total) throws Exception;

	}

}
