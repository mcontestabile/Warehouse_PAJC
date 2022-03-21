package it.unibs.pajc.warehouse;

import java.awt.EventQueue;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unibs.pajc.network.Protocol;
import it.unibs.pajc.network.Server;
import it.unibs.pajc.utilities.UsefulStrings;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainForm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	final String host = "127.0.0.1";
	final int port = 4040;
	JFrame frame;
	private LoginView logInView;
	private WareHouseCustomerView customerView;
	private WareHouseOwnerView warehousemanView;
	//private static Server server;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//server = new Server();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm wareHouse = new MainForm();
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
	public MainForm() throws UnknownHostException, IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	private void initialize() throws UnknownHostException, IOException {
		String serverAddress = JOptionPane.showInputDialog(UsefulStrings.INSERT_SERVER_NAME);
		String serverPort = JOptionPane.showInputDialog(UsefulStrings.INSERT_PORT_NUMBER);
		Socket socket = new Socket(serverAddress, Integer.parseInt(serverPort)); //La socket si chiude nella classe Server, non serve chiuderla qui.
		/*
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String answer = input.readLine();
		JOptionPane.showMessageDialog(null, answer);
		System.exit(0);
		*/
		
		frame = new JFrame("Magazzino");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new CardLayout());

		frame.getContentPane().setLayout(new CardLayout(20, 20));

		logInView = new LoginView(contentPane, socket);
		customerView = new WareHouseCustomerView(contentPane, socket);
		warehousemanView = new WareHouseOwnerView(contentPane, socket);

		contentPane.add(logInView, UsefulStrings.LOGIN_GUI);
		contentPane.add(customerView, UsefulStrings.CUSTOMER_GUI);
		contentPane.add(warehousemanView, UsefulStrings.WAREHOUSEMAN_GUI);

		frame.setContentPane(contentPane);      
		frame.pack();   
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}
}