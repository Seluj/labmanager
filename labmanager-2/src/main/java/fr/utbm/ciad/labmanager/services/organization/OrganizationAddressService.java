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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressRepository;
import fr.utbm.ciad.labmanager.services.AbstractService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.arakhne.afc.vmutil.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for organizations' addresses.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class OrganizationAddressService extends AbstractService {

	private final OrganizationAddressRepository addressRepository;

	private DownloadableFileManager fileManager;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param fileManager the manager of files.
	 * @param addressRepository the address repository.
	 */
	public OrganizationAddressService(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired DownloadableFileManager fileManager,
			@Autowired OrganizationAddressRepository addressRepository) {
		super(messages, constants);
		this.fileManager = fileManager;
		this.addressRepository = addressRepository;
	}

	/** Replies all the organizations' addresses.
	 *
	 * @return the address.
	 */
	public List<OrganizationAddress> getAllAddresses() {
		return this.addressRepository.findAll();
	}

	/** Replies all the organizations' addresses.
	 *
	 * @param pageable the manager of pages.
	 * @return the address.
	 * @since 4.0
	 */
	public Page<OrganizationAddress> getAllAddresses(Pageable pageable) {
		return this.addressRepository.findAll(pageable);
	}

	/** Replies all the organizations' addresses.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of addresses.
	 * @return the address.
	 * @since 4.0
	 */
	public Page<OrganizationAddress> getAllAddresses(Pageable pageable, Specification<OrganizationAddress> filter) {
		return this.addressRepository.findAll(filter, pageable);
	}

	/** Replies the organization address with the given identifier.
	 *
	 * @param identifier the identifier of the address.
	 * @return the address or {@code null} if none.
	 */
	public OrganizationAddress getAddressById(int identifier) {
		final Optional<OrganizationAddress> adr = this.addressRepository.findById(Integer.valueOf(identifier));
		if (adr.isPresent()) {
			return adr.get();
		}
		return null;
	}

	/** Create a research organization address.
	 *
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param name the name of the address.
	 * @param complement the complementary information that may appear before the rest of the address.
	 * @param street the building number and street name. 
	 * @param zipCode the postal code.
	 * @param city the name of the city.
	 * @param mapCoordinates the geo. coordinates of the location.
	 * @param googleLink the link to the Google Map.
	 * @param backgroundImage the background image.
	 * @return the created address in the database.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	public Optional<OrganizationAddress> createAddress(boolean validated,
			String name, String complement, String street, String zipCode, String city,
			String mapCoordinates, String googleLink, MultipartFile backgroundImage) throws IOException {
		final OrganizationAddress adr = new OrganizationAddress();
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

	/** Update a research organization address.
	 *
	 * @param identifier the identifier in the database of the address to update.
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param name the name of the address.
	 * @param complement the complementary information that may appear before the rest of the address.
	 * @param street the building number and street name. 
	 * @param zipCode the postal code.
	 * @param city the name of the city.
	 * @param mapCoordinates the geo. coordinates of the location.
	 * @param googleLink the link to the Google Map.
	 * @param backgroundImage the background image.
	 * @param removedBackgroundImage indicates if the background image should be removed.
	 * @return the created address in the database.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	public Optional<OrganizationAddress> updateAddress(
			int identifier, boolean validated, String name, String complement, String street, String zipCode,
			String city, String mapCoordinates, String googleLink, MultipartFile backgroundImage, boolean removedBackgroundImage) throws IOException {
		final Optional<OrganizationAddress> res = this.addressRepository.findById(Integer.valueOf(identifier));
		if (res.isPresent()) {
			final OrganizationAddress address = res.get();
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

	/** Update the references to the background image for the given address based on the 
	 * inputs.
	 * The just-uploaded files are given as argument.
	 * 
	 * @param address the address.
	 * @param backgroundImage the background image.
	 * @param saveInDb indicates if the address should be saved in database by this function.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	protected void updateUploadedImage(OrganizationAddress address, MultipartFile backgroundImage,
			boolean removedBackgroundImage, boolean saveInDb) throws IOException {
		// Treat the uploaded files
		boolean hasChanged = false;
		if (removedBackgroundImage) {
			final String ext = FileSystem.extension(address.getPathToBackgroundImage());
			try {
				this.fileManager.deleteAddressBackgroundImage(address.getId(), ext);
			} catch (Throwable ex) {
				// Silent
			}
			address.setPathToBackgroundImage(null);
			hasChanged = true;
		}
		if (backgroundImage != null && !backgroundImage.isEmpty()) {
			final String ext = FileSystem.extension(backgroundImage.getOriginalFilename());
			final File filename = this.fileManager.makeAddressBackgroundImage(address.getId(), ext);
			this.fileManager.saveImage(filename, backgroundImage);
			address.setPathToBackgroundImage(filename.getPath());
			hasChanged = true;
			getLogger().info("Address background image uploaded at: " + filename.getPath()); //$NON-NLS-1$
		}
		if (hasChanged && saveInDb) {
			this.addressRepository.save(address);
		}
	}

	/** Remove from the database the organization address with the given database identifier.
	 *
	 * @param identifier the database identifier of the address.
	 */
	public void removeAddress(int identifier) {
		final Integer id = Integer.valueOf(identifier);
		this.addressRepository.deleteById(id);
	}

}
