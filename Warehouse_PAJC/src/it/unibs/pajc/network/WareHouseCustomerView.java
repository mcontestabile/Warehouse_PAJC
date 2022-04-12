package it.unibs.pajc.warehouse;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.*;
import javax.swing.tree.*;
import it.unibs.pajc.utilities.*;

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
	private JButton quit; // bottone che permette di passare all'altra card (login).
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	
	private Socket socket;

	// IO streams
	private ObjectOutputStream toServer;
	private ObjectInputStream fromServer;

	private WareHouseModel model;

	public WareHouseCustomerView(JPanel contentPane, Socket socket) throws IOException, ClassNotFoundException {
		this.warehouseContentPane = contentPane;

		this.socket = socket;
		fromServer = new ObjectInputStream(socket.getInputStream()); // Create an input stream to receive data from the server
		toServer = new ObjectOutputStream(socket.getOutputStream()); // Create an output stream to send data to the server

		Thread receiver = new Thread(new Receiver());
		receiver.setDaemon(true);
		receiver.start();

		System.out.println("Prima della chiamata");
		getModel();
		System.out.println("Dopo la chiamata");

		initializeWareHouseComponents();
	}

	private class Receiver implements Runnable, Serializable {
		public void run() {
			while(true) {
				String request;
				try {
					while (socket.isConnected() && (request = (String) fromServer.readObject()) != null) {

						switch (request) {
						case "Model":
							model = (WareHouseModel) fromServer.readObject();
							System.out.println("Ho ricevuto il model: " + model);
							System.out.println("\n\n\n\n");

							DefaultTreeModel newModel = populateJTree(rootNode, model);
							tree.setModel(newModel);
							break;

						case "Aggiornato":
							model = (WareHouseModel) fromServer.readObject();
							System.out.println("Ho ricevuto il model aggiornato: " + model);
							System.out.println("\n\n\n\n");
							
							tree.removeAll();
							rootNode.removeAllChildren();
							newModel = populateJTree(rootNode, model);
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
	}

	@SuppressWarnings("unchecked")
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

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.
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
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
						Article article = (Article)parent.getUserObject();
						String version = (String)nodeInfo;
						for (int i = 0; i < model.getSubcategories().size(); i++) {
							if(article.getVersions().containsKey(version)) {
								ImageIcon icon = model.getVersionIcon(version);
								Integer price = article.getPrice()[i];
								Integer units = article.getQuantity()[i];
								showMessage(version, icon, price);
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


		JLabel welcomeLabel = new JLabel("Benvenuto in Negozio");
		welcomeLabel.setBounds(300, 5, 200, 20);
		welcomeLabel.setLabelFor(centerPanel);
		welcomeLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		centerPanel.add(welcomeLabel);
		
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
	 * Nuovo messaggio popup per l'utente.
	 *
	 *
	 * @param msg — messaggio da mostrare.
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * Inserisco la merce nel Jtree.
	 *
	 * @param root — il nome del magazzino.
	 * @return 
	 */
	public DefaultTreeModel populateJTree(DefaultMutableTreeNode root, WareHouseModel model) {

		for(Article article : model.getProducts()) {
			DefaultMutableTreeNode articleNode = new DefaultMutableTreeNode(article, true);
			root.add(articleNode);

			for(String versionName : article.getVersions().keySet()) {
				DefaultMutableTreeNode versionNode = new DefaultMutableTreeNode(versionName, true);
				articleNode.add(versionNode);
			}
		}
		
		return new DefaultTreeModel(rootNode);
	}

	public void showMessage(String msg, ImageIcon image) {
		JOptionPane.showMessageDialog(null, new JLabel(msg, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showMessage(String msg, ImageIcon image, Integer price) {
		String message = msg + "\n"
				+ "Prezzo: " + price + "\n";

		JOptionPane.showMessageDialog(null, new JLabel(message, JLabel.LEFT), msg, JOptionPane.INFORMATION_MESSAGE, image);
	}

	public void showInput(int units, String version, Article article) throws IOException {
		JTextField quantity = new JTextField(5);

		Object[] inputs = {"Inserire le unità desiderate di " + version, quantity};

		int result = JOptionPane.showConfirmDialog(null, inputs, "Effettuare l'ordine", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Quantità desiderata: " + quantity.getText());

			if (!quantity.equals("") || !quantity.equals(null)) {
				toServer.writeObject("Acquisto");
				toServer.writeObject(article);
				toServer.writeObject(model);
				toServer.writeInt(Integer.parseInt(quantity.getText()));
				toServer.writeObject(version);

				// Send the pay to the server
				toServer.flush();
			} else return;
		} else if(result == JOptionPane.CLOSED_OPTION) 
			return;
		
	}

	public void getModel() throws IOException, ClassNotFoundException {
		System.out.println("Richiedo il model");
		toServer.writeObject("Model");
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
}