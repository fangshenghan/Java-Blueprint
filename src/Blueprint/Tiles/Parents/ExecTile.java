package Blueprint.Tiles.Parents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.IfTile;
import Blueprint.Tiles.ObjectTile;
import Blueprint.Tiles.StartTile;
import Blueprint.Tiles.WhileTile;

public class ExecTile extends Tile {
	
	private ExecTile lastExecTile, nextExecTile;
	private ExecTile lastSpecialExecTile;

	private ObjectTile receiveObject;
	
	public ExecTile getLastExecTile() {
		return lastExecTile;
	}
	
	public ExecTile getNextExecTile() {
		return nextExecTile;
	}

	public ExecTile getLastSpecialExecTile() {
		return lastSpecialExecTile;
	}

	public ObjectTile getReceiveObject() {
		return receiveObject;
	}

	public void setReceiveObject(ObjectTile receiveObject) {
		this.receiveObject = receiveObject;
	}

	public void setLastSpecialExecTile(ExecTile lastSpecialExecTile) {
		this.lastSpecialExecTile = lastSpecialExecTile;
		if(lastSpecialExecTile != null) {
			this.setLastExecTile(null);
		}
	}

	public void setNextExecTile(ExecTile nextTile) {
		this.nextExecTile = nextTile;
		if(nextTile != null && nextTile.getLastExecTile() != this) {
			nextTile.setLastExecTile(this);
		}
	}
	
	public void setLastExecTile(ExecTile lastTile) {
		this.lastExecTile = lastTile;
		if(lastTile != null && lastTile.getNextExecTile() != this) {
			lastTile.setNextExecTile(this);
			this.setLastSpecialExecTile(null);
		}
	}
	
	public ExecTile getRootExecTile() {
		ExecTile root = this;
		while(root.getLastExecTile() != null) {
			root = root.getLastExecTile();
		}
		return root;
	}
	
	public ObjectTile createReceiveObjectTile() {
		ObjectTile ot = new ObjectTile();
		ot.setSourceTile(this);
		return ot;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
        
        g.setColor(Color.RED);
        if(this.getLastExecTile() != null) {
    		Point entry = this.getExecEntryPos();
    		Point exit = this.getLastExecTile().getExecExitPos();
    		g.drawLine(entry.x, entry.y, exit.x, exit.y);
    	}
        
        g.setColor(Color.BLACK);
        if(!(this instanceof StartTile)) {
        	JLabel enter = Utils.getIcon(this, "execEnter", this.getLastExecTile() != null || this.getLastSpecialExecTile() != null);
            enter.setLocation(this.getPosX() + 5, this.getPosY() + 25);
        }
        JLabel exit = Utils.getIcon(this, "execExit", this.getNextExecTile() != null);
        exit.setLocation(this.getPosX() + this.getWidth() - 16 - 5, this.getPosY() + 25);
    }
	
	public Point getExecEntryPos() {
    	return new Point(this.getPosX(), this.getPosY() + 25);
    }
    
    public Point getExecExitPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getPosY() + 25);
    }
    
    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JLabel && (lastSelection == null || lastSelection.getComponent() instanceof JLabel)) {
    		if(lastSelection != null && !lastSelection.getTile().equals(this)) {
    			if(lastSelection.getTag().contains("IfTile") || lastSelection.getTag().contains("WhileTile")) {
    				lastSelection.getTile().onClick(lastSelection.getComponent(), lastSelection.getTag(), 
        					new Selection(this, comp, tag));
    				return;
    			}
    		}
    		if(tag.equals("execEnter")) {
    			if(lastSelection == null) {
    	    		JLabel icon = (JLabel) comp;
    				if(icon.getIcon().equals(Main.arrow_on)) {
            			if(this.getLastSpecialExecTile() != null) {
            				if(this.getLastSpecialExecTile() instanceof WhileTile) {
            					this.getLastSpecialExecTile().getAsWhileTile().setRunTile(null);
            				}else if(this.getLastSpecialExecTile() instanceof IfTile) {
            					this.getLastSpecialExecTile().getAsIfTile().setRunTile(null);
            				}
                			this.setLastSpecialExecTile(null);
            			}
            			if(this.getLastExecTile() != null) {
            				this.getLastExecTile().setNextExecTile(null);
            			}
            			this.setLastExecTile(null);
            		}
    			}else {
    	    		JLabel icon = (JLabel) lastSelection.getComponent();
    				if(lastSelection.getTag().equals("execExit") && !lastSelection.getTile().equals(this)) {
    					if(icon.getIcon().equals(Main.arrow_off) && ((JLabel) comp).getIcon().equals(Main.arrow_off)) {
                			this.setLastExecTile((ExecTile) lastSelection.getTile());
                		}
    				}
    			}
    		}else if(tag.equals("execExit")) {
    			if(lastSelection == null) {
    	    		JLabel icon = (JLabel) comp;
    				if(icon.getIcon().equals(Main.arrow_on)) {
            			if(this.getNextExecTile() != null) {
            				this.getNextExecTile().setLastExecTile(null);
            			}
            			this.setNextExecTile(null);
            		}
    			}else {
    	    		JLabel icon = (JLabel) lastSelection.getComponent();
    				if(lastSelection.getTag().equals("execEnter") && !lastSelection.getTile().equals(this)) {
    					if(icon.getIcon().equals(Main.arrow_off) && ((JLabel) comp).getIcon().equals(Main.arrow_off)) {
                			this.setNextExecTile((ExecTile) lastSelection.getTile());
                		}
    				}
    			}
    		}
    	}
    }
	
}
