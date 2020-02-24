// Copyright 2020 Tingyi Zhang tingyi97@bu.edu
// Inherited from lazyBrain.java by prof. Ari Trachtenberg
package edu.bu.ec504.spr19.Brain;

import edu.bu.ec504.spr19.sameGameTris.CircleColor;
import edu.bu.ec504.spr19.sameGameTris.GUI;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * A stupid but hardworking brain!
 */
public class smarterBrain extends Brain {
    // fields
    private volatile boolean allDone = false; // when set to true, the Brain should stop what it's doing and exit (at an appropriate time)
    private board currState;


    public smarterBrain(GUI myGUI) {
        super(myGUI);
    }

    public void allDone() {
        allDone = true;
    }

    public String myName() {
        return "Tingyi";
    }

    public void run() {
        while (!allDone && !myGUI.gameOverQ()) {
            currState = new board();
            for (int xx=0; xx<myGUI.boardWidth(); xx++)
                for (int yy=0; yy<myGUI.boardHeight(); yy++)
                    currState.modify(xx, yy, myGUI.colorAt(xx, myGUI.boardHeight()-yy-1));
            pos nextMove = chooseMove();
            myGUI.makeMove(nextMove.xx, nextMove.yy); // i.e. click on the lower left corner
        }
    }

    // internal classes
    static class pos {
        final int xx;
        int yy;
        pos(int xx, int yy) {this.xx=xx; this.yy=yy;}
    }

    private class board {
        final LinkedList<LinkedList<CircleColor>> data;
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

        board() {
            this(myGUI.boardWidth(),myGUI.boardHeight());
        }

        board(board basis) {
            // allocate space
            this(basis.width, basis.height);

            // copy over all the specific items
            for (int xx=0; xx<columns(); xx++)
                for (int yy=0; yy<rows(xx); yy++)
                    modify(xx,yy,basis.get(xx, yy));
        }

        private CircleColor get(int xx, int yy) {
            try {
                return data.get(xx).get(yy);
            } catch (IndexOutOfBoundsException e) {
                return CircleColor.NONE;
            }
        }

        private LinkedList<CircleColor> get(int xx) {
            try {
                return data.get(xx);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        private void modify(int xx, int yy, CircleColor col) {
            data.get(xx).set(yy, col);
        }

        private void delete(int xx, int yy) {
            data.get(xx).remove(yy);
        }

        private void delete(int xx) {
            data.remove(xx);
        }

        // public accessors
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

        int columns() {
            return data.size();
        }

        int rows(int xx) {
            return data.get(xx).size();
        }

        public String display() {
            String temp = data.toString();
            return temp.replace("], [", "]\n[");
        }
    }

    // internal methods
    private pos chooseMove() {
        int max=0;                  // the maximum number of points for the best position found
        pos bestPos = new pos(0, 0); // the best position found
        board currStateCopy = new board(currState);

        // Calculate the highest column on the board
        int maxHeight = 0;
        LinkedList<Integer> highest = new LinkedList<>();
        for (int xx = 0; xx < currState.columns(); xx++) {
            for (int yy = 0; yy < currState.rows(xx); yy++) {
                if (currState.get(xx, yy) == CircleColor.NONE) {
                    if (yy > maxHeight) {
                        maxHeight = yy - 1;
                    }
                    break;
                }
            }
        }

        // Find which columns are higher than anybody else
        for (int xx = 0; xx < currState.columns(); xx++) {
            for (int yy = 0; yy < currState.rows(xx); yy++) {
                if (currState.get(xx, yy) == CircleColor.NONE) {
                    if (yy - 1 == maxHeight) {
                        highest.add(xx);
                    }
                    break;
                }
            }
        }
        // this section of code is to find how many same-color circles in a row in those highest columns
        int maxLine = 1; int whichColumn = 0;
        for (int hh : highest) {
            int line = 1; Map<Integer, CircleColor> same = new HashMap<>();
            for (int yy = 0; yy < currState.rows(hh); yy++) {
                if (currStateCopy.get(hh, yy) != CircleColor.NONE) {
                    same.put(yy, currStateCopy.get(hh, yy));
                    if (currStateCopy.get(hh, yy) == same.get(yy - 1)) {
                        line++;
                        if (line > maxLine) {
                            maxLine = line;
                            whichColumn = hh;
                        }
                    } else {
                        line = 1;
                    }
                }
            }
        }

        int same = 1;
        for (int yy = 0; yy < currStateCopy.rows(whichColumn); yy++) {
            if (currStateCopy.get(whichColumn, yy) != CircleColor.NONE) {
                if (currStateCopy.get(whichColumn, yy) == currStateCopy.get(whichColumn, yy - 1)) {
                    same++;
                    if (same == maxLine) {
                        max = maxLine;
                        bestPos = new pos(whichColumn, yy);
                        break;
                    }
                } else {
                    same = 1;
                }
            }
        }

        for (int hh : highest) {
            for (int yy = 0; yy < currState.rows(hh); yy++) {
                if (currStateCopy.get(hh, yy) != CircleColor.NONE) {
                    board test = new board(currStateCopy);
                    currStateCopy.clickNodeHelper(hh, yy, test.get(hh, yy)); // mark all other nodes in the region as "clear" (but does not delete anything)
                    int count = test.clickNode(hh, yy); // try removing the region to see what is left over
                    if (count > max) {
                        max = count;
                        bestPos = new pos(hh, yy);
                    }
                }
            }
        }
        // Avoid that one move only score 1 point
        // if so, the brain will choose the circle that score the most points
        if (max == 1) {
            for (int xx = 0; xx < currState.columns(); xx++) {
                for (int yy = 0; yy < currState.rows(xx); yy++) {
                    if (currStateCopy.get(xx, yy) != CircleColor.NONE) {
                        board test = new board(currStateCopy);
                        currStateCopy.clickNodeHelper(xx, yy, test.get(xx, yy)); // mark all other nodes in the region as "clear" (but does not delete anything)
                        int count = test.clickNode(xx, yy); // try removing the region to see what is left over
                        if (count > max) {
                            // record a new best move
                            max = count;
                            bestPos = new pos(xx, yy);
                        }
                    }
                }
            }
        }
        // register the selected move on the board
        currState.clickNode(bestPos.xx, bestPos.yy);
        // convert bestPos to GUI coordinates
        bestPos.yy = myGUI.boardHeight() - 1 - bestPos.yy;
        return bestPos;
    }
}
