import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.util.*;
import java.io.*;

public class TextEditor extends Application {
    @Override
    public void start(Stage primaryStage) {
        Stack stack = new Stack<>();
        Stack stack2 = new Stack<>();
        TextArea ta = new TextArea();

        BorderPane pane = new BorderPane();
        pane.setCenter(ta);

        Scene scene = new Scene(pane, 450, 200);
        primaryStage.setTitle("TextEditor"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setFullScreenExitHint("Press ESC to exit full screen mode");
        HBox hBox = new HBox();
        Button newb = new Button("New");
        Button open = new Button("Open");
        Button save = new Button("Save");
        Button saveAs = new Button("Save As");
        Button exit = new Button("Exit");
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");
        hBox.getChildren().addAll(newb, open, save, saveAs, exit, undo, redo);
        TextArea textArea = new TextArea();
        BorderPane borderPane = new BorderPane(textArea);
        borderPane.setTop(hBox);
        Scene scene2 = new Scene(borderPane);
        primaryStage.setScene(scene2);
        primaryStage.show();

        // add text to stack
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            stack.push(oldValue);
            stack2.push(newValue);
            stack2.clear();
        });

        // undo

        newb.setOnAction(e -> {
            textArea.clear();
            stack.clear();
            stack2.clear();
        });
        
        SimpleStringProperty path = new SimpleStringProperty();
        // open handler
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
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //save halder
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
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

//saveas handler
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
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        exit.setOnAction(e -> {
            primaryStage.close();
        });
        undo.setOnAction(e -> {
            if (!stack.isEmpty()) {
                textArea.setText((String) stack.pop());
            }
        });
        redo.setOnAction(e -> {
            if (!stack2.isEmpty()) {
                textArea.setText((String) stack2.pop());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
