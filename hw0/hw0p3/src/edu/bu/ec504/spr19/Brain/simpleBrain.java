package edu.bu.ec504.spr19.Brain;

import edu.bu.ec504.spr19.sameGameTris.GUI;

/**
 * A very simple Brain for the game.
 */
public class simpleBrain extends Brain {
	
	// fields
private volatile boolean allDone = false; // when set to true, the Brain should stop what it's doing and exit (at an appropriate time)

	public simpleBrain(GUI myGUI) {
		super(myGUI);
	}

	/**
	 * {@inheritDoc}
	 */
	public void allDone() {
		allDone = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String myName() {
		return "Simple Brain";
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		while (!allDone && !myGUI.gameOverQ())
			myGUI.makeMove(0, myGUI.boardHeight()-1); // i.e. click on the lower left corner
	}
}
	