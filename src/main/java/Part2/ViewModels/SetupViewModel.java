package Part2.ViewModels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.LinkedHashMap;
import java.util.Set;

public class SetupViewModel {
    private final ObservableMap<String, String> passageStrings = FXCollections.observableMap(new LinkedHashMap<>());
    {
        passageStrings.put("Short", "I must not fear. Fear is the mind-killer. Fear is the little-death " +
                "that brings total obliteration. I will face my fear. I will permit it to pass over me " +
                "and through me. And when it has gone past I will turn the inner eye to see its path. Where " +
                "the fear has gone there will be nothing. Only I will remain.");
        passageStrings.put("Medium", "Many that live deserve death. And some that die deserve life. " +
                "Can you give it to them? Then do not be too eager to deal out death in judgement. " +
                "For even the very wise cannot see all ends. I have not much hope that Gollum can be " +
                "cured before he dies, but there is a chance of it. And he is bound up with the fate of " +
                "the Ring. My heart tells me that he has some part to play yet, for good or ill, " +
                "before the end; and when that comes, the pity of Bilbo may " +
                "rule the fate of many - yours not least.");
        passageStrings.put("Long", "'I ask of you your lives,' Elend said, voice echoing, 'and your courage. " +
                "I ask of you your faith, and your honor—your strength, and your compassion. For today, " +
                "I lead you to die. I will not ask you to welcome this event. I will not insult you by " +
                "calling it well, or just, or even glorious. But I will say this. " +
                "'Each moment you fight is a gift to those in this cavern. Each second we fight is a " +
                "second longer that thousands of people can draw breath. Each stroke of the sword, " +
                "each koloss felled, each breath earned is a victory! It is a person protected for " +
                "a moment longer, a life extended, an enemy frustrated!' There was a brief pause." +
                "'In the end, they will kill us,' Elend said, voice loud, ringing in the cavern. " +
                "'But first, they shall fear us!'");
    }

    private final StringProperty selected = new SimpleStringProperty("Short");
    private final StringProperty text = new SimpleStringProperty("");

    public ObservableMap<String, String> getPassagesProperty() {
        return passageStrings;
    }

    public Set<String> getPassageKeys() {
        return passageStrings.keySet();
    }

    public boolean passagesContainsKey(String key) {
        return passageStrings.containsKey(key);
    }

    public String getPassage(String key) {
        return passageStrings.get(key);
    }

    public boolean addPassage(String text) {
        if (!(selected.get() == null || selected.get().isBlank() || text == null || text.isBlank())) {
            passageStrings.put(selected.get(), text);
            return true;
        } else {
            return false;
        }
    }

    public StringProperty getSelectedProperty() {
        return selected;
    }

    public String getSelected() {
        return selected.get();
    }

    public StringProperty getTextProperty() {
        return text;
    }
}