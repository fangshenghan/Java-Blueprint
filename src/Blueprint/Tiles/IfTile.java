package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class IfTile extends ExecTile {

	private Tile conditionObject;
	private ExecTile runTile;
	
	public IfTile() {
		
	}

	public Tile getConditionObject() {
		return conditionObject;
	}

	public void setConditionObject(Tile conditionObject) {
		this.conditionObject = conditionObject;
	}

	public ExecTile getRunTile() {
		return runTile;
	}

	public void setRunTile(ExecTile runTile) {
		this.runTile = runTile;
		if(runTile != null) {
			this.runTile.setLastSpecialExecTile(this);
		}
	}

	public String parse() {
		String seg = "if(";
		
		seg += this.conditionObject.autoParse();
		seg += "){\n";
		
		seg += Utils.compile(this.runTile);
		
		seg += "}\n";
		
		return seg;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        g.setColor(Color.BLACK);
        
        if(this.getConditionObject() != null) {
        	Point entry1 = this.getEntryPosConditionObject();
            Point exit1 = new Point(0, 0);
            if(this.getConditionObject() instanceof ObjectTile) {
            	exit1 = this.getConditionObject().getAsObjectTile().getExitPos();
            	this.getConditionObject().getAsObjectTile().addUsingTiles(this);
    		}else if(this.getConditionObject() instanceof FieldTile) {
            	exit1 = this.getConditionObject().getAsFieldTile().getResultPos();
            	this.getConditionObject().getAsFieldTile().addUsingTiles(this);
    		}
    		g.drawLine(entry1.x, entry1.y, exit1.x, exit1.y);
        }

        g.setColor(Color.MAGENTA);
        if(this.getRunTile() != null) {
        	Point entry2 = this.getRunTile().getExecEntryPos();
            Point exit2 = this.getExitPosRunTile();
    		g.drawLine(entry2.x, entry2.y, exit2.x, exit2.y);
        }

        g.setColor(Color.BLACK);
		g.drawString("conditionObject", this.getXFromLeft(), this.getYFromBottom(1));
		g.drawString("true ", this.getXFromRight("true "), this.getYFromBottom(1));

		JRadioButton radio1 = Utils.getRadio(this, "conditionObject");
		radio1.setLocation(this.getPosX(), this.getYFromBottom(1) - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getConditionObject() != null);

		JLabel icon = Utils.getIcon(this, "execRunIfTile", this.getRunTile() != null);
		icon.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (icon.getHeight() * 0.75));
    }
	
	public Point getEntryPosConditionObject() {
    	return new Point(this.getPosX(), this.getYFromBottom(1));
    }
	
	public Point getExitPosRunTile() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }

    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JRadioButton) {
    		JRadioButton radio = (JRadioButton) comp;
    		if(tag.equals("conditionObject")) {
        		if(this.getConditionObject() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				this.setConditionObject(lastSelection.getTile());
        			}
        		}else {
        			if(lastSelection == null) {
        				if(this.getConditionObject() instanceof ObjectTile) {
            				this.getConditionObject().getAsObjectTile().removeUsingTiles(this);
        				}else if(this.getConditionObject() instanceof FieldTile) {
            				this.getConditionObject().getAsFieldTile().removeUsingTiles(this);
        				}
            			this.setConditionObject(null);
        			}
        		}
    		}
    	}else if(comp instanceof JLabel) {
    		JLabel icon = (JLabel) comp;
    		if(tag.equals("execRunIfTile")) {
        		if(this.getRunTile() == null) {
        			if(lastSelection != null && !lastSelection.getTile().equals(this)) {
        				if(lastSelection.getTile() instanceof ExecTile) {
        					if(lastSelection.getTag().equals("execEnter")
        							&& ((JLabel) lastSelection.getComponent()).getIcon().equals(Main.arrow_off)) {
        						this.setRunTile((ExecTile) lastSelection.getTile());
        					}
        				}
        			}
        		}else {
        			if(lastSelection == null) {
        				this.getRunTile().setLastSpecialExecTile(null);
            			this.setRunTile(null);
        			}
        		}
    		}
    	}
    }
	
}
