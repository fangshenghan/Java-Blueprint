package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.JarUtils;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.Tile;

public class FieldTile extends Tile {

	private String classType = "";
	private Object value = null;
	private List<Tile> usingTiles = new ArrayList<>();
	
	public FieldTile() {
		
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setUsingTiles(List<Tile> usingTiles) {
		this.usingTiles = usingTiles;
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
	
	public String getTypeName() {
		return this.getClassType();
	}

	public String parse() {
		if(this.getClassType().equals("java.lang.String")) {
			return "\"" + value.toString() + "\"";
		}
		return value.toString();
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        g.setColor(Color.BLACK);
		g.drawString("fieldType", this.getXFromLeft(), this.getYFromBottom(2));
		g.drawString("value", this.getXFromLeft(), this.getYFromBottom(1));
		g.drawString("resultObject", this.getXFromRight("resultObject"), this.getYFromBottom(1));

		JRadioButton radio1 = Utils.getRadio(this, "resultObject");
		radio1.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getUsingTiles().size() > 0);
		
        int widthAdd = 0;
		JTextField comp1 = Utils.getTextField(this, "fieldType_field", Utils.parseClassName(this.getClassType()));
		comp1.setSize(g.getFontMetrics().stringWidth(comp1.getText()) + 10, 18);
		comp1.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("fieldType "), 
				this.getYFromBottom(2) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, comp1.getWidth());
		
		JTextField comp2 = Utils.getTextField(this, "value_field", this.getValue() != null ? this.getValue().toString() : "");
		comp2.setSize(g.getFontMetrics().stringWidth(comp2.getText()) + 10, 18);
		comp2.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("value "), 
				this.getYFromBottom(1) - (int) (comp2.getHeight() * 0.75));
		this.setValue(comp2.getText());
		widthAdd = Math.max(widthAdd, comp2.getWidth());
		
		this.setWidth(Main.nWidth + widthAdd);
    }
	
	public Point getResultPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }
    
    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.startsWith("resultObject")) {
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
    	if(tag.equals("fieldType_field")) {
    		JTextField field = (JTextField) comp;
    		field.setText(this.getClassType());
    	}
    }
    
    @Override
    public void onFocusLost(JComponent comp, String tag) {
    	if(tag.equals("fieldType_field")) {
    		JTextField field = (JTextField) comp;
    		if(JarUtils.classes.containsKey(field.getText())) {
    			this.setClassType(field.getText());
			}else {
				this.setClassType("");
			}
			field.setText(Utils.parseClassName(this.getClassType()));
    	}else if(tag.equals("value_field")) {
    		JTextField field = (JTextField) comp;
			this.setValue(field.getText());
    	}
    }
	
}
