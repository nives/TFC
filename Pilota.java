import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.awt.image.*;
import java.lang.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.io.*;


public class Pilota {

	private static final int MITAT_X = 320;
	private static final int MITAT_Y = 242;
	private static final double BALL_MOVE = 10.0;		//Distancia de moviment de a pilota

	private int posX;
	private int posY;
	private double moveX;
	private double moveY;
	private int targX;
	private int targY;
	private Agent[] teamA;
	private Agent[] teamB;
	private int nTeamA, nTeamB;
	
	// Constructors de classe.
	public Pilota( Agent[] teamA, Agent[] teamB, int nteamA, int nteamB ) {
		this.teamA = teamA;
		this.teamB = teamB;
		this.nTeamA = nteamA;
		this.nTeamB = nteamB;
		posX = MITAT_X;
		posY = MITAT_Y;
		moveX = 0.0;
		moveY = 0.0;
		targX = 0;
		targY = 0;
		reset();
	}
	public Pilota( int x, int y ) {
		posX = x;
		posY = y;
		moveX = 0.0;
		moveY = 0.0;
		targX = 0;
		targY = 0;
	} // Fi constructors de classe.
	
	// Getters i Setters.
	public void setPosX( int x ) {
		posX = x;
	}
	public int getPosX() {
		return posX;
	}
	public void setPosY( int y ) {
		posY = y;
	}
	public int getPosY() {
		return posY;
	}
	public void setPilota( int x, int y ) {
		posX = x;
		posY = y;
	}
	public void setTargX( int x ) {
		targX = x;
	}
	public void setTargY( int y ) {
		targY = y;
	} // Fi Getters i Setters.
	
	//Funcio que mou la pilota segons els parametres establers en les seves variables.
	public void move() {
		if( moveX == 0.0 && moveY == 0.0 ) {
			if( targX != 0.0 || targY != 0.0 ) {
				double dist = Math.sqrt( Math.pow( targX - posX, 2 ) + Math.pow( targY - posY , 2 ) );
				if( dist < BALL_MOVE ) {
					moveX = (targX-posX)/dist*BALL_MOVE;
					moveY = (targY-posY)/dist*BALL_MOVE;
					posX += moveX;
					posY += moveY;
				}
				else {
					double eixX = (targX-posX)/dist*BALL_MOVE;
					double eixY = (targY-posY)/dist*BALL_MOVE;
					posX += (int)eixX;
					posY += (int)eixY;
				}
			}
		}
		else {
			posX += moveX;
			posY += moveY;
		}
		imOnPlayer();
	} // Fi move
	
	//Funcio que reinicia la pilota aturada en una posicio del camp aleatoria.
	public void reset() {
		stop();
		posX = 320;
		posY = 242;
	} // Fi reset.
	
	//Funcio que atura el moviment de la pilota.
	public void stop() {
		moveX = 0.0;
		moveY = 0.0;
		targX = 0;
		targY = 0;
	} // Fi stop
	
	//Funcio que detecta si estic en possecio d'un jugador.
	private void imOnPlayer() {
		for( int i=1; i<nTeamA; i++ ) {
			if( Math.sqrt( Math.pow(teamA[i].getPosX() - posX,2) + Math.pow(teamA[i].getPosY() - posY,2) ) < 20.0 ) {
				stop();
			}
		}
		for( int i=1; i<nTeamB; i++ ) {
			if( Math.sqrt( Math.pow(teamB[i].getPosX() - posX,2) + Math.pow(teamB[i].getPosY() - posY,2) ) <= 20.0 ) {
				stop();
			}
		}
	} // Fi imOnPlayer
	
}