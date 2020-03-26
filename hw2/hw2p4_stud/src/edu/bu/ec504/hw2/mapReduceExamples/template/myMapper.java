package edu.bu.ec504.hw2.mapReduceExamples.template;

import edu.bu.ec504.hw2.Mapper;

public class myMapper extends Mapper {

	/**
	 * A helpful constructor for instantiating a mapper based on a key-value pair.
	 */
	public myMapper(Integer theKey, String theValue) { super(theKey, theValue); }

	/**
	 * Default constructor - required by the mapReduce engine, though it need not do anything.
	 */
	public myMapper() { super(); }

	/**
	 * 	This does the actual work of your mapper.
	 */
	@Override
	public MapperEmissionList call() {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED!");
	}
}
