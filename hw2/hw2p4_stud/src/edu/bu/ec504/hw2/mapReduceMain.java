package edu.bu.ec504.hw2;

import edu.bu.ec504.hw2.mapReduceExamples.commonCharacters.CommonCharactersMapper;
import edu.bu.ec504.hw2.mapReduceExamples.commonCharacters.CommonCharactersReducer;
import edu.bu.ec504.hw2.Mapper.MapperEmission;
import edu.bu.ec504.hw2.Mapper.MapperEmissionList;
import edu.bu.ec504.hw2.mapReduceExamples.template.myMapper;
import edu.bu.ec504.hw2.mapReduceExamples.template.myReducer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


class mapReduceMain {
	// CONSTANTS
	private static final int NUM_MAPPERS = 5;                   /** The max number of mappers. */
	private static final int NUM_REDUCERS = 3;                  /** The max number of reducers. */

	// STATICS
	private static final ExecutorService mapper = Executors.newFixedThreadPool(NUM_MAPPERS);     /* manages mapper threads */
	private static final ExecutorService reducer = Executors.newFixedThreadPool(NUM_REDUCERS);   /* manages mapper threads */

	
	// METHODS
	/**
	 * Performs a five-stage parallelized map-reduce on a file with the given filename.
	 * @param fileName The name of the file on which to operate.
	 * @param MapperClass The class that implements MAPping.
	 * @param ReducerClass The class that implements REDUCEing.
	 * @return A summary of the results emitted by the Reducers.
	 */
	private static String performMapReduce(
			String fileName,
			Class<? extends Mapper> MapperClass,
			Class<? extends Reducer> ReducerClass) {

		// STEP 1 - INPUT
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file " + fileName);
			System.exit(-1);
		}

		// STEP 2 - MAP - produce mapper emissions
		try {
			String text;
			Integer line;
			for (line = 0; (text = reader.readLine()) != null; line++) {
				Mapper theMapper = MapperClass.newInstance();
				theMapper.init(line, text);
				Mapper.addTask(theMapper);                               // add the task to the schedule
			}
			reader.close();

			// ... execute all tasks
			Mapper.futures = mapper.invokeAll(Mapper.tasks);
		} catch (IOException e) {
			System.err.println("I/O error on file " + fileName);
			System.exit(-1);
		} catch (InterruptedException e) {
			System.err.println("Interrupted thread " + Thread.currentThread().getName());
		} catch (IllegalAccessException | InstantiationException ignored) {
			throw new UnsupportedOperationException(
					"Could not construct mapper from default constructor.  Is it defined?");
		}

		// STEP 3/4 - PARTITION/COMPARE - produce shards
		Map<String, ArrayList<Integer>> shards = new HashMap<>();
		for (Future<MapperEmissionList> future : Mapper.futures) {  // go through all emissions and taxonomize them into shards
			try {
				List<MapperEmission> emittedList = future.get();

				for (MapperEmission emitted : emittedList) {
					if (shards.get(emitted.getKey()) == null) {           // i.e. no key yet - start a new ArrayList
						ArrayList<Integer> valueList = new ArrayList<>();
						valueList.add(emitted.getValue());
						shards.put(emitted.getKey(), valueList);            // shards[key] = valueList
					} else { // the key already exists
						shards.get(emitted.getKey())
								.add(emitted.getValue());                       // simply add to the existing ArrayList linked to emitted's key
					}
				}
			} catch (InterruptedException | ExecutionException ex) {
				System.err.println("Interruption or execution of thread: " + ex);
			}
		}

		// STEP 5 - REDUCE - produce reducer emissions
		try {
			for (String key : shards.keySet()) {                      // assign each shard to a reducer
				Reducer theReducer = ReducerClass.newInstance();
				theReducer.init(key, shards.get(key));
				Reducer.addTask(theReducer);                            // ship off to the reducer
			}
			// execute all tasks
			Reducer.setFutures(reducer.invokeAll(Reducer.getTasks()));
		} catch (InterruptedException e) {
			System.err.println("Interrupted thread " + Thread.currentThread().getName());
		} catch (IllegalAccessException | InstantiationException ignored) {
			System.err.println("Could not construct reducer from default constructor.  Is it defined?");
		}

		// STEP 6 - SHUTDOWN AND PRINT OUTPUT
		mapper.shutdown();
		reducer.shutdown();
		try {
			Reducer theReducer = ReducerClass.newInstance();
			return theReducer.print();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new UnsupportedOperationException(
					"Could not instantiate Reducer object.  Is it defined?");
		}
	}


	/**
	 * MAIN METHOD
	 * @param args the first parameter on the command line is the name of the file from which to draw input.
	 */
	public static void main(String[] args) {
		long startTime = System.nanoTime();

		if (args.length==0) {
			System.out.println("Please supply the file name for input as a first argument."); // you may use "foo.txt" as an example.
			System.exit(0);
		}

		System.out.println(performMapReduce(
				args[0],
				mapper4d.class,
				reducer4d.class
		));

		long endTime = System.nanoTime();

		System.out.format("Elapsed time: %f seconds.",(endTime-startTime)/1000000000.0);
	}
}
