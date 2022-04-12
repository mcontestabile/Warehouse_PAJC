package it.unibs.pajc.warehouse;

import javax.management.modelmbean.ModelMBean;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Toolkit;

import it.unibs.pajc.network.Server;
import it.unibs.pajc.utilities.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
/**
 *
 * @author martinacontestabile
 *
 *
 * Interfaccia grafica. Il JTree contiene i prodotti in vendita e disponibili all'acquisto,
 * Il bottone «quit» serve per uscire e terminare la connessione.
 * 
 * 
 * 
 * Fonti utilizzate per comprenddere come
 * sfruttare questa struttura per salvare
 * file immagini sulle nuove label
 * 
 * https://stackoverflow.com/questions/34487105/add-actionlistener-to-a-button-in-joptionpane-java
 * https://stackoverflow.com/questions/22261130/how-to-save-a-file-using-jfilechooser-showsavedialog
 * https://stackoverflow.com/questions/27709758/file-chooser-display-with-a-button-click-java-swing
 * https://stackoverflow.com/questions/16351875/jfilechooser-on-a-button-click
 * https://alvinalexander.com/java/joptionpane-showmessagedialog-example-scrolling/
 * http://www.simplesoft.it/java/tutorial-jfilechooser-in-java-swing.html
 * http://www2.hawaii.edu/~takebaya/ics111/jfilechooser/jfilechooser.html
 * https://www.codejava.net/java-se/swing/show-save-file-dialog-using-jfilechooser
 * https://www.tutorialspoint.com/swingexamples/show_file_chooser_images_only.htm
 * http://www.java2s.com/Tutorials/Java/Swing_How_to/JFileChooser/Make_JFileChooser_to_save_file.htm
 * https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html#filters
 * https://stackoverflow.com/questions/13517770/jfilechooser-filters
 * https://stackoverflow.com/questions/28757272/how-to-restrict-a-jfilechooser-to-a-custom-file-type
 * https://stackoverflow.com/questions/25666642/jfilechooser-to-pick-a-directory-or-a-single-file
 * 
 * 
 */
public class WareHouseWokerView extends JPanel implements ActionListener, WindowListener,  TreeModelListener, TreeExpansionListener, TreeWillExpandListener {
	private static final long serialVersionUID = 1L;

	private JPanel warehouseContentPane, centerPanel;
	private JButton quit, addButton, removeButton; // quit è il bottone che permette di passare all'altra card (login), add aggiunge merce e remove la toglie.
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private Toolkit toolkit = Toolkit.getDefaultToolkit(); // riproduce il suono di sistema.

	private Socket socket;

	// IO streams
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	// Per salvare le immagini delle nuove merci.
	ImageIcon nImageIcon = new ImageIcon();
	ImageIcon vImageIcon = new ImageIcon();
	
	WareHouseController controller;

	public WareHouseWokerView(JPanel contentPane, Socket socket) throws IOException, ClassNotFoundException {
		this.warehouseContentPane = contentPane;

		this.socket = socket;
		fromServer = new ObjectInputStream(socket.getInputStream()); // Create an input stream to receive data from the server
		toServer = new ObjectOutputStream(socket.getOutputStream()); // Create an output stream to send data to the server

		Thread receiver = new Thread(new Receiver());
		receiver.setDaemon(true);
		receiver.start();

		System.out.println("Prima della chiamata");
		getModel();
		getController();
		System.out.println("Dopo la chiamata");
		
		initializeWareHouseComponents();
	}

	public class Receiver implements Runnable, TreeModelListener {
		public void run() {
			while(true) {
				String request;
				try {
					while ((request = (String) fromServer.readObject()) != null) {
						//request = (String) in.readObject();

						switch (request) {

						case "Controller":
							controller = (WareHouseController) fromServer.readObject();
							System.out.println("Ho ricevuto il controller: " + controller);
							System.out.println("\n\n\n\n");
							
							DefaultTreeModel newModel = populateJTree(rootNode, controller);
							tree.setModel(newModel);
							
							break;

						case "Aggiornato":
							controller = (WareHouseController) fromServer.readObject();
							System.out.println("Ho ricevuto il controller: " + controller + " aggiornato.");
							System.out.println("\n\n\n\n");
							
							tree.removeAll();
							rootNode.removeAllChildren();
							newModel = populateJTree(rootNode, controller);
							tree.setModel(newModel);
							
							break;	
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

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

	private void initializeWareHouseComponents() throws IOException, ClassNotFoundException {
		System.out.println("E' nel metodo");
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

		ImageIcon warehouseIcon = new ImageIcon(new ImageIcon(UsefulStrings.WH_ICON).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
		JLabel icon = new JLabel(warehouseIcon);
		icon.setBounds(643, 5, 154, 149);
		centerPanel.add(icon);
		centerPanel.setLayout(null);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(quit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(6)
										.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 809, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 423, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(quit)
						.addContainerGap(426, Short.MAX_VALUE))
				);
		setLayout(groupLayout);

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.

		// Create root node of tree
		rootNode = new DefaultMutableTreeNode("Articoli a magazzino");
		//populateJTree(rootNode, model); // articleNode, sono gli articoli.

		// Create tree model
		treeModel = new DefaultTreeModel(rootNode);
		treeModel.addTreeModelListener(this);
		treeModel.setAsksAllowsChildren(true);

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
						for (int i = 0; i < controller.getWarehouse().getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = controller.getWarehouse().getVersionIcon(version);
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
						for (int i = 0; i < controller.getWarehouse().getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = controller.getWarehouse().getVersionIcon(version);
								Integer units = article.getQuantity()[i];
								try {
									showInput(units, version, article);
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


		addButton = new JButton("aggiungi merce");
		addButton.setSize(121, 28);
		addButton.setLocation(229, 162);
		addButton.setActionCommand(UsefulStrings.ADD_COMMAND);
		addButton.addActionListener(this);
		centerPanel.add(addButton);

		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					joptionpaneMultiInput(controller);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});


		removeButton = new JButton("rimuovi merce");
		removeButton.setLocation(229, 192);
		removeButton.setSize(121, 28);
		removeButton.setActionCommand(UsefulStrings.REMOVE_COMMAND);
		removeButton.addActionListener(this);
		centerPanel.add(removeButton);

		JLabel welcomeWorker = new JLabel(UsefulStrings.WELCOME_WORKER, SwingConstants.RIGHT);
		welcomeWorker.setFont(new Font("Lucida Grande", Font.BOLD, 10));
		welcomeWorker.setBounds(466, 223, 337, 195);
		welcomeWorker.setAlignmentX(Component.RIGHT_ALIGNMENT);
		centerPanel.add(welcomeWorker);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(229, 221, 121, 29);
		centerPanel.add(btnNewButton);
	}

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
	public DefaultTreeModel populateJTree(DefaultMutableTreeNode root, WareHouseController controller) {

		for(Article article : controller.getWarehouse().getProducts()) {
			DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode(article, true);
			root.add(articleNode);

			for(String versionName : article.getVersions().keySet()) {
				DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(versionName, true);
				articleNode.add(versionNode);
			}
		}
		return new DefaultTreeModel(root);
	}

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public void showMessage(String msg, ImageIcon image) {
		JOptionPane.showMessageDialog(null, new JLabel(msg, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price) {
		String message = msg + "\n"
				+ "Prezzo: " + price + "\n";

		JOptionPane.showMessageDialog(null, new JLabel(message, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price, Integer units) {
		String message = msg + "\n"
				+ "Prezzo: " + price + "\n"
				+ "Disponibili: " + units;

		JOptionPane.showMessageDialog(null, new JLabel(message, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showInput(int units, String version, Article article) throws IOException {
		JTextField quantity = new JTextField(5);

		Object[] inputs = {"Inserire le unità desiderate di " + version, quantity};

		int result = JOptionPane.showConfirmDialog(null, inputs, "Effettuare l'ordine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Quantità desiderata: " + quantity.getText());

			if (!quantity.equals("") || !quantity.equals(null)) {
				toServer.writeObject("Ordine");
				toServer.writeObject(article);
				toServer.writeObject(controller);
				toServer.writeInt(Integer.parseInt(quantity.getText()));
				toServer.writeObject(version);

				// Send the pay to the server
				toServer.flush();
				toServer.reset();
			} else return;
		} else if(result == JOptionPane.CLOSED_OPTION) {
			return;
		}
	}


	public void getModel() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il model");
		toServer.writeObject("Model");
		toServer.flush();
		toServer.reset();
	}

	public void getController() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il controller");
		toServer.writeObject("Controller");
		toServer.flush();
		toServer.reset();
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
		System.out.println("Richiedo il controller");
		toServer.writeObject("Controller");
		toServer.flush();
		//WareHouseController controller = new WareHouseController();
		//return controller;
		WareHouseController controller = (WareHouseController) fromServer.readObject();
		System.out.println("W Ho ricevuto controller.");
		return controller;
	}
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

	public void joptionpaneMultiInput(WareHouseController controller) throws IOException {
		JTextField nameField = new JTextField(5);
		JTextField quantityField = new JTextField(5);
		JTextField priceField = new JTextField(5);
		JTextField minimumQuantityField = new JTextField(5);
		JTextField maximumQuantityField = new JTextField(5);
		JButton articleButton = new JButton("aggiungi");
		JTextField versionsField = new JTextField(5);
		JButton versionButton = new JButton("aggiungi");

		Object[] inputs = {"Aggiungi categoria « articolo »", nameField,
				"Quantità", quantityField,
				"Prezzo", priceField,
				"Minimo", minimumQuantityField,
				"Massimo", maximumQuantityField,
				"Aggiungi immagine « articolo »", articleButton,
				"Versione", versionsField,
				"Aggiungi immagine « versione »", versionButton};

		articleButton.addActionListener (e -> {
			JFileChooser jfcArticle = new JFileChooser(FileSystemView.getFileSystemView());
			jfcArticle.setDialogTitle("Scegli un'immagine per l'articolo");
			jfcArticle.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfcArticle.addChoosableFileFilter(new FileFilter() {

				public String getDescription() {
					return "File di tipo immagine";
				}

				public boolean accept(File f) {

					// Allow directories to be seen.
					if (f.isDirectory()) return true;

					// Allows files with .rtf extension to be seen.
					if(f.getName().endsWith(".png") ||
							f.getName().endsWith(".jpeg") ||
							f.getName().endsWith(".jpg") ||
							f.getName().endsWith(".gif"))
						return true;

					// Otherwise file is not shown.
					return false;
				}
			});
			jfcArticle.setAcceptAllFileFilterUsed(false);

			int userSelection = jfcArticle.showOpenDialog(this);
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File file = jfcArticle.getSelectedFile();
				nImageIcon = new ImageIcon(file.getPath());
				System.out.println("file » " + file.getAbsolutePath());
				System.out.println("Settato nIcon.");
			} else {
				System.out.println("No file choosen!");
			}
		});

		versionButton.addActionListener (e -> {
			JFileChooser jfcVersion = new JFileChooser(FileSystemView.getFileSystemView());
			jfcVersion.setDialogTitle("Scegli un'immagine per la versione dell'articolo"); 
			jfcVersion.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfcVersion.addChoosableFileFilter(new FileFilter() {

				public String getDescription() {
					return "File di tipo immagine";
				}

				public boolean accept(File f) {

					// Allow directories to be seen.
					if (f.isDirectory()) return true;

					// Allows files with .rtf extension to be seen.
					if(f.getName().toLowerCase().endsWith(".png") ||
							f.getName().toLowerCase().endsWith(".jpeg") ||
							f.getName().toLowerCase().endsWith(".jpg") ||
							f.getName().toLowerCase().endsWith(".gif"))
						return true;

					// Otherwise file is not shown.
					return false;
				}
			});
			jfcVersion.setAcceptAllFileFilterUsed(false);

			int userSelection = jfcVersion.showOpenDialog(this);
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File file = jfcVersion.getSelectedFile();
				vImageIcon = new ImageIcon(file.getPath());
				System.out.println("file » " + file.getAbsolutePath());
				System.out.println("Settato vIcon.");
			} else {
				System.out.println("No file choosen!");
			}
		});

		int result = JOptionPane.showConfirmDialog(null, inputs, "Multiple Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Name value: " + nameField.getText());
			System.out.println("Quantity value: " + quantityField.getText());
		} else if(result == JOptionPane.CANCEL_OPTION)
			return;

		String name = nameField.getText();
		Integer quantity = Integer.parseInt(quantityField.getText());
		Integer price = Integer.parseInt(priceField.getText());
		Integer minimum = Integer.parseInt(minimumQuantityField.getText());
		Integer maximum = Integer.parseInt(maximumQuantityField.getText());
		String version = versionsField.getText();

		System.out.println("Invio i dati.");
		toServer.writeObject("Nuova merce");
		toServer.writeObject(controller);
		toServer.writeObject(name);
		toServer.writeInt(quantity);
		toServer.writeInt(price);
		toServer.writeInt(minimum);
		toServer.writeInt(maximum);
		toServer.writeObject(version);
		toServer.writeObject(nImageIcon);
		toServer.writeObject(vImageIcon);

		// Send the pay to the server
		toServer.flush();
		toServer.reset();
		
		System.out.println("Dati inviati.");
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
}