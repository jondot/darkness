# darkness - JRuby with Swing Darkroom-like editor.
# Copyright (C) 2010 Dotan Nahum <dotan@paracode.com>
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
    
require "java"

include_class java.awt.Font
include_class java.awt.Point
include_class java.awt.Color
include_class javax.swing.JFrame
include_class javax.swing.JPanel
include_class javax.swing.JLabel
include_class javax.swing.JTextField
include_class javax.swing.JEditorPane
include_class javax.swing.JScrollPane
include_class javax.swing.JButton
include_class javax.swing.ImageIcon
include_class javax.swing.WindowConstants
include_class javax.swing.JOptionPane
include_class javax.swing.UIManager
include_class javax.swing.SwingUtilities
include_class javax.swing.KeyStroke
include_class javax.swing.BorderFactory
include_class java.awt.event.KeyEvent
include_class java.awt.Event
include_class java.awt.Dimension 
include_class java.awt.FlowLayout
include_class java.awt.GridBagLayout
include_class java.awt.GridBagConstraints
include_class javax.swing.Box
include_class java.awt.GraphicsEnvironment
include_class javax.swing.AbstractAction
include_class java.lang.System
include_class javax.swing.JEditorPane
include_class javax.swing.undo.UndoManager
#
# porting gotchas
#
# variable namings, method namings - use that_what and not thisWay preferrable. easy to get confused.
# include_class
# java primitives from ruby
#
# AbstractAction
#   deriving AbstractAction must call super() and have a ctor (DO NOT CALL super without braces)
#   actionPerformed and NOT action_performed (something is wrong there).
#
module Paracode
  module Darkness
    class BlockAction < AbstractAction

        def self.action(&block)
             block_given? ? BlockAction.new(block)  : BlockAction.new(nil)
        end
        def actionPerformed(event)
            @block.call(event)
        end
        private 
        def initialize(b)
            super()
            @block = b
        end
        
    end

    class UndoableEditorPane < JEditorPane

        
        def initialize
            super()
            @undo = UndoManager.new
            self.document.add_undoable_edit_listener  { |ev| @undo.add_edit(ev.edit) if(ev.respond_to? :edit) }
            self.input_map.put(KeyStroke.getKeyStroke(KeyEvent::VK_Z, Event::CTRL_MASK), BlockAction.action { |e| @undo.undo if @undo.can_undo })
            self.input_map.put(KeyStroke.getKeyStroke(KeyEvent::VK_Y, Event::CTRL_MASK), BlockAction.action { |e| @undo.redo if @undo.can_redo })
        end
    end

    class Editor < JFrame
       
        def initialize( title = nil)
            super( title )
            self.create_components
        end
        
        def set_dark_scheme
            [@frame, @statbar, @backpanel, @textpane, @info_label, @input].each { | c | c.background = Color::BLACK }
            [@textpane, @input].each do |c| 
                c.foreground          = Color.decode("#30b902")
                c.caret_color         = Color.decode("#307202")
                c.selection_color     = Color.decode("#363706")
                c.selected_text_color = Color.decode("#c6e29c")
            end

            @info_label.foreground = Color.decode("#307202")
            @info_label.opaque     = true
            @current_scheme  = 0 ;
        end
        
        def create_components
            @frame      = JFrame.new
            @backpanel  = JPanel.new
            @textpane   = UndoableEditorPane.new
            @jsp        = JScrollPane.new(@textpane)
            @info_label = JLabel.new("Darkness v1.0")
            @input      = JTextField.new
            @statbar    = JPanel.new
            @textfont   = Font.new "Courier New", Font::PLAIN, 18
            @action     = :none
            @current_scheme = 0
            @memoryloss = 0
            @current_file   = ''
            @invisible_cursor = @frame.toolkit.create_custom_cursor ImageIcon.new(''.to_java_bytes).image, Point.new(0,0), "Invisible"
                
            set_dark_scheme

            @textpane.font = @textfont
            @textpane.input_map.put(KeyStroke.get_key_stroke(KeyEvent::VK_ESCAPE,0), BlockAction.action { |e| System.exit 0 } )
            @textpane.input_map.put(KeyStroke.get_key_stroke(KeyEvent::VK_S, Event::CTRL_MASK), 
                                        BlockAction.action do |event|
                                            @input.visible = true
                                            @action = :save
                                            @info_label.text = "Save to:"
                                            @input.requestFocus
                                        end)
            @textpane.input_map.put(KeyStroke.get_key_stroke(KeyEvent::VK_L, Event::CTRL_MASK), 
                                        BlockAction.action do |event|
                                            @input.visible = true
                                            @action = :load
                                            @info_label.text = "Load from:"
                                            @input.requestFocus
                                        end)                                    
            @input.input_map.put(KeyStroke.get_key_stroke(KeyEvent::VK_ESCAPE,0),
                             BlockAction.action do |event|
                                @info_label.text=" "
                                @input.text = ''
                                @input.visible = false
                                @textpane.request_focus
                             end)                           
            
           @textpane.add_key_listener do | event |
               @frame.glass_pane.visible = false if @frame.glass_pane.visible?
               @memoryloss = @memoryloss + 1
               if @memoryloss > 100
                 @info_label.text = '' 
                 @memoryloss = 0
               end
            end
        
            @frame.glass_pane.add_mouse_motion_listener do | event |
               @frame.glass_pane.visible = false if @frame.glass_pane.visible?
            end 
            
            @input.addActionListener do |event|
                if(@action == :save)
                    File.open(@input.text, 'w') do | f |
                        f.write(@textpane.text)
                    end
                    @info_label.text = "Wrote #{@textpane.text.size} chars."
                    @input.visible = false
                    @textpane.request_focus
                else
                   File.open(@input.text, 'r') do |f|
                        @textpane.text = f.read
                   end
                   @info_label.text = "loaded #{@textpane.text.size} chars."
                   @input.visible = false
                   @textpane.request_focus
                end
            end
            
            [@jsp, @input].each{ |c| c.border = BorderFactory.createEmptyBorder }
            [@info_label, @input].each { |c| c.font = @textfont }

            @jsp.maximum_size     = Dimension.new(600, 500)
            @jsp.preferred_size   = Dimension.new(600, 500)
            @input.preferred_size = Dimension.new(150, 21)
            @jsp.vertical_scroll_bar_policy   = JScrollPane::VERTICAL_SCROLLBAR_NEVER
            @jsp.horizontal_scroll_bar_policy =  JScrollPane::HORIZONTAL_SCROLLBAR_NEVER
            @input.visible = false

            @statbar.layout = FlowLayout.new
            @statbar.add @info_label
            @statbar.add @input

            @backpanel.layout = GridBagLayout.new
            c = GridBagConstraints.new
            c.fill    = GridBagConstraints::BOTH
            c.anchor  = GridBagConstraints::CENTER
            c.gridy   = 0
            c.weighty = 0.0
            @backpanel.add Box.create_rigid_area(Dimension.new(50,50)), c 
            
            c.gridy   = 1
            c.weighty = 0.5
            @backpanel.add @jsp, c
            
            c.gridy   = 2
            c.weighty = 0.0
            @backpanel.add Box.create_rigid_area(Dimension.new(50,50)), c 
            
            c.gridy = 3;
            @backpanel.add @statbar, c
            @frame.content_pane.add @backpanel

            @textpane.request_focus
            @frame.glass_pane.cursor = @invisible_cursor

            @frame.undecorated = true
            @frame.default_close_operation = JFrame::EXIT_ON_CLOSE
            GraphicsEnvironment.local_graphics_environment.default_screen_device.full_screen_window = @frame

            @frame.visible = true
        end
    end
  end #darkness
end #paracode
m = Paracode::Darkness::Editor.new( "Darkness" )
