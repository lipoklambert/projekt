package maze.gui;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.Setter;
import maze.results.GameResult;
import maze.results.GameResultRepository;
import maze.state.Position;
import maze.state.MazeState;
import org.tinylog.Logger;
import util.javafx.ControllerHelper;
import util.javafx.Stopwatch;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class GameController {

    @FXML
    private Button giveUpFinishButton;

    @FXML
    private GridPane grid;

    @FXML
    private Label messageLabel;

    @FXML
    private Button resetButton;

    @FXML
    private Label stepsLabel;

    @FXML
    private Label stopwatchLabel;

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultRepository gameResultRepository;

    @Setter
    private String playerName;

    private Instant startTime;

    private MazeState state;

    private ImageView[] pieceViews;

    private BooleanProperty isSolved = new SimpleBooleanProperty();

    private IntegerProperty countOfSteps = new SimpleIntegerProperty();

    private Stopwatch stopwatch = new Stopwatch();

    @FXML
    private void initialize() {
        // creating bindings
        stepsLabel.textProperty().bind(countOfSteps.asString());
        stopwatchLabel.textProperty().bind(stopwatch.hhmmssProperty());
        isSolved.addListener(this::handleSolved);

        // loading images
        pieceViews = Stream.of("ball.png", "finish.png")
                .map(s -> "/images/" + s)
                .peek(s -> Logger.debug("Loading image resource {}", s))
                .map(Image::new)
                .map(ImageView::new)
                .toArray(ImageView[]::new);

        // populating grid
        populateGrid();

        // registering listeners
        registerKeyEventHandler();

        // starting new game
        Platform.runLater(() -> messageLabel.setText(String.format("Good luck, %s!", playerName)));
        resetGame();
    }

    private void resetGame() {
        // initializing state
        state = new MazeState();
        countOfSteps.set(0);
        isSolved.set(false);

        // initializing watch
        startTime = Instant.now();
        if (stopwatch.getStatus() == Animation.Status.PAUSED) {
            stopwatch.reset();
        }
        stopwatch.start();

        // initializing board
        clearState();
        showState();
    }

    @FXML
    public void handleGiveUpFinishButton(
            @NonNull final ActionEvent actionEvent) throws IOException {

        final var buttonText = ((Button) actionEvent.getSource()).getText();
        Logger.debug("{} is pressed", buttonText);
        if (Objects.equals(buttonText, "Give Up")) {
            stopwatch.stop();
            Logger.info("The game has been given up");
        }

        Logger.debug("Saving result");
        gameResultRepository.addOne(createGameResult());

        Logger.debug("Loading HighScoreController");
        ControllerHelper.loadAndShowFXML(
                fxmlLoader,
                "/fxml/high-scores.fxml",
                (Stage) ((Node) actionEvent.getSource()).getScene().getWindow()
        );
    }

    @FXML
    public void handleResetButton(ActionEvent actionEvent) {
        Logger.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        Logger.info("Resetting game");
        stopwatch.stop();
        resetGame();
    }

    private void handleSolved(ObservableValue<? extends Boolean> observableValue, boolean oldValue, boolean newValue) {
        if (newValue) {
            Logger.info("Player {} has solved the game in {} steps", playerName, countOfSteps.get());
            stopwatch.stop();
            messageLabel.setText(String.format("Congratulations, %s!", playerName));
            resetButton.setDisable(true);
            giveUpFinishButton.setText("Finish");
        }
    }

    private void performMove(
            @NonNull final Position.Direction direction) {
        if (!state.checkMove(direction)){
            Logger.warn("Invalid move: {}", direction);
        }
        if(state.checkMove(direction)){
            countOfSteps.set(countOfSteps.get() + 1);
        }
        while(state.checkMove(direction)) {
            state = state.doMove(direction);

            if (!state.checkMove(direction)){
                Logger.info("Move: {}", direction);
                Logger.trace("New state: {}", state);
            }

            showState();

            if (state.isGoal()) {
                isSolved.set(true);
            }
        }
        Platform.runLater(() -> grid.requestFocus());
    }

    private void populateGrid() {
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColumnCount(); col++) {
                final var square = new StackPane();
                square.getStyleClass().add("square");
                grid.add(square, col, row);
            }
        }
    }

    private void registerKeyEventHandler() {
        final KeyCombination restartKeyCombination = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
        final KeyCombination quitKeyCombination = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
        Platform.runLater(() -> grid.getScene().setOnKeyPressed(
                keyEvent -> {
                    if (restartKeyCombination.match(keyEvent)) {
                        Logger.debug("Restarting game...");
                        resetGame();
                    } else if (quitKeyCombination.match(keyEvent)) {
                        Logger.debug("Exiting...");
                        Platform.exit();
                    } else if (keyEvent.getCode() == KeyCode.UP) {
                        Logger.debug("Up arrow pressed");
                        performMove(Position.Direction.UP);
                    } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                        Logger.debug("Right arrow pressed");
                        performMove(Position.Direction.RIGHT);
                    } else if (keyEvent.getCode() == KeyCode.DOWN) {
                        Logger.debug("Down arrow pressed");
                        performMove(Position.Direction.DOWN);
                    } else if (keyEvent.getCode() == KeyCode.LEFT) {
                        Logger.debug("Left arrow pressed");
                        performMove(Position.Direction.LEFT);
                    }
                }
        ));
    }

    private GameResult createGameResult() {
        return GameResult.builder()
                .player(playerName)
                .solved(state.isGoal())
                .duration(Duration.between(startTime, Instant.now()))
                .steps(countOfSteps.get())
                .build();
    }

    private void clearState() {
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                getGridNodeAtPosition(grid, row, col)
                        .ifPresent(node -> ((StackPane) node).getChildren().clear());
            }
        }
    }

    private void showState() {
        clearState();
        for (int i = 0; i < 2; i++) {
            final var position = state.getPosition(i);
            final var pieceView = pieceViews[i];
            getGridNodeAtPosition(grid, position)
                    .ifPresent(node -> ((StackPane) node).getChildren().add(pieceView));
        }
        Platform.runLater(() -> grid.requestFocus());
    }

    private static Optional<Node> getGridNodeAtPosition(
            @NonNull final GridPane gridPane,
            @NonNull final Position pos) {

        return getGridNodeAtPosition(gridPane, pos.getRow(), pos.getCol());
    }

    private static Optional<Node> getGridNodeAtPosition(
            @NonNull final GridPane gridPane,
            final int row,
            final int col) {

        return gridPane.getChildren().stream()
                .filter(child -> GridPane.getRowIndex(child) == row && GridPane.getColumnIndex(child) == col)
                .findFirst();
    }


}

