package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.JarUtils;
import Blueprint.Helper.JarUtils.ClassData;
import Blueprint.Helper.JarUtils.MethodData;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class MethodTile extends ExecTile {

	private String methodName = "";
	private Tile sourceObject;
	private HashMap<Integer, Tile> parameters = new HashMap<>();
	
	private MethodData selectedMethod = null;
	
	public MethodTile() {
		
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		if(this.methodName != null && this.methodName.equals(methodName)) {
			return;
		}
		this.methodName = methodName;
		selectedMethod = this.getMethodData();
		this.resetParameters();
	}
	
	public MethodData getMethodData() {
		ClassData cd = null;
		if(this.sourceObject instanceof ClassTile) {
			cd = JarUtils.classes.get(this.sourceObject.getAsClassTile().getClassName());
		}else if(this.sourceObject instanceof ObjectTile) {
			cd = JarUtils.classes.get(this.sourceObject.getAsObjectTile().getTypeName());
		}
		if(cd == null) {
			return null;
		}
		return cd.getMethod(this.methodName);
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
		
		if(selectedMethod != null) {
			int size = selectedMethod.getParameters().size();
			for(int i = 1;i <= size;i++) {
				this.parameters.put(i, null);
			}
		}
	}

	public String getReturnType() {
		ClassData cd = null;
		if(this.sourceObject instanceof ClassTile) {
			cd = JarUtils.classes.get(this.sourceObject.getAsClassTile().getClassName());
		}else if(this.sourceObject instanceof ObjectTile) {
			cd = JarUtils.classes.get(this.sourceObject.getAsObjectTile().getTypeName());
		}
		if(cd != null && cd.getMethod(this.methodName) != null) {
			return cd.getMethod(this.methodName).getReturnType();
		}
		return "void";
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

	public Tile getSourceObject() {
		return sourceObject;
	}

	public void setSourceObject(Tile sourceObject) {
		this.sourceObject = sourceObject;
	}
	
	public String parse() {
		String var = "";
		if(!this.getReturnType().equals("void")) {
			var += this.getReturnType() + " " + this.getTileID() + " = ";
		}
		
		String seg = "";
		if(this.getSourceObject() != null) {
			seg += this.getSourceObject().autoParse();
		}
		
		seg += "." + this.getMethodName().substring(0, this.getMethodName().indexOf("(")) + "(";
		
		for(Entry<Integer, Tile> entry : this.getParameters().entrySet()) {
			if(entry.getKey() > 1) {
				seg += ", ";
			}
			
			seg += entry.getValue().autoParse();
		}
		
		seg += ");\n";
		
		return var + seg;
	}
	
	@Override
	public void draw(Graphics g) {
		this.setHeight(Main.nHeight + this.parameters.size() * 20);
		
        super.draw(g);
        
        if(this.getSourceObject() != null) {
        	g.setColor(Color.BLACK);
            Point entry1 = this.getSourceEntryPos();
    		Point exit1 = new Point(0, 0);
    		if(this.getSourceObject() instanceof ObjectTile) {
    			exit1 = this.getSourceObject().getAsObjectTile().getExitPos();
    			this.getSourceObject().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getSourceObject() instanceof ClassTile) {
    			exit1 = this.getSourceObject().getAsClassTile().getResultPos();
    			this.getSourceObject().getAsClassTile().addUsingTiles(this);
    		}
    		g.drawLine(entry1.x, entry1.y, exit1.x, exit1.y);
        }
        
        for(Entry<Integer, Tile> en : this.parameters.entrySet()) {
        	if(en.getValue() != null) {
        		Point entry2 = this.getParameterEntryPos(en.getKey());
        		Point exit2 = new Point(0, 0);
        		if(en.getValue() instanceof FieldTile) {
        			exit2 = en.getValue().getAsFieldTile().getResultPos();
        		}else if(en.getValue() instanceof ObjectTile) {
        			exit2 = en.getValue().getAsObjectTile().getExitPos();
        		}
        		g.drawLine(entry2.x, entry2.y, exit2.x, exit2.y);
        	}
        }

		g.drawString("sourceObject", this.getXFromLeft(), this.getYFromBottom(this.parameters.size() + 2));
		JRadioButton radio = Utils.getRadio(this, "sourceObject");
		radio.setLocation(this.getPosX(), this.getYFromBottom(this.parameters.size() + 2) - (int) (radio.getHeight() * 0.75));
		radio.setSelected(this.getSourceObject() != null);
		
		g.drawString("methodName", this.getXFromLeft(), this.getYFromBottom(this.parameters.size() + 1));
		int widthAdd = 0;
		JTextField comp1 = Utils.getTextField(this, "methodName", this.getMethodName());
		comp1.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("methodName "), 
				this.getYFromBottom(this.parameters.size() + 1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, comp1.getWidth());

		JComboBox<String> combo = Utils.getComboBox(this, "methodSelector", this.getMethodName());
		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
		combo.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("methodName "), 
				this.getYFromBottom(this.parameters.size() + 1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, combo.getWidth());
		
		comp1.setSize(Utils.calculateComboBoxWidth(combo), 18);
		
		this.setWidth(Main.nWidth + widthAdd);
		
		if(this.getMethodData() == null && this.getMethodName().length() > 0) {
        	this.setMethodName("");
        	comp1.setText("");
        	combo.hidePopup();
        }
		
		for(Entry<Integer, Tile> en : this.parameters.entrySet()) {
			String s = selectedMethod != null ? Utils.parseClassName(selectedMethod.getParameters().get(en.getKey() - 1).getParameterType()) : "value";
			g.drawString(en.getKey() + ") " + s, 
					this.getXFromLeft(), this.getYFromBottom(this.parameters.size() - en.getKey() + 1));
			JRadioButton radio1 = Utils.getRadio(this, "parameter_" + en.getKey());
			radio1.setLocation(this.getPosX(), this.getYFromBottom(this.parameters.size() - en.getKey() + 1) - (int) (radio1.getHeight() * 0.75));
			radio1.setSelected(en.getValue() != null);
			radio1.setVisible(true);
			if(en.getValue() != null) {
				if(en.getValue() instanceof ObjectTile) {
					en.getValue().getAsObjectTile().addUsingTiles(this);
				}else if(en.getValue() instanceof FieldTile) {
					en.getValue().getAsFieldTile().addUsingTiles(this);
				}
			}
        }
		
		JRadioButton radio2 = Utils.getRadio(this, "returnObject");
		radio2.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(true);
		
		if(this.getReturnType().equals("void")) {
			g.drawString("noReturn", this.getXFromRight("noReturn"), this.getYFromBottom(1));
			radio2.setVisible(false);
			if(this.getReceiveObject() != null) {
				this.getReceiveObject().remove(false);
			}
		}else {
			g.drawString("returnObject", this.getXFromRight("returnObject"), this.getYFromBottom(1));
			radio2.setVisible(true);
			if(this.getReceiveObject() == null) {
				ObjectTile ot = this.createReceiveObjectTile();
				this.setReceiveObject(ot);
			}
		}
    }
	
	public Point getParameterEntryPos(int id) {
    	return new Point(this.getPosX(), this.getYFromBottom(this.parameters.size() - id + 1));
    }
	
    public Point getReturnPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }
	
	public Point getSourceEntryPos() {
    	return new Point(this.getPosX(), this.getYFromBottom(this.parameters.size() + 2));
    }

    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.equals("sourceObject")) {
        		if(this.getSourceObject() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setSourceObject(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getSourceObject() instanceof ObjectTile) {
            				this.getSourceObject().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getSourceObject() instanceof ClassTile) {
            				this.getSourceObject().getAsClassTile().removeUsingTiles(this);
        				}
            			this.setSourceObject(null);
        			}
        		}
    		}else if(tag.startsWith("parameter_")) {
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
    	if(tag.equals("methodName")) {
    		JTextField field = (JTextField) comp;
    		field.setText(this.getMethodName());
    		
    		ClassData cd = null;
    		if(this.sourceObject instanceof ClassTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsClassTile().getClassName());
    		}else if(this.sourceObject instanceof ObjectTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsObjectTile().getTypeName());
    		}else {
    			return;
    		}
    		JComboBox<String> combo = Utils.getComboBox(this, "methodSelector", this.getMethodName());
    		combo.removeAllItems();
    		combo.hidePopup();
    		for(MethodData md : cd.getMethods().values()) {
    			if(md.getMethodName().contains(field.getText())) {
    				combo.addItem(md.getMethodName());
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
    	if(tag.equals("methodName")) {
    		JTextField field = (JTextField) comp;
    		ClassData cd = null;
    		if(this.sourceObject instanceof ClassTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsClassTile().getClassName());
    		}else if(this.sourceObject instanceof ObjectTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsObjectTile().getTypeName());
    		}else {
    			return;
    		}
    		if(cd.getMethods().containsKey(field.getText())) {
    			this.setMethodName(field.getText());
			}else {
				this.setMethodName("");
			}
			field.setText(Utils.parseClassName(this.getMethodName()));
			
    		JComboBox<String> combo = Utils.getComboBox(this, "methodSelector", this.getMethodName());
    		combo.hidePopup();
    	}
    }
    
    @Override
    public void onActionPerform(JComponent comp, String tag) {
    	super.onActionPerform(comp, tag);
    	
    	if(tag.equals("methodName")) {
    		JTextField field = (JTextField) comp;
    		ClassData cd = null;
    		if(this.sourceObject instanceof ClassTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsClassTile().getClassName());
    		}else if(this.sourceObject instanceof ObjectTile) {
    			cd = JarUtils.classes.get(this.sourceObject.getAsObjectTile().getTypeName());
    		}
    		if(cd == null) {
    			return;
    		}
    		
    		JComboBox<String> combo = Utils.getComboBox(this, "methodSelector", this.getMethodName());
    		combo.removeAllItems();
    		combo.hidePopup();
    		for(MethodData md : cd.getMethods().values()) {
    			if(md.getMethodName().toLowerCase().contains(field.getText().toLowerCase())) {
    				combo.addItem(md.getMethodName());
    			}
    		}
    		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
    		if(combo.isShowing()) {
    			combo.showPopup();
    		}
    	}else if(tag.equals("methodSelector")) {
    		JTextField field = Utils.getTextField(this, "methodName", this.getMethodName());
    		JComboBox<String> combo = Utils.getComboBox(this, "methodSelector", this.getMethodName());
    		if(combo.isPopupVisible() && combo.getSelectedItem() != null) {
    			field.setText(combo.getSelectedItem().toString());
    			this.setMethodName(combo.getSelectedItem().toString());
    		}
    	}
    }
	
}
