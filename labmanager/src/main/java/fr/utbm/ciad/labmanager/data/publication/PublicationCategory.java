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

package fr.utbm.ciad.labmanager.data.publication;

import com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * A publication category defines a family of research papers in France.
 * The publication categories group several {@link PublicationType publications types}.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see PublicationType
 */
public enum PublicationCategory {
    /**
     * Articles in international or national journals with selection commitee and ranked in international databases.
     */
    ACL {
        @Override
        public boolean isScientificJournalPaper() {
            return true;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Articles in international or national journals with selection committee and not ranked in international databases.
     */
    ACLN {
        @Override
        public boolean isScientificJournalPaper() {
            return true;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Articles in international or national journals without selection committee.
     */
    ASCL {
        @Override
        public boolean isScientificJournalPaper() {
            return true;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Papers in the proceedings of an international conference.
     */
    C_ACTI {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return true;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Papers in the proceedings of a national conference.
     */
    C_ACTN {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return true;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Oral Communications without proceeding in international or national conference.
     */
    C_COM {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return true;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Posters in international or national conference.
     */
    C_AFF {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return true;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Editor of books or journals.
     */
    DO {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return true;
        }
    },
    /**
     * Scientific books.
     */
    OS {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return true;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Chapters in scientific books.
     */
    COS {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return true;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Keynotes in international or national conference.
     */
    C_INV {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return true;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * PhD or Master theses.
     */
    TH {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return true;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Patents.
     */
    BRE {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return true;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Publications for research transfer.
     */
    PT {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return true;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Research tools (not software).
     */
    OR {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return true;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Books for scientific culture dissemination.
     */
    OV {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return true;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Chapters in a book for scientific culture dissemination.
     */
    COV {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return true;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Papers for scientific culture dissemination.
     */
    PV {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return true;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Artistic research productions.
     */
    PAT {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return true;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    },
    /**
     * Other productions (registered software, reports...)
     */
    AP {
        @Override
        public boolean isScientificJournalPaper() {
            return false;
        }

        @Override
        public boolean isPatent() {
            return false;
        }

        @Override
        public boolean isScientificEventPaper() {
            return false;
        }

        @Override
        public boolean isScientificCultureDissemination() {
            return false;
        }

        @Override
        public boolean isOtherScientificDocument() {
            return false;
        }

        @Override
        public boolean isProjectRelatedDocument() {
            return false;
        }

        @Override
        public boolean isDirectedPublication() {
            return false;
        }
    };

    private static final String MESSAGE_PREFIX = "publicationCategory."; //$NON-NLS-1$

    /**
     * Replies the label for the publication category.
     *
     * @param messages the accessor to the localized labels.
     * @param locale   the locale to use.
     * @return the label of the status in the current language.
     */
    public String getLabel(MessageSourceAccessor messages, Locale locale) {
        final var label = messages.getMessage(MESSAGE_PREFIX + name(), locale);
        return Strings.nullToEmpty(label);
    }

    /**
     * Replies the acronym for the publication category.
     *
     * @return the acronym of the status in the current language.
     * @since 3.6
     */
    public String getAcronym() {
        return name().replace('_', '-');
    }

    /**
     * Replies if the category is for the scientific papers in journals.
     *
     * @return {@code true} if the category is for scientific papers.
     */
    public abstract boolean isScientificJournalPaper();

    /**
     * Replies if the category is for the patents.
     *
     * @return {@code true} if the category is for patents.
     */
    public abstract boolean isPatent();

    /**
     * Replies if the category is for the scientific events.
     *
     * @return {@code true} if the category is for scientific events.
     */
    public abstract boolean isScientificEventPaper();

    /**
     * Replies if the category is for the scientific documents that are not journal or conference paper.
     * These documents may be PhD report, books, etc.
     *
     * @return {@code true} if the category is for scientific documents that are not in another category.
     * @since 4.0
     */
    public abstract boolean isOtherScientificDocument();

    /**
     * Replies if the category is for the documents that are related to projects.
     *
     * @return {@code true} if the category is for projects' documents.
     * @since 4.0
     */
    public abstract boolean isProjectRelatedDocument();

    /**
     * Replies if the category is for the direction of publication, such as journal or book edition.
     *
     * @return {@code true} if the category is for publication direction.
     * @since 4.0
     */
    public abstract boolean isDirectedPublication();

    /**
     * Replies if the category is for the dissemination of scientific culture.
     *
     * @return {@code true} if the category is for scientific culture dissemination.
     */
    public abstract boolean isScientificCultureDissemination();

}
