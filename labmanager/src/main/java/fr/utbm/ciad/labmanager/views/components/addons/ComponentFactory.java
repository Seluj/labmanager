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

package fr.utbm.ciad.labmanager.views.components.addons;

import com.google.common.base.Strings;
import com.ibm.icu.text.Normalizer2;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog.ConfirmEvent;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.server.StreamResource;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.project.Project;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.avatars.AvatarItem;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.arakhne.afc.vmutil.FileSystem;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import static fr.utbm.ciad.labmanager.views.ViewConstants.DEFAULT_MINIMAL_WIDTH_FOR_2_COLUMNS_FORM;

/**
 * Factory of Vaadin components.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@org.springframework.stereotype.Component
public final class ComponentFactory {

    /**
     * Define the color of the border of the regular user avatars: red.
     */
    private static final int ADMINISTRATOR_BORDER_COLOR = 0;

    /**
     * Define the color of the border of the regular user avatars: dark blue.
     */
    private static final int USER_BORDER_COLOR = 5;

    private static final String FOR_MANY = "%"; //$NON-NLS-1$

    private static final Normalizer2 NORMALIZER = Normalizer2.getNFKDInstance();

    private static final Pattern PATTERN = Pattern.compile(".[\\p{M}]"); //$NON-NLS-1$

    private static final String FOR_ONE = "_"; //$NON-NLS-1$

    private ComponentFactory() {
        //
    }

    /**
     * Create a form layout with multiple columns.
     *
     * @param columns the number of columns between 1 and 2.
     * @return the layout.
     */
    public static FormLayout newColumnForm(int columns) {
        assert columns >= 1 && columns <= 2;
        final var content = new FormLayout();
        switch (columns) {
            case 1:
                // No specific configuration for a single column
                break;
            case 2:
                content.setResponsiveSteps(
                        new FormLayout.ResponsiveStep("0", 1), //$NON-NLS-1$
                        new FormLayout.ResponsiveStep(DEFAULT_MINIMAL_WIDTH_FOR_2_COLUMNS_FORM, 2));
                break;
            default:
                throw new IllegalArgumentException();
        }
        return content;
    }

    /**
     * Create a text field with a clickable 16x16 icon as suffix.
     *
     * @param href     the URL of the target page.
     * @param iconPath the path of the icon in the JAva resources, starting with a slash character.
     * @return the field.
     * @see #newClickableIconTextField(String, String, int)
     */
    public static TextField newClickableIconTextField(String href, String iconPath) {
        return newClickableIconTextField(href, iconPath, 16);
    }

    /**
     * Create a text field with a clickable icon as suffix.
     *
     * @param href     the URL of the target page.
     * @param iconPath the path of the icon in the JAva resources, starting with a slash character.
     * @param iconSize the size of the icon in points.
     * @return the field.
     * @see #newClickableIconTextField(String, String)
     */
    public static TextField newClickableIconTextField(String href, String iconPath, int iconSize) {
        assert !Strings.isNullOrEmpty(href);
        final var content = new TextField();
        final var imageResource = newStreamImage(iconPath);
        final var image = new Image(imageResource, href);
        image.setMinHeight(iconSize, Unit.POINTS);
        image.setMaxHeight(iconSize, Unit.POINTS);
        image.setMinWidth(iconSize, Unit.POINTS);
        image.setMaxWidth(iconSize, Unit.POINTS);
        final var anchor = new Anchor(href, image);
        anchor.setTitle(href);
        anchor.setTarget(AnchorTarget.BLANK);
        anchor.setTabIndex(-1);
        content.setSuffixComponent(anchor);
        return content;
    }

    /**
     * Create a image stream with the image representing an empty background.
     *
     * @return the stream.
     */
    public static StreamResource newEmptyBackgroundStreamImage() {
        return newStreamImage("/images/empty_background.png"); //$NON-NLS-1$
    }

    /**
     * Create a image stream from an URL pointing a Java resource.
     *
     * @param iconPath the path of the icon in the Java resources, starting with a slash character.
     * @return the stream.
     */
    public static StreamResource newStreamImage(String iconPath) {
        assert !Strings.isNullOrEmpty(iconPath);
        final URL url;
        try {
            url = FileSystem.convertStringToURL(iconPath, false);
        } catch (Throwable ex) {
            throw new IllegalArgumentException(ex);
        }
        return new StreamResource(FileSystem.largeBasename(url),
                () -> ComponentFactory.class.getResourceAsStream(iconPath));
    }

    /**
     * Create a resource stream from an URL pointing a Java resource.
     *
     * @param resourceFile the path to the server-side file.
     * @return the resource to the server-side file.
     */
    public static StreamResource newStreamImage(File resourceFile) {
        return new StreamResource(resourceFile.getName(), () -> {
            try {
                return new FileInputStream(resourceFile);
            } catch (IOException ex) {
                throw new IOError(ex);
            }
        });
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu          the receiver of the new item.
     * @param icon          the icon of the item..
     * @param label         the label of the item.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(MenuBar menu, IconFactory icon, String label, String ariaLabel,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return addIconItem(menu, icon, label, ariaLabel, false, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu      the receiver of the new item.
     * @param icon      the icon of the item..
     * @param label     the label of the item.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(MenuBar menu, IconFactory icon, String label, String ariaLabel) {
        return addIconItem(menu, icon, label, ariaLabel, false, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu          the receiver of the new item.
     * @param icon          the icon of the item..
     * @param label         the label of the item.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(SubMenu menu, IconFactory icon, String label, String ariaLabel,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return addIconItem(menu, icon, label, ariaLabel, false, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu      the receiver of the new item.
     * @param icon      the icon of the item..
     * @param label     the label of the item.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(SubMenu menu, IconFactory icon, String label, String ariaLabel) {
        return addIconItem(menu, icon, label, ariaLabel, false, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu          the receiver of the new item.
     * @param icon          the icon of the item..
     * @param label         the label of the item.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(SubMenu menu, StreamResource icon, String label, String ariaLabel,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return addIconItem(menu, icon, label, ariaLabel, false, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu      the receiver of the new item.
     * @param icon      the icon of the item..
     * @param label     the label of the item.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(SubMenu menu, StreamResource icon, String label, String ariaLabel) {
        return addIconItem(menu, icon, label, ariaLabel, false, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver  the receiver of the new item.
     * @param icon      the icon of the item, never {@code null}.
     * @param label     the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @param isChild   indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(HasMenuItems receiver, IconFactory icon, String label, String ariaLabel, boolean isChild) {
        return addIconItem(receiver, icon, label, ariaLabel, isChild, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver      the receiver of the new item.
     * @param icon          the icon of the item, never {@code null}.
     * @param label         the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param isChild       indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(HasMenuItems receiver, IconFactory icon, String label, String ariaLabel, boolean isChild,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        assert icon != null;
        return addIconItem(receiver, icon.create(), label, ariaLabel, isChild, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu          the receiver of the new item.
     * @param icon          the icon of the item..
     * @param label         the label of the item.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(MenuBar menu, LineAwesomeIcon icon, String label, String ariaLabel,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return addIconItem(menu, icon, label, ariaLabel, false, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu      the receiver of the new item.
     * @param icon      the icon of the item..
     * @param label     the label of the item.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(MenuBar menu, LineAwesomeIcon icon, String label, String ariaLabel) {
        return addIconItem(menu, icon, label, ariaLabel, false, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver  the receiver of the new item.
     * @param icon      the icon of the item, never {@code null}.
     * @param label     the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @param isChild   indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(HasMenuItems receiver, LineAwesomeIcon icon, String label, String ariaLabel, boolean isChild) {
        return addIconItem(receiver, icon, label, ariaLabel, isChild, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver      the receiver of the new item.
     * @param icon          the icon of the item, never {@code null}.
     * @param label         the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param isChild       indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static MenuItem addIconItem(HasMenuItems receiver, LineAwesomeIcon icon, String label, String ariaLabel, boolean isChild,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        assert icon != null;
        return addIconItem(receiver, icon.create(), label, ariaLabel, isChild, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu          the receiver of the new item.
     * @param icon          the icon of the item..
     * @param label         the label of the item.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(MenuBar menu, StreamResource icon, String label, String ariaLabel,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return addIconItem(menu, icon, label, ariaLabel, false, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param menu      the receiver of the new item.
     * @param icon      the icon of the item..
     * @param label     the label of the item.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(MenuBar menu, StreamResource icon, String label, String ariaLabel) {
        return addIconItem(menu, icon, label, ariaLabel, false, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver  the receiver of the new item.
     * @param icon      the icon of the item, never {@code null}.
     * @param label     the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                  the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                  the menu item.
     * @param isChild   indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(HasMenuItems receiver, StreamResource icon, String label, String ariaLabel, boolean isChild) {
        return addIconItem(receiver, icon, label, ariaLabel, isChild, null);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver      the receiver of the new item.
     * @param icon          the icon of the item, never {@code null}.
     * @param label         the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param isChild       indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @since 4.0
     */
    public static MenuItem addIconItem(HasMenuItems receiver, StreamResource icon, String label, String ariaLabel, boolean isChild,
                                       ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        assert icon != null;
        final var image = new Image(icon, label);
        image.setMinWidth(ViewConstants.ICON_SIZE, Unit.PIXELS);
        image.setMinHeight(ViewConstants.ICON_SIZE, Unit.PIXELS);
        image.setMaxWidth(ViewConstants.MAX_ICON_SIZE, Unit.PIXELS);
        image.setMaxHeight(ViewConstants.MAX_ICON_SIZE, Unit.PIXELS);
        return addIconItem(receiver, image, label, ariaLabel, isChild, clickListener);
    }

    /**
     * Create a menu item with an icon and text and add it into the given receiver.
     *
     * @param receiver      the receiver of the new item.
     * @param iconInstance  the icon of the item, never {@code null}.
     * @param label         the label of the item. It may be {@code null} for creating an item without text.
     * @param ariaLabel     the aria label of the item. It is recommended to have this aria label not {@code null} or empty when
     *                      the {@code label} is {@code null} or empty, to enable disabled persons to have information on the feature of
     *                      the menu item.
     * @param isChild       indicates if the item is for a sub-menu, when {@code true}; or the root item, when {@code false}.
     * @param clickListener the listener on clicks on the item.
     * @return the created item.
     * @see #setIconItemText(MenuItem, String)
     */
    public static <T extends com.vaadin.flow.component.Component & HasStyle> MenuItem addIconItem(
            HasMenuItems receiver, T iconInstance, String label, String ariaLabel, boolean isChild,
            ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        assert iconInstance != null;
        if (isChild) {
            iconInstance.getStyle().setWidth("var(--lumo-icon-size-s)"); //$NON-NLS-1$
            iconInstance.getStyle().setHeight("var(--lumo-icon-size-s)"); //$NON-NLS-1$
            iconInstance.getStyle().set("marginRight", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final var item = receiver.addItem(iconInstance, clickListener);
        if (!Strings.isNullOrEmpty(ariaLabel)) {
            item.setAriaLabel(ariaLabel);
        }
        if (!Strings.isNullOrEmpty(label)) {
            item.add(new Text(label));
        }
        return item;
    }

    /**
     * Change te text of the given menu item assuming it was build with an icon.
     *
     * @param item the item to change..
     * @param text the new text.
     * @return the item.
     * @see #addIconItem(MenuBar, LineAwesomeIcon, String, String)
     * @see #addIconItem(MenuBar, IconFactory, String, String)
     * @see #addIconItem(HasMenuItems, LineAwesomeIcon, String, String, boolean, ComponentEventListener)
     * @see #addIconItem(HasMenuItems, IconFactory, String, String, boolean, ComponentEventListener)
     */
    public static MenuItem setIconItemText(MenuItem item, String text) {
        final var eltOpt = item.getElement().getChildren()
                .filter(it -> it.getComponent().isPresent() && it.getComponent().get() instanceof Text)
                .map(it -> (Text) it.getComponent().get())
                .findAny();
        if (Strings.isNullOrEmpty(text)) {
            if (eltOpt.isPresent()) {
                final var elt = eltOpt.get();
                elt.removeFromParent();
            }
        } else if (eltOpt.isPresent()) {
            final var elt = eltOpt.get();
            elt.setText(text);
        } else {
            item.add(new Text(text));
        }
        return item;
    }

    /**
     * Create a dialog that asks for a critical question and that is modal.
     * This function does not attach an event handler to the confirm and cancel buttons.
     *
     * @param parent  the parent component; mostly used for obtaining the translation of text.
     * @param title   the title of the box.
     * @param message the message in the box.
     * @return the dialog.
     */
    public static ConfirmDialog newDeletionDialog(com.vaadin.flow.component.Component parent, String title, String message) {
        return newDeletionDialog(parent, title, message, null);
    }

    /**
     * Create a dialog that asks for a critical question and that is modal.
     *
     * @param parent         the parent component; mostly used for obtaining the translation of text.
     * @param title          the title of the box.
     * @param message        the message in the box.
     * @param confirmHandler the handler invoked when the confirm button is pushed.
     * @return the dialog.
     */
    public static ConfirmDialog newDeletionDialog(com.vaadin.flow.component.Component parent, String title, String message,
                                                  ComponentEventListener<ConfirmEvent> confirmHandler) {
        return newCriticalQuestionDialog(
                title, message,
                parent.getTranslation("views.delete"), //$NON-NLS-1$
                confirmHandler);
    }

    /**
     * Create a dialog that asks for a critical question and that is modal.
     * This function does not attach an event handler to the confirm and cancel buttons.
     *
     * @param title       the title of the box.
     * @param message     the message in the box.
     * @param confirmText the text of the confirm button.
     * @return the dialog.
     */
    public static ConfirmDialog newCriticalQuestionDialog(String title, String message, String confirmText) {
        return newCriticalQuestionDialog(title, message, confirmText, null);
    }

    /**
     * Create a dialog that asks for a critical question and that is modal.
     *
     * @param title          the title of the box.
     * @param message        the message in the box.
     * @param confirmText    the text of the confirm button.
     * @param confirmHandler the handler invoked when the confirm button is pushed.
     * @return the dialog.
     */
    public static ConfirmDialog newCriticalQuestionDialog(String title, String message, String confirmText,
                                                          ComponentEventListener<ConfirmEvent> confirmHandler) {
        return newCriticalQuestionDialog(title, message, confirmText, null, confirmHandler);
    }

    /**
     * Create a dialog that asks for a critical question and that is modal.
     *
     * @param title          the title of the box.
     * @param message        the message in the box.
     * @param confirmText    the text of the confirm button.
     * @param cancelText     the text of the cancel button.
     * @param confirmHandler the handler invoked when the confirm button is pushed.
     * @return the dialog.
     * @since 4.0
     */
    public static ConfirmDialog newCriticalQuestionDialog(String title, String message, String confirmText,
                                                          String cancelText, ComponentEventListener<ConfirmEvent> confirmHandler) {
        final var dialog = new ConfirmDialog();
        dialog.setConfirmButtonTheme("error primary"); //$NON-NLS-1$
        dialog.setHeader(title);
        dialog.setText(message);
        dialog.setConfirmText(confirmText);
        if (!Strings.isNullOrEmpty(cancelText)) {
            dialog.setCancelText(cancelText);
        }
        dialog.setCancelable(true);
        dialog.setCloseOnEsc(true);
        if (confirmHandler != null) {
            dialog.addConfirmListener(confirmHandler);
        }
        return dialog;
    }

    /**
     * Add an hidden column in the grid (usually the last column) for supporting the hover menu bar.
     *
     * @param <T>       the type of data in the grid.
     * @param grid      the grid to update.
     * @param generator the callback function for creating the buttons of the menu bar.
     */
    public static <T> void addGridCellHoverMenuBar(Grid<T> grid, BiConsumer<T, MenuBar> generator) {
        grid.addComponentColumn(it -> {
            final var menuBar = new MenuBar();
            menuBar.addClassName("hoverGridHlHide"); //$NON-NLS-1$
            menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON, MenuBarVariant.LUMO_SMALL, MenuBarVariant.LUMO_TERTIARY_INLINE);
            generator.accept(it, menuBar);
            return menuBar;
        }).setTextAlign(ColumnTextAlign.END).setKey("controls").setAutoWidth(true).setFlexGrow(0).setWidth("0px"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Open a standard dialog box for editing an information.
     * This function provides three callbacks. One is the standard saving callback that is verifying the validity of
     * the input data. Second, it is the standard validation callback that is saving the data after
     * turning on the validation flag by the local authority. Third is the standard deletion callback that invoked
     * after querying the user for accepting the deletion.
     *
     * @param <T>                the type of the edited entity.
     * @param title              the title of the dialog.
     * @param content            the content of the dialog, where the editing fields are located.
     * @param mapEnterKeyToSave  if {@code true}, the "save" button is activated when the {@code Enter}
     *                           is pushed. If {@code false}, the {@code Enter} is not mapped to a component.
     * @param saveDoneCallback   the callback that is invoked after saving
     * @param deleteDoneCallback the callback that is invoked after deleting
     */
    public static <T extends IdentifiableEntity> void openEditionModalDialog(
            String title,
            AbstractEntityEditor<T> content,
            boolean mapEnterKeyToSave,
            SerializableBiConsumer<Dialog, T> saveDoneCallback,
            SerializableBiConsumer<Dialog, T> deleteDoneCallback) {
        openEditionModalDialog(title, content, mapEnterKeyToSave, true, saveDoneCallback, deleteDoneCallback);
    }

    /**
     * Open a standard dialog box for editing an information.
     * This function provides three callbacks. One is the standard saving callback that is verifying the validity of
     * the input data. Second, it is the standard validation callback that is saving the data after
     * turning on the validation flag by the local authority. Third is the standard deletion callback that invoked
     * after querying the user for accepting the deletion.
     *
     * @param <T>                the type of the edited entity.
     * @param title              the title of the dialog.
     * @param content            the content of the dialog, where the editing fields are located.
     * @param mapEnterKeyToSave  if {@code true}, the "save" button is activated when the {@code Enter}
     *                           is pushed. If {@code false}, the {@code Enter} is not mapped to a component.
     * @param saveInDatabase     if {@code true}, the entity is saved in the database.
     * @param saveDoneCallback   the callback that is invoked after saving
     * @param deleteDoneCallback the callback that is invoked after deleting
     * @since 4.0
     */
    public static <T extends IdentifiableEntity> void openEditionModalDialog(
            String title,
            AbstractEntityEditor<T> content,
            boolean mapEnterKeyToSave,
            boolean saveInDatabase,
            SerializableBiConsumer<Dialog, T> saveDoneCallback,
            SerializableBiConsumer<Dialog, T> deleteDoneCallback) {
        openEditionModalDialog(title, "views.save", content, mapEnterKeyToSave, //$NON-NLS-1$
                saveInDatabase,
                saveDoneCallback,
                deleteDoneCallback);
    }

    /**
     * Open a standard dialog box for editing an information.
     * This function provides three callbacks. One is the standard saving callback that is verifying the validity of
     * the input data. Second, it is the standard validation callback that is saving the data after
     * turning on the validation flag by the local authority. Third is the standard deletion callback that invoked
     * after querying the user for accepting the deletion.
     *
     * @param <T>                the type of the edited entity.
     * @param saveTextPath       the key from the localized list of messages that must be used for the label of the "saving" button.
     * @param title              the title of the dialog.
     * @param content            the content of the dialog, where the editing fields are located.
     * @param mapEnterKeyToSave  if {@code true}, the "save" button is activated when the {@code Enter}
     *                           is pushed. If {@code false}, the {@code Enter} is not mapped to a component.
     * @param saveInDatabase     if {@code true}, the entity is saved in the database.
     * @param saveDoneCallback   the callback that is invoked after saving
     * @param deleteDoneCallback the callback that is invoked after deleting
     */
    public static <T extends IdentifiableEntity> void openEditionModalDialog(
            String title,
            String saveTextPath,
            AbstractEntityEditor<T> content,
            boolean mapEnterKeyToSave,
            boolean saveInDatabase,
            SerializableBiConsumer<Dialog, T> saveDoneCallback,
            SerializableBiConsumer<Dialog, T> deleteDoneCallback) {
        final SerializableBiConsumer<Dialog, T> validateCallback;
        if (content.isBaseAdmin()) {
            validateCallback = (dialog, entity) -> {
                if (content.isValidData()) {
                    final var similarityType = content.getEntityCreationStatus();
                    if (similarityType.isError()) {
                        content.notifySimilarityError(similarityType.getErrorMessage());
                    } else if (similarityType.isWarning()) {
                        content.notifySimilarityWarning(similarityType.getErrorMessage());
                    } else {
                        content.validateByOrganizationalStructureManager();
                    }
                } else {
                    content.notifyInvalidity();
                }
            };
        } else {
            validateCallback = null;
        }
        openEditionModalDialog(title, saveTextPath, content, mapEnterKeyToSave,
                saveInDatabase,
                saveDoneCallback,
                validateCallback,
                deleteDoneCallback);
    }

    /**
     * Open a standard dialog box for editing an information.
     * This function provides a standard saving callback that is verifying the validity of
     * the input data.
     *
     * @param <T>                the type of the edited entity.
     * @param title              the title of the dialog.
     * @param saveTextPath       the key from the localized list of messages that must be used for the label of the "saving" button.
     * @param content            the content of the dialog, where the editing fields are located.
     * @param mapEnterKeyToSave  if {@code true}, the "save" button is activated when the {@code Enter}
     *                           is pushed. If {@code false}, the {@code Enter} is not mapped to a component.
     * @param saveInDatabase     if {@code true}, the entity is saved in the database.
     * @param saveDoneCallback   the callback that is invoked after saving
     * @param validateCallback   the callback for validating the information.
     * @param deleteDoneCallback the callback that is invoked after deleting.
     */
    public static <T extends IdentifiableEntity> void openEditionModalDialog(
            String title,
            String saveTextPath,
            AbstractEntityEditor<T> content,
            boolean mapEnterKeyToSave,
            boolean saveInDatabase,
            SerializableBiConsumer<Dialog, T> saveDoneCallback,
            SerializableBiConsumer<Dialog, T> validateCallback,
            SerializableBiConsumer<Dialog, T> deleteDoneCallback) {
        final SerializableConsumer<Dialog> validateCallback0;
        if (validateCallback != null) {
            validateCallback0 = dialog -> validateCallback.accept(dialog, content.getEditedEntity());
        } else {
            validateCallback0 = null;
        }
        final SerializableConsumer<Dialog> deleteCallback = getDialogSerializableConsumer(content, deleteDoneCallback);
        doOpenEditionModalDialog(title, saveTextPath, content, mapEnterKeyToSave,
                dialog -> {
                    if (content.isValidData()) {
                        if (content.isNewEntity()) {
                            final var similarityType = content.getEntityCreationStatus();
                            if (similarityType.isError()) {
                                content.notifySimilarityError(similarityType.getErrorMessage());
                            } else if (similarityType.isWarning()) {
                                final var confirmDialog = newCriticalQuestionDialog(
                                        content.getTranslation("views.save.entity"), //$NON-NLS-1$
                                        similarityType.getErrorMessage(),
                                        content.getTranslation("views.save"), //$NON-NLS-1$
                                        content.getTranslation("views.save.cancel"), //$NON-NLS-1$
                                        event -> executeSaveProcess(content, saveInDatabase, saveDoneCallback, dialog));
                                confirmDialog.open();
                            } else {
                                executeSaveProcess(content, saveInDatabase, saveDoneCallback, dialog);
                            }
                        } else {
                            executeSaveProcess(content, saveInDatabase, saveDoneCallback, dialog);
                        }
                    } else {
                        content.notifyInvalidity();
                    }
                },
                validateCallback0,
                deleteCallback);
    }

    private static <T extends IdentifiableEntity> void executeSaveProcess(AbstractEntityEditor<T> content, boolean saveInDatabase, SerializableBiConsumer<Dialog, T> saveDoneCallback, Dialog dialog) {
        if (saveInDatabase) {
            if (content.save()) {
                dialog.close();
                if (saveDoneCallback != null) {
                    saveDoneCallback.accept(dialog, content.getEditedEntity());
                }
            }
        } else {
            dialog.close();
            if (saveDoneCallback != null) {
                saveDoneCallback.accept(dialog, content.getEditedEntity());
            }
        }
    }

    private static <T extends IdentifiableEntity> SerializableConsumer<Dialog> getDialogSerializableConsumer(AbstractEntityEditor<T> content, SerializableBiConsumer<Dialog, T> deleteDoneCallback) {
        final SerializableConsumer<Dialog> deleteCallback;
        if (deleteDoneCallback != null) {
            deleteCallback = dialog -> {
                final var confirmDialog = new ConfirmDialog();
                confirmDialog.setHeader(content.getTranslation("views.delete.entity")); //$NON-NLS-1$
                confirmDialog.setText(content.getTranslation("views.delete.entity.text")); //$NON-NLS-1$
                confirmDialog.setCancelable(true);
                confirmDialog.setConfirmText(content.getTranslation("views.delete")); //$NON-NLS-1$
                confirmDialog.setConfirmButtonTheme("error primary"); //$NON-NLS-1$
                confirmDialog.addConfirmListener(event -> {
                    if (content.delete()) {
                        dialog.close();
                        deleteDoneCallback.accept(dialog, content.getEditedEntity());
                    }
                });
                confirmDialog.open();
            };
        } else {
            deleteCallback = null;
        }
        return deleteCallback;
    }

    /**
     * Configure the given dialog to be a modal dialog with the given title.
     *
     * @param dialog       the dialog box to configure.
     * @param title        the title of the dialog box.
     * @param setFullWidth indicates if the dialog should cover the full width of the screen.
     * @since 4.0
     */
    public static void configureModalDialog(Dialog dialog, String title, boolean setFullWidth) {
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        if (setFullWidth) {
            dialog.setWidthFull();
        }
        dialog.setHeaderTitle(Strings.nullToEmpty(title));
    }

    /**
     * Open a standard dialog box for editing an information.
     *
     * @param title             the title of the dialog.
     * @param saveTextPath      the key from the localized list of messages that must be used for the label of the "saving" button.
     * @param content           the content of the dialog, where the editing fields are located.
     * @param mapEnterKeyToSave if {@code true}, the "save" button is activated when the {@code Enter}
     *                          is pushed. If {@code false}, the {@code Enter} is not mapped to a component.
     * @param saveCallback      the callback for saving the information.
     * @param validateCallback  the callback for validating the information.
     * @param deleteCallback    the callback for deleting the information.
     */
    public static void doOpenEditionModalDialog(String title, String saveTextPath, Component content, boolean mapEnterKeyToSave,
                                                SerializableConsumer<Dialog> saveCallback,
                                                SerializableConsumer<Dialog> validateCallback,
                                                SerializableConsumer<Dialog> deleteCallback) {
        final var dialog = new Dialog();
        configureModalDialog(dialog, title, true);
        dialog.add(content);

        final var saveButton = new Button(content.getTranslation(saveTextPath), e -> saveCallback.accept(dialog));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        if (mapEnterKeyToSave) {
            saveButton.addClickShortcut(Key.ENTER);
        }
        saveButton.setIcon(LineAwesomeIcon.SAVE_SOLID.create());

        final var cancelButton = new Button(content.getTranslation("views.cancel"), e -> dialog.close()); //$NON-NLS-1$

        final Button deletionButton;
        if (deleteCallback != null) {
            deletionButton = new Button(content.getTranslation("views.delete"), //$NON-NLS-1$
                    event -> deleteCallback.accept(dialog));
            deletionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            deletionButton.setIcon(LineAwesomeIcon.TRASH_SOLID.create());
        } else {
            deletionButton = null;
        }

        final Button validateButton;
        if (validateCallback != null) {
            validateButton = new Button(content.getTranslation("views.validate"), //$NON-NLS-1$
                    event -> validateCallback.accept(dialog));
            validateButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            validateButton.setIcon(LineAwesomeIcon.CHECK_DOUBLE_SOLID.create());
        } else {
            validateButton = null;
        }

        if (deletionButton != null) {
            if (validateButton != null) {
                dialog.getFooter().add(validateButton, cancelButton, deletionButton, saveButton);
            } else {
                dialog.getFooter().add(cancelButton, deletionButton, saveButton);
            }
        } else if (validateButton != null) {
            dialog.getFooter().add(validateButton, cancelButton, saveButton);
        } else {
            dialog.getFooter().add(cancelButton, saveButton);
        }

        dialog.open();
    }

    /**
     * Create a combo box that contains the countries.
     *
     * @param locale the locale for rendering the country names.
     * @return the combo box.
     */
    public static ComboBox<CountryCode> newCountryComboBox(Locale locale) {
        final var combo = new ComboBox<CountryCode>();
        combo.setItems(CountryCode.getAllDisplayCountries(locale));
        combo.setItemLabelGenerator(it -> getCountryLabelForCombo(it, locale));
        combo.setValue(CountryCode.getDefault());
        return combo;
    }

    /**
     * Update the items of the given combo box for displaying the country names according to the locale.
     *
     * @param combo  the combo box to update.
     * @param locale the locale for rendering the country names.
     */
    public static void updateCountryComboBoxItems(ComboBox<CountryCode> combo, Locale locale) {
        combo.setItemLabelGenerator(it -> getCountryLabelForCombo(it, locale));
    }

    private static String getCountryLabelForCombo(CountryCode country, Locale locale) {
        return country.getDisplayCountry(locale);
    }

    /**
     * Create a combo box that contains the languages.
     *
     * @param locale the locale for rendering the language names.
     * @return the combo box.
     */
    public static ComboBox<CountryCode> newLanguageComboBox(Locale locale) {
        final var combo = new ComboBox<CountryCode>();
        combo.setItems(CountryCode.getAllDisplayLanguages(locale));
        combo.setItemLabelGenerator(it -> getLanguageLabelForCombo(it, locale));
        combo.setValue(CountryCode.getDefault());
        return combo;
    }

    /**
     * Update the items of the given combo box for displaying the country names according to the locale.
     *
     * @param combo  the combo box to update.
     * @param locale the locale for rendering the country names.
     */
    public static void updateLanguageComboBoxItems(ComboBox<CountryCode> combo, Locale locale) {
        combo.setItemLabelGenerator(it -> getLanguageLabelForCombo(it, locale));
    }

    private static String getLanguageLabelForCombo(CountryCode country, Locale locale) {
        return StringUtils.capitalize(country.getDisplayLanguage(locale));
    }

    /**
     * Convert the given comparator to a serializable comparator.
     *
     * @param <T>        the type of data that is compared.
     * @param comparator the comparator to convert.
     * @return the serializable comparator.
     */
    public static <T> SerializableComparator<T> toSerializableComparator(Comparator<T> comparator) {
        if (comparator == null) {
            return null;
        }
        if (comparator instanceof SerializableComparator<T> cmp) {
            return cmp;
        }
        return (a, b) -> comparator.compare(a, b);
    }

    /**
     * Create the standard avatar item for the given person.
     *
     * @param person the person to show in the avatar item, never {@code null}.
     * @return the avatar item for the person.
     */
    public static AvatarItem newPersonAvatar(Person person) {
        return newPersonAvatar(person, null, null);
    }

    /**
     * Create the standard avatar item for the given person.
     *
     * @param person          the person to show in the avatar item, never {@code null}.
     * @param associatedUser  the user associated to the person, or {@code null}.
     * @param detailsProvider the provider of the details string from the user information, or {@code null}.
     * @return the avatar item for the person.
     */
    public static AvatarItem newPersonAvatar(Person person, User associatedUser, PersonDetailProvider detailsProvider) {
        assert person != null;

        final var fullName = person.getFullNameWithLastNameFirst();
        final var photo = person.getPhotoURL();

        String contactDetails = null;
        Integer avatarBorder = null;
        if (associatedUser != null) {
            final var login = associatedUser.getLogin();
            if (!Strings.isNullOrEmpty(login)) {
                final var role = associatedUser.getRole();
                avatarBorder = Integer.valueOf(role == UserRole.ADMIN ? ADMINISTRATOR_BORDER_COLOR : USER_BORDER_COLOR);
                if (detailsProvider != null) {
                    contactDetails = detailsProvider.getUserDetails(login, role);
                }
            }
        }

        final var avatar = new AvatarItem();
        avatar.setHeading(fullName);
        avatar.setAvatarBorderColor(avatarBorder);
        if (!Strings.isNullOrEmpty(contactDetails)) {
            avatar.setDescription(contactDetails);
        } else if (detailsProvider != null) {
            avatar.setDescription(Strings.emptyToNull(detailsProvider.getPersonDetails(person.getEmail())));
        } else {
            avatar.setDescription(Strings.emptyToNull(person.getEmail()));
        }
        if (photo != null) {
            avatar.setAvatarURL(photo.toExternalForm());
        }

        return avatar;
    }

    /**
     * Create the standard avatar item for the given organization, without the organization logo.
     *
     * @param organization the organization to show in the avatar item, never {@code null}.
     * @return the avatar item for the organization.
     */
    public static AvatarItem newOrganizationAvatar(ResearchOrganization organization) {
        return newOrganizationAvatar(organization, null);
    }

    /**
     * Create the standard avatar item for the given organization.
     *
     * @param organization the organization to show in the avatar item, never {@code null}.
     * @param fileManager  the manager of files that may be used for obtaining the organization logo in the avatar item.
     * @return the avatar item for the organization.
     */
    public static AvatarItem newOrganizationAvatar(ResearchOrganization organization, FileManager fileManager) {
        assert organization != null;

        final var acronym = organization.getAcronym();
        final var name = organization.getName();
        final var logo = organization.getPathToLogo();
        final var identifier = organization.getNationalIdentifier();
        final var rnsr = organization.getRnsr();

        final var details = new StringBuilder();
        if (!Strings.isNullOrEmpty(acronym)) {
            details.append(acronym);
        }
        if (!Strings.isNullOrEmpty(identifier)) {
            if (details.length() > 0) {
                details.append(' ');
            }
            details.append(identifier);
        }
        if (!Strings.isNullOrEmpty(rnsr)) {
            if (details.length() > 0) {
                details.append(" - "); //$NON-NLS-1$
            }
            details.append("RNSR ").append(rnsr); //$NON-NLS-1$
        }

        final var avatar = new AvatarItem();
        avatar.setHeading(name);
        if (details.length() > 0) {
            avatar.setDescription(details.toString());
        }
        if (organization.isMajorOrganization()) {
            avatar.setAvatarBorderColor(Integer.valueOf(3));
        }
        if (fileManager != null) {
            var logoFile = FileSystem.convertStringToFile(logo);
            if (logoFile != null) {
                logoFile = fileManager.normalizeForServerSide(logoFile);
                if (logoFile != null) {
                    avatar.setAvatarResource(ComponentFactory.newStreamImage(logoFile));
                }
            }
        }
        return avatar;
    }

    /**
     * Create the standard avatar item for the given membership.
     *
     * @param membership the membership to show in the avatar item, never {@code null}.
     * @param messages   the accessor to the localized messages.
     * @param locale     the locale to use for building the details.
     * @return the avatar item for the membership.
     */
    public static AvatarItem newMembershipAvatar(Membership membership, MessageSourceAccessor messages, Locale locale) {
        return newMembershipAvatar(membership, messages, locale, null);
    }

    /**
     * Create the standard avatar item for the given membership.
     *
     * @param membership      the membership to show in the avatar item, never {@code null}.
     * @param detailsProvider the provider of the details string from the membership information, never {@code null}.
     * @return the avatar item for the membership.
     */
    public static AvatarItem newMembershipAvatar(Membership membership, Function<Membership, String> detailsProvider) {
        assert detailsProvider != null;
        return newMembershipAvatar(membership, null, null, detailsProvider);
    }

    /**
     * Create the standard avatar item for the given membership.
     *
     * @param membership      the membership to show in the avatar item, never {@code null}.
     * @param messages        the accessor to the localized messages.
     * @param locale          the locale to use for building the details.
     * @param detailsProvider the provider of the details string from the membership information, or {@code null}.
     * @return the avatar item for the membership.
     */
    private static AvatarItem newMembershipAvatar(Membership membership, MessageSourceAccessor messages, Locale locale, Function<Membership, String> detailsProvider) {
        assert membership != null;

        final var person = membership.getPerson();
        final var fullName = person.getFullNameWithLastNameFirst();
        final var photo = person.getPhotoURL();

        final String details;
        if (detailsProvider != null) {
            details = detailsProvider.apply(membership);
        } else {
            final StringBuilder label = new StringBuilder();
            final var gender = person.getGender();
            final var status = membership.getMemberStatus().getLabel(messages, gender, false, locale);
            label.append(status);
            final var organization = membership.getDirectResearchOrganization().getAcronymOrName();
            if (!Strings.isNullOrEmpty(organization)) {
                if (label.length() > 0) {
                    label.append(" - "); //$NON-NLS-1$
                }
                label.append(organization);
            }
            details = label.toString();
        }

        final var avatar = new AvatarItem();
        avatar.setHeading(fullName);
        avatar.setDescription(Strings.emptyToNull(details));
        if (photo != null) {
            avatar.setAvatarURL(photo.toExternalForm());
        }

        return avatar;
    }

    /**
     * Replies the default label of a membership.
     *
     * @param membership the membership to show in the avatar item, never {@code null}.
     * @param messages   the accessor to the localized messages.
     * @param locale     the locale to use for building the details.
     * @return the avatar item for the membership.
     */
    public static String newMembershipLabel(Membership membership, MessageSourceAccessor messages, Locale locale) {
        assert membership != null;

        final var person = membership.getPerson();

        final StringBuilder label = new StringBuilder();

        final var fullName = person.getFullNameWithLastNameFirst();
        label.append(fullName);

        final var gender = person.getGender();
        final var status = membership.getMemberStatus().getLabel(messages, gender, false, locale);
        if (label.length() > 0) {
            label.append(" - "); //$NON-NLS-1$
        }
        label.append(status);

        final var organization = membership.getDirectResearchOrganization().getAcronymOrName();
        if (!Strings.isNullOrEmpty(organization)) {
            if (label.length() > 0) {
                label.append(" - "); //$NON-NLS-1$
            }
            label.append(organization);
        }

        return label.toString();
    }

    /**
     * Create the standard avatar item for the given conference, without the year.
     *
     * @param conference the conference to show in the avatar item, never {@code null}.
     * @return the avatar item for the conference.
     */
    public static AvatarItem newConferenceAvatar(Conference conference) {
        return newConferenceAvatar(conference, 0);
    }

    /**
     * Create the standard avatar item for the given conference.
     *
     * @param conference the conference to show in the avatar item, never {@code null}.
     * @param year       the year of the conference.
     * @return the avatar item for the conference.
     */
    public static AvatarItem newConferenceAvatar(Conference conference, int year) {
        assert conference != null;

        final var acronym = conference.getAcronym();
        final var name = conference.getName();

        final var details = new StringBuilder();
        if (!Strings.isNullOrEmpty(acronym)) {
            details.append(acronym);
        }
        if (year > 0) {
            details.append("-").append(year); //$NON-NLS-1$
        }

        final var avatar = new AvatarItem();
        avatar.setHeading(name);
        if (details.length() > 0) {
            avatar.setDescription(details.toString());
        }
        return avatar;
    }

    /**
     * Create the standard avatar item for the given journal, without the year.
     *
     * @param journal the journal to show in the avatar item, never {@code null}.
     * @return the avatar item for the journal.
     */
    public static AvatarItem newJournalAvatar(Journal journal) {
        assert journal != null;

        final var name = journal.getJournalName();
        final var publisher = journal.getPublisher();
        final var issn = journal.getISSN();
        final var isbn = journal.getISBN();

        final var details = new StringBuilder();
        if (!Strings.isNullOrEmpty(publisher)) {
            details.append(publisher);
        }
        if (!Strings.isNullOrEmpty(issn)) {
            if (details.length() > 0) {
                details.append(" - "); //$NON-NLS-1$
            }
            details.append(issn);
        } else if (!Strings.isNullOrEmpty(isbn)) {
            if (details.length() > 0) {
                details.append(" - "); //$NON-NLS-1$
            }
            details.append(isbn);
        }

        final var avatar = new AvatarItem();
        avatar.setHeading(name);
        if (details.length() > 0) {
            avatar.setDescription(details.toString());
        }
        return avatar;
    }

    /**
     * Create the standard avatar item for the given project.
     *
     * @param project the project to show in the avatar item, never {@code null}.
     * @return the avatar item for the project.
     */
    public static AvatarItem newProjectAvatar(Project project) {
        return newProjectAvatar(project, null);
    }

    /**
     * Create the standard avatar item for the given project.
     *
     * @param project     the project to show in the avatar item, never {@code null}.
     * @param fileManager the accessor to the files that are stored on the server.
     * @return the avatar item for the project.
     */
    public static AvatarItem newProjectAvatar(Project project, FileManager fileManager) {
        final var acronym = project.getAcronym();
        final var logo = project.getPathToLogo();

        final var avatar = new AvatarItem();
        avatar.setHeading(acronym);
        var emptyLogo = true;
        if (fileManager != null) {
            var logoFile = FileSystem.convertStringToFile(logo);
            if (logoFile != null) {
                logoFile = fileManager.normalizeForServerSide(logoFile);
                if (logoFile != null) {
                    avatar.setAvatarResource(ComponentFactory.newStreamImage(logoFile));
                    emptyLogo = false;
                }
            }
        }
        if (emptyLogo) {
            avatar.setAvatarResource(ComponentFactory.newEmptyBackgroundStreamImage());
        }
        return avatar;
    }

    private static List<StringBuilder> buildCases(String filter) {
        final var allCases = new ArrayList<StringBuilder>();
        for (final var filterItem : filter.split("[ \n\r\t\f%]+")) { //$NON-NLS-1$
            final var filter0 = new StringBuilder(FOR_MANY);
            var normedFilter0 = NORMALIZER.normalize(filterItem);
            normedFilter0 = normedFilter0.toLowerCase();
            final var matcher = PATTERN.matcher(normedFilter0);
            normedFilter0 = matcher.replaceAll(FOR_ONE);
            filter0.append(normedFilter0);
            filter0.append(FOR_MANY);
            allCases.add(filter0);
        }
        return allCases;
    }

    /**
     * Create a collection of predicate that matches the given keywords.
     * The keywords are considered separately as separated words (separator
     * is a spacing character) and each word may be part of a larger value.
     *
     * @param <T>             the type of entity.
     * @param keywords        the keywords to match.
     * @param root            the root element that must be used for the query.
     * @param query           the query.
     * @param criteriaBuilder the builder of criteria component.
     * @param filterBuilder   the builder of the filter query. First argument is the query keyword.
     *                        Second argument is the list of predicates to fill up, assuming that they are merged with
     *                        "or". Third argument is {@code root}. Fourth argument is {@code critriaBuilder}.
     * @return the predicate for the query.
     */
    public static <T> Predicate newPredicateContainsOneOf(String keywords, Root<T> root, CriteriaQuery<?> query,
                                                          CriteriaBuilder criteriaBuilder, HqlQueryFilterBuilder<T> filterBuilder) {
        assert filterBuilder != null;
        final var kws = Strings.nullToEmpty(keywords).trim();
        if (!Strings.isNullOrEmpty(kws)) {
            final var cases = buildCases(kws);
            final var predicates = new ArrayList<Predicate>();
            for (final var acase : cases) {
                final var predicates0 = new ArrayList<Predicate>();
                filterBuilder.buildQueryFor(acase.toString(), predicates0, root, criteriaBuilder);
                predicates.add(criteriaBuilder.or(predicates0.toArray(new Predicate[predicates0.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }
        return null;
    }

    /**
     * Show an error notification with the given message.
     *
     * @param message the message to show up.
     * @return the notification object.
     */
    public static Notification showErrorNotification(String message) {
        final var notification = new Notification(message, ViewConstants.DEFAULT_POPUP_DURATION, ViewConstants.DEFAULT_POPUP_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
        return notification;
    }

    /**
     * Show a warning notification with the given message.
     *
     * @param message the message to show up.
     * @return the notification object.
     */
    public static Notification showWarningNotification(String message) {
        final var notification = new Notification(message, ViewConstants.DEFAULT_POPUP_DURATION, ViewConstants.DEFAULT_POPUP_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        notification.open();
        return notification;
    }

    /**
     * Show a success notification with the given message.
     *
     * @param message the message to show up.
     * @return the notification object.
     */
    public static Notification showSuccessNotification(String message) {
        final var notification = new Notification(message, ViewConstants.DEFAULT_POPUP_DURATION, ViewConstants.DEFAULT_POPUP_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
        return notification;
    }

    /**
     * Show an information notification with the given message.
     *
     * @param message the message to show up.
     * @return the notification object.
     */
    public static Notification showInfoNotification(String message) {
        final var notification = new Notification(message, ViewConstants.DEFAULT_POPUP_DURATION, ViewConstants.DEFAULT_POPUP_POSITION);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        notification.open();
        return notification;
    }

    /**
     * Replies the locale that is defined by Vaadin app.
     *
     * @return the locale.
     * @see #getTranslation(String, Object...)
     * @see #getTranslation(Locale, String, Object...)
     */
    public static Locale getLocale() {
        return LocaleUtil.getLocale(LocaleUtil::getI18NProvider);
    }

    /**
     * Get the translation for the locale.
     *
     * <p>The method never returns a null. If there is no {@link I18NProvider}
     * available or no translation for the {@code key} it returns an exception
     * string e.g. '!{key}!'.
     *
     * @param key        translation key.
     * @param parameters the parameters used in translation string.
     * @return translation text.
     * @see #getLocale()
     * @see #getTranslation(Locale, String, Object...)
     */
    public static String getTranslation(String key, Object... parameters) {
        final var i18NProvider = LocaleUtil.getI18NProvider();
        return i18NProvider.map(i18n -> i18n.getTranslation(key, LocaleUtil.getLocale(() -> i18NProvider), parameters)).orElseGet(() -> "!{" + key + "}!"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Get the translation for the locale.
     *
     * <p>The method never returns a null. If there is no {@link I18NProvider}
     * available or no translation for the {@code key} it returns an exception
     * string e.g. '!{key}!'.
     *
     * @param locale     the locale to be used for the message.
     * @param key        translation key.
     * @param parameters the parameters used in translation string.
     * @return translation text.
     * @see #getLocale()
     * @see #getTranslation(String, Object...)
     */
    public static String getTranslation(Locale locale, String key, Object... parameters) {
        final var i18NProvider = LocaleUtil.getI18NProvider();
        return i18NProvider.map(i18n -> i18n.getTranslation(key, locale, parameters)).orElseGet(() -> "!{" + key + "}!"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Builder of a HQL query filter.
     *
     * @param <T> the type of entity.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public interface HqlQueryFilterBuilder<T> extends Serializable {

        /**
         * Build the HQL query for the filtering.
         *
         * @param keyword         the keyword to search for.
         * @param predicates      the list of filtering criteria with "or" semantic, being filled by this function.
         * @param root            the root not for the search.
         * @param criteriaBuilder the criteria builder. It is the Hibernate version in order to
         *                        have access to extra functions, e.g. {@code collate}.
         */
        void buildQueryFor(String keyword, List<Predicate> predicates, Root<T> root, CriteriaBuilder criteriaBuilder);

    }

    /**
     * Provider of details for a person's avatar item.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    @FunctionalInterface
    public interface PersonDetailProvider extends Serializable {

        /**
         * Replies the details string.
         *
         * @param login the login of the user.
         * @param role  the role of the user.
         * @return the details string.
         */
        String getUserDetails(String login, UserRole role);

        /**
         * Replies the details string.
         *
         * @param email the email of the person
         * @return the details string.
         */
        default String getPersonDetails(String email) {
            return Strings.emptyToNull(email);
        }

    }

}
