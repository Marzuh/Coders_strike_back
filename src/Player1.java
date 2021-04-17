
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/

class Player1 {
    // Steering Behaviors
    // https://gamedevelopment.tutsplus.com/series/understanding-steering-behaviors--gamedev-12732
    // 1. Seek
    // 2. Boost
    // 3. Slowing Down ( straight only )

    private static final int MAX_THRUST = 100;
    private static final int CHECK_POINT_RADIUS = 600;

    private int x = 0;
    private int y = 0;
    private int prevX = 0;
    private int prevY = 0;
    private int prevTargetX = 1;
    private int prevTargetY = 1;
    private int nextCheckpointX = 0; // x position of the next check point
    private int nextCheckpointY = 0; // y position of the next check point
    private int nextCheckpointDist = 0; // distance to the next checkpoint
    private int nextCheckpointAngle = 0; // angle between your pod orientation and the direction of the next checkpoint
    private int opponentX = 0;
    private int opponentY = 0;
    private double accelerationCoefficientFromDist = 0;
    private double accelerationCoefficientFromAngle = 0;
    private boolean boost = true;
    private boolean firstLap = true;
    private int thrust = 100;
    private static int checkPointIdForMap = 1;
    private ArrayList<Checkpoint> checkpointsArray = new ArrayList<>();
    private Vector currentDirection = new Vector();
    private Vector steeringDirection= new Vector();
    private Vector desiredDirection= new Vector();


    private static double findAccelerationCoefficientFromDist(int distance) {
        double result = (distance / (double) (4 * CHECK_POINT_RADIUS));
        if (result > 1) {
            result = 1;
        }
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    private static double findAccelerationCoefficientFromAngle(int angle) {
        return Math.cos(Math.toRadians(angle));
    }

    private void errLog(Player1 p) {
        System.err.println("Coordinates x: " + p.x + "\ty: " + p.y);
        System.err.println("First lap: " + p.firstLap);
        System.err.println("Thrust: " + p.thrust);
        System.err.println("Angle " + p.nextCheckpointAngle);
        System.err.println("Distance " + p.nextCheckpointDist);
        System.err.println("accCoefDist " + p.accelerationCoefficientFromDist);
        System.err.println("accCoefAngl " + p.accelerationCoefficientFromAngle);
    }

    private void errVectorLog(Player1 p){
        System.err.println("Next checkpoint x: " + p.nextCheckpointY + " y: "+p.nextCheckpointY);
        System.err.println("Player x: " + p.x+" y: "+p.y);

    }


    private void addCheckpointToArrayList(Player1 p) {
        if (firstLap && p.nextCheckpointX != p.prevTargetX && p.nextCheckpointY != p.prevTargetY) {
            Checkpoint cp = new Checkpoint(p.nextCheckpointX, p.nextCheckpointY, p.checkPointIdForMap);

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


        public void  debug(){
            System.err.println("Vector x: "+this.x+" y: " +this.y);
        }

    }

    Vector normalize(Vector vector) {
        vector.setX(vector.x / (int) vector.getLength());
        vector.setY(vector.y / (int) vector.getLength());
        return vector;
    }

    Vector vectorsSubtraction(Vector v1, Vector v2) {
        Vector resultVector = new Vector();
        resultVector.setX(v1.getX() - v2.getX());
        resultVector.setY(v1.getY() - v2.getY());
        return resultVector;
    }


    class Checkpoint {
        private int x;
        private int y;
        private int id;

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


    private int findFarthestCheckpointIndex(Player1 p) {
        int index = 0;
        if (!p.firstLap) {
            double longestDistance = 0;
            double distance = 0;
            for (int i = 0; i < p.checkpointsArray.size(); i++) {
                if (i == 0) {
                    distance = Math.sqrt(Math.pow((double) p.checkpointsArray.get(i).getX() - p.checkpointsArray.get(p.checkpointsArray.size() - 1).getX(), 2)
                            + Math.pow((double) p.checkpointsArray.get(i).getX() - p.checkpointsArray.get(p.checkpointsArray.size() - 1).getX(), 2));
                } else {
                    distance = Math.sqrt(Math.pow((double) p.checkpointsArray.get(i).getX() - p.checkpointsArray.get(i - 1).getX(), 2)
                            + Math.pow((double) p.checkpointsArray.get(i).getX() - p.checkpointsArray.get(i - 1).getX(), 2));
                }
                if (distance > longestDistance) {
                    longestDistance = distance;
                    index = i;
                }
            }
        }
        return index;
    }

    private void boostController(Player1 p) {
        if (!p.firstLap) {
            int index = p.findFarthestCheckpointIndex(p);
            if (p.boost && p.nextCheckpointX == p.checkpointsArray.get(index).getX() && p.nextCheckpointY == p.checkpointsArray.get(index).getY()
                    && p.nextCheckpointAngle < 1) {
                System.err.println("boost pre start");
                p.boost = false;
                System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + " BOOST");
            }
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int alpha = 1; //angle less than alpha will give max speed and try boost
        boolean firstStep = true;

        Player1 p = new Player1();


        // game loop
        while (true) {
            //new input reading
            p.x = in.nextInt();
            p.y = in.nextInt();
            p.nextCheckpointX = in.nextInt(); // x position of the next check point
            p.nextCheckpointY = in.nextInt(); // y position of the next check point
            p.nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            p.nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            p.opponentX = in.nextInt();
            p.opponentY = in.nextInt();

            //coefficients calculation
            p.accelerationCoefficientFromDist = findAccelerationCoefficientFromDist(p.nextCheckpointDist);
            p.accelerationCoefficientFromAngle = findAccelerationCoefficientFromAngle(p.nextCheckpointAngle);

            p.errVectorLog(p);

            //vectors
            if(p.nextCheckpointX!=0 && p.x!=0){
                p.desiredDirection.setX(p.nextCheckpointX - p.x);
                p.desiredDirection.setY(p.nextCheckpointY - p.y);
                p.desiredDirection = p.normalize(p.desiredDirection);
                p.currentDirection.setX(p.x - p.prevX);
                p.currentDirection.setY(p.y - p.prevY);
                p.currentDirection = p.normalize(p.currentDirection);
                p.steeringDirection = p.vectorsSubtraction(p.desiredDirection, p.currentDirection);
                p.steeringDirection = p.normalize(p.steeringDirection);

                p.nextCheckpointX += p.steeringDirection.getX() * 100;
                p.nextCheckpointY += p.steeringDirection.getY() * 100;
            }

            p.addCheckpointToArrayList(p);

            p.boostController(p);

            if (p.nextCheckpointAngle > 90 || p.nextCheckpointAngle < -90) {
                p.thrust = 0;
            } else {
                if (p.nextCheckpointAngle < alpha) {
                    //1-nextCheckpointAngle/90 - bigger angle, lower acceleration
                    p.thrust = (int) Math.round(100 * p.accelerationCoefficientFromAngle * p.accelerationCoefficientFromDist);
                }
            }
            if (p.nextCheckpointDist < 2400) {
                p.thrust *= p.nextCheckpointDist / 2400;
            }

            // if(nextCheckpointAngle>2){
            //     nextCheckpointX= (int) Math.round(x+ nextCheckpointDist*Math.cos(nextCheckpointAngle));
            //     nextCheckpointY=(int) Math.round(y+ nextCheckpointDist*Math.sin(nextCheckpointAngle));
            // }

            p.errLog(p);

            p.prevTargetX = p.nextCheckpointX;
            p.prevTargetY = p.nextCheckpointY;
            p.prevX = p.x;
            p.prevY = p.y;
            System.out.println(p.nextCheckpointX + " " + p.nextCheckpointY + " " + p.thrust);
        }
    }
}

