package it.unibs.pajc.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.awt.Image;
import javax.swing.table.AbstractTableModel;

public class WareHouseTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Article> products;
	private String[] columns;
	private WareHouseModel model = new WareHouseModel();
	

	public WareHouseTableModel(){
		super();
		
		products = model.getProducts();
		
		columns = new String[]{"Prodotto", "Prezzo", "Categorie", "Disponibile", "Stock minimo", "Stock massimo", "Δt isponibilità stock"};
	}

	// Numero delle colonne della tabella.
	public int getColumnCount() {
		return columns.length ;
	}

	// The object to render in a cell
	public Object getValueAt(int row, int columns) {
		Article product = products.get(row);
		switch(columns) {
		case 0: return (String)product.getName();
		case 1: return (String)product.getVersions().toString();
		case 2: return (int[])product.getPrice();
		case 3: return (int[])product.getQuantity();
		case 4: return (int[])product.getMinimum();
		case 5: return (int[])product.getMaximum();
		case 6: return (int)product.getRequestedTime();
		default: return null;
		}
	}

	// Optional, the name of your column
	public String getColumnName(int col) {
		return columns[col] ;
	}

	public int getRowCount() {
		return products.size();
	}

	public ArrayList<Article> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Article> products) {
		this.products = products;
	}
	
	
}