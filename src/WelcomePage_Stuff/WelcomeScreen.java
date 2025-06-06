package WelcomePage_Stuff;

import maze_stuff.Maze;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WelcomeScreen extends JFrame {
    //i kindly took photos and fonts from internet hope thats ok
    private Font pacmanFont;

    public WelcomeScreen() {
        setTitle("Pac-Man Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // i also have the button like it was asked in the documantation
        setSize(700, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 400));

        loadFont();

        ImageIcon backgroundIcon = new ImageIcon("src/assets/WelcomePage/background.png");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BoxLayout(backgroundLabel, BoxLayout.Y_AXIS));

        JPanel logoPlace = new JPanel();
        logoPlace.setOpaque(false);
        logoPlace.setLayout(new BoxLayout(logoPlace, BoxLayout.Y_AXIS));
        logoPlace.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon logoIcon = new ImageIcon("src/assets/WelcomePage/logo.png");
            Image logoImage = logoIcon.getImage().getScaledInstance(600, -1, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(logoImage));
        } catch (Exception e) { //backup plan
            logoLabel.setText("PAC-MAN");
            logoLabel.setFont(pacmanFont.deriveFont(48f));
            logoLabel.setForeground(Color.YELLOW);
        }

        logoPlace.add(logoLabel);
        logoPlace.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 30, 0));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] labels = {"New Game", "High Scores", "Exit"};
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setBackground(Color.YELLOW);
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setFont(pacmanFont.deriveFont(35f));
            if (label.equals("High Scores")) { //cause its fat
                button.setPreferredSize(new Dimension(400, 80));
            } else {
                button.setPreferredSize(new Dimension(250, 80));
            }
            button.addActionListener(e -> handleButtonClick(label));
            buttonPanel.add(button);
        }

        logoPlace.add(Box.createVerticalStrut(30));
        logoPlace.add(buttonPanel);
        logoPlace.add(Box.createVerticalStrut(30));

        backgroundLabel.add(logoPlace);
        setContentPane(backgroundLabel);
        setVisible(true);
    }

    private void loadFont() {
        try {
            File fontFile = new File("src/assets/Fonts/font2.ttf");
            pacmanFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pacmanFont);
        } catch (FontFormatException | IOException e) {
            pacmanFont = new Font("Arial", Font.BOLD, 20);
            System.err.println("Custom font load failed, using default.");
        }
    }

    private void handleButtonClick(String label) {
        switch (label) {
            case "New Game" :
                System.out.println("New Game clicked"); //here for debugging
                newGame();
                break;
            case "High Scores" :
                System.out.println("High Scores clicked"); //here fot debuggong purp.
                dispose();
                SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("Pac-Man High Scores");
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setSize(400, 500);
                    frame.setContentPane(new HighScores(frame));
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
                break;
            case "Exit" :
                System.exit(0);
        }
    }

    private void newGame(){

        JDialog dialog = new JDialog(this, "New Game",true);
        dialog.setSize(300,200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel txt=new JLabel("Enter board size",JLabel.CENTER);
        txt.setFont(pacmanFont.deriveFont(22f));
        dialog.add(txt,BorderLayout.NORTH);

        JPanel choices=new JPanel(new GridLayout(2,2,10,10));
        choices.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel w=new JLabel("Width:");
        JTextField wChoice=new JTextField("20");

        JLabel h=new JLabel("Height:");
        JTextField hChoice=new JTextField("20");

        choices.add(w);
        choices.add(wChoice);
        choices.add(h);
        choices.add(hChoice);

        dialog.add(choices,BorderLayout.CENTER);

        JButton letsGetThisStarted=new JButton("ENTER");
        letsGetThisStarted.setFont(pacmanFont.deriveFont(20f));
        letsGetThisStarted.setBackground(Color.YELLOW);
        letsGetThisStarted.setFocusPainted(false);

        letsGetThisStarted.addActionListener(e -> {
            try {
                int width = Integer.parseInt(wChoice.getText().trim());
                int height = Integer.parseInt(hChoice.getText().trim());

                if (width < 10 || width > 100 || height < 10 || height > 100) {
                    JOptionPane.showMessageDialog(dialog, "Enter values between 10 and 100");
                    return;
                }

                dialog.dispose();
                dispose();
                Maze.startGame(height, width);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid NUMBERS");
            }
        });

        JPanel bp=new JPanel();
        bp.add(letsGetThisStarted);

        dialog.add(bp,BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(letsGetThisStarted);
        dialog.setVisible(true);

    }

    public static void main(String[] args) {SwingUtilities.invokeLater(WelcomeScreen::new);}
}

//***TODO LIST***
//add welcome page                      DONE
//add buttons 3                         DONE
//add high scores page                  DONE
//add high scores doc                   DONE
//connect it to the game                DONE
//set the game board size rules         DONE
//Maybe add main to Main class??        DONE
//Fix highscores                        DONE
//Fix high scores                       DONE