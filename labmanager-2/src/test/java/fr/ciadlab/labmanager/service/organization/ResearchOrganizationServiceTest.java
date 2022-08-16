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

package fr.ciadlab.labmanager.service.organization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.ciadlab.labmanager.entities.member.MemberStatus;
import fr.ciadlab.labmanager.entities.member.Membership;
import fr.ciadlab.labmanager.entities.member.Person;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.repository.member.MembershipRepository;
import fr.ciadlab.labmanager.repository.member.PersonRepository;
import fr.ciadlab.labmanager.repository.organization.ResearchOrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests for {@link ResearchOrganizationService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class ResearchOrganizationServiceTest {

	private ResearchOrganization orga0;

	private ResearchOrganization orga1;

	private ResearchOrganization orga2;

	private ResearchOrganization orga3;

	private ResearchOrganizationRepository organizationRepository;

	private MembershipRepository membershipRepository;

	private PersonRepository personRepository;

	private ResearchOrganizationService test;

	@BeforeEach
	public void setUp() {
		this.organizationRepository = mock(ResearchOrganizationRepository.class);
		this.membershipRepository = mock(MembershipRepository.class);
		this.personRepository = mock(PersonRepository.class);
		this.test = new ResearchOrganizationService(this.organizationRepository,
				this.membershipRepository, this.personRepository);

		// Prepare some organizations to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.orga0 = mock(ResearchOrganization.class);
		lenient().when(this.orga0.getId()).thenReturn(123);
		lenient().when(this.orga0.getAcronym()).thenReturn("O1");
		lenient().when(this.orga0.getName()).thenReturn("N1");
		this.orga1 = mock(ResearchOrganization.class);
		lenient().when(this.orga1.getId()).thenReturn(234);
		lenient().when(this.orga1.getAcronym()).thenReturn("O2");
		lenient().when(this.orga1.getName()).thenReturn("N2");
		this.orga2 = mock(ResearchOrganization.class);
		lenient().when(this.orga2.getId()).thenReturn(345);
		lenient().when(this.orga2.getAcronym()).thenReturn("O3");
		lenient().when(this.orga2.getName()).thenReturn("N3");
		this.orga3 = mock(ResearchOrganization.class);
		lenient().when(this.orga3.getId()).thenReturn(456);
		lenient().when(this.orga3.getAcronym()).thenReturn("O4");
		lenient().when(this.orga3.getName()).thenReturn("N4");

		lenient().when(this.organizationRepository.findAll()).thenReturn(
				Arrays.asList(this.orga0, this.orga1, this.orga2, this.orga3));
		lenient().when(this.organizationRepository.findById(anyInt())).then(it -> {
			switch (((Integer) it.getArgument(0)).intValue()) {
			case 123:
				return Optional.of(this.orga0);
			case 234:
				return Optional.of(this.orga1);
			case 345:
				return Optional.of(this.orga2);
			case 456:
				return Optional.of(this.orga3);
			}
			return Optional.empty();
		});
	}

	@Test
	public void getAllResearchOrganizations() {
		final List<ResearchOrganization> list = this.test.getAllResearchOrganizations();
		assertNotNull(list);
		assertEquals(4, list.size());
		assertSame(this.orga0, list.get(0));
		assertSame(this.orga1, list.get(1));
		assertSame(this.orga2, list.get(2));
		assertSame(this.orga3, list.get(3));
	}

	@Test
	public void getResearchOrganizationById() {
		assertTrue(this.test.getResearchOrganizationById(-4756).isEmpty());
		assertTrue(this.test.getResearchOrganizationById(0).isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationById(123).get());
		assertSame(this.orga1, this.test.getResearchOrganizationById(234).get());
		assertSame(this.orga2, this.test.getResearchOrganizationById(345).get());
		assertSame(this.orga3, this.test.getResearchOrganizationById(456).get());
		assertTrue(this.test.getResearchOrganizationById(7896).isEmpty());
	}

	@Test
	public void getResearchOrganizationByAcronym() {
		assertTrue(this.test.getResearchOrganizationByAcronym(null).isEmpty());
		assertTrue(this.test.getResearchOrganizationByAcronym("").isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationByAcronym("O1").get());
		assertSame(this.orga1, this.test.getResearchOrganizationByAcronym("O2").get());
		assertSame(this.orga2, this.test.getResearchOrganizationByAcronym("O3").get());
		assertSame(this.orga3, this.test.getResearchOrganizationByAcronym("O4").get());
		assertTrue(this.test.getResearchOrganizationByAcronym("O5").isEmpty());
	}

	@Test
	public void getResearchOrganizationByName() {
		assertTrue(this.test.getResearchOrganizationByName(null).isEmpty());
		assertTrue(this.test.getResearchOrganizationByName("").isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationByName("N1").get());
		assertSame(this.orga1, this.test.getResearchOrganizationByName("N2").get());
		assertSame(this.orga2, this.test.getResearchOrganizationByName("N3").get());
		assertSame(this.orga3, this.test.getResearchOrganizationByName("N4").get());
		assertTrue(this.test.getResearchOrganizationByName("N5").isEmpty());
	}

	@Test
	public void createResearchOrganization() {
		final int id = this.test.createResearchOrganization("NA", "NN", "ND");
		assertEquals(0, id);

		final ArgumentCaptor<ResearchOrganization> arg = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, only()).save(arg.capture());
		final ResearchOrganization actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(id, actual.getId());
		assertEquals("NA", actual.getAcronym());
		assertEquals("NN", actual.getName());
		assertEquals("ND", actual.getDescription());
	}

	@Test
	public void removeResearchOrganization() throws Exception {
		this.test.removeResearchOrganization(234);

		final ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);

		verify(this.organizationRepository, atLeastOnce()).findById(arg.capture());
		Integer actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);

		verify(this.organizationRepository, atLeastOnce()).deleteById(arg.capture());
		actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);
	}

	@Test
	public void updateResearchOrganization() {
		this.test.updateResearchOrganization(234, "NA", "NN", "ND");

		final ArgumentCaptor<Integer> arg0 = ArgumentCaptor.forClass(Integer.class);
		verify(this.organizationRepository, atLeastOnce()).findById(arg0.capture());
		Integer actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeastOnce()).save(arg1.capture());
		final ResearchOrganization actual1 = arg1.getValue();
		assertSame(this.orga1, actual1);

		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		verify(this.orga1, atLeastOnce()).setAcronym(arg2.capture());
		assertEquals("NA", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setName(arg2.capture());
		assertEquals("NN", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setDescription(arg2.capture());
		assertEquals("ND", arg2.getValue());
	}

	@Test
	public void addMembership() {
		final LocalDate d0 = LocalDate.parse("2022-07-14");
		final LocalDate d1 = LocalDate.parse("2022-07-24");
		final Person pers = mock(Person.class);
		when(this.personRepository.findById(anyInt())).thenReturn(Optional.of(pers));
		when(pers.getMemberships()).thenReturn(Collections.emptySet());

		final boolean r = this.test.addMembership(234, 890, d0, d1, MemberStatus.ENGINEER);

		assertTrue(r);

		final ArgumentCaptor<Integer> arg0 = ArgumentCaptor.forClass(Integer.class);

		verify(this.organizationRepository, atLeastOnce()).findById(arg0.capture());
		Integer actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		verify(this.personRepository, atLeastOnce()).findById(arg0.capture());
		Integer actual1 = arg0.getValue();
		assertNotNull(actual1);
		assertEquals(890, actual1);

		final ArgumentCaptor<Membership> arg1 = ArgumentCaptor.forClass(Membership.class);
		verify(this.membershipRepository, atLeastOnce()).save(arg1.capture());
		final Membership actual2 = arg1.getValue();
		assertSame(pers, actual2.getPerson());
		assertSame(this.orga1, actual2.getResearchOrganization());
		assertSame(d0, actual2.getMemberSinceWhen());
		assertSame(d1, actual2.getMemberToWhen());
		assertSame(MemberStatus.ENGINEER, actual2.getMemberStatus());
	}

	@Test
	public void addMembership_invalidOrga() {
		final LocalDate d0 = LocalDate.parse("2022-07-14");
		final LocalDate d1 = LocalDate.parse("2022-07-24");
		final boolean r = this.test.addMembership(1, 890, d0, d1, MemberStatus.ENGINEER);
		assertFalse(r);
	}

	@Test
	public void addMembership_invalidPerson() {
		final LocalDate d0 = LocalDate.parse("2022-07-14");
		final LocalDate d1 = LocalDate.parse("2022-07-24");
		when(this.personRepository.findById(anyInt())).thenReturn(Optional.empty());
		final boolean r = this.test.addMembership(234, 890, d0, d1, MemberStatus.ENGINEER);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);

		final boolean r = this.test.linkSubOrganization(234, 456);
		assertTrue(r);

		final ArgumentCaptor<ResearchOrganization> arg0 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.orga3).setSuperOrganization(arg0.capture());
		assertSame(this.orga1, arg0.getValue());

		assertEquals(1, suborgas.size());
		assertTrue(suborgas.contains(this.orga3));

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeast(2)).save(arg1.capture());
		final ResearchOrganization actual0 = arg1.getAllValues().get(0);
		final ResearchOrganization actual1 = arg1.getAllValues().get(1);
		assertSame(this.orga3, actual0);
		assertSame(this.orga1, actual1);
	}

	@Test
	public void linkSubOrganization_invalidSub() {
		final boolean r = this.test.linkSubOrganization(234, 1);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_invalidSup() {
		final boolean r = this.test.linkSubOrganization(1, 234);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_invalidBoth() {
		final boolean r = this.test.linkSubOrganization(1, 2);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_same() {
		final boolean r = this.test.linkSubOrganization(234, 234);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);
		suborgas.add(this.orga3);

		final boolean r = this.test.unlinkSubOrganization(234, 456);

		assertTrue(r);

		final ArgumentCaptor<ResearchOrganization> arg0 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.orga3).setSuperOrganization(arg0.capture());
		assertNull(arg0.getValue());

		assertTrue(suborgas.isEmpty());

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeast(2)).save(arg1.capture());
		final ResearchOrganization actual0 = arg1.getAllValues().get(0);
		final ResearchOrganization actual1 = arg1.getAllValues().get(1);
		assertSame(this.orga1, actual0);
		assertSame(this.orga3, actual1);
	}

	@Test
	public void unlinkSubOrganization_invalidSup() {
		final boolean r = this.test.unlinkSubOrganization(1, 456);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_invalidBoth() {
		final boolean r = this.test.unlinkSubOrganization(1, 2);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_same() {
		final boolean r = this.test.unlinkSubOrganization(456, 456);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_validButNotLinked() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);
		final boolean r = this.test.unlinkSubOrganization(234, 456);
		assertFalse(r);
	}

}
