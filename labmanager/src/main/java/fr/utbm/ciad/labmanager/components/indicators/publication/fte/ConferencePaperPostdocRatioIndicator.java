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

package fr.utbm.ciad.labmanager.components.indicators.publication.fte;

import fr.utbm.ciad.labmanager.components.indicators.AbstractAnnualIndicator;
import fr.utbm.ciad.labmanager.components.indicators.members.fte.PostdocFteIndicator;
import fr.utbm.ciad.labmanager.components.indicators.publication.count.PostdocConferencePaperCountIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Count the number of conference papers per postdoc per year.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Component
public class ConferencePaperPostdocRatioIndicator extends AbstractAnnualIndicator {

    private static final long serialVersionUID = 589984309914933655L;

    private final PostdocConferencePaperCountIndicator paperCount;

    private final PostdocFteIndicator fteIndicator;

    /**
     * Constructor.
     *
     * @param messages     the provider of messages.
     * @param constants    the accessor to the constants.
     * @param fteIndicator the indicator that counts the postdocs.
     * @param paperCount   the indicator for counting the papers.
     */
    public ConferencePaperPostdocRatioIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired PostdocFteIndicator fteIndicator,
            @Autowired PostdocConferencePaperCountIndicator paperCount) {
        super(messages, constants, AbstractAnnualIndicator::average);
        this.fteIndicator = fteIndicator;
        this.paperCount = paperCount;
    }

    @Override
    public Map<Integer, Number> getValuesPerYear(ResearchOrganization organization, int startYear, int endYear) {
        final var rankedPapers = this.paperCount.getValuesPerYear(organization, startYear, endYear);
        final var ftes = this.fteIndicator.getValuesPerYear(organization, startYear, endYear);
        final Map<Integer, Number> ratios = rankedPapers.entrySet().parallelStream().collect(Collectors.toConcurrentMap(
                Map.Entry::getKey,
                it -> {
                    final Number count = it.getValue();
                    final Number fte = ftes.get(it.getKey());
                    if (fte != null && count != null) {
                        return Float.valueOf(count.floatValue() / fte.floatValue());
                    }
                    return Float.valueOf(0f);
                }));
        //
        setComputationDetails(ratios);
        return ratios;
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "conferencePaperPostdocRatioIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getMessage(locale, "conferencePaperPostdocRatioIndicator.label"); //$NON-NLS-1$
    }

}
