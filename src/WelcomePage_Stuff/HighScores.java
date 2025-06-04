package WelcomePage_Stuff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class HighScores extends JPanel {
    private List<Integer> scores;
    private Font titleFont;
    private Font scoreFont;

   public HighScores() {
       loadFonts();
       getScores();

       setBackground(Color.BLACK);
       setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
       setAlignmentX(CENTER_ALIGNMENT);

       JLabel title = new JLabel("HIGH SCORES");
       title.setFont(titleFont);
       title.setAlignmentX(CENTER_ALIGNMENT);
       title.setForeground(Color.WHITE);
       title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
       add(title);


       for(int i=0;i<scores.size();i++){
           String rank=String.format("%2d.", i + 1);//first part jsut cause it looks fancy
           String score=String.format("%04d", scores.get(i));
           JLabel scoreLabel = new JLabel(rank + "    " + score);
           scoreLabel.setFont(scoreFont);
           scoreLabel.setAlignmentX(CENTER_ALIGNMENT);
           scoreLabel.setForeground(dropOfRainbow(i));
           add(scoreLabel);
       }

       JLabel back=new JLabel("PRESS ENTER TO GO BACK");
       back.setAlignmentX(CENTER_ALIGNMENT);
       back.setForeground(Color.WHITE);
       back.setFont(scoreFont);
       back.setFont(scoreFont.deriveFont(25f));
       back.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
       bindEnterKey();
       add(back);
   }

   private void loadFonts() {
       try {
           titleFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/font3.ttf")).deriveFont(40f);
           scoreFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/assets/font2.ttf")).deriveFont(30f);
           GraphicsEnvironment g= GraphicsEnvironment.getLocalGraphicsEnvironment();
           g.registerFont(titleFont);
           g.registerFont(scoreFont);
       } catch (Exception e) {
           e.printStackTrace();
           titleFont = new Font("SansSerif", Font.BOLD, 40);
           scoreFont = new Font("SansSerif", Font.PLAIN, 30);
       }
   }

   private void getScores() {
       scores = new ArrayList<>();
       try (Scanner scanner = new Scanner(new File("src/HighScores.txt"))) {
           while (scanner.hasNextInt()) {
               scores.add(scanner.nextInt());
           }
       } catch (IOException e) {
           System.out.println("erorr while uploading high scores file");
           //FIXME send back to main menu maybe?
       }
       scores.sort(Comparator.reverseOrder());
       if (scores.size() > 10) {
           scores = scores.subList(0, 10);
       }
   }

   private Color dropOfRainbow(int rank) {
       //yes i spend way tooo much time here
       Color orange = new Color(255, 89, 0);
       Color pink = new Color(255, 98, 244);
       Color green = new Color(57, 255, 20);
       Color blue=new Color(4, 55, 242);
       Color red=new Color(255, 49, 49);

       switch (rank % 5) {
           case 0: return red;
           case 1: return pink;
           case 2: return blue;
           case 3: return orange;
           case 4: return green;
           default: return Color.LIGHT_GRAY;
       }
   }

    private void bindEnterKey() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "goBack");
        getActionMap().put("goBack", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("enter pressed"); //here for debugging
                SwingUtilities.invokeLater(WelcomeScreen::new);
                //maybe dispose after?? idk
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man High Scores");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 500);
            frame.setContentPane(new HighScores());
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}



/*
        Color orange = new Color(255, 89, 0);
        Color pink = new Color(255, 98, 244);
        Color green = new Color(57, 255, 20);
        Color blue=new Color(4, 55, 242);
        Color red=new Color(255, 49, 49);
 */ //Colors
