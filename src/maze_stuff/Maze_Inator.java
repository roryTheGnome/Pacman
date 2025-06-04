package maze_stuff;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Maze_Inator {

    public static int score;
    private final int size;//i cant remember the word for horizontal size
    private final int height;
    public static int lives;

    private final CellType[][] maze;
    private GameObject[][] gameObjects;
    private final List<Point> ghostPositions = new ArrayList<>();

    private int pacX=1;
    private int pacY=1;

    public boolean isPacmanDead=false;

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

    public boolean isInFrame(int x, int y) {
        return  x>0 && y>0 && x<size && y<height;
    }

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

                    if (open == 1) { // dead end
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

        int ghostCounter=4;//cause i found 4 photos
        int ghostSoFar=0;

        while(ghostSoFar<ghostCounter){

            int x=rando.nextInt(size);
            int y=rando.nextInt(height);

            if(maze[y][x]==CellType.PATH && gameObjects[y][x]!=GameObject.PACMAN){
                gameObjects[y][x] = GameObject.BLINKY;
                ghostPositions.add(new Point(x, y));
                ghostSoFar++;
            }
        }
    }

    public void moveGhosts() {
        List<Point> newPositions = new ArrayList<>();

        for (Point ghost : ghostPositions) {
            int x = ghost.x;
            int y = ghost.y;

            List<Point> options = new ArrayList<>();
            Point pacman=null;

            if (isMovable(x + 1, y)){
                Point next = new Point(x + 1, y);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) {
                    pacman = next;
                }
                options.add(next);
            }
            if (isMovable(x - 1, y)) {
                Point next = new Point(x -1, y);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) {
                    pacman = next;
                }
                options.add(next);
            }
            if (isMovable(x, y + 1)) {
                Point next = new Point(x, y+1);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) {
                    pacman = next;
                }
                options.add(next);
            }
            if (isMovable(x, y - 1)) {
                Point next = new Point(x, y-1);
                if (gameObjects[next.y][next.x] == GameObject.PACMAN) {
                    pacman = next;
                }
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

            gameObjects[y][x] = GameObject.DOT;

            if (gameObjects[next.y][next.x] == GameObject.PACMAN) {
                lives--;
                System.out.println("minus 1 life");//keep for debugging
                gameObjects[next.y][next.x] = GameObject.DEADMAN;
                isPacmanDead=true;

                if (lives <= 0) {
                    System.out.println("GAME OVER\nSCORE: " + score);
                    //TODO add here
                }else{

                }

            }

            gameObjects[next.y][next.x] = GameObject.BLINKY;
            newPositions.add(next);
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

    public void movePackman(int x, int y){
        int newX=pacX+x;
        int newY=pacY+y;

        if (newX<0 || newY<0 || newX>=size || newY>=height) {
            return;
        }
        if(maze[newY][newX]!=CellType.PATH){
            return;
        }

        if (gameObjects[newY][newX] == GameObject.DOT) {
            gameObjects[newY][newX] = GameObject.NONE;
            score++;
            System.out.println("score is:"+score);  //not needed, used for debugging
        }

        gameObjects[pacY][pacX] = GameObject.NONE;
        pacX=newX;
        pacY=newY;
        gameObjects[pacY][pacX] = GameObject.PACMAN;

    }

}
