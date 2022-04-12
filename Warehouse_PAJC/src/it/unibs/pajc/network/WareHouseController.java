package it.unibs.pajc.warehouse;

import it.unibs.pajc.utilities.*;
import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WareHouseController implements Serializable {
	private WareHouseModel model;

	public WareHouseController() {
		model = createWareHouse();
	}

	/**
	 * Controllo se il nome dell'oggetto {@code Articolo} ricercato
	 * esiste davvero o meno. Il metodo è pensato per un riuso, nel
	 * caso venga implementata la possibilità all'utente di inserire
	 * nuovi articoli nel magazzino (nel caso il programma venga
	 * usato da un'azienda di logistica).
	 * @param n       nome inserito dall'utente nella ricerca.
	 * @param model   articoli all'interno del magazzino.
	 * @return se esiste true, se non esiste false.
	 */
	public boolean checkNameAvailability(String n, WareHouseModel model) {
		for (Article a : model.getProducts()) {
			if(a.checkNameAvailability(n)) return true;
		}
		return false;
	}

	/**
	 * Questo metodo permette di settare la quantità
	 * la merce in magazzino di ciascun oggetto dopo un ordine.
	 *
	 * @param ordered quantità ordinata.
	 */
	public WareHouseModel settingNewUnits(int ordered, String version, Article article) {
		model.settingNewUnits(ordered, version, article);
		return model;
	}

	/**
	 * Questo metodo permette di settare la quantità
	 * la merce in magazzino di ciascun oggetto dopo un ordine.
	 *
	 * @param ordered quantità ordinata.
	 */
	public WareHouseModel processOrder(int ordered, String version, Article article) {
		model.processOrder(ordered, version, article);
		return model;
	}

	public WareHouseModel getWarehouse() {
		return model;
	}

	public WareHouseModel addNewVersion(String name, int quantity, int price, int minimum, int maximum, String versionName, ImageIcon vIcon) {
		model.setSubcategory(name, quantity, price, minimum, maximum, versionName, vIcon);
		return model;
	}

	public WareHouseModel createWareHouse() {
		ArrayList<Article> products = new ArrayList<>();

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
				new int[]{939, 939, 939, 939, 939},
				new int[]{250, 250, 250, 250, 250},
				new int[]{25, 25, 25, 25, 25},
				new int[]{400, 400, 400, 400, 400},
				new ImageIcon(UsefulStrings.IPHONES)
		));

		products.add(new Article(
				"Samsung Galaxy S22",
				new LinkedHashMap<String, ImageIcon>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						put("Versione Burgundy", new ImageIcon(UsefulStrings.BURGUNDY));
						put("Versione Phantom Black", new ImageIcon(UsefulStrings.PB));
						put("Versione Phantom White", new ImageIcon(UsefulStrings.PW));
						put("Versione Verde", new ImageIcon(UsefulStrings.GREEN));
					}
				},
				new int[] {879, 879, 879, 879},
				new int[]{120, 120, 120, 120},
				new int[]{24, 24, 24, 24},
				new int[]{700, 700, 700, 700},
				new ImageIcon(UsefulStrings.GALAXY_S22)
		));

		products.add(new Article(
				"Smart TV",
				new LinkedHashMap<String, ImageIcon>() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					{
						put("Smart TV Samsung 55", new ImageIcon(UsefulStrings.TV_2));
					}
				},
				new int[] {450},
				new int[] {100},
				new int[] {10},
				new int[] {650},
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
				new int[]{30, 30, 30, 30, 30, 30, 30},
				new int[]{245, 245, 245, 245, 245, 245, 245},
				new ImageIcon(UsefulStrings.IMAC)
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
				new int[]{150, 150},
				new int[]{25, 25},
				new int[]{600, 600},
				new ImageIcon(UsefulStrings.SURFACES)
		));

		products.add(new Article(
				"Cuffie bluethoot",
				new LinkedHashMap<String, ImageIcon>(){/**
				 *
				 */
				private static final long serialVersionUID = 1L;

					{
						put("AirPods III generazione", new ImageIcon(UsefulStrings.AIRPODS3));
						put("Galaxy Buds 2", new ImageIcon(UsefulStrings.GALAXY_BUDS));
						put("LG", new ImageIcon(UsefulStrings.LG));
					}},
				new int[] {180, 150, 70}, //prezzo
				new int[]{80, 80, 80}, //quantità iniziali
				new int[]{30, 30, 30}, //minimo
				new int[]{300, 300, 300}, //massimo
				new ImageIcon(UsefulStrings.CUFFIE)
		));

		products.add(new Article(
				"Alexa",
				new LinkedHashMap<String, ImageIcon>() {/**
				 *
				 */
				private static final long serialVersionUID = 1L;

					{
						put("Echo Dot III generazione", new ImageIcon(UsefulStrings.AMAZON_DOT));
						put("Echo Dot IV generazione", new ImageIcon(UsefulStrings.AMAZON_ECHO));
					}},
				new int[] {99, 119},
				new int[]{250, 250},
				new int[] {10, 10},
				new int[]{500, 500},
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
				new int[]{150, 150, 150, 150, 150},
				new int[] {25, 25, 25, 25, 25},
				new int[]{450, 450, 450, 450, 450},
				new ImageIcon(UsefulStrings.HOMEPOD)
		));

		return new WareHouseModel(products);
	}
}