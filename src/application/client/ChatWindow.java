package application.client;

import java.awt.event.*;
import java.io.IOException;
import java.awt.*;
import javax.swing.*;

public class ChatWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final EventQueue eventQueue;
    private final String currentUser;
    private final String distantUser;

    static JPanel panel;
    static JList liste;
    static DefaultListModel model;
    static JButton openChat;
    static JLabel description;

    ChatWindow(String currentUser, String distantUser EventQueue eventQueue){

        this.currentUser = currentUser;
        this.distantUser = distantUser;
        this.eventQueue = eventQueue;

        //REGARDER MAINGUI (martin)!!
    }
}
