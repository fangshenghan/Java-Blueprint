package Blueprint;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Blueprint.Tiles.Parents.Tile;

public class Window {
	
	public static Window instance;
	public static FontMetrics fontMetrics = null;
	public static long lastRepaint = 0;
	
	public JFrame frmJavaBlueprint;
    public JPanel canvas;
    
    private Tile selectedTile = null;
    private Point mouseOffset = new Point();
    private Point mouseLocation = new Point();
    
    public Point menuLocation = new Point(0, 0);
    public boolean showMenu = false;
    private JButton compileBtn;
    private JPanel panel;

    public Window() {
    	instance = this;
        frmJavaBlueprint = new JFrame("Blueprint");
        frmJavaBlueprint.setTitle("Java Blueprint");
        frmJavaBlueprint.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmJavaBlueprint.setSize(1920, 1080);
        frmJavaBlueprint.setExtendedState(Frame.MAXIMIZED_BOTH);

        canvas = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
//            	if(System.currentTimeMillis() - lastRepaint < 1000 / 120) {
//            		return;
//            	}
//            	lastRepaint = System.currentTimeMillis();
                fontMetrics = g.getFontMetrics();
            	
                super.paintComponent(g);
                panel.paintComponents(g);
                for(Tile t : new ArrayList<>(Main.tiles.values())) {
                    t.draw(g);
                }
                
                g.setColor(Color.CYAN);
                if(Utils.lastSelection != null) {
                    Point p = Utils.lastSelection.getComponent().getLocation();
                    g.drawLine(p.x + 5, p.y + 5, mouseLocation.x, mouseLocation.y);
                }
                
            }
        };
        canvas.setBounds(0, 0, 1906, 1043);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	if(e.getButton() == MouseEvent.BUTTON1) {
            		for(Tile t : new ArrayList<>(Main.tiles.values())) {
                        if(t.containsPoint(e.getPoint())) {
                            selectedTile = t;
                            mouseOffset.setLocation(e.getX() - t.getPosX(), e.getY() - t.getPosY());
                            break;
                        }
                    }
            	}
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	if(e.getButton() == MouseEvent.BUTTON1) {
                	selectedTile = null;
            	}else if(e.getButton() == MouseEvent.BUTTON2) {
            		for(Tile t : new ArrayList<>(Main.tiles.values())) {
                        if(t.containsPoint(e.getPoint())) {
                            t.remove(true);
                            break;
                        }
                    }
            	}
            	if(e.getButton() == MouseEvent.BUTTON3) {
            		menuLocation = e.getPoint();
            		showMenu = true;
                    JTextField field = Utils.getTextField(Main.startTile, "tileSelectorField", "");
                    field.setText("");
            	}else {
            		showMenu = false;
            	}
            	canvas.grabFocus();
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(selectedTile != null) {
                	selectedTile.setLocation(e.getX() - (int) mouseOffset.getX(), e.getY() - (int) mouseOffset.getY());
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseLocation = e.getPoint();
            }
        });
        frmJavaBlueprint.getContentPane().setLayout(null);
        
        panel = new JPanel();
        panel.setBounds(0, 0, 381, 33);
        frmJavaBlueprint.getContentPane().add(panel);
        panel.setLayout(null);
        
        compileBtn = new JButton("Compile & Run");
        compileBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Utils.compileAndRun();
        	}
        });
        compileBtn.setBounds(10, 10, 135, 23);
        panel.add(compileBtn);

        frmJavaBlueprint.getContentPane().add(canvas);
        frmJavaBlueprint.setVisible(true);
    }
    
    public void addComponent(JComponent com) {
    	canvas.add(com);
    	frmJavaBlueprint.revalidate();
    }
	public JPanel getPanel() {
		return panel;
	}
}
