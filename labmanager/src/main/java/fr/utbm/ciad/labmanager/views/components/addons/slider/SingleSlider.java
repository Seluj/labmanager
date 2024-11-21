package fr.utbm.ciad.labmanager.views.components.addons.slider;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.ClientCallable;
import io.reactivex.Single;

@Tag("div")
public class SingleSlider extends Div {

    private int minValue = 0;

    private int maxValue = 1;

    private double step = 0.1;

    private double currentValue = 0;


    public SingleSlider() {
        setWidth("100%");

        // Injecter le slider HTML
        String sliderHtml = """
            <div class="slider-container">
                <input type="range" min="%d" max="%d" step=0.1 class="slider" id="slider">
                <div class="slider-values">
                    <span id="min-value">%d</span>
                    <span id="max-value">%d</span>
                </div>
            </div>
        """;

        getElement().setProperty("innerHTML", String.format(sliderHtml, minValue, maxValue, minValue, maxValue));

        // Ajouter JavaScript pour gérer la couleur dynamique
        getElement().executeJs("""
            const slider = this.querySelector('#slider');
            const minValue = parseFloat(slider.min);
            const maxValue = parseFloat(slider.max);

            const updateSliderStyle = () => {
                const value = parseFloat(slider.value);
                const percentage = ((value - minValue) / (maxValue - minValue)) * 100;
                slider.style.background = `linear-gradient(to right, dodgerblue ${percentage}%, #ccc ${percentage}%)`;
            };

            slider.addEventListener('input', () => {
                updateSliderStyle();
                this.$server.updateValue(parseFloat(slider.value));
            });

            // Initialiser le style
            updateSliderStyle();
        """);
    }

    @ClientCallable
    public void updateValue(double value) {
        currentValue = value;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        updateSliderHtml();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        updateSliderHtml();
    }

    public void setStep(double step) {
        this.step = step;
        updateSliderHtml();
    }

    private void updateSliderHtml() {
        getElement().executeJs("""
            const slider = this.querySelector('#slider');
            slider.min = $0;
            slider.max = $1;
            slider.step = $2;
        """, minValue, maxValue, step);
    }

    public double getCurrentValue() {
        return currentValue;
    }
}
