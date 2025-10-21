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

package fr.utbm.ciad.labmanager.services;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.utils.HasAsynchronousUploadService;
import org.apache.commons.io.function.IOBiConsumer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * Abstract implementation of a Spring boot service.
 *
 * @param <T> the type of the entity to be deleted.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEntityService<T extends IdentifiableEntity> extends AbstractService {

    private static final long serialVersionUID = -7685425685906373059L;

    /**
     * Constructor.
     *
     * @param messages       the provider of messages.
     * @param constants      the accessor to the constants.
     * @param sessionFactory the factory of JPA session.
     */
    public AbstractEntityService(MessageSourceAccessor messages, ConfigurationConstants constants, SessionFactory sessionFactory) {
        super(messages, constants, sessionFactory);
    }

    /**
     * Start the editing of the given entity.
     *
     * @param entity the entity to save.
     * @param logger the logger to be used.
     * @return the editing context that enables to keep track of any information needed
     * for saving the entity and its related resources.
     * @since 4.0
     */
    public abstract EntityEditingContext<T> startEditing(T entity, Logger logger);

    /**
     * Start the deletion of the given entities.
     *
     * @param entities the entities to delete.
     * @param logger   the logger to be used.
     * @return the deletion context that enables to keep track of any information needed
     * for deleting the entity and its related resources.
     * @since 4.0
     */
    public abstract EntityDeletingContext<T> startDeletion(Set<T> entities, Logger logger);

    /**
     * Context for editing an entity.
     *
     * @param <T> the type of the entity to be edited.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public interface EntityEditingContext<T extends IdentifiableEntity> extends Serializable {

        /**
         * Replies the edited entity. This entity could be the original (first) edited entity as
         * it is replied by {@link #getOriginalEntity()} or the entity that is provided as
         * argument of {@link #setEntity(IdentifiableEntity)}.
         *
         * @return the edited entity.
         * @see #getOriginalEntity()
         * @see #setEntity(IdentifiableEntity)
         */
        T getEntity();

        /**
         * Change the edited entity. This function changes the value replied by {@link #getEntity()}, but not
         * those replied by {@link #getOriginalEntity()}.
         *
         * @param entity the new edited entity.
         * @see #getEntity()
         * @see #getOriginalEntity()
         */
        void setEntity(T entity);

        /**
         * Replies the original edited entity. It is the entity that is initially provided as editable.
         * The value replied by this function is not changed by a call to {@link #setEntity(IdentifiableEntity)}.
         * To obtain the argument of this call, you must invoke {@link #getEntity()}.
         *
         * @return the original edited entity.
         * @see #getEntity()
         * @see #setEntity(IdentifiableEntity)
         */
        T getOriginalEntity();

        /**
         * Save the entity in the JPA database.
         *
         * <p>After calling this function, it is preferable to not use
         * the entity object that was provided before the saving.
         * Invoke {@link #getEntity()} for obtaining the new entity
         * instance, since the content of the saved object may have totally changed.
         *
         * @param components list of components to update if the service detects an inconsistent value.
         * @throws IOException if files cannot be saved on the server.
         */
        @Transactional
        void save(HasAsynchronousUploadService... components) throws IOException;

        /**
         * Create a deletion context from the entity that is edited. This context is usually used for deleting the entity that is currently edited.
         *
         * @return the deletion context.
         */
        EntityDeletingContext<T> startDeletion();

        /**
         * Create a deletion context from the entity that is edited. This context is usually used for deleting the entity that is currently edited.
         *
         * @return the deletion context, never {@code null}.
         */
        EntityDeletingContext<T> createDeletionContext();

        /**
         * Replies the logger that is associated to the editing context.
         *
         * @return the logger.
         */
        Logger getLogger();

    }

    /**
     * Context for deleting an entity.
     *
     * @param <T> the type of the entity to be deleted.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public interface EntityDeletingContext<T extends IdentifiableEntity> extends Serializable {

        /**
         * Replies the entities to be deleted.
         *
         * @return the entities.
         */
        Set<T> getEntities();

        /**
         * Indicates if the deletion is possible without error.
         * If this function replies {@code false}, then a call to
         * {@code #delete(HasAsynchronousUploadService...)} should throw
         * an acception, and the reason of the deletion rejection could
         * be obtained by calling {@link #getDeletionStatus()}.
         *
         * @return {@code true} if deletion is possible; {@code false}
         * if deletion is not possible.
         * @see #delete(HasAsynchronousUploadService...)
         * @see #getDeletionStatus()
         */
        boolean isDeletionPossible();

        /**
         * Replies the reason of the deletion rejection.
         * See the documentation of {@link #delete(HasAsynchronousUploadService...)}
         * for obtaining the possible constraints for deletion of an organization.
         *
         * @return the reason of the deletion rejection, or {@code null} if deletion is possible.
         * @see #isDeletionPossible()
         * @see #delete(HasAsynchronousUploadService...)
         */
        DeletionStatus getDeletionStatus();

        /**
         * Delete the organization in the JPA database.
         * Conditions for deletion are: <ol>
         * <li>Organization has no linked suborganization;</li>
         * <li>Organization has no linked membership.</li>
         * </ol>
         *
         * <p>After calling this function, it is preferable to not use
         * the organization object any more.
         *
         * @throws Exception if organization or associated files cannot be deleted from the server.
         * @see #isDeletionPossible()
         * @see #getDeletionStatus()
         */
        @Transactional
        void delete() throws Exception;

        /**
         * Replies the logger that is associated to the deletion context.
         *
         * @return the logger.
         */
        Logger getLogger();

    }

    /**
     * Context for editing an entity.
     *
     * @param <T> the type of the entity to be edited.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public abstract static class AbstractEntityEditingContext<T extends IdentifiableEntity> implements EntityEditingContext<T> {

        private static final long serialVersionUID = -7350966100644929293L;

        private final Logger logger;

        private final T originalEntity;

        protected T entity;

        /**
         * Constructor.
         *
         * @param entity the entity to be edited.
         * @param logger the logger to use.
         */
        protected AbstractEntityEditingContext(T entity, Logger logger) {
            this.logger = logger;
            this.originalEntity = entity;
            this.entity = entity;
        }

        @Override
        public Logger getLogger() {
            return this.logger;
        }

        @Override
        public T getEntity() {
            return this.entity;
        }

        @Override
        public void setEntity(T entity) {
            this.entity = entity;
        }

        @Override
        public T getOriginalEntity() {
            return this.originalEntity;
        }

        @Override
        @Transactional
        public abstract void save(HasAsynchronousUploadService... components) throws IOException;

        @Override
        public final EntityDeletingContext<T> startDeletion() {
            final var entity = this.entity;
            if (entity != null && entity.getId() != 0L) {
                return createDeletionContext();
            }
            return new AbstractEntityDeletingContext<>(Collections.singleton(entity), getLogger()) {

                private static final long serialVersionUID = 2393794876950796791L;

                @Override
                protected void deleteEntities(Collection<Long> identifiers) throws Exception {
                    // Nothing to do because the entity was not saved in the JPA infrastructure.
                }

            };
        }

    }

    /**
     * Representation of an uploaded file during the edition process.
     *
     * @param <T> the type of the publication.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class UploadedFileTracker<T extends IdentifiableEntity> implements Serializable {

        private static final long serialVersionUID = 1872347201559398676L;

        private final Function<T, String> reader;

        private final IOBiConsumer<Long, String> deleter;

        private final IOBiConsumer<Long, Long> renamer;

        private String savedPath;

        /**
         * Construction.
         *
         * @param publication publication to be associated to.
         * @param reader      the reading lambda for obtaining the filename.
         * @param deleter     the lambda for deleting the associated file.
         * @param renamer     the lambda for renaming the associated file.
         */
        public UploadedFileTracker(T publication, Function<T, String> reader, IOBiConsumer<Long, String> deleter, IOBiConsumer<Long, Long> renamer) {
            this.reader = reader;
            this.deleter = deleter;
            this.renamer = renamer;
            this.savedPath = reader.apply(publication);
        }

        /**
         * Reset the path memory to keep track of changes.
         *
         * @param entity entity to read for reseting.
         * @param logger the logger to be used.
         */
        public void resetPathMemory(T entity, Logger logger) {
            this.savedPath = this.reader.apply(entity);
            logger.info("Saved file for id " + entity.getId() + " and path: " + this.savedPath); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * Replies if the path to the file has changed since the last saving.
         *
         * @param entity entity to read for reseting.
         * @return {@code true} if the path has changed.
         */
        public boolean isPathChanged(T entity) {
            return !Objects.equals(this.savedPath, this.reader.apply(entity));
        }

        /**
         * Delete the associated file.
         *
         * @param entity entity to read for deletion.
         * @param logger the logger to be used.
         * @return {@code true} if the file is deleted, or {@code false} if no file is deleted.
         * @throws IOException is thrown when the associated file cannot be deleted.
         */
        public boolean deleteFile(T entity, Logger logger) throws IOException {
            final var currentPath = this.reader.apply(entity);
            if (Strings.isNullOrEmpty(currentPath)) {
                if (this.deleter != null) {
                    this.deleter.accept(Long.valueOf(entity.getId()), this.savedPath);
                    logger.info("Deleted file for id " + entity.getId() + " and path: " + this.savedPath); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return true;
            }
            return false;
        }

        /**
         * Delete or rename the associated file to be consistent with the
         * new identifier of the associated entity.
         *
         * @param oldId  the previous value of the entity's identifier. It it is {@code 0} then the entity was never created in the database.
         * @param entity the JPE entity to be tracked.
         * @param logger the logger to be used.
         * @throws IOException is thrown when the associated file cannot be deleted or renamed.
         */
        public void deleteOrRenameFile(long oldId, T entity, Logger logger) throws IOException {
            assert oldId != entity.getId();
            if (oldId != 0) {
                final var currentPath = this.reader.apply(entity);
                if (Strings.isNullOrEmpty(currentPath)) {
                    if (this.deleter != null) {
                        this.deleter.accept(Long.valueOf(oldId), this.savedPath);
                        logger.info("Deleted file for id " + oldId + " and path: " + this.savedPath); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else if (this.renamer != null) {
                    this.renamer.accept(Long.valueOf(oldId), Long.valueOf(entity.getId()));
                    logger.info("Renaming file for id " + oldId); //$NON-NLS-1$
                }
            }
        }

    }

    /**
     * Representation of a list of uploaded files during the edition process.
     *
     * @param <T> the type of the publication.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected static class UploadedFilesTracker<T extends IdentifiableEntity> implements Serializable {

        private static final long serialVersionUID = -775887678637997587L;

        private final Function<T, List<String>> reader;

        private final IOBiConsumer<Long, List<String>> deleter;

        private final IOBiConsumer<Long, Long> renamer;

        private List<String> savedPaths;

        /**
         * Construction.
         *
         * @param publication publication to be associated to.
         * @param reader      the reading lambda for obtaining the filenames.
         * @param deleter     the lambda for deleting the associated files.
         * @param renamer     the lambda for renaming the associated files.
         */
        public UploadedFilesTracker(T publication, Function<T, List<String>> reader, IOBiConsumer<Long, List<String>> deleter, IOBiConsumer<Long, Long> renamer) {
            this.reader = reader;
            this.deleter = deleter;
            this.renamer = renamer;
            this.savedPaths = reader.apply(publication);
        }

        /**
         * Reset the path memory to keep track of changes.
         *
         * @param entity entity to read for reseting.
         * @param logger the logger to be used.
         */
        public void resetPathMemory(T entity, Logger logger) {
            this.savedPaths = this.reader.apply(entity);
            logger.info("Reset file paths: " + this.savedPaths); //$NON-NLS-1$
        }

        /**
         * Replies if the path to the file has changed since the last saving.
         *
         * @param entity entity to read for reseting.
         * @return {@code true} if the path has changed.
         */
        public boolean isPathChanged(T entity) {
            return !Objects.equals(this.savedPaths, this.reader.apply(entity));
        }

        /**
         * Delete the associated files.
         *
         * @param entity entity to read for deletion.
         * @param logger the logger to be used.
         * @return {@code true} if the files are deleted, or {@code false} if no file is deleted.
         * @throws IOException is thrown when an associated file cannot be deleted.
         */
        public boolean deleteFiles(T entity, Logger logger) throws IOException {
            final var currentPaths = this.reader.apply(entity);
            if (currentPaths == null || currentPaths.isEmpty()) {
                if (this.deleter != null) {
                    this.deleter.accept(Long.valueOf(entity.getId()), this.savedPaths);
                    logger.info("Deleted files for the entity with id " + entity.getId() + " and paths: " + this.savedPaths); //$NON-NLS-1$ //$NON-NLS-2$
                }
                return true;
            }
            return false;
        }

        /**
         * Delete or rename the associated files to be consistent with the
         * new identifier of the associated entity.
         *
         * @param oldId  the previous value of the entity's identifier. It it is {@code 0} then the entity was never created in the database.
         * @param entity the JPE entity to be tracked.
         * @param logger the logger to be used.
         * @throws IOException is thrown when an associated file cannot be deleted or renamed.
         */
        public void deleteOrRenameFiles(long oldId, T entity, Logger logger) throws IOException {
            assert oldId != entity.getId();
            if (oldId != 0) {
                final var currentPaths = this.reader.apply(entity);
                if (currentPaths == null || currentPaths.isEmpty()) {
                    if (this.deleter != null) {
                        this.deleter.accept(Long.valueOf(oldId), this.savedPaths);
                        logger.info("Deleted files for entity with id " + entity.getId() + " and paths: " + this.savedPaths); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else if (this.renamer != null) {
                    this.renamer.accept(Long.valueOf(oldId), Long.valueOf(entity.getId()));
                    logger.info("Renamed files for entity with id " + entity.getId()); //$NON-NLS-1$
                }
            }
        }

    }

    /**
     * Context for editing an entity that has also associated files on the server file system.
     *
     * @param <T> the type of the entity to be edited.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public abstract static class AbstractEntityWithServerFilesEditingContext<T extends IdentifiableEntity> extends AbstractEntityEditingContext<T> {

        private static final long serialVersionUID = -3495386730939748318L;

        private long id;

        /**
         * Constructor.
         *
         * @param entity the entity to be edited.
         * @param logger the logger to be used.
         */
        protected AbstractEntityWithServerFilesEditingContext(T entity, Logger logger) {
            super(entity, logger);
            this.id = entity.getId();
        }

        /**
         * Create a tracker for an uploaded file.
         *
         * @param entity  the entity to track.
         * @param reader  the reading lambda for obtaining the filename.
         * @param deleter the lambda for deleting the associated file.
         * @param renamer the lambda for renaming the associated file.
         * @return the tracker.
         */
        protected UploadedFileTracker<T> newUploadedFileTracker(T entity, Function<T, String> reader, IOBiConsumer<Long, String> deleter, IOBiConsumer<Long, Long> renamer) {
            return new UploadedFileTracker<>(entity, reader, deleter, renamer);
        }

        /**
         * Create a tracker for an uploaded file.
         *
         * @param entity  the entity to track.
         * @param reader  the reading lambda for obtaining the filename.
         * @param deleter the lambda for deleting the associated file.
         * @param renamer the lambda for renaming the associated file.
         * @return the tracker.
         */
        protected UploadedFilesTracker<T> newUploadedFilesTracker(T entity, Function<T, List<String>> reader, IOBiConsumer<Long, List<String>> deleter, IOBiConsumer<Long, Long> renamer) {
            return new UploadedFilesTracker<>(entity, reader, deleter, renamer);
        }

        @Override
        public final void save(HasAsynchronousUploadService... components) throws IOException {
            this.entity = writeInJPA(this.entity, true);

            if (this.id != this.entity.getId()) {
                // The id of the entity has changed.

                // Therefore, the associated files may be not correctly named
                deleteOrRenameAssociatedFiles(this.id);

                // If there is any uploaded file (new file content), they should be updated also.
                var changed = false;
                for (final var component : components) {
                    component.updateValue();
                    changed = true;
                }

                // Save the changes of the attributes
                if (changed) {
                    this.entity = writeInJPA(this.entity, false);
                }
            }

            // Upload the new file on the server
            final var hasUploadedFile = prepareAssociatedFileUpload();
            if (hasUploadedFile) {
                for (final var component : components) {
                    component.saveUploadedFileOnServer();
                }
            }
            this.id = this.entity.getId();
            postProcessAssociatedFiles();
        }

        /**
         * Invoked to save the given entity in the JPA infrastructure.
         *
         * @param entity        the entity to save.
         * @param initialSaving indicates if the call to this function is the function in the global processing of saving. This function mayt be called multiple times.
         * @return the new instance of the saved entity.
         */
        protected abstract T writeInJPA(T entity, boolean initialSaving);

        /**
         * Delete or move all the associated files to the entity with the given identifier.
         * The files are deleted when the new entity not has the path in the fields.
         * The files are moved when the new entity has the path in the fields.
         *
         * @param oldId the identifier of the entity before change.
         * @throws IOException the error if an associated file cannot be deleted or renamed.
         */
        protected abstract void deleteOrRenameAssociatedFiles(long oldId) throws IOException;

        /**
         * Invoked just before the associated files are uploaded for preparing the server
         * to receive them. For example, the old files are removed when their is no file to
         * be uploaded and the associated path is empty.
         *
         * @return {@code true} if at least one file should be uploaded from the client to the server.
         * @throws IOException the error if an associated file cannot be updated.
         */
        protected abstract boolean prepareAssociatedFileUpload() throws IOException;

        /**
         * Invoked just after the associated files are uploaded.
         *
         * @throws IOException the error if an associated file cannot be updated.
         */
        protected abstract void postProcessAssociatedFiles() throws IOException;

    }

    /**
     * Context for deleting an entity.
     *
     * @param <T> the type of the entity to be deleted.
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    public abstract static class AbstractEntityDeletingContext<T extends IdentifiableEntity> implements EntityDeletingContext<T> {

        private static final long serialVersionUID = -7909548992030372794L;

        private final Logger logger;

        private final Set<T> entities;

        private DeletionStatus deletionStatus;

        /**
         * Constructor.
         *
         * @param entities the entities to delete.
         * @param logger   the logger to be used.
         */
        protected AbstractEntityDeletingContext(Set<T> entities, Logger logger) {
            this.logger = logger;
            this.entities = entities;
        }

        @Override
        public Logger getLogger() {
            return this.logger;
        }

        @Override
        public Set<T> getEntities() {
            return this.entities;
        }

        /**
         * Replies the list of the identifiers that shoçuld be deleted.
         *
         * @return the list of identifiers for deletable entities.
         */
        protected List<Long> getDeletableEntityIdentifiers() {
            return this.entities.stream().map(it -> Long.valueOf(it.getId())).toList();
        }

        @Override
        public boolean isDeletionPossible() {
            checkDeletionStatus();
            return this.deletionStatus.isOk();
        }

        @Override
        public DeletionStatus getDeletionStatus() {
            checkDeletionStatus();
            return this.deletionStatus;
        }

        private void checkDeletionStatus() {
            if (this.deletionStatus == null) {
                this.deletionStatus = computeDeletionStatus();
            }
        }

        /**
         * Compute the deletion status for the given entities to be deleted.
         *
         * @return the new deletion status.
         */
        @SuppressWarnings("static-method")
        protected DeletionStatus computeDeletionStatus() {
            return DeletionStatus.OK;
        }

        @Override
        @Transactional
        public void delete() throws Exception {
            if (!isDeletionPossible()) {
                throw new IllegalStateException();
            }
            final var identifiers = getDeletableEntityIdentifiers();
            deleteEntities(identifiers);
        }

        /**
         * Do the deletion of the entities.
         *
         * @param identifiers the identifiers of the entities to be deleted.
         * @throws Exception if organization or associated files cannot be deleted from the server.
         */
        protected abstract void deleteEntities(Collection<Long> identifiers) throws Exception;

    }

}
