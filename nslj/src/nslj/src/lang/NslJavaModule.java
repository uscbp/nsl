package nslj.src.lang;

/**
 * NslJavaModule
 * Eventually NslModule functionality should be moved here and NslModule should be made abstract
 */
public class NslJavaModule extends NslModule
{
    public NslJavaModule()
    {
        super();
    }

    public NslJavaModule(String label)
    {
        super(label);
    }
    
    public NslJavaModule(String label, NslModule nslParent)
    {
        super(label, nslParent);
    }
}
