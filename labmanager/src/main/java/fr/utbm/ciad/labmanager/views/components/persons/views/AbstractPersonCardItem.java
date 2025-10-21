package fr.utbm.ciad.labmanager.views.components.persons.views;

import com.google.common.base.Strings;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.entities.PersonCardDataProvider;

import java.util.function.Function;

/**
 * Abstract Vaadin item for showing person's information in a virtual card that could be included in a grid of cards.
 *
 * @param <T> the type of the entity that is associated to this card.
 * @author $Author: callaire$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPersonCardItem<T> extends ListItem {

    private static final long serialVersionUID = 7018402595460195576L;

    private final PersonCardDataProvider<T> dataProvider;

    private final boolean hasEmail;

    private final boolean hasMobilePhone;

    private final boolean hasOfficePhone;

    private final boolean hasOfficeRoom;

    /**
     * Construct the card item with the given builder.
     *
     * @param provider       the tools for obtaining the data and build the card.
     * @param hasEmail       indicates if the email information is shown in the card.
     * @param hasMobilePhone indicates if the mobile phone information is shown in the card.
     * @param hasOfficePhone indicates if the office phone information is shown in the card.
     * @param hasOfficeRoom  indicates if the office room information is shown in the card.
     */
    public AbstractPersonCardItem(PersonCardDataProvider<T> provider, boolean hasEmail, boolean hasMobilePhone, boolean hasOfficePhone, boolean hasOfficeRoom) {
        this.dataProvider = provider;
        this.hasEmail = hasEmail;
        this.hasMobilePhone = hasMobilePhone;
        this.hasOfficePhone = hasOfficePhone;
        this.hasOfficeRoom = hasOfficeRoom;
        buildCard();
    }

    private static <T, D> D nullSafe(PersonCardDataProvider<T> dataProvider, D defaultData, Function<PersonCardDataProvider<T>, D> callback) {
        if (dataProvider != null) {
            return callback.apply(dataProvider);
        }
        return defaultData;
    }

    /**
     * Create a single info item with an icon and text
     *
     * @param iconType The icon to display
     * @param info     The text to display
     * @return A Div element containing the icon and text
     */
    private static Div createInfoItem(VaadinIcon iconType, String info) {
        final var infoItem = new Div();
        infoItem.addClassNames(Display.FLEX, AlignItems.CENTER, Margin.Bottom.SMALL);
        //infoItem.getStyle().set("height", "20px"); //$NON-NLS-1$ //$NON-NLS-2$

        final var icon = iconType.create();
        icon.setSize(ViewConstants.ICON_SIZE_STR);
        icon.addClassNames(Margin.Right.SMALL);

        final var infoText = new Span(!Strings.isNullOrEmpty(info) ? info : " "); //$NON-NLS-1$
        infoText.addClassNames(FontSize.SMALL);

        infoItem.add(icon, infoText);
        return infoItem;
    }

    /**
     * Update the card content.
     */
    public void updateCard() {
        removeAll();
        buildCard();
    }

    /**
     * Handle the click event on the card.
     *
     * @param entity the entity associated with the card.
     */
    protected abstract void onClickEvent(T entity);

    /**
     * Build the card with the provided data.
     */
    private void buildCard() {
        getStyle().set("cursor", "pointer"); //$NON-NLS-1$ //$NON-NLS-2$
        addClassNames(
                Background.CONTRAST_5,
                Display.FLEX,
                FlexDirection.COLUMN,
                AlignItems.START,
                Padding.MEDIUM,
                BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL);

        // Setup the image
        final var divImage = new Div();
        divImage.addClassNames(
                Display.FLEX,
                AlignItems.CENTER,
                JustifyContent.CENTER,
                Margin.Bottom.MEDIUM,
                Overflow.HIDDEN,
                BorderRadius.MEDIUM,
                Width.FULL);
        divImage.setHeight("0"); //$NON-NLS-1$
        divImage.getStyle().set("padding-top", "56.25%"); //$NON-NLS-1$ //$NON-NLS-2$
        divImage.getStyle().set("position", "relative"); //$NON-NLS-1$ //$NON-NLS-2$

        final var imageResource = nullSafe(this.dataProvider, null, it -> it.getPhotoResource());
        final var imageUrl = nullSafe(this.dataProvider, null, it -> it.getPhotoUrl());
        if (!Strings.isNullOrEmpty(imageUrl) || imageResource != null) {
            final var image = new Image();
            image.getStyle()
                    .set("display", "block") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("position", "absolute") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("top", "0") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("left", "0") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("width", "100%") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("height", "100%") //$NON-NLS-1$ //$NON-NLS-2$
                    .set("object-fit", "cover"); //$NON-NLS-1$ //$NON-NLS-2$
            if (imageResource != null) {
                image.setSrc(imageResource);
            } else {
                image.setSrc(imageUrl);
            }
            divImage.add(image);
        } else {
            Icon userIcon = VaadinIcon.USER.create();
            userIcon.setSize("100px"); //$NON-NLS-1$
            userIcon.getStyle().set("position", "absolute"); //$NON-NLS-1$ //$NON-NLS-2$
            userIcon.getStyle().set("top", "50%"); //$NON-NLS-1$ //$NON-NLS-2$
            userIcon.getStyle().set("left", "50%"); //$NON-NLS-1$ //$NON-NLS-2$
            userIcon.getStyle().set("transform", "translate(-50%, -50%)"); //$NON-NLS-1$ //$NON-NLS-2$
            divImage.add(userIcon);
        }

        // Setup the header
        final var headerDiv = new Div();
        headerDiv.setWidth("100%"); //$NON-NLS-1$
        headerDiv.addClassName(Margin.Bottom.NONE);
        headerDiv.getStyle()
                .set("max-height", "40px") //$NON-NLS-1$ //$NON-NLS-2$
                .set("overflow", "hidden") //$NON-NLS-1$ //$NON-NLS-2$
                .set("text-overflow", "ellipsis"); //$NON-NLS-1$ //$NON-NLS-2$
        final var header = new Span(nullSafe(this.dataProvider, "", it -> it.getName())); //$NON-NLS-1$
        header.addClassNames(FontSize.MEDIUM, FontWeight.SEMIBOLD);
        headerDiv.add(header);

        // Setup the subtitle
        final var subtitleDiv = new Div();
        subtitleDiv.setWidth("100%"); //$NON-NLS-1$
        subtitleDiv.addClassNames(Margin.Bottom.SMALL, Margin.Top.NONE);
        final var subtitle = new Span();
        final var role = nullSafe(this.dataProvider, null, it -> it.getRole());
        if (!Strings.isNullOrEmpty(role)) {
            subtitle.setText(role);
        } else {
            subtitle.setText(getTranslation("views.persons.no_role")); //$NON-NLS-1$
        }
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitleDiv.add(subtitle);

        final var usefulInfoDiv = new Div();
        usefulInfoDiv.setWidth("100%"); //$NON-NLS-1$
        usefulInfoDiv.addClassNames(Margin.Bottom.MEDIUM);
        if (this.hasEmail) {
            usefulInfoDiv.add(createInfoItem(VaadinIcon.ENVELOPE, Strings.nullToEmpty(
                    nullSafe(this.dataProvider, null, it -> it.getEmail()))));
        }
        if (this.hasMobilePhone) {
            usefulInfoDiv.add(createInfoItem(VaadinIcon.MOBILE, Strings.nullToEmpty(
                    nullSafe(this.dataProvider, null, it -> it.getMobilePhone()))));
        }
        if (this.hasOfficePhone) {
            usefulInfoDiv.add(createInfoItem(VaadinIcon.PHONE, Strings.nullToEmpty(
                    nullSafe(this.dataProvider, null, it -> it.getOfficePhone()))));
        }
        if (this.hasOfficeRoom) {
            usefulInfoDiv.add(createInfoItem(VaadinIcon.OFFICE, Strings.nullToEmpty(
                    nullSafe(this.dataProvider, null, it -> it.getOfficeRoom()))));
        }

        add(divImage, headerDiv, subtitleDiv, usefulInfoDiv);

        // Setup the labels
        final var labels = nullSafe(this.dataProvider, null, it -> it.getLabels());
        if (labels != null) {
            final var iterator = labels.iterator();
            if (iterator.hasNext()) {
                final var badgesLayout = new HorizontalLayout();
                badgesLayout.addClassNames(Margin.Top.MEDIUM);
                while (iterator.hasNext()) {
                    final var badge = iterator.next();
                    badgesLayout.add(badge);
                }
                add(badgesLayout);
            }
        }

        final var associatedEntity = nullSafe(this.dataProvider, null, it -> it.getEntity());
        if (associatedEntity != null) {
            getElement().addEventListener("click", event -> onClickEvent(associatedEntity)); //$NON-NLS-1$
        }
    }

}
