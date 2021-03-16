package main;

import game.Board;
import game.Cell;
import game.*;
import game.Statics;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

public class GUI extends JFrame {
	
	static boolean pressed = false;
	static Cell selected = null;
	
	public Board board;
	private JButton[][] fields;
	private ArrayList<JButton> buttons;
	private HashMap<String, ImageIcon> black_icons;
	private HashMap<String, ImageIcon> white_icons;
	private JTextField textField;
	private StyledDocument chat;
	private JLabel lblStatus;
	private JLabel lblTurn;
	private JLabel lblColour;
	
	public boolean ready;
	
	public GUI(Board board) {
		this.board = board;
		this.fields = new JButton[Statics.SIZE][Statics.SIZE];
		this.buttons = new ArrayList<JButton>();
		this.black_icons = new HashMap<String, ImageIcon>();
		this.white_icons = new HashMap<String, ImageIcon>();
		this.ready = false;
		
		readImages();
		
		this.setSize(1280, 720);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		panel_2.add(splitPane);
		splitPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
		splitPane.setDividerLocation(350);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		
		panel.setLayout(new GridLayout(9, 8, 0, 0));
		
		
		
		panel.add(new JLabel(""));
		
		panel.add(new JLabel("A", SwingConstants.CENTER));
		panel.add(new JLabel("B", SwingConstants.CENTER));
		panel.add(new JLabel("C", SwingConstants.CENTER));
		panel.add(new JLabel("D", SwingConstants.CENTER));
		panel.add(new JLabel("E", SwingConstants.CENTER));
		panel.add(new JLabel("F", SwingConstants.CENTER));
		panel.add(new JLabel("G", SwingConstants.CENTER));
		panel.add(new JLabel("H", SwingConstants.CENTER));
		
		JPanel panel_1 = new JPanel();
		splitPane.setLeftComponent(panel_1);
		panel_1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
		panel_1.setLayout(new BorderLayout(10, 10));
		
		
		JScrollPane scrollPane = new JScrollPane();
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtpnText = new JTextPane();
		txtpnText.setEditable(false);
		scrollPane.setViewportView(txtpnText);
		
		this.chat = txtpnText.getStyledDocument();
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		textField = new JTextField();
		panel_3.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ready) {
					return;
				}
				
				String text = textField.getText();
				if (!text.isEmpty()) {
					textField.setText("");
					
					String message = "SEND;" + text;
					
					chatAdd(board.player.name + ": " + text, false);
					
					board.socket.send(message);
				}
			}
		});
		panel_3.add(btnSend, BorderLayout.EAST);
		
		JToolBar toolBar = new JToolBar();
		JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int choice = JOptionPane.showConfirmDialog(null, "Do you really want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
				
				if (choice == JOptionPane.YES_OPTION) {
					dispose();
					System.exit(0);
				}
				
				
				
			}
		});
		JButton surr = new JButton("Surrender");
		surr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (board.end || !ready) {
					return;
				}
				
				int choice = JOptionPane.showConfirmDialog(null, "Do you really want to surrender?", "Surrender", JOptionPane.YES_NO_OPTION);
				
				if (choice == JOptionPane.YES_OPTION) {
					chatAdd("You surrendered", true);
					board.endGame(board.player);
					board.socket.send("SURRENDER");
					updateTurn();
				}
				
			}
		});
		
		
		toolBar.add(exit);
		toolBar.add(surr);
		
		
		toolBar.add(Box.createHorizontalGlue());
		toolBar.setFloatable(false);
		
		getContentPane().add(toolBar, BorderLayout.NORTH);
		
		JPanel panel_4 = new JPanel();
		
		
		toolBar.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		panel_4.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		
		JLabel lblStatus = new JLabel("Status");
		this.lblStatus = lblStatus;
		panel_4.add(lblStatus, BorderLayout.EAST);
		
		JLabel lblTurn = new JLabel("", SwingConstants.CENTER);
		this.lblTurn = lblTurn;
		lblTurn.setFont(new Font(lblTurn.getFont().getFontName(), Font.BOLD, 20));
		panel_4.add(lblTurn, BorderLayout.CENTER);
		
		JLabel lblColour = new JLabel("Colour");
		this.lblColour = lblColour;
		panel_4.add(lblColour, BorderLayout.WEST);
		

		int count = Statics.SIZE;
		for (int i = 0; i < Statics.SIZE; i++) {
			panel.add(new JLabel("" + count, SwingConstants.CENTER));
			for (int j = 0; j < Statics.SIZE; j++) {
				// Every Cell has a Button
				JButton button = new JButton();
				final int x = j;
				final int y = i;
				
				
				button.addActionListener(new ActionListener() {
					Cell cell = board.getCell(x, y);
					public void actionPerformed(ActionEvent e) {
						
						if (board.end || !ready) {
							return;
						}/* else if (board.check) {
							if (cell.occupied) {
								if (!(cell.piece instanceof King && cell.piece.owner.white == board.player.white)) {
									if (!pressed && selected == null) {
										
										if (!board.checkFree()) {
											
										}
										
										return;
									}
								}
							} else {
								if (!pressed && selected == null) {
									return;
								}
							}
						}*/
						
						
						if (!pressed) {
							
							if (selected == null) {
								if (!cell.occupied) {
									return;
								} else {
									if (cell.piece.owner.white == board.player.white && cell.piece.owner.white == board.turn.white) {
										
										if (!cell.piece.noPossibleMoves()) {
											pressed = true;
											selected = cell;

											showMoves(cell);
										} else {
											return;
										}

									} else {
										return;
									}
								}
							}
						} else {
							colorButtons();
							
							if (selected.piece.checkMove(cell)) {
								board.movePiece(selected, cell, false);
								updatePieces();
								updateTurn();
							}
							
							selected = null;
							pressed = false;
						}
					}
				});
				buttons.add(button);
				panel.add(button);
				fields[i][j] = button;
			}
			count--;

		}
		
		colorButtons();
		updatePieces();
	}
	
	private void showMoves(Cell cell) {
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {

				JButton button = buttons.get((i * 8) + j);
				Cell current = this.board.getCell(j, i);

				if (cell.occupied) {
					if (cell.piece.checkMove(current)) {
						button.setBackground(Color.GREEN);
					}
				}

			}
		}

	}
	
	private void colorButtons() {
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				if (i % 2 == 0) {
					if (j % 2 == 0) {
						buttons.get((i*8)+j).setBackground(Color.WHITE);
					} else {
						buttons.get((i*8)+j).setBackground(Color.GRAY);
					}
				} else {
					if (j % 2 == 1) {
						buttons.get((i*8)+j).setBackground(Color.WHITE);
					} else {
						buttons.get((i*8)+j).setBackground(Color.GRAY);
					}
				}

			}
		}

	}
	
	private void updatePieces() {
		for (int i = 0; i < Statics.SIZE; i++) {
			for (int j = 0; j < Statics.SIZE; j++) {
				JButton button = buttons.get((i * 8) + j);
				Cell cell = this.board.getCell(j, i);

				if (cell.occupied) {
					if (cell.piece instanceof Pawn) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_pawn"));
						} else {
							button.setIcon(this.black_icons.get("black_pawn"));
						}
					} else if (cell.piece instanceof Tower) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_tower"));
						} else {
							button.setIcon(this.black_icons.get("black_tower"));
						}
					} else if (cell.piece instanceof Knight) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_knight"));
						} else {
							button.setIcon(this.black_icons.get("black_knight"));
						}
					} else if (cell.piece instanceof Bishop) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_bishop"));
						} else {
							button.setIcon(this.black_icons.get("black_bishop"));
						}

					} else if (cell.piece instanceof Queen) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_queen"));
						} else {
							button.setIcon(this.black_icons.get("black_queen"));
						}
					} else if (cell.piece instanceof King) {
						if (cell.piece.owner.white) {
							button.setIcon(this.white_icons.get("white_king"));
						} else {
							button.setIcon(this.black_icons.get("black_king"));
						}
					}
				} else {
					button.setIcon(null);
				}

			}
		}
	}
	
	private void readImages() {
		
		
		try {
			
			if (System.getProperty("java.class.path").contains("org.eclipse.equinox.launcher")) {
				//IDE
				this.black_icons.put("black_pawn", new ImageIcon(ImageIO.read(Main.class.getResource("resources/black_pawn.png"))));
				this.black_icons.put("black_tower", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_tower.png"))));
				this.black_icons.put("black_knight", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_knight.png"))));
				this.black_icons.put("black_bishop", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_bishop.png"))));
				this.black_icons.put("black_queen", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_queen.png"))));
				this.black_icons.put("black_king", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_king.png"))));
				
				this.white_icons.put("white_pawn", new ImageIcon(ImageIO.read(Main.class.getResource("resources/white_pawn.png"))));
				this.white_icons.put("white_tower", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_tower.png"))));
				this.white_icons.put("white_knight", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_knight.png"))));
				this.white_icons.put("white_bishop", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_bishop.png"))));
				this.white_icons.put("white_queen", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_queen.png"))));
				this.white_icons.put("white_king", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_king.png"))));
			} else {
				//JAR
				this.black_icons.put("black_pawn", new ImageIcon(ImageIO.read(Main.class.getClassLoader().getResource("black_pawn.png"))));
				this.black_icons.put("black_tower", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_tower.png"))));
				this.black_icons.put("black_knight", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_knight.png"))));
				this.black_icons.put("black_bishop", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_bishop.png"))));
				this.black_icons.put("black_queen", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_queen.png"))));
				this.black_icons.put("black_king", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_king.png"))));
				
				this.white_icons.put("white_pawn", new ImageIcon(ImageIO.read(Main.class.getClassLoader().getResource("white_pawn.png"))));
				this.white_icons.put("white_tower", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_tower.png"))));
				this.white_icons.put("white_knight", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_knight.png"))));
				this.white_icons.put("white_bishop", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_bishop.png"))));
				this.white_icons.put("white_queen", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_queen.png"))));
				this.white_icons.put("white_king", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_king.png"))));
			}
			
			/* THIS WORKS AS PROJECT
			this.black_icons.put("black_pawn", new ImageIcon(ImageIO.read(Main.class.getResource("resources/black_pawn.png"))));
			this.black_icons.put("black_tower", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_tower.png"))));
			this.black_icons.put("black_knight", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_knight.png"))));
			this.black_icons.put("black_bishop", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_bishop.png"))));
			this.black_icons.put("black_queen", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_queen.png"))));
			this.black_icons.put("black_king", new ImageIcon (ImageIO.read(Main.class.getResource("resources/black_king.png"))));
			
			this.white_icons.put("white_pawn", new ImageIcon(ImageIO.read(Main.class.getResource("resources/white_pawn.png"))));
			this.white_icons.put("white_tower", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_tower.png"))));
			this.white_icons.put("white_knight", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_knight.png"))));
			this.white_icons.put("white_bishop", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_bishop.png"))));
			this.white_icons.put("white_queen", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_queen.png"))));
			this.white_icons.put("white_king", new ImageIcon (ImageIO.read(Main.class.getResource("resources/white_king.png"))));
			
			// THIS WORKS FOR JAR
			this.black_icons.put("black_pawn", new ImageIcon(ImageIO.read(Main.class.getClassLoader().getResource("black_pawn.png"))));
			this.black_icons.put("black_tower", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_tower.png"))));
			this.black_icons.put("black_knight", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_knight.png"))));
			this.black_icons.put("black_bishop", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_bishop.png"))));
			this.black_icons.put("black_queen", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_queen.png"))));
			this.black_icons.put("black_king", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("black_king.png"))));
			
			this.white_icons.put("white_pawn", new ImageIcon(ImageIO.read(Main.class.getClassLoader().getResource("white_pawn.png"))));
			this.white_icons.put("white_tower", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_tower.png"))));
			this.white_icons.put("white_knight", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_knight.png"))));
			this.white_icons.put("white_bishop", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_bishop.png"))));
			this.white_icons.put("white_queen", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_queen.png"))));
			this.white_icons.put("white_king", new ImageIcon (ImageIO.read(Main.class.getClassLoader().getResource("white_king.png"))));
			*/
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public void chatAdd(String message, boolean status) {
		
		SimpleAttributeSet set = new SimpleAttributeSet();
		
		if (status) {
			StyleConstants.setForeground(set, Color.GRAY);
			
			try {
				if (this.chat.getLength() == 0) {
					this.chat.insertString(this.chat.getLength(), message, set);
				} else {
					this.chat.insertString(this.chat.getLength(), "\n" + message, set);
				}
				
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
		} else {
			Pattern pattern = Pattern.compile("(\\w+)(: .+)");
			Matcher matcher = pattern.matcher(message);
			matcher.find();
			
			String sender = matcher.group(1);
			String other = matcher.group(2);
			
			if (sender.equals(this.board.player.name)) {
				StyleConstants.setForeground(set, Color.RED);
			} else {
				StyleConstants.setForeground(set, Color.BLUE);
			}
			
			try {
				if (this.chat.getLength() == 0) {
					this.chat.insertString(this.chat.getLength(), sender, set);
				} else {
					this.chat.insertString(this.chat.getLength(), "\n" + sender, set);
				}
				
				
				if (this.chat.getLength() == 0) {
					this.chat.insertString(this.chat.getLength(), other, null);
				} else {
					this.chat.insertString(this.chat.getLength(), other, null);
				}
				
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void movePiece(Cell position, Cell target) {
		
		if (position.occupied) {
			
			if (position.piece.checkMove(target)) {
				board.movePiece(position, target, true);
				updatePieces();
				updateTurn();
			}
		} else {
			return;
		}
	}
	
	public void changeStatus(String status) {
		this.lblStatus.setText(status);
	}
	
	public void changeColour(String text) {
		this.lblColour.setText(text);
	}
	
	public void updateTurn() {
		String text = "Turn: ";
		if (this.board.turn.white) {
			text = text + "White";
		} else {
			text = text + "Black";
		}
		
		if (this.board.check) {
			this.chatAdd("Check", true);
		}
		
		if (this.board.end) {
			
			text = "Winner: " + this.board.winner.name;
			this.chatAdd(this.board.winner.name + " won!", true);
		}
		
		this.lblTurn.setText(text);
	}
}
