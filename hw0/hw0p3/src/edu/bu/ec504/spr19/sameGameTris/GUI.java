package edu.bu.ec504.spr19.sameGameTris;

import javax.swing.*;

/**
 * This class represents a generic encapsulation of a Graphical User Interface for your Brain.
 * It contains all the interfaces your Brain might need to make its moves.
 */
public abstract class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	GUI(String s) {
		super(s);
	}

	/**
	 * Returns the color of the circle at location [xx][yy], or NONE if the circle has been cleared
	 * @param xx must be between 0 and width
	 * @param yy must be between 0 and height
	 */
	public abstract CircleColor colorAt(int xx, int yy);

	/**
	 * @return the width of the current board
	 */
	public abstract int boardWidth();

	/**
	 * @return the height of the current board
	 */
	public abstract int boardHeight();

	/**
	 * @return true iff the game is over
	 */
	public abstract boolean gameOverQ();

	/**
	 * "Clicks" on the circle at location ({@code xx},{@code yy}).
	 *
	 * When your Brain wishes to make a move, it should call this method with the location of the
	 * circle that it wishes to select.
	 * @param xx The x-coordinate of the circle on which to click, within the grid of circles.
	 * @param yy The x-coordinate of the circle on which to click, within the grid of circles.
	 */
	public abstract void makeMove(int xx, int yy);

	/**
	 * Calculates a score for a putative same-color region of {@code regionLen} circles of color {@code clr}.
	 * Some scoring functions may assign different weights to different selection colors.
	 * @return the score achieved from clicking on a region of length {@code regionLen} of color {@code clr}.
	 * @param regionLen The length of the region that is being scored (in terms of numbers of circles).
	 * @param clr The color of the region being scored.
	 */
	public abstract int score(int regionLen, CircleColor clr);
}
