package kt_tc;

import robocode.*;


/**
 * Record the state of an enemy bot.
 * 
 * @author Kushal Tirumala, Tiffany Chiang
 * @version 5/31/2014
 * 
 * @author Period - 6
 * @author Assignment - EnemyBot
 * 
 * @author Sources - Kushal Tirumala, Tiffany Chiang
 */
public class EnemyBot
{
    /**
     * Enemy's bearing
     */
    private double bearing;

    /**
     * Enemy's distance
     */
    private double distance;

    /**
     * Enemy's energy
     */
    private double energy;

    /**
     * Enemy's heading
     */
    private double heading;

    /**
     * Enemy's velocity
     */
    private double velocity;

    /**
     * Enemy's name
     */
    private String name;


    /**
     * Constructor
     */
    public EnemyBot()
    {
        // TODO Your code here
    }


    /**
     * Gets the bearing TODO Write your method description here.
     * 
     * @return the bearing
     */
    public double getBearing()
    {
        return bearing;
    }


    /**
     * 
     * The distance of enemy robot
     * 
     * @return distance
     */
    public double getDistance()
    {
        return distance;
    }


    /**
     * 
     * Energy level enemy robot
     * 
     * @return energy
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * 
     * Heading of enemy robot
     * 
     * @return heading
     */
    public double getHeading()
    {
        return heading;
    }


    /**
     * 
     * Velocity of enemy robot
     * 
     * @return velocity
     */
    public double getVelocity()
    {
        return velocity;
    }


    /**
     * 
     * Name of enemy robot
     * 
     * @return name
     */
    public String getName()
    {
        return name;
    }


    /**
     * 
     * Updates enemies information
     * 
     * @param srEvt
     *            the scannedrobotevent
     */
    public void update( ScannedRobotEvent srEvt )
    {
        bearing = srEvt.getBearing();
        distance = srEvt.getDistance();
        energy = srEvt.getEnergy();
        heading = srEvt.getHeading();
        velocity = srEvt.getVelocity();
        name = srEvt.getName();

    }


    /**
     * 
     * Resets the enemy's information
     */
    public void reset()
    {
        bearing = 0;
        distance = 0;
        energy = 0;
        heading = 0;
        velocity = 0;
        name = "";
    }


    /**
     * 
     * Checks to see if we scanned a robot yet
     * 
     * @return if radar picked up a robot
     */
    public boolean none()
    {
        return name.length() == 0;
    }
}