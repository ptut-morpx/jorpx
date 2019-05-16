package org.morpx.jorpx.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.morpx.jorpx.event.EventHandlerList;
import org.morpx.jorpx.event.Int3Listener;
import org.morpx.jorpx.event.IntListener;
import org.morpx.jorpx.event.TIntListener;
import org.morpx.jorpx.event.VoidListener;

public class Morpx {
	
	private static final class Board {
		private int[] board;
		private int[] metaBoard;
		
		public Board() {
			this.board=new int[81];
			this.metaBoard=new int[9];
		}
		private Board(Board obj) {
			this.board=Arrays.copyOf(obj.board, 81);
			this.metaBoard=Arrays.copyOf(obj.metaBoard, 9);
		}
		
		public int get(int x0, int y0, int x1, int y1) {
			return this.board[x0+3*y0+9*x1+27*y1];
		}
		public int get(MorpxMove move) {
			return this.board[move.x0+3*move.y0+9*move.x1+27*move.y1];
		}
		public void set(int x0, int y0, int x1, int y1, int value) {
			this.board[x0+3*y0+9*x1+27*y1]=value;
		}
		public boolean isFull() {
			for(int i=0; i<9; i++) {
				if(this.metaBoard[i]==0) return false;
			}
			return true;
		}
		
		public int getMeta(int x, int y) {
			return this.metaBoard[x+3*y];
		}
		public void setMeta(int x, int y, int value) {
			this.metaBoard[x+3*y]=value;
		}
		public boolean isMetaFull(int x, int y) {
			for(int i=0; i<9; i++) {
				if(this.board[x+3*y+9*i]==0) return false;
			}
			return true;
		}
		
		@Override
		public Board clone() {
			return new Board(this);
		}
	}
	
	public static final class MorpxMove {
		protected int x0, y0, x1, y1;
		
		public MorpxMove(int x0, int y0, int x1, int y1) {
			this.x0=x0;
			this.y0=y0;
			this.x1=x1;
			this.y1=y1;
		}
		
		public int getX0() {
			return this.x0;
		}
		public int getY0() {
			return this.y0;
		}
		public int getX1() {
			return this.x1;
		}
		public int getY1() {
			return this.y1;
		}
		
		@Override
		public boolean equals(Object other) {
			if(!(other instanceof MorpxMove)) return false;
			if(other==this) return true;
			
			MorpxMove mm=(MorpxMove) other;
			return this.x0==mm.x0 && this.y0==mm.y0 && this.x1==mm.x1 && this.y1==mm.y1;
		}
		@Override
		public int hashCode() {
			return this.x0+3*this.y0+9*this.x1+27*this.y1;
		}
	}
	
	private Board board;
	private int nextPlayer;
	private boolean finished;
	private int winner;
	private int estimatedScore;
	private int lastX, lastY;
	
	public Morpx() {
		this.board=new Board();
		this.nextPlayer=1;
		this.estimatedScore=Integer.MIN_VALUE;
		this.lastX=-1;
		this.lastY=-1;
	}
	private Morpx(Morpx obj) {
		this.board=obj.board.clone();
		this.nextPlayer=obj.nextPlayer;
		this.finished=obj.finished;
		this.winner=obj.winner;
		this.estimatedScore=obj.estimatedScore;
		this.lastX=obj.lastX;
		this.lastY=obj.lastY;
	}
	
	public int getNextPlayer() {
		return this.nextPlayer;
	}
	
	public boolean isFinished() {
		return this.finished;
	}
	public int getWinner() {
		return this.winner;
	}
	public int getEstimatedScore() {
		if(this.estimatedScore==Integer.MIN_VALUE) this.estimateScore();
		return this.estimatedScore;
	}
	
	private boolean canPlayGrid(int x, int y) {
		if(this.board.getMeta(x, y)!=0) return false;
		if(this.lastX==-1) return true;
		return (x==this.lastX && y==this.lastY) || (this.board.getMeta(this.lastX, this.lastY)!=0);
	}
	
	public boolean canPlay(MorpxMove mm) {
		if(this.finished) return false;
		
		return (this.canPlayGrid(mm.x0, mm.y0) && this.board.get(mm)==0);
	}
	public List<MorpxMove> getPossibleMoves() {
		if(this.finished) return Collections.emptyList();
		
		List<MorpxMove> moves=new LinkedList<MorpxMove>();
		for(int x0=0; x0<3; x0++) for(int y0=0; y0<3; y0++) {
			if(this.canPlayGrid(x0, y0)) {
				for(int x1=0; x1<3; x1++) for(int y1=0; y1<3; y1++) {
					if(this.board.get(x0, y0, x1, y1)==0) moves.add(new MorpxMove(x0, y0, x1, y1));
				}
			}
		}
		return moves;
	}
	
	public void play(MorpxMove mm) {
		// invalidate the estimated score
		this.estimatedScore=Integer.MIN_VALUE;
		
		// play the actual move
		int x0=mm.x0, y0=mm.y0, x1=mm.x1, y1=mm.y1;
		this.board.set(x0, y0, x1, y1, this.nextPlayer);
		this.lastX=x1;
		this.lastY=y1;
		this.sendMoveEvent(mm, this.nextPlayer);
		
		// update the meta board
		boolean updatedMeta=false;
		boolean updatedStatus=false;
		if(
			(
				this.board.get(x0, y0, x1, 0)==this.nextPlayer &&
				this.board.get(x0, y0, x1, 1)==this.nextPlayer &&
				this.board.get(x0, y0, x1, 2)==this.nextPlayer
			) || (
				this.board.get(x0, y0, 0, y1)==this.nextPlayer &&
				this.board.get(x0, y0, 1, y1)==this.nextPlayer &&
				this.board.get(x0, y0, 2, y1)==this.nextPlayer
			) || (
				x1==y1 && (
					this.board.get(x0, y0, 0, 0)==this.nextPlayer &&
					this.board.get(x0, y0, 1, 1)==this.nextPlayer &&
					this.board.get(x0, y0, 2, 2)==this.nextPlayer
				)
			) || (
				y1==2-x1 && (
					this.board.get(x0, y0, 2, 0)==this.nextPlayer &&
					this.board.get(x0, y0, 1, 1)==this.nextPlayer &&
					this.board.get(x0, y0, 0, 2)==this.nextPlayer
				)
			)
		) {
			this.board.setMeta(x0, y0, this.nextPlayer);
			updatedMeta=true;
			this.sendCellFinishEvent(x0, y0, this.nextPlayer);
			
			// update global state
			if(
				(
					this.board.getMeta(x0, 0)==this.nextPlayer &&
					this.board.getMeta(x0, 1)==this.nextPlayer &&
					this.board.getMeta(x0, 2)==this.nextPlayer
				) || (
					this.board.getMeta(0, y0)==this.nextPlayer &&
					this.board.getMeta(1, y0)==this.nextPlayer &&
					this.board.getMeta(2, y0)==this.nextPlayer
				) || (
					x0==y0 && (
						this.board.getMeta(0, 0)==this.nextPlayer &&
						this.board.getMeta(1, 1)==this.nextPlayer &&
						this.board.getMeta(2, 2)==this.nextPlayer
					)
				) || (
					y0==2-x0 && (
						this.board.getMeta(2, 0)==this.nextPlayer &&
						this.board.getMeta(1, 1)==this.nextPlayer &&
						this.board.getMeta(0, 2)==this.nextPlayer
					)
				)
			) {
				this.finished=true;
				this.winner=this.nextPlayer;
				updatedStatus=true;
				this.sendFinishEvent(this.nextPlayer);
			}
		}
		
		// check for full grid
		if(!updatedMeta) {
			if(this.board.isMetaFull(x0, y0)) {
				updatedMeta=true;
				this.sendCellFinishEvent(x0, y0, 0);
			}
		}
		
		// check for full board
		if(updatedMeta && !updatedStatus) {
			if(this.board.isFull()) {
				this.finished=true;
				this.sendFinishEvent(0);
			}
		}
		
		// end the turn
		this.nextPlayer=-this.nextPlayer;
		if(!this.finished) this.sendTurnEvent();
	}
	
	public void start() {
		this.sendTurnEvent();
	}
	
	public int getCell(int x0, int y0, int x1, int y1) {
		return this.board.get(x0, y0, x1, y1);
	}
	public int getMetaCell(int x, int y) {
		return this.board.getMeta(x, y);
	}
	
	@Override
	public Morpx clone() {
		return new Morpx(this);
	}
	
	/**
	 * Event: turn
	 * When: a turn is finished
	 */
	private EventHandlerList<VoidListener> turnListeners=new EventHandlerList<VoidListener>();
	/**
	 * Event: finish
	 * When: the game is finished
	 * Args:
	 * 	int: the winning player, if any
	 */
	private EventHandlerList<IntListener> finishListeners=new EventHandlerList<IntListener>();
	/**
	 * Event: cellFinish
	 * When: a meta cell is finished
	 * Args:
	 * 	int: the x coordinate of this cell
	 * 	int: the y coordinate of this cell
	 * 	int: the player who took the cell, if any
	 */
	private EventHandlerList<Int3Listener> cellFinishListeners=new EventHandlerList<Int3Listener>();
	/**
	 * Event: move
	 * When: a move was player
	 * Args:
	 * 	MorpxMove: the move itself
	 * 	int: the player who made the move
	 */
	private EventHandlerList<TIntListener<MorpxMove>> moveListeners=new EventHandlerList<TIntListener<MorpxMove>>();
	
	public void onTurn(VoidListener listener) {
		this.turnListeners.addListener(listener);
	}
	public void onTurn(VoidListener listener, int count) {
		this.turnListeners.addListener(listener, count);
	}
	public void onceTurn(VoidListener listener) {
		this.turnListeners.addOnceListener(listener);
	}
	
	public void onFinish(IntListener listener) {
		this.finishListeners.addListener(listener);
	}
	
	public void onCellFinish(Int3Listener listener) {
		this.cellFinishListeners.addListener(listener);
	}
	public void onCellFinish(Int3Listener listener, int count) {
		this.cellFinishListeners.addListener(listener, count);
	}
	public void onceCellFinish(Int3Listener listener) {
		this.cellFinishListeners.addOnceListener(listener);
	}
	
	public void onMove(TIntListener<MorpxMove> listener) {
		this.moveListeners.addListener(listener);
	}
	public void onMove(TIntListener<MorpxMove> listener, int count) {
		this.moveListeners.addListener(listener, count);
	}
	public void onceMove(TIntListener<MorpxMove> listener) {
		this.moveListeners.addOnceListener(listener);
	}
	
	private void sendTurnEvent() {
		this.turnListeners.sendEvent(l -> l.onVoid());
	}
	private void sendFinishEvent(int player) {
		this.finishListeners.sendEvent(l -> l.onInt(player));
	}
	private void sendCellFinishEvent(int x, int y, int player) {
		this.cellFinishListeners.sendEvent(l -> l.onInt3(x, y, player));
	}
	private void sendMoveEvent(MorpxMove move, int player) {
		this.moveListeners.sendEvent(l -> l.onTInt(move, player));
	}
	
	private void estimateScore() {
		this.estimatedScore=0;
	}
}
