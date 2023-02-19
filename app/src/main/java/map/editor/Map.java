package map.editor;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Cell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Map {

    private int[][] map;
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
        this.tab = new Tab("Untitled");

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

    public void redraw() {
        // Clear all children in gridPane
        gridPane.getChildren().clear();

        // Re-populate the grid pane with new cells
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Rectangle rectangle = new Rectangle(size, size);
                rectangle.setStyle("-fx-fill: black; -fx-stroke: white; -fx-stroke-width: 1;");
                gridPane.add(rectangle, col, row);
            }
        }

        // Add the plus and minus buttons to the bottom right corner
        createButtons();
        createButtonPane();

        // Bind events to buttons
        plusButton.setOnAction(event -> zoom(0.05));
        minusButton.setOnAction(event -> zoom(-.05));
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