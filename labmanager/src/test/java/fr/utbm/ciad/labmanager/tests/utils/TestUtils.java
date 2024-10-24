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

package fr.utbm.ciad.labmanager.tests.utils;

import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/** Utilities for tests.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class TestUtils {

	private static Boolean networkEnable;

	/** Replies if the network connection is enable or note.
	 * The Java property {@code tests.network.enable} must be equals to {@code "true"} for enabling
	 * the network for tests.
	 *
	 * @return {@code true} if the network is turned on.
	 */
	public static boolean isNetworkEnable() {
		synchronized (TestUtils.class) {
			if (networkEnable == null) {
				networkEnable = Boolean.FALSE;
				if (Objects.equals("true", System.getProperty("tests.network.enable"))) {
					try {
						final URL url = new URL("http://www.google.com");
						final URLConnection connection = url.openConnection();
						connection.setConnectTimeout(5000);
						connection.connect();
						networkEnable = Boolean.TRUE;
					} catch (Exception ex) {
						//
					}
				}
			}
			return networkEnable.booleanValue();
		}
	}

}
