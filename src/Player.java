import java.util.ArrayList;
import java.util.Scanner;


/**
 * Steering Behaviors
 * https://gamedevelopment.tutsplus.com/series/understanding-steering-behaviors--gamedev-12732
 * 1. Seek
 * 2. Boost
 * 3. Slowing Down
 */
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

    //for checkpoints and boost
    private boolean boostAvailable = true;
    private boolean firstLap = true;
    private static int checkPointIdForArray = 0;
    private final ArrayList<Player.Checkpoint> checkpointsArray = new ArrayList<>();
    private int prevTargetX = 0;
    private int prevTargetY = 0;
    private int checkpointIdForBoost = -1;

    //steering and acceleration
    private int thrustInt = 100;
    private String thrustStr = "";
    public static final int SLOWING_RADIUS = 4 * 600;//4 times checkpoint radius
    private double accelerationCoefficientFromDist = 0;
    private double accelerationCoefficientFromAngle = 0;
    private int prevX = 0;
    private int prevY = 0;
    private Vector currentDirection = new Vector();
    private Vector steeringDirection = new Vector();
    private Vector desiredDirection = new Vector();


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
            Player.Checkpoint cp = new Checkpoint(p.nextCheckpointX, p.nextCheckpointY, Player.checkPointIdForArray);

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

    /**
     * Method contains console outputs with debug info about checkpoints
     *
     * @param p player
     */
    public void CheckpointsDebug(Player p) {
        System.err.println("First lap " + p.firstLap);
        System.err.println("Boost avaliable " + p.boostAvailable);
        System.err.println("next check point x " + p.nextCheckpointX);
        System.err.println("next check point y " + p.nextCheckpointY);
        System.err.println("Points in list " + p.checkpointsArray.size());
        System.err.println("Checkpoint id for boost " + p.checkpointIdForBoost);

    }

    /**
     * Activate boost if it is not first lap, ship moving to farthest checkpoint and angle to target is 0;
     *
     * @param p player
     */
    private void boostController(Player p) {
        if (!p.firstLap && p.checkpointsArray.get(p.checkpointIdForBoost).getX() == p.nextCheckpointX
                && p.checkpointsArray.get(p.checkpointIdForBoost).getY() == p.nextCheckpointY && p.boostAvailable
                && p.nextCheckpointAngle == 0) {
            p.boostAvailable = false;
            p.thrustStr = "BOOST";
        }
    }

    class Vector {
        private int x;
        private int y;


        public double getLength() {
            return Math.sqrt(this.x * this.x + this.y + this.y);
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }


        public void debug() {
            System.err.println("Vector x: " + this.x + " y: " + this.y);
        }

    }

    public Vector normalize(Vector vector) {
        vector.setX(vector.x / (int) vector.getLength());
        vector.setY(vector.y / (int) vector.getLength());
        return vector;
    }

    public Vector vectorsSubtraction(Vector v1, Vector v2) {
        Vector resultVector = new Vector();
        resultVector.setX(v1.getX() - v2.getX());
        resultVector.setY(v1.getY() - v2.getY());
        return resultVector;
    }

    public void vectorDebug(Player p){
        System.err.println("Desired x" +p.desiredDirection.getX());
        System.err.println("Desired y" +p.desiredDirection.getY());
        System.err.println("Current x" +p.currentDirection.getX());
        System.err.println("Current y" +p.currentDirection.getY());
        System.err.println("Steering x" +p.steeringDirection.getX());
        System.err.println("Steering  y" +p.steeringDirection.getY());

    }
    /**
     * Set acceleration coefficient depends on distance between 0 and 1.
     *
     * @param p player
     */
    private void setAccelerationCoefficientFromDist(Player p) {
        double result = (p.nextCheckpointDist / (double) (SLOWING_RADIUS));
        if (result > 1) {
            result = 1;
        }
        if (result < 0.05) {
            result = 0.05;
        }
        p.accelerationCoefficientFromDist = result;
    }


    /**
     * Set acceleration coefficient depends on angle between 0 and 1.
     *
     * @param p player
     */
    private void setAccelerationCoefficientFromAngle(Player p) {
        p.accelerationCoefficientFromAngle = Math.cos(Math.toRadians(p.nextCheckpointAngle));
    }

    /**
     * Method contains console outputs with debug info about ship moving
     *
     * @param p player
     */
    private void errLog(Player p) {
        System.err.println("Coordinates x: " + p.x + "\ty: " + p.y);
        System.err.println("First lap: " + p.firstLap);
        System.err.println("Boost available " + p.boostAvailable);
        System.err.println("Thrust: " + p.thrustInt);
        System.err.println("Angle " + p.nextCheckpointAngle);
        System.err.println("Distance " + p.nextCheckpointDist);
        System.err.println("accCoefDist " + p.accelerationCoefficientFromDist);
        System.err.println("accCoefAngl " + p.accelerationCoefficientFromAngle);
    }

    /**
     * Set thrust and give control console output
     *
     * @param p player
     */
    private void setControlCommand(Player p) {
        p.thrustInt = (int) Math.round(100 * p.accelerationCoefficientFromDist
                * p.accelerationCoefficientFromAngle);
        p.thrustStr = Integer.toString(p.thrustInt);
        p.prevTargetX = p.nextCheckpointX;
        p.prevTargetY = p.nextCheckpointY;
        p.prevX = p.x;
        p.prevY = p.y;
        p.errLog(p);
        System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + " " + p.thrustStr);
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


            p.addCheckpointToArrayList(p);
            p.findFarthestCheckpointIndex(p);

            if (p.nextCheckpointAngle != 0) {

                //1)steering vector
                p.desiredDirection.setX(p.nextCheckpointX - p.x);
                p.desiredDirection.setY(p.nextCheckpointY - p.y);
                //p.desiredDirection = p.normalize(p.desiredDirection);
                p.currentDirection.setX(p.x - p.prevX);
                p.currentDirection.setY(p.y - p.prevY);
                //p.currentDirection = p.normalize(p.currentDirection);
                p.steeringDirection = p.vectorsSubtraction(p.desiredDirection, p.currentDirection);
                //p.steeringDirection = p.normalize(p.steeringDirection);

                p.nextCheckpointX += p.steeringDirection.getX() * 100;
                p.nextCheckpointY += p.steeringDirection.getY() * 100;

                //3)Slowing down
                if (p.nextCheckpointAngle >= 90 || p.nextCheckpointAngle <= -90) {
                    p.thrustInt = 0;
                }
                if (Math.abs(p.nextCheckpointAngle) < 90) {
                    p.setAccelerationCoefficientFromAngle(p);
                }
                p.setAccelerationCoefficientFromDist(p);
                p.setControlCommand(p);

            } else {
                //not steering
                //2)check boost
                if (p.boostAvailable) {
                    p.boostController(p);
                }
                p.setAccelerationCoefficientFromDist(p);
                p.setAccelerationCoefficientFromAngle(p);
                p.setControlCommand(p);
            }


            ;
        }
    }
}