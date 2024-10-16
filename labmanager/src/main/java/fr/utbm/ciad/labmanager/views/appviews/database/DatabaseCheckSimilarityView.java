package fr.utbm.ciad.labmanager.views.appviews.database;

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

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import jakarta.annotation.security.RolesAllowed;
import static fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory.getTranslation;

/** Enable to manage the database.
 *
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "database_similarity", layout = MainLayout.class)
@RolesAllowed({UserRole.ADMIN_GRANT})
@Uses(Icon.class)
public class DatabaseCheckSimilarityView extends VerticalLayout implements HasDynamicTitle, LocaleChangeObserver {

    private static final long serialVersionUID = -1583805930880620625L;

    private TabSheet tabSheet;

    /** Constructor.
     */
    public DatabaseCheckSimilarityView(){
        tabSheet = new TabSheet();

        tabSheet.add("Personne",
                new Div());
        tabSheet.add("Organisation",
                new Div());
        tabSheet.add("Journal",
                new Div());
        tabSheet.add("Conférence",
                new Div());
        add(tabSheet);
    }

    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {

    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.databases.similarity.title");
    }
}
