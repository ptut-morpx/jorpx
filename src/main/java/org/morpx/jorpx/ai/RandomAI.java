package org.morpx.jorpx.ai;

import java.util.List;
import java.util.Random;

import org.morpx.jorpx.game.Morpx;
import org.morpx.jorpx.game.Morpx.MorpxMove;

public class RandomAI implements AI {
	
	private RandomAI() {}
	public static final RandomAI INSTANCE=new RandomAI();
	
	@Override
	public void install(Morpx game, int player) {
		Random rnd=new Random();
		
		game.onTurn(() -> {
			if(game.getNextPlayer()==player) {
				List<MorpxMove> moves=game.getPossibleMoves();
				game.play(moves.get(rnd.nextInt(moves.size())));
			}
		});
	}

}
