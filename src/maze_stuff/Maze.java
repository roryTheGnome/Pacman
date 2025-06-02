package maze_stuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static maze_stuff.Maze_Inator.score;

public class Maze {
    //public static int life;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int r=20;
            int c=20;
            //life=3;

            Maze_Inator mazematic=new Maze_Inator(r,c);
            MazeBoard board=new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(),mazematic);

            //THE UP PANEL STUFF
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(Color.darkGray);

            JPanel heartCounter=new JPanel(new FlowLayout(FlowLayout.LEFT));
            heartCounter.setBackground(Color.darkGray);
            for(int i=0;i<Maze_Inator.lives;i++){
                JLabel hearts=new JLabel(new ImageIcon("src/assets/lifeCounter.png"));//FIND A WAY TO MAKE IT RED
                heartCounter.add(hearts);
            }
            infoPanel.add(heartCounter);

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

            Thread scoreBoard=new Thread(()->{
               while(true){
                   int currentScore=Maze_Inator.score;

                   SwingUtilities.invokeLater(() -> scores.setText("   Score : " + currentScore + "   "));

                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       break;
                   }
               }

            });
            scoreBoard.setDaemon(true);
            scoreBoard.start();

            Thread lifeBoard=new Thread(()->{
                while(true){
                    int currentLives=Maze_Inator.lives;

                    SwingUtilities.invokeLater(() -> {
                        heartCounter.removeAll();
                        for (int i = 0; i < currentLives; i++) {
                            JLabel hearts = new JLabel(new ImageIcon("src/assets/lifeCounter.png"));
                            heartCounter.add(hearts);
                        }
                        heartCounter.revalidate();
                        heartCounter.repaint();
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }

            });
            lifeBoard.setDaemon(true);
            lifeBoard.start();


        Thread ghostThread = new Thread(() -> {
            while (true) {
                SwingUtilities.invokeLater(() -> {
                    mazematic.moveGhosts();
                    board.repaint();
                });

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        ghostThread.setDaemon(true);
        ghostThread.start();
    });
}}

// ***TODO LIST-Vol3***
//Add score board,timer and lives on the top                                DONE
//Adjust the WALL's to look like bricks like in the og game
//Add ghosts                                                                DONE
//Adjust packman animations to each direction                               DONE
//Fin a way to make heart red pls                                           NOPE      i changed my mind and add pacman face
//Add game over logic


//LOOK WHAT I FOUND ❤️
