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

package fr.ciadlab.labmanager.io.hal;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import org.arakhne.afc.vmutil.FileSystem;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Default implementation for the utilities for HAL numbers.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@Component
@Primary
public class DefaultHalTools implements HalTools {

	private static final URL HAL_BASE;

	private static final String HAL_PATTERN_STR = "^hal\\-.*+$"; //$NON-NLS-1$

	private static final Pattern HAL_PATTERN;

	static {
		try {
			HAL_BASE = new URL("https://hal.science"); //$NON-NLS-1$
		} catch (MalformedURLException ex) {
			throw new Error(ex);
		}
		HAL_PATTERN = Pattern.compile(HAL_PATTERN_STR);
	}

	private static String validatePath(String path) {
		String pth = path;
		if (!Strings.isNullOrEmpty(pth)) {
			pth = pth.trim();
			if (!Strings.isNullOrEmpty(pth)) {
				final Matcher matcher = HAL_PATTERN.matcher(pth);
				if (matcher.matches()) {
					return pth;
				}
			}
		}
		return null;
	}

	@Override
	public String getHALNumberFromHALUrl(URL url) {
		if (url != null) {
			String path = url.getPath();
			if (!Strings.isNullOrEmpty(path)) {
				if (path.startsWith("/")) { //$NON-NLS-1$
					path = path.substring(1);
				}
			}
			path = validatePath(path);
			if (path != null) {
				return path;
			}
		}
		throw new IllegalArgumentException("Invalid HAL: " + url); //$NON-NLS-1$
	}

	@Override
	public String getHALNumberFromHALUrl(String url) {
		if (!Strings.isNullOrEmpty(url)) {
			String path = null;
			try {
				final URI urlObj = new URI(url);
				path = urlObj.getPath();
				if (Strings.isNullOrEmpty(path)) {
					path = urlObj.getSchemeSpecificPart();
				} else if (path.startsWith("/")) { //$NON-NLS-1$
					path = path.substring(1);
				}
			} catch (Throwable ex0) {
				path = url;
			}
			path = validatePath(path);
			if (path != null) {
				return path;
			}
		}
		throw new IllegalArgumentException("Invalid HAL: " + url); //$NON-NLS-1$
	}

	@Override
	public URL getHALUrlFromHALNumber(String number) {
		if (!Strings.isNullOrEmpty(number)) {
			return FileSystem.join(HAL_BASE, number);
		}
		return null;
	}

}
