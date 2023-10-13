package Blueprint.Tiles.Parents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import javax.swing.JComponent;

import Blueprint.Main;
import Blueprint.Window;
import Blueprint.Helper.Selection;
import Blueprint.Tiles.AssignTile;
import Blueprint.Tiles.BreakTile;
import Blueprint.Tiles.ClassFieldTile;
import Blueprint.Tiles.ClassTile;
import Blueprint.Tiles.ConstructorTile;
import Blueprint.Tiles.FieldTile;
import Blueprint.Tiles.IfTile;
import Blueprint.Tiles.LogicTile;
import Blueprint.Tiles.MathTile;
import Blueprint.Tiles.MethodTile;
import Blueprint.Tiles.NumCompareTile;
import Blueprint.Tiles.ObjectTile;
import Blueprint.Tiles.StartTile;
import Blueprint.Tiles.WhileTile;

public class Tile {

	private String tileID;
	private HashMap<String, JComponent> components = new HashMap<>();
	
	public Tile() {
		if(this instanceof StartTile) {
			this.tileID = "_startTile";
		}else {
			this.tileID = "_" + UUID.randomUUID().toString().replace("-", "");
		}
		this.setWidth(Main.nWidth);
		this.setHeight(Main.nHeight);
		
		if(this instanceof StartTile) {
			this.setLocation(100,  600);
		}
		
		Main.tiles.put(this.tileID, this);
	}
	
	public void removeReferences(Tile t) {
		try {
			this.nullifyFields(this.getClass(), t);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void nullifyFields(Class<?> clazz, Tile t) throws Exception {
        if(clazz == null) {
            return;
        }

        // Nullify fields declared in the current class
        Field[] declaredFields = clazz.getDeclaredFields();
        for(Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(this);
            if(o instanceof HashMap) {
            	HashMap<Object, Object> map = (HashMap<Object, Object>) o;
            	for(Entry<Object, Object> entry : map.entrySet()) {
            		if(entry.getValue() != null && 
            				entry.getValue().equals(t)) {
            			map.put(entry.getKey(), null);
            		}
            	}
            }else if(o instanceof ArrayList) {
            	ArrayList<Object> list = (ArrayList<Object>) o;
            	list.remove(t);
            }else {
            	if(o != null && o.equals(t)) {
                	field.set(this, null);
                }
            }
        }

        this.nullifyFields(clazz.getSuperclass(), t);
    }
	
	public String getTileID() {
		return tileID;
	}

	public ExecTile getAsExecTile() {
		return (ExecTile) this;
	}
	
	public ClassTile getAsClassTile() {
		return (ClassTile) this;
	}
	
	public MethodTile getAsMethodTile() {
		return (MethodTile) this;
	}
	
	public ObjectTile getAsObjectTile() {
		return (ObjectTile) this;
	}
	
	public FieldTile getAsFieldTile() {
		return (FieldTile) this;
	}
	
	public ConstructorTile getAsConstructorTile() {
		return (ConstructorTile) this;
	}
	
	public NumCompareTile getAsNumCompareTile() {
		return (NumCompareTile) this;
	}
	
	public IfTile getAsIfTile() {
		return (IfTile) this;
	}
	
	public ClassFieldTile getAsClassFieldTile() {
		return (ClassFieldTile) this;
	}
	
	public WhileTile getAsWhileTile() {
		return (WhileTile) this;
	}
	
	public MathTile getAsMathTile() {
		return (MathTile) this;
	}
	
	public BreakTile getAsBreakTile() {
		return (BreakTile) this;
	}
	
	public LogicTile getAsLogicTile() {
		return (LogicTile) this;
	}
	
	public AssignTile getAsAssignTile() {
		return (AssignTile) this;
	}
	
	public StartTile getAsStartTile() {
		return (StartTile) this;
	}
	
	public String getTypeName() {
		if(this instanceof MethodTile) {
			return this.getAsMethodTile().getReturnType();
		}else if(this instanceof ClassFieldTile) {
			return this.getAsClassFieldTile().getTypeName();
		}else if(this instanceof ConstructorTile) {
			return this.getAsConstructorTile().getClassName();
		}else if(this instanceof NumCompareTile) {
			return "java.lang.Boolean";
		}else if(this instanceof MathTile) {
			return this.getAsMathTile().getResultTypeName();
		}else if(this instanceof FieldTile) {
			return this.getAsFieldTile().getTypeName();
		}else if(this instanceof AssignTile) {
			return this.getAsAssignTile().getTargetObjectTile().getTypeName();
		}
		return "void";
	}
	
	public Tile getRootExecTile() {
		Tile root = this;
		while(root != null) {
			if(this instanceof ObjectTile) {
				return this.getAsObjectTile().getSourceTile().getRootExecTile();
			}else if(this instanceof ExecTile){
				return this.getAsExecTile().getRootExecTile();
			}else {
				break;
			}
		}
		return root;
	}
	
	public String autoParse() {
		if(this instanceof ClassTile) {
			return this.getAsClassTile().parse();
		}else if(this instanceof MethodTile) {
			return this.getAsMethodTile().parse();
		}else if(this instanceof ObjectTile) {
			return this.getAsObjectTile().parse();
		}else if(this instanceof FieldTile) {
			return this.getAsFieldTile().parse();
		}else if(this instanceof ConstructorTile) {
			return this.getAsConstructorTile().parse();
		}else if(this instanceof NumCompareTile) {
			return this.getAsNumCompareTile().parse();
		}else if(this instanceof IfTile) {
			return this.getAsIfTile().parse();
		}else if(this instanceof ClassFieldTile) {
			return this.getAsClassFieldTile().parse();
		}else if(this instanceof WhileTile) {
			return this.getAsWhileTile().parse();
		}else if(this instanceof MathTile) {
			return this.getAsMathTile().parse();
		}else if(this instanceof BreakTile) {
			return this.getAsBreakTile().parse();
		}else if(this instanceof LogicTile) {
			return this.getAsLogicTile().parse();
		}else if(this instanceof AssignTile) {
			return this.getAsAssignTile().parse();
		}else if(this instanceof StartTile) {
			return this.getAsStartTile().parse();
		}
		return null;
	}
	
	private int posX = 200, posY = 800, width = 0, height = 0;
	
	public void draw(Graphics g) {
		if(this instanceof ObjectTile) {
			g.setColor(Color.PINK);
		}else if(this instanceof WhileTile) {
			g.setColor(Color.YELLOW);
		}else if(this instanceof ExecTile){
			g.setColor(Color.GREEN);
		}else {
			g.setColor(Color.GRAY);
		}
        g.fillRect(posX, posY, width, height);
        g.setColor(Color.BLACK);
        g.drawString(this.getClass().getSimpleName(), posX + 20, posY + 20);
    }
	
	public void onClick(JComponent comp, String tag, Selection lastSelection) {
		
	}
	
	public boolean containsPoint(Point p) {
        return posX <= p.x && p.x <= posX + width && posY <= p.y && p.y <= posY + height;
    }
	
	public void setLocation(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getXFromLeft() {
		return this.getPosX() + 20;
	}
	
	public int getXFromRight(String text) {
		return this.getPosX() + this.getWidth() - Window.fontMetrics.stringWidth(text) - 20;
	}
	
	public int getYFromBottom(int index) {
		return this.getPosY() + this.getHeight() + 10 - index * 20;
	}

	public HashMap<String, JComponent> getComponents() {
		return components;
	}

	public void setComponents(HashMap<String, JComponent> components) {
		this.components = components;
	}

	public void onFocusGained(JComponent comp, String tag) {
		
	}

	public void onFocusLost(JComponent comp, String tag) {
		
	}
	
	public void remove(boolean removeParentIfObjectTile) {
		if(this instanceof StartTile) {
			return;
		}
		Main.tiles.remove(this.getTileID());
		for(JComponent comp : this.getComponents().values()) {
			comp.getParent().remove(comp);
		}
		for(Tile t : Main.tiles.values()) {
			t.removeReferences(this);
		}
		if(this instanceof ObjectTile && removeParentIfObjectTile) {
			if(this.getAsObjectTile().getSourceTile() != null) {
				this.getAsObjectTile().getSourceTile().remove(false);
			}
		}
	}

	public void onActionPerform(JComponent comp, String tag) {
		
	}
    
}
