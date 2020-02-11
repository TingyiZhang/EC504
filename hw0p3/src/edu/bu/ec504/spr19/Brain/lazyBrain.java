package edu.bu.ec504.spr19.Brain;

import java.util.LinkedList;
import java.util.Objects;

import edu.bu.ec504.spr19.sameGameTris.GUI;
import edu.bu.ec504.spr19.sameGameTris.CircleColor;

/**
 * Does whatever a lazy brain does ...
 */
public class lazyBrain extends Brain {
	// fields
	private volatile boolean allDone = false; // when set to true, the Brain should stop what it's doing and exit (at an appropriate time)
	private board currState;

	/** Instantiates a Brain linked to a specified myGUI
	 * @param myGUI The GUI that will be instantiating the Brain
	 */
	public lazyBrain(GUI myGUI) {
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
		return "Greedy Brain";
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		// Initialize and set up the board with the current position
		currState = new board();
		for (int xx=0; xx<myGUI.boardWidth(); xx++)
			for (int yy=0; yy<myGUI.boardHeight(); yy++)
				currState.modify(xx, yy, myGUI.colorAt(xx, myGUI.boardHeight()-yy-1));

		while (!allDone && !myGUI.gameOverQ()) {
				pos nextMove = chooseMove();
				myGUI.makeMove(nextMove.xx, nextMove.yy); // i.e. click on the lower left corner
			}
	}

	// internal classes

	/**
	 * Stores an (xx,yy) coordinate
	 */
	static class pos {
		final int xx;
		int yy;
		pos(int xx, int yy) {this.xx=xx; this.yy=yy;}
	}

	/**
	 * Stores a board set up
	 */
	private class board {
		final LinkedList< LinkedList <CircleColor>> data;
		final private int width, height;

		// constructs a board of specified width and height
		board(int width, int height) {
			this.width = width; this.height = height;

			// allocate the data structure
			data = new LinkedList<>();

			// set up the data structure
			for (int ii=0; ii<width; ii++) {
				LinkedList<CircleColor> temp = new LinkedList<>();
				for (int jj=0; jj<height; jj++)
					temp.add(CircleColor.NONE);
				data.add(temp);
			}		
		}
		/**
		 * default constructor
		 */
		board() {
			this(myGUI.boardWidth(),myGUI.boardHeight());
		}

		/**
		 * copy constructor
		 * @param basis the board to copy
		 */
		board(board basis) {
			// allocate space
			this(basis.width, basis.height);

			// copy over all the specific items
			for (int xx=0; xx<columns(); xx++)
				for (int yy=0; yy<rows(xx); yy++)
					modify(xx,yy,basis.get(xx, yy));
		}

		/**
		 * @return the color of the yy'th item in the xx'th column, or CircleColor.NONE
		 * 	if the cell is not in the <b>data</b> container
		 */
		private CircleColor get(int xx, int yy) {
			try {
				return data.get(xx).get(yy);
			} catch (IndexOutOfBoundsException e) {
				return CircleColor.NONE;
			}
		}

		/**
		 * @return the xx'th column, or null if the xx'th column does not exist in the <b>data</b> container
		 */
		private LinkedList<CircleColor> get(int xx) {
			try {
				return data.get(xx);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
		}
		/**
		 * Changes yy'th item in xx'th column
		 * %R:  The circle at this location must still be in the <b>data</b> field
		 */
		private void modify(int xx, int yy, CircleColor col) {
			data.get(xx).set(yy, col);
		}

		/**
		 * Deletes yy'th item in xx'th column
		 * %R:  The circle at this location must still be in the <b>data</b> field
		 */
		private void delete(int xx, int yy) {
			data.get(xx).remove(yy);
		}

		/**
		 * Deletes the column at (horizontal) board location xx
		 *  * %R:  The column at this location must still be in the <b>data</b> field
		 */
		private void delete(int xx) {
			data.remove(xx);
		}

		// public accessors
		/**
		 * Simulates a click on the yy'th cell of the xx'th column
		 * @return the number of cells deleted
		 * @modifies Deletes cells in the same region as cell (xx,yy)
		 * @expects The clicked cell is assumed not to be empty
		 */
		int clickNode(int xx, int yy) {
			CircleColor myColor = get(xx,yy); // the color to match

			// mark the region
			int count = clickNodeHelper(xx, yy, myColor);

			// clean up
			// ... delete all cells with no color
			// ... delete from the end to the front so that there are no problems with re-indexing
			for (int ii=data.size()-1; ii>=0; ii--) {
				for (int jj = Objects.requireNonNull(get(ii)).size()-1; jj>=0; jj--)
					if (get(ii,jj)==CircleColor.NONE)
						delete(ii, jj);

				// ... delete the column if it is empty
				if (Objects.requireNonNull(get(ii)).size()==0)
					delete(ii);
			}

			return count;
		}

		/**
		 * Recursive procedure for propagating a click at a location with color "col".
		 *   All items in the same region as the clicked cell are made to have CircleColor.NONE
		 *   @modifies the color of some cells, but no cells are actually deleted
		 *   @return the number of cells changed to CircleColor.NONE in this call (and its recursive subcalls)
		 */
		private int clickNodeHelper(int xx, int yy, CircleColor col) {
			if (get(xx,yy) == CircleColor.NONE || // we've either already seen this, or we've hit an empty space
					get(xx,yy) != col)            // this is not the color we want
				return 0; 

			// modify the current cell
			modify(xx,yy,CircleColor.NONE);

			return 1 + // 1 is for the current cell
			clickNodeHelper(xx-1, yy, col) + // cell to the left
			clickNodeHelper(xx+1, yy, col) + // cell to the right
			clickNodeHelper(xx, yy-1, col) + // cell below
			clickNodeHelper(xx, yy+1, col) // cell above
			;
		}

		/**
		 * @return the number of columns in the current state
		 */
		int columns() {
			return data.size();
		}

		/**
		 * 
		 * @param xx the column being considered (assumed to exist)
		 * @return the number of rows in column <b>xx</b>
		 */
		int rows(int xx) {
			return data.get(xx).size();
		}

		/**
		 * @return a "pretty-printed" version of the data structure
		 */
		public String display() {
			String temp = data.toString();
			return temp.replace("], [", "]\n[");
		}
	}

	// internal methods
	/**
	 * Chooses the next move to make.
	 * @return the move chosen, in GUI coordinates
	 */
	private pos chooseMove() {
		// greedy choice
		int max=0;                  // the maximum number of points for the best position found
		pos bestPos = new pos(0, 0); // the best position found
		board currStateCopy = new board(currState);

		for (int xx=0; xx<currState.columns(); xx++)
			for (int yy=0; yy<currState.rows(xx); yy++) {
				if (currStateCopy.get(xx,yy)!= CircleColor.NONE) {
					board test = new board(currStateCopy);
					currStateCopy.clickNodeHelper(xx, yy, test.get(xx,yy)); // mark all other nodes in the region as "clear" (but does not delete anything)
					int count = test.clickNode(xx, yy); // try removing the region to see what is left over
					if (count > max) {
						// record a new best move
						max = count;
						bestPos = new pos(xx, yy);
					}
				}
			}

		// register the selected move on the board
		currState.clickNode(bestPos.xx, bestPos.yy);
		// convert bestPos to GUI coordinates
		bestPos.yy = myGUI.boardHeight() - 1 - bestPos.yy;

		// return the result to the GUI
		return bestPos;
	}
}