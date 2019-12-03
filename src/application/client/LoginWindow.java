package application.client;

import application.client.LoginEvent;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame implements ActionListener, ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static JPanel panel;
	static JLabel usrLabel, mailLabel, message, externalUser;
	static JTextField usrnmTxt, mailTxt;
	static JCheckBox extUsr;
	static JButton login, cancel;
	static int isExternal = 0;
	
	LoginWindow() {

		// username input
		usrLabel = new JLabel();
		usrLabel.setText("User Name :");
		usrnmTxt = new JTextField();

		// mail/username input
		mailLabel = new JLabel();
		mailLabel.setText("Mail :");
		mailTxt = new JTextField();

		//External user checkbox
		externalUser = new JLabel();
		externalUser.setText("I'm an external user");
		extUsr = new JCheckBox();

		// login button
		login = new JButton("LOGIN");

		panel = new JPanel(new GridLayout(4, 1));

		panel.add(usrLabel);
		panel.add(usrnmTxt);
		panel.add(mailLabel);
		panel.add(mailTxt);
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

	public static void incorrectUsername(){
		message.setText("Username Incorrect, please select another one");
	}

	//Listener for "external user" button
	@Override
	public void itemStateChanged(ItemEvent e) {

		AbstractButton absB = (AbstractButton) e.getSource();
		ButtonModel bMod = absB.getModel(); 
		boolean isPressed = bMod.isSelected();

		if (isPressed) { isExternal = 1; }
		else { isExternal = 0; }
	}
	
	//Listener for "login" button
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String usrnm = usrnmTxt.getText();
		//LoginEvent evt = new LoginEvent(usrnm, isExternal);
		login.setEnabled(false);
		login.setText("Checking username...");
		//eventQueue.addEventToQueue(evt);
	}

	//pour test (?)
	public static void main (String[] args) {
		new LoginWindow();
	}
}
