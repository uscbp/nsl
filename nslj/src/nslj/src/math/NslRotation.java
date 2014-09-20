package nslj.src.math;

/**
 * User: jbonaiuto
 * Date: Oct 6, 2007
 * Time: 9:57:39 PM
 */
public class NslRotation
{
    public static int[] eval(int[] x, int[] y, int z)
    {
        int[] ret=new int[3];
        double[] y2=new double[y.length];
        for(int i=0; i<y.length; i++)
            y2[i]=(double)y[i];
        double[] angle=zap2Y(y2);
        ret=Yrotate(x,(int)angle[1]);
        ret=Xrotate(ret,(int)angle[0]);
        ret=Yrotate(ret,z);
        ret=Xrotate(ret,(int)-angle[0]);
        ret=Yrotate(ret,(int)-angle[1]);
        return ret;
    }

    public static double[] eval(double[] x, double[] y, double z)
    {
        double[] ret=new double[3];
        double[] angle=zap2Y(y);
        ret=Yrotate(x,angle[1]);
        ret=Xrotate(ret,angle[0]);
        ret=Yrotate(ret,z);
        ret=Xrotate(ret,-angle[0]);
        ret=Yrotate(ret,-angle[1]);
        return ret;
    }
    /*static public Point3d rotate(Point3d p, Point3d n,double t)
    {
        Point3d angle=zap2Y(n);
        Point3d py =Yrotate(p ,angle.y);
        Point3d pyx=Xrotate(py,angle.x);
        Point3d qyx=Yrotate(pyx,t);
        Point3d qy =Xrotate(qyx,-angle.x);
        Point3d q =Yrotate(qy,-angle.y);
        return q;
    }*/

    public static float[] eval(float[] x, float[] y, float z)
    {
        float[] ret=new float[3];
        double[] y2=new double[y.length];
        for(int i=0; i<y.length; i++)
            y2[i]=(double)y[i];
        double[] angle=zap2Y(y2);
        ret=Yrotate(x,(float)angle[1]);
        ret=Xrotate(ret,(float)angle[0]);
        ret=Yrotate(ret,z);
        ret=Xrotate(ret,(float)-angle[0]);
        ret=Yrotate(ret,(float)-angle[1]);
        return ret;
    }

    static public double[] zap2Y(double[] n)
    {
        double Xang=0,Yang=0,cosT=0,sinT=0;
        double bot1=Math.sqrt(n[0]*n[0]+n[2]*n[2]);
        if (bot1==0)
        {
            Yang=0;
        }
        else
        {
            cosT=n[2]/bot1;
            sinT=n[0]/bot1;
            Yang=Math.acos(cosT);
            if(sinT<0)
                Yang*=-1;
        }
        double[] np=Yrotate(n,-Yang);

        double bot2=Math.sqrt(n[1]*n[1]+bot1*bot1);
        Xang=Math.acos(np[1]/bot2);

        np=Xrotate(np,-Xang);

        return new double[]{-Xang,-Yang,0};
    }

    static public double[] Zrotate(double[] p,double t)
    {
        double xx= p[0]*Math.cos(t) - p[1]*Math.sin(t) ;
        double yy= p[0]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new double[]{xx,yy,p[2]};
    }

    /** Returns rotated version of p  around X axis by t radians */
    static public double[] Xrotate(double[] p,double t)
    {
        double zz= p[2]*Math.cos(t) + p[1]*Math.sin(t) ;
        double yy=-p[2]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new double[]{p[0],yy,zz};
    }

    /*static public Point3d Xrotate(Point3d p,double t)
    {
        double zz= p.z*Math.cos(t) + p.y*Math.sin(t) ;
        double yy=-p.z*Math.sin(t) + p.y*Math.cos(t) ;
        return new Point3d(p.x,yy,zz);
    }*/

    /** Returns rotated version of p  around Y axis by t radians */
    static public double[] Yrotate(double[] p,double t)
    {
        double xx= p[0]*Math.cos(t) + p[2]*Math.sin(t) ;
        double zz=-p[0]*Math.sin(t) + p[2]*Math.cos(t) ;
        return new double[]{xx,p[1],zz};
    }

    /*static public Point3d Yrotate(Point3d p,double t)
    {
        double xx= p.x*Math.cos(t) + p.z*Math.sin(t) ;
        double zz=-p.x*Math.sin(t) + p.z*Math.cos(t) ;
        return new Point3d(xx,p.y,zz);
    }*/

    static public int[] Zrotate(int[] p,int t)
    {
        double xx= p[0]*Math.cos(t) - p[1]*Math.sin(t) ;
        double yy= p[0]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new int[]{(int)xx,(int)yy,p[2]};
    }

    /*
    static public void _Zrotate(Point3d p,double t)
    {
        double xx= p.x*Math.cos(t) + -p.y*Math.sin(t) ;
        double yy= p.x*Math.sin(t) + p.y*Math.cos(t) ;
        p.x=xx;
        p.y=yy;
    }
     */

    /** Returns rotated version of p  around X axis by t radians */
    static public int[] Xrotate(int[] p,int t)
    {
        double zz= p[2]*Math.cos(t) + p[1]*Math.sin(t) ;
        double yy=-p[2]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new int[]{p[0],(int)yy,(int)zz};
    }

    /** Returns rotated version of p  around Y axis by t radians */
    static public int[] Yrotate(int[] p,int t)
    {
        double xx= p[0]*Math.cos(t) + p[2]*Math.sin(t) ;
        double zz=-p[0]*Math.sin(t) + p[2]*Math.cos(t) ;
        return new int[]{(int)xx,p[1],(int)zz};
    }

    static public float[] Zrotate(float[] p,float t)
    {
        double xx= p[0]*Math.cos(t) - p[1]*Math.sin(t) ;
        double yy= p[0]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new float[]{(float)xx,(float)yy,p[2]};
    }

    /** Returns rotated version of p  around X axis by t radians */
    static public float[] Xrotate(float[] p,float t)
    {
        double zz= p[2]*Math.cos(t) + p[1]*Math.sin(t) ;
        double yy=-p[2]*Math.sin(t) + p[1]*Math.cos(t) ;
        return new float[]{p[0],(float)yy,(float)zz};
    }

    /** Returns rotated version of p  around Y axis by t radians */
    static public float[] Yrotate(float[] p,float t)
    {
        double xx= p[0]*Math.cos(t) + p[2]*Math.sin(t) ;
        double zz=-p[0]*Math.sin(t) + p[2]*Math.cos(t) ;
        return new float[]{(float)xx,p[1],(float)zz};
    }

    static public int[] Lrotate(int[] p, int[] p1, int[] p2, int t)
    {
        int[] ret=new int[3];
        int[] n=NslSub.eval(p2,p1); //n=p2-p1;
        ret=NslSub.eval(p,p1);          //p-=p1;
        ret=NslRotation.eval(ret,n,t);
        ret=NslAdd.eval(ret,p1);
        return ret;
    }

    static public double[] Lrotate(double[] p, double[] p1, double[] p2, double t)
    {
        double[] ret=new double[3];
        double[] n=NslSub.eval(p2,p1); //n=p2-p1;
        ret=NslSub.eval(p,p1);          //p-=p1;
        ret=NslRotation.eval(ret,n,t);
        ret=NslAdd.eval(ret,p1);
        return ret;
    }

    static public float[] Lrotate(float[] p, float[] p1, float[] p2, float t)
    {
        float[] ret=new float[3];
        float[] n=NslSub.eval(p2,p1); //n=p2-p1;
        ret=NslSub.eval(p,p1);          //p-=p1;
        ret=NslRotation.eval(ret,n,t);
        ret=NslAdd.eval(ret,p1);
        return ret;
    }
}
