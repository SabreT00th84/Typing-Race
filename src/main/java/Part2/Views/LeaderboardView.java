package Part2.Views;

import Part2.AppState;
import Part2.Models.RaceStat;
import Part2.Models.Typist;
import Part2.Navigator;
import Part2.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class LeaderboardView extends View {
    private AppState appState;
    private Navigator navigator;

    public LeaderboardView(AppState appState, Navigator navigator) {
        super(appState, navigator);
    }

    @Override
    public void init(Object... o) {
        this.appState = (AppState) o[0];
        this.navigator = (Navigator) o[1];
    }

    @Override
    protected Parent build() {
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);

        List<Typist> sorted = appState.getTypists().stream().sorted(
                Comparator.comparing(
                        t -> t.getRaceStats().stream()
                                .map(RaceStat::pointsEarned)
                                .reduce(0, Integer::sum))).toList().reversed();

        int i = 1;
        for (Typist typist : sorted) {
            int points = typist.getRaceStats().stream()
                    .map(RaceStat::pointsEarned)
                    .reduce(0, Integer::sum);

            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER);

            row.getChildren().addAll(
                    new Label("Rank: " + i),
                    new Label("Name: " + typist.getName()),
                    new Label("Points: " + points)
            );

            vbox.getChildren().add(row);
            i++;
        }

        Button back = new Button("Back");
        back.setOnAction(e -> navigator.navigateTo(new MainMenuView(appState, navigator)));
        vbox.getChildren().add(back);

        return new ScrollPane(vbox);
    }
}
