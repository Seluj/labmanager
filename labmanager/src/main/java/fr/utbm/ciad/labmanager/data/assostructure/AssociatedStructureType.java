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

package fr.utbm.ciad.labmanager.data.assostructure;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Describe the type of associated structure.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public enum AssociatedStructureType {

    /**
     * Created private company, e.g., Sping-Off...
     */
    PRIVATE_COMPANY {
        @Override
        public Boolean isInternational() {
            return null;
        }

        @Override
        public Boolean isEuropean() {
            return null;
        }

        @Override
        public Boolean isNational() {
            return null;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Industrial chair with a company.
     */
    INDUSTRIAL_CHAIR {
        @Override
        public Boolean isInternational() {
            return null;
        }

        @Override
        public Boolean isEuropean() {
            return null;
        }

        @Override
        public Boolean isNational() {
            return null;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return true;
        }
    },

    /**
     * Research chair.
     */
    RESEARCH_CHAIR {
        @Override
        public Boolean isInternational() {
            return null;
        }

        @Override
        public Boolean isEuropean() {
            return null;
        }

        @Override
        public Boolean isNational() {
            return null;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return true;
        }
    },

    /**
     * Shared international research lab.
     */
    INTERNATIONAL_RESEARCH_LAB {
        @Override
        public Boolean isInternational() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return true;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Shared European research lab.
     *
     * @since 3.6
     */
    EUROPEAN_RESEARCH_LAB {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return true;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Shared national research lab.
     */
    NATIONAL_RESEARCH_LAB {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.TRUE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return true;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Shared international group of scientific interest.
     */
    INTERNATIONAL_SCIENTIFIC_INTEREST_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return true;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Shared European group of scientific interest.
     *
     * @since 3.6
     */
    EUROPEAN_SCIENTIFIC_INTEREST_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return true;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * Shared national group of scientific interest.
     */
    NATIONAL_SCIENTIFIC_INTEREST_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.TRUE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return true;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * International research group.
     *
     * @since 3.6
     */
    INTERNATIONAL_RESEARCH_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return true;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * European research group.
     *
     * @since 3.6
     */
    EUROPEAN_RESEARCH_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return true;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * National research group.
     *
     * @since 3.6
     */
    NATIONAL_RESEARCH_GROUP {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.TRUE;
        }

        @Override
        public boolean isHostedCompany() {
            return false;
        }

        @Override
        public boolean isResearchGroup() {
            return true;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * International company that is hosted in the research organization.
     *
     * @since 3.6
     */
    HOSTED_INTERNATIONAL_COMPANY {
        @Override
        public Boolean isInternational() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return true;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * European company that is hosted in the research organization.
     *
     * @since 3.6
     */
    HOSTED_EUROPEAN_COMPANY {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.TRUE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isHostedCompany() {
            return true;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    },

    /**
     * National company that is hosted in the research organization.
     *
     * @since 3.6
     */
    HOSTED_NATIONAL_COMPANY {
        @Override
        public Boolean isInternational() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isEuropean() {
            return Boolean.FALSE;
        }

        @Override
        public Boolean isNational() {
            return Boolean.TRUE;
        }

        @Override
        public boolean isHostedCompany() {
            return true;
        }

        @Override
        public boolean isResearchGroup() {
            return false;
        }

        @Override
        public boolean isScientificInterestGroup() {
            return false;
        }

        @Override
        public boolean isSharedResearchLab() {
            return false;
        }

        @Override
        public boolean isChair() {
            return false;
        }
    };

    private static final String MESSAGE_PREFIX = "associatedStructureType."; //$NON-NLS-1$

    /**
     * Replies the project status that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the project status, to search for.
     * @return the project status.
     * @throws IllegalArgumentException if the given name does not corresponds to a project status.
     */
    public static AssociatedStructureType valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var ranking : values()) {
                if (name.equalsIgnoreCase(ranking.name())) {
                    return ranking;
                }
            }
        }
        throw new IllegalArgumentException("Invalid project status: " + name); //$NON-NLS-1$
    }

    /**
     * Replies all the associated structure types sorted according to their display names.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the list of all the types.
     */
    public static List<AssociatedStructureType> getAllDisplayTypes(MessageSourceAccessor messages, Locale locale) {
        return Arrays.asList(values()).stream().sorted(
                        (a, b) -> a.getLabel(messages, locale).compareToIgnoreCase(b.getLabel(messages, locale)))
                .collect(Collectors.toList());
    }

    /**
     * Replies the label of the project status in the given language.
     *
     * @param messages the accessor to the localized messages.
     * @param locale   the locale to use.
     * @return the label of the project status in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies if this type of associated structure is for international structure, i.e. a structure
     * outside Europe.
     * If the replied value is {@code null}, then the international status is not determined by its type.
     *
     * @return {@link Boolean#TRUE} if the type is for an international structure;
     * {@link Boolean#FALSE} if the type is for a structure that is not international, i.e. European or national;
     * {@code null} if the type is not associated to an international, European or national tag.
     * @since 3.6
     */
    public abstract Boolean isInternational();

    /**
     * Replies if this type of associated structure is for European structure, i.e. a structure
     * in Europe but not in the country.
     * If the replied value is {@code null}, then the European status is not determined by its type.
     *
     * @return {@link Boolean#TRUE} if the type is for an European structure;
     * {@link Boolean#FALSE} if the type is for a structure that is not European, i.e. international or national;
     * {@code null} if the type is not associated to an international, European or national tag.
     * @since 3.6
     */
    public abstract Boolean isEuropean();

    /**
     * Replies if this type of associated structure is for national structure, i.e. a structure
     * in the country.
     * If the replied value is {@code null}, then the national status is not determined by its type.
     *
     * @return {@link Boolean#TRUE} if the type is for a national structure;
     * {@link Boolean#FALSE} if the type is for a structure that is not national, i.e. international or European;
     * {@code null} if the type is not associated to an international, European or national tag.
     * @since 3.6
     */
    public abstract Boolean isNational();

    /**
     * Replies if this type represents an hosted company.
     *
     * @return {@code true} if the type is for an hosted.
     * @since 3.6
     */
    public abstract boolean isHostedCompany();

    /**
     * Replies if this type represents a research group, e.g. GdR.
     *
     * @return {@code true} if the type is for a research group.
     * @since 3.6
     */
    public abstract boolean isResearchGroup();

    /**
     * Replies if this type represents a scientific interest group, e.g. GIS.
     *
     * @return {@code true} if the type is for a scientific interest group.
     * @since 3.6
     */
    public abstract boolean isScientificInterestGroup();

    /**
     * Replies if this type represents a shared research lab, e.g. LabCom.
     *
     * @return {@code true} if the type is for a shared research lab.
     * @since 3.6
     */
    public abstract boolean isSharedResearchLab();

    /**
     * Replies if this type represents a char, whatever it it is industrial or academic.
     *
     * @return {@code true} if the type is for a chair.
     * @since 3.6
     */
    public abstract boolean isChair();

}
