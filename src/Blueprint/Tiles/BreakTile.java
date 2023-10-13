package Blueprint.Tiles;

import Blueprint.Tiles.Parents.ExecTile;

public class BreakTile extends ExecTile {
	
	public BreakTile() {
		
	}
	
	public String parse(){
		return "break;\n";
	}
	
}
