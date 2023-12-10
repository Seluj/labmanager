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

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.KeyNote;
import fr.utbm.ciad.labmanager.data.publication.type.KeyNoteRepository;
import fr.utbm.ciad.labmanager.services.publication.AbstractPublicationTypeService;
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.hal.HalTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/** Service for managing keynotes.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class KeyNoteService extends AbstractPublicationTypeService {

	private KeyNoteRepository repository;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param downloadableFileManager downloadable file manager.
	 * @param doiTools the tools for manipulating the DOI.
	 * @param halTools the tools for manipulating the HAL ids.
	 * @param repository the repository for this service.
	 */
	public KeyNoteService(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired DownloadableFileManager downloadableFileManager,
			@Autowired DoiTools doiTools,
			@Autowired HalTools halTools,
			@Autowired KeyNoteRepository repository) {
		super(messages, constants, downloadableFileManager, doiTools, halTools);
		this.repository = repository;
	}

	/** Replies all the keynotes.
	 *
	 * @return the keynotes.
	 */
	public List<KeyNote> getAllKeyNotes() {
		return this.repository.findAll();
	}

	/** Replies all the keynotes.
	 *
	 * @param pageable the manager of pages.
	 * @return the keynotes.
	 * @since 4.0
	 */
	public Page<KeyNote> getAllKeyNotes(Pageable pageable) {
		return this.repository.findAll(pageable);
	}

	/** Replies all the keynotes.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the fitler of keynotes.
	 * @return the keynotes.
	 * @since 4.0
	 */
	public Page<KeyNote> getAllKeyNotes(Pageable pageable, Specification<KeyNote> filter) {
		return this.repository.findAll(filter, pageable);
	}

	/** Replies the keynotes with the given identifier.
	 *
	 * @param identifier the identifier of the keynote.
	 * @return the keynote or {@code null}.
	 */
	public KeyNote getKeyNote(int identifier) {
		final Optional<KeyNote> byId = this.repository.findById(Integer.valueOf(identifier));
		return byId.orElse(null);
	}

	/** Create a keynote.
	 *
	 * @param publication the publication to copy.
	 * @param conference the reference to the conference
	 * @param conferenceOccurrenceNumber the number of the conference's occurrence.
	 * @param editors the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
	 *     with {@code AND}.
	 * @param orga the name of the organization institution.
	 * @param address the geographical location of the event, usually a city and a country.
	 * @param saveInDb {@code true} for saving the publication in the database.
	 * @return the created keynote.
	 * @since 3.8
	 */
	public KeyNote createKeyNote(Publication publication, Conference conference, int conferenceOccurrenceNumber, String editors, String orga, String address, boolean saveInDb) {
		final KeyNote res = new KeyNote(publication, conference, conferenceOccurrenceNumber, editors, orga, address);
		if (saveInDb) {
			this.repository.save(res);
		}
		return res;
	}

	/** Create a keynote.
	 *
	 * @param publication the publication to copy.
	 * @param conference the reference to the conference
	 * @param conferenceOccurrenceNumber the number of the conference's occurrence.
	 * @param editors the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
	 *     with {@code AND}.
	 * @param orga the name of the organization institution.
	 * @param address the geographical location of the event, usually a city and a country.
	 * @return the created keynote.
	 */
	public KeyNote createKeyNote(Publication publication, Conference conference, int conferenceOccurrenceNumber, String editors, String orga, String address) {
		return createKeyNote(publication, conference, conferenceOccurrenceNumber, editors, orga, address, true);
	}

	/** Update the keynote with the given identifier.
	 *
	 * @param pubId identifier of the paper to change.
	 * @param title the new title of the publication, never {@code null} or empty.
	 * @param type the new type of publication, never {@code null}.
	 * @param date the new date of publication. It may be {@code null}. In this case only the year should be considered.
	 * @param year the new year of the publication. 
	 * @param abstractText the new text of the abstract.
	 * @param keywords the new list of keywords.
	 * @param doi the new DOI number.
	 * @param halId the new HAL id.
	 * @param isbn the new ISBN number.
	 * @param issn the new ISSN number.
	 * @param dblpUrl the new URL to the DBLP page of the publication.
	 * @param extraUrl the new URL to the page of the publication.
	 * @param language the new major language of the publication.
	 * @param pdfContent the content of the publication PDF that is encoded in {@link Base64}. The content will be saved into
	 *     the dedicated folder for PDF files.
	 * @param awardContent the content of the publication award certificate that is encoded in {@link Base64}. The content will be saved into
	 *     the dedicated folder for PDF files.
	 * @param pathToVideo the path that allows to download the video of the publication.
	 * @param conference the reference to the conference
	 * @param conferenceOccurrenceNumber the number of the conference's occurrence.
	 * @param editors the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
	 *     with {@code AND}.
	 * @param orga the name of the organization institution.
	 * @param address the geographical location of the event, usually a city and a country.
	 */
	public void updateKeyNote(int pubId,
			String title, PublicationType type, LocalDate date, int year, String abstractText, String keywords,
			String doi, String halId, String isbn, String issn, String dblpUrl, String extraUrl,
			PublicationLanguage language, String pdfContent, String awardContent, String pathToVideo,
			Conference conference, int conferenceOccurrenceNumber, String editors, String orga, String address) {
		final Optional<KeyNote> res = this.repository.findById(Integer.valueOf(pubId));
		if (res.isPresent()) {
			final KeyNote paper = res.get();

			updatePublicationNoSave(paper, title, type, date, year,
					abstractText, keywords, doi, halId, isbn, issn, dblpUrl,
					extraUrl, language, pdfContent, awardContent,
					pathToVideo);

			paper.setConference(conference);
			paper.setConferenceOccurrenceNumber(conferenceOccurrenceNumber);
			paper.setEditors(Strings.emptyToNull(editors));
			paper.setOrganization(Strings.emptyToNull(orga));
			paper.setAddress(Strings.emptyToNull(address));

			this.repository.save(res.get());
		}
	}

	/** Remove the keynote from the database.
	 *
	 * @param identifier the identifier of the keynote to be removed.
	 */
	public void removeKeyNote(int identifier) {
		this.repository.deleteById(Integer.valueOf(identifier));
	}

}
