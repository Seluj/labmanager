/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
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

package fr.ciadlab.labmanager.entities.project;

import java.util.Locale;

import com.google.common.base.Strings;
import fr.ciadlab.labmanager.configuration.BaseMessageSource;
import org.springframework.context.support.MessageSourceAccessor;

/** Describe the category of a project.
 * The order of the items (their ordinal numbers) is from the less important to the more important.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.0
 */
public enum ProjectCategory {

	/** Project that is open source, freely available on Internet.
	 */
	OPEN_SOURCE {
		@Override
		public boolean isContractualProject() {
			return false;
		}
	},

	/** Project that is auto-funding.
	 */
	AUTO_FUNDING {
		@Override
		public boolean isContractualProject() {
			return false;
		}
	},

	/** Project with not academic partner.
	 */
	NOT_ACADEMIC_PROJECT {
		@Override
		public boolean isContractualProject() {
			return true;
		}
	},

	/** Project in a competitive call.
	 */
	COMPETITIVE_CALL_PROJECT {
		@Override
		public boolean isContractualProject() {
			return true;
		}
	};

	private static final String MESSAGE_PREFIX = "projectCateogry."; //$NON-NLS-1$
	
	private MessageSourceAccessor messages;

	/** Replies the message accessor to be used.
	 *
	 * @return the accessor.
	 */
	public MessageSourceAccessor getMessageSourceAccessor() {
		if (this.messages == null) {
			this.messages = BaseMessageSource.getStaticMessageSourceAccessor();
		}
		return this.messages;
	}

	/** Change the message accessor to be used.
	 *
	 * @param messages the accessor.
	 */
	public void setMessageSourceAccessor(MessageSourceAccessor messages) {
		this.messages = messages;
	}

	/** Replies the label of the category in the current language.
	 *
	 * @return the label of the category in the current language.
	 */
	public String getLabel() {
		final String label = getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + name());
		return Strings.nullToEmpty(label);
	}

	/** Replies the label of the category in the given language.
	 *
	 * @param locale the locale to use.
	 * @return the label of the category in the given  language.
	 */
	public String getLabel(Locale locale) {
		final String label = getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + name(), locale);
		return Strings.nullToEmpty(label);
	}

	/** Replies the category that corresponds to the given name, with a case-insensitive
	 * test of the name.
	 *
	 * @param name the name of the category, to search for.
	 * @return the category.
	 * @throws IllegalArgumentException if the given name does not corresponds to a category.
	 */
	public static ProjectCategory valueOfCaseInsensitive(String name) {
		if (!Strings.isNullOrEmpty(name)) {
			for (final ProjectCategory ranking : values()) {
				if (name.equalsIgnoreCase(ranking.name())) {
					return ranking;
				}
			}
		}
		throw new IllegalArgumentException("Invalid category: " + name); //$NON-NLS-1$
	}

	/** Replies the ordinal number of this item in reverse order.
	 *
	 * @return the ordinal from the end
	 * @see #ordinal()
	 * @since 3.0
	 */
	public int reverseOrdinal() {
		return values().length - ordinal() - 1;
	}

	/** Replies if the category of project has a contract (public or private).
	 *
	 * @return {@code true} if the project category needs a contract or a consortium agreement.
	 * @since 3.6
	 */
	public abstract boolean isContractualProject();

}
