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

package fr.utbm.ciad.labmanager.services.indicator;

import fr.utbm.ciad.labmanager.components.indicators.Indicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.indicator.GlobalIndicators;
import fr.utbm.ciad.labmanager.data.indicator.GlobalIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.AbstractService;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service related to the global indicators.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Service
public class GlobalIndicatorsService extends AbstractService {

    private static final long serialVersionUID = -4318074626202678598L;

    private static final int MAX_CACHE_AGE = 7;

    private final GlobalIndicatorsRepository indicatorRepository;

    private final List<? extends Indicator> allIndicators;

    private final Map<String, Indicator> allIndicatorsPerKey;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param indicatorRepository the global indicator repository.
     * @param allIndicators       the list of all the indicators that were install in the app.
     * @param messages            the provider of localized messages.
     * @param constants           the accessor to the live constants.
     * @param sessionFactory      the factory of JPA session.
     */
    public GlobalIndicatorsService(
            @Autowired GlobalIndicatorsRepository indicatorRepository,
            @Autowired List<? extends Indicator> allIndicators,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(messages, constants, sessionFactory);
        this.indicatorRepository = indicatorRepository;
        this.allIndicators = allIndicators;
        this.allIndicatorsPerKey = this.allIndicators.stream().collect(
                Collectors.toMap(Indicator::getKey, Function.identity()));
    }

    /**
     * Ensure that the global indicators' object is created.
     *
     * @param logger the logger to be used.
     * @return the global indicators' object.
     */
    public GlobalIndicators getGlobalIndicatorsNeverNull(Logger logger) {
        final var opt = this.indicatorRepository.findAll().stream().findFirst();
        GlobalIndicators gi;
        if (opt.isEmpty()) {
            gi = createGlobalIndicators(logger);
        } else {
            gi = opt.get();
        }
        return gi;
    }

    /**
     * Replies the global indicators.
     *
     * @return the indicators or {@code null} if there is no global indicators yet.
     */
    public GlobalIndicators getGlobalIndicatorsOrNull() {
        final var opt = this.indicatorRepository.findAll().stream().findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    /**
     * Create the global indicators' object.
     *
     * @param logger the logger to be used.
     * @return a new global indicators' object.
     */
    protected GlobalIndicators createGlobalIndicators(Logger logger) {
        logger.info("Creating global indicators into the database"); //$NON-NLS-1$
        final GlobalIndicators gi = new GlobalIndicators();
        this.indicatorRepository.save(gi);
        return gi;
    }

    /**
     * Replies all the visible global indicators in the order that they should be displayed.
     *
     * @param logger the logger to be used.
     * @return the visible indicators.
     */
    public List<? extends Indicator> getVisibleIndicators(Logger logger) {
        return getVisibleIndicatorStream(logger).collect(Collectors.toList());
    }

    /**
     * Replies in a stream all the visible global indicators in the order that they should be displayed.
     *
     * @param logger the logger to be used.
     * @return the visible indicators in a stream.
     */
    public Stream<? extends Indicator> getVisibleIndicatorStream(Logger logger) {
        final var indicatorKeys = getGlobalIndicatorsNeverNull(logger).getVisibleIndicatorKeyList();
        return indicatorKeys.stream().map(it -> this.allIndicatorsPerKey.get(it)).filter(it -> it != null);
    }

    /**
     * Replies all the invisible global indicators.
     *
     * @param logger the logger to be used.
     * @return the invisible indicators.
     */
    public List<? extends Indicator> getInvisibleIndicators(Logger logger) {
        final var visibles = new TreeSet<>(getGlobalIndicatorsNeverNull(logger).getVisibleIndicatorKeyList());
        if (visibles.isEmpty()) {
            return this.allIndicators;
        }
        return this.allIndicators.stream().filter(it -> {
            return !visibles.contains(it.getKey());
        }).collect(Collectors.toList());
    }

    /**
     * Save or create the global indicators.
     *
     * @param visibleIndicators the list of th keys of the visible indicators.
     * @param logger            the logger to be used.
     */
    public void setVisibleIndicators(List<String> visibleIndicators, Logger logger) {
        final var keys = new StringBuilder();
        for (final String key : visibleIndicators) {
            if (keys.length() > 0) {
                keys.append(","); //$NON-NLS-1$
            }
            keys.append(key);
        }
        setVisibleIndicators(keys.toString(), logger);
    }

    /**
     * Save or create the global indicators.
     *
     * @param visibleIndicators the list of th keys of the visible indicators.
     * @param logger            the logger to be used.
     */
    public void setVisibleIndicators(String visibleIndicators, Logger logger) {
        final var gi = getGlobalIndicatorsNeverNull(logger);
        gi.setVisibleIndicatorKeys(visibleIndicators);
        this.indicatorRepository.save(gi);
        logger.info("Global indicators are saved into the database"); //$NON-NLS-1$
    }

    /**
     * Replies the values of the visibles indicators, without reading the cache.
     * The values are computed on-the-fly without reading any cache system.
     *
     * @param organization the organization for which the indicators must be computed.
     * @param logger       the logger to use for put a message in the log.
     * @return the map from the indicator keys to the values.
     */
    public Map<String, Number> getVisibleIndicatorsValues(ResearchOrganization organization, Logger logger) {
        return getVisibleIndicatorStream(logger).collect(Collectors.toConcurrentMap(
                it -> it.getKey(),
                it -> it.getNumericValue(organization, logger)));
    }

    /**
     * Replies the indicators and their associated values of the visibles indicators, without reading the cache.
     * The values are computed on-the-fly without reading any cache system.
     *
     * @param organization the organization for which the indicators must be computed.
     * @param useCache     indicates if the value cache must be used or not. If this flag is {@code false}, the values are neither
     *                     read from the cache system nor written back in the cache system.
     * @param logger       the logger to use for put a message in the log.
     * @return the map from the indicator keys to the values.
     */
    public List<Pair<? extends Indicator, Number>> getVisibleIndicatorsWithValues(ResearchOrganization organization, boolean useCache, Logger logger) {
        final var gi = getGlobalIndicatorsNeverNull(logger);
        if (useCache) {
            final Map<String, Number> cache;
            if (gi.getCacheAge() > MAX_CACHE_AGE) {
                gi.resetCachedValues();
                cache = Collections.emptyMap();
            } else {
                cache = gi.getCachedValues();
            }
            assert cache != null;
            return getVisibleIndicatorStream(logger)
                    .map(it -> {
                        var value = cache.get(it.getKey());
                        if (value == null) {
                            value = it.getNumericValue(organization, logger);
                            gi.setCachedValues(it.getKey(), value);
                            this.indicatorRepository.save(gi);
                        }
                        return Pair.of(it, value);
                    })
                    .collect(Collectors.toList());
        }
        return getVisibleIndicatorStream(logger)
                .map(it -> Pair.of(it, it.getNumericValue(organization, logger)))
                .collect(Collectors.toList());
    }

    /**
     * Clear the cache content.
     *
     * @param logger the logger to be used.
     */
    public void clearCache(Logger logger) {
        final var gi = getGlobalIndicatorsOrNull();
        if (gi != null) {
            gi.resetCachedValues();
            this.indicatorRepository.save(gi);
            logger.info("Global indicators have been cleared from the database"); //$NON-NLS-1$
        }
    }

}
