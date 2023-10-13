package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import Blueprint.Utils;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class AssignTile extends ExecTile {
	
	private Tile targetObjectTile, valueTile; 
	
	public AssignTile() {
		
	}

	public Tile getTargetObjectTile() {
		return targetObjectTile;
	}

	public void setTargetObjectTile(Tile targetObjectTile) {
		this.targetObjectTile = targetObjectTile;
	}

	public Tile getValueTile() {
		return valueTile;
	}

	public void setValueTile(Tile valueTile) {
		this.valueTile = valueTile;
	}

	public String parse(){
		String res = "";
		res = this.targetObjectTile.autoParse() + " = " + valueTile.autoParse() + ";\n";
		return res;
	}
	
	@Override
	public void draw(Graphics g) {
        super.draw(g);
        
        g.setColor(Color.BLACK);
        if(this.getTargetObjectTile() != null) {
            Point entry1 = this.getEntryPosTargetObject();
            Point exit1 = new Point(0, 0);
            if(this.getTargetObjectTile() instanceof ObjectTile) {
            	exit1 = this.getTargetObjectTile().getAsObjectTile().getExitPos();
            	this.getTargetObjectTile().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getTargetObjectTile() instanceof FieldTile) {
            	exit1 = this.getTargetObjectTile().getAsFieldTile().getResultPos();
            	this.getTargetObjectTile().getAsFieldTile().addUsingTiles(this);
    		}
    		g.drawLine(entry1.x, entry1.y, exit1.x, exit1.y);
        }
		
        if(this.getValueTile() != null) {
        	Point entry2 = this.getEntryPosValue();
            Point exit2 = new Point(0, 0);
            if(this.getValueTile() instanceof ObjectTile) {
            	exit2 = this.getValueTile().getAsObjectTile().getExitPos();
            	this.getValueTile().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getValueTile() instanceof FieldTile) {
            	exit2 = this.getValueTile().getAsFieldTile().getResultPos();
            	this.getValueTile().getAsFieldTile().addUsingTiles(this);
    		}
    		g.drawLine(entry2.x, entry2.y, exit2.x, exit2.y);
        }
		
		g.drawString("targetObject", this.getXFromLeft(), this.getEntryPosTargetObject().y);
		g.drawString("newValue", this.getXFromLeft(), this.getEntryPosValue().y);
		
		JRadioButton radio1 = Utils.getRadio(this, "targetObject");
		radio1.setLocation(this.getPosX(), this.getEntryPosTargetObject().y - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getTargetObjectTile() != null);

		JRadioButton radio2 = Utils.getRadio(this, "newValue");
		radio2.setLocation(this.getPosX(), this.getEntryPosValue().y - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(this.getValueTile() != null);
    }
	
	public Point getEntryPosTargetObject() {
    	return new Point(this.getPosX(), this.getYFromBottom(2));
    }
	
	public Point getEntryPosValue() {
    	return new Point(this.getPosX(), this.getYFromBottom(1));
    }

    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		if(tag.equals("targetObject")) {
        		if(this.getTargetObjectTile() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setTargetObjectTile(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getTargetObjectTile() instanceof ObjectTile) {
            				this.getTargetObjectTile().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getTargetObjectTile() instanceof FieldTile) {
            				this.getTargetObjectTile().getAsFieldTile().removeUsingTiles(this);
        				}
            			this.setTargetObjectTile(null);
        			}
        		}
    		}else if(tag.equals("newValue")) {
        		if(this.getValueTile() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setValueTile(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getValueTile() instanceof ObjectTile) {
            				this.getValueTile().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getValueTile() instanceof FieldTile) {
            				this.getValueTile().getAsFieldTile().removeUsingTiles(this);
        				}
            			this.setValueTile(null);
        			}
        		}
    		}
    	}
    }
	
}
