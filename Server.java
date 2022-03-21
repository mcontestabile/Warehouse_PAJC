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
	private ArrayList<Article> products;

	/**
	 * Costruttore dell'oggetto {@code Magazzino},
	 * è una collezione di oggetti {@code Articolo},
	 * questi ultimi dotati di determinate proprietà,
	 * definite a priori nella loro classe.
	 * @param articles
	 */
	public Server() {

		products = new ArrayList<>();

		products.add(new Article(
				"Apple iPhone 13",
				new LinkedHashMap<String, ImageIcon>() {/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("iPhone Blu", new ImageIcon(UsefulStrings.BLUE_IPHONE));
						put("iPhone Galassia", new ImageIcon(UsefulStrings.WHITE_IPHONE));
						put("iPhone Mezzanotte", new ImageIcon(UsefulStrings.MIDNIGHT_IPHONE));
						put("iPhone Product Red", new ImageIcon(UsefulStrings.RED_IPHONE));
						put("iPhone Rosa", new ImageIcon(UsefulStrings.PINK_IPHONE));
					}},
				new int[] {939, 939, 939, 939, 939},
				new int[]{25, 25, 25, 25, 25},
				new int[]{250, 250, 250, 250, 250},
				new int[]{400, 400, 400, 400, 400},
				300,
				new ImageIcon(UsefulStrings.IPHONES)
				));

		products.add(new Article(
				"Samsung Galaxy S22",
				new LinkedHashMap<String, ImageIcon>(){/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("Versione Burgundy", new ImageIcon(UsefulStrings.BURGUNDY));
						put("Versione Phantom Black", new ImageIcon(UsefulStrings.PB));
						put("Versione Phantom White", new ImageIcon(UsefulStrings.PW));
						put("Versione Verde", new ImageIcon(UsefulStrings.GREEN));
					}},
				new int[] {879, 879, 879, 879},
				new int[]{12, 12, 12, 12},
				new int[]{240, 240, 240, 240},
				new int[]{700, 700, 700, 700},
				200,
				new ImageIcon(UsefulStrings.GALAXY_S22)
				));

		products.add(new Article(
				"Smart TV",
				new LinkedHashMap<String, ImageIcon>() {/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("Smart TV Samsung 55", new ImageIcon(UsefulStrings.TV_2));
					}},
				new int[] {450},
				new int[] {10},
				new int[] {100},
				new int[] {650},
				50,
				new ImageIcon(UsefulStrings.TV_1)
				));

		products.add(new Article(
				"Apple iMac 24''",
				new LinkedHashMap<String, ImageIcon>(){/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("iMac Arancione", new ImageIcon(UsefulStrings.IMAC_ORANGE));
						put("iMac Argento", new ImageIcon(UsefulStrings.IMAC_WHITE));
						put("iMac Blu", new ImageIcon(UsefulStrings.IMAC_BLUE));
						put("iMac Giallo", new ImageIcon(UsefulStrings.IMAC_YELLOW));
						put("iMac Rosa", new ImageIcon(UsefulStrings.IMAC_PINK));
						put("iMac Rosso", new ImageIcon(UsefulStrings.IMAC_RED));
						put("iMac Verde", new ImageIcon(UsefulStrings.IMAC_GREEN));
						put("iMac Viola", new ImageIcon(UsefulStrings.IMAC_VIOLET));
					}},
				new int[] {1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500},
				new int[]{70, 70, 70, 70, 70, 70, 70},
				new int[]{300, 300, 300, 300, 300, 300, 300},
				new int[]{245, 245, 245, 245, 245, 245, 245}, 400, new ImageIcon(UsefulStrings.IMAC)
				));

		products.add(new Article(
				"Microsoft Surface",
				new LinkedHashMap<String, ImageIcon>(){/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("Surface Argento", new ImageIcon(UsefulStrings.SURFACE_WHITE));
						put("Surface Nero", new ImageIcon(UsefulStrings.SURFACE_BLACK));
					}},
				new int[]{899, 899},
				new int[]{15, 15},
				new int[]{250, 250},
				new int[]{600, 600},
				30,
				new ImageIcon(UsefulStrings.SURFACES)
				));

		products.add(new Article(
				"Cuffie bluethoot",
				new LinkedHashMap<String, ImageIcon>(){/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("AirPods 3° generazione", new ImageIcon(UsefulStrings.AIRPODS3));
						put("Galaxy Buds 2", new ImageIcon(UsefulStrings.GALAXY_BUDS));
						put("LG", new ImageIcon(UsefulStrings.LG));
					}},
				new int[] {180, 150, 70}, //prezzo
				new int[]{80, 80, 80}, //quantità iniziali
				new int[]{30, 30, 30}, //minimo
				new int[]{300, 300, 300}, //massimo
				20,
				new ImageIcon(UsefulStrings.CUFFIE)
				));

		products.add(new Article(
				"Alexa",
				new LinkedHashMap<String, ImageIcon>() {/**
				 * 
				 */
					private static final long serialVersionUID = 1L;

					{
						put("Echo Dot 3° generazione", new ImageIcon(UsefulStrings.AMAZON_DOT));
						put("Echo Dot 4° generazione", new ImageIcon(UsefulStrings.AMAZON_ECHO));
					}},
				new int[] {99, 119},
				new int[]{25, 25}, new int[] {100, 100},
				new int[]{500, 500},
				50,
				new ImageIcon(UsefulStrings.ALEXA)
				));

		products.add(new Article(
				"HomePod mini",
				new LinkedHashMap<String, ImageIcon> () {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					{
						put("HomePod Arancione", new ImageIcon(UsefulStrings.HOMEPOD_ORANGE));
						put("HomePod Bianco", new ImageIcon(UsefulStrings.HOMEPOD_WHITE));
						put("HomePod Blu", new ImageIcon(UsefulStrings.HOMEPOD_BLUE));
						put("HomePod Giallo", new ImageIcon(UsefulStrings.HOMEPOD_YELLOW));
						put("HomePod Nero", new ImageIcon(UsefulStrings.HOMEPOD_BLACK));
					}},
				new int[] {99, 99, 99, 99, 99},
				new int[]{15, 15},
				new int[] {250, 250},
				new int[]{450, 450},
				25,
				new ImageIcon(UsefulStrings.HOMEPOD)
				));
	}

	/**
	 * Getter
	 * @return articoli nel magazzino
	 */
	public ArrayList<Article> getProducts() {
		return products;
	}

	public List<String> productsNames() {
		List<String> a = new ArrayList<>();

		for (Article article : products)
			a.add(article.getName());

		return a;
	}

	public Map<String, ImageIcon> getSubcategories() {
		Map<String, ImageIcon> subMap = new LinkedHashMap<>();

		for (int i = 0; i < products.size(); i++) 
			subMap.putAll(products.get(i).getVersions());

		return subMap;
	}

	/**
	 * Setter permette di aggiungere articoli al {@code Magazzino}.
	 * Inoltre, permette l'inserimento del rifornimento
	 * di articoli, nel caso si scenda rispetto alla soglia
	 * minima e sia necessario averne una nuova scorta.
	 * @param articles articoli da inserire nel magazzino.
	 */
	public void setProduct(Article article) {
		products.add(article);
	}

	public Article getArticle(String n) {
		for (Article a : products) {
			if (a.checkNameAvailability(n)) return a;
		}
		return null;
	}

	public Article getArticle(int i) {
		return products.get(i);
	}

	public void generateNewUnitiesForWarehouse() {
		for (Article a : products)
			for(int i : a.getQuantity())
				i += a.settingInitialQuantity(a.getMinimum()[i], a.getMaximum()[i]);
	}

	protected ImageIcon createImageIcon(String path, String description) {
		URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public ImageIcon getVersionIcon(String s) {
		for (Article article : products)
			if (article.getVersions().containsKey(s))
				return article.getVersions().get(s);

		return null;
	}

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
