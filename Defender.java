import java.util.concurrent.*;

public class Defender extends Agent implements Runnable{
	public static final double MOVE = 5.0;
	Pos[] myTeam, otherTeam;
	int nMyTeam, nOtherTeam;
	int myPos;
	Pos defence;
	Pilota ball;
	
	//Constructor de classe.
	public Defender(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team, int pos, Pilota ball) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myPos = pos;
		defence = new Pos();
		this.ball = ball;
		if( team == 2 ) {
			myTeam = teamB;
			otherTeam = teamA;
			nMyTeam = nteamB;
			nOtherTeam = nteamA;
			defence.x = limitOest - 10;
			defence.y = limitSud/2;
		}
		else {
			myTeam = teamA;
			otherTeam = teamB;
			nMyTeam = nteamA;
			nOtherTeam = nteamB;
			defence.x = limitEst + 10;
			defence.y = limitSud/2;
		}
	} //Fi constructor.
	
	//Funcio que mou el jugador cap a les coordenades que s'ha de moure per poder cobrir al rival.
	private void moveToCover( double x, double y ) {
		double nouX, nouY, nouTX, nouTY;
		if( x>0 && y>0 ) {
			double distTarg = Math.sqrt( Math.pow( defence.x - x,2 ) + Math.pow( defence.y - y,2 ) );
			nouTX = x + (defence.x - x)/distTarg*50.0;
			nouTY = y + (defence.y - y)/distTarg*50.0;
			
			double distmove = Math.sqrt( Math.pow( nouTX - my_x,2 ) + Math.pow( nouTY - my_y,2 ) );
			nouX = my_x + (nouTX - my_x)/distmove*MOVE;
			nouY = my_y + (nouTY - my_y)/distmove*MOVE;
			
			Pos p = pathBlocked( (int)nouX, (int)nouY );
			
			my_x = p.x;
			my_y = p.y;
		}
	
	} //Fi moveToCover.
	
	//Funcio que comprova si la posicio que se li passa per parametres esta obstruïda per algun altre jugador i proposa una possible solució per esquivar a aquest.
	private Pos pathBlocked( int x, int y ) {
		for( int i=0; i<nMyTeam; i++ ) {
			//Si el jugador que es comprova no soc jo, i la distancia entre la posicio proposada i aquest jugador es inferior a la suma dels radis dels dos.
			if( i!=myPos && Math.sqrt( Math.pow(myTeam[i].x-x, 2) + Math.pow(myTeam[i].y-y, 2) ) < 40 ) {
				//System.out.println("Xoco amb myTeam["+i+"]");
				double eixX = x - myTeam[i].x;
				double eixY = y - myTeam[i].y;
				double mod = Math.sqrt( Math.pow(eixX, 2) + Math.pow(eixY, 2) );
				eixX = eixX/mod*40.0; 
				eixY = eixY/mod*40.0;
				double preX = myTeam[i].x + eixX;
				double preY = myTeam[i].y + eixY;
				eixX = preX - my_x;
				eixY = preY - my_y;
				mod = Math.sqrt( Math.pow(eixX, 2) + Math.pow(eixY, 2) );
				eixX = eixX/mod*MOVE; 
				eixY = eixY/mod*MOVE;
				x = (int)(my_x + eixX);
				y = (int)(my_y + eixY);
				if( x<limitOest+20.0 || x>limitEst-20.0 || y<limitNord+20.0 || y>limitSud+20.0 ) {
					x = (int)(my_x - eixX);
					y = (int)(my_y - eixY);
				}
			}
		}
		for( int i=0; i<nOtherTeam; i++ ) {
			//Si el jugador que es comprova no soc jo, i la distancia entre la posicio proposada i aquest jugador es inferior a la suma dels radis dels dos.
			if( Math.sqrt( Math.pow(otherTeam[i].x-x, 2) + Math.pow(otherTeam[i].y-y, 2) ) <= 40 ) {
				//System.out.println("Xoco amb otherTeam["+i+"]");
				double eixX = x - otherTeam[i].x;
				double eixY = y - otherTeam[i].y;
				double mod = Math.sqrt( Math.pow(eixX, 2) + Math.pow(eixY, 2) );
				eixX = eixX/mod*40.0; 
				eixY = eixY/mod*40.0;
				double preX = otherTeam[i].x + eixX;
				double preY = otherTeam[i].y + eixY;
				eixX = preX - my_x;
				eixY = preY - my_y;
				mod = Math.sqrt( Math.pow(eixX, 2) + Math.pow(eixY, 2) );
				eixX = eixX/mod*MOVE; 
				eixY = eixY/mod*MOVE;
				x = (int)(my_x + eixX);
				y = (int)(my_y + eixY);
				if( x<limitOest+20.0 || x>limitEst-20.0 || y<limitNord+20.0 || y>limitSud+20.0 ) {
					x = (int)(my_x - eixX);
					y = (int)(my_y - eixY);
				}
			}
		}
		if( x<limitOest+20.0 || x>limitEst-20.0 || y<limitNord+20.0 || y>limitSud+20.0 ) {
			x = (int)my_x;
			y = (int)my_y;
		}
		Pos p = new Pos();
		p.x = x;
		p.y = y;
		return p;
	} //Fi pathBlocked.
	
	private Pos selectCover() {
		Pos p = new Pos();
		double x, y;
		p.x=-1; 
		p.y=-1;
		double covers[][] = new double[nOtherTeam][nMyTeam];
		int priCover[] = new int[nOtherTeam];
		for( int i=1; i<nOtherTeam; i++ ) {
			double distTarg = Math.sqrt( Math.pow( defence.x - otherTeam[i].x,2 ) + Math.pow( defence.y - otherTeam[i].y,2 ) );
			x = otherTeam[i].x + (defence.x - otherTeam[i].x)/distTarg*50.0;
			y = otherTeam[i].y + (defence.y - otherTeam[i].y)/distTarg*50.0;
			for( int j=0; j<nMyTeam; j++ ) {
				covers[i][j] = Math.sqrt( Math.pow( x - myTeam[j].x,2 ) + Math.pow( y - myTeam[j].y,2 ) );
			}
		}
		for( int i=1; i<nOtherTeam; i++ ) {
			double mydist = covers[i][myPos];
			int num = 0;
			for( int j=0; j<nMyTeam; j++ ) {
				if( covers[i][j] < mydist ) num++;
			}
			priCover[i] = num;
		}
		int min = 10;
		int pos = -1;
		for( int i=1; i<nOtherTeam; i++ ) {
			if( priCover[i] < min ) {
				min = priCover[i];
				pos = i;
			}
		}
		p.x = otherTeam[pos].x;
		p.y = otherTeam[pos].y;
		return p;
	}
	
	private boolean hasTheBall( double x, double y ) {
		double distTarg = Math.sqrt( Math.pow( pilota.x - x,2 ) + Math.pow( pilota.y - y,2 ) );
		if( distTarg < 16.0 ) {
			return true;
		}
		return false;
	}
	
	//Funcio que comprova si la pilota ha entrar a una de les porteries.
	private boolean goal() {
		if( (pilota.x < limitOest-9 || pilota.x > limitEst+9) && (pilota.y < limitSud/2+32 && pilota.y > limitNord/2-32) ) {
			return true;
		}
		else return false;	
	} //Fi goal.
	
	/*Inici funcio run.
	 *Aquesta funcio executa el comportament de l'agent Defender dins de l'entorn assignat en format de thread.
	 */
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			//System.out.println("My X: "+(int)my_x+" My Y: "+(int)my_y+"    Pilota("+pilota.x+","+pilota.y+")");
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
			Pos p = selectCover();
			moveToCover( p.x, p.y );
			
			//Si gol...
			fi = goal();
			
		}
	} //Fi funcio run.
	
}