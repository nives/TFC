import java.io.*;
import java.util.Random;

public class Porter extends Agent implements Runnable{

	public static final double MOVE = 5.0;
	Pos[] myTeam, otherTeam;
	int nMyTeam, nOtherTeam;
	int myPos;
	Pos target, defence;
	Pilota ball;
	int pass = 0, team;
	boolean defending;
	
	public Porter( int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team, int pos, Pilota ball) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myPos = pos;
		target = new Pos();
		defence = new Pos();
		this.ball = ball;
		defending = false;
		this.team = team;
		if( team == 1 ) {
			myTeam = teamA;
			otherTeam = teamB;
			nMyTeam = nteamA;
			nOtherTeam = nteamB;
			target.x = limitOest;
			target.y = limitSud/2;
			defence.x = limitEst;
			defence.y = limitSud/2;
			my_x = defence.x;
			my_y = defence.y;
		}
		else {
			myTeam = teamB;
			otherTeam = teamA;
			nMyTeam = nteamB;
			nOtherTeam = nteamA;
			target.x = limitEst;
			target.y = limitSud/2;
			defence.x = limitOest;
			defence.y = limitSud/2;
			my_x = defence.x;
			my_y = defence.y;
		}
	}
	
	//Funcio que recoloca la posicio del Porter en funcio de la posicio de la pilota en el camp.
	private void Recolocar() {
		double distBall = Math.sqrt( Math.pow( pilota.x - defence.x, 2) + Math.pow( pilota.y - defence.y, 2) );
		double goX = defence.x + (pilota.x - defence.x) / distBall * 32.0;
		double goY = defence.y + (pilota.y - defence.y) / distBall * 32.0;
		double distGo = Math.sqrt( Math.pow( goX - my_x, 2) + Math.pow( goY - my_y, 2) );
		if( distGo >= MOVE ) {
			my_x = (int)(my_x + (goX - my_x) / distGo * MOVE);
			my_y = (int)(my_y + (goY - my_y) / distGo * MOVE);
		}
		else {
			my_x = goX;
			my_y = goY;
		}
	
	} // Fi Recolocar.

	//Funcio que comprova si la pilota ha entrar a una de les porteries.
	private boolean goal() {
		if( (pilota.x < limitOest-9 || pilota.x > limitEst+9) && (pilota.y < limitSud/2+32 && pilota.y > limitNord/2-32) ) {
			return true;
		}
		else return false;	
	} //Fi goal.
	
	private Pos choosePassTarget() {
		Pos p = new Pos();
		p.x = -1;
		p.y = -1;
		double dist = 99999.9;
		
		for( int i=0; i<nMyTeam; i++ ) {
			if( i!=myPos && passPossible( myTeam[i].x, myTeam[i].y ) ) {
				double distTarg = Math.sqrt( Math.pow( myTeam[i].x - my_x,2 ) + Math.pow( myTeam[i].y - my_y,2 ) );
				if( distTarg < dist && distTarg > 75.0 ) {
					p.x = myTeam[i].x;
					p.y = myTeam[i].y;
					dist = distTarg;
				}
			}
		}
				
		return p;
	} // Fi choosePassTarget.
	
	// Funcio que passa la pilota en direccio a les cordenades x,y rebudes.
	private void passBall( int x, int y ) {
		if( x!=-1 ) {
			double distTarg = Math.sqrt( Math.pow( x - my_x,2 ) + Math.pow( y - my_y,2 ) );
			pilota.x = (int)(my_x + (x - my_x)/distTarg*15.0);
			pilota.y = (int)(my_y + (y - my_y)/distTarg*15.0);
			ball.setPilota( pilota.x, pilota.y );
			ball.setTargX( x );
			ball.setTargY( y );
		}
		pass = 0;
	} // Fi passBall.
	
	//Funcio que comprova si el passe és possible.
	private boolean passPossible( int dx, int dy ) {
		//Inicialitzem valors a la nostra posicio.
		double x = my_x;
		double y = my_y;
		double distTarg, distEne;
		//Anem avançant 5.0 fins que arribem al desti.
		while( x!= dx && y != dy ) {
			distTarg = Math.sqrt( Math.pow( dx - x,2 ) + Math.pow( dy - y,2 ) );
			x = (int)(x + (dx - x)/distTarg*5.0);
			y = (int)(y + (dy - y)/distTarg*5.0);
			if( distTarg <= 5.0 ) {
				x = dx;
				y = dy;
			}
			//Comprovem per cada contrari si aquest es troba a suficient distancia com per interceptar el passe.
			for( int i=0; i<nOtherTeam; i++ ) {
				distEne = Math.sqrt( Math.pow( otherTeam[i].x - x,2 ) + Math.pow( otherTeam[i].y - y,2 ) );
				if( distEne <= 30.0 ) return false;
			}
		}
		return true;	
	
	} // Fi passPossible.
	
	//Funcio que comprova si te la pilota.
	private boolean hasBall() {
		if( Math.sqrt( Math.pow( pilota.x - my_x,2 ) + Math.pow( pilota.y - my_y,2 ) ) < 20.0 ) 
			return true;
		return false;
	} // Fi hasBall.
	
	private void parar() {
		Random generator = new Random();
		int n = (generator.nextInt() % 100);
		if( n < 60 || !defending ) {
			ball.stop();
		}
		else {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException ie) {
			}
		}
	}
	
	//Funcio que retorna si la pilota la te un company d'equip. 
	private boolean teammateHasBall() {
		for( int i=0; i<nMyTeam; i++ ) {
			double dist = Math.sqrt( Math.pow( pilota.x-myTeam[i].x, 2 ) + Math.pow( pilota.y-myTeam[i].y, 2 ) );
			if( dist<20.0 && i!=myPos ) {
				return true;
			}
		}
		return false;
	} //Fi myTeamHasBall.
	
	//Funcio que retorna si la pilota la te un rival. 
	private boolean enemyHasBall() {
		for( int i=0; i<nOtherTeam; i++ ) {
			double dist = Math.sqrt( Math.pow( pilota.x-otherTeam[i].x, 2 ) + Math.pow( pilota.y-otherTeam[i].y, 2 ) );
			if( dist<20.0 ) {
				return true;
			}
		}
		return false;
	} //Fi myTeamHasBall.
			
	
	/*Inici funcio run.
	 *Aquesta funcio executa el comportament de l'agent Porter dins de l'entorn assignat en format de thread.
	 */
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
			if( enemyHasBall() ) {
				defending = true;
			}
			if( teammateHasBall() ) {
				defending = false;
			}
				
			Recolocar();
			if( hasBall() ) {
				parar();
				pass++;
				if( pass > 5 ) {
					Pos p = choosePassTarget();
					passBall( p.x, p.y );
				}
			}
			else {
				pass=0;
			}			
		}
	} //Fi funcio run.

}