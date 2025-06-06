package WelcomePage_Stuff;

import java.io.Serializable;

public class ScoreEntry implements Serializable {

    public static final long serialVersionUID = 1L;

    int score;
    String nickname;

    public ScoreEntry(int score, String nickname) {
        this.score = score;
        this.nickname = nickname;
    }
}