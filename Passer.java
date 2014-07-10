import java.util.concurrent.*;
import java.util.Random;
import java.io.*;

public class Passer extends Agent implements Runnable{
	public static final double MOVE = 5.0;
	Pos[] myTeam, otherTeam;
	int nMyTeam, nOtherTeam;
	int myPos;
	Pos randomPos;
	Pilota ball;
	boolean haveBall;
	int pass;
	Random generator = new Random();
	
	//Constructor de classe.
	public Passer(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team, int pos, Pilota ball) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myPos = pos;
		haveBall = false;
		randomPos = new Pos();
		this.ball = ball;
		nextRandomPos();
		
		if( team == 1 ) {
			myTeam = teamA;
			otherTeam = teamB;
			nMyTeam = nteamA;
			nOtherTeam = nteamB;
		}
		else {
			myTeam = teamB;
			otherTeam = teamA;
			nMyTeam = nteamB;
			nOtherTeam = nteamA;
		}
	} //Fi constructor.
	
	//Funcio que mou l'agent direcció un objectiu.
	private void moveToRandom() {
		double nouX, nouY;
		double distTarg = Math.sqrt( Math.pow( randomPos.x - my_x,2 ) + Math.pow( randomPos.y - my_y,2 ) );
		
		nouX = my_x + (randomPos.x - my_x)/distTarg*MOVE;
		nouY = my_y + (randomPos.y - my_y)/distTarg*MOVE;
		
		Pos p = pathBlocked( (int)nouX, (int)nouY );
		
		if( haveBall ) {
			distTarg = Math.sqrt( Math.pow( p.x - my_x,2 ) + Math.pow( p.y - my_y,2 ) );
			pilota.x = (int)(my_x + (p.x - my_x)/distTarg*15.0);
			pilota.y = (int)(my_y + (p.y - my_y)/distTarg*15.0);
			ball.setPilota( pilota.x, pilota.y );
		}
		my_x = p.x;
		my_y = p.y;
	
	} //Fi moveToObjective.
	
	//Funcio que mou l'agent direcció la pilota.
	private void moveToBall() {
		double nouX, nouY;
		double distTarg = Math.sqrt( Math.pow( pilota.x - my_x,2 ) + Math.pow( pilota.y - my_y,2 ) );
		if( distTarg < 20.0 ) {
			haveBall = true;
			ball.stop();
		}
		nouX = my_x + (pilota.x - my_x)/distTarg*MOVE;
		nouY = my_y + (pilota.y - my_y)/distTarg*MOVE;
		
		Pos p = pathBlocked( (int)nouX, (int)nouY );
		
		my_x = p.x;
		my_y = p.y;
	
	} //Fi moveToBall.
	
	//Funcio que comprova si l'agent té la pilota.
	private boolean doIHaveBall() {
		return haveBall;
	} //Fi doIHaveBall.
	
	//Funcio que comprova si la pilota ha entrar a una de les porteries.
	private boolean goal() {
		if( (pilota.x < limitOest-9 || pilota.x > limitEst+9) && (pilota.y < limitSud/2+32 && pilota.y > limitNord/2-32) ) {
			return true;
		}
		else return false;	
	} //Fi goal.
	
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
				if( x<limitOest+10.0 || x>limitEst-10.0 || y<limitNord+10.0 || y>limitSud+10.0 ) {
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
				if( x<limitOest+10.0 || x>limitEst-10.0 || y<limitNord+10.0 || y>limitSud+10.0 ) {
					x = (int)(my_x - eixX);
					y = (int)(my_y - eixY);
				}
			}
		}
		if( x<limitOest+10.0 || x>limitEst-10.0 || y<limitNord+10.0 || y>limitSud+10.0 ) {
			x = (int)my_x;
			y = (int)my_y;
		}
		Pos p = new Pos();
		p.x = x;
		p.y = y;
		return p;
	} //Fi pathBlocked.
	
	//Funcio que retorna si la pilota la te un company d'equip. 
	private boolean teammateHasBall() {
		
		for( int i=0; i<nMyTeam; i++ ) {
			double dist = Math.sqrt( Math.pow( pilota.x-myTeam[i].x, 2 ) + Math.pow( pilota.y-myTeam[i].y, 2 ) );
			//System.out.println("Pilota("+pilota.x+","+pilota.y+")  myTeam["+i+"]("+myTeam[i].x+","+myTeami].y+")  Me("+my_x+","+my_y+")");
			if( dist<=16.0 && i!=myPos ) {
				return true;
			}
		}
		return false;
	} //Fi myTeamHasBall.
	// Funcio que escull un company a qui passar-li la pilota.
	private Pos choosePassTarget() {
		Pos p = new Pos();
		p.x = -1;
		p.y = -1;
		double dist = 99999.9;
		/*Random r = new Random();
		int i = (r.nextInt() % nMyTeam);
		if( i<0 ) i*=(-1);
		//for( int i=0; i<nMyTeam; i++ ) {
			if( i!=myPos  ) {
				p.x = myTeam[i].x;
				p.y = myTeam[i].y;
			}
		//}*/
		for( int i=0; i<nMyTeam; i++ ) {
			if( i!=myPos && passPossible( myTeam[i].x, myTeam[i].y ) ) {
				double distTarg = Math.sqrt( Math.pow( myTeam[i].x - my_x,2 ) + Math.pow( myTeam[i].y - my_y,2 ) );
				if( distTarg < dist ) {
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
			haveBall = false;
			double distTarg = Math.sqrt( Math.pow( x - my_x,2 ) + Math.pow( y - my_y,2 ) );
			pilota.x = (int)(my_x + (x - my_x)/distTarg*15.0);
			pilota.y = (int)(my_y + (y - my_y)/distTarg*15.0);
			ball.setPilota( pilota.x, pilota.y );
			ball.setTargX( x );
			ball.setTargY( y );
		}
		pass = 0;
	} // Fi passBall.
	private void nextRandomPos() {
		randomPos.x = (generator.nextInt() % (610-28));
		if (randomPos.x<0) { 
			randomPos.x *= (-1); 
		}
		randomPos.y = (generator.nextInt() % (484-0));
		if (randomPos.y<0) { 
			randomPos.y *= (-1); 
		}
		randomPos.x += 28;
		pass=0;
	}
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
				if( distEne <= 20.0 ) return false;
			}
		}
		return true;	
	
	} // Fi passPossible.

	/*Inici funcio run.
	 *Aquesta funcio executa el comportament de l'agent BallFinder dins de l'entorn assignat en format de thread.
	 */
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			//System.out.println("My X: "+(int)my_x+" My Y: "+(int)my_y+"    Pilota("+pilota.x+","+pilota.y+")");
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
			if( teammateHasBall()==false ) {
				if( doIHaveBall() ) {
					pass++;
					if( pass > 15 ) {
						Pos p = choosePassTarget();
						passBall( p.x, p.y );
					}
					moveToRandom();
				}
				else {
					moveToBall();
					nextRandomPos();
				}
			}
			else {
				moveToRandom();
			}
			
			//Si gol...
			fi = goal();
			
		}
	} //Fi funcio run.
	
}