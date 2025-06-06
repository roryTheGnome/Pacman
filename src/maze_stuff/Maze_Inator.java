package maze_stuff;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Maze_Inator {

    public static int score;
    private final int size;//i cant remember the word for horizontal size
    private final int height;
    public static int lives;
    private long prevDeath=0;

    public boolean shileded=false;
    public long shieldEndTime=0;
    public boolean doubleScored=false;
    public long doubleScoreEndTime=0;
    public long freezeEndTime=0;

    private final CellType[][] maze;
    private GameObject[][] gameObjects;
    private final List<Point> ghostPositions = new ArrayList<>();

    private int pacX=1;
    private int pacY=1;

    public boolean isPacmanDead=false;
    public boolean gameOver=false;

    private final Random rando=new Random();


    public Maze_Inator(int row, int colmns) {

        score=0;
        lives=3;

        this.size=row%2==0?row-1: row;
        this.height=colmns%2==0?colmns-1: colmns;

        this.maze=new CellType[height][size];
        this.gameObjects = new GameObject[height][size];

        for(int i=0;i<height;i++){
            Arrays.fill(maze[i],CellType.WALL);
            Arrays.fill(gameObjects[i], GameObject.NONE);
        }

        createMaze(1,1);//0,0 is the wall etc
        removeDeadEnds();

        placeObjects();
        spawnGhosts();

    }

    public void createMaze(int x, int y) {

        maze[y][x]=CellType.PATH;
//https://www.baeldung.com/java-arrays-aslist-vs-new-arraylist
        List<int[]> directs=Arrays.asList(
                new int[]{0,-2}, // ^
                new int[]{0,2},  // V
                new int[]{2,0},  // >
                new int[]{-2,0}  // <
        );
        Collections.shuffle(directs);

        for(int[] d:directs){
            int ix=x+d[0];
            int iy=y+d[1];

            if(isInFrame(ix,iy)&&maze[iy][ix]==CellType.WALL){
                maze[y+d[1]/2][x+d[0]/2]=CellType.PATH;
                createMaze(ix,iy);
            }
        }//FIXME

    }

    public boolean isInFrame(int x, int y) {return  x>0 && y>0 && x<size && y<height;}

    public CellType[][] getMaze(){return maze;}

    private void removeDeadEnds() {
        boolean changed;

        do {
            changed = false;

            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < size - 1; x++) {
                    if (maze[y][x] != CellType.PATH) continue;

                    int open = 0;
                    if (maze[y - 1][x] == CellType.PATH) open++;
                    if (maze[y + 1][x] == CellType.PATH) open++;
                    if (maze[y][x - 1] == CellType.PATH) open++;
                    if (maze[y][x + 1] == CellType.PATH) open++;

                    if (open == 1) {
                        List<int[]> directions = new ArrayList<>(List.of(
                                new int[]{0, -1}, new int[]{0, 1},
                                new int[]{-1, 0}, new int[]{1, 0}
                        ));
                        Collections.shuffle(directions);

                        for (int[] d : directions) {
                            int nx = x + d[0];
                            int ny = y + d[1];

                            if (maze[ny][nx] == CellType.WALL) {
                                maze[ny][nx] = CellType.PATH;
                                changed = true;
                                break;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    private void placeObjects(){

        for(int y=0;y<height;y++){

            for(int x=0;x<size;x++){

                if (maze[y][x] == CellType.PATH) {
                    if (x == 1 && y == 1) {
                        gameObjects[y][x] = GameObject.PACMAN;
                    }else {
                        gameObjects[y][x] = GameObject.DOT;
                    }
                }}
        }
    }

    private void spawnGhosts(){

        int ghostCounter=4;
        int ghostSoFar=0;

        while(ghostSoFar<ghostCounter){

            int x=rando.nextInt(size);
            int y=rando.nextInt(height);

            if(maze[y][x]==CellType.PATH && gameObjects[y][x]!=GameObject.PACMAN){
                //gameObjects[y][x] = GameObject.BLINKY;
                if (ghostSoFar == 0) gameObjects[y][x] = GameObject.BLINKY;
                else if (ghostSoFar == 1) gameObjects[y][x] = GameObject.CLYDE;
                else if (ghostSoFar == 2) gameObjects[y][x] = GameObject.INKY;
                else if (ghostSoFar == 3) gameObjects[y][x] = GameObject.PINKY;
                ghostPositions.add(new Point(x, y));
                ghostSoFar++;
            }
        }




    }

    public void moveGhosts() {
        List<Point> newPositions = new ArrayList<>();
        if (System.currentTimeMillis() < freezeEndTime) return;

        for (Point ghost : ghostPositions) {
            int x = ghost.x;
            int y = ghost.y;

            List<Point> options = new ArrayList<>();
            Point pacman = null;

            if (isMovable(x + 1, y)) {
                Point next = new Point(x + 1, y);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) pacman = next;
                options.add(next);
            }
            if (isMovable(x - 1, y)) {
                Point next = new Point(x - 1, y);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) pacman = next;
                options.add(next);
            }
            if (isMovable(x, y + 1)) {
                Point next = new Point(x, y + 1);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) pacman = next;
                options.add(next);
            }
            if (isMovable(x, y - 1)) {
                Point next = new Point(x, y - 1);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) pacman = next;
                options.add(next);
            }

            Point next;
            if (pacman != null) {
                next = pacman;
            } else if (!options.isEmpty()) {
                next = options.get(rando.nextInt(options.size()));
            } else {
                newPositions.add(new Point(x, y)); // stay in place
                continue;
            }

            GameObject ghostType = gameObjects[y][x];
            gameObjects[y][x] = GameObject.DOT;

            boolean pacmanKilled = false;

            if (gameObjects[next.y][next.x] == GameObject.PACMAN && !recentltDied() && !shileded) {
                lives--;
                prevDeath = System.currentTimeMillis();
                System.out.println("minus 1 life");
                isPacmanDead = true;
                pacmanKilled = true;

                int gx = next.x;
                int gy = next.y;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gameObjects[gy][gx] = ghostType;
                    }
                }, 500);

                if (lives <= 0) {
                    System.out.println("GAME OVER\nSCORE: " + score);
                    gameOver = true;
                }
            }

            if (!pacmanKilled) {
                switch (ghostType) {
                    case BLINKY -> gameObjects[next.y][next.x] = GameObject.BLINKY;
                    case INKY   -> gameObjects[next.y][next.x] = GameObject.INKY;
                    case PINKY  -> gameObjects[next.y][next.x] = GameObject.PINKY;
                    case CLYDE  -> gameObjects[next.y][next.x] = GameObject.CLYDE;
                }
            }

            newPositions.add(next);
        }

        if (rando.nextInt(100) < 25) { //technicly this should give 1/4 change
            int ux = rando.nextInt(size);
            int uy = rando.nextInt(height);
            if (maze[uy][ux] == CellType.PATH && gameObjects[uy][ux] == GameObject.DOT) {
                GameObject upgrade = switch (rando.nextInt(5)) {
                    case 0 -> GameObject.UPGRADE_MULTIPLIER;
                    case 1 -> GameObject.UPGRADE_LIFE;
                    case 2 -> GameObject.UPGRADE_SHIELD;
                    case 3 -> GameObject.UPGRADE_FREEZE;
                    case 4 -> GameObject.UPGRADE_SCORE;
                    default -> GameObject.UPGRADE_SHIELD;
                };
                gameObjects[uy][ux] = upgrade;
            }
        }

        ghostPositions.clear();
        ghostPositions.addAll(newPositions);
    }


    private boolean isMovable(int x, int y) {
        return x >= 0 && y >= 0 && x < size && y < height &&
                maze[y][x] == CellType.PATH &&
                gameObjects[y][x] != GameObject.BLINKY; //yes im proud to write this in one line
    }

    public GameObject[][] getGameObjects() {return gameObjects;}

    public int getPacX(){return pacX;}
    public int getPacY(){return pacY;}

    public void movePackman(int dx, int dy) {
        int newX = pacX + dx;
        int newY = pacY + dy;

        if (newX < 0 || newY < 0 || newX >= size || newY >= height) return;
        if (maze[newY][newX] != CellType.PATH) return;

        GameObject target = gameObjects[newY][newX];

        if ((target == GameObject.BLINKY || target == GameObject.INKY || target == GameObject.PINKY || target == GameObject.CLYDE) && !recentltDied()) {
            if (!shileded) {
                lives--;
                prevDeath = System.currentTimeMillis();
                System.out.println("minus 1 life");

                gameObjects[pacY][pacX] = GameObject.NONE;
                gameObjects[newY][newX] = GameObject.DEADMAN;

                GameObject ghost = target;
                int gx = newX;
                int gy = newY;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gameObjects[gy][gx] = ghost;
                    }
                }, 500);

                pacX = newX;
                pacY = newY;

                isPacmanDead = true;

                if (lives <= 0) {
                    System.out.println("GAME OVER\nSCORE: " + score);
                    gameOver = true;
                }

                return;
            } else {
                gameObjects[pacY][pacX] = GameObject.NONE;
                pacX = newX;
                pacY = newY;
                gameObjects[pacY][pacX] = GameObject.PACMAN;
                return;
            }
        }

        if (!gameOver) {
            switch (target) {
                case DOT -> {
                    score += doubleScored ? 2 : 1;
                    System.out.println("score is:" + score);
                }
                case UPGRADE_LIFE -> {
                    lives++;
                    System.out.println("upgrade life");
                }
                case UPGRADE_SCORE -> {
                    score += 20;
                    System.out.println("upgrade score");
                }
                case UPGRADE_FREEZE -> {
                    freezeEndTime = System.currentTimeMillis() + 5000;
                    System.out.println("upgrade freeze");
                }
                case UPGRADE_SHIELD -> {
                    shileded = true;
                    shieldEndTime = System.currentTimeMillis() + 5000;
                    System.out.println("upgrade shield");
                }
                case UPGRADE_MULTIPLIER -> {
                    doubleScored = true;
                    doubleScoreEndTime = System.currentTimeMillis() + 10000;
                    System.out.println("upgrade multiplier");
                }
            }
        }

        gameObjects[pacY][pacX] = GameObject.NONE;
        pacX = newX;
        pacY = newY;
        gameObjects[pacY][pacX] = GameObject.PACMAN;
    }


    public boolean isGameOver(){return gameOver;}

    public boolean recentltDied(){return System.currentTimeMillis()-prevDeath<500;}

}
