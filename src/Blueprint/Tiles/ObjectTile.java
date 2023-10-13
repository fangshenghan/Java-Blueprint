package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JRadioButton;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Window;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class ObjectTile extends Tile {
	
	private Tile sourceTile;
	private List<Tile> usingTiles = new ArrayList<>();
	
	public ObjectTile() {
		
	}

	public Tile getSourceTile() {
		return sourceTile;
	}

	public void setSourceTile(Tile sourceTile) {
		this.sourceTile = sourceTile;
		if(this.sourceTile instanceof ExecTile) {
			this.sourceTile.getAsExecTile().setReceiveObject(this);
		}
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
		if(this.sourceTile instanceof MethodTile) {
			return this.sourceTile.getAsMethodTile().getReturnType();
		}else if(this.sourceTile instanceof ClassFieldTile) {
			return this.sourceTile.getAsClassFieldTile().getTypeName();
		}else if(this.sourceTile instanceof ConstructorTile) {
			return this.sourceTile.getAsConstructorTile().getClassName();
		}else if(this.sourceTile instanceof NumCompareTile) {
			return "java.lang.Boolean";
		}else if(this.sourceTile instanceof MathTile) {
			return this.sourceTile.getAsMathTile().getResultTypeName();
		}else if(this.sourceTile instanceof FieldTile) {
			return this.sourceTile.getAsFieldTile().getTypeName();
		}else if(this.sourceTile instanceof AssignTile) {
			return this.sourceTile.getAsAssignTile().getTargetObjectTile().getTypeName();
		}
		return "void";
	}
	
	public String parse(){
		if(this.sourceTile instanceof AssignTile) {
			return this.sourceTile.getAsAssignTile().getTargetObjectTile().getTileID();
		}
		return this.sourceTile.getTileID();
	}
	
	@Override
	public void draw(Graphics g) {
        super.draw(g);
        
        if(this.getSourceTile() == null) {
        	this.remove(false);
        	return;
        }
        
        g.setColor(Color.BLACK);
        Tile source = this.getSourceTile();
        if(source != null) {
    		Point entry = this.getEntryPos();
    		Point exit = new Point(0, 0);
    		if(source instanceof MethodTile) {
    			exit = source.getAsMethodTile().getReturnPos();
    		}else if(source instanceof ClassFieldTile) {
    			exit = source.getAsClassFieldTile().getResultPos();
    		}else if(source instanceof ConstructorTile) {
    			exit = source.getAsConstructorTile().getResultPos();
    		}else if(source instanceof NumCompareTile) {
    			exit = source.getAsNumCompareTile().getResultPos();
    		}else if(source instanceof MathTile) {
    			exit = source.getAsMathTile().getResultPos();
    		}else if(source instanceof LogicTile) {
    			exit = source.getAsLogicTile().getResultPos();
    		}else if(source instanceof FieldTile) {
    			exit = source.getAsFieldTile().getResultPos();
    		}
    		g.drawLine(entry.x, entry.y, exit.x, exit.y);
    	}

        String type = this.getSourceTile().getTypeName();
		g.drawString("sourceObject (" + type + ")", this.getXFromLeft(), this.getEntryPos().y);
		g.drawString("resultObject", this.getXFromRight("resultObject"), this.getExitPos().y);
		this.setWidth(Main.nWidth + Window.fontMetrics.stringWidth(this.getSourceTile().getTypeName() + "  "));
		
		JRadioButton radio1 = Utils.getRadio(this, "sourceObject");
		radio1.setLocation(this.getPosX(), this.getYFromBottom(1) - (int) (radio1.getHeight() * 0.75));
		radio1.setSelected(this.getSourceTile() != null);

		JRadioButton radio2 = Utils.getRadio(this, "resultObject");
		radio2.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (radio2.getHeight() * 0.75));
		radio2.setSelected(this.getUsingTiles().size() > 0);
    }
	
	public Point getEntryPos() {
    	return new Point(this.getPosX(), this.getYFromBottom(1));
    }
    
    public Point getExitPos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }
    
    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
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
	
}
