/*
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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * UI and JPA filters for {@link AbstractFilters}.
 *
 * @param <T> the type of the entities to be filtered.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractFilters<T> extends Div implements Specification<T>, LocaleChangeObserver {

    private static final long serialVersionUID = 1052687254355760397L;

    private final TextField keywords;

    private final Button resetButton;

    private final Button searchButton;

    /**
     * Constructor.
     *
     * @param onSearch the callback function for running the filtering.
     */
    public AbstractFilters(Runnable onSearch) {
        setWidthFull();
        addClassName("filter-layout"); //$NON-NLS-1$
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM, LumoUtility.BoxSizing.BORDER);

        this.keywords = new TextField();

        this.resetButton = new Button();
        this.resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        this.resetButton.addClickListener(event -> {
            this.keywords.clear();
            resetFilters();
            onSearch.run();
        });
        this.resetButton.addClickShortcut(Key.ESCAPE);
        this.resetButton.addClickShortcut(Key.CANCEL);
        this.resetButton.addClickShortcut(Key.CLEAR);

        this.searchButton = new Button();
        this.searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.searchButton.addClickListener(event -> onSearch.run());
        this.searchButton.addClickShortcut(Key.ENTER);
        this.searchButton.addClickShortcut(Key.FIND);
        this.searchButton.addClickShortcut(Key.EXECUTE);

        final var actions = new Div(this.resetButton, this.searchButton);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions"); //$NON-NLS-1$

        final var options = new HorizontalLayout();
        options.setSpacing(false);
        buildOptionsComponent(options);

        add(this.keywords, options, actions);
    }

    /**
     * Build the component for filtering options.
     *
     * @param options the component that should receive the options, never {@code null}.
     */
    protected abstract void buildOptionsComponent(HorizontalLayout options);

    /**
     * Reset the filters.
     */
    protected abstract void resetFilters();

    /**
     * Build the HQL query for the filtering.
     *
     * @param keywords        the keywords to search for.
     * @param predicates      the list of filtering criteria with "or" semantic, being filled by this function.
     * @param root            the root not for the search.
     * @param criteriaBuilder the criteria builder. It is the Hibernate version in order to
     *                        have access to extra functions, e.g. {@code collate}.
     */
    protected abstract void buildQueryFor(String keywords, List<Predicate> predicates, Root<T> root, CriteriaBuilder criteriaBuilder);

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return ComponentFactory.newPredicateContainsOneOf(this.keywords.getValue(), root, query,
                criteriaBuilder, this::buildQueryFor);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        this.keywords.setLabel(getTranslation("views.filters.keywords")); //$NON-NLS-1$
        this.resetButton.setText(getTranslation("views.filters.reset")); //$NON-NLS-1$
        this.searchButton.setText(getTranslation("views.filters.apply")); //$NON-NLS-1$
    }

}
