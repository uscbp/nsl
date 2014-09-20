package nslj.src.display;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: jbonaiuto
 * Date: Sep 11, 2007
 * Time: 6:49:20 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class NslInput extends JDialog
{
    protected JTextArea mTextArea;

    public NslInput(NslFrame parent, String name)
    {
        super(parent, name, false);
    }
}
