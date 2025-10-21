package fr.utbm.ciad.labmanager.views.components.addons.value;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Custom Field in order to set a time period. It was initially thought in order to be used by {@code AbstractPublicationCategoryLayout}.
 *
 * @author $Author: erenon$
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class YearRange extends CustomField<Integer> {

    /**
     * Default value of the age of the initially selected year.
     */
    public static final int DEFAULT_INITIAL_AGE = 6;
    private static final long serialVersionUID = 5602438261321169532L;
    private static final Integer ZERO = Integer.valueOf(0);

    private final PublicationService publicationService;

    private final List<Integer> years;

    private final Select<Integer> start;
    private final Text text;
    private final HorizontalLayout horizontalLayout;
    private Select<Integer> end;
    private Integer chosenStartValue;
    private Integer chosenEndValue;

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     */
    public YearRange(@Autowired PublicationService publicationService) {
        this(publicationService, DEFAULT_INITIAL_AGE);
    }

    /**
     * Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @param initialAge         specify the age in number of years of the initially selected year.
     *                           This value enables to select a year according to "last year - age".
     */
    public YearRange(PublicationService publicationService, int initialAge) {
        this.publicationService = publicationService;

        this.years = this.publicationService.getAllYears();

        final Integer initialYear;
        if (initialAge < this.years.size()) {
            initialYear = this.years.get(this.years.size() - initialAge);
        } else if (this.years.size() > 0) {
            initialYear = this.years.get(0);
        } else {
            initialYear = null;
        }

        this.horizontalLayout = new HorizontalLayout();
        this.horizontalLayout.setSizeFull();
        this.horizontalLayout.setAlignItems(FlexComponent.Alignment.END);

        this.start = new Select<>();
        this.start.setLabel(getTranslation("views.start")); //$NON-NLS-1$
        this.start.setItems(this.years);
        this.start.setValue(initialYear);
        this.chosenStartValue = initialYear;
        this.start.addValueChangeListener(e -> {
            final var selectedValue = e.getValue();
            this.chosenStartValue = selectedValue;
            if (this.chosenStartValue != null) {
                int startValue = this.years.indexOf(selectedValue);
                this.end.setItems(this.years.subList(startValue, this.years.size()));
            } else {
                this.end.setItems(this.years);
            }
            this.end.setValue(null);
        });

        this.end = new Select<>();
        this.end.setLabel(getTranslation("views.end")); //$NON-NLS-1$
        if (this.chosenStartValue != null) {
            this.end.setItems(this.years.subList(this.years.indexOf(this.chosenStartValue), this.years.size()));
            this.end.setValue(null);
        }
        this.end.addValueChangeListener(e -> {
            final var selectedValue = e.getValue();
            this.chosenEndValue = selectedValue;
        });

        this.text = new Text(" - "); //$NON-NLS-1$
        this.horizontalLayout.add(this.start, this.text, this.end);
        add(this.horizontalLayout);

    }

    @Override
    protected Integer generateModelValue() {
        return ZERO;
    }

    @Override
    protected void setPresentationValue(Integer integer) {
        //
    }

    /**
     * Replies the chosen start value
     *
     * @return the chosen start value
     * @since 4.0
     */
    public Integer getChosenStartValue() {
        return this.chosenStartValue;
    }

    /**
     * Replies the chosen end value
     *
     * @return the chosen end value
     * @since 4.0
     */
    public Integer getChosenEndValue() {
        return this.chosenEndValue;
    }

    /**
     * Replies the start select field
     *
     * @return the start select field
     * @since 4.0
     */
    public Select<Integer> getStart() {
        return this.start;
    }

    /**
     * Replies the end select field
     *
     * @return the end select field
     * @since 4.0
     */
    public Select<Integer> getEnd() {
        return this.end;
    }

}
