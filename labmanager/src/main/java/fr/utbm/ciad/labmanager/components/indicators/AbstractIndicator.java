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

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.components.AbstractComponent;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Abstract implementation of a computed value that indicates a key element for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public abstract class AbstractIndicator extends AbstractComponent implements Indicator {

    private static final long serialVersionUID = 607277285581915549L;
    private final Map<Long, Number> values = new TreeMap<>();
    private String key;
    private String details;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public AbstractIndicator(MessageSourceAccessor messages, ConfigurationConstants constants) {
        super(messages, constants);
    }

    /**
     * Replies the start date of the reference period if the duration of this period corresponds to the argument.
     * This function provides the January 1 of the X years before today.
     *
     * @param years number of years for the reference period.
     * @return the start date.
     */
    protected static LocalDate computeStartDate(int years) {
        final var ref = LocalDate.now().getYear() - 1;
        return LocalDate.of(ref - years + 1, 1, 1);
    }

    /**
     * Replies the end date of the reference period if the duration of this period corresponds to the argument.
     * This function provides the December 31 of the previous year than today.
     *
     * @param years number of years for the reference period.
     * @return the end date.
     */
    protected static LocalDate computeEndDate(int years) {
        final var ref = LocalDate.now().getYear() - 1;
        return LocalDate.of(ref - years, 12, 31);
    }

    @Override
    public void clear() {
        this.values.clear();
    }

    @Override
    public String getKey() {
        if (this.key == null) {
            this.key = StringUtils.uncapitalize(getClass().getSimpleName().replace("Indicator$", "")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return this.key;
    }

    /**
     * Replies the label with the year at the end.
     *
     * @param locale    the locale in which the label must be replied.
     * @param key       the key for the message.
     * @param arguments the arguments to put in the label.
     * @return the label with the year.
     */
    protected final String getLabelWithYears(Locale locale, String key, Object... arguments) {
        final var text = new StringBuilder(getMessage(locale, key, arguments));
        final var start = getReferencePeriodStart();
        final var end = getReferencePeriodEnd();
        if (start != null && end != null) {
            final var syear = start.getYear();
            final var eyear = end.getYear();
            if (syear != eyear) {
                text.append(" (").append(syear).append("-").append(eyear).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } else {
                text.append(" (").append(syear).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else if (start != null) {
            final var year = start.getYear();
            text.append(" (").append(year).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (end != null) {
            final var year = end.getYear();
            text.append(" (").append(year).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return text.toString();
    }

    /**
     * Replies the label with the year at the end.
     *
     * @param locale    the locale in which the label must be replied.
     * @param key       the key for the message.
     * @param arguments the arguments to put in the label.
     * @return the label without the year.
     */
    protected final String getLabelWithoutYears(Locale locale, String key, Object... arguments) {
        return getMessage(locale, key, arguments);
    }

    @Override
    public String getComputationDetails() {
        return this.details;
    }

    /**
     * Change the details of the computation.
     *
     * @param details the details of the computation.
     * @since 2.4
     */
    protected void setComputationDetails(String details) {
        this.details = Strings.emptyToNull(details);
    }

    /**
     * Change the details of the computation.
     *
     * @param collection the elements that are inside the explanation.
     * @param name       the function that provides the name of each element
     * @since 2.4
     */
    protected <T> void setComputationDetails(Collection<T> collection, Function<T, String> name) {
        final var bb = new StringBuffer();
        final var index = new AtomicInteger(0);
        collection.stream().map(it -> name.apply(it)).sorted().forEach(it -> {
            if (bb.length() > 0) {
                bb.append("\n"); //$NON-NLS-1$
            }
            if (!Strings.isNullOrEmpty(it)) {
                bb.append(index.incrementAndGet()).append(") ").append(it); //$NON-NLS-1$
            }
        });
        setComputationDetails(bb.toString());
    }

    @Override
    public Number getNumericValue(ResearchOrganization organization, Logger logger) {
        final var value = this.values.computeIfAbsent(Long.valueOf(organization.getId()), it -> {
            logger.info("Computing indicator value for " + getKey()); //$NON-NLS-1$
            final var v = computeValue(organization);
            logger.info(getKey() + " = " + v); //$NON-NLS-1$
            return v;
        });
        return value;
    }

    /**
     * Compute the numeric value of the indicator.
     *
     * @param organization the organization for which the indicator should be computed.
     * @return the numeric value.
     */
    protected abstract Number computeValue(ResearchOrganization organization);

    /**
     * Filter the given collection by years.
     *
     * @param <T>           the type of elements in the collection.
     * @param collection    the collection to filter.
     * @param yearExtractor the extractor of year from the elements.
     * @return the filtered stream.
     */
    protected <T> Stream<T> filterByYearWindow(Collection<T> collection, Function<T, Integer> yearExtractor) {
        final var start = getReferencePeriodStart().getYear();
        final var end = getReferencePeriodEnd().getYear();
        return collection.stream().filter(it -> {
            final var year = yearExtractor.apply(it);
            return year != null && year.intValue() >= start && year.intValue() <= end;
        });
    }

}
