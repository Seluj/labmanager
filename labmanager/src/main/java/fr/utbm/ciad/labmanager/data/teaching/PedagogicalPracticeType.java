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

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Innovative pedagogical practices are listing in this enumeration.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.7
 */
public enum PedagogicalPracticeType {

    /**
     * Practices are based on the historical method with lectures, tutorials and lab works.
     */
    HISTORICAL_METHOD,

    /**
     * Pedagogy 3.0 is based on 3 notions: autonomy, permanence and creativity.
     *
     * <ul>
     * <li>Autonomy: In order to help the student understand the consequences and impacts of his actions, a monetary
     *     and political system is put in place. Every decision is made democratically so that the students are
     *     directly involved. They are endowed with a responsibility in the classroom, thus taking a position in
     *     the school hierarchy.</li>
     * <li>The permanence of learning: Based on a study which proves that the student learns better when he
     *     acts, the students make the corrections of his exercises, create lessons and carry out an overall
     *     assessment at the end.</li>
     * <li>Creativity: Students can choose the tools they want to learn and transmit the content of the course,
     *     promoting the sharing of knowledge with peers in a fun way.</li>
     * </ul>
     */
    PEDAGOGY_3_0,

    /**
     * The inverted classroom consists in providing students, before the course, with documents so that they
     * can work on the theory themselves and once in class, they can apply their knowledge thus acquired to
     * concrete situations that are proposed to them.
     * The Professor no longer ensures the presentation of the rules but, by supervising the students, he will
     * nevertheless have the opportunity to explain these rules and to give examples of their application.
     */
    INVERTED_CLASSROOM,

    /**
     * The reverse class consists of completely reversing the roles between teacher and learner. Thus the
     * learners build the course, the knowledge checks thus taking the place of the teacher while it is the
     * teacher who will be noted, checked by the learners. During the time of the lessons, the learners build
     * the lessons together in groups, share them, confront them and the teacher plays the role of coach,
     * orients them, guides them, challenges them. Learners are graded with regard to their skills, to organize
     * themselves, to think, to build in a given time, as within a company.
     */
    REVERSE_CLASS,

    /**
     * By collaborative work, it is necessary to understand on the one hand, the cooperation between the members
     * of a team and, on the other hand, the realization of a finished product.
     */
    COLLABORATIVE_WORKS,

    /**
     * The students are located remotely to the teacher. The main types of distance learning are:
     * <ul>
     * <li>MOOCs: this is a chaptered course open to any Internet user. Often spread over several weeks, embellished with
     *     remote group work, online chat, quizzes, it allows everyone to learn about a subject and benefit from knowledge
     *     on a theme.</li>
     * <li>SPOCs: online chapter courses, often spread over several sessions, on the same principle as MOOCs. On the other
     *     hand, the audience is limited to a group of users defined upstream (about thirty for example).</li>
     * <li>COOCs (for Corporate Open Online Course): this is online training used within the company to train employees.</li>
     * <li>SOOC (for Social Open Online Course): this is an online training whose objective is to increase interactions
     *     between course participants. To do this, SOOCs are enhanced with collaborative tools (e.g. forum, content
     *     creation tools such as wiki, blog, etc.) (See {@link #COLLABORATIVE_WORKS}).</li>
     * </ul>
     */
    DISTANCE_LEARNING,

    /**
     * The use of social networks can help promote interactivity in the classroom by providing additional resources, such
     * as news. Students can react online or in class, but also post content that they think is related to the course taught.
     */
    SOCIAL_NETWORK_LEARNING,

    /**
     * Many tools allow you to modernize a course. They come in the form of presentation tools such as Powerpoint, diagrams,
     * videos. Their strengths are: their attractiveness, mobilize emotion, allow the transmission of testimony,
     * expects a large audience, often space-saving support, collective use.
     */
    MODERN_PRESENTATION_TOOLS,

    /**
     * The virtual classroom is an educational device allowing students and one or more teachers to be brought together
     * at a given time via a tool. A virtual class can take different formats (single, multiple, blended sessions, etc.).
     */
    VIRTUAL_CLASSROOM,

    /**
     * Pedagogy in project mode encourages people to put knowledge into practice through collective work on a given problem.
     */
    PROJECT_ORIENTED,

    /**
     * Problem-Based Learning (or PBL) takes a socio-constructivist approach to learning. It is a question of immersing
     * the learner in the subject through a problem to be solved in a few hours (or a project to be carried out over
     * several days, even several months). This leads him to seek information to develop a solution, knowing that the
     * concepts are not previously taught to students.
     */
    PROBLEM_BASED_LEARNING,

    /**
     * The game is used here as a pedagogical spring, but to lead to serious learning. Some of these games are diverted
     * from their initial playful use. Furthermore, serious games can also be used when they are explicitly dedicated to
     * the conduct of educational activities.
     */
    SERIOUS_GAME,

    /**
     * A practice that is not directly supported by the labmanager software is applied.
     */
    OTHER_METHOD;

    private static final String MESSAGE_PREFIX = "pedagogicalPracticeType."; //$NON-NLS-1$

    private static final String MESSAGE_PREFIX_DESCRIPTION = MESSAGE_PREFIX + "description."; //$NON-NLS-1$

    /**
     * Replies the type of practice that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the practice type, to search for.
     * @return the type of practice.
     * @throws IllegalArgumentException if the given name does not corresponds to a type of practice.
     */
    public static PedagogicalPracticeType valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid activity type: " + name); //$NON-NLS-1$
    }

    /**
     * Replies all the pedagogical practice types sorted according to their display names.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the list of all the types.
     */
    public static List<PedagogicalPracticeType> getAllDisplayTypes(MessageSourceAccessor messages, Locale locale) {
        return Arrays.asList(values()).stream().sorted(getLabelBasedComparator(messages, locale))
                .collect(Collectors.toList());
    }

    /**
     * Replies the comparator that may be used for sorting the pedagogical pratices based on their labels.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the comparator.
     */
    public static Comparator<PedagogicalPracticeType> getLabelBasedComparator(MessageSourceAccessor messages, Locale locale) {
        return (a, b) -> a.getLabel(messages, locale).compareToIgnoreCase(b.getLabel(messages, locale));
    }

    /**
     * Replies the label of the practice type in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the practice type in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the description of the practice type in the given language.
     *
     * @param messages the accessor to the localized descriptions.
     * @param locale   the locale to use.
     * @return the description of the practice type in the given  language.
     */
    public String getDescription(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX_DESCRIPTION + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
