package application.client;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame implements ActionListener, ListSelectionListener{

	private static final long serialVersionUID = 1L;
	
	private EventQueue eventQueue;
	private String currentUser;

    static JPanel panel;
    static JList<String> liste;
    static DefaultListModel<String> model;
    static JButton openChat;
    static JLabel description;

	

    /**
     * MainWindow launcher
     * @param currentUser Name of the current user, used for the window title.
     * @param eventQueue the queue used for receiving and sending events to the controler.
     */
    MainWindow(String currentUser, EventQueue eventQueue){

        this.eventQueue = eventQueue;
        this.currentUser = currentUser;
        
        //Connected User list, updates with AddConnectedUser(), RemoveConnectedUser(), or setConnectUser()
        model = new DefaultListModel<String>();
        liste = new JList<String>(model);
        
        //"Open Chat" Button, unclickable until a connected User is selected
        openChat = new JButton("Chat!");
        openChat.setEnabled(false);

        panel = new JPanel(new GridLayout(2,1)); 
        
        panel.add(liste, BorderLayout.NORTH);
        panel.add(openChat, BorderLayout.SOUTH);

        //Adding actionListeners
        liste.addListSelectionListener(this);
        openChat.addActionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("ChatSystem v9000 [" + currentUser + "] ");
        add(panel, BorderLayout.NORTH);
        setSize(300, 600);
        setVisible(true);
    }

    /**
     * Adds a newly-connected user
     * @param user the name of the connected user
     */
    public static void addConnectedUser(String user){
        //addElement, removeElement, set
        model.addElement(user);
    }

    /**
     * Removes a disconnected user
     * @param user the name of the disconnected user
     */
    public static void removeConnectedUser(String user){
        model.removeElement(user);
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e){
        openChat.setText("Chat!");
        openChat.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        openChat.setText("Ouverture fenetre de chat...");
        openChat.setEnabled(false);
        //MessageEvent evt =;
    }

    
//pour tester
/*
    public static void main(String[] args){
        new MainWindow("poupidou");
        addConnectedUser("Saucier");
        addConnectedUser("SavaneBrossard");
        addConnectedUser("Marton");
        try{ Thread.sleep(5000);} catch (InterruptedException e){ e.printStackTrace();}
        addConnectedUser("Flo");
    }
*/
}

