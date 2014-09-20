package nslj.src.display;

import java.util.Hashtable;

/**
 * User: jbonaiuto
 * Date: Sep 17, 2007
 * Time: 11:58:50 AM
 */
public class NslParamsNode
{
    private int parc;
    private Hashtable parhash;
    private double par[][];
    private int slot=0;
    private String names[];

    public NslParamsNode(int maxsample, String[] params)
    {
        parhash=new Hashtable();
        parc=params.length;
        for (int i=0;i<parc;i++)
            parhash.put(params[i],new Integer(i));
        par=new double[parc][maxsample];
        names=new String[parc];
        System.arraycopy(params, 0, names, 0, parc);
    }

    private int getIndex(String s)
    {
        return ((Integer)parhash.get(s)).intValue();
    }

    public void advance()
    {
        slot++;
    }

    public void put(String s, double value)
    {
        int k=getIndex(s);
        par[k][slot]=value;
    }

    public double[][] getAll()
    {
        return par;
    }

    public double getRel(String s,int relpos)
    {
        int k=getIndex(s);
        return par[k][slot+relpos];
    }

    public double getAbs(String s,int pos)
    {
        int k=getIndex(s);
        return par[k][pos];
    }

    public int getSlot()
    {
        return slot;
    }

	public NslSpline[] getSplines()
	{
		NslSpline[] sp=new NslSpline[parc];
		for (int k=0;k<parc;k++)
		{
			sp[k]=new NslSpline(slot,par[k]);
			sp[k].setLabel(names[k]);
		}
		return sp;
	}

    public void reset()
    {
        slot=0;
        par=new double[parc][par[0].length];
    }
}
