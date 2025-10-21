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

import java.util.Locale;

/**
 * Describe the type of teaching activity.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
public enum TeachingActivityType {

    /**
     * Lectures (or Cours Magistral in French) are, in most cases, theoretical courses in an amphitheater which bring together all the
     * students of a class.
     */
    LECTURES(1.5f, 1.5f),

    /**
     * Integrated courses (or Cours Intégrés in French) are courses in which the exercises are immediately integrated
     * by way of explanation. Knowledge is implemented alongside learning.
     */
    INTEGRATED_COURSES(1.25f, 1.25f),

    /**
     * Turotials (or Travaux dirigés in Frencj) are an opportunity to meet in small groups and to deepen the
     * notions covered in {@link #LECTURE lectures}, to put into practice the methods that will be required
     * for the exam and above all to ask questions.
     */
    TUTORIALS(1f, 1f),

    /**
     * Practical works (or Travaux pratiques in French) are made up of a small number of students. They are an opportunity
     * to carry out experiments and put into practice the theory learned in class.
     */
    PRACTICAL_WORKS(1f, 2f / 3f),

    /**
     * Supervision of student groups.
     */
    GROUP_SUPERVISION(1f, 2f / 3f),

    /**
     * Supervision of student projects.
     */
    PROJECT_SUPERVISION(1f, 2f / 3f);

    private static final String MESSAGE_PREFIX = "teachingActivityType."; //$NON-NLS-1$

    private final float internalContractHetdFactor;

    private final float externalContractHetdFactor;

    TeachingActivityType(float permanentFactor, float externalFactor) {
        this.internalContractHetdFactor = permanentFactor;
        this.externalContractHetdFactor = externalFactor;
    }

    /**
     * Replies the type of activity that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the activity type, to search for.
     * @return the type of activity.
     * @throws IllegalArgumentException if the given name does not corresponds to a type of activity.
     */
    public static TeachingActivityType valueOfCaseInsensitive(String name) {
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
     * Replies the number of hETD (equivalent number of hours for tutorials) for a single hour of this type of activity.
     *
     * @param differentHetdFactors indicates if the contract for the person implies that his/her has different factors for
     *                             computing hETD from the real teaching hours.
     * @return the hETD factor.
     */
    public float getHetdFactor(boolean differentHetdFactors) {
        return differentHetdFactors ? this.externalContractHetdFactor : this.internalContractHetdFactor;
    }

    /**
     * Convert a given number of hours to its equivalent number of hETD.
     *
     * @param hours                the number of hours to convert.
     * @param differentHetdFactors indicates if the contract for the person implies that his/her has different factors for
     *                             computing hETD from the real teaching hours.
     * @return the hETD number.
     */
    public float convertHoursToHetd(float hours, boolean differentHetdFactors) {
        return getHetdFactor(differentHetdFactors) * hours;
    }

    /**
     * Replies the label of the activity type in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the activity type in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final String label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

}
