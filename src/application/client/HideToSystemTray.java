package application.client;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

import utils.EventQueue;

/**
 *
 * @author Mohammad Faisal ermohammadfaisal.blogspot.com
 *         facebook.com/m.faisal6621
 *
 */

public class HideToSystemTray {
	
	public static void configureSystemTray(JFrame frame, EventQueue queue, boolean disconnectAction) {
		Image image = null;
		try {
			image = ImageIO.read( ClassLoader.getSystemResource("image/icon.png"));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		System.out.println("creating instance");
		try {
			System.out.println("setting look and feel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Unable to set LookAndFeel");
		}
		if (SystemTray.isSupported()) {
			System.out.println("system tray supported");
			SystemTray tray = SystemTray.getSystemTray();
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(disconnectAction) {
						try {
							queue.addEventToQueue(new DisconnectEvent());
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} else {
						System.exit(0);
					}
				}
			};
			PopupMenu popup = new PopupMenu();
			MenuItem item = new MenuItem("Open");
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.setVisible(true);
					frame.setExtendedState(JFrame.NORMAL);
				}
			});
			popup.add(item);
			item = new MenuItem(disconnectAction ? "Disconnect" : "Exit");
			item.addActionListener(exitListener);
			popup.add(item);
			TrayIcon trayIcon = new TrayIcon(image, "Chat System", popup);
			trayIcon.setImageAutoSize(true);
			frame.addWindowStateListener(new WindowStateListener() {
				public void windowStateChanged(WindowEvent e) {
					if (e.getNewState() == JFrame.ICONIFIED) {
						try {
							tray.add(trayIcon);
							frame.setVisible(false);
							System.out.println("added to SystemTray");
						} catch (AWTException ex) {
							System.out.println("unable to add to tray");
						}
					}
					if (e.getNewState() == 7) {
						try {
							tray.add(trayIcon);
							frame.setVisible(false);
							System.out.println("added to SystemTray");
						} catch (AWTException ex) {
							System.out.println("unable to add to system tray");
						}
					}
					if (e.getNewState() == JFrame.MAXIMIZED_BOTH) {
						tray.remove(trayIcon);
						frame.setVisible(true);
						System.out.println("Tray icon removed");
					}
					if (e.getNewState() == JFrame.NORMAL) {
						tray.remove(trayIcon);
						frame.setVisible(true);
						System.out.println("Tray icon removed");
					}
				}
			});
		} else {
			System.out.println("system tray not supported");
		}
	}
}