package nslj.src.display.j3d;

import org.odejava.HashSpace;
import org.odejava.World;
import org.openmali.vecmath2.Vector3f;

import javax.media.j3d.*;

/**                                                                                                              
 * Abstract body class - basically manages a hierarchy of body segments and provides methods for inverse and forward
 * kinematics and localization of end-effector
 */
public abstract class Body extends NslObject3d
{
    // Array of body segments
    public BodySegment[] bodySegments;
    // Controller for whole body
    protected BodyController controller;

    /**
     * Create body
     * @param name - name of body
     * @param app - visual appearance
     * @param dim - size
     * @param translation - local position
     * @param eulerAngles - local orientation
     */
    public Body(String name, Appearance app, Vector3f dim, Vector3f translation, float[] eulerAngles)
    {
        super(name, app, dim, translation, eulerAngles);
        createBody();
    }

    /**
     * Create body segments - must be implemented by subclasses
     */
    protected abstract void createBody();

    /**
     * Initialize physical components - body has none, this is handled by body segments
     * @param world - world
     * @param collSpace - collision space
     */
    protected void initPhysical(World world, HashSpace collSpace)
    {}

    /**
     * Initialize visual appearance - body has none, this is handled by body segments
     */
    protected void initVisual()
    {}

    /**
     * Update visual information from physical simulation - not used by body, handled by body segments
     */
    public void initPhysicalLocationOrientation()
    {}

    /**
     * Recursively searches segment hierarchy and returns body segment with given name
     * @param name - name of segment to return
     * @return BodySegment
     */
    public BodySegment getSegment(String name)
    {
        if(getRootSegment().getName().equals(name))
            return getRootSegment();
        else
            return (BodySegment)getRootSegment().getChild(name);
    }

    /**
     * Setup body and body segment controllers - must be implemented by subclasses
     */
    public abstract void setupControllers();

    /**
     * Get inverse Jacobian matrix - must be implemented by subclasses
     * @return float[][]
     */
    public abstract float[][] getInverseJacobian();

    /**
     * Performs inverse kinematics - set controller targets to achieve joint angles required to achieve given difference
     * in end effector position - must be implemented by subclasses
     * @param DX - desired difference in end effector position
     */
    public abstract void inverseKinematics(javax.vecmath.Vector3f DX);

    /**
     * Forward kinematics - returns end effector position given joint angles - must be implemented by subclasses
     * @param angles - joint angles to use
     * @return Vector3d
     */
    public abstract javax.vecmath.Vector3d forwardKinematics(float angles[]);

    /**
     * Gets the current end-effector position - must be implemented by subclasses
     * @return Vector3f
     */
    public abstract javax.vecmath.Vector3f getEndEffectorPosition();

    /**
     * Gets root segment - the first non-null segment in the array
     * @return BodySegment
     */
    public BodySegment getRootSegment()
    {
        if(bodySegments!=null)
        {
            for (BodySegment bodySegment : bodySegments)
            {
                if (bodySegment != null)
                    return bodySegment;
            }
        }
        return null;
    }

    /**
     * Get the body controller
     * @return BodyController
     */
    public BodyController getController()
    {
        return controller;
    }

    /**
     * Set the body controller
     * @param controller - BodyController to user
     */
    public void setController(BodyController controller)
    {
        this.controller = controller;
    }

    public void process()
    {
        super.process();
        if(this.controller!=null)
            this.controller.process();
    }

    /**
     * Get shape
     * @return  -1 - no primitive shape
     */
    public int getShape()
    {
        return -1;
    }
}
