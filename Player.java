import java.util.concurrent.*;

public class Player extends Agent implements Runnable{
	public static final double MOVE = 5.0;
	public static final double SPRINT = 8.0;
	public static final double SHOOT_DIST = 150.0;
	Pos[] myTeam, otherTeam;
	int nMyTeam, nOtherTeam;
	int myPos, team;
	Pos target, defence;
	Pilota ball;
	int pass = 0, desm = 0;
	boolean defending;
	
	//Clase auxiliar per poder utilitzar les funcionalitats de Avoider.
	private class Dist{
		private double dist1;
		private int pos1;
		private double x1,y1;
		private double dist2;
		private int pos2;
		private double x2,y2;
		private double dist3;
		private int pos3;
		private double x3,y3;
		private double distNorth;
		private double distSouth;
		private double distEast;
		private double distWest;
		
		//Constructor de clase Dist
		public Dist(){
			dist1=9999.9;
			pos1=-1;
			dist2=9999.9;
			pos2=-1;
			dist3=9999.9;
			pos3=-1;
			distNorth=9999.9;
			distSouth=9999.9;
			distEast=9999.9;
			distWest=9999.9;
		} //Fi constructor.
	}
	
/* ****************   Constructor de classe.   **************** */
	public Player(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team, int pos, Pilota ball) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myPos = pos;
		this.team = team;
		defending = false;
		target = new Pos();
		defence = new Pos();
		this.ball = ball;
		if( team == 1 ) {
			myTeam = teamA;
			otherTeam = teamB;
			nMyTeam = nteamA;
			nOtherTeam = nteamB;
			target.x = limitOest - 10;
			target.y = limitSud/2;
			defence.x = limitEst + 10;
			defence.y = limitSud/2;
		}
		else {
			myTeam = teamB;
			otherTeam = teamA;
			nMyTeam = nteamB;
			nOtherTeam = nteamA;
			target.x = limitEst + 10;
			target.y = limitSud/2;
			defence.x = limitOest - 10;
			defence.y = limitSud/2;
		}
		Reset();
	} 
	
	void Reset() {
		//Equip esquerra.
		if( team == 2 ){
			if( myPos == 1 ) {
				my_x = 108;
				my_y = 242;
			}
			else if( myPos == 2 ) {
				my_x = 188;
				my_y = 121;
			}
			else if( myPos == 3 ) {
				my_x = 188;
				my_y = 363;
			}
			else if( myPos == 4 ) {
				my_x = 268;
				my_y = 242;
			}
		}
		else if( team == 1 ){
			if( myPos == 1 ) {
				my_x = 532;
				my_y = 242;
			}
			else if( myPos == 2 ) {
				my_x = 452;
				my_y = 121;
			}
			else if( myPos == 3 ) {
				my_x = 452;
				my_y = 363;
			}
			else if( myPos == 4 ) {
				my_x = 372;
				my_y = 242;
			}
		}
	}
// **************** Fi constructor. ****************
	
	
/* **************** Funcionalitats BallFinder **************** */
	
	// Funcio que mou l'agent direcció un objectiu.
	private void moveToObjecive( int targX, int targY, int mode ) {
		double nouX, nouY, moveMode;
		if( mode == 0 ) {
			moveMode = MOVE;
		}
		else {
			moveMode = SPRINT;
		}
		double distTarg = Math.sqrt( Math.pow( targX - my_x,2 ) + Math.pow( targY - my_y,2 ) );
		nouX = my_x + (targX - my_x)/distTarg*moveMode;
		nouY = my_y + (targY - my_y)/distTarg*moveMode;
		
		//Comprovem que la posicio sigui correcte i que no xoquem amb altres agents.
		Pos p = pathBlocked( (int)nouX, (int)nouY );
		
		//Si tenim la pilota movem la pilota amb nosaltres.
		if( haveBall() ) {
			distTarg = Math.sqrt( Math.pow( p.x - my_x,2 ) + Math.pow( p.y - my_y,2 ) );
			pilota.x = (int)(my_x + (p.x - my_x)/distTarg*15.0);
			pilota.y = (int)(my_y + (p.y - my_y)/distTarg*15.0);
			ball.setPilota( pilota.x, pilota.y );
		}
		//Situem la nova posicio de l'agent un cop finalitzats tots els moviments.
		my_x = p.x;
		my_y = p.y;
	
	} //Fi moveToObjective.
	
	//Funcio que mou l'agent direcció la pilota.
	private void moveToBall() {
		double nouX, nouY;
		double distTarg = Math.sqrt( Math.pow( pilota.x - my_x,2 ) + Math.pow( pilota.y - my_y,2 ) );
		if( distTarg < 20.0 ) {
			ball.stop();
		}
		nouX = my_x + (pilota.x - my_x)/distTarg*MOVE;
		nouY = my_y + (pilota.y - my_y)/distTarg*MOVE;
		
		//Comprovem que la posicio sigui correcte i que no xoquem amb altres agents.
		Pos p = pathBlocked( (int)nouX, (int)nouY );
		
		//Situem la nova posicio de l'agent un cop finalitzats tots els moviments.
		my_x = p.x;
		my_y = p.y;
	
	} //Fi moveToBall.
	
/* **************** Fi funcionalitats BallFinder **************** */

/* **************** Funcionalitats Auxiliars **************** */
	
	//Funcio que comprova si l'agent té la pilota.
	private boolean haveBall() {
		double distTarg = Math.sqrt( Math.pow( pilota.x - my_x,2 ) + Math.pow( pilota.y - my_y,2 ) );
		if( distTarg < 20.0 ) {
			return true;
		}
		return false;
	} //Fi haveBall.
	
	//Funcio que comprova si la pilota ha entrat a una de les porteries.
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
				//Comprovem que no ens sortim del camp.
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
				//Comprovem que no ens sortim del camp.
				if( x<limitOest+10.0 || x>limitEst-10.0 || y<limitNord+10.0 || y>limitSud+10.0 ) {
					x = (int)(my_x - eixX);
					y = (int)(my_y - eixY);
				}
			}
		}
		//Comprovem que no ens sortim del camp.
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
	
	//Funcio que comprova si es el jugador del seu equip que esta mes aprop de la pilota.
	private boolean iAmTheClosestToBall() {
		double mydist = Math.sqrt( Math.pow( pilota.x - my_x,2 ) + Math.pow( pilota.y - my_y,2 ) );
		for( int i=1; i<nMyTeam; i++ ) {
			if( i!=myPos ) {
				double dist = Math.sqrt( Math.pow( pilota.x - myTeam[i].x,2 ) + Math.pow( pilota.y - myTeam[i].y,2 ) );
				if( dist < mydist ) return false;
			}
		}
		return true;
	} // Fi iAmTheClosestToBall.
	
	//Funcio que comprova si el jugador te la pilota.
	private boolean hasTheBall( double x, double y ) {
		double distTarg = Math.sqrt( Math.pow( pilota.x - x,2 ) + Math.pow( pilota.y - y,2 ) );
		if( distTarg < 20.0 ) {
			return true;
		}
		return false;
	} // Fi hasTheBall.
	
/* **************** Fi funcionalitats Auxiliars **************** */	

/* **************** Funcionalitats Shooter **************** */

	//Funcio que mira si es possible xutar i xuta si pot.
	private boolean shoot() {
		double dist = Math.sqrt( Math.pow( target.x-my_x, 2 ) + Math.pow( target.y-my_y, 2 ) );
		if( dist <= SHOOT_DIST ) {
			for( int i=0; i<32; i++ ) {
				if( passPossible( my_x, my_y, target.x, target.y+i )) {
					ball.setTargX( target.x );
					ball.setTargY( target.y+i );
					return true;
				}
				if( passPossible( my_x, my_y, target.x, target.y-i )) {
					ball.setTargX( target.x );
					ball.setTargY( target.y-i );
					return true;
				}
			}
		}
		return false;		
	} //Fi shoot.
	
/* **************** Fi funcionalitats Shooter **************** */

/* **************** Funcionalitats Avoider **************** */
	
	//Funcio que detecta el dos perseguidors mes propers aixi com la distancia amb els limits del mapa.
	public Dist findClosestChaser(){
		Dist d = new Dist();
		
		//Calculem les distancies i vectors respecte als jugadors contraris.
		for( int i=1; i<nOtherTeam; i++ ) {
			double a = Math.sqrt( Math.pow(otherTeam[i].x - my_x,2) + Math.pow(otherTeam[i].y - my_y,2) );
			if ( a<d.dist1 ) {
				d.dist3=d.dist2;
				d.pos3=d.pos2;
				d.x3=d.x2;
				d.y3=d.y2;
				d.dist2=d.dist1;
				d.pos2=d.pos1;
				d.x2=d.x1;
				d.y2=d.y1;
				d.dist1=a;
				d.pos1=i;
				d.x1=otherTeam[i].x - my_x;
				d.y1=otherTeam[i].y - my_y;
			}
			else if (a>=d.dist1 && a<d.dist2) {
				d.dist3=d.dist2;
				d.pos3=d.pos2;
				d.x3=d.x2;
				d.y3=d.y2;
				d.dist2=a;
				d.pos2=i;
				d.x2=otherTeam[i].x - my_x;
				d.y2=otherTeam[i].y - my_y;
			}
			else if (a<d.dist3) {
				d.dist3=a;
				d.pos3=i;
				d.x3=otherTeam[i].x - my_x;
				d.y3=otherTeam[i].y - my_y;
			}
		}
		
		//Calculem les distancies i vectors respecte els marges del camp.
		d.distNorth = Math.abs(limitNord - my_y);
		if( d.distNorth < d.dist1 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.dist1;
			d.pos2=d.pos1;
			d.x2=d.x1;
			d.y2=d.y1;
			d.dist1=d.distNorth;
			d.pos1=-2;
			d.x1=0;
			d.y1=limitNord - my_y;
		}
		else if( d.distNorth >= d.dist1 && d.distNorth < d.dist2 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.distNorth;
			d.pos2=-2;
			d.x2=0;
			d.y2=limitNord - my_y;
		}
		else if( d.distNorth >= d.dist2 && d.distNorth < d.dist3 ) {
			d.dist3=d.distNorth;
			d.pos3=-2;
			d.x3=0;
			d.y3=limitNord - my_y;
		}
		d.distSouth = Math.abs(limitSud - my_y);
		if( d.distSouth < d.dist1 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.dist1;
			d.pos2=d.pos1;
			d.x2=d.x1;
			d.y2=d.y1;
			d.dist1=d.distSouth;
			d.pos1=-3;
			d.x1=0;
			d.y1=limitSud - my_y;
		}
		else if( d.distSouth >= d.dist1 && d.distSouth < d.dist2 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.distSouth;
			d.pos2=-3;
			d.x2=0;
			d.y2=limitSud - my_y;
		}
		else if( d.distSouth >= d.dist2 && d.distSouth < d.dist3 ) {
			d.dist3=d.distSouth;
			d.pos3=-3;
			d.x3=0;
			d.y3=limitSud - my_y;
		}
		d.distEast = Math.abs(limitEst - my_x);
		if( d.distEast < d.dist1 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.dist1;
			d.pos2=d.pos1;
			d.x2=d.x1;
			d.y2=d.y1;
			d.dist1=d.distEast;
			d.pos1=-4;
			d.x1=limitEst - my_x;
			d.y1=0;
		}
		else if( d.distEast >= d.dist1 && d.distEast < d.dist2 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.distEast;
			d.pos2=-4;
			d.x2=limitEst - my_x;
			d.y2=0;
		}
		else if( d.distEast >= d.dist2 && d.distEast < d.dist3 ) {
			d.dist3=d.distEast;
			d.pos3=-4;
			d.x3=limitEst - my_x;
			d.y3=0;
		}
		d.distWest = Math.abs(limitOest - my_x);
		if( d.distWest < d.dist1 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.dist1;
			d.pos2=d.pos1;
			d.x2=d.x1;
			d.y2=d.y1;
			d.dist1=d.distWest;
			d.pos1=-5;
			d.x1=limitOest - my_x;
			d.y1=0;
		}
		else if( d.distWest >= d.dist1 && d.distWest < d.dist2 ) {
			d.dist3=d.dist2;
			d.pos3=d.pos2;
			d.x3=d.x2;
			d.y3=d.y2;
			d.dist2=d.distWest;
			d.pos2=-5;
			d.x2=limitOest - my_x;
			d.y2=0;
		}
		else if( d.distWest >= d.dist2 && d.distWest < d.dist3 ) {
			d.dist3=d.distWest;
			d.pos3=-5;
			d.x3=limitOest - my_x;
			d.y3=0;
		}
		
		return d;
	}// Funcio findClosesChaser.
	
	
	/*Inici funcio avoidThat.
	 *Aquesta funcio esquiva els jugadors contraris mitjançants l'us de vectors amb aquests i els limits del camp.
	 */
	public void avoidThat( Dist d ) {
		double angle=0.0;
		double x_axis=0.0, prex1=0.0, prex2=0.0;
		double y_axis=0.0, prey1=0.0, prey2=0.0;
		
		//Girem els moduls dels dos vectors mes llunyans per donar mes prioritat al vector mes curt, és a dir, al jugador o paret que estigui mes aprop dels dos.
		double mod1 = Math.sqrt( Math.pow( d.x3,2 ) + Math.pow( d.y3,2 ) );
		double mod2 = Math.sqrt( Math.pow( d.x2,2 ) + Math.pow( d.y2,2 ) );
		prex1 = (d.x3/mod1*mod2) + (d.x2/mod2*mod1);
		prey1 = (d.y3/mod1*mod2) + (d.y2/mod2*mod1);
		
		//Fem el mateix pel vector resultat i el vector mes curt.
		mod1 = Math.sqrt( Math.pow( prex1,2 ) + Math.pow( prey1,2 ) );
		mod2 = Math.sqrt( Math.pow( d.x1,2 ) + Math.pow( d.y1,2 ) );
		x_axis = (prex1/mod1*mod2) + (d.x1/mod2*mod1);
		y_axis = (prey1/mod1*mod2) + (d.y1/mod2*mod1);
		
		//Reescalem el vector a la ida de moviment que desitgem.
		double modf = Math.sqrt( Math.pow( x_axis,2 ) + Math.pow( y_axis,2 ) );
		x_axis = x_axis/modf*(-MOVE);
		y_axis = y_axis/modf*(-MOVE);
		
		//Calculem la nova posició en que se situara.
		double noux = my_x + x_axis;
		double nouy = my_y + y_axis;
		
		Pos p = pathBlocked( (int)noux, (int)nouy );
		my_x = p.x;
		my_y = p.y;

	} //Fi funcio avoidThat.
	
	private Pos offTheBall() {
		double[][] distances = new double[612][480];
		double dist;
		Pos zonaP = new Pos();
		Pos zonaD = new Pos();
		Pos zonaA1 = new Pos();
		Pos zonaA2 = new Pos();
		double distZonaP = 0.0, distZonaD = 0.0, distZonaA1 = 0.0, distZonaA2 = 0.0;
		int[] positions = new int[nMyTeam];
		for( int i=28; i<612; i+=5 ) {
			for( int j=1; j<480; j+=5 ) {
				distances[i][j] = 99999.9;
				//Calculem la distancia de la posicio a tots els rivals.
				for( int n=0; n<nOtherTeam; n++ ) {
					dist = Math.sqrt( Math.pow(otherTeam[n].x - i,2) + Math.pow(otherTeam[n].y - j,2) );
					if( dist < distances[i][j] ) {
						distances[i][j] = dist;
					}
				}
				//Calculem la distancia de la posicio a tots els meus companys.
				for( int n=0; n<nMyTeam; n++ ) {
					dist = Math.sqrt( Math.pow(myTeam[n].x - i,2) + Math.pow(myTeam[n].y - j,2) );
					if( dist < distances[i][j] && n!=myPos ) {
						distances[i][j] = dist;
						
					}
				}
				//Calculem la distancia de la posicio al limit nord.
				dist = Math.abs(limitNord - j);
				if( dist < distances[i][j] ) {
					distances[i][j] = dist;
				}
				//Calculem la distancia de la posicio al limit sud.
				dist = Math.abs(limitSud - j);
				if( dist < distances[i][j] ) {
					distances[i][j] = dist;
				}
				//Calculem la distancia de la posicio al limit est.
				dist = Math.abs(limitEst - i);
				if( dist < distances[i][j] ) {
					distances[i][j] = dist;
				}
				//Calculem la distancia de la posicio al limit oest.
				dist = Math.abs(limitOest - i);
				if( dist < distances[i][j] ) {
					distances[i][j] = dist;
				}
				//Calculem la posicio mes desmarcada de la zonaPivot/zonaDefensa.
				if( ((i<222 && myTeam[0].x < 222) || (i>418 && myTeam[0].x > 418)) && distances[i][j] > distZonaD ) {
					distZonaD = distances[i][j];
					zonaD.x = i;
					zonaD.y = j;
				}
				else if( ((i<222 && myTeam[0].x > 418) || (i>418 && myTeam[0].x < 222)) && distances[i][j] > distZonaP) {
					distZonaP = distances[i][j];
					zonaP.x = i;
					zonaP.y = j;
				}
				//Calculem la posicio mes desmarcada de la zonaAla1
				else if( j<240 ) {
					if( distances[i][j] > distZonaA1 ) {
						distZonaA1 = distances[i][j];
						zonaA1.x = i;
						zonaA1.y = j;
					}
				}
				//Calculem la posicio mes desmarcada de la zonaAla1
				else if( j>241 ) {
					if( distances[i][j] > distZonaA2 ) {
						distZonaA2 = distances[i][j];
						zonaA2.x = i;
						zonaA2.y = j;
					}
				}
			}
		}
		
		//Inicialitzem les posicions assignades a cada company d'equip.
		for( int i=0; i<nMyTeam; i++ ) {
			positions[i] = -1;
		}
		double min = 99999.9;
		int jug = -1;
		//Assignem jugador a la posicio pivot(1).
		for( int i=1; i<nMyTeam; i++ ) {
			double d = Math.sqrt( Math.pow(myTeam[i].x - zonaP.x,2) + Math.pow(myTeam[i].y - zonaP.y,2) );
			if( d < min && positions[i]==-1 ) {
				min = d;
				jug = i;
			}
		}
		if( jug == myPos ) {
			return zonaP;
		}
		positions[jug] = 1;
		//Assignem jugador a la posicio defensa(2).
		for( int i=1; i<nMyTeam; i++ ) {
			double d = Math.sqrt( Math.pow(myTeam[i].x - zonaD.x,2) + Math.pow(myTeam[i].y - zonaD.y,2) );
			if( d < min && positions[i]==-1 ) {
				min = d;
				jug = i;
			}
		}
		if( jug == myPos ) {
			return zonaD;
		}
		positions[jug] = 2;
		//Assignem jugador a la posicio ala1(3).
		for( int i=1; i<nMyTeam; i++ ) {
			double d = Math.sqrt( Math.pow(myTeam[i].x - zonaA1.x,2) + Math.pow(myTeam[i].y - zonaA1.y,2) );
			if( d < min && positions[i]==-1 ) {
				min = d;
				jug = i;
			}
		}
		if( jug == myPos ) {
			return zonaA1;
		}
		positions[jug] = 3;
		//Assignem jugador a la posicio ala2(4).
		for( int i=1; i<nMyTeam; i++ ) {
			double d = Math.sqrt( Math.pow(myTeam[i].x - zonaA2.x,2) + Math.pow(myTeam[i].y - zonaA2.y,2) );
			if( d < min && positions[i]==-1 ) {
				min = d;
				jug = i;
			}
		}
		if( jug == myPos ) {
			return zonaA2;
		}
		positions[jug] = 4;
		return zonaA2;
} // Fi offTheBall.
	
/* **************** Fi funcionalitats Avoider **************** */

/* **************** Funcionalitats Defender **************** */
	
	//Funcio que mou el jugador cap a les coordenades que s'ha de moure per poder cobrir al rival.
	private void moveToCover( double x, double y ) {
		double nouX, nouY, nouTX, nouTY;
		if( x>0 && y>0 ) {
			//Calculem la posicio de covertura a la que ens haurem de moure.
			double distTarg = Math.sqrt( Math.pow( defence.x - x,2 ) + Math.pow( defence.y - y,2 ) );
			nouTX = x + (defence.x - x)/distTarg*50.0;
			nouTY = y + (defence.y - y)/distTarg*50.0;
			
			//Ens movem direccio la posicio de covertura
			double distmove = Math.sqrt( Math.pow( nouTX - my_x,2 ) + Math.pow( nouTY - my_y,2 ) );
			//Si la distancia calculada es inferior a la distancia de moviment ens movem a la posicio final directament.
			if( distmove <= MOVE ) {
				//nouX = (nouTX - my_x);
				//nouY = (nouTY - my_y);
				nouX = my_x;
				nouY = my_y;
			}
			//Sino ens movem la distancia de moviment en la direccio a la posicio desti.
			else {
				nouX = my_x + (nouTX - my_x)/distmove*MOVE;
				nouY = my_y + (nouTY - my_y)/distmove*MOVE;
			}
			//Comprovem que la posicio sigui correcte i no xoquem contra altres jugadors.
			Pos p = pathBlocked( (int)nouX, (int)nouY );
			
			my_x = p.x;
			my_y = p.y;
		}
	
	} //Fi moveToCover.
	
	// Funcio que escull el jugador que ha de cobrir segons les posicions dels seus companys.
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
	} // Fi selectCover.
	
	// Funcio que escull el jugador que ha de cobrir segons les posicions dels seus companys.
	private Pos selectCover2() {
		Pos p = new Pos();
		double x, y;
		p.x=-1; 
		p.y=-1;
		double distCovers[] = new double[nOtherTeam];
		int priCover[] = new int[nOtherTeam];
		
		for( int i=0; i<nOtherTeam; i++ ) {
			distCovers[i] = 99999.9;
			priCover[i] = -1;
		}
		
		for( int i=1; i<nOtherTeam; i++ ) {
			if( hasTheBall( otherTeam[i].x, otherTeam[i].x ) && !isCovered( otherTeam[i].x, otherTeam[i].x ) ) {
				p.x = otherTeam[i].x;
				p.y = otherTeam[i].y;
				return p;
			}
			double distTarg = Math.sqrt( Math.pow( defence.x - otherTeam[i].x,2 ) + Math.pow( defence.y - otherTeam[i].y,2 ) );
			distCovers[nOtherTeam-1] = distTarg;
			priCover[nOtherTeam-1] = i;
			for( int j=nOtherTeam-2; j>=0; j-- ) {
				if( distCovers[j] > distCovers[j+1] ) {
					double aux = distCovers[j];
					int aux1 = priCover[j];
					distCovers[j] = distCovers[j+1];
					priCover[j] = priCover[j+1];
					distCovers[j+1] = aux;
					priCover[j+1] = aux1;
				}
			}
		}
		for( int i=0; i<nOtherTeam-1; i++ ) {
			if( !isCovered( otherTeam[priCover[i]].x, otherTeam[priCover[i]].y ) ) {
				p.x = otherTeam[priCover[i]].x;
				p.y = otherTeam[priCover[i]].y;
				return p;
			}
		}
		return p;			
	} // Fi selectCover.
	
	private boolean isCovered( double x, double y ) {
		double distTarg = Math.sqrt( Math.pow( defence.x - x,2 ) + Math.pow( defence.y - y,2 ) );
		x = x + (defence.x - x)/distTarg*50.0;
		y = y + (defence.y - y)/distTarg*50.0;
		for( int i=1; i<nMyTeam; i++ ) {
			distTarg = Math.sqrt( Math.pow( myTeam[i].x - x,2 ) + Math.pow( myTeam[i].y - y,2 ) );
			if( i != myPos && distTarg <= 20.0 ) {
				return true;
			}
		}
		return false;
	}
	
/* **************** Fi funcionalitats Defender **************** */
	
/* **************** Funcionalitats Passer **************** */
	
	// Funcio que escull un company a qui passar-li la pilota.
	private Pos choosePassTarget() {
		Pos p = new Pos();
		p.x = -1;
		p.y = -1;
		double dist = 99999.0;
		
		//Escollim el jugador mes aprop de la porteria rival i que el passe cap al qual esta despejat. Evitem passar al porter i a nosaltres mateixos.
		for( int i=0; i<nMyTeam; i++ ) {
			if( i!=myPos && passPossible( my_x, my_y, myTeam[i].x, myTeam[i].y ) ) {
				double distTarg = Math.sqrt( Math.pow( target.x - myTeam[i].x,2 ) + Math.pow( target.y - myTeam[i].y,2 ) );
				if( distTarg <= dist ) {
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
			//Recoloquem la posicio de la pilota en direccio al jugador que volem passar-li.
			pilota.x = (int)(my_x + (x - my_x)/distTarg*15.0);
			pilota.y = (int)(my_y + (y - my_y)/distTarg*15.0);
			//Li marquem a la pilota que estem xutant-la cap aquella direccio.
			ball.setPilota( pilota.x, pilota.y );
			ball.setTargX( x );
			ball.setTargY( y );
		}
	} // Fi passBall.
	
	//Funcio que comprova si el passe és possible.
	private boolean passPossible( double posX, double posY, int dx, int dy ) {
		//Inicialitzem valors a la nostra posicio.
		double x = posX;
		double y = posY;
		double distTarg, distEne;
		//Anem avançant 5.0 fins que arribem al desti.
		while( x!= dx && y != dy ) {
			distTarg = Math.sqrt( Math.pow( dx - x,2 ) + Math.pow( dy - y,2 ) );
			x = (x + (dx - x)/distTarg*5.0);
			y = (y + (dy - y)/distTarg*5.0);
			if( distTarg <= 5.0 ) {
				x = dx;
				y = dy;
			}
			//Comprovem per cada contrari si aquest es troba a suficient distancia com per interceptar el passe.
			for( int i=1; i<nOtherTeam; i++ ) {
				distEne = Math.sqrt( Math.pow( otherTeam[i].x - x,2 ) + Math.pow( otherTeam[i].y - y,2 ) );
				if( distEne <= 30.0 ) return false;
			}
		}
		return true;	
	
	} // Fi passPossible.
	
	//Funcio que busca un jugador desmarcat i li passa la pilota.
	private void passOpenMan() {
		for( int i=1; i<nMyTeam; i++ ) {
			if( i!=myPos && passPossible( myTeam[i].x, myTeam[i].y, target.x, target.y ) && passPossible( my_x, my_y, myTeam[i].x, myTeam[i].y ) ) {
				double dist = Math.sqrt( Math.pow( myTeam[i].x - target.x,2 ) + Math.pow( myTeam[i].y - target.y,2 ) );
				double mydist = Math.sqrt( Math.pow( my_x - target.x,2 ) + Math.pow( my_y - target.y,2 ) );
				if( passPossible(my_x, my_y, target.x, target.y)==false || dist<mydist ) {
					passBall( myTeam[i].x, myTeam[i].y );
					pass=0;
				}
			}
		}
	} // Fi passOpenMan.

/* **************** Fi funcionalitats Passer **************** */
	
	/*Inici funcio run.
	 *Aquesta funcio executa el comportament de l'agent Shooter dins de l'entorn assignat en format de thread.
	 */
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
			//Si m'estic desmarcant despres d'un passe.
			if( desm > 0 && enemyHasBall()==false ) {
				desm--;
				moveToObjecive( target.x, target.y, 1 );
			}
			//Sino...
			else {
				if( teammateHasBall()==false ) {
					if( enemyHasBall()==false ) {
						//Jo tinc la pilota.
						if( haveBall() ) {
							defending = false;
							//Si estic en condicions de xutar, xuto.
							if( shoot() ) {}
							//Si no xuto.
							else {
								moveToObjecive( target.x, target.y, 0 );
								pass++;
								//Busco jugadors desmarcats.
								passOpenMan();
								//Si toca passar, passo la pilota
								if( pass > 10 && passPossible(my_x, my_y, target.x, target.y)==false ) {
									Pos p = choosePassTarget();
									passBall( p.x, p.y );
									pass = 0;
									desm = 15;
								}
							}
						}
						//Ningu te la pilota.
						else {
							//Si soc el jugador del meu equip mes proper a la pilota, la vaig a buscar.
							if( iAmTheClosestToBall() ) {
								moveToBall();
							}
							else {
								//Si NO estava defensant, evito els defensors.
								if( !defending ) {
									//Calculo distancies del joc
									//Dist d = findClosestChaser();
									//Escullo moviment d'evasio
									//avoidThat(d);
									Pos p = offTheBall();
									moveToObjecive( p.x, p.y, 0 );
								}
								//Si estava defensant, cobreixo els rivals.
								else {
									Pos p = selectCover2();
									moveToCover( p.x, p.y );
								}
							}
						}
					}
					//Contrari te la pilota.
					else {
						defending = true;
						Pos p = selectCover2();
						moveToCover( p.x, p.y );
					}
				}
				//Company te la pilota, em desmarco.
				else {
					defending = false;
					//Calculo distancies del joc
					//Dist d = findClosestChaser();
					//Escullo moviment d'evasio
					//avoidThat(d);
					Pos p = offTheBall();
					moveToObjecive( p.x, p.y, 0 );
				}
			}			
		}
	} //Fi funcio run.
}