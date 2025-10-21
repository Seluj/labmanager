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

package fr.utbm.ciad.labmanager.security.cas;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Configuration description for a CAS.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Component
public class CasServerConfiguration implements Serializable {

    private static final long serialVersionUID = -6309929457086811382L;

    private String base;

    private String login;

    private String logout;

    private String service;

    private String key;

    /**
     * Replies the base URL for the CAS server.
     *
     * @return the base URL.
     */
    public String getBase() {
        return this.base;
    }

    /**
     * Change the base URL for the CAS server.
     *
     * @param baseUrl the base URL.
     */
    public void setBase(String baseUrl) {
        this.base = baseUrl;
    }

    /**
     * Replies the login URL for the CAS server.
     *
     * @return the login URL.
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Change the login URL for the CAS server.
     *
     * @param loginUrl the login URL.
     */
    public void setLogin(String loginUrl) {
        this.login = loginUrl;
    }

    /**
     * Replies the logout URL for the CAS server.
     *
     * @return the logout URL.
     */
    public String getLogout() {
        return this.logout;
    }

    /**
     * Change the logout URL for the CAS server.
     *
     * @param logoutUrl the logout URL.
     */
    public void setLogout(String logoutUrl) {
        this.logout = logoutUrl;
    }

    /**
     * Replies the URL for the application's login service.
     *
     * @return the login URL.
     */
    public String getService() {
        return this.service;
    }

    /**
     * Change the URL for the application's login service.
     *
     * @param serviceUrl the URL of the application login service.
     */
    public void setService(String serviceUrl) {
        this.service = serviceUrl;
    }

    /**
     * Replies the authentication public key for the CAS server.
     *
     * @return the key.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Change the authentication public key for the CAS server.
     *
     * @param key the CAS public key.
     */
    public void setKey(String key) {
        this.key = key;
    }

}
