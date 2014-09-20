package nslj.src.display.j3d;

/**
 * Controller for a single body segment axis
 */
public abstract class BodySegmentAxisController
{
    // The body segment being controlled
    protected BodySegment controlledLimb;
    // The axis being controlled
    protected int controlledAxis;

    /**
     * Create controller for given axis of given body segment
     * @param limb - the limb to control
     * @param axis - the axis to control
     */
    public BodySegmentAxisController(BodySegment limb, int axis)
    {
        this.controlledLimb=limb;
        this.controlledAxis=axis;
    }

    /**
     * Process the controller - must be implemented by subclasses
     */
    public abstract void process();
}
