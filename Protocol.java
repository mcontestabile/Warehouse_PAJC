package it.unibs.pajc.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import it.unibs.pajc.warehouse.LoginModel;
import it.unibs.pajc.warehouse.WareHouseModel;

public class Protocol implements Runnable {
	private BufferedReader in;
	private PrintWriter out;
	private static ArrayList<Protocol> clients = new ArrayList<>();
	private Socket client;
	private LoginModel user;
	private boolean isRunning = true;
	private WareHouseModel wModel = new WareHouseModel();
	
	
	private static HashMap<String, Consumer<ClientEvent>> commandMap; // Con le prorpietà statiche, si può inizializzare inline.
	/* Verrà svolto una volta sola e inizializzerà l'oggetto commandMap.
	static {
		commandMap = new HashMap<>();
		commandMap.put("@LIST", e -> e.sender.listClient(e.sender));
		commandMap.put("@ALL", e -> e.sender.sendToAll(e.sender, e.getLastParameters()));
		commandMap.put("@TIME", e -> e.sender.sendMessage(e.sender, LocalDateTime.now().toString()));
		commandMap.put("@QUIT", e -> e.sender.close());
		commandMap.put("@default@", e -> e.sender.sendMessage(e.sender, e.getLastParameters()));
	}
	 */

	public Protocol(Socket client) {
		this.client = client;
		clients.add(this);
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(
					new InputStreamReader(client.getInputStream())); // Socket client diventa una presa per client-server, sono collegati fra loro.
			
			out = new PrintWriter(client.getOutputStream(), true);  // Vedere l'output, i pacchetti, inviato dal server.
			
			System.out.println("Client connesso » " + client.getPort());
			
			boolean convertToUpper = true;
			String request;
			
			while(isRunning) {
				System.out.println("Processing request » " + request);
				
				ClientEvent ce = ClientEvent.parse(this, request);
				Consumer<ClientEvent> commandExe = ce.command != null ?
						commandMap.get(ce.command.toUpperCase()) : commandMap.get("@default@");
				
				commandExe.accept(ce);
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
				 * 
				 * La Classe ClientEvent si occupa di comporre i messaggi in arrivo
				 * al server in blocchi: parametro in input e comando in output.
				 * Il comando è implementato nell'apposito metodo.
				 */
				
				/*
				String command = request.toUpperCase();
				
				if("@LIST".equals(command))
					listClient(this);
				else if("@QUIT".equals(command))
					break;
				else 
					sendToAll(this, request);
			    */
				
				/*
				String response = (convertToUpper) ? request.toUpperCase() : request.toLowerCase();
				
				if("!TOUPPER".equals(command))
					convertToUpper = true;
				else if("!TOLOWER".equals(command))
					convertToUpper = false;
				else if("@QUIT".equals(command))
					break;
				else
					out.println(response);
			    */
			}
			
			out.printf("Arrivederci.");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			clients.remove(this); // devo togliermi per cessare il tutto, ovvero avere il server senza client collegati.
		}
	}

	/*
	protected void sendToAll(Protocol sender, String message) {
		//Ogni oggetto che implementa un'interfaccia lista può essere elaborato come flusso di informazione.
		clients.forEach(c -> c.sendMessage(sender, message));
		
		/*
		for(Protocol c : clients) {
			c.sendMessage(sender, message);
		}
		*/
	}
	*/

    /*
	protected void sendMessage(Protocol sender, String message) {
		this.out.printf("[%s] » %s\n", sender.clientNameString, message);
		this.out.flush();
	}
	
	protected void listClient(Protocol sender) {
		StringBuilder msg = new StringBuilder();
		clients.forEach(c -> sendMessage(c, String.format("%s.\n", c.clientNameString)));
		
		this.sendMessage(sender,  msg.toString());
	}
	*/

/*
	private void close() {
		isRunning = false;
	}

 */



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
