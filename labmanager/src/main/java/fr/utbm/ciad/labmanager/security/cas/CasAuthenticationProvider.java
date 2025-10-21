package fr.utbm.ciad.labmanager.security.cas;

import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Abstract class for CAS authentication provider.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class CasAuthenticationProvider extends org.springframework.security.cas.authentication.CasAuthenticationProvider {

    /**
     * Create a new instance with the given user details service, service properties, ticket validator and key.
     *
     * @param userDetailsService the user details service.
     * @param serviceProperties  the service properties.
     * @param ticket             the ticket validator.
     * @param key                the key associated to the server.
     */
    public CasAuthenticationProvider(
            UserDetailsService userDetailsService,
            ServiceProperties serviceProperties,
            TicketValidator ticket,
            String key) {
        setUserDetailsService(userDetailsService);
        setAuthenticationUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
        setServiceProperties(serviceProperties);
        setTicketValidator(ticket);
        setKey(key);
    }

}