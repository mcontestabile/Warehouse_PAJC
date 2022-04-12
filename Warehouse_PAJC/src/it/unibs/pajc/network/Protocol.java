package it.unibs.pajc.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;

import it.unibs.pajc.utilities.UsefulStrings;
import it.unibs.pajc.warehouse.*;


/**
 * E' il controller del programma, permette alla view di dialogare
 * con model runtime, fa da tramite nello scambio di informazioni e dati.
 * 
 * 
 * @author martinacontestabile
 *
 */
public class Protocol implements Runnable {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	//private static ArrayList<Protocol> clients = new ArrayList<>();
	private Socket client;
	private boolean isRunning = true;
	private String clientNameString;

	private Article article;
	private int units;
	private String version;
	
	private WareHouseController controller;
	
	/*
	WareHouseController controller = new WareHouseController();
	WareHouseModel model = controller.getWarehouse();
	 */
	public Protocol(Socket client) throws IOException {
		out = new ObjectOutputStream(client.getOutputStream());  // Vedere l'output, i pacchetti, inviato dal server.
		in = new ObjectInputStream(client.getInputStream());     // Socket client diventa una presa per client-server, sono collegati fra loro.
		
		this.client = client;
		//Server.clients.add(client);
	}

	@Override
	public void run() {
		try {
			JFrame jf=new JFrame();
			jf.setAlwaysOnTop(true);
			clientNameString = JOptionPane.showInputDialog(jf, UsefulStrings.INSERT_YOUR_NAME);


			System.out.println("Client connesso » " + client.getPort() + " " + clientNameString);

			String request;
			while (isRunning && (request = (String) in.readObject()) != null) {
				//request = (String) in.readObject();

				switch (request) {
				case "Controller":
					System.out.println("\n\n\n\n");
					System.out.println("Invio il controller: " + Server.controller);
					send(Server.controller);
					System.out.println("Ho inviato il controller: " + Server.controller);
					System.out.println("\n\n\n\n");
					break;
					
				case "Ordine":
					article = (Article) in.readObject();
					controller = (WareHouseController) in.readObject();
					units = in.readInt();
					version = (String) in.readObject();
					//model = controller.getWarehouse();

					System.out.println("\n\n\n\n");
					System.out.println("Processo l'ordine " + request);
					System.out.println("Articolo " + article.getName());
					System.out.println("Unità " + units);
					System.out.println("Versione " + version);

					System.out.println("Vecchia quantità di " + version + " » " + controller.getWarehouse().getArticle(article.getName()).getQuantity(new ArrayList<String>(controller.getWarehouse().getArticle(article.getName()).getVersions().keySet()).indexOf(version)));

					Server.model = controller.settingNewUnits(units, version, article);
					Server.controller = controller;

					System.out.println("Nuova quantità di " + version + " » " + Server.model.getArticle(article.getName()).getQuantity(new ArrayList<String>(Server.model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
					System.out.println("Controller " + Server.controller + " aggiornato.");
					sendToAll(Server.controller);
					System.out.println("Controller " + Server.controller + " inviato.");

					out.reset();

					break;

			   /*
				* La view mi manda il controller, aggiorno il model
				* e invio model e controller aggiornati.
				*/
				case "Acquisto":
					article = (Article) in.readObject();
					controller = (WareHouseController) in.readObject();
					units = in.readInt();
					version = (String) in.readObject();
					//model = controller.getWarehouse();

					System.out.println("\n\n\n\n");
					System.out.println("Processo l'ordine " + request);
					System.out.println("Articolo " + article.getName());
					System.out.println("Unità " + units);
					System.out.println("Versione " + version);

					int available = controller.getWarehouse().getArticle(article.getName()).getQuantity(new ArrayList<String>(controller.getWarehouse().getArticle(article.getName()).getVersions().keySet()).indexOf(version));
					if (available < controller.getWarehouse().getArticle(article.getName()).getMinimum(new ArrayList<String>(controller.getWarehouse().getArticle(article.getName()).getVersions().keySet()).indexOf(version))) {
						JOptionPane.showMessageDialog(null, UsefulStrings.APOLOGISE);
					} else {
						System.out.println("Vecchia quantità di " + version + " » " + Server.model.getArticle(article.getName()).getQuantity(new ArrayList<String>(controller.getWarehouse().getArticle(article.getName()).getVersions().keySet()).indexOf(version)));

						Server.model = controller.processOrder(units, version, article);
						Server.controller = controller;

						System.out.println("Nuova quantità di " + version + " » " + Server.model.getArticle(article.getName()).getQuantity(new ArrayList<String>(Server.model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
						System.out.println("Controller " + Server.controller + " aggiornato.");
						sendToAll(Server.controller);
						System.out.println("Controller " + Server.controller + " inviato.");

						out.reset();
					}

					break;


				case "Nuova merce":

					System.out.println("\n\n\n\n");

					controller = (WareHouseController) in.readObject();
					String name = (String) in.readObject();
					int quantity = in.readInt();
					int price = in.readInt();
					int minimum = in.readInt();
					int maximum = in.readInt();
					String versionName = (String) in.readObject();
					ImageIcon nIcon = (ImageIcon) in.readObject();
					ImageIcon vIcon = (ImageIcon) in.readObject();


					boolean doesArticleExist = controller.checkNameAvailability(name, Server.model);

					if (doesArticleExist == false){
						int[] prices = {price};
						int[] quantities = {quantity};
						int[] minimums = {minimum};
						int[] maximums = {maximum};
						Article newArticle = new Article(name, new LinkedHashMap<String, ImageIcon>(){
							{
								put(versionName, vIcon);
							}}, prices, quantities, maximums, minimums, nIcon);
						controller.getWarehouse().setProduct(newArticle);
					} else {
						controller.addNewVersion(name, quantity, price, minimum, maximum, versionName, vIcon);
					}

					Server.model = controller.getWarehouse(); //TODO controlla che la merce si aggiunga davvero!!!
					Server.controller = controller;

					System.out.println("Invio il model: " + Server.controller);
					sendToAll(Server.controller);
					System.out.println("Ho inviato il model: " + Server.controller);
					System.out.println("\n\n\n\n");

					out.reset();

					break;


				case "Esci":
					close();
					break;

				default:
					break;
				}

				try {
					this.out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			quit();
		}
	}

	private void close() {
		isRunning = false;
	}

	private void quit() {
		Server.clients.remove(this); // devo togliermi per cessare il tutto, ovvero avere il server senza client collegati.
		System.out.println("Client " + clientNameString + " disconnesso.");
		if (Server.clients.size() == 0) close();
	}

	public void sendToAll(WareHouseController controller) throws IOException {
		System.out.println("Client connessi:");
		for(Protocol client : Server.clients) {
			System.out.println(client.clientNameString);
			client.sendUpdate(Server.controller);
		}
	}

	public void sendUpdate(WareHouseController controller) throws IOException {
		this.out.writeObject("Aggiornato");
		this.out.writeObject(controller);
		this.out.flush();
		this.out.reset();
	}

	public void send(WareHouseController controller) throws IOException {
		this.out.writeObject("Controller");
		this.out.writeObject(controller);
		this.out.flush();
		this.out.reset();
	}
}


/*
 * Serve:
 * 1. Array list dei client connessi.
 * 2. Metodo sendToAll(...) per inviare a tutte le persone connesse uno stesso messaggio.
 * 3. Comando @LIST che stampa tutti gli utenti collegati.
 * 
 * 
 * Come si crea un client? Fino ad ora, abbiamo sfruttato telnet.
 * Ci sono due macchine a stati: client e server.
 * Il client ha due thread, uno per ciascun server, dove il primo gestisce i
 * messaggu client-server e l'altro i messasggi server-client.
 */