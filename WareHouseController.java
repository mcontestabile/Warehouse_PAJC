package it.unibs.pajc.warehouse;

import java.util.ArrayList;

public class WareHouseController {
	private WareHouseModel model;
	private WareHouseCustomerView customer;
	private WareHouseOwnerView warehouseman;

	public WareHouseController(WareHouseCustomerView customer, WareHouseModel model) {
		this.model = model;
		this.customer = customer;
	}
	
	public WareHouseController(WareHouseOwnerView warehouseman, WareHouseModel model) {
		this.model = model;
		this.warehouseman = warehouseman;
	}

	/**
	 * Questo metodo permette di settare la quantità
	 * la merce in magazzino di ciascun oggetto dopo un ordine.
	 * 
	 * @param ordered quantità ordinata.
	 * @param previous minima quantità possibile.
	 * @param maximum controllo che non ecceda,
	 *        altrimenti setto direttamente a massimo.
	 * @return quantità iniziale.
	 */
	public void settingNewUnits(int ordered, int previous, String version, Article article) {
		int position = new ArrayList<String>(article.getVersions().keySet()).indexOf(version);
		int maximum = article.getMaximum()[position];
		if(previous < maximum)
			article.setQuantity(ordered, position, previous);
		else article.setQuantity(maximum, position);
	}
	
	/**
	 * Controllo se il nome dell'oggetto {@code Articolo} ricercato
	 * esiste davvero o meno. Il metodo è pensato per un riuso, nel
	 * caso venga implementata la possibilità all'utente di inserire
	 * nuovi articoli nel magazzino (nel caso il programma venga
	 * usato da un'azienda di logistica).
	 * @param n nome inserito dall'utente nella ricerca.
	 * @param articles articoli all'interno del magazzino.
	 * @return se esiste true, se non esiste false.
	 */
	public boolean checkNameAvailability(String n, ArrayList<Article> articles) {
		for (Article a : articles) {
			if(a.checkNameAvailability(n)) return true;
		}
		return false;
	}
	
	/**
	 * Questo metodo permette di settare la quantità
	 * iniziale di ciascun oggetto di tipo {@code Articolo}.
	 * 
	 * @param maximum massima quantità possibile.
	 * @param minimum minima quantità possibile.
	 * @return quantità iniziale.
	 */
	public void settingInitialQuantity(int maximum, int minimum) {
		for (Article a : model.getProducts()) 
			for (int iterator = 0; iterator < a.getVersions().size(); iterator++) 
				a.getQuantity()[iterator] =  RandomNumbers.obtainInt(maximum, minimum);
	}
	
	public WareHouseModel getWarehouse() {
		model = new WareHouseModel();
		return model;       
	}
}
