package Part2.Views;

import Part2.AppState;
import Part2.Models.RaceStat;
import Part2.Navigator;
import Part2.View;
import Part2.ViewModels.StatsViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StatsView extends View {
    private AppState appState;
    private Navigator navigator;
    private StatsViewModel viewModel;

    public StatsView(AppState appState, Navigator navigator) {
        super(appState, navigator);
    }

    @Override
    protected void init(Object... o) {
        this.appState = (AppState) o[0];
        this.navigator = (Navigator) o[1];
        this.viewModel = new StatsViewModel(appState);
    }

    @Override
    protected Parent build() {
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));

        ChoiceBox<String> typistPicker = new ChoiceBox<>();
        typistPicker.getItems().addAll(viewModel.getTypistNames());
        typistPicker.valueProperty().bindBidirectional(viewModel.selectedTypistNameProperty());

        Label heading = new Label();
        heading.textProperty().bind(Bindings.createStringBinding(() -> viewModel.getSelectedTypist().getName()
                + ((!viewModel.getSelectedTypist().getAwardsReceived().isEmpty()) ?
                        "(" + String.join(", ", viewModel.getSelectedTypist().getAwardsReceived()) + ")" : ""),
                viewModel.selectedTypistNameProperty())
        );
        heading.setFont(Font.font("sans-serif", FontWeight.BOLD, 20));

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Race Number");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");
        LineChart<Number, Number> trends = new LineChart<>(xAxis, yAxis);

        viewModel.selectedTypistNameProperty().addListener(
                (observable, oldValue, newValue) -> {
                    vbox.getChildren().clear();
                    vbox.getChildren().addAll(typistPicker, heading, trends);

                    XYChart.Series<Number, Number> wpmSeries = new XYChart.Series<>();
                    wpmSeries.setName("WPM");
                    XYChart.Series<Number, Number> accuracySeries = new XYChart.Series<>();
                    accuracySeries.setName("Accuracy (Proportion of non-mistyped characters in %)");
                    int i = 1;
                    for (RaceStat stat : viewModel.getSelectedTypist().getRaceStats()) {
                        wpmSeries.getData().add(new XYChart.Data<>(i, stat.wpm()));
                        accuracySeries.getData().add(new XYChart.Data<>(i, stat.raceAccuracy() * 100));

                        Label statHeading = new Label("Race " + i);
                        statHeading.setFont(Font.font("sans-serif", FontWeight.BOLD, 15));

                        Label position = new Label("Position: " + stat.position());
                        Label wpm = new Label("WPM: " + stat.wpm());
                        Label accuracyPerc = new Label("Accuracy Percentage (Proportion of non-mistyped characters): "
                                + String.format("%.2f", stat.raceAccuracy() * 100) + "%"
                        );
                        Label numOfBurnout = new Label("Number of burnouts: " + stat.numOfBurnouts());
                        Label accuracyChange = new Label("Accuracy Change: " +
                                (stat.typistAccuracyChange() >= 0 ? "+" : "") +
                                String.format("%.2f", stat.typistAccuracyChange() * 100) + "%"
                        );
                        Label points = new Label("Points Earned: " + stat.pointsEarned());

                        vbox.getChildren().addAll(
                                statHeading,
                                position,
                                wpm,
                                accuracyPerc,
                                numOfBurnout,
                                accuracyChange,
                                points
                        );
                        i++;
                    }

                    trends.getData().clear();
                    trends.getData().addAll(wpmSeries, accuracySeries);

                    Button back = new Button("Back");
                    back.setOnAction(e -> navigator.navigateTo(new MainMenuView(appState, navigator)));
                    vbox.getChildren().add(back);
                }
        );

        viewModel.setInitialTypist();

        return new ScrollPane(vbox);
    }


}
