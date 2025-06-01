package maze_stuff;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MazeBoard extends JTable {

    private final Maze_Inator mazeinator;
    private int animationFrame = 0;

    private Thread moveThread;
    private volatile String currentDirection = null;
    private volatile boolean running = false;

    public MazeBoard(CellType[][] maze, GameObject[][] objects, Maze_Inator mazeinator) {
        super(new MazeModel(maze, objects, mazeinator));
        this.mazeinator = mazeinator;

        setRowHeight(20);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setDefaultRenderer(Object.class, new CellRenderer());

        InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        im.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");

        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMoving("UP");
            }
        });
        am.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMoving("DOWN");
            }
        });
        am.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMoving("LEFT");
            }
        });
        am.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMoving("RIGHT");
            }
        });
        startAnimationThread();
    }

    private void startAnimationThread() {
        Thread animationThread = new Thread(() -> {
            while (true) {
                animationFrame = (animationFrame + 1) % 3;
                repaint();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        animationThread.setDaemon(true);
        animationThread.start();
    }

    public void startMoving(String direction) {
        if (running && currentDirection != null && currentDirection.equals(direction)) {
            return;
        }

        currentDirection = direction;
        running = true;

        if (moveThread != null && moveThread.isAlive()) {
            moveThread.interrupt();
        }

        moveThread = new Thread(() -> {
            MazeModel model = (MazeModel) getModel();
            int dx = 0, dy = 0;

            switch (direction) {
                case "UP" -> dy = -1;
                case "DOWN" -> dy = 1;
                case "LEFT" -> dx = -1;
                case "RIGHT" -> dx = 1;
            }

            while (!Thread.currentThread().isInterrupted()) {
                if (!model.canMove(dx, dy)) {
                    break;
                }

                model.movePacman(dx, dy);
                animationFrame = (animationFrame + 1) % 3;
                repaint();

                try {
                    Thread.sleep(150); // control speed
                } catch (InterruptedException e) {
                    break;
                }
            }

            running = false;
        });

        moveThread.start();
    }

    public class CellRenderer extends DefaultTableCellRenderer {
        private final ImageIcon dotIcon = new ImageIcon("src/assets/dot.png");
        private final ImageIcon[] pacmanRight = {
                new ImageIcon("src/assets/right/1.png"),
                new ImageIcon("src/assets/right/2.png"),
                new ImageIcon("src/assets/right/3.png")
        };

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setText("");

            if (value instanceof CellData cell) {
                if (cell.type == CellType.WALL) {
                    c.setBackground(Color.BLUE);
                    setIcon(null);
                } else if (cell.type == CellType.PATH) {
                    c.setBackground(Color.BLACK);

                    if (cell.object == GameObject.DOT) {
                        setIcon(dotIcon);
                    } else if (cell.object == GameObject.PACMAN) {
                        setIcon(pacmanRight[animationFrame]);
                    } else {
                        setIcon(null);
                    }
                }
            }

            return c;
        }
    }
}

