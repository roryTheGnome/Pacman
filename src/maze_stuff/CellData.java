package maze_stuff;

public class CellData {

    public final CellType type;
    public final GameObject object;

    public CellData(CellType type, GameObject object) {
        this.type = type;
        this.object = object;
    }
}

