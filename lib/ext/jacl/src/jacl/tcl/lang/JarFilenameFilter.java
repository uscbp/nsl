package jacl.tcl.lang;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: jbonaiuto
 * Date: Mar 27, 2007
 * Time: 10:35:36 AM
 * To change this template use File | Settings | File Templates.
 */
class JarFilenameFilter implements FilenameFilter {

    
/*
 *----------------------------------------------------------------------
 *
 * accept --
 *
 *	Used by the getClassFromJar method.  When list returns a list
 *      of files in a directory, the list will only be of jar or zip
 *      files.
 *
 * Results:
 *	True if the file ends with .jar or .zip
 *
 * Side effects:
 *	None.
 *
 *----------------------------------------------------------------------
 */

    public boolean
            accept(
            File dir,
            String name)
    {
        if (name.endsWith(".jar") || name.endsWith(".zip"))
        {
            return (true);
        }
        else
        {
            return (false);
        }
    }

} // end JarFilenameFilter
