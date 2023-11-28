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

package fr.ciadlab.labmanager.controller.view.admin;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.controller.view.AbstractViewController;
import fr.ciadlab.labmanager.entities.EntityUtils;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.service.organization.ResearchOrganizationService;
import fr.ciadlab.labmanager.service.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

/** This general controller shows up the administration tools and the list of all the controllers in the backend.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@RestController
@CrossOrigin
public class AdminViewController extends AbstractViewController {

	private ResearchOrganizationService organizationService;

	private ProjectService projectService;

	private String helpUrl;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the live constants.
	 * @param organizationService the research organization service.
	 * @param projectService the project service.
	 * @param usernameKey the key string for encrypting the usernames.
	 * @param helpUrl the URL to the help or the documentation page.
	 */
	public AdminViewController(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired ResearchOrganizationService organizationService,
			@Autowired ProjectService projectService,
			@Value("${labmanager.security.username-key}") String usernameKey,
			@Value("${labmanager.web.help-url}") String helpUrl) {
		super(messages, constants, usernameKey);
		this.organizationService = organizationService;
		this.projectService = projectService;
		this.helpUrl = helpUrl;
	}

	/** Shows up the main administration page.
	 *
	 * @param username the name of the logged-in user.
	 * @return the model-view of the list of publications.
	 */
	@GetMapping(value = "/" + Constants.ADMIN_ENDPOINT)
	public ModelAndView admin(
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) {
		readCredentials(username, Constants.ADMIN_ENDPOINT);
		final ModelAndView modelAndView = new ModelAndView(Constants.ADMIN_ENDPOINT);
		initModelViewWithInternalProperties(modelAndView, false);
		final List<ResearchOrganization> list = this.organizationService.getAllResearchOrganizations().stream()
				.sorted(EntityUtils.getPreferredResearchOrganizationComparator()).collect(Collectors.toList());
		modelAndView.addObject("organizations", list); //$NON-NLS-1$
		modelAndView.addObject("username", Strings.nullToEmpty(getCurrentUsername())); //$NON-NLS-1$
		final UriComponents urlComponents = ServletUriComponentsBuilder.fromCurrentContextPath().build();
		final String baseName = urlComponents.getHost().toUpperCase();
		String baseUrl;
		try {
			final URL url = new URL(urlComponents.getScheme(),
					urlComponents.getHost(), urlComponents.getPort(), ""); //$NON-NLS-1$
			baseUrl = url.toExternalForm();
		} catch (Throwable ex) {
			baseUrl = urlComponents.toUriString();
		}
		modelAndView.addObject("mainSiteUrl", baseUrl); //$NON-NLS-1$
		modelAndView.addObject("mainSiteName", baseName); //$NON-NLS-1$
		if (!Strings.isNullOrEmpty(this.helpUrl)) {
			modelAndView.addObject("helpUrl", this.helpUrl); //$NON-NLS-1$
		}
		final int now = LocalDate.now().getYear();
		modelAndView.addObject("yearList", Arrays.asList( //$NON-NLS-1$
				Integer.valueOf(now), Integer.valueOf(now - 1), Integer.valueOf(now - 2)));
		modelAndView.addObject("projectService", this.projectService); //$NON-NLS-1$
		return modelAndView;
	}

	/** Show the view for merging the database JSON and a given BibTeX for generating a new JSON file to be download.
	 *
	 * @param username the name of the logged-in user.
	 * @return the model-view object.
	 * @throws IOException if there is some internal IO error when building the form's data.
	 */
	@GetMapping(value = "/mergeDatabaseBibTeXToJson")
	public ModelAndView mergeDatabaseBibTeXToJson(
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) throws IOException {
		ensureCredentials(username, "mergeDatabaseBibTeXToJson"); //$NON-NLS-1$
		final ModelAndView modelAndView = new ModelAndView("mergeDatabaseBibTeXToJson"); //$NON-NLS-1$
		initModelViewWithInternalProperties(modelAndView, false);
		//
		modelAndView.addObject("formActionUrl", rooted(Constants.GET_JSON_FROM_DATABASE_AND_BIBTEX_ENDPOINT)); //$NON-NLS-1$
		modelAndView.addObject("URLS_edit", rooted(Constants.PUBLICATION_EDITING_ENDPOINT) + "?" //$NON-NLS-1$ //$NON-NLS-2$
				+ Constants.PUBLICATION_ENDPOINT_PARAMETER + "="); //$NON-NLS-1$
		//
		return modelAndView;
	}

	/** Show the view that show how the saving of the Zip archive into a server side file is progressing.
	 *
	 * @param username the name of the logged-in user.
	 * @return the model-view that shows the duplicate persons.
	 */
	@GetMapping("/saveDatabaseToServerZip")
	public ModelAndView saveDatabaseToServerZip(
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) {
		ensureCredentials(username, "saveDatabaseToServerZip"); //$NON-NLS-1$
		final ModelAndView modelAndView = new ModelAndView("saveDatabaseToServerZip"); //$NON-NLS-1$
		initModelViewWithInternalProperties(modelAndView, false);
		//
		modelAndView.addObject("batchUrl", endpoint(Constants.SAVE_DATABASE_TO_SERVER_ZIP_BATCH_ENDPOINT)); //$NON-NLS-1$
		modelAndView.addObject("finishingUrl", endpoint(Constants.ADMIN_ENDPOINT)); //$NON-NLS-1$
		//
		return modelAndView;
	}


	/** Compute and show the orphan entities in the JPA.
	 *
	 * @param username the name of the logged-in user.
	 * @return the model-view object.
	 */
	@GetMapping(value = "/orphanEntities")
	public ModelAndView orphanEntities(
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) {
		ensureCredentials(username, "orphanEntities"); //$NON-NLS-1$
		final ModelAndView modelAndView = new ModelAndView("orphanEntities"); //$NON-NLS-1$
		initModelViewWithInternalProperties(modelAndView, false);
		//
		modelAndView.addObject("batchUrl", endpoint(Constants.COMPUTE_ORPHAN_ENTITIES_ENDPOINT)); //$NON-NLS-1$
		modelAndView.addObject("finishingUrl", endpoint(Constants.ADMIN_ENDPOINT)); //$NON-NLS-1$
		//
		return modelAndView;
	}

}
