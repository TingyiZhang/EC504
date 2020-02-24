package edu.bu.ec504.spr19.sameGameTris;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


/*
 * An extension of JButton that keeps track of its own location on a grid
 */
class SelfAwareCircle extends Component implements MouseListener, SelfAwareListener {
	// Constants
	private final Color hltColor = new Color(200,200,200); // the color to use for highlighting a button

	// Data fields
	public final int xx;
	public final int yy;
	public CircleColor clr;
	private static final long serialVersionUID = 1L;
	private final ArrayList<SelfAwareListener> _listeners = new ArrayList<>(); // who is listening to events fired by this circle
	private static int id=0;
	private final int myid; // the circle's ID
	private Visibility state = Visibility.plain;	// the current state of the circle
	private final sge sgeGui;                       // a link to the GUI that created this circle

	// concurrency variables
	private static final AtomicInteger regionLength = new AtomicInteger(0);

	// SUBCLASSES
	/*
	 * Records the length of the selected region
	 */
	public enum Visibility{
		/*
		 * the circle is highlighted
		 */
		highlight,
		/*
		 * the circle is plain (i.e. the default)
		 */
		plain,
		/*
		 * the circle is cleared (i.e. background color)
		 */
		clear}

	// CONSTRUCTORS

	/**
	 * Creates a circle of the specified color at the specified location
	 * @param clr The color of the new circle.
	 * @param xx The xx location of the circle in the grid of circles.
	 * @param yy The yy location of the circle in the grid of circles.
	 * @param sgeGui A reference to the game GUI in which the circle will lie.
	 */
		SelfAwareCircle(CircleColor clr, int xx, int yy, sge sgeGui) {
			// record field parameters
			this.xx = xx;
			this.yy = yy;
			this.clr = clr; // record the color of the button
			if (clr == CircleColor.NONE)    // circles with no color should be cleared
				setClear();
			this.sgeGui = sgeGui;   // record the GUI that created this button
			addMouseListener(this);        // register for mouse events
			myid = id++;
		}

		/**
		*  Creates a circle of the specified color at the specified location, based on {@link #SelfAwareCircle(CircleColor, int, int, sge).}
		*/
		SelfAwareCircle(int xx, int yy, sge mySgeGui) {
			this(CircleColor.randColor(), xx, yy, mySgeGui);
		}
		
		// METHODS
		// ... helpers

		// .. info methods and accessors/setters
		 static int getRegionLength() {
			return regionLength.get();
		}

		public int getId() {
			return myid;
		}

		 Visibility getState() {
			return state;
		}

		public CircleColor getColor() {
			return clr;
		}

	/**
	 * Resets the color of this circle to a random color.
	 */
		void resetColor() {
			clr=CircleColor.randColor();
		}

		void setState (Visibility state) {
			if (state==Visibility.clear)
				setClear();   // i.e. there is extra overhead in clearing
			else
				this.state = state;
			repaint();
		}

		boolean isCleared() {
			return this.state == Visibility.clear;
		}

		void setClear() {
			state = Visibility.clear;
			repaint();
		}

		/*
		 * Copies immutable elements of this SelfAwareCircle to "recipient"
		 *    *except* position-dependent items (e.g. _listeners, xx, yy)
		 */
		void copy(SelfAwareCircle recipient) {
			recipient.clr=clr;
			if (recipient.state == state)
				return; // i.e. nothing to do

			// special cases
			// ... copying a cleared circle to an uncleared circle
			if (state == Visibility.clear)
				recipient.setClear();   // includes removing a mouse listener

			// copy the state
			recipient.setState(state);
		}

		// ... Mouse methods
		public void mouseClicked(MouseEvent e) {}

		/*
		 * Highlights the button by changing its icon and firing off an event
		 * @param state determines whether to highlight or unhighlight the circle
		 */
		private void highlightButton(Visibility stt) {
			if (isCleared())  // don't go further with already cleared circles
				return;

			// set the state
			if (stt==Visibility.clear) // clear has its own special treatment
				setClear(); // properly clear this circle (including removal of mouse listener)
			else
				setState(stt);   // set the non-cleared state appropriately

			// deal with the region length
			regionLength.getAndIncrement(); // update the region length by the newly highlighted item

			// announce the rollover to other circles
			_fireButtonRolloverEvent(stt);
		}

		/*
		 * Mouse entered the object area:
		 * *  Change its button icon
		 * *  Broadcast the change to other buttons
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
			regionLength.set(0); // i.e. reinitialize
			highlightButton(Visibility.highlight);
		}

		public void mouseExited(MouseEvent e) {
			regionLength.set(0); // i.e. reinitialize
			highlightButton(Visibility.plain);
		}
		public void mousePressed(MouseEvent e) {
			regionLength.set(0); // i.e. reinitialize
		}
		public void mouseReleased(MouseEvent e) {
			highlightButton(Visibility.clear);

			// register the score
			sgeGui.totalPoints.setText("" +
					(Integer.parseInt(sgeGui.totalPoints.getText()) +
							sgeGui.score(SelfAwareCircle.getRegionLength(), getColor())));

			// request that the board be shifted appropriately
			sgeGui.shiftCircles();

			// update tetrising
			sgeGui.updateNumClicks();
			sgeGui.updateTetrising();

		}

		// ... event methods
		public synchronized void addSelfAwareListener(SelfAwareListener l) {
			_listeners.add(l);
		}

		public synchronized void removeSelfAwareListener(SelfAwareListener l) {
			_listeners.remove(l);
		}


		/*
		 * fires off events to highlight or unhighlight circles
		 * @param state determines whether to highlight or unhighlight circles
		 */
		private synchronized void _fireButtonRolloverEvent(SelfAwareCircle.Visibility state) {
			CircleRolloverEvent bre = new CircleRolloverEvent(this, xx, yy, clr, state);
			for (SelfAwareListener _listener : _listeners) {
				SelfAwareCircle temp = (SelfAwareCircle) _listener;
				temp.rollingOver(bre);
			}
		}

		/*
		 * What to do when receiving information from another button of a rolling over event
		 * @param e the event broadcast by the other button
		 */
		public void rollingOver(CircleRolloverEvent e) {
			if (state == e.getAction() || isCleared()) // i.e. this circle has already been processed
				return;

			if (e.getColor().equals(clr)) { // check if the color is the same as mine
				highlightButton(e.getAction());
			}
			// in all other cases, only update the text field (i.e. stop the recursion)
			sgeGui.regionPoints.setText(""+(SelfAwareCircle.getRegionLength())); // update the gui text field
		}

		// ... drawing methods
		public void paint(Graphics g) {
			// set the color, depending on whether this is being highlighted
			switch (state) {
			case highlight:
				g.setColor(hltColor); // change to highlighting color
				break;
			case plain:
				g.setColor(clr.getColor());      // the default color
				break;
			case clear:
				g.setColor(getParent().getBackground()); // i.e. set to background color
				break;
			}

			// size of component
			Dimension size = getSize();

			// draw the circle
			g.fillOval(0, 0, size.width, size.height);
		}
}
