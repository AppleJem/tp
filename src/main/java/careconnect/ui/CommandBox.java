package careconnect.ui;

import careconnect.logic.Logic;
import careconnect.logic.autocompleter.exceptions.AutocompleteException;
import careconnect.logic.commands.CommandResult;
import careconnect.logic.commands.exceptions.CommandException;
import careconnect.logic.parser.AddressBookParser;
import careconnect.logic.parser.exceptions.ParseException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final AddressBookParser parser;
    private final CommandAutocompleter commandAutocompleter;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor, CommandAutocompleter commandAutocompleter) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.commandAutocompleter = commandAutocompleter;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        this.parser = new AddressBookParser();
    }

    /**
     * Initializes the tab button listener.
     */
    @FXML
    public void initialize() {
        // Add a key listener for the Tab key
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                handleTabPressed();
                event.consume(); // Prevents default behavior
            }
        });
    }

    /**
     * Handles the Tab button pressed event.
     */
    private void handleTabPressed() {
        String commandText = commandTextField.getText();

        try {
            String autocompletedCommand = commandAutocompleter.autocompleteCommand(commandText);
            String autocompletedCommandWithSpace = autocompletedCommand + " ";
            commandTextField.setText(autocompletedCommandWithSpace);
            commandTextField.positionCaret(commandTextField.getText().length());
        } catch (AutocompleteException e) {
            setStyleToIndicateCommandFailure();
        }

    }


    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Validates command typed on every key press.
     */
    @FXML
    private void handleCommandTyped() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            parser.parseCommand(commandText);

            // Sets style back to default if command is valid
            this.setStyleToDefault();
            assert(!(this.commandTextField.getStyleClass()
                            .contains(ERROR_STYLE_CLASS)));
        } catch (ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a incorrect / failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
        assert(styleClass.contains(ERROR_STYLE_CLASS));
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a function that can autocomplete commands.
     */
    @FunctionalInterface
    public interface CommandAutocompleter {
        /**
         * Autocompletes the command and returns the suggestion.
         *
         * @see Logic#autocompleteCommand(String)
         */
        String autocompleteCommand(String commandText) throws AutocompleteException;
    }

}
