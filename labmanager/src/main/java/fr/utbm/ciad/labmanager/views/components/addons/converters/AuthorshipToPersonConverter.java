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

package fr.utbm.ciad.labmanager.views.components.addons.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Authorship;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.comparators.AuthorshipComparator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A converter that is removing the spaces at the ends of the input string.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AuthorshipToPersonConverter implements Converter<List<Person>, Set<Authorship>> {

    private static final long serialVersionUID = 5344005355126348062L;

    private final Publication publication;

    /**
     * Constructor.
     *
     * @param publication the publication to associate to the authorships.
     */
    public AuthorshipToPersonConverter(Publication publication) {
        this.publication = publication;
    }

    @Override
    public Result<Set<Authorship>> convertToModel(List<Person> value, ValueContext context) {
        final var list = new HashSet<Authorship>();
        if (value != null) {
            int order = 0;
            for (final var person : value) {
                final var authorship = new Authorship(this.publication, person, order);
                list.add(authorship);
                ++order;
            }
        }
        return Result.ok(list);
    }

    @Override
    public List<Person> convertToPresentation(Set<Authorship> value, ValueContext context) {
        final var list = new ArrayList<Person>();
        if (value != null) {
            value.stream().sorted(AuthorshipComparator.DEFAULT).forEachOrdered(it -> list.add(it.getPerson()));
        }
        return list;
    }

}
