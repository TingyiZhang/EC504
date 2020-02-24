package edu.bu.ec504.spr19.sameGameTris;

/* An implementation of the "samegnome" game, possibly with a computer-aided solver.
   Written by Prof. Ari Trachtenberg for EC504 at Boston University.
*/


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import edu.bu.ec504.spr19.Brain.smarterBrain;
import edu.bu.ec504.spr19.highScores.highScore;
import edu.bu.ec504.spr19.Brain.Brain;
import edu.bu.ec504.spr19.Brain.lazyBrain;
import edu.bu.ec504.spr19.Brain.simpleBrain;

import static java.util.concurrent.Executors.newScheduledThreadPool;

class sge extends GUI implements ActionListener, ItemListener {

    // CONSTANTS
    static public final int defaultWidth = 15, defaultHeight = 10; // sizes in terms of numbers of circles
    static public final int defaultEmptyRows = defaultHeight / 3;  // number of empty rows on the top of the game
    static public final int defaultWindowWidth = 500, defaultWindowHeight = 300; // default window size, if run as a standalone application
    static public final int numColors = 3; // the number of colors available for circles
    static private final int displayTime = 1000; // number of milliseconds to display (highlight) a move before actually making it
    static private final String highScoreFile = ".highscores.db"; // where high scores are kept
    static private final long serialVersionUID = 1L; // required for serializability

    // FIELDS (all static - only one instance of the game should be running at a time)
    final JLabel regionPoints = new JLabel("0"); // keeps track of the number of selected points
    final JLabel totalPoints = new JLabel("0");  // keeps track of the total number of points so far
    private int width = defaultWidth, height = defaultHeight; // initial width and height of the array of circles
    private int emptyRows = defaultEmptyRows;         // number of rows on top of screen with no circles
    private SelfAwareCircle[][] circles;
    private String highScoreName = null;              // the name to use for the high score
    static final Random randGen = new Random();       // package-private random number generator used for the entire package (i.e., repeatedly seeding with the same value produces the same randomness).
	private int numClicks = 1;	                      // Must be a positive integer; counts the number of clicks on circles since the last time the board was reset.


	// ... GUI elements
    private JPanel circlePanel; // the panel containing the circles
    private JPanel textPanel;   // the panel containing scoring information
    // ... ... menu items
    private JMenuItem changeBoard;
    private JMenuItem quitGame;
    private JRadioButtonMenuItem noPlayerMenuItem, simplePlayerMenuItem, lazyPlayerMenuItem, // various automatic player options
            smarterPlayerMenuItem;
    private ReentrantLock GUIlock = new ReentrantLock(); // thread lock used to synchronize manipulation of circles on the board

    // ... Brain elements
    Brain theBrain;                   // the Brain (automated player) for the game
    Thread brainThread;               // the thread that will run the Brain

	// ... Tetris executor
    final ScheduledExecutorService tetrisExec=newScheduledThreadPool(2); // sets up tetris threads
	private ScheduledFuture<?> produceCircleFuture=null; // schedule for producing circles for the app
	private ScheduledFuture<?> dropCircleFuture=null;    // schedule for dropping circles for the app


	// SUBCLASSES

    /**
     * Drops a new circle at the top of the board every time it is running.
     * If this is not possible, then the gameOverSignal is set to true
     */
    private class initDropCircles implements Runnable {

		@Override
		public void run() {
			GUIlock.lock(); // lock circle manipulations down

			try {
				int randColumn = randGen.nextInt(width);
				SelfAwareCircle myCircle = circles[randColumn][0];
				if (myCircle.getState() != SelfAwareCircle.Visibility.clear) { // i.e. the spot is already occupied
					doGameOver("overran column "+randColumn);
				}
				else {
					myCircle.resetColor();
					myCircle.setState(SelfAwareCircle.Visibility.plain);
				}
			} finally {
				GUIlock.unlock();
			}

			repaint();
		}
	}

	/**
	 * Moves circles down until they are connected to the main collection of circles.
	 */
	private class moveDropCircles implements Runnable {

		@Override
		public void run() {
			GUIlock.lock(); // lock circle manipulations down

			try {
				// hunt for a circle that is above a clear circle
				for (int xx = 0; xx < width; xx++)
					for (int yy = height - 2; yy >= 0; yy--) {  // go down to up, so as not to move circles more than once
						SelfAwareCircle orig = circles[xx][yy],
								dest = circles[xx][yy + 1];
						if (orig.getState() != SelfAwareCircle.Visibility.clear &&        // a colored circle
								dest.getState() == SelfAwareCircle.Visibility.clear) {    // above a cleared circle
							// move it down
							moveCircle(orig, dest);
						}
					}
			}
			finally {
				GUIlock.unlock();
			}
			repaint();
		}
	}

    // PUBLIC ACCESS METHODS

    /*
     * Default no-args constructor
     */
    public sge() {
        super("Ari's samegame");
        try {
            SwingUtilities.invokeAndWait(
                    this::setupGUI
            );
        } catch (Exception e) {
            System.out.println("Saw exception " + e);
        }
    }

    /*
     * Returns the color of the circle at location [xx][yy], or NONE if the circle has been cleared
     * @param xx must be between 0 and width
     * @param yy must be between 0 and height
     */
    public CircleColor colorAt(int xx, int yy) {
        if (circles[xx][yy].isCleared())
            return CircleColor.NONE;
        else
            return circles[xx][yy].clr;
    }

    /*
     * Returns the width of the current board
     */
    public int boardWidth() {
        return width;
    }

    /*
     * Returns the height of the current board
     */
    public int boardHeight() {
        return height;
    }

    /*
     * Returns true iff the game is over
     * (i.e. every circle is surrounded by cleared circles or circles of a different color)
     */
    public boolean gameOverQ() {
    	for (int xx = 0; xx < width; xx++)
            for (int yy = 0; yy < height; yy++) {
                if (!circles[xx][yy].isCleared()) {
                    CircleColor myColor = circles[xx][yy].getColor();
                    // check its neighbors
                    if (sameColor(myColor, xx - 1, yy) ||
                            sameColor(myColor, xx + 1, yy) ||
                            sameColor(myColor, xx, yy - 1) ||
                            sameColor(myColor, xx, yy + 1))
                        return false; // the game has not ended
                }
            }

        return true; // there are no viable moves
    }

    /*
     * "Clicks" on the circle at location (xx,yy)
     */
    public void makeMove(int xx, int yy) {

        circles[xx][yy].mouseEntered(null);  // pretend the mouse was pressed at location (xx,yy)
        try {
            Thread.sleep(displayTime);          // wait a bit for the user to register the move
        } catch (InterruptedException ignored) {
        }
        circles[xx][yy].mouseExited(null);
        circles[xx][yy].mousePressed(null);
        circles[xx][yy].mouseReleased(null); // pretend that the mouse button was released at the location
    }

    /**
     * @return the score achieved from clicking on a region of length <b>level</b>
     */
    final public int score(int level, CircleColor clr) {
        if (level == 1)
            return 1;
        else
            return level * level;
    }

    // OTHER METHODS

    /*
     * setupGUI helper
     *   - adds to circle[ii][jj] a SelfAwareListener of circle[xx][yy] as 0<=xx<width and 0<=yy<height
     * @param ii = x coordinate of the circle to whom we will be listening
     * @param jj = y coordinate of the circle to whom we will be listening
     * @param xx = x coordinate of the listener circle
     * @param yy = y coordinate of the listener circle
     */

    private void addNeighbor(int ii, int jj, int xx, int yy) {
        if ((0 <= xx && xx < width) &&
                (0 <= yy && yy < height))
            circles[ii][jj].addSelfAwareListener(circles[xx][yy]);
    }

    /**
     * Setup the initial screen items
     * NOTE:  must perform cleanUp() first if there is already a GUI set up on the screen
     */
    void setupGUI() {

        // SET LAYOUT STYLE
        setLayout(new BorderLayout());

        // SET UP MENUS
        JMenuBar menuBar = new JMenuBar();

        // ... File Menu
        JMenu menu = new JMenu("File");

        changeBoard = new JMenuItem("Change board");
        changeBoard.addActionListener(this);
        menu.add(changeBoard);

        quitGame = new JMenuItem("Quit");
        quitGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_MASK));
        quitGame.setMnemonic('Q');
        quitGame.addActionListener(this);
        menu.add(quitGame);

        menuBar.add(menu);

        // ... Automatic play Menu
        menu = new JMenu("Automatic play");
        ButtonGroup group = new ButtonGroup();

        noPlayerMenuItem = new JRadioButtonMenuItem("none");
        noPlayerMenuItem.addItemListener(this);
        noPlayerMenuItem.setSelected(true); // i.e. the default item
        group.add(noPlayerMenuItem);
        menu.add(noPlayerMenuItem);
        menu.addSeparator();

        simplePlayerMenuItem = new JRadioButtonMenuItem("Simple player");
        simplePlayerMenuItem.addItemListener(this);
        group.add(simplePlayerMenuItem);
        menu.add(simplePlayerMenuItem);

        lazyPlayerMenuItem = new JRadioButtonMenuItem("Lazy player");
        lazyPlayerMenuItem.addItemListener(this);
        group.add(lazyPlayerMenuItem);
        menu.add(lazyPlayerMenuItem);

        smarterPlayerMenuItem = new JRadioButtonMenuItem("Smarter player");
        smarterPlayerMenuItem.addItemListener(this);
        group.add(smarterPlayerMenuItem);
        menu.add(smarterPlayerMenuItem);
        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        // SET UP CIRCLE PANEL
        circlePanel = new JPanel(new GridLayout(height, width));
        // ... allocate circles
        circles = new SelfAwareCircle[width][height];

        // ..... set up some circles
        for (int jj = 0; jj < height; jj++)
            for (int ii = 0; ii < width; ii++) {
                // establish the button
                if (jj<=emptyRows)
					circles[ii][jj] = new SelfAwareCircle(CircleColor.NONE, ii, jj, this); // the top emptyRows should have a circle with no color
				else
					circles[ii][jj] = new SelfAwareCircle(ii, jj, this);
                circlePanel.add(circles[ii][jj]);
            }
        // ... set up listeners in the area
        for (int xx = 0; xx < width; xx++)
            for (int yy = 0; yy < height; yy++) {
                // add all neighbors
                addNeighbor(xx, yy, xx, yy - 1);
                addNeighbor(xx, yy, xx, yy + 1);
                addNeighbor(xx, yy, xx - 1, yy);
                addNeighbor(xx, yy, xx + 1, yy);
            }

        add(circlePanel, BorderLayout.CENTER);

        // ... set up the text panel
        textPanel = new JPanel(new FlowLayout());
        textPanel.add(new JLabel("Selected len: "));
        textPanel.add(regionPoints);
        textPanel.add(new JLabel("")); // i.e. blank
        textPanel.add(new JLabel("Total pts: "));
        textPanel.add(totalPoints);
        add(textPanel, BorderLayout.SOUTH);

        // paint the display
        validate();
        repaint();
    }

	/**
	 * Increment the number of clicks seen.
     * If this ends up being greater than the size of the board, the game is declared over.
	 */
	public void updateNumClicks() {
    	numClicks++;
    	if (numClicks>width*height)
    	    doGameOver("too many clicks - "+numClicks);
	}


	/**
	 * Cancel schedules for adding / dropping circles.
	 */
	private void cancelTetrisingSchedules() {
		if (produceCircleFuture!=null)
			produceCircleFuture.cancel(true);
		if (dropCircleFuture!=null)
			dropCircleFuture.cancel(true);
	}

    /**
     * Sets up threads that drop circles onto the board and move them down over time.
	 * As {@link #numClicks} gets larger, circles come more often and drop faster.
     */
    public void updateTetrising() {
    	// cancel the old schedules
		cancelTetrisingSchedules();

    	// start up the new ones
		produceCircleFuture = tetrisExec.scheduleAtFixedRate(new initDropCircles(), 0, 5000000 / numClicks, TimeUnit.MICROSECONDS);
		dropCircleFuture = tetrisExec.scheduleAtFixedRate(new moveDropCircles(), 0, 2000000 / numClicks, TimeUnit.MICROSECONDS);
    }

	/*
     * Performs any clean up actions needed before setting up a new GUI
     */
    void cleanUp() {
        // Removes elements for garbage collection

        // close down the brain
        highScoreName = null;
        if (theBrain != null)
            theBrain.allDone();

        // score panel
        totalPoints.setText("0");
        textPanel.setVisible(false);
        textPanel = null;

        // circles
        for (int ii = 0; ii < width; ii++)
            for (int jj = 0; jj < height; jj++) {
                circles[ii][jj].removeMouseListener(circles[ii][jj]);
                circles[ii][jj] = null;
            }

        // circle panel
        circlePanel.setVisible(false);
        circlePanel = null;

        // cancel tetrising schedules
		numClicks=1;  // reset the number of circle clicks in the system
		cancelTetrisingSchedules();
    }

    /*
     * Returns true iff circles[xx][yy] has color theColor, is not cleared, AND (xx,yy) is within the range of the board
     */
    private boolean sameColor(CircleColor theColor, int xx, int yy) {
        if (xx < 0 || yy < 0 || xx >= width || yy >= height || circles[xx][yy].isCleared())
            return false;
        else
            return circles[xx][yy].getColor().equals(theColor);
    }

    /*
     * Moves circle c1 into location c2, leaving c1 as a clear circle that
     * does not receive mouse events
     */
    synchronized private void moveCircle(SelfAwareCircle orig, SelfAwareCircle dest) {
        // copy the immutable, position independent values
        orig.copy(dest);

        // clear the top item
        orig.setClear();
    }

    /*
     * Called to request a reshifting of the board (as necessary).
     *    This should happen if some circles are rendered "clear"ed
     */

    final void shiftCircles() {
        /* start at the bottom and move up ... all cleared circles are
         *    removed, with upper circles falling into their positions;
         *    if a column is totally empty, then its rightmost columns
         *    shift into it
         */

        GUIlock.lock(); // acquire a lock

		try {
			// 1.  SHIFT VERTICALLY
			for (int xx = 0; xx < width; xx++) {
				int firstClr = height - 1;  // the lowest cleared entry in the column
				int firstFull = height - 1; // the lowest uncleared entry in the column that has not yet been processed
				boolean moveOn = false;      // set to true in order to move on to the next column
				while (!moveOn) {
					// find the lowest clear entry in the column (if it exists)
					try {
						while (!circles[xx][firstClr].isCleared())
							firstClr--;
					} catch (ArrayIndexOutOfBoundsException e) {
						moveOn = true;
						continue;  // i.e. no cleared circle found in this column --- go to the next column
					}

					if (firstFull > firstClr)
						firstFull = firstClr; // only move items "down" the column

					// find the lowest non-cleared entry in the column (if it exists)
					try {
						while (circles[xx][firstFull].isCleared())
							firstFull--;
					} catch (ArrayIndexOutOfBoundsException e) {
						moveOn = true;
						continue;  // i.e. the whole column is clear --- for now, go to the next column
					}

					moveCircle(circles[xx][firstFull], circles[xx][firstClr]);

					firstFull--;
					firstClr--; // iterate
				}
			}

			// 2.  SHIFT HORIZONTALLY
			// Check to see if any column is now empty
			// ... this could have been done within the loop above, but it would detract from readability of the code
			boolean emptySoFar = true;       // remains true if all columns seen so far have only cleared circles
			for (int xx = width - 1; xx >= 0; xx--) {
				boolean allCleared = true;   // remains true if all circles in column xx have been cleared
				for (int yy = 0; yy < height; yy++)
					if (!circles[xx][yy].isCleared()) {
						allCleared = false;
						break;
					}

				if (allCleared) {
					if (!emptySoFar) { // i.e. do not do anything with empty columns on the right of the screen
						// move other columns into this empty column
						for (int ii = xx + 1; ii < width; ii++)
							for (int jj = 0; jj < height; jj++)
								moveCircle(circles[ii][jj], circles[ii - 1][jj]);
					}
				} else
					emptySoFar = false;
			}

			// 3.  (LAST ITEM) CHECK IF THE GAME HAS ENDED
			// This happens if every circle is surrounded by cleared circles or circles of a different color
			if (gameOverQ())
				doGameOver("exhausted all circles"); // all done
		}
		finally {
			GUIlock.unlock(); // release the circles for other processes
		}
    }

    /**
     * The game is over - report to the user and/or save a high score, if relevant.
     * @param feedback Some string to present to the user; typically the reason that the game is over.
     */
    private void doGameOver(String feedback) {
        cancelTetrisingSchedules(); // cancel tetrising threads

		int score = new Integer(totalPoints.getText());
		highScore hs = new highScore(highScoreFile);

		// check the high score
		if (hs.newRecordQ(score)) { // i.e. a new record
			JOptionPane.showMessageDialog(null, "Game Over ("+feedback+") - You got a new high score of " + score + " points!\n" +
					"Your weighted score for this sized board was " + highScore.hsComp.weightedScore(new highScore.HSdata("", width, height, score)) + ".");
			if (highScoreName == null)
				highScoreName = JOptionPane.showInputDialog(null, "You got a new high score!\nPlease enter your name:");

			if (highScoreName != null) { // i.e. the user is  interested in high scores
                // populate the high score item
                highScore.HSdata datum = new highScore.HSdata(highScoreName, width, height, score);
                hs.putScore(datum); // enter the name into the high score list
            }
		} else
			JOptionPane.showMessageDialog(null, "Game Over ("+feedback+") - You did not make the high score.  You had " + score + " points.\n" +
					"Your weighted score for this sized board was " + highScore.hsComp.weightedScore(new highScore.HSdata("", width, height, score)) + ".");


		JOptionPane.showMessageDialog(null, "Current high scores:\n" + hs.display());

		// i.e. time for a new game
		cleanUp();
		setupGUI();
	}

	public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == quitGame)
            System.exit(0);
        else if (source == changeBoard) {
            // modified from http://www.vbforums.com/showthread.php?t=513699
            JTextField width = new JTextField();
            width.setText("" + defaultWidth);
            JTextField height = new JTextField();
            height.setText("" + defaultHeight);
            Object[] msg = {"Width:", width, "Height:", height};

            JOptionPane op = new JOptionPane(
                    msg,
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.OK_CANCEL_OPTION,
                    null,
                    null);

            JDialog dialog = op.createDialog(this, "Enter new board size ...");
            dialog.setVisible(true);

            int result = JOptionPane.OK_OPTION;

            try {
                result = (Integer) op.getValue();
            } catch (Exception ignored) {
            }

            if (result == JOptionPane.OK_OPTION) // i.e. effect the change
            {
                // i.e. destroy the old board
                cleanUp();
                this.width = new Integer(width.getText());
                this.height = new Integer(height.getText());

                // create the new board
                setupGUI();
            }
        }
    }

    /**
     * Starts a Brain that makes moves
     */
    private void startBrain(Brain theBrain) {
        this.theBrain = theBrain;
        highScoreName = theBrain.myName(); // the computer gets credit for any subsequent high score
        brainThread = new Thread(theBrain, "Brain Thread");
        brainThread.start();
    }

    // For handling the menu checkBox
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (source == noPlayerMenuItem)
                return; // i.e. no players
            else if (source == simplePlayerMenuItem)
                startBrain(new simpleBrain(this));
            else if (source == lazyPlayerMenuItem)
                startBrain(new lazyBrain(this));
            else if (source == smarterPlayerMenuItem)
                startBrain(new smarterBrain( this));
        } else {
            // deselected
            if (theBrain != null)
                theBrain.allDone(); // will ask the Brain to stop thinking
        }
    }


    // MAIN METHOD

    /**
     * Runs the application
     */
    public static void main(String[] args) {
        JFrame myApp = new sge();
        myApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myApp.setSize(defaultWindowWidth, defaultWindowHeight);
        myApp.setVisible(true);
    }
}