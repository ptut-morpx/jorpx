package org.morpx.jorpx.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;

import org.morpx.jorpx.game.Morpx;
import org.morpx.jorpx.game.Morpx.MorpxMove;

public class NeatAI implements AI {
	
	private BufferedReader input;
	private PrintWriter output;
	private String[] args;
	private File dir;
	
	private Morpx game;
	private int player;

	public NeatAI(String path, File dir, String configno) {
		this.dir=dir;
		this.args=new String[] {
			"python3",
			path,
			null,
			configno+""
		};
	}
	public NeatAI(String path, File dir, String configno, int netid) {
		this.dir=dir;
		this.args=new String[] {
			"python3",
			path,
			null,
			configno+"",
			netid+""
		};
	}
	
	private void startBackend() {
		try {
			Process proc=new ProcessBuilder().command(this.args).directory(this.dir).redirectError(Redirect.INHERIT).start();
			this.input=new BufferedReader(new InputStreamReader(proc.getInputStream()));
			this.output=new PrintWriter(proc.getOutputStream(), true);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	private void finish(int player) {
		this.output.println("end");
	}
	
	private void sendMove(MorpxMove move, int player) {
		if(player!=this.player) {
			System.out.printf("-> %d%d%d%d\n", move.getX0(), move.getY0(), move.getX1(), move.getY1());
			this.output.printf("%d%d%d%d\n", move.getX0(), move.getY0(), move.getX1(), move.getY1());
		}
	}
	private void readTurn() {
		if(this.game.getNextPlayer()==this.player) {
			new Thread(() -> {
				try {
					String in=this.input.readLine();
					int[] c=new int[4];
					for(int i=0; i<4; i++) c[i]=in.charAt(i)-'0';
					System.out.printf("<- %d%d%d%d\n", c[0], c[1], c[2], c[3]);
					this.game.play(new MorpxMove(c[0], c[1], c[2], c[3]));
				} catch(Exception e) {
					this.finish(player);
					throw new RuntimeException(e);
				}
			}).start();
		}
	}
	
	@Override
	public void install(Morpx game, int player) {
		// initialize interface
		this.player=player;
		this.game=game;
		
		// initialize the backend
		this.args[2]=player==-1?"P2":"P1";
		this.startBackend();
		
		// transfer data back and forth
		game.onMove(this::sendMove);
		game.onTurn(this::readTurn);
		game.onFinish(this::finish);
	}

}
