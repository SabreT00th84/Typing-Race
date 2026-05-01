package Part2.ViewModels;

import Part2.AppState;
import Part2.Models.Typist;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class StatsViewModel {

    private final AppState appState;
    private final Map<String, Typist> typistNames;
    private final ObjectProperty<String> selectedTypistName;

    public StatsViewModel(AppState appState) {
        this.appState = appState;
        this.typistNames = new LinkedHashMap<>();
        this.selectedTypistName = new SimpleObjectProperty<>();
        appState.getTypists().forEach(typist -> typistNames.put(typist.getName(), typist));
    }

    public Set<String> getTypistNames() {
        return typistNames.keySet();
    }

    public ObjectProperty<String> selectedTypistNameProperty() {
        return selectedTypistName;
    }

    public Typist getSelectedTypist() {
        return typistNames.get(selectedTypistName.get());
    }

    public void setInitialTypist() {
        selectedTypistName.set(appState.getTypists().getFirst().getName());
    }
}
