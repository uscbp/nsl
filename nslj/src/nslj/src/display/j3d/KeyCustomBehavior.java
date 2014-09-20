package nslj.src.display.j3d;

import javax.media.j3d.TransformGroup;

/**
 * Key behavior that passes move and rotate events back to a KeyListener rather than handling them itself
 */
public class KeyCustomBehavior extends KeyBehavior
{
    // Key listener
    KeyListener listener;

    /**
     * KeyCustomBehavior constructor
     * @param tg - avatar transform group
     */
    public KeyCustomBehavior(TransformGroup tg)
    {
        super(tg);
    }

    /**
     * KeyCustomBehavior constructor
     * @param tg - avatar transform group
     * @param l - key listener to pass events back to
     */
    public KeyCustomBehavior(TransformGroup tg, KeyListener l)
    {
        super(tg);
        listener=l;
    }

    /**
     * Process move forward event
     */
    protected void moveForward()
    {
        if(listener!=null)
            listener.forwardPushed();
    }

    /**
     * Process move backward event
     */
    protected void moveBackward()
    {
        if(listener!=null)
            listener.backPushed();
    }

    /**
     * Process rotate right event
     */
    protected void rotRight()
    {
        if(listener!=null)
            listener.rightPushed();
    }

    /**
     * Process rotate left event
     */
    protected void rotLeft()
    {
        if(listener!=null)
            listener.leftPushed();
    }
}
