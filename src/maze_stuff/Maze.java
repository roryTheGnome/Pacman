package maze_stuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static maze_stuff.Maze_Inator.score;

public class Maze {
    public static int life;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int r=20;
            int c=20;
            life=3;

            Maze_Inator mazematic=new Maze_Inator(r,c);
            MazeBoard board=new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(),mazematic);

            //THE UP PANEL STUFF
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(Color.darkGray);

            for(int i=0;i<life;i++){
                JLabel hearts=new JLabel(" ❤\uFE0F ");//FIND A WAY TO MAKE IT RED
                infoPanel.add(hearts);
            }

            JLabel scores=new JLabel("   Score : "+score+"   ");
            scores.setForeground(Color.white);
            infoPanel.add(scores);

            JLabel timeline=new JLabel("Time : 00:00");//DO NOT USE TIMER
            timeline.setForeground(Color.white);
            infoPanel.add(timeline);

            JPanel cont=new JPanel();
            cont.setLayout(new BorderLayout());
            cont.add(infoPanel, BorderLayout.NORTH);

            cont.add(new JScrollPane(board), BorderLayout.CENTER);

            JFrame frame=new JFrame("Packman");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //frame.add(new JScrollPane(board));
            frame.add(cont);
            frame.pack();
            frame.setLocationRelativeTo(null);


            board.setFocusable(true);
            board.requestFocusInWindow();

            frame.setVisible(true);

            Thread timer=new Thread(()->{
                int time=0;
                while(true){  //change true into a flag
                    int mins=time/60;
                    int secs=time%60;
                    String timeTXT=String.format("Time: %02d:%02d", mins, secs);
                    SwingUtilities.invokeLater(() -> timeline.setText(timeTXT));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                    time++;
                }
            });
            timer.setDaemon(true);
            timer.start();
        });
    }
}

// ***TODO LIST-Vol3***
//Add score board,timer and lives on the top
//Adjust the WALL's to look like bricks like in the og game
//Add ghosts
//Adjust packman animations to each direction                               DONE
//Fin a way to make heart red pls



//LOOK WHAT I FOUND ❤️
