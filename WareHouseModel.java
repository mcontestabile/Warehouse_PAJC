package it.unibs.pajc.warehouse;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

import it.unibs.pajc.utilities.RandomNumbers;
import it.unibs.pajc.utilities.UsefulStrings;

/**
 * Questo metodo inizializza un oggetto di tipo {@code Magazzino}.
 * Esso rappresenta la merce presente nel magazzino.
 */
public class WareHouseModel implements Serializable {
	private ArrayList<Article> products;
	
	/**
	 * Costruttore dell'oggetto {@code Magazzino},
	 * è una collezione di oggetti {@code Articolo},
	 * questi ultimi dotati di determinate proprietà,
	 * definite a priori nella loro classe.
	 */
	public WareHouseModel(ArrayList<Article> products) {
		this.products = products;
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

		for (Article product : products) subMap.putAll(product.getVersions());

		return subMap;
	}

	public void setSubcategory(String name, int quantity, int price, int minimum, int maximum, String versionName, ImageIcon vIcon) {
		Article article = getArticle(name);

		Map<String, ImageIcon> subMap = new LinkedHashMap<>();
		subMap.putAll(article.getVersions());

		int nVersions = subMap.size();

		article.setPrice(price, (nVersions - 1));
		article.setQuantity(quantity, (nVersions - 1));
		article.setMinimum(minimum, (nVersions - 1));
		article.setMaximum(maximum, (nVersions - 1));
		article.setVersion(versionName, vIcon);
	}

	/**
	 * Setter permette di aggiungere articoli al {@code Magazzino}.
	 * Inoltre, permette l'inserimento del rifornimento
	 * di articoli, nel caso si scenda rispetto alla soglia
	 * minima e sia necessario averne una nuova scorta.
	 * @param article articolo da inserire nel magazzino.
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

	/**
	 * Questo metodo permette di settare la quantità
	 * la merce in magazzino di ciascun oggetto dopo un ordine.
	 *
	 * @param ordered quantità ordinata.
	 */
	public void settingNewUnits(int ordered, String version, Article article) {
		int position = new ArrayList<String>(article.getVersions().keySet()).indexOf(version);
		int minimum = article.getMinimum(position);
		int maximum = article.getMaximum(position);
		int previous = article.getQuantity(position);
		int newQuantity = previous + ordered;
		int[] quantities = article.getQuantity();

		/*
		 * Se non sforo il massimo, imposto la quantità previous + ordered
		 * come nuova quantità, altimenti la metto al massimo.
		 */
		if(newQuantity > minimum && newQuantity < maximum) quantities[position] = newQuantity;
		else quantities[position] = RandomNumbers.obtainInt(minimum, maximum);
		article.setQuantity(quantities);
	}

	/**
	 * Questo metodo permette di settare la quantità
	 * la merce in magazzino di ciascun oggetto dopo un ordine.
	 *
	 * @param ordered quantità ordinata.
	 */
	public void processOrder(int ordered, String version, Article article) {
		//int position = getIndex(version, model);
		int position = new ArrayList<String>(article.getVersions().keySet()).indexOf(version);
		//int previous = article.getQuantity(position);
		//model.getArticle(article.getName()).setQuantity((-ordered), position, previous);
		article.newQuantityFromOrder(ordered, position);//article.newQuantityFromOrder(ordered, position, previous);
		System.out.println("Nuova quantità: " + article.getQuantity(position));
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

	public ArrayList<String> getArticleNames() {
		ArrayList<String> articleNames = new ArrayList<>();

		for (Article a : products)
			articleNames.add(a.getName());

		return articleNames;
	}
}