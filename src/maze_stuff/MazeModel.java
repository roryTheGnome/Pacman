package maze_stuff;

import javax.swing.table.AbstractTableModel;

public class MazeModel extends AbstractTableModel {

    private final CellType[][] maze;
    private final GameObject[][] objects;
    private final Maze_Inator mazeinator;



    public MazeModel(CellType[][] maze, GameObject[][] objects,Maze_Inator mazeInator) {
        this.maze = maze;
        this.objects = objects;
        this.mazeinator = mazeInator;
    }

    @Override
    public int getRowCount() {
        return maze.length;
    }

    @Override
    public int getColumnCount() {
        return maze[0].length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return new CellData(maze[rowIndex][columnIndex], objects[rowIndex][columnIndex]);
    }

    public boolean canMove(int dx, int dy) {
        int x = mazeinator.getPacX();
        int y = mazeinator.getPacY();
        int newX = x + dx;
        int newY = y + dy;

        return newX >= 0 && newX < getColumnCount() &&
                newY >= 0 && newY < getRowCount() &&
                maze[newY][newX] == CellType.PATH;
    }
    public void movePacman(int dx, int dy) {
        mazeinator.movePackman(dx, dy);
        fireTableDataChanged();
    }

}
