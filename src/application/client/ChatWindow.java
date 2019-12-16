package application.client;

import application.User;
import utils.EventQueue;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;



public class ChatWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

	private EventQueue eventQueue;
    private final User currentUser;
    private final User distantUser;

    static JPanel mainPanel, southPanel;
    static JTextField messageBox;
    static JButton sendMessage;
    static JTextArea chatBox;

    ChatWindow(final User currentUser, final User distantUser/* , EventQueue eventQueue */) {

        this.currentUser = currentUser;
        this.distantUser = distantUser;
        // this.eventQueue = eventQueue;

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

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

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                System.out.println("Boom booooooom");
                
                String ObjButtons[] = {"Yes","No"};

                int PromptResult = JOptionPane.showOptionDialog(null, 
                    "Are you sure you want to exit?\nThis will shut down the TCP session.", "", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, 
                    ObjButtons,ObjButtons[1]);

                if(PromptResult==0) {
                    System.exit(0);
                    //TCP Close event          
                }
            }
        });
        setTitle("CS v9000 - Chatting with " + distantUser.pseudo);
        add(mainPanel);
        setSize(900, 500);
        setVisible(true);
    }

    public void showErrorMessage(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
    
    public static void addMessageReceived(long timeStamp, String from, String content) {
    	Date dateRecu = new Date(timeStamp*1000);
        chatBox.append(dateRecu + "<" + from + ">: " + content + "\n");
    }

    public void actionPerformed(final ActionEvent e) {

        final Date date = new Date();

        final DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        final String dateEnvoi = shortDateFormat.format(date);

        //adds the message to the chatbox
        chatBox.append(dateEnvoi + "<" + currentUser.pseudo + ">:  " + messageBox.getText() + "\n");
        
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

    public static void main(final String args[]) {
        final User Marton = new User(0, "Marton", false, "192.168.0.1");
        final User Flo = new User(1, "Flo", false, "192.168.0.2");
        new ChatWindow(Marton, Flo);
    }

}
