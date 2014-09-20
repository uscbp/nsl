package nslj.src.display;

import nslj.src.lang.NslModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.lang.reflect.Method;

public class NslSlider extends JSlider
{
    public static int HORIZONTAL=JSlider.HORIZONTAL;
    public static int VERTICAL=JSlider.VERTICAL;
    private String name;
    private NslModule module;

    public NslSlider(String name, NslModule module)
    {
        super();
        this.name = name;
        this.module = module;
        addChangeListener(new NslSliderListener());
    }

    class NslSliderListener implements ChangeListener
    {
        boolean found = true;

        public void stateChanged(ChangeEvent e)
        {
            if (found)
            {
                try
                {
                    JSlider source=(JSlider)e.getSource();
                    String n = name + "ValueChanged";
                    Method m = module.getClass().getMethod(n, new Class[]{NslSlider.class,Integer.TYPE});
                    m.invoke(module, new Object[]{(NslSlider)e.getSource(),new Integer(source.getValue())});
                }
                catch (Exception ex)
                {
                    try
                    {
                        JSlider source=(JSlider)e.getSource();
                        String n = name + "ValueChanged";
                        Method m = module.getClass().getMethod(n, new Class[]{Integer.TYPE});
                        m.invoke(module, new Object[]{new Integer(source.getValue())});
                    }
                    catch(Exception ex2)
                    {
                        System.out.println("Error: Couldn't find method " + name + "ValueChanged(int newValue) {}");
                        found = false;
                    }
                }
            }
            else
            {
                System.out.println("Error: Not executing the method");
            }
        }
    }
}
