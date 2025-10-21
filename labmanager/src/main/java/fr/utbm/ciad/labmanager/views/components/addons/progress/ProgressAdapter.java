/*
 * $Id$
 *
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * Copyright (c) 2019 Kaspar Scherrer
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

package fr.utbm.ciad.labmanager.views.components.addons.progress;

import com.google.common.base.Strings;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.arakhne.afc.progress.ProgressionEvent;
import org.arakhne.afc.progress.ProgressionListener;

import java.io.Serializable;
import java.util.Optional;

/**
 * Adapter that is linking a Vaadin progress to a generic Arakhne progress indicator.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class ProgressAdapter implements ProgressionListener, Serializable {

    private static final long serialVersionUID = 3793954623984428659L;

    private final Span text;

    private final ProgressBar progressBar;

    /**
     * Constructor.
     *
     * @param bar  the Vaadin progress bar to link to.
     * @param text the Vaadin span to link to.
     */
    public ProgressAdapter(ProgressBar bar, Span text) {
        this.progressBar = bar;
        this.text = text;
    }

    /**
     * Constructor.
     *
     * @param bar the Vaadin progress bar to link to.
     */
    public ProgressAdapter(ProgressBar bar) {
        this(bar, null);
    }

    /**
     * Replies the progression percentage for the task.
     * When the progression is {@link #isIndeterminate() indeterminate}, the value
     * replied by this function is inconsistent.
     *
     * @return the progression percentage.
     */
    public double getPercent() {
        return this.progressBar.getValue();
    }

    /**
     * Replies the progression is indeterminate. When the progression is indeterminate, the value
     * replied by {@link #getPercent()} is inconsistent.
     *
     * @return {@code true} if the progression is indeterminate.
     */
    public boolean isIndeterminate() {
        return this.progressBar.isIndeterminate();
    }

    @Override
    public void onProgressionValueChanged(ProgressionEvent event) {
        updateProgressBar(event.isIndeterminate(), event.getPercent(), event.getComment());
    }

    @Override
    public void onProgressionStateChanged(ProgressionEvent event) {
        updateProgressBar(event.isIndeterminate(), event.getPercent(), event.getComment());
    }

    /**
     * Replies the UI associated to the progress bar.
     *
     * @return the UI.
     */
    protected final Optional<UI> getUI() {
        return this.progressBar.getUI();
    }

    /**
     * Invoked to update the progress bar according to the information in the given event.
     *
     * @param isIndetemrinateindicates if hte status of the task progression is indetemrinate.
     * @param currentValue             the current value that could be shown by the progress bar, between {@code 0} and {@code 100}.
     * @param text                     the text associated to the task.
     */
    protected void updateProgressBar(boolean isIndeterminate, double currentValue, String text) {
        assert currentValue >= 0. && currentValue <= 100.;
        final var ui = getUI().orElse(null);
        if (ui != null) {
            if (isIndeterminate) {
                ui.access(() -> this.progressBar.setIndeterminate(true));
            } else {
                ui.access(() -> {
                    if (this.progressBar.isIndeterminate()) {
                        this.progressBar.setMin(0.);
                        this.progressBar.setMax(100.);
                        this.progressBar.setIndeterminate(false);
                    }
                    if (this.text != null) {
                        this.text.setText(Strings.nullToEmpty(text));
                    }
                    this.progressBar.setValue(currentValue);
                });
            }
        }
    }

}
