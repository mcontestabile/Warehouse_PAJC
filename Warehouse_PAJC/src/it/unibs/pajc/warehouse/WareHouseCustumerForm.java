package it.unibs.pajc.warehouse;

import java.awt.*;
import javax.swing.*;
import it.unibs.pajc.utilities.*;
import java.io.IOException;
import java.net.*;

public class WareHouseCustumerForm extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    JFrame frame;
    private LoginViewCustomer logInView;
    private WareHouseCustomerView customerView;
    private String serverPort;
    private Socket socket;
    private final String ip = "127.0.0.1";


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        //server = new Server();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WareHouseCustumerForm wareHouse = new WareHouseCustumerForm();
                    wareHouse.frame.setVisible(true);
                    wareHouse.frame.setResizable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     * @throws IOException
     * @throws UnknownHostException
     */
    public WareHouseCustumerForm() throws UnknownHostException, IOException, ClassNotFoundException {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     * @throws IOException
     * @throws UnknownHostException
     */
    private void initialize() throws UnknownHostException, IOException, ClassNotFoundException {

        frame = new JFrame("Negozio");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new CardLayout());

        frame.getContentPane().setLayout(new CardLayout(20, 20));

        serverPort = JOptionPane.showInputDialog(UsefulStrings.INSERT_PORT_NUMBER);
        socket = new Socket(ip, Integer.parseInt(serverPort));
        //String answer = input.readLine();
		/*
				JOptionPane.showMessageDialog(null, answer);
				System.exit(0);
		 */

        customerView = new WareHouseCustomerView(contentPane, socket);
        logInView = new LoginViewCustomer(contentPane, socket);

        contentPane.add(logInView, UsefulStrings.LOGIN_GUI);
        contentPane.add(customerView, UsefulStrings.CUSTOMER_GUI);

        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}