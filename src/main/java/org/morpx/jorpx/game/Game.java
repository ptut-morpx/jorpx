package org.morpx.jorpx.game;

import java.util.List;

public interface Game {
	public int getNextPlayer();
	
	public boolean isFinished();
	public int getWinner();
	public int getEstimatedScore();
	
	public boolean canPlay(Move move);
	public List<? extends Move> getPossibleMoves();
	
	public void play(Move move);
	default Game playClone(Move move) {
		Game clone=this.clone();
		clone.play(move);
		return clone;
	}
	
	public Game clone();
}
