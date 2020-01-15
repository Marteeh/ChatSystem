package application.client;

import utils.EventQueue;

import java.awt.event.*;
import javax.swing.*;

public class LoginWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final EventQueue eventQueue;
	boolean extUsrChecked;
	boolean extUsrEnabled;
	boolean isExternal = false;

	JPanel panel;
	JLabel usrLabel, mailLabel, pswrdLabel, externalUser, loginLabel;
	JTextField usernameInput;
	JCheckBox extUserCheck;
	JButton loginButton, cancel;
	JLabel messageLog;

	public LoginWindow(EventQueue eventQueue, boolean extUsrChecked, boolean extUsrEnabled) {

		this.eventQueue = eventQueue;
		this.extUsrChecked = extUsrChecked;
		this.extUsrEnabled = extUsrEnabled;
		this.isExternal = extUsrChecked;
		
		extUserCheck = new JCheckBox();
		extUserCheck.setEnabled(extUsrEnabled);
		extUserCheck.setSelected(extUsrChecked);
		
		initComponents();
		HideToSystemTray.configureSystemTray(this, eventQueue, false);
	}
		
		private void initComponents() {

			loginLabel = new javax.swing.JLabel();
			usernameInput = new javax.swing.JTextField();
			//extUserCheck = new javax.swing.JCheckBox();
			messageLog = new javax.swing.JLabel();
			loginButton = new javax.swing.JButton();

			setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			setTitle("ChatSystem Turbo 9000 - Welcome !");

			loginLabel.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
			loginLabel.setText("PLEASE LOG IN");

			usernameInput.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
			usernameInput.setText("Username");
			usernameInput.setToolTipText("Visible username by other online users");
			usernameInput.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent evt) {
					if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
						loginButtonActionPerformed(null);
					}
				}
			});

			extUserCheck.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
			extUserCheck.setText("I'm an external user");
			extUserCheck.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent evt) {
					extUserCheckItemStateChanged(evt);
				}
			});
			messageLog.setFont(new java.awt.Font("Ubuntu Light", 2, 14)); // NOI18N
			messageLog.setForeground(new java.awt.Color(255, 102, 0));
			messageLog.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			messageLog.setText("");

			loginButton.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
			loginButton.setText("LOGIN !");
			loginButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			loginButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					loginButtonActionPerformed(evt);
				}
			});

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
							layout.createSequentialGroup().addContainerGap(80, Short.MAX_VALUE)
									.addComponent(usernameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 227,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(80, 80, 80))
					.addGroup(layout.createSequentialGroup().addGroup(layout
							.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup().addGap(147, 147, 147).addComponent(loginLabel))
							.addGroup(layout.createSequentialGroup().addGap(156, 156, 156).addComponent(loginButton))
							.addGroup(layout.createSequentialGroup().addGap(129, 129, 129).addComponent(extUserCheck))
							.addGroup(layout.createSequentialGroup().addGap(117, 117, 117).addComponent(messageLog)))
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
			layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addGroup(layout.createSequentialGroup().addGap(35, 35, 35)
							.addComponent(loginLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
									javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18)
							.addComponent(usernameInput, javax.swing.GroupLayout.PREFERRED_SIZE, 39,
									javax.swing.GroupLayout.PREFERRED_SIZE)
							.addGap(18, 18, 18).addComponent(extUserCheck).addGap(18, 18, 18)
							.addComponent(loginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37,
									javax.swing.GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(messageLog)
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

			loginLabel.getAccessibleContext().setAccessibleDescription("");
			loginLabel.getAccessibleContext().setAccessibleParent(null);
			extUserCheck.getAccessibleContext().setAccessibleParent(null);
			loginButton.getAccessibleContext().setAccessibleParent(null);

			pack();
			setResizable(false);
			setVisible(true);
		}

	public void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	// Listener for "external user" button
	public void extUserCheckItemStateChanged(ItemEvent e) {

		AbstractButton absB = (AbstractButton) e.getSource();
		ButtonModel bMod = absB.getModel();
		boolean isPressed = bMod.isSelected();

		isExternal = isPressed;
	}

	// Listener for "login" button
	public void loginButtonActionPerformed(ActionEvent ae) {

		String usrnm = usernameInput.getText();
		LoginEvent evt = new LoginEvent(usrnm, isExternal);
		loginButton.setEnabled(false);
		loginButton.setText("Checking username...");

		try {
			eventQueue.addEventToQueue(evt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void enableLoginButton() {
		loginButton.setEnabled(true);
		loginButton.setText("LOGIN");
	}

	public void disableLoginButton() {
		loginButton.setEnabled(false);
	}

	public void disableExternalUserCheckbox() {
		extUserCheck.setEnabled(false);
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
			java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LoginWindow(null, false, false).setVisible(true);
			}
		});
	}

}
