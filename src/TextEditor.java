import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

import java.util.*;
import java.io.*;

public class TextEditor extends Application {
    @Override
    public void start(Stage primaryStage) {

        MenuItem newb = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem save = new MenuItem("_Save");
        MenuItem saveAs = new MenuItem("Save _As");
        MenuItem exit = new MenuItem("Exit");
        MenuItem exitAll = new MenuItem("Exit All");
        MenuButton fileMenu = new MenuButton("File", null, newb, open, new SeparatorMenuItem(), save, saveAs,
                new SeparatorMenuItem(), exit, exitAll);

        Button undo = new Button("_Undo");
        Button redo = new Button("_Redo");

        CheckMenuItem wordWrap = new CheckMenuItem("Word Wrap");
        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem keyword = new MenuItem("Disable Keywords");
        MenuButton view = new MenuButton("View", null, wordWrap, new SeparatorMenuItem(), zoomIn, zoomOut,
                new SeparatorMenuItem(), keyword);

        ToolBar toolBar = new ToolBar(fileMenu, undo, redo, view);

        Stack<String> undoStack = new Stack<>();
        Stack<String> redoStack = new Stack<>();

        // primaryStage.setFullScreen(true);
        // primaryStage.setMaximized(true);
        // primaryStage.setAlwaysOnTop(true);
        primaryStage.setFullScreenExitHint("Press ESC to exit full screen mode");
        TextArea textArea = new TextArea();
        textArea.wrapTextProperty().bindBidirectional(wordWrap.selectedProperty());

        StackPane stackPane = new StackPane(textArea);
        BorderPane borderPane = new BorderPane(stackPane);
        borderPane.setTop(toolBar);
        Scene scene = new Scene(borderPane);

        primaryStage.setTitle("Lemon TextEditor"); // Set the stage title
        primaryStage.getIcons().add(new Image("file:images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();

        newb.setOnAction(e -> {
            Platform.runLater(() -> {
                TextEditor t = new TextEditor();
                Stage stage = new Stage();
                t.start(stage);
            });
        });

        SimpleStringProperty path = new SimpleStringProperty();
        SimpleIntegerProperty textHash = new SimpleIntegerProperty();

        open.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
                    new ExtensionFilter("All Files", "*.*"));
            fileChooser.setTitle("Open Resource File");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                path.setValue(file.getPath());
                try {
                    Scanner in = new Scanner(file);
                    while (in.hasNext()) {
                        textArea.appendText(in.nextLine() + "\n");
                    }
                    in.close();
                    textHash.setValue(textArea.getText().hashCode());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        save.setOnAction(e -> {
            File file;
            if (path.getValue() == null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new ExtensionFilter("Text file", "*.txt"));
                fileChooser.setTitle("Save Resource File");
                file = fileChooser.showSaveDialog(primaryStage);
                path.set(file.getPath());
            } else {
                file = new File(path.getValue());
            }

            if (file != null) {
                try {
                    PrintWriter out = new PrintWriter(file);
                    out.print(textArea.getText());
                    out.close();
                    textHash.setValue(textArea.getText().hashCode());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        saveAs.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"),
                    new ExtensionFilter("All Files", "*.*"));
            fileChooser.setTitle("Save Resource File");
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                path.set(file.getPath());
                try {
                    PrintWriter out = new PrintWriter(file);
                    out.print(textArea.getText());
                    out.close();
                    textHash.setValue(textArea.getText().hashCode());
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        primaryStage.setOnCloseRequest(e -> {
            if (path.getValue() == null && textArea.getText().equals("")) {
                primaryStage.close();
                return;
            }
            if (textHash.getValue() == textArea.getText().hashCode()) {
                primaryStage.close();
                return;
            }

            Alert alert = new Alert(AlertType.CONFIRMATION, "Exit without Saving?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                primaryStage.close();
            } else {
                e.consume();
            }

        });

        exit.setOnAction(e -> {

            if (path.getValue() == null && textArea.getText().equals("")) {
                primaryStage.close();
                return;
            }
            if (textHash.getValue() == textArea.getText().hashCode()) {
                primaryStage.close();
                return;
            }
            Alert alert = new Alert(AlertType.CONFIRMATION, "Exit without Saving?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                primaryStage.close();
            }
        });

        exitAll.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Exit ALL??!!?!?!?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.exit(0);
            }
        });

        Timeline stackTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (undoStack.isEmpty()) {
                undoStack.push("");
            }
            if (!textArea.getText().equals(undoStack.peek())) {
                undoStack.push(textArea.getText());
            }

        }));
        stackTimeline.setCycleCount(Timeline.INDEFINITE);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            stackTimeline.play();
        });

        undo.setOnAction(e -> {
            System.out.println(undoStack);
            if (!undoStack.isEmpty() && undoStack.peek().equals(textArea.getText())) {
                redoStack.push(undoStack.pop());
            }
            if (!undoStack.isEmpty()) {
                redoStack.push(undoStack.peek());
                textArea.setText(undoStack.pop());
            }
            stackTimeline.pause();

        });

        redo.setOnAction(e -> {
            System.out.println(redoStack);
            if (!redoStack.isEmpty() && redoStack.peek().equals(textArea.getText())) {
                undoStack.push(redoStack.pop());
            }
            if (!redoStack.isEmpty()) {
                undoStack.push(redoStack.pop());
                textArea.setText(undoStack.peek());
            }
            stackTimeline.pause();
        });

        zoomIn.setOnAction(e -> {
            textArea.setStyle("-fx-font-size: " + (textArea.getFont().getSize() + 2) + "px;");

        });

        zoomOut.setOnAction(e -> {
            textArea.setStyle("-fx-font-size: " + (textArea.getFont().getSize() - 2) + "px;");
        });

        ImageView iView = new ImageView(new Image("file:images/professor.jpg"));
        iView.setPreserveRatio(true);
        iView.fitWidthProperty().bind(stackPane.widthProperty());
        iView.fitHeightProperty().bind(stackPane.heightProperty());
        iView.setOpacity(.3);
        iView.setMouseTransparent(true);

        Bouncing balls = new Bouncing();
        balls.setMouseTransparent(true);
        balls.setOpacity(.3);

        Timeline keywordTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            if (textArea.getText().contains("professor")) {
                if (!stackPane.getChildren().contains(iView))
                    stackPane.getChildren().add(iView);
            } else {
                if (stackPane.getChildren().contains(iView)) {
                    stackPane.getChildren().remove(iView);
                }
            }
            if (textArea.getText().contains("ball")) {
                if (!stackPane.getChildren().contains(balls)) {
                    stackPane.getChildren().add(balls);
                    balls.start();
                }
            } else {
                if (stackPane.getChildren().contains(balls)) {
                    balls.stop();
                    stackPane.getChildren().remove(balls);
                }
            }
        }));
        keywordTimeline.setCycleCount(Timeline.INDEFINITE);
        keywordTimeline.play();

        keyword.setOnAction(e -> {
            if (keyword.getText().equals("Disable Keywords")) {
                keyword.setText("Enable Keywords");
                keywordTimeline.stop();
                if (stackPane.getChildren().contains(iView)) {
                    stackPane.getChildren().remove(iView);
                }
                if (stackPane.getChildren().contains(balls)) {
                    balls.stop();
                    stackPane.getChildren().remove(balls);
                }
            } else {
                keyword.setText("Disable Keywords");
                keywordTimeline.play();

            }

        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
