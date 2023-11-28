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

package fr.ciadlab.labmanager.entities.project;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

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
		int cmp = compareBudgets(o1.getBudgets(), o2.getBudgets());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareDate(o1.getStartDate(), o2.getStartDate());
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
		cmp = Float.compare(o1.getGlobalBudget(), o2.getGlobalBudget());
		if (cmp != 0) {
			return cmp;
		}
		return Integer.compare(o1.getId(), o2.getId());
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

	/** Null-safe comparison the two budgets.
	 * 
	 * @param s0 the first value.
	 * @param s1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareBudgets(ProjectBudget b0, ProjectBudget b1) {
		if (b0 == b1) {
			return 0;
		}
		if (b0 == null) {
			return Integer.MIN_VALUE;
		}
		if (b1 == null) {
			return Integer.MAX_VALUE;
		}
		return b0.compareTo(b1);
	}

	/** Null-safe comparison the two budgets.
	 * 
	 * @param s0 the first value.
	 * @param s1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareBudgets(List<ProjectBudget> b0, List<ProjectBudget> b1) {
		if (b0 == b1) {
			return 0;
		}
		if (b0 == null) {
			return Integer.MIN_VALUE;
		}
		if (b1 == null) {
			return Integer.MAX_VALUE;
		}
		for (int i = 0; i < b0.size() && i < b1.size(); ++i) {
			final ProjectBudget p0 = b0.get(i); 
			final ProjectBudget p1 = b1.get(i);
			final int cmp = compareBudgets(p0, p1);
			if (cmp != 0) {
				return cmp;
			}
		}
		if (b0.size() < b1.size()) {
			return Integer.MIN_VALUE;
		}
		if (b0.size() > b1.size()) {
			return Integer.MAX_VALUE;
		}
		return 0;
	}

}
