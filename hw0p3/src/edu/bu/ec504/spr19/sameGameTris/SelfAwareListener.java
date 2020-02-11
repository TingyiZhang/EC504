package edu.bu.ec504.spr19.sameGameTris;

/*
 * Listener for events of interest to SelfAwareJButtons
 */

interface SelfAwareListener {
	/*
	 * Fired by a circle when it's been "rolled over" by the mouse
	 */
	
	void rollingOver(CircleRolloverEvent e);
}
