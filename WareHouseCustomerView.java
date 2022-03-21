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
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import it.unibs.pajc.network.Protocol;
import it.unibs.pajc.utilities.UsefulStrings;

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
	private JButton quit, onOff, placeOrder; // quit è il bottone che permette di passare all'altra card (login), onOff accende e spegne la connessione al server.
	private JLabel articleLabel;
	private WareHouseController controller;
	private ImageIcon warehouseIcon;
	//private boolean connected;
	//private Client client;
	//private static Socket sockey;
	//private Server server;
	private AbstractButton arrowButton;
	private JPopupMenu popupMenu;
	private ArrayList<String> flattenedData; // Merce ordinata in ordine alfabetico, categorie delle merci.
	//private JLabel NO_IMAGES_AVAILABLE =new JLabel(UsefulStrings.NO_IMAGE_AVAILABLE);
	private JComboBox<String> comboBox;
	// to Logout and get the list of the users
	//private JButton login, logout;
	// for the chat room
	private JTextArea ta;
	// the default port number
	private int port; // La porta di default su cui avviene il collegamento (1234).
	//private String defaultHost;
	private JTree tree;
	private Socket socket;
	//private Protocol protocol;



	public WareHouseCustomerView(JPanel contentPane, Socket socket) {
		this.warehouseContentPane = contentPane;
		this.socket = socket;
		//this.protocol = protocol;
		getController();
		controller.getWarehouse();
		//super("Chat Server");
		initializeWareHouseComponents();
	}

	@SuppressWarnings("unchecked")
	private void initializeWareHouseComponents() {
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

		warehouseIcon = new ImageIcon(new ImageIcon(UsefulStrings.TRUCK).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
		JLabel icon = new JLabel(warehouseIcon);
		icon.setBounds(643, 5, 154, 149);
		centerPanel.add(icon);
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

		popupMenu = new JPopupMenu();
		popupMenu.setBorder(new MatteBorder(1, 1, 1, 1, Color.DARK_GRAY));

		//LinkedHashMap<String, ImageIcon> subcategories = new LinkedHashMap<String, ImageIcon>(); // Sottocategorie dei prodotti.
		ArrayList<String> categories = new ArrayList<String>(menuData.keySet());; // Categorie di prodotti, quelle che si vedono per prime nel combobox.
		flattenedData = new ArrayList<String>(); // è semplicemente una copia di «menuData», però in ArrayList.

		/*
		for (Article a : model.getProducts())
			subcategories = a.getVersions();

		for (String s : flattenedData) 
			categoriesIcon.add(model.getArticle(s).getArticleIcon());

		for(int i = 0; i < model.getProducts().size(); i++)
			subcategoriesIcons.add(model.getArticle(i).getVersionIcon());
		 */
		/*
		 * Immagini principali delle categorie di prodotti.
		ImageIcon[] images = new ImageIcon[categories.size()];
		Integer[] intArray = new Integer[categories.size()];
		for (int i = 0; i < categories.size(); i++) {
			intArray[i] = new Integer(i);
			images[i] = model.getArticle(i).getArticleIcon();
			if (images[i] != null) {
				images[i].setDescription(categories.get(i));
			}
		}
		 */
		for (String category : categories) {
			/*
			 * Ottengo un JMenu ogni volta che pongo il mouse
			 * su una categoria, così da «svelare» le sottocategorie
			 * del prodotto prescelto.
			 */
			JMenu menu = new JMenu(category);

			for (String itemName : menuData.get(category).keySet()) {
				menu.add(createMenuItem(itemName));
				flattenedData.add(category + " > " + itemName);
			}
			popupMenu.add(menu);
		}

		comboBox = new JComboBox<String>();
		comboBox.setBounds(238, 64, 355, 32);
		centerPanel.add(comboBox);

		for (Component component : comboBox.getComponents()) {
			if (component instanceof AbstractButton)
				arrowButton = (AbstractButton) component;
		}

		arrowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				setPopupVisible(!popupMenu.isVisible());
			}
		});

		comboBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setPopupVisible(!popupMenu.isVisible());
			}
		});

		onOff = new JButton("Connessione");
		onOff.setBounds(673, 389, 125, 29);
		centerPanel.add(onOff);

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.

		// Create root node of tree
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Articoli a magazzino");
		populateJTree(rootNode); // articleNode, sono gli articoli.

		// Create tree model
		DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
		treeModel.addTreeModelListener(this);
		treeModel.setAsksAllowsChildren(true);

		articleLabel = new JLabel("");
		articleLabel.setBounds(1140, 276, 0, 361);
		articleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(articleLabel);

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

		JScrollPane scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(5, 5, 221, 413);
		centerPanel.add(scrollPane);

		treeModel.setAsksAllowsChildren(true);

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
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
						for (int i = 0; i < controller.getWarehouse().getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = controller.getWarehouse().getVersionIcon(version);
								Integer price = article.getPrice()[i];
								showMessage(version, icon, price);
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

		placeOrder  = new JButton("Ordina articolo");
		placeOrder.setBounds(440, 108, 153, 29);
		placeOrder.setBackground(Color.BLACK);
		placeOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(comboBox.getSelectedItem().toString());
			}
		});
		centerPanel.add(placeOrder);

		JButton clearSelection = new JButton("Elimina selezione");
		clearSelection.setBounds(229, 108, 153, 29);
		clearSelection.setBackground(Color.BLACK);
		centerPanel.add(clearSelection);

		clearSelection.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				comboBox.setSelectedIndex(-1);		
			}

		});
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
	public void populateJTree(DefaultMutableTreeNode root) {

		for(Article article : controller.getWarehouse().getProducts()) {
			DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode(article, true);;
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
	
	public WareHouseController getController() {
		controller = new WareHouseController(this, new WareHouseModel());
		return controller;       
	}


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

		System.out.println("The user has finished editing the node.");
		System.out.println("New value: " + node.getUserObject());
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
	public void treeStructureChanged(
			TreeModelEvent arg0) {
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

	/*
	@Override
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == quit) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}

		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));				
			tf.setText("");
			return;
		}


		if(o == quit) {
			// ok it is a connection request
			String username = ;

			// empty username ignore it
			if(username.length() == 0)
				return;

			// empty serverAddress ignore it
			String server = serverField.getText().trim();

			if(server.length() == 0)
				return;

			// empty or invalid port numer, ignore it
			String portNumber = portNumber.toString().trim();

			if(portNumber.length() == 0)
				return;

			int port = 0;

			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, this);

			// test if we can start the Client
			if(!client.start()) 
				return;

			tf.setText("");
			chatLabel.setText("Inserire il messaggio");
			connected = true;

			// disable login button
			quit.setEnabled(false);
			// enable the 2 buttons
			quit.setEnabled(true);
			// disable the Server and Port JTextField
			serverField.setEditable(false);
			portNumber.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
		}

	}
	*/

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

	/*
	 * Search for the given name in the flattened list of menu items.
	 * If found, add that item to the combo and select it.
	 */
	private void setComboSelection(String name) {
		Vector<String> items = new Vector<String>();

		for (String item : flattenedData) {
			/*
			 * We're cheating here: if two items have the same name
			 * (Fruit->Orange and Color->Orange, for example)
			 * the wrong one may get selected. This should be more sophisticated
			 * (left as an exercise to the reader)
			 */
			if (item.endsWith(name)) {
				items.add(item);
				break;
			}
		}

		comboBox.setModel(new DefaultComboBoxModel<String>(items));

		if (items.size() == 1)
			comboBox.setSelectedIndex(0);
	}

	/*
	 * Toggle the visibility of the custom popup.
	 */
	private void setPopupVisible(boolean visible) {
		if (visible)
			popupMenu.show(comboBox, 0, comboBox.getSize().height);
		else
			popupMenu.setVisible(false);
	}

	/*
	 * Create a JMenuItem whose listener will display
	 * the item in the combo.
	 */
	private JMenuItem createMenuItem(final String name) {
		JMenuItem item = new JMenuItem(name);
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				setComboSelection(name);
			}
		});
		return item;
	}


	protected static void clientToServer(Socket client) {

		try(PrintWriter out = new PrintWriter(client.getOutputStream());) {

			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			String request;
			while((request = stdin.readLine()) != null) {
				out.println(request);
				out.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	/*
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);

		tf.setText("Anonymous");
		// reset port number and host name as a construction time
		portNumber.setText("" + port);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		// don't react to a <CR> after the username
		connected = false;
	}

*/
}
