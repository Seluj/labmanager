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

import fr.utbm.ciad.labmanager.data.conference.Conference;

/**
 * Fake of a conference.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.4
 */
public class ConferenceFake extends Conference {

    private static final long serialVersionUID = -7666146265221068147L;

    /**
     * Constructor.
     *
     * @param name      the name of the conference.
     * @param publisher the name of the conference proceedings' publisher.
     * @param isbn      the ISBN number of the conference.
     * @param issn      the ISSN number of the conference.
     */
    public ConferenceFake(String name, String publisher, String isbn, String issn) {
        setName(name);
        setPublisher(publisher);
        setISBN(isbn);
        setISSN(issn);
    }

    @Override
    public boolean isFakeEntity() {
        return true;
    }

}
