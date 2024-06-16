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

package fr.utbm.ciad.labmanager.services.publication;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;

import java.time.LocalDate;

/** Implementation of a publication that is temporary and defined for pre-initialization of a real publication.
 * This class is not supposed to be used intensively or stored into the database.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
final class PrePublication extends Publication {

	private static final long serialVersionUID = 3190476422430385432L;

	/** Create a publication with the given field values.
	 *
	 * @param type the type of the publication. It cannot be {@code null}.
	 * @param title the title of the publication.
	 * @param abstractText the text of the abstract for the publication.
	 * @param keywords the keywords, seperated by coma or column characters 
	 * @param date the date of publication. If it is {@code null}, then the {@code year} should be considered only.
	 * @param year the year of publication.
	 * @param isbn the ISBN number if any.
	 * @param issn the ISSN number if any.
	 * @param doi the DOI reference number if any.
	 * @param halId the HAL reference number if any.
	 * @param extraUrl an URL to a page associated to the publication.
	 * @param videoUrl an URL to a video associated to the publication.
	 * @param dblpUrl the URL to the DBLP page of the publication if any.
	 * @param pdfPath the path (may be an URL, but preferably a simple path) to a downloadable PDF file for the publication.
	 * @param awardPath the path (may be an URL, but preferably a simple path) to a downloadable PDF file that is a award certificate associated to the publication.
	 * @param language the major language used for writing the publication. It cannot be {@code null}.
	 */
	PrePublication(PublicationType type, String title, String abstractText, String keywords,
			LocalDate date, int year, String isbn, String issn, String doi, String halId,
			String extraUrl, String videoUrl, String dblpUrl, String pdfPath,
			String awardPath, PublicationLanguage language) {
		super(type, title, abstractText, keywords, date, year, isbn, issn, doi, halId, extraUrl,
				videoUrl, dblpUrl, pdfPath, awardPath, language);
	}

	@Override
	public boolean isRanked() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getWherePublishedShortDescription() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPublicationTarget() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFakeEntity() {
		return true;
	}

}
