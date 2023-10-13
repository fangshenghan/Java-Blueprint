package Blueprint.Helper;

import javax.swing.JComponent;

import Blueprint.Tiles.Parents.Tile;

public class Selection {

	private Tile tile;
	private JComponent comp;
	private String tag;
	
	public Selection(Tile tile, JComponent comp, String tag) {
		this.tile = tile;
		this.comp = comp;
		this.tag = tag;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public JComponent getComponent() {
		return comp;
	}

	public void setComponent(JComponent comp) {
		this.comp = comp;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
