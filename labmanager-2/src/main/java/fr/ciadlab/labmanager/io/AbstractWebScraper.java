/*
 * $Id$
 * 
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.io;

import java.net.URL;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;

/** Abstract implementation of a web-scraper.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.3
 */
public abstract class AbstractWebScraper {

	/** Default flag for the {@code developer} arguments.
	 */
	protected static final boolean DEFAULT_DEVELOPER = false;

	private static final int TIMEOUT = 30000;

	/** Ensure the definition of a progress bar.
	 *
	 * @param progress the original progress bar.
	 * @return the given progress bar or an empty one.
	 */
	protected static Progression ensureProgress(Progression progress) {
		return progress == null ? new DefaultProgression() : progress;
	}

	/** Wait for the loading of the element.
	 *
	 * @param loadingPage the page that is loading.
	 * @param selector the XPath selector for the element.
	 * @return the loaded element.
	 */
	protected static ElementHandle waitForElement(Page loadingPage, String selector) {
		ElementHandle section = loadingPage.querySelector(selector);
		final long timeout = System.currentTimeMillis() + TIMEOUT;
		while (section == null && System.currentTimeMillis() < timeout) {
			Thread.yield();
			section = loadingPage.querySelector(selector);
		}
		return section;
	}

	/** Read an integer value in the element pointed by the given selector.
	 *
	 * @param handle the original handle.
	 * @param selector the element selector.
	 * @return the value or {@code null} if it is not possible to extract the number.
	 */
	protected static Integer readInt(ElementHandle handle, String selector) {
		if (handle != null) {
			final ElementHandle h0 = handle.querySelector(selector);
			if (h0 != null) {
				return readInt(h0);
			}
		}
		return null;
	}

	/** Read an integer value from the element.
	 *
	 * @param handle the handle to read.
	 * @return the value or {@code null} if it is not possible to extract the number.
	 */
	protected static Integer readInt(ElementHandle handle) {
		if (handle != null) {
			String content = handle.textContent();
			if (!Strings.isNullOrEmpty(content)) {
				content = content.trim(); 
				// Remove any mark that is related to the local format of the number
				final String unformattedContent = content.replaceAll("[^0-9+\\-]+", ""); //$NON-NLS-1$ //$NON-NLS-2$
				if (!Strings.isNullOrEmpty(unformattedContent)) {
					try {
						return Integer.valueOf(unformattedContent);
					} catch (Throwable ex) {
						//
					}
				}
			}
		}
		return null;
	}

	/** Read an integer value in the element pointed by the given selectors.
	 *
	 * @param handle the original handle.
	 * @param selector0 the first element selector.
	 * @param selector1 the second element selector.
	 * @return the value or {@code null} if it is not possible to extract the number.
	 */
	protected static Integer readInt(Page handle, String selector0, String selector1) {
		if (handle != null) {
			final ElementHandle h0 = handle.querySelector(selector0);
			if (h0 != null) {
				final ElementHandle h1 = h0.querySelector(selector1);
				if (h1 != null) {
					return readInt(h1);
				}
			}
		}
		return null;
	}

	/** Replies the integer value from the given integer. If the given argument is {@code null}, the value
	 * {@code -1} is returned.
	 *
	 * @param value the value.
	 * @return the value if it is positive or nul, or {@code -1} if it is negative.
	 */
	protected static int positiveInt(Integer value) {
		if (value != null) {
			final int ivalue = value.intValue();
			if (ivalue >= 0) {
				return ivalue;
			}
		}
		return -1;
	}

	/** Read the content of the page pointed by the given URL.
	 *
	 * @param developer indicates if the browser is launched in developer mode (window visible) or not (window invisible).
	 * @param url the URL.
	 * @param progress the progress indicator.
	 * @param loadElementSelector the selector that enables to detect the end of the loading of the page.
	 * @param waitingDuration the number of millis to wait before searching for the {@code loadElementSelector}.
	 * @param loadedHandler the handler invoked when the page is loaded.
	 * @throws Exception if it is impossible to read the page.
	 */
	protected static void loadHtmlPage(boolean developer, URL url, Progression progress,
			String loadElementSelector, int waitingDuration,
			HtmlPageExtractor loadedHandler) throws Exception {
		assert progress != null;
		progress.setProperties(0, 0, 100, false);
		try {
			if (url != null) {
				try (Playwright playwright = Playwright.create()) {
					final BrowserType browserType = playwright.firefox();
					final LaunchOptions options = new LaunchOptions();
					options.setDevtools(developer);
					try (final Browser browser = browserType.launch(options)) {
						try (final Page page = browser.newPage()) {
							progress.setValue(20);
							final Response response = page.navigate(url.toExternalForm());
							response.finished();
							progress.setValue(80);
							if (waitingDuration > 0) {
								Thread.sleep(waitingDuration);
							}
							ElementHandle section0 = waitForElement(page, loadElementSelector);
							progress.setValue(95);
							if (section0 != null) {
								loadedHandler.apply(page, section0);
							}
						}
					}
				}
			}
		} finally {
			progress.end();
		}
	}

	/** Extractor from HTML page.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 3.6
	 */
	@FunctionalInterface
	protected interface HtmlPageExtractor {

		/** Invoked when the element is discovered from the HTML page.
		 *
		 * @param page the page.
		 * @param element the discovered page.
		 */
		void apply(Page page, ElementHandle element);

	}

}
