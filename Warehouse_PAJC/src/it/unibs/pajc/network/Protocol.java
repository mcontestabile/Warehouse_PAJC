package it.unibs.pajc.network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.swing.*;

import it.unibs.pajc.utilities.UsefulStrings;
import it.unibs.pajc.warehouse.*;

public class Protocol implements Runnable {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private static ArrayList<Protocol> clients = new ArrayList<>();
	private Socket client;
	private boolean isRunning = true;
	private String clientNameString;

	private Article article;
	private int units;
	private  String version;

	private WareHouseController controller;
	private WareHouseModel model;
	/*
	WareHouseController controller = new WareHouseController();
	WareHouseModel model = controller.getWarehouse();
	 */
	public Protocol(Socket client) throws IOException {
		out = new ObjectOutputStream(client.getOutputStream());  // Vedere l'output, i pacchetti, inviato dal server.
		in = new ObjectInputStream(client.getInputStream());     // Socket client diventa una presa per client-server, sono collegati fra loro.
		
		this.client = client;
		clients.add(this);
	}

	@Override
	public void run() {
		try {
			clientNameString = JOptionPane.showInputDialog(UsefulStrings.INSERT_YOUR_NAME);


			System.out.println("Client connesso » " + client.getPort() + " " + clientNameString);

			String request;
			while (isRunning && (request = (String) in.readObject()) != null) {
				//request = (String) in.readObject();

				switch (request) {
				case "Model":
					System.out.println("\n\n\n\n");
					System.out.println("Invio il model: " + Server.model);
					send(Server.model);
					System.out.println("Ho inviato il model: " + Server.model);
					System.out.println("\n\n\n\n");
					break;

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
					model = (WareHouseModel) in.readObject();
					units = in.readInt();
					version = (String) in.readObject();
					//model = controller.getWarehouse();

					System.out.println("\n\n\n\n");
					System.out.println("Processo l'ordine " + request);
					System.out.println("Articolo " + article.getName());
					System.out.println("Unità " + units);
					System.out.println("Versione " + version);

					System.out.println("Vecchia quantità di " + version + " » " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));

					model = controller.settingNewUnits(units, version, article);
					Server.model = model;
					Server.controller = controller;

					System.out.println("Nuova quantità di " + version + " » " + Server.model.getArticle(article.getName()).getQuantity(new ArrayList<String>(Server.model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
					System.out.println("Model " + model + " aggiornato.");
					sendToAll(Server.model, Server.controller);
					System.out.println("Model " + model + " e controller " + controller + " inviati.");

					out.reset();

					break;

					/*
					 * La view mi manda il controller, aggiorno il model e invio il model aggiornato.
					 */
				case "Acquisto":
					article = (Article) in.readObject();
					controller = (WareHouseController) in.readObject();
					model = (WareHouseModel) in.readObject();
					units = in.readInt();
					version = (String) in.readObject();
					//model = controller.getWarehouse();

					System.out.println("\n\n\n\n");
					System.out.println("Processo l'ordine " + request);
					System.out.println("Articolo " + article.getName());
					System.out.println("Unità " + units);
					System.out.println("Versione " + version);

					int available = model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version));
					if (available < model.getArticle(article.getName()).getMinimum(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version))) {
						JOptionPane.showMessageDialog(null, UsefulStrings.APOLOGISE);
					} else {
						System.out.println("Vecchia quantità di " + version + " » " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));

						Server.model = controller.processOrder(units, version, article);
						Server.controller = controller;

						System.out.println("Nuova quantità di " + version + " » " + Server.model.getArticle(article.getName()).getQuantity(new ArrayList<String>(Server.model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
						System.out.println("Model " + model + " aggiornato.");
						sendToAll(Server.model, Server.controller);
						System.out.println("Model " + model + " e controller " + controller + " inviati.");

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

					System.out.println("Invio il model: " + model);
					sendToAll(Server.model, Server.controller);
					System.out.println("Ho inviato il model: " + model);
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
				/*
				 * Se command è vuoto, cosa vado ad eseguire?
				 */


				// sendToAll(this, request);

				/*
				 * Come evitare tutti questi if-else?
				 * Serve creare una matrice che mappi comandi e azioni.
				 * A sinistra comando, a destra lista di client.
				 * Il comando @QUIT corrisponde all'uscita dal client.
				 * E' possibile creare una mia sintassi che permette
				 * al protocollo di svolgere una certa operazione.
				 *
				 * La prima cosa da fare per verificare la validità di un comando
				 * è quella di fare il parsing delle stringhe.
				 * La seconda cosa da fare è quella di determinare il formato del
				 * comando, in modo da permettere il parsing e la lettura corretta.
				 * La terza cosa da fare è associare il comando ai parametri corretti.
				 */
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
		clients.remove(this); // devo togliermi per cessare il tutto, ovvero avere il server senza client collegati.
		System.out.println("Client " + clientNameString + " disconnesso.");
		if (clients.size() == 0) close();
	}

	public void sendToAll(WareHouseModel model, WareHouseController controller) throws IOException {
		for(Protocol client : clients)
			client.sendUpdate(client, model, controller);
	}

	public void sendUpdate(Protocol client, WareHouseModel model, WareHouseController controller) throws IOException {
		this.out.writeObject(model);
		this.out.writeObject(controller);
		this.out.flush();
	}

	public void send(WareHouseController controller) throws IOException {
		this.out.writeObject(controller);
		this.out.flush();
	}

	public void send(WareHouseModel model) throws IOException {
		this.out.writeObject(model);
		this.out.flush();
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