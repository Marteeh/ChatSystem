package application.client;

import utils.EventQueue;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

public class LoginWindow extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 1L;
	
	private final EventQueue eventQueue;

	JPanel panel;
	JLabel usrLabel, mailLabel, pswrdLabel, externalUser;
	JTextField usrnmTxt, mailTxt;
	JCheckBox extUsr;
	JPasswordField pswrd;
	JButton login, cancel;
	boolean isExternal = false;
	static JLabel message;
	
	LoginWindow(EventQueue eventQueue) {
		
		this.eventQueue = eventQueue;

		// username input
		usrLabel = new JLabel();
		usrLabel.setText("User Name :");
		usrnmTxt = new JTextField();

		// mail input
		mailLabel = new JLabel();
		mailLabel.setText("Mail :");
		mailTxt = new JTextField();

		//password input
		pswrdLabel = new JLabel();
		pswrdLabel.setText("Password");
		pswrd = new JPasswordField();

		//External user checkbox
		externalUser = new JLabel();
		externalUser.setText("I'm an external user");
		extUsr = new JCheckBox();

		// login button
		login = new JButton("LOGIN");

		panel = new JPanel(new GridLayout(5, 1));

		panel.add(usrLabel);
		panel.add(usrnmTxt);
		panel.add(mailLabel);
		panel.add(mailTxt);
		panel.add(pswrdLabel);
		panel.add(pswrd);
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

		if (isPressed) { isExternal = true; }
		else { isExternal = false; }
	}
	
	//Listener for "login" button
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		String usrnm = usrnmTxt.getText();
		String mail = mailTxt.getText();
		LoginEvent evt = new LoginEvent(usrnm, mail, isExternal);
		login.setEnabled(false);
		login.setText("Checking username...");
		try {
			eventQueue.addEventToQueue(evt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/*
	public static void main (String[] args) {
		new LoginWindow();
	}
	*/
}
