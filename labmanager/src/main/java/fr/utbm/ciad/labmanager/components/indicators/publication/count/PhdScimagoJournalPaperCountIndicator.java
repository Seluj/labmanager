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

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.services.publication.type.JournalPaperService;
import fr.utbm.ciad.labmanager.utils.Unit;
import fr.utbm.ciad.labmanager.utils.ranking.JournalRankingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Count the number of journal papers ranked on Scimago with PhD student as author.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Component
public class PhdScimagoJournalPaperCountIndicator extends AbstractRankedJournalPaperCountIndicator {

    private static final long serialVersionUID = 886768545509538658L;

    /**
     * Constructor.
     *
     * @param messages            the provider of messages.
     * @param constants           the accessor to the constants.
     * @param journalPaperService the service for accessing the journal papers.
     */
    public PhdScimagoJournalPaperCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired JournalPaperService journalPaperService) {
        super(messages, constants, journalPaperService, it -> EntityUtils.hasPhDStudentAuthor(it));
    }

    @Override
    public JournalRankingSystem getJournalRankingSystem() {
        return JournalRankingSystem.SCIMAGO;
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "phdScimagoJournalPaperCountIndicator.name", getJournalRankingSystem().getLabel(getMessageSourceAccessor(), locale)); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithYears(locale, "phdScimagoJournalPaperCountIndicator.label", getJournalRankingSystem().getLabel(getMessageSourceAccessor(), locale)); //$NON-NLS-1$
    }

}
