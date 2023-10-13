package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.JarUtils;
import Blueprint.Helper.JarUtils.ClassData;
import Blueprint.Helper.JarUtils.ConstructorData;
import Blueprint.Helper.JarUtils.ParameterData;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class ConstructorTile extends ExecTile {
	
	private String className = "";
	private HashMap<Integer, Tile> parameters = new HashMap<>();
	
	private HashMap<String, ConstructorData> constructors = new HashMap<String, ConstructorData>();
	private int selectedConstructorID = 0;
	
	public ConstructorTile() {
		
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		if(this.className != null && this.className.equals(className)) {
			return;
		}
		this.className = className;
		
		JComboBox<String> combo = Utils.getComboBox(this, "constructor", null);
		combo.removeAllItems();
		
		ClassData cd = JarUtils.classes.get(this.getClassName());
		if(cd == null) {
			return;
		}
		
		constructors = cd.getConstructors();
		this.resetParameters();
		if(constructors.size() == 0) {
			this.className = "";
			return;
		}
		constructors.values().forEach(c -> combo.addItem(c.toString()));
	}
	
	public HashMap<Integer, Tile> getParameters() {
		return parameters;
	}
	
	public Tile getParameter(int parameterID) {
		return this.parameters.get(parameterID);
	}
	
	public void setParameter(int parameterID, Tile tile) {
		this.parameters.put(parameterID, tile);
	}
	
	public void resetParameters() {
		for(Tile t : this.parameters.values()) {
			if(t != null) {
				t.removeReferences(this);
			}
		}
		for(Entry<String, JComponent> entry : this.getComponents().entrySet()) {
			if(entry.getValue() != null && entry.getKey().startsWith("parameter_")) {
				entry.getValue().setVisible(false);
			}
		}
		this.parameters.clear();
		
		List<ConstructorData> cons = new ArrayList<>(constructors.values());
		if(cons.size() > selectedConstructorID && selectedConstructorID > -1) {
			int size = cons.get(selectedConstructorID).getParameters().size();
			for(int i = 1;i <= size;i++) {
				this.parameters.put(i, null);
			}
		}
	}
	
	public String parse() {
		String var = this.getClassName() + " " + this.getTileID() + " = ";
		
		String seg = "new " + this.getClassName() + "(";
		
		for(Entry<Integer, Tile> entry : this.getParameters().entrySet()) {
			Tile pt = entry.getValue();
			if(entry.getKey() > 1) {
				seg += ", ";
			}
			
			if(pt instanceof FieldTile) {
				seg += pt.getAsFieldTile().parse();
			}else if(pt instanceof ObjectTile) {
				seg += pt.getAsObjectTile().parse();
			}
		}
		
		seg += ");";
		
		return var + seg + "\n";
	}
	
	@Override
	public void draw(Graphics g) {
		this.setHeight(Main.nHeight + this.parameters.size() * 20);
		
        super.draw(g);
        
        int widthAdd = 0;
        
        g.setColor(Color.BLACK);
        for(Entry<Integer, Tile> en : this.parameters.entrySet()) {
        	if(en.getValue() == null) {
        		continue;
        	}
        	Point entry = this.getParameterEntryPos(en.getKey());
    		Point exit = new Point(0, 0);
    		if(en.getValue() instanceof FieldTile) {
    			exit = en.getValue().getAsFieldTile().getResultPos();
    			en.getValue().getAsFieldTile().addUsingTiles(this);
    		}else if(en.getValue() instanceof ObjectTile) {
    			exit = en.getValue().getAsObjectTile().getExitPos();
    			en.getValue().getAsObjectTile().addUsingTiles(this);
    		}
    		g.drawLine(entry.x, entry.y, exit.x, exit.y);
        }
        
		g.drawString("className", this.getXFromLeft(), this.getYFromBottom(this.parameters.size() + 2));
		JTextField comp1 = Utils.getTextField(this, "className", Utils.parseClassName(this.getClassName()));
		comp1.setSize(g.getFontMetrics().stringWidth(comp1.getText()) + 10, 18);
		comp1.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("className "), 
				this.getYFromBottom(this.parameters.size() + 2) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, comp1.getWidth());

		JComboBox<String> combo0 = Utils.getComboBox(this, "classSelector", this.getClassName());
		combo0.setSize(Utils.calculateComboBoxWidth(combo0), 18);
		combo0.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("className "), 
				this.getYFromBottom(this.parameters.size() + 2) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, combo0.getWidth());
		
		comp1.setSize(Utils.calculateComboBoxWidth(combo0), 18);
		widthAdd = Math.max(widthAdd, comp1.getWidth());
		
		g.drawString("constructor", this.getXFromLeft(), this.getYFromBottom(this.parameters.size() + 1));
		JComboBox<String> combo = Utils.getComboBox(this, "constructor", null);
		combo.setSize(g.getFontMetrics().stringWidth(combo.getSelectedItem() == null ? "" : combo.getSelectedItem().toString()) + 10, 18);
		combo.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("constructor "), 
				this.getYFromBottom(this.parameters.size() + 1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, combo.getWidth());
		this.setWidth(Main.nWidth + widthAdd);
		
		List<ConstructorData> cons = new ArrayList<>(constructors.values());
		if(cons.size() > selectedConstructorID && selectedConstructorID > -1) {
			List<ParameterData> consPara = cons.get(selectedConstructorID).getParameters();
			for(Entry<Integer, Tile> en : this.parameters.entrySet()) {
				g.drawString(en.getKey() + ") " + Utils.parseClassName(consPara.get(en.getKey() - 1).getParameterType()), 
						this.getXFromLeft(), this.getYFromBottom(this.parameters.size() - en.getKey() + 1));
				JRadioButton radio1 = Utils.getRadio(this, "parameter_" + en.getKey());
				radio1.setLocation(this.getPosX(), this.getYFromBottom(this.parameters.size() - en.getKey() + 1) - (int) (radio1.getHeight() * 0.75));
				radio1.setSelected(en.getValue() != null);
				radio1.setVisible(true);
	        }
		}
		
		g.drawString("resultObject", this.getXFromRight("resultObject"), this.getYFromBottom(1));
		JRadioButton radio2 = Utils.getRadio(this, "resultObject");
		radio2.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(true);
		
		if(this.getReceiveObject() == null) {
			ObjectTile ot = this.createReceiveObjectTile();
			this.setReceiveObject(ot);
		}
    }
	
	public Point getParameterEntryPos(int id) {
    	return new Point(this.getPosX(), this.getYFromBottom(this.parameters.size() - id + 1));
    }
	
    public Point getResultPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }
    
    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.startsWith("parameter_")) {
        		int id = Integer.valueOf(tag.substring(10));
        		if(this.getParameter(id) == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				if(lastSelection.getTile() instanceof ObjectTile) {
        					this.setParameter(id, lastSelection.getTile());
        				}else if(lastSelection.getTile() instanceof FieldTile) {
        					this.setParameter(id, lastSelection.getTile());
        				}
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getParameter(id) instanceof ObjectTile) {
            				this.getParameter(id).getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getParameter(id) instanceof FieldTile) {
            				this.getParameter(id).getAsFieldTile().removeUsingTiles(this);
        				}
    					this.setParameter(id, null);
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
    	
    	if(tag.equals("constructor")) {
    		JComboBox<String> combo = (JComboBox<String>) comp;
    		if(selectedConstructorID != combo.getSelectedIndex()) {
    			selectedConstructorID = combo.getSelectedIndex();
        		this.resetParameters();
    		}
    	}else if(tag.equals("className")) {
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
