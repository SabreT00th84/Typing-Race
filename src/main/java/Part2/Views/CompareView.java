package Part2.Views;

import Part2.AppState;
import Part2.Models.Typist;
import Part2.Navigator;
import Part2.View;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CompareView extends View {

    AppState appState;
    Navigator navigator;

    public CompareView(AppState appState, Navigator navigator) {
        super(appState, navigator);
    }

    @Override
    protected void init(Object... o) {
        this.appState = (AppState) o[0];
        this.navigator = (Navigator) o[1];
    }

    @Override
    protected Parent build() {
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));

        Label heading = new Label("Compare Typists");
        heading.setFont(Font.font("sans-serif", FontWeight.BOLD, 20));

        HBox selector = new HBox(5);

        ChoiceBox<Typist> typistPicker = new ChoiceBox<>();
        typistPicker.getItems().addAll(appState.getTypists());

        ChoiceBox<Typist> comparePicker = new ChoiceBox<>();
        comparePicker.getItems().addAll(appState.getTypists());

        ChoiceBox<String> metricPicker = new ChoiceBox<>();
        metricPicker.getItems().addAll("Accuracy", "WPM");

        selector.getChildren().addAll(
                new Label("Compare:"),
                typistPicker,
                new Label("With:"),
                comparePicker,
                new Label("On:"),
                metricPicker
        );

        Button compare = new Button("Compare");

        Button back = new Button("Back");
        back.setOnAction(e -> navigator.navigateTo(new MainMenuView(appState, navigator)));

        vbox.getChildren().addAll(heading, selector, compare, back);

        compare.setOnAction(e -> {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Race Number");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Value");
            LineChart<Number, Number> trends = new LineChart<>(xAxis, yAxis);
            XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
            XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
            trends.getData().addAll(series1, series2);

            Typist typist1 = typistPicker.getValue();
            Typist typist2 = comparePicker.getValue();

            if (metricPicker.getValue().equals("WPM")) {
                series1.setName(typist1.getName() + " - WPM");
                series2.setName(typist2.getName() + " - WPM");

                for (int i = 1; i <= Math.min(typist1.getRaceStats().size(), typist2.getRaceStats().size()); i++) {
                    series1.getData().add(new XYChart.Data<>(i, typist1.getRaceStats().get(i - 1).wpm()));
                    series2.getData().add(new XYChart.Data<>(i, typist2.getRaceStats().get(i - 1).wpm()));
                }
            } else {
                series1.setName(typist1.getName() + " - Accuracy");
                series2.setName(typist2.getName() + " - Accuracy");

                for (int i = 1; i <= Math.min(typist1.getRaceStats().size(), typist2.getRaceStats().size()); i++) {
                    series1.getData().add(new XYChart.Data<>(i, typist1.getRaceStats().get(i - 1).raceAccuracy()));
                    series2.getData().add(new XYChart.Data<>(i, typist2.getRaceStats().get(i - 1).raceAccuracy()));
                }
            }
            vbox.getChildren().clear();
            vbox.getChildren().addAll(heading, selector, compare, trends, back);
        });

        return vbox;
    }
}
