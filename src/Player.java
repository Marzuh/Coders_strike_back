import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    //for input reading
    private int x = 0;
    private int y = 0;
    private int nextCheckpointX = 0; // x position of the next check point
    private int nextCheckpointY = 0; // y position of the next check point
    private int nextCheckpointDist = 0; // distance to the next checkpoint
    private int nextCheckpointAngle = 0; // angle between your pod orientation and the direction of the next checkpoint
    private int opponentX = 0;
    private int opponentY = 0;

    //for checkpoints
    public boolean firstLap = true;
    private static int checkPointIdForMap = 1;
    public ArrayList<Player.Checkpoint> checkpointsArray = new ArrayList<>();
    private int prevTargetX = 0;
    private int prevTargetY = 0;

    /**
     * new class for holding checkpoints coordinates
     */
    static class Checkpoint {
        private final int x;
        private final int y;
        private final int id;

        public Checkpoint(int x, int y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getId() {
            return id;
        }
    }

    /**
     * Method control if checkpoint in list, and if no add it.
     * @param p player
     */
    private void addCheckpointToArrayList(Player p) {
        if (firstLap && p.nextCheckpointX != p.prevTargetX && p.nextCheckpointY != p.prevTargetY) {
            Player.Checkpoint cp = new Checkpoint(p.nextCheckpointX, p.nextCheckpointY, p.checkPointIdForMap);

            if (p.checkpointsArray.isEmpty() || (p.checkpointsArray.get(0).getX() != p.nextCheckpointX
                    && p.checkpointsArray.get(0).getY() != p.nextCheckpointY)) {
                p.checkpointsArray.add(cp);
                System.err.println("Checkpoint added, id: " + cp.getId());
                checkPointIdForMap++;
            } else {
                p.firstLap = false;
            }
        }
    }

    public void CheckpointsDebug(){
        System.err.println("First lap "+firstLap);

    }


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        Player p = new Player();
        // game loop
        while (true) {
            p.x = in.nextInt();
            p.y = in.nextInt();
            p.nextCheckpointX = in.nextInt(); // x position of the next check point
            p.nextCheckpointY = in.nextInt(); // y position of the next check point
            p.nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            p.nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            p.opponentX = in.nextInt();
            p.opponentY = in.nextInt();

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            p.addCheckpointToArrayList(p);
            p.CheckpointsDebug();
            // You have to output the target position
            // followed by the power (0 <= thrust <= 100) or "BOOST"
            // i.e.: "x y thrust"
            System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + " 80");
        }
    }
}