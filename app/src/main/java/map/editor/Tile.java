import java.awt.Color;

public class Tile {
    private int id;
    private Color color;

    public Tile(int id, Color color) {
        this.id = id;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
