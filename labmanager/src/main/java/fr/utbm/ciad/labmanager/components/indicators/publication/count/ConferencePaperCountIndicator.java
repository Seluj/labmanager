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
import fr.utbm.ciad.labmanager.services.publication.type.ConferencePaperService;
import fr.utbm.ciad.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Count the number of conference papers for an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Component
public class ConferencePaperCountIndicator extends AbstractConferencePaperCountIndicator {

    private static final long serialVersionUID = -3244698493378696516L;

    /**
     * Constructor.
     *
     * @param messages               the provider of messages.
     * @param constants              the accessor to the constants.
     * @param conferencePaperService the service for accessing the conference papers.
     */
    public ConferencePaperCountIndicator(
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired ConferencePaperService conferencePaperService) {
        super(messages, constants, conferencePaperService, null);
    }

    @Override
    public String getName(Locale locale) {
        return getMessage(locale, "conferencePaperCountIndicator.name"); //$NON-NLS-1$
    }

    @Override
    public String getLabel(Unit unit, Locale locale) {
        return getLabelWithYears(locale, "conferencePaperCountIndicator.label"); //$NON-NLS-1$
    }

}
