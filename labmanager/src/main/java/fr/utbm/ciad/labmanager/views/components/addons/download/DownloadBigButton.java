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

package fr.utbm.ciad.labmanager.views.components.addons.download;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.function.SerializableSupplier;

import java.lang.ref.WeakReference;

/**
 * A button that enables to download a file, with a big icon.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DownloadBigButton extends Button {

    private static final long serialVersionUID = 8484095728459257303L;

    private static final int BUTTON_WIDTH = 512;

    private static final int BUTTON_HEIGHT = 164;

    private static final int ICON_MIN_HEIGHT = 32;

    private static final int ICON_MIN_WIDTH = 32;

    private static final int ICON_MAX_HEIGHT = 128;

    private static final int ICON_MAX_WIDTH = 128;

    private static final String ICON_SIZE_STR = ICON_MAX_WIDTH + Unit.PIXELS.getSymbol();

    private static final String BUTTON_MARGIN_STR = 10 + Unit.PIXELS.getSymbol();

    private final WeakReference<DownloadExtension<? extends Button>> configurator;

    /**
     * Constructor.
     *
     * @param dialogTitle the title of the download dialog box.
     */
    protected DownloadBigButton(String dialogTitle) {
        setMinHeight(BUTTON_HEIGHT, Unit.PIXELS);
        setMaxHeight(BUTTON_HEIGHT, Unit.PIXELS);
        setMinWidth(BUTTON_WIDTH, Unit.PIXELS);
        setMaxWidth(BUTTON_WIDTH, Unit.PIXELS);
        setWhiteSpace(WhiteSpace.NORMAL);
        addThemeVariants(ButtonVariant.LUMO_LARGE);
        getStyle().setMargin(BUTTON_MARGIN_STR);

        final var config = DownloadExtension.extend(this);
        this.configurator = new WeakReference<>(config);

        config.withProgressTitle(dialogTitle);
    }

    /**
     * Factory method for a big button for downloading a file.
     *
     * @param <C>          the type of the button.
     * @param dialogTitle  the title of the download dialog box.
     * @param iconProvider the lambda that provides the icon for the button and the download dialog.
     * @return the button.
     */
    public static <C extends Component & HasSize> DownloadBigButton newButtonWithComponent(String dialogTitle, SerializableSupplier<C> iconProvider) {
        final var button = new DownloadBigButton(dialogTitle);
        final var buttonIcon = iconProvider.get();
        buttonIcon.setMinHeight(ICON_MIN_HEIGHT, Unit.PIXELS);
        buttonIcon.setMaxHeight(ICON_MAX_HEIGHT, Unit.PIXELS);
        buttonIcon.setMinWidth(ICON_MIN_WIDTH, Unit.PIXELS);
        buttonIcon.setMaxWidth(ICON_MAX_WIDTH, Unit.PIXELS);
        button.setIcon(buttonIcon);
        button.configure().withProgressIcon(iconProvider.get());
        return button;
    }

    /**
     * Factory method for a big button for downloading a file.
     *
     * @param dialogTitle  the title of the download dialog box.
     * @param iconProvider the lambda that provides the icon for the button and the download dialog.
     * @return the button.
     */
    public static DownloadBigButton newButtonWithIcon(String dialogTitle, SerializableSupplier<AbstractIcon<?>> iconProvider) {
        final var button = new DownloadBigButton(dialogTitle);
        final var buttonIcon = iconProvider.get();
        buttonIcon.setSize(ICON_SIZE_STR);
        button.setIcon(buttonIcon);
        button.configure().withProgressIcon(iconProvider.get());
        return button;
    }

    /**
     * Configure the download button.
     *
     * @return the configurator.
     */
    public DownloadExtension<? extends Button> configure() {
        return this.configurator.get();
    }

}
