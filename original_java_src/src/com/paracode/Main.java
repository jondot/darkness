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

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main
{
    final JFrame frame = new JFrame();
    JPanel backpanel = new JPanel();
    JEditorPane textpane = new CustomJEditorPane();
    JScrollPane jsp = new JScrollPane(textpane);
    JLabel infolabel = new JLabel("jdarkroom v1.0");
    JTextField input = new JTextField();
    JPanel statbar = new JPanel();
    Font textfont = new Font("Courier New", Font.PLAIN, 18);
    private String action="";

    private int currentScheme=0;
    private String currentfile="";


    private final Cursor _invisibleCursor = frame.getToolkit().createCustomCursor( new ImageIcon(new byte[0]).getImage(), new Point(0,0), "Invisible");

    //todo: implement periodic backup
    //todo: implement transparency
    //todo: implement configuration form for colors etc., configuration loader/saver
    //todo: word statistics
    //todo: better layout
    //todo: put schemes in a vector, each scheme is a hashmap, make an installer function
    //todo: implement CVS save
    //todo: save to html, text needs to be that forum coded thingie, textile ?
    //todo: classloader export text filter interface
    public void schemeDarkRoom()
    {
        frame.setBackground(Color.BLACK);
        statbar.setBackground(Color.BLACK);
        backpanel.setBackground(Color.BLACK);
        textpane.setBackground(Color.BLACK);
        textpane.setForeground(Color.decode("#30b902"));
        textpane.setCaretColor(Color.decode("#307202"));
        textpane.setSelectionColor(Color.decode("#363706"));
        textpane.setSelectedTextColor(Color.decode("#c6e29c"));
        infolabel.setBackground(Color.BLACK);
        infolabel.setForeground(Color.decode("#307202"));
       // textpane.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(false));
        infolabel.setOpaque(true);
       // infolabel.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(false));
        input.setBackground(Color.BLACK);
        input.setForeground(Color.decode("#30b902"));
        input.setCaretColor(Color.decode("#307202"));
        input.setSelectionColor(Color.decode("#363706"));
        input.setSelectedTextColor(Color.decode("#c6e29c"));
     //   input.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(false));
        currentScheme  =0 ;
    }
    public void schemeWhiteRoom()
    {
        frame.setBackground(Color.WHITE);
        statbar.setBackground(Color.WHITE);
        backpanel.setBackground(Color.WHITE);
        textpane.setForeground(Color.BLACK);
        textpane.setBackground(Color.WHITE);
        textpane.setCaretColor(Color.BLACK);
        textpane.setSelectionColor(Color.decode("#d8d8d8"));
        textpane.setSelectedTextColor(Color.BLACK);
       // textpane.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));
        infolabel.setBackground(Color.WHITE);
        infolabel.setForeground(Color.BLACK);
        //infolabel.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));
        infolabel.setOpaque(true);
        input.setBackground(Color.WHITE);
        input.setForeground(Color.BLACK);
        input.setCaretColor(Color.BLACK);
        input.setSelectionColor(Color.BLACK);
        input.setSelectedTextColor(Color.BLACK);
      //  input.putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, new Boolean(true));
        currentScheme =1;
    }


    public Main()
    {
        // textpane
        //
        
        
        
        textpane.setFont(textfont);
        textpane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), new ExitAction());
        textpane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), new ShowSaveAction());
        textpane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK), new ShowLoadAction());
        textpane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.ALT_MASK), new RotateSchemesAction());
        textpane.addKeyListener(new KeyListener(){

            public void keyPressed(KeyEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            public void keyReleased(KeyEvent arg0)
            {
                // TODO Auto-generated method stub

            }

            public void keyTyped(KeyEvent arg0)
            {
                if(!frame.getGlassPane().isVisible()) 
                    frame.getGlassPane().setVisible(true);
            }

        });


        frame.getGlassPane().addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent arg0){}
            public void mouseMoved(MouseEvent arg0)
            {
                if(frame.getGlassPane().isVisible()) 
                    frame.getGlassPane().setVisible(false);
            } });


        //scroller
        //
        jsp.setMaximumSize(new Dimension(600, 500));
        jsp.setPreferredSize(new Dimension(600, 500));
        jsp.setBorder(BorderFactory.createEmptyBorder());
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);




        //info label
        //
        infolabel.setFont(textfont);




        //input field
        //
        input.setBorder(BorderFactory.createEmptyBorder());
        input.setFont(textfont);
        input.setPreferredSize(new Dimension(150,21));
        input.addActionListener(new FileAction());
        input.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), new ResumeEditAction());





        // statbar
        //
        statbar.setLayout(new FlowLayout());
        statbar.add(infolabel);
        statbar.add(input);
        input.setVisible(false);





        // layout
        //
        backpanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 0;
        c.weighty = 0.0;
        backpanel.add(Box.createRigidArea(new Dimension(50,50)),c );
        
        c.gridy = 1;
        c.weighty = 0.5;
        backpanel.add(jsp, c);
        c.gridy = 2;
        c.weighty = 0.0;
        backpanel.add(Box.createRigidArea(new Dimension(50,50)),c );
        c.gridy = 3;
        backpanel.add(statbar, c);
        frame.getContentPane().add(backpanel);





        // visual setup
        //
        schemeDarkRoom();
        textpane.requestFocus();
        frame.getGlassPane().setCursor(_invisibleCursor);




        // frame
        //
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);

        frame.setVisible(true);
    }





    //
    //  Actions
    //

    class ExitAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent arg0)
        {
            System.exit(0);
        }
    }
    class ShowSaveAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            input.setVisible(true);
            action = "save";
            infolabel.setText("Save to:");
            input.requestFocus();
        }
    }
    class ShowLoadAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            input.setVisible(true);
            action = "load";
            infolabel.setText("Load from:");
            input.requestFocus();
        }
    }
    //
    class FileAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            
            if(action.equals("save"))
            {
                try
                {
                    
                        
                    FileWriter fw = new FileWriter(input.getText());
                    fw.write(textpane.getText());
                    fw.flush();
                    fw.close();
                    infolabel.setText("Wrote "+textpane.getText().length()+" chars.");
                    input.setVisible(false);
                    textpane.requestFocus();
    
                }
                catch(Exception ex)
                {
                    infolabel.setText("Cannot save: "+ ex.getMessage());
                }
            }
            else
            {
                try
                {
                    
                    BufferedReader br = new BufferedReader(new FileReader(input.getText()));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    
                    while((line = br.readLine()) != null)
                    {
                        sb.append(line+"\n");
                    }
                    br.close();
                    textpane.setText(sb.toString());
                    infolabel.setText("Loaded "+textpane.getText().length()+" chars.");
                    input.setVisible(false);
                    textpane.requestFocus();

                }
                catch(Exception ex)
                {
                    infolabel.setText("Cannot load: "+ ex.getMessage());
                }
            }
        }
    }
    

    
    class RotateSchemesAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            if(currentScheme == 0)
                schemeWhiteRoom();
            else if(currentScheme == 1)
                schemeDarkRoom();
        }
    }
    class ResumeEditAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;
        public void actionPerformed(ActionEvent e)
        {
            infolabel.setText(" ");
            input.setText("");
            input.setVisible(false);
            textpane.requestFocus();
        }
    }


    public static void main(String argsp[])
    {
        new Main();
    }
}
