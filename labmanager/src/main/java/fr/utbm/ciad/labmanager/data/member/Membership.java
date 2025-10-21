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

package fr.utbm.ciad.labmanager.data.member;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.supervision.Supervision;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Relation between a person and a research organization.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "Memberships")
public class Membership implements Serializable, AttributeProvider, Comparable<Membership>, IdentifiableEntity, Cloneable {

    private static final long serialVersionUID = 297499358606685801L;

    /**
     * Identifier of the membership.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * First date of involvement.
     */
    @Column
    private LocalDate memberSinceWhen;

    /**
     * Last dat eof involvement.
     */
    @Column
    private LocalDate memberToWhen;

    /**
     * Status of the person in the research organization.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;

    /**
     * Indicates if the membership concerns a permanent position, or not.
     */
    @Column(nullable = false)
    private boolean permanentPosition;

    /**
     * Position of the person in the research organization.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private Responsibility responsibility;

    /**
     * Number of the CNU section of the person related to which membership.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private CnuSection cnuSection;

    /**
     * Number of the CoNRS section of the person related to which membership.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private ConrsSection conrsSection;

    /**
     * Type of job of a not-researcher staff.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private FrenchBap frenchBap;

    /**
     * Indicates if this membership is marked a main position for the associated person.
     * This flag may be used by the services of the application to show or hide this membership.
     * For example, on the person's card, onyl the main positions are shown.
     */
    @Column(nullable = false)
    private boolean isMainPosition = true;

    /**
     * Reference to the person.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Person person;

    /**
     * Reference to the direct research organization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private ResearchOrganization researchOrganization;

    /**
     * Reference to the employer research organization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private ResearchOrganization superResearchOrganization;

    /**
     * Reference to the address of the research organization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationAddress organizationAddress;

    /**
     * List of scientific axes that are associated to this membership.
     *
     * @since 3.5
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<ScientificAxis> scientificAxes = new HashSet<>();

    /**
     * The supervision that is associated to this membership.
     *
     * @since 4.0
     */
    @OneToOne(fetch = FetchType.LAZY)
    private Supervision supervision;

    /**
     * Construct a membership with the given values.
     *
     * @param person         the person.
     * @param orga           the research organization.
     * @param address        the address of the research organization.
     * @param since          the start date of involvement.
     * @param to             the end date of involvement.
     * @param status         the status.
     * @param responsibility the responsibility in the organization.
     * @param cnuSection     the number of the CNU section, or {@code null} if it is unknown or irrelevant.
     * @param conrsSection   the number of the CoNRS section, or {@code null} if it is unknown or irrelevant.
     * @param frenchBap      the type of job for a not-researcher staff.
     * @param isMainPosition indicates if the membership is associated to the main position of the associated person.
     */
    public Membership(Person person, ResearchOrganization orga, OrganizationAddress address, LocalDate since, LocalDate to, MemberStatus status,
                      Responsibility responsibility, CnuSection cnuSection, ConrsSection conrsSection, FrenchBap frenchBap, boolean isMainPosition) {
        this.person = person;
        this.researchOrganization = orga;
        this.organizationAddress = validateAddress(address, this.researchOrganization);
        this.memberSinceWhen = since;
        this.memberToWhen = to;
        this.memberStatus = status;
        this.permanentPosition = validatePermanentPosition(true, status);
        this.responsibility = responsibility;
        this.cnuSection = cnuSection;
        this.conrsSection = conrsSection;
        this.frenchBap = frenchBap;
        this.isMainPosition = isMainPosition;
    }

    /**
     * Construct an empty membership.
     */
    public Membership() {
        //
    }

    /**
     * Validate the permanent flag according to the given membership status.
     *
     * @param permanent     the permanent flag to validate.
     * @param currentStatus the current membership status.
     * @return the permanent flag adapted according to the given membership status.
     */
    protected static boolean validatePermanentPosition(boolean permanent, MemberStatus currentStatus) {
        return permanent && (currentStatus == null || currentStatus.isPermanentPositionAllowed());
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.person);
        h = HashCodeUtils.add(h, this.researchOrganization);
        h = HashCodeUtils.add(h, this.memberSinceWhen);
        h = HashCodeUtils.add(h, this.memberToWhen);
        h = HashCodeUtils.add(h, this.responsibility);
        return h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final var other = (Membership) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.person, other.person)
                && Objects.equals(this.researchOrganization, other.researchOrganization)
                && Objects.equals(this.memberSinceWhen, other.memberSinceWhen)
                && Objects.equals(this.memberToWhen, other.memberToWhen)
                && Objects.equals(this.responsibility, other.responsibility);
    }

    /**
     * Create a copy of this membership without the supervision link.
     *
     * @return the copy of this membership.
     */
    @Override
    public Membership clone() {
        final var newMembership = new Membership();
        newMembership.setPerson(getPerson());
        newMembership.setDirectResearchOrganization(getDirectResearchOrganization());
        newMembership.setSuperResearchOrganization(getSuperResearchOrganization());
        newMembership.setOrganizationAddress(getOrganizationAddress());
        newMembership.setScientificAxes(getScientificAxes());

        newMembership.setMemberSinceWhen(getMemberSinceWhen());
        newMembership.setMemberToWhen(getMemberToWhen());
        newMembership.setMemberStatus(getMemberStatus());
        newMembership.setPermanentPosition(getMemberStatus().isPermanentPositionAllowed());
        newMembership.setResponsibility(getResponsibility());
        newMembership.setCnuSection(getCnuSection());
        newMembership.setConrsSection(getConrsSection());
        newMembership.setFrenchBap(getFrenchBap());
        newMembership.setMainPosition(isMainPosition());

        return newMembership;
    }

    @Override
    public int compareTo(Membership o) {
        return EntityUtils.getPreferredMembershipComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getCnuSection() != null) {
            consumer.accept("cnuSection", Integer.valueOf(getCnuSection().getNumber())); //$NON-NLS-1$
        }
        if (getConrsSection() != null) {
            consumer.accept("conrsSection", Integer.valueOf(getConrsSection().getNumber())); //$NON-NLS-1$
        }
        if (getFrenchBap() != null) {
            consumer.accept("frenchBap", getFrenchBap().getShortName()); //$NON-NLS-1$
        }
        if (getMemberSinceWhen() != null) {
            consumer.accept("memberSinceWhen", getMemberSinceWhen()); //$NON-NLS-1$
        }
        if (getMemberToWhen() != null) {
            consumer.accept("memberToWhen", getMemberToWhen()); //$NON-NLS-1$
        }
        if (getMemberStatus() != null) {
            consumer.accept("memberStatus", getMemberStatus()); //$NON-NLS-1$
        }
        consumer.accept("permanentPosition", Boolean.valueOf(isPermanentPosition())); //$NON-NLS-1$
        if (getResponsibility() != null) {
            consumer.accept("responsibility", getResponsibility()); //$NON-NLS-1$
        }
        if (getOrganizationAddress() != null) {
            consumer.accept("organizationAddress", getOrganizationAddress()); //$NON-NLS-1$
        }
        consumer.accept("isMainPosition", Boolean.valueOf(isMainPosition())); //$NON-NLS-1$
    }

    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Change the membership identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the person related to this membership.
     *
     * @return the person.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the person related to this membership.
     *
     * @param person the person.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Replies the direct research organization related to this membership.
     *
     * @return the organization.
     */
    public ResearchOrganization getDirectResearchOrganization() {
        return this.researchOrganization;
    }

    /**
     * Change the direct research organization related to this membership.
     *
     * @param orga the organization.
     */
    public void setDirectResearchOrganization(ResearchOrganization orga) {
        this.researchOrganization = orga;
    }

    /**
     * Replies the employer research organization related to this membership.
     *
     * @return the organization.
     */
    public ResearchOrganization getSuperResearchOrganization() {
        return this.superResearchOrganization;
    }

    /**
     * Change the employer research organization related to this membership.
     *
     * @param orga the organization.
     */
    public void setSuperResearchOrganization(ResearchOrganization orga) {
        this.superResearchOrganization = orga;
    }

    /**
     * Replies the organization address related to this membership.
     *
     * @return the address or {@code null} if unknown.
     */
    public OrganizationAddress getOrganizationAddress() {
        return this.organizationAddress;
    }

    /**
     * Change the organization address related to this membership.
     *
     * @param address the address.
     */
    public void setOrganizationAddress(OrganizationAddress address) {
        setOrganizationAddress(address, true);
    }

    /**
     * Change the organization address related to this membership.
     *
     * @param address  the address.
     * @param validate indicates if the address ust be validated against the current organization.
     */
    public void setOrganizationAddress(OrganizationAddress address, boolean validate) {
        if (validate) {
            this.organizationAddress = validateAddress(address, getDirectResearchOrganization());
        } else {
            this.organizationAddress = address;
        }
    }

    /**
     * Validate the given address against the addresses that are associated to the given organization.
     * If the given address does not corresponds to an address of the given organization, this function
     * is not validating it.
     *
     * @param address      the address to validate.
     * @param organization the organization that must serve as reference.
     * @return the validated address, or {@code null} if the address is not valid.
     */
    @SuppressWarnings("static-method")
    protected OrganizationAddress validateAddress(OrganizationAddress address, ResearchOrganization organization) {
        if (address != null && organization != null && organization.getAddresses().contains(address)) {
            return address;
        }
        return null;
    }

    /**
     * Replies the first date of involvement in the research organization.
     *
     * @return the date.
     */
    public LocalDate getMemberSinceWhen() {
        return this.memberSinceWhen;
    }

    /**
     * Change the first date of involvement in the research organization.
     *
     * @param date the date.
     */
    public void setMemberSinceWhen(LocalDate date) {
        this.memberSinceWhen = date;
    }

    /**
     * Change the first date of involvement in the research organization.
     *
     * @param date the date.
     */
    public final void setMemberSinceWhen(String date) {
        if (Strings.isNullOrEmpty(date)) {
            setMemberSinceWhen((LocalDate) null);
        } else {
            setMemberSinceWhen(LocalDate.parse(date));
        }
    }

    /**
     * Replies the last date of involvement in the research organization.
     *
     * @return the date.
     */
    public LocalDate getMemberToWhen() {
        return this.memberToWhen;
    }

    /**
     * Change the last date of involvement in the research organization.
     *
     * @param date the date.
     */
    public void setMemberToWhen(LocalDate date) {
        this.memberToWhen = date;
    }

    /**
     * Change the last date of involvement in the research organization.
     *
     * @param date the date.
     */
    public final void setMemberToWhen(String date) {
        if (Strings.isNullOrEmpty(date)) {
            setMemberToWhen((LocalDate) null);
        } else {
            try {
                setMemberToWhen(LocalDate.parse(date));
            } catch (Throwable ex) {
                setMemberToWhen(LocalDate.parse(date));
            }
        }
    }

    /**
     * Replies the status of the member in the research organization.
     *
     * @return the status.
     */
    public MemberStatus getMemberStatus() {
        return this.memberStatus;
    }

    /**
     * Change the status of the member in the research organization.
     *
     * @param status the status.
     */
    public void setMemberStatus(MemberStatus status) {
        this.memberStatus = status;
        // Reset the permanent position flag if needed
        this.permanentPosition = validatePermanentPosition(this.permanentPosition, this.memberStatus);
    }

    /**
     * Change the status of the member in the research organization.
     *
     * @param status the status.
     */
    public final void setMemberStatus(String status) {
        if (Strings.isNullOrEmpty(status)) {
            setMemberStatus((MemberStatus) null);
        } else {
            setMemberStatus(MemberStatus.valueOfCaseInsensitive(status));
        }
    }

    /**
     * Replies if the membership concerns a permanent position.
     *
     * @return {@code true} if the position is permanent.
     */
    public boolean isPermanentPosition() {
        return this.permanentPosition;
    }

    /**
     * Change the flag that indicates if the membership concerns a permanent position.
     *
     * @param permanent {@code true} if the position is permanent.
     */
    public void setPermanentPosition(boolean permanent) {
        this.permanentPosition = validatePermanentPosition(permanent, getMemberStatus());
    }

    /**
     * Change the flag that indicates if the membership concerns a permanent position.
     *
     * @param permanent {@code true} if the position is permanent.
     */
    public final void setPermanentPosition(Boolean permanent) {
        if (permanent == null) {
            setPermanentPosition(false);
        } else {
            setPermanentPosition(permanent.booleanValue());
        }
    }

    /**
     * Replies the CNU section of the member in the research organization.
     * CNU means "Conseil National des Universités".
     *
     * @return the CNU section number or {@code null} if unknown.
     */
    public CnuSection getCnuSection() {
        return this.cnuSection;
    }

    /**
     * Change the CNU section of the member in the research organization.
     * CNU means "Conseil National des Universités".
     *
     * @param cnu the CNU section number or {@code null} if unknown.
     */
    public void setCnuSection(CnuSection cnu) {
        this.cnuSection = cnu;
    }

    /**
     * Change the CNU section of the member in the research organization.
     * CNU means "Conseil National des Universités".
     *
     * @param cnu the CNU section number or {@code null} if unknown.
     */
    public final void setCnuSection(Number cnu) {
        if (cnu == null) {
            setCnuSection((CnuSection) null);
        } else {
            try {
                setCnuSection(CnuSection.valueOf(cnu));
            } catch (Throwable ex) {
                setCnuSection((CnuSection) null);
            }
        }
    }

    /**
     * Change the CNU section of the member in the research organization.
     * CNU means "Conseil National des Universités".
     *
     * @param cnu the CNU section number or {@code null} if unknown.
     */
    public final void setCnuSection(String cnu) {
        if (Strings.isNullOrEmpty(cnu)) {
            setCnuSection((CnuSection) null);
        } else {
            try {
                setCnuSection(CnuSection.valueOfCaseInsensitive(cnu));
            } catch (Throwable ex) {
                setCnuSection((CnuSection) null);
            }
        }
    }

    /**
     * Replies the CoNRS section of the member in the research organization.
     * CoNRS means "Comité national de la recherche scientifique".
     *
     * @return the CoNRS section number or {@code null} if unknown.
     */
    public ConrsSection getConrsSection() {
        return this.conrsSection;
    }

    /**
     * Change the CoNRS section of the member in the research organization.
     * CoNRS means "Comité national de la recherche scientifique".
     *
     * @param conrs the CoNRS section number or {@code null} if unknown.
     */
    public void setConrsSection(ConrsSection conrs) {
        this.conrsSection = conrs;
    }

    /**
     * Change the CoNRS section of the member in the research organization.
     * CoNRS means "Comité national de la recherche scientifique".
     *
     * @param conrs the CoNRS section number or {@code null} if unknown.
     */
    public final void setConrsSection(Number conrs) {
        if (conrs == null) {
            setConrsSection((ConrsSection) null);
        } else {
            try {
                setConrsSection(ConrsSection.valueOf(conrs));
            } catch (Throwable ex) {
                setConrsSection((ConrsSection) null);
            }
        }
    }

    /**
     * Change the CoNRS section of the member in the research organization.
     * CoNRS means "Comité national de la recherche scientifique".
     *
     * @param conrs the CoNRS section number or {@code null} if unknown.
     */
    public final void setConrsSection(String conrs) {
        if (Strings.isNullOrEmpty(conrs)) {
            setConrsSection((ConrsSection) null);
        } else {
            try {
                setConrsSection(ConrsSection.valueOfCaseInsensitive(conrs));
            } catch (Throwable ex) {
                setConrsSection((ConrsSection) null);
            }
        }
    }

    /**
     * Replies the French BAP of the member in the research organization.
     *
     * @return the CNY section number or {@code null} if unknown.
     */
    public FrenchBap getFrenchBap() {
        return this.frenchBap;
    }

    /**
     * Change the French BAP of the member in the research organization.
     *
     * @param bap the French BAP or {@code null} if unknown.
     */
    public void setFrenchBap(FrenchBap bap) {
        this.frenchBap = bap;
    }

    /**
     * Change the French BAP of the member in the research organization.
     *
     * @param bap the French BAP  or {@code null} if unknown.
     */
    public final void setFrenchBap(String bap) {
        if (Strings.isNullOrEmpty(bap)) {
            setFrenchBap((FrenchBap) null);
        } else {
            try {
                setFrenchBap(FrenchBap.valueOfCaseInsensitive(bap));
            } catch (Throwable ex) {
                setFrenchBap((FrenchBap) null);
            }
        }
    }

    /**
     * Replies if the membership is active.
     * A membership is active if the current date is inside the membership time windows.
     *
     * @return {@code true} if the membership time windows contains the current date.
     */
    public boolean isActive() {
        final var now = LocalDate.now();
        return isActiveAt(now);
    }

    /**
     * Replies if the membership is active.
     * A membership is active if the given date is inside the membership time windows.
     *
     * @param now the given date to consider.
     * @return {@code true} if the membership time windows contains the given date.
     */
    public boolean isActiveAt(LocalDate now) {
        final var start = getMemberSinceWhen();
        if (start != null && now.isBefore(start)) {
            return false;
        }
        final var end = getMemberToWhen();
        return end == null || !now.isAfter(end);
    }

    /**
     * Replies if the membership is active during the given time windows.
     * A membership is active if the current date is inside the membership time windows.
     * If a date is {@code null}, it is assumed to be infinity.
     *
     * @param windowStart is the start date of the windows, or {@code null} for an infinite date in the past.
     * @param windowEnd   is the end date of the windows, or {@code null} for an infinite date in the future.
     * @return {@code true} if the membership time windows intersects the given date window.
     */
    public boolean isActiveIn(LocalDate windowStart, LocalDate windowEnd) {
        if (windowStart == null && windowEnd == null) {
            return true;
        }
        final var start = getMemberSinceWhen();
        if (windowStart == null) {
            assert windowEnd != null;
            return start == null || !windowEnd.isBefore(start);
        }
        final var end = getMemberToWhen();
        if (windowEnd == null) {
            assert windowStart != null;
            return end == null || !windowStart.isAfter(end);
        }
        assert windowStart != null;
        assert windowEnd != null;
        if (start != null) {
            if (end != null) {
                return !windowEnd.isBefore(start) && !windowStart.isAfter(end);
            }
            return !windowEnd.isBefore(start);
        }
        if (end != null) {
            return !windowStart.isAfter(end);
        }
        return true;
    }

    /**
     * Replies if the membership is for former member.
     * A membership is for former member if the current date is after the end of the membership.
     *
     * @return {@code true} if the membership is finished.
     */
    public boolean isFormer() {
        final var dt = getMemberToWhen();
        if (dt == null) {
            // Without a end date, the membership is active for ever.
            // So that it cannot be for a former member.
            return false;
        }
        final var now = LocalDate.now();
        return now.isAfter(dt);
    }

    /**
     * Replies if the membership is for future member.
     * A membership is for future member if the current date is before the start of the membership.
     *
     * @return {@code true} if the membership is not yet started.
     */
    public boolean isFuture() {
        final var dt = getMemberSinceWhen();
        if (dt == null) {
            // Without a start date, the membership is active since ever.
            // So that it cannot be for a future member.
            return false;
        }
        final var now = LocalDate.now();
        return now.isBefore(dt);
    }

    /**
     * Replies if this membership is marked as a main position for the associated person.
     * This flag is used by the services to show or hide the membership. For example,
     * on the person's card, only the main position memberships are shown.
     *
     * @return {@code true} if the membership is a main position.
     */
    public boolean isMainPosition() {
        return this.isMainPosition;
    }

    /**
     * Change the flag that indicates if this membership is marked as a main position for the associated person.
     * This flag is used by the services to show or hide the membership. For example,
     * on the person's card, only the main position memberships are shown.
     *
     * @param main {@code true} if the membership is a main position.
     */
    public void setMainPosition(boolean main) {
        this.isMainPosition = main;
    }

    /**
     * Change the flag that indicates if this membership is marked as a main position for the associated person.
     * This flag is used by the services to show or hide the membership. For example,
     * on the person's card, only the main position memberships are shown.
     *
     * @param main {@code Boolean#TRUE} if the membership is a main position. If {@code null}, the {@code true} is
     *             assigned.
     */
    public final void setMainPosition(Boolean main) {
        if (main == null) {
            setMainPosition(true);
        } else {
            setMainPosition(main.booleanValue());
        }
    }

    /**
     * Replies the responsibility of the member in the research organization.
     *
     * @return the responsibility.
     */
    public Responsibility getResponsibility() {
        return this.responsibility;
    }

    /**
     * Change the responsibility of the member in the research organization.
     *
     * @param responsibility the responsibility.
     */
    public void setResponsibility(Responsibility responsibility) {
        this.responsibility = responsibility;
    }

    /**
     * Change the responsibility of the member in the research organization.
     *
     * @param responsibility the responsibility.
     */
    public final void setResponsibility(String responsibility) {
        if (Strings.isNullOrEmpty(responsibility)) {
            setResponsibility((Responsibility) null);
        } else {
            setResponsibility(Responsibility.valueOfCaseInsensitive(responsibility));
        }
    }

    /**
     * Replies the short description of this membership. It is composed of the status, organization and dates.
     * This short description is usually used in the form for describing the membership.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use for generating the labels.
     * @return the short description of the membership.
     */
    public String getShortDescription(MessageSourceAccessor messages, Locale locale) {
        final var b = new StringBuilder();
        b.append(getMemberStatus().getLabel(messages, getPerson().getGender(), false, locale));
        b.append(" - ").append(getDirectResearchOrganization().getAcronymOrName()); //$NON-NLS-1$
        b.append(" ["); //$NON-NLS-1$
        if (getMemberSinceWhen() != null && getMemberToWhen() != null) {
            final var y0 = getMemberSinceWhen().getYear();
            final var y1 = getMemberToWhen().getYear();
            if (y0 != y1) {
                b.append(y0).append("\u2192").append(y1); //$NON-NLS-1$
            } else {
                b.append(y0);
            }
        } else if (getMemberSinceWhen() != null) {
            b.append(getMemberSinceWhen().getYear()).append("\u21A6"); //$NON-NLS-1$
        } else if (getMemberToWhen() != null) {
            b.append("\u21E5").append(getMemberToWhen().getYear()); //$NON-NLS-1$
        }
        b.append("]"); //$NON-NLS-1$
        return b.toString();
    }

    /**
     * Replies the scientific axes that are associated to this membership.
     *
     * @return the scientific axes.
     * @since 3.5
     */
    public Set<ScientificAxis> getScientificAxes() {
        if (this.scientificAxes == null) {
            this.scientificAxes = new HashSet<>();
        }
        return this.scientificAxes;
    }

    /**
     * Change the scientific axes that are associated to this membership.
     * This function updates the relationship from the axis to the membership AND
     * from the membership to the axis.
     *
     * @param axes the scientific axes associated to this membership.
     * @since 3.5
     */
    public void setScientificAxes(Collection<ScientificAxis> axes) {
        if (this.scientificAxes == null) {
            this.scientificAxes = new HashSet<>();
        } else {
            this.scientificAxes.clear();
        }
        if (axes != null && !axes.isEmpty()) {
            this.scientificAxes.addAll(axes);
        }
    }

    /**
     * Replies the supervision that is associated to this membership.
     * The supervision is is associated to a membership that corresponds
     * to a supervisable position.
     *
     * @return the supervision, or {@code null} if no supervision is known.
     * @since 4.0
     */
    public Supervision getSupervision() {
        return this.supervision;
    }

    /**
     * Change the supervision that is associated to this membership.
     * The supervision is is associated to a membership that corresponds
     * to a supervisable position.
     *
     * @param supervision the instance of supervision, or {@code null} if no supervision is known.
     * @since 4.0
     */
    public void setSupervision(Supervision supervision) {
        this.supervision = supervision;
    }

    /**
     * Replies the number of days that this membership has with the given year.
     *
     * @param year the reference year.
     * @return the number of days of intersection between the given year and this membership.
     * @since 3.6
     */
    public int daysInYear(int year) {
        var start = getMemberSinceWhen();
        if (start == null || start.getYear() < year) {
            start = LocalDate.of(year, 1, 1);
        } else if (start.getYear() > year) {
            return 0;
        }
        //
        var end = getMemberToWhen();
        if (end == null || end.getYear() > year) {
            end = LocalDate.of(year, 12, 31);
        } else if (end.getYear() < year) {
            return 0;
        }
        //
        assert start != null && end != null;
        return end.getDayOfYear() - start.getDayOfYear() + 1;
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

    /**
     * Replies the employer organization. If the membership is associated to multiple employing organizations,
     * this function will reply a single one, randomly selected.
     *
     * @return the employer organization.
     * @since 4.0
     */
    public Optional<ResearchOrganization> getEmployer() {
        var referenceOrganization = getSuperResearchOrganization();
        if (referenceOrganization == null) {
            referenceOrganization = getDirectResearchOrganization();
        }
        return referenceOrganization.getEmployingOrganizations().stream().findAny();
    }

}


