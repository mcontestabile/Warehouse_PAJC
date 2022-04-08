package it.unibs.pajc.warehouse;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Toolkit;

import it.unibs.pajc.utilities.MyDragDropListener;
import it.unibs.pajc.utilities.UsefulStrings;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;

/**
 *
 * @author martinacontestabile
 *
 *
 * Interfaccia grafica. Il JTree contiene i prodotti in vendita e disponibili all'acquisto,
 * Il bottone «quit» serve per uscire e terminare la connessione.
 */
public class WareHouseWorkerView extends JPanel implements ActionListener, WindowListener,  TreeModelListener, TreeExpansionListener, TreeWillExpandListener, Observer {
	private static final long serialVersionUID = 1L;

	private JPanel warehouseContentPane, centerPanel;
	private JButton quit, addButton, removeButton; // quit è il bottone che permette di passare all'altra card (login), onOff accende e spegne la connessione al server.
	private JLabel articleLabel;
	private ImageIcon warehouseIcon;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();

	private Socket socket;
	// IO streams
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;
	
	private WareHouseModel model;
	private WareHouseController controller;

	public WareHouseWorkerView(JPanel contentPane, Socket socket) throws IOException, ClassNotFoundException {
		this.warehouseContentPane = contentPane;

		this.socket = socket;
		fromServer = new ObjectInputStream(socket.getInputStream()); // Create an input stream to receive data from the server
		toServer = new ObjectOutputStream(socket.getOutputStream()); // Create an output stream to send data to the server
		
		new Thread (new Connection(socket,this)).start();            // Thread per la gestione della ricezione del model e controller.
		
		initializeWareHouseComponents();
	}

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

		warehouseIcon = new ImageIcon(new ImageIcon(UsefulStrings.WH_ICON).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
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

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.

		// Create root node of tree
		rootNode = new DefaultMutableTreeNode("Articoli a magazzino");
		populateJTree(rootNode, model); // articleNode, sono gli articoli.

		// Ogni quanti millisecondi eseguire il timer e cosa fare quando lo si esegue.
		Timer timer = new Timer(10, (e) -> {
			populateJTree(rootNode, model);
			centerPanel.repaint();
		});

		timer.start();

		// Create tree model
		treeModel = new DefaultTreeModel(rootNode);
		treeModel.addTreeModelListener(this);
		treeModel.setAsksAllowsChildren(true);

		articleLabel = new JLabel("");
		articleLabel.setBounds(1140, 276, 0, 361);
		articleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(articleLabel);

		tree = new JTree(treeModel);
		tree.setBackground(new Color(255, 255, 255));
		tree.setBounds(5, 9, 224, 409);
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
				if (SwingUtilities.isRightMouseButton(e)) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
					if (node == null) return;
					Object nodeInfo = node.getUserObject();
					if(nodeInfo instanceof Article) {
						Article article = (Article)nodeInfo;
						ImageIcon icon = article.getArticleIcon();
						showMessage(article.getName(), icon);
					} else if(nodeInfo instanceof String) {
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
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
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
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
									showInput(units, version, article, tree, controller, model);
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


		addButton = new JButton("add");
		addButton.setSize(92, 28);
		addButton.setLocation(229, 133);
		addButton.setActionCommand(UsefulStrings.ADD_COMMAND);
		addButton.addActionListener(this);
		centerPanel.add(addButton);

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					joptionpaneMultiInput(controller, model);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});


		removeButton = new JButton("remove");
		removeButton.setLocation(229, 163);
		removeButton.setSize(92, 28);
		removeButton.setActionCommand(UsefulStrings.REMOVE_COMMAND);
		removeButton.addActionListener(this);
		centerPanel.add(removeButton);
	}

	/*
	private static void doLoop(ObjectOutputStream fromServer, ObjectInputStream toServer) {
		Thread newThread = new Thread(() -> {
			while (true) {
				SwingUtilities.invokeLater(() -> {
					label.setText(new java.util.Date().toString());
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(NewMain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
		newThread.start();
	}
	 */

	/**
	 * @return The tree which this uses as a browser.  Will not be null.
	 */
	public JTree getTree() {
		return tree;
	}

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

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public void showMessage(String msg, ImageIcon image) {
		JOptionPane.showMessageDialog(null, new JLabel(msg, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price) {
		JOptionPane.showMessageDialog(null, new JLabel(msg + " » Prezzo: " + price + "€", JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price, Integer units) {
		JOptionPane.showMessageDialog(null, new JLabel(msg + " » Prezzo: " + price + "€" +", Disponibili: " + units, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showInput(int units, String version, Article article, JTree tree, WareHouseController controller, WareHouseModel model) throws IOException {
		JTextField quantity = new JTextField(5);

		Object[] inputs = {"Inserire le unità desiderate di " + version, quantity};

		int result = JOptionPane.showConfirmDialog(null, inputs, "Effettuare l'ordine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Quantità desiderata: " + quantity.getText());

			if (!quantity.equals("") || !quantity.equals(null)) {
				toServer.writeObject("Ordine");
				toServer.writeObject(article);
				toServer.writeObject(controller);
				toServer.writeObject(model);
				toServer.writeInt(Integer.parseInt(quantity.getText()));
				toServer.writeObject(version);

				// Send the pay to the server
				toServer.flush();
				// Get area pay the server
				/*
				try {
					model = (WareHouseModel) fromServer.readObject();
					controller = (WareHouseController) fromServer.readObject();
					System.out.println("Ricevuto model e controller aggiornati, aggiorno la view.");
					System.out.println("View aggiornata.");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				tree.firePropertyChange("quantità", units, model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
				//treeModel.reload();
				populateJTree(rootNode, model);
				System.out.println("L'articolo " + article.getName() + " nella versione " + version + " ha quantità " + model.getArticle(article.getName()).getQuantity(new ArrayList<String>(model.getArticle(article.getName()).getVersions().keySet()).indexOf(version)));
				repaint();
				 */
			} else  return;
		} else if(result == JOptionPane.CLOSED_OPTION) {
			return;
		}
	}

	/*
	public WareHouseModel getModel() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il model");
		toServer.writeObject("Model");
		toServer.flush();
		//WareHouseController controller = new WareHouseController();
		//return controller;
		WareHouseModel model = (WareHouseModel) fromServer.readObject();
		System.out.println("W Ho ricevuto model.");
		return model;
	}

	public WareHouseController getController() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il controller.");
		toServer.writeObject("Controller");
		toServer.flush();
		//WareHouseController controller = new WareHouseController();
		//return controller;
		WareHouseController controller = (WareHouseController) fromServer.readObject();
		System.out.println("W Ho ricevuto controller.");
		return controller;
	}
	*/

	/** Remove all nodes except the root node. */
	public void clear() {
		rootNode.removeAllChildren();
		treeModel.reload();
	}

	/** Remove the currently selected node. */
	public void removeCurrentNode() {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
					(currentSelection.getLastPathComponent());
			MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
			if (parent != null) {
				treeModel.removeNodeFromParent(currentNode);
				return;
			}
		}

		// Either there was no selection, or the root was selected.
		toolkit.beep();
	}

	/** Add child to the currently selected node. */
	public DefaultMutableTreeNode addObject(Article child) {
		DefaultMutableTreeNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		if (parentPath == null) {
			parentNode = rootNode;
		} else {
			parentNode = (DefaultMutableTreeNode)
					(parentPath.getLastPathComponent());
		}

		return addObject(parentNode, child, true);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
		return addObject(parent, child, false);
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child, boolean shouldBeVisible) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

		if (parent == null) parent = rootNode;

		//It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

		//Make sure the user can see the lovely new node.
		if (shouldBeVisible) tree.scrollPathToVisible(new TreePath(childNode.getPath()));

		return childNode;
	}

	class MyTreeModelListener implements TreeModelListener {
		public void treeNodesChanged(TreeModelEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we have already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */
			try {
				int index = e.getChildIndices()[0];
				node = (DefaultMutableTreeNode)
						(node.getChildAt(index));
			} catch (NullPointerException exc) {}

			System.out.println("The user has finished editing the node.");
			System.out.println("New value: " + node.getUserObject());
		}

		public void treeNodesInserted(TreeModelEvent e) {}
		public void treeNodesRemoved(TreeModelEvent e) {}
		public void treeStructureChanged(TreeModelEvent e) {}
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (UsefulStrings.REMOVE_COMMAND.equals(command)) {
			//Remove button clicked
			removeCurrentNode();
		} else if (UsefulStrings.CLEAR_COMMAND.equals(command)) {
			//Clear button clicked.
			clear();
		}
	}

	public void joptionpaneMultiInput(WareHouseController controller, WareHouseModel model) throws IOException {
		JTextField nameField = new JTextField(5);
		JTextField quantityField = new JTextField(5);
		JTextField priceField = new JTextField(5);
		JTextField minimumQuantityField = new JTextField(5);
		JTextField maximumQuantityField = new JTextField(5);
		JLabel nImageIconField = new JLabel("trascina immagine\narticolo");
		JTextField versionsField = new JTextField(5);
		JLabel vImageIconField = new JLabel("trascina immagine\nversione");

		Object[] inputs = {"Aggiungi categoria « articolo »", nameField,
				"Quantità", quantityField,
				"Prezzo", priceField,
				"Minimo", minimumQuantityField,
				"Massimo", maximumQuantityField,
				nImageIconField,
				"Versione", versionsField,
				vImageIconField};

		nImageIconField.setTransferHandler(new TransferHandler("png"));
		vImageIconField.setTransferHandler(new TransferHandler("png"));

		// Create the drag and drop listener
		MyDragDropListener myDragDropListenerArticle = new MyDragDropListener();
		MyDragDropListener myDragDropListenerVersion = new MyDragDropListener();

		// Connect the label with a drag and drop listener
		new DropTarget(nImageIconField, myDragDropListenerArticle);
		new DropTarget(vImageIconField, myDragDropListenerVersion);

		/*
		JPanel myPanel = new JPanel();
		myPanel.add(new JLabel("Categoria « articolo »"));
		myPanel.add(nameField);
		myPanel.add(Box.createVerticalStrut(10)); // a spacer
		myPanel.add(new JLabel("Quantità"));
		myPanel.add(quantityField);
		myPanel.add(new JLabel("Prezzo"));
		myPanel.add(priceField);
		myPanel.add(new JLabel("Minimo"));
		myPanel.add(minimumQuantityField);
		myPanel.add(new JLabel("Massimo"));
		myPanel.add(maximumQuantityField);
		myPanel.add(new JLabel("Immagine articolo"));
		myPanel.add(nImageIconField);
		nImageIconField.setDragEnabled(true);
		myPanel.add(new JLabel("Versione"));
		myPanel.add(versionsField);
		myPanel.add(new JLabel("Immagine versione"));
		myPanel.add(vImageIconField);
		vImageIconField.setDragEnabled(true);
		 */

		int result = JOptionPane.showConfirmDialog(null, inputs, "Multiple Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Name value: " + nameField.getText());
			System.out.println("Quantity value: " + quantityField.getText());
		} else if(result == JOptionPane.CANCEL_OPTION) {
			return;
		}

		String name = nameField.getText();
		Integer quantity = Integer.parseInt(quantityField.getText());
		Integer price = Integer.parseInt(priceField.getText());
		Integer minimum = Integer.parseInt(minimumQuantityField.getText());
		Integer maximum = Integer.parseInt(maximumQuantityField.getText());
		String version = versionsField.getText();
		ImageIcon nIcon = new ImageIcon(nImageIconField.getText());
		ImageIcon vIcon = new ImageIcon(vImageIconField.getText());

		toServer.writeObject("Nuova merce");
		toServer.writeObject(controller);
		toServer.writeObject(name);
		toServer.writeInt(quantity);
		toServer.writeInt(price);
		toServer.writeInt(minimum);
		toServer.writeInt(maximum);
		toServer.writeObject(version);
		toServer.writeObject(nIcon);
		toServer.writeObject(vIcon);

		// Send the pay to the server
		toServer.flush();
		// Get area pay the server
		try {
			model = (WareHouseModel) fromServer.readObject();
			controller = (WareHouseController) fromServer.readObject();
			System.out.println("Ricevuto model e controller aggiornati, aggiorno la view.");
			populateJTree(rootNode, model);
			repaint();
			System.out.println("View aggiornata.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent arg0) {
		TreePath path = arg0.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String data = node.getUserObject().toString();
		System.out.println("WillCollapse: " + data);
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent arg0) {
		TreePath path = arg0.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		String data = node.getUserObject().toString();
		System.out.println("WillExpand: " + data);
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeExpanded(TreeExpansionEvent arg0) {
		// TODO Auto-generated method stub

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
		} catch (NullPointerException exc) {
			exc.printStackTrace();
		}

		System.out.println("The user has finished editing the node.");
		System.out.println("New value: " + node.getUserObject());
	}

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

	class Connection extends Thread {
		private Socket socket        = null;
		private ObjectInputStream input = null;
		private ObjectOutputStream out     = null;
		WareHouseWorkerView workerView;
		public WareHouseModel model;
		public WareHouseController controller;
		public Connection(Socket socket, WareHouseWorkerView workerView){
			this.socket = socket;
			this.workerView = workerView;
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
					workerView.populateJTree(rootNode, model);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
