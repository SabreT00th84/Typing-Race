package Part2.Views;

import Part2.AppState;
import Part2.Models.RaceStat;
import Part2.Models.Typist;
import Part2.Navigator;
import Part2.View;
import Part2.ViewModels.RaceViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;

public class RaceView extends View {

    private AppState appState;
    private Navigator navigator;
    private RaceViewModel viewModel;

    public RaceView(AppState appState, Navigator navigator, boolean autocorrect, String passage) {
        super(appState, navigator, autocorrect, passage);
    }

    @Override
    protected void init(Object... o) {
        this.appState = (AppState) o[0];
        this.navigator = (Navigator) o[1];
        this.viewModel = new RaceViewModel((AppState) o[0], (boolean) o[2], (String) o[3]);
    }

    @Override
    public Parent build() {
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));

        VBox progressSection = new VBox(2.5);
        TextFlow passage = new TextFlow(viewModel.getPassageAsTextNodes());
        passage.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-border-style: solid;");
        passage.setPadding(new Insets(5));

        for (Typist typist : viewModel.getTypists()) {
            HBox typistLane = new HBox(2.5);

            StackPane progress = new StackPane();
            progress.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(progress, javafx.scene.layout.Priority.ALWAYS);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.progressProperty().bind(Bindings.createDoubleBinding(
                    () -> ((double) typist.getProgress() / viewModel.getPassage().length()),
                    typist.getProgressProperty()
            ));
            progressBar.setStyle("-fx-accent: " + typist.getColour());

            Label symbol = new Label(typist.getSymbol());
            symbol.translateXProperty().bind(Bindings.createDoubleBinding(
                    () -> (progressBar.getProgress() * progressBar.getWidth()),
                    progressBar.progressProperty(), progressBar.widthProperty()
            ));

            progress.getChildren().addAll(progressBar, symbol);

            Label info = new Label();
            info.textProperty().bind(Bindings.createStringBinding(
                    () -> "      (Accuracy: " + String.format("%.2f", typist.getAccuracy()) +
                            ", Speed: " + typist.getSpeed() + ") ",
                    typist.getSpeedBoostProperty(), typist.getAccuracyBoostProperty(), typist.getBaseAccuracyProperty()
            ));

            Label burnout = new Label();
            burnout.textProperty().bind(Bindings.createStringBinding(
                    () -> "BURNT OUT (" + typist.getBurnoutTurnsRemaining() + " turns)",
                    typist.getBurnoutRemainingProperty()
            ));
            burnout.visibleProperty().bind(typist.getBurntOutProperty());

            Label mistyped = new Label("← just mistyped");
            mistyped.visibleProperty().bind(typist.getJustMistypedProperty());

            typistLane.getChildren().addAll(new Label(typist.getName()), progress, info, burnout, mistyped);
            progressSection.getChildren().add(typistLane);

            ChangeListener<Number> cursorListener = (observable, oldValue, newValue) ->
                Platform.runLater(() ->
                        viewModel.updateTypistCursorPosition(
                                newValue.intValue(),
                                typist,
                                passage
                        )
                );
            typist.getProgressProperty().addListener(cursorListener);

            viewModel.getRaceFinishedProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        progressBar.progressProperty().unbind();
                        symbol.textProperty().unbind();
                        info.textProperty().unbind();
                        burnout.textProperty().unbind();
                        mistyped.textProperty().unbind();
                        typist.getProgressProperty().removeListener(cursorListener);
                    });
        }

        vbox.getChildren().addAll(progressSection, passage);

        VBox stats = new VBox(5);
        viewModel.getShowRaceStatsProperty().addListener(((
                (observable, oldValue, newValue) -> {
            if (newValue) {
                for (Typist typist : viewModel.getTypists()) {
                    RaceStat stat = typist.getLastRaceStat();

                    Label heading = new Label(typist.getName() + " (" + stat.position() + (
                            (stat.position() == 1) ? "st" :
                                    (stat.position() == 2) ? "nd" :
                                    (stat.position() == 3) ? "rd" : "th")
                            + " place)"
                    );
                    heading.setFont(Font.font("sans-serif", FontWeight.BOLD, 20));

                    Label wpm = new Label("WPM: " + stat.wpm());
                    Label accuracyPerc = new Label("Accuracy Percentage (Proportion of non-mistyped characters): "
                           + String.format("%.2f", stat.raceAccuracy() * 100) + "%"
                    );
                    Label numOfBurnout = new Label("Number of burnouts: " + stat.numOfBurnouts());
                    Label prevAccuracy = new Label("Previous Accuracy: " +
                            String.format("%.2f", viewModel.getStartingAccuracy(typist)));
                    Label newAccuracy = new Label("New Accuracy: " +
                            String.format("%.2f", typist.getBaseAccuracy()));
                    Label accuracyChange = new Label("Accuracy Change: " +
                            (stat.typistAccuracyChange() >= 0 ? "+" : "") +
                            String.format("%.2f", stat.typistAccuracyChange() * 100) + "%"
                    );
                    Label points = new Label("Points Earned: " + stat.pointsEarned());

                    stats.getChildren().addAll(
                            heading,
                            wpm,
                            accuracyPerc,
                            numOfBurnout,
                            prevAccuracy,
                            newAccuracy,
                            accuracyChange,
                            points
                    );
                }
            }
        })));
        stats.visibleProperty().bind(viewModel.getRaceFinishedProperty());

        vbox.getChildren().add(stats);

        Button back = new Button("Main Menu");
        back.visibleProperty().bind(viewModel.getRaceFinishedProperty());
        back.setOnAction(e -> navigator.navigateTo(new MainMenuView(appState, navigator)));

        vbox.getChildren().add(back);

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);

        scrollPane.sceneProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Thread.startVirtualThread(viewModel::startRaceGUI);
                    }
                });

        return scrollPane;
    }
}
