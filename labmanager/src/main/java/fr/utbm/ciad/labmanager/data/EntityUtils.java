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

package fr.utbm.ciad.labmanager.data;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureComparator;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolderComparator;
import fr.utbm.ciad.labmanager.data.conference.ConferenceComparator;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationComparator;
import fr.utbm.ciad.labmanager.data.journal.JournalComparator;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectBudgetComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectMemberComparator;
import fr.utbm.ciad.labmanager.data.publication.Production;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.comparators.PublicationComparator;
import fr.utbm.ciad.labmanager.data.publication.comparators.SorensenDicePublicationComparator;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.data.supervision.SupervisionComparator;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorComparator;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityComparator;
import fr.utbm.ciad.labmanager.data.user.UserComparator;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;

/**
 * Tools and configuration for the JPA entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public final class EntityUtils {

    private static PersonComparator PERSON_COMPARATOR;

    private static PersonListComparator PERSONLIST_COMPARATOR;
    private static final Comparator<? super Publication> DEFAULT_PUBLICATION_COMPARATOR = (a, b) -> {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return Integer.MIN_VALUE;
        }
        if (b == null) {
            return Integer.MAX_VALUE;
        }
        var cmp = -Integer.compare(a.getPublicationYear(), b.getPublicationYear());
        if (cmp != 0) {
            return cmp;
        }
        cmp = EntityUtils.getPreferredPersonListComparator().compare(a.getAuthors(), b.getAuthors());
        if (cmp != 0) {
            return cmp;
        }
        cmp = a.getType().compareTo(b.getType());
        if (cmp != 0) {
            return cmp;
        }
        cmp = StringUtils.compareIgnoreCase(a.getTitle(), b.getTitle(), true);
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(a.getId(), b.getId());
    };
    private static MembershipComparator MEMBERSHIP_COMPARATOR;
    private static NameBasedMembershipComparator PERSON_NAME_MEMBERSHIP_COMPARATOR;
    private static ResearchOrganizationComparator ORGANIZATION_COMPARATOR;
    private static ScientificAxisComparator SCIENTFIC_AXIS_COMPARATOR;
    private static ProjectComparator PROJECT_COMPARATOR;
    private static TeachingActivityComparator TEACHING_ACTIVITY_COMPARATOR;
    private static UserComparator USER_COMPARATOR;
    private static OrganizationAddressComparator ORGANIZATION_ADDRESS_COMPARATOR;
    private static PublicationComparator PUBLICATION_COMPARATOR;
    private static JournalComparator JOURNAL_COMPARATOR;
    private static ConferenceComparator CONFERENCE_COMPARATOR;
    private static JuryMembershipComparator JURY_MEMBERSHIP_COMPARATOR;
    private static PersonInvitationComparator PERSON_INVITATION_COMPARATOR;
    private static SupervisorComparator SUPERVISOR_COMPARATOR;
    private static SupervisionComparator SUPERVISION_COMPARATOR;
    private static ProjectMemberComparator PROJECT_MEMBER_COMPARATOR;
    private static ProjectBudgetComparator PROJECT_BUDGET_COMPARATOR;
    private static AssociatedStructureHolderComparator ASSOCIATED_STRUCTURE_HOLDER_COMPARATOR;
    private static AssociatedStructureComparator ASSOCIATED_STRUCTURE_COMPARATOR;

    private EntityUtils() {
        //
    }

    /**
     * Replies the preferred comparator of persons.
     *
     * @return the comparator.
     */
    public static PersonComparator getPreferredPersonComparator() {
        synchronized (EntityUtils.class) {
            if (PERSON_COMPARATOR == null) {
                PERSON_COMPARATOR = new PersonComparator();
            }
            return PERSON_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of persons.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredPersonComparator(PersonComparator comparator) {
        synchronized (EntityUtils.class) {
            PERSON_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of lists of persons.
     *
     * @return the comparator.
     */
    public static PersonListComparator getPreferredPersonListComparator() {
        synchronized (EntityUtils.class) {
            if (PERSONLIST_COMPARATOR == null) {
                PERSONLIST_COMPARATOR = new PersonListComparator(getPreferredPersonComparator());
            }
            return PERSONLIST_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of lists of persons.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredPersonListComparator(PersonListComparator comparator) {
        synchronized (EntityUtils.class) {
            PERSONLIST_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of memberships.
     *
     * @return the comparator.
     */
    public static MembershipComparator getPreferredMembershipComparator() {
        synchronized (EntityUtils.class) {
            if (MEMBERSHIP_COMPARATOR == null) {
                MEMBERSHIP_COMPARATOR = new MembershipComparator(
                        getPreferredPersonComparator(), getPreferredResearchOrganizationComparator(),
                        getPreferredOrganizationAddressComparator());
            }
            return MEMBERSHIP_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of memberships.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredMembershipComparator(MembershipComparator comparator) {
        synchronized (EntityUtils.class) {
            MEMBERSHIP_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of research organizations.
     *
     * @return the comparator.
     */
    public static ResearchOrganizationComparator getPreferredResearchOrganizationComparator() {
        synchronized (EntityUtils.class) {
            if (ORGANIZATION_COMPARATOR == null) {
                ORGANIZATION_COMPARATOR = new ResearchOrganizationComparator();
            }
            return ORGANIZATION_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of research organizations.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredResearchOrganizationComparator(ResearchOrganizationComparator comparator) {
        synchronized (EntityUtils.class) {
            ORGANIZATION_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of scientific axes.
     *
     * @return the comparator.
     */
    public static ScientificAxisComparator getPreferredScientificAxisComparator() {
        synchronized (EntityUtils.class) {
            if (SCIENTFIC_AXIS_COMPARATOR == null) {
                SCIENTFIC_AXIS_COMPARATOR = new ScientificAxisComparator();
            }
            return SCIENTFIC_AXIS_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of scientific axes.
     *
     * @param comparator the comparator.
     * @since 3.5
     */
    public static void setPreferredScientificAxisComparator(ScientificAxisComparator comparator) {
        synchronized (EntityUtils.class) {
            SCIENTFIC_AXIS_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of organization addresses.
     *
     * @return the comparator.
     */
    public static OrganizationAddressComparator getPreferredOrganizationAddressComparator() {
        synchronized (EntityUtils.class) {
            if (ORGANIZATION_ADDRESS_COMPARATOR == null) {
                ORGANIZATION_ADDRESS_COMPARATOR = new OrganizationAddressComparator();
            }
            return ORGANIZATION_ADDRESS_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of organization addresses.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredOrganizationAddressComparator(OrganizationAddressComparator comparator) {
        synchronized (EntityUtils.class) {
            ORGANIZATION_ADDRESS_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of publications.
     *
     * @return the comparator.
     */
    public static PublicationComparator getPreferredPublicationComparator() {
        synchronized (EntityUtils.class) {
            if (PUBLICATION_COMPARATOR == null) {
                PUBLICATION_COMPARATOR = new SorensenDicePublicationComparator();
            }
            return PUBLICATION_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of publications.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredPublicationComparator(PublicationComparator comparator) {
        synchronized (EntityUtils.class) {
            PUBLICATION_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of projects.
     *
     * @return the comparator.
     * @since 3.0
     */
    public static ProjectComparator getPreferredProjectComparator() {
        synchronized (EntityUtils.class) {
            if (PROJECT_COMPARATOR == null) {
                PROJECT_COMPARATOR = new ProjectComparator();
            }
            return PROJECT_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of projects.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredProjectComparator(ProjectComparator comparator) {
        synchronized (EntityUtils.class) {
            PROJECT_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of publications that is used for sorting the list of publications.
     *
     * @return the comparator.
     */
    public static Comparator<? super Publication> getPreferredPublicationComparatorInLists() {
        synchronized (EntityUtils.class) {
            return DEFAULT_PUBLICATION_COMPARATOR;
        }
    }

    /**
     * Replies the preferred comparator of memberships based on the person's names.
     *
     * @return the comparator.
     */
    public static NameBasedMembershipComparator getPreferredPersonNameBasedMembershipComparator() {
        synchronized (EntityUtils.class) {
            if (PERSON_NAME_MEMBERSHIP_COMPARATOR == null) {
                PERSON_NAME_MEMBERSHIP_COMPARATOR = new NameBasedMembershipComparator(
                        getPreferredPersonComparator(), getPreferredResearchOrganizationComparator());
            }
            return PERSON_NAME_MEMBERSHIP_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of memberships based on the person's names.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredPersonNameBasedMembershipComparator(NameBasedMembershipComparator comparator) {
        synchronized (EntityUtils.class) {
            PERSON_NAME_MEMBERSHIP_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of journals.
     *
     * @return the comparator.
     */
    public static JournalComparator getPreferredJournalComparator() {
        synchronized (EntityUtils.class) {
            if (JOURNAL_COMPARATOR == null) {
                JOURNAL_COMPARATOR = new JournalComparator();
            }
            return JOURNAL_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of journals.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredJournalComparator(JournalComparator comparator) {
        synchronized (EntityUtils.class) {
            JOURNAL_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of conferences.
     *
     * @return the comparator.
     */
    public static ConferenceComparator getPreferredConferenceComparator() {
        synchronized (EntityUtils.class) {
            if (CONFERENCE_COMPARATOR == null) {
                CONFERENCE_COMPARATOR = new ConferenceComparator();
            }
            return CONFERENCE_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of conferences.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredConferenceComparator(ConferenceComparator comparator) {
        synchronized (EntityUtils.class) {
            CONFERENCE_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of teaching activities.
     *
     * @return the comparator.
     * @since 3.4
     */
    public static TeachingActivityComparator getPreferredTeachingActivityComparator() {
        synchronized (EntityUtils.class) {
            if (TEACHING_ACTIVITY_COMPARATOR == null) {
                TEACHING_ACTIVITY_COMPARATOR = new TeachingActivityComparator(getPreferredPersonComparator());
            }
            return TEACHING_ACTIVITY_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of teaching activities.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredTeachingActivityComparator(TeachingActivityComparator comparator) {
        synchronized (EntityUtils.class) {
            TEACHING_ACTIVITY_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of application users.
     *
     * @return the comparator.
     * @since 4.0
     */
    public static UserComparator getPreferredUserComparator() {
        synchronized (EntityUtils.class) {
            if (USER_COMPARATOR == null) {
                USER_COMPARATOR = new UserComparator(getPreferredPersonComparator());
            }
            return USER_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of application users.
     *
     * @param comparator the comparator.
     * @since 4.0
     */
    public static void setPreferredUserComparator(UserComparator comparator) {
        synchronized (EntityUtils.class) {
            USER_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of jury memberships.
     *
     * @return the comparator.
     */
    public static JuryMembershipComparator getPreferredJuryMembershipComparator() {
        synchronized (EntityUtils.class) {
            if (JURY_MEMBERSHIP_COMPARATOR == null) {
                JURY_MEMBERSHIP_COMPARATOR = new JuryMembershipComparator(getPreferredPersonComparator());
            }
            return JURY_MEMBERSHIP_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of jury memberships.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredJuryMembershipComparator(JuryMembershipComparator comparator) {
        synchronized (EntityUtils.class) {
            JURY_MEMBERSHIP_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of person invitations.
     *
     * @return the comparator.
     */
    public static PersonInvitationComparator getPreferredPersonInvitationComparator() {
        synchronized (EntityUtils.class) {
            if (PERSON_INVITATION_COMPARATOR == null) {
                PERSON_INVITATION_COMPARATOR = new PersonInvitationComparator(getPreferredPersonComparator());
            }
            return PERSON_INVITATION_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of person invitations.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredPersonInvitationComparator(PersonInvitationComparator comparator) {
        synchronized (EntityUtils.class) {
            PERSON_INVITATION_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of supervisors.
     *
     * @return the comparator.
     */
    public static SupervisorComparator getPreferredSupervisorComparator() {
        synchronized (EntityUtils.class) {
            if (SUPERVISOR_COMPARATOR == null) {
                SUPERVISOR_COMPARATOR = new SupervisorComparator(getPreferredPersonComparator());
            }
            return SUPERVISOR_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of supervisors.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredSupervisorComparator(SupervisorComparator comparator) {
        synchronized (EntityUtils.class) {
            SUPERVISOR_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of supervisions.
     *
     * @return the comparator.
     */
    public static SupervisionComparator getPreferredSupervisionComparator() {
        synchronized (EntityUtils.class) {
            if (SUPERVISION_COMPARATOR == null) {
                SUPERVISION_COMPARATOR = new SupervisionComparator(
                        getPreferredMembershipComparator(),
                        getPreferredSupervisorComparator());
            }
            return SUPERVISION_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of supervisions.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredSupervisionComparator(SupervisionComparator comparator) {
        synchronized (EntityUtils.class) {
            SUPERVISION_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of project members.
     *
     * @return the comparator.
     */
    public static ProjectMemberComparator getPreferredProjectMemberComparator() {
        synchronized (EntityUtils.class) {
            if (PROJECT_MEMBER_COMPARATOR == null) {
                PROJECT_MEMBER_COMPARATOR = new ProjectMemberComparator(getPreferredPersonComparator());
            }
            return PROJECT_MEMBER_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of project members.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredProjectMemberComparator(ProjectMemberComparator comparator) {
        synchronized (EntityUtils.class) {
            PROJECT_MEMBER_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of project budgets.
     *
     * @return the comparator.
     * @since 3.0
     */
    public static ProjectBudgetComparator getPreferredProjectBudgetComparator() {
        synchronized (EntityUtils.class) {
            if (PROJECT_BUDGET_COMPARATOR == null) {
                PROJECT_BUDGET_COMPARATOR = new ProjectBudgetComparator();
            }
            return PROJECT_BUDGET_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of project budgets.
     *
     * @param comparator the comparator.
     */
    public static void setPreferredProjectBudgetComparator(ProjectBudgetComparator comparator) {
        synchronized (EntityUtils.class) {
            PROJECT_BUDGET_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of associated structure holders.
     *
     * @return the comparator.
     * @since 3.2
     */
    public static AssociatedStructureHolderComparator getPreferredAssociatedStructureHolderComparator() {
        synchronized (EntityUtils.class) {
            if (ASSOCIATED_STRUCTURE_HOLDER_COMPARATOR == null) {
                ASSOCIATED_STRUCTURE_HOLDER_COMPARATOR = new AssociatedStructureHolderComparator(
                        getPreferredPersonComparator(),
                        getPreferredResearchOrganizationComparator());
            }
            return ASSOCIATED_STRUCTURE_HOLDER_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of associated structure holders.
     *
     * @param comparator the comparator.
     * @since 3.2
     */
    public static void setPreferredAssociatedStructureHolderComparator(AssociatedStructureHolderComparator comparator) {
        synchronized (EntityUtils.class) {
            ASSOCIATED_STRUCTURE_HOLDER_COMPARATOR = comparator;
        }
    }

    /**
     * Replies the preferred comparator of associated structures.
     *
     * @return the comparator.
     * @since 3.2
     */
    public static AssociatedStructureComparator getPreferredAssociatedStructureComparator() {
        synchronized (EntityUtils.class) {
            if (ASSOCIATED_STRUCTURE_COMPARATOR == null) {
                ASSOCIATED_STRUCTURE_COMPARATOR = new AssociatedStructureComparator(
                        getPreferredResearchOrganizationComparator());
            }
            return ASSOCIATED_STRUCTURE_COMPARATOR;
        }
    }

    /**
     * Change the preferred comparator of associated structures.
     *
     * @param comparator the comparator.
     * @since 3.2
     */
    public static void setPreferredAssociatedStructureComparator(AssociatedStructureComparator comparator) {
        synchronized (EntityUtils.class) {
            ASSOCIATED_STRUCTURE_COMPARATOR = comparator;
        }
    }

    /**
     * Replies if the given production has an author who is a PhD student.
     *
     * @param production the production to test.
     * @return {@code true} if one author is a PhD student.
     * @since 3.6
     */
    public static boolean hasPhDStudentAuthor(Production production) {
        final var year = production.getPublicationYear();
        final var d0 = LocalDate.of(year, 1, 1);
        final var d1 = LocalDate.of(year, 12, 31);
        return production.getAuthorships().stream().anyMatch(
                it -> it.getPerson() != null && hasPhdStudentMembership(it.getPerson().getMemberships(), d0, d1));
    }

    private static boolean hasPhdStudentMembership(Set<Membership> memberships, LocalDate d0, LocalDate d1) {
        return memberships.stream().anyMatch(it -> it.getMemberStatus() == MemberStatus.PHD_STUDENT && it.isActiveIn(d0, d1));
    }

    /**
     * Replies if the given production has an author who is a Postdoc.
     *
     * @param production the production to test.
     * @return {@code true} if one author is a Postdoc.
     * @since 3.6
     */
    public static boolean hasPostdocAuthor(Production production) {
        final var year = production.getPublicationYear();
        final var d0 = LocalDate.of(year, 1, 1);
        final var d1 = LocalDate.of(year, 12, 31);
        return production.getAuthorships().stream().anyMatch(
                it -> it.getPerson() != null && hasPostdocMembership(it.getPerson().getMemberships(), d0, d1));
    }

    private static boolean hasPostdocMembership(Set<Membership> memberships, LocalDate d0, LocalDate d1) {
        return memberships.stream().anyMatch(it -> it.getMemberStatus() == MemberStatus.POSTDOC && it.isActiveIn(d0, d1));
    }

    /**
     * Replies a string representation of the given entity with the given parameters in the representation.
     *
     * @param entity     the entity to output.
     * @param parameters the parameters to add in the returned representation. The parameters is a list of values to append to the string representation.
     * @return the string representation.
     * @since 4.0
     */
    public static String toString(IdentifiableEntity entity, Object... parameters) {
        final var buffer = new StringBuilder();
        if (entity != null) {
            buffer.append(entity.getClass().getSimpleName()).append("@ID=").append(entity.getId()); //$NON-NLS-1$
            for (final var parameter : parameters) {
                if (parameter != null) {
                    final var value = parameter.toString();
                    if (!Strings.isNullOrEmpty(value)) {
                        buffer.append("|").append(value); //$NON-NLS-1$
                    }
                }
            }
        }
        return buffer.toString();
    }

}
