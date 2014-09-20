package nslj.src.lang;

import nslj.src.system.NslSystem;
import nslj.src.main.NslMain;

import java.lang.reflect.Field;
import java.io.File;

public class NslMatlabModule extends NslModule
{
    public NslMatlabModule(String nslName, NslModule nslParent)
    {
        super(nslName, nslParent);
        if(NslMain.modelPath!=null)
        {
            NslSystem.getMatlabEngine().engEvalString("cd "+NslMain.modelPath+ File.separatorChar+
                                                      NslMain.modelName.substring(0,NslMain.modelName.indexOf('.')));
        }
        else
        {
            NslSystem.getMatlabEngine().engEvalString("cd "+NslMain.libDir);
        }
    }
    
    protected void callMethod(String method, Object[] args)
    {
        StringBuilder cmd=new StringBuilder(this._name);
        cmd.append('=');
        cmd.append(this._name);
        cmd.append('.');
        cmd.append(method);
        cmd.append('(');
        if(args!=null && args.length>0)
        {
            cmd.append(args[0]);
            for(int i=1; i<args.length; i++)
            {
                cmd.append(',');
                cmd.append(args[i]);
            }
        }
        cmd.append(");");
        NslSystem.getMatlabEngine().engEvalString(cmd.toString());
    }

    public void callFromConstructorTop(Object[] args)
    {
        super.callFromConstructorTop(args);
        StringBuilder cmd=new StringBuilder(this._name);
        cmd.append('=');
        String className=this.getClass().getName();
        if(className.indexOf('.')>-1)
            className=className.substring(className.lastIndexOf('.')+1);
        cmd.append(className);
        cmd.append('(');
        if(args.length>0)
        {
            cmd.append(args[0]);
            for(int i=1;i<args.length;i++)
            {
                cmd.append(", ");
                cmd.append(args[1]);
            }
        }
        cmd.append(");");
        NslSystem.getMatlabEngine().engEvalString(cmd.toString());
    }

    public void endSys()
    {
        callMethod("endSys", null);
    }

    public void initModule()
    {
        callMethod("initModule", null);
        updateInternalVariables();
    }

    public void endModule()
    {
        callMethod("endModule",  null);
    }

    public void initRun()
    {
        callMethod("initRun", null);
        updateInternalVariables();
    }

    public void endRun()
    {
        callMethod("endRun", null);
    }

    public void initRunEpoch()
    {
        callMethod("initRunEpoch", null);
        updateInternalVariables();
    }

    public void simRun()
    {
        updateInputPorts();
        callMethod("simRun", null);
        updateInternalVariables();
    }

    public void endRunEpoch()
    {
        callMethod("endRunEpoch", null);
    }

    public void initTrainEpoch()
    {
        callMethod("initTrainEpoch",  null);
        updateInternalVariables();
    }

    public void initTrain()
    {
        callMethod("initTrain", null);
        updateInternalVariables();
    }

    public void simTrain()
    {
        updateInputPorts();
        callMethod("simTrain", null);
        updateInternalVariables();
    }

    public void endTrain()
    {
        callMethod("endTrain", null);
    }

    public void endTrainEpoch()
    {
        callMethod("endTrainEpoch", null);
    }

    /**
     * Update the MATLAB module variables based on the values at this module's inports
     */
    protected void updateInputPorts()
    {
        for(int i=0; i<_inports.size(); i++)
        {
            NslInport in=_inports.get(i);
            if(in.isInitialized() && in.getData()!=null && in.getData() instanceof NslNumeric)
            {
                NslData d=in.getData();
                if(d instanceof NslNumeric0)
                {
                    NslSystem.getMatlabEngine().engPutArray("nslReserved", ((NslNumeric0)d).getdouble());
                    NslSystem.getMatlabEngine().engEvalString(_name+'.'+in._name+"=nslReserved;");
                }
                else if(d instanceof NslNumeric1)
                {
                    NslSystem.getMatlabEngine().engPutArray("nslReserved", ((NslNumeric1)d).getdouble1());
                    NslSystem.getMatlabEngine().engEvalString(_name+'.'+in._name+"=nslReserved;");
                }
                else if(d instanceof NslNumeric2)
                {
                    NslSystem.getMatlabEngine().engPutArray("nslReserved", ((NslNumeric2)d).getdouble2());
                    NslSystem.getMatlabEngine().engEvalString(_name+'.'+in._name+"=nslReserved;");
                }
                else
                {
                    System.err.println("ERROR: Matrices with dimensions greater than 2 not currently supported.");
                }
            }
        }
    }

    /**
     * Update this module's output ports and internal variables based on the values of the MATLAB variables
     */
    protected void updateInternalVariables()
    {
        Class c=this.getClass();
        for(int i=0; i<c.getDeclaredFields().length; i++)
        {
            Field f=c.getDeclaredFields()[i];
            if(isAncestorClass(f.getType(), NslNumeric.class))
            {
                boolean accessibility=f.isAccessible();
                f.setAccessible(true);
                if(isAncestorClass(f.getType(), NslNumeric0.class) && !(f.getType().equals(NslDinInt0.class) ||
                                                                        f.getType().equals(NslDinDouble0.class) ||
                                                                        f.getType().equals(NslDinFloat0.class)))
                {
                    try
                    {
                        NslSystem.getMatlabEngine().engEvalString("nslReserved="+_name+'.'+f.getName());
                        double d=NslSystem.getMatlabEngine().engGetScalar("nslReserved");
                        NslNumeric0 x=(NslNumeric0)f.get(this);
                        x._set(d);
                        f.set(this,x);
                    }
                    catch(Exception e2)
                    {}
                }
                else if(isAncestorClass(f.getType(), NslNumeric1.class) && !(f.getType().equals(NslDinInt1.class) ||
                                                                        f.getType().equals(NslDinDouble1.class) ||
                                                                        f.getType().equals(NslDinFloat1.class)))
                {
                    try
                    {
                        NslSystem.getMatlabEngine().engEvalString("nslReserved="+_name+'.'+f.getName());
                        double[][] d=NslSystem.getMatlabEngine().engGetArray("nslReserved");
                        NslNumeric1 x=(NslNumeric1)f.get(this);
                        x._set(d[0]);
                        f.set(this,x);
                    }
                    catch(Exception e2)
                    {}
                }
                else if(isAncestorClass(f.getType(), NslNumeric2.class) && !(f.getType().equals(NslDinInt2.class) ||
                                                                        f.getType().equals(NslDinDouble2.class) ||
                                                                        f.getType().equals(NslDinFloat2.class)))
                {
                    try
                    {
                        NslSystem.getMatlabEngine().engEvalString("nslReserved="+_name+'.'+f.getName());
                        double[][] d=NslSystem.getMatlabEngine().engGetArray("nslReserved");
                        NslNumeric2 x=(NslNumeric2)f.get(this);
                        x._set(d);
                        f.set(this,x);
                    }
                    catch(Exception e2)
                    {}
                }
                f.setAccessible(accessibility);
            }
        }
    }

    public static boolean isAncestorClass(Class child, Class ancestor)
    {
        if(child.equals(ancestor))
            return true;

        Class s=child;
        while(s!=null)
        {
            s=s.getSuperclass();
            if(s!=null && s.equals(ancestor))
                return true;
        }
        return false;
    }
}
