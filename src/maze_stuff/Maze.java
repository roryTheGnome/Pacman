package maze_stuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static maze_stuff.Maze_Inator.score;

public class Maze {
    //public static int life;
    public static void startGame() {
        SwingUtilities.invokeLater(() -> {
            int r=100;
            int c=100;

            int maxWindowWidth = 1200;
            int maxWindowHeight = 800;

            int tileWidth = maxWindowWidth / r;
            int tileHeight = (maxWindowHeight-100) / c;
            int cellSize = Math.min(tileWidth, tileHeight);


            //Maze_Inator mazematic=new Maze_Inator(r,c);
            //MazeBoard board=new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(),mazematic);

            Maze_Inator mazematic = new Maze_Inator(r, c);
            MazeBoard board = new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(), mazematic, cellSize);



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
                    if (mazematic.isPacmanDead && Maze_Inator.lives > 0) {
                        new Thread(() -> {
                           try{
                               Thread.sleep(450);//150*3 change here if you change the animation speed
                           } catch (InterruptedException e) {}

                            SwingUtilities.invokeLater(() -> {
                                // Clear old Pacman position (now BLINKY probably)
                                for (int y = 0; y < mazematic.getMaze().length; y++) {
                                    for (int x = 0; x < mazematic.getMaze()[0].length; x++) {
                                        if (mazematic.getGameObjects()[y][x] == GameObject.DEADMAN) {
                                            mazematic.getGameObjects()[y][x] = GameObject.DOT;
                                        }
                                    }
                                }

                                // Reset Pacman to (1,1)
                                mazematic.movePackman(1 - mazematic.getPacX(), 1 - mazematic.getPacY());

                                // Reset flag
                                mazematic.isPacmanDead = false;
                            });

                        }).start();
                    }
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
}
    public static void main (String args[]){
        startGame();
    }
}

// ***TODO LIST-Vol3***
//Add score board,timer and lives on the top                                DONE
//Adjust the WALL's to look like bricks like in the og game
//Add ghosts                                                                DONE
//Adjust packman animations to each direction                               DONE
//Fin a way to make heart red pls                                           NOPE      i changed my mind and add pacman face
//Add game over logic
//Add other ghosts
//Create upgrades
//Add dead logic                                                            DONE
//Add death animation
//Adjust the screen for bigger maze size

//LOOK WHAT I FOUND ❤️
