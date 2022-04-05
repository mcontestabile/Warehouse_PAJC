package it.unibs.pajc.warehouse;

import it.unibs.pajc.utilities.UsefulStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class LoginViewCustomer extends JPanel implements ActionListener {


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
    private WareHouseCustomerView customerView;
    private WareHouseWorkerView ownerView;


    public LoginViewCustomer(JPanel cp, Socket socket) throws NumberFormatException, UnknownHostException, IOException {
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
        passwordField = new JPasswordField(15); // Lunghezza massima della password.

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

        centerPanel = new JPanel();

        // Etichetta per accedere.
        login = new JButton("Accedi");
        login.addActionListener(this);

        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        centerPanel.setOpaque(true);
        centerPanel.setBackground(Color.WHITE);
        SpringLayout sl_centerPanel = new SpringLayout();
        sl_centerPanel.putConstraint(SpringLayout.EAST, quit, -680, SpringLayout.EAST, centerPanel);
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
        centerPanel.setLayout(sl_centerPanel);
        centerPanel.add(userLabel);
        centerPanel.add(insertUsername);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(quit);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(centerPanel, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                        .addComponent(login, GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addComponent(centerPanel, GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(login)
                                .addContainerGap())
        );

        JLabel title = new JLabel("NEGOZIO");
        sl_centerPanel.putConstraint(SpringLayout.WEST, title, 320, SpringLayout.WEST, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.EAST, title, -315, SpringLayout.EAST, centerPanel);
        title.setForeground(SystemColor.textHighlight);
        title.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 20));
        centerPanel.add(title);

        authenticationLabel = new JLabel("Autenticazione richiesta");
        sl_centerPanel.putConstraint(SpringLayout.NORTH, title, -31, SpringLayout.NORTH, authenticationLabel);
        sl_centerPanel.putConstraint(SpringLayout.SOUTH, title, -6, SpringLayout.NORTH, authenticationLabel);
        sl_centerPanel.putConstraint(SpringLayout.NORTH, authenticationLabel, 101, SpringLayout.NORTH, centerPanel);
        sl_centerPanel.putConstraint(SpringLayout.WEST, authenticationLabel, 296, SpringLayout.WEST, centerPanel);
        centerPanel.add(authenticationLabel);
        setLayout(groupLayout);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void actionPerformed(ActionEvent e) {
        LoginController controller = new LoginController(insertUsername.getText(), passwordField.getText());

        // Controllo la correttezza delle credenziali immesse.
        try {
            if (controller.checkUser() && controller.getUser().getUsername().equals("pajc")) {
                // Si è autenticato il cliente? Visualizzo la chermata cliente.
                CardLayout layout = (CardLayout)loginContentPane.getLayout();
                layout.next(loginContentPane); //, UsefulStrings.CUSTOMER_GUI
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
