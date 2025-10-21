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

package fr.utbm.ciad.labmanager.components.indicators;

import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import org.springframework.context.support.MessageSourceAccessor;

import java.time.LocalDate;

/**
 * Abstract implementation of an indicator for the "current time".
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.3
 */
public abstract class AbstractInstantIndicator extends AbstractIndicator {

    private static final long serialVersionUID = 4231316529509431609L;

    /**
     * Constructor.
     *
     * @param messages  the provider of messages.
     * @param constants the accessor to the constants.
     */
    public AbstractInstantIndicator(MessageSourceAccessor messages, ConfigurationConstants constants) {
        super(messages, constants);
    }

    @Override
    public LocalDate getReferencePeriodStart() {
        return LocalDate.now();
    }

    @Override
    public LocalDate getReferencePeriodEnd() {
        return LocalDate.now();
    }

}
