package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.JarUtils;
import Blueprint.Helper.JarUtils.ClassData;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.Tile;

public class ClassTile extends Tile {

	private String className = "";
	private List<Tile> usingTiles = new ArrayList<>();
	
	public ClassTile() {
		
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String parse() {
		return this.className;
	}
	
	public List<Tile> getUsingTiles() {
		return usingTiles;
	}

	public void addUsingTiles(Tile tile) {
		if(this.usingTiles.contains(tile)) {
			return;
		}
		this.usingTiles.add(tile);
	}
	
	public void removeUsingTiles(Tile tile) {
		this.usingTiles.remove(tile);
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        g.setColor(Color.BLACK);
		g.drawString("className", this.getXFromLeft(), this.getResultPos().y);

		int widthAdd = 0;
		
		JTextField comp1 = Utils.getTextField(this, "className", Utils.parseClassName(this.getClassName()));
		comp1.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("className "), 
				this.getYFromBottom(1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(comp1.getWidth(), widthAdd);

		JComboBox<String> combo = Utils.getComboBox(this, "classSelector", this.getClassName());
		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
		combo.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("className "), 
				this.getYFromBottom(1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, combo.getWidth());
		
		comp1.setSize(Utils.calculateComboBoxWidth(combo), 18);
		
		this.setWidth(Main.nWidth + widthAdd);

		g.drawString("resultClass", this.getXFromRight("resultClass"), this.getResultPos().y);

		JRadioButton radio1 = Utils.getRadio(this, "resultClass");
		radio1.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getUsingTiles().size() > 0);
    }
	
	public Point getResultPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }
    
    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.startsWith("resultClass")) {
        		if(lastSelection != null) {
        			if(!lastSelection.getTile().equals(this)) {
        				lastSelection.getTile().onClick(
            					lastSelection.getComponent(), lastSelection.getTag(), 
            					new Selection(this, comp, tag));
        			}
        		}
        	}
    	}
    }
    
    @Override
    public void onFocusGained(JComponent comp, String tag) {
    	if(tag.equals("className")) {
    		JTextField field = (JTextField) comp;
    		field.setText(this.getClassName());
    		
    		JComboBox<String> combo = Utils.getComboBox(this, "classSelector", this.getClassName());
    		combo.removeAllItems();
    		combo.hidePopup();
    		for(ClassData cd : JarUtils.classes.values()) {
    			if(cd.getClassName().contains(field.getText())) {
    				combo.addItem(cd.getClassName());
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
    	if(tag.equals("className")) {
    		JTextField field = (JTextField) comp;
    		if(JarUtils.classes.containsKey(field.getText())) {
    			this.setClassName(field.getText());
			}else {
				this.setClassName("");
			}
			field.setText(Utils.parseClassName(this.getClassName()));
			
    		JComboBox<String> combo = Utils.getComboBox(this, "classSelector", this.getClassName());
    		combo.hidePopup();
    	}
    }
    
    @Override
    public void onActionPerform(JComponent comp, String tag) {
    	super.onActionPerform(comp, tag);
    	
    	if(tag.equals("className")) {
    		JTextField field = (JTextField) comp;
    		
    		if(field.hasFocus()) {
    			JComboBox<String> combo = Utils.getComboBox(this, "classSelector", this.getClassName());
        		combo.removeAllItems();
        		combo.hidePopup();
        		for(ClassData cd : JarUtils.classes.values()) {
        			if(cd.getClassName().toLowerCase().contains(field.getText().toLowerCase())) {
        				combo.addItem(cd.getClassName());
        			}
        		}
        		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
        		if(combo.isShowing()) {
        			combo.showPopup();
        		}
    		}
    	}else if(tag.equals("classSelector")) {
    		JTextField field = Utils.getTextField(this, "className", this.getClassName());
    		JComboBox<String> combo = Utils.getComboBox(this, "classSelector", this.getClassName());
    		if(combo.isPopupVisible() && combo.getSelectedItem() != null) {
    			field.setText(combo.getSelectedItem().toString());
    			this.setClassName(combo.getSelectedItem().toString());
    		}
    	}
    }
	
}
