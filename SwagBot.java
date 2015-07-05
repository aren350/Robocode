package kt_tc;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;


/**
 * A modular bot adhering to the RoboPart Interface. Has abilities to move in a
 * star formation, randomly strafe, ram, predictively shoot, and have constant
 * knowledge of all enemies on the field.
 * 
 * @author Kushal Tirumala, Tiffany Chiang
 * @version 5/30/2014
 * 
 * @author Period - 6
 * @author Assignment - PartsBot
 * 
 * @author Sources - Kushal Tirumala, Tiffany Chiang
 */
public class SwagBot extends AdvancedRobot
{
    /**
     * Keeps track if whether the robot has already ventured to a corner to make
     * its own
     */
    boolean alreadyInACorner = false;

    /**
     * ArrayList to keep an updated list of all enemies on the battle field
     */
    private ArrayList<AdvancedEnemyBot> enemies = new ArrayList<AdvancedEnemyBot>();

    /**
     * enemy robot
     */
    public AdvancedEnemyBot enemy = new AdvancedEnemyBot();

    /**
     * Array to hold robot parts
     */
    public RobotPart[] parts = new RobotPart[3]; // make three parts

    /**
     * integer to represent radar
     */
    public final static int RADAR = 0;

    /**
     * integer to represent the gun
     */
    public final static int GUN = 1;

    /**
     * integer to represent tank/movement
     */
    public final static int TANK = 2;


    /**
     * The main run program of the robot Switches between melee mode and one vs.
     * one mode at the start match.
     */
    public void run()
    {
        if ( getOthers() == 1 )
        {
            parts[RADAR] = new Radar();
            parts[GUN] = new Gun();
            parts[TANK] = new Tank();
        }
        else
        {
            parts[RADAR] = new MeleeRadar();
            parts[GUN] = new MeleeGun();
            parts[TANK] = new MeleeTank();
        }

        // initialize each part
        for ( int i = 0; i < parts.length; i++ )
        {
            // behold, the magic of polymorphism
            parts[i].init();
        }

        // iterate through each part, moving them as we go
        for ( int i = 0; true; i = ( i + 1 ) % parts.length )
        {
            // polymorphism galore!
            parts[i].move();
            if ( i == 0 )
            {
                execute();
            }
        }
    }


    /**
     * On scanned Robot, update enemy information In the case of the melee robot
     * being called, update the arraylist with all the enemies information.
     * 
     * @param e
     *            scannedrobotEvent
     */
    public void onScannedRobot( ScannedRobotEvent e )
    {

        if ( getOthers() == 1 )
        {
            System.out.println( "Theres only one" );

            Radar radar = (Radar)parts[RADAR];
            if ( radar.shouldTrack( e ) )
            {
                enemy.update( e, this );
            }

        }
        else
        {
            MeleeRadar radar = (MeleeRadar)parts[RADAR];
            if ( !inList( e.getName() ) )
            {
                AdvancedEnemyBot newBot = new AdvancedEnemyBot();
                newBot.update( e, this );
                enemies.add( newBot );
                this.setColors( Color.black, Color.black, Color.black );
            }
            else
            {
                radar.updating( e, this );
            }
            radar.reorder();
        }
    }


    /**
     * Similar to the isTracking method. Checks to see if the bot has an entry
     * on the particular robot by name in the ArrayList enemies.
     * 
     * @param name
     *            = name of the robot just scanned
     * @return = true if there the name appears in the list of
     *         AdvancedEnemyBots; false if it does not.
     */
    private boolean inList( String name )
    {
        for ( int j = 0; j < enemies.size(); j++ )
        {
            if ( name.equals( enemies.get( j ).getName() ) )
            {
                return true;
            }
        }
        return false;
    }


    /**
     * One vs One: On robot death, start tracking another robot Melee: Update
     * the death to the arrayList This method is also essential to knowing if
     * our robot made it to the top 2, in which case it switched to the one vs
     * one mode robot.
     * 
     * @param e
     *            RobotDeathEvent
     */
    public void onRobotDeath( RobotDeathEvent e )
    {

        if ( getOthers() == 1 )
        {
            parts[RADAR] = new Radar();
            parts[GUN] = new Gun();
            parts[TANK] = new Tank();
            System.out.println( "Theres only one" );
            Radar radar = (Radar)parts[RADAR];
            if ( radar.wasTracking( e ) )
            {
                enemy.reset();
            }
        }
        else
        {
            String name = e.getName();
            if ( inList( name ) )
            {
                for ( int j = 0; j < enemies.size(); j++ )
                {
                    if ( name.equals( enemies.get( j ).getName() ) )
                    {
                        enemies.remove( j );
                    }
                }
            }
        }
    }


    /**
     * 
     * Finds absolute bearing of enemy robot given coordinartes
     * 
     * @param x1
     *            your x
     * @param y1
     *            your y
     * @param x2
     *            enemy's x
     * @param y2
     *            enemy's y
     * @return the absolute bearing
     */
    public double absoluteBearing( double x1, double y1, double x2, double y2 )
    {
        return Math.toDegrees( Math.atan2( x2 - x1, y2 - y1 ) );
    }


    /**
     * 
     * Find's normalized bearing between robot and eemy
     * 
     * @param angle
     *            the absolute angle
     * @return the normalized bearing
     */
    public double normalizeBearing( double angle )
    {
        while ( angle > 180 )
        {
            angle -= 360;
        }
        while ( angle < -180 )
        {
            angle += 360;
        }
        return angle;
    }


    /**
     * 
     * Interface class for the RobotPart array
     * 
     * @author Kushal Tirumala
     * @version May 16, 2014
     * @author Period: 6
     * @author Assignment: Robo05PartsBot
     * 
     * @author Sources: Kushal Tirumala
     */
    public interface RobotPart
    {
        /**
         * 
         * The initial action of the object
         */
        public void init();


        /**
         * 
         * The real method where the real code that does stuff is stored
         */
        public void move();
    }


    /**
     * 
     * Radar class to control the radar aspect of the robot. Contain's the
     * init(), and move() method from the RobotPart class.
     * 
     * @author Kushal Tirumala
     * @version May 16, 2014
     * @author Period: 6
     * @author Assignment: Robo05PartsBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    public class Radar implements RobotPart
    {
        /**
         * useless code, remove ir mr.fulk allows you too.
         */
        int scanDirection = 1;


        /**
         * Just sets the radar for gun turn
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * Finds, and continuously tracks a robot width-lock
         */
        public void move()
        {

            if ( enemy.none() )
            {
                turnRadarRight( 360 );
            }

            // Absolute angle towards target
            double angleToEnemy = getHeadingRadians()
                + Math.toRadians( enemy.getBearing() );

            // Subtract current radar heading to get the turn required to
            // face
            // the enemy, be sure it is normalized
            double radarTurn = Utils.normalRelativeAngle( angleToEnemy
                - getRadarHeadingRadians() );

            // Distance we want to scan from middle of enemy to either side
            // The 36.0 is how many units from the center of the enemy robot
            // it
            // scans.
            double extraTurn = Math.min( Math.atan( 36.0 / enemy.getDistance() ),
                Rules.RADAR_TURN_RATE_RADIANS );

            // Adjust the radar turn so it goes that much further in the
            // direction it is going to turn
            // Basically if we were going to turn it left, turn it even more
            // left, if right, turn more right.
            // This allows us to overshoot our enemy so that we get a good
            // sweep
            // that will not slip.
            radarTurn += ( radarTurn < 0 ? -extraTurn : extraTurn );

            // Turn the radar
            setTurnRadarRightRadians( radarTurn );
        }


        /**
         * 
         * Check's if the scannd robot is the one we should be tracking
         * 
         * @param e
         *            the scanned robot event
         * @return if or if not the robot is the right robot
         */
        public boolean shouldTrack( ScannedRobotEvent e )
        {
            // track if we have no enemy, the one we found is significantly
            // closer, or we scanned the one we've been tracking.
            return ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 || e.getName()
                .equals( enemy.getName() ) );
        }


        /**
         * 
         * Check's if the robot that died was the one we were tracking
         * 
         * @param e
         *            the robot death event
         * @return if it was the robot we were tracking
         */
        public boolean wasTracking( RobotDeathEvent e )
        {
            return e.getName().equals( enemy.getName() );
        }

    }


    /**
     * 
     * Gun class to control gun aspect of Robot. Has init() and move() methods
     * from Robot part
     * 
     * @author Kushal Tirumala
     * @version May 16, 2014
     * @author Period: 6
     * @author Assignment: Robo05PartsBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    public class Gun implements RobotPart
    {
        /**
         * Just sets the radar for gun turn
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * Fires the bullets where the radar is currently tracking the enemy
         * robot to be
         */
        public void move()
        {

            // don't shoot if I've got no enemy
            if ( enemy.none() )
                return;

            // calculate firepower based on distance
            double firePower = Math.min( 500 / enemy.getDistance(), 3 );
            // calculate speed of bullet
            double bulletSpeed = 20 - firePower * 3;
            // distance = rate * time, solved for time
            long time = (long)( enemy.getDistance() / bulletSpeed );

            // calculate gun turn to predicted x,y location
            double futureX = enemy.getFutureX( time );
            double futureY = enemy.getFutureY( time );

            // non-predictive firing can be done like this:
            // double absDeg = absoluteBearing(getX(), getY(), enemy.getX(),
            // enemy.getY());

            double absDeg = absoluteBearing( getX(), getY(), futureX, futureY );
            // turn the gun to the predicted x,y location
            setTurnGunRight( normalizeBearing( absDeg - getGunHeading() ) );

            // if the gun is cool and we're pointed in the right direction,
            // shoot!

            setFire( firePower );

        }
    }


    /**
     * 
     * Controls Tank/Movement aspect of the Robot. Has init() and move() methods
     * from RobotPart class.
     * 
     * @author Kushal Tirumala
     * @version May 16, 2014
     * @author Period: 6
     * @author Assignment: Robo05PartsBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    private class Tank implements RobotPart
    {
        /**
         * Variable used in the ramming method to keep track of the direction we
         * intend on moving in
         */
        public byte moveDirection = -1;

        /**
         * Random object for random movement
         */
        Random rand = new Random();


        /**
         * Set's the color's to green
         */
        public void init()
        {
            setAllColors( Color.green );
        }


        /**
         * If the robot's position is closer than 300, or if the enemy has way
         * less energy than u, change to the ramming strategy. Otherwise, just
         * randomly strafe and shoot where the enemy is going to be
         */
        public void move()
        {

            if ( enemy.getDistance() < 300
                || ( ( getEnergy() - enemy.getEnergy() ) > 30 ) )
            {
                ram();
            }
            else
            {
                avoidWalls();
            }
        }


        /**
         * Squares up to the enemy with a little turn towards the enemy, and
         * randomly strafes: Random direction, random distance
         */
        public void avoidWalls()
        {
            // decide the random movement direction, 1 or -1;
            int randDirection;

            int random = rand.nextInt( 2 ) + 1;
            if ( random == 1 )
            {
                randDirection = -1;
            }
            else
            {
                randDirection = 1;
            }

            // always square off our enemy, turning slightly toward him
            setTurnRight( normalizeBearing( enemy.getBearing() + 90
                - ( 15 * moveDirection ) ) );

            // if we're at the end of a strafing leg, go ahead in a random
            // direction for a random distance
            if ( getVelocity() == 0 )
            {

                setAhead( ( rand.nextInt( 300 ) + 1 ) * randDirection );

                // 300 = 60% win rate (looks like could use work)

            }
        }


        /**
         * Ram's into the enemy if close to the enemy. Otherwise strafes closer
         * to the enemy, again for a random distance BUT NOT for in a random
         * direction
         */
        public void ram()
        {

            // amount towards the enemy
            double absBearing = Math.toRadians( enemy.getBearing() )
                + Math.toRadians( getHeading() );
            // amount to turn perpendicular to the enemy
            double turn = absBearing + Math.PI / 2;
            // subtracting a little from the turn to have a strafing method
            // mechanism
            // NOTE: the reliance on 1/enemy.getDistance() here is so as the
            // robot gets closer, our turns will be sharper
            turn -= Math.max( 0.5, ( 1 / enemy.getDistance() ) * 100 )
                * moveDirection;
            // turn toward that angle
            setTurnRightRadians( Utils.normalRelativeAngle( turn
                - getHeadingRadians() ) );

            // switch directions if we've stopped also decides whether to go in
            // for the ramming kill
            if ( getVelocity() == 0 )
            {
                if ( enemy.getDistance() < 200 )
                {
                    setMaxVelocity( 8 );

                    spiralAndRam();
                }
                else
                {

                    moveDirection *= -1;
                    setMaxVelocity( 8 );

                    setAhead( ( rand.nextInt( 300 ) + 1 ) * moveDirection );
                }

            }

        }


        /**
         * 
         * Simple ramming code, ram's at where the robot identifies where the
         * enemy robot's position is, and rams there.
         */
        public void spiralAndRam()
        {
            System.out.println( "im ramming" );

            setAhead( enemy.getDistance() * moveDirection );

        }

    }


    /**
     * 
     * A different radar, same concept as the one on one radar, but constantly
     * updates to an ArrayList instead.
     * 
     * @author Tiffany Chiang
     * @version 5/30/2014
     * @author Period: 6
     * @author Assignment: SwagBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    public class MeleeRadar implements RobotPart
    {
        /**
         * Just sets the radar for gun turn
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * Finds, and continuosly tracks a robot (width-lock)
         */
        public void move()
        {
            /**
             * Uses width lock beam to track enemy robot and every 100 ticks,
             * scans the whole arena in search of a better target
             */

            if ( getTime() % 100 == 0 )
            {
                setTurnRadarRight( 360 );
                reorder();
            }
            else if ( enemies.size() > 0 )
            {
                double angleToEnemy = getHeadingRadians()
                    + Math.toRadians( enemies.get( 0 ).getBearing() );
                double radarTurn = Utils.normalRelativeAngle( angleToEnemy
                    - getRadarHeadingRadians() );
                double extraTurn = Math.min( Math.atan( 36.0 / enemies.get( 0 )
                    .getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );
                radarTurn += ( radarTurn < 0 ? -extraTurn : extraTurn );
                setTurnRadarRightRadians( radarTurn );
            }
            else
            {
                setTurnRadarRight( 360 );
                reorder();
            }
        }


        /**
         * Finds the corresponding robot file by searching by name and then
         * updates the information on that particular robot.
         * 
         * @param e
         *            = information of robot that are scanned
         * @param me
         *            = information of our own robot
         */
        private void updating( ScannedRobotEvent e, AdvancedRobot me )
        {
            String name = e.getName();
            for ( int j = 0; j < enemies.size(); j++ )
            {
                if ( name.equals( enemies.get( j ).getName() ) )
                {
                    enemies.get( j ).update( e, me );
                }
            }
        }


        /**
         * Reorders ArrayList in order of most viable to least viable by
         * creating a new ArrayList that will keep entries in order as they are
         * passed in.
         */
        private void reorder()
        {
            if ( enemies.size() > 1 )
            {
                for ( int j = 0; j < enemies.size(); j++ )
                {
                    enemies.get( j ).calcViability();
                }

                ArrayList<AdvancedEnemyBot> hello = new ArrayList<AdvancedEnemyBot>();
                hello.add( enemies.get( 0 ) );
                for ( int j = 1; j < enemies.size(); j++ )
                {
                    int index = getIndex( hello, enemies.get( j ) );// sort?
                    hello.add( index, enemies.get( j ) );
                }
                enemies = hello;
                System.out.println( "I will track "
                    + enemies.get( 0 ).getName() );
            }

        }


        /**
         * Takes robot and compares its viability with rest of AdvancedEnemyBots
         * in hello and finds its predicted index in order of most to least
         * viable.
         * 
         * @param hello
         *            = ArrayList of what is currently done sorted
         * @param robot
         *            = desired AdvancedEnemyBot to be sorted in hello in order
         *            of viability.
         * @return index = where robot would be if it were to be ordered in
         *         viability
         */
        private int getIndex(
            ArrayList<AdvancedEnemyBot> hello,
            AdvancedEnemyBot robot )
        {
            int index = 0;
            while ( index < hello.size()
                && ( hello.get( index ).getViability() < robot.getViability() ) )
            {
                index++;
            }
            return index;

        }

    }


    /**
     * 
     * Predictive shooting adapted to the ArrayList usage
     * 
     * @author Tiffany Chiang
     * @version Jun 1, 2014
     * @author Period: 6
     * @author Assignment: SwagBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    public class MeleeGun implements RobotPart
    {
        /**
         * Sets Radar for Gun turn
         */
        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * Shoots at the currently most viable (or closest) target since the
         * robot last scanned. It won't shoot until it has scanned most of the
         * area, to limit premature shooting as much as possible. Otherwise, it
         * uses predictive shooting.
         */
        public void move()
        {
            if ( enemies.size() > 0 && getRadarTurnRemaining() < 37 )
            {

                System.out.println( "Shooting at " + enemies.get( 0 ).getName() );
                double firePower = Math.min( 500 / enemies.get( 0 )
                    .getDistance(), 3 );
                double bulletSpeed = 20 - firePower * 3;
                long time = (long)( enemies.get( 0 ).getDistance() / bulletSpeed );
                double futX = enemies.get( 0 ).getFutureX( time );
                double futY = enemies.get( 0 ).getFutureY( time );
                double absBearing = absoluteBearing( getX(), getY(), futX, futY );
                setTurnGunRight( normalizeBearing( absBearing - getGunHeading() ) );
                if ( getGunHeat() == 0
                    && Math.abs( getGunTurnRemaining() ) < 10 )
                {
                    setFire( firePower );
                }
            }
        }
    }


    /**
     * 
     * Contains the method to allow a robot to decide which corner is closest,
     * go to that corner, and move in a star formation
     * 
     * @author Kushal Tirumala
     * @version Jun 1, 2014
     * @author Period: 6
     * @author Assignment: SwagBot
     * 
     * @author Sources: Kushal Tirumala, Tiffany Chiang
     */
    public class MeleeTank implements RobotPart
    {

        /**
         * Used for the star movement pattern, serves as variable to hold the
         * amount we move each leg of the star
         */
        double howFarToMoveEachLeg = 180;

        /**
         * Initial movement for the robot to move
         */
        double movement = Double.POSITIVE_INFINITY;

        /**
         * Amount to be changed by the end of each leg; used in star()
         */
        double baseDirection = Math.PI / 2;

        /**
         * 1/4 of the battle field width
         */
        double xfourth = getBattleFieldWidth() / 4;

        /**
         * 1/4 of the battle field height
         */
        double yfourth = getBattleFieldHeight() / 4;


        /**
         * Sets the colors to green.
         */
        public void init()
        {
            setAllColors( Color.green );

        }


        /**
         * Check's to see if the robot has already tried to move to a corner.
         * This is the first thing it does in the beginning of a battle. Once it
         * accomplishes this, it moves in a star pattern.
         */
        public void move()
        {
            if ( !alreadyInACorner )
            {
                decideWhichQuadrantToGoTo();
            }
            star();
        }


        /**
         * 
         * Tells the robot which quadrant to go to
         */
        public void decideWhichQuadrantToGoTo()
        {
            if ( robotQuadrant() == 1 )
            {
                goToCorner( 1 );
            }
            if ( robotQuadrant() == 2 )
            {
                goToCorner( 2 );
            }
            if ( robotQuadrant() == 3 )
            {
                goToCorner( 3 );
            }
            if ( robotQuadrant() == 4 )
            {
                goToCorner( 4 );
            }
            alreadyInACorner = true;
        }


        /**
         * 
         * Return's the quadrant of your current robot. NOTE: the left bottom
         * hand corner is (0,0) The grid is divided into a coordinate plane,
         * where it follows the normal quadrant numbering system, with the top
         * right corner being in quadrant 1.
         * 
         * @return the quadrant of your robot
         */
        public int robotQuadrant()
        {
            if ( quadrantX( getX() ) == 1 && quadrantY( getY() ) == 1 )
            {
                return 3;
            }
            else if ( quadrantX( getX() ) == 1 && quadrantY( getY() ) == 2 )
            {
                return 2;
            }
            else if ( quadrantX( getX() ) == 2 && quadrantY( getY() ) == 1 )
            {
                return 4;
            }
            else
            {// if (quadrantX(enemy.getX()) == 2 && quadrantY(enemy.getY())
             // ==
             // 2) {
                return 1;
            }
        }


        /**
         * 
         * Divides the battle field into two parts by drawing a line vertically
         * in the center of the battle field. The left hand part is considered
         * quadrant 1, the right hand is considered quadrant 2. Depending on the
         * X coordinate input, determines the quadrant of said X coordinate.
         * 
         * @param x
         *            an X coordinate
         * @return the quadrant the given X coordinate is in: 1 or 2.
         */
        public int quadrantX( double x )
        {

            if ( x < getBattleFieldWidth() && x > xfourth * 2 )
            {
                return 2;
            }
            else
            {
                return 1;
            }

        }


        /**
         * 
         * Divides the battle field into two parts by drawing a horizontal line
         * in the middle of the battlefield. The top hand part is considered
         * quadrant 1, the bottom is considered quadrant 2. Depending on the Y
         * coordinate input, determines the quadrant of said Y coordinate.
         * 
         * @param y
         *            a Y coordinate
         * @return the quadrant the given Y coordinate is in: 1 or 2.
         */
        public int quadrantY( double y )
        {
            if ( y < getBattleFieldHeight() && y > yfourth * 2 )
            {
                return 2;
            }
            else
            {
                return 1;
            }
        }


        /**
         * 
         * Tells the robot to turn and go towards a corner in quadrant 1-4. X
         * represents the quadrant/corner you want to go to. Quadrant 1 is the
         * top right hand box when dividing the battle field into 4 equal
         * areas.Corner is represented by the middle of the box that is the
         * qudrant
         * 
         * @param x
         *            the quadrant you want to go to
         */
        public void goToCorner( int x )
        {

            double turnBearing = 0;

            double dToPoint = 0;

            if ( x == 1 )
            {
                turnBearing = absoluteBearing( getX(),
                    getY(),
                    3 * xfourth,
                    3 * yfourth );
                dToPoint = Point2D.distance( getX(), getY(), xfourth, yfourth );
            }
            if ( x == 2 )
            {
                turnBearing = absoluteBearing( getX(),
                    getY(),
                    xfourth,
                    3 * yfourth );
                dToPoint = Point2D.distance( getX(), getY(), xfourth, yfourth );
            }
            if ( x == 3 )
            {
                turnBearing = absoluteBearing( getX(), getY(), xfourth, yfourth );
                dToPoint = Point2D.distance( getX(), getY(), xfourth, yfourth );
            }
            if ( x == 4 )
            {
                turnBearing = absoluteBearing( getX(),
                    getY(),
                    3 * xfourth,
                    yfourth );
                dToPoint = Point2D.distance( getX(), getY(), xfourth, yfourth );
            }
            double turn = turnBearing - getHeading();
            // normalize the turn for more efficient movement
            setTurnRight( normalizeBearing( turn ) );
            setAhead( dToPoint );
            alreadyInACorner = true;
        }


        /**
         * Moves the robot in a star formation
         */
        public void star()
        {
            if ( getVelocity() == 0 )
            {
                if ( Math.abs( movement ) > howFarToMoveEachLeg )
                {
                    movement = howFarToMoveEachLeg;

                }

            }

            if ( getDistanceRemaining() == 0 )
            {
                setAhead( movement = -movement );
                setTurnRightRadians( baseDirection );
            }
        }

    }
}
