package it.unibs.pajc.warehouse;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import it.unibs.pajc.utilities.UsefulStrings;

public class LoginViewServer extends JPanel implements ActionListener {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	// Creazione dei bottone, pannello, etichetta e campo di testo.
    JButton login, quit; // login è quello che permette di passare all'altra card.
    JPanel loginContentPane, centerPanel;
    JLabel userLabel, passwordLabel, loginLabel;  
    JTextField  insertUsername, passwordField;
    private LoginModel model;
    private JLabel lblNewLabel_1;

    
	public LoginViewServer(JPanel cp) {
		this.loginContentPane = cp;
		initializeLoginComponents();
    }

    private void initializeLoginComponents() {
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
        passwordField = new JPasswordField(15); // Lunghezza massima della password. 
        
        // Etichetta per uscire dal programma. 
        quit = new JButton("Esci");
        quit.setBackground(UIManager.getColor("info"));
        quit.setFont(new Font("Lucida Grande", Font.BOLD, 13));
        quit.setForeground(Color.RED);
        quit.addActionListener (new ActionListener () {
        	 public void actionPerformed (ActionEvent e) {
        		 System.exit(0);
        	 }
        });
        
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
        sl_centerPanel.putConstraint(SpringLayout.NORTH, quit, 0, SpringLayout.NORTH, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.WEST, quit, 0, SpringLayout.WEST, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.SOUTH, quit, 27, SpringLayout.NORTH, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.EAST, quit, -732, SpringLayout.EAST, centerPanel);
        centerPanel.setLayout(sl_centerPanel);
        centerPanel.add(userLabel);
        centerPanel.add(insertUsername);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(quit);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
        				.addGroup(groupLayout.createSequentialGroup()
        					.addComponent(login, GroupLayout.DEFAULT_SIZE, 809, Short.MAX_VALUE)
        					.addContainerGap())))
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(login)
        			.addContainerGap())
        );
        
        JLabel title = new JLabel("W A R E H O U S E    I C O N");
        sl_centerPanel.putConstraint(SpringLayout.WEST, title, 229, SpringLayout.WEST, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.SOUTH, title, -307, SpringLayout.SOUTH, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.EAST, title, -270, SpringLayout.EAST, centerPanel);
        title.setForeground(SystemColor.textHighlight);
        title.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 20));
        centerPanel.add(title);
        
        lblNewLabel_1 = new JLabel("Autenticazione richiesta");
        sl_centerPanel.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 6, SpringLayout.SOUTH, title);
        sl_centerPanel.putConstraint(SpringLayout.WEST, lblNewLabel_1, 296, SpringLayout.WEST, centerPanel);
        centerPanel.add(lblNewLabel_1);
        setLayout(groupLayout);
    }
    
	public LoginModel getUser() {
		model = new LoginModel(insertUsername.getText(), passwordField.getText());
		return model;       
	}
	
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	public void actionPerformed(ActionEvent e) {
		LoginController controller = new LoginController(this);
		
        // Controllo la correttezza delle credenziali immesse. 
        try {
			if (controller.checkUser(getUser()) && getUser().getUsername().equals("pajc")) {
				// Si è autenticato il proprietario del magazzino? Visualizzo la chermata proprietario.
				CardLayout layout = (CardLayout)loginContentPane.getLayout();
			    layout.next(loginContentPane);
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
