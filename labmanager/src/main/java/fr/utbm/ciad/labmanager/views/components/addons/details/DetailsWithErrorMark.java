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

package fr.utbm.ciad.labmanager.views.components.addons.details;

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * A Details with an error marker on the side of the title.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public class DetailsWithErrorMark extends Details {

    private static final long serialVersionUID = -7845592866965170183L;

    private final Map<Object, IssueDescription> statuses = new WeakHashMap<>();

    private final HorizontalLayout errorBadge;

    private final Span errorText = new Span();

    private final Text title;

    /**
     * Constructor without initial title.
     *
     * @param content the content of the details component.
     */
    public DetailsWithErrorMark(Component content) {
        this(null, content);
    }

    /**
     * Constructor.
     *
     * @param title   the title of the details.
     * @param content the content of the details component.
     */
    public DetailsWithErrorMark(String title, Component content) {
        final var summary = new HorizontalLayout();
        summary.setSpacing(false);

        final var icon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
        icon.getStyle().set("width", "var(--lumo-icon-size-s)");  //$NON-NLS-1$//$NON-NLS-2$
        icon.getStyle().set("height", "var(--lumo-icon-size-s)");  //$NON-NLS-1$//$NON-NLS-2$

        this.errorBadge = new HorizontalLayout(icon, this.errorText);
        this.errorBadge.setSpacing(false);
        this.errorBadge.getStyle().set("color", "var(--lumo-error-text-color)");  //$NON-NLS-1$//$NON-NLS-2$
        this.errorBadge.getStyle().set("margin-left", "var(--lumo-space-s)");  //$NON-NLS-1$//$NON-NLS-2$
        this.errorBadge.setVisible(false);

        this.title = new Text(title);
        summary.add(this.title);
        summary.add(this.errorBadge);

        setSummary(summary);
        add(content);
        setWidthFull();
    }

    @Override
    public void setSummaryText(String title) {
        this.title.setText(title);
    }

    /**
     * Change the error message. This function causes the error mark to be displayed or hidden.
     *
     * @param message the error message. If it is {@code null} or empty, the error mark is hidden.
     * @see #updateErrorMessage()
     */
    public void setError(String message) {
        final var normedText = Strings.emptyToNull(message);
        this.errorText.setText(normedText);
        this.errorBadge.setVisible(normedText != null);
    }

    /**
     * Update the error message according to the current reported statuses.
     *
     * @see #setError(String)
     */
    public void updateErrorMessage() {
        var nbErrors = 0;
        var nbWarnings = 0;
        for (final var description : this.statuses.values()) {
            if (description.getError() != null) {
                ++nbErrors;
            } else if (description.getWarning() != null) {
                ++nbWarnings;
            }
        }
        // Update the message
        final String message;
        if (nbErrors > 1) {
            if (nbWarnings > 1) {
                message = getTranslation("views.forms.details.errors_warnings", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
            } else if (nbWarnings > 0) {
                message = getTranslation("views.forms.details.errors_warning", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
            } else {
                message = getTranslation("views.forms.details.errors", Integer.valueOf(nbErrors)); //$NON-NLS-1$
            }
        } else if (nbErrors > 0) {
            if (nbWarnings > 1) {
                message = getTranslation("views.forms.details.error_warnings", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
            } else if (nbWarnings > 0) {
                message = getTranslation("views.forms.details.error_warning", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
            } else {
                message = getTranslation("views.forms.details.error", Integer.valueOf(nbErrors)); //$NON-NLS-1$
            }
        } else if (nbWarnings > 1) {
            message = getTranslation("views.forms.details.warnings", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
        } else if (nbWarnings > 0) {
            message = getTranslation("views.forms.details.warning", Integer.valueOf(nbErrors), Integer.valueOf(nbWarnings)); //$NON-NLS-1$
        } else {
            message = null;
        }
        setError(message);
    }

    /**
     * Remove the error and warning messages that are displayed as markers of the details section for the given field.
     *
     * @param field the field that is the source of the status change.
     */
    public void removeStatusFor(Object field) {
        updateStatus(field, null, null);
    }

    /**
     * Update the error and warning messages that are displayed as markers of the details section.
     *
     * @param field          the field that is the source of the status change.
     * @param errorMessage   indicates the new error message.
     * @param warningMessage indicates the new warning message.
     */
    public void updateStatus(Object field, String errorMessage, String warningMessage) {
        final var errMsg = Strings.emptyToNull(errorMessage);
        final var wrnMsg = Strings.emptyToNull(warningMessage);
        // Update the status counters
        var errorChange = false;
        var warningChange = false;
        if (errMsg != null || wrnMsg != null) {
            final var description = this.statuses.computeIfAbsent(field, it -> new IssueDescription());
            errorChange = Objects.equals(errMsg, description.getError());
            warningChange = Objects.equals(wrnMsg, description.getWarning());
            description.setMessages(errMsg, wrnMsg);
        } else {
            final var removedDescription = this.statuses.remove(field);
            if (removedDescription != null) {
                if (removedDescription.getError() != null) {
                    errorChange = true;
                }
                if (removedDescription.getWarning() != null) {
                    warningChange = true;
                }
            }
        }
        // Compute the total number of errors and warnings if necessary
        if (errorChange || warningChange) {
            updateErrorMessage();
        }
    }

    /**
     * Remove all the error and warnings marks.
     */
    public void removeAllStatuses() {
        if (!this.statuses.isEmpty()) {
            this.statuses.clear();
            updateErrorMessage();
        }
    }

    /**
     * A Details with an error marker on the side of the title.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private static class IssueDescription implements Serializable {

        private static final long serialVersionUID = -89177742726006577L;

        private String error;

        private String warning;

        /**
         * Constructor.
         */
        IssueDescription() {
            //
        }

        /**
         * Change the messages.
         *
         * @param error   the error message.
         * @param warning the warning message.
         */
        public void setMessages(String error, String warning) {
            this.error = error;
            this.warning = warning;
        }

        /**
         * Replies the error message.
         *
         * @return the error message or {@code null}.
         */
        public String getError() {
            return this.error;
        }

        /**
         * Replies the warning message.
         *
         * @return the warning message or {@code null}.
         */
        public String getWarning() {
            return this.warning;
        }

    }

}
