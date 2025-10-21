package fr.utbm.ciad.labmanager.security.cas;

import org.springframework.security.cas.ServiceProperties;

/**
 * Default class for CAS authentication entry point.
 *
 * @author $Author: jferlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DirectCasAuthenticationEntryPoint extends org.springframework.security.cas.web.CasAuthenticationEntryPoint {

    private final String organizationName;

    /**
     * Create a new instance with the given login URL and service properties.
     *
     * @param organizationName  the name of the organization to which this filter is associated to.
     * @param loginUrl          the login URL.
     * @param serviceProperties the service properties.
     */
    public DirectCasAuthenticationEntryPoint(String organizationName, String loginUrl, ServiceProperties serviceProperties) {
        this.organizationName = organizationName;
        setLoginUrl(loginUrl);
        setServiceProperties(serviceProperties);
    }

    /**
     * Replies the name of the organization to which this filter is associated to.
     *
     * @return the name, never {@code null}.
     */
    public String getOrganizationName() {
        return this.organizationName;
    }

}
