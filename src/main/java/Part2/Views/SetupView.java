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
        passagePicker.getItems().addAll(viewModel.passageStrings.keySet());
        passagePicker.valueProperty().bindBidirectional(viewModel.selected);
        passagePicker.setEditable(true);

        TextArea preview = new TextArea(viewModel.passageStrings.get(viewModel.selected.get()));
        preview.setWrapText(true);
        preview.editableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.passageStrings.containsKey(viewModel.selected.get()),
                viewModel.selected
        ));
        viewModel.selected.addListener(((observable, oldValue, newValue) -> {
            String fromMap = viewModel.passageStrings.get(newValue);
            if (fromMap != null) {
                preview.setText(fromMap);
            } else {
                preview.textProperty().bindBidirectional(viewModel.text);
            }
        }));

        Button addPassageButton = new Button("Add");
        addPassageButton.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.passageStrings.containsKey(viewModel.selected.get()),
                viewModel.selected, viewModel.passageStrings
        ));
        addPassageButton.setOnAction(e -> {
            if (viewModel.addPassage(preview.getText())) {
                passagePicker.getItems().add(viewModel.selected.get());
                preview.setText(viewModel.passageStrings.get(viewModel.selected.get()));
            }
        });

        Spinner<Integer> numOfTypists = new Spinner<>(2, 6, 2);

        CheckBox autocorrect = new CheckBox("Autocorrect (Reduces slideback amount)");
        CheckBox caffeine = new CheckBox("Caffeine (Increased speed for first half of the race)");
        CheckBox night = new CheckBox("Night Shift (Reduced Accuracy)");

        Button submitButton = new Button("Next");
        submitButton.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> viewModel.passageStrings.containsKey(viewModel.selected.get()),
                viewModel.selected, viewModel.passageStrings
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