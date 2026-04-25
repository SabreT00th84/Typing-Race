package Part2.Views;

import Part2.Models.RaceConfig;
import Part2.Navigator;
import Part2.View;
import Part2.ViewModels.SetupViewModel;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SetupView extends View {
    private SetupViewModel viewModel;
    private Navigator navigator;

    public SetupView(Navigator navigator) {
        super(navigator);
    }

    /**
     *
     */
    @Override
    protected void init(Object... o) {
        viewModel = new SetupViewModel();
        navigator = (Navigator) o[0];
    }

    @Override
    protected Parent build() {
        ComboBox<String> passagePicker = new ComboBox<>();
        passagePicker.getItems().addAll(viewModel.getPassageKeys());
        passagePicker.valueProperty().bindBidirectional(viewModel.getSelectedProperty());
        passagePicker.setEditable(true);

        TextArea preview = new TextArea(viewModel.getPassage(viewModel.getSelected()));
        preview.setWrapText(true);
        preview.editableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.passagesContainsKey(viewModel.getSelected()),
                viewModel.getSelectedProperty()
        ));
        viewModel.getSelectedProperty().addListener(((observable, oldValue, newValue) -> {
            String fromMap = viewModel.getPassage(newValue);
            if (fromMap != null) {
                preview.setText(fromMap);
            } else {
                preview.textProperty().bindBidirectional(viewModel.getTextProperty());
            }
        }));

        Button addPassageButton = new Button("Add");
        addPassageButton.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.passagesContainsKey(viewModel.getSelected()),
                viewModel.getSelectedProperty(), viewModel.getPassagesProperty()
        ));
        addPassageButton.setOnAction(e -> {
            if (viewModel.addPassage(preview.getText())) {
                passagePicker.getItems().add(viewModel.getSelected());
                preview.setText(viewModel.getPassage(viewModel.getSelected()));
            }
        });

        Spinner<Integer> numOfTypists = new Spinner<>(2, 6, 2);

        CheckBox autocorrect = new CheckBox("Autocorrect (Halves slideback amount)");
        CheckBox caffeine = new CheckBox("Caffeine (Increased speed for first 10 turns" +
                " of the race with increased burnout risk)");
        CheckBox night = new CheckBox("Night Shift (Reduced Accuracy)");

        Button submitButton = new Button("Next");
        submitButton.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> viewModel.passagesContainsKey(viewModel.getSelected()),
                viewModel.getSelectedProperty(), viewModel.getPassagesProperty()
        ));
        submitButton.setOnAction(e -> navigator.navigateTo(new SetupTypistsView(new RaceConfig(
                preview.getText(),
                numOfTypists.getValue(),
                autocorrect.isSelected(),
                caffeine.isSelected(),
                night.isSelected()
        ), navigator)));

        VBox vBox = new VBox(5,
                new Label("Select Passage"),
                passagePicker,
                new Label("Preview"),
                preview,
                addPassageButton,
                new Label("Number of Typists"),
                numOfTypists,
                new Label("Difficulty Modifiers (Applies to everyone)"),
                autocorrect,
                caffeine,
                night,
                submitButton
        );
        vBox.setPadding(new Insets(10));

        return vBox;
    }
}