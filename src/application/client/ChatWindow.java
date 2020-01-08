package application.client;

import application.User;
import utils.EventQueue;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;



public class ChatWindow extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

	private EventQueue eventQueue;
    private final User currentUser;
    private final User distantUser;

    static JPanel mainPanel, southPanel;
    static JTextField messageBox;
    static JButton sendMessage;
    static JTextArea chatBox;

    public ChatWindow(final User currentUser, final User distantUser , EventQueue eventQueue) {

        this.currentUser = currentUser;
        this.distantUser = distantUser;
        this.eventQueue = eventQueue;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();
        messageBox.addKeyListener(this);
        
        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(this);

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setLineWrap(true);

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        final GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        final GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                
                boolean prompt = false;
                if(prompt) {                    
                    String ObjButtons[] = {"Yes","No"};

                    int PromptResult = JOptionPane.showOptionDialog(null, 
                        "Are you sure you want to exit?\nThis will shut down the TCP session.", "", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, 
                        ObjButtons,ObjButtons[1]);

                    if(PromptResult==JOptionPane.YES_OPTION) {
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
        });
        setTitle("CS v9000 - Chatting with " + distantUser.pseudo);
        add(mainPanel);
        setSize(900, 500);
        setVisible(true);
    }

    public void showMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
    
    public void addMessage(long timeStamp, String from, String content) {
    	Date dateRecu = new Date(timeStamp);
    	final DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final String dateEnvoi = shortDateFormat.format(dateRecu);
        chatBox.append(dateEnvoi + "<" + from + ">: " + content + "\n");
    }

    public void actionPerformed(final ActionEvent e) {
        
        //adds the message to the eventqueue
        MessageEvent evt = new MessageEvent(currentUser, distantUser, messageBox.getText());
        try {
            eventQueue.addEventToQueue(evt);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        messageBox.setText("");
        messageBox.requestFocusInWindow();
    }
    
    //"ENTER" Key listener
    public void keyPressed(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
    		MessageEvent evt = new MessageEvent(currentUser, distantUser, messageBox.getText());
            try {
                eventQueue.addEventToQueue(evt);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            messageBox.setText("");
            messageBox.requestFocusInWindow();
    	}
    }

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
	
//	public static void main(String[] args) {
//		User Tinmar = new User(0, "martin", false, "localhost");
//		User Floflo = new User(1, "flo", false, "localhost");
//		EventQueue evtq = new EventQueue(null);
//		new ChatWindow (Floflo, Tinmar, evtq);
//	}

}
