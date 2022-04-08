package it.unibs.pajc.warehouse;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;
import javax.swing.tree.*;

import it.unibs.pajc.network.Protocol;
import it.unibs.pajc.utilities.UsefulStrings;
import it.unibs.pajc.warehouse.WareHouseWorkerView.Connection;

/**
 *
 * Visualizzazione grafica del magazzino
 * per il cliente, come un e-commerce.
 *
 *
 * @author martinacontestabile
 *
 */
public class WareHouseCustomerView extends JPanel implements ActionListener, TreeModelListener, TreeExpansionListener, TreeWillExpandListener {

	private static final long serialVersionUID = 1L;

	private JPanel warehouseContentPane, centerPanel;
	private JButton quit; // quit è il bottone che permette di passare all'altra card (login), onOff accende e spegne la connessione al server.
	private JLabel articleLabel;
	//private JLabel NO_IMAGES_AVAILABLE =new JLabel(UsefulStrings.NO_IMAGE_AVAILABLE);
	private JTree tree;
	private DefaultMutableTreeNode rootNode;
	private Socket socket;

	// IO streams
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	private WareHouseModel model;
	private WareHouseController controller;
	
	public WareHouseCustomerView(JPanel contentPane, Socket socket) throws IOException, ClassNotFoundException {
		this.warehouseContentPane = contentPane;

		this.socket = socket;
		fromServer = new ObjectInputStream(socket.getInputStream()); // Create an input stream to receive data from the server
		toServer = new ObjectOutputStream(socket.getOutputStream()); // Create an output stream to send data to the server
		
		new Thread (new Connection(socket,this)).start();            // Thread per la gestione della ricezione del model e controller.

		initializeWareHouseComponents();
	}

	@SuppressWarnings("unchecked")
	private void initializeWareHouseComponents() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo model e controller");
		toServer.writeObject("Model e Controller");
		toServer.flush();
		System.out.println("W Ho ricevuto model.");
		System.out.println("W Ho ricevuto controller.");


		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setBackground(new Color(0, 191, 255));

		// Etichetta per uscire dalla gestione del magazzino e tornare nel login.
		quit = new JButton("Esci");
		quit.setBackground(UIManager.getColor("info"));
		quit.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		quit.setForeground(Color.RED);
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				CardLayout layout = (CardLayout)warehouseContentPane.getLayout();
				layout.show(warehouseContentPane, UsefulStrings.LOGIN_GUI);
				showMessage("Logout avvenuto con successo.");
			}
		});

		centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPanel.setOpaque(true);
		centerPanel.setBackground(new Color(255, 255, 255));

		JLabel warehouseIcon = new JLabel(new ImageIcon(new ImageIcon(UsefulStrings.TRUCK).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT)));
		warehouseIcon.setBounds(643, 5, 154, 149);
		centerPanel.add(warehouseIcon);
		centerPanel.setLayout(null);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(quit, GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
										.addGroup(groupLayout.createSequentialGroup()
												.addContainerGap()
												.addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)))
								.addContainerGap())
		);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 423, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(quit, GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE)
								.addContainerGap())
		);
		setLayout(groupLayout);

		ArrayList<ImageIcon> categoriesIcon = new ArrayList<>();
		ArrayList<ArrayList<ImageIcon>> subcategoriesIcons = new ArrayList<>();

		// Combo box contenente la merce presente all'interno del magazzino.
		LinkedHashMap<String, LinkedHashMap<String, ImageIcon>> menuData = new LinkedHashMap<String, LinkedHashMap<String, ImageIcon>>();
		for (int i = 0; i < controller.getWarehouse().getProducts().size(); i++) {
			/*
			 * La Key è la categoria di prodotto ordinabile,
			 * il Value sono le sottocategorie disponibili di quel prodotto.
			 */
			menuData.put("Ordina " + controller.getWarehouse().getProducts().get(i).getName(), controller.getWarehouse().getProducts().get(i).getVersions());
			categoriesIcon.add(controller.getWarehouse().getArticle(i).getArticleIcon());
		}

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.

		// Creo il nodo
		rootNode = new DefaultMutableTreeNode("Articoli a magazzino");
		populateJTree(rootNode, model); // articleNode, sono gli articoli.

		// Create tree model
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		treeModel.addTreeModelListener(this);
		treeModel.setAsksAllowsChildren(true);

		tree = new JTree(treeModel);
		tree.setBackground(new Color(255, 255, 255));
		tree.setBounds(2, 2, 224, 409);
		tree.setRootVisible(false);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeExpansionListener(this);
		tree.addTreeWillExpandListener(this);
		tree.setShowsRootHandles(true);
		centerPanel.add(tree);

		articleLabel = new JLabel("");
		articleLabel.setBounds(1140, 276, 0, 361);
		articleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(articleLabel);

		JScrollPane scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(5, 5, 221, 413);
		centerPanel.add(scrollPane);

		treeModel.setAsksAllowsChildren(true);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					if (node == null) return;
					Object nodeInfo = node.getUserObject();
					if(nodeInfo instanceof Article) {
						Article article = (Article)nodeInfo;
						ImageIcon icon = article.getArticleIcon();
						showMessage(article.getName(), icon);
					} else if(nodeInfo instanceof String) {
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
						Article article = (Article)parent.getUserObject();
						String version = (String)nodeInfo;
						for (int i = 0; i < model.getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = model.getVersionIcon(version);
								Integer price = article.getPrice()[i];
								Integer units = article.getQuantity()[i];
								showMessage(version, icon, price, units);
								break;
							} else continue;
						}
					}
				} else if(SwingUtilities.isLeftMouseButton(e)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					
					if (node == null) return;
					Object nodeInfo = node.getUserObject();
					if(nodeInfo instanceof String) {
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
						Article article = (Article)parent.getUserObject();
						String version = (String)nodeInfo;
						for (int i = 0; i < model.getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = model.getVersionIcon(version);
								Integer units = article.getQuantity()[i];
								try {
									showInput(units, version, article, controller, model, treeModel, node);
								} catch (IOException ex) {
									ex.printStackTrace();
								}
								break;
							} else continue;
						}
					}
				}
			}
		});


		JLabel welcomeLabel = new JLabel("Benvenuto in Magazzino");
		welcomeLabel.setBounds(300, 5, 200, 20);
		welcomeLabel.setLabelFor(centerPanel);
		welcomeLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		centerPanel.add(welcomeLabel);
	}

	/**
	 * Nuovo messaggio popup per l'utente.
	 *
	 *
	 * @param msg — messaggio da mostrare.
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	/*
	// append message to the two JTextArea
	// position at the end
	protected void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}

	protected void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);

	}

	public void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	*/

	/**
	 * Inserisco la merce nel Jtree.
	 *
	 * @param root — il nome del magazzino.
	 */
	public void populateJTree(DefaultMutableTreeNode root, WareHouseModel model) {

		for(Article article : model.getProducts()) {
			DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode(article, true);
			root.add(articleNode);

			for(String versionName : article.getVersions().keySet()) {
				DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(versionName, true);
				articleNode.add(versionNode);
			}
		}
	}

	public void showMessage(String msg, ImageIcon image) {
		JOptionPane.showMessageDialog(null, new JLabel(msg, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price) {
		JOptionPane.showMessageDialog(null, new JLabel(msg + " » Prezzo: " + price + "€", JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price, Integer units) {
		JOptionPane.showMessageDialog(null, new JLabel(msg + " » Prezzo: " + price + "€" +", Disponibili: " + units, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
		System.out.println("L'articolo nella versione " + msg + " ha quantità " + units);
	}

	public void showInput(int units, String version, Article article, WareHouseController controller, WareHouseModel model, DefaultTreeModel treeModel, DefaultMutableTreeNode node) throws IOException {
		JTextField quantity = new JTextField(5);

		Object[] inputs = {"Inserire le unità desiderate di " + version, quantity};

		int result = JOptionPane.showConfirmDialog(null, inputs, "Effettuare l'ordine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		System.out.println("L'articolo " + article.getName() + " nella versione " + version + " ha quantità " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Quantità desiderata: " + quantity.getText());

			if (!quantity.equals("") || !quantity.equals(null)) {
				toServer.writeObject("Acquisto");
				toServer.writeObject(article);
				toServer.writeObject(controller);
				toServer.writeObject(model);
				toServer.writeInt(Integer.parseInt(quantity.getText()));
				toServer.writeObject(version);

				// Send the pay to the server
				toServer.flush();
				// Get area pay the server
				try {
					model = (WareHouseModel) fromServer.readObject();
					controller = (WareHouseController) fromServer.readObject();
					System.out.println("Ricevuto model e controller aggiornati, aggiorno la view.");
					System.out.println("View aggiornata.");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				treeModel.nodeStructureChanged(node);
				//treeModel.reload();
				//populateJTree(rootNode, model);
				System.out.println("L'articolo " + article.getName() + " nella versione " + version + " ha quantità " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
				//repaint();
			} else  return;
		} else if(result == JOptionPane.CLOSED_OPTION) {
			return;
		}
	}

	/*
	public WareHouseModel getModel() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il model.");
		toServer.writeObject("Model");
		toServer.flush();
		//WareHouseController controller = new WareHouseController();
		//return controller;
		WareHouseModel model = (WareHouseModel) fromServer.readObject();
		System.out.println("C Ho ricevuto model.");
		return model;
	}

	public WareHouseController getController() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il controller.");
		toServer.writeObject("Controller");
		toServer.flush();
		//WareHouseController controller = new WareHouseController();
		//return controller;
		WareHouseController controller = (WareHouseController) fromServer.readObject();
		System.out.println("C Ho ricevuto controller.");
		return controller;
	}
    */

	/**
	 * @return The tree which this uses as a browser.  Will not be null.
	 */
	public JTree getTree() {
		return tree;
	}

	@Override
	public void treeNodesChanged(TreeModelEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)arg0.getTreePath().getLastPathComponent();

		/*
		 * If the event lists children, then the changed
		 * node is the child of the node we have already
		 * gotten.  Otherwise, the changed node and the
		 * specified node are the same.
		 */
		try {
			int index = arg0.getChildIndices()[0];
			node = (DefaultMutableTreeNode)node.getChildAt(index);
		}
		catch (NullPointerException exc) {}

		System.out.println("Il nodo è stato modificato");
		System.out.println("Nuovo valore: " + node.getUserObject());
	}

	@Override
	public void treeNodesInserted(
			TreeModelEvent arg0) {
	}

	@Override
	public void treeNodesRemoved(
			TreeModelEvent arg0) {
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) {
	}

	@Override
	public void treeExpanded(TreeExpansionEvent arg0) {
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent arg0) throws ExpandVetoException {
		TreePath path = arg0.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String data = node.getUserObject().toString();
		System.out.println("WillCollapse: " + data);
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent arg0) throws ExpandVetoException {
		TreePath path = arg0.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String data = node.getUserObject().toString();
		System.out.println("WillExpand: " + data);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	/*
	 * Prevents the default popup from being displayed
	 */
	class EmptyComboBoxUI extends MetalComboBoxUI  {
		@Override
		protected ComboPopup createPopup() {
			BasicComboPopup thePopup = (BasicComboPopup) super.createPopup();
			thePopup.setPreferredSize(new Dimension(0,0));
			return thePopup;
		}
	}
	
	class Connection extends Thread {
		private Socket socket        = null;
		private ObjectInputStream input = null;
		private ObjectOutputStream out     = null;
		WareHouseCustomerView customerView;
		public WareHouseModel model;
		public WareHouseController controller;
		public Connection(Socket socket, WareHouseCustomerView customerView){
			this.socket = socket;
			this.customerView = customerView;
		}

		@Override
		public void run() {

			try {
				input = new ObjectInputStream(socket.getInputStream());
				out = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

			while(true){
				try {
					System.out.println("Aggiorno view");
					model = (WareHouseModel) input.readObject();
					controller = (WareHouseController) input.readObject();
					customerView.populateJTree(rootNode, model);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}