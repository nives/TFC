import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.border.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;


public class TFC extends JFrame {

	private static Grafics g;
	private Entorn env;
	

	public TFC( ) {
	
		g = new Grafics();
		env = new Entorn( g );
	
		add( g );

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(645, 508);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);

	}
	
	public static void main(String args[]){
	
		TFC j = new TFC();
		while(true) {
			j.env.moveBall();
			j.env.goal();
			j.env.sendEnv();
			g.repaint();
			try {
				Thread.sleep(75);
			} catch(InterruptedException ie) {
			}
		}

	}
}