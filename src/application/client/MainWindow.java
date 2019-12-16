package application.client;

import application.User;
import utils.EventQueue;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame implements ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private EventQueue eventQueue;
	JPanel panel;
	JList<String> liste;
	DefaultListModel<String> model;
	JButton openChat;
	JLabel description;
	List<User> connectedUsers = new ArrayList<User>();
	
	/**
	 * MainWindow launcher
	 * 
	 * @param currentUser User type representing the current user, used for the
	 *                    window title.
	 * @param eventQueue  the queue used for receiving and sending events to the
	 *                    controler.
	 */
	MainWindow(User currentUser, EventQueue eventQueue) {

		this.eventQueue = eventQueue;

		// Connected User list, updates with AddConnectedUser(), RemoveConnectedUser(),
		// or setConnectUser()
		model = new DefaultListModel<String>();
		liste = new JList<String>(model);

		// "Open Chat" Button, unclickable until a connected User is selected
		openChat = new JButton("Chat!");
		openChat.setEnabled(false);

		panel = new JPanel(new GridLayout(2, 1));

		panel.add(liste, BorderLayout.NORTH);
		panel.add(openChat, BorderLayout.SOUTH);

		// Adding actionListeners
		liste.addListSelectionListener(this);
		openChat.addActionListener(this);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ChatSystem v9000 [" + currentUser.pseudo + "] ");
		add(panel, BorderLayout.NORTH);
		setSize(300, 600);
		setVisible(true);
	}

	/**
	 * Adds a newly-connected user
	 * 
	 * @param user User type representing the connected user
	 */
	public void addConnectedUser(User user) {
		model.addElement(user.pseudo);
		connectedUsers.add(user);
	}

	/**
	 * Removes a disconnected user
	 * 
	 * @param user User type representing the disconnected user
	 */
	public void removeConnectedUser(User user) {
		model.removeElement(user.pseudo);
		connectedUsers.remove(user);
	}
	
	/**
	 * Renames a connected user
	 * 
	 * @param user User type representing the user that renamed
	 */
	public void setConnectedUser(User user) {
		int index = 0;
		for (User u : connectedUsers) {
			if (u == user) {
				break;
			}
			index ++;
		}
		model.set(index, user.pseudo);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		openChat.setText("Chat!");
		openChat.setEnabled(true);
	}

	@Override
    public void actionPerformed(ActionEvent ae){
		
		User distantUser = connectedUsers.get(liste.getSelectedIndex());;
        openChat.setText("Ouverture fenetre de chat...");
        openChat.setEnabled(false);
        
        SessionEvent evt = new SessionEvent(distantUser);
        
        try {
			eventQueue.addEventToQueue(evt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }  	 
}
