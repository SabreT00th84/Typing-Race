package Part2.ViewModels;

import Part2.Models.Typist;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RaceViewModel {

    private static final int BASE_SLIDE_BACK_AMOUNT = 2;
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    BURNOUT_DURATION     = 3;
    private static final double BASE_ACCURACY_MULTIPLIER = 0.02;

    private final int slideBackAmount;
    private final Map<Typist, Text> typistCursors;
    private final String passage;

    public RaceViewModel(Typist[] typists, boolean autocorrect, String passage) {
        this.typistCursors = new LinkedHashMap<>();
        this.slideBackAmount = autocorrect ? (BASE_SLIDE_BACK_AMOUNT / 2) : BASE_SLIDE_BACK_AMOUNT;
        this.passage = passage;

        for (Typist typist : typists) {
            this.typistCursors.put(typist, getTypistCursor(typist));
        }
    }

    public Typist[] getTypists() {
        return typistCursors.keySet().toArray(new Typist[0]);
    }

    public String getPassage() {
        return passage;
    }

    public Text[] getPassageAsTextNodes() {
        Text[] textNodes = new Text[passage.length()];

        for (int i = 0; i < passage.length(); i++) {
            textNodes[i] = new Text(passage.substring(i, i + 1));
        }

        return textNodes;
    }

    private Text getTypistCursor(Typist typist) {
        Text cursor = new Text("|");
        cursor.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-fill: " + typist.getColour());

        return cursor;
    }

    public void updateTypistCursorPosition(int newPosition, Typist typist, TextFlow textFlow) {
        textFlow.getChildren().remove(typistCursors.get(typist));
        textFlow.getChildren().add(newPosition, typistCursors.get(typist));
    }

    public boolean raceFinished() {

        for (Typist typist : getTypists()) {
            if (!typist.isFinished(passage.length())) {
                return false;
            }
        }

        return true;
    }

    public void startRaceGUI() {

        while (!raceFinished()) {
            for (Typist typist : getTypists()) {
                if (!typist.isFinished(passage.length())) {
                    Platform.runLater(() ->
                        typist.takeTurn(MISTYPE_BASE_CHANCE,
                                slideBackAmount,
                                BURNOUT_DURATION,
                                BASE_ACCURACY_MULTIPLIER
                        )
                    );
                }
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception ignored) {}
        }
    }
}
