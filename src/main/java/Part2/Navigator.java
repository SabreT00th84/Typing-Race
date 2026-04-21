package Part2;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Navigator {
    private final ObjectProperty<View> currentView;

    public Navigator() {
        this.currentView = new SimpleObjectProperty<>();
    }

    /**
     * Switches the currentView to the newView provided.
     *
     * @param newView the view to navigate to.
     */
    public void navigateTo(View newView) {
        currentView.set(newView);
    }

    /**
     * Getter method for the currentView Property.
     *
     * @return The object property stored in currentView.
     */
    public ObjectProperty<View> getCurrentViewProperty() {
        return currentView;
    }

    public View getView() {
        return currentView.get();
    }
}
