package it.unibs.pajc.warehouse;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Toolkit;

import it.unibs.pajc.network.Protocol;
import it.unibs.pajc.utilities.UsefulStrings;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EventObject;
import java.awt.Image;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;

/**
 * 
 * @author martinacontestabile
 *
 *
 * Interfaccia grafica. La JTable contiene i prodotti in vendita e disponibili all'acquisto,
 * mentre il JTextField contiene il numero della porta che il server deve necessariamente
 * ascoltare. Il bottone «quit» serve per uscire e terminare la connessione.
The CENTER region contains two JScrollPane both containing a JTextArea. The first JTextArea contains the messages exchanged in the ChatRoom, basically what the Clients see. The secong JTextArea contains event messages: who login, who logout, error messages, and so on
To execute that GUI type
> java ServerGUI
at the console prompt
 */
public class WareHouseOwnerView extends JPanel implements ActionListener, WindowListener,  TreeModelListener, TreeExpansionListener, TreeWillExpandListener {

	private static final long serialVersionUID = 1L;

	private JPanel warehouseContentPane, centerPanel;
	private JButton quit, onOff; // quit è il bottone che permette di passare all'altra card (login), onOff accende e spegne la connessione al server.
	private JLabel articleLabel;
	private WareHouseController controller;
	private ImageIcon warehouseIcon;
	//private boolean connected;
	//private Client client;
	//private static Socket sockey;
	//private Server server;
	private ArrayList<String> flattenedData; // Merce ordinata in ordine alfabetico, categorie delle merci.
	// to Logout and get the list of the users
	//private JButton login, logout;
	// for the chat room
	// the default port number
	private int port; // La porta di default su cui avviene il collegamento (1234).
	//private String defaultHost;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode rootNode;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private int newNodeSuffix = 1;
	private Socket socket;
	//private Protocol protocol;

	public WareHouseOwnerView(JPanel contentPane, Socket socket) {
		this.warehouseContentPane = contentPane;
		this.socket = socket;
		//this.protocol = protocol;
		getController();
		controller.getWarehouse();
		//super("Chat Server");
		initializeWareHouseComponents();
	}

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

		onOff = new JButton("Connessione");
		onOff.setBounds(673, 389, 125, 29);
		centerPanel.add(onOff);
		onOff.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				
			}
			
		});

		// Costruizione dell'albero, da cui il cliente vede i prodotti disponibili a catalogo.

		// Create root node of tree
		rootNode = new DefaultMutableTreeNode("Articoli a magazzino");
		populateJTree(rootNode); // articleNode, sono gli articoli.

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
						DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
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
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
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
								showInput(units, version, article);
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

		JButton addButton = new JButton("add");
		addButton.setSize(92, 28);
		addButton.setLocation(229, 133);
		addButton.setActionCommand(UsefulStrings.ADD_COMMAND);
		addButton.addActionListener(this);
		centerPanel.add(addButton);

		JButton removeButton = new JButton("remove");
		removeButton.setLocation(229, 163);
		removeButton.setSize(92, 28);
		removeButton.setActionCommand(UsefulStrings.REMOVE_COMMAND);
		removeButton.addActionListener(this);
		centerPanel.add(removeButton);

		JButton clearButton = new JButton("clear");
		clearButton.setSize(92, 28);
		clearButton.setLocation(229, 193);
		clearButton.setActionCommand(UsefulStrings.CLEAR_COMMAND);
		clearButton.addActionListener(this);
		centerPanel.add(clearButton);
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
	public void populateJTree(DefaultMutableTreeNode root) {

		for(Article article : controller.getWarehouse().getProducts()) {
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

	public void showInput(Integer units, String version, Article article) {
		String value = JOptionPane.showInputDialog(null, "Inserire le unità desiderate di " + version, null, JOptionPane.PLAIN_MESSAGE);
		
		if (!value.equals("")) {
			controller.settingNewUnits(Integer.parseInt(value), units, version, article);
		}
		populateJTree(rootNode);
		repaint();
	}
	
	public WareHouseController getController() {
		controller = new WareHouseController(this, new WareHouseModel());
		return controller;       
	}

	// append message to the two JTextArea
	// position at the end
	/*
	protected void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}

	protected void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);

	}
	 */

	/*
	 * Thread per il funzionamento del server.
	class ServerRunning extends Thread {
		public void run() {
			server.start();         // should execute until if fails
			// the server failed
			onOff.setText("Connettiti");
			portNumber.setEditable(true);
			appendEvent("Il server non funziona.\n");
			server = null;
		}
	}
	 */

	/*
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
			onOff.setText("Connettiti");
			return;
		}
		// OK start the server	
		int port;
		try {
			port = Integer.parseInt(portNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Porta inesistente");
			return;
		}
		// ceate a new Server
		server = new Server(port, this);
		// and start it as a thread
		new ServerRunning().start();
		onOff.setText("Stop");
		portNumber.setEditable(false);
	}

	private void updateAfterOrder( ) {
		repaint();
	}

	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		onOff.setEnabled(true);
		quit.setEnabled(false);
		portNumber.setText("" + port); // reset port number and host name as a construction time
		portNumber.setEditable(false); // L'utente può cambiare la porta di collegamento.
		connected = false;
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
	public DefaultMutableTreeNode addObject(Object child) {
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
			node = (DefaultMutableTreeNode)(e.getTreePath().getLastPathComponent());

			/*
			 * If the event lists children, then the changed
			 * node is the child of the node we've already
			 * gotten.  Otherwise, the changed node and the
			 * specified node are the same.
			 */

			int index = e.getChildIndices()[0];
			node = (DefaultMutableTreeNode)(node.getChildAt(index));

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

		if (UsefulStrings.ADD_COMMAND.equals(command)) {
			//Add button clicked
			addObject("New Node " + newNodeSuffix++);
		} else if (UsefulStrings.REMOVE_COMMAND.equals(command)) {
			//Remove button clicked
			removeCurrentNode();
		} else if (UsefulStrings.CLEAR_COMMAND.equals(command)) {
			//Clear button clicked.
			clear();
		}
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
		} 
		catch (NullPointerException exc) {}

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
