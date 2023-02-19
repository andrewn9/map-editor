package map.editor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import map.editor.Map;

public class App extends Application {

    private TabPane tabPane;
    private BorderPane root;
    private Gson gson;

    @Override
    public void start(Stage stage) {
        // Create a menu bar 
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu optionsMenu = new Menu("Options");
        menuBar.getMenus().addAll(fileMenu, optionsMenu);

        // Add dropdown for file
        MenuItem newMenuItem = new MenuItem("New");
        MenuItem openMenuItem = new MenuItem("Open from File");
        fileMenu.getItems().addAll(newMenuItem, openMenuItem);

        // Create a tab pane to hold the open maps
        tabPane = new TabPane();
        
        // Create a layout pane and add the menu bar and tab pane to it
        root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(tabPane);

        // Create a scene with the layout pane and set it on the stage
        Scene scene = new Scene(root, 640, 400);
        stage.setScene(scene);

        // Set the title of the window and show it
        stage.setTitle("Map Editor");
        stage.show();

        // Set up event handlers for menu items
        noFile();
        tabPane.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if (tabPane.getTabs().isEmpty()) {
                noFile();
            }
        });

        newMenuItem.setOnAction(e -> {
            handleNewFile();
        });

        openMenuItem.setOnAction(e -> {
            openFile();
        });
    }

    private void noFile()
    {
        // Add label with instructions
        Label messageLabel = new Label("Create or open a new file\nFile > New\nFile > Open from file");
        messageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: gray;");
        BorderPane.setAlignment(messageLabel, Pos.CENTER);
        root.setCenter(messageLabel);
    }

    private void openFile()
    {
        // To be implemented
    }

    private void handleNewFile() {
        // Create a custom dialog box to prompt the user for the width and height of the map
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("New Map");
        dialog.setHeaderText("Enter the dimensions of the new map:");

        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create the width and height input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField widthField = new TextField();
        widthField.setPromptText("Width");
        TextField heightField = new TextField();
        heightField.setPromptText("Height");

        grid.add(new Label("Width:"), 0, 0);
        grid.add(widthField, 1, 0);
        grid.add(new Label("Height:"), 0, 1);
        grid.add(heightField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result of the dialog to a pair of width and height values
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    return new Pair<>(width, height);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
        result.ifPresent(widthHeight -> {
            // Create a new map with the given width and height
            Map newMap = new Map(widthHeight.getKey(), widthHeight.getValue());
            tabPane.getTabs().add(newMap.getTab());
            tabPane.getSelectionModel().select(newMap.getTab());
            root.setCenter(tabPane);
        });

    }

    public static void main(String[] args) {
        // Init program
        launch(args);
    }
}
