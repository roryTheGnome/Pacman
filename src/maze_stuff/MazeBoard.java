package maze_stuff;

import WelcomePage_Stuff.WelcomeScreen;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class MazeBoard extends JTable {

    private final Maze_Inator mazeinator;
    private int animationFrame = 0;

    private Thread moveThread;
    private volatile String currentDirection = "RIGHT";
    private volatile boolean running = false;

    public MazeBoard(CellType[][] maze, GameObject[][] objects, Maze_Inator mazeinator, int cellSize) {
        super(new MazeModel(maze, objects, mazeinator));
        this.mazeinator = mazeinator;

        setRowHeight(cellSize);
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

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "exitToMenu");

        getActionMap().put("exitToMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window w=SwingUtilities.getWindowAncestor(MazeBoard.this);
                if(w!=null)w.dispose();

                SwingUtilities.invokeLater(WelcomeScreen::new);
            }
        });

    }

    private void startAnimationThread() {
        Thread animationThread = new Thread(() -> {
            while (true) {
                if(mazeinator.isGameOver())break;
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
                if(mazeinator.isGameOver())break;
                if (!model.canMove(dx, dy))break;

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
        private final ImageIcon[] pacmanLeft = {
                new ImageIcon("src/assets/left/1.png"),
                new ImageIcon("src/assets/left/2.png"),
                new ImageIcon("src/assets/left/3.png")
        };
        private final ImageIcon[] pacmanDown = {
                new ImageIcon("src/assets/down/1.png"),
                new ImageIcon("src/assets/down/2.png"),
                new ImageIcon("src/assets/down/3.png")
        };
        private final ImageIcon[] pacmanUp = {
                new ImageIcon("src/assets/up/1.png"),
                new ImageIcon("src/assets/up/2.png"),
                new ImageIcon("src/assets/up/3.png")
        };

        private final ImageIcon blinkyIcon = new ImageIcon("src/assets/ghosts/blinky.png");
        private final ImageIcon clydeIcon = new ImageIcon("src/assets/ghosts/clyde.png");
        private final ImageIcon inkyIcon = new ImageIcon("src/assets/ghosts/inky.png");
        private final ImageIcon pinkyIcon = new ImageIcon("src/assets/ghosts/pinky.png");

        private final ImageIcon scoreMultiplier=new ImageIcon("src/assets/upgrades/apple.png");
        private final ImageIcon extraScore=new ImageIcon("src/assets/upgrades/cherry.png");
        private final ImageIcon freeze=new ImageIcon("src/assets/upgrades/strawberry.png");
        private final ImageIcon heart=new ImageIcon("src/assets/upgrades/heart.png");
        private final ImageIcon shield =new ImageIcon("src/assets/upgrades/shield.png");

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
                    setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                } else if (cell.type == CellType.PATH) {
                    c.setBackground(Color.BLACK);

                    if (cell.object == GameObject.DOT) {
                        setIcon(dotIcon);
                    } else if (cell.object == GameObject.PACMAN) {
                        //setIcon(pacmanRight[animationFrame]);
                        ImageIcon[] pacman = pacmanRight;
                        switch (currentDirection) {
                            case "UP": 
                                pacman= pacmanUp;
                                break;
                            case "DOWN":
                                pacman = pacmanDown;
                                break;
                            case "LEFT":
                                pacman= pacmanLeft;
                                break;
                            case "RIGHT":
                                pacman = pacmanRight;
                                break;
                            default:
                                pacman=pacmanRight;
                        }
                        setIcon(pacman[animationFrame]);
                    }else if(cell.object == GameObject.BLINKY) {
                        setIcon(blinkyIcon);
                    }else if(cell.object == GameObject.INKY) {
                        setIcon(inkyIcon);
                    }else if(cell.object == GameObject.CLYDE) {
                        setIcon(clydeIcon);
                    }else if(cell.object == GameObject.PINKY) {
                        setIcon(pinkyIcon);
                    }else if(cell.object == GameObject.UPGRADE_FREEZE) {
                        setIcon(freeze);
                    }else if(cell.object == GameObject.UPGRADE_LIFE) {
                        setIcon(heart);
                    } else if(cell.object == GameObject.UPGRADE_MULTIPLIER) {
                        setIcon(scoreMultiplier);
                    }else if(cell.object == GameObject.UPGRADE_SHIELD) {
                        setIcon(shield);
                    }else if(cell.object == GameObject.UPGRADE_SCORE) {
                        setIcon(extraScore);
                    }else {
                        setIcon(null);
                    }
                }
            }
            return c;
        }
    }
}

