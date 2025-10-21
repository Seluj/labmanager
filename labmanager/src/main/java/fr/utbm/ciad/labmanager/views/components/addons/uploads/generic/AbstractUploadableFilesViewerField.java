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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.StreamResource;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * A field that enables to upload a file and show the image representations of the uploaded files.
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
public abstract class AbstractUploadableFilesViewerField<T> extends AbstractUploadableFilesField<T> {

    private static final long serialVersionUID = -8187059598900862475L;

    private final EmptySetMultiSelectListBox list;

    private final Button clearButton;

    private final SerializableBiFunction<Integer, String, File> filenameSupplier;

    /**
     * Constructor with an image renderer. The rendering of the image is based on
     * the function {@link #renderImage(Object)}.
     *
     * @param filenameSupplier the supplier of the filenames. First argument is the index of the image.
     *                         Second argument is the filename extension. It returns the filename.
     * @param loggerSupplier   the dynamic supplier of the loggers.
     */
    public AbstractUploadableFilesViewerField(SerializableBiFunction<Integer, String, File> filenameSupplier,
                                              SerializableSupplier<Logger> loggerSupplier) {
        super(loggerSupplier);

        this.filenameSupplier = filenameSupplier;

        this.list = new EmptySetMultiSelectListBox();

        this.clearButton = new Button("", it -> thumbnailRemoved()); //$NON-NLS-1$
        this.clearButton.setIcon(VaadinIcon.CLOSE_SMALL.create());
        this.clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.clearButton.setEnabled(false);
        this.clearButton.setVisible(false);

        final var layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.setAlignItems(Alignment.START);
        layout.add(this.list.withScroller(), this.clearButton);

        getContent().addComponentAsFirst(layout);

        this.list.addSelectionListener(it -> {
            final var active = !it.getAllSelectedItems().isEmpty();
            this.clearButton.setEnabled(active);
            this.clearButton.setVisible(active);
        });
    }

    /**
     * Invoked when the list of thumbnail should be changed by removing items.
     */
    protected void thumbnailRemoved() {
        final Thumbnail[] thumbnails;
        synchronized (this.list) {
            thumbnails = this.list.getSelectedItems().toArray(size -> new Thumbnail[size]);
        }
        for (final var thumbnail : thumbnails) {
            removeThumbnail(thumbnail);
        }
        this.clearButton.setEnabled(false);
    }

    @Override
    protected void uploadSucceeded(String filename) {
        final var buffer = getUploadMemoryBufferFor(filename);
        synchronized (this.list) {
            final var index = this.list.getListDataView().getItemCount();
            final var publicFilename = this.filenameSupplier.apply(Integer.valueOf(index), buffer.getFileName());
            final var thumbnail = new Thumbnail(publicFilename, buffer);
            addThumbnail(thumbnail);
        }
        resetUploadMemoryBuffer(filename, false);
        super.uploadSucceeded(filename);
    }

    /**
     * Replies the thumbnails.
     *
     * @return the thumbnails
     */
    protected Stream<Thumbnail> getThumbnails() {
        synchronized (this.list) {
            return this.list.getListDataView().getItems();
        }
    }

    /**
     * Remove all the thumbnails.
     */
    protected void removeAllThumbnails() {
        synchronized (this.list) {
            this.list.clear();
        }
    }

    /**
     * Add a thumbnail in the component.
     *
     * @param thumbnail the new thumbnail.
     */
    protected void addThumbnail(Thumbnail thumbnail) {
        synchronized (this.list) {
            this.list.addEntity(thumbnail);
        }
    }

    /**
     * Remove a thumbnail from the component.
     *
     * @param thumbnail the thumbnail to be removed.
     */
    protected void removeThumbnail(Thumbnail thumbnail) {
        synchronized (this.list) {
            this.list.removeEntity(thumbnail);
        }
    }

    /**
     * Data in the thumbnail viewer.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public static class Thumbnail implements Serializable, Comparable<Thumbnail> {

        private static final long serialVersionUID = -8602109561728702324L;

        private static final int THUMBNAIL_WIDTH = 256;

        private static final int THUMBNAIL_HEIGHT = 128;

        private final ResetableMemoryBuffer buffer;

        private final String sortName;

        private final String filename;

        private Image image;

        /**
         * Constructor with file data in memory, usually during upload of a new file.
         *
         * @param filename the filename of the entity fro the JPA server.
         * @param buffer   the buffer to put in the thumbnail.
         */
        public Thumbnail(File filename, ResetableMemoryBuffer buffer) {
            this.buffer = buffer;
            this.sortName = filename.getPath();
            this.filename = filename.getPath();
            this.image = renderImage(buffer);
        }

        /**
         * Constructor with file data already on the server.
         *
         * @param filename       the filename of the entity fro the JPA server.
         * @param serverFilename the filename of the entity on the server.
         */
        public Thumbnail(File filename, File serverFilename) {
            this.buffer = null;
            this.sortName = filename.getPath();
            this.filename = filename.getPath();
            this.image = renderImage(serverFilename);
        }

        /**
         * Replies the memory buffer if it exists. The memory buffer indicates that a file was
         * not saved on the server already.
         *
         * @return the memory buffer or {@code null} if there is no buffer in memory.
         */
        public ResetableMemoryBuffer getMemoryBuffer() {
            return this.buffer;
        }

        @Override
        public int compareTo(Thumbnail o) {
            return this.sortName.compareTo(o.sortName);
        }

        /**
         * Render the image in the list.
         *
         * @param entityBuffer the buffer for the uploaded entity.
         * @return the image component.
         */
        @SuppressWarnings("static-method")
        protected Image renderImage(ResetableMemoryBuffer entityBuffer) {
            final var image = new Image();
            final StreamResource source;
            if (entityBuffer != null && entityBuffer.hasFileData()) {
                source = entityBuffer.createStreamResource();
            } else {
                source = ComponentFactory.newEmptyBackgroundStreamImage();
            }
            image.setSrc(source);
            image.setMaxHeight(THUMBNAIL_HEIGHT, Unit.PIXELS);
            image.setMaxWidth(THUMBNAIL_WIDTH, Unit.PIXELS);
            return image;
        }

        /**
         * Render the image in the list.
         *
         * @param filename the filename on the server.
         * @return the image component.
         */
        @SuppressWarnings("static-method")
        protected Image renderImage(File filename) {
            final var image = new Image();
            final StreamResource source = ComponentFactory.newStreamImage(filename);
            image.setSrc(source);
            image.setMaxHeight(THUMBNAIL_HEIGHT, Unit.PIXELS);
            image.setMaxWidth(THUMBNAIL_WIDTH, Unit.PIXELS);
            return image;
        }

        /**
         * Replies the image of the thumbnail.
         *
         * @return the image.
         */
        public Image getImage() {
            return this.image;
        }

        /**
         * Replies the filename in the thumbnail.
         *
         * @return the filename.
         */
        public String getName() {
            return this.filename;
        }

        /**
         * Change the filename in the thumbnail.
         *
         * @param serverFilename the filename of the entity on the server.
         */
        public void setImageFromServerFilename(File serverFilename) {
            this.image = renderImage(serverFilename);
        }

    }

    /**
     * Multi-selection list with empty set as empty value.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private static class EmptySetMultiSelectListBox extends MultiSelectListBox<Thumbnail> {

        private static final long serialVersionUID = 1019082190432979756L;

        private Set<Thumbnail> dataStorage;

        private ListDataProvider<Thumbnail> dataProvider;

        /**
         * Constructor.
         */
        EmptySetMultiSelectListBox() {
            getStyle().setBackground("var(--lumo-shade-5pct)"); //$NON-NLS-1$
            setRenderer(new ComponentRenderer<>(this::renderThumbnail));
            //
            setEntities(null);
        }

        private Component renderThumbnail(Thumbnail thumbnail) {
            if (thumbnail != null) {
                final var image = thumbnail.getImage();
                if (image != null) {
                    return image;
                }
            }
            return new Span();
        }

        /**
         * Return the list inside a scroller.
         *
         * @return the scroller.
         */
        Component withScroller() {
            // Use "height" instead of "max height" or "min height" for enabling the scroller
            setHeight(ViewConstants.DEFAULT_LIST_HEIGHT, Unit.PIXELS);
            setWidthFull();
            return this;
        }

        /**
         * Add the given entity in the list.
         *
         * @param entity the entity to add.
         */
        void addEntity(Thumbnail entity) {
            getListDataView().addItem(entity);
        }

        /**
         * Remove the given entity from the list.
         *
         * @param entity the entity to remove.
         * @return {@code true} if the entity was removed from the list. {@code false} if
         * the entity
         */
        boolean removeEntity(Thumbnail entity) {
            if (getListDataView().contains(entity)) {
                getListDataView().removeItem(entity);
                return true;
            }
            return false;
        }

        /**
         * Change the entities that are inside the list.
         *
         * @param entities the new collection of entities.
         */
        void setEntities(Collection<Thumbnail> entities) {
            final var currentFilter = this.dataProvider == null ? null : this.dataProvider.getFilter();
            this.dataStorage = createStorageFor(entities);
            this.dataProvider = new ListDataProvider<>(this.dataStorage);
            this.dataProvider.setSortComparator((a, b) -> {
                if (a == b) {
                    return 0;
                }
                if (a == null) {
                    return -1;
                }
                if (b == null) {
                    return 1;
                }
                return a.compareTo(b);
            });
            if (currentFilter != null) {
                this.dataProvider.setFilter(currentFilter);
            }
            setItems(this.dataProvider);
        }

        /**
         * Create a storage data structure that may be used for the internal lists for
         * containing the given entities.
         *
         * @param entities the entities to put in the created data structure.
         * @return the created data structure.
         */
        @SuppressWarnings("static-method")
        Set<Thumbnail> createStorageFor(Collection<Thumbnail> entities) {
            final var newCollection = new TreeSet<Thumbnail>();
            if (entities != null) {
                newCollection.addAll(entities);
            }
            return newCollection;
        }

        @Override
        public Set<Thumbnail> getEmptyValue() {
            return new TreeSet<>();
        }

    }

}