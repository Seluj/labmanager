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

package fr.utbm.ciad.labmanager.views.components.addons.badges;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * State of a badge that is used by a renderer in grids, combos, etc.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public enum BadgeState {

    /**
     * Primary badge.
     */
    PRIMARY(null) {
        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    },

    /**
     * Success badge.
     */
    SUCCESS("success") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return VaadinIcon.CHECK;
        }
    },

    /**
     * Error badge.
     */
    ERROR("error") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return VaadinIcon.CLOSE_SMALL;
        }
    },

    /**
     * Contrast badge.
     */
    CONTRAST("contrast") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    },

    /**
     * Pill badge.
     */
    PILL("contrast pill") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    },

    /**
     * Success pill badge.
     */
    SUCCESS_PILL("success pill") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    },

    /**
     * Error pill badge.
     */
    ERROR_PILL("error pill") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    },

    /**
     * Contrast pill badge.
     */
    CONTRAST_PILL("contrast pill") { //$NON-NLS-1$

        @Override
        public VaadinIcon getIcon() {
            return null;
        }
    };

    private static final String BADGE_STYLE = "badge"; //$NON-NLS-1$

    private final String style;

    BadgeState(String style) {
        if (style == null) {
            this.style = BADGE_STYLE;
        } else {
            this.style = BADGE_STYLE + " " + style //$NON-NLS-1$
            ;
        }
    }

    /**
     * Replies the given state or the default state if the given state is {@code null}..
     *
     * @param state a state or {@code null}.
     * @return a state, never {@code null}.
     */
    public static BadgeState orDefault(BadgeState state) {
        return state == null ? PRIMARY : state;
    }

    /**
     * Replies the style of the badge.
     *
     * @return the CSS style.
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * Replies the icon of the badge.
     *
     * @return the icon.
     */
    public abstract VaadinIcon getIcon();

    /**
     * Assign the style of this badge to the given component.
     *
     * @param component the component to change.
     */
    public void assignTo(Component component) {
        assert component != null;
        component.getElement().getThemeList().add(getStyle());
    }

}
