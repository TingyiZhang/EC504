package edu.bu.ec504.spr19.sameGameTris;

import java.awt.Color;

import static edu.bu.ec504.spr19.sameGameTris.sge.randGen;

/*
 * Records the color of one circle
 */

public enum CircleColor {
	NONE(null),
	Red(new Color(255, 0, 0)),
    Green(new Color(0, 255, 0)),
    Blue(new Color(0, 0, 255)),
    Pink(new Color(255,0,255));

	// fields
	private final Color myColor;

	// Constructor
	CircleColor(Color myColor) {
		this.myColor = myColor;
	}

	// Accessors
	Color getColor() {
		return myColor;
	}

	/*
	 * Generate a random color from the first num possible colors (excluding NONE)
	 */
	static CircleColor randColor() {
		if (sge.numColors < 1 || sge.numColors >= CircleColor.values().length)
			throw new IndexOutOfBoundsException("Only " + CircleColor.values().length + " colors are available.  You requested choosing one of " + sge.numColors + " colors.");

		int randNum = 1 + Math.abs(randGen.nextInt()) % sge.numColors;
		return CircleColor.values()[randNum];

	}
}
