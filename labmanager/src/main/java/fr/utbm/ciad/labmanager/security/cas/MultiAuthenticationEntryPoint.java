package fr.utbm.ciad.labmanager.security.cas;

import com.google.common.base.Strings;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

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
 * @since 4.0
 */
public class MultiAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    //TODO Remove if duplicate of another constant
    private static final String LOGIN_PAGE = "/LabManager/login"; //$NON-NLS-1$

    private final Map<String, DirectCasAuthenticationEntryPoint> entryPoints;

    /**
     * Create a new instance with the given CAS authentication entry points.
     *
     * @param entryPoints the entry points to redirect to.
     */
    public MultiAuthenticationEntryPoint(Stream<DirectCasAuthenticationEntryPoint> entryPoints) {
        assert entryPoints != null;
        this.entryPoints = entryPoints.collect(Collectors.toMap(it -> it.getOrganizationName(), it -> it));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (final var entryPoint : this.entryPoints.values()) {
            entryPoint.afterPropertiesSet();
        }
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //
        // Redirect the user to the correct CAS server based on the organization parameter or to the login page.
        //
        final var param = request.getParameter(MultiAuthenticationFilter.ORGANIZATION_PARAMETER_NAME);
        if (!Strings.isNullOrEmpty(param)) {
            final var entryPoint = this.entryPoints.get(param);
            if (entryPoint != null) {
                entryPoint.commence(request, response, authException);
                return;
            }
        }
        response.sendRedirect(LOGIN_PAGE);
    }

}