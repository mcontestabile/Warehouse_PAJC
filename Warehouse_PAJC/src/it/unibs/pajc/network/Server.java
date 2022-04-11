package it.unibs.pajc.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import it.unibs.pajc.utilities.UsefulStrings;
import it.unibs.pajc.warehouse.Article;
import it.unibs.pajc.warehouse.WareHouseController;
import it.unibs.pajc.warehouse.WareHouseModel;

/**
 * Where is the actual main game model being held? On each client or on the server?
 * It should be on the server, and the clients simply pass requests to the server that
 * the model should be changed. Once changed, the server should send updated data to
 * all the models so that their graphical representation of the model (their local view)
 * can be properly updated.
 * 
 * @author martinacontestabile
 *
 */
public class Server {

	static WareHouseController controller = new WareHouseController();
	static WareHouseModel model = controller.getWarehouse();
	
	public static void main(String[] args) throws IOException {

 		// Attiviamo il servizio su questa porta, non può essere modificata.
 		final int port = 1234;

 		
 		ServerSocket server = new ServerSocket(port); // Server che vogliamo creare.

 		System.out.println("Il server è stato avviato.");
		/*
		 *  Creare un server significa creare una risorsa a livello di Classe Java.
		 *  Gli standard ISO/OSI hanno servizi che permettono al layer applicativo
		 *  di non preoccuparsi di come avvengono le cose a livelli più bassi.
		 *  
		 *  Il Server è nel try per chiuderlo in modo corretto appena finito il programma.
		 */
		try {
			while (true) {
				/*
				 * Ora dobbiamo mettere in ascolto il server e accettare le richieste di comunicazione.
				 * Come si può fare? Il server ha un metodo che si chiama accept che, finché non
				 * faccio una richiesta al server, rimane in ascolto. Appena un client si connette,
				 * il server restituisce un oggetto che è il client stesso, una socket.
				 */
				Socket client = server.accept();
				Protocol protocol = new Protocol(client);
				Thread clientThread = new Thread(protocol);
				clientThread.start();
			}
		} catch (IOException e) {
			System.err.println("Errore di comunicazione » " + e);
		} finally {
			server.close();
 		}

 		System.out.println("Uscita dal server.");

 	}
}

/*
 * if you use a classic model/view pattern then this is straighforward.
 * Your «model» classes encapsulate the current state of the game (who
 * has which cards, whose turn is it next etc) in a purely logical form
 * (no user interface artifacts). Your «view» classes display the current
 * state as a GUI or whatever (no involvement in how that state evolved).
 * The model is maintained on the server. The GUI(s) live on the client(s).
 * All clients have a thread that sits in a loop listening on an
 * ObjectInputStream on their server connection. Whenever anything happens
 * to change the model the server sends the latest state to all the clients.
 * When a client receives a new state it displays it. When the user does something
 * the client sends the corresponding request to the server, which updates
 * the model (and thus sends its latest state to all clients).
 */
