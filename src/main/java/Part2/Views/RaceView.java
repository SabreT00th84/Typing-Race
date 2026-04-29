package Part2.Views;

import Part2.Models.Typist;
import Part2.View;
import Part2.ViewModels.RaceViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class RaceView extends View {

    private RaceViewModel viewModel;

    public RaceView(Typist[] typists, boolean autocorrect, String passage) {
        super(typists, autocorrect, passage);
    }

    @Override
    protected void init(Object... o) {
        viewModel = new RaceViewModel((Typist[]) o[0], (boolean) o[1], (String) o[2]);
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
                    typist.getSpeedBoostProperty(), typist.getAccuracyBoostProperty()
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

            typist.getProgressProperty().addListener(
                    (observable, oldValue, newValue) ->
                        viewModel.updateTypistCursorPosition(newValue.intValue() + 1, typist, passage)
                    );
        }

        vbox.getChildren().addAll(progressSection, passage);

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
