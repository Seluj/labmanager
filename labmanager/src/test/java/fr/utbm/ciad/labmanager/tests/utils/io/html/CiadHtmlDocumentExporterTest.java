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

package fr.utbm.ciad.labmanager.tests.utils.io.html;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.configuration.messages.BaseMessageSource;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.MemberStatus;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.ConferencePaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.hal.HalTools;
import fr.utbm.ciad.labmanager.utils.io.html.CiadHtmlDocumentExporter;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.DefaultProgression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link CiadHtmlDocumentExporter}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class CiadHtmlDocumentExporterTest {

	//private static final String OUTPUT_FILE = "/tmp/export.html";
	private static final String OUTPUT_FILE = null;

	private MessageSourceAccessor messages;

	private DoiTools doiTools;
	
	private HalTools halTools;

	private CiadHtmlDocumentExporter test;

	@BeforeEach
	public void setUp() throws Exception {
		this.messages = BaseMessageSource.getGlobalMessageAccessor();
		this.doiTools = mock(DoiTools.class);
		when(this.doiTools.getDOIUrlFromDOINumber(any())).thenReturn(new URL("https://doi.org/XXX"));
		this.halTools = mock(HalTools.class);
		when(this.halTools.getHALUrlFromHALNumber(any())).thenReturn(new URL("https://hal.science/XXX"));
		this.test = new CiadHtmlDocumentExporter(new ConfigurationConstants(), this.messages, this.doiTools, this.halTools);
	}

	@Test
	public void exportPublications_Iterable_null() throws Exception {
		assertNull(this.test.exportPublications(null, new ExporterConfigurator(null, Locale.US), new DefaultProgression(), LoggerFactory.getLogger(getClass())));
	}

	@Test
	public void exportPublications_Iterable_one() throws Exception {
		ConferencePaper p0 = mock(ConferencePaper.class);
		when(p0.getAddress()).thenReturn("location0");
		when(p0.getDOI()).thenReturn("doinumber0");
		when(p0.getEditors()).thenReturn("Dupont, Henri");
		when(p0.getISBN()).thenReturn("isbn0");
		when(p0.getISSN()).thenReturn("issn0");
		when(p0.getNumber()).thenReturn("num0");
		when(p0.getMajorLanguage()).thenReturn(PublicationLanguage.ENGLISH);
		when(p0.getOrganization()).thenReturn("organizer0");
		when(p0.getPages()).thenReturn("xx-yy");
		when(p0.getPublicationYear()).thenReturn(2022);
		Conference conf = mock(Conference.class);
		when(conf.getName()).thenReturn("conference0");
		when(p0.getConference()).thenReturn(conf);
		when(p0.getSeries()).thenReturn("series0");
		when(p0.getTitle()).thenReturn("This is the title");
		when(p0.getType()).thenReturn(PublicationType.INTERNATIONAL_CONFERENCE_PAPER);
		when(p0.getVolume()).thenReturn("vol0");

		Journal j = mock(Journal.class);
		when(j.getJournalName()).thenReturn("Int. Journal of Science");
		when(j.getPublisher()).thenReturn("The Publisher");
		
		JournalPaper p1 = mock(JournalPaper.class);
		when(p1.getDOI()).thenReturn("doinumber1");
		when(p1.getISBN()).thenReturn("isbn1");
		when(p1.getISSN()).thenReturn("issn1");
		when(p1.getNumber()).thenReturn("num1");
		when(p1.getMajorLanguage()).thenReturn(PublicationLanguage.FRENCH);
		when(p1.getPages()).thenReturn("xx-yy");
		when(p1.getPublicationYear()).thenReturn(2020);
		when(p1.getTitle()).thenReturn("This is the title of an older publication");
		when(p1.getType()).thenReturn(PublicationType.INTERNATIONAL_JOURNAL_PAPER);
		when(p1.getVolume()).thenReturn("vol1");
		when(p1.getJournal()).thenReturn(j);
		when(p1.getScimagoQIndex()).thenReturn(QuartileRanking.Q1);
		when(p1.getWosQIndex()).thenReturn(QuartileRanking.Q2);
		when(p1.getImpactFactor()).thenReturn(8.901f);

		JournalPaper p2 = mock(JournalPaper.class);
		when(p2.getDOI()).thenReturn("doinumber2");
		when(p2.getISBN()).thenReturn("isbn2");
		when(p2.getISSN()).thenReturn("issn2");
		when(p2.getNumber()).thenReturn("num2");
		when(p2.getMajorLanguage()).thenReturn(PublicationLanguage.ENGLISH);
		when(p2.getPages()).thenReturn("xx-yy");
		when(p2.getPublicationYear()).thenReturn(2021);
		when(p2.getTitle()).thenReturn("This is the title of a publication");
		when(p2.getType()).thenReturn(PublicationType.INTERNATIONAL_JOURNAL_PAPER);
		when(p2.getVolume()).thenReturn("vol2");
		when(p2.getJournal()).thenReturn(j);
		when(p2.getScimagoQIndex()).thenReturn(QuartileRanking.Q1);
		when(p2.getWosQIndex()).thenReturn(QuartileRanking.Q1);
		when(p2.getImpactFactor()).thenReturn(5.45f);

		Person a0 = mock(Person.class);
		when(a0.getFirstName()).thenReturn("Stephane");
		when(a0.getLastName()).thenReturn("Martin");

		Person a1 = mock(Person.class);
		when(a1.getFirstName()).thenReturn("Etienne");
		when(a1.getLastName()).thenReturn("Dupont");

		Person a2 = mock(Person.class);
		when(a2.getFirstName()).thenReturn("Andrew");
		when(a2.getLastName()).thenReturn("Schmit");

		when(p0.getAuthors()).thenReturn(Arrays.asList(a0, a1));
		when(p1.getAuthors()).thenReturn(Arrays.asList(a2, a1, a0));
		when(p2.getAuthors()).thenReturn(Arrays.asList(a2, a0));

		Membership m0 = mock(Membership.class);
		when(m0.getMemberStatus()).thenReturn(MemberStatus.ASSOCIATE_PROFESSOR);
		when(m0.isActiveIn(any(), any())).thenReturn(true);

		when(a0.getMemberships()).thenReturn(Collections.singleton(m0));

		Membership m1 = mock(Membership.class);
		when(m1.getMemberStatus()).thenReturn(MemberStatus.PHD_STUDENT);
		when(m1.isActiveIn(any(), any())).thenReturn(true);

		when(a1.getMemberships()).thenReturn(Collections.singleton(m1));

		ExporterConfigurator configurator = new ExporterConfigurator(null, Locale.US);

		String content = this.test.exportPublications(Arrays.asList(p0), configurator, new DefaultProgression(), LoggerFactory.getLogger(getClass()));

		assertNotNull(content);

		if (OUTPUT_FILE != null) {
			File file = new File(OUTPUT_FILE);
			try {
				file.delete();
			} catch (Throwable ex) {
				//
			}
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(content.getBytes());
			}
		}
	}

}
