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

import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.Wizard;
import io.overcoded.vaadin.wizard.config.WizardContentConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Abstract implementation of a wizard step for form with components specific to the lab manager API.
 *
 * @param <T> the type of the context data.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLabManagerFormWizardStep<T extends AbstractContextData> extends AbstractFormWizardStep<T> {

    private static final long serialVersionUID = 7840293414512156931L;

    /**
     * Constructor.
     *
     * @param context the data context.
     * @param name    the name of the step. It is also the initial value of the step's title.
     * @param order   the order number for the wizard step.
     */
    public AbstractLabManagerFormWizardStep(T context, String name, int order) {
        super(context, name, order);
    }

    /**
     * Constructor.
     *
     * @param properties the configuration of the content.
     * @param context    the data context.
     * @param name       the name of the step.
     * @param order      the order of the step in the enclosing wizard.
     */
    public AbstractLabManagerFormWizardStep(WizardContentConfigurationProperties properties, T context, String name, int order) {
        super(properties, context, name, order);
    }

    /**
     * Replies the logger than should be used by this component.
     *
     * @param <T>    the type of the data context of the wizard.
     * @param wizard the wizard reference.
     * @return the logger, never {@code null}.
     */
    public static <T> Logger getLogger(Optional<Wizard<T>> wizard) {
        if (wizard.isPresent()) {
            final var wzd = wizard.get();
            if (wzd instanceof AbstractLabManagerWizard lwzd) {
                return lwzd.getLogger();
            }
        }
        return LoggerFactory.getLogger(AbstractLabManagerFormWizardStep.class);
    }

    /**
     * Replies the logger than should be used by this component.
     *
     * @return the logger, never {@code null}.
     */
    protected Logger getLogger() {
        return getLogger(getWizard());
    }

}
