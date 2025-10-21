/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
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

package fr.utbm.ciad.labmanager.components.indicators.publication.count;

import fr.utbm.ciad.labmanager.components.indicators.AbstractAnnualIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.ConferencePaper;
import fr.utbm.ciad.labmanager.services.publication.type.ConferencePaperService;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Count the number of conference papers for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public abstract class AbstractConferencePaperCountIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = -3026722865225605289L;
    private final Predicate<? super ConferencePaper> filter;
    private final ConferencePaperService conferencePaperService;

    /**
     * Constructor.
     *
     * @param messages               the provider of messages.
     * @param constants              the accessor to the constants.
     * @param conferencePaperService the service for accessing the conference papers.
     * @param filter                 the filter to apply on the paper collection.
     */
    public AbstractConferencePaperCountIndicator(
            MessageSourceAccessor messages, ConfigurationConstants constants,
            ConferencePaperService conferencePaperService,
            Predicate<? super ConferencePaper> filter) {
        this(messages, constants, AbstractAnnualIndicator::sum, conferencePaperService, filter);
    }

    /**
     * Constructor.
     *
     * @param messages               the provider of messages.
     * @param constants              the accessor to the constants.
     * @param mergingFunction        the function that should be used for merging the annual values.
     *                               If it is {@code null}, the {@link #sum(Map)} is used.
     * @param conferencePaperService the service for accessing the conference papers.
     * @param filter                 the filter to apply on the paper collection.
     */
    public AbstractConferencePaperCountIndicator(
            MessageSourceAccessor messages, ConfigurationConstants constants,
            Function<Map<Integer, Number>, Number> mergingFunction,
            ConferencePaperService conferencePaperService,
            Predicate<? super ConferencePaper> filter) {
        super(messages, constants, mergingFunction);
        this.conferencePaperService = conferencePaperService;
        this.filter = filter;
    }

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var papers = this.conferencePaperService.getConferencePapersByOrganizationId(organization.getId(), true);
        //
        var stream = filterByYearWindow(papers, it -> Integer.valueOf(it.getPublicationYear()))
                .filter(it -> {
                    final PublicationType type = it.getType();
                    return type == PublicationType.INTERNATIONAL_CONFERENCE_PAPER || type == PublicationType.NATIONAL_CONFERENCE_PAPER;
                });
        if (this.filter != null) {
            stream = stream.filter(this.filter);
        }
        //
        final Map<Integer, Number> annualCounts = stream.collect(Collectors.toConcurrentMap(
                it -> Integer.valueOf(it.getPublicationYear()),
                it -> Integer.valueOf(1),
                (a, b) -> Integer.valueOf(a.intValue() + b.intValue())));
        //
        setComputationDetails(annualCounts);
        return annualCounts;
    }

}
