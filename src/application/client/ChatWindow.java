package application.client;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

public class ChatWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    //private final EventQueue eventQueue;
    private final String currentUser;
    private final String distantUser;

    static JPanel mainPanel, southPanel;
    static JTextField messageBox;
    static JButton sendMessage;
    static JTextArea chatBox;

    ChatWindow(String currentUser, String distantUser/*, EventQueue eventQueue */){

        this.currentUser = currentUser;
        this.distantUser = distantUser;
        //this.eventQueue = eventQueue;

        //REGARDER MAINGUI (martin)!!
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

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("CS v9000 - Chatting with " + distantUser);
        add(mainPanel);
        setSize(470, 300);
        setVisible(true);  
    }

    public void actionPerformed(ActionEvent e) {
        if (messageBox.getText().length() < 1) {
            //TODO: Start TCP Session
        } else {
            chatBox.append( "<" + currentUser + ">:  " + messageBox.getText() + "\n");
            messageBox.setText("");
            //TODO: Envoi messageEvent
        }
        messageBox.requestFocusInWindow();
    }

    public static void main(String args[]){
        new ChatWindow("Marton", "Flo");
    }

}
