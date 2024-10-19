/*
 * $Id$
 * 
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.data.member;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.PhoneNumberJPAConverter;
import fr.utbm.ciad.labmanager.data.invitation.PersonInvitation;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.ProjectMember;
import fr.utbm.ciad.labmanager.data.publication.Authorship;
import fr.utbm.ciad.labmanager.data.publication.comparators.AuthorshipComparator;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivity;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.hal.HalTools;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractContextData;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.context.support.MessageSourceAccessor;

/** Represent a person.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@Table(name = "Persons")
public class Person extends AbstractContextData implements JsonSerializable, AttributeProvider, Comparable<Person>, IdentifiableEntity {

	private static final long serialVersionUID = -1312811718336186349L;

	/** Base URL of the ResearchGate platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String RESEARCHGATE_BASE_URL = "http://www.researchgate.net/"; //$NON-NLS-1$

	/** URL of the Gravatar photo database, with the terminal slash character.
	 */
	public static final String GRAVATAR_ROOT_URL = "https://www.gravatar.com/"; //$NON-NLS-1$

	/** Base URL for gravatar pictures.
	 */
	public static final String GRAVATAR_URL = GRAVATAR_ROOT_URL + "avatar/"; //$NON-NLS-1$

	/** HTML query parameter name for specifying the size of an avatar on Gravatar.
	 */
	public static final String GRAVATAR_SIZE_PARAM = "s"; //$NON-NLS-1$

	/** Base URL for github pictures.
	 */
	public static final String GITHUB_AVATAR_URL = "https://avatars.githubusercontent.com/"; //$NON-NLS-1$

	/** HTML query parameter name for specifying the size of an avatar on Github.
	 */
	public static final String GITHUB_AVATAR_SIZE_PARAM = "s"; //$NON-NLS-1$

	/** Base URL for Google scholar pictures.
	 */
	public static final String GOOGLE_SCHOLAR_AVATAR_URL = "https://scholar.googleusercontent.com/citations?view_op=view_photo&user="; //$NON-NLS-1$

	/** HTML query parameter name for specifying the size of an avatar on Google Scholar.
	 */
	public static final String GOOGLE_SCHOLAR_AVATAR_SIZE_PARAM = "s"; //$NON-NLS-1$

	/** Base URL of the Scopus platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String SCOPUS_BASE_URL = "https://www.scopus.com/"; //$NON-NLS-1$

	/** Base URL of the ORCID platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String ORCID_BASE_URL = "https://orcid.org/"; //$NON-NLS-1$

	/** Base URL of the Web-Of-Science platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String WOS_BASE_URL = "https://www.webofscience.com/"; //$NON-NLS-1$

	/** Base URL of the Gogle Scholar platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String GSCHOLAR_BASE_URL = "https://scholar.google.fr/"; //$NON-NLS-1$

	/** Base URL of the AD Sscientific Index platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String ADSCIENTIFICINDEX_BASE_URL = "https://www.adscientificindex.com/"; //$NON-NLS-1$

	/** Base URL of the LinkedIn platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String LINKEDIN_BASE_URL = "http://linkedin.com/"; //$NON-NLS-1$

	/** Base URL of the GitHub platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String GITHUB_BASE_URL = "https://www.github.com/"; //$NON-NLS-1$

	/** Base URL of the Facebook platform, with the final slash character.
	 *
	 * @since 4.0
	 */
	public static final String FACEBOOK_BASE_URL = "https://www.facebook.com/"; //$NON-NLS-1$

	private static final String LINKEDIN_URL = LINKEDIN_BASE_URL + "in/"; //$NON-NLS-1$

	/** Before 2022, ResearcherId was linked to Publons.
	 * Since 2022, Publons was integrated in Web-of-Science.
	 * The old URL of ResearcherId is {@code http://www.researcherid.com/rid/}.
	 */
	private static final String WOS_URL = WOS_BASE_URL + "wos/author/rid/"; //$NON-NLS-1$

	private static final String SCOPUS_URL = SCOPUS_BASE_URL + "authid/detail.uri?authorId="; //$NON-NLS-1$

	private static final String GOOGLESCHOLAR_URL = GSCHOLAR_BASE_URL + "citations?user="; //$NON-NLS-1$

	private static final String HAL_URL = HalTools.HAL_URL_BASE + "search/index/?qa%5Bidentifiers_id%5D%5B%5D="; //$NON-NLS-1$

	private static final String RESEARCHGATE_URL = RESEARCHGATE_BASE_URL + "profile/"; //$NON-NLS-1$

	private static final String ADSCIENTIFICINDEX_URL = ADSCIENTIFICINDEX_BASE_URL + "scientist/"; //$NON-NLS-1$

	/** Identifier of a person.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private long id;

	/** First name of the person.
	 */
	@Column
	private String firstName;

	/** Last name of the person.
	 */
	@Column
	private String lastName;

	/** Gender of the person.
	 */
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	/** Primary email of the person.
	 *
	 * @since 4.0
	 */
	@Column
	private String primaryEmail;

	/** Secondary email of the person.
	 *
	 * @since 4.0
	 */
	@Column
	private String secondaryEmail;

	/** Naming convention for the webpage of the person.
	 */
	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private WebPageNaming webPageNaming;

	/** Identifier for the web page of the person.
	 * This information is stored in the database for accelerating the JPA queries.
	 */
	@Column(nullable = true)
	private String webPageId;

	/** ORCID of the person.
	 */
	@Column
	private String orcid;

	/** URL of the person on {@code academia.edu}.
	 */
	@Column
	private String academiaURL;

	/** Office phone number, using the international prefix if possible.
	 */
	@Column
	@Convert(converter = PhoneNumberJPAConverter.class)
	private PhoneNumber officePhone;

	/** Mobile phone number, using the international prefix if possible.
	 */
	@Column
	@Convert(converter = PhoneNumberJPAConverter.class)
	private PhoneNumber mobilePhone;

	/** Number of the office room.
	 */
	@Column
	private String officeRoom;

	/** URL of the person on {@code cordis.europa.eu}.
	 */
	@Column
	private String cordisURL;

	/** URL of the person on DBLP.
	 */
	@Column
	private String dblpURL;

	/** Identifier of the person on Facebook.
	 */
	@Column
	private String facebookId;

	/** Identifier of the person on Github.
	 */
	@Column
	private String githubId;

	/** Identifier of the person on Linked-In.
	 */
	@Column
	private String linkedInId;

	/** Research Identifier of the person.
	 */
	@Column
	private String researcherId;

	/** Scopus Identifier of the person.
	 *
	 * @since 3.3
	 */
	@Column
	private String scopusId;

	/** Google Scholar Identifier of the person.
	 */
	@Column
	private String googleScholarId;

	/** HAL Identifier of the person.
	 */
	@Column
	private String idhal;

	/** Identifier of the person on Research Gate.
	 */
	@Column
	private String researchGateId;

	/** Identifier of the person on AD Scientific Index.
	 */
	@Column
	private String adScientificIndexId;

	/** H-index of the person provided by Google Scholar.
	 */
	@Column
	private int googleScholarHindex;

	/** H-index of the person provided by Web-Of-Science.
	 */
	@Column
	private int wosHindex;

	/** H-index of the person provided by Scopus.
	 *
	 * @since 3.3
	 */
	@Column
	private int scopusHindex;

	/** Number of paper citations for the person provided by Google Scholar.
	 */
	@Column
	private int googleScholarCitations;

	/** Number of paper citations for the person provided by Web-Of-Science.
	 */
	@Column
	private int wosCitations;

	/** Number of paper citations for the person provided by Scopus.
	 *
	 * @since 3.3
	 */
	@Column
	private int scopusCitations;

	/** Identifier of the person on Gravatar.
	 */
	@Column
	private String gravatarId;

	/** Biography of the person.
	 *
	 * @since 4.0
	 */
	@Column(length = EntityConstants.LARGE_TEXT_SIZE)
	private String biography;

	/** Indicates if the biography of the person should be private.
	 *
	 * @since 4.0
	 */
	@Column
	private boolean privateBiography;

	/** List of research organizations for the person.
	 */
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Membership> memberships;

	/** List of publications of the person.
	 */
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Authorship> authorships;

	/** List of teaching activities for the person.
	 */
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<TeachingActivity> teachingActivities;

	/** List of jury memberships for the person as member.
	 */
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<JuryMembership> participationJurys;

	/** List of jury memberships for the person as candidate.
	 */
	@OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<JuryMembership> candidateJurys;

	/** List of jury memberships for the person as promoters.
	 */
	@OneToMany(mappedBy = "promoters", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<JuryMembership> promoterJurys;

	/** List of invitations as guest for the person.
	 */
	@OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<PersonInvitation> guestInvitations;

	/** List of invitations as inviter for the person.
	 */
	@OneToMany(mappedBy = "inviter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<PersonInvitation> inviterInvitations;

	/** List of projects in which the person is involved.
	 */
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<ProjectMember> projectParticipations;

	/** Indicates if the address was validated by an authority.
	 */
	@Column(nullable = false)
	private boolean validated;

	/** The country of the organization.
	 * @since 4.0
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CountryCode nationality = CountryCode.getDefault();

	/** Construct a person with the given values.
	 *
	 * @param id the identifier of the person.
	 * @param publications the list of publications.
	 * @param orgas the memberships of the person.
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @param gender the gender of the person.
	 * @param primaryEmail the email of the person.
	 * @param secondaryEmail the email of the person.
	 * @param orcid the ORCID identifier of the person.
	 * @since 4.0
	 */
	public Person(int id, Set<Authorship> publications, Set<Membership> orgas, String firstName, String lastName,
			Gender gender, String primaryEmail, String secondaryEmail, String orcid) {
		this.id = id;
		this.authorships = publications;
		this.memberships = orgas;
		this.firstName = firstName;
		this.lastName = lastName;
		if (gender == Gender.NOT_SPECIFIED) {
			this.gender = null;
		}else {
			this.gender = gender;
		}
		this.primaryEmail = primaryEmail;
		this.secondaryEmail = secondaryEmail;
		this.orcid = orcid;
	}

	/** Construct an empty person.
	 */
	public Person() {
		//
	}

	@Override
	public int hashCode() {
		if (this.id != 0) {
			return Long.hashCode(this.id);
		}
		var h = HashCodeUtils.start();
		h = HashCodeUtils.add(h, this.lastName);
		h = HashCodeUtils.add(h, this.firstName);
		h = HashCodeUtils.add(h, this.orcid);
		h = HashCodeUtils.add(h, this.primaryEmail);
		h = HashCodeUtils.add(h, this.secondaryEmail);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final var other = (Person) obj;
		if (this.id != 0 && other.id != 0) {
			return this.id == other.id;
		}
		return Objects.equals(this.lastName, other.lastName)
				&& Objects.equals(this.firstName, other.firstName)
				&& Objects.equals(this.orcid, other.orcid)
				&& Objects.equals(this.primaryEmail, other.primaryEmail)
				&& Objects.equals(this.secondaryEmail, other.secondaryEmail);
	}

	@Override
	public int compareTo(Person o) {
		return EntityUtils.getPreferredPersonComparator().compare(this, o);
	}

	@Override
	public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
		if (getId() != 0) {
			consumer.accept("id", Long.valueOf(getId())); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getAcademiaURL())) {
			consumer.accept("academiaURL", getAcademiaURL()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getCordisURL())) {
			consumer.accept("cordisURL", getCordisURL()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getDblpURL())) {
			consumer.accept("dblpURL", getDblpURL()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getPrimaryEmail())) {
			consumer.accept("primaryEmail", getPrimaryEmail()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getSecondaryEmail())) {
			consumer.accept("secondaryEmail", getSecondaryEmail()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getFacebookId())) {
			consumer.accept("facebookId", getFacebookId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getFirstName())) {
			consumer.accept("firstName", getFirstName()); //$NON-NLS-1$
		}
		if (getGender() != null) {
			consumer.accept("gender", getGender()); //$NON-NLS-1$
		}
		final var title = getCivilTitle(messages, locale); 
		if (!Strings.isNullOrEmpty(title)) {
			consumer.accept("civilTitle", title); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getGithubId())) {
			consumer.accept("githubId", getGithubId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getGravatarId())) {
			consumer.accept("gravatarId", getGravatarId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getLastName())) {
			consumer.accept("lastName", getLastName()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getFullName())) {
			consumer.accept("fullName", getFullName()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getFullNameWithLastNameFirst())) {
			consumer.accept("fullNameWithLastNameFirst", getFullNameWithLastNameFirst()); //$NON-NLS-1$
		}
		if (getNationality() != null) {
			consumer.accept("nationality", getNationality()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getLinkedInId())) {
			consumer.accept("linkedInId", getLinkedInId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getOfficeRoom())) {
			consumer.accept("officeRoom", getOfficeRoom()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getORCID())) {
			consumer.accept("orcid", getORCID()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getResearcherId())) {
			consumer.accept("researcherId", getResearcherId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getScopusId())) {
			consumer.accept("scopusId", getScopusId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getGoogleScholarId())) {
			consumer.accept("googleScholarId", getGoogleScholarId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getIdhal())) {
			consumer.accept("idhal", getIdhal()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getResearchGateId())) {
			consumer.accept("researchGateId", getResearchGateId()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getAdScientificIndexId())) {
			consumer.accept("adScientificIndexId", getAdScientificIndexId()); //$NON-NLS-1$
		}
		if (getGoogleScholarHindex() > 0) {
			consumer.accept("googleScholarHindex", Integer.valueOf(getGoogleScholarHindex())); //$NON-NLS-1$
		}
		if (getWosHindex() > 0) {
			consumer.accept("wosHindex", Integer.valueOf(getWosHindex())); //$NON-NLS-1$
		}
		if (getScopusHindex() > 0) {
			consumer.accept("scopusHindex", Integer.valueOf(getScopusHindex())); //$NON-NLS-1$
		}
		if (getGoogleScholarCitations() > 0) {
			consumer.accept("googleScholarCitations", Integer.valueOf(getGoogleScholarCitations())); //$NON-NLS-1$
		}
		if (getWosCitations() > 0) {
			consumer.accept("wosCitations", Integer.valueOf(getWosCitations())); //$NON-NLS-1$
		}
		if (getScopusCitations() > 0) {
			consumer.accept("scopusCitations", Integer.valueOf(getScopusCitations())); //$NON-NLS-1$
		}
		if (getWebPageNaming() != null) {
			consumer.accept("webPageNaming", getWebPageNaming()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getWebPageId())) {
			consumer.accept("webPageId", getWebPageId()); //$NON-NLS-1$
		}
		if (getWebPageURI() != null) {
			consumer.accept("webPageURI", getWebPageURI()); //$NON-NLS-1$
		}
		if (getMobilePhone() != null) {
			consumer.accept("mobilePhone", getMobilePhone()); //$NON-NLS-1$
		}
		if (getOfficePhone() != null) {
			consumer.accept("officePhone", getOfficePhone()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getBiography())) {
			consumer.accept("biography", getBiography()); //$NON-NLS-1$
		}
		consumer.accept("privateBiography", Boolean.valueOf(getPrivateBiography())); //$NON-NLS-1$
		consumer.accept("validated", Boolean.valueOf(isValidated())); //$NON-NLS-1$
	}

	@Override
	public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
		generator.writeStartObject();
		forEachAttribute((attrName, attrValue) -> {
			JsonUtils.writeField(generator, attrName, attrValue);
		});
		//
		final var organizations = JsonUtils.cache(generator);
		final var publications = JsonUtils.cache(generator);
		final var journals = JsonUtils.cache(generator);
		//
		generator.writeArrayFieldStart("memberships"); //$NON-NLS-1$
		for (final var membership : getMemberships()) {
			generator.writeStartObject();
			membership.forEachAttribute((attrName, attrValue) -> {
				JsonUtils.writeField(generator, attrName, attrValue);
			});
			organizations.writeReferenceOrObjectField("researchOrganization", membership.getDirectResearchOrganization(), () -> { //$NON-NLS-1$
				JsonUtils.writeObjectAndAttributes(generator, membership.getDirectResearchOrganization());
			});
			organizations.writeReferenceOrObjectField("superResearchOrganization", membership.getSuperResearchOrganization(), () -> { //$NON-NLS-1$
				JsonUtils.writeObjectAndAttributes(generator, membership.getSuperResearchOrganization());
			});
			final var axes = membership.getScientificAxes();
			if (axes != null && !axes.isEmpty()) {
				generator.writeArrayFieldStart("scientificAxes"); //$NON-NLS-1$
				for (final var axis : axes) {
					organizations.writeReferenceOrObject(axis, () -> {
						JsonUtils.writeObjectAndAttributes(generator, axis);
					});
				}
				generator.writeEndArray();
			}
			generator.writeEndObject();
		}
		generator.writeEndArray();
		//
		generator.writeArrayFieldStart("authorships"); //$NON-NLS-1$
		for (final var authorship : getAuthorships()) {
			generator.writeStartObject();
			authorship.forEachAttribute((attrName, attrValue) -> {
				JsonUtils.writeField(generator, attrName, attrValue);
			});
			publications.writeReferenceOrObjectField("publication", authorship.getPublication(), () -> { //$NON-NLS-1$
				journals.writePublicationAndAttributes(authorship.getPublication(), journal -> {
					JsonUtils.writeObjectAndAttributes(generator, journal);
				}, conference -> {
					JsonUtils.writeObjectAndAttributes(generator, conference);
				});
			});
			generator.writeEndObject();
		}
		generator.writeEndArray();
		//
		generator.writeEndObject();
	}

	@Override
	public void serializeWithType(JsonGenerator generator, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		serialize(generator, serializers);
	}

	@Override
	public long getId() {
		return this.id;
	}

	/** Change the identifier of the person.
	 *
	 * @param id the identifier.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/** Replies the first name of the person.
	 *
	 * @return the first name.
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/** Change the first name of the person.
	 *
	 * @param name the first name.
	 */
	public void setFirstName(String name) {
		this.firstName = Strings.emptyToNull(name);
		resetWebPageId();
	}

	/** Replies the last name of the person.
	 *
	 * @return the last name.
	 */
	public String getLastName() {
		return this.lastName;
	}

	/** Change the last name of the person.
	 *
	 * @param name the last name.
	 */
	public void setLastName(String name) {
		this.lastName = Strings.emptyToNull(name);
		resetWebPageId();
	}

	/** Replies the sequence of first and last names of the person.
	 *
	 * @return the full name in the form {@code FIRST LAST}.
	 * @see #getFullNameWithLastNameFirst()
	 */
	public String getFullName() {
		return getFirstName() + " " + getLastName(); //$NON-NLS-1$
	}

	/** Replies the sequence of last and first names of the person.
	 *
	 * @return the full name in the form {@code LAST FIRST}.
	 * @see #getFullName()
	 */
	public String getFullNameWithLastNameFirst() {
		return getLastName() + " " + getFirstName(); //$NON-NLS-1$
	}

	/** Replies the primary or secondary email of the person.
	 * If the primary email is not set, the secondary email is replied.
	 * Otherwise, the primary email is replied.
	 *
	 * @return the email.
	 * @see #getPrimaryEmail()
	 * @see #getSecondaryEmail()
	 */
	public final String getEmail() {
		if (Strings.isNullOrEmpty(this.primaryEmail)) {
			return this.secondaryEmail;
		}
		return this.primaryEmail;
	}

	/** Replies the primary email of the person.
	 *
	 * @return the primary email.
	 * @since 4.0
	 * @see #getSecondaryEmail()
	 * @see #getEmail()
	 */
	public String getPrimaryEmail() {
		return this.primaryEmail;
	}

	/** Change the primary email of the person.
	 *
	 * @param email the email.
	 * @since 4.0
	 * @see #setSecondaryEmail(String)
	 */
	public void setPrimaryEmail(String email) {
		this.primaryEmail = Strings.emptyToNull(email);
		resetWebPageId();
	}

	/** Replies the secondary email of the person.
	 *
	 * @return the secondary email.
	 * @since 4.0
	 * @see #getPrimaryEmail()
	 * @see #getEmail()
	 */
	public String getSecondaryEmail() {
		return this.secondaryEmail;
	}

	/** Change the primary email of the person.
	 *
	 * @param email the email.
	 * @since 4.0
	 * @see #setPrimaryEmail(String)
	 */
	public void setSecondaryEmail(String email) {
		this.secondaryEmail = Strings.emptyToNull(email);
		resetWebPageId();
	}

	/** Replies the name of the nationality of the person.
	 *
	 * @return the display name of the nationality.
	 * @since 4.0
	 */
	public String getNationalityDisplayName() {
		return this.nationality.getDisplayCountry();
	}


	/** Replies the nationality of the person.
	 *
	 * @return the nationality.
	 * @since 4.0
	 */
	public CountryCode getNationality() {
		return this.nationality;
	}

	/** Change the nationality of the person.
	 *
	 * @param nationality the nationality.
	 * @since 4.0
	 */
	public void setNationality(CountryCode nationality) {
		this.nationality = nationality == null ? CountryCode.getDefault() : nationality;
	}

	/** Change the nationality of the person.
	 *
	 * @param nationality the nationality.
	 * @since 4.0
	 */
	public final void setNationality(String nationality) {
		if (Strings.isNullOrEmpty(nationality)) {
			setNationality((CountryCode) null);
		} else {
			setNationality(CountryCode.valueOfCaseInsensitive(nationality));
		}
	}
	
	/** Replies the naming convention for the webpage of the person.
	 *
	 * @return the convention.
	 */
	public WebPageNaming getWebPageNaming() {
		if (this.webPageNaming == null) {
			return WebPageNaming.UNSPECIFIED;
		}
		return this.webPageNaming;
	}

	/** Recompute and reset the field for the webpage id.
	 */
	protected void resetWebPageId() {
		final var naming = getWebPageNaming();
		if (naming == null) {
			this.webPageId = null;
		} else {
			this.webPageId = naming.getWebpageIdFor(this);
		}
	}

	/** Change the naming convention for the webpage of the person.
	 *
	 * @param namingConvention the new naming convention.
	 */
	public void setWebPageNaming(WebPageNaming namingConvention) {
		this.webPageNaming = namingConvention;
		resetWebPageId();
	}

	/** Change the naming convention for the webpage of the person.
	 *
	 * @param namingConvention the new naming convention.
	 */
	public final void setWebPageNaming(String namingConvention) {
		if (Strings.isNullOrEmpty(namingConvention)) {
			setWebPageNaming((WebPageNaming) null);
		} else {
			setWebPageNaming(WebPageNaming.valueOfCaseInsensitive(namingConvention));
		}
	}

	/** Replies the URI of the webpage of the person.
	 *
	 * @return the URI or {@code null} if none.
	 */
	public URI getWebPageURI() {
		return getWebPageNaming().getWebpageURIFor(this);
	}

	/** Replies the identifier of the webpage of the person.
	 *
	 * @param id the identifier or {@code null}.
	 */
	public void setWebPageId(String id) {
		this.webPageId = Strings.emptyToNull(id);
	}

	/** Replies the identifier of the webpage of the person.
	 *
	 * @return the identifier or {@code null}.
	 */
	public String getWebPageId() {
		return this.webPageId;
	}

	/** Replies the ORCID of the person.
	 *
	 * @return the ORCID.
	 */
	public String getORCID() {
		return this.orcid;
	}

	/** Replies the URL to ORCID of the person.
	 *
	 * @return the ORCID URL.
	 */
	public final URL getOrcidURL() {
		if (!Strings.isNullOrEmpty(getORCID())) {
			try {
				return new URI(ORCID_BASE_URL + getORCID()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the ORCID of the person.
	 *
	 * @param orcid the orcid.
	 */
	public void setORCID(String orcid) {
		this.orcid = Strings.emptyToNull(orcid);
	}

	/** Replies the gender of the person.
	 *
	 * @return the gender.
	 */
	public Gender getGender() {
		if (this.gender == null) {
			return Gender.NOT_SPECIFIED;
		}
		return this.gender;
	}

	/** Change the gender of the person.
	 *
	 * @param gender the gender, or {@code null} to set to "unspecified".
	 */
	public void setGender(Gender gender) {
		if (gender == Gender.NOT_SPECIFIED) {
			this.gender = null;
		} else {
			this.gender = gender;
		}
	}

	/** Change the gender of the person.
	 *
	 * @param gender the gender.
	 */
	public final void setGender(String gender) {
		if (Strings.isNullOrEmpty(gender)) {
			setGender((Gender) null);
		} else {
			setGender(Gender.valueOfCaseInsensitive(gender));
		}
	}

	/** Replies the list of teaching activities of the person.
	 *
	 * @return the list of teaching activities.
	 * @since 4.0
	 */
	public Set<TeachingActivity> getTeachingActivities() {
		if (this.teachingActivities == null) {
			this.teachingActivities = new HashSet<>();
		}
		return this.teachingActivities;
	}

	/** Change the list of teaching activities of the person.
	 *
	 * @param list the list of activities.
	 * @since 4.0
	 */
	public void setTeachingActivities(Set<TeachingActivity> list) {
		if (this.teachingActivities != null) {
			this.teachingActivities.clear();
		} else {
			this.teachingActivities = new HashSet<>();
		}
		if (list != null) {
			this.teachingActivities.addAll(list);
		}
	}

	/** Replies the list of participations to jurys as member.
	 *
	 * @return the list of jury memberships.
	 * @since 4.0
	 */
	public Set<JuryMembership> getParticipationJurys() {
		if (this.participationJurys == null) {
			this.participationJurys = new HashSet<>();
		}
		return this.participationJurys;
	}

	/** Change the list of participations to jurys as member.
	 *
	 * @param list the list of jury memberships.
	 * @since 4.0
	 */
	public void setJuryParticipations(Set<JuryMembership> list) {
		if (this.participationJurys != null) {
			this.participationJurys.clear();
		} else {
			this.participationJurys = new HashSet<>();
		}
		if (list != null) {
			this.participationJurys.addAll(list);
		}
	}

	/** Replies the list of participations to jurys as candidate.
	 *
	 * @return the list of jury memberships.
	 * @since 4.0
	 */
	public Set<JuryMembership> getCandidateJurys() {
		if (this.candidateJurys == null) {
			this.candidateJurys = new HashSet<>();
		}
		return this.candidateJurys;
	}

	/** Change the list of participations to jurys as candidate.
	 *
	 * @param list the list of jury memberships.
	 * @since 4.0
	 */
	public void setCandidateJurys(Set<JuryMembership> list) {
		if (this.candidateJurys != null) {
			this.candidateJurys.clear();
		} else {
			this.candidateJurys = new HashSet<>();
		}
		if (list != null) {
			this.candidateJurys.addAll(list);
		}
	}

	/** Replies the list of participations to jurys as promoter.
	 *
	 * @return the list of jury memberships.
	 * @since 4.0
	 */
	public Set<JuryMembership> getPromoterJurys() {
		if (this.promoterJurys == null) {
			this.promoterJurys = new HashSet<>();
		}
		return this.promoterJurys;
	}

	/** Change the list of participations to jurys as promoter.
	 *
	 * @param list the list of jury memberships.
	 * @since 4.0
	 */
	public void setPromoterJurys(Set<JuryMembership> list) {
		if (this.promoterJurys != null) {
			this.promoterJurys.clear();
		} else {
			this.promoterJurys = new HashSet<>();
		}
		if (list != null) {
			this.promoterJurys.addAll(list);
		}
	}

	/** Replies the list of invitations as guest of the person.
	 *
	 * @return the list of guest invitations.
	 * @since 4.0
	 */
	public Set<PersonInvitation> getGuestInvitations() {
		if (this.guestInvitations == null) {
			this.guestInvitations = new HashSet<>();
		}
		return this.guestInvitations;
	}

	/** Change the list of invitations as guest of the person.
	 *
	 * @param list the list of guest invitations.
	 * @since 4.0
	 */
	public void setGuestInvitations(Set<PersonInvitation> list) {
		if (this.guestInvitations != null) {
			this.guestInvitations.clear();
		} else {
			this.guestInvitations = new HashSet<>();
		}
		if (list != null) {
			this.guestInvitations.addAll(list);
		}
	}

	/** Replies the list of invitations as inviter of the person.
	 *
	 * @return the list of inviter invitations.
	 * @since 4.0
	 */
	public Set<PersonInvitation> getInviterInvitations() {
		if (this.inviterInvitations == null) {
			this.inviterInvitations = new HashSet<>();
		}
		return this.inviterInvitations;
	}

	/** Change the list of invitations as inviter of the person.
	 *
	 * @param list the list of inviter invitations.
	 * @since 4.0
	 */
	public void setInviterInvitations(Set<PersonInvitation> list) {
		if (this.inviterInvitations != null) {
			this.inviterInvitations.clear();
		} else {
			this.inviterInvitations = new HashSet<>();
		}
		if (list != null) {
			this.inviterInvitations.addAll(list);
		}
	}

	/** Replies the list of publications of the person.
	 *
	 * @return the list of publications.
	 */
	public Set<Authorship> getAuthorships() {
		if (this.authorships == null) {
			this.authorships = new TreeSet<>(AuthorshipComparator.DEFAULT);
		}
		return this.authorships;
	}

	/** Replies the number of publications of the person.
	 *
	 * @return the number of publications.
	 */
	public int getAuthorshipNumber(){
		return getAuthorships().size();
	}

	/** Change the list of publications of the person.
	 *
	 * @param list the list of publications.
	 */
	public void setAuthorships(Set<Authorship> list) {
		this.authorships = list;
	}

	/** Replies the memberships of the person.
	 *
	 * @return the research organizations in which the person is involved.
	 */
	public Set<Membership> getMemberships() {
		if (this.memberships == null) {
			//this.memberships = new TreeSet<>(EntityUtils.getPreferredMembershipComparator());
			this.memberships = new HashSet<>();
		}
		return this.memberships;
	}

	/** Replies the more recent membership per research organization.
	 *
	 * @return the recent memberships per organization.
	 */
	public Map<ResearchOrganization, Membership> getRecentMemberships() {
		return getRecentMemberships(null);
	}

	/** Replies the more recent membership per research organization.
	 *
	 * @param filteringCondition the condition to apply for filtering the memberships, never {@code null}.
	 * @return the recent membership for each organization.
	 */
	public Map<ResearchOrganization, Membership> getRecentMemberships(Predicate<? super Membership> filteringCondition) {
		var  stream = getMemberships().stream();
		if (filteringCondition != null) {
			stream = stream.filter(filteringCondition);
		}
		return stream.collect(Collectors.toMap(
				it -> it.getDirectResearchOrganization(),
				it -> it,
				// Sort the memberships from the highest date to the lowest date
				BinaryOperator.minBy(EntityUtils.getPreferredMembershipComparator()),
				() -> new TreeMap<>(EntityUtils.getPreferredResearchOrganizationComparator())));
	}

	public String getOrganizations() {
		return getMemberships().stream().map(membership -> membership.getDirectResearchOrganization().getAcronym()).collect(Collectors.joining(", "));
	}

	/** Replies the supervisable memberships of the persons.
	 *
	 * @return the supervisable memberships.
	 * @since 2.1
	 * @see #getSupervisorMemberships()
	 */
	public List<Membership> getSupervisableMemberships() {
		return getMemberships().stream().filter(it -> it.getMemberStatus().isSupervisable()).collect(Collectors.toList());
	}

	/** Replies the supervisor memberships of the persons.
	 *
	 * @return the supervisor memberships.
	 * @since 2.1
	 * @see #getSupervisableMemberships()
	 */
	public List<Membership> getSupervisorMemberships() {
		return getMemberships().stream().filter(it -> it.getMemberStatus().isSupervisor()).collect(Collectors.toList());
	}

	/** Replies the active membership per research organization.
	 * The current system date is used as the temporal reference.
	 *
	 * @return the active membership for each organization.
	 */
	public Map<ResearchOrganization, Membership> getActiveMemberships() {
		return getRecentMemberships(it -> it.isActive());
	}

	/** Replies the finished membership per research organization.
	 * The current system date is used as the temporal reference.
	 *
	 * @return the finished membership for each organization.
	 */
	public Map<ResearchOrganization, Membership> getFinishedMemberships() {
		return getRecentMemberships(it -> it.isFormer());
	}

	/** Replies the active or finished membership per research organization.
	 * The current system date is used as the temporal reference.
	 *
	 * @return the active or finished membership for each organization.
	 */
	public Map<ResearchOrganization, Membership> getActiveOrFinishedMemberships() {
		return getRecentMemberships(it -> !it.isFuture());
	}

	/** Replies the future membership per research organization.
	 * The current system date is used as the temporal reference.
	 *
	 * @return the future membership for each organization.
	 */
	public Map<ResearchOrganization, Membership> getFutureMemberships() {
		return getRecentMemberships(it -> it.isFuture());
	}

	/** Change the memberships of the person.
	 *
	 * @param orgas the research organizations in which the person is involved.
	 */
	public void setMemberships(Set<Membership> orgas) {
		this.memberships = orgas;
	}

	/** Delete a publication for the person.
	 *
	 * @param pub is the publication authorship.
	 */
	public void deleteAuthorship(Authorship pub) {
		if (this.authorships != null) {
			this.authorships.remove(pub);
		}
	}

	/** Delete all the publications for the person.
	 */
	public void deleteAllAuthorships() {
		if (this.authorships != null) {
			this.authorships.clear();
		}
	}

	/** Replies the URL of the person on {@code academia.edu}.
	 *
	 * @return the URL.
	 */
	public String getAcademiaURL() {
		return this.academiaURL;
	}

	/** Replies the URL of the person on {@code academia.edu}.
	 *
	 * @return the URL.
	 */
	public final URL getAcademiaURLObject() {
		if (!Strings.isNullOrEmpty(getAcademiaURL())) {
			try {
				return new URI(getAcademiaURL()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the URL of the person on {@code academia.edu}.
	 *
	 * @param url the URL.
	 */
	public void setAcademiaURL(String url) {
		this.academiaURL = Strings.emptyToNull(url);
	}

	/** Replies the URL of the person on {@code cordis.europa.eu}.
	 *
	 * @return the URL.
	 */
	public String getCordisURL() {
		return this.cordisURL;
	}

	/** Replies the URL of the person on {@code cordis.europa.eu}.
	 *
	 * @return the URL.
	 */
	public final URL getCordisURLObject() {
		if (!Strings.isNullOrEmpty(getAcademiaURL())) {
			try {
				return new URI(getCordisURL()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the URL of the person on {@code cordis.europa.eu}.
	 *
	 * @param url the URL.
	 */
	public void setCordisURL(String url) {
		this.cordisURL = Strings.emptyToNull(url);
	}

	/** Replies the URL of the person on DBLP.
	 *
	 * @return the URL.
	 */
	public String getDblpURL() {
		return this.dblpURL;
	}

	/** Replies the URL of the person on DBLP.
	 *
	 * @return the URL.
	 */
	public final URL getDblpURLObject() {
		if (!Strings.isNullOrEmpty(getDblpURL())) {
			try {
				return new URI(getDblpURL()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the URL of the person on DBLP.
	 *
	 * @param url the URL.
	 */
	public void setDblpURL(String url) {
		this.dblpURL = Strings.emptyToNull(url);
	}

	/** Replies the identifier of the person on Facebook.
	 *
	 * @return the identifier.
	 */
	public String getFacebookId() {
		return this.facebookId;
	}

	/** Replies the URL of the person on Facebook.
	 *
	 * @return the URL.
	 */
	public final URL getFacebookURL() {
		if (!Strings.isNullOrEmpty(getFacebookId())) {
			try {
				return new URI(FACEBOOK_BASE_URL + getFacebookId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person on Facebook.
	 *
	 * @param id the identifier.
	 */
	public void setFacebookId(String id) {
		this.facebookId = Strings.emptyToNull(id);
	}

	/** Replies the identifier of the person on Github.
	 *
	 * @return the identifier.
	 */
	public String getGithubId() {
		return this.githubId;
	}

	/** Replies the URL of the person on Github.
	 *
	 * @return the URL.
	 * @see #getGithubAvatarURL()
	 */
	public final URL getGithubURL() {
		if (!Strings.isNullOrEmpty(getGithubId())) {
			try {
				return new URI(GITHUB_BASE_URL + getGithubId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Replies the URL of the person's avatar on Github.
	 *
	 * @return the URL.
	 * @see #getGithubURL()
	 * @see #getPhotoURL()
	 */
	public final URL getGithubAvatarURL() {
		if (!Strings.isNullOrEmpty(getGithubId())) {
			try {

				return new URI(GITHUB_AVATAR_URL + getGithubId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Replies the URL of the person's avatar on Github with a specific size.
	 *
	 * @param size the expecting size of the photo, in pixels.
	 * @return the URL.
	 * @see #getGithubURL()
	 * @see #getPhotoURL()
	 */
	public final URL getGithubAvatarURL(int size) {
		if (!Strings.isNullOrEmpty(getGithubId())) {
			try {
				return new URI(GITHUB_AVATAR_URL + getGithubId() + "?" + GITHUB_AVATAR_SIZE_PARAM + "=" + Integer.toString(size)).toURL(); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person on Github.
	 *
	 * @param id the identifier.
	 */
	public void setGithubId(String id) {
		this.githubId = Strings.emptyToNull(id);
	}

	/** Replies the identifier of the person on LinkedIn.
	 *
	 * @return the identifier.
	 */
	public String getLinkedInId() {
		return this.linkedInId;
	}

	/** Replies the URL of the person on LinkedIn.
	 *
	 * @return the URL.
	 */
	public final URL getLinkedInURL() {
		if (!Strings.isNullOrEmpty(getLinkedInId())) {
			try {
				return new URI(LINKEDIN_URL + getLinkedInId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person on LinkedIn.
	 *
	 * @param id the identifier.
	 */
	public void setLinkedInId(String id) {
		this.linkedInId = Strings.emptyToNull(id);
	}

	/** Replies the ResearcherID of the person.
	 *
	 * @return the identifier.
	 */
	public String getResearcherId() {
		return this.researcherId;
	}

	/** Replies the URL of the person on Plubon.
	 *
	 * @return the URL.
	 */
	public final URL getResearcherIdURL() {
		if (!Strings.isNullOrEmpty(getResearcherId())) {
			try {
				return new URI(WOS_URL + getResearcherId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the ResearcherID of the person.
	 *
	 * @param id the identifier.
	 */
	public void setResearcherId(String id) {
		this.researcherId = Strings.emptyToNull(id);
	}

	/** Replies the Scopus ID of the person.
	 *
	 * @return the identifier.
	 * @since 3.3
	 */
	public String getScopusId() {
		return this.scopusId;
	}

	/** Replies the URL of the person on Scopus.
	 *
	 * @return the URL.
	 * @since 3.3
	 */
	public final URL getScopusURL() {
		if (!Strings.isNullOrEmpty(getScopusId())) {
			try {
				return new URI(SCOPUS_URL + getScopusId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the Scopus ID of the person.
	 *
	 * @param id the identifier.
	 * @since 3.3
	 */
	public void setScopusId(String id) {
		this.scopusId = Strings.emptyToNull(id);
	}

	/** Replies the Google Scholar ID of the person.
	 *
	 * @return the identifier.
	 */
	public String getGoogleScholarId() {
		return this.googleScholarId;
	}

	/** Replies the URL of the person on Google Scholar.
	 *
	 * @return the URL.
	 */
	public final URL getGoogleScholarURL() {
		if (!Strings.isNullOrEmpty(getGoogleScholarId())) {
			try {
				return new URI(GOOGLESCHOLAR_URL + getGoogleScholarId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Replies the URL of the person's avatar on Google scholar.
	 *
	 * @return the URL.
	 * @see #getGoogleScholarURL()
	 * @see #getPhotoURL()
	 */
	public final URL getGoogleScholarAvatarURL() {
		if (!Strings.isNullOrEmpty(getGoogleScholarId())) {
			try {
				return new URI(GOOGLE_SCHOLAR_AVATAR_URL + getGoogleScholarId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Replies the URL of the person's avatar on Google Scholar with a specific size.
	 *
	 * @param size the expecting size of the photo, in pixels.
	 * @return the URL.
	 * @see #getGoogleScholarURL()
	 * @see #getPhotoURL()
	 */
	public final URL getGoogleScholarAvatarURL(int size) {
		if (!Strings.isNullOrEmpty(getGoogleScholarId())) {
			try {
				return new URI(GOOGLE_SCHOLAR_AVATAR_URL + getGoogleScholarId() + "&" + GOOGLE_SCHOLAR_AVATAR_SIZE_PARAM + "=" + Integer.toString(size)).toURL(); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the Google Scholar Id of the person.
	 *
	 * @param id the identifier.
	 */
	public void setGoogleScholarId(String id) {
		this.googleScholarId = Strings.emptyToNull(id);
	}

	/** Replies the HAL identifier of the person.
	 *
	 * @return the idhal.
	 * @since 3.8
	 */
	public String getIdhal() {
		return this.idhal;
	}

	/** Replies the URL of the person on HAL.
	 *
	 * @return the URL.
	 */
	public final URL getHalURL() {
		if (!Strings.isNullOrEmpty(getIdhal())) {
			try {
				return new URI(HAL_URL + getIdhal()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the HAL identifier of the person.
	 *
	 * @param idhal the idhal.
	 */
	public void setIdhal(String idhal) {
		this.idhal = Strings.emptyToNull(idhal);
	}

	/** Replies the identifier of the person on ResearchGate.
	 *
	 * @return the identifier.
	 */
	public String getResearchGateId() {
		return this.researchGateId;
	}

	/** Replies the URL of the person on ResearchGate.
	 *
	 * @return the URL.
	 */
	public final URL getResearchGateURL() {
		if (!Strings.isNullOrEmpty(getResearchGateId())) {
			try {
				return new URI(RESEARCHGATE_URL + getResearchGateId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person on ResearchGate.
	 *
	 * @param id the identifier.
	 */
	public void setResearchGateId(String id) {
		this.researchGateId = Strings.emptyToNull(id);
	}

	/** Replies the H-index of the person provided by Google Scholar.
	 *
	 * @return the h-index.
	 */
	public int getGoogleScholarHindex() {
		return this.googleScholarHindex;
	}

	/** Change the H-index of the person provided by Google Scholar.
	 *
	 * @param hindex the H-index.
	 */
	public void setGoogleScholarHindex(int hindex) {
		if (hindex < 0) {
			this.googleScholarHindex = 0;
		} else {
			this.googleScholarHindex = hindex;
		}
	}

	/** Change the H-index of the person provided by Google Scholar.
	 *
	 * @param hindex the H-index.
	 */
	public final void setGoogleScholarHindex(Number hindex) {
		if (hindex == null) {
			setGoogleScholarHindex(0);
		} else {
			setGoogleScholarHindex(hindex.intValue());
		}
	}

	/** Replies the H-index of the person provided by Web-of-Science.
	 *
	 * @return the h-index.
	 */
	public int getWosHindex() {
		return this.wosHindex;
	}

	/** Change the H-index of the person provided by Web-of-Science.
	 *
	 * @param hindex the H-index.
	 */
	public void setWosHindex(int hindex) {
		if (hindex < 0) {
			this.wosHindex = 0;
		} else {
			this.wosHindex = hindex;
		}
	}

	/** Change the H-index of the person provided by Web-of-Science.
	 *
	 * @param hindex the H-index.
	 */
	public final void setWosHindex(Number hindex) {
		if (hindex == null) {
			setWosHindex(0);
		} else {
			setWosHindex(hindex.intValue());
		}
	}

	/** Replies the H-index of the person provided by Scopus.
	 *
	 * @return the h-index.
	 * @since 3.3
	 */
	public int getScopusHindex() {
		return this.scopusHindex;
	}

	/** Change the H-index of the person provided by Scopus.
	 *
	 * @param hindex the H-index.
	 * @since 3.3
	 */
	public void setScopusHindex(int hindex) {
		if (hindex < 0) {
			this.scopusHindex = 0;
		} else {
			this.scopusHindex = hindex;
		}
	}

	/** Change the H-index of the person provided by Scopus.
	 *
	 * @param hindex the H-index.
	 * @since 3.3
	 */
	public final void setScopusHindex(Number hindex) {
		if (hindex == null) {
			setScopusHindex(0);
		} else {
			setScopusHindex(hindex.intValue());
		}
	}

	/** Replies the number of paper citations for the person provided by Google Scholar.
	 *
	 * @return the number of citations.
	 * @since 3.3
	 */
	public int getGoogleScholarCitations() {
		return this.googleScholarCitations;
	}

	/** Change the number of citations for the person provided by Google Scholar.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public void setGoogleScholarCitations(int citations) {
		if (citations < 0) {
			this.googleScholarCitations = 0;
		} else {
			this.googleScholarCitations = citations;
		}
	}

	/** Change the number of paper citations for the person provided by Google Scholar.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public final void setGoogleScholarCitations(Number citations) {
		if (citations == null) {
			setGoogleScholarCitations(0);
		} else {
			setGoogleScholarCitations(citations.intValue());
		}
	}

	/** Replies the number of paper citations for the person provided by Web-of-Science.
	 *
	 * @return the number of citations.
	 * @since 3.3
	 */
	public int getWosCitations() {
		return this.wosCitations;
	}

	/** Change the number of citations for the person provided by Web-of-Science.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public void setWosCitations(int citations) {
		if (citations < 0) {
			this.wosCitations = 0;
		} else {
			this.wosCitations = citations;
		}
	}

	/** Change the number of paper citations for the person provided by Web-of-Science.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public final void setWosCitations(Number citations) {
		if (citations == null) {
			setWosCitations(0);
		} else {
			setWosCitations(citations.intValue());
		}
	}

	/** Replies the number of paper citations for the person provided by Scopus.
	 *
	 * @return the number of citations.
	 * @since 3.3
	 */
	public int getScopusCitations() {
		return this.scopusCitations;
	}

	/** Change the number of citations for the person provided by Scopus.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public void setScopusCitations(int citations) {
		if (citations < 0) {
			this.scopusCitations = 0;
		} else {
			this.scopusCitations = citations;
		}
	}

	/** Change the number of paper citations for the person provided by Scopus.
	 *
	 * @param citations the number of citations.
	 * @since 3.3
	 */
	public final void setScopusCitations(Number citations) {
		if (citations == null) {
			setScopusCitations(0);
		} else {
			setScopusCitations(citations.intValue());
		}
	}

	/** Replies the identifier of the person provided by Gravatar.
	 *
	 * @return the identifier.
	 */
	public String getGravatarId() {
		return this.gravatarId;
	}

	/** Replies the URL to the photo of the person that is provided by Gravatar.
	 *
	 * @return the URL, or {@code null} if none.
	 * @see #getPhotoURL()
	 */
	public final URL getGravatarURL() {
		final String id = getGravatarId();
		if (!Strings.isNullOrEmpty(id)) {
			try {
				return new URI(GRAVATAR_URL + id).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Replies the URL to the photo of the person that is provided by Gravatar.
	 *
	 * @param size the expecting size of the photo, in pixels.
	 * @return the URL, or {@code null} if none.
	 * @see #getPhotoURL(int)
	 */
	public final URL getGravatarURL(int size) {
		final var id = getGravatarId();
		if (size > 0 && !Strings.isNullOrEmpty(id)) {
			try {
				return new URI(GRAVATAR_URL + id + "?" + GRAVATAR_SIZE_PARAM + "=" + Integer.toString(size)).toURL(); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person provided by Gravatar.
	 *
	 * @param identifier the identifier.
	 */
	public void setGravatarId(String identifier) {
		this.gravatarId = Strings.emptyToNull(identifier);
	}

	/** Replies the office phone number of the person. This phone number is supposed to follows the international
	 * standards
	 *
	 * @return the number.
	 */
	public PhoneNumber getOfficePhone() {
		return this.officePhone;
	}

	/** Change the office phone number of the person. This phone number is supposed to follows the international
	 * standards
	 *
	 * @param number the number.
	 */
	public void setOfficePhone(PhoneNumber number) {
		this.officePhone = number;
	}

	/** Replies the mobile phone number of the person. This phone number is supposed to follows the international
	 * standards
	 *
	 * @return the number.
	 */
	public PhoneNumber getMobilePhone() {
		return this.mobilePhone;
	}

	/** Change the mobile phone number of the person. This phone number is supposed to follows the international
	 * standards.
	 *
	 * @param number the number.
	 */
	public void setMobilePhone(PhoneNumber number) {
		this.mobilePhone = number;
	}

	/** Replies the office room's number of the person.
	 *
	 * @return the number.
	 */
	public String getOfficeRoom() {
		return this.officeRoom;
	}

	/** Change the office room's number of the person.
	 *
	 * @param room the number.
	 */
	public void setOfficeRoom(String room) {
		this.officeRoom = Strings.emptyToNull(room);
	}

	/** Replies the biography of the person.
	 *
	 * @return the biography.
	 * @since 4.0
	 */
	public String getBiography() {
		return this.biography;
	}

	/** Change the biography of the person.
	 *
	 * @param text the biography text.
	 * @since 4.0
	 */
	public void setBiography(String text) {
		this.biography = Strings.emptyToNull(text);
	}

	/** Replies the biography of the person should be considered as private.
	 *
	 * @return {@code true} if the biography should be private.
	 * @since 4.0
	 */
	public boolean getPrivateBiography() {
		return this.privateBiography;
	}

	/** Change the flag that indicates if the biography of the person should be private.
	 *
	 * @param privateFlag is {@code true} to make the biography private. If it is {@code null}, then the default value is applied.
	 * @since 4.0
	 */
	public void setPrivateBiography(Boolean privateFlag) {
		this.privateBiography = privateFlag == null ? false : privateFlag.booleanValue();
	}

	/** Change the flag that indicates if the biography of the person should be private.
	 *
	 * @param privateFlag is {@code true} to make the biography private. If it is {@code null}, then the default value is applied.
	 * @since 4.0
	 */
	public void setPrivateBiography(boolean privateFlag) {
		this.privateBiography = privateFlag;
	}

	/** Replies the preferred civil title for this person. This civil title is not stored and computed based
	 * on the values replied by {@link #getActiveOrFinishedMemberships()} and {@link #getGender()}.
	 *
	 * @param messages the accessor to the localized labels.
	 * @param locale the locale to use for generating the labels.
	 * @param locale the locale to use for generating the civil title.
	 * @return the civil title, or {@code null} if none.
	 * @see #getCivilTitle(MessageSourceAccessor, Locale, boolean)
	 */
	public final String getCivilTitle(MessageSourceAccessor messages, Locale locale) {
		return getCivilTitle(messages, locale, true);
	}

	/** Replies the preferred civil title for this person. This civil title is not stored and computed based
	 * on the values replied by {@link #getActiveOrFinishedMemberships()}, and, if {@code includeGenderTitle}
	 * is evaluated to {@code true}, the value replied by {@link #getGender()}.
	 * The language of the title depends on the current locale.
	 *
	 * @param messages the accessor to the localized labels.
	 * @param locale the locale to use for generating the labels.
	 * @param includeGenderTitle indicates if the gender title should be consider and eventually replied.
	 * @return the civil title, or {@code null} if none.
	 * @see #getCivilTitle(MessageSourceAccessor, Locale)
	 */
	public String getCivilTitle(MessageSourceAccessor messages, Locale locale, boolean includeGenderTitle) {
		final var status = findHigherMemberStatus();
		String title = null;
		if (status != null) {
			title = status.getCivilTitle(messages, locale);
		}
		if (includeGenderTitle && Strings.isNullOrEmpty(title)) {
			title = getGender().getCivilTitle(messages, locale);
		}
		return Strings.emptyToNull(title);
	}

	private MemberStatus findHigherMemberStatus() {
		final var status = getActiveOrFinishedMemberships().values().stream().map(it -> it.getMemberStatus())
				.min((a, b) -> {
					final int cmp = Integer.compare(a.getHierachicalLevel(), b.getHierachicalLevel());
					if (cmp != 0) {
						return cmp;
					}
					return b.compareTo(a);
				});
		if (status.isEmpty()) {
			return null;
		}
		return status.get();
	}


	/** Replies the URL to the photo of the person that is provided by one of the sources (Gravatar, Github, ...).
	 *
	 * @return the URL, or {@code null} if none.
	 * @see #getGravatarURL()
	 * @see #getGithubAvatarURL()
	 * @see #getGoogleScholarAvatarURL()
	 */
	public final URL getPhotoURL() {
		var url = getGravatarURL();
		if (url != null) {
			return url;
		}
		url = getGithubAvatarURL();
		if (url != null) {
			return url;
		}
		url = getGoogleScholarAvatarURL();
		if (url != null) {
			return url;
		}
		return null;
	}

	/** Replies the URL to the photo of the person that is provided by one of the sources (Gravatar, Github, ...).
	 *
	 * @param size the expecting size of the photo, in pixels.
	 * @return the URL, or {@code null} if none.
	 * @see #getGravatarURL(int)
	 * @see #getGithubAvatarURL(int)
	 * @see #getGoogleScholarAvatarURL(int)
	 */
	public final URL getPhotoURL(int size) {
		var url = getGravatarURL(size);
		if (url != null) {
			return url;
		}
		url = getGithubAvatarURL(size);
		if (url != null) {
			return url;
		}
		url = getGoogleScholarAvatarURL(size);
		if (url != null) {
			return url;
		}
		return null;
	}

	/** Replies if this person information was validated by an authority.
	 *
	 * @return {@code true} if the person information is validated.
	 * @since 3.2
	 */
	public boolean isValidated() {
		return this.validated;
	}

	/** Change the flag that indicates if this person information was validated by an authority.
	 *
	 * @param validated {@code true} if the person information is validated.
	 * @since 3.2
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/** Change the flag that indicates if this person information was validated by an authority.
	 *
	 * @param validated {@code true} if the person information is validated.
	 * @since 3.2
	 */
	public final void setValidated(Boolean validated) {
		if (validated == null) {
			setValidated(false);
		} else {
			setValidated(validated.booleanValue());
		}
	}

	/** Replies the identifier of the person on AD Scientific INdex.
	 *
	 * @return the identifier.
	 * @since 3.6
	 */
	public String getAdScientificIndexId() {
		return this.adScientificIndexId;
	}

	/** Replies the URL of the person on AD Scientific Index.
	 *
	 * @return the URL.
	 * @since 3.6
	 */
	public final URL getAdScientificIndexURL() {
		if (!Strings.isNullOrEmpty(getAdScientificIndexId())) {
			try {

				return new URI(ADSCIENTIFICINDEX_URL + getAdScientificIndexId()).toURL();
			} catch (Throwable ex) {
				//
			}
		}
		return null;
	}

	/** Change the identifier of the person on AD Scientific Index.
	 *
	 * @param id the identifier.
	 */
	public void setAdScientificIndexId(String id) {
		this.adScientificIndexId = Strings.emptyToNull(id);
	}

	@Override
	public String toString() {
		return EntityUtils.toString(this, getFullNameWithLastNameFirst());
	}

	/** Replies the list of projects in which the person is involved.
	 *
	 * @return the list of projects.
	 * @since 4.0
	 */
	public Set<ProjectMember> getProjectParticipations() {
		if (this.projectParticipations == null) {
			this.projectParticipations = new HashSet<>();
		}
		return this.projectParticipations;
	}

	/** Change the list of projects in which the person is involved.
	 *
	 * @param list the list of projects.
	 * @since 4.0
	 */
	public void setProjectParticipations(Set<ProjectMember> list) {
		if (this.projectParticipations != null) {
			this.projectParticipations.clear();
		} else {
			this.projectParticipations = new HashSet<>();
		}
		if (list != null) {
			this.projectParticipations.addAll(list);
		}
	}

}
