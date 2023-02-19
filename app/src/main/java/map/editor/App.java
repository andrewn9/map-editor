package map.editor;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Create a label with some text
        Label label = new Label("Hello, World!");

        // Create a layout pane and add the label to it
        StackPane root = new StackPane();
        root.getChildren().add(label);

        // Create a scene with the layout pane and set it on the stage
        Scene scene = new Scene(root, 320, 240);
        stage.setScene(scene);

        // Set the title of the window and show it
        stage.setTitle("My Window");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
