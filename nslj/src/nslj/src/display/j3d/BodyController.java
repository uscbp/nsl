package nslj.src.display.j3d;

/**
 * Motor controller for a whole body
 */
public abstract class BodyController
{
    // The body being controlled
    protected Body body;

    /**
     * Create body controller
     * @param body - the body to control
     */
    public BodyController(Body body)
    {
        this.body=body;
    }

    /**
     * Processes the controller - unless overridden, recursively runs controllers of body segments starting with
     * the root segment
     */
    public void process()
    {
        if(body.getRootSegment()!=null)
            body.getRootSegment().runControllers();
    }
}
