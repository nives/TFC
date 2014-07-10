import java.util.concurrent.*;

public class BallFinder extends Agent implements Runnable{
	public static final double MOVE = 5.0;
	Pos[] myTeam, otherTeam;
	int nMyTeam, nOtherTeam;
	int myPos;
	Pos target;
	Pilota ball;
	boolean haveBall;
	
	//Constructor de classe.
	public BallFinder(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team, int pos, Pilota ball) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myPos = pos;
		haveBall = false;
		target = new Pos();
		this.ball = ball;
		if( team == 1 ) {
			myTeam = teamA;
			otherTeam = teamB;
			nMyTeam = nteamA;
			nOtherTeam = nteamB;
			target.x = limitOest - 10;
			target.y = limitSud/2;
		}
		else {
			myTeam = teamB;
			otherTeam = teamA;
			nMyTeam = nteamB;
			nOtherTeam = nteamA;
			target.x = limitEst + 10;
			target.y = limitSud/2;
		}
	} //Fi constructor.
	
	//Funcio que mou l'agent direcció un objectiu.
	private void moveToObjecive( int targX, int targY ) {
		double nouX, nouY;
		double distTarg = Math.sqrt( Math.pow( targX - my_x,2 ) + Math.pow( targY - my_y,2 ) );
		
		nouX = my_x + (targX - my_x)/distTarg*MOVE;
		nouY = my_y + (targY - my_y)/distTarg*MOVE;
		
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
				System.out.println("Xoco amb myTeam["+i+"]");
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
				System.out.println("Xoco amb otherTeam["+i+"]");
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
					moveToObjecive( target.x, target.y );
				}
				else {
					moveToBall();
				}
			}
			
			//Si gol...
			fi = goal();
			
		}
	} //Fi funcio run.
	
}
	