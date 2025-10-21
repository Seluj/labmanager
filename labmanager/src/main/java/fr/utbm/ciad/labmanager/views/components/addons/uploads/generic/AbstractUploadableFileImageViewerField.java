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

package fr.utbm.ciad.labmanager.views.components.addons.uploads.generic;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.slf4j.Logger;

/**
 * A field that enables to upload a file and show an image representation of the uploaded file.
 * This field does not assume that the field's data is of a specific type.
 * Subclasses must implement function to handle the upload file data.
 *
 * @param <T> the type of the values managed by this field.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractUploadableFileImageViewerField<T> extends AbstractUploadableFileField<T> implements HasClearButton {

    private static final long serialVersionUID = 3100784001699040679L;

    private static final int DEFAULT_IMAGE_SIZE = 100;

    private final Image image;

    private final Button clearButton;

    /**
     * Default constructor.
     *
     * @param loggerSupplier the dynamic supplier of the loggers.
     */
    public AbstractUploadableFileImageViewerField(SerializableSupplier<Logger> loggerSupplier) {
        super(loggerSupplier);

        this.image = new Image();
        this.image.setVisible(true);
        setImageSize(DEFAULT_IMAGE_SIZE, Unit.PIXELS);

        this.clearButton = new Button("", it -> onImageCleared()); //$NON-NLS-1$
        this.clearButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        this.clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.clearButton.setEnabled(false);
        this.clearButton.setVisible(false);

        final var layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.setAlignItems(Alignment.START);
        layout.add(this.image, this.clearButton);

        getContent().addComponentAsFirst(layout);

        updateImage(this.image, this.clearButton);
    }

    @Override
    public boolean isClearButtonVisible() {
        return this.clearButton.isVisible();
    }

    @Override
    public void setClearButtonVisible(boolean clearButtonVisible) {
        this.clearButton.setVisible(true);
    }

    /**
     * Invoked to clear all UI components that are not the uploader itself.
     *
     * @see #resetUploader()
     */
    protected void resetUi() {
        final var source = ComponentFactory.newEmptyBackgroundStreamImage();
        this.clearButton.setEnabled(false);
        this.image.setSrc(source);
    }

    /**
     * Invoked to clear all internal properties for resetting.
     */
    protected void resetProperties() {
        //
    }

    /**
     * Change the size of the image on the viewer.
     *
     * @param size the size of the image.
     * @param unit the unit.
     */
    public void setImageSize(int size, Unit unit) {
        this.image.setMinHeight(size, unit);
        this.image.setMaxHeight(size, unit);
    }

    /**
     * Invoked to change the image in the viewer.
     *
     * @param viewer      the receiver of the image.
     * @param clearButton the clear button.
     */
    protected void updateImage(Image viewer, Button clearButton) {
        final var buffer = getMemoryReceiver();
        final StreamResource source;
        if (buffer != null && buffer.hasFileData()) {
            source = buffer.createStreamResource();
            clearButton.setEnabled(true);
        } else {
            source = ComponentFactory.newEmptyBackgroundStreamImage();
            clearButton.setEnabled(false);
        }
        viewer.setSrc(source);
    }

    @Override
    protected void uploadSucceeded(String filename) {
        updateImage(this.image, this.clearButton);
    }

    @Override
    protected void uploadFailed(String filename, Throwable error) {
        resetProperties();
        resetUi();
    }

    private void onImageCleared() {
        resetUploader();
        resetProperties();
        resetUi();
        imageCleared();
    }

    /**
     * Invoked when the image is cleared.
     */
    protected void imageCleared() {
        //
    }

    /**
     * Change the image that is displayed in the viewer.
     *
     * @param stream the stream to the resource.
     */
    protected void setImageSource(AbstractStreamResource stream) {
        this.image.setSrc(stream);
        this.clearButton.setEnabled(true);
    }

}