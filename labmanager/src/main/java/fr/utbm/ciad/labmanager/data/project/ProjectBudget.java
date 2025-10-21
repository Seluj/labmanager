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

package fr.utbm.ciad.labmanager.data.project;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.funding.FundingScheme;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Description of a specific budget for a project.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Entity
@Table(name = "ProjectBudgets")
public class ProjectBudget implements Serializable, AttributeProvider, Comparable<ProjectBudget>, IdentifiableEntity {

    private static final long serialVersionUID = -2642101613859032867L;

    /**
     * Identifier of the budget.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Budget only for the research organization.
     */
    @Column
    private float budget;

    /**
     * Funding scheme of the project.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private FundingScheme fundingScheme = FundingScheme.NOT_FUNDED;

    /**
     * Grant number of the project.
     */
    @Column
    private String fundingReference;

    /**
     * Construct an empty member.
     */
    public ProjectBudget() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.fundingScheme);
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
        final var other = (ProjectBudget) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.fundingScheme, other.fundingScheme);
    }

    @Override
    public int compareTo(ProjectBudget o) {
        return EntityUtils.getPreferredProjectBudgetComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (getBudget() > 0f) {
            consumer.accept("budget", Float.valueOf(getBudget())); //$NON-NLS-1$
        }
        if (getFundingScheme() != null) {
            consumer.accept("fundingScheme", getFundingScheme()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getFundingReference())) {
            consumer.accept("grant", getFundingReference()); //$NON-NLS-1$
        }
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
     * Replies the funding scheme of the project.
     *
     * @return the funding scheme.
     */
    public FundingScheme getFundingScheme() {
        return this.fundingScheme;
    }

    /**
     * Change the funding scheme of the project.
     *
     * @param fundingScheme the funding scheme.
     */
    public void setFundingScheme(FundingScheme fundingScheme) {
        if (fundingScheme == null) {
            this.fundingScheme = FundingScheme.NOT_FUNDED;
        } else {
            this.fundingScheme = fundingScheme;
        }
    }

    /**
     * Change the funding scheme of the project.
     *
     * @param fundingScheme the funding scheme.
     */
    public final void setFundingScheme(String fundingScheme) {
        try {
            setFundingScheme(FundingScheme.valueOfCaseInsensitive(fundingScheme));
        } catch (Throwable ex) {
            setFundingScheme((FundingScheme) null);
        }
    }

    /**
     * Replies the grant number of the project.
     *
     * @return the grant number.
     */
    public String getFundingReference() {
        return this.fundingReference;
    }

    /**
     * Change the grant number of the project.
     *
     * @param grant the grant number.
     */
    public void setFundingReference(String grant) {
        this.fundingReference = Strings.emptyToNull(grant);
    }

    /**
     * Replies the budget for the local organization in Kilo euros.
     *
     * @return the budget in kilo euros.
     */
    public float getBudget() {
        return this.budget;
    }

    /**
     * Set the budget for the local organization in Kilo euros.
     *
     * @param budget the budget in kilo euros.
     */
    public void setBudget(float budget) {
        if (budget > 0f) {
            this.budget = budget;
        } else {
            this.budget = 0f;
        }
    }

    /**
     * Set the budget for the local organization in Kilo euros.
     *
     * @param budget the budget in kilo euros.
     */
    public final void setBudget(Number budget) {
        if (budget != null) {
            setBudget(budget.floatValue());
        } else {
            setBudget(0f);
        }
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this);
    }

}
