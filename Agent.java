import java.util.Random;
import java.io.*;

public abstract class Agent {

	protected class Pos {
		protected int x;
		protected int y;
	}
	protected double my_x;
	protected double my_y;
	protected int limitNord;
	protected int limitSud;
	protected int limitEst;
	protected int limitOest;
	protected Pos pilota;
	protected Pos[] teamA;
	protected Pos[] teamB;
	protected int nteamA;
	protected int nteamB;
	
	
	public Agent( int lN, int lS, int lE, int lO, int nteamA, int nteamB ) {
		Random generator = new Random();
		my_x = (generator.nextInt() % (lE-lO-40));
		if (my_x<0) { 
			my_x *= (-1); 
		}
		my_y = (generator.nextInt() % (lS-lN-40));
		if (my_y<0) { 
			my_y *= (-1); 
		}
		my_x += lO+20.0;
		my_y += lN+20.0;
		limitNord = lN;
		limitSud = lS;
		limitEst = lE;
		limitOest = lO;
		
		this.nteamA = nteamA;
		this.nteamB = nteamB;
		teamA = new Pos[nteamA];
		teamB = new Pos[nteamB];
		
		pilota = new Pos();
		for (int i=0; i<nteamA; i++) {
			teamA[i] = new Pos();
		}
		for (int i=0; i<nteamB; i++) {
			teamB[i] = new Pos();
		}
	}
	
	public void setPosX( int x ) {
		my_x = x;
	}
	public int getPosX() {
		return (int)my_x;
	}
	public void setPosY( int y ) {
		my_y = y;
	}
	public int getPosY() {
		return (int)my_y;
	}
	public void gatherDades( int[] dades ) {
	//Dades -> pilota_x, pilota_y, teamA_1_x, teamA_1_y, ... , teamA_5_x, teamA_5_y, teamB_*, ...
		int i,j;
		
		pilota.x = dades[0];
		pilota.y = dades[1];
		for ( i=0, j=2; i<nteamA; i++, j+=2) {
			teamA[i].x = dades[j];
			teamA[i].y = dades[j+1];
		}
		for ( i=0, j=nteamA*2+2; i<nteamB; i++, j+=2 ) {
			teamB[i].x = dades[j];
			teamB[i].y = dades[j+1];
		}
	
	}

}