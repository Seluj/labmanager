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

package fr.utbm.ciad.labmanager.views.components.addons.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract implementation of the data in the wizards.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractContextData implements Serializable {

    private static final long serialVersionUID = -5396310351401186260L;

    private final List<Long> entityIdentifiers = new ArrayList<>();

    /**
     * Constructor.
     */
    public AbstractContextData() {
        //
    }

    /**
     * Replies the list of entity identifiers that were initially selected for updating.
     *
     * @return the list of entity identifiers.
     */
    public synchronized List<Long> getEntityIdentifiers() {
        return Collections.unmodifiableList(this.entityIdentifiers);
    }

    /**
     * Change the list of entity identifiers that were initially selected for updating.
     *
     * @param identifiers the list of entity identifiers.
     */
    public synchronized void setEntityIdentifiers(List<Long> identifiers) {
        this.entityIdentifiers.clear();
        this.entityIdentifiers.addAll(identifiers);
    }

}
