package application.client;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panel;
	JLabel usrLabel, pswrdLabel, message;
	JTextField usrnmTxt;
	JPasswordField pswrdTxt;
	JButton login, cancel;

	LoginWindow() {

		// username input
		usrLabel = new JLabel();
		usrLabel.setText("User Name :");
		usrnmTxt = new JTextField();

		// username input
		pswrdLabel = new JLabel();
		pswrdLabel.setText("User Name :");
		pswrdTxt = new JPasswordField();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
