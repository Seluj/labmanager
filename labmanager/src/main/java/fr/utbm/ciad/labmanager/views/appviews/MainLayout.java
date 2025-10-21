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

package fr.utbm.ciad.labmanager.views.appviews;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.data.publication.AuthorshipRepository;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.appviews.about.AboutView;
import fr.utbm.ciad.labmanager.views.appviews.assocstructures.AssociatedStructuresListView;
import fr.utbm.ciad.labmanager.views.appviews.charts.ChartsView;
import fr.utbm.ciad.labmanager.views.appviews.conferences.ConferencesListView;
import fr.utbm.ciad.labmanager.views.appviews.culture.ScientificCultureActionsListView;
import fr.utbm.ciad.labmanager.views.appviews.dashboard.DashboardView;
import fr.utbm.ciad.labmanager.views.appviews.database.DatabaseCheckSimilarityView;
import fr.utbm.ciad.labmanager.views.appviews.database.DatabaseInputOutputView;
import fr.utbm.ciad.labmanager.views.appviews.exports.ReportExportView;
import fr.utbm.ciad.labmanager.views.appviews.invitations.IncomingInvitationsListView;
import fr.utbm.ciad.labmanager.views.appviews.invitations.OutgoingInvitationsListView;
import fr.utbm.ciad.labmanager.views.appviews.journals.JournalsListView;
import fr.utbm.ciad.labmanager.views.appviews.jurys.JuryMembershipsListView;
import fr.utbm.ciad.labmanager.views.appviews.memberships.MembershipsListView;
import fr.utbm.ciad.labmanager.views.appviews.organizations.AddressesListView;
import fr.utbm.ciad.labmanager.views.appviews.organizations.OrganizationsListView;
import fr.utbm.ciad.labmanager.views.appviews.persons.MyProfileView;
import fr.utbm.ciad.labmanager.views.appviews.persons.PersonsListView;
import fr.utbm.ciad.labmanager.views.appviews.projects.PatentsListView;
import fr.utbm.ciad.labmanager.views.appviews.projects.ProjectsListView;
import fr.utbm.ciad.labmanager.views.appviews.projects.ReportsListView;
import fr.utbm.ciad.labmanager.views.appviews.projects.ToolsListView;
import fr.utbm.ciad.labmanager.views.appviews.publications.ScientificEditionsListView;
import fr.utbm.ciad.labmanager.views.appviews.publications.ScientificPublicationsListView;
import fr.utbm.ciad.labmanager.views.appviews.publications.ScientificTalksListView;
import fr.utbm.ciad.labmanager.views.appviews.scientificaxes.ScientificAxesListView;
import fr.utbm.ciad.labmanager.views.appviews.supervisions.SupervisionsListView;
import fr.utbm.ciad.labmanager.views.appviews.teaching.TeachingActivitiesListView;
import fr.utbm.ciad.labmanager.views.appviews.teaching.TeachingPublicationsListView;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import fr.utbm.ciad.labmanager.views.components.addons.localization.LanguageSelect;
import fr.utbm.ciad.labmanager.views.components.dashboard.Reporting;
import fr.utbm.ciad.labmanager.views.components.users.UserIdentityChangedObserver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The main view is a top-level placeholder for other views.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class MainLayout extends AppLayout implements LocaleChangeObserver, UserIdentityChangedObserver {

    private static final long serialVersionUID = 4459306874348372506L;
    private static Reporting reporting;
    private final String applicationName;
    private final AuthenticatedUser authenticatedUser;
    private final AuthorshipRepository authorshipRepository;
    private final AccessAnnotationChecker accessChecker;
    private final MessageSourceAccessor messages;
    private final boolean disableCasLogin;
    private H2 viewTitle;
    private Anchor loginLink;

    private MenuItem logoutLink;

    private MenuItem languagesLink;

    private LanguageRecord[] languageLinks;

    private Avatar avatar;

    private Text username;

    private Span roleInUserMenu;

    private SideNavItem positionSection;

    private SideNavItem myprofile;

    private SideNavItem allpersons;

    private SideNavItem positions;

    private SideNavItem scientificActivitySection;

    private SideNavItem scientificPublications;

    private SideNavItem supervisions;

    private SideNavItem journals;

    private SideNavItem conferences;

    private SideNavItem projectSection;

    private SideNavItem projects;

    private SideNavItem patents;

    private SideNavItem associatedStructures;

    private SideNavItem reports;

    private SideNavItem tools;

    private SideNavItem diffusionSection;

    private SideNavItem scientificEditions;

    private SideNavItem outgoingInvitations;

    private SideNavItem incomingInvitations;

    private SideNavItem juryMemberships;

    private SideNavItem scientificTalks;

    private SideNavItem cultureSection;

    private SideNavItem cultureActions;

    private SideNavItem teachingSection;

    private SideNavItem teachingActivites;

    private SideNavItem teachingPublications;

    private SideNavItem organizationsSection;

    private SideNavItem organizations;

    private SideNavItem scientificAxes;

    private SideNavItem addresses;

    private SideNavItem exportingSection;

    private SideNavItem exportingReports;

    private SideNavItem databaseSection;

    private SideNavItem databaseInputOutput;

    private SideNavItem documentationSection;

    private SideNavItem documentation;

    private SideNavItem about;

    private SideNavItem statisticSection;

    private SideNavItem charts;

    private SideNavItem databaseSimilarity;

    /**
     * Constructor.
     *
     * @param authenticatedUser the logged-in user.
     * @param accessChecker     the access checker.
     * @param messages          the accessor to the localized messages in the Spring framework.
     * @param applicationName   name of the application.
     * @param disableCasLogin   indicates if the CAS login is disabled in the global configuration.
     */
    public MainLayout(@Autowired AuthenticatedUser authenticatedUser, @Autowired AccessAnnotationChecker accessChecker,
                      @Autowired MessageSourceAccessor messages, @Value("${labmanager.application-name}") String applicationName,
                      @Value("${labmanager.disable-cas-login}") boolean disableCasLogin,
                      @Autowired AuthorshipRepository authorshipRepository) {
        this.messages = messages;
        this.authenticatedUser = authenticatedUser;
        this.disableCasLogin = disableCasLogin;
        this.accessChecker = accessChecker;
        this.applicationName = applicationName;
        this.authorshipRepository = authorshipRepository;
        setPrimarySection(Section.DRAWER);
        addNavigationPanel();
        addViewHeader();
    }

    public static void refreshReporting() {
        reporting.refresh();
    }

    /**
     * Add the title of the view, with the menu toggle button and the view title.
     */
    protected void addViewHeader() {
        final var toggle = new DrawerToggle();
        toggle.setAriaLabel(getTranslation("views.menu_toggle")); //$NON-NLS-1$
        this.viewTitle = new H2();
        this.viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Whitespace.NOWRAP);
        reporting = new Reporting(authorshipRepository, authenticatedUser);
        addToNavbar(true, toggle, this.viewTitle, reporting);
    }

    /**
     * Add the navigation panel, with the application header, the menus, and the navigation footer.
     */
    protected void addNavigationPanel() {
        final var scroller = new Scroller(createNavigationPanelContent());
        addToDrawer(createNavigationPanelHeader(), scroller, createNavigationPanelFooter());
    }

    /**
     * Create the welcome items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createWelcomeNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new SideNavItem("Welcome", DashboardView.class));
        }
    }

    /**
     * Create the profile and career items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createProfileNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(MyProfileView.class)
                || this.accessChecker.hasAccess(PersonsListView.class)
                || this.accessChecker.hasAccess(MembershipsListView.class)) {
            this.positionSection = new SideNavItem(null);
            if (this.accessChecker.hasAccess(MyProfileView.class)) {
                this.myprofile = new SideNavItem(null, MyProfileView.class, LineAwesomeIcon.USER_CIRCLE.create());
                this.positionSection.addItem(this.myprofile);
            }
            if (this.accessChecker.hasAccess(PersonsListView.class)) {
                this.allpersons = new SideNavItem(null, PersonsListView.class, LineAwesomeIcon.USERS_SOLID.create());
                this.positionSection.addItem(this.allpersons);
            }
            if (this.accessChecker.hasAccess(MembershipsListView.class)) {
                this.positions = new SideNavItem("", MembershipsListView.class, LineAwesomeIcon.ID_CARD_ALT_SOLID.create()); //$NON-NLS-1$
                this.positionSection.addItem(this.positions);
            }
            nav.addItem(this.positionSection);
        }
    }

    /**
     * Create the scientific activities items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createScientificActivitiesNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(ScientificPublicationsListView.class)
                || this.accessChecker.hasAccess(SupervisionsListView.class)
                || this.accessChecker.hasAccess(JournalsListView.class)
                || this.accessChecker.hasAccess(ConferencesListView.class)) {
            this.scientificActivitySection = new SideNavItem(null);
            if (this.accessChecker.hasAccess(ScientificPublicationsListView.class)) {
                this.scientificPublications = new SideNavItem("", ScientificPublicationsListView.class, LineAwesomeIcon.BOOK_SOLID.create()); //$NON-NLS-1$
                this.scientificActivitySection.addItem(this.scientificPublications);
            }
            if (this.accessChecker.hasAccess(SupervisionsListView.class)) {
                this.supervisions = new SideNavItem("", SupervisionsListView.class, LineAwesomeIcon.GRADUATION_CAP_SOLID.create()); //$NON-NLS-1$
                this.scientificActivitySection.addItem(this.supervisions);
            }
            if (this.accessChecker.hasAccess(JournalsListView.class)) {
                this.journals = new SideNavItem("", JournalsListView.class, LineAwesomeIcon.NEWSPAPER_SOLID.create()); //$NON-NLS-1$
                this.scientificActivitySection.addItem(this.journals);
            }
            if (this.accessChecker.hasAccess(ConferencesListView.class)) {
                this.conferences = new SideNavItem("", ConferencesListView.class, LineAwesomeIcon.CHALKBOARD_TEACHER_SOLID.create()); //$NON-NLS-1$
                this.scientificActivitySection.addItem(this.conferences);
            }
            nav.addItem(this.scientificActivitySection);
        }
    }

    /**
     * Create the project and innovation items in the navigation bar.
     *
     * @param nav the navigation bar.
     */
    protected void createProjectInnovationNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(ProjectsListView.class)
                || this.accessChecker.hasAccess(PatentsListView.class)
                || this.accessChecker.hasAccess(ReportsListView.class)
                || this.accessChecker.hasAccess(ToolsListView.class)
                || this.accessChecker.hasAccess(AssociatedStructuresListView.class)) {
            this.projectSection = new SideNavItem(""); //$NON-NLS-1$
            if (this.accessChecker.hasAccess(ProjectsListView.class)) {
                this.projects = new SideNavItem("", ProjectsListView.class, LineAwesomeIcon.CUBES_SOLID.create()); //$NON-NLS-1$
                this.projectSection.addItem(this.projects);
            }
            if (this.accessChecker.hasAccess(PatentsListView.class)) {
                this.patents = new SideNavItem("", PatentsListView.class, LineAwesomeIcon.REGISTERED_SOLID.create()); //$NON-NLS-1$
                this.projectSection.addItem(this.patents);
            }
            if (this.accessChecker.hasAccess(AssociatedStructuresListView.class)) {
                this.associatedStructures = new SideNavItem("", AssociatedStructuresListView.class, LineAwesomeIcon.LINK_SOLID.create()); //$NON-NLS-1$
                this.projectSection.addItem(this.associatedStructures);
            }
            if (this.accessChecker.hasAccess(ReportsListView.class)) {
                this.reports = new SideNavItem("", ReportsListView.class, LineAwesomeIcon.BOOK_SOLID.create()); //$NON-NLS-1$
                this.projectSection.addItem(this.reports);
            }
            if (this.accessChecker.hasAccess(ToolsListView.class)) {
                this.tools = new SideNavItem("", ToolsListView.class, LineAwesomeIcon.TOOLBOX_SOLID.create()); //$NON-NLS-1$
                this.projectSection.addItem(this.tools);
            }
            nav.addItem(this.projectSection);
        }
    }

    /**
     * Create the scientific influence items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createScientificInfluenceNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(IncomingInvitationsListView.class)
                || this.accessChecker.hasAccess(OutgoingInvitationsListView.class)
                || this.accessChecker.hasAccess(ScientificEditionsListView.class)
                || this.accessChecker.hasAccess(ScientificTalksListView.class)
                || this.accessChecker.hasAccess(JuryMembershipsListView.class)) {
            this.diffusionSection = new SideNavItem(""); //$NON-NLS-1$
            if (this.accessChecker.hasAccess(ScientificEditionsListView.class)) {
                this.scientificEditions = new SideNavItem("", ScientificEditionsListView.class, LineAwesomeIcon.JOURNAL_WHILLS_SOLID.create()); //$NON-NLS-1$
                this.diffusionSection.addItem(this.scientificEditions);
            }
            if (this.accessChecker.hasAccess(IncomingInvitationsListView.class)) {
                this.incomingInvitations = new SideNavItem("", IncomingInvitationsListView.class, LineAwesomeIcon.SIGN_IN_ALT_SOLID.create()); //$NON-NLS-1$
                this.diffusionSection.addItem(this.incomingInvitations);
            }
            if (this.accessChecker.hasAccess(OutgoingInvitationsListView.class)) {
                this.outgoingInvitations = new SideNavItem("", OutgoingInvitationsListView.class, LineAwesomeIcon.SIGN_OUT_ALT_SOLID.create()); //$NON-NLS-1$
                this.diffusionSection.addItem(this.outgoingInvitations);
            }
            if (this.accessChecker.hasAccess(JuryMembershipsListView.class)) {
                this.juryMemberships = new SideNavItem("", JuryMembershipsListView.class, LineAwesomeIcon.USER_GRADUATE_SOLID.create()); //$NON-NLS-1$
                this.diffusionSection.addItem(this.juryMemberships);
            }
            if (this.accessChecker.hasAccess(ScientificTalksListView.class)) {
                this.scientificTalks = new SideNavItem("", ScientificTalksListView.class, LineAwesomeIcon.CHALKBOARD_TEACHER_SOLID.create()); //$NON-NLS-1$
                this.diffusionSection.addItem(this.scientificTalks);
            }
            nav.addItem(this.diffusionSection);
        }
    }

    /**
     * Create the scientific culture items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createScientificCultureNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(ScientificCultureActionsListView.class)) {
            this.cultureSection = new SideNavItem(""); //$NON-NLS-1$
            if (this.accessChecker.hasAccess(ScientificCultureActionsListView.class)) {
                this.cultureActions = new SideNavItem("", ScientificCultureActionsListView.class, LineAwesomeIcon.CHALKBOARD_TEACHER_SOLID.create()); //$NON-NLS-1$
                this.cultureSection.addItem(this.cultureActions);
            }
            nav.addItem(this.cultureSection);
        }
    }

    /**
     * Create the teaching items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createTeachingNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(TeachingActivitiesListView.class)
                || this.accessChecker.hasAccess(TeachingPublicationsListView.class)) {
            this.teachingSection = new SideNavItem(""); //$NON-NLS-1$
            if (this.accessChecker.hasAccess(TeachingActivitiesListView.class)) {
                this.teachingActivites = new SideNavItem("", TeachingActivitiesListView.class, LineAwesomeIcon.CHALKBOARD_TEACHER_SOLID.create()); //$NON-NLS-1$
                this.teachingSection.addItem(this.teachingActivites);
            }
            if (this.accessChecker.hasAccess(TeachingPublicationsListView.class)) {
                this.teachingPublications = new SideNavItem("", TeachingPublicationsListView.class, LineAwesomeIcon.BOOK_SOLID.create()); //$NON-NLS-1$
                this.teachingSection.addItem(this.teachingPublications);
            }
            nav.addItem(this.teachingSection);
        }
    }

    /**
     * Create the structure management items in the navigation bar.
     *
     * @param nav the navigation bar.
     */
    protected void createStructureManagementNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(OrganizationsListView.class)
                || this.accessChecker.hasAccess(ScientificAxesListView.class)
                || this.accessChecker.hasAccess(AddressesListView.class)) {
            this.organizationsSection = new SideNavItem(""); //$NON-NLS-1$
            if (this.accessChecker.hasAccess(OrganizationsListView.class)) {
                this.organizations = new SideNavItem("", OrganizationsListView.class, LineAwesomeIcon.UNIVERSITY_SOLID.create()); //$NON-NLS-1$
                this.organizationsSection.addItem(this.organizations);
            }
            if (this.accessChecker.hasAccess(ScientificAxesListView.class)) {
                this.scientificAxes = new SideNavItem("", ScientificAxesListView.class, LineAwesomeIcon.LAYER_GROUP_SOLID.create()); //$NON-NLS-1$
                this.organizationsSection.addItem(this.scientificAxes);
            }
            if (this.accessChecker.hasAccess(AddressesListView.class)) {
                this.addresses = new SideNavItem("", AddressesListView.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()); //$NON-NLS-1$
                this.organizationsSection.addItem(this.addresses);
            }
            nav.addItem(this.organizationsSection);
        }
    }

    /**
     * Create the export management items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createExportNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(ReportExportView.class)) {
            this.exportingSection = new SideNavItem(""); //$NON-NLS-1$
            this.exportingReports = new SideNavItem("", ReportExportView.class, LineAwesomeIcon.DOWNLOAD_SOLID.create()); //$NON-NLS-1$
            this.exportingSection.addItem(this.exportingReports);
            nav.addItem(this.exportingSection);
        }
    }

    /**
     * Create the database management items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createDatabaseNavigation(SideNav nav) {
        if (this.accessChecker.hasAccess(DatabaseInputOutputView.class)) {
            this.databaseSection = new SideNavItem(""); //$NON-NLS-1$
            this.databaseInputOutput = new SideNavItem("", DatabaseInputOutputView.class, LineAwesomeIcon.DATABASE_SOLID.create()); //$NON-NLS-1$
            this.databaseSection.addItem(this.databaseInputOutput);
            this.databaseSimilarity = new SideNavItem("", DatabaseCheckSimilarityView.class, LineAwesomeIcon.ANGLE_DOUBLE_RIGHT_SOLID.create()); //$NON-NLS-1$
            this.databaseSection.addItem(this.databaseSimilarity);

            nav.addItem(this.databaseSection);
        }
    }

    /**
     * Create the documentation items in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createDocumentationNavigation(SideNav nav) {
        this.documentationSection = new SideNavItem(""); //$NON-NLS-1$
        this.documentation = new SideNavItem("", ViewConstants.ONLINE_MANUAL_URL, LineAwesomeIcon.BOOK_SOLID.create()); //$NON-NLS-1$
        this.documentationSection.addItem(this.documentation);
        this.about = new SideNavItem("", AboutView.class, LineAwesomeIcon.COPYRIGHT_SOLID.create()); //$NON-NLS-1$
        this.documentationSection.addItem(this.about);
        nav.addItem(this.documentationSection);
    }

    /**
     * Create the menus in the navigation panel.
     *
     * @param nav the navigation panel.
     */
    protected void createStatisticNavigation(SideNav nav) {
        this.statisticSection = new SideNavItem(""); //$NON-NLS-1$
        this.charts = new SideNavItem("", ChartsView.class, LineAwesomeIcon.CHART_BAR.create()); //$NON-NLS-1$
        this.statisticSection.addItem(this.charts);
        nav.addItem(this.statisticSection);
    }

    /**
     * Create the menus in the navigation panel.
     *
     * @return the created navigation panel.
     */
    protected SideNav createNavigationPanelContent() {
        final var nav = new SideNav();
        createWelcomeNavigation(nav);
        createProfileNavigation(nav);
        createScientificActivitiesNavigation(nav);
        createProjectInnovationNavigation(nav);
        createScientificInfluenceNavigation(nav);
        createScientificCultureNavigation(nav);
        createTeachingNavigation(nav);
        createStructureManagementNavigation(nav);
        createExportNavigation(nav);
        createDatabaseNavigation(nav);
        createDocumentationNavigation(nav);
        createStatisticNavigation(nav);
        return nav;
    }

    /**
     * Create the header of the navigation panel.
     *
     * @return the header of the navigation panel.
     */
    protected Header createNavigationPanelHeader() {
        final var appName = new H1(this.applicationName);
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        return new Header(appName);
    }

    /**
     * Create the footer of the navigation panel.
     *
     * @return the footer of the navigation panel.
     */
    protected Footer createNavigationPanelFooter() {
        this.avatar = null;
        this.username = null;
        this.loginLink = null;
        this.logoutLink = null;
        this.languageLinks = null;

        final var layout = new Footer();
        final var maybeUser = this.authenticatedUser.get();
        if (maybeUser.isPresent()) {
            final var user = maybeUser.get();
            final var person = user.getPerson();

            this.avatar = new Avatar(person.getFullName());
            final var photo = person.getPhotoURL();
            if (photo != null) {
                this.avatar.setImage(photo.toExternalForm());
            } else {
                this.avatar.setImage(null);
            }
            this.avatar.setThemeName("xsmall"); //$NON-NLS-1$
            this.avatar.getElement().setAttribute("tabindex", "-1"); //$NON-NLS-1$//$NON-NLS-2$

            this.username = new Text(person.getFullName());

            final var userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast"); //$NON-NLS-1$

            final var userName = userMenu.addItem(""); //$NON-NLS-1$
            final var div = new Div();
            div.add(this.avatar);
            div.add(this.username);
            div.add(new Icon("lumo", "dropdown")); //$NON-NLS-1$//$NON-NLS-2$
            div.getElement().getStyle().set("display", "flex"); //$NON-NLS-1$//$NON-NLS-2$
            div.getElement().getStyle().set("align-items", "center"); //$NON-NLS-1$//$NON-NLS-2$
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)"); //$NON-NLS-1$//$NON-NLS-2$
            userName.add(div);

            createUserMenu(userName.getSubMenu());

            layout.add(userMenu);
        } else {
            this.loginLink = new Anchor("login", "");  //$NON-NLS-1$ //$NON-NLS-2$
            layout.add(this.loginLink);
        }
        return layout;
    }

    /**
     * Invoked for creating the user menu.
     *
     * @param mainMenu the user menu to fill up.
     */
    protected void createUserMenu(SubMenu mainMenu) {
        //
        // User role
        //
        this.roleInUserMenu = new Span();
        mainMenu.add(this.roleInUserMenu);

        //
        // Language selection
        //
        this.languagesLink = mainMenu.addItem(""); //$NON-NLS-1$s
        final var locales = LanguageSelect.getAvailableLocales();
        this.languageLinks = new LanguageRecord[locales.length];
        final var languageSubMenu = this.languagesLink.getSubMenu();
        final var currentLang = getLocale().getLanguage();
        final var langItems = new ArrayList<MenuItem>(locales.length);
        MenuItem currentLanguageItem = null;
        int i = 0;
        for (final var locale : locales) {
            final var hl = new HorizontalLayout();
            hl.setSpacing(false);
            hl.setAlignItems(Alignment.CENTER);

            final var countryCode = CountryCode.fromLocale(locale);
            final var flag = new CountryFlag(countryCode);
            flag.setSizeFromHeight(2, Unit.EX);
            flag.getStyle().set("margin-right", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$

            final var text = new Span(""); //$NON-NLS-1$
            this.languageLinks[i] = new LanguageRecord(locale, text);
            hl.add(flag, text);
            final var menuItem = languageSubMenu.addItem(hl, e -> {
                final var current = e.getSource();
                langItems.stream().filter(it -> it != current).forEach(it -> it.setChecked(false));
                LanguageSelect.setUILocale(locale);
            });
            menuItem.setCheckable(true);
            langItems.add(menuItem);
            if (currentLanguageItem == null && currentLang.equalsIgnoreCase(locale.getLanguage())) {
                currentLanguageItem = menuItem;
            }
            ++i;
        }
        if (currentLanguageItem != null) {
            currentLanguageItem.setChecked(true);
        }

        //
        // Logout
        //
        this.logoutLink = mainMenu.addItem("", e -> { //$NON-NLS-1$
            logout();
        });
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        this.viewTitle.setText(getCurrentPageTitle());
    }

    /**
     * Replies the title of the current page.
     *
     * @return the current page title.
     */
    protected String getCurrentPageTitle() {
        final var obj = getContent();
        if (obj == null) {
            return ""; //$NON-NLS-1$
        }
        if (obj instanceof HasDynamicTitle castValue) {
            return castValue.getPageTitle();
        }
        final var title = obj.getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : getTranslation(title.value()); //$NON-NLS-1$
    }

    @Override
    public void authenticatedUserIdentityChange() {
        final var maybeUser = this.authenticatedUser.get();
        if (maybeUser.isPresent()) {
            final var user = maybeUser.get();
            final var person = user.getPerson();
            final var fullname = person.getFullName();
            if (this.avatar != null) {
                this.avatar.setName(fullname);
                final var photo = person.getPhotoURL();
                if (photo != null) {
                    this.avatar.setImage(photo.toExternalForm());
                } else {
                    this.avatar.setImage(null);
                }
            }
            if (this.username != null) {
                this.username.setText(fullname);
            }
            updateUserRoleInMenu(user.getRole());
        }
    }

    private void logout() {
        if (this.authenticatedUser != null && this.disableCasLogin) {
            this.authenticatedUser.logout();
        } else {
            //TODO Move to AuthenticatedUser class?
            UI.getCurrent().getPage().executeJs("window.location.href = 'https://localhost:8080/LabManager/logout'");
        }
    }

    private void updateUserRoleInMenu(UserRole role) {
        if (role != null && this.roleInUserMenu != null) {
            final var roleName = role.getLabel(this.messages, getLocale());
            this.roleInUserMenu.setText(getTranslation("views.roleInUserMenu", roleName)); //$NON-NLS-1$
            String color = null;
            switch (role) {
                case RESPONSIBLE:
                    color = "var(--lumo-success-text-color)"; //$NON-NLS-1$
                    break;
                case ADMIN:
                    color = "var(--lumo-error-text-color)"; //$NON-NLS-1$
                    break;
                case USER:
                default:
                    color = "var(--lumo-primary-text-color)"; //$NON-NLS-1$
                    break;
            }
            this.roleInUserMenu.getStyle().setColor(color);
        }
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        if (this.authenticatedUser != null && this.authenticatedUser.get().isPresent()) {
            updateUserRoleInMenu(this.authenticatedUser.get().get().getRole());
        }

        this.viewTitle.setText(getCurrentPageTitle());
        reporting.setText(getTranslation("views.dashboard.reporting.doi"), getTranslation("views.dashboard.reporting.orcid")); //$NON-NLS-1$ //$NON-NLS-2$

        if (this.positionSection != null) {
            this.positionSection.setLabel(getTranslation("views.navitem.positionSection")); //$NON-NLS-1$
        }
        if (this.myprofile != null) {
            this.myprofile.setLabel(this.getTranslation("views.navitem.my_profile")); //$NON-NLS-1$
        }
        if (this.allpersons != null) {
            this.allpersons.setLabel(getTranslation("views.navitem.all_persons")); //$NON-NLS-1$
        }
        if (this.positions != null) {
            this.positions.setLabel(getTranslation("views.navitem.positions")); //$NON-NLS-1$
        }

        if (this.scientificActivitySection != null) {
            this.scientificActivitySection.setLabel(getTranslation("views.navitem.scientificactivitiesSection")); //$NON-NLS-1$
        }
        if (this.supervisions != null) {
            this.supervisions.setLabel(getTranslation("views.navitem.supervisions")); //$NON-NLS-1$
        }
        if (this.scientificPublications != null) {
            this.scientificPublications.setLabel(getTranslation("views.navitem.publications")); //$NON-NLS-1$
        }
        if (this.journals != null) {
            this.journals.setLabel(getTranslation("views.navitem.journals")); //$NON-NLS-1$
        }
        if (this.conferences != null) {
            this.conferences.setLabel(getTranslation("views.navitem.conferences")); //$NON-NLS-1$
        }

        if (this.projectSection != null) {
            this.projectSection.setLabel(getTranslation("views.navitem.projectSection")); //$NON-NLS-1$
        }
        if (this.projects != null) {
            this.projects.setLabel(getTranslation("views.navitem.projects")); //$NON-NLS-1$
        }
        if (this.patents != null) {
            this.patents.setLabel(getTranslation("views.navitem.patents")); //$NON-NLS-1$
        }
        if (this.associatedStructures != null) {
            this.associatedStructures.setLabel(getTranslation("views.navitem.associated_structures")); //$NON-NLS-1$
        }
        if (this.reports != null) {
            this.reports.setLabel(getTranslation("views.navitem.reports")); //$NON-NLS-1$
        }
        if (this.tools != null) {
            this.tools.setLabel(getTranslation("views.navitem.tools")); //$NON-NLS-1$
        }

        if (this.diffusionSection != null) {
            this.diffusionSection.setLabel(getTranslation("views.navitem.diffusionSection")); //$NON-NLS-1$
        }
        if (this.incomingInvitations != null) {
            this.incomingInvitations.setLabel(getTranslation("views.navitem.incoming_invitations")); //$NON-NLS-1$
        }
        if (this.outgoingInvitations != null) {
            this.outgoingInvitations.setLabel(getTranslation("views.navitem.outgoing_invitations")); //$NON-NLS-1$
        }
        if (this.juryMemberships != null) {
            this.juryMemberships.setLabel(getTranslation("views.navitem.jury_memberships")); //$NON-NLS-1$
        }
        if (this.scientificEditions != null) {
            this.scientificEditions.setLabel(getTranslation("views.navitem.editions")); //$NON-NLS-1$
        }
        if (this.scientificTalks != null) {
            this.scientificTalks.setLabel(getTranslation("views.navitem.talks")); //$NON-NLS-1$
        }

        if (this.cultureSection != null) {
            this.cultureSection.setLabel(getTranslation("views.navitem.cultureSection")); //$NON-NLS-1$
        }
        if (this.cultureActions != null) {
            this.cultureActions.setLabel(getTranslation("views.navitem.scientific_culture_actions")); //$NON-NLS-1$
        }

        if (this.teachingSection != null) {
            this.teachingSection.setLabel(getTranslation("views.navitem.teachingSection")); //$NON-NLS-1$
        }
        if (this.teachingActivites != null) {
            this.teachingActivites.setLabel(getTranslation("views.navitem.teaching_activities")); //$NON-NLS-1$
        }
        if (this.teachingPublications != null) {
            this.teachingPublications.setLabel(getTranslation("views.navitem.teaching_documents")); //$NON-NLS-1$
        }

        if (this.organizationsSection != null) {
            this.organizationsSection.setLabel(getTranslation("views.navitem.organizationsSection")); //$NON-NLS-1$
        }
        if (this.organizations != null) {
            this.organizations.setLabel(getTranslation("views.navitem.organizations")); //$NON-NLS-1$
        }
        if (this.scientificAxes != null) {
            this.scientificAxes.setLabel(getTranslation("views.navitem.scientific_axes")); //$NON-NLS-1$
        }
        if (this.addresses != null) {
            this.addresses.setLabel(getTranslation("views.navitem.addresses")); //$NON-NLS-1$
        }

        if (this.exportingSection != null) {
            this.exportingSection.setLabel(getTranslation("views.navitem.export")); //$NON-NLS-1$
        }
        if (this.exportingReports != null) {
            this.exportingReports.setLabel(getTranslation("views.navitem.export.reports")); //$NON-NLS-1$
        }

        if (this.databaseSection != null) {
            this.databaseSection.setLabel(getTranslation("views.navitem.database")); //$NON-NLS-1$
        }
        if (this.databaseInputOutput != null) {
            this.databaseInputOutput.setLabel(getTranslation("views.navitem.database_io")); //$NON-NLS-1$
        }
        if (this.databaseSimilarity != null) {
            this.databaseSimilarity.setLabel(getTranslation("views.navitem.database_similarity")); //$NON-NLS-1$
        }


        if (this.documentationSection != null) {
            this.documentationSection.setLabel(getTranslation("views.navitem.documentations")); //$NON-NLS-1$
        }
        if (this.documentation != null) {
            this.documentation.setLabel(getTranslation("views.navitem.online_manuals")); //$NON-NLS-1$
        }

        if (this.about != null) {
            this.about.setLabel(getTranslation("views.navitem.about_app")); //$NON-NLS-1$
        }

        if (this.statisticSection != null) {
            this.statisticSection.setLabel(getTranslation("views.navitem.statisticSection")); //$NON-NLS-1$
        }
        if (this.charts != null) {
            this.charts.setLabel(getTranslation("views.navitem.charts")); //$NON-NLS-1$
        }
        if (this.loginLink != null) {
            this.loginLink.setText(getTranslation("views.sign_in")); //$NON-NLS-1$
        }
        if (this.logoutLink != null) {
            this.logoutLink.setText(getTranslation("views.sign_out")); //$NON-NLS-1$
        }
        if (this.languagesLink != null) {
            this.languagesLink.setText(getTranslation("views.languages")); //$NON-NLS-1$
        }
        if (this.languageLinks != null) {
            for (final LanguageRecord menuItem : this.languageLinks) {
                menuItem.menuText.setText(StringUtils.capitalize(menuItem.locale.getDisplayLanguage(getLocale())));
            }
        }
    }

    /**
     * Description of a language menu item.
     *
     * @author $Author: sgalland$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.0
     */
    private record LanguageRecord(Locale locale, Span menuText) {
        //
    }

}
