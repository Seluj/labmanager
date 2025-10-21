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

package fr.utbm.ciad.labmanager.services.organization;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.ConfigurationConstants;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressRepository;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.arakhne.afc.vmutil.FileSystem;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service for organizations' addresses.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class OrganizationAddressService extends AbstractEntityService<OrganizationAddress> {

    private static final long serialVersionUID = 1997081746266439673L;

    private final OrganizationAddressRepository addressRepository;

    private final DownloadableFileManager fileManager;

    /**
     * Constructor for injector.
     * This constructor is defined for being invoked by the IOC injector.
     *
     * @param fileManager       the manager of files.
     * @param addressRepository the address repository.
     * @param messages          the provider of localized messages.
     * @param constants         the accessor to the live constants.
     * @param sessionFactory    the Hibernate session factory.
     */
    public OrganizationAddressService(
            @Autowired DownloadableFileManager fileManager,
            @Autowired OrganizationAddressRepository addressRepository,
            @Autowired MessageSourceAccessor messages,
            @Autowired ConfigurationConstants constants,
            @Autowired SessionFactory sessionFactory) {
        super(messages, constants, sessionFactory);
        this.fileManager = fileManager;
        this.addressRepository = addressRepository;
    }

    /**
     * Replies all the organizations' addresses.
     *
     * @return the address.
     */
    public List<OrganizationAddress> getAllAddresses() {
        return this.addressRepository.findAll();
    }

    /**
     * Replies all the organizations' addresses.
     *
     * @param pageable the manager of pages.
     * @param filter   the filter of addresses.
     * @return the address.
     * @since 4.0
     */
    public Page<OrganizationAddress> getAllAddresses(Pageable pageable, Specification<OrganizationAddress> filter) {
        return this.addressRepository.findAll(filter, pageable);
    }

    /**
     * Replies the organization address with the given identifier.
     *
     * @param identifier the identifier of the address.
     * @return the address or {@code null} if none.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public OrganizationAddress getAddressById(long identifier) {
        final var adr = this.addressRepository.findById(Long.valueOf(identifier));
        if (adr.isPresent()) {
            return adr.get();
        }
        return null;
    }

    /**
     * Create a research organization address.
     *
     * @param validated       indicates if the journal is validated by a local authority.
     * @param name            the name of the address.
     * @param complement      the complementary information that may appear before the rest of the address.
     * @param street          the building number and street name.
     * @param zipCode         the postal code.
     * @param city            the name of the city.
     * @param mapCoordinates  the geo. coordinates of the location.
     * @param googleLink      the link to the Google Map.
     * @param backgroundImage the background image.
     * @return the created address in the database.
     * @throws IOException if the uploaded files cannot be treated correctly.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public Optional<OrganizationAddress> createAddress(boolean validated,
                                                       String name, String complement, String street, String zipCode, String city,
                                                       String mapCoordinates, String googleLink, MultipartFile backgroundImage) throws IOException {
        final var adr = new OrganizationAddress();
        adr.setName(name);
        adr.setComplement(complement);
        adr.setStreet(street);
        adr.setZipCode(zipCode);
        adr.setCity(city);
        adr.setMapCoordinates(mapCoordinates);
        adr.setGoogleMapLink(googleLink);
        adr.setValidated(validated);
        // Save to get the ID of the address
        this.addressRepository.save(adr);
        //
        updateUploadedImage(adr, backgroundImage, false, true);
        return Optional.of(adr);
    }

    /**
     * Update a research organization address.
     *
     * @param identifier             the identifier in the database of the address to update.
     * @param validated              indicates if the journal is validated by a local authority.
     * @param name                   the name of the address.
     * @param complement             the complementary information that may appear before the rest of the address.
     * @param street                 the building number and street name.
     * @param zipCode                the postal code.
     * @param city                   the name of the city.
     * @param mapCoordinates         the geo. coordinates of the location.
     * @param googleLink             the link to the Google Map.
     * @param backgroundImage        the background image.
     * @param removedBackgroundImage indicates if the background image should be removed.
     * @return the created address in the database.
     * @throws IOException if the uploaded files cannot be treated correctly.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    public Optional<OrganizationAddress> updateAddress(
            long identifier, boolean validated, String name, String complement, String street, String zipCode,
            String city, String mapCoordinates, String googleLink, MultipartFile backgroundImage, boolean removedBackgroundImage) throws IOException {
        final var res = this.addressRepository.findById(Long.valueOf(identifier));
        if (res.isPresent()) {
            final var address = res.get();
            if (!Strings.isNullOrEmpty(name)) {
                address.setName(name);
            }
            if (!Strings.isNullOrEmpty(complement)) {
                address.setComplement(complement);
            }
            if (!Strings.isNullOrEmpty(street)) {
                address.setStreet(street);
            }
            address.setZipCode(zipCode);
            if (!Strings.isNullOrEmpty(city)) {
                address.setCity(city);
            }
            address.setMapCoordinates(mapCoordinates);
            address.setGoogleMapLink(googleLink);
            address.setValidated(validated);
            //
            updateUploadedImage(address, backgroundImage, removedBackgroundImage, false);
            //
            this.addressRepository.save(address);
        }
        return res;
    }

    /**
     * Update the references to the background image for the given address based on the
     * inputs.
     * The just-uploaded files are given as argument.
     *
     * @param address         the address.
     * @param backgroundImage the background image.
     * @param saveInDb        indicates if the address should be saved in database by this function.
     * @throws IOException if the uploaded files cannot be treated correctly.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    protected void updateUploadedImage(OrganizationAddress address, MultipartFile backgroundImage,
                                       boolean removedBackgroundImage, boolean saveInDb) throws IOException {
        final var logger = LoggerFactory.getLogger(getClass());
        // Treat the uploaded files
        var hasChanged = false;
        if (removedBackgroundImage) {
            final var ext = FileSystem.extension(address.getPathToBackgroundImage());
            try {
                this.fileManager.deleteAddressBackgroundImage(address.getId(), ext, logger);
            } catch (Throwable ex) {
                // Silent
            }
            address.setPathToBackgroundImage(null);
            hasChanged = true;
        }
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            final var ext = FileSystem.extension(backgroundImage.getOriginalFilename());
            final var filename = this.fileManager.makeAddressBackgroundImage(address.getId(), ext);
            this.fileManager.saveImage(filename, backgroundImage, logger);
            address.setPathToBackgroundImage(filename.getPath());
            hasChanged = true;
            //getLogger().info("Address background image uploaded at: " + filename.getPath()); //$NON-NLS-1$
        }
        if (hasChanged && saveInDb) {
            this.addressRepository.save(address);
        }
    }

    /**
     * Remove from the database the organization address with the given database identifier.
     *
     * @param identifier the database identifier of the address.
     * @Deprecated no replacement.
     */
    @Deprecated(since = "4.0", forRemoval = true)
    @Transactional
    public void removeAddress(long identifier) {
        final var id = Long.valueOf(identifier);
        this.addressRepository.deleteById(id);
    }

    @Override
    public EntityEditingContext<OrganizationAddress> startEditing(OrganizationAddress address, Logger logger) {
        assert address != null;
        logger.info("Starting the editing of the organization address: " + address); //$NON-NLS-1$
        return new EditingContext(address, logger);
    }

    @Override
    public EntityDeletingContext<OrganizationAddress> startDeletion(Set<OrganizationAddress> addresses, Logger logger) {
        assert addresses != null && !addresses.isEmpty();
        logger.info("Starting the deletion of the organization addresses: " + addresses); //$NON-NLS-1$
        // Force loading of the linked entities
        inSession(session -> {
            for (final var address : addresses) {
                if (address.getId() != 0L) {
                    session.load(address, Long.valueOf(address.getId()));
                    Hibernate.initialize(address.getMemberships());
                }
            }
        });
        return new DeletingContext(addresses, logger);
    }

    /**
     * Context for editing a {@link OrganizationAddress}.
     * This context is usually defined when the entity is associated to
     * external resources in the server file system.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class EditingContext extends AbstractEntityWithServerFilesEditingContext<OrganizationAddress> {

        private static final long serialVersionUID = -9212082677199758148L;

        private final UploadedFileTracker<OrganizationAddress> pathToBackgroundImage;

        /**
         * Constructor.
         *
         * @param address the edited address.
         * @param logger  the logger to be used.
         */
        EditingContext(OrganizationAddress address, Logger logger) {
            super(address, logger);
            this.pathToBackgroundImage = newUploadedFileTracker(address,
                    OrganizationAddress::getPathToBackgroundImage,
                    (id, savedPath) -> {
                        if (!Strings.isNullOrEmpty(savedPath)) {
                            final var ext = FileSystem.extension(savedPath);
                            if (!Strings.isNullOrEmpty(ext)) {
                                OrganizationAddressService.this.fileManager.deleteAddressBackgroundImage(id.longValue(), ext, logger);
                            }
                        }
                    },
                    null);
        }

        @Override
        protected OrganizationAddress writeInJPA(OrganizationAddress entity, boolean initialSaving) {
            final var address = OrganizationAddressService.this.addressRepository.save(this.entity);
            getLogger().info("Saved the organization address: " + address); //$NON-NLS-1$
            return address;
        }

        @Override
        protected void deleteOrRenameAssociatedFiles(long oldId) throws IOException {
            this.pathToBackgroundImage.deleteOrRenameFile(oldId, this.entity, getLogger());
        }

        @Override
        protected boolean prepareAssociatedFileUpload() throws IOException {
            return !this.pathToBackgroundImage.deleteFile(this.entity, getLogger());
        }

        @Override
        protected void postProcessAssociatedFiles() {
            this.pathToBackgroundImage.resetPathMemory(this.entity, getLogger());
        }

        @Override
        public EntityDeletingContext<OrganizationAddress> createDeletionContext() {
            return OrganizationAddressService.this.startDeletion(Collections.singleton(this.entity), getLogger());
        }

    }

    /**
     * Context for deleting a {@link OrganizationAddress}.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    protected class DeletingContext extends AbstractEntityDeletingContext<OrganizationAddress> {

        private static final long serialVersionUID = 8315627618389635135L;

        /**
         * Constructor.
         *
         * @param addresses the organization addresses to delete.
         * @param logger    the logger to be used.
         */
        protected DeletingContext(Set<OrganizationAddress> addresses, Logger logger) {
            super(addresses, logger);
        }

        @Override
        protected DeletionStatus computeDeletionStatus() {
            for (final var entity : getEntities()) {
                if (!entity.getMemberships().isEmpty()) {
                    return OrganizationAddressDeletionStatus.MEMBERSHIP;
                }
            }
            return DeletionStatus.OK;
        }

        @Override
        protected void deleteEntities(Collection<Long> identifiers) throws Exception {
            OrganizationAddressService.this.addressRepository.deleteAllById(identifiers);
            getLogger().info("Deletd organization addresses: " + identifiers); //$NON-NLS-1$
            for (final Long id : identifiers) {
                OrganizationAddressService.this.fileManager.deleteAddressBackgroundImage(id.longValue(), getLogger());
            }
        }

    }

}
