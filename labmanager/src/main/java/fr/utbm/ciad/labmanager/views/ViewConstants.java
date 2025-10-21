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

package fr.utbm.ciad.labmanager.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.notification.Notification.Position;

/**
 * Constants for the views.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class ViewConstants {

    /**
     * Default size of the icons in the download and upload dialogs.
     *
     * @since 4.0
     */
    public static final String DEFAULT_DIALOG_ICON_WIDTH = "256px"; //$NON-NLS-1$

    /**
     * Default number of cards in a person card grid's row.
     *
     * @since 4.0
     */
    public static final int DEFAULT_PERSON_CARDS_PER_ROW = 5;

    /**
     * Default number of rows in a person card grid.
     *
     * @since 4.0
     */
    public static final int DEFAULT_ROWS_IN_PERSON_CARD_GRID = 4;

    /**
     * Default size of a photo when it is used in a grid of person cards.
     *
     * @since 4.0
     */
    public static final int PHOTO_SIZE_IN_PERSON_CARD_GRID = 256;

    /**
     * Default name fo the backend view theme.
     *
     * @since 4.0
     */
    public static final String DEFAULT_BACKEND_THEME = "labmanager"; //$NON-NLS-1$

    /**
     * Duration of the pop-up notification in milliseconds.
     */
    public static final int DEFAULT_POPUP_DURATION = 20000;

    /**
     * Position of the pop-up notification.
     */
    public static final Position DEFAULT_POPUP_POSITION = Position.BOTTOM_START;

    /**
     * Default height in pixels of the input list.
     */
    public static final float DEFAULT_LIST_HEIGHT = 150f;

    /**
     * Default height in pixels of the text area.
     */
    public static final float DEFAULT_TEXT_AREA_HEIGHT = 200f;

    /**
     * URL of the online manuals.
     */
    public static final String ONLINE_MANUAL_URL = "https://www.ciad-lab.fr/docs/"; //$NON-NLS-1$

    /**
     * URL path of the images with terminal slash.
     */
    public static final String IMAGE_URL_PATH = "/images/"; //$NON-NLS-1$

    /**
     * Path to the logo of UTBM.
     */
    public static final String UTBM_LOGO = IMAGE_URL_PATH + "utbmlogo.svg"; //$NON-NLS-1$

    /**
     * Path to the logo of UB.
     */
    public static final String UB_LOGO = IMAGE_URL_PATH + "ublogo.svg"; //$NON-NLS-1$

    /**
     * Path to the logo of SPIM.
     */
    public static final String SPIM_LOGO = IMAGE_URL_PATH + "spimlogo.svg"; //$NON-NLS-1$

    /**
     * Path to the logo of Carnot ARTS.
     */
    public static final String CARNOT_ARTS_LOGO = IMAGE_URL_PATH + "carnotartslogo.svg"; //$NON-NLS-1$

    /**
     * URL path of the icons with terminal slash.
     */
    public static final String ICON_URL_PATH = "/icons/"; //$NON-NLS-1$

    /**
     * Path to the icon of Gravatar.
     */
    public static final String GRAVATAR_ICON = ICON_URL_PATH + "gravatar.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of ORCID.
     */
    public static final String ORCID_ICON = ICON_URL_PATH + "orcid.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of Scopus.
     */
    public static final String SCOPUS_ICON = ICON_URL_PATH + "scopus.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of Web-of-Science.
     */
    public static final String WOS_ICON = ICON_URL_PATH + "wos.svg"; //$NON-NLS-1$

    /**
     * URL of the WOS information platform, with the terminal slash character.
     */
    public static final String WOSINFO_BASE_URL = "https://wos-journal.info/"; //$NON-NLS-1$

    /**
     * Path to the icon of Scimago.
     */
    public static final String SCIMAGO_ICON = ICON_URL_PATH + "scimago.svg"; //$NON-NLS-1$

    /**
     * URL of the Scimago platform, with the terminal slash character.
     */
    public static final String SCIMAGO_BASE_URL = "https://www.scimagojr.com/"; //$NON-NLS-1$

    /**
     * Path to the icon of CORE Portal.
     */
    public static final String CORE_PORTAL_ICON = ICON_URL_PATH + "coreportal.svg"; //$NON-NLS-1$

    /**
     * URL of the CORE portal, with the terminal slash character.
     */
    public static final String CORE_PORTAL_BASE_URL = "https://www.core.edu.au/conference-portal/"; //$NON-NLS-1$

    /**
     * Path to the icon of Google Scholar.
     */
    public static final String GSCHOLAR_ICON = ICON_URL_PATH + "googlescholar.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of ResearchGate.
     */
    public static final String RESEARCHGATE_ICON = ICON_URL_PATH + "researchgate.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of AD Scientific Index.
     */
    public static final String ADSCIENTIFICINDEX_ICON = ICON_URL_PATH + "adscientificindex.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of DBLP.
     */
    public static final String DBLP_ICON = ICON_URL_PATH + "dblp.svg"; //$NON-NLS-1$

    /**
     * URL of DBLP, with the terminal slash character.
     */
    public static final String DBLP_BASE_URL = "http://dblp.uni-trier.de/"; //$NON-NLS-1$

    /**
     * Path to the icon of Academia.edu.
     */
    public static final String ACADEMIA_EDU_ICON = ICON_URL_PATH + "academia-edu.svg"; //$NON-NLS-1$

    /**
     * URL of Academia.edu, with the terminal slash character.
     */
    public static final String ACADEMIA_EDU_BASE_URL = "https://multiagent.academia.edu/"; //$NON-NLS-1$

    /**
     * Path to the icon of EU CORDIS.
     */
    public static final String EU_CORDIS_ICON = ICON_URL_PATH + "europe.svg"; //$NON-NLS-1$

    /**
     * URL of EU CORDIS, with the terminal slash character.
     */
    public static final String EU_CORDIS_BASE_URL = "https://cordis.europa.eu/"; //$NON-NLS-1$

    /**
     * Path to the icon of LinkedIn.
     */
    public static final String LINKEDIN_ICON = ICON_URL_PATH + "linkedin.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of GitHub.
     */
    public static final String GITHUB_ICON = ICON_URL_PATH + "github.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of GitFacebook.
     */
    public static final String FACEBOOK_ICON = ICON_URL_PATH + "facebook.svg"; //$NON-NLS-1$

    /**
     * Path to the icon of DOI.
     */
    public static final String DOI_ICON = ICON_URL_PATH + "doi.svg"; //$NON-NLS-1$

    /**
     * URL of the DOI portal, with the terminal slash character.
     */
    public static final String DOI_BASE_URL = "https://doi.org/"; //$NON-NLS-1$

    /**
     * Path to the icon of HAL.
     */
    public static final String HAL_ICON = ICON_URL_PATH + "hal.svg"; //$NON-NLS-1$

    /**
     * URL of the HAL portal, with the terminal slash character.
     */
    public static final String HAL_BASE_URL = "https://hal.science/"; //$NON-NLS-1$

    /**
     * Path of the black icon for exporting to JSON.
     *
     * @see #EXPORT_JSON_WHITE_ICON
     */
    public static final String EXPORT_JSON_BLACK_ICON = ICON_URL_PATH + "json_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for exporting to JSON.
     *
     * @see #EXPORT_JSON_BLACK_ICON
     */
    public static final String EXPORT_JSON_WHITE_ICON = ICON_URL_PATH + "json_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for importing from JSON.
     *
     * @see #IMPORT_JSON_WHITE_ICON
     */
    public static final String IMPORT_JSON_BLACK_ICON = ICON_URL_PATH + "json_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for importing from JSON.
     *
     * @see #IMPORT_JSON_BLACK_ICON
     */
    public static final String IMPORT_JSON_WHITE_ICON = ICON_URL_PATH + "json_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for exporting to BibTeX.
     *
     * @see #EXPORT_BIBTEX_WHITE_ICON
     */
    public static final String EXPORT_BIBTEX_BLACK_ICON = ICON_URL_PATH + "bibtex_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for exporting to BibTeX.
     *
     * @see #EXPORT_BIBTEX_BLACK_ICON
     */
    public static final String EXPORT_BIBTEX_WHITE_ICON = ICON_URL_PATH + "bibtex_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for importing from BibTeX.
     *
     * @see #IMPORT_BIBTEX_WHITE_ICON
     */
    public static final String IMPORT_BIBTEX_BLACK_ICON = ICON_URL_PATH + "bibtex_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for importing from BibTeX.
     *
     * @see #IMPORT_BIBTEX_BLACK_ICON
     */
    public static final String IMPORT_BIBTEX_WHITE_ICON = ICON_URL_PATH + "bibtex_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for exporting to RIS.
     *
     * @see #EXPORT_RIS_WHITE_ICON
     */
    public static final String EXPORT_RIS_BLACK_ICON = ICON_URL_PATH + "ris_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for exporting to RIS.
     *
     * @see #EXPORT_RIS_BLACK_ICON
     */
    public static final String EXPORT_RIS_WHITE_ICON = ICON_URL_PATH + "ris_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for importing from RIS.
     *
     * @see #IMPORT_RIS_WHITE_ICON
     */
    public static final String IMPORT_RIS_BLACK_ICON = ICON_URL_PATH + "ris_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for importing from RIS.
     *
     * @see #IMPORT_RIS_BLACK_ICON
     */
    public static final String IMPORT_RIS_WHITE_ICON = ICON_URL_PATH + "ris_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for exporting to OpenDocument Text.
     *
     * @see #EXPORT_ODT_WHITE_ICON
     */
    public static final String EXPORT_ODT_BLACK_ICON = ICON_URL_PATH + "odt_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for exporting to OpenDocument Text.
     *
     * @see #EXPORT_ODT_BLACK_ICON
     */
    public static final String EXPORT_ODT_WHITE_ICON = ICON_URL_PATH + "odt_w_file.svg"; //$NON-NLS-1$

    /**
     * Path of the black icon for exporting to HTML document.
     *
     * @see #EXPORT_HTML_WHITE_ICON
     */
    public static final String EXPORT_HTML_BLACK_ICON = ICON_URL_PATH + "html_file.svg"; //$NON-NLS-1$

    /**
     * Path of the white icon for exporting to HTML document.
     *
     * @see #EXPORT_HTML_BLACK_ICON
     */
    public static final String EXPORT_HTML_WHITE_ICON = ICON_URL_PATH + "html_w_file.svg"; //$NON-NLS-1$

    /**
     * Default size of a page in the Vaadin grids.
     */
    public static final int GRID_PAGE_SIZE = 100;

    /**
     * Default size of an icon in pixels.
     */
    public static final int ICON_SIZE = 16;

    /**
     * Default size of an icon in pixels.
     *
     * @since 4.0
     */
    public static final String ICON_SIZE_STR = ViewConstants.ICON_SIZE + Unit.PIXELS.getSymbol();

    /**
     * Default maximum size of an icon in pixels.
     */
    public static final int MAX_ICON_SIZE = 16;

    /**
     * Default maximum size of an icon in pixels.
     *
     * @since 4.0
     */
    public static final String MAX_ICON_SIZE_STR = ViewConstants.MAX_ICON_SIZE + Unit.PIXELS.getSymbol();

    /**
     * Default size of an photo in pixels.
     */
    public static final int PHOTO_SIZE = 75;

    /**
     * Root attribute name for the UI preferences.
     *
     * @since 4.0
     */
    public static final String PREFERENCE_ROOT = ViewConstants.class.getPackage().getName() + ".preferences."; //$NON-NLS-1$

    /**
     * Root attribute name for the details section opening.
     *
     * @since 4.0
     */
    public static final String DETAILS_SECTION_OPENING_ROOT = PREFERENCE_ROOT + "details_open."; //$NON-NLS-1$

    /**
     * Default minimal size of a two column form.
     *
     * @since 4.0
     */
    public static final String DEFAULT_MINIMAL_WIDTH_FOR_2_COLUMNS_FORM = "500px"; //$NON-NLS-1$

    /**
     * Root attribute name for the filter checkbox for the authenticated user.
     *
     * @since 4.0
     */
    public static final String AUTHENTICATED_USER_FILTER_ROOT = PREFERENCE_ROOT + "authenticated_user_filter."; //$NON-NLS-1$

    /**
     * Root attribute name for the filter checkbox for the missing DOI.
     *
     * @since 4.0
     */
    public static final String MISSING_DOI_FILTER = PREFERENCE_ROOT + "missing_doi_filter"; //$NON-NLS-1$

    /**
     * Root attribute name for publication ID to edit.
     *
     * @since 4.0
     */
    public static final String PUBLICATION_ID_TO_EDIT = PREFERENCE_ROOT + "publication_id_to_edit"; //$NON-NLS-1$

    /**
     * Root attribut name for opening by default the ORCID details of the person.
     *
     * @since 4.0
     */
    public static final String OPEN_DEFAULT_ORCID_DETAILS = PREFERENCE_ROOT + "open_default_orcid_details"; //$NON-NLS-1$

    /**
     * Root attribut name for opening by default the DOI details.
     *
     * @since 4.0
     */
    public static final String OPEN_DEFAULT_DOI_DETAILS = PREFERENCE_ROOT + "open_default_doi_details"; //$NON-NLS-1$

    /**
     * Root attribute name for the filter checkbox for the default organization.
     *
     * @since 4.0
     */
    public static final String DEFAULT_ORGANIZATION_FILTER_ROOT = PREFERENCE_ROOT + "default_organization_filter."; //$NON-NLS-1$

    private ViewConstants() {
        //
    }

}
