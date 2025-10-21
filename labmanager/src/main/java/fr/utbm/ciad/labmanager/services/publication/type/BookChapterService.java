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
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.BookChapter;
import fr.utbm.ciad.labmanager.data.publication.type.BookChapterRepository;
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

/**
 * Service for managing book chapters.
 *
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class BookChapterService extends AbstractPublicationTypeService {

    private static final long serialVersionUID = -6922690712393829407L;

    private final BookChapterRepository repository;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param downloadableFileManager downloadable file manager.
     * @param doiTools                the tools for manipulating the DOI.
     * @param halTools                the tools for manipulating the HAL ids.
     * @param repository              the repository for this service.
     * @param messages                the provider of localized messages.
     * @param constants               the accessor to the live constants.
     * @param sessionFactory          the Hibernate session factory.
     */
    public BookChapterService(
            @Autowired DownloadableFileManager downloadableFileManager,
            @Autowired DoiTools doiTools,
            @Autowired HalTools halTools,
            @Autowired BookChapterRepository repository,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(downloadableFileManager, doiTools, halTools, messages, constants, sessionFactory);
        this.repository = repository;
    }

    /**
     * Replies all the book chapters.
     *
     * @return the book chapters.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public List<BookChapter> getAllBookChapters() {
        return this.repository.findAll();
    }

    /**
     * Replies the book chapter with the given identifier.
     *
     * @param identifier the identifier of the book chapter.
     * @return the book chapter, or {@code null}.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public BookChapter getBookChapter(long identifier) {
        return this.repository.findById(Long.valueOf(identifier)).orElse(null);
    }

    /**
     * Create a book chapter.
     *
     * @param publication   the publication to copy.
     * @param bookTitle     the title of the book in which the chapter is.
     * @param chapterNumber the number of the chapter in the book.
     * @param edition       the edition number of the book.
     * @param volume        the volume of the journal.
     * @param number        the number of the journal.
     * @param pages         the pages in the journal.
     * @param editors       the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
     *                      with {@code AND}.
     * @param series        the number or the name of the series for the conference proceedings.
     * @param publisher     the name of the publisher of the book.
     * @param address       the geographical location of the event, usually a city and a country.
     * @return the created book chapter.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public BookChapter createBookChapter(Publication publication, String bookTitle, String chapterNumber, String edition,
                                         String volume, String number, String pages, String editors, String series,
                                         String publisher, String address) {
        return createBookChapter(publication, bookTitle, chapterNumber, edition, volume, number, pages,
                editors, series, publisher, address, true);
    }

    /**
     * Create a book chapter.
     *
     * @param publication   the publication to copy.
     * @param bookTitle     the title of the book in which the chapter is.
     * @param chapterNumber the number of the chapter in the book.
     * @param edition       the edition number of the book.
     * @param volume        the volume of the journal.
     * @param number        the number of the journal.
     * @param pages         the pages in the journal.
     * @param editors       the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
     *                      with {@code AND}.
     * @param series        the number or the name of the series for the conference proceedings.
     * @param publisher     the name of the publisher of the book.
     * @param address       the geographical location of the event, usually a city and a country.
     * @param saveInDb      {@code true} for saving the publication in the database.
     * @return the created book chapter.
     */
    public BookChapter createBookChapter(Publication publication, String bookTitle, String chapterNumber, String edition,
                                         String volume, String number, String pages, String editors, String series,
                                         String publisher, String address, boolean saveInDb) {
        final var res = new BookChapter(publication, volume, number, pages, editors,
                address, series, publisher, edition, bookTitle, chapterNumber);
        if (saveInDb) {
            this.repository.save(res);
        }
        return res;
    }

    /**
     * Update the book chapter with the given identifier.
     * <p>This function does not check the associated between the given publication type and the book class.
     *
     * @param pubId         identifier of the chapter to change.
     * @param title         the new title of the publication, never {@code null} or empty.
     * @param type          the new type of publication, never {@code null}.
     * @param date          the new date of publication. It may be {@code null}. In this case only the year should be considered.
     * @param year          the new year of the publication.
     * @param abstractText  the new text of the abstract.
     * @param keywords      the new list of keywords.
     * @param doi           the new DOI number.
     * @param halId         the new HAL id.
     * @param isbn          the new ISBN number.
     * @param issn          the new ISSN number.
     * @param dblpUrl       the new URL to the DBLP page of the publication.
     * @param extraUrl      the new URL to the page of the publication.
     * @param language      the new major language of the publication.
     * @param pdfContent    the content of the publication PDF that is encoded in {@link Base64}. The content will be saved into
     *                      the dedicated folder for PDF files.
     * @param awardContent  the content of the publication award certificate that is encoded in {@link Base64}. The content will be saved into
     *                      the dedicated folder for PDF files.
     * @param pathToVideo   the path that allows to download the video of the publication.
     * @param bookTitle     the title of the book in which the chapter is.
     * @param chapterNumber the number of the chapter in the book.
     * @param edition       the edition number of the book.
     * @param volume        the volume of the journal.
     * @param number        the number of the journal.
     * @param pages         the pages in the journal.
     * @param editors       the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
     *                      with {@code AND}.
     * @param series        the number or the name of the series for the conference proceedings.
     * @param publisher     the name of the publisher of the book.
     * @param address       the geographical location of the event, usually a city and a country.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public void updateBookChapter(long pubId,
                                  String title, PublicationType type, LocalDate date, int year, String abstractText, String keywords,
                                  String doi, String halId, String isbn, String issn, String dblpUrl, String extraUrl,
                                  PublicationLanguage language, String pdfContent, String awardContent, String pathToVideo,
                                  String bookTitle, String chapterNumber, String edition,
                                  String volume, String number, String pages, String editors, String series,
                                  String publisher, String address) {
        final var res = this.repository.findById(Long.valueOf(pubId));
        if (res.isPresent()) {
            final var chapter = res.get();

            updatePublicationNoSave(chapter, title, type, date, year,
                    abstractText, keywords, doi, halId, isbn, issn, dblpUrl,
                    extraUrl, language, pdfContent, awardContent,
                    pathToVideo);

            chapter.setBookTitle(Strings.emptyToNull(bookTitle));
            chapter.setChapterNumber(Strings.emptyToNull(chapterNumber));
            chapter.setEdition(Strings.emptyToNull(edition));
            chapter.setVolume(Strings.emptyToNull(volume));
            chapter.setNumber(Strings.emptyToNull(number));
            chapter.setPages(Strings.emptyToNull(pages));
            chapter.setEditors(Strings.emptyToNull(editors));
            chapter.setSeries(Strings.emptyToNull(series));
            chapter.setPublisher(Strings.emptyToNull(publisher));
            chapter.setAddress(Strings.emptyToNull(address));

            this.repository.save(res.get());
        }
    }

    /**
     * Remove the book chapter with the given identifier.
     *
     * @param identifier the identifier if the chapter to remove.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public void removeBookChapter(long identifier) {
        this.repository.deleteById(Long.valueOf(identifier));
    }

}
