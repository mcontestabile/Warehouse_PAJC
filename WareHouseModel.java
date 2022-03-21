package it.unibs.pajc.warehouse;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import it.unibs.pajc.utilities.UsefulStrings;

/**
 * Questo metodo inizializza un oggetto di tipo {@code Magazzino}.
 * Esso rappresenta la merce presente nel magazzino.
 */
public class WareHouseModel {
	private ArrayList<Article> products;
	
	/**
	 * Costruttore dell'oggetto {@code Magazzino},
	 * è una collezione di oggetti {@code Articolo},
	 * questi ultimi dotati di determinate proprietà,
	 * definite a priori nella loro classe.
	 * @param articles
	 */
	public WareHouseModel() {

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
}