package map.editor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Cell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ToolBar;

import map.editor.MapTab;


public class Map{

    private int[][] map;
    private File file;

    private ToolBar toolbar;

    private ScrollPane scrollPane;
    private GridPane gridPane;
    private AnchorPane buttonPane;
    private int size = 20;

    private int width;
    private int height;
    private Tab tab;
    private Button plusButton;
    private Button minusButton;
    private Button fitButton;

    public Map(int width, int height) {
        // Initialize map and UI elements
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
        this.tab = new MapTab("Untitled", this);

        this.gridPane = new GridPane();
        this.scrollPane = new ScrollPane();

        BorderPane borderPane = new BorderPane();
        this.tab.setContent(borderPane);
        
        createButtons();
        redraw();

        // Add scrolling
        this.scrollPane.setContent(gridPane);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        borderPane.setCenter(scrollPane);
        borderPane.setBottom(toolbar);
    }

    public Map(int[][] array, File file)
    {   
        // Initialize map and UI elements
        this.width = array[0].length;
        this.height = array.length;
        this.map = array;
        this.tab = new MapTab(file.getName(),this);
        setFile(file);

        this.gridPane = new GridPane();
        this.scrollPane = new ScrollPane();

        BorderPane borderPane = new BorderPane();
        this.tab.setContent(borderPane);

        createButtons();
        redraw();
        
        // Add scrolling
        this.scrollPane.setContent(gridPane);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        borderPane.setCenter(scrollPane);
        createButtonPane();
        borderPane.setBottom(getButtonPane());
    }
    
    public void zoom(int increment)
    {
        // Increment/decrement size to zoom in and out
        size+=increment;
        redraw();
    }

    public void createButtons() {
        // Zoom control buttons
        plusButton = new Button("+");
        minusButton = new Button("-");
        fitButton = new Button("=");
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public void save()
    {
        if(file != null)
        {
            // Create Gson instance
            Gson gson = new Gson();

            // Write JSON string to file
            try (FileWriter writer = new FileWriter(file.getPath())) {
                gson.toJson(map, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            saveAs();
        }
    }

    public void saveAs()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        
        // Set extension filter for JSON files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        
        File mapFolder = new File("maps");
        fileChooser.setInitialDirectory(mapFolder);

        fileChooser.getExtensionFilters().add(extFilter);
        
        // Show save file dialog
        File saveFile = fileChooser.showSaveDialog(new Stage());
        
        if (saveFile != null) {
            try {
                // Serialize your data to JSON string
                String jsonData = new Gson().toJson(map);
                
                // Write JSON string to file
                FileWriter writer = new FileWriter(saveFile);
                writer.write(jsonData);
                writer.close();
                this.file = saveFile;
                this.tab.setText(saveFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void redraw() {
        // Clear all children in gridPane
        gridPane.getChildren().clear();

        // Re-populate the grid pane with new cells
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                Rectangle rectangle = new Rectangle(size, size);
                rectangle.setStyle("-fx-fill: white; -fx-stroke: rgba(0, 0, 0, 0.5); -fx-stroke-width: 1;");
            
                Text text = new Text(String.valueOf(map[row][col]));
                text.setFill(Color.rgb(0, 0, 0, 0.5));
                text.setFont(Font.font("Arial", FontWeight.BOLD, size / 2));
                text.setX(rectangle.getX() + (size - text.getLayoutBounds().getWidth()) / 2);
                text.setY(rectangle.getY() + (size + text.getLayoutBounds().getHeight()) / 2);

                Group group = new Group(rectangle, text);
                gridPane.add(group, col, row);
            }
        }

        // Add the plus and minus buttons to the bottom right corner
        createButtons();
        createButtonPane();

        // Bind events to buttons
        plusButton.setOnAction(event -> zoom(5));
        minusButton.setOnAction(event -> zoom(-5));
        fitButton.setOnAction(event -> fitMapToWindow());
    }

    public void createButtonPane()
    {
        // Create "sticky" pane in bottom right
        buttonPane = new AnchorPane();
        buttonPane.setBottomAnchor(plusButton, 5.0);
        buttonPane.setRightAnchor(plusButton, 30.0);

        buttonPane.setBottomAnchor(minusButton, 5.0);
        buttonPane.setRightAnchor(minusButton, 10.0);

        buttonPane.setBottomAnchor(fitButton, 5.0);
        buttonPane.setRightAnchor(fitButton, 55.0);

        // Parent the buttons to the pane
        buttonPane.getChildren().addAll(plusButton, minusButton, fitButton);
    }

    public AnchorPane getButtonPane() {
        return buttonPane;
    }

    public void fitMapToWindow() {
        // Calculate how large to make tiles to fit
        double newTileSize = Math.min(scrollPane.getWidth() / width, scrollPane.getHeight() / height);
        gridPane.setPrefWidth(size * width);
        gridPane.setPrefHeight(size * height);
        double newHvalue = (scrollPane.getHvalue() * (gridPane.getWidth() - scrollPane.getWidth()) + scrollPane.getWidth() / 2) / size * width;
        double newVvalue = (scrollPane.getVvalue() * (gridPane.getHeight() - scrollPane.getHeight()) + scrollPane.getHeight() / 2) / size * height;
        scrollPane.setHvalue(newHvalue);
        scrollPane.setVvalue(newVvalue);
        size = (int) newTileSize;
        redraw();
    }

    public Tab getTab()
    {
        return this.tab;
    }
}