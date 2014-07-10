import java.io.*;

public class Entorn {

	private static final int N_PLAYERS_TA = 5;			//# agents en l'equip A
	private static final int N_PLAYERS_TB = 5;			//# agents en l'equip B
	private static final boolean BALL = true;			//Hi ha pilota (o no)
	

	private Grafics g;
	private Pilota pilota;
	private Agent[] teamA;
	private Agent[] teamB;
	private int goalsA, goalsB;
	
	public Entorn() {}
	
	public Entorn( Grafics g ) {
		
		this.g = g;
		g.init_Grafics( BALL, N_PLAYERS_TA, N_PLAYERS_TB );
		
		teamA = new Agent[N_PLAYERS_TA];
		teamB = new Agent[N_PLAYERS_TB];
		goalsA = 0;
		goalsB = 0;
		
		Inicialitza();
		g.getDades( pilota, teamA, teamB );
		
	}
	
	private void Inicialitza() {
		
		if ( BALL ) pilota = new Pilota( teamA, teamB, N_PLAYERS_TA, N_PLAYERS_TB );
		else pilota = new Pilota(-1,-1 );
		
		for( int i=0; i<N_PLAYERS_TA; i++ ) {
			//teamA[i] = new Agent( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB );
			//teamA[i] = new Chaser( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1);
			//new Thread(((Chaser)teamA[i])).start();
			/*teamA[i] = new BallFinder( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1, i, pilota);
			new Thread(((BallFinder)teamA[i])).start();*/
			//teamA[i] = new Shooter( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1, i, pilota);
			//new Thread(((Shooter)teamA[i])).start();
			teamA[i] = new Player( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1, i, pilota);
			new Thread(((Player)teamA[i])).start();
			/*teamA[i] = new Passer( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1, i, pilota);
			new Thread(((Passer)teamA[i])).start();*/
		}
		teamA[0] = new Porter( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 1, 0, pilota);
		new Thread(((Porter)teamA[0])).start();

		for( int i=0; i<N_PLAYERS_TB; i++ ) {
			//teamB[i] = new Agent( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB );
			//teamB[i] = new Avoider( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2);
			//new Thread(((Avoider)teamB[i])).start();
			//teamB[i] = new BallFinder( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2, i, pilota);
			//new Thread(((BallFinder)teamB[i])).start();
			//teamB[i] = new Defender( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2, i, pilota);
			//new Thread(((Defender)teamB[i])).start();
			//teamB[i] = new Shooter( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2, i, pilota);
			//new Thread(((Shooter)teamB[i])).start();
			teamB[i] = new Player( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2, i, pilota);
			new Thread(((Player)teamB[i])).start();
		}
		teamB[0] = new Porter( 0, 484, g.GOL_DRET, g.GOL_ESQ, N_PLAYERS_TA, N_PLAYERS_TB, 2, 0, pilota);
		new Thread(((Porter)teamB[0])).start();
		
	}
	
	public void sendEnv() {
		
		int[] dades = new int[N_PLAYERS_TA*2+N_PLAYERS_TB*2+2];
		int i,j;
		g.getDades( pilota, teamA, teamB );
		
		dades[0] = pilota.getPosX();
		dades[1] = pilota.getPosY();
		for ( i=0, j=2; i<N_PLAYERS_TA; i++, j+=2) {
			dades[j] = teamA[i].getPosX();
			dades[j+1] = teamA[i].getPosY();
		}
		for ( i=0, j=N_PLAYERS_TA*2+2; i<N_PLAYERS_TB; i++, j+=2 ) {
			dades[j] = teamB[i].getPosX();
			dades[j+1] = teamB[i].getPosY();
		}
		for( i=0; i<N_PLAYERS_TA; i++ ) {
			teamA[i].gatherDades( dades );
		}
		for( i=0; i<N_PLAYERS_TB; i++ ) {
			teamB[i].gatherDades( dades );
		}
	}
	
	public void moveBall() {
		if( BALL ) {
			pilota.move();
			int px = pilota.getPosX();
			int py = pilota.getPosY();
			//La pilota esta fora del camp.
			if( ( ( px < g.GOL_ESQ-5 && ( py < g.PAL_NORD || py > g.PAL_SUD ) ) || ( px > g.GOL_DRET+5 && ( py < g.PAL_NORD || py > g.PAL_SUD ) ) ) || py < 0 || py > g.MITAT_Y*2 ) {
				System.out.println(" *** OUT! *** ");
				pilota.stop();
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ie) {
				}
				pilota.reset();
				for( int i=1; i<N_PLAYERS_TA; i++ ) {
					((Player)teamA[i]).Reset();
				}
				for( int i=1; i<N_PLAYERS_TB; i++ ) {
					((Player)teamB[i]).Reset();
				}
			}
		}
	}
	public void goal() {
		if( BALL ) {
			if( pilota.getPosX() <= g.GOL_ESQ-5 && pilota.getPosY() >= g.PAL_NORD+5 && pilota.getPosY() <= g.PAL_SUD-5 ) {
				goalsA++;
				System.out.println(" *** GOAL! *** ");
				System.out.println("Team A: "+goalsA);
				System.out.println("Team B: "+goalsB);
				pilota.stop();
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ie) {
				}
				pilota.reset();
				for( int i=1; i<N_PLAYERS_TA; i++ ) {
					((Player)teamA[i]).Reset();
				}
				for( int i=1; i<N_PLAYERS_TB; i++ ) {
					((Player)teamB[i]).Reset();
				}
			}
			else if( pilota.getPosX() >= g.GOL_DRET+5 && pilota.getPosY() >= g.PAL_NORD+5 && pilota.getPosY() <= g.PAL_SUD-5 ) {
				goalsB++;
				System.out.println(" *** GOAL! *** ");
				System.out.println("Team A: "+goalsA);
				System.out.println("Team B: "+goalsB);
				pilota.stop();
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ie) {
				}
				pilota.reset();
				for( int i=1; i<N_PLAYERS_TA; i++ ) {
					((Player)teamA[i]).Reset();
				}
				for( int i=1; i<N_PLAYERS_TB; i++ ) {
					((Player)teamB[i]).Reset();
				}
			}
		}
	}
	
}