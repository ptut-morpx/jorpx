package org.morpx.jorpx.game;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.morpx.jorpx.struct.Pair;

public class GUI {
	private JFrame frame;
	private Map<Morpx.MorpxMove, JButton> cells;
	private Map<Pair<Integer, Integer>, JPanel> panels;
	private JPanel panel;
	private Morpx game;
	private Set<Integer> humanPlayers;
	
	private void draw() {
		for(int x=0; x<3; x++) for(int y=0; y<3; y++) {
			if(this.game.getMetaCell(x, y)==1) {
				int[] xs={0, 2, 1, 0, 2};
				int[] ys={0, 0, 1, 2, 2};
				for(int i=0; i<5; i++) {
					this.cells.get(new Morpx.MorpxMove(x, y, xs[i], ys[i])).setBackground(Color.BLUE);
				}
			} else if(this.game.getMetaCell(x, y)==-1) {
				int[] xs={0, 1, 2, 0, 2, 0, 1, 2};
				int[] ys={0, 0, 0, 1, 1, 2, 2, 2};
				for(int i=0; i<8; i++) {
					this.cells.get(new Morpx.MorpxMove(x, y, xs[i], ys[i])).setBackground(Color.ORANGE);
				}
			}
		}
	}
	
	private void onFinish(int winner) {
		this.frame.setTitle("Game ended");
		if(winner==1) this.frame.setTitle("P1 won!");
		if(winner==-1) this.frame.setTitle("P2 won!");
		for(JButton btn:this.cells.values()) {
			btn.setBackground(Color.DARK_GRAY);
			btn.setEnabled(false);
		}
		draw();
	}
	private void onMove(Morpx.MorpxMove move, int player) {
		this.cells.get(move).setText(player==1?"X":"O");
		this.cells.get(move).setEnabled(false);
		this.frame.pack();
		
	}
	private void onCellFinish(int x, int y, int player) {
		this.panels.get(new Pair<Integer, Integer>(x, y)).setEnabled(false);
	}
	
	private void onTurn() {
		// check if a human will play
		boolean canPlay=false;
		for(int player:this.humanPlayers) {
			if(this.game.getNextPlayer()==player) {
				canPlay=true;
				break;
			}
		}
		
		if(canPlay) {
			// enable only the available moves
			for(Morpx.MorpxMove move:this.cells.keySet()) {
				if(this.game.canPlay(move)) {
					this.cells.get(move).setEnabled(true);
					this.cells.get(move).setBackground(Color.GREEN);
				} else {
					this.cells.get(move).setBackground(Color.DARK_GRAY);
					this.cells.get(move).setEnabled(false);
				}
			}
		} else {
			// disable all moves
			for(JButton btn:this.cells.values()) {
				btn.setBackground(Color.DARK_GRAY);
				btn.setEnabled(false);
			}
		}
		
		this.draw();
	}
	
	public GUI(Morpx game) {
		this.game=game;
		this.humanPlayers=new HashSet<Integer>();
		
		// initialize the window and structures
		this.frame=new JFrame();
		this.panel=(JPanel) frame.add(new JPanel());
		this.cells=new HashMap<Morpx.MorpxMove, JButton>();
		this.panels=new HashMap<Pair<Integer, Integer>, JPanel>();
		
		// create the big grid
		this.panel.setLayout(new GridLayout(3, 3));
		for(int y0=0; y0<3; y0++) {
			for(int x0=0; x0<3; x0++) {
				
				// create sub grids
				JPanel subPanel=(JPanel) this.panel.add(new JPanel());
				subPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				subPanel.setLayout(new GridLayout(3, 3));
				for(int y1=0; y1<3; y1++) {
					for(int x1=0; x1<3; x1++) {
						
						// create buttons
						JButton btn=(JButton) subPanel.add(new JButton(" "));
						Morpx.MorpxMove move=new Morpx.MorpxMove(x0, y0, x1, y1);
						btn.addActionListener(evt -> {
							if(game.canPlay(move)) game.play(move);
						});
						this.cells.put(move, btn);
					}
				}
				this.panels.put(new Pair<Integer, Integer>(x0, y0), subPanel);
			}
		}
		
		// add required listeners
		this.game.onFinish(this::onFinish);
		this.game.onMove(this::onMove);
		this.game.onCellFinish(this::onCellFinish);
		this.game.onTurn(this::onTurn);
		
		// finish setting up the window
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("Morpx");
		this.frame.pack();
	}
	
	public void show() {
		this.frame.setVisible(true);
	}
	
	public void addPlayer(int player) {
		this.humanPlayers.add(player);
	}
}
