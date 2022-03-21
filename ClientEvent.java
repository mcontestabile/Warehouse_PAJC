package it.unibs.pajc.network;

import java.util.ArrayList;

public class ClientEvent {
	String command;
	ArrayList<String> parameters = new ArrayList<>();
	Protocol sender; // client da cui abbiamo ricevuto il messaggio da processare.
	
	/*
	 * private per non far creare eventi a qualcuno. Per crearli devo passare il comando
	 * in modo esplicito e l'arraylist.
	 */
	private ClientEvent(Protocol sender, String command, ArrayList<String> parameters) {
		this.sender = sender;
		this.command = command;
		this.parameters = parameters;
	}
	
	public static ClientEvent parse(Protocol sender, String message) {
		String command = null;
		ArrayList<String> parameters = new ArrayList<>();
		
		// parsing
		if(message.startsWith("@")) {
			String[] tokens = message.split(":");
			command = tokens[0];
			
			for(int i = 0; i< tokens.length; i++) {
				parameters.add(tokens[i]);
			}
		} else {
			parameters.add(message);
		}
		
		return new ClientEvent(sender, command, parameters);
	}
	
	public String getLastParameters() {
		return parameters.size() > 0 ? parameters.get(parameters.size() - 1) : "";
	}
}
