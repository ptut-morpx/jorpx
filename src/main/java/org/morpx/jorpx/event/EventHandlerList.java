package org.morpx.jorpx.event;

import java.awt.EventQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventHandlerList<T extends Listener> {
	private static class EventHandler<T extends Listener> {
		private T listener;
		private int useCount;
		
		public EventHandler(T listener, int useCount) {
			this.listener=listener;
			this.useCount=useCount;
		}
		public EventHandler(T listener) {
			this.listener=listener;
			this.useCount=-1;
		}
		
		/**
		 * Decrements the use count and checks if the handler should be removed
		 * @return whether or not to remove this handler
		 */
		protected boolean use() {
			if(this.useCount!=-1) this.useCount--;
			return this.useCount==0;
		}
	}
	
	private List<EventHandler<T>> listeners;
	
	/**
	 * Adds a new listener to be used an infinite amount of times
	 * @param l
	 */
	public void addListener(T l) {
		this.ensureList();
		this.listeners.add(new EventHandler<T>(l));
	}
	/**
	 * Adds a new listener to be used at most n times
	 * @param l
	 * @param n
	 */
	public void addListener(T l, int n) {
		this.ensureList();
		this.listeners.add(new EventHandler<T>(l, n));
	}
	/**
	 * Adds a new listener to be used at most once
	 * @param l
	 */
	public void addOnceListener(T l) {
		this.addListener(l, 1);
	}
	
	/**
	 * Ensures the internal list exists
	 */
	private void ensureList() {
		if(this.listeners==null) this.listeners=new LinkedList<EventHandler<T>>();
	}
	
	/**
	 * Sends an event, that is, invokes a function on every listener and removes all of those which are expired
	 * @param eventFunction
	 * @return
	 */
	public boolean sendEvent(Consumer<? super T> eventFunction) {
		if(this.listeners==null) return false;
		
		EventQueue.invokeLater(() -> {
			Iterator<EventHandler<T>> it=this.listeners.iterator();
			while(it.hasNext()) {
				EventHandler<T> handler=it.next();
				eventFunction.accept(handler.listener);
				if(handler.use()) it.remove();
			}
		});
		
		return this.listeners.size()!=0;
	}
}
