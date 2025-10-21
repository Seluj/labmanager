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

package fr.utbm.ciad.labmanager.utils.funding;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * The enumeration {@code FundingScheme} provides a list of well-known funding sources.
 * The order of the items (their ordinal values) is frm the less important to the most important.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
public enum FundingScheme {
    /**
     * No funding scheme.
     */
    NOT_FUNDED {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Funds are provided by the person himself.
     */
    SELF_FUNDING {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * A local institution is funding.
     */
    LOCAL_INSTITUTION {
        @Override
        public boolean isRegional() {
            return true;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * The hosting organization is funding on its own budgets.
     */
    HOSTING_ORGANIZATION {
        @Override
        public boolean isRegional() {
            return true;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * A french university is funding.
     */
    FRENCH_UNIVERSITY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return true;
        }
    },
    /**
     * Other source of funding from France.
     */
    FRENCH_OTHER {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Funds are provided in the context of a CARNOT Institution and managed by ANR.
     *
     * @see "https://www.instituts-carnot.eu/fr"
     * @see "https://www.anrt.asso.fr/fr"
     */
    CARNOT {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * The regional council of Burgundy Franche Comte is funding.
     */
    REGION_BFC {
        @Override
        public boolean isRegional() {
            return true;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * "Contrat Plan Etat Région".
     *
     * @see "http://www.datar.gouv.fr/contrats-etat-regions"
     */
    CPER {
        @Override
        public boolean isRegional() {
            return true;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Fonds uniques interministériels.
     *
     * @see "https://www.entreprises.gouv.fr/fr/innovation/poles-de-competitivite/presentation-des-poles-de-competitivite"
     */
    FUI {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * ADEME.
     *
     * @see "https://www.ademe.fr/"
     */
    ADEME {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * French ANR is funding.
     *
     * @see "https://anr.fr/"
     */
    ANR {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * I-SITE (Initiatives-Science – Innovation –Territoires – Economie)
     */
    ISITE {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * IDEX (Initiatives d’Excellence)
     */
    IDEX {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * PIA (Plan d'Investissement d'Avenir)
     */
    PIA {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * France 2030 (Plan d'Investissement d'Avenir France 2030)
     */
    FRANCE_2030 {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Funds are provided in the context of a CIFRE by ANRT.
     *
     * @see #FRENCH_COMPANY
     * @see "https://www.enseignementsup-recherche.gouv.fr/fr/les-cifre-46510"
     * @see "https://www.anrt.asso.fr/fr"
     */
    CIFRE {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * A private company is funding, but outside the scope of a {@link #CIFRE}.
     *
     * @see #CIFRE
     */
    FRENCH_COMPANY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return true;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * An EU university is funding.
     */
    EU_UNIVERSITY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return true;
        }
    },
    /**
     * Other source of funding from Europe.
     */
    EU_OTHER {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * "Fonds européen de développement régional".
     *
     * @see "https://ec.europa.eu/info/funding-tenders/find-funding/eu-funding-programmes/european-regional-development-fund-erdf_fr"
     */
    FEDER {
        @Override
        public boolean isRegional() {
            return true;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * EUREKA project is funding.
     */
    EUREKA {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * EUROSTAR project is funding.
     */
    EUROSTAR {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * European COST action.
     *
     * @see "https://www.cost.eu/"
     */
    COST_ACTION {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * JPI Urban Europe.
     *
     * @see "https://jpi-urbaneurope.eu/"
     */
    JPIEU {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * INTERREG project is funding.
     */
    INTERREG {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Pôles européens d’innovation numérique.
     *
     * @see "https://digital-strategy.ec.europa.eu/fr/activities/edihs"
     */
    EDIH {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },

    /**
     * Programme for the Environment and Climate Action (LIFE)
     *
     * @see "https://cinea.ec.europa.eu/programmes/life_en"
     * @since 3.2
     */
    LIFE {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * H2020 project is funding.
     */
    H2020 {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Horizon Europe funds.
     */
    HORIZON_EUROPE {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * An european company is directly funding.
     */
    EU_COMPANY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return true;
        }

        @Override
        public boolean isInternational() {
            return false;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * An international university is funding.
     */
    INTERNATIONAL_UNIVERSITY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return true;
        }
    },
    /**
     * Another source of funding at international level.
     */
    INTERNTATIONAL_OTHER {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * International mobility programme from CDEFI, i.e. FITEC, e.g. ARFITEC or BRAFITEC.
     *
     * @see "http://www.cdefi.fr/fr/activites/les-programmes-de-mobilite-internationale"
     */
    FITEC {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Campus France is funding.
     *
     * @see "https://www.campusfrance.org/fr"
     */
    CAMPUS_FRANCE {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Nicolas Baudin Programme for mobiltiy and internships.
     *
     * @see "https://au.ambafrance.org/Initiative-stages-en-France"
     */
    NICOLAS_BAUDIN {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Funding is provided by the CONACYT possibly with Campus France.
     *
     * @see "https://conacyt.mx/"
     */
    CONACYT {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Funds are provided by the Chinease Scholarship Council (CSC).
     *
     * @see "https://www.chinesescholarshipcouncil.com/"
     */
    CSC {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * Partenariats Hubert Curien (PHC) possibly through Campus France.
     *
     * @see "https://www.campusfrance.org/fr/phc"
     */
    PHC {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return true;
        }

        @Override
        public boolean isNotAcademic() {
            return false;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    },
    /**
     * An international company is directly funding.
     */
    INTERNATIONAL_COMPANY {
        @Override
        public boolean isRegional() {
            return false;
        }

        @Override
        public boolean isNational() {
            return false;
        }

        @Override
        public boolean isEuropean() {
            return false;
        }

        @Override
        public boolean isInternational() {
            return true;
        }

        @Override
        public boolean isCompetitive() {
            return false;
        }

        @Override
        public boolean isNotAcademic() {
            return true;
        }

        @Override
        public boolean isAcademicButContractual() {
            return false;
        }
    };

    private static final String MESSAGE_PREFIX = "fundingScheme."; //$NON-NLS-1$

    /**
     * Replies the CoNRS section that corresponds to the given name, with a case-insensitive
     * test of the name.
     *
     * @param name the name of the CoNRS section, to search for.
     * @return the CoNRS section.
     * @throws IllegalArgumentException if the given name does not corresponds to a CoNRS section.
     */
    public static FundingScheme valueOfCaseInsensitive(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            for (final var section : values()) {
                if (name.equalsIgnoreCase(section.name())) {
                    return section;
                }
            }
        }
        throw new IllegalArgumentException("Invalid funding scheme: " + name); //$NON-NLS-1$
    }

    /**
     * Replies the label of the funding scheme in the given language.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the funding scheme in the given  language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Indicates if the funding from a regional institution.
     *
     * @return {@code true} if the funding is regional.
     */
    public abstract boolean isRegional();

    /**
     * Indicates if the funding from a national institution.
     *
     * @return {@code true} if the funding is national.
     */
    public abstract boolean isNational();

    /**
     * Indicates if the funding from a european institution.
     *
     * @return {@code true} if the funding is european.
     */
    public abstract boolean isEuropean();

    /**
     * Indicates if the funding from a international institution.
     *
     * @return {@code true} if the funding is international.
     */
    public abstract boolean isInternational();

    /**
     * Indicates if the funding is with competitive project call.
     *
     * @return {@code true} if competitive project call.
     * @since 3.0
     */
    public abstract boolean isCompetitive();

    /**
     * Indicates if the funding is by not academic institution.
     *
     * @return {@code true} if not academic funder.
     * @since 3.0
     */
    public abstract boolean isNotAcademic();

    /**
     * Indicates if the funding is for an academic institution that is also in a contract context.
     *
     * @return {@code true} if academic and contract-based funder.
     * @since 3.0
     */
    public abstract boolean isAcademicButContractual();

    /**
     * Replies the ordinal number of this item in reverse order.
     *
     * @return the ordinal from the end
     * @see #ordinal()
     * @since 3.0
     */
    public int reverseOrdinal() {
        return values().length - ordinal() - 1;
    }

}
