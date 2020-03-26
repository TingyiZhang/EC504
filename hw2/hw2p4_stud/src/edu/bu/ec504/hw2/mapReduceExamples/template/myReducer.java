package edu.bu.ec504.hw2.mapReduceExamples.template;

import edu.bu.ec504.hw2.Reducer;

import java.util.ArrayList;

public class myReducer extends Reducer {

	public myReducer(String theKey, ArrayList<Integer> theValue) {
		super(theKey, theValue);
	}

	/**
	 * 	Default constructor - required by the mapReduce engine, though it need not do anything.
	 */
	public myReducer() { super(); }


	/**
	 * 	This does the actual work of your reducer.
	 */
	@Override
	public ReducerEmissionList call() {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED!");
	}

	/**
	 * Coalesces reduction output.
	 * @return A string representing the coalesced reduction output.
	 */
	@Override
	 public String print() {
		/* THIS IS WHERE YOUR REDUCE EMISSIONS ARE SUMMARIZED. */
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED!");
	}
}
