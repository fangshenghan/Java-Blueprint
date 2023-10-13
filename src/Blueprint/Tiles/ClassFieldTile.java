package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.JarUtils;
import Blueprint.Helper.JarUtils.ClassData;
import Blueprint.Helper.JarUtils.FieldData;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class ClassFieldTile extends ExecTile {

	private String fieldName = "";
	private Tile sourceTile;
	
	public ClassFieldTile() {
		
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		if(this.fieldName != null && this.fieldName.equals(fieldName)) {
			return;
		}
		this.fieldName = fieldName;
	}

	public Tile getSourceTile() {
		return sourceTile;
	}

	public void setSourceTile(Tile sourceTile) {
		this.sourceTile = sourceTile;
	}
	
	public FieldData getFieldData() {
		ClassData cd = null;
		boolean isClass = true;
		if(this.sourceTile instanceof ClassTile) {
			cd = JarUtils.classes.get(this.sourceTile.getAsClassTile().getClassName());
		}else if(this.sourceTile instanceof ObjectTile) {
			isClass = false;
			cd = JarUtils.classes.get(this.sourceTile.getAsObjectTile().getTypeName());
		}
		if(cd == null) {
			return null;
		}
		
		if(cd.getField(this.getFieldName()) != null && cd.getField(this.getFieldName()).isStatic() == isClass) {
			return cd.getField(this.getFieldName());
		}
		return null;
	}

	public String getTypeName() {
		if(this.sourceTile instanceof ClassTile) {
			ClassData cd = JarUtils.classes.get(this.sourceTile.getAsClassTile().getClassName());
			if(cd == null || !cd.getFields().containsKey(this.getFieldName())) {
				return "void";
			}
			return cd.getField(this.getFieldName()).getFieldType();
		}else if(this.sourceTile instanceof ObjectTile) {
			ClassData cd = JarUtils.classes.get(this.sourceTile.getAsObjectTile().getTypeName());
			if(cd == null || !cd.getFields().containsKey(this.getFieldName())) {
				return "void";
			}
			return cd.getField(this.getFieldName()).getFieldType();
		}
		return "void";
	}

	public String parse() {
		String var = this.getTypeName() + " " + this.getTileID() + " = ";
		
		String seg = "";
		if(this.getSourceTile() != null) {
			seg += this.getSourceTile().autoParse();
		}
		
		seg += "." + this.getFieldName() + ";\n";
		return var + seg;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        g.setColor(Color.BLACK);
        Tile source = this.getSourceTile();
        if(source != null) {
    		Point entry = this.getEntrySourcePos();
    		Point exit = new Point(0, 0);
    		if(source instanceof ClassTile) {
    			exit = source.getAsClassTile().getResultPos();
    			source.getAsClassTile().addUsingTiles(this);
    		}else if(source instanceof ObjectTile) {
    			exit = source.getAsObjectTile().getExitPos();
    			source.getAsObjectTile().addUsingTiles(this);
    		}
    		g.drawLine(entry.x, entry.y, exit.x, exit.y);
    	}

		g.drawString("class/object", this.getXFromLeft(), this.getEntrySourcePos().y);
		g.drawString("fieldName", this.getXFromLeft(), this.getEntryFieldPos().y);
		g.drawString("resultObject", this.getXFromRight("resultObject"), this.getResultPos().y);

		JRadioButton radio1 = Utils.getRadio(this, "class/object");
		radio1.setLocation(this.getEntrySourcePos().x, this.getEntrySourcePos().y - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getSourceTile() != null);

		JRadioButton radio2 = Utils.getRadio(this, "resultObject");
		radio2.setLocation(this.getResultPos().x - 20, this.getResultPos().y - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(true);
		
		int widthAdd = 0;
		JTextField comp1 = Utils.getTextField(this, "fieldName", this.getFieldName());
		comp1.setSize(g.getFontMetrics().stringWidth(comp1.getText()) + 10, 18);
		comp1.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("fieldName "), 
				this.getYFromBottom(1) - (int) (comp1.getHeight() * 0.75));
		
		JComboBox<String> combo = Utils.getComboBox(this, "fieldSelector", this.getFieldName());
		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
		combo.setLocation(this.getXFromLeft() + g.getFontMetrics().stringWidth("fieldName "), 
				this.getYFromBottom(1) - (int) (comp1.getHeight() * 0.75));
		widthAdd = Math.max(widthAdd, combo.getWidth());
		
		comp1.setSize(Utils.calculateComboBoxWidth(combo), 18);
		widthAdd = Math.max(widthAdd, comp1.getWidth());
		
		this.setWidth(Main.nWidth + widthAdd);
		
		if(this.getFieldData() == null && this.getFieldName().length() > 0) {
        	this.setFieldName("");
        	comp1.setText("");
        	combo.hidePopup();
        }
		
		if(this.getReceiveObject() == null) {
			ObjectTile ot = this.createReceiveObjectTile();
			this.setReceiveObject(ot);
		}
    }
	
	public Point getEntrySourcePos() {
    	return new Point(this.getPosX(), this.getYFromBottom(2));
    }
	
	public Point getEntryFieldPos() {
    	return new Point(this.getPosX(), this.getYFromBottom(1));
    }
    
    public Point getResultPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }

    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.equals("class/object")) {
        		if(this.getSourceTile() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setSourceTile(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getSourceTile() instanceof ObjectTile) {
            				this.getSourceTile().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getSourceTile() instanceof ClassTile) {
            				this.getSourceTile().getAsClassTile().removeUsingTiles(this);
        				}
            			this.setSourceTile(null);
        			}
        		}
    		}
    	}
    }
    
    @Override
    public void onFocusGained(JComponent comp, String tag) {
    	if(tag.equals("fieldName")) {
    		JTextField field = (JTextField) comp;
    		field.setText(this.getFieldName());
    		
    		JComboBox<String> combo = Utils.getComboBox(this, "fieldSelector", this.getFieldName());
    		ClassData cd = null;
			boolean isClass = true;
			if(this.sourceTile instanceof ClassTile) {
				cd = JarUtils.classes.get(this.sourceTile.getAsClassTile().getClassName());
			}else if(this.sourceTile instanceof ObjectTile) {
				isClass = false;
				cd = JarUtils.classes.get(this.sourceTile.getAsObjectTile().getTypeName());
			}
			if(cd == null) {
				return;
			}
			
    		combo.removeAllItems();
    		combo.hidePopup();
    		for(FieldData fd : cd.getFields().values()) {
    			if(fd.isStatic() == isClass && fd.getFieldName().toLowerCase().contains(field.getText().toLowerCase())) {
    				combo.addItem(fd.getFieldName());
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
    	if(tag.equals("fieldName")) {
    		JTextField field = (JTextField) comp;
    		
    		ClassData cd = null;
			boolean isClass = true;
			if(this.sourceTile instanceof ClassTile) {
				cd = JarUtils.classes.get(this.sourceTile.getAsClassTile().getClassName());
			}else if(this.sourceTile instanceof ObjectTile) {
				isClass = false;
				cd = JarUtils.classes.get(this.sourceTile.getAsObjectTile().getTypeName());
			}
    		if(cd == null) {
    			return;
    		}
    		
    		if(cd.getField(field.getText()) != null && cd.getField(field.getText()).isStatic() == isClass) {
    			this.setFieldName(field.getText());
			}else {
				this.setFieldName("");
			}
			field.setText(Utils.parseClassName(this.getFieldName()));
			
    		JComboBox<String> combo = Utils.getComboBox(this, "fieldSelector", this.getFieldName());
    		combo.hidePopup();
    	}
    }
    
    @Override
    public void onActionPerform(JComponent comp, String tag) {
    	super.onActionPerform(comp, tag);
    	
    	if(tag.equals("fieldName")) {
    		JTextField field = (JTextField) comp;
    		
    		if(field.hasFocus()) {
    			JComboBox<String> combo = Utils.getComboBox(this, "fieldSelector", this.getFieldName());
    			ClassData cd = null;
    			boolean isClass = true;
    			if(this.sourceTile instanceof ClassTile) {
    				cd = JarUtils.classes.get(this.sourceTile.getAsClassTile().getClassName());
    			}else if(this.sourceTile instanceof ObjectTile) {
    				isClass = false;
    				cd = JarUtils.classes.get(this.sourceTile.getAsObjectTile().getTypeName());
    			}
        		if(cd == null) {
        			this.setFieldName("");
        			return;
        		}
    			
        		combo.removeAllItems();
        		combo.hidePopup();
        		for(FieldData fd : cd.getFields().values()) {
        			if(fd.isStatic() == isClass && fd.getFieldName().toLowerCase().contains(field.getText().toLowerCase())) {
        				combo.addItem(fd.getFieldName());
        			}
        		}
        		combo.setSize(Utils.calculateComboBoxWidth(combo), 18);
        		if(combo.isShowing()) {
        			combo.showPopup();
        		}
    		}
    	}else if(tag.equals("fieldSelector")) {
    		JTextField field = Utils.getTextField(this, "fieldName", this.getFieldName());
    		JComboBox<String> combo = Utils.getComboBox(this, "fieldSelector", this.getFieldName());
    		if(combo.isPopupVisible() && combo.getSelectedItem() != null) {
    			field.setText(combo.getSelectedItem().toString());
    			this.setFieldName(combo.getSelectedItem().toString());
    		}
    	}
    }
	
}
