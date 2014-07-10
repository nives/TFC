import java.util.concurrent.*;

public class Chaser extends Agent implements Runnable{
	public static final double MOVE = 5.0;
	int myTeam;
	
	public Chaser(int lN, int lS, int lE, int lO, int nteamA, int nteamB, int team) {
		super(lN, lS, lE, lO, nteamA, nteamB);
		myTeam = team;
	}
	
	public int findClosestTarget(){
		double dist=9999;
		int pos=-1;
		if (myTeam==1) {
			for( int i=0; i<nteamB; i++ ) {
				if( teamB[i].x>=0 && teamB[i].y>=0 ){
					double a = Math.sqrt( Math.pow(teamB[i].x - my_x,2) + Math.pow(teamB[i].y - my_y,2) );
					if (a<dist) {
						dist=a;
						pos=i;
					}
				}
			}
		}
		else {
			for( int i=0; i<nteamA; i++ ) {
				if( teamA[i].x>=0 && teamA[i].y>=0 ){
					double a = Math.sqrt( Math.pow(teamA[i].x - my_x,2) + Math.pow(teamA[i].y - my_y,2) );
					if (a<dist) {
						dist=a;
						pos=i;
					}
				}
			}
		}
		return pos;
	}
	public void chaseThat(int obj) {
		double angle=0.0;
		double x_axis;
		double y_axis;
		int nteam;
		Pos[] team;
		if (myTeam==1) {
			x_axis = teamB[obj].x - my_x;
			y_axis = teamB[obj].y - my_y;
			nteam = nteamA;
			team = teamA;
		}
		else {
			x_axis = teamA[obj].x - my_x;
			y_axis = teamA[obj].y - my_y;
			nteam = nteamB;
			team = teamB;
		}
		if (x_axis>0 && y_axis>=0) angle = Math.atan(y_axis/x_axis);
		else if (x_axis>0 && y_axis<0) angle = Math.atan(y_axis/x_axis)+2*Math.PI;
		else if (x_axis<0) angle = Math.atan(y_axis/x_axis)+Math.PI;
		else if (x_axis==0 && y_axis>0) angle = Math.PI/2;
		else if (x_axis==0 && y_axis<0) angle = 3*Math.PI/2;
		double nou_x = my_x+(Math.cos(angle)*MOVE);
		double nou_y = my_y+(Math.sin(angle)*MOVE);
		boolean ok=true;
		for( int i=0; i<nteam && ok; i++ ) {
			if ( Math.sqrt( Math.pow(team[i].x-nou_x,2) + Math.pow(team[i].y-nou_y,2) ) < 40.0 && team[i].x != (int)my_x && team[i].y != (int)my_y) {
				//System.out.println("OK = false.     Team("+team[i].x+","+team[i].y+")    Me("+my_x+","+my_y+")");
				ok=false;
			}
		}
		if( ok ) {
			my_x = nou_x;
			my_y = nou_y;
		}

	}
	
	public synchronized void run() {
		boolean fi=false;
		while( !fi ) {
			int pos = findClosestTarget();
			if(pos==-1) fi=true;
			else chaseThat(pos);
			try {
				Thread.sleep(200);
			} catch(InterruptedException ie) {
			}
		}
	}



}