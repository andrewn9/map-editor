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
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ColorPicker;


import map.editor.MapTab;


public class Map{

    private int[][] map;
    private File file;

    private ToolBar toolbar;

    private ScrollPane scrollPane;
    private GridPane gridPane;
    private int size = 20;

    private int width;
    private int height;
    private Tab tab;

    private ColorPicker colorPicker;
    private int currentId;
    private Tool currentTool;

    public enum Tool {
        BRUSH,
        ERASER
    }

    public Map(int width, int height) {
        // Initialize map and UI elements
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
        this.tab = new MapTab("Untitled", this);
        createMap();
    }

    public Map(int[][] array, File file)
    {   
        // Initialize map and UI elements
        this.width = array[0].length;
        this.height = array.length;
        this.map = array;
        this.tab = new MapTab(file.getName(),this);
        setFile(file);
        createMap();
    }

    public void createMap() {
        this.gridPane = new GridPane();
        this.scrollPane = new ScrollPane();

        BorderPane borderPane = new BorderPane();
        this.tab.setContent(borderPane);

        this.scrollPane.setContent(gridPane);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        borderPane.setCenter(scrollPane);

        // Create the toolbar and zoom buttons
        toolbar = new ToolBar();
        Button zoomInButton = new Button("+");
        Button zoomOutButton = new Button("-");
        Button fitButton = new Button("=");
        
        TextField idBox = new TextField();
        idBox.setPrefWidth(25);
        idBox.setText("1");
        currentId = 1;
        idBox.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d+")) {
                currentId = Integer.parseInt(newValue);
            }
        });

        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);

        Button brush = new Button("Brush");
        Button eraser = new Button("Eraser");

        Separator separator = new Separator(Orientation.VERTICAL);

        redraw();

        // Add the zoom buttons to the toolbar
        toolbar.getItems().addAll(idBox,colorPicker,brush,eraser,separator,zoomInButton, zoomOutButton, fitButton);

        // Add the toolbar to the bottom of the border pane
        borderPane.setBottom(toolbar);

        this.currentTool = Tool.BRUSH;
        brush.setOnAction(event -> {currentTool = Tool.BRUSH;});
        eraser.setOnAction(event -> {currentTool = Tool.ERASER;});

        // Set the action for the zoom in button
        zoomInButton.setOnAction(event -> {zoom(5);});
        zoomOutButton.setOnAction(event -> {zoom(-5);});
        fitButton.setOnAction(event -> {fitMapToWindow();});

         // Add event handler for mouse pressed and dragged events
        gridPane.setOnMousePressed(event -> {
            if (currentTool == Tool.BRUSH || currentTool == Tool.ERASER) {
                handleMousePressOrDrag(event);
            }
        });

        gridPane.setOnMouseDragged(event -> {
            if (currentTool == Tool.BRUSH || currentTool == Tool.ERASER) {
                handleMousePressOrDrag(event);
            }
        });
    }
        
    private void handleMousePressOrDrag(MouseEvent event) {
        int row = (int) (event.getY() / size);
        int col = (int) (event.getX() / size);

        // Check that row and col are within the bounds of the map array
        if (row >= 0 && row < height && col >= 0 && col < width) {
            int id = (currentTool == Tool.BRUSH) ? currentId : 0;
            map[row][col] = id;
            redrawCell(row, col);
        }
    }

    private void redrawCell(int row, int col) {
        Group group = (Group) gridPane.getChildren().get(row * width + col);
        Rectangle rectangle = (Rectangle) group.getChildren().get(0);
        if(currentTool != Tool.ERASER)
        {   
            rectangle.setFill(colorPicker.getValue());
        }
        else
        {   
            rectangle.setFill(Color.WHITE);
        }
        Text text = (Text) group.getChildren().get(1);
        text.setText(String.valueOf(map[row][col]));
    }
    

    public void zoom(int increment)
    {
        // Increment/decrement size to zoom in and out
        size+=increment;
        redraw();
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
                rectangle.setStyle("-fx-stroke: rgba(0, 0, 0, 0.5); -fx-stroke-width: 1;");
                rectangle.setFill(Color.WHITE);
    
                Text text = new Text(String.valueOf(map[row][col]));
                text.setFill(Color.rgb(0, 0, 0, 0.5));
                text.setFont(Font.font("Arial", FontWeight.BOLD, size / 2));
                text.setX(rectangle.getX() + (size - text.getLayoutBounds().getWidth()) / 2);
                text.setY(rectangle.getY() + (size + text.getLayoutBounds().getHeight()) / 2);
    
                Group group = new Group(rectangle, text);
                gridPane.add(group, col, row);
            }
        }
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