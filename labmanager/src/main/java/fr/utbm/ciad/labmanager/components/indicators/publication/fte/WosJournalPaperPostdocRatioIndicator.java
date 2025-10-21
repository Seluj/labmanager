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

import fr.utbm.ciad.labmanager.components.indicators.members.fte.PostdocFteIndicator;
import fr.utbm.ciad.labmanager.components.indicators.publication.count.PostdocWosJournalPaperCountIndicator;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Calculate the number of ranked papers per postdoc per year for WoS journals.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Component
public class WosJournalPaperPostdocRatioIndicator extends AbstractRankedJournalPaperFteRatioIndicator {

    private static final long serialVersionUID = -25645747560142249L;

    /**
     * Constructor.
     *
     * @param messages     the provider of messages.
     * @param constants    the accessor to the constants.
     * @param fteIndicator the indicator that counts the postdoc.
     * @param paperCount   the counter of WoS papers.
     */
    public WosJournalPaperPostdocRatioIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired PostdocFteIndicator fteIndicator,
            @Autowired PostdocWosJournalPaperCountIndicator paperCount) {
        super(messages, constants, fteIndicator, paperCount);
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "wosJournalPaperPostdocRatioIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithYears(locale, "wosJournalPaperPostdocRatioIndicator.label"); //$NON-NLS-1$
    }

}
