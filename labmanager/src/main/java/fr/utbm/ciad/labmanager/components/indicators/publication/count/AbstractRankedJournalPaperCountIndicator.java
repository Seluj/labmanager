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
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.services.publication.type.JournalPaperService;
import fr.utbm.ciad.labmanager.utils.ranking.JournalRankingSystem;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Count the number of ranked journal papers for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
public abstract class AbstractRankedJournalPaperCountIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = 2989065255521518504L;
    private final Predicate<? super JournalPaper> filter;
    private final JournalPaperService journalPaperService;

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param journalPaperService the service for accessing the journal papers.
     * @param filter              the filter to be used for the publications.
     */
    public AbstractRankedJournalPaperCountIndicator(
            MessageSourceAccessor messages, ConfigurationConstants constants,
            JournalPaperService journalPaperService,
            Predicate<? super JournalPaper> filter) {
        this(messages, constants, AbstractAnnualIndicator::sum, journalPaperService, filter);
    }

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param mergingFunction     the function that should be used for merging the annual values.
     *                            If it is {@code null}, the {@link #sum(Map)} is used.
     * @param journalPaperService the service for accessing the journal papers.
     * @param filter              the filter to be used for the publications.
     */
    public AbstractRankedJournalPaperCountIndicator(
            MessageSourceAccessor messages, ConfigurationConstants constants,
            Function<Map<Integer, Number>, Number> mergingFunction,
            JournalPaperService journalPaperService,
            Predicate<? super JournalPaper> filter) {
        super(messages, constants, mergingFunction);
        this.journalPaperService = journalPaperService;
        this.filter = filter;

    }

    /**
     * Replies the journal ranking system to be used.
     *
     * @return the journal ranking system to be used.
     */
    public abstract JournalRankingSystem getJournalRankingSystem();

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var papers = this.journalPaperService.getJournalPapersByOrganizationId(organization.getId(), true, true);
        //
        var stream = filterByYearWindow(papers, it -> Integer.valueOf(it.getPublicationYear()));
        switch (getJournalRankingSystem()) {
            case SCIMAGO:
                stream = stream.filter(it -> QuartileRanking.normalize(it.getScimagoQIndex()) != QuartileRanking.NR);
                break;
            case WOS:
                stream = stream.filter(it -> QuartileRanking.normalize(it.getWosQIndex()) != QuartileRanking.NR);
                break;
            default:
                throw new IllegalArgumentException("Unsupported ranking system"); //$NON-NLS-1$
        }
        if (this.filter != null) {
            stream = stream.filter(this.filter);
        }
        //
        final Map<Integer, Number> rankedPapers = stream.collect(Collectors.toConcurrentMap(
                it -> Integer.valueOf(it.getPublicationYear()),
                it -> Integer.valueOf(1),
                (a, b) -> Integer.valueOf(a.intValue() + b.intValue())));
        //
        setComputationDetails(rankedPapers);
        return rankedPapers;
    }

}
