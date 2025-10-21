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

package fr.utbm.ciad.labmanager.security;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import fr.utbm.ciad.labmanager.Constants;
import fr.utbm.ciad.labmanager.security.cas.*;
import fr.utbm.ciad.labmanager.views.appviews.login.DevelopperLoginView;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuration of the security for API and Vaadin.
 *
 * @author $Author: sgalland$
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@EnableWebSecurity
@Configuration
@EnableConfigurationProperties
public class SecurityConfiguration extends VaadinWebSecurity {

    private static final String API_URL = "/api/v" + Constants.MANAGER_MAJOR_VERSION + "/**/*"; //$NON-NLS-1$ //$NON-NLS-2$

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CasServerConfigurations casServers;

    // TODO Security check
    @Value("${labmanager.disable-cas-login}")
    private boolean disableCasLogin;

    private Logger logger;

    /**
     * Create a ticket validator.
     *
     * @param url the URL of the CAS server.
     * @return the ticket validator.
     */
    private static TicketValidator ticketValidator(String url) {
        return new Cas30ServiceTicketValidator(url);
    }

    /**
     * Create the service properties.
     * This is used to configure the CAS service.
     *
     * @param url the URL of the service.
     * @return the service properties.
     */
    private static ServiceProperties serviceProperties(String url) {
        final var serviceProperties = new ServiceProperties();
        serviceProperties.setService(url);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    private synchronized Logger getLogger() {
        if (this.logger == null) {
            this.logger = LoggerFactory.getLogger("[/SecurityManager]"); //$NON-NLS-1$
        }
        return this.logger;
    }

    private boolean isCasLoginDisabled() {
        return this.disableCasLogin;
    }

    /**
     * The default password encoder when using local security system.
     *
     * @return the encoder.
     */
    @SuppressWarnings("static-method")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // TODO return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Invoked when an authenticated user has successsfully logged in.
     *
     * @param event the authenticated success event.
     */
    @EventListener
    public void onLoginSuccesss(AuthenticationSuccessEvent event) {
        getLogger().info("Login success for " + event.getAuthentication().getName()); //$NON-NLS-1$
    }

    /**
     * Invoked when an authenticated user has successsfully logged in.
     *
     * @param event the authenticated success event.
     */
    @EventListener
    public void onLoginFailure(AbstractAuthenticationFailureEvent event) {
        getLogger().info("Login error for " + event.getAuthentication().getName()); //$NON-NLS-1$
    }

    /**
     * Configure the security.
     *
     * @param http the http security.
     * @throws Exception if an error occurs.
     */
    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        if (isCasLoginDisabled()) {
            http.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(new AntPathRequestMatcher("/api/**/*")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/images/**/*")).permitAll() //$NON-NLS-1$
                    .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll() //$NON-NLS-1$
                    .requestMatchers(new AntPathRequestMatcher("/VAADIN/**/")).permitAll() //$NON-NLS-1$
                    .requestMatchers(new AntPathRequestMatcher("/themes/**")).permitAll() //$NON-NLS-1$
                    .requestMatchers(new AntPathRequestMatcher("/themes/**")).permitAll() //$NON-NLS-1$
                    .requestMatchers("/").permitAll() //$NON-NLS-1$
            );

            http.csrf(cfg -> cfg.ignoringRequestMatchers(new AntPathRequestMatcher(API_URL)));

            super.configure(http);

            setLoginView(http, DevelopperLoginView.class);
        } else {
            //
            // Override the default configuration to permit access to the login page bypass the vaadin configure.
            //
            http.authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers(new AntPathRequestMatcher("/api/**/*")).permitAll()
                            .requestMatchers(new AntPathRequestMatcher("/images/**/*")).permitAll() //$NON-NLS-1$
                            .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll() //$NON-NLS-1$
                            .requestMatchers(new AntPathRequestMatcher("/VAADIN/**/")).permitAll() //$NON-NLS-1$
                            .requestMatchers(new AntPathRequestMatcher("/themes/**")).permitAll() //$NON-NLS-1$
                            .requestMatchers("/login").permitAll() //$NON-NLS-1$
                            .requestMatchers("/").permitAll());//$NON-NLS-1$

            http.csrf(AbstractHttpConfigurer::disable);
        }
    }

    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (isCasLoginDisabled()) {
            return super.filterChain(http);
        }

        //
        // Override the default configuration to add the CAS authentication.
        //
        this.configure(http);

        http
                .addFilter(getMultiFilter())
                .addFilterBefore(new SingleSignOutFilter(), CasAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(getMultiAuthenticationEntryPoint()));

        http
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //$NON-NLS-1$
                                .addLogoutHandler(new SecurityContextLogoutHandler())
                                .logoutSuccessUrl("/").permitAll()); //$NON-NLS-1$

        return http.build();
    }

    /**
     * Create the common filter.
     * This filter is used to redirect the user to the correct CAS server based on the organization parameter or to the login page.
     *
     * @return the common filter.
     */
    @Bean
    public MultiAuthenticationFilter getMultiFilter() {
        final var filters = this.casServers.entrySet().stream().map(pair -> {
            final var name = pair.getKey();
            final var config = pair.getValue();
            final var serviceProperties = serviceProperties(config.getService());
            final var ticketValidator = ticketValidator(config.getBase());
            final var casKey = config.getKey();
            final var provider = new CasAuthenticationProvider(
                    this.userDetailsService,
                    serviceProperties,
                    ticketValidator,
                    casKey);
            final var manager = new ProviderManager(provider);
            final var filter = new DirectCasAuthenticationFilter(name, manager);
            return filter;
        });
        return new MultiAuthenticationFilter(filters);
    }

    /**
     * First step of the CAS authentication process.
     * Create the common authentication entry point that will redirect to the correct CAS server.
     *
     * @return the authentication entry point.
     */
    @Bean
    public MultiAuthenticationEntryPoint getMultiAuthenticationEntryPoint() {
        final var entryPoints = this.casServers.entrySet().stream().map(pair -> {
            final var name = pair.getKey();
            final var config = pair.getValue();
            final var login = config.getLogin();
            final var serviceProperties = serviceProperties(config.getService());
            final var entryPoint = new DirectCasAuthenticationEntryPoint(name, login, serviceProperties);
            return entryPoint;
        });
        return new MultiAuthenticationEntryPoint(entryPoints);
    }

    /**
     * Provide a filter for signing out.
     *
     * @return the filter.
     */
    @SuppressWarnings("static-method")
    @Bean
    public SingleSignOutFilter getSingleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    /**
     * Provide a filter for signing out.
     *
     * @return the filter.
     */
    @Bean
    public LogoutFilter getLogoutFilter() {
        // FIXME Implements logout mechanism for CAS
        final var logoutFilter = new LogoutFilter("", new SecurityContextLogoutHandler()); //$NON-NLS-1$
        return logoutFilter;
    }

}
