package Blueprint.Tiles;

import java.awt.Graphics;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Window;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class StartTile extends ExecTile {

	public StartTile() {
		
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        JTextField field = Utils.getTextField(this, "tileSelectorField", "");
        field.setSize(Window.fontMetrics.stringWidth(field.getText()) + 10, 18);
        field.setLocation(Window.instance.menuLocation);
        
        JComboBox<String> combo = Utils.getComboBox(this, "tileSelectorCombo", "");
        combo.setSize(Window.fontMetrics.stringWidth(field.getText()) + 10, 18);
        combo.setLocation(Window.instance.menuLocation);
        combo.setVisible(Window.instance.showMenu);
        if(combo.isShowing() && !field.hasFocus()) {
        	combo.showPopup();
        	field.grabFocus();
        }
        if(Window.instance.showMenu && !field.isVisible()) {
        	field.setText("");
        }
        field.setVisible(Window.instance.showMenu);
    }
	
	public String parse() {
		return "";
	}
    
    @Override
    public void onFocusGained(JComponent comp, String tag) {
    	if(tag.equals("tileSelectorField")) {
    		JTextField field = (JTextField) comp;
    		JComboBox<String> combo = Utils.getComboBox(this, "tileSelectorCombo", "");
    		combo.removeAllItems();
    		combo.hidePopup();
    		for(String s : Main.tileTypes) {
    			if(s.toLowerCase().contains(field.getText().toLowerCase())) {
    				combo.addItem(s);
    			}
    		}
    		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
    		if(combo.isShowing()) {
    			combo.showPopup();
    		}
    	}
    }
    
    @Override
    public void onFocusLost(JComponent comp, String tag) {
    	if(tag.equals("tileSelectorField")) {
    		JComboBox<String> combo = Utils.getComboBox(this, "tileSelectorCombo", "");
    		combo.hidePopup();
    	}
    }
	
	@Override
    public void onActionPerform(JComponent comp, String tag) {
    	super.onActionPerform(comp, tag);
    	
    	if(tag.equals("tileSelectorField")) {
    		JTextField field = (JTextField) comp;
    		
    		if(field.hasFocus()) {
    			JComboBox<String> combo = Utils.getComboBox(this, "tileSelectorCombo", "");
        		combo.removeAllItems();
        		combo.hidePopup();
        		for(String s : Main.tileTypes) {
        			if(s.toLowerCase().contains(field.getText().toLowerCase())) {
        				combo.addItem(s);
        			}
        		}
        		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
        		if(combo.isShowing()) {
        			combo.showPopup();
        		}
    		}
    	}else if(tag.equals("tileSelectorCombo")) {
    		JTextField field = Utils.getTextField(this, "tileSelectorField", "");
    		JComboBox<String> combo = Utils.getComboBox(this, "tileSelectorCombo", "");
    		if(combo.isPopupVisible() && combo.getSelectedItem() != null) {
    			String s = combo.getSelectedItem().toString();
    			try {
					Tile t = (Tile) Class.forName("Blueprint.Tiles." + s).newInstance();
					t.setLocation(Window.instance.menuLocation.x, Window.instance.menuLocation.y);
					Window.instance.showMenu = false;
				}catch(Exception ex) {
					ex.printStackTrace();
				}
    		}
    	}
    }
	
}
