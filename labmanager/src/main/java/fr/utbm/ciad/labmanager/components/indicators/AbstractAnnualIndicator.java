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

package fr.utbm.ciad.labmanager.components.indicators;

import com.google.common.util.concurrent.AtomicDouble;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.springframework.context.support.MessageSourceAccessor;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Abstract implementation of a computed value that indicates a key element for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.3
 */
public abstract class AbstractAnnualIndicator extends AbstractIndicator implements AnnualIndicator {

    /**
     * Default number of years for the year window.
     */
    public static final int DEFAULT_YEAR_WINDOW = 5;
    private static final long serialVersionUID = 3420694450212058330L;
    private final Function<Map<Integer, Number>, Number> mergingFunction;
    private int yearCount = DEFAULT_YEAR_WINDOW;
    private int startYear;

    /**
     * Constructor.
     *
     * @param messages        the provider of messages.
     * @param constants       the accessor to the constants.
     * @param mergingFunction the function that should be used for merging the annual values.
     *                        If it is {@code null}, the {@link #sum(Map)} is used.
     */
    public AbstractAnnualIndicator(MessageSourceAccessor messages, ConfigurationConstants constants,
                                   Function<Map<Integer, Number>, Number> mergingFunction) {
        super(messages, constants);
        this.mergingFunction = mergingFunction;
        this.startYear = LocalDate.now().getYear() - getYearCount();
    }

    /**
     * Sum the values for building a single global value.
     *
     * @param values the values per year.
     * @return the sum value.
     */
    protected static Number sum(Map<Integer, Number> values) {
        final var sum = new AtomicDouble();
        values.values().parallelStream().forEach(it -> {
            sum.addAndGet(it.doubleValue());
        });
        return Double.valueOf(sum.doubleValue());
    }

    /**
     * Average the values for building a single global value.
     *
     * @param values the values per year.
     * @return the average value.
     */
    protected static Number average(Map<Integer, Number> values) {
        final var sum = new AtomicDouble();
        final var count = new AtomicInteger();
        values.values().parallelStream().forEach(it -> {
            sum.addAndGet(it.doubleValue());
            count.incrementAndGet();
        });
        final var n = count.get();
        final double s;
        if (n > 0) {
            s = sum.doubleValue() / n;
        } else {
            s = 0.;
        }
        return Double.valueOf(s);
    }

    @Override
    public int getYearCount() {
        return this.yearCount;
    }

    @Override
    public void setYearCount(int year) {
        this.yearCount = year;
    }

    @Override
    public int getReferenceStartYear() {
        return this.startYear;
    }

    @Override
    public void setReferenceStartYear(int year) {
        this.startYear = year;
    }

    @Override
    protected Number computeValue(ResearchOrganization organization) {
        final var values = getValuesPerYear(organization, getReferenceStartYear(), getReferenceEndYear());
        return mergeValues(values);
    }

    /**
     * Merge the values for building a single global value.
     *
     * @param values the values per year.
     * @return the merged value.
     */
    protected Number mergeValues(Map<Integer, Number> values) {
        if (this.mergingFunction == null) {
            return sum(values);
        }
        return this.mergingFunction.apply(values);
    }

    /**
     * Change the details of the computation.
     *
     * @param collection the elements that are inside the explanation.
     * @since 2.4
     */
    protected <T> void setComputationDetails(Map<Integer, Number> collection) {
        final var bb = new StringBuilder();
        for (final var entry : collection.entrySet()) {
            if (bb.length() > 0) {
                bb.append("\n"); //$NON-NLS-1$
            }
            bb.append(entry.getKey());
            bb.append(") "); //$NON-NLS-1$
            bb.append(entry.getValue());
        }
        setComputationDetails(bb.toString());
    }

}
