package maze_stuff;

import WelcomePage_Stuff.ScoreEntry;
import WelcomePage_Stuff.WelcomeScreen;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static maze_stuff.Maze_Inator.score;

public class Maze {

    public static void startGame(int r,int c) {
        SwingUtilities.invokeLater(() -> {

            int maxWindowWidth = 1200;
            int maxWindowHeight = 800;

            int tileWidth = maxWindowWidth / r;
            int tileHeight = (maxWindowHeight-100) / c;
            int cellSize = Math.min(tileWidth, tileHeight);

            Maze_Inator mazematic = new Maze_Inator(r, c);
            MazeBoard board = new MazeBoard(mazematic.getMaze(), mazematic.getGameObjects(), mazematic, cellSize);

            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBackground(Color.darkGray);

            JPanel heartCounter=new JPanel(new FlowLayout(FlowLayout.LEFT));
            heartCounter.setBackground(Color.darkGray);
            for(int i=0;i<Maze_Inator.lives;i++){
                JLabel hearts=new JLabel(new ImageIcon("src/assets/lifeCounter.png"));
                heartCounter.add(hearts);
            }
            infoPanel.add(heartCounter);

            JLabel scores=new JLabel("   Score : "+score+"   ");
            scores.setForeground(Color.white);
            infoPanel.add(scores);

            JLabel timeline=new JLabel("Time : 00:00");//DO *NOT* USE TIMER!!!!
            timeline.setForeground(Color.white);
            infoPanel.add(timeline);

            JLabel multiplerCountdown=new JLabel("");
            multiplerCountdown.setForeground(Color.yellow);
            infoPanel.add(multiplerCountdown);

            JLabel freezeCountdown = new JLabel("");
            freezeCountdown.setForeground(Color.CYAN);
            infoPanel.add(freezeCountdown);

            JLabel shieldCountdown = new JLabel("");
            shieldCountdown.setForeground(Color.green);
            infoPanel.add(shieldCountdown);

            JPanel cont=new JPanel();
            cont.setLayout(new BorderLayout());
            cont.add(infoPanel, BorderLayout.NORTH);

            cont.add(board, BorderLayout.CENTER);

            JFrame frame=new JFrame("Packman");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(cont);
            frame.pack();
            frame.setLocationRelativeTo(null);


            board.setFocusable(true);
            board.requestFocusInWindow();

            frame.setVisible(true);

            Thread timer=new Thread(()->{
                int time=0;
                while(true){
                    if(mazematic.isGameOver())break;
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
                   if(mazematic.isGameOver())break;
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
                AtomicBoolean hasHandledGameOver = new AtomicBoolean(false);

                while (true) {
                    SwingUtilities.invokeLater(() -> {
                        if (!mazematic.isGameOver()) {
                            mazematic.moveGhosts();

                            if (mazematic.isPacmanDead && Maze_Inator.lives > 0) {
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(450);
                                    } catch (InterruptedException e) {}

                                    SwingUtilities.invokeLater(() -> {
                                        for (int y = 0; y < mazematic.getMaze().length; y++) {
                                            for (int x = 0; x < mazematic.getMaze()[0].length; x++) {
                                                if (mazematic.getGameObjects()[y][x] == GameObject.DEADMAN) {
                                                    mazematic.getGameObjects()[y][x] = GameObject.DOT;
                                                }
                                            }
                                        }

                                        mazematic.movePackman(1 - mazematic.getPacX(), 1 - mazematic.getPacY());
                                        mazematic.isPacmanDead = false;
                                    });
                                }).start();
                            }

                            board.repaint();
                        }

                        if (mazematic.isGameOver() && !hasHandledGameOver.get()) {
                            hasHandledGameOver.set(true);

                            String nickname = "";
                            while (nickname == null || nickname.length() != 3) {
                                nickname = JOptionPane.showInputDialog(
                                        null,
                                        "GAME OVER\nSCORE: " + Maze_Inator.score + "\n\nENTER YOUR NICK (3 LETTERS):",
                                        "Game Over",
                                        JOptionPane.PLAIN_MESSAGE
                                );

                                if (nickname == null) return;
                                nickname = nickname.trim().toUpperCase();
                            }

                            String formattedScore = String.format("%04d-%s", Maze_Inator.score, nickname);
                            try {
                                File file = new File("src/highscores.ser");
                                List<ScoreEntry> saved = new ArrayList<>();

                                if (file.exists()) {
                                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                                        saved = (List<ScoreEntry>) in.readObject();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                saved.add(new ScoreEntry(Maze_Inator.score, nickname));
                                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                                    out.writeObject(saved);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                            Window window = SwingUtilities.getWindowAncestor(board);
                            if (window != null) {
                                window.dispose();
                            }

                            SwingUtilities.invokeLater(WelcomeScreen::new);
                        }
                    });

                    if (hasHandledGameOver.get()) break;

                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            ghostThread.setDaemon(true);
            ghostThread.start();

            Thread upgradeCountdows= new Thread(()->{
                while(!mazematic.isGameOver()){

                    long currenet=System.currentTimeMillis();

                    SwingUtilities.invokeLater(() -> {

                        if(mazematic.doubleScored){
                            long countdown=mazematic.doubleScoreEndTime-currenet;

                            if(countdown>0){
                                multiplerCountdown.setText("  x2 Score: " +(countdown/1000) +"secs  ");
                            }
                            else{
                                multiplerCountdown.setText("");
                                mazematic.doubleScored=false;
                            }
                        }

                        if(mazematic.freezeEndTime > currenet){
                            long countdown=mazematic.freezeEndTime-currenet;
                            freezeCountdown.setText("  Freezed: " +(countdown/1000) +"secs  ");
                        }else{freezeCountdown.setText("");}

                        if(mazematic.shileded){
                            long countdown=mazematic.shieldEndTime-currenet;

                            if(countdown>0){
                                shieldCountdown.setText("  Shield: " +(countdown/1000) +"secs  ");
                            }
                            else{
                                shieldCountdown.setText("");
                                mazematic.shileded=false;
                            }
                        }


                    });

                    try{
                        Thread.sleep(250);
                    }catch(InterruptedException e){
                        break;
                    }
                }
            });
            upgradeCountdows.setDaemon(true);
            upgradeCountdows.start();

    });
}

}

// ***TODO LIST-Vol2***
//Add score board,timer and lives on the top                                DONE
//Adjust the WALL's to look like bricks like in the og game                 DECIDED NOT TO
//Add ghosts                                                                DONE
//Adjust packman animations to each direction                               DONE
//Fin a way to make heart red pls                                           NOPE      i changed my mind and add pacman face
//Add game over logic                                                       DONE
//Add the new score into the score board                                    DONE
//Add other ghosts                                                          DONE
//Create upgrades                                                           DONE
//Add dead logic                                                            DONE
//Adjust the screen for bigger maze size                                    DONE
//Add go back to menu in game mode via crtl+shift+q                         DONE
//Adjust the dead ends and make sure there r multi-path between 2 points    DONE
//There is something wrong with the death, fix it                           DONE
//Fix upgrade pics                                                          DONE
//Add counter for upgrades                                                  DONE