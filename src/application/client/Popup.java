package application.client;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Popup extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public Popup() {
		System.out.println("Popup");
		this.setUndecorated(true);
		this.setSize(600, 600);
		this.setLocationRelativeTo(null);
		Random r = new Random();
		float x = r.nextFloat();
		float y = r.nextFloat();
		float z = r.nextFloat();
		float ratio = 1 / Math.max(Math.max(x, y), z);
		x *= ratio;
		y *= ratio;
		z *= ratio;
		this.setBackground(new Color(x, y, z));
		this.setVisible(true);
		JFrame frame = this;
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				try {
					Thread.sleep(100);
					frame.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args) {
		new Popup();
	}

}
