package kt_tc;

import robocode.*;


/**
 * Record the advanced state of an enemy bot.
 * 
 * @author Kushal Tirumala, Tiffany Chiang
 * @version 5/30/2014
 * 
 * @author Period - 6
 * @author Assignment - AdvancedEnemyBot
 * 
 * @author Sources - Kushal Tirumala, Tiffany Chiang
 */
public class AdvancedEnemyBot extends EnemyBot
{
    /**
     * Enemy's X coordinate
     */
    private double x;

    /**
     * Enemy's Y coordinate
     */
    private double y;

    /**
     * How many times we have been hit
     */
    private int hitCount;

    /**
     * Viability to target an enemy robot
     */
    private double viability;


    /**
     * Constructor
     */
    public AdvancedEnemyBot()
    {
        reset();
    }


    /**
     * 
     * Gets the x
     * 
     * @return x
     */
    public double getX()
    {
        return x;
    }


    public int getHitCount()
    {
        return hitCount;
    }


    public double getViability()
    {
        return viability;
    }


    /**
     * 
     * Gets the y
     * 
     * @return y
     */
    public double getY()
    {
        return y;
    }


    /**
     * 
     * Updates the information of the enemy robot
     * 
     * @param e
     *            the scanned robot event
     * @param robot
     *            the object representative
     */
    public void update( ScannedRobotEvent e, Robot robot )
    {
        super.update( e );

        double absBearingDeg = ( robot.getHeading() + e.getBearing() );

        if ( absBearingDeg < 0 )

        {

            absBearingDeg += 360;

        }

        x = robot.getX() + Math.sin( Math.toRadians( absBearingDeg ) )

        * e.getDistance();

        y = robot.getY() + Math.cos( Math.toRadians( absBearingDeg ) )

        * e.getDistance();
    }


    /**
     * 
     * Predicts the X of the enemy
     * 
     * @param when
     *            time until shoot
     * @return the x
     */
    public double getFutureX( long when )
    {
        return x + Math.sin( Math.toRadians( getHeading() ) ) * getVelocity()
            * when;
    }


    /**
     * How many times the we have been hit
     */
    public void hit()
    {
        hitCount++;
    }


    /**
     * Calculates viablity of target. The lower it is, the more likely we should
     * target it.
     */
    public void calcViability()
    {
        viability = getDistance();
    }


    /**
     * 
     * Gets future Y of the enemy
     * 
     * @param when
     *            the time until shoot
     * @return the future y
     */
    public double getFutureY( long when )
    {
        return y + Math.cos( Math.toRadians( getHeading() ) ) * getVelocity()
            * when;
    }


    /**
     * Reset's the enemy's information
     */
    public void reset()
    {
        super.reset();
        x = 0;
        y = 0;
        hitCount = 0;
        viability = 0;
    }

}