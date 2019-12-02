package application.client;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class MainWindow extends JFrame implements ListSelectionListener{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    private final EventQueue eventQueue;
    private final String currentUser;

    static JPanel panel;
    static JList liste;
    static DefaultListModel model;
    static JLabel description;

    MainWindow(String currentUser){

        this.eventQueue = eventQueue;
        this.currentUser = currentUser;

        panel = new JPanel(new GridLayout(1,1)); 
        model = new DefaultListModel();
        liste = new JList(model); 

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel, BorderLayout.CENTER);
        setTitle("ChatSystem v9000 [" + currentUser + "] ");
        setSize(300, 600);
        setVisible(true);

    }

    public static void AddConnectedUser(String user){
        //addElement, removeElement, set
        model.addElement(user);
    }

    
//pour tester
    public static void main(String[] args){
        new MainWindow("poupidou");
    }
}
