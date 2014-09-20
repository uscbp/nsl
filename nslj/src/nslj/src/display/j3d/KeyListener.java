package nslj.src.display.j3d;

/**
 * KeyListener interface - models that want to handle key events themselves should implement this
 */
public interface KeyListener
{
    public void forwardPushed();

    public void backPushed();

    public void leftPushed();

    public void rightPushed();
}
