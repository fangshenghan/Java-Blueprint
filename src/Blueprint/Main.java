package Blueprint;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import Blueprint.Helper.JarUtils;
import Blueprint.Tiles.StartTile;
import Blueprint.Tiles.Parents.Tile;

public class Main {
	
	public static String javaPath = "", javacPath = "";
	
	public static HashMap<String, Tile> tiles = new HashMap<>();
	public static List<String> tileTypes = Arrays.asList("AssignTile", "BreakTile", "ClassFieldTile", "ClassTile", 
			"ConstructorTile", "FieldTile", "IfTile", "LogicTile", "MathTile", "NumCompareTile", "MethodTile", "WhileTile");
	public static StartTile startTile;
	
	public static ImageIcon arrow_off, arrow_on;
	public static int nWidth = 200, nHeight = 100;
	
	public static void main(String args[]) throws Exception {
		try {
			Main.init();
		}catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "Initialize Failed! Make sure your blueprint_lib folder is here! \nStacktrace:\n" + ex.toString());
			System.exit(0);
		}
	}
	
	public static void init() throws Exception {
		String config = Utils.read("blueprint_lib/config.txt");
		javaPath = config.split("\n")[0].substring(9);
		javacPath = config.split("\n")[1].substring(10);
				
		arrow_off = new ImageIcon(ImageIO.read(new File("blueprint_lib/arrow_off.png")));
		arrow_on = new ImageIcon(ImageIO.read(new File("blueprint_lib/arrow_on.png")));
		JarUtils.loadJarFile("blueprint_lib/rt.jar");
		new Window();
		startTile = new StartTile();
	}
}

