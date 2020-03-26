package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.Reducer.ReducerEmissionList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Abstract implementation of a REDUCE-capable object.
 * All inherited class *must* implement a constructor that takes a String and A
 * @author Ari Trachtenberg
 */

public abstract class Reducer implements Callable<ReducerEmissionList> {

	/**
	 * CONSTRUCTOR
	 * @param theKey The key input for the map
	 * @param theValue The value input for the map
	 */
	protected Reducer(String theKey, ArrayList<Integer> theValue) {
		init(theKey, theValue);
	}

	/**
	 * Default constructor - required by the mapReduce engine.
	 */
	protected Reducer() {
	}

	/**
	 * MUST be called before any other method.
	 * @param theKey The key input for the reducer
	 * @param theValue The value input for the reducer
	 */
	public void init(String theKey, ArrayList<Integer> theValue) {
		key = theKey;
		value = theValue;
	}

	// NESTED CLASSES
	/**
	 * Reducer-specific version of the EmissionTemplate
	 */
	protected static class ReducerEmission extends EmissionTemplate {
		public ReducerEmission(String theKey, Integer theValue) {
			super(theKey, theValue);
		}
	}

	/**
	 * A list of {@link ReducerEmission}.
	 */
	public static class ReducerEmissionList extends ArrayList<ReducerEmission> {}


	// METHODS
	/**
	 * THIS DOES THE ACTUAL WORK OF YOUR REDUCER.
	 * @return A list of emissions (i.e. key=String, value=Integer pairs) produced by this task.
	 */
	@Override
	abstract public ReducerEmissionList call();

	/**
	 * Collect emissions and produce a summarizing string
	 */
	 public String print() {
		throw new UnsupportedOperationException("No print method for the Reducer base class.  Please override in your subclass.");
	}


	// ... STATIC METHODS
	//     (getters and setters)
	public static List<Reducer> getTasks() {
		return tasks;
	}
	public static void addTask(Reducer task) {
		Reducer.tasks.add(task);
	}
	public static List<Future<ReducerEmissionList>> getFutures() {
		return futures;
	}
	public static void setFutures(
			List<Future<ReducerEmissionList>> futures) {
		Reducer.futures = futures;
	}

	// FIELDS
	protected String key=null;                /* The key provided when constructing this object. */
	protected ArrayList<Integer> value=null;  /* The value provided when constructing this object. */

	// ... STATIC FIELDS
	private static final List<Reducer> tasks = new ArrayList<>();                   // all the tasks to be executed
	private static List<Future<ReducerEmissionList>> futures = new ArrayList<>();  // results of task executions
}
