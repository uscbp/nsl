package nslj.src.display.j3d;

import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.Enumeration;

/**
 * Behavior for navigating 3d worlds with the keypad
 */
public class KeyBehavior extends Behavior
{
    // Fast movement speed
    protected static final double FAST_SPEED = 20.0;

    // Normal movement speed
    protected static final double NORMAL_SPEED = 1.0;

    // Slow movement speed
    protected static final double SLOW_SPEED = 0.5;

    // Transform group defining avatar location
    protected TransformGroup transformGroup;

    // Transform for changing avatar orientation and position
    protected Transform3D transform3D;

    // Condition to wake up and do things (key events)
    protected WakeupCondition keyCriterion;

    // Amount to rotate for each key press
    private double rotateXAmount = Math.PI / 16.0;

    private double rotateYAmount = Math.PI / 16.0;

    private double rotateZAmount = Math.PI / 16.0;

    private double moveRate = 5;

    // Current speed
    private double speed = NORMAL_SPEED;

    // Forward movement scale
    private final double kMoveForwardScale = 1.1;

    // Backward movement scale
    private final double kMoveBackwardScale = 0.9;

    // Forward key event
    private int forwardKey = KeyEvent.VK_UP;

    // Backward key event
    private int backKey = KeyEvent.VK_DOWN;

    // Left key event
    private int leftKey = KeyEvent.VK_LEFT;

    // Right key event
    private int rightKey = KeyEvent.VK_RIGHT;

    /**
     * KeyBehavior constructor
     * @param tg - avatar transform group
     */
    public KeyBehavior(TransformGroup tg)
    {
        super();

        transformGroup = tg;
        transform3D = new Transform3D();
    }

    /**
     * Initialize behavior - wakeup criteria
     */
    public void initialize()
    {
        // Initialize wakeup criterion - key pressed and released
        WakeupCriterion[] keyEvents = new WakeupCriterion[2];
        keyEvents[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        keyEvents[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
        keyCriterion = new WakeupOr(keyEvents);

        wakeupOn(keyCriterion);
    }

    /**
     * Process wakeup stimuli
     * @param criteria - set of wakeup criteria
     */
    public void processStimulus(Enumeration criteria)
    {
        WakeupCriterion wakeup;
        AWTEvent[] event;

        // Loop through criteria
        while (criteria.hasMoreElements())
        {
            wakeup = (WakeupCriterion) criteria.nextElement();

            // Only interested in wakeup events
            if (!(wakeup instanceof WakeupOnAWTEvent))
                continue;

            event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();

            // Look at event
            for (AWTEvent anEvent : event)
            {
                // Process key press
                if (anEvent.getID() == KeyEvent.KEY_PRESSED)
                {
                    processKeyEvent((KeyEvent) anEvent);
                }
            }
        }

        wakeupOn(keyCriterion);
    }

    /**
     * Process key events
     * @param event - key event
     */
    protected void processKeyEvent(KeyEvent event)
    {
        int keycode = event.getKeyCode();

        // Alter speed with shift
        if (event.isShiftDown())
            speed = FAST_SPEED;
        else
            speed = NORMAL_SPEED;

        if (event.isAltDown())
            altMove(keycode);
        else if (event.isControlDown())
            controlMove(keycode);
        else
            standardMove(keycode);
    }

    /**
     * Moves forward backward or rotates left right
     * @param keycode - code for key pressed
     */
    private void standardMove(int keycode)
    {
        if (keycode == forwardKey)
            moveForward();
        else if (keycode == backKey)
            moveBackward();
        else if (keycode == leftKey)
            rotLeft();
        else if (keycode == rightKey)
            rotRight();
    }

    //moves left right, rotate up down
    protected void altMove(int keycode)
    {
        if (keycode == forwardKey)
            rotUp();
        else if (keycode == backKey)
            rotDown();
        else if (keycode == leftKey)
            rotLeft();
        else if (keycode == rightKey)
            rotRight();
        else if (keycode == leftKey)
            moveLeft();
        else if (keycode == rightKey)
            moveRight();
    }

    //move up down, rot left right
    protected void controlMove(int keycode)
    {
        if (keycode == forwardKey)
            moveUp();
        else if (keycode == backKey)
            moveDown();
        else if (keycode == leftKey)
            rollLeft();
        else if (keycode == rightKey)
            rollRight();
    }

    protected void moveForward()
    {
        // Move in negative z direction based on speed
        doMove(new Vector3d(0.0, 0.0, -kMoveForwardScale * speed));
    }

    /**
     * Move backward
     */
    protected void moveBackward()
    {
        // Move in positive z direction based on speed
        doMove(new Vector3d(0.0, 0.0, kMoveBackwardScale * speed));
    }

    private void moveLeft()
    {
        doMove(new Vector3d(-getMovementRate(), 0.0, 0.0));
    }

    private void moveRight()
    {
        doMove(new Vector3d(getMovementRate(), 0.0, 0.0));
    }

    private void moveUp()
    {
        doMove(new Vector3d(0.0, getMovementRate(), 0.0));
    }

    private void moveDown()
    {
        doMove(new Vector3d(0.0, -getMovementRate(), 0.0));
    }

    protected void rotRight()
    {
        doRotateY(getRotateRightAmount());
    }

    protected void rotUp()
    {
        doRotateX(getRotateUpAmount());
    }

    protected void rotLeft()
    {
        doRotateY(getRotateLeftAmount());
    }

    /**
     * Update transform group
     */
    protected void rotDown()
    {
        doRotateX(getRotateDownAmount());
    }

    protected void rollLeft()
    {
        doRotateZ(getRollLeftAmount());
    }

    protected void rollRight()
    {
        doRotateZ(getRollRightAmount());
    }

    protected void updateTransform()
    {
        transformGroup.setTransform(transform3D);
    }

    /**
     * Rotate in the y axis
     * @param radians - angle to rotate by
     */
    protected void doRotateY(double radians)
    {
        // Update transform
        transformGroup.getTransform(transform3D);
        // Create rotation matrix
        Transform3D toMove = new Transform3D();
        toMove.rotY(radians);
        // Multiply transform by rotation matrix
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doRotateX(double radians)
    {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.rotX(radians);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doRotateZ(double radians)
    {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.rotZ(radians);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doMove(Vector3d theMove)
    {
        // Update transform
        transformGroup.getTransform(transform3D);
        // Create translation matrix
        Transform3D toMove = new Transform3D();
        toMove.setTranslation(theMove);
        // Multiple transform by translation matrix
        transform3D.mul(toMove);
        updateTransform();
    }

    protected double getMovementRate()
    {
        return moveRate * speed;
    }

    protected double getRollLeftAmount()
    {
        return rotateZAmount * speed;
    }

    protected double getRollRightAmount()
    {
        return -rotateZAmount * speed;
    }

    protected double getRotateUpAmount()
    {
        return rotateYAmount * speed;
    }

    protected double getRotateDownAmount()
    {
        return -rotateYAmount * speed;
    }

    protected double getRotateLeftAmount()
    {
        return rotateYAmount * speed;
    }

    /**
     * Get amount to rotate when rotating right
     * @return - radians to rotate by
     */
    protected double getRotateRightAmount()
    {
        return -rotateYAmount * speed;
    }

    public void setRotateXAmount(double radians)
    {
        rotateXAmount = radians;
    }

    public void setRotateYAmount(double radians)
    {
        rotateYAmount = radians;
    }

    public void setRotateZAmount(double radians)
    {
        rotateZAmount = radians;
    }

    public void setMovementRate(double meters)
    {
        moveRate = meters; // Travel rate in meters/frame
    }

    public void setForwardKey(int key)
    {
        forwardKey = key;
    }

    public void setBackKey(int key)
    {
        backKey = key;
    }

    public void setLeftKey(int key)
    {
        leftKey = key;
    }
}
