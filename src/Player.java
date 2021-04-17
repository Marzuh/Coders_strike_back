import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    //for input reading
    private boolean boost = true;
    private int x = 0;
    private int y = 0;
    private int nextCheckpointX = 0; // x position of the next check point
    private int nextCheckpointY = 0; // y position of the next check point
    private int nextCheckpointDist = 0; // distance to the next checkpoint
    private int nextCheckpointAngle = 0; // angle between your pod orientation and the direction of the next checkpoint
    private int opponentX = 0;
    private int opponentY = 0;

    //for checkpoints

    private boolean firstLap = true;
    private static int checkPointIdForArray = 0;
    private ArrayList<Player.Checkpoint> checkpointsArray = new ArrayList<>();
    private int prevTargetX = 0;
    private int prevTargetY = 0;
    private int checkpointIdForBoost = -1;


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
     *
     * @param p player
     */
    private void addCheckpointToArrayList(Player p) {
        if (firstLap && p.nextCheckpointX != p.prevTargetX && p.nextCheckpointY != p.prevTargetY) {
            Player.Checkpoint cp = new Checkpoint(p.nextCheckpointX, p.nextCheckpointY, p.checkPointIdForArray);

            if (p.checkpointsArray.isEmpty() || (p.checkpointsArray.get(0).getX() != p.nextCheckpointX
                    && p.checkpointsArray.get(0).getY() != p.nextCheckpointY)) {
                p.checkpointsArray.add(cp);
                System.err.println("Checkpoint added, id: " + cp.getId());
                checkPointIdForArray++;
            } else {
                p.firstLap = false;
            }
        }
    }

    /**
     * @param p player
     * @return Id of next checkpoint with longest distance to it
     */
    private void findFarthestCheckpointIndex(Player p) {
        int index = 0;
        if (!p.firstLap) {
            double longestDistance = 0;
            double distance = 0;
            for (int i = 0; i < p.checkpointsArray.size(); i++) {
                if (i == 0) {
                    distance = Math.sqrt(Math.pow(((double) p.checkpointsArray.get(i).getX()
                            - p.checkpointsArray.get(p.checkpointsArray.size() - 1).getX()), 2)
                            + Math.pow(((double) p.checkpointsArray.get(i).getX()
                            - p.checkpointsArray.get(p.checkpointsArray.size() - 1).getX()), 2));
                } else {
                    distance = Math.sqrt(Math.pow(((double) p.checkpointsArray.get(i).getX()
                            - p.checkpointsArray.get(i - 1).getX()), 2)
                            + Math.pow(((double) p.checkpointsArray.get(i).getX()
                            - p.checkpointsArray.get(i - 1).getX()), 2));
                }
                if (distance > longestDistance) {
                    longestDistance = distance;
                    index = i;
                }
            }
        }
        p.checkpointIdForBoost = index;
    }

    public void CheckpointsDebug(Player p) {
        System.err.println("First lap " + firstLap);
        System.err.println("next check point x " + p.nextCheckpointX);
        System.err.println("next check point y " + p.nextCheckpointY);
        System.err.println("Points in list " + p.checkpointsArray.size());
        System.err.println("Checkpoint id for boost " + p.checkpointIdForBoost);

    }

    private void boostController(Player p) {
        if (!p.firstLap) {
            System.err.println("Checkpoint ID with longest distance " + p.checkpointIdForBoost);
        }

        if (!p.firstLap && p.checkpointsArray.get(p.checkpointIdForBoost).getX() == p.nextCheckpointX
                && p.checkpointsArray.get(p.checkpointIdForBoost).getY() == p.nextCheckpointY && p.boost
                && p.nextCheckpointAngle == 0) {
            p.boost = false;
            System.err.println("ACTIVATE BOOST");
            System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + "BOOST");
        }
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
            p.CheckpointsDebug(p);
            p.boostController(p);
            // You have to output the target position
            // followed by the power (0 <= thrust <= 100) or "BOOST"
            // i.e.: "x y thrust"

            p.prevTargetX = p.nextCheckpointX;
            p.prevTargetY = p.nextCheckpointY;
            System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + " 80");
        }
    }
}