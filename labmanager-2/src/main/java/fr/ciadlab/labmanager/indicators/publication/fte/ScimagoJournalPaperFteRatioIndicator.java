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

package fr.ciadlab.labmanager.indicators.publication.fte;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.indicators.members.fte.PermanentResearcherFteIndicator;
import fr.ciadlab.labmanager.indicators.publication.count.ScimagoJournalPaperCountIndicator;
import fr.ciadlab.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/** Calculate the number of ranked papers per full-time equivalent (FTE) per year for Scimago journals. 
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Component
public class ScimagoJournalPaperFteRatioIndicator extends AbstractRankedJournalPaperFteRatioIndicator {

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 * @param fteIndicator the indicator that counts the FTE.
	 * @param paperCount the counter of Scimago papers.
	 */
	public ScimagoJournalPaperFteRatioIndicator(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired PermanentResearcherFteIndicator fteIndicator,
			@Autowired ScimagoJournalPaperCountIndicator paperCount) {
		super(messages, constants, fteIndicator, paperCount);
	}

	@Override
	public String getName() {
		return getMessage("scimagoJournalPaperFteRatioIndicator.name"); //$NON-NLS-1$
	}

	@Override
	public String getLabel(Unit unit) {
		return getLabelWithYears("scimagoJournalPaperFteRatioIndicator.label"); //$NON-NLS-1$
	}

}
