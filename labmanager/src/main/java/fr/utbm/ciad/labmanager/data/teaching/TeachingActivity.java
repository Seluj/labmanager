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

package fr.utbm.ciad.labmanager.data.teaching;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.AttributeProvider;
import fr.utbm.ciad.labmanager.data.EntityConstants;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.json.JsonUtils;
import jakarta.persistence.*;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Description of a teaching activity for a person.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
@Entity
@Table(name = "TeachingActivities")
public class TeachingActivity implements Serializable, JsonSerializable, AttributeProvider, Comparable<TeachingActivity>, IdentifiableEntity {

    /**
     * Default level for a teaching activity.
     */
    public static final TeachingActivityLevel DEFAULT_ACTIVITY_LEVEL = TeachingActivityLevel.MASTER_DEGREE;
    /**
     * Default role for the person associated to a teaching activity.
     */
    public static final TeacherRole DEFAULT_ROLE = TeacherRole.PARTICIPANT;
    /**
     * Default type of student that is targeted by a teaching activity.
     */
    public static final StudentType DEFAULT_STUDENT_TYPE = StudentType.INITIAL_TRAINING;
    /**
     * Default teaching language.
     */
    public static final CountryCode DEFAULT_LANGUAGE = CountryCode.getDefault();
    private static final long serialVersionUID = -7216145511954914471L;
    /**
     * Identifier of the jury.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    /**
     * Reference to the person who has done the activity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    /**
     * Code of the teaching unit, if any.
     */
    @Column
    private String code;

    /**
     * Title of the teaching unit.
     */
    @Column
    private String title;

    /**
     * Level of the teaching unit of the teaching unit.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private TeachingActivityLevel level = DEFAULT_ACTIVITY_LEVEL;

    /**
     * Type of students in the teaching unit.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private StudentType studentType = DEFAULT_STUDENT_TYPE;

    /**
     * Role of the person in the teaching unit.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private TeacherRole role = DEFAULT_ROLE;

    /**
     * Indicate if the person has different factors for computing hETD from tutorial and practice work hours.
     */
    @Column
    private boolean differentHetdForTdTp;

    /**
     * Amount of work per type of activity per year.
     */
    @ElementCollection
    private Map<TeachingActivityType, Float> annualWorkPerType;

    /**
     * Estimated number of students per year.
     */
    @Column
    private int numberOfStudents;

    /**
     * Explanation on the content of the teaching activity that may appear in the resume of the teacher.
     */
    @Column(length = EntityConstants.LARGE_TEXT_SIZE)
    private String explanation;

    /**
     * Types of pedagogical activities that are carried in the teaching activities.
     */
    @ElementCollection
    private Set<PedagogicalPracticeType> pedagogicalPracticeTypes;

    /**
     * Name of the degree
     */
    @Column
    private String degree;

    /**
     * Hosting organization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private ResearchOrganization university;

    /**
     * Start date of the activity.
     */
    @Column
    private LocalDate startDate;

    /**
     * End date of the activity.
     */
    @Column
    private LocalDate endDate;

    /**
     * Language of the activity.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private CountryCode language = DEFAULT_LANGUAGE;

    /**
     * URL of a website related to the teaching unit
     */
    @Column
    private String activityUrl;

    /**
     * URL of a website related to the source code related to the teaching unit
     */
    @Column
    private String sourceUrl;

    /**
     * Path to the uploaded slides, if any.
     */
    @Column
    private String pathToSlides;

    /**
     * Construct a teaching activity with the given values.
     *
     * @param person               the teacher.
     * @param code                 the code or acronym of the teaching activity, if any.
     * @param title                the totle of the teaching activity.
     * @param degree               the name of the degree.
     * @param university           the University in which the teaching activity is realized.
     * @param level                the level of the teaching activity.
     * @param studentType          the type of students that ar etargeted by the activity.
     * @param role                 the role of the teacher in the activity.
     * @param differentHetdForTdTp indicates if the person has different factors for computing hETD from real hours.
     * @param annualWorkPerType    maps each type of realized activity to the number of hours per year.
     * @param numberOfStudents     an estimation of the number of students per year.
     * @param explanation          a text that may be used for explaining the content and details on the teaching activity.
     * @param language             is the language of the activity.
     * @param startDate            the starting date of the activity.
     * @param endDate              the ending date of the activity, if any.
     * @param activityUrl          the URL of a page that describes the activity.
     * @param sourceUrl            the URL of a website that provides the source for the activity.
     */
    public TeachingActivity(Person person, String code, String title, String degree, ResearchOrganization university,
                            TeachingActivityLevel level, StudentType studentType, TeacherRole role, boolean differentHetdForTdTp,
                            Map<TeachingActivityType, Float> annualWorkPerType, int numberOfStudents, String explanation,
                            CountryCode language, LocalDate startDate, LocalDate endDate, String activityUrl, String sourceUrl) {
        this.person = person;
        this.code = code;
        this.title = title;
        this.degree = degree;
        this.university = university;
        this.level = level == null ? DEFAULT_ACTIVITY_LEVEL : level;
        this.studentType = studentType == null ? DEFAULT_STUDENT_TYPE : studentType;
        this.role = role == null ? DEFAULT_ROLE : role;
        this.differentHetdForTdTp = differentHetdForTdTp;
        this.annualWorkPerType = annualWorkPerType;
        this.numberOfStudents = numberOfStudents >= 0 ? numberOfStudents : 0;
        this.explanation = explanation;
        this.language = language == null ? DEFAULT_LANGUAGE : language;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityUrl = activityUrl;
        this.sourceUrl = sourceUrl;
    }

    /**
     * Construct an empty invitation.
     */
    public TeachingActivity() {
        //
    }

    @Override
    public int hashCode() {
        if (this.id != 0) {
            return Long.hashCode(this.id);
        }
        var h = HashCodeUtils.start();
        h = HashCodeUtils.add(h, this.code);
        h = HashCodeUtils.add(h, this.title);
        h = HashCodeUtils.add(h, this.level);
        h = HashCodeUtils.add(h, this.startDate);
        h = HashCodeUtils.add(h, this.endDate);
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
        final var other = (TeachingActivity) obj;
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }
        return Objects.equals(this.code, other.code)
                && Objects.equals(this.title, other.title)
                && this.level == other.level
                && Objects.equals(this.startDate, other.startDate)
                && Objects.equals(this.endDate, other.endDate);
    }

    @Override
    public int compareTo(TeachingActivity o) {
        return EntityUtils.getPreferredTeachingActivityComparator().compare(this, o);
    }

    @Override
    public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
        if (!Strings.isNullOrEmpty(getCode())) {
            consumer.accept("code", getCode()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getTitle())) {
            consumer.accept("title", getTitle()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getDegree())) {
            consumer.accept("degree", getDegree()); //$NON-NLS-1$
        }
        if (getLevel() != null) {
            consumer.accept("level", getLevel()); //$NON-NLS-1$
        }
        if (getStudentType() != null) {
            consumer.accept("studentType", getStudentType()); //$NON-NLS-1$
        }
        if (getRole() != null) {
            consumer.accept("role", getRole()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getExplanation())) {
            consumer.accept("explanation", getExplanation()); //$NON-NLS-1$
        }
        consumer.accept("pedagogicalPracticeTypes", getPedagogicalPracticeTypes()); //$NON-NLS-1$
        if (getLanguage() != null) {
            consumer.accept("language", getLanguage()); //$NON-NLS-1$
        }
        if (getStartDate() != null) {
            consumer.accept("startDate", getStartDate()); //$NON-NLS-1$
        }
        if (getEndDate() != null) {
            consumer.accept("endDate", getEndDate()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getActivityUrl())) {
            consumer.accept("activityUrl", getActivityUrl()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getSourceUrl())) {
            consumer.accept("sourceUrl", getSourceUrl()); //$NON-NLS-1$
        }
        if (!Strings.isNullOrEmpty(getPathToSlides())) {
            consumer.accept("pathToSlides", getPathToSlides()); //$NON-NLS-1$
        }
        if (!getAnnualWorkPerType().isEmpty()) {
            consumer.accept("annualWorkPerType", getAnnualWorkPerType()); //$NON-NLS-1$
        }
        consumer.accept("differentHetdForTdTp", Boolean.valueOf(isDifferentHetdForTdTp())); //$NON-NLS-1$
        consumer.accept("numberOfStudents", Integer.valueOf(getNumberOfStudents())); //$NON-NLS-1$
    }

    @Override
    public void serialize(JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartObject();
        forEachAttribute((attrName, attrValue) -> {
            JsonUtils.writeField(generator, attrName, attrValue);
        });
        //
        final var persons = JsonUtils.cache(generator);
        final var person = getPerson();
        persons.writeReferenceOrObjectField("person", person, () -> { //$NON-NLS-1$
            JsonUtils.writeObjectAndAttributes(generator, person);
        });
        //
        final var universities = JsonUtils.cache(generator);
        final var university = getUniversity();
        universities.writeReferenceOrObjectField("university", university, () -> { //$NON-NLS-1$
            JsonUtils.writeObjectAndAttributes(generator, university);
        });
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

    /**
     * Change the membership identifier.
     *
     * @param id the identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Replies the teacher.
     *
     * @return the teacher.
     */
    public Person getPerson() {
        return this.person;
    }

    /**
     * Change the teacher related to this activity.
     *
     * @param person the teacher.
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Replies the university in which this activity is realized.
     *
     * @return the university.
     */
    public ResearchOrganization getUniversity() {
        return this.university;
    }

    /**
     * Change the university in which this activity is realized.
     *
     * @param university the university.
     */
    public void setUniversity(ResearchOrganization university) {
        this.university = university;
    }

    /**
     * Replies the first non empty value in the code/acronym and title in that order.
     *
     * @return the code or title.
     * @see #getTitleOrCode()
     * @see #getCode()
     * @see #getTitle()
     */
    public String getCodeOrTitle() {
        final var code = getCode();
        if (Strings.isNullOrEmpty(code)) {
            return getTitle();
        }
        return code;
    }

    /**
     * Replies the first non empty value in the title and code/acronym in that order.
     *
     * @return the title or code.
     * @see #getCodeOrTitle()
     * @see #getCode()
     * @see #getTitle()
     */
    public String getTitleOrCode() {
        final var title = getTitle();
        if (Strings.isNullOrEmpty(title)) {
            return getCode();
        }
        return title;
    }

    /**
     * Replies the code or acronym of the teaching activity.
     *
     * @return the code or acronym.
     * @see #getTitleOrCode()
     * @see #getCodeOrTitle()
     * @see #getTitle()
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Change the code or acronym of the teaching activity.
     *
     * @param code the code or acronym.
     */
    public void setCode(String code) {
        this.code = Strings.emptyToNull(code);
    }

    /**
     * Replies the title of the teaching activity.
     *
     * @return the title.
     * @see #getTitleOrCode()
     * @see #getCodeOrTitle()
     * @see #getCode()
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the teaching activity.
     *
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = Strings.emptyToNull(title);
    }

    /**
     * Replies the level of the teaching activity.
     *
     * @return the level.
     */
    public TeachingActivityLevel getLevel() {
        return this.level;
    }

    /**
     * Change the level of the teaching activity.
     *
     * @param level the level. If it is {@code null}, the level is reset to its default value.
     */
    public void setLevel(TeachingActivityLevel level) {
        this.level = level == null ? DEFAULT_ACTIVITY_LEVEL : level;
    }

    /**
     * Change the level of the teaching activity.
     *
     * @param level the level. If it is {@code null}, the level is reset to its default value.
     */
    public final void setLevel(String level) {
        if (Strings.isNullOrEmpty(level)) {
            setLevel((TeachingActivityLevel) null);
        } else {
            setLevel(TeachingActivityLevel.valueOfCaseInsensitive(level));
        }
    }

    /**
     * Replies the type of student in the teaching activity.
     *
     * @return the student type.
     */
    public StudentType getStudentType() {
        return this.studentType;
    }

    /**
     * Change the type of student in the teaching activity.
     *
     * @param type the student type. If it is {@code null}, the type is reset to its default value.
     */
    public void setStudentType(StudentType type) {
        this.studentType = type == null ? DEFAULT_STUDENT_TYPE : type;
    }

    /**
     * Change the type of student in the teaching activity.
     *
     * @param type the student type. If it is {@code null}, the type is reset to its default value.
     */
    public final void setStudentType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            setStudentType((StudentType) null);
        } else {
            setStudentType(StudentType.valueOfCaseInsensitive(type));
        }
    }

    /**
     * Replies the tearcher's role in the teaching activity.
     *
     * @return the tearcher's role .
     */
    public TeacherRole getRole() {
        return this.role;
    }

    /**
     * Change the tearcher's role in the teaching activity.
     *
     * @param role tearcher's role . If it is {@code null}, the role is reset to its default value.
     */
    public void setRole(TeacherRole role) {
        this.role = role == null ? DEFAULT_ROLE : role;
    }

    /**
     * Change the tearcher's role in the teaching activity.
     *
     * @param role tearcher's role . If it is {@code null}, the role is reset to its default value.
     */
    public final void setRole(String role) {
        if (Strings.isNullOrEmpty(role)) {
            setRole((TeacherRole) null);
        } else {
            setRole(TeacherRole.valueOfCaseInsensitive(role));
        }
    }

    /**
     * Replies the language of the teaching activity.
     *
     * @return the language.
     */
    public CountryCode getLanguage() {
        return this.language;
    }

    /**
     * Change the language of the teaching activity.
     *
     * @param language the language. If it is {@code null}, the level is reset to its default value.
     */
    public void setLanguage(CountryCode language) {
        this.language = language == null ? DEFAULT_LANGUAGE : language;
    }

    /**
     * Change the language of the teaching activity.
     *
     * @param language the language. If it is {@code null}, the level is reset to its default value.
     */
    public final void setLanguage(String language) {
        if (Strings.isNullOrEmpty(language)) {
            setLanguage((CountryCode) null);
        } else {
            setLanguage(CountryCode.valueOfCaseInsensitive(language));
        }
    }

    /**
     * Replies the degree for the teaching activity.
     *
     * @return the degree.
     */
    public String getDegree() {
        return this.degree;
    }

    /**
     * Change the degree for the teaching activity.
     *
     * @param degree the name of the degree.
     */
    public void setDegree(String degree) {
        this.degree = Strings.emptyToNull(degree);
    }

    /**
     * Replies the explanation for the teaching activity that may appear in the resume of the teacher.
     *
     * @return the explanation.
     */
    public String getExplanation() {
        return this.explanation;
    }

    /**
     * Change the explanation for the teaching activity that may appear in the resume of the teacher.
     *
     * @param explanation the explanation.
     */
    public void setExplanation(String explanation) {
        this.explanation = Strings.emptyToNull(explanation);
    }

    /**
     * Replies the start date of the teaching activity.
     *
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * Change the start date of the teaching activity.
     *
     * @param date the start date.
     */
    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    /**
     * Change the start date of the teaching activity.
     *
     * @param date the start date.
     */
    public final void setStartDate(String date) {
        if (!Strings.isNullOrEmpty(date)) {
            try {
                setStartDate(LocalDate.parse(date));
            } catch (Throwable ex) {
                setStartDate((LocalDate) null);
            }
        } else {
            setStartDate((LocalDate) null);
        }
    }

    /**
     * Replies the end date of the teaching activity.
     *
     * @return the start date, or {@code null} if the activity is still active.
     */
    public LocalDate getEndDate() {
        return this.endDate;
    }

    /**
     * Change the end date of the teaching activity.
     *
     * @param date the start date, or {@code null} if the activity is still active.
     */
    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

    /**
     * Change the end date of the teaching activity.
     *
     * @param date the start date, or {@code null} if the activity is still active.
     */
    public final void setEndDate(String date) {
        if (!Strings.isNullOrEmpty(date)) {
            try {
                setEndDate(LocalDate.parse(date));
            } catch (Throwable ex) {
                setEndDate((LocalDate) null);
            }
        } else {
            setEndDate((LocalDate) null);
        }
    }

    /**
     * Replies the URL of the website related to the teaching activity.
     *
     * @return the activity's website.
     */
    public String getActivityUrl() {
        return this.activityUrl;
    }

    /**
     * Change the URL of the website related to the teaching activity.
     *
     * @param url the activity's website.
     */
    public void setActivityUrl(String url) {
        this.activityUrl = Strings.emptyToNull(url);
    }

    /**
     * Change the URL of the website related to the teaching activity.
     *
     * @param url the activity's website.
     */
    public final void setActivityUrl(URL url) {
        if (url != null) {
            setActivityUrl(url.toExternalForm());
        } else {
            setActivityUrl((String) null);
        }
    }

    /**
     * Replies the URL object of the website related to the teaching activity.
     *
     * @return the activity's website, or {@code null} if none.
     */
    public final URL getActivityUrlObject() {
        final var url = getActivityUrl();
        if (!Strings.isNullOrEmpty(url)) {
            try {
                return new URI(url).toURL();
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the URL of the website related to the source code of the teaching activity.
     *
     * @return the source's website.
     */
    public String getSourceUrl() {
        return this.sourceUrl;
    }

    /**
     * Change the URL of the website related to the source code of the teaching activity.
     *
     * @param url the source's website.
     */
    public void setSourceUrl(String url) {
        this.sourceUrl = Strings.emptyToNull(url);
    }

    /**
     * Change the URL of the website related to the source code of the teaching activity.
     *
     * @param url the source's website.
     */
    public final void setSourceUrl(URL url) {
        if (url != null) {
            setSourceUrl(url.toExternalForm());
        } else {
            setSourceUrl((String) null);
        }
    }

    /**
     * Replies the URL object of the website related to the source code of the teaching activity.
     *
     * @return the source's website, or {@code null} if none.
     */
    public final URL getSourceUrlObject() {
        final var url = getSourceUrl();
        if (!Strings.isNullOrEmpty(url)) {
            try {
                return new URI(url).toURL();
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the path to the uploaded slides.
     *
     * @return the path to the uploaded slides.
     */
    public String getPathToSlides() {
        return this.pathToSlides;
    }

    /**
     * Change the path to the uploaded slides.
     *
     * @param path the path to the uploaded slides.
     */
    public void setPathToSlides(String path) {
        this.pathToSlides = Strings.emptyToNull(path);
    }

    /**
     * Replies if the contract for the person implies that his/her has different factors for
     * computing hETD from the real teaching hours.
     *
     * @return {@code true} if there is different factors.
     */
    public boolean isDifferentHetdForTdTp() {
        return this.differentHetdForTdTp;
    }

    /**
     * Change if the contract for the person implies that his/her has different factors for
     * computing hETD from the real teaching hours.
     *
     * @param different {@code true} if there is different factors.
     */
    public void setDifferentHetdForTdTp(boolean different) {
        this.differentHetdForTdTp = different;
    }

    /**
     * Change if the contract for the person implies that his/her has different factors for
     * computing hETD from the real teaching hours.
     *
     * @param different {@code true} if there is different factors.
     */
    public final void setDifferentHetdForTdTp(Boolean different) {
        if (different != null) {
            setDifferentHetdForTdTp(different.booleanValue());
        } else {
            setDifferentHetdForTdTp(false);
        }
    }

    /**
     * Replies the estimated number of students per year in this activity.
     *
     * @return the estimated number of students.
     */
    public int getNumberOfStudents() {
        return this.numberOfStudents;
    }

    /**
     * Change the estimated number of students per year in this activity.
     *
     * @param number the estimated number of students.
     */
    public void setNumberOfStudents(int number) {
        this.numberOfStudents = number >= 0 ? number : 0;
    }

    /**
     * Change the estimated number of students per year in this activity.
     *
     * @param number the estimated number of students.
     */
    public final void setNumberOfStudents(Number number) {
        if (number != null) {
            setNumberOfStudents(number.intValue());
        } else {
            setNumberOfStudents(0);
        }
    }

    /**
     * Replies the map from the types of activities to the number of hours.
     *
     * @return the annual activity mapping.
     * @see #getAnnualTotalHetd()
     * @see #getAnnualTotalHours()
     */
    public Map<TeachingActivityType, Float> getAnnualWorkPerType() {
        if (this.annualWorkPerType == null) {
            this.annualWorkPerType = new HashMap<>();
        }
        return this.annualWorkPerType;
    }

    /**
     * Change the map from the types of activities to the number of hours.
     *
     * @param mapping the annual activity mapping.
     */
    public void setAnnualWorkPerType(Map<TeachingActivityType, Float> mapping) {
        if (this.annualWorkPerType == null) {
            this.annualWorkPerType = new HashMap<>();
        } else {
            this.annualWorkPerType.clear();
        }
        if (mapping != null) {
            this.annualWorkPerType.putAll(mapping);
        }
    }

    /**
     * Replies the total of hETD for this activity.
     *
     * @return the total number of hETD.
     * @see #getAnnualWorkPerType()
     */
    public float getAnnualTotalHetd() {
        var total = 0f;
        final var different = isDifferentHetdForTdTp();
        for (final var activity : getAnnualWorkPerType().entrySet()) {
            final var hours = activity.getValue();
            final var type = activity.getKey();
            if (type != null && hours != null) {
                final var vhours = hours.floatValue();
                if (vhours > 0f) {
                    total += type.convertHoursToHetd(vhours, different);
                }
            }
        }
        return total;
    }

    /**
     * Replies the total of hours for this activity.
     *
     * @return the total number of hours.
     * @see #getAnnualWorkPerType()
     */
    public float getAnnualTotalHours() {
        var total = 0f;
        for (final var hours : getAnnualWorkPerType().values()) {
            if (hours != null) {
                final var vhours = hours.floatValue();
                if (vhours > 0f) {
                    total += vhours;
                }
            }
        }
        return total;
    }

    /**
     * Replies if this activity is active, i.e., the activity is not ended according to today.
     *
     * @return {@code true} if the activity is active for the current date.
     */
    public boolean isActive() {
        return isActive(null);
    }

    /**
     * Replies if this activity is active, i.e., the activity is not ended according to the reference date.
     *
     * @param referenceDate the date of reference.
     * @return {@code true} if the activity is active for the reference date.
     */
    public boolean isActive(LocalDate referenceDate) {
        final LocalDate now = referenceDate == null ? LocalDate.now() : referenceDate;
        final LocalDate start = getStartDate();
        if (start != null && now.isBefore(start)) {
            return false;
        }
        final LocalDate end = getEndDate();
        return end == null || !now.isAfter(end);
    }

    /**
     * Replies a string representation of the year range of activity.
     *
     * @param sinceLabel the text that represents the "Since 9999" message with the year in place of 9999.
     *                   The label should contains the placeholder {@code {0}} for marking the position of the year,
     *                   according to {@link MessageFormat#format(String, Object...)}.
     * @return the activity years.
     */
    public String getActivityYears(String sinceLabel) {
        final var start = getStartDate();
        final var sy = start.getYear();
        final var end = getEndDate();
        if (end == null) {
            if (Strings.isNullOrEmpty(sinceLabel)) {
                return sy + "+"; //$NON-NLS-1$
            }
            return MessageFormat.format(sinceLabel, Integer.toString(sy));
        }
        final var ey = end.getYear();
        if (sy == ey) {
            return Integer.toString(sy);
        }
        if (sy < ey) {
            return sy + "-" + ey; //$NON-NLS-1$
        }
        return ey + "-" + sy; //$NON-NLS-1$
    }

    /**
     * Replies the first available URL in the {@link #getActivityUrl()}, {@link #getPathToSlides()} and
     * {@link #getSourceUrl()}.
     *
     * @return the first available URL.
     */
    public String getFirstUrl() {
        var url = getActivityUrl();
        if (!Strings.isNullOrEmpty(url)) {
            return url;
        }
        url = getPathToSlides();
        if (!Strings.isNullOrEmpty(url)) {
            return url;
        }
        url = getSourceUrl();
        if (!Strings.isNullOrEmpty(url)) {
            return url;
        }
        return null;
    }

    /**
     * Replies the first available URI in the {@link #getActivityUrl()}, {@link #getPathToSlides()} and
     * {@link #getSourceUrl()}.
     *
     * @return the first available URL.
     */
    public URI getFirstUri() {
        final var url = getFirstUrl();
        if (!Strings.isNullOrEmpty(url)) {
            try {
                return new URI(url);
            } catch (Throwable ex) {
                //
            }
        }
        return null;
    }

    /**
     * Replies the types of pedagogical activities that are carried out in the teaching activity.
     *
     * @return the types of pedagogical activities.
     * @since 3.7
     */
    public Set<PedagogicalPracticeType> getPedagogicalPracticeTypes() {
        if (this.pedagogicalPracticeTypes == null) {
            this.pedagogicalPracticeTypes = new HashSet<>();
        }
        return this.pedagogicalPracticeTypes;
    }

    /**
     * Change the types of pedagogical activities that are carried out in the teaching activity.
     *
     * @param types the types of pedagogical activities.
     * @since 3.7
     */
    public void setPedagogicalPracticeTypes(Collection<PedagogicalPracticeType> types) {
        if (this.pedagogicalPracticeTypes == null) {
            this.pedagogicalPracticeTypes = new HashSet<>();
        } else {
            this.pedagogicalPracticeTypes.clear();
        }
        this.pedagogicalPracticeTypes.addAll(types);
    }

    @Override
    public String toString() {
        return EntityUtils.toString(this, getCode(), getTitle());
    }

}
