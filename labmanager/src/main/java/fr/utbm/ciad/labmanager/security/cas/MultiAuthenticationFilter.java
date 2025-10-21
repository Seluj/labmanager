package fr.utbm.ciad.labmanager.security.cas;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.cas.web.CasAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Redirect the user to the correct CAS server based on the organization parameter or to the login page.
 *
 * @author $Author: jferlin$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see DirectCasAuthenticationFilter
 * @since 4.0
 */
public class MultiAuthenticationFilter extends CasAuthenticationFilter {

    static final String ORGANIZATION_PARAMETER_NAME = "organization"; //$NON-NLS-1$

    private final Map<String, DirectCasAuthenticationFilter> filters;

    /**
     * Construct a multi-authentication filter.
     *
     * @param filters the filters to delegate to. Each filter must have an associated organization name.
     */
    public MultiAuthenticationFilter(Stream<DirectCasAuthenticationFilter> filters) {
        assert filters != null;
        this.filters = filters.collect(Collectors.toMap(it -> it.getOrganizationName(), it -> it));
        setFilterProcessesUrl(DirectCasAuthenticationFilter.DEFAULT_PROCESS_URL);
    }

    @Override
    public void afterPropertiesSet() {
        for (final var filter : this.filters.values()) {
            filter.afterPropertiesSet();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final var requiresAuthentication = super.requiresAuthentication(request, response);
        if (!requiresAuthentication) {
            chain.doFilter(request, response);
        } else {
            final var organizationParam = request.getParameter(ORGANIZATION_PARAMETER_NAME);
            final var filter = this.filters.get(organizationParam);
            if (filter != null) {
                filter.doFilter(request, response, chain);
            } else {
                this.logger.warn("Undefined organization for the multi authentication filter: " + organizationParam); //$NON-NLS-1$
            }
        }
    }

}