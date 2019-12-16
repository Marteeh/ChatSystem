package application.client;

import utils.EventQueue;

import java.awt.event.*;
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
		/* mailLabel = new JLabel();
		mailLabel.setText("Mail :");
		mailTxt = new JTextField();

		//password input
		pswrdLabel = new JLabel();
		pswrdLabel.setText("Password");
		pswrd = new JPasswordField();*/

		//External user checkbox
		externalUser = new JLabel();
		externalUser.setText("I'm an external user");
		extUsr = new JCheckBox();

		// login button
		login = new JButton("LOGIN");
		login.setEnabled(false);

		panel = new JPanel(new GridLayout(3, 1));

		panel.add(usrLabel);
		panel.add(usrnmTxt);
		/*panel.add(mailLabel);
		panel.add(mailTxt);
		panel.add(pswrdLabel);
		panel.add(pswrd);*/
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

		isExternal = isPressed;
	}
	
	//Listener for "login" button
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		String usrnm = usrnmTxt.getText();
		//String mail = mailTxt.getText();
		LoginEvent evt = new LoginEvent(usrnm, isExternal);
		login.setEnabled(false);
		login.setText("Checking username...");

		try {
			eventQueue.addEventToQueue(evt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void enableLoginButton() {
		login.setEnabled(true);
	}
	
	public static void main (String[] args) {
		EventQueue evtq = new EventQueue(null);
		new LoginWindow(evtq);
	}
	
}
