package edu.bu.ec504.spr19.Brain;

import edu.bu.ec504.spr19.sameGameTris.GUI;

/**
 * The Brain is the artificial intelligence that tries to come up with the
 * best possible moves for the current position.
 * 
 * It typically runs in its own thread so that it will not interfere with other processing.
 */
public abstract class Brain implements Runnable {
	// fields
	protected final GUI myGUI; // the GUI class attached to this Brain
	
	// methods
	/**
	 * A constructor that accepts a parameter representing the GUI interface on which the game is played.
	 */
	Brain(GUI myGUI) { this.myGUI = myGUI; }
	
	/**
	 * This is called when the Brain is being asked to close down (i.e., the game is over).
	 * It should clean up any data structures and/or signal threads to close, etc.
	 */
	public abstract void allDone();
	
	/**
	 * Each Brain should have a name, which is provided by this method.
	 * @return the name of the brain
	 */
	public abstract String myName();
	
	/**
	 * Starts the Brain a'thinking.
	 * This is the code for making decisions about which circles you Brain selects.
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

}
