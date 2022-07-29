/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the Systems and Transportation Laboratory ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the SeT.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.entities.publication.type;

import java.util.Objects;
import java.util.function.BiConsumer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import fr.ciadlab.labmanager.entities.journal.Journal;
import fr.ciadlab.labmanager.entities.publication.Publication;
import fr.ciadlab.labmanager.entities.ranking.QuartileRanking;
import fr.ciadlab.labmanager.utils.HashCodeUtils;
import org.apache.jena.ext.com.google.common.base.Strings;

/** Paper in a journal.
 *
 * <p>This type is equivalent to the BibTeX types: {@code article}, {@code incollection}.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "JournalPapers")
@PrimaryKeyJoinColumn(name = "id")
public class JournalPaper extends Publication {

	private static final long serialVersionUID = -3322028380433314352L;

	/** Number that represent the volume of the journal.
	 */
	@Column
	private String volume;

	/** Number of the journal.
	 */
	@Column
	private String number;

	/** Range of pages that corresponds to the paper in the journal.
	 */
	@Column
	private String pages;

	/** Reference to the journal.
	 */
	@ManyToOne
	private Journal journal;

	/** Construct a journal paper with the given values.
	 *
	 * @param publication the publication to copy.
	 * @param volume the volume of the journal.
	 * @param number the number of the journal.
	 * @param pages the pages in the journal.
	 */
	public JournalPaper(Publication publication, String volume, String number, String pages) {
		super(publication);
		this.volume = volume;
		this.number = number;
		this.pages = pages;
	}

	/** Construct an empty journal paper.
	 */
	public JournalPaper() {
		//
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = HashCodeUtils.add(h, this.volume);
		h = HashCodeUtils.add(h, this.number);
		h = HashCodeUtils.add(h, this.pages);
		h = HashCodeUtils.add(h, this.journal);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		final JournalPaper other = (JournalPaper) obj;
		if (!Objects.equals(this.volume, other.volume)) {
			return false;
		}
		if (!Objects.equals(this.number, other.number)) {
			return false;
		}
		if (!Objects.equals(this.pages, other.pages)) {
			return false;
		}
		if (!Objects.equals(this.journal, other.journal)) {
			return false;
		}
		return true;
	}

	@Override
	public void forEachAttribute(BiConsumer<String, Object> consumer) {
		super.forEachAttribute(consumer);
		if (!Strings.isNullOrEmpty(getVolume())) {
			consumer.accept("volume", getVolume()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getNumber())) {
			consumer.accept("number", getNumber()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getPages())) {
			consumer.accept("pages", getPages()); //$NON-NLS-1$
		}		
		final Journal journal = getJournal();
		if (journal != null) {
			consumer.accept("journal", journal); //$NON-NLS-1$
		}
	}

	/** Replies the volume number of the journal in which the publication was published.
	 * 
	 * @return the volume number.
	 */
	public String getVolume() {
		return this.volume;
	}

	/** Change the volume number of the journal in which the publication was published.
	 * 
	 * @param volume the volume number.
	 */
	public void setVolume(String volume) {
		this.volume = Strings.emptyToNull(volume);
	}

	/** Replies the number of the journal in which the publication was published.
	 * 
	 * @return the number.
	 */
	public String getNumber() {
		return this.number;
	}

	/** Change the number of the journal in which the publication was published.
	 * 
	 * @param number the number.
	 */
	public void setNumber(String number) {
		this.number = Strings.emptyToNull(number);
	}

	/** Replies the page range in the journal in which the publication was published.
	 * 
	 * @return the number.
	 */
	public String getPages() {
		return this.pages;
	}

	/** Change the page range in the journal in which the publication was published.
	 * 
	 * @param range the page range.
	 */
	public void setPages(String range) {
		this.pages = Strings.emptyToNull(range);
	}

	/** Replies the journal in which the publication was published.
	 *
	 * @return the journal.
	 */
	public Journal getJournal() {
		return this.journal;
	}

	/** Change the journal in which the publication was published.
	 *
	 * @param journal the journal.
	 */
	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	/** Replies the Scimago Q-index.
	 *
	 * @return the Scimago ranking.
	 */
	public QuartileRanking getScimagoQIndex() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getScimagoQIndexByYear(getPublicationYear());
		}
		return null;
	}

	/** Replies the JCR/Web-of-Science Q-index.
	 *
	 * @return the JCR/WOS ranking.
	 */
	public QuartileRanking getWosQIndex() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getWosQIndexByYear(getPublicationYear());
		}
		return null;
	}

	/** Replies the journal impact factor.
	 *
	 * @return the IF or zero.
	 */
	public float getImpactFactor() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getImpactFactorByYear(getPublicationYear());
		}
		return 0f;
	}

	@Override
	public boolean isRanked() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getScimagoQIndexByYear(getPublicationYear()) != null
				|| journal.getWosQIndexByYear(getPublicationYear()) != null;
		}
		return false;
	}

}
