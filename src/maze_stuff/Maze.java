package maze_stuff;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Maze {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int r=20;
            int c=20;

            Maze_Inator mazematic=new Maze_Inator(r,c);
            MazeBoard board=new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(),mazematic);

            JFrame frame=new JFrame("maze_stuff.Maze");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(board));
            frame.pack();
            frame.setLocationRelativeTo(null);

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> {
                            mazematic.movePackman(0, -1);
                            board.repaint();
                        }
                        case KeyEvent.VK_DOWN -> {
                            mazematic.movePackman(0, 1);
                            board.repaint();
                        }
                        case KeyEvent.VK_LEFT -> {
                            mazematic.movePackman(-1, 0);
                            board.repaint();
                        }
                        case KeyEvent.VK_RIGHT -> {
                            mazematic.movePackman(1, 0);
                            board.repaint();
                        }
                    }
                }
            });

            frame.setFocusable(true);
            frame.requestFocusInWindow();

            frame.setVisible(true);
        });
    }
}
