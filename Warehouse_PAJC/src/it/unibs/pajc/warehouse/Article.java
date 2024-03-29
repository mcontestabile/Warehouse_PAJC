package it.unibs.pajc.warehouse;

import it.unibs.pajc.utilities.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;

public class Article implements Serializable {
	
	/*
     * Nome dell'articolo, sottocategorie (con relative
     * immagini rappresentative), quantità
     * minime e massime per ciascun articolo, quantità
     * attuale del prodotto e tempo di produzione.
     * Non si può sforare da maximum e minimum.
     * Sono private perché l'utente non può
     * assolutamente modificare questi valori a suo
     * piacimento, la modifica è autorizzata solo su
     * volontà del programmatore.
     */
    private String name;
    private int[] quantity;
    private int[] price;
    private int[] minimum;
    private int[] maximum;
    private ImageIcon articleIcon;
    private LinkedHashMap<String, ImageIcon> versions;

    // Questo oggetto consente il calcolo del tempo trascorso.
    Timer timer;

    /**
     * Costruttore dell'oggetto articolo
     * @param name nome della categoria di merce.
     * @param minimum quantità minima che si può avere dell'articolo {@code X}.
     * @param maximum quantità massima che si può avere dell'articolo {@code X}.
	 * @param price prezzo dell'articolo.
	 * @param articleIcon icona dell'articolo.
	 * @param quantity quantità esistente dell'articolo.
	 * @param versions versioni della merce.
     */
    public Article (String name, LinkedHashMap<String, ImageIcon> versions, int[] price, int[] quantity,int[] minimum, int[] maximum, ImageIcon articleIcon) {
        this.name = name;
        this.versions = versions;
        this.price = price;
        this.quantity = quantity;
        this.minimum = minimum;
        this.maximum = maximum;
        this.articleIcon = articleIcon;
    }

    public int[] getPrice() {
		return price;
	}

	public void setPrice(int[] price) {
		this.price = price;
	}

	public void setPrice(int price, int i) {this.price[i] = price;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getQuantity() {
		return quantity;
	}
    
    public int getQuantity(int i) {return quantity[i];}

	public void setQuantity(int[] q) {
		quantity = q;
	}
	
	public void setQuantity(int q, int i) {
		quantity[i] += q ;
	}

	public void setQuantityToMax(int i) {quantity[i] = maximum[i];}

	public void newQuantityFromOrder(int q, int i) {
		quantity[i] -= q;
	}

	public int[] getMinimum() {
		return minimum;
	}
	
	public int getMinimum(int i) {
		return minimum[i];
	}

	public void setMinimum(int[] minimum) {
		this.minimum = minimum;
	}

	public void setMinimum(int minimum, int i) {this.minimum[i] = minimum;}

	public int[] getMaximum() {
		return maximum;
	}

	public int getMaximum(int i) {return this.maximum[i];}

	public void setMaximum(int[] maximum) {
		this.maximum = maximum;
	}

	public void setMaximum(int maximum, int i) {this.maximum[i] = maximum;}
	
	public LinkedHashMap<String, ImageIcon> getVersions() {
		return versions;
	}
	
	public List<String> getStringVersions() {
		List<String> al = versions.keySet().stream().collect(Collectors.toList());
		return al;
	}
	
	public List<ImageIcon> getVersionsIcons() {
		List<ImageIcon> al = versions.values().stream().collect(Collectors.toList());
		return al;
	}

	public void setVersions(LinkedHashMap<String, ImageIcon> versions) {
		this.versions = versions;
	}

	public void setVersion(String name, ImageIcon icon) {
		this.versions.put(name, icon);
	}
	
	public ImageIcon getArticleIcon() {
		return articleIcon;
	}

	public void setArticleIcon(ImageIcon articleIcon) {
		this.articleIcon = articleIcon;
	}

	public boolean checkIfIsNeededANewProduction(int quantity[], int position) {
		for (int i = 0; i < quantity.length; i++) 
			if (quantity[position] < minimum[position]) return true;
		
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
    public int settingInitialQuantity(int maximum, int minimum) {
        return RandomNumbers.obtainInt(maximum, minimum);
    }

    /**
     * Controllo se il nome dell'oggetto {@code Articolo} ricercato
     * esiste davvero o meno.
     * 
     * @param n nome inserito dall'utente nella ricerca.
     * @return se esiste true, se non esiste false.
     */
    public boolean checkNameAvailability(String n) {
        if (name.equalsIgnoreCase(n)) return true;
        return false;
    }

	@Override
	public String toString() {
		return name;
	}
}
