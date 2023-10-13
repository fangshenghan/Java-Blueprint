package Blueprint.Tiles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JLabel;

import Blueprint.Main;
import Blueprint.Utils;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;

public class WhileTile extends ExecTile {

	private ExecTile runTile;
	
	public WhileTile() {
		
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
		String seg = "while(true){\n";
		
		seg += Utils.compile(this.runTile);
		
		seg += "}\n";
		
		return seg;
	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
		
        g.setColor(Color.ORANGE);
        ExecTile run = this.getRunTile();
        if(run != null) {
    		Point entry = run.getExecEntryPos();
    		Point exit = this.getExitRunTilePos();
    		g.drawLine(entry.x, entry.y, exit.x, exit.y);
    	}
        
        g.setColor(Color.BLACK);
		g.drawString("run", this.getXFromRight("run"), this.getYFromBottom(1));

		JLabel icon = Utils.getIcon(this, "execRunWhileTile", this.getRunTile() != null);
		icon.setLocation(this.getXFromRight(""), this.getYFromBottom(1) - (int) (icon.getHeight() * 0.75));
    }
	
	public Point getExitRunTilePos() {
    	return new Point(this.getPosX() + this.getWidth(), this.getYFromBottom(1));
    }

    @Override
    public void onClick(JComponent comp, String tag, Selection lastSelection) {
    	super.onClick(comp, tag, lastSelection);
    	
    	if(comp instanceof JLabel) {
    		JLabel icon = (JLabel) comp;
    		if(tag.equals("execRunWhileTile")) {
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
