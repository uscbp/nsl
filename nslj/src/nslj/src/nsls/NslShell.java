/* SCCS  @(#)NslShell.java	1.9---09/01/99--00:19:28 */

/* Copyright: 
   Copyright (c) 1998 University of Southern California Brain Project.
   This software may be freely copied provided the top level
   COPYRIGHT file is included with each such copy.
   Emai:l nsl@java.usc.edu.
*/
/**
 @author Nikunj Mehta
 */

package nslj.src.nsls;

import jacl.tcl.lang.Interp;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class NslShell extends JTextArea
{
    String command;
    String currentCommand;
    String prompt = "nsls% ";
    String prompt2 = "> ";
    int beginning = prompt.length();
    int start;
    nslj.src.nsls.NslCommandList commands;

    public NslShell()
    {
        super("", 15, 40);
        setText(prompt);
        command = "";
        currentCommand = "";
        start = beginning;

        addKeyListener(new ShellEventHandler());
        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if(e.getButton()==MouseEvent.BUTTON1)
                {
                    if(getCaretPosition()<start)
                        setCaretPosition(getText().length());
                }
            }
        });
        addFocusListener(new ShellEventHandler());
        commands = new nslj.src.nsls.NslCommandList();
    }

    public void paste(String text)
    {
        char array[] = text.toCharArray();

        int i = 0, len = text.length();
        StringBuilder temp;

        while (i < len)
        {
            temp = new StringBuilder("");
            while (i < len && array[i] != '\n')
            {
                temp.append(array[i]);
                i++;
            }

            append(temp.toString());

            if (i < len && array[i] == '\n')
            {
                dispatchEvent(new KeyEvent(this,
                        KeyEvent.KEY_PRESSED,
                        System.currentTimeMillis(),
                        Event.KEY_PRESS, KeyEvent.VK_ENTER, '\n'));
            }

            i++;
            if (i < len && array[i] == '\r')
            {
                i++;
            }
        }
    }

    public void clear()
    {
        setText(prompt);
        command = "";
        currentCommand = "";
        start = beginning;
    }
    
    public void nslPrintln(String msg)
    {
        // check if the last element is a prompt
        // if it is clear it an paint it at the end
        String temp = "";
        int len = getText().length();
        if (start != len)
        {
            temp = getText().substring(start, len);
        }
        int tempLen = temp.length();
        String command = msg + '\n' + prompt + temp;
        replaceRange(command, len - 6 - tempLen, len);
        start = getText().length() - tempLen;
        setCaretPosition(start + tempLen);
    }

    public void nslPrint(String msg)
    {
        String temp = "";
        int len = getText().length();
        if (start != len)
        {
            temp = getText().substring(start, len);
        }
        int tempLen = temp.length();
        String command = msg + prompt + temp;
        replaceRange(command, len - 6 - temp.length(), len);
        start = getText().length() - tempLen;
        setCaretPosition(start + tempLen);
    }

    private class ShellEventHandler extends KeyAdapter implements FocusListener
    {
        String result;

        public void keyPressed(KeyEvent evt)
        {
            int mod = evt.getModifiers();
            if (mod == InputEvent.ALT_MASK || mod == InputEvent.CTRL_MASK || mod == InputEvent.META_DOWN_MASK)
            {
                evt.consume();
            }

            int evtCode = evt.getKeyCode();
            if (evtCode == KeyEvent.VK_ENTER)
            {
                command = getText().substring(start);
                currentCommand = currentCommand + command;
                if (Interp.commandComplete(currentCommand))
                {

                    append('\n' + prompt);
                    start = getText().length();
                    setCaretPosition(start);
                    result = nslj.src.nsls.Executive.execute_line(currentCommand);
                    if (result.length() != 0)
                    {
                        nslPrintln(result);
                    }

                    if (currentCommand.length() != 0)
                    {
                        commands.add(currentCommand);
                    }
                    currentCommand = "";

                }
                else
                {
                    append('\n' + prompt2);
                    currentCommand = currentCommand + '\n';
                }
                start = getText().length();
                setCaretPosition(start);
                evt.consume();
            }
            else if (evtCode == KeyEvent.VK_BACK_SPACE || evtCode == KeyEvent.VK_LEFT)
            {
                if (getCaretPosition() < start + 1)
                {
                    evt.consume();
                }
            }
            else if (evtCode == KeyEvent.VK_UP)
            {
                command = commands.prev();
                int len = getText().length();
                replaceRange(command, start, len);
                setCaretPosition(start);
                evt.consume();
            }
            else if (evtCode == KeyEvent.VK_DOWN)
            {
                command = commands.next();
                setText(getText().substring(0, start).concat(command));
                setCaretPosition(start);
                evt.consume();
            }
            else if (evtCode == KeyEvent.VK_PAGE_UP || evtCode == KeyEvent.VK_PAGE_DOWN)
            {
                evt.consume();
            }
            else if (evtCode == KeyEvent.VK_HOME)
            {
                setCaretPosition(start);
                evt.consume();
            }
            else if (evtCode == KeyEvent.VK_RIGHT)
            {
            }
            else if (getCaretPosition()<start)
                setCaretPosition(getText().length());
        }

        public void focusGained(FocusEvent e)
        {
            setCaretPosition(getText().length());
        }

        public void focusLost(FocusEvent e)
        {}
    }
}
