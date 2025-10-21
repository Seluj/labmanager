package fr.utbm.ciad.labmanager.security.cas;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.web.CasAuthenticationFilter;

/**
 * Class for CAS authentication filter.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see MultiCasAuthenticationFilter
 * @since 4.0
 */
public class DirectCasAuthenticationFilter extends CasAuthenticationFilter {

    /**
     * Default URL for processing the login.
     */
    static final String DEFAULT_PROCESS_URL = "/login/cas"; //$NON-NLS-1$

    private final String organizationName;

    /**
     * Construct a filter with the provider authentication manager.
     *
     * @param organizationName      the name of the organization to which this filter is associated to.
     * @param authenticationManager the manager to use.
     */
    public DirectCasAuthenticationFilter(String organizationName, AuthenticationManager authenticationManager) {
        this.organizationName = organizationName;
        setFilterProcessesUrl(DEFAULT_PROCESS_URL);
        setAuthenticationManager(authenticationManager);
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
