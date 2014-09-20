package jacl.tcl.lang;

/**
 * The purpose of AutoloadStub is to load-on-demand the classes that
 * implement Tcl commands. This reduces Jacl start up time and, when
 * running Jacl off a web page, reduces download time significantly.
 */

class AutoloadStub implements Command {
    String className;

    /**
     * Create a stub command which autoloads the real command the first time
     * the stub command is invoked.
     *
     * @param clsName name of the Java class that implements this command,
     *                e.g. "tcl.lang.AfterCmd"
     */
    AutoloadStub(String clsName)
    {
        className = clsName;
    }

    /**
     * Load the class that implements the given command and execute it.
     *
     * @param interp the current interpreter.
     * @param argv   command arguments.
     * @throws jacl.tcl.lang.TclException if error happens inside the real command proc.
     */
    public void cmdProc(Interp interp, TclObject argv[]) throws TclException {
        Class cmdClass;
        Command cmd;
        try
        {
            cmdClass = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new TclException(interp,
                    "ClassNotFoundException for class \"" + className + "\"");
        }

        try
        {
            cmd = (Command) cmdClass.newInstance();
        }
        catch (IllegalAccessException e1)
        {
            throw new TclException(interp,
                    "IllegalAccessException for class \"" + cmdClass.getName()
                            + "\"");
        }
        catch (InstantiationException e2)
        {
            throw new TclException(interp,
                    "InstantiationException for class \"" + cmdClass.getName()
                            +
                            "\"");
        }
        catch (ClassCastException e3)
        {
            throw new TclException(interp,
                    "ClassCastException for class \"" + cmdClass.getName()
                            + "\"");
        }
        interp.createCommand(argv[0].toString(), cmd);
        cmd.cmdProc(interp, argv);
    }
}
