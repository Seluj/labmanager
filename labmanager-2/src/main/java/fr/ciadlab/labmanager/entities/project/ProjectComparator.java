/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.entities.project;

import java.time.LocalDate;
import java.util.Comparator;

import fr.ciadlab.labmanager.utils.funding.FundingScheme;
import fr.ciadlab.labmanager.utils.trl.TRL;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Comparator of projects. The order of the projects depends on the implementation
 * of this interface.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
@Component
@Primary
public class ProjectComparator implements Comparator<Project> {

	@Override
	public int compare(Project o1, Project o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return Integer.MIN_VALUE;
		}
		if (o2 == null) {
			return Integer.MAX_VALUE;
		}
		int cmp = compareDate(o1.getStartDate(), o2.getStartDate());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareFundingScheme(o1.getFundingScheme(), o2.getFundingScheme());
		if (cmp != 0) {
			return cmp;
		}
		cmp = StringUtils.compare(o1.getAcronym(), o2.getAcronym());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareTRL(o1.getTRL(), o2.getTRL());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareActivityType(o1.getActivityType(), o2.getActivityType());
		if (cmp != 0) {
			return cmp;
		}
		cmp = StringUtils.compare(o1.getScientificTitle(), o2.getScientificTitle());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Float.compare(o1.getBudget(), o2.getBudget());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Float.compare(o1.getGlobalBudget(), o2.getGlobalBudget());
		if (cmp != 0) {
			return cmp;
		}
		return Integer.compare(o1.getId(), o2.getId());
	}

	/** Null-safe comparison the two funding schemes.
	 * 
	 * @param v0 the first value.
	 * @param v1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareFundingScheme(FundingScheme v0, FundingScheme v1) {
		if (v0 == v1) {
			return 0;
		}
		if (v0 == null) {
			return Integer.MIN_VALUE;
		}
		if (v1 == null) {
			return Integer.MAX_VALUE;
		}
		return v0.compareTo(v1);
	}

	/** Null-safe comparison the two TRLs.
	 * 
	 * @param v0 the first value.
	 * @param v1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareTRL(TRL v0, TRL v1) {
		if (v0 == v1) {
			return 0;
		}
		if (v0 == null) {
			return Integer.MIN_VALUE;
		}
		if (v1 == null) {
			return Integer.MAX_VALUE;
		}
		return v0.compareTo(v1);
	}

	/** Null-safe comparison the two activity types.
	 * 
	 * @param v0 the first value.
	 * @param v1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareActivityType(ProjectActivityType v0, ProjectActivityType v1) {
		if (v0 == v1) {
			return 0;
		}
		if (v0 == null) {
			return Integer.MIN_VALUE;
		}
		if (v1 == null) {
			return Integer.MAX_VALUE;
		}
		return v0.compareTo(v1);
	}

	/** Null-safe comparison the two dates.
	 * 
	 * @param s0 the first value.
	 * @param s1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareDate(LocalDate d0, LocalDate d1) {
		if (d0 == d1) {
			return 0;
		}
		if (d0 == null) {
			return Integer.MIN_VALUE;
		}
		if (d1 == null) {
			return Integer.MAX_VALUE;
		}
		return d1.compareTo(d0);
	}

}
