package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Window;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class MathTile extends ExecTile {
	
	public static enum MathType {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD;
	}
	
	private MathType mathType = MathType.ADD;
	private Tile firstObject, secondObject;
	
	private ObjectTile receiveObject;
	
	public MathTile() {
		
	}

	public MathType getMathType() {
		return mathType;
	}

	public void setMathType(MathType mathType) {
		this.mathType = mathType;
	}
	
	public Tile getFirstObject() {
		return firstObject;
	}

	public void setFirstObject(Tile firstObject) {
		this.firstObject = firstObject;
	}

	public Tile getSecondObject() {
		return secondObject;
	}

	public void setSecondObject(Tile secondObject) {
		this.secondObject = secondObject;
	}

	public ObjectTile getReceiveObject() {
		return receiveObject;
	}

	public void setReceiveObject(ObjectTile receiveObject) {
		this.receiveObject = receiveObject;
	}
	
	public String getResultTypeName() {
		if(firstObject == null || secondObject == null) {
			return "void";
		}
		String type1 = firstObject instanceof ObjectTile ? firstObject.getAsObjectTile().getTypeName() : firstObject.getAsFieldTile().getTypeName();
		String type2 = secondObject instanceof ObjectTile ? secondObject.getAsObjectTile().getTypeName() : secondObject.getAsFieldTile().getTypeName();
		if(type1.equalsIgnoreCase("java.lang.Double") || type2.equalsIgnoreCase("java.lang.Double")) {
			return "java.lang.Double";
		}
		if(type1.equalsIgnoreCase("java.lang.Float") || type2.equalsIgnoreCase("java.lang.Float")) {
			return "java.lang.Float";
		}
		if(type1.equalsIgnoreCase("java.lang.Long") || type2.equalsIgnoreCase("java.lang.Long")) {
			return "Long";
		}
		return "java.lang.Integer";
	}

	public String parse() {
		String type = this.getResultTypeName();
		String var = this.getTileID() + " = ";
		
		String seg = "";
		seg += this.getFirstObject().autoParse();
		
		if(this.getMathType() == MathType.ADD) {
			seg += " + ";
		}else if(this.getMathType() == MathType.SUBTRACT) {
			seg += " - ";
		}else if(this.getMathType() == MathType.MULTIPLY) {
			seg += " * ";
		}else if(this.getMathType() == MathType.DIVIDE) {
			seg += " / ";
		}else if(this.getMathType() == MathType.MOD) {
			seg += " % ";
		}
		
		seg += this.getSecondObject().autoParse();
		
		seg += ";\n";
		
		return type + " " + var + seg;
	}
	
	@Override
	public void draw(Graphics g) {
		this.setHeight(Main.nHeight + 20);
		super.draw(g);
		
        g.setColor(Color.BLACK);
        
        if(this.getFirstObject() != null) {
        	Point entry1 = this.getEntryPosFirst();
            Point exit1 = new Point(0, 0);
            if(this.getFirstObject() instanceof ObjectTile) {
            	exit1 = this.getFirstObject().getAsObjectTile().getExitPos();
            	this.getFirstObject().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getFirstObject() instanceof FieldTile) {
            	exit1 = this.getFirstObject().getAsFieldTile().getResultPos();
            	this.getFirstObject().getAsFieldTile().addUsingTiles(this);
    		}
    		g.drawLine(entry1.x, entry1.y, exit1.x, exit1.y);
        }
		
        if(this.getSecondObject() != null) {
        	Point entry2 = this.getEntryPosSecond();
            Point exit2 = new Point(0, 0);
            if(this.getSecondObject() instanceof ObjectTile) {
            	exit2 = this.getSecondObject().getAsObjectTile().getExitPos();
            	this.getSecondObject().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getSecondObject() instanceof FieldTile) {
            	exit2 = this.getSecondObject().getAsFieldTile().getResultPos();
            	this.getSecondObject().getAsFieldTile().addUsingTiles(this);
    		}
    		g.drawLine(entry2.x, entry2.y, exit2.x, exit2.y);
        }

		g.drawString("operation", this.getXFromLeft(), this.getYFromBottom(3));
		g.drawString("object1", this.getXFromLeft(), this.getYFromBottom(2));
		g.drawString("object2", this.getXFromLeft(), this.getYFromBottom(1));
		g.drawString("resultObject", this.getXFromRight("resultObject"), this.getYFromBottom(1));
		
		JComboBox<String> combo = Utils.getComboBox(this, "operation", null);
		combo.setLocation(this.getXFromLeft() + Window.fontMetrics.stringWidth("operation "), 
				this.getYFromBottom(3) - (int) (combo.getHeight() * 0.75));
		if(combo.getItemCount() == 0) {
			for(MathType mt : MathType.values()) {
				combo.addItem(mt.toString());
			}
			combo.setSelectedItem(this.getMathType().toString());
		}else {
			this.setMathType(MathType.valueOf(combo.getSelectedItem().toString()));
		}
		
		JRadioButton radio1 = Utils.getRadio(this, "object1");
		radio1.setLocation(this.getPosX(), this.getYFromBottom(2) - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getFirstObject() != null);
		
		JRadioButton radio2 = Utils.getRadio(this, "object2");
		radio2.setLocation(this.getPosX(), this.getYFromBottom(1) - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(this.getSecondObject() != null);
		
		JRadioButton radio3 = Utils.getRadio(this, "resultObject");
		radio3.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio3.getHeight() * 0.75));
		radio3.setSelected(true);
		
		if(this.getReceiveObject() == null) {
			ObjectTile ot = this.createReceiveObjectTile();
			this.setReceiveObject(ot);
		}
    }
	
	public Point getEntryPosFirst() {
    	return new Point(this.getPosX(), this.getYFromBottom(2));
    }
	
	public Point getEntryPosSecond() {
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
    		if(tag.equals("object1")) {
        		if(this.getFirstObject() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setFirstObject(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getFirstObject() instanceof ObjectTile) {
            				this.getFirstObject().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getFirstObject() instanceof FieldTile) {
            				this.getFirstObject().getAsFieldTile().removeUsingTiles(this);
        				}
            			this.setFirstObject(null);
        			}
        		}
    		}else if(tag.equals("object2")) {
        		if(this.getSecondObject() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setSecondObject(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getSecondObject() instanceof ObjectTile) {
            				this.getSecondObject().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getSecondObject() instanceof FieldTile) {
            				this.getSecondObject().getAsFieldTile().removeUsingTiles(this);
        				}
            			this.setSecondObject(null);
        			}
        		}
    		}
    	}
    }
	
}
