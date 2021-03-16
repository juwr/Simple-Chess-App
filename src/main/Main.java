package main;


import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import game.Board;

public class Main {

	public static void main(String[] args) {
		
		Board board = new Board();
		
		GUI gui = new GUI(board);
		gui.setVisible(true);

		
		Object[] options = {"Host", "Join"};
		int choice = JOptionPane.showOptionDialog(gui, "Host or Join a Game?", "Choose an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		
		boolean end = false;
		String name = "Anonymous";
		while (!end) {
			name = JOptionPane.showInputDialog("Type your name. No special characters please.");
			if (name == null) {
				name = "Anonymous";
			}
			if (Pattern.matches("\\w+", name)) {
				end = true;
			}
		}
		
		
		
		
		if (choice == 0) {
			Server server = new Server(name, gui);
		} else {
			
			end = false;
			String ip = "127.0.0.1"; //localhost
			while (!end) {
				ip = JOptionPane.showInputDialog("Type the ip adress of the host. Type 'localhost', if you want are hosting on the same machine.");
				if (ip == null || ip.equals("localhost")) {
					ip = "127.0.0.1"; //localhost
				}
				if (Pattern.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$", ip)) {
					end = true;
				}
			}
			
			Client client = new Client(name, gui, ip);
		}
		
	}
}
