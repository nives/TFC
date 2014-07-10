import java.util.concurrent.*;

public class Avoider extends Agent implements Runnable{
	public static final double MOVE = 7.0;
	int myTeam;
	
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
		}
	}
	
	/*Constructor de classe*/
	public Avoider(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myTeam = team;
	} //Fi constructor
	
	
	/*Inici funcio findClosesChaser.
	 *Aquesta funcio detecta el dos perseguidors mes propers aixi com la distancia amb els limits del mapa.
	 */
	public Dist findClosestChaser(){
		Dist d = new Dist();
		Pos[] team;
		int nteam = 0;
		
		if (myTeam==1) {
			team = teamB;
			nteam = nteamB;
		}
		else {
			team = teamA;
			nteam = nteamA;
		}
		for( int i=0; i<nteam; i++ ) {
			double a = Math.sqrt( Math.pow(team[i].x - my_x,2) + Math.pow(team[i].y - my_y,2) );
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
				d.x1=team[i].x - my_x;
				d.y1=team[i].y - my_y;
			}
			else if (a>=d.dist1 && a<d.dist2) {
				d.dist3=d.dist2;
				d.pos3=d.pos2;
				d.x3=d.x2;
				d.y3=d.y2;
				d.dist2=a;
				d.pos2=i;
				d.x2=team[i].x - my_x;
				d.y2=team[i].y - my_y;
			}
			else if (a<d.dist3) {
				d.dist3=a;
				d.pos3=i;
				d.x3=team[i].x - my_x;
				d.y3=team[i].y - my_y;
			}
		}
			
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
		Pos[] team, my_Team;
		int nteam;
		
		if (myTeam==1) {
			team = teamB;
			my_Team = teamA;
			nteam = nteamA;
		}
		else {
			team = teamA;
			my_Team = teamB;
			nteam = nteamB;
		}
		
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
		
		//Comprovem que no se surti de la pantalla.
		boolean ok=true;
		if( noux <= limitOest || noux >= limitEst) ok=false;
		if( nouy <= limitNord || nouy >= limitSud) ok=false;
		
		//Comprovem que no colisioni amb algun alre company. 
		for( int i=0; i<nteam && ok; i++ ) {
			if ( Math.sqrt( Math.pow(my_Team[i].x-noux,2) + Math.pow(my_Team[i].y-nouy,2) ) < 40.0 && my_Team[i].x != (int)my_x && my_Team[i].y != (int)my_y) {
				ok=false;
			}
		}
		
		//Si passa totes les comprovacions modifiquem el seu valor.
		if( ok ) {
			System.out.print("Pos1="+d.pos1+" Pos2="+d.pos2+" Pos3="+d.pos3);
			if(d.pos1>=0) System.out.print(" POS1("+(int)team[d.pos1].x+","+(int)team[d.pos1].y+") ");
			if(d.pos2>=0) System.out.print(" POS2("+(int)team[d.pos2].x+","+(int)team[d.pos2].y+") ");
			if(d.pos3>=0) System.out.print(" POS3("+(int)team[d.pos3].x+","+(int)team[d.pos3].y+") ");
			System.out.println(" MOVE("+(int)x_axis+","+(int)y_axis+") ME("+(int)my_x+","+(int)my_y+")");
			my_x = noux;
			my_y = nouy;		
		}

	} //Fi funcio avoidThat.
	
	/*Inici funcio chased.
	 *Aquesta funcio detecta si algun jugador rival m'ha atrapat.
	 */
	private boolean chased() {
		Pos[] team;
		int nteam = -1;
		if (myTeam==1) {
			team = teamB;
			nteam = nteamB;
		}
		else {
			team = teamA;
			nteam = nteamA;
		}
		for( int i=0; i<nteam; i++ ) {
			double a = Math.sqrt( Math.pow(team[i].x - my_x,2) + Math.pow(team[i].y - my_y,2) );
			if( a<35.0 ) {
				my_x=-30.0;
				my_y=-30.0;
				return true;
			}
		}
		return false;
	} //Fi funcio chased.
	
	/*Inici funcio run.
	 *Aquesta funcio executa el comportament de l'agent Avoider dins de l'entorn assignat en format de thread.
	 */
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
			//Calculo distancies del joc
			Dist d = findClosestChaser();
			//Escullo moviment d'evasio
			avoidThat(d);
			//Si em pillen...
			fi=chased();
			
		}
	} //Fi funcio run.

}