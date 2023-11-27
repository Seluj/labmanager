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

package fr.ciadlab.labmanager.entities.publication;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.ciadlab.labmanager.entities.journal.Journal;
import fr.ciadlab.labmanager.utils.HashCodeUtils;
import fr.ciadlab.labmanager.utils.RequiredFieldInForm;
import fr.ciadlab.labmanager.utils.ranking.JournalRankingSystem;
import fr.ciadlab.labmanager.utils.ranking.QuartileRanking;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;

/** Abstract publication that is related to a journal.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
@Entity
@Table(name = "JournalBasedPublications")
@Inheritance(strategy = InheritanceType.JOINED)
@Polymorphism(type = PolymorphismType.IMPLICIT)
public abstract class AbstractJournalBasedPublication extends Publication implements JournalBasedPublication {

	private static final long serialVersionUID = 5389842889949388812L;

	/** Reference to the journal.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Journal journal;

	/** Constructor by copy.
	 *
	 * @param publication the publication to copy.
	 */
	public AbstractJournalBasedPublication(Publication publication) {
		super(publication);
	}

	/** Construct an empty publication.
	 */
	public AbstractJournalBasedPublication() {
		//
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = HashCodeUtils.add(h, this.journal);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		final AbstractJournalBasedPublication other = (AbstractJournalBasedPublication) obj;
		if (!Objects.equals(this.journal, other.journal)) {
			return false;
		}
		return true;
	}

	@Override
	public void forEachAttribute(AttributeConsumer consumer) throws IOException {
		super.forEachAttribute(consumer);
		if (isRanked()) {
			consumer.accept("scimagoQIndex", getScimagoQIndex()); //$NON-NLS-1$
			consumer.accept("wosQIndex", getWosQIndex()); //$NON-NLS-1$
			consumer.accept("impactFactor", Float.valueOf(getImpactFactor())); //$NON-NLS-1$
		}
	}

	@Override
	public String getPublicationTarget() {
		final StringBuilder buf = new StringBuilder();
		final Journal journal = getJournal();
		if (journal != null) {
			buf.append(journal.getJournalName());
			if (!Strings.isNullOrEmpty(journal.getPublisher())) {
				buf.append(", "); //$NON-NLS-1$
				buf.append(journal.getPublisher());
			}
		}
		return buf.toString();
	}

	@Override
	@RequiredFieldInForm
	public Journal getJournal() {
		return this.journal;
	}

	@Override
	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	@Override
	public QuartileRanking getScimagoQIndex() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getScimagoQIndexByYear(getPublicationYear());
		}
		return QuartileRanking.NR;
	}

	@Override
	public QuartileRanking getWosQIndex() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getWosQIndexByYear(getPublicationYear());
		}
		return QuartileRanking.NR;
	}

	@Override
	public float getImpactFactor() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getImpactFactorByYear(getPublicationYear());
		}
		return 0f;
	}

	@Override
	public boolean isRanked() {
		return isRanked(null);
	}

	@Override
	public boolean isRanked(JournalRankingSystem rankingSystem) {
		final Journal journal = getJournal();
		if (journal != null) {
			if (rankingSystem != null) {
				switch (rankingSystem) {
				case SCIMAGO:
					return journal.getScimagoQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
				case WOS:
					return journal.getWosQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
				default:
				}
			}
			return journal.getScimagoQIndexByYear(getPublicationYear()) != QuartileRanking.NR
					|| journal.getWosQIndexByYear(getPublicationYear()) != QuartileRanking.NR;
		}
		return false;
	}

	@Override
	public PublicationCategory getCategory(JournalRankingSystem rankingSystem) {
		final JournalRankingSystem rankingSystem0 = rankingSystem == null ? JournalRankingSystem.getDefault() : rankingSystem;
		final Supplier<Boolean> rank;
		switch (rankingSystem0) {
		case SCIMAGO:
			rank = () -> {
				final QuartileRanking r = getScimagoQIndex();
				return Boolean.valueOf(r != QuartileRanking.NR);
			};
			break;
		case WOS:
			rank = () -> {
				final QuartileRanking r = getWosQIndex();
				return Boolean.valueOf(r != QuartileRanking.NR);
			};
			break;
		default:
			throw new IllegalStateException();
		}
		return getCategoryWithSupplier(rank);
	}

	/** Replies the ISBN number that is associated to this publication.
	 * This functions delegates to the journal.
	 *
	 * @return the ISBN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/ISBN"
	 * @deprecated See {@link Journal#getISBN()}
	 */
	@Override
	@Deprecated(since = "2.0.0")
	public String getISBN() {
		if (this.journal != null) {
			return this.journal.getISBN();
		}
		return null;
	}

	/** Change the ISBN number that is associated to this publication.
	 * This functions delegates to the journal.
	 *
	 * @param isbn the ISBN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/ISBN"
	 * @deprecated See {@link Journal#setISBN(String)}
	 */
	@Override
	@Deprecated(since = "2.0.0")
	public void setISBN(String isbn) {
		if (this.journal != null) {
			this.journal.setISBN(isbn);
		}
	}

	/** Replies the ISSN number that is associated to this publication.
	 * This functions delegates to the journal.
	 *
	 * @return the ISSN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
	 * @deprecated See {@link Journal#getISSN()}
	 */
	@Override
	@Deprecated(since = "2.0.0")
	public String getISSN() {
		if (this.journal != null) {
			return this.journal.getISSN();
		}
		return null;
	}

	/** Change the ISSN number that is associated to this publication.
	 * This functions delegates to the journal.
	 *
	 * @param issn the ISSN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
	 * @deprecated See {@link Journal#setISSN(String)}
	 */
	@Override
	@Deprecated(since = "2.0.0")
	public final void setISSN(String issn) {
		if (this.journal != null) {
			this.journal.setISSN(issn);
		}
	}

	@Override
	public Boolean getOpenAccess() {
		final Journal journal = getJournal();
		if (journal != null) {
			return journal.getOpenAccess();
		}
		return null;
	}

}
