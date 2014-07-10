import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.Shape.*;
import javax.swing.border.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;


public class Grafics extends JPanel {

	public static final int JUG_X = 20;			//Offset de les fitxes de jugadors perque quedin centrats en les Xs.
	public static final int JUG_Y = 20;			//Offset de les fitxes de jugadors perque quedin centrats en les Ys.
	public static final int JUG_TAM = 40;		//Tamany de les fitxes dels jugadors.
	public static final int BALL = 9;			//Offset de les fitxes de la pilota perque quedi centrada.
	public static final int GOL_ESQ = 28;
	public static final int GOL_DRET = 612;
	public static final int MITAT_X = 320;
	public static final int MITAT_Y = 242;
	public static final int PAL_NORD = 209;
	public static final int PAL_SUD = 274;

	public class Imatges {
		public int x;
		public int y;
		BufferedImage _img;
	}
	
	public JFrame finestra;
	
	int teamA, teamB;
	boolean pilota;
	
	ImageIcon fondo;
	BufferedImage img1, img2;
	
	Imatges[] _teamA;
	Imatges[] _teamB;
	Imatges _pilotaxy;
	ImageIcon _pilota;
	
	public Grafics() {
	}
	
	public void init_Grafics( boolean pilota, int teamA, int teamB ) {
	
		this.pilota = pilota;
		this.teamA = teamA;
		this.teamB = teamB;
		
		_teamA = new Imatges[teamA];
		_teamB = new Imatges[teamB];
		
		carregaimatges();
		repaint();
		
	}
	
	private void carregaimatges(){	
		
		try {
			fondo = new ImageIcon("img/futbol.jpg");							//Carreguem fondo de pantalla
			img1 = ImageIO.read(new File("./img/fitxes-01-v2.png"));			//Carreguem imatge base de teamA
			img2 = ImageIO.read(new File("./img/fitxes-02-v2.png"));			//Carreguem imatge base de teamB
			
			
			if( pilota ) {
				_pilota = new ImageIcon("img/Pilota.png");		//Si hi ha pilota, carrega la imatge de la pilota.
				_pilotaxy = new Imatges();
			}
					
			for (int i = 0; i < teamA; i++) {
				_teamA[i] = new Imatges();
				_teamA[i]._img = img1.getSubimage( i * JUG_TAM, 0, JUG_TAM, JUG_TAM );
			}
			for (int i = 0; i < teamB; i++) {
				_teamB[i] = new Imatges();
				_teamB[i]._img = img2.getSubimage( i * JUG_TAM, 0, JUG_TAM, JUG_TAM );
			} 
			
		} catch (IOException e) {
		}
		
	}

	public void paint(Graphics g) {
	
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		
		
		g2d.drawImage(fondo.getImage(), 0, 0, this);
		
		Toolkit.getDefaultToolkit().sync();
		
		for (int i=0; i<teamA; i++) {
			g2d.drawImage( _teamA[i]._img, _teamA[i].x - JUG_X, _teamA[i].y - JUG_Y, this);
		}
		for (int i=0; i<teamB; i++) {
			g2d.drawImage( _teamB[i]._img, _teamB[i].x - JUG_X, _teamB[i].y - JUG_Y, this);
		}
		
		if( pilota ) {
			g2d.drawImage( _pilota.getImage(), _pilotaxy.x-BALL, _pilotaxy.y-BALL, this);
		}
		
		g.dispose();
	}

	public void creaIMostraGUI(){ // esto seria el panel principal dentro de este metes los paneles mas pekes
		
		BorderLayout bl = new BorderLayout();
		//bl.setVgap(20); // esto para dejar espacio como un margen

		
		/**********************Panell Principal******************************/
		JPanel pContingut = new JPanel();
		pContingut.setLayout(bl);
		pContingut.setOpaque(true);
		finestra.setContentPane(pContingut); // pone como panel de la ventana el pContingut
		finestra.pack(); // mida se adecua a la finestra segun lo k tengamos dentro
		finestra.setVisible(true); // esto es para k se vea
		
	}
	
	public void getDades( Pilota a_pilota, Agent[] a_teamA, Agent[] a_teamB ) {
	
		if ( pilota ) {
			_pilotaxy.x = a_pilota.getPosX();
			_pilotaxy.y = a_pilota.getPosY();
		}
		for (int i=0; i<teamA; i++) {
			_teamA[i].x = a_teamA[i].getPosX();
			_teamA[i].y = a_teamA[i].getPosY();
		}
		for (int i=0; i<teamB; i++) {
			_teamB[i].x = a_teamB[i].getPosX();
			_teamB[i].y = a_teamB[i].getPosY();
		}
	}
	public void pintar(){
		repaint();
	}

}