/*
# Swing Demo - Darkroom-like editor.
# Copyright (C) 2006-2010 Dotan Nahum <dotan@paracode.com>
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.paracode;

import java.awt.Event;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

public class CustomJEditorPane extends JEditorPane
{
    private static final long serialVersionUID = 1L;
    UndoManager undo = new UndoManager();

    public CustomJEditorPane()
    {
        this.getDocument()
                .addUndoableEditListener(new MyUndoableEditListener());
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK), new UndoAction());
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK), new RedoAction());
        
    }

    class MyUndoableEditListener implements UndoableEditListener
    {
        public void undoableEditHappened(UndoableEditEvent e)
        {
            // Remember the edit and update the menus
            undo.addEdit(e.getEdit());
        }
    }
 
    
    class UndoAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                if(undo.canUndo())
                    undo.undo();
            } catch (CannotUndoException ex)
            {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }

        }

    }
    class RedoAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                if(undo.canRedo())
                    undo.redo();
            } catch (CannotRedoException ex)
            {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }

        }
    }
}
