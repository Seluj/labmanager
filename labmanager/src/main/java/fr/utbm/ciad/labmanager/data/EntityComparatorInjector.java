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

import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureComparator;
import fr.utbm.ciad.labmanager.data.assostructure.AssociatedStructureHolderComparator;
import fr.utbm.ciad.labmanager.data.conference.ConferenceComparator;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitationComparator;
import fr.utbm.ciad.labmanager.data.journal.JournalComparator;
import fr.utbm.ciad.labmanager.data.jury.JuryMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.MembershipComparator;
import fr.utbm.ciad.labmanager.data.member.NameBasedMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.PersonComparator;
import fr.utbm.ciad.labmanager.data.member.PersonListComparator;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectBudgetComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectComparator;
import fr.utbm.ciad.labmanager.data.project.ProjectMemberComparator;
import fr.utbm.ciad.labmanager.data.publication.comparators.PublicationComparator;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.data.supervision.SupervisionComparator;
import fr.utbm.ciad.labmanager.data.supervision.SupervisorComparator;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityComparator;
import fr.utbm.ciad.labmanager.data.user.UserComparator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring component that is injecting the comparators for the Spring entities.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
public class EntityComparatorInjector {

    @Autowired
    private PersonComparator personComparator;

    @Autowired
    private PersonListComparator personListComparator;

    @Autowired
    private MembershipComparator membershipComparator;

    @Autowired
    private NameBasedMembershipComparator nameMembershipComparator;

    @Autowired
    private ResearchOrganizationComparator organizationComparator;

    @Autowired
    private OrganizationAddressComparator organizationAddressComparator;

    @Autowired
    private PublicationComparator publicationComparator;

    @Autowired
    private JournalComparator journalComparator;

    @Autowired
    private ConferenceComparator conferenceComparator;

    @Autowired
    private JuryMembershipComparator juryMembershipComparator;

    @Autowired
    private PersonInvitationComparator personInvitationComparator;

    @Autowired
    private SupervisorComparator supervisorComparator;

    @Autowired
    private SupervisionComparator supervisionComparator;

    @Autowired
    private ProjectComparator projectComparator;

    @Autowired
    private ProjectMemberComparator projectMemberComparator;

    @Autowired
    private ProjectBudgetComparator projectBudgetComparator;

    @Autowired
    private AssociatedStructureHolderComparator externalStructureHolderComparator;

    @Autowired
    private AssociatedStructureComparator externalStructureComparator;

    @Autowired
    private TeachingActivityComparator teachingActivityComparator;

    @Autowired
    private ScientificAxisComparator scientificAxisComparator;

    @Autowired
    private UserComparator userComparator;

    /**
     * Invoked by the Spring engine is started and this injector is created in memory.
     */
    @PostConstruct
    public void postConstruct() {
        EntityUtils.setPreferredUserComparator(this.userComparator);
        EntityUtils.setPreferredResearchOrganizationComparator(this.organizationComparator);
        EntityUtils.setPreferredOrganizationAddressComparator(this.organizationAddressComparator);
        EntityUtils.setPreferredPersonComparator(this.personComparator);
        EntityUtils.setPreferredPersonListComparator(this.personListComparator);
        EntityUtils.setPreferredMembershipComparator(this.membershipComparator);
        EntityUtils.setPreferredPersonNameBasedMembershipComparator(this.nameMembershipComparator);
        EntityUtils.setPreferredPublicationComparator(this.publicationComparator);
        EntityUtils.setPreferredJournalComparator(this.journalComparator);
        EntityUtils.setPreferredJuryMembershipComparator(this.juryMembershipComparator);
        EntityUtils.setPreferredPersonInvitationComparator(this.personInvitationComparator);
        EntityUtils.setPreferredSupervisorComparator(this.supervisorComparator);
        EntityUtils.setPreferredSupervisionComparator(this.supervisionComparator);
        EntityUtils.setPreferredProjectComparator(this.projectComparator);
        EntityUtils.setPreferredProjectMemberComparator(this.projectMemberComparator);
        EntityUtils.setPreferredProjectBudgetComparator(this.projectBudgetComparator);
        EntityUtils.setPreferredAssociatedStructureHolderComparator(this.externalStructureHolderComparator);
        EntityUtils.setPreferredAssociatedStructureComparator(this.externalStructureComparator);
        EntityUtils.setPreferredTeachingActivityComparator(this.teachingActivityComparator);
        EntityUtils.setPreferredScientificAxisComparator(this.scientificAxisComparator);
        EntityUtils.setPreferredConferenceComparator(this.conferenceComparator);
    }

}
