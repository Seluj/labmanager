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

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.utils.SerializableExceptionProvider;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressAdapter;
import fr.utbm.ciad.labmanager.views.components.addons.progress.ProgressExtension;
import io.overcoded.vaadin.wizard.AbstractProgressionWizardStep;
import io.overcoded.vaadin.wizard.ExceptionRunnable;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Abstract implementation of a wizard step with progression indicator with components specific to the lab manager API.
 *
 * @param <T> the type of the context data.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractLabManagerProgressionWizardStep<T extends AbstractContextData> extends AbstractProgressionWizardStep<T> {

    private static final long serialVersionUID = 4234768503505276966L;

    /**
     * Constructor.
     *
     * @param context              the data context.
     * @param name                 the name of the step. It is also the initial value of the step's title.
     * @param order                the order number for the wizard step.
     * @param tasksCount           the number of tasks that should be run in parallel.
     * @param automaticNextClick   indicates if the wizard has to move to the next step automatically when all the parallel tasks are finished and a next button is defined.
     * @param automaticFinishClick indicates if the wizard has to move to the finish automatically when all the parallel tasks are finished and a finish button is defined.
     */
    public AbstractLabManagerProgressionWizardStep(T context, String name, int order, int tasksCount, boolean automaticNextClick, boolean automaticFinishClick) {
        super(context, name, order, tasksCount, automaticNextClick, automaticFinishClick);
    }

    /**
     * Replies the logger than should be used by this component.
     *
     * @return the logger, never {@code null}.
     */
    protected Logger getLogger() {
        return AbstractLabManagerWizardStep.getLogger(getWizard());
    }

    @Override
    protected ParallelTask createParallelTask(int taskNo) {
        return new ExtendedParallelTask(taskNo);
    }

    @Override
    protected final ExceptionRunnable createAsynchronousTask(int taskNo) {
        final var progression = createProgression(taskNo);
        final var run0 = createAsynchronousTask(taskNo, progression);
        return () -> {
            String endMessage = null;
            try {
                endMessage = run0.get();
            } finally {
                progression.end();
                ProgressExtension.forceComment(progression, Strings.nullToEmpty(endMessage));
            }
        };
    }

    /**
     * Create and reply the asynchronous task.
     *
     * @param taskNo      the number of the task.
     * @param progression the progression indicator to be used by the asynchronous task.
     * @return the asynchronous task.
     */
    protected abstract SerializableExceptionProvider<String> createAsynchronousTask(int taskNo, Progression progression);

    /**
     * Create a progression indicator. By default, the maximum value of the progression is {@code 100}.
     *
     * @param taskNo the number of the task.
     * @return the progression indicator.
     */
    protected Progression createProgression(int taskNo) {
        final var task = getParallelTask(taskNo, ExtendedParallelTask.class);
        final var progression = new DefaultProgression(0, 100);
        progression.addProgressionListener(task.orElseThrow().progress);
        return progression;
    }

    /**
     * Replies the progression adapter.
     *
     * @param taskNo the number of the task.
     * @return the progress adapter.
     */
    protected Optional<ProgressAdapter> getProgressAdapter(int taskNo) {
        final var task = getParallelTask(taskNo, ExtendedParallelTask.class);
        return task.flatMap(it -> Optional.of(it.progress));
    }

    /**
     * Container for parallel tasks.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 24.3.1
     */
    protected static class ExtendedParallelTask extends ParallelTask {

        private static final long serialVersionUID = 6403025328952462105L;

        /**
         * Progress adapter.
         */
        public final ProgressAdapter progress;

        /**
         * Constructor.
         *
         * @param taskNo the task number.
         */
        public ExtendedParallelTask(int taskNo) {
            super(taskNo);
            this.progress = new ProgressAdapter(getProgressBar(), getProgressText());
        }

    }

}
