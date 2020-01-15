package application.client;

import java.awt.event.WindowAdapter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import application.User;
import utils.EventQueue;

public class MainWindow extends javax.swing.JFrame {

	private EventQueue eventQueue;
	private final User currentUser;

	private static final long serialVersionUID = 1L;

	public MainWindow(final User currentUser, EventQueue eventQueue) {

		this.currentUser = currentUser;
		this.eventQueue = eventQueue;

		initComponents();

	}

	private void initComponents() {

		mainPanel = new javax.swing.JPanel();
		model = new javax.swing.DefaultListModel<String>();
		connectedUsersList = new javax.swing.JList<String>(model);
		chatButton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		menuOptions = new javax.swing.JMenu();
		renameClick = new javax.swing.JMenuItem();
		disconnectClick = new javax.swing.JMenuItem();
		renamePopup = new javax.swing.JPanel(new java.awt.GridLayout(2, 1));
		renameField = new javax.swing.JTextField(14);

		chatButton.setText("Chat !");
		chatButton.setEnabled(false);
		chatButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chatButtonActionPerformed(evt);
			}
		});
		
		setTitle("ChatSystem Turbo 9000 - [" + currentUser.pseudo +"]");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(final java.awt.event.WindowEvent evt) {
        		disconnectClickActionPerformed(null);
        	}
		});
		
		javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout
				.setHorizontalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(connectedUsersList, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(chatButton, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE));
		mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(mainPanelLayout.createSequentialGroup()
						.addComponent(connectedUsersList, javax.swing.GroupLayout.DEFAULT_SIZE, 375,
								Short.MAX_VALUE)
						.addComponent(chatButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40,
								javax.swing.GroupLayout.PREFERRED_SIZE)));

		menuOptions.setText("Options");
		
		//POUR METTRE AU MILIEU MAIS CA MARCHE PAS ENCORE
		DefaultListCellRenderer centerRenderer = new DefaultListCellRenderer();
		centerRenderer.setHorizontalTextPosition(javax.swing.JLabel.CENTER);
		connectedUsersList.setCellRenderer(centerRenderer);
		connectedUsersList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				connectedUsersValueChanged(e);
			}
		});

		renameClick.setText("Rename");
		renameClick.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				renameClickActionPerformed(evt);
			}
		});
		menuOptions.add(renameClick);

		disconnectClick.setText("Disconnect");
		disconnectClick.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				disconnectClickActionPerformed(evt);
			}
		});
		menuOptions.add(disconnectClick);

		menuBar.add(menuOptions);

		setJMenuBar(menuBar);

		// Rename popup
		renamePopup.add(new javax.swing.JLabel("New Username : "));
		renamePopup.add(renameField);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
		setVisible(true);
	}

	private void renameClickActionPerformed(java.awt.event.ActionEvent evt) {
		
		String popupButtons[] = { "Confirm Rename", "Cancel" };

		int changed = JOptionPane.showOptionDialog(mainPanel, renamePopup, "Change Username",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, popupButtons, null);

		if (changed == JOptionPane.YES_OPTION) {
			String newUsername = renameField.getText();

			if (!newUsername.equals(currentUser.pseudo)) {
				RenamePseudoEvent renameEv = new RenamePseudoEvent(newUsername);

				try {
					eventQueue.addEventToQueue(renameEv);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} else {
				showMessage("This is your current username, please select a new one.", "Error");
			}
		}
	}

	private void disconnectClickActionPerformed(java.awt.event.ActionEvent evt) {
		DisconnectEvent discoEv = new DisconnectEvent();
		
		try {
			eventQueue.addEventToQueue(discoEv);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void chatButtonActionPerformed(java.awt.event.ActionEvent evt) {
		User distantUser = connectedUsers.get(connectedUsersList.getSelectedIndex());
        
        SessionEvent sessionEv = new SessionEvent(distantUser);
        
        try {
			eventQueue.addEventToQueue(sessionEv);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			index++;
		}
		model.set(index, user.pseudo);
	}
	
	/**
	 * Show a message in pop-up window
	 * 
	 * @param message The message to be shown
	 * @param title	The title of the new frame
	 */
	public void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void connectedUsersValueChanged(ListSelectionEvent e) {
		try {
			User u = connectedUsersList.getSelectedIndex() >= 0 ? connectedUsers.get(connectedUsersList.getSelectedIndex()) : null;
			eventQueue.addEventToQueue(new UserSelectionChangedEvent(u));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Changes the state of the "Chat!" Button
	 * 
	 * @param enabled Enabled if true, disabled if false
	 */
	public void setOpenChatEnabled(boolean enabled) {
        chatButton.setEnabled(enabled);
	}
	
	public void unselectUser() {
		connectedUsersList.clearSelection();
	}

	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				User marton = new User(0, "marton", false, "localhost");
				new MainWindow(marton, null).setVisible(true);
			}
		});
	}

	// Variables declaration
	private javax.swing.JButton chatButton;
	private javax.swing.JList<String> connectedUsersList;
	private static javax.swing.DefaultListModel<String> model;
	private javax.swing.JMenuItem disconnectClick;
	private javax.swing.JPanel mainPanel;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenu menuOptions;
	private javax.swing.JMenuItem renameClick;
	private javax.swing.JPanel renamePopup;
	private javax.swing.JTextField renameField;
	private static java.util.List<User> connectedUsers = new java.util.ArrayList<User>();
}