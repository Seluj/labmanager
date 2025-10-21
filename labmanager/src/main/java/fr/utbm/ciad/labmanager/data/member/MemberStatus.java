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
 * Status of a person in a research organization.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum MemberStatus {
    /**
     * Emeritus full professor (professeur émérite des universites).
     */
    EMERITUS_FULL_PROFESSOR {
        @Override
        public int getHierachicalLevel() {
            return 0;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return true;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return true;
        }
    },
    /**
     * Full professor (professeur des universites).
     */
    FULL_PROFESSOR {
        @Override
        public int getHierachicalLevel() {
            return 0;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0.5f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return true;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Research Director (Directeur de recherche).
     */
    RESEARCH_DIRECTOR {
        @Override
        public int getHierachicalLevel() {
            return 0;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return true;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Emeritus associate professor (maitre de conferences émérite) with HDR.
     */
    EMERITUS_ASSOCIATE_PROFESSOR_HDR {
        @Override
        public int getHierachicalLevel() {
            return 1;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return true;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return true;
        }
    },
    /**
     * Emeritus associate professor (maitre de conferences émérite) without HDR.
     */
    EMERITUS_ASSOCIATE_PROFESSOR {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return true;
        }
    },
    /**
     * Associate professor (maitre de conferences) with HDR.
     */
    ASSOCIATE_PROFESSOR_HDR {
        @Override
        public int getHierachicalLevel() {
            return 1;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0.5f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return true;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Associate professor (maitre de conferences) without HDR.
     */
    ASSOCIATE_PROFESSOR {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0.5f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Contractual teacher-researcher (enseignant chercheur contractuel) with PhD.
     */
    CONTRACTUAL_RESEARCHER_TEACHER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0.5f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Contractual teacher-researcher (enseignant chercheur contractuel) without PhD.
     */
    CONTRACTUAL_RESEARCHER_TEACHER {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0.5f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Researcher (Chargé de recherche, LRU, etc.) with PhD.
     */
    RESEARCHER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Researcher (Chargé de recherche, LRU, etc.) without PhD.
     */
    RESEARCHER {
        @Override
        public int getHierachicalLevel() {
            return 2;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Postdoc.
     */
    POSTDOC {
        @Override
        public int getHierachicalLevel() {
            return 3;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Research engineer (IGR) with PhD.
     */
    RESEARCH_ENGINEER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 3;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Research engineer (IGR) without PhD.
     */
    RESEARCH_ENGINEER {
        @Override
        public int getHierachicalLevel() {
            return 3;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * PHD student.
     */
    PHD_STUDENT {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Engineer (IGE) with PhD.
     */
    ENGINEER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Engineer (IGE) without PhD.
     */
    ENGINEER {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Administrative staff.
     */
    ADMIN {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return true;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Teacher (PRAG, etc.) with PhD.
     */
    TEACHER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Teacher (PRAG, etc.) without PhD.
     */
    TEACHER {
        @Override
        public int getHierachicalLevel() {
            return 4;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return true;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return true;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Master student with contract (alternant in France)
     */
    CONTRACT_MASTER_STUDENT {
        @Override
        public int getHierachicalLevel() {
            return 5;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Master student in internship.
     */
    MASTER_STUDENT {
        @Override
        public int getHierachicalLevel() {
            return 5;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return true;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Bachelor student with contract (apprenti in France).
     */
    OTHER_CONTRACT_STUDENT {
        @Override
        public int getHierachicalLevel() {
            return 6;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return true;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return false;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Bachelor student in internship.
     */
    OTHER_STUDENT {
        @Override
        public int getHierachicalLevel() {
            return 6;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 1f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return false;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return true;
        }

        @Override
        public boolean isSupervisor() {
            return false;
        }

        @Override
        public boolean isExternalPosition() {
            return true;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Associate member with PhD.
     */
    ASSOCIATED_MEMBER_PHD {
        @Override
        public int getHierachicalLevel() {
            return 7;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return true;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return true;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    },
    /**
     * Associate member without PhD.
     */
    ASSOCIATED_MEMBER {
        @Override
        public int getHierachicalLevel() {
            return 7;
        }

        @Override
        public float getUsualResearchFullTimeEquivalent() {
            return 0f;
        }

        @Override
        public boolean isPermanentPositionAllowed() {
            return false;
        }

        @Override
        public boolean isResearcher() {
            return true;
        }

        @Override
        public boolean isTeacher() {
            return false;
        }

        @Override
        public boolean isTechnicalStaff() {
            return false;
        }

        @Override
        public boolean isAdministrativeStaff() {
            return false;
        }

        @Override
        public boolean isPhDOwner() {
            return false;
        }

        @Override
        public boolean isHdrOwner() {
            return false;
        }

        @Override
        public boolean isSupervisable() {
            return false;
        }

        @Override
        public boolean isSupervisor() {
            return true;
        }

        @Override
        public boolean isExternalPosition() {
            return true;
        }

        @Override
        public boolean isEmeritus() {
            return false;
        }
    };

    private static final String MESSAGE_PREFIX = "memberStatus."; //$NON-NLS-1$

    private static final String TITLE_POSTFIX = "_title"; //$NON-NLS-1$

    /**
     * Replies the member status that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the status, to search for.
     * @return the status.
     * @throws IllegalArgumentException if the given name does not corresponds to a type.
     */
    public static MemberStatus valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var status : values()) {
                if (name.equalsIgnoreCase(status.name())) {
                    return status;
                }
            }
        }
        throw new IllegalArgumentException("Invalid member status: " + name); //$NON-NLS-1$
    }

    /**
     * Replies if the status supposes that the member owns a PhD.
     *
     * @return {@code true} if the member is supposed to own a PhD.
     * @since 3.6
     */
    public abstract boolean isPhDOwner();

    /**
     * Replies if the status supposes that the member owns a HDR.
     *
     * @return {@code true} if the member is supposed to own a HDR.
     * @since 3.6
     */
    public abstract boolean isHdrOwner();

    /**
     * Replies if the status is for emeritus position.
     *
     * @return {@code true} if the member is supposed to be emeritus.
     * @since 3.6
     */
    public abstract boolean isEmeritus();

    /**
     * Replies the hierarchical level.
     *
     * @return the level: {@code 0} for the higher level, higher is the value lower is the hierarchical position.
     */
    public abstract int getHierachicalLevel();

    /**
     * Replies the amount of full-time equivalent for this type of member status that is usually defined
     * in the job status for the associated position.
     *
     * @return the amount of full-time equivalent. {@code 1} means a full-time, {@code 0.5} means 50% part-time.
     * @since 2.2
     */
    public abstract float getUsualResearchFullTimeEquivalent();

    /**
     * Replies if this status allowed to be associated to a permanent position.
     *
     * @return {@code true} if the status could be associated to a permanent position.
     * @since 2.2
     */
    public abstract boolean isPermanentPositionAllowed();

    /**
     * Replies if the status includes research activities.
     *
     * @return {@code true} if the status includes activities of research.
     */
    public abstract boolean isResearcher();

    /**
     * Replies if the status includes teaching activities.
     *
     * @return {@code true} if the status includes activities of teaching.
     */
    public abstract boolean isTeacher();

    /**
     * Replies if the status includes engineering or technical activities.
     *
     * @return {@code true} if the status includes engineering or technical activities.
     */
    public abstract boolean isTechnicalStaff();

    /**
     * Replies if the status includes administrative activities.
     *
     * @return {@code true} if the status includes administrative activities.
     */
    public abstract boolean isAdministrativeStaff();

    /**
     * Replies if the status enables to be supervised by another person.
     *
     * @return {@code true} if the status enables supervision by another person.
     * @since 2.1
     */
    public abstract boolean isSupervisable();

    /**
     * Replies if the status enables to be supervisor of another person.
     *
     * @return {@code true} if the status enables supervisor of another person.
     * @since 2.1
     */
    public abstract boolean isSupervisor();

    /**
     * Replies if this status corresponds to an external position outside the organization itself.
     *
     * @return {@code true} if the status could be associated to an external position.
     * @since 2.3
     */
    public abstract boolean isExternalPosition();

    /**
     * Replies the label of the status in the current language.
     *
     * @param messages the accessor to the localized labels.
     * @param gender   the gender of the person concerned by the member status.
     * @param former   is the status for a former member?
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Gender gender, boolean former, Locale locale) {
        final var gndr = gender == null || gender == Gender.NOT_SPECIFIED ? Gender.OTHER : gender;
        final var key = new StringBuilder();
        key.append(MESSAGE_PREFIX).append(name());
        if (former) {
            key.append("_former"); //$NON-NLS-1$
        }
        key.append("_").append(gndr.name()); //$NON-NLS-1$
        final String label;
        if (locale == null) {
            label = messages.getMessage(key.toString());
        } else {
            label = messages.getMessage(key.toString(), locale);
        }
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the typical acronym used for this position in the French research bodies.
     *
     * @param messages the accessor to the localized acronyms.
     * @return the typical acronym in France for the position.
     * @since 4.0
     */
    public String getFrenchAcronym(MessageSourceAccessor messages) {
        String key = MESSAGE_PREFIX + name() +
                "_acronym"; //$NON-NLS-1$
        final var label = messages.getMessage(key, Locale.FRANCE);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the civil title associated to this status, if it not a civil title based on gender.
     *
     * @param messages the accessors to the localized titles.
     * @param locale   the locale that must be used for creating the localized civil title.
     * @return the civil title or {@code null} if none is defined.
     * @see Gender
     */
    public String getCivilTitle(MessageSourceAccessor messages, Locale locale) {
        return Strings.emptyToNull(messages.getMessage(MESSAGE_PREFIX + name() + TITLE_POSTFIX, (String) null, locale));
    }

}
