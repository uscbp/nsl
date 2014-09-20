/* 
 *Copyright(c) 1997 USC Brain Project. email nsl@java.usc.edu
*/

/**
 @author Nikunj Mehta
 */

package nslj.src.display;

import java.util.Vector;

public class NslDisplaySystemVector extends Vector<NslDisplaySystem>
{
    public NslDisplaySystemVector(int initial, int increment)
    {
        super(initial, increment);
    }

    public NslDisplaySystemVector(int initial)
    {
        super(initial);
    }

    public void init()
    {
        for (int i = 0; i < elementCount; i++)
        {
            NslDisplaySystem ds = (NslDisplaySystem) elementData[i];
            if(!ds.frame.isVisible())
                ds.show();
            ds.initialize();
        }
    }

    public void initEpoch()
    {
        for (int i = 0; i < elementCount; i++)
        {
            NslDisplaySystem ds = (NslDisplaySystem) elementData[i];
            //ds.show();
            ds.initializeEpoch();
        }
    }

    public void endEpoch(int epoch)
    {
        for (int i = 0; i < elementCount; i++)
        {
            NslDisplaySystem ds = (NslDisplaySystem) elementData[i];
            ds.endEpoch(epoch);
        }
    }

    public static synchronized void awake()
    {
        System.out.println("Notifying displays");

        NslFrame.system.notifyDisplays();
    }

    public synchronized void collect()
    {
        for (int i = 0; i < elementCount; i++)
        {
            NslDisplaySystem ds = (NslDisplaySystem) elementData[i];
            ds.notifyDisplay();
        }

        for (int i = 0; i < elementCount; i++)
        {
            NslFrame.system.waitDisplayAck();
        }
    }
}
