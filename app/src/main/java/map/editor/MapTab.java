package map.editor;

import javafx.scene.control.Tab;
import map.editor.Map;

public class MapTab extends Tab {
    private Map map;
    
    public MapTab(String title, Map map) {
        super(title);
        this.map = map;
    }

    public Map getMap() {
        return map;
    }
}
