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
	private Socket client;
	private boolean isRunning = true;
	//private String clientNameString;
	
	ArrayList<Protocol> clients = new ArrayList<>();
	Object lock = new Object();
	
	private Article article;
	private int units;
	private  String version;
	
	WareHouseController controller = new WareHouseController();
	WareHouseModel model = new WareHouseModel();

	public Protocol(Socket client) throws IOException {
		this.client = client;
		this.in = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));       // Vedere l'output, i pacchetti, inviato dal server.
	    this.out = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));   // Socket client diventa una presa per client-server, sono collegati fra loro.
	}

	@Override
	public void run() {
		try {
			//clientNameString = JOptionPane.showInputDialog(UsefulStrings.INSERT_YOUR_NAME);
			
			System.out.println("Client connesso » " + client.getPort() + " " + client.toString());

			String request;
			while (isRunning && (request = (String) in.readObject()) != null) {
				//request = (String) in.readObject();

				
				switch (request) {
					case "Model e Controller":
						System.out.println("\n\n\n\n");
						System.out.println("Invio il model: " + model);
						out.writeObject(model);
						System.out.println("Ho inviato il model: " + model);
						System.out.println("\n\n\n\n");
						System.out.println("Invio il controller: " + controller);
						out.writeObject(controller);
						System.out.println("Ho inviato il controller: " + controller);
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

						System.out.println("Nuova quantità di " + version + " » " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
						System.out.println("Model " + model + " aggiornato.");
						out.writeObject(model);
						out.writeObject(controller);
						System.out.println("Model " + model + " e controller " + controller + " inviati.");
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

						System.out.println("Vecchia quantità di " + version + " » " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));

						model = controller.processOrder(units, version, article);

						System.out.println("Nuova quantità di " + version + " » " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
						System.out.println("Model " + model + " aggiornato.");
						out.writeObject(model);
						out.writeObject(controller);
						System.out.println("Model " + model + " e controller " + controller + " inviati.");
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


						boolean doesArticleExist = controller.checkNameAvailability(name, model);

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

						model = controller.getWarehouse();

						System.out.println("Invio il model: " + model);
						out.writeObject(model);
						out.writeObject(controller);
						System.out.println("Ho inviato il model: " + model);
						System.out.println("\n\n\n\n");

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

	public void send(WareHouseModel model) throws IOException {
		this.out.writeObject(model);
		this.out.flush();
		
	}

	private void close() {
		isRunning = false;
	}

	private void quit() {
		clients.remove(this); // devo togliermi per cessare il tutto, ovvero avere il server senza client collegati.
		System.out.println("Client " + clientNameString + " disconnesso.");
		if (clients.size() == 0) close();
	}


	public void send(WareHouseController controller) throws IOException {
		this.out.writeObject(controller);
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
