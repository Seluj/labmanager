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
import fr.utbm.ciad.labmanager.utils.doi.DoiTools;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Factory of a publication that is temporary and defined for pre-initialization of a real publication.
 * This class is not supposed to be used intensively or stored into the database.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class DefaultPrePublicationFactory implements PrePublicationFactory {

    private final DoiTools doiTools;

    /**
     * Constructor.
     *
     * @param doiTools the tools for manipulating the DOI.
     */
    public DefaultPrePublicationFactory(DoiTools doiTools) {
        this.doiTools = doiTools;
    }

    @Override
    public Publication createPrePublication(PublicationType type, String title, String abstractText, String keywords,
                                            LocalDate date, int year, String isbn, String issn,
                                            String doi, String halId, String extraUrl, String videoUrl, String dblpUrl, String pdfPath,
                                            String awardPath, PublicationLanguage language) {
        return new PrePublication(type, title, abstractText, keywords, date, year, isbn, issn,
                this.doiTools.getDOINumberFromDOIUrlOrNull(doi),
                halId, extraUrl, videoUrl, dblpUrl, pdfPath, awardPath,
                language);
    }

}
