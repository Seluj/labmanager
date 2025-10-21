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

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * Type of responsibility taken by a member of an organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum Responsibility {

    /**
     * President or chief executive officer of the organization.
     */
    PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Dean of the organization.
     */
    DEAN(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Executive Director of the organization. A executive director is usually an employee of the organization.
     */
    EXECUTIVE_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Director of the organization. A director is usually not an employee of the organization.
     */
    DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Vice-president of the organization.
     */
    VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Deputy director of the organization.
     */
    DEPUTY_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Research vice-president of the organization.
     */
    RESEARCH_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Research director of the organization.
     */
    RESEARCH_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Numeric service vice-president of the organization.
     */
    NUMERIC_SERVICE_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Numeric service director of the organization.
     */
    NUMERIC_SERVICE_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director for pedagogy of the organization.
     */
    PEDAGOGY_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Communication vice-president of the organization.
     */
    COMMUNICATION_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Communication director of the organization.
     */
    COMMUNICATION_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Faculty director of the organization.
     */
    FACULTY_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Faculty dean of the organization.
     */
    FACULTY_DEAN(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Department director of the organization.
     */
    DEPARTMENT_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Administration council vice-president of the organization.
     */
    ADMINISTRATION_COUNCIL_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * President of the Restricted Administration Council of the organization.
     */
    RESTRICTED_ADMINISTRATION_COUNCIL_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Scientific council vice-president of the organization.
     */
    SCIENTIFIC_COUNCIL_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Teaching council vice-president of the organization.
     */
    PEDAGOGY_COUNCIL_VICE_PRESIDENT(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Member of the administration council of the organization.
     */
    ADMINISTRATION_COUNCIL_MEMBER(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Member of the scientific council of the organization.
     */
    SCIENTIFIC_COUNCIL_MEMBER(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Member of the pedagogy council of the organization.
     */
    PEDAGOGY_COUNCIL_MEMBER(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Member of the numeric council of the organization.
     */
    NUMERIC_COUNCIL_MEMBER(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Member of the laboratory council of the organization.
     */
    LABORATORY_COUNCIL_MEMBER(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Responsible of business unit of the organization.
     */
    BUSINESS_UNIT_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Leader of a team of the organization.
     */
    TEAM_SCIENTFIC_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Leader of a scientific axis of the organization.
     */
    SCIENTIFIC_AXIS_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Leader of a application axis of the organization.
     */
    APPLICATION_AXIS_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Head of a platform of the organization.
     */
    PLATFORM_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Head of a transverse axis of the organization.
     */
    TRANSVERSE_AXIS_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Direction secretary of the organization.
     */
    DIRECTION_SECRETARY(false) {
        @Override
        public boolean isDirectionLevel() {
            return true;
        }
    },

    /**
     * Responsible of IT of the organization.
     */
    IT_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Seminar and conference responsible of the organization.
     */
    SEMINAR_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Gender-equality responsible of the organization.
     */
    GENDER_EQUALITY_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Human-resource responsible of the organization.
     */
    HUMAN_RESOURCE_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Responsible of Open Science of the organization.
     */
    OPEN_SCIENCE_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Communication responsible of the organization.
     */
    COMMUNICATION_RESPONSIBLE(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director of the department of engineering training under student status in computer science.
     * This responsibility is specific to the "Université de Technologie de Belfort Montbeliard".
     */
    CS_FISE_DIRECTOR(true) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director of the department of engineering training under apprentice status in computer science
     * This responsibility is specific to the "Université de Technologie de Belfort Montbeliard".
     */
    CS_FISA_DIRECTOR(true) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director of the Center of Energy and Computer Science.
     * This responsibility is specific to the "Université de Technologie de Belfort Montbeliard".
     */
    ENERGY_CS_CENTER_DIRECTOR(true) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director of the Center of Transport and Mobility.
     * This responsibility is specific to the "Université de Technologie de Belfort Montbeliard".
     */
    TRANSPORT_MOBILITY_CENTER_DIRECTOR(true) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    },

    /**
     * Director of the international service
     * This responsibility is specific to the "Université de Technologie de Belfort Montbeliard".
     */
    INTERNATIONAL_SERVICE_DIRECTOR(false) {
        @Override
        public boolean isDirectionLevel() {
            return false;
        }
    };

    private static final String MESSAGE_PREFIX = "positionType."; //$NON-NLS-1$

    private final boolean isSpecific;

    /**
     * Constructor.
     *
     * @param specific indicates if this responsibility is specific to a research institution.
     */
    Responsibility(boolean specific) {
        this.isSpecific = specific;
    }

    /**
     * Replies the member status that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the status, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static Responsibility valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid position type: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the status in the current language.
     *
     * @param messages the accessors to the localized labels.
     * @param gender   the gender of the person who has the position of this type.
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Gender gender, Locale locale) {
        var g = gender;
        if (g == null || g == Gender.NOT_SPECIFIED) {
            g = Gender.OTHER;
        }
        final var label = messages.getMessage(MESSAGE_PREFIX + name() + "_" + g.name(), locale); //$NON-NLS-1$
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies if this responsibility is specific to a research organization.
     *
     * @return {@code true} if the responsibility is specific.
     */
    public boolean isSpecific() {
        return this.isSpecific;
    }

    /**
     * Replies if this responsibility corresponds to a direction of the associated research organization.
     *
     * @return {@code true} if the responsibility is for direction.
     */
    public abstract boolean isDirectionLevel();

}
