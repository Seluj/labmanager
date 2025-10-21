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

package fr.utbm.ciad.labmanager.utils.io.ris;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.PublicationExporter;
import fr.utbm.ciad.labmanager.utils.io.bibtex.ConferenceFake;
import fr.utbm.ciad.labmanager.utils.io.bibtex.JournalFake;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for RIS.
 * RIS is a standardized tag format developed by Research Information Systems, Incorporated to enable citation programs
 * to exchange data.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "https://en.wikipedia.org/wiki/RIS_(file_format)"
 * @since 3.7
 */
public interface RIS extends PublicationExporter<String> {

    /**
     * Extract the publications from a RIS source.
     * This function does not save the publication in the database, as well as the authors.
     *
     * @param ris                    the RIS data
     * @param keepRisId              indicates if the RIS reference id should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the reference ids are provided to the publication.
     *                               If this argument is {@code false}, the reference ids are ignored.
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
     * @param locale                 the locale to use.
     * @param progression            the progression indicator.
     * @return the list of publications that are detected in the RIS data.
     * @throws Exception if the RIS source cannot be processed.
     * @see #extractPublications(Reader, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @since 4.0
     */
    default List<Publication> extractPublications(String ris, boolean keepRisId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                  boolean createMissedJournal, boolean createMissedConference, Locale locale, Progression progression) throws Exception {
        try {
            return getPublicationStreamFrom(ris, keepRisId, assignRandomId, ensureAtLeastOneMember,
                    createMissedJournal, createMissedConference, locale, progression).collect(Collectors.toList());
        } finally {
            if (progression != null) {
                progression.end();
            }
        }
    }

    /**
     * Extract the publications from a RIS source.
     * This function does not save the publication in the database.
     *
     * @param ris                    the RIS data
     * @param keepRisId              indicates if the RIS reference id should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the reference ids are provided to the publication.
     *                               If this argument is {@code false}, the reference ids are ignored.
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
     * @param locale                 the locale to use.
     * @param progression            the progression indicator.
     * @return the list of publications that are detected in the RIS data.
     * @throws Exception if the RIS source cannot be processed.
     * @see #extractPublications(String, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @since 4.0
     */
    default List<Publication> extractPublications(Reader ris, boolean keepRisId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                  boolean createMissedJournal, boolean createMissedConference, Locale locale, Progression progression) throws Exception {
        try {
            return getPublicationStreamFrom(ris, keepRisId, assignRandomId, ensureAtLeastOneMember,
                    createMissedJournal, createMissedConference, locale, progression).collect(Collectors.toList());
        } finally {
            if (progression != null) {
                progression.end();
            }
        }
    }

    /**
     * Extract the publications from a RIS source.
     * This function does not save the publication in the database.
     *
     * @param ris                    the RIS data
     * @param keepRisId              indicates if the RIS reference id should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the reference ids are provided to the publication.
     *                               If this argument is {@code false}, the reference ids are ignored.
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
     * @param locale                 the locale to use for importing.
     * @param progression            the progression indicator.
     * @return the stream of publications that are detected in the RIS data.
     * @throws Exception if the RIS source cannot be processed.
     * @see #getPublicationStreamFrom(String, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @see #extractPublications(String, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @since 4.0
     */
    default Stream<Publication> getPublicationStreamFrom(String ris, boolean keepRisId, boolean assignRandomId,
                                                         boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference,
                                                         Locale locale, Progression progression) throws Exception {
        final var progress = progression == null ? new DefaultProgression() : progression;
        if (!Strings.isNullOrEmpty(ris)) {
            try (final var reader = new StringReader(ris)) {
                return getPublicationStreamFrom(reader, keepRisId, assignRandomId, ensureAtLeastOneMember, createMissedJournal,
                        createMissedConference, locale, progress);
            }
        }
        return Collections.<Publication>emptySet().stream();
    }

    /**
     * Extract the publications from a RIS source.
     * This function does not save the publication in the database.
     *
     * @param ris                    the RIS data
     * @param keepRisId              indicates if the RIS reference id should be used as the
     *                               {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
     *                               If this argument is {@code true}, the reference ids are provided to the publication.
     *                               If this argument is {@code false}, the reference ids are ignored.
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
     * @param locale                 the locale to use for importing.
     * @param progression            the progression indicator.
     * @return the stream of publications that are detected in the RIS data.
     * @throws Exception if the RIS source cannot be processed.
     * @see #getPublicationStreamFrom(String, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @see #extractPublications(Reader, boolean, boolean, boolean, boolean, boolean, Locale, Progression)
     * @since 4.0
     */
    Stream<Publication> getPublicationStreamFrom(Reader ris, boolean keepRisId, boolean assignRandomId, boolean ensureAtLeastOneMember,
                                                 boolean createMissedJournal, boolean createMissedConference, Locale locale, Progression progression) throws Exception;

    @Override
    default String exportPublications(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression, Logger logger) {
        try (final var writer = new StringWriter()) {
            exportPublications(writer, publications, configurator, progression);
            return Strings.emptyToNull(writer.toString());
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Export the given the publications to a RIS source.
     *
     * @param output       the writer of the RIS for saving its content.
     * @param publications the publications to export.
     * @param configurator the configuration of the exporter.
     * @param progression  the progression indicator.
     * @throws IOException if any problem occurred when writing the RIS content.
     */
    void exportPublications(Writer output, Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) throws IOException;

}
