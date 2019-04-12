package org.morpx.jorpx.game;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

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
	
	public GUI(Morpx game) {
		this.game=game;
		this.frame=new JFrame();
		this.panel=(JPanel) frame.add(new JPanel());
		this.cells=new HashMap<Morpx.MorpxMove, JButton>();
		this.panels=new HashMap<Pair<Integer, Integer>, JPanel>();
		
		this.panel.setLayout(new GridLayout(3, 3));
		for(int y0=0; y0<3; y0++) {
			for(int x0=0; x0<3; x0++) {
				JPanel subPanel=(JPanel) this.panel.add(new JPanel());
				subPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
				subPanel.setLayout(new GridLayout(3, 3));
				for(int y1=0; y1<3; y1++) {
					for(int x1=0; x1<3; x1++) {
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
		
		this.game.onFinish(player -> {
			this.frame.setTitle("Game ended");
			if(player==1) this.frame.setTitle("P1 won!");
			if(player==-1) this.frame.setTitle("P2 lose!");
			
			for(JButton btn:this.cells.values()) btn.setEnabled(false);
		});
		this.game.onMove((move, player) -> {
			this.cells.get(move).setText(player==1?"X":"O");
			this.cells.get(move).setEnabled(false);
			this.frame.pack();
		});
		this.game.onCellFinish((x, y, player) -> {
			this.panels.get(new Pair<Integer, Integer>(x, y)).setEnabled(false);
		});
		this.game.onTurn(() -> {
			for(Morpx.MorpxMove move:this.cells.keySet()) {
				if(this.game.canPlay(move)) {
					this.cells.get(move).setEnabled(true);
					this.cells.get(move).setBackground(Color.GREEN);
				} else {
					this.cells.get(move).setBackground(Color.DARK_GRAY);
					this.cells.get(move).setEnabled(false);
				}
			}
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
		});
		
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setTitle("Morpx");
		this.frame.pack();
	}
	
	public void show() {
		this.frame.setVisible(true);
	}
}
