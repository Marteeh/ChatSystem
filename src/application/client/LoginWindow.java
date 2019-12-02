package application.client;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame implements ActionListener, ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JPanel panel;
	JLabel usrLabel, pswrdLabel, message, externalUser;
	JTextField usrnmTxt;
	JPasswordField pswrdTxt;
	JCheckBox extUsr;
	JButton login, cancel;
	int isExternal = 0;
	
	LoginWindow() {

		// username input
		usrLabel = new JLabel();
		usrLabel.setText("User Name :");
		usrnmTxt = new JTextField();

		// password input
		pswrdLabel = new JLabel();
		pswrdLabel.setText("Password :");
		pswrdTxt = new JPasswordField();

		//External user checkbox
		externalUser = new JLabel();
		externalUser.setText("I'm an external user");
		extUsr = new JCheckBox();

		// login button
		login = new JButton("LOGIN");

		panel = new JPanel(new GridLayout(4, 1));

		panel.add(usrLabel);
		panel.add(usrnmTxt);
		panel.add(pswrdLabel);
		panel.add(pswrdTxt);
		panel.add(externalUser);
		panel.add(extUsr);
		message = new JLabel();
		panel.add(message);
		panel.add(login);

		//Adding actionlisteners
		extUsr.addItemListener(this);
		login.addActionListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(panel, BorderLayout.CENTER);
		setTitle("Welcome !");
		setSize(450, 150);
		setVisible(true);
	}

	//Listener for external user button
	@Override
	public void itemStateChanged(ItemEvent e) {

		AbstractButton absB = (AbstractButton) e.getSource();
		ButtonModel bMod = absB.getModel(); 
		boolean pressed = bMod.isSelected();

		if (pressed) { isExternal = 1; }
		else { isExternal = 0; }

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String usrnm = usrnmTxt.getText();
		char pswrdTab[] = pswrdTxt.getPassword();
		//use String or char[]??
		String pswrd = String.valueOf(pswrdTab);
		eventQueue.add();
	}

	public static void main (String[] args) {
		new LoginWindow();
	}
}
