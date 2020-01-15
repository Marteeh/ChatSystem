package application.client;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import application.User;
import utils.EventQueue;

public class ChatWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private EventQueue eventQueue;
	private final User currentUser;
	private final User distantUser;

	JPanel mainPanel, southPanel;
	JTextField inputTextField;
	JButton sendButton;
	JTextArea chatTextArea;

	private javax.swing.JTextArea chatInput;
	private javax.swing.JScrollPane chatScrollPane;
	private javax.swing.JLabel chattingWith;
	private javax.swing.JPanel messageContainer;
	private javax.swing.JPanel topContainer;

	public ChatWindow(final User currentUser, final User distantUser, EventQueue eventQueue) {

		this.currentUser = currentUser;
		this.distantUser = distantUser;
		this.eventQueue = eventQueue;

		initComponents();
	}

	private void initComponents() {

		topContainer = new javax.swing.JPanel();
		chattingWith = new javax.swing.JLabel();
		messageContainer = new javax.swing.JPanel();
		inputTextField = new javax.swing.JTextField();
		chatInput = new javax.swing.JTextArea();
		sendButton = new javax.swing.JButton();
		chatScrollPane = new javax.swing.JScrollPane();
		chatTextArea = new javax.swing.JTextArea();

		setTitle("ChatSystem Turbo 9000 - [" + currentUser.pseudo + "]");
		setBackground(new java.awt.Color(0, 0, 0));

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(final java.awt.event.WindowEvent evt) {
				windowClosingConfirmation(evt);
			}
		});

		topContainer.setBackground(new java.awt.Color(153, 153, 153));
		topContainer.setForeground(new java.awt.Color(255, 255, 255));

		chattingWith.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
		chattingWith.setForeground(new java.awt.Color(255, 255, 255));
		chattingWith.setText("Chatting with " + distantUser.pseudo);

		javax.swing.GroupLayout topContainerLayout = new javax.swing.GroupLayout(topContainer);
		topContainer.setLayout(topContainerLayout);
		topContainerLayout.setHorizontalGroup(
				topContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						topContainerLayout.createSequentialGroup().addContainerGap().addComponent(chattingWith)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		topContainerLayout
				.setVerticalGroup(topContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								topContainerLayout.createSequentialGroup()
										.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(chattingWith).addContainerGap()));

		messageContainer.setBackground(new java.awt.Color(255, 255, 255));

		inputTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					sendButtonActionPerformed(null);
				}
			}
		});

		chatInput.setColumns(20);
		chatInput.setRows(5);

		sendButton.setBackground(new java.awt.Color(204, 204, 255));
		sendButton.setForeground(new java.awt.Color(255, 255, 255));
		sendButton.setText("SEND");
		sendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout messageContainerLayout = new javax.swing.GroupLayout(messageContainer);
		messageContainer.setLayout(messageContainerLayout);
		messageContainerLayout.setHorizontalGroup(messageContainerLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(messageContainerLayout.createSequentialGroup().addContainerGap()
						.addComponent(inputTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(sendButton)
						.addContainerGap()));
		messageContainerLayout.setVerticalGroup(messageContainerLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, messageContainerLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(messageContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(messageContainerLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
										.addComponent(inputTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 44,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(106, 106, 106)));

		chatTextArea.setBackground(new java.awt.Color(238, 238, 238));
		chatTextArea.setColumns(20);
		chatTextArea.setRows(5);
		chatTextArea.setEditable(false);
		chatScrollPane.setViewportView(chatTextArea);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(topContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)
				.addComponent(messageContainer, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(chatScrollPane));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(topContainer, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, 0)
						.addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
						.addGap(0, 0, 0).addComponent(messageContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 64,
								javax.swing.GroupLayout.PREFERRED_SIZE)));

		pack();
		setVisible(true);
	}

	public void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public void addMessage(long timeStamp, String from, String content) {
		Date dateRecu = new Date(timeStamp);
		final DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		final String dateEnvoi = shortDateFormat.format(dateRecu);
		chatTextArea.append(dateEnvoi + "<" + from + ">: " + content + "\n");
	}

	public void sendButtonActionPerformed(final ActionEvent e) {

		// adds the message to the eventqueue
		MessageEvent evt = new MessageEvent(currentUser, distantUser, inputTextField.getText());
		try {
			eventQueue.addEventToQueue(evt);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		inputTextField.setText("");
		inputTextField.requestFocusInWindow();
	}

	public void windowClosingConfirmation(java.awt.event.WindowEvent evt) {
		boolean prompt = false;
		if (prompt) {
			String ObjButtons[] = { "Yes", "No" };

			int PromptResult = javax.swing.JOptionPane.showOptionDialog(null,
					"Are you sure you want to exit?\nThis will shut down the TCP session.", "",
					javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, ObjButtons,
					ObjButtons[1]);

			if (PromptResult == javax.swing.JOptionPane.YES_OPTION) {
				try {
					eventQueue.addEventToQueue(new SessionCloseEvent(distantUser));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				eventQueue.addEventToQueue(new SessionCloseEvent(distantUser));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
