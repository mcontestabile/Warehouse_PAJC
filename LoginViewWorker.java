package it.unibs.pajc.warehouse;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import it.unibs.pajc.utilities.UsefulStrings;

public class LoginViewWorker extends JPanel implements ActionListener {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	// Creazione dei bottone, pannello, etichetta e campo di testo.
	private JButton login, quit; // login è quello che permette di passare all'altra card.
	private JPanel loginContentPane, centerPanel;
	private JLabel userLabel, passwordLabel, authenticationLabel;  
	private JTextField  insertUsername, passwordField;
	private Socket socket;


	public LoginViewWorker(JPanel cp, Socket socket) throws NumberFormatException, UnknownHostException, IOException {
		this.loginContentPane = cp;
		this.socket = socket;
		initializeLoginComponents();
	}

	private void initializeLoginComponents() throws NumberFormatException, UnknownHostException, IOException {

		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setBackground(new Color(0, 191, 255));

		// Etichetta dell'username.
		userLabel = new JLabel("Username");     // Imposto il testo dell'etichetta. 

		// Campo per permettere all'utente di inserire il suo username.  
		insertUsername = new JTextField(15);    // Lunghezza massima del testo.

		// Etichetta della password.
		passwordLabel = new JLabel("Password"); // Imposto il testo dell'etichetta. 

		// Campo per permettere all'utente di inserire la sua password.   
		passwordField = new JPasswordField(15);

		centerPanel = new JPanel();

		// Etichetta per accedere. 
		login = new JButton("Accedi");
		login.addActionListener(this);

		centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPanel.setOpaque(true);
		centerPanel.setBackground(Color.WHITE);
		SpringLayout sl_centerPanel = new SpringLayout();
		sl_centerPanel.putConstraint(SpringLayout.WEST, userLabel, 172, SpringLayout.WEST, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.EAST, userLabel, -56, SpringLayout.WEST, insertUsername);
		sl_centerPanel.putConstraint(SpringLayout.WEST, insertUsername, 0, SpringLayout.WEST, passwordField);
		sl_centerPanel.putConstraint(SpringLayout.SOUTH, insertUsername, 29, SpringLayout.NORTH, userLabel);
		sl_centerPanel.putConstraint(SpringLayout.WEST, passwordField, 296, SpringLayout.WEST, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.EAST, passwordField, -166, SpringLayout.EAST, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.NORTH, insertUsername, -5, SpringLayout.NORTH, userLabel);
		sl_centerPanel.putConstraint(SpringLayout.EAST, insertUsername, 0, SpringLayout.EAST, passwordField);
		sl_centerPanel.putConstraint(SpringLayout.WEST, passwordLabel, 172, SpringLayout.WEST, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.EAST, passwordLabel, -46, SpringLayout.WEST, passwordField);
		sl_centerPanel.putConstraint(SpringLayout.SOUTH, userLabel, -53, SpringLayout.NORTH, passwordLabel);
		sl_centerPanel.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.NORTH, passwordField);
		sl_centerPanel.putConstraint(SpringLayout.NORTH, passwordField, 279, SpringLayout.NORTH, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.SOUTH, passwordField, -94, SpringLayout.SOUTH, centerPanel);
		centerPanel.setLayout(sl_centerPanel);
		centerPanel.add(userLabel);
		centerPanel.add(insertUsername);
		centerPanel.add(passwordLabel);
		centerPanel.add(passwordField);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
				.addComponent(login, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(login)
						.addContainerGap())
				);

		JLabel title = new JLabel("MAGAZZINO");
		sl_centerPanel.putConstraint(SpringLayout.WEST, title, 306, SpringLayout.WEST, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.EAST, title, -172, SpringLayout.EAST, centerPanel);
		title.setForeground(SystemColor.textHighlight);
		title.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 20));
		centerPanel.add(title);

		authenticationLabel = new JLabel("Autenticazione richiesta");
		sl_centerPanel.putConstraint(SpringLayout.SOUTH, title, -3, SpringLayout.NORTH, authenticationLabel);
		sl_centerPanel.putConstraint(SpringLayout.NORTH, authenticationLabel, 101, SpringLayout.NORTH, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.WEST, authenticationLabel, 296, SpringLayout.WEST, centerPanel);
		centerPanel.add(authenticationLabel);

		
		// Etichetta per uscire dal programma. 
		quit = new JButton("Esci");
		quit.setForeground(Color.RED);
		sl_centerPanel.putConstraint(SpringLayout.NORTH, quit, 0, SpringLayout.NORTH, centerPanel);
		sl_centerPanel.putConstraint(SpringLayout.WEST, quit, 0, SpringLayout.WEST, centerPanel);
		centerPanel.add(quit);
		setLayout(groupLayout);

		// Etichetta per uscire dal programma.
		quit = new JButton("Esci");
		quit.setBackground(UIManager.getColor("info"));
		quit.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		quit.setForeground(Color.RED);
		quit.addActionListener (e -> {
			try {
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer.writeBytes("Esci");
				socket.close(); // Chiudo la socket, mi disconnetto dal server.
				System.exit(0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public void actionPerformed(ActionEvent e) {
		LoginController controller = new LoginController(insertUsername.getText(), passwordField.getText());

		// Controllo la correttezza delle credenziali immesse. 
		try {
			if(controller.checkUser() && controller.getUser().getUsername().equals("mcontestabile")) {
				// Si è autenticato il magazziniere? Visualizzo la chermata proprietario.
				CardLayout layout = (CardLayout)loginContentPane.getLayout();
				layout.next(loginContentPane); //, UsefulStrings.WAREHOUSEMAN_GUI
			} else {
				// Autenticazione non valida = messaggio d'errore.
				showMessage(UsefulStrings.ERROR_LOGIN);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
	}
}
