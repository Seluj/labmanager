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

package fr.utbm.ciad.labmanager.views.components.addons.avatars;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.AbstractStreamResource;

/**
 * Item for representing an avatar with vaadin API.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class AvatarItem extends Composite<HorizontalLayout> implements HasSize {

    private static final long serialVersionUID = -6842659849331355769L;

    private final Span heading = new Span();

    private final Span description = new Span();

    /**
     * Constructor.
     */
    public AvatarItem() {
        this(null, null, new Avatar());
    }

    /**
     * Construtor.
     *
     * @param heading     the heading text.
     * @param description the description;
     * @param avatar      the avatar to display.
     */
    public AvatarItem(String heading, String description, Avatar avatar) {
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);

        this.description.getStyle()
                .set("color", "var(--lumo-secondary-text-color)") //$NON-NLS-1$ //$NON-NLS-2$
                .set("font-size", "var(--lumo-font-size-s)"); //$NON-NLS-1$ //$NON-NLS-2$

        final var column = new VerticalLayout(this.heading, this.description);
        column.setPadding(false);
        column.setSpacing(false);

        getContent().add(column);
        getContent().getStyle().set("line-height", "var(--lumo-line-height-m)"); //$NON-NLS-1$ //$NON-NLS-2$

        setHeading(heading);
        setDescription(description);
        setAvatar(avatar);
    }

    /**
     * Change the heading text.
     *
     * @param text the text in the heading.
     */
    public void setHeading(String text) {
        this.heading.setText(text);
    }

    /**
     * Change the description of the avatar.
     *
     * @param text the description.
     */
    public void setDescription(String text) {
        this.description.setText(text);
    }

    /**
     * Change the avatar.
     *
     * @param avatar the new avatar.
     */
    public void setAvatar(Avatar avatar) {
        if (getContent().getComponentAt(0) instanceof Avatar existing) {
            existing.removeFromParent();
        }
        getContent().addComponentAsFirst(avatar);
    }

    /**
     * Change the avatar border color.
     *
     * <p>The color index defines which color will be used for the border of the
     * avatar. Color index N applies CSS variable {@code --vaadin-user-color-N}
     * to the border.
     *
     * @param index the index of the color, or {@code null} to remove the border.
     */
    public void setAvatarBorderColor(Integer index) {
        if (getContent().getComponentAt(0) instanceof Avatar existing) {
            // Bug fix in Vaadin Avatar, that is not supporting null argument in opposite to the API documentation.
            if (index == null) {
                existing.getElement().removeProperty("colorIndex"); //$NON-NLS-1$
            } else {
                existing.setColorIndex(index);
            }
        }
    }

    /**
     * Change the URL of the avatar image.
     *
     * @param url the URL of the avatar image.
     */
    public void setAvatarURL(String url) {
        if (getContent().getComponentAt(0) instanceof Avatar existing) {
            existing.setImage(url);
        }
    }

    /**
     * Change the resource of the avatar image.
     *
     * @param resource the resource of the avatar image.
     */
    public void setAvatarResource(AbstractStreamResource resource) {
        if (getContent().getComponentAt(0) instanceof Avatar existing) {
            existing.setImageResource(resource);
        }
    }

}
