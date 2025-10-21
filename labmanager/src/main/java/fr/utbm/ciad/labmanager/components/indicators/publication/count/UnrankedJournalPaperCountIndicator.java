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
import fr.utbm.ciad.labmanager.services.publication.type.JournalPaperService;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Count the number of unranked journal papers for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Component
public class UnrankedJournalPaperCountIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = 8586666423829525726L;

    private final JournalPaperService journalPaperService;

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param journalPaperService the service for accessing the journal papers.
     */
    public UnrankedJournalPaperCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired JournalPaperService journalPaperService) {
        super(messages, constants, AbstractAnnualIndicator::sum);
        this.journalPaperService = journalPaperService;
    }

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var papers = this.journalPaperService.getJournalPapersByOrganizationId(organization.getId(), true, true);
        //
        final Map<Integer, Number> rankedPapers = filterByYearWindow(papers, it -> Integer.valueOf(it.getPublicationYear()))
                .filter(it -> !it.isRanked())
                .collect(Collectors.toConcurrentMap(
                        it -> Integer.valueOf(it.getPublicationYear()),
                        it -> Integer.valueOf(1),
                        (a, b) -> Integer.valueOf(a.intValue() + b.intValue())));
        //
        setComputationDetails(rankedPapers);
        return rankedPapers;
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "unrankedJournalPaperCountIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getMessage(locale, "unrankedJournalPaperCountIndicator.label"); //$NON-NLS-1$
    }

}
