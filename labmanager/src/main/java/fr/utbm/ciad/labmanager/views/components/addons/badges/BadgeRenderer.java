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

import com.google.common.base.Strings;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableFunction;
import org.arakhne.afc.util.OutputParameter;

/**
 * A renderer that could be using for displaying badges in grids, combos, etc.
 *
 * @param <ITEM> the type of the data that is displayed.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class BadgeRenderer<ITEM> extends ComponentRenderer<Component, ITEM> {

    private static final long serialVersionUID = 3215157356978585101L;

    /**
     * Constructor.
     *
     * @param componentFunction callback function that takes data as first argument.
     *                          The second argument is a function that must be called to set the badge set (first argument)
     *                          and the badge text (second argument).
     */
    public BadgeRenderer(SerializableBiConsumer<ITEM, ComponentCreator> componentFunction) {
        super(createComponentFunction(componentFunction));
    }

    private static <ITEM> SerializableFunction<ITEM, Component> createComponentFunction(SerializableBiConsumer<ITEM, ComponentCreator> componentFunction) {
        return it -> {
            final var component = new OutputParameter<Component>();
            final ComponentCreator creator = (badgeState, badgeText, badgeLabel) -> {
                final var state = BadgeState.orDefault(badgeState);
                final var vaadinIcon = state.getIcon();
                final var isEmpty = Strings.isNullOrEmpty(badgeText);
                Component createdComponent = null;
                if (vaadinIcon != null) {
                    final var icon = vaadinIcon.create();
                    icon.getStyle().set("padding", "var(--lumo-space-xs)"); //$NON-NLS-1$ //$NON-NLS-2$
                    if (isEmpty) {
                        createdComponent = icon;
                    } else {
                        createdComponent = new Span(icon, new Span(badgeText));
                    }
                } else if (isEmpty) {
                    throw new IllegalStateException();
                } else {
                    createdComponent = new Span(badgeText);
                }
                assert createdComponent != null;
                state.assignTo(createdComponent);
                if (!Strings.isNullOrEmpty(badgeLabel)) {
                    // Accessible label
                    createdComponent.getElement().setAttribute("aria-label", badgeLabel); //$NON-NLS-1$
                    // Tooltip
                    createdComponent.getElement().setAttribute("title", badgeLabel); //$NON-NLS-1$
                }
                component.set(createdComponent);
            };
            componentFunction.accept(it, creator);
            return component.get();
        };
    }

    /**
     * The creator of a badge component.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    public interface ComponentCreator {

        /**
         * Create the component.
         *
         * @param state      the stage of the badge.
         * @param badgeText  the text to put in the badge.
         * @param badgeLabel the accessibility and tooltip label.
         */
        void create(BadgeState state, String badgeText, String badgeLabel);

    }

}
