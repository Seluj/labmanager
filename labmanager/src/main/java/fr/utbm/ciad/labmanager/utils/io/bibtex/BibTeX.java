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

package fr.utbm.ciad.labmanager.utils.io.bibtex;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.PublicationExporter;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for BibTeX. BibTeX is reference management software for formatting lists of bibliography references.
 * The BibTeX tool is typically used together with the LaTeX document preparation system.
 * The purpose of BibTeX is to make it easy to cite sources in a consistent manner, by separating bibliographic
 * information from the presentation of this information, similarly to the separation of content and
 * presentation/style supported by LaTeX itself.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/BibTeX"
 * @since 2.0.0
 */
public interface BibTeX extends PublicationExporter<String> {

    /**
     * Convert any special macro from a TeX string into its equivalent in the current character encoding.
     * For example, the macros {@code \'e} is translated to {@code é}.
     * <p>
     * The conversion in the opposite direction is supported by {@link #toTeXString(String)}.
     *
     * @param texString the TeX data.
     * @return the Java string that corresponds to the given TeX data.
     * @throws Exception if the TeX string cannot be parsed.
     * @see #toTeXString(String)
     */
    String parseTeXString(String texString) throws Exception;

    /**
     * Convert the given Java string to its equivalent TeX string.
     * For example, the macros {@code é} is translated to {@code \'e}.
     * <p>
     * The conversion in the opposite direction is supported by {@link #parserTeXString(String)}.
     *
     * @param text the Java data.
     * @return the TeX string that corresponds to the given Java data.
     * @see #parseTeXString(String)
     * @see #toTeXString(String, boolean)
     */
    default String toTeXString(String text) {
        return toTeXString(text, false);
    }

    /**
     * Convert the given Java string to its equivalent TeX string.
     * For example, the macros {@code é} is translated to {@code \'e}.
     * <p>
     * The conversion in the opposite direction is supported by {@link #parserTeXString(String)}.
     *
     * @param text            the Java data.
     * @param protectAcronyms indicates if the acronyms are protected. Add curly-braces around the upper-case words of the given text.
     *                        This feature is usually applied in the titles of the BibTeX entries in
     *                        order to avoid BibTeX tools to change the case of the words in the titles
     *                        when it is rendered on a final document.
     * @return the TeX string that corresponds to the given Java data.
     * @see #parseTeXString(String)
     * @see #toTeXString(String)
     * @since 3.6
     */
    String toTeXString(String text, boolean protectAcronyms);

    /**
     * Extract the publications from a BibTeX source.
     * This function does not save the publication in the database, as well as the authors.
     *
     * @param bibtex                 the BibTeX data
     * @param keepBibTeXId           indicates if the BibTeX keys should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the BibTeX keys are provided to the publication.
     *                               If this argument is {@code false}, the BibTeX keys are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    if {@code true} the missed journals from the JPA database will be automatically the subject
     *                               of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thown when
     *                               a journal is missed from the JPA database.
     * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
     *                               of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
     *                               a conference is missed from the JPA database.
     * @param progression            progression indicator to be used.
     * @return the list of publications that are detected in the BibTeX data.
     * @throws Exception if the BibTeX source cannot be processed.
     * @see #extractPublications(Reader, boolean, boolean, boolean, boolean, boolean, Progression)
     * @since 4.0
     */
    default List<Publication> extractPublications(String bibtex, boolean keepBibTeXId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                  boolean createMissedJournal, boolean createMissedConference, Progression progression) throws Exception {
        try {
            return getPublicationStreamFrom(bibtex, keepBibTeXId, assignRandomId, ensureAtLeastOneMember,
                    createMissedJournal, createMissedConference, progression).collect(Collectors.toList());
        } finally {
            if (progression != null) {
                progression.end();
            }
        }
    }

    /**
     * Extract the publications from a BibTeX source.
     * This function does not save the publication in the database.
     *
     * @param bibtex                 the BibTeX data
     * @param keepBibTeXId           indicates if the BibTeX keys should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the BibTeX keys are provided to the publication.
     *                               If this argument is {@code false}, the BibTeX keys are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    if {@code true} the missed journals from the JPA database will be automatically the subject
     *                               of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thown when
     *                               a journal is missed from the JPA database.
     * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
     *                               of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
     *                               a conference is missed from the JPA database.
     * @param progression            progression indicator to be used.
     * @return the list of publications that are detected in the BibTeX data.
     * @throws Exception if the BibTeX source cannot be processed.
     * @see #extractPublications(String, boolean, boolean, boolean, boolean, boolean, Progression)
     * @since 4.0
     */
    default List<Publication> extractPublications(Reader bibtex, boolean keepBibTeXId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                  boolean createMissedJournal, boolean createMissedConference, Progression progression) throws Exception {
        try {
            return getPublicationStreamFrom(bibtex, keepBibTeXId, assignRandomId, ensureAtLeastOneMember,
                    createMissedJournal, createMissedConference, progression).collect(Collectors.toList());
        } finally {
            if (progression != null) {
                progression.end();
            }
        }
    }

    /**
     * Extract the publications from a BibTeX source.
     * This function does not save the publication in the database.
     *
     * @param bibtex                 the BibTeX data
     * @param keepBibTeXId           indicates if the BibTeX keys should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the BibTeX keys are provided to the publication.
     *                               If this argument is {@code false}, the BibTeX keys are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    if {@code true} the missed journals from the JPA database will be automatically the subject
     *                               of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thown when
     *                               a journal is missed from the JPA database.
     * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
     *                               of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
     *                               a conference is missed from the JPA database.
     * @param progression            progression indicator to be used.
     * @return the stream of publications that are detected in the BibTeX data.
     * @throws Exception if the BibTeX source cannot be processed.
     * @see #getPublicationStreamFrom(Reader, boolean, boolean, boolean, boolean, boolean, Progression)
     * @see #extractPublications(String, boolean, boolean, boolean, boolean, boolean, Progression)
     * @since 4.0
     */
    default Stream<Publication> getPublicationStreamFrom(String bibtex, boolean keepBibTeXId, boolean assignRandomId,
                                                         boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference, Progression progression) throws Exception {
        if (!Strings.isNullOrEmpty(bibtex)) {
            try (final var reader = new StringReader(bibtex)) {
                return getPublicationStreamFrom(reader, keepBibTeXId, assignRandomId, ensureAtLeastOneMember, createMissedJournal,
                        createMissedConference, progression);
            }
        }
        return Collections.<Publication>emptySet().stream();
    }

    /**
     * Extract the publications from a BibTeX source.
     * This function does not save the publication in the database.
     *
     * @param bibtex                 the BibTeX data
     * @param keepBibTeXId           indicates if the BibTeX keys should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the BibTeX keys are provided to the publication.
     *                               If this argument is {@code false}, the BibTeX keys are ignored.
     * @param assignRandomId         indicates if a random identifier will be assigned to the created entities.
     *                               If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
     *                               If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
     * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
     *                               the list of the persons. If {@code false}, the list of persons could contain no organization member.
     * @param createMissedJournal    if {@code true} the missed journals from the JPA database will be automatically the subject
     *                               of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thrown when
     *                               a journal is missed from the JPA database.
     * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
     *                               of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
     *                               a conference is missed from the JPA database.
     * @param progression            progression indicator to be used.
     * @return the stream of publications that are detected in the BibTeX data.
     * @throws Exception if the BibTeX source cannot be processed.
     * @see #getPublicationStreamFrom(String, boolean, boolean, boolean, boolean, boolean, Progression)
     * @see #extractPublications(Reader, boolean, boolean, boolean, boolean, boolean, Progression)
     * @since 4.0
     */
    Stream<Publication> getPublicationStreamFrom(Reader bibtex, boolean keepBibTeXId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                 boolean createMissedJournal, boolean createMissedConference, Progression progression) throws Exception;

    @Override
    default String exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression, Logger logger) {
        try (final var writer = new StringWriter()) {
            exportPublications(writer, publications, configurator, progression, logger);
            return Strings.emptyToNull(writer.toString());
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Export the given the publications to a BibTeX source.
     *
     * @param output       the writer of the BibTeX for saving its content.
     * @param publications the publications to export.
     * @param configurator the configuration of the exporter.
     * @param progression  the progression indicator.
     * @param logger       the logger to be used.
     * @throws IOException if any problem occurred when writing the BibTeX content.
     */
    void exportPublications(Writer output, Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression, Logger logger) throws IOException;

}
