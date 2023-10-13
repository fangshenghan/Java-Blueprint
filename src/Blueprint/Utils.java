package Blueprint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboBoxUI;

import Blueprint.Helper.Selection;
import Blueprint.Tiles.Parents.ExecTile;
import Blueprint.Tiles.Parents.Tile;

public class Utils {
	
	public static Selection lastSelection = null;
	
	public static JTextField getTextField(Tile t, String tag, String initText) {
		JTextField field = null;
		if(t.getComponents().containsKey(tag)) {
			field = (JTextField) t.getComponents().get(tag);
		}else {
			field = new JTextField(){
				@Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                Window.instance.canvas.repaint();
				}
            };
			field.setText(initText);
			Window.instance.addComponent(field);
			t.getComponents().put(tag, field);
			
			field.addFocusListener(new FocusListener() {
				@Override
				public void focusGained(FocusEvent e) {
					t.onFocusGained(t.getComponents().get(tag), tag);
				}

				@Override
				public void focusLost(FocusEvent e) {
					t.onFocusLost(t.getComponents().get(tag), tag);
				}
            });
			field.getDocument().addDocumentListener(new DocumentListener() {
	            @Override
	            public void insertUpdate(DocumentEvent e) {
	                updateLabel();
	            }

	            @Override
	            public void removeUpdate(DocumentEvent e) {
	                updateLabel();
	            }

	            @Override
	            public void changedUpdate(DocumentEvent e) {
	                // This method is typically not used for plain text fields
	            }

	            private void updateLabel() {
					t.onActionPerform(t.getComponents().get(tag), tag);
	            }
	        });
		}
		return field;
	}

	public static JRadioButton getRadio(Tile t, String tag) {
		JRadioButton radio = null;
		if(t.getComponents().containsKey(tag)) {
			radio = (JRadioButton) t.getComponents().get(tag);
		}else {
			radio = new JRadioButton() {
				@Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                Window.instance.canvas.repaint();
				}
            };
			radio.setOpaque(true);
			radio.setBackground(new Color(0, 0, 0, 0));
			Window.instance.addComponent(radio);
			t.getComponents().put(tag, radio);
			
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JRadioButton radio = (JRadioButton) t.getComponents().get(tag);
					boolean isSecond = lastSelection != null;
					if(!isSecond && radio.isSelected()) {
						lastSelection = new Selection(t, radio, tag);
//						System.out.println("lastSelect");
					}
					t.onClick(radio, tag, lastSelection);
					if(isSecond) {
						lastSelection = null;
					}
				}
            });
		}
		return radio;
	}

	public static JComboBox<String> getComboBox(Tile t, String tag, String initText) {
		JComboBox<String> combo = null;
		if(t.getComponents().containsKey(tag)) {
			combo = (JComboBox<String>) t.getComponents().get(tag);
		}else {
			combo = new JComboBox<String>() {
				@Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                Window.instance.canvas.repaint();
				}
            };
            combo.setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    return new JButton() {
                        @Override
                        public int getWidth() {
                            return 0;
                        }
                    };
                }
            });
            if(initText != null) {
            	combo.addItem(initText);
                combo.setSelectedIndex(0);
            }
			Window.instance.addComponent(combo);
			t.getComponents().put(tag, combo);
			combo.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	t.onActionPerform(t.getComponents().get(tag), tag);
	            }
	        });
		}
		return combo;
	}
	
	public static JLabel getIcon(Tile t, String tag, boolean active) {
		JLabel picLabel = null;
		if(t.getComponents().containsKey(tag)) {
			picLabel = (JLabel) t.getComponents().get(tag);
		}else {
            picLabel = new JLabel(Main.arrow_off) {
				@Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                Window.instance.canvas.repaint();
				}
            };
			Window.instance.addComponent(picLabel);
			t.getComponents().put(tag, picLabel);
			picLabel.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					JLabel icon = (JLabel) t.getComponents().get(tag);
					boolean isSecond = lastSelection != null;
					if(!isSecond && icon.getIcon().equals(Main.arrow_off)) {
						lastSelection = new Selection(t, icon, tag);
					}
					t.onClick(icon, tag, lastSelection);
					if(isSecond) {
						lastSelection = null;
					}
				}
            });
		}
		if(active) {
			picLabel.setIcon(Main.arrow_on);
		}else {
			picLabel.setIcon(Main.arrow_off);
		}
		return picLabel;
	}
	
	public static String parseClassName(String classPath) {
		return classPath.substring(classPath.lastIndexOf(".") + 1);
	}
	
	public static int calculateComboBoxWidth(JComboBox<String> combo) {
		int min = 20;
		for(int i = 0;i < combo.getItemCount();i++) {
			min = Math.max(min, Window.fontMetrics.stringWidth(combo.getItemAt(i).toString()) + 20);
		}
		return min;
	}
	
	public static String showSaveFileDialog(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Java Blueprint Files", "jbp");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showSaveDialog(parent);

        if(result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }else{
            return null;
        }
    }
	
	public static String read(String path) {
		File file = new File(path);
		InputStreamReader isr;
		try {
			isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			StringBuffer res = new StringBuffer();
			 String line = null;
			 try {
				while ((line = br.readLine()) != null) {
					   res.append(line + "\n");
					  }
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean write(String cont, File dist) {
		try {
		  OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(dist), "UTF-8");
		  BufferedWriter writer = new BufferedWriter(osw);
		  writer.write(cont);
		  writer.flush();
		  writer.close();
		  return true;
		} catch (IOException e) {
		  e.printStackTrace();
		  return false;
		}
	}
	
	public static void compileAndRun() {
		String code = Utils.compile(Main.startTile);
		code = "public class Main {\n" 
					+ "public static void main(String args[]) throws Exception {\n"
					+ code + "\n"
					+ "java.util.Scanner scanner = new java.util.Scanner(java.lang.System.in);\r\n"
					+ "        System.out.print(\"Press Enter to exit...\");\r\n"
					+ "        scanner.nextLine();\r\n"
					+ "        scanner.close();\n"
					+ "}\n" 
					+ "}";
//		System.out.println(code);
		new File("blueprint_lib/run/Main.class").delete();
		File f = new File("blueprint_lib/run/Main.java");
		f.delete();
		try {
			Utils.write(code, f);
			String cmd = Main.javacPath + " \"" + f.getAbsolutePath() + "\"";
//			System.out.println(cmd);
			Process p = Runtime.getRuntime().exec(cmd);
			while(p.isAlive()) {}
			Runtime.getRuntime().exec("cmd /c start cmd /k " + Main.javaPath + " -cp blueprint_lib/run Main");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String compile(ExecTile startTile) {
		String result = "";
		
		ExecTile tile = startTile;
		while(tile != null) {
			result += tile.autoParse();
			tile = tile.getNextExecTile();
		}
		
		return result;
	}
	
}
