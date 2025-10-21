package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.AbstractStreamResource;

/**
 * Tool for building the specification of a person's card.
 *
 * @param <T> the type of the JPA entity that is associated to the card.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public interface PersonCardDataProvider<T> {

    /**
     * Replies the resource of the photo. This function may returns {@code null} if there is no resource or the URL of the photo is provided by {@link #getPhotoUrl()}.
     *
     * @return the resource of {@code null} if there is no photo resource.
     */
    AbstractStreamResource getPhotoResource();

    /**
     * Replies the URL of the photo. This function may returns {@code null} if there is no URL or the resource of the photo is provided by {@link #getResourceUrl()}.
     *
     * @return the URL of {@code null} if there is no photo URL.
     */
    String getPhotoUrl();

    /**
     * Replies the name of the person.
     *
     * @return the name of the person.
     */
    String getName();

    /**
     * Replies the role of the person.
     *
     * @return the role of the person.
     */
    String getRole();

    /**
     * Replies the email of the person.
     *
     * @return the email of the person.
     */
    String getEmail();

    /**
     * Replies the mobile phone of the person.
     *
     * @return the phone number.
     */
    String getMobilePhone();

    /**
     * Replies the office phone of the person.
     *
     * @return the phone number.
     */
    String getOfficePhone();

    /**
     * Replies the office room number of the person.
     *
     * @return the room number.
     */
    String getOfficeRoom();

    /**
     * Replies the labels associated to the person.
     *
     * @return the labels.
     */
    Iterable<Span> getLabels();

    /**
     * Replies the entity that is associated to the card.
     *
     * @return the entity.
     */
    T getEntity();

}
