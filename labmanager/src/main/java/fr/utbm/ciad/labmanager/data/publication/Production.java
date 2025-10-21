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

import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.member.Person;

import java.io.Serializable;
import java.util.List;

/**
 * Represents all the productions (publications, etc.) of the organization.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.6
 */
public interface Production extends Serializable, IdentifiableEntity {

    /**
     * Replies the category of publication.
     *
     * @return the category, never {@code null}.
     */
    PublicationCategory getCategory();

    /**
     * Replies the ordered list of authors.
     * The authors are replied in the order provided in the paper.
     *
     * @return the list of authors.
     */
    List<Person> getAuthors();

    /**
     * Replies the set of authorships. The authorships are replied in the order provided in the paper.
     *
     * @return the authorships.
     */
    List<Authorship> getAuthorships();

    /**
     * Replies the DOI reference number that is associated to this publication.
     * Usually, the DOI number should not be prefixed by the {@code http://doi.org} prefix.
     *
     * @return the DOI reference or {@code null}.
     * @see "https://en.wikipedia.org/wiki/Digital_object_identifier"
     */
    String getDOI();

    /**
     * Replies the year of publication.
     * If the publication date is specified, the year is extracted from this date.
     * If the publication date is not specified, the current year is replied.
     *
     * @return the year.
     */
    int getPublicationYear();

    /**
     * Replies the title of the publication.
     *
     * @return the title.
     */
    String getTitle();

}
