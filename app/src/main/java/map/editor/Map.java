package map.editor;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

public class Map {

    private int[][] map;
    private int width;
    private int height;
    private Tab tab;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.map = new int[width][height];
        this.tab = new Tab("Untitled");
        TextArea textArea = new TextArea();
        textArea.setEditable(true);
        this.tab.setContent(textArea);
    }

    public void saveMap(String fileName) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(this.map, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tab getTab() {
        return this.tab;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }

    public int get(int x, int y) {
        return this.map[x][y];
    }

    public void set(int x, int y, int value) {
        this.map[x][y] = value;
    }

    public void clear() {
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.map[i][j] = 0;
            }
        }
    }
    
    public TextArea getTextArea() {
        return (TextArea) this.tab.getContent();
    }
    
    public void setTextArea(TextArea textArea) {
        this.tab.setContent(textArea);
    }
    
    public String getTabName() {
        return this.tab.getText();
    }
    
    public void setTabName(String tabName) {
        this.tab.setText(tabName);
    }
}
