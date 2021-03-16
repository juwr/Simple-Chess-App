package main;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Server implements SCInterface {
	java.net.Socket client;
	GUI gui;
	String name;
	public String out;

	Server(String name, GUI gui) {
		this.gui = gui;
		this.name = name;
		
		try {
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void start() throws IOException {
		int port = 8999;
		java.net.ServerSocket serverSocket = new java.net.ServerSocket(port);
		
		this.gui.changeStatus("Status: Waiting for connection");
		
		this.client = warteAufAnmeldung(serverSocket);
		
		startProtocol();
	}

	java.net.Socket warteAufAnmeldung(java.net.ServerSocket serverSocket) throws IOException {
		java.net.Socket socket = serverSocket.accept(); // blockiert, bis sich ein Client angemeldet hat
		return socket;
	}

	String getMessage(java.net.Socket socket) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		char[] buffer = new char[200];
		int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
		String message = new String(buffer, 0, anzahlZeichen);
		System.out.println("Received: " + message);
		return message;
	}

	void sendMessage(java.net.Socket socket, String message) throws IOException {
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		printWriter.print(message);
		printWriter.flush();
		System.out.println("Sending: " + message);
	}
	
	
	private void startProtocol() {
		this.gui.board.socket = this;
		
		Object[] options = {"White", "Black"};
		int choice = JOptionPane.showOptionDialog(gui, "Want to be white or black?", "Choose an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		
		if (choice == 0) {
			this.gui.board.player = this.gui.board.p1;
		} else {
			this.gui.board.player = this.gui.board.p2;
			choice = 1;
		}
		
		this.gui.board.player.name = name;
		
		
		
		greeting(choice);
		
		if (this.gui.board.getOther().name.isEmpty()) {
			this.gui.changeStatus("Status: Connection failed");
		} else {
			this.gui.changeStatus("Status: Connected to user " + this.gui.board.getOther().name);
			this.gui.chatAdd("Chat connected.", true);
			if (this.gui.board.player.white) {
				this.gui.changeColour("You are: White");
			} else {
				this.gui.changeColour("You are: Black");
			}
			this.gui.ready = true;
		}
		
		
		this.gui.updateTurn();
		
		loop();
		
	}
	
	private void greeting(int choice) {
		int count = 0;
		Pattern pattern = Pattern.compile("PLAYER;(\\w+);(\\d)");
		while (count < 10) {
			String message = "PLAYER;" + this.name + ";" + choice;
			try {
				sendMessage(client, message);
				String incoming = getMessage(client);
				
				if (Pattern.matches("PLAYER;\\w+;\\d", incoming)) {
					Matcher matcher = pattern.matcher(incoming);
					matcher.find();
					int choice2 = Integer.parseInt(matcher.group(2));
					
					if (choice == choice2) {
						//TODO Error.
					} else {
						this.gui.board.getOther().name = matcher.group(1);
						
						sendMessage(client, "OK");
						return;
					}
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
				TimeUnit.SECONDS.sleep(1);
				String message = this.out;
				sendMessage(client, message);
				
				if (this.out.equals(message)) {
					this.out = "K";
				}
				
				String incoming = getMessage(client);
				
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
							// TODO Error.
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