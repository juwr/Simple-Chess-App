package main;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import game.Cell;

public class Client implements SCInterface {
	java.net.Socket server;
	GUI gui;
	String name;
	private String out;
	private String ip;
	
	Client(String name, GUI gui, String ip) {
		this.gui = gui;
		this.name = name;
		this.ip = ip;
		
		try {
			start();
		} catch (Exception e) {
			this.gui.changeStatus("Status: Connection failed");
		}
	}

	void start() throws IOException, InterruptedException {
		int port = 8999;
		int count = 0;
		this.gui.changeStatus("Status: Waiting for connection");
		while (count < 60) {
			try {
				server = new java.net.Socket(ip, port); // verbindet sich mit Server
				break;
			} catch (Exception e) {
				count++;
				TimeUnit.SECONDS.sleep(1);
			}
		}
		
		
		startProtocol();
	}

	String getMessage(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
		String message = new String(buffer, 0, anzahlZeichen);
		System.out.println("Received:\t" + message);
		return message;
	}

	void sendMessage(java.net.Socket socket, String message) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
		System.out.println("Sending:\t" + message);
	}
	
	
	private void startProtocol() {
		this.gui.board.socket = this;
		
		this.gui.changeStatus("Status: Waiting for connection");
		
		greeting();
		
		this.gui.changeStatus("Status: Connected to user " + this.gui.board.getOther().name);
		this.gui.chatAdd("Chat connected.", true);
		if (this.gui.board.player.white) {
			this.gui.changeColour("You are: White");
		} else {
			this.gui.changeColour("You are: Black");
		}
		this.gui.ready = true;
		
		this.gui.board.player.name = name;
		this.gui.updateTurn();
		
		loop();
		
	}

	private void greeting() {
		int count = 0;
		Pattern pattern = Pattern.compile("PLAYER;(\\w+);(\\d)");
		while (count < 10) {
			try {
				String incoming = getMessage(server);
				
				if (Pattern.matches("PLAYER;\\w+;\\d", incoming)) {
					Matcher matcher = pattern.matcher(incoming);
					matcher.find();
					int choice = Integer.parseInt(matcher.group(2));
					
					if (choice == 0) {
						this.gui.board.player = this.gui.board.p2;
						this.gui.board.getOther().name = matcher.group(1);
						choice = 1;
					} else if (choice == 1) {
						this.gui.board.player = this.gui.board.p1;
						this.gui.board.getOther().name = matcher.group(1);
						choice = 0;
					} else {
						//TODO Error.
						this.gui.chatAdd("ERROR", true);
					}
					
					String message = "PLAYER;" + this.name + ";" + choice;
					sendMessage(server, message);
					
					incoming = getMessage(server);
					
					if (incoming.equals("OK")) {
						return;
					} else {
						//TODO Error.
					}
					
				} else {
					sendMessage(server, "ERROR");
				}
			} catch (IOException e) {
				this.gui.changeStatus("Status: Connection failed");
			}
			
			count++;
		}
	}
	
	private void loop() {
		this.out = "K";
		Pattern move = Pattern.compile("MOVE;(\\d),(\\d);(\\d),(\\d);(\\d)");
		Pattern send = Pattern.compile("SEND;(.+)");
		boolean end = false;
		while(!end) {
			try {
				
				String incoming = getMessage(server);
				
				if (!incoming.equals("K")) {
					
					if (Pattern.matches("MOVE;(\\d),(\\d);(\\d),(\\d);(\\d)", incoming)) {
						Matcher matcher = move.matcher(incoming);
						matcher.find();
						
						int x1 = Integer.parseInt(matcher.group(1));
						int y1 = Integer.parseInt(matcher.group(2));
						int x2 = Integer.parseInt(matcher.group(3));
						int y2 = Integer.parseInt(matcher.group(4));
						int mod = Integer.parseInt(matcher.group(5));
						
						if ((x1 + y1 + x2 + y2) % 8 == mod) {
							
							this.gui.chatAdd("Moving: " + this.gui.board.translate(x1, y1) + " to " + this.gui.board.translate(x2, y2), true);
							
							this.gui.movePiece(this.gui.board.getCell(x1, y1), this.gui.board.getCell(x2, y2));
							
						} else {
							//TODO Error.
						}
					} else if (Pattern.matches("SEND;(.+)", incoming)) {
						Matcher matcher = send.matcher(incoming);
						matcher.find();
						
						this.gui.chatAdd(this.gui.board.getOther().name + ": " + matcher.group(1), false);
					} else if (Pattern.matches("SURRENDER", incoming)) {
						
						this.gui.board.endGame(this.gui.board.getOther());
						this.gui.updateTurn();
						this.gui.chatAdd(this.gui.board.getOther().name + " surrendered", true);
					}
					
				}
				
				
				
				
				
				String message = this.out;
				sendMessage(server, message);
				TimeUnit.SECONDS.sleep(1);
				
				if (this.out.equals(message)) {
					this.out = "K";
				}
				
			} catch (Exception e) {
				end = true;
				
				this.gui.changeStatus("Status: Connection closed");
				this.gui.chatAdd("Chat disconnected.", true);
				this.gui.ready = false;
			}
		}
	}
	
	public void send(String message) {
		int count = 0;
		while (count < 10) {
			if (!this.out.equals("K")) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				this.out = message;
				return;
			}
		}
		
	}
}