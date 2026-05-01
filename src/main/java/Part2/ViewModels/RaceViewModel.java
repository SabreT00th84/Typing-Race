package Part2.ViewModels;

import Part2.AppState;
import Part2.Models.RaceStat;
import Part2.Models.Typist;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RaceViewModel {

    private static final int BASE_SLIDE_BACK_AMOUNT = 2;
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    BURNOUT_DURATION     = 3;
    private static final double BASE_ACCURACY_MULTIPLIER = 0.02;

    private final AppState appState;
    private final int slideBackAmount;
    private final Map<Typist, Text> typistCursors;
    private final Map<Typist, Double> startingAccuracies;
    private final String passage;
    private final BooleanProperty raceFinished;
    private final BooleanProperty showRaceStats;

    public RaceViewModel(AppState appState, boolean autocorrect, String passage) {
        this.appState = appState;
        this.slideBackAmount = autocorrect ? (BASE_SLIDE_BACK_AMOUNT / 2) : BASE_SLIDE_BACK_AMOUNT;
        this.typistCursors = new HashMap<>();
        this.startingAccuracies = new HashMap<>();
        this.passage = passage;
        this.raceFinished = new SimpleBooleanProperty(false);
        this.showRaceStats = new SimpleBooleanProperty(false);

        for (Typist typist : appState.getTypists()) {
            this.typistCursors.put(typist, getTypistCursor(typist));
            this.startingAccuracies.put(typist, typist.getBaseAccuracy());
        }
    }

    public List<Typist> getTypists() {
        return appState.getTypists();
    }

    public String getPassage() {
        return passage;
    }

    public double getStartingAccuracy(Typist typist) {
        return startingAccuracies.get(typist);
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
        if (newPosition >= passage.length()) {
            textFlow.getChildren().add(typistCursors.get(typist));
        } else {
            textFlow.getChildren().add(newPosition, typistCursors.get(typist));
        }
    }

    private boolean raceFinished() {

        for (Typist typist : getTypists()) {
            if (!typist.isFinished(passage.length())) {
                return false;
            }
        }
        return true;
    }

    public BooleanProperty getRaceFinishedProperty() {
        return raceFinished;
    }

    public BooleanProperty getShowRaceStatsProperty() {
        return showRaceStats;
    }

    public void startRaceGUI() {

        for (Typist typist : getTypists()) typist.prepareForRace();

        while (!raceFinished()) {
            for (Typist typist : getTypists()) {
                if (!typist.isFinished(passage.length())) {
                    Platform.runLater(() ->
                        typist.takeTurn(MISTYPE_BASE_CHANCE + typist.getMistypeChanceModifier(),
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

        Platform.runLater(() -> {
            raceFinished.set(true);
            postRaceActions();
            showRaceStats.set(true);
        });
    }

    private void addRaceStat(Typist typist, int position) {
        int wpm = (int) Math.round(passage.split(" ").length / (typist.getTimeTaken() / 60));
        double accuracy = (double) (passage.length() - typist.getNumberOfMistypes()) / passage.length();
        double accuracyChange = typist.getBaseAccuracy() - startingAccuracies.get(typist);
        int points = (int) ((double) ((appState.getTypists().size() - position + 1) * wpm)
                / (typist.getNumberOfBurnouts() + 1));

        typist.addRaceStat(new RaceStat(
                position,
                wpm,
                accuracy,
                typist.getNumberOfBurnouts(),
                accuracyChange,
                points
        ));
    }

    private void payCoins(Typist typist, int position) {
        int coins = 10 * (appState.getTypists().size() - position + 1);

        if (typist.getLastRaceStat().wpm() >= 200) {
            coins += 100;
        }

        if (typist.getLastRaceStat().numOfBurnouts() > 0) {
            coins -= 25;
        }

        if (typist.isSponsored() && typist.getLastRaceStat().numOfBurnouts() == 0) {
            coins += 50;
        }

        typist.addCoins(coins);
    }

    public void awardTypist(Typist typist) {
        if (typist.getRaceStats().stream().filter(s -> s.position() == 1).count() >= 3) {
            typist.addAward(Typist.awards.get(0));
        } else if (typist.getRaceStats().stream().filter(s -> s.numOfBurnouts() == 0).count() >= 5) {
            typist.addAward(Typist.awards.get(1));
        }
    }

    private void increaseWinnerAccuracy(Typist winner) {
        winner.setAccuracy(winner.getBaseAccuracy() + (winner.getBaseAccuracy() * BASE_ACCURACY_MULTIPLIER * 10));
    }

    private void postRaceActions() {
        appState.getTypists().sort(Comparator.comparingDouble(Typist::getTimeTaken));
        increaseWinnerAccuracy(appState.getTypists().getFirst());

        for (int i = 0; i < appState.getTypists().size(); i++) {
            addRaceStat(appState.getTypists().get(i), i + 1);
            payCoins(appState.getTypists().get(i), i + 1);
            awardTypist(appState.getTypists().get(i));
            appState.getTypists().get(i).reset();
        }
    }
}
