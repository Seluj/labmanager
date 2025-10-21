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

package fr.utbm.ciad.labmanager.services.publication.type;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaperRepository;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.publication.AbstractPublicationTypeService;
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.hal.HalTools;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing journal paper.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class JournalPaperService extends AbstractPublicationTypeService {

    private static final long serialVersionUID = 7713709761848603121L;

    private final JournalPaperRepository repository;

    private final MembershipService membershipService;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param downloadableFileManager downloadable file manager.
     * @param doiTools                the tools for manipulating the DOI.
     * @param halTools                the tools for manipulating the HAL ids.
     * @param repository              the repository for this service.
     * @param membershipService       the service for accessing the memberships.
     * @param messages                the provider of localized messages.
     * @param constants               the accessor to the live constants.
     * @param sessionFactory          the Hibernate session factory.
     */
    public JournalPaperService(
            @Autowired DownloadableFileManager downloadableFileManager,
            @Autowired DoiTools doiTools,
            @Autowired HalTools halTools,
            @Autowired JournalPaperRepository repository,
            @Autowired MembershipService membershipService,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(downloadableFileManager, doiTools, halTools, messages, constants, sessionFactory);
        this.repository = repository;
        this.membershipService = membershipService;
    }

    /**
     * Replies all the journal papers.
     *
     * @return the journal papers.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public List<JournalPaper> getAllJournalPapers() {
        return this.repository.findAll();
    }

    /**
     * Replies the journal paper with the given identifier.
     *
     * @param identifier the identifier of the journal paper.
     * @return the journal paper or {@code null}.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public JournalPaper getJournalPaper(long identifier) {
        return this.repository.findById(Long.valueOf(identifier)).orElse(null);
    }

    /**
     * Replies the journal papers that are associated to the journal with the given identifier.
     *
     * @param journalId the identifier of the journal.
     * @return the journal papers.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public List<JournalPaper> getJournalPapersByJournalId(long journalId) {
        return this.repository.findAllByJournalId(journalId);
    }

    /**
     * Replies all the journal paper from the database that are attached to a person involved in the given organization.
     *
     * @param identifier                             the identifier of the organization.
     * @param includeSubOrganizations                indicates if the members of the suborganizations are considered.
     * @param filterAuthorshipsWithActiveMemberships indicates if the authorships must correspond to active memberships.
     * @return the publications.
     */
    public Set<JournalPaper> getJournalPapersByOrganizationId(long identifier, boolean includeSubOrganizations,
                                                              boolean filterAuthorshipsWithActiveMemberships) {
        final Set<Person> members;
        if (includeSubOrganizations) {
            members = this.membershipService.getMembersOf(identifier);
        } else {
            members = this.membershipService.getDirectMembersOf(identifier);
        }
        final var identifiers = members.stream().map(it -> Long.valueOf(it.getId())).collect(Collectors.toUnmodifiableSet());
        final var publications = this.repository.findAllByAuthorshipsPersonIdIn(identifiers);
        if (filterAuthorshipsWithActiveMemberships) {
            return filterPublicationsWithMemberships(publications, identifier, includeSubOrganizations);
        }
        return publications;
    }

    /**
     * Create a journal paper.
     *
     * @param publication the publication to copy.
     * @param volume      the volume of the journal.
     * @param number      the number of the journal.
     * @param pages       the pages in the journal.
     * @param series      the series of the journal.
     * @param journal     the associated journal.
     * @return the created journal paper.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public JournalPaper createJournalPaper(Publication publication, String volume, String number, String pages, String series, Journal journal) {
        return createJournalPaper(publication, volume, number, pages, series, journal, true);
    }

    /**
     * Create a journal paper.
     *
     * @param publication the publication to copy.
     * @param volume      the volume of the journal.
     * @param number      the number of the journal.
     * @param pages       the pages in the journal.
     * @param series      the series of the journal.
     * @param journal     the associated journal.
     * @param saveInDb    {@code true} for saving the publication in the database.
     * @return the created journal paper.
     */
    public JournalPaper createJournalPaper(Publication publication, String volume, String number, String pages,
                                           String series, Journal journal, boolean saveInDb) {
        final var res = new JournalPaper(publication, volume, number, pages, series);
        res.setJournal(journal);
        if (saveInDb) {
            this.repository.save(res);
        }
        return res;
    }

    /**
     * Update the journal paper with the given identifier.
     *
     * @param pubId        identifier of the paper to change.
     * @param title        the new title of the publication, never {@code null} or empty.
     * @param type         the new type of publication, never {@code null}.
     * @param date         the new date of publication. It may be {@code null}. In this case only the year should be considered.
     * @param year         the new year of the publication.
     * @param abstractText the new text of the abstract.
     * @param keywords     the new list of keywords.
     * @param doi          the new DOI number.
     * @param halId        the new HAL id.
     * @param dblpUrl      the new URL to the DBLP page of the publication.
     * @param extraUrl     the new URL to the page of the publication.
     * @param language     the new major language of the publication.
     * @param pdfContent   the content of the publication PDF that is encoded in {@link Base64}. The content will be saved into
     *                     the dedicated folder for PDF files.
     * @param awardContent the content of the publication award certificate that is encoded in {@link Base64}. The content will be saved into
     *                     the dedicated folder for PDF files.
     * @param pathToVideo  the path that allows to download the video of the publication.
     * @param volume       the volume of the journal.
     * @param number       the number of the journal.
     * @param pages        the pages in the journal.
     * @param series       the series of the journal.
     * @param journal      the journal.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public void updateJournalPaper(long pubId,
                                   String title, PublicationType type, LocalDate date, int year, String abstractText, String keywords,
                                   String doi, String halId, String dblpUrl, String extraUrl,
                                   PublicationLanguage language, String pdfContent, String awardContent, String pathToVideo,
                                   String volume, String number, String pages, String series, Journal journal) {
        final var res = this.repository.findById(Long.valueOf(pubId));
        if (res.isPresent()) {
            final var paper = res.get();

            updatePublicationNoSave(paper, title, type, date, year,
                    abstractText, keywords, doi, halId, null, null, dblpUrl,
                    extraUrl, language, pdfContent, awardContent,
                    pathToVideo);

            paper.setVolume(Strings.emptyToNull(volume));
            paper.setNumber(Strings.emptyToNull(number));
            paper.setPages(Strings.emptyToNull(pages));
            paper.setSeries(Strings.emptyToNull(series));

            paper.setJournal(journal);

            this.repository.save(res.get());
        }
    }

    /**
     * Remove the journal paper from the database.
     *
     * @param identifier the identifier of the journal paper to be removed.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public void removeJournalPaper(long identifier) {
        this.repository.deleteById(Long.valueOf(identifier));
    }

}
