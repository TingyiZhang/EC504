package edu.bu.ec504.spr19.highScores;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.TreeSet;

/*
 * Maintains high scores in a file (this will not work for web-accessed applets)
 */
public class highScore {

	// CONSTANTS
	public final int maxScores = 10; // the maximum number of scores to display (and remember)

	// FIELDS
	/*
	 * where the high score database is kept
	 */
	private final String dataFile;
	private final TreeSet<HSdata> db;

	// SUBCLASSES
	/**
	 * The data structure used to store high scores.
	 */
	public static class HSdata {
		public HSdata(String nm, int bx, int by, int sc) {
			name=nm; boardSizeX=bx; boardSizeY=by; score=sc;
		}

		final String name;
		final int boardSizeX;
		final int boardSizeY;
		final int score;
	}

	/**
	 * A comparator for comparing two high scores.
	 */
	public static class hsComp implements Comparator<HSdata> {
		/**
		 * Computes the weighted score from an HS item
		 */
		public static int weightedScore(HSdata item) {
			final int totalSize = Math.max(item.boardSizeX,item.boardSizeY);
			return (int) (item.score/Math.sqrt(totalSize));
		}

		public int compare(HSdata o1, HSdata o2) {
			if (o1.score==o2.score
					&& o1.boardSizeX==o2.boardSizeX && o1.boardSizeY==o2.boardSizeY
					&& o1.name.equals(o2.name))
				return 0; // i.e. equal
			else
				return (weightedScore(o1)>weightedScore(o2)?-1:1);
		}
	}

	// CONSTRUCTORS

	/**
	 * Constructs a high score class, backed by a specified file.
	 * @param fileName The name of the file to use for high score information.
	 */
	public highScore(String fileName) {
		dataFile = fileName;
		db = new TreeSet<>(new hsComp());
		loadScores();
	}


	/**
	 * Save one score to the database.
	 * As a cleanup step, this method also removes the lowest scores until
	 * there are at most {@code maxScores} scores in the database..
	 */
	public boolean putScore(HSdata foo) {
		if (newRecordQ(foo.score)) {
			db.add(foo);
			while (db.size()>maxScores)
				db.remove(db.last());
			saveScores();
			return true;
		}
		else
			return false;
	}


	/**
	 * @return true iff score is a new record on the high score list.
	 */
	public boolean newRecordQ(int score) {
		if (db.isEmpty()) // nothing in the db so far
			return true;
		else
			return (db.size()<maxScores-1 || score > db.last().score);
	}


	/*
	 * @return a human-readable version of the high score list
	 */
	public String display() {
		StringBuilder temp= new StringBuilder();
		for (HSdata foo: db) {
			temp.append(hsComp.weightedScore(foo)).append(" [weighted]: ").append(foo.score).append("[actual] (").append(foo.name).append(" on ").append(foo.boardSizeX).append("x").append(foo.boardSizeY).append(")\n");

		}
		return temp.toString();
	}

	// HELPER FUNCTIONS
	/**
	 * Load high scores from the associated file.
	 */
	private void loadScores() {
		FileInputStream fis;
		try {
			fis = new FileInputStream(dataFile);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				String[] res = strLine.split("\t");
				HSdata temp = new HSdata(res[0],new Integer(res[1]),new Integer(res[2]), new Integer(res[3]));
				db.add(temp);
			}
		} catch (FileNotFoundException e) {
			System.err.println("High score file not found - Starting fresh!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save current database to the associated file.
	 */
	private void saveScores() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dataFile);
			DataOutputStream out = new DataOutputStream(fos);
			BufferedWriter br = new BufferedWriter(new OutputStreamWriter(out));

			for (HSdata foo: db) {
				br.write(foo.name+"\t"+foo.boardSizeX+"\t"+foo.boardSizeY+"\t"+foo.score+"\n");
			}

			br.close();

		} catch (FileNotFoundException e) {
			System.err.println("High score file not found - Cannot write data!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
